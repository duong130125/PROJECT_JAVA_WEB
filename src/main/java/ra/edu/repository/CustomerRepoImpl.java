package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Customer;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepoImpl implements CustomerRepo {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Customer> findAllCustomer(Integer page, Integer size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Customer";
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setFirstResult(page * size); // bắt đầu từ index
            query.setMaxResults(size);         // số lượng bản ghi
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public Customer findCustomerById(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Customer.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean saveCustomer(Customer customer) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(customer);
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
    public boolean deleteCustomer(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            Customer customer = session.get(Customer.class, id);
            if (customer != null) {
                session.beginTransaction();
                session.delete(customer);
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
    public boolean updateCustomer(Customer customer) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(customer);
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
    public List<Customer> findCustomerByName(String name, Integer page, Integer size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Customer WHERE name LIKE :name";
            Query<Customer> query = session.createQuery(hql, Customer.class);
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
    public Customer findCustomerByPhone(String phone) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Customer WHERE phone = :phone";
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setParameter("phone", phone);
            return query.uniqueResult(); // hoặc .getSingleResult() nếu chắc chắn có 1
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Customer WHERE email = :email";
            return session.createQuery(hql, Customer.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateCustomerStatus(Integer id, boolean status) {
        Session session = sessionFactory.openSession();
        try {
            Customer customer = findCustomerById(id);
            if (customer != null) {
                session.beginTransaction();
                customer.setStatus(status);
                session.update(customer);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public Long countAllCustomer() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("SELECT COUNT(*) FROM Customer", Long.class).getSingleResult();
        } finally {
            session.close();
        }
    }

    @Override
    public Long countCustomerByName(String name) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("SELECT COUNT(*) FROM Customer WHERE name LIKE :name", Long.class)
                    .setParameter("name", "%" + name + "%")
                    .getSingleResult();
        } finally {
            session.close();
        }
    }
}
