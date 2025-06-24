package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.InvoiceDTO;
import ra.edu.entity.Invoice;
import ra.edu.service.InvoiceService;
import ra.edu.utils.InvoiceStatus;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("admin/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public String getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Invoice> invoices = invoiceService.findAllInvoice(page, size);
        long totalElements = invoiceService.countAllInvoice();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceDTO", new InvoiceDTO());
        model.addAttribute("content", "invoices");
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);

        return "homeAdmin";
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
