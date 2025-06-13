package ra.edu.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.utils.InvoiceStatus;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
public class InvoiceDTO {
    private int id;
    private int customer_id;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày tạo phải ngày hiện tại hoặc quá khứ!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created_at;
    private double total_amount;
    private InvoiceStatus status;
}
