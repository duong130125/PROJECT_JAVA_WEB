package ra.edu.repository;

import ra.edu.entity.Customer;

import java.util.List;

public interface CustomerRepo {
    List<Customer> findAllCustomer();
    Customer findCustomerById(Integer id);
    boolean saveCustomer(Customer customer);
    boolean deleteCustomer(Integer id);
    boolean updateCustomer(Customer customer);
}
