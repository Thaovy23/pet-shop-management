package view.dialog;

import controller.user.AuthController;
import controller.user.UserController;
import model.user.Manager;
import model.user.Staff;
import model.user.SysUser;

import service.users.UserService;

import util.hash.BCrypt;

import javax.swing.*;
import java.awt.*;


public class ProfileDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public ProfileDialog(Window owner) {
        super(owner, "My Profile", ModalityType.APPLICATION_MODAL);
        setSize(420, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("New Password:"));
        formPanel.add(passwordField);

        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> handleUpdate());

        add(formPanel, BorderLayout.CENTER);
        add(updateBtn, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    private void loadData() {
        SysUser user = AuthController.currentUser;

        if (user == null) {
            JOptionPane.showMessageDialog(this, "No user session found.");
            dispose();
            return;
        }

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        usernameField.setText(user.getUsername());
    }
private void handleUpdate() {
    SysUser currentUser = AuthController.currentUser;

    if (currentUser == null) return;

    String name = nameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();
    String username = usernameField.getText().trim();
    String newPassword = new String(passwordField.getPassword()).trim();

    try {
        UserService service = new UserService();

        // gọi business logic để tạo bản cập nhật user mới
        SysUser updatedUser = service.updateProfile(currentUser, name, email, phone, username, newPassword);

        // update xuống DB
        UserController controller = new UserController();
        boolean success = controller.updateUser(updatedUser);

        if (success) {
            AuthController.currentUser = updatedUser;
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.");
        }

    } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
    }
}

}
