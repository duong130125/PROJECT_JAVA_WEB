package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.CustomerDTO;
import ra.edu.entity.Customer;

import ra.edu.service.CustomerService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("admin/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String getAllCustomers(Model model, HttpSession session) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Customer> customers = customerService.findAllCustomer();
        model.addAttribute("customers", customers);
        model.addAttribute("customerDTO", new CustomerDTO());
        model.addAttribute("content", "customers");
        return "homeAdmin";
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute("customerDTO") CustomerDTO customerDTO,
                               BindingResult result, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("customerDTO", result);
            redirectAttributes.addFlashAttribute("customerDTO", customerDTO);
            return "redirect:/admin/customers";
        }

        Customer customer = convertToEntity(customerDTO);
        customerService.saveCustomer(customer);
        return "redirect:/admin/customers";
    }

    // Xóa sản phẩm
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            // Kiểm tra sản phẩm có tồn tại không
            Customer customer = customerService.findCustomerById(id);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng cần xóa!");
                return "redirect:/admin/customers";
            }

            boolean deleted = customerService.deleteCustomer(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Xóa khách hàng thất bại!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khách hàng: " + e.getMessage());
        }

        return "redirect:/admin/customers";
    }

    // ======= CHUYỂN ĐỔI =========
    private CustomerDTO convertToDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setAddress(c.getAddress());
        dto.setStatus(c.isStatus());
        return dto;
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer c = new Customer();
        c.setId(dto.getId()); // để phòng trường hợp update sau này
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setAddress(dto.getAddress());
        c.setStatus(dto.isStatus()); // mặc định true nếu null
        return c;
    }
}
