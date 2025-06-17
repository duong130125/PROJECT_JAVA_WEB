package ra.edu.repository;

import ra.edu.entity.Customer;

import java.util.List;

public class CustomerRepoImpl implements CustomerRepo {
    @Override
    public List<Customer> findAllCustomer() {
        return List.of();
    }

    @Override
    public Customer findCustomerById(Integer id) {
        return null;
    }

    @Override
    public boolean saveCustomer(Customer customer) {
        return false;
    }

    @Override
    public boolean deleteCustomer(Integer id) {
        return false;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return false;
    }
}
