package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Invoice;
import ra.edu.entity.InvoiceDetail;
import ra.edu.repository.InvoiceRepo;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Override
    public Invoice findInvoiceById(Integer id) {
        return invoiceRepo.findInvoiceById(id);
    }

    @Override
    public boolean saveInvoice(Invoice invoice) {
        return invoiceRepo.saveInvoice(invoice);
    }

    @Override
    public boolean deleteInvoice(Integer id) {
        return invoiceRepo.deleteInvoice(id);
    }

    @Override
    public boolean updateInvoice(Invoice invoice) {
        return invoiceRepo.updateInvoice(invoice);
    }

    @Override
    public List<Invoice> findInvoiceByCustomerName(String name, int page, int size) {
        return invoiceRepo.findInvoiceByCustomerName(name, page, size);
    }

    @Override
    public List<Invoice> findAllInvoice(int page, int size) {
        return invoiceRepo.findAllInvoice(page, size);
    }

    @Override
    public long countInvoiceByCustomerName(String name) {
        return invoiceRepo.countInvoiceByCustomerName(name);
    }

    @Override
    public long countAllInvoice() {
        return invoiceRepo.countAllInvoice();
    }

    @Override
    public BigDecimal getTotalRevenueByDay() {
        return invoiceRepo.getTotalRevenueByDay();
    }

    @Override
    public BigDecimal getTotalRevenueByMonth() {
        return invoiceRepo.getTotalRevenueByMonth();
    }

    @Override
    public BigDecimal getTotalRevenueByYear() {
        return invoiceRepo.getTotalRevenueByYear();
    }

    @Override
    public boolean saveInvoiceDetail(InvoiceDetail detail) {
       return invoiceRepo.saveInvoiceDetail(detail);
    }
}
