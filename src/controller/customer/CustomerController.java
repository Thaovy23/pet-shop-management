package controller.customer;

import dao.customer.CustomerDAO;
import model.user.Customer;
import service.human.CustomerService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerController {
    private static final CustomerDAO customerDao = new CustomerDAO();
    private static final CustomerService customerService = new CustomerService();

    // Add customer with full validation
    public static String addCustomer(Customer customer) {
        if (customer == null) {
            return "Invalid customer data";
        }
        
        try {
            // Validate data and check for duplicates
            customerService.validateCustomerForCreate(
                customer.getName(), 
                customer.getEmail(), 
                customer.getPhone(), 
                customer.getLoyaltyPoints()
            );
            
            // Trim data before saving
            customer.setName(customer.getName().trim());
            customer.setEmail(customer.getEmail().trim());
            customer.setPhone(customer.getPhone().trim());
            
            customerDao.saveCustomer(customer);
            return null; // Success - no error message
            
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (RuntimeException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        } catch (Exception e) {
            return "System error: " + e.getMessage();
        }
    }

    // Get all customers
    public static List<Customer> getAllCustomers() {
        try {
            return customerDao.getAllCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Get customer by ID returning Optional
    public static Optional<Customer> getCustomerById(int id) {
        try {
            return customerDao.getCustomerById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Get customers sorted by loyalty points (ASC or DESC)
    public static List<Customer> getCustomersByLoyalty(String order) {
        try {
            return customerDao.getByLoyaltyPoints(order);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Update customer with full validation
    public static String updateCustomer(Customer customer) {
        if (customer == null || customer.getId() <= 0) {
            return "Invalid customer data";
        }
        
        try {
            // Validate data and check for duplicates
            customerService.validateCustomerForUpdate(
                customer,
                customer.getName(), 
                customer.getEmail(), 
                customer.getPhone(), 
                customer.getLoyaltyPoints()
            );
            
            // Trim data before saving
            customer.setName(customer.getName().trim());
            customer.setEmail(customer.getEmail().trim());
            customer.setPhone(customer.getPhone().trim());
            
            boolean success = customerDao.updateCustomer(customer);
            if (success) {
                return null; // Success - no error message
            } else {
                return "Unable to update customer. Customer may have been deleted.";
            }
            
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (RuntimeException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        } catch (Exception e) {
            return "System error: " + e.getMessage();
        }
    }

    // Delete customer by ID
    public static boolean deleteCustomer(int customerId) {
        if (customerId <= 0) {
            System.err.println("[ERROR] Invalid customer ID for deletion");
            return false;
        }
        try {
            return customerDao.deleteCustomer(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
