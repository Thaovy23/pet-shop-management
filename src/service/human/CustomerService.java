package service.human;

import dao.customer.CustomerDAO;
import model.user.Customer;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerService {
    
    private static final CustomerDAO customerDAO = new CustomerDAO();
    
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,11}$" // Vietnamese phone number format
    );

    public void validateCustomerData(String name, String email, String phone, int loyalty) {
        // Basic validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        
        // Format validation
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Phone number must contain 10-11 digits");
        }
        
        if (loyalty < 0) {
            throw new IllegalArgumentException("Loyalty points cannot be negative");
        }
        
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Customer name must have at least 2 characters");
        }
        
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Customer name cannot exceed 100 characters");
        }
    }
    
    public void validateCustomerForCreate(String name, String email, String phone, int loyalty) {
        // Validate basic data first
        validateCustomerData(name, email, phone, loyalty);
        
        // Check for duplicates
        try {
            List<Customer> existingCustomers = customerDAO.getAllCustomers();
            
            boolean emailExists = existingCustomers.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email.trim()));
            
            if (emailExists) {
                throw new IllegalArgumentException("This email is already in use");
            }
            
            boolean phoneExists = existingCustomers.stream()
                .anyMatch(c -> c.getPhone().equals(phone.trim()));
            
            if (phoneExists) {
                throw new IllegalArgumentException("This phone number is already in use");
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking for duplicate data: " + e.getMessage());
        }
    }
    
    public void validateCustomerForUpdate(Customer existingCustomer, String name, String email, String phone, int loyalty) {
        // Validate basic data first
        validateCustomerData(name, email, phone, loyalty);
        
        // Check for duplicates (excluding current customer)
        try {
            List<Customer> allCustomers = customerDAO.getAllCustomers();
            
            boolean emailExists = allCustomers.stream()
                .anyMatch(c -> c.getId() != existingCustomer.getId() && 
                              c.getEmail().equalsIgnoreCase(email.trim()));
            
            if (emailExists) {
                throw new IllegalArgumentException("This email is already used by another customer");
            }
            
            boolean phoneExists = allCustomers.stream()
                .anyMatch(c -> c.getId() != existingCustomer.getId() && 
                              c.getPhone().equals(phone.trim()));
            
            if (phoneExists) {
                throw new IllegalArgumentException("This phone number is already used by another customer");
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking for duplicate data: " + e.getMessage());
        }
    }
}
