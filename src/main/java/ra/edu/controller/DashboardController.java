package ra.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @GetMapping
    public String showDashboard(Model model, HttpSession session) {
        // Thêm dữ liệu mẫu (hoặc lấy từ service)
        model.addAttribute("totalUsers", 100);
        model.addAttribute("activeUsers", 85);
        model.addAttribute("totalProducts", 250);
        model.addAttribute("totalOrders", 120);
        model.addAttribute("pendingOrders", 20);
        model.addAttribute("totalRevenue", "12,345.67");

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }
        // Gắn nội dung của dashboard vào layout
        model.addAttribute("content", "dashboard");
        return "homeAdmin";
    }
}
