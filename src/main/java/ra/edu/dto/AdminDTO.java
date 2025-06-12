package ra.edu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AdminDTO {
    private int id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 5, max = 50 , message = "Tên đăng nhập phải từ 5 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min =6 , message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
}
