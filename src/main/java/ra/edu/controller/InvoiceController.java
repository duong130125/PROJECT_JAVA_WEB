package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.InvoiceDTO;
import ra.edu.dto.InvoiceDetailDTO;
import ra.edu.entity.Customer;
import ra.edu.entity.Invoice;
import ra.edu.entity.InvoiceDetail;
import ra.edu.entity.Product;
import ra.edu.service.CustomerService;
import ra.edu.service.InvoiceService;
import ra.edu.service.ProductService;
import ra.edu.utils.InvoiceStatus;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("admin/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Product> products = productService.getAllProducts(0, 100);
        List<Customer> customers = customerService.findAllCustomer(0, 100);
        List<Invoice> invoices = invoiceService.findAllInvoice(page, size);
        long totalElements = invoiceService.countAllInvoice();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // ✅ Tạo danh sách invoiceDetails tương ứng với products
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        List<InvoiceDetailDTO> detailDTOs = new ArrayList<>();
        for (Product p : products) {
            InvoiceDetailDTO detail = new InvoiceDetailDTO();
            detail.setProduct_id(p.getId()); // Khởi tạo product_id đúng index
            detail.setQuantity(0); // Mặc định 0
            detail.setUnitPrice(p.getPrice());
            detailDTOs.add(detail);
        }
        invoiceDTO.setInvoiceDetails(detailDTOs);

        model.addAttribute("products", products);
        model.addAttribute("customers", customers);
        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("content", "invoices");
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);

        return "homeAdmin";
    }


    @PostMapping("/save")
    public String addInvoice(@Valid @ModelAttribute("invoiceDTO") InvoiceDTO invoiceDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        // Nếu có lỗi validate, trả về trang chính kèm thông báo lỗi
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin hóa đơn.");
            redirectAttributes.addFlashAttribute("invoiceDTO", invoiceDTO);
            return "redirect:/admin/invoices";
        }

        // Kiểm tra khách hàng tồn tại
        Customer customer = customerService.findCustomerById(invoiceDTO.getCustomer_id());
        if (customer == null) {
            redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại.");
            return "redirect:/admin/invoices";
        }

        List<InvoiceDetail> detailList = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceDetailDTO detailDTO : invoiceDTO.getInvoiceDetails()) {
            Product product = productService.findProductById(detailDTO.getProduct_id());
            if (product == null) continue;

            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(detailDTO.getQuantity()));
            total = total.add(lineTotal);

            InvoiceDetail detail = new InvoiceDetail();
            detail.setProduct(product);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(unitPrice);
            detailList.add(detail);
        }

        if (detailList.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không có sản phẩm hợp lệ để tạo hóa đơn.");
            return "redirect:/admin/invoices";
        }

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setCreated_at(new Date());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setTotal_amount(total);

        boolean success = invoiceService.saveInvoice(invoice);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu hóa đơn.");
            return "redirect:/admin/invoices";
        }

        for (InvoiceDetail detail : detailList) {
            detail.setInvoice(invoice);
            invoiceService.saveInvoiceDetail(detail);
        }

        redirectAttributes.addFlashAttribute("success", "Thêm hóa đơn thành công!");
        return "redirect:/admin/invoices";
    }

    @GetMapping("/searchNameCustomer")
    public String getInvoicesByCustomerName(
            @RequestParam(required = false) String searchNameCustomer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            List<Invoice> invoices;
            long totalElements;

            if (searchNameCustomer != null && !searchNameCustomer.trim().isEmpty()) {
                invoices = invoiceService.findInvoiceByCustomerName(searchNameCustomer.trim(), page, size);
                totalElements = invoiceService.countInvoiceByCustomerName(searchNameCustomer.trim());
                model.addAttribute("searchKeyword", searchNameCustomer);
            } else {
                invoices = invoiceService.findAllInvoice(page, size);
                totalElements = invoiceService.countAllInvoice();
            }

            int totalPages = (int) Math.ceil((double) totalElements / size);

            model.addAttribute("invoices", invoices);
            model.addAttribute("invoiceDTO", new InvoiceDTO());
            model.addAttribute("content", "invoices");
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
            model.addAttribute("totalPages", totalPages);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi tải hóa đơn.");
            model.addAttribute("invoices", new ArrayList<>());
        }

        return "homeAdmin";
    }

    @PostMapping("/update")
    public String updateInvoiceStatus(@RequestParam("id") Integer id,
                                      @RequestParam("status") InvoiceStatus newStatus,
                                      RedirectAttributes redirectAttributes) {
        Invoice invoice = invoiceService.findInvoiceById(id);

        if (invoice == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/admin/invoices";
        }

        InvoiceStatus currentStatus = invoice.getStatus();

        // Nếu đã CANCELLED hoặc COMPLETED thì không cho thay đổi
        if (currentStatus == InvoiceStatus.CANCELLED || currentStatus == InvoiceStatus.COMPLETED) {
            redirectAttributes.addFlashAttribute("error", "Không thể thay đổi trạng thái đơn hàng đã bị hủy hoặc hoàn tất.");
            return "redirect:/admin/invoices";
        }

        // Trạng thái kế tiếp hợp lệ
        boolean isValidTransition = false;
        switch (currentStatus) {
            case PENDING:
                isValidTransition = newStatus == InvoiceStatus.CONFIRMED || newStatus == InvoiceStatus.CANCELLED;
                break;
            case CONFIRMED:
                isValidTransition = newStatus == InvoiceStatus.SHIPPING || newStatus == InvoiceStatus.CANCELLED;
                break;
            case SHIPPING:
                isValidTransition = newStatus == InvoiceStatus.COMPLETED || newStatus == InvoiceStatus.CANCELLED;
                break;
            default:
                isValidTransition = false;
        }

        if (!isValidTransition) {
            redirectAttributes.addFlashAttribute("error", "Không thể chuyển sang trạng thái đã chọn.");
            return "redirect:/admin/invoices";
        }

        // Cập nhật trạng thái
        invoice.setStatus(newStatus);
        boolean updated = invoiceService.updateInvoice(invoice);
        if (updated) {
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại!");
        }

        return "redirect:/admin/invoices";
    }
}
