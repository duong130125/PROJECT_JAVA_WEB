package ra.edu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_name", nullable = false, length = 100)
    private String name;

    @Column(name = "product_brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "product_price", nullable = false)
    private double price;

    @Column(name = "product_stock", nullable = false)
    private int Stock;

    @Column(name = "product_image", nullable = false)
    private String image;

    @Column(name = "product_status", nullable = false)
    private boolean status = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
}
