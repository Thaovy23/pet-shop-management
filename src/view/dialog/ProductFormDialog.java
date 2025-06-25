package view.dialog;

import view.panel.ProductPanel;

import model.product.Food;
import model.product.Toy;
import model.product.Medicine;
import model.product.Product;

import controller.product.ProductController;

import service.product.ProductService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ProductFormDialog extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"TOY", "FOOD", "MEDICINE"});

    // Type-specific fields
    private final JTextField materialField = new JTextField(); // TOY
    private final JTextField expirationField = new JTextField(); // FOOD + MEDICINE
    private final JTextField nutritionalField = new JTextField(); // FOOD
    private final JTextField manufactureField = new JTextField(); // MEDICINE
    private final JTextField dosageField = new JTextField(); // MEDICINE

    private final ProductPanel parent;
    private final Product existing;

    private final ProductController productController = new ProductController();
    private final ProductService productService = new ProductService();

    public ProductFormDialog(ProductPanel parent, Product product) {
        super((Frame) null, true);
        this.parent = parent;
        this.existing = product;

        setTitle(product == null ? "Add Product" : "Update Product");
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0, 2, 10, 10));

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Price:")); add(priceField);
        add(new JLabel("Stock Quantity:")); add(quantityField);
        add(new JLabel("Category:")); add(categoryBox);

        add(new JLabel("Material:")); add(materialField);
        add(new JLabel("Expiration Date (YYYY-MM-DD):")); add(expirationField);
        add(new JLabel("Nutritional Info:")); add(nutritionalField);
        add(new JLabel("Manufacture Date (YYYY-MM-DD):")); add(manufactureField);
        add(new JLabel("Dosage:")); add(dosageField);

        // Nút Save và Cancel
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        add(new JLabel());  // Ô trống giữ layout
        add(buttonPanel);

        // Nếu đang sửa thì fill dữ liệu vào form
        if (product != null) {
            nameField.setText(product.getName());
            priceField.setText(product.getPrice().toPlainString());
            quantityField.setText(String.valueOf(product.getStockQuantity()));
            categoryBox.setSelectedItem(product.getClass().getSimpleName().toUpperCase());

            if (product instanceof Toy t) {
                materialField.setText(t.getMaterial());
            } else if (product instanceof Food f) {
                expirationField.setText(f.getExpirationDate().toString());
                nutritionalField.setText(f.getNutritionalInfo());
            } else if (product instanceof Medicine m) {
                expirationField.setText(m.getExpirationDate().toString());
                manufactureField.setText(m.getManufactureDate().toString());
                dosageField.setText(m.getDosage());
            }
        }

        // Bật tắt các trường theo category
        categoryBox.addActionListener(e -> toggleFields());

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String priceStr = priceField.getText().trim();
                String quantityStr = quantityField.getText().trim();
                String type = categoryBox.getSelectedItem().toString().toUpperCase();

                if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                    throw new IllegalArgumentException("Name, Price and Quantity cannot be empty");
                }

                BigDecimal price = new BigDecimal(priceStr);
                int quantity = Integer.parseInt(quantityStr);

                Product productToSave;
                switch(type) {
                    case "TOY" -> productToSave = productService.createToy(name, price, quantity, materialField.getText().trim());
                    case "FOOD" -> productToSave = productService.createFood(name, price, quantity, expirationField.getText().trim(), nutritionalField.getText().trim());
                    case "MEDICINE" -> productToSave = productService.createMedicine(name, price, quantity, dosageField.getText().trim(), manufactureField.getText().trim(), expirationField.getText().trim());
                    default -> throw new IllegalArgumentException("Unknown product type");
                }

                if (existing != null) {
                    productToSave.setId(existing.getId());
                }

                boolean success;
                if (existing == null) {
                    success = productController.addProduct(productToSave);
                } else {
                    success = productController.updateProduct(productToSave);
                }

                if (!success) {
                    JOptionPane.showMessageDialog(this, "Operation failed, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                parent.refreshTable();
                dispose();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,
                        "Price and Quantity must be numeric.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this,
                        iae.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Unexpected error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        toggleFields(); // Khởi tạo trạng thái enable/disable các trường theo category
    }

    private void toggleFields() {
        String type = categoryBox.getSelectedItem().toString();

        materialField.setEnabled(type.equals("TOY"));
        expirationField.setEnabled(type.equals("FOOD") || type.equals("MEDICINE"));
        nutritionalField.setEnabled(type.equals("FOOD"));
        manufactureField.setEnabled(type.equals("MEDICINE"));
        dosageField.setEnabled(type.equals("MEDICINE"));
    }
}
