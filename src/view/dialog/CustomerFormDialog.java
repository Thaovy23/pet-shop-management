package view.dialog;

import view.panel.CustomerPanel;
import controller.customer.CustomerController;
import model.user.Customer;
import service.human.CustomerService;
import util.ui.ButtonStyler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.regex.Pattern;

public class CustomerFormDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField loyaltyField = new JTextField(5);
    
    // Labels for displaying validation errors
    private final JLabel nameErrorLabel = new JLabel(" ");
    private final JLabel emailErrorLabel = new JLabel(" ");
    private final JLabel phoneErrorLabel = new JLabel(" ");
    private final JLabel loyaltyErrorLabel = new JLabel(" ");

    private final CustomerPanel parent;
    private final Customer existingCustomer;
    private final CustomerService customerService = new CustomerService();
    
    // Patterns for real-time validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");

    public CustomerFormDialog(CustomerPanel parent, Customer customer) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), true);
        this.parent = parent;
        this.existingCustomer = customer;

        setTitle(customer == null ? "Add Customer" : "Update Customer");
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        initializeUI();
        setupValidation();
        populateFields();
        
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeUI() {
        // Main form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Customer Information", 
            TitledBorder.LEFT, 
            TitledBorder.TOP
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        form.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        nameErrorLabel.setForeground(Color.RED);
        nameErrorLabel.setFont(nameErrorLabel.getFont().deriveFont(Font.ITALIC, 11f));
        form.add(nameErrorLabel, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        form.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        emailErrorLabel.setForeground(Color.RED);
        emailErrorLabel.setFont(emailErrorLabel.getFont().deriveFont(Font.ITALIC, 11f));
        form.add(emailErrorLabel, gbc);

        // Phone field
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        form.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        form.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        phoneErrorLabel.setForeground(Color.RED);
        phoneErrorLabel.setFont(phoneErrorLabel.getFont().deriveFont(Font.ITALIC, 11f));
        form.add(phoneErrorLabel, gbc);

        // Loyalty field
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        form.add(new JLabel("Loyalty Points:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        form.add(loyaltyField, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        loyaltyErrorLabel.setForeground(Color.RED);
        loyaltyErrorLabel.setFont(loyaltyErrorLabel.getFont().deriveFont(Font.ITALIC, 11f));
        form.add(loyaltyErrorLabel, gbc);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton(existingCustomer == null ? "Add" : "Update");
        JButton cancelBtn = new JButton("Cancel");
        
        ButtonStyler.styleSaveButton(saveBtn);
        ButtonStyler.styleCancelButton(cancelBtn);
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> saveCustomer());
        cancelBtn.addActionListener(e -> dispose());

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void setupValidation() {
        // Real-time validation for name
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateName();
            }
        });
        
        // Real-time validation for email
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });
        
        // Real-time validation for phone
        phoneField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePhone();
            }
        });
        
        // Real-time validation for loyalty
        loyaltyField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateLoyalty();
            }
        });
    }
    
    private boolean validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameErrorLabel.setText("Name cannot be empty");
            return false;
        } else if (name.length() < 2) {
            nameErrorLabel.setText("Name must have at least 2 characters");
            return false;
        } else if (name.length() > 100) {
            nameErrorLabel.setText("Name cannot exceed 100 characters");
            return false;
        } else {
            nameErrorLabel.setText(" ");
            return true;
        }
    }
    
    private boolean validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailErrorLabel.setText("Email cannot be empty");
            return false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            emailErrorLabel.setText("Invalid email format");
            return false;
        } else {
            emailErrorLabel.setText(" ");
            return true;
        }
    }
    
    private boolean validatePhone() {
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            phoneErrorLabel.setText("Phone number cannot be empty");
            return false;
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            phoneErrorLabel.setText("Phone must contain 10-11 digits");
            return false;
        } else {
            phoneErrorLabel.setText(" ");
            return true;
        }
    }
    
    private boolean validateLoyalty() {
        try {
            int loyalty = Integer.parseInt(loyaltyField.getText().trim());
            if (loyalty < 0) {
                loyaltyErrorLabel.setText("Loyalty points cannot be negative");
                return false;
            } else {
                loyaltyErrorLabel.setText(" ");
                return true;
            }
        } catch (NumberFormatException e) {
            loyaltyErrorLabel.setText("Loyalty points must be a number");
            return false;
        }
    }
    
    private void populateFields() {
        if (existingCustomer != null) {
            nameField.setText(existingCustomer.getName());
            emailField.setText(existingCustomer.getEmail());
            phoneField.setText(existingCustomer.getPhone());
            loyaltyField.setText(String.valueOf(existingCustomer.getLoyaltyPoints()));
        } else {
            loyaltyField.setText("0");
        }
    }
    
    private void saveCustomer() {
        // Clear all error messages first
        nameErrorLabel.setText(" ");
        emailErrorLabel.setText(" ");
        phoneErrorLabel.setText(" ");
        loyaltyErrorLabel.setText(" ");
        
        // Validate all fields
        boolean isValid = validateName() & validateEmail() & validatePhone() & validateLoyalty();
        
        if (!isValid) {
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            int loyalty = Integer.parseInt(loyaltyField.getText().trim());

            String errorMessage;
            
            if (existingCustomer != null) {
                // Update existing customer
                existingCustomer.setName(name);
                existingCustomer.setEmail(email);
                existingCustomer.setPhone(phone);
                existingCustomer.setLoyaltyPoints(loyalty);
                errorMessage = CustomerController.updateCustomer(existingCustomer);
            } else {
                // Create new customer
                Customer newCustomer = new Customer(name, email, phone);
                newCustomer.setLoyaltyPoints(loyalty);
                errorMessage = CustomerController.addCustomer(newCustomer);
            }
            
            if (errorMessage == null) {
                // Success
                JOptionPane.showMessageDialog(this, 
                    existingCustomer != null ? "Customer updated successfully!" : "Customer added successfully!",
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                parent.refreshTable();
                dispose();
            } else {
                // Error
                JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Unexpected error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
