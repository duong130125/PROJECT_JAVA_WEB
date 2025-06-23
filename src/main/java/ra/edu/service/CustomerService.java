package ra.edu.service;

import ra.edu.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAllCustomer(Integer page, Integer size);
    Customer findCustomerById(Integer id);
    boolean saveCustomer(Customer customer);
    boolean deleteCustomer(Integer id);
    boolean updateCustomer(Customer customer);
    List<Customer> findCustomerByName(String name, Integer page, Integer size);
    Customer findCustomerByPhone(String phone);
    Customer findCustomerByEmail(String email);
    boolean updateCustomerStatus(Integer id, boolean status);
    Long countAllCustomer();
    Long countCustomerByName(String name);
}
