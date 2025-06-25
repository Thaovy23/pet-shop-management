package view.panel;

import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;
import controller.customer.CustomerController;
import model.user.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;
import view.dialog.CustomerFormDialog;

public class CustomerPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> sortBox;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Modern light gray
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // === Header Panel ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Manage customer information and loyalty points");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // === Top Panel with Controls ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // === Left (Add Button) ===
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JButton addBtn = createModernButton("+ Add Customer", new Color(40, 167, 69), Color.WHITE);
        addBtn.setPreferredSize(new Dimension(150, 42));
        leftPanel.add(addBtn);
        
        topPanel.add(leftPanel, BorderLayout.WEST);

        // === Right (Search/Filter) ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);

        searchField = createModernTextField("Search by ID...", 220, 42);
        JButton searchBtn = createModernButton("Search", new Color(0, 123, 255), Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(90, 42));

        sortBox = new JComboBox<>(new String[]{"All Orders", "High Loyalty ↓", "Low Loyalty ↑"});
        styleModernComboBox(sortBox);

        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(searchBtn);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(sortBox);

        topPanel.add(filterPanel, BorderLayout.EAST);

        // === Modern Table Setup ===
        String[] columns = {"ID", "Name", "Email", "Phone", "Loyalty Points", "Edit", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 5;
            }
        };

        customerTable = new JTable(model);
        setupModernTable(customerTable);

        customerTable.getColumn("Edit").setCellRenderer(new ButtonCellRenderer("✏️"));
        customerTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

customerTable.getColumn("Edit").setCellEditor(new ButtonCellEditor<>(
    customerTable,
    "update",
    (model, row) -> {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        return CustomerController.getCustomerById(id);
    },
    customerOpt -> {
        if (customerOpt.isPresent()) 
        {
            CustomerFormDialog customerFormDialog = new CustomerFormDialog(this, customerOpt.get());
        } 
        else 
        {
            JOptionPane.showMessageDialog(this, "Customer not found.");
        }
    },
    null
));

customerTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
    customerTable,
    "delete",
    (model, row) -> {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        return CustomerController.getCustomerById(id);
    },
    null,
    optionalCustomer -> {
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete customer ID " + customer.getId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = CustomerController.deleteCustomer(customer.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully.");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot delete customer (may have related records).");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found for deletion.");
        }
    }
));


        // === Modern Table Container ===
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // === Action Listeners ===
        addBtn.addActionListener(e -> new CustomerFormDialog(this, null));

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.equals("Search by ID...") || searchText.isEmpty()) {
                loadAllCustomers();
                return;
            }
            try {
                int id = Integer.parseInt(searchText);
                Optional<Customer> c = CustomerController.getCustomerById(id);
                model.setRowCount(0);
                if (c.isPresent()) addCustomerToTable(c.get());
                else JOptionPane.showMessageDialog(this, "Customer not found.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        });

        sortBox.addActionListener(e -> applyFilter());

        loadAllCustomers();

        // === Final Layout ===
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void loadAllCustomers() {
        model.setRowCount(0);
        CustomerController.getAllCustomers().forEach(this::addCustomerToTable);
    }

    private void applyFilter() {
        String order = sortBox.getSelectedItem().toString();
        model.setRowCount(0);
        
        switch (order) {
            case "All Orders" -> loadAllCustomers();
            case "High Loyalty ↓" -> CustomerController.getCustomersByLoyalty("DESC").forEach(this::addCustomerToTable);
            case "Low Loyalty ↑" -> CustomerController.getCustomersByLoyalty("ASC").forEach(this::addCustomerToTable);
            default -> loadAllCustomers();
        }
    }

    private void addCustomerToTable(Customer c) {
        model.addRow(new Object[]{
                c.getId(), 
                c.getName(), 
                c.getEmail(), 
                c.getPhone(), 
                c.getLoyaltyPoints() + " pts", 
                "Edit", 
                "Delete"
        });
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x007BFF));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(0x0056B3), 1, true));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x0056B3));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x007BFF));
            }
        });
        return button;
    }

    private JTextField createTextField(String placeholder, int width, int height) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(width, height));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setPreferredSize(new Dimension(120, 35));
        box.setBackground(Color.WHITE);
        box.setForeground(Color.BLACK);
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public void refreshTable() {
        loadAllCustomers();
    }

    // ===== MODERN UI STYLING METHODS =====
    
    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JTextField createModernTextField(String placeholder, int width, int height) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(width, height));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(new Color(0, 123, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(33, 37, 41));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private void styleModernComboBox(JComboBox<String> box) {
        box.setPreferredSize(new Dimension(140, 42));
        box.setBackground(Color.WHITE);
        box.setForeground(new Color(33, 37, 41));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupModernTable(JTable table) {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(0, 123, 255, 30));
        table.setSelectionForeground(new Color(33, 37, 41));
        table.setGridColor(new Color(233, 236, 239));
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(233, 236, 239)));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                }
                return c;
            }
        });
    }
}