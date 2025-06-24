package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Invoice;
import ra.edu.entity.InvoiceDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InvoiceRepoImpl implements InvoiceRepo {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Invoice> findAllInvoice(int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT i FROM Invoice i JOIN FETCH i.customer";
            Query<Invoice> query = session.createQuery(hql, Invoice.class);
            query.setFirstResult(page * size); // ví dụ page=1 -> bắt đầu từ 5 nếu size=5
            query.setMaxResults(size);
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
    public List<Invoice> findInvoiceByCustomerName(String name, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT i FROM Invoice i JOIN FETCH i.customer WHERE i.customer.name LIKE :name";
            Query<Invoice> query = session.createQuery(hql, Invoice.class);
            query.setParameter("name", "%" + name + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);
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
            BigDecimal result = session.createQuery(hql, BigDecimal.class).uniqueResult();
            return result != null ? result : BigDecimal.ZERO;
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
            BigDecimal result = session.createQuery(hql, BigDecimal.class).uniqueResult();
            return result != null ? result : BigDecimal.ZERO;
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
            BigDecimal result = session.createQuery(hql, BigDecimal.class).uniqueResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean saveInvoiceDetail(InvoiceDetail detail) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(detail);
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
    public long countAllInvoice() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("SELECT COUNT(i.id) FROM Invoice i", Long.class).getSingleResult();
        } finally {
            session.close();
        }
    }

    @Override
    public long countInvoiceByCustomerName(String name) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("SELECT COUNT(i.id) FROM Invoice i WHERE i.customer.name LIKE :name", Long.class)
                    .setParameter("name", "%" + name + "%")
                    .getSingleResult();
        } finally {
            session.close();
        }
    }
}
