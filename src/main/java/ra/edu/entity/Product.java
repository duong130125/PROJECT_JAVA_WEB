package ra.edu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
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
    private Integer id;

    @Column(name = "product_name", nullable = false, length = 100)
    private String name;

    @Column(name = "product_brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "product_price", nullable = false)
    private BigDecimal price;

    @Column(name = "product_stock", nullable = false)
    private Integer stock;

    @Column(name = "product_image", nullable = false)
    private String image;

    @Column(name = "product_status", nullable = false)
    private Boolean status = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
}
