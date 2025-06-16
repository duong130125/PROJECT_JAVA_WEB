package ra.edu.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Getter
@Setter
public class ProductDTO {
    private int id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "Thương hiệu không được để trống")
    private String brand;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Positive(message = "Giá sản phẩm phải lớn hơn 0")
    private Double price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Tồn kho không được nhỏ hơn 0")
    private Integer stock;

    @NotBlank(message = "Ảnh của sản phẩm không được để trống")
    private String image;

    private boolean status;
}
