package ra.edu.dto;

import lombok.Data;
import ra.edu.utils.InvoiceStatus;


import java.util.Date;

@Data
public class InvoiceDTO {
    private int id;
    private int customer_id;
    private Date created_at;
    private double total_amount;
    private InvoiceStatus status;
}
