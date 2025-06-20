package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InvoiceRepoImpl implements InvoiceRepo {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Invoice> findAllInvoice() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT i FROM Invoice i JOIN FETCH i.customer";
            Query<Invoice> query = session.createQuery(hql, Invoice.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public Invoice findInvoiceById(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Invoice.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean saveInvoice(Invoice invoice) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(invoice);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deleteInvoice(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            Invoice invoice = session.get(Invoice.class, id);
            if (invoice != null) {
                session.beginTransaction();
                session.delete(invoice);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateInvoice(Invoice invoice) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(invoice);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Invoice> findInvoiceByCustomerName(String name) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Invoice i WHERE i.customer.name LIKE :name";
            Query<Invoice> query = session.createQuery(hql, Invoice.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public BigDecimal getTotalRevenueByDay() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COALESCE(SUM(i.total_amount), 0) FROM Invoice i " +
                    "WHERE DATE(i.created_at) = CURRENT_DATE AND i.status = 'COMPLETED'";
            Double result = session.createQuery(hql, Double.class).uniqueResult();
            return BigDecimal.valueOf(result != null ? result : 0.0);
        } finally {
            session.close();
        }
    }

    @Override
    public BigDecimal getTotalRevenueByMonth() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COALESCE(SUM(i.total_amount), 0) FROM Invoice i " +
                    "WHERE MONTH(i.created_at) = MONTH(CURRENT_DATE) " +
                    "AND YEAR(i.created_at) = YEAR(CURRENT_DATE) AND i.status = 'COMPLETED'";
            Double result = session.createQuery(hql, Double.class).uniqueResult();
            return BigDecimal.valueOf(result != null ? result : 0.0);
        } finally {
            session.close();
        }
    }

    @Override
    public BigDecimal getTotalRevenueByYear() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COALESCE(SUM(i.total_amount), 0) FROM Invoice i " +
                    "WHERE YEAR(i.created_at) = YEAR(CURRENT_DATE) AND i.status = 'COMPLETED'";
            Double result = session.createQuery(hql, Double.class).uniqueResult();
            return BigDecimal.valueOf(result != null ? result : 0.0);
        } finally {
            session.close();
        }
    }
}
