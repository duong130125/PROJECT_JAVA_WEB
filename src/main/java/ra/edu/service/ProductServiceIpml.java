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
    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
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
    public List<Product> searchProductsByBrand(String keyword) {
        return productRepo.searchProductsByBrand(keyword);
    }

    @Override
    public List<Product> searchPhonesByPriceRange(double minPrice, double maxPrice) {
        return productRepo.searchPhonesByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<Product> searchPhonesInStock(int stock) {
        return productRepo.searchPhonesInStock(stock);
    }
}
