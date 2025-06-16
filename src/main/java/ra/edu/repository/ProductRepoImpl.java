package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Product;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepoImpl implements ProductRepo {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Product> getAllProducts() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Product";
            Query<Product> query = session.createQuery(hql, Product.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public Product findProductById(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Product.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean addProduct(Product product) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateProduct(Product product) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deleteProduct(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public Product findProductByName(String name) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Product p WHERE lower(p.name) = :name";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("name", name.toLowerCase());

            List<Product> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Product> searchProductsByBrand(String keyword) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Product p WHERE p.brand LIKE :keyword";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Product> searchPhonesByPriceRange(double minPrice, double maxPrice) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Product p WHERE p.price BETWEEN :min AND :max";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("min", minPrice);
            query.setParameter("max", maxPrice);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Product> searchPhonesInStock(int stock) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Product p WHERE p.Stock >= :stock";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("stock", stock);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }
}
