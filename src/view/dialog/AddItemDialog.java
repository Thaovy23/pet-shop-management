package view.dialog;

import view.panel.BillingPanel;

import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;

import model.product.Product;
import model.pet.Pet;

import controller.bill.BillingController;
import controller.pet.PetController;
import controller.product.ProductController;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.util.List;

public class AddItemDialog extends JDialog {
    
    private final JComboBox<String> labelBox;
    private final JComboBox<String> typeBox;
    private final JTable itemTable;
    private final DefaultTableModel tableModel;
    private final BillingController billingController;
    private final BillingPanel parent;
    private final ProductController productController;
    private final PetController petController;


    public AddItemDialog(BillingPanel parent, BillingController controller)
    {
        super(SwingUtilities.getWindowAncestor(parent), "Add Item", ModalityType.APPLICATION_MODAL);
        
        this.parent = parent;
        
        this.billingController = controller;
        
        this.productController = new ProductController();
        
        this.petController = new PetController();


        setSize(800, 500);
        
        setLocationRelativeTo(parent);
        
        setLayout(new BorderLayout(10, 10));
        
        getContentPane().setBackground(Color.WHITE);

        // === Top filter panel ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filterPanel.setBackground(Color.WHITE);

        labelBox = new JComboBox<>(new String[]{"PRODUCT", "PET"});
        
        typeBox = new JComboBox<>();
        
        styleCombo(labelBox);
        
        styleCombo(typeBox);

        JButton loadBtn = new JButton("Load Items");
            
        styleButton(loadBtn);

        filterPanel.add(new JLabel("Label:"));
        
        filterPanel.add(labelBox);
        
        filterPanel.add(new JLabel("Type:"));  
        
        filterPanel.add(typeBox);
        
        filterPanel.add(loadBtn);

        // === Table ===
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Price", "Choose"}, 0) 
        {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 4; }
        };

        itemTable = new JTable(tableModel);
        
        itemTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(itemTable);

        // Setup button renderer and editor
        itemTable.getColumn("Choose").setCellRenderer(new ButtonCellRenderer("🛒"));
        
        itemTable.getColumn("Choose").setCellEditor(new ButtonCellEditor<>
        (
                itemTable, "choose", this::mapRowToItem, this::handleChoose, null
        ));

        // === Load Type Options ===
        labelBox.addActionListener(e -> updateTypeOptions());

        loadBtn.addActionListener(e -> loadItems());

        add(filterPanel, BorderLayout.NORTH);
        
        add(scrollPane, BorderLayout.CENTER);
        
        updateTypeOptions(); // Initial type load
        
        setVisible(true);
    }

    private void updateTypeOptions() 
    {
        typeBox.removeAllItems();
        
        switch (labelBox.getSelectedItem().toString()) 
        {
            case "PRODUCT" -> 
            {
                typeBox.addItem("TOY");
                typeBox.addItem("FOOD");
                typeBox.addItem("MEDICINE");
            }
            case "PET" ->
            {
                typeBox.addItem("DOG");
                typeBox.addItem("CAT");
            }
        }
    }

    private void loadItems() 
    {
        String label = labelBox.getSelectedItem().toString();
        
        String type = typeBox.getSelectedItem().toString();

        tableModel.setRowCount(0);

        if ("PRODUCT".equals(label))
        {
            List<Product> products = productController.getAvailableProducts(type.toUpperCase());
            
            for (Product p : products)
            {
                tableModel.addRow(new Object[]
                {
                    p.getId(), p.getName(), type, p.getPrice(), "🛒"
                });
            }
        } 
        else
        {
            List<Pet> pets = petController.getPetsByFilter(type.toUpperCase(), null);
            
            for (Pet p : pets)
            {
                tableModel.addRow(new Object[]
                {
                    p.getId(), p.getName(), type, p.getPrice(), "🛒"
                });
            }
        }
    }

    private Object mapRowToItem(DefaultTableModel model, int row)
    {
        String label = labelBox.getSelectedItem().toString();
        
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        
        if ("PRODUCT".equals(label))
        {
            return productController.getAllProducts().stream()
                .filter(p -> p.getId() == id)
                .findFirst().orElseThrow();
        } 
        else 
        {
            return petController.getAllPets().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst().orElseThrow();
        }
    }

    private void handleChoose(Object obj) 
    {
        switch (obj) {
            case Product p -> 
            {
                int qty = askQuantity(p.getStockQuantity());
                
                if (qty > 0)
                {
                    billingController.addProductToCart(p, qty);
                    parent.refreshCart();
                    dispose();
                }
            }
            case Pet pet -> 
            {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Add pet " + pet.getName() + " to cart?", "Confirm", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) 
                {
                    billingController.addPetToCart(pet); // pets are qty 1
                    
                    parent.refreshCart();
                    
                    dispose();
                }
            }
            default -> {}
        }
    }

    private int askQuantity(int maxQty) 
    {
        String input = JOptionPane.showInputDialog(this, "Enter quantity (max: " + maxQty + "):", 1);
        
        try 
        {
            int qty = Integer.parseInt(input);
            
            return (qty > 0 && qty <= maxQty) ? qty : 0;
        } 
        catch (Exception e)
        {
            return 0;
        }
    }

    private void styleButton(JButton btn) 
    {
        btn.setBackground(new Color(0x007BFF));
        
        btn.setForeground(Color.WHITE);
        
        btn.setFocusPainted(false);
        
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private void styleCombo(JComboBox<String> combo) 
    {
        combo.setPreferredSize(new Dimension(130, 30));
        
        combo.setBackground(Color.WHITE);
        
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }
}
