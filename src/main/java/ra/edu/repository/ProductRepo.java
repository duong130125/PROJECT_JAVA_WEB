package ra.edu.repository;

import ra.edu.entity.Product;

import java.util.List;

public interface ProductRepo {
    List<Product> getAllProducts();
    Product findProductById(int id);
    boolean addProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    Product findProductByName(String name);
    List<Product> searchProducts(String brand, Double minPrice, Double maxPrice, Integer stock);
}
