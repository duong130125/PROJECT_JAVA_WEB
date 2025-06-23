package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Product;
import ra.edu.repository.ProductRepo;

import java.util.List;

@Service
public class ProductServiceIpml implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public List<Product> getAllProducts(int page, int size) {
        return productRepo.getAllProducts(page, size);
    }

    @Override
    public Product findProductById(int id) {
        return productRepo.findProductById(id);
    }

    @Override
    public boolean addProduct(Product product) {
        return productRepo.addProduct(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        return productRepo.updateProduct(product);
    }

    @Override
    public boolean deleteProduct(int id) {
        return productRepo.deleteProduct(id);
    }

    @Override
    public Product findProductByName(String name) {
        return  productRepo.findProductByName(name);
    }

    @Override
    public List<Product> searchProducts(String brand, Double minPrice, Double maxPrice, Integer stock, int page, int size) {
        return productRepo.searchProducts(brand, minPrice, maxPrice, stock, page, size);
    }

    @Override
    public long countAllProducts() {
        return productRepo.countAllProducts();
    }

    @Override
    public long countSearchedProducts(String brand, Double minPrice, Double maxPrice, Integer stock) {
        return productRepo.countSearchedProducts(brand, minPrice, maxPrice, stock);
    }

    @Override
    public boolean updateProductsStatus(Integer id, boolean status) {
        return productRepo.updateProductsStatus(id, status);
    }
}
