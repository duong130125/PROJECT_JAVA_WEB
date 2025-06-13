package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.AdminDTO;
import ra.edu.entity.Admin;
import ra.edu.service.AuthAdminService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AuthAdminController {

    @Autowired
    private AuthAdminService authService;

    @GetMapping("login")
    public String showLoginForm(Model model) {
        model.addAttribute("AdminDTO", new AdminDTO());
        return "login";
    }

    @PostMapping("login")
    public String processLogin(@Valid @ModelAttribute("AdminDTO") AdminDTO adminDTO,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }

        Admin admin = authService.login(adminDTO.getUsername(), adminDTO.getPassword());
        if (admin != null) {
            session.setAttribute("adminLogin", admin);
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            return "login";
        }
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate(); // xoá session
        return "redirect:/login";
    }

}
