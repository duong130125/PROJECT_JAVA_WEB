package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.AdminDTO;
import ra.edu.entity.Admin;
import ra.edu.service.AuthAdminService;

import javax.validation.Valid;

@Controller
public class AuthAdminController {

    @Autowired
    private AuthAdminService authService;

    @GetMapping("register")
    public String showRegisterForm(Model model) {
        model.addAttribute("AdminDTO", new AdminDTO());
        return "register";
    }

    @PostMapping("register")
    public String processRegister(@Valid @ModelAttribute("AdminDTO") AdminDTO adminDTO,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (authService.existsByUsername(adminDTO.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }

        Admin admin = new Admin();
        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword());

        if (authService.saveRegister(admin)) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Lỗi khi đăng ký!");
            return "register";
        }
    }

    @GetMapping("login")
    public String showLoginForm(Model model) {
        model.addAttribute("AdminDTO", new AdminDTO());
        return "login";
    }

    @PostMapping("login")
    public String processLogin(@ModelAttribute("AdminDTO") @Valid AdminDTO adminDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        Admin admin = authService.login(adminDTO.getUsername(), adminDTO.getPassword());
        if (admin != null) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            return "login";
        }
    }
}
