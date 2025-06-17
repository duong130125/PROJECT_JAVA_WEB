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
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("view", "products");
        return "homeAdmin";
    }


    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        // Kiểm tra đăng nhập admin
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        boolean isEdit = (productDTO.getId() != null && productDTO.getId() > 0);

        // Thêm các attribute cần thiết cho modal
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("isEdit", isEdit);

        // Kiểm tra lỗi validation từ @Valid
        if (bindingResult.hasErrors()) {
            // Lấy danh sách sản phẩm để hiển thị lại trang
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("openModal", true); // Mở lại modal khi có lỗi

            // Log errors for debugging
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("Validation error: " + error.getDefaultMessage());
            });

            return "redirect:admin/products"; // Trả về trang hiện tại với modal mở
        }

        try {
            Product product;

            if (isEdit) {
                // Chế độ chỉnh sửa
                product = productService.findProductById(productDTO.getId());
                if (product == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật!");
                    return "redirect:/admin/products";
                }
            } else {
                // Chế độ thêm mới - kiểm tra tên trùng
                Product existingByName = productService.findProductByName(productDTO.getName().trim());
                if (existingByName != null) {
                    List<Product> products = productService.getAllProducts();
                    model.addAttribute("products", products);
                    model.addAttribute("openModal", true);
                    model.addAttribute("errorMessage", "Tên sản phẩm đã tồn tại!");
                    return "redirect:/admin/products";
                }
                product = new Product();
            }

            // Gán các trường cơ bản
            product.setName(productDTO.getName().trim());
            product.setBrand(productDTO.getBrand());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
            product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : true);

            // Xử lý upload ảnh
            MultipartFile imageFile = productDTO.getImageFile();
            boolean hasNewImage = (imageFile != null && !imageFile.isEmpty());

            if (hasNewImage) {
                // Validate file type
                String contentType = imageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    List<Product> products = productService.getAllProducts();
                    model.addAttribute("products", products);
                    model.addAttribute("openModal", true);
                    model.addAttribute("errorMessage", "File phải là hình ảnh!");
                    return "redirect:/admin/products";
                }

                // Validate file size (10MB)
                if (imageFile.getSize() > 10 * 1024 * 1024) {
                    List<Product> products = productService.getAllProducts();
                    model.addAttribute("products", products);
                    model.addAttribute("openModal", true);
                    model.addAttribute("errorMessage", "File không được vượt quá 10MB!");
                    return "redirect:/admin/products";
                }

                // Upload to Cloudinary
                try {
                    String imageUrl = uploadToCloudinary(imageFile);
                    product.setImage(imageUrl);
                } catch (Exception e) {
                    List<Product> products = productService.getAllProducts();
                    model.addAttribute("products", products);
                    model.addAttribute("openModal", true);
                    model.addAttribute("errorMessage", "Lỗi khi upload ảnh: " + e.getMessage());
                    return "redirect:/admin/products";
                }
            } else {
                // Không có ảnh mới được upload
                if (!isEdit) {
                    // Thêm mới mà không có ảnh
                    List<Product> products = productService.getAllProducts();
                    model.addAttribute("products", products);
                    model.addAttribute("openModal", true);
                    model.addAttribute("errorMessage", "Vui lòng chọn ảnh cho sản phẩm!");
                    return "redirect:/admin/products";
                } else {
                    // Chỉnh sửa mà không upload ảnh mới - giữ ảnh cũ
                    Product existingProduct = productService.findProductById(productDTO.getId());
                    if (existingProduct != null && existingProduct.getImage() != null) {
                        product.setImage(existingProduct.getImage());
                    }
                }
            }

            // Lưu vào database
            boolean result;
            if (isEdit) {
                result = productService.updateProduct(product);
            } else {
                result = productService.addProduct(product);
            }

            if (result) {
                String successMessage = isEdit ? "Cập nhật sản phẩm thành công!" : "Thêm sản phẩm thành công!";
                redirectAttributes.addFlashAttribute("successMessage", successMessage);
            } else {
                String errorMessage = isEdit ? "Cập nhật sản phẩm thất bại!" : "Thêm sản phẩm thất bại!";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            }

        } catch (Exception e) {
            // Log exception
            System.err.println("Error saving product: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
        }

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