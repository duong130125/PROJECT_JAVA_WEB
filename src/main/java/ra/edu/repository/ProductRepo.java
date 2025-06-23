package ra.edu.repository;

import ra.edu.entity.Product;

import java.util.List;

public interface ProductRepo {
    Product findProductById(int id);
    boolean addProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    Product findProductByName(String name);
    boolean updateProductsStatus(Integer id, boolean status);
    List<Product> getAllProducts(int page, int size);
    List<Product> searchProducts(String brand, Double minPrice, Double maxPrice, Integer stock, int page, int size);
    long countAllProducts();
    long countSearchedProducts(String brand, Double minPrice, Double maxPrice, Integer stock);

}
