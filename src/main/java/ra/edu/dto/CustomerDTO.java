package ra.edu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CustomerDTO {
    private Integer id;

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String name;

    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(03|05|07|08|09|01[2|6|8|9])[0-9]{8}$",
            message = "Số điện thoại không đúng định dạng"
    )
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    private Boolean status = true;
}
