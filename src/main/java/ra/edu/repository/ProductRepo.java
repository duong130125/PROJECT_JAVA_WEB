package ra.edu.repository;

import ra.edu.entity.Product;

import java.util.List;

public interface ProductRepo {
    List<Product> getAllProducts();
    Product findProductById(int id);
    boolean addProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    List<Product> searchProductsByBrand(String keyword);
    List<Product> searchPhonesByPriceRange(double minPrice, double maxPrice);
    List<Product> searchPhonesInStock(int stock);
}
