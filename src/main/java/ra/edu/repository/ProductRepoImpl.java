package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Customer;
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
    public List<Product> searchProducts(String brand, Double minPrice, Double maxPrice, Integer stock) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("FROM Product p WHERE 1=1");

            if (brand != null && !brand.trim().isEmpty()) {
                hql.append(" AND lower(p.brand) LIKE :brand");
            }
            if (minPrice != null && maxPrice != null && minPrice <= maxPrice) {
                hql.append(" AND p.price BETWEEN :minPrice AND :maxPrice");
            }
            if (stock != null && stock > 0) {
                hql.append(" AND p.stock >= :stock");
            }

            Query<Product> query = session.createQuery(hql.toString(), Product.class);

            if (brand != null && !brand.trim().isEmpty()) {
                query.setParameter("brand", "%" + brand.toLowerCase() + "%");
            }
            if (minPrice != null && maxPrice != null && minPrice <= maxPrice) {
                query.setParameter("minPrice", minPrice);
                query.setParameter("maxPrice", maxPrice);
            }
            if (stock != null && stock > 0) {
                query.setParameter("stock", stock);
            }

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateProductsStatus(Integer id, boolean status) {
        Session session = sessionFactory.openSession();
        try {
            Product product = findProductById(id);
            if (product != null) {
                session.beginTransaction();
                product.setStatus(status);
                session.update(product);
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
}
