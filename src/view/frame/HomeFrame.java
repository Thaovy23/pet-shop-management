package view.frame;

import view.dialog.ProfileDialog;
import view.panel.CustomerPanel;
import view.panel.StaffPanel;
import view.panel.ProductPanel;
import view.panel.PetPanel;
import view.panel.BillingPanel;
import view.panel.HomePanel;
import view.panel.ReportsPanel;
import controller.user.AuthController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeFrame extends JFrame {
    private final CardLayout layout = new CardLayout();
    private final JPanel contentPanel = new JPanel(layout);
    private JLabel statusLabel;
    private JLabel timeLabel;
    private Timer timeTimer;
    
    // Color schemes - fixed light theme
    private final Color primaryColor = new Color(0x2C3E50);
    private final Color accentColor = new Color(0x3498DB);
    private final Color successColor = new Color(0x27AE60);
    private final Color warningColor = new Color(0xF39C12);
    private final Color dangerColor = new Color(0xE74C3C);
    private final Color lightBg = new Color(0xF8F9FA);
    private final Color cardBg = Color.WHITE;

    public HomeFrame(boolean isManager) {
        initializeFrame();
        setupUI(isManager);
        startTimeUpdater();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Petshop Management System - Advanced Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        
        // Set app icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("images/petshop-icon.png"));
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(lightBg);
    }

    private void setupUI(boolean isManager) {
        // Create main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContainer.setBackground(lightBg);

        // ===== Top Header Bar =====
        JPanel headerPanel = createHeaderPanel(isManager);
        
        // ===== Sidebar =====
        JPanel sidebar = createModernSidebar(isManager);
        
        // ===== Content Area =====
        setupContentArea();
        
        // ===== Status Bar =====
        JPanel statusBar = createStatusBar();

        // Add components to main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(mainContainer);
    }

    private JPanel createHeaderPanel(boolean isManager) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Left side - Welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + AuthController.currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(isManager ? "Manager" : "Staff");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(0xBDC3C7));
        roleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(roleLabel);

        // Right side - Profile and time
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);

        // Time display
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(0xBDC3C7));
        updateTime();

        // Profile button
        JButton profileBtn = createStyledButton("Profile", "View Profile");
        profileBtn.addActionListener(e -> new ProfileDialog(this));

        controlPanel.add(timeLabel);
        controlPanel.add(Box.createHorizontalStrut(15));
        controlPanel.add(profileBtn);

        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createModernSidebar(boolean isManager) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(cardBg);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xE0E0E0)),
            new EmptyBorder(20, 15, 20, 15)
        ));

        // Navigation title
        JLabel navTitle = new JLabel("Navigation", SwingConstants.CENTER);
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navTitle.setForeground(primaryColor);
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        sidebar.add(navTitle);

        // Navigation buttons - professional single color scheme
        addModernNavButton(sidebar, "Dashboard", "home", primaryColor);
        addModernNavButton(sidebar, "Pets", "pet", primaryColor);
        addModernNavButton(sidebar, "Products", "product", primaryColor);
        addModernNavButton(sidebar, "Customers", "customer", primaryColor);
        if (isManager) {
            addModernNavButton(sidebar, "Staff", "staff", primaryColor);
        }
        addModernNavButton(sidebar, "Billing", "bill", primaryColor);

        // Spacer
        sidebar.add(Box.createVerticalGlue());

        // Quick actions section
        JLabel actionsTitle = new JLabel("Quick Actions", SwingConstants.CENTER);
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionsTitle.setForeground(primaryColor);
        actionsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionsTitle.setBorder(new EmptyBorder(10, 0, 15, 0));
        sidebar.add(actionsTitle);

        // Quick action buttons
        JButton refreshBtn = createActionButton("Refresh", warningColor);
        refreshBtn.addActionListener(e -> refreshApplication());
        
        JButton logoutBtn = createActionButton("Logout", dangerColor);
        logoutBtn.addActionListener(e -> logout());

        sidebar.add(refreshBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private void setupContentArea() {
        contentPanel.setBackground(lightBg);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Add panels with improved styling
        HomePanel homePanel = new HomePanel();
        homePanel.setParentFrame(this);
        contentPanel.add(homePanel, "home");
        contentPanel.add(new PetPanel(), "pet");
        contentPanel.add(new ProductPanel(), "product");
        contentPanel.add(new CustomerPanel(), "customer");
        contentPanel.add(new StaffPanel(), "staff");
        contentPanel.add(new BillingPanel(), "bill");
        contentPanel.add(new ReportsPanel(), "reports");

        layout.show(contentPanel, "home");
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(0xF1F2F6));
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xDDD)),
            new EmptyBorder(5, 15, 5, 15)
        ));

        // Status info
        statusLabel = new JLabel("System Ready | Connected to Database");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0x2F3542));

        // Version info
        JLabel versionLabel = new JLabel("v2.0 Enhanced");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(0x57606F));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void addModernNavButton(JPanel parent, String text, String panelKey, Color color) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 50));
        btn.setPreferredSize(new Dimension(220, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(color.brighter());
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color.brighter());
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            }
        });

        btn.addActionListener(e -> {
            layout.show(contentPanel, panelKey);
            updateStatusForPanel(text);
        });

        parent.add(btn);
        parent.add(Box.createVerticalStrut(8));
    }

    private JButton createStyledButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(accentColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(accentColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(accentColor);
            }
        });

        return btn;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void refreshApplication() {
        statusLabel.setText("Refreshing application...");
        SwingUtilities.invokeLater(() -> {
            dispose();
            new HomeFrame(AuthController.isManager());
        });
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            if (timeTimer != null) {
                timeTimer.stop();
            }
            dispose();
            new LoginFrame();
        }
    }

    private void updateStatusForPanel(String panelName) {
        statusLabel.setText("Currently viewing: " + panelName);
    }

    private void startTimeUpdater() {
        timeTimer = new Timer(1000, e -> updateTime());
        timeTimer.start();
    }

    private void updateTime() {
        if (timeLabel != null) {
            String currentTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            );
            timeLabel.setText(currentTime);
        }
    }
}