package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.edu.entity.Customer;
import ra.edu.entity.Invoice;
import ra.edu.entity.Product;
import ra.edu.service.CustomerService;
import ra.edu.service.InvoiceService;
import ra.edu.service.ProductService;
import ra.edu.utils.InvoiceStatus;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String showDashboard(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model, HttpSession session) {
        List<Customer> customers = customerService.findAllCustomer(page, size);

        // Tổng số customer
        model.addAttribute("totalCustomers", customers.size());

        // Số customer đang hoạt động (giả sử có trường isActive hoặc status)
        long activeCount = customers.stream()
                .filter(customer -> customer.getStatus() == true)
                .count();
        model.addAttribute("activeCustomers", activeCount);

        List<Product> products = productService.getAllProducts(page, size);
        model.addAttribute("totalProducts", products.size());

        List<Invoice> allInvoices = invoiceService.findAllInvoice(page, size);
        model.addAttribute("totalOrders", allInvoices.size());

        long pendingOrders = allInvoices.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PENDING)
                .count();

        model.addAttribute("pendingOrders", pendingOrders);

        model.addAttribute("totalRevenue", invoiceService.getTotalRevenueByDay());
        model.addAttribute("monthlyRevenue", invoiceService.getTotalRevenueByMonth());
        model.addAttribute("yearlyRevenue", invoiceService.getTotalRevenueByYear());

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }
        // Gắn nội dung của dashboard vào layout
        model.addAttribute("content", "dashboard");
        return "homeAdmin";
    }
}
