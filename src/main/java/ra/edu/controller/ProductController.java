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
        model.addAttribute("products", products);
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("view", "products");
        return "homeAdmin";
    }


    // Xử lý thêm/sửa sản phẩm thống nhất
    @PostMapping("/save")
    public String saveProduct(@RequestParam(value = "id", required = false) Integer id,
                              @RequestParam("name") String name,
                              @RequestParam("brand") String brand,
                              @RequestParam("price") Double price,
                              @RequestParam("stock") Integer stock,
                              @RequestParam("status") Boolean status,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            // --- VALIDATION ---
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống!");
                return "redirect:/admin/products";
            }

            if (brand == null || brand.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Thương hiệu không được để trống!");
                return "redirect:/admin/products";
            }

            if (price == null || price <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giá sản phẩm phải lớn hơn 0!");
                return "redirect:/admin/products";
            }

            if (stock == null || stock < 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số lượng tồn kho không được âm!");
                return "redirect:/admin/products";
            }

            boolean isEdit = (id != null && id > 0);
            Product existingProduct = productService.findProductByName(name.trim());

            // --- KIỂM TRA TRÙNG TÊN ---
            if (!isEdit && existingProduct != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên sản phẩm đã tồn tại!");
                return "redirect:/admin/products";
            }

            Product product;

            if (isEdit) {
                // --- CHẾ ĐỘ SỬA ---
                product = productService.findProductById(id);
                if (product == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm cần sửa!");
                    return "redirect:/admin/products";
                }

                product.setName(name.trim());
                product.setBrand(brand);
                product.setPrice(price);
                product.setStock(stock);
                product.setStatus(status);

                // Xử lý ảnh nếu có file mới
                if (imageFile != null && !imageFile.isEmpty()) {
                    String contentType = imageFile.getContentType();
                    if (contentType == null || (!contentType.startsWith("image/"))) {
                        redirectAttributes.addFlashAttribute("errorMessage", "File phải là hình ảnh (PNG, JPG, JPEG)!");
                        return "redirect:/admin/products";
                    }

                    if (imageFile.getSize() > 10 * 1024 * 1024) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Kích thước file không được vượt quá 10MB!");
                        return "redirect:/admin/products";
                    }

                    String imageUrl = uploadToCloudinary(imageFile);
                    product.setImage(imageUrl);
                }

                boolean updated = productService.updateProduct(product);
                if (updated) {
                    redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật sản phẩm thất bại!");
                }

            } else {
                // --- CHẾ ĐỘ THÊM MỚI ---
                product = new Product();
                product.setName(name.trim());
                product.setBrand(brand);
                product.setPrice(price);
                product.setStock(stock);
                product.setStatus(status != null ? status : true); // Mặc định là true

                // Xử lý ảnh
                if (imageFile != null && !imageFile.isEmpty()) {
                    String contentType = imageFile.getContentType();
                    if (contentType == null || (!contentType.startsWith("image/"))) {
                        redirectAttributes.addFlashAttribute("errorMessage", "File phải là hình ảnh (PNG, JPG, JPEG)!");
                        return "redirect:/admin/products";
                    }

                    if (imageFile.getSize() > 10 * 1024 * 1024) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Kích thước file không được vượt quá 10MB!");
                        return "redirect:/admin/products";
                    }

                    String imageUrl = uploadToCloudinary(imageFile);
                    product.setImage(imageUrl);
                }

                boolean saved = productService.addProduct(product);
                if (saved) {
                    redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Thêm sản phẩm thất bại!");
                }
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi upload ảnh: " + e.getMessage());
        } catch (Exception e) {
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

    // Tìm kiếm sản phẩm theo thương hiệu
    @GetMapping("/searchBrand")
    public String searchProductsByBrand(@RequestParam(value = "brand", required = false) String brand,
                                        Model model,
                                        HttpSession session) {

        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            List<Product> products;

            if (brand != null && !brand.trim().isEmpty()) {
                products = productService.searchProductsByBrand(brand.trim());
                model.addAttribute("searchBrand", brand.trim());

                if (products.isEmpty()) {
                    model.addAttribute("infoMessage", "Không tìm thấy sản phẩm nào với thương hiệu: " + brand);
                }
            } else {
                products = productService.getAllProducts();
                model.addAttribute("infoMessage", "Vui lòng nhập từ khóa tìm kiếm!");
            }

            model.addAttribute("view", "products");
            model.addAttribute("products", products);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm: " + e.getMessage());
            model.addAttribute("products", "products");
        }

        return "homeAdmin";
    }

    @GetMapping("/searchByPriceAndStock")
    public String searchByPriceAndStock(@RequestParam(value = "minPrice", required = false) Double minPrice,
                                        @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                        @RequestParam(value = "stock", required = false) Integer stock,
                                        Model model,
                                        HttpSession session) {
        if (session.getAttribute("adminLogin") == null) {
            return "redirect:/login";
        }

        try {
            List<Product> products;

            if (minPrice != null && maxPrice != null) {
                products = productService.searchPhonesByPriceRange(minPrice, maxPrice);
                model.addAttribute("infoMessage", "Kết quả tìm kiếm theo khoảng giá từ " + minPrice + " đến " + maxPrice);
            } else if (stock != null) {
                products = productService.searchPhonesInStock(stock);
                model.addAttribute("infoMessage", "Kết quả tìm kiếm các sản phẩm có tồn kho >= " + stock);
            } else {
                products = productService.getAllProducts();
                model.addAttribute("infoMessage", "Vui lòng nhập điều kiện tìm kiếm!");
            }

            model.addAttribute("products", products);
            model.addAttribute("view", "products");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm: " + e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("view", "products");
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

    // Chuyển DTO sang Entity (nếu cần dùng DTO)
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

    // Chuyển Entity sang DTO (nếu cần dùng DTO)
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