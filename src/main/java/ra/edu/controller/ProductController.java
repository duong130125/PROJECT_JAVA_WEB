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
import ra.edu.entity.Customer;
import ra.edu.entity.Product;
import ra.edu.service.ProductService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model, HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Product> products = productService.getAllProducts(page, size);
        long total = productService.countAllProducts();
        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("products", products);
        model.addAttribute("isSearch", true);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("content", "products");

        return "homeAdmin";
    }

    @PostMapping("/save")
    public String saveProduct(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              HttpSession session,
                              Model model) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        boolean isEdit = productDTO.getId() != null && productDTO.getId() > 0;

        // Kiểm tra trùng tên sản phẩm (chỉ khi thêm mới hoặc sửa tên khác)
        Product existingByName = productService.findProductByName(productDTO.getName().trim());
        if (!result.hasFieldErrors("name") &&
                existingByName != null &&
                (productDTO.getId() == null || !existingByName.getId().equals(productDTO.getId()))) {
            result.rejectValue("name", "error.name", "Tên sản phẩm đã tồn tại");
        }

        // Kiểm tra file ảnh nếu có upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.rejectValue("imageFile", "error.image", "File phải là hình ảnh");
            } else if (imageFile.getSize() > 10 * 1024 * 1024) {
                result.rejectValue("imageFile", "error.image", "File không được vượt quá 10MB");
            }
        } else if (!isEdit) {
            // Nếu là thêm mới mà không chọn ảnh
            result.rejectValue("imageFile", "error.image", "Vui lòng chọn ảnh cho sản phẩm");
        }

        // Nếu có lỗi thì trả về lại trang
        if (result.hasErrors()) {
            prepareProductList(model, page, size);
            model.addAttribute("content", "products");
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("showModal", true);
            if (isEdit) model.addAttribute("editMode", true);
            model.addAttribute("openModal", true);
            model.addAttribute("editMode", isEdit);
            return "homeAdmin";
        }

        // Không có lỗi => xử lý lưu
        Product product = isEdit
                ? productService.findProductById(productDTO.getId())
                : new Product();

        if (product == null) {
            model.addAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật!");
            return "redirect:/admin/products";
        }

        product.setName(productDTO.getName().trim());
        product.setBrand(productDTO.getBrand());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : true);

        // Upload ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = uploadToCloudinary(imageFile);
                product.setImage(imageUrl);
            } catch (Exception e) {
                result.rejectValue("image", "error.image", "Lỗi khi upload ảnh: " + e.getMessage());
                prepareProductList(model, page, size);
                model.addAttribute("content", "products");
                model.addAttribute("showModal", true);
                if (isEdit) model.addAttribute("editMode", true);
                model.addAttribute("openModal", true);
                model.addAttribute("editMode", isEdit);
                return "homeAdmin";
            }
        } else if (isEdit) {
            // Nếu không chọn ảnh mới khi sửa, giữ ảnh cũ
            Product existing = productService.findProductById(productDTO.getId());
            if (existing != null) {
                product.setImage(existing.getImage());
            }
        }

        boolean saved = isEdit
                ? productService.updateProduct(product)
                : productService.addProduct(product);

        return "redirect:/admin/products";
    }


    // Xóa sản phẩm
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            // Kiểm tra sản phẩm có tồn tại không
            Product product = productService.findProductById(id);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm cần xóa!");
                return "redirect:/admin/products";
            }

            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Xóa sản phẩm thất bại!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String stock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model, HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            Double minPrice = null;
            Double maxPrice = null;
            Integer stockInt = null;

            // Xử lý khoảng giá
            if (priceRange != null && !priceRange.isEmpty()) {
                String[] parts = priceRange.split("-");
                if (parts.length == 2) {
                    try {
                        minPrice = Double.parseDouble(parts[0]);
                        maxPrice = Double.parseDouble(parts[1]);
                        if (minPrice > maxPrice) {
                            minPrice = null;
                            maxPrice = null;
                        }
                    } catch (NumberFormatException ignored) {}
                }
                model.addAttribute("searchPriceRange", priceRange);
            }

            // Tồn kho
            if (stock != null && !stock.isEmpty()) {
                try {
                    stockInt = Integer.parseInt(stock);
                    model.addAttribute("searchStock", stock);
                } catch (NumberFormatException ignored) {}
            }

            // Thương hiệu
            if (brand != null && !brand.trim().isEmpty()) {
                model.addAttribute("searchBrand", brand.trim());
            }

            // Danh sách & đếm
            List<Product> results = productService.searchProducts(brand, minPrice, maxPrice, stockInt, page, size);
            long total = productService.countSearchedProducts(brand, minPrice, maxPrice, stockInt);
            int totalPages = (int) Math.ceil((double) total / size);

            if (results.isEmpty()) {
                model.addAttribute("infoMessage", "Không tìm thấy sản phẩm phù hợp với tiêu chí tìm kiếm.");
            }

            model.addAttribute("isSearch", true);
            model.addAttribute("products", results);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("size", size);
            model.addAttribute("content", "products");
            model.addAttribute("productDTO", new ProductDTO());

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm: " + e.getMessage());
            model.addAttribute("products", new ArrayList<>());
            model.addAttribute("content", "products");
            model.addAttribute("productDTO", new ProductDTO());
        }

        return "homeAdmin";
    }

    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam("id") Integer id,
                               @RequestParam("status") boolean status) {
        productService.updateProductsStatus(id, status);
        return "redirect:/admin/products";
    }

    // Upload ảnh lên Cloudinary
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "products",
                            "resource_type", "image",
                            "quality", "auto:eco",
                            "fetch_format", "auto"
                    ));
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new IOException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
        }
    }

    private void prepareProductList(Model model, Integer page, Integer size) {
        List<Product> products = productService.getAllProducts(page, size);
        long totalItems = productService.countAllProducts();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
    }
}