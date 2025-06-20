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
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        Customer customer = convertToEntity(customerDTO);

        // Kiểm tra trùng số điện thoại
        Customer existingPhone = customerService.findCustomerByPhone(customer.getPhone());
        if (!result.hasFieldErrors("phone") &&
                existingPhone != null &&
                (customer.getId() == null || !existingPhone.getId().equals(customer.getId()))) {
            result.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại");
        }

        // Kiểm tra trùng email
        Customer existingEmail = customerService.findCustomerByEmail(customer.getEmail());
        if (!result.hasFieldErrors("email") &&
                existingEmail != null &&
                (customer.getId() == null || !existingEmail.getId().equals(customer.getId()))) {
            result.rejectValue("email", "error.email", "Email đã tồn tại");
        }

        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.findAllCustomer());
            model.addAttribute("content", "customers");
            model.addAttribute("showModal", true);
            if (customerDTO.getId() != null) {
                model.addAttribute("editMode", true);
            }
            return "homeAdmin";
        }

        boolean success;
        if (customer.getId() != null) {
            success = customerService.updateCustomer(customer);
        } else {
            success = customerService.saveCustomer(customer);
        }

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

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("id") Integer id,
                               @RequestParam("status") boolean status) {
        customerService.updateCustomerStatus(id, status);
        return "redirect:/admin/customers";
    }

    @PostMapping("/searchCustomer")
    public String searchCustomerByName(@RequestParam(required = false) String customerName,
                                       Model model,
                                       HttpSession session) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Customer> customers;
        if (customerName == null || customerName.trim().isEmpty()) {
            customers = customerService.findAllCustomer(); // Nếu ô tìm kiếm rỗng
        } else {
            customers = customerService.findCustomerByName(customerName.trim());
        }

        model.addAttribute("customers", customers);
        model.addAttribute("customerDTO", new CustomerDTO());
        model.addAttribute("content", "customers");
        model.addAttribute("keywordName", customerName);

        return "homeAdmin";
    }

    // ======= CHUYỂN ĐỔI =========
    private CustomerDTO convertToDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setAddress(c.getAddress());
        dto.setStatus(c.getStatus());
        return dto;
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer c = new Customer();
        c.setId(dto.getId()); // để phòng trường hợp update sau này
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setAddress(dto.getAddress());
        c.setStatus(dto.getStatus()); // mặc định true nếu null
        return c;
    }
}
