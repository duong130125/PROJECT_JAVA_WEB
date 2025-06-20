package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Invoice;
import ra.edu.repository.InvoiceRepo;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Override
    public List<Invoice> findAllInvoice() {
        return invoiceRepo.findAllInvoice();
    }

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
    public List<Invoice> findInvoiceByCustomerName(String name) {
        return invoiceRepo.findInvoiceByCustomerName(name);
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
}
