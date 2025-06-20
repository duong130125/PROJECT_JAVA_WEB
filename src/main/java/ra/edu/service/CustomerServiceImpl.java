package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Customer;
import ra.edu.repository.CustomerRepo;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public List<Customer> findAllCustomer() {
        return customerRepo.findAllCustomer();
    }

    @Override
    public Customer findCustomerById(Integer id) {
        return customerRepo.findCustomerById(id);
    }

    @Override
    public boolean saveCustomer(Customer customer) {
        return customerRepo.saveCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(Integer id) {
        return customerRepo.deleteCustomer(id);
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return customerRepo.updateCustomer(customer);
    }

    @Override
    public List<Customer> findCustomerByName(String name) {
        return customerRepo.findCustomerByName(name);
    }

    @Override
    public Customer findCustomerByPhone(String phone) {
        return customerRepo.findCustomerByPhone(phone);
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        return customerRepo.findCustomerByEmail(email);
    }

    @Override
    public boolean updateCustomerStatus(Integer id, boolean status) {
        return customerRepo.updateCustomerStatus(id, status);
    }
}
