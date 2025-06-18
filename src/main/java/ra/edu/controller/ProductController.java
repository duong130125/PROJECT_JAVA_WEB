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

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String listProducts(Model model, HttpSession session) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("content", "products");
        return "homeAdmin";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                              BindingResult bindingResult,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        // Kiểm tra đăng nhập
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        boolean isEdit = (productDTO.getId() != null && productDTO.getId() > 0);

        // Nếu có lỗi validation
        if (bindingResult.hasErrors()) {
            return returnWithError(model, productDTO, isEdit, "Có lỗi trong dữ liệu nhập vào!");
        }

        try {
            Product product;

            if (isEdit) {
                product = productService.findProductById(productDTO.getId());
                if (product == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật!");
                    return "redirect:/admin/products";
                }
            } else {
                Product existing = productService.findProductByName(productDTO.getName().trim());
                if (existing != null) {
                    return returnWithError(model, productDTO, false, "Tên sản phẩm đã tồn tại!");
                }
                product = new Product();
            }

            // Thiết lập các giá trị
            product.setName(productDTO.getName().trim());
            product.setBrand(productDTO.getBrand());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
            product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : true);

            // Xử lý ảnh
            boolean hasImage = (imageFile != null && !imageFile.isEmpty());
            if (hasImage) {
                String contentType = imageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return returnWithError(model, productDTO, isEdit, "File phải là hình ảnh!");
                }
                if (imageFile.getSize() > 10 * 1024 * 1024) {
                    return returnWithError(model, productDTO, isEdit, "File không được vượt quá 10MB!");
                }

                try {
                    String imageUrl = uploadToCloudinary(imageFile);
                    product.setImage(imageUrl);
                } catch (Exception e) {
                    return returnWithError(model, productDTO, isEdit, "Lỗi khi upload ảnh: " + e.getMessage());
                }

            } else {
                // Nếu thêm mới mà không có ảnh
                if (!isEdit) {
                    return returnWithError(model, productDTO, false, "Vui lòng chọn ảnh cho sản phẩm!");
                } else {
                    Product existingProduct = productService.findProductById(productDTO.getId());
                    if (existingProduct != null) {
                        product.setImage(existingProduct.getImage());
                    }
                }
            }

            boolean result = isEdit ? productService.updateProduct(product) : productService.addProduct(product);

            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", isEdit ? "Cập nhật sản phẩm thành công!" : "Thêm sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", isEdit ? "Cập nhật sản phẩm thất bại!" : "Thêm sản phẩm thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    private String returnWithError(Model model, ProductDTO productDTO, boolean isEdit, String errorMessage) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("openModal", true);
        model.addAttribute("errorMessage", errorMessage);
        return "admin/products";
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
            Model model,
            HttpSession session
    ) {
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
                    } catch (NumberFormatException ignored) {
                    }
                }
                model.addAttribute("searchPriceRange", priceRange);
            }

            // Xử lý tồn kho
            if (stock != null && !stock.isEmpty()) {
                try {
                    stockInt = Integer.parseInt(stock);
                    model.addAttribute("searchStock", stock);
                } catch (NumberFormatException ignored) {
                }
            }

            // Xử lý thương hiệu
            if (brand != null && !brand.trim().isEmpty()) {
                model.addAttribute("searchBrand", brand.trim());
            }

            // Tìm kiếm sản phẩm
            List<Product> results = productService.searchProducts(brand, minPrice, maxPrice, stockInt);

            if (results.isEmpty()) {
                model.addAttribute("infoMessage", "Không tìm thấy sản phẩm phù hợp với tiêu chí tìm kiếm.");
            }

            model.addAttribute("view", "products");
            model.addAttribute("products", results);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm: " + e.getMessage());
            model.addAttribute("view", "products");
            model.addAttribute("products", new ArrayList<>());
        }

        return "homeAdmin";
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
}