package ra.edu.service;

import ra.edu.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAllCustomer();
    Customer findCustomerById(Integer id);
    boolean saveCustomer(Customer customer);
    boolean deleteCustomer(Integer id);
    boolean updateCustomer(Customer customer);
    List<Customer> findCustomerByName(String name);
}
