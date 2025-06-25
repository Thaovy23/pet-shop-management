package view.dialog;

import view.panel.PetPanel;
import controller.pet.PetController;

import model.pet.Pet;

import service.pet.PetService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class PetFormDialog extends JDialog {

    private final JTextField nameField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"DOG", "CAT"});
    private final JTextField breedField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField priceField = new JTextField();

    private final PetPanel parent;
    private final Pet existingPet;
    
    private final PetController petController = new PetController();
    private final PetService petService = new PetService();

    public PetFormDialog(PetPanel parent, Pet pet) {
        super((Frame) null, true);
        
        this.parent = parent;
        this.existingPet = pet;
  
        setTitle(pet == null ? "Add Pet" : "Update Pet");
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(7, 2, 10, 10));

        // ==== Form Layout ====
        add(new JLabel("Name:"));    add(nameField);
        add(new JLabel("Type:"));    add(typeBox);
        add(new JLabel("Breed:"));   add(breedField);
        add(new JLabel("Age:"));     add(ageField);
        add(new JLabel("Price:"));   add(priceField);
        add(new JLabel());           // Spacer

        // ==== Buttons ====
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(saveBtn);
        styleCancelButton(cancelBtn);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        add(new JLabel());  // spacer cho bên trái
        add(buttonPanel);   // gắn panel chứa 2 nút


        // Enter = Submit
        getRootPane().setDefaultButton(saveBtn);

        // ==== Load Existing Pet ====
        if (pet != null) {
            nameField.setText(pet.getName());
            breedField.setText(pet.getBreed());
            ageField.setText(String.valueOf(pet.getAge()));
            priceField.setText(pet.getPrice().toPlainString());
            typeBox.setSelectedItem(pet.getClass().getSimpleName().toUpperCase());
        }

        // ==== Cancel ====
        cancelBtn.addActionListener(e -> dispose());

        // ==== Save Action ====
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String type = (String) typeBox.getSelectedItem();
                String breed = breedField.getText().trim();
                String ageStr = ageField.getText().trim();
                String priceStr = priceField.getText().trim();

                if (name.isEmpty() || breed.isEmpty() || ageStr.isEmpty() || priceStr.isEmpty()) {
                    throw new IllegalArgumentException("All fields are required.");
                }

                int age = Integer.parseInt(ageStr);
                BigDecimal price = new BigDecimal(priceStr);

                if (age < 0) throw new IllegalArgumentException("Age cannot be negative.");
                if (price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price must be positive.");
                
                Pet petToSave = switch(type) {
                    case "DOG", "CAT" -> petService.createPet(type, name, breed, age, price);
                    default -> throw new IllegalArgumentException("Unknown pet type: " + type);
                };

                if (existingPet != null) {
                    petToSave.setId(existingPet.getId());
                    petController.updatePet(petToSave);
                } else {
                    petController.addPet(petToSave,type);
                }

                parent.refreshTable();
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Age and Price must be valid numbers.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Unexpected error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0x007BFF));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setContentAreaFilled(false); // Hoặc false nếu bạn tự vẽ nền
    }


    private void styleCancelButton(JButton btn) {
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }
}