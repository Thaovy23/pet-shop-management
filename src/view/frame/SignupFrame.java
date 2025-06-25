package view.frame;

import controller.user.AuthController;
import model.user.Staff;
import util.ui.PasswordFieldUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

public class SignupFrame extends JFrame {
    private final AuthController authController = new AuthController();
    
    // Modern color palette (same as LoginFrame)
    private final Color PRIMARY_COLOR = new Color(74, 144, 226);
    private final Color PRIMARY_DARK = new Color(45, 106, 179);
    private final Color ACCENT_COLOR = new Color(29, 185, 84);
    private final Color BACKGROUND_START = new Color(240, 245, 251);
    private final Color BACKGROUND_END = new Color(230, 238, 250);
    private final Color CARD_BACKGROUND = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);

    public SignupFrame() {
        System.out.println("Creating SignupFrame...");
        setupFrame();
        setupComponents();
        setVisible(true);
        System.out.println("SignupFrame created and visible.");
    }
    
    private void setupFrame() {
        setTitle("Pet Shop Management - Create Account");
        setSize(650, 1000);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Custom gradient background
        setContentPane(createGradientPanel());
        setLayout(new BorderLayout());
    }
    
    private JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_START,
                    0, getHeight(), BACKGROUND_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }
    
    private void setupComponents() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Header section
        JPanel headerPanel = createHeaderPanel();
        
        // Signup card
        JPanel signupCard = createSignupCard();
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(signupCard, BorderLayout.CENTER);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);
        
        // Wrap in scroll pane to ensure all components are visible
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Logo/Icon
        JLabel logoLabel = new JLabel("🐾");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("Join Pet Shop Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Create your staff account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createSignupCard() {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                for (int i = 0; i < 5; i++) {
                    g2d.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 15, 15);
                }
                
                // Draw card background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);
            }
        };
        
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        
        // Form title
        JLabel formTitle = new JLabel("Create Account");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields with icons
        JPanel namePanel = createFieldPanel("👤", "Full Name");
        JTextField nameField = (JTextField) namePanel.getComponent(1);
        
        JPanel emailPanel = createFieldPanel("✉️", "Email Address");
        JTextField emailField = (JTextField) emailPanel.getComponent(1);
        
        JPanel phonePanel = createFieldPanel("📱", "Phone Number");
        JTextField phoneField = (JTextField) phonePanel.getComponent(1);
        
        JPanel usernamePanel = createFieldPanel("🆔", "Username");
        JTextField usernameField = (JTextField) usernamePanel.getComponent(1);
        
        // Password fields with icons
        JPanel passwordOuterPanel = createPasswordFieldPanel("🔒", "Password");
        JPasswordField passwordField = getPasswordFieldFromPanel(passwordOuterPanel);
        
        JPanel confirmPasswordOuterPanel = createPasswordFieldPanel("🔐", "Confirm Password");
        JPasswordField confirmPasswordField = getPasswordFieldFromPanel(confirmPasswordOuterPanel);
        
        // Create account button
        JButton createButton = createModernButton("CREATE ACCOUNT", SUCCESS_COLOR, Color.WHITE);
        createButton.addActionListener(e -> handleSignup(nameField, emailField, phoneField, 
                                                         usernameField, passwordField, confirmPasswordField));
        
        // Divider
        JPanel dividerPanel = createDividerPanel();
        
        // Back to login button
        JButton backButton = createModernButton("Back to Sign In", PRIMARY_COLOR, Color.WHITE);
        backButton.addActionListener(e -> {
            System.out.println("Back to Sign In button clicked - Opening LoginFrame...");
            try {
                new LoginFrame();
                dispose();
                System.out.println("LoginFrame opened successfully, SignupFrame disposed.");
            } catch (Exception ex) {
                System.err.println("Error opening LoginFrame: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Add components with optimized spacing
        cardPanel.add(formTitle);
        cardPanel.add(Box.createVerticalStrut(20));
        
        cardPanel.add(namePanel);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(emailPanel);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(phonePanel);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(usernamePanel);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(passwordOuterPanel);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(confirmPasswordOuterPanel);
        cardPanel.add(Box.createVerticalStrut(20));
        
        cardPanel.add(createButton);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(dividerPanel);
        cardPanel.add(Box.createVerticalStrut(12));
        
        cardPanel.add(backButton);
        
        return cardPanel;
    }
    
    private JPanel createFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setPreferredSize(new Dimension(400, 50));
        
        // Icon container for better alignment
        JPanel iconContainer = new JPanel(new BorderLayout());
        iconContainer.setOpaque(false);
        iconContainer.setPreferredSize(new Dimension(50, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        iconContainer.add(iconLabel, BorderLayout.CENTER);
        
        JTextField field = createModernTextField(placeholder);
        field.setPreferredSize(new Dimension(350, 50));
        
        panel.add(iconContainer, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPasswordFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setPreferredSize(new Dimension(400, 50));
        
        // Icon container for better alignment
        JPanel iconContainer = new JPanel(new BorderLayout());
        iconContainer.setOpaque(false);
        iconContainer.setPreferredSize(new Dimension(50, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        iconContainer.add(iconLabel, BorderLayout.CENTER);
        
        JPasswordField passwordField = new JPasswordField();
        JPanel passwordPanel = PasswordFieldUtil.createPasswordFieldWithToggle(passwordField, placeholder);
        styleModernField(passwordPanel);
        passwordPanel.setPreferredSize(new Dimension(350, 50));
        
        panel.add(iconContainer, BorderLayout.WEST);
        panel.add(passwordPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPasswordField getPasswordFieldFromPanel(JPanel passwordOuterPanel) {
        JPanel passwordPanel = (JPanel) passwordOuterPanel.getComponent(1);
        // Find the JPasswordField within the PasswordFieldUtil panel
        for (Component comp : passwordPanel.getComponents()) {
            if (comp instanceof JPasswordField) {
                return (JPasswordField) comp;
            }
        }
        return null;
    }
    
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        styleModernField(field);
        
        field.setForeground(TEXT_SECONDARY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        return field;
    }
    
    private void styleModernField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
    }
    
    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(200, 45));
        
        // Hover effects
        Color originalColor = bgColor;
        Color hoverColor = bgColor.darker();
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private JPanel createDividerPanel() {
        JPanel dividerPanel = new JPanel(new BorderLayout());
        dividerPanel.setOpaque(false);
        dividerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JSeparator leftLine = new JSeparator();
        JSeparator rightLine = new JSeparator();
        leftLine.setForeground(BORDER_COLOR);
        rightLine.setForeground(BORDER_COLOR);
        
        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        orLabel.setForeground(TEXT_SECONDARY);
        orLabel.setHorizontalAlignment(SwingConstants.CENTER);
        orLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        
        dividerPanel.add(leftLine, BorderLayout.WEST);
        dividerPanel.add(orLabel, BorderLayout.CENTER);
        dividerPanel.add(rightLine, BorderLayout.EAST);
        
        return dividerPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel footerLabel = new JLabel("© 2024 Pet Shop Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(TEXT_SECONDARY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        footerPanel.add(footerLabel);
        return footerPanel;
    }
    
    private void handleSignup(JTextField nameField, JTextField emailField, JTextField phoneField,
                             JTextField usernameField, JPasswordField passwordField, JPasswordField confirmPasswordField) {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            
            // Validation
            if (name.equals("Full Name") || name.isEmpty()) {
                showErrorMessage("Please enter your full name");
                return;
            }
            
            if (email.equals("Email Address") || email.isEmpty()) {
                showErrorMessage("Please enter your email address");
                return;
            }
            
            if (phone.equals("Phone Number") || phone.isEmpty()) {
                showErrorMessage("Please enter your phone number");
                return;
            }
            
            if (username.equals("Username") || username.isEmpty()) {
                showErrorMessage("Please enter a username");
                return;
            }
            
            if (password.isEmpty()) {
                showErrorMessage("Please enter a password");
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                showErrorMessage("Passwords do not match!");
                return;
            }
            
            if (password.length() < 6) {
                showErrorMessage("Password must be at least 6 characters long");
                return;
            }
            
            Staff staff = new Staff(name, email, phone, username, new BigDecimal(0));
            
            boolean success = authController.signup(staff, password);
            if (success) {
                showSuccessMessage("Account created successfully!\nYou can now sign in with your credentials.");
                dispose();
                new LoginFrame();
            } else {
                showErrorMessage("Signup failed. Email or username might already be taken.");
            }
            
        } catch (Exception ex) {
            showErrorMessage("Invalid input: " + ex.getMessage());
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

