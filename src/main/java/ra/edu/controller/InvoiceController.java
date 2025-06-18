package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.edu.entity.Invoice;
import ra.edu.service.InvoiceService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("admin/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public String getAllInvoices(Model model, HttpSession session) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Invoice> invoices = invoiceService.findAllInvoice();
        model.addAttribute("invoices", invoices);
        model.addAttribute("content", "invoices");

        return "homeAdmin";
    }
}
