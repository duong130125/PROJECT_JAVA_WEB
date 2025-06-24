package ra.edu.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.utils.InvoiceStatus;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceDTO {
    private Integer id;

    @NotNull(message = "khách hàng không được để trống")
    private Integer customer_id;

    private Date created_at;

    private BigDecimal total_amount;

    private InvoiceStatus status = InvoiceStatus.PENDING;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    @Size(min = 1, message = "Vui lòng chọn ít nhất một sản phẩm")
    private List<@Valid InvoiceDetailDTO> invoiceDetails = new ArrayList<>();
}
