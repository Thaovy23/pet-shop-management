package view.panel;

import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;
import model.billing.BillItem;
import model.billing.Bill;
import model.user.Customer;
import controller.user.AuthController;
import controller.bill.BillingController;
import controller.customer.CustomerController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import view.dialog.AddItemDialog;

public class BillingPanel extends JPanel {
    private final JTable cartTable;
    private final DefaultTableModel tableModel;
    private final JTextField totalField;
    private final JLabel customerLabel;
    private final BillingController billingController = new BillingController();
    private Customer selectedCustomer;

    public BillingPanel() {
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
        
        JLabel titleLabel = new JLabel("Billing & Checkout");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Process sales transactions and generate invoices");
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

        // === Left Panel (Action Buttons) ===
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        
        JButton addBtn = createModernButton("+ Add Item", new Color(40, 167, 69), Color.WHITE);
        addBtn.setPreferredSize(new Dimension(130, 42));
        
        JButton chooseCustomerBtn = createModernButton("Select Customer", new Color(23, 162, 184), Color.WHITE);
        chooseCustomerBtn.setPreferredSize(new Dimension(160, 42));

        leftPanel.add(addBtn);
        leftPanel.add(chooseCustomerBtn);
        
        topPanel.add(leftPanel, BorderLayout.WEST);

        // === Right Panel (Customer Info) ===
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        customerLabel = new JLabel("No customer selected");
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerLabel.setForeground(new Color(108, 117, 125));
        customerLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        customerLabel.setOpaque(true);
        customerLabel.setBackground(Color.WHITE);

        rightPanel.add(customerLabel);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // === Modern Table Setup ===
        String[] columns = {"ID", "Label", "Type", "Name", "Quantity", "Price", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 6;
            }
        };
        cartTable = new JTable(tableModel);
        setupModernTable(cartTable);

        // Add buttons to table
        cartTable.getColumn("Edit").setCellRenderer(new ButtonCellRenderer("✏️"));
        cartTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

        cartTable.getColumn("Edit").setCellEditor(new ButtonCellEditor<>(
                cartTable,
                "update",
                this::mapRowToItem,
                this::handleUpdate,
                null
        ));

        cartTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
                cartTable,
                "delete",
                this::mapRowToItem,
                null,
                this::handleDelete
        ));

        // === Modern Table Container ===
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // === Bottom Panel (Total & Checkout) ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // === Total Display ===
        totalField = new JTextField();
        totalField.setPreferredSize(new Dimension(300, 45));
        totalField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalField.setEditable(false);
        totalField.setHorizontalAlignment(JTextField.CENTER);
        totalField.setText("Total: $0.00");
        totalField.setBackground(new Color(248, 249, 250));
        totalField.setForeground(new Color(44, 62, 80));
        totalField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setOpaque(false);
        totalPanel.add(totalField);
        bottomPanel.add(totalPanel, BorderLayout.WEST);

        // === Print Button ===
        JButton printBtn = createModernButton("Print Bill", new Color(220, 53, 69), Color.WHITE);
        printBtn.setPreferredSize(new Dimension(140, 45));
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel printPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        printPanel.setOpaque(false);
        printPanel.add(printBtn);
        bottomPanel.add(printPanel, BorderLayout.EAST);

        // === Action Listeners ===
        addBtn.addActionListener(e -> new AddItemDialog(this, billingController));
        
        chooseCustomerBtn.addActionListener(e -> {
            List<Customer> allCustomers = CustomerController.getAllCustomers();
            Customer selected = (Customer) JOptionPane.showInputDialog(
                    this,
                    "Select Customer:",
                    "Customer Picker",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    allCustomers.toArray(),
                    null
            );
            if (selected != null) {
                selectedCustomer = selected;
                customerLabel.setText("Customer: " + selected.getName());
                customerLabel.setForeground(new Color(40, 167, 69));
            }
        });

        printBtn.addActionListener(e -> {
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer.", "No Customer", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Shopping cart is empty.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Print this bill?\n\nCustomer: " + selectedCustomer.getName() + 
                "\nTotal: " + totalField.getText(), 
                "Confirm Checkout", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                Bill bill = billingController.finalizeBill(
                        selectedCustomer.getId(),
                        AuthController.currentUser.getId(),
                        "CASH"
                );

                try {
                    if (billingController.processBill(bill)) {
                        billingController.exportBillAsPdf(bill);
                        billingController.applyLoyaltyPoints(selectedCustomer, bill.getTotalAmount());
                        JOptionPane.showMessageDialog(this, "Bill saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshCart();
                        selectedCustomer = null;
                        customerLabel.setText("No customer selected");
                        customerLabel.setForeground(new Color(108, 117, 125));
                    } else {
                        JOptionPane.showMessageDialog(this, "Error processing bill.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // === Final Layout ===
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCart();
    }

    public void refreshCart() {
        tableModel.setRowCount(0);
        for (BillItem item : billingController.getCartItemsAsList()) {
            String label = item.getItemType().toString();
            String type = item.getType();

            tableModel.addRow(new Object[]{
                    item.getItemId(),
                    label,
                    type,
                    item.getItemName(),
                    item.getQuantity(),
                    "$" + item.getUnitPrice(),
                    "✏️",
                    "🗑️"
            });
        }
        totalField.setText("Total: $" + billingController.getCartTotal());
    }

    private BillItem mapRowToItem(DefaultTableModel model, int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String label = model.getValueAt(row, 1).toString();
        String type = model.getValueAt(row, 2).toString();
        String name = model.getValueAt(row, 3).toString();
        int quantity = Integer.parseInt(model.getValueAt(row, 4).toString());
        String priceStr = model.getValueAt(row, 5).toString().replace("$", "");
        BigDecimal price = new BigDecimal(priceStr);

        if ("PRODUCT".equals(label)) {
            return new BillItem(id, name, price, quantity, type); // productType = type
        } else {
            return new BillItem(id, name, price, type); // petType = type
        }
    }

    private void handleUpdate(BillItem item) {
        if (item.getItemType() == BillItem.ItemType.PRODUCT) {
            String input = JOptionPane.showInputDialog(this, 
                "Enter new quantity for " + item.getItemName() + ":", 
                "Update Quantity", 
                JOptionPane.QUESTION_MESSAGE);
            try {
                if (input != null && !input.trim().isEmpty()) {
                    int qty = Integer.parseInt(input.trim());
                    if (qty > 0) {
                        billingController.updateCartItem(item.getProductId(), qty);
                        refreshCart();
                    } else {
                        JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pet quantities cannot be updated.", "Cannot Update", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDelete(BillItem item) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + item.getItemName() + " from cart?",
                "Confirm Remove", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            billingController.removeCartItem(item.getItemId());
            refreshCart();
        }
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Hover effects
        Color hoverColor = bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void setupModernTable(JTable table) {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 247, 255));
        table.setSelectionForeground(new Color(44, 62, 80));
        table.setGridColor(new Color(241, 243, 245));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        
        // Cell renderer for better appearance
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return c;
            }
        };
        
        // Apply to all columns except action columns
        for (int i = 0; i < table.getColumnCount() - 2; i++) {
            table.getColumn(table.getColumnName(i)).setCellRenderer(cellRenderer);
        }
    }
}
