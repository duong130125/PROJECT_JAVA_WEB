package ra.edu.service;

import ra.edu.entity.Invoice;

import java.util.List;

public interface InvoiceService {
    List<Invoice> findAllInvoice();
    Invoice findInvoiceById(Integer id);
    boolean saveInvoice(Invoice invoice);
    boolean deleteInvoice(Integer id);
    boolean updateInvoice(Invoice invoice);
    List<Invoice> findInvoiceByCustomerName(String name);

}
