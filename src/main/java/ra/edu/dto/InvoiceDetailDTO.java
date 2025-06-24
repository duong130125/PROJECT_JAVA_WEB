package ra.edu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceDetailDTO {
    private Integer id;
    private Integer invoice_id;
    private Integer product_id;
    private Integer quantity;
    private BigDecimal unitPrice;
}
