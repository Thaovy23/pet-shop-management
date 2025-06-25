package view.panel;

import controller.product.ProductController;
import model.product.Food;
import model.product.Medicine;
import model.product.Product;
import model.product.Toy;
import util.ui.ButtonCellEditor;
import util.ui.ButtonCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import view.dialog.ProductFormDialog;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> priceOrderBox;

    // Danh sách sản phẩm hiện tại được load từ DB
    private List<Product> currentProducts;

    // Controller
    private final ProductController productController = new ProductController();

    public ProductPanel() {
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
        
        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Manage food, medicines, toys and accessories inventory");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // === Control Panel ===
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(getBackground());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Left Side - Add Button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JButton addBtn = createModernButton("+ Add Product", new Color(40, 167, 69), Color.WHITE);
        addBtn.setPreferredSize(new Dimension(150, 42));
        leftPanel.add(addBtn);
        
        controlPanel.add(leftPanel, BorderLayout.WEST);

        // Right Side - Filter/Search Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);

        searchField = createModernTextField("Search by ID...", 200, 42);
        JButton searchBtn = createModernButton("Search", new Color(0, 123, 255), Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(90, 42));

        categoryBox = new JComboBox<>(new String[]{"All Types", "🧸 Toys", "🍖 Food", "💊 Medicine"});
        priceOrderBox = new JComboBox<>(new String[]{"Default Order", "Price Low→High", "Price High→Low"});
        styleModernComboBox(categoryBox);
        styleModernComboBox(priceOrderBox);

        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(searchBtn);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(categoryBox);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(priceOrderBox);

        controlPanel.add(filterPanel, BorderLayout.EAST);

        // === Modern Table Setup ===
        String[] columns = {"ID", "Name", "Type", "Quantity", "Price", "Details", "Edit", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7;
            }
        };

        productTable = new JTable(model);
        setupModernProductTable(productTable);
        
        // === Modern Table Container ===
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // === Button Renderers and Editors ===
        productTable.getColumn("Edit").setCellRenderer(new ButtonCellRenderer("✏️"));
        productTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

        productTable.getColumn("Edit").setCellEditor(new ButtonCellEditor<>(
                productTable,
                "update",
                this::mapRowToProduct,
                product -> {
                    ProductFormDialog dialog = new ProductFormDialog(this, product);
                    dialog.setVisible(true);
                    refreshTable();
                },
                null
        ));

        productTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
                productTable,
                "delete",
                this::mapRowToProduct,
                null,
                product -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete product ID " + product.getId() + "?", 
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = productController.deleteProduct(product.getId());
                        if(success) {
                            JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete product (may have related records).");
                        }
                    }
                }
        ));

        // === Action Listeners ===
        addBtn.addActionListener(e -> {
            ProductFormDialog dialog = new ProductFormDialog(this, null);
            dialog.setVisible(true);
            refreshTable();
        });

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.equals("Search by ID...") || searchText.isEmpty()) {
                loadAllProducts();
                return;
            }
            try {
                int id = Integer.parseInt(searchText);
                Product p = currentProducts.stream()
                        .filter(prod -> prod.getId() == id)
                        .findFirst().orElse(null);
                model.setRowCount(0);
                if (p != null) addProductToTable(p);
                else JOptionPane.showMessageDialog(this, "No product found with ID " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        });

        categoryBox.addActionListener(e -> applyFilters());
        priceOrderBox.addActionListener(e -> applyFilters());

        loadAllProducts();

        // === Final Layout ===
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void loadAllProducts() {
        model.setRowCount(0);
        currentProducts = productController.getAllProducts();
        currentProducts.forEach(this::addProductToTable);
    }

    private void applyFilters() {
        String category = categoryBox.getSelectedItem().toString();
        
        // Convert display text to database values
        switch (category) {
            case "All Types" -> category = null;
            case "🧸 Toys" -> category = "TOY";
            case "🍖 Food" -> category = "FOOD";
            case "💊 Medicine" -> category = "MEDICINE";
        }

        String priceOrder = priceOrderBox.getSelectedItem().toString();
        switch (priceOrder) {
            case "Default Order" -> priceOrder = null;
            case "Price Low→High" -> priceOrder = "ASC";
            case "Price High→Low" -> priceOrder = "DESC";
        }

        currentProducts = productController.getProductsByFilter(category, priceOrder);
        model.setRowCount(0);
        currentProducts.forEach(this::addProductToTable);
    }

    private void addProductToTable(Product p) {
        String details = "-";
        if (p instanceof Toy t) {
            details = "Material: " + t.getMaterial();
        } else if (p instanceof Food f) {
            details = "Exp: " + f.getExpirationDate() + ", Info: " + f.getNutritionalInfo();
        } else if (p instanceof Medicine m) {
            details = "Dose: " + m.getDosage() + ", Exp: " + m.getExpirationDate();
        }

        model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getClass().getSimpleName().toUpperCase(),
                p.getStockQuantity(),
                p.getPrice(),
                details,
                "✏️",
                "🗑️"
        });
    }

    private Product mapRowToProduct(DefaultTableModel model, int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        return currentProducts.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID " + id));
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

    public JTable getProductTable() {
        return productTable;
    }

    public void refreshTable() {
        loadAllProducts();
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
        box.setPreferredSize(new Dimension(150, 42));
        box.setBackground(Color.WHITE);
        box.setForeground(new Color(33, 37, 41));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupModernProductTable(JTable table) {
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
