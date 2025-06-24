package ra.edu.service;

import ra.edu.entity.Invoice;
import ra.edu.entity.InvoiceDetail;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {
    Invoice findInvoiceById(Integer id);
    boolean saveInvoice(Invoice invoice);
    boolean deleteInvoice(Integer id);
    boolean updateInvoice(Invoice invoice);
    List<Invoice> findInvoiceByCustomerName(String name, int page, int size);
    List<Invoice> findAllInvoice(int page, int size);
    long countInvoiceByCustomerName(String name);
    long countAllInvoice();
    BigDecimal getTotalRevenueByDay();
    BigDecimal getTotalRevenueByMonth();
    BigDecimal getTotalRevenueByYear();
    boolean saveInvoiceDetail(InvoiceDetail detail);
}
