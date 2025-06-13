package ra.edu.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.ProductDTO;
import ra.edu.entity.Product;
import ra.edu.service.ProductService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String listProducts(Model model, HttpSession session) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }
        List<Product> products = productService.getAllProducts();
        model.addAttribute("view", "products");
        model.addAttribute("products", products);
        return "homeAdmin";
    }

    // Hiển thị form thêm sản phẩm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        return "add_product";
    }

    // Xử lý thêm sản phẩm
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute ProductDTO productDTO,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add_product";
        }

        try {
            if (!imageFile.isEmpty()) {
                String imageUrl = uploadToCloudinary(imageFile);
                productDTO.setImage(imageUrl);
            }

            Product product = convertToEntity(productDTO);
            boolean saved = productService.addProduct(product);

            if (saved) {
                redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Thêm sản phẩm thất bại!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Hiển thị form sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Product product = productService.findProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        ProductDTO dto = convertToDTO(product);
        model.addAttribute("productDTO", dto);
        return "edit_product";
    }

    // Xử lý cập nhật sản phẩm
    @PostMapping("/edit")
    public String editProduct(@Valid @ModelAttribute ProductDTO productDTO,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit_product";
        }

        try {
            Product current = productService.findProductById(productDTO.getId());

            if (!imageFile.isEmpty()) {
                String imageUrl = uploadToCloudinary(imageFile);
                productDTO.setImage(imageUrl);
            } else {
                productDTO.setImage(current.getImage());
            }

            Product product = convertToEntity(productDTO);
            boolean updated = productService.updateProduct(product);

            if (updated) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật sản phẩm thất bại!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Xóa sản phẩm
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = productService.deleteProduct(id);

            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Xóa sản phẩm thất bại!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Tìm kiếm sản phẩm theo tên
    @GetMapping("/searchBrand")
    public String searchProductsByBrand(@RequestParam String brand, Model model) {
        List<Product> products = productService.searchProductsByBrand(brand);
        model.addAttribute("view", "products");
        model.addAttribute("products", products);
        model.addAttribute("searchBrand", brand);
        return "homeAdmin";
    }

    // Upload ảnh lên Cloudinary
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "products",
                        "resource_type", "image",
                        "quality", "auto:eco",
                        "fetch_format", "auto"
                ));
        return uploadResult.get("secure_url").toString();
    }

    // Chuyển DTO sang Entity
    private Product convertToEntity(ProductDTO dto) {
        Product p = new Product();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setBrand(dto.getBrand());
        p.setStock(dto.getStock());
        p.setImage(dto.getImage());
        p.setStatus(dto.isStatus());
        return p;
    }

    // Chuyển Entity sang DTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setBrand(product.getBrand());
        dto.setStock(product.getStock());
        dto.setImage(product.getImage());
        dto.setStatus(product.isStatus());
        return dto;
    }
}
