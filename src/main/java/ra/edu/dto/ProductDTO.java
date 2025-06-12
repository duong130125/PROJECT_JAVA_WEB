package ra.edu.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private int id;
    private String name;
    private String brand;
    private double price;
    private int Stock;
    private String image;
}
