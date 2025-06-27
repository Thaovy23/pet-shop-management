package view.frame;

import view.frame.HomeFrame;
import controller.user.AuthController;
import util.ui.PasswordFieldUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    
    // Modern color palette
    private final Color PRIMARY_COLOR = new Color(74, 144, 226);
    private final Color PRIMARY_DARK = new Color(45, 106, 179);
    private final Color ACCENT_COLOR = new Color(29, 185, 84);
    private final Color BACKGROUND_START = new Color(240, 245, 251);
    private final Color BACKGROUND_END = new Color(230, 238, 250);
    private final Color CARD_BACKGROUND = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color ERROR_COLOR = new Color(220, 53, 69);
    
    public LoginFrame() {
        setupFrame();
        setupComponents();
        setVisible(true);
    }
    
    private void setupFrame() {
        setTitle("Pet Shop Management - Sign In");
        setSize(600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        
        // Login card
        JPanel loginCard = createLoginCard();
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(loginCard, BorderLayout.CENTER);
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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Logo/Icon
        JLabel logoLabel = new JLabel("🐾");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("Pet Shop Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createLoginCard() {
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
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Form title
        JLabel formTitle = new JLabel("Welcome Back!");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Email field with icon
        JPanel emailPanel = createFieldPanel("✉️", "Email address");
        JTextField emailField = (JTextField) emailPanel.getComponent(1);
        
        // Password field with icon
        JPanel passwordOuterPanel = new JPanel(new BorderLayout());
        passwordOuterPanel.setOpaque(false);
        passwordOuterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordOuterPanel.setPreferredSize(new Dimension(400, 50));
        
        // Icon container for better alignment
        JPanel passwordIconContainer = new JPanel(new BorderLayout());
        passwordIconContainer.setOpaque(false);
        passwordIconContainer.setPreferredSize(new Dimension(50, 50));
        
        JLabel passwordIcon = new JLabel("🔒");
        passwordIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        passwordIcon.setHorizontalAlignment(SwingConstants.CENTER);
        passwordIcon.setVerticalAlignment(SwingConstants.CENTER);
        
        passwordIconContainer.add(passwordIcon, BorderLayout.CENTER);
        
        JPasswordField passwordField = new JPasswordField();
        JPanel passwordPanel = PasswordFieldUtil.createPasswordFieldWithToggle(passwordField, "Password");
        styleModernField(passwordPanel);
        passwordPanel.setPreferredSize(new Dimension(350, 50));
        
        passwordOuterPanel.add(passwordIconContainer, BorderLayout.WEST);
        passwordOuterPanel.add(passwordPanel, BorderLayout.CENTER);
        
        // Sign in button
        JButton signInButton = createModernButton("SIGN IN", PRIMARY_COLOR, Color.WHITE);
        signInButton.addActionListener(e -> handleLogin(emailField, passwordField));
        
        // Divider
        JPanel dividerPanel = createDividerPanel();
        
        // Sign up button
        JButton signUpButton = createModernButton("Create New Account", ACCENT_COLOR, Color.WHITE);
        System.out.println(" Sign Up button created - Text: '" + signUpButton.getText() + "', Background: " + signUpButton.getBackground());
        signUpButton.addActionListener(e -> {
            System.out.println("Sign Up button clicked - Opening SignupFrame...");
            try {
                new SignupFrame();
                dispose();               
                System.out.println("SignupFrame opened successfully, LoginFrame disposed.");
            } catch (Exception ex) {
                System.err.println("Error opening SignupFrame: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Add components
        cardPanel.add(formTitle);
        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(emailPanel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(passwordOuterPanel);
        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(signInButton);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(dividerPanel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(signUpButton);
        System.out.println(" Sign Up button added to card panel. Panel component count: " + cardPanel.getComponentCount());
        
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
        dividerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JSeparator leftLine = new JSeparator();
        JSeparator rightLine = new JSeparator();
        leftLine.setForeground(BORDER_COLOR);
        rightLine.setForeground(BORDER_COLOR);
        
        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orLabel.setForeground(TEXT_SECONDARY);
        orLabel.setHorizontalAlignment(SwingConstants.CENTER);
        orLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        dividerPanel.add(leftLine, BorderLayout.WEST);
        dividerPanel.add(orLabel, BorderLayout.CENTER);
        dividerPanel.add(rightLine, BorderLayout.EAST);
        
        return dividerPanel;
    }
    

    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel footerLabel = new JLabel("© 2025 Pet Shop Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(TEXT_SECONDARY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        footerPanel.add(footerLabel);
        return footerPanel;
    }
    
    private void handleLogin(JTextField emailField, JPasswordField passwordField) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (email.equals("Email address") || email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please fill in all fields");
            return;
        }
        
        try {
            boolean success = authController.login(email, password);
            if (success) {
                // Add a subtle success animation
                showSuccessMessage("Login successful!");
                dispose();
                new HomeFrame(AuthController.isManager());
            } else {
                showErrorMessage("Invalid email or password");
            }
        } catch (Exception e) {
            showErrorMessage("An error occurred during login");
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
