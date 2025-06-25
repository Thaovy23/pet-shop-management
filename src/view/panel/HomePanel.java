package view.panel;

import controller.product.ProductController;
import controller.pet.PetController;
import controller.customer.CustomerController;
import controller.bill.BillingController;
import controller.user.AuthController;
import dao.user.UserDAO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class HomePanel extends JPanel {
    
    private JLabel timeLabel;
    private JLabel statusLabel;
    private Timer refreshTimer;
    private Timer timeTimer;
    private JFrame parentFrame;
    
    // Colors for modern theme
    private final Color primaryColor = new Color(0x2C3E50);
    private final Color successColor = new Color(0x27AE60);
    private final Color infoColor = new Color(0x3498DB);
    private final Color warningColor = new Color(0xF39C12);
    private final Color dangerColor = new Color(0xE74C3C);
    private final Color cardBg = Color.WHITE;
    private final Color lightBg = new Color(0xF8F9FA);
    
    private PetController petController = new PetController();
    private ProductController productController = new ProductController();

    public HomePanel() {
        initializePanel();
        setupDashboard();
        startAutoRefresh();
        startTimeUpdater();
    }
    
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(lightBg);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void setupDashboard() {
        // Main container with scroll pane for better UX
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setOpaque(false);

        // Header section
        JPanel headerPanel = createHeaderPanel();
        
        // Statistics cards section
        JPanel statsPanel = createStatsPanel();
        
        // Charts section
        JPanel chartsPanel = createChartsPanel();
        
        // Quick actions section
        JPanel actionsPanel = createQuickActionsPanel();

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with scroll
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(chartsPanel, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(0, 100));

        // Welcome section
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Analytics Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Welcome, " + AuthController.currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(new Color(0xBDC3C7));
        userLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(userLabel);

        headerPanel.add(welcomePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setOpaque(false);

        // Title
        JLabel statsTitle = new JLabel("Key Performance Indicators");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(primaryColor);
        statsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        statsContainer.add(statsTitle, BorderLayout.NORTH);

        // Stats grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        statsGrid.setOpaque(false);

        // Fetch data
        BillingController billing = new BillingController();
        UserDAO userDAO = new UserDAO();
        

 
        int totalProducts = productController.getAllProducts().size();
        int totalPets = petController.getAllPets().size();
        int totalCustomers = CustomerController.getAllCustomers().size();
        int totalOrders = billing.getTotalOrders();
        BigDecimal totalRevenue = billing.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        int totalStaff = 0;
        try {
            totalStaff = userDAO.getAllStaff().size();
        } catch (Exception e) {
            totalStaff = 0;
        }
        
        // Calculate additional stats
        BigDecimal avgOrderValue = totalOrders > 0 && totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        int lowStockProducts = getLowStockCount();

        // Create stat cards without default trend values
        statsGrid.add(createStatCard("Total Pets", String.valueOf(totalPets), successColor, "Active pets in system", ""));
        statsGrid.add(createStatCard("Products", String.valueOf(totalProducts), infoColor, "Available products", ""));
        statsGrid.add(createStatCard("Customers", String.valueOf(totalCustomers), warningColor, "Registered customers", ""));
        statsGrid.add(createStatCard("Staff", String.valueOf(totalStaff), primaryColor, "Active staff members", ""));
        
        statsGrid.add(createStatCard("Orders", String.valueOf(totalOrders), new Color(0x9B59B6), "Orders processed", ""));
        statsGrid.add(createStatCard("Revenue", "$" + totalRevenue, successColor, "Total sales revenue", ""));
        statsGrid.add(createStatCard("Avg Order", "$" + avgOrderValue, infoColor, "Average order value", ""));
        statsGrid.add(createStatCard("Low Stock", String.valueOf(lowStockProducts), dangerColor, "Products need restock", ""));

        statsContainer.add(statsGrid, BorderLayout.CENTER);
        return statsContainer;
    }

    private JPanel createChartsPanel() {
        JPanel chartsContainer = new JPanel(new BorderLayout());
        chartsContainer.setOpaque(false);
        chartsContainer.setPreferredSize(new Dimension(0, 600));

        // Title
        JLabel chartsTitle = new JLabel("Business Analytics");
        chartsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        chartsTitle.setForeground(primaryColor);
        chartsTitle.setBorder(new EmptyBorder(20, 0, 15, 0));
        chartsContainer.add(chartsTitle, BorderLayout.NORTH);

        // Charts grid
        JPanel chartsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        chartsGrid.setOpaque(false);

        // Create charts
        chartsGrid.add(createPetDistributionChart());
        chartsGrid.add(createRevenueChart());
        chartsGrid.add(createCustomerGrowthChart());
        chartsGrid.add(createInventoryChart());

        chartsContainer.add(chartsGrid, BorderLayout.CENTER);
        return chartsContainer;
    }

    private JPanel createPetDistributionChart() {
        // Create pie chart for pet distribution
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Get real pet distribution data from database
        try {
            dao.pet.PetDAO petDAO = new dao.pet.PetDAO();
            java.util.Map<String, Integer> distribution = petDAO.getPetDistribution();
            
            // Add data to dataset
            for (java.util.Map.Entry<String, Integer> entry : distribution.entrySet()) {
                String type = entry.getKey();
                Integer count = entry.getValue();
                String label = formatPetTypeLabel(type) + " (" + count + ")";
                dataset.setValue(label, count);
            }
            
            // If no pets, show empty message
            if (distribution.isEmpty()) {
                dataset.setValue("No pets available", 1);
            }
            
        } catch (Exception e) {
            dataset.setValue("Error loading data", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Pet Distribution", dataset, true, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        // Set colors for different pet types based on actual data
        for (Object key : dataset.getKeys()) {
            String keyStr = key.toString();
            if (keyStr.contains("Dog")) {
                plot.setSectionPaint(keyStr, new Color(0x3498DB));
            } else if (keyStr.contains("Cat")) {
                plot.setSectionPaint(keyStr, new Color(0xE74C3C));
            } else if (keyStr.contains("No pets")) {
                plot.setSectionPaint(keyStr, new Color(0x95A5A6));
            } else if (keyStr.contains("Error")) {
                plot.setSectionPaint(keyStr, dangerColor);
            } else {
                // For any other pet types (Others)
                plot.setSectionPaint(keyStr, new Color(0x1ABC9C));
            }
        }
        
        // Customize plot appearance
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", java.text.NumberFormat.getNumberInstance(), 
            java.text.NumberFormat.getPercentInstance()));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    // Helper method to format pet type labels
    private String formatPetTypeLabel(String type) {
        switch (type.toUpperCase()) {
            case "DOG":
                return "Dogs";
            case "CAT":
                return "Cats";
            default:
                return "Others";
        }
    }

    private JPanel createRevenueChart() {
        // Create bar chart for current month revenue by week
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual weekly revenue data for dashboard overview
        try {
            BillingController billing = new BillingController();
            java.util.Map<String, BigDecimal> weeklyRevenue = billing.getWeeklyRevenue();
            
            for (java.util.Map.Entry<String, BigDecimal> entry : weeklyRevenue.entrySet()) {
                String week = entry.getKey();
                BigDecimal revenue = entry.getValue();
                dataset.addValue(revenue, "Revenue", week);
            }
        } catch (Exception e) {
            // Fallback data if error
            for (int i = 1; i <= 4; i++) {
                dataset.addValue(0, "Revenue", "Week " + i);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "This Month Revenue", "Week", "Revenue ($)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, successColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createCustomerGrowthChart() {
        // Create line chart for customer activity over last 6 months
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual customer activity data for dashboard overview
        try {
            BillingController billing = new BillingController();
            java.util.Map<String, Integer> monthlyActivity = billing.getCustomerActivityByMonth();
            
            // Show last 6 months for dashboard overview
            String[] last6Months = {"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (String month : last6Months) {
                Integer customers = monthlyActivity.getOrDefault(month, 0);
                dataset.addValue(customers, "Active Customers", month);
            }
        } catch (Exception e) {
            // Fallback data if error
            String[] months = {"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (String month : months) {
                dataset.addValue(0, "Active Customers", month);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Customer Activity (Last 6 Months)", "Month", "Active Customers", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, infoColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createInventoryChart() {
        // Create bar chart for inventory status
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual product type distribution from database
        try {
            java.util.List<model.product.Product> products = productController.getAllProducts();
            java.util.Map<String, Integer> distribution = new java.util.HashMap<>();
            
            // Count products by type
            for (model.product.Product product : products) {
                String type = product.getClass().getSimpleName();
                distribution.put(type, distribution.getOrDefault(type, 0) + 1);
            }
            
            int foodCount = distribution.getOrDefault("Food", 0);
            int toyCount = distribution.getOrDefault("Toy", 0);
            int medicineCount = distribution.getOrDefault("Medicine", 0);
            
            // Add data to chart
            dataset.addValue(foodCount, "Count", "Food");
            dataset.addValue(toyCount, "Count", "Toys");
            dataset.addValue(medicineCount, "Count", "Medicine");
            
        } catch (Exception e) {
            dataset.addValue(0, "Count", "Error loading data");
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Product Inventory", "Product Type", "Count", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, warningColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel actionsContainer = new JPanel(new BorderLayout());
        actionsContainer.setOpaque(false);
        actionsContainer.setPreferredSize(new Dimension(0, 200));

        // Title
        JLabel actionsTitle = new JLabel("Quick Actions & Insights");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionsTitle.setForeground(primaryColor);
        actionsTitle.setBorder(new EmptyBorder(20, 0, 15, 0));
        actionsContainer.add(actionsTitle, BorderLayout.NORTH);

        JPanel actionsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        actionsGrid.setOpaque(false);

        // Recent Activity Panel
        actionsGrid.add(createRecentActivityPanel());
        
        // Quick Actions Panel
        actionsGrid.add(createActionsButtonsPanel());
        
        // System Status Panel
        actionsGrid.add(createSystemStatusPanel());

        actionsContainer.add(actionsGrid, BorderLayout.CENTER);
        return actionsContainer;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Recent Activity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(primaryColor);

        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);

        // Get real recent activities from database
        List<ActivityItem> recentActivities = getRecentActivities();
        
        if (recentActivities.isEmpty()) {
            activityList.add(createActivityItem("No recent activity", "System ready", infoColor));
        } else {
            for (ActivityItem activity : recentActivities) {
                activityList.add(createActivityItem(activity.description, activity.timeAgo, activity.color));
            }
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(activityList, BorderLayout.CENTER);

        return panel;
    }

    // Helper class for activity items
    private static class ActivityItem {
        String description;
        String timeAgo;
        Color color;
        
        ActivityItem(String description, String timeAgo, Color color) {
            this.description = description;
            this.timeAgo = timeAgo;
            this.color = color;
        }
    }

    private List<ActivityItem> getRecentActivities() {
        List<ActivityItem> activities = new ArrayList<>();
        
        try {
            // Get recent bills (last 5)
            List<model.billing.Bill> recentBills = getRecentBills(3);
            for (model.billing.Bill bill : recentBills) {
                String timeAgo = getTimeAgo(bill.getTransactionTime());
                activities.add(new ActivityItem("Order #" + bill.getId() + " completed", timeAgo, successColor));
            }
            
            // Get recent customers (last 3)
            List<model.user.Customer> recentCustomers = getRecentCustomers(2);
            for (model.user.Customer customer : recentCustomers) {
                // Since we don't have creation timestamp, we'll show them as recent
                activities.add(new ActivityItem("Customer " + customer.getName() + " registered", "Recently", infoColor));
            }
            
            // Get recent pets (last 2)
            List<model.pet.Pet> recentPets = getRecentPets(2);
            for (model.pet.Pet pet : recentPets) {
                activities.add(new ActivityItem("Pet " + pet.getName() + " added to inventory", "Recently", warningColor));
            }
            
            // Limit to 4 activities maximum
            if (activities.size() > 4) {
                activities = activities.subList(0, 4);
            }
            
        } catch (Exception e) {
            activities.add(new ActivityItem("Error loading activities", "System error", dangerColor));
        }
        
        return activities;
    }

    private List<model.billing.Bill> getRecentBills(int limit) {
        try {
            dao.bill.BillDAO billDAO = new dao.bill.BillDAO();
            return billDAO.getRecentBills(limit);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<model.user.Customer> getRecentCustomers(int limit) {
        try {
            dao.customer.CustomerDAO customerDAO = new dao.customer.CustomerDAO();
            List<model.user.Customer> allCustomers = customerDAO.getAllCustomers();
            // Get last few customers (assuming higher ID = more recent)
            if (allCustomers.size() > limit) {
                return allCustomers.subList(allCustomers.size() - limit, allCustomers.size());
            }
            return allCustomers;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<model.pet.Pet> getRecentPets(int limit) {
        try {
            dao.pet.PetDAO petDAO = new dao.pet.PetDAO();
            List<model.pet.Pet> allPets = petDAO.getAllPets();
            // Get last few pets (assuming higher ID = more recent)
            if (allPets.size() > limit) {
                return allPets.subList(allPets.size() - limit, allPets.size());
            }
            return allPets;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getTimeAgo(java.time.LocalDateTime dateTime) {
        try {
            java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());
            long minutes = duration.toMinutes();
            long hours = duration.toHours();
            long days = duration.toDays();
            
            if (days > 0) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else if (hours > 0) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (minutes > 0) {
                return minutes + " min" + (minutes > 1 ? "s" : "") + " ago";
            } else {
                return "Just now";
            }
        } catch (Exception e) {
            return "Recently";
        }
    }

    private JPanel createActionsButtonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(primaryColor);

        JPanel actionsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        actionsGrid.setOpaque(false);

        actionsGrid.add(createQuickActionButton("New Sale", "Start new sale", successColor, "bill"));
        actionsGrid.add(createQuickActionButton("Add Pet", "Register new pet", infoColor, "pet"));
        actionsGrid.add(createQuickActionButton("Inventory", "Check stock", warningColor, "product"));
        actionsGrid.add(createQuickActionButton("Reports", "View reports", primaryColor, "reports"));

        panel.add(title, BorderLayout.NORTH);
        panel.add(actionsGrid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSystemStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("System Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(primaryColor);

        JPanel statusList = new JPanel();
        statusList.setLayout(new BoxLayout(statusList, BoxLayout.Y_AXIS));
        statusList.setOpaque(false);

        // Get real system data
        String dbStatus = getDatabaseStatus();
        String memoryUsage = getMemoryUsage();
        String activeUsers = getActiveUsersCount();
        String lastBackup = getLastBackupTime();

        statusList.add(createStatusItem("Database", dbStatus, 
            dbStatus.equals("Connected") ? successColor : dangerColor));
        statusList.add(createStatusItem("Memory Usage", memoryUsage, 
            getMemoryPercentage() > 80 ? dangerColor : getMemoryPercentage() > 60 ? warningColor : successColor));
        statusList.add(createStatusItem("Active Users", activeUsers, infoColor));
        statusList.add(createStatusItem("Last Backup", lastBackup, infoColor));

        panel.add(title, BorderLayout.NORTH);
        panel.add(statusList, BorderLayout.CENTER);

        return panel;
    }

    // Helper methods for real system data
    private String getDatabaseStatus() {
        try {
            database.connection_provider.getCon().close();
            return "Connected";
        } catch (Exception e) {
            return "Disconnected";
        }
    }

    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double percentage = (double) usedMemory / maxMemory * 100;
        return String.format("%.1f%% used", percentage);
    }

    private int getMemoryPercentage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return (int) ((double) usedMemory / maxMemory * 100);
    }

    private String getActiveUsersCount() {
        try {
            UserDAO userDAO = new UserDAO();
            int staffCount = userDAO.getAllStaff().size();
            return staffCount + " registered";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getLastBackupTime() {
        // Check if backup directory exists and get last modified time
        try {
            File backupDir = new File("database");
            if (backupDir.exists()) {
                File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
                if (files != null && files.length > 0) {
                    long lastModified = 0;
                    for (File file : files) {
                        if (file.lastModified() > lastModified) {
                            lastModified = file.lastModified();
                        }
                    }
                    long hoursAgo = (System.currentTimeMillis() - lastModified) / (1000 * 60 * 60);
                    if (hoursAgo == 0) {
                        long minutesAgo = (System.currentTimeMillis() - lastModified) / (1000 * 60);
                        return minutesAgo + " min ago";
                    }
                    return hoursAgo + " hours ago";
                }
            }
            return "Not available";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private JPanel createStatCard(String title, String value, Color color, String description, String trend) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setPreferredSize(new Dimension(200, 140));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter(), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(color.brighter().brighter());
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(cardBg);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Top section with title and trend
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(primaryColor);

        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        trendLabel.setForeground(trend.startsWith("+") ? successColor : 
                                trend.startsWith("-") ? dangerColor : primaryColor);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(trendLabel, BorderLayout.EAST);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(new Color(0x7F8C8D));

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActivityItem(String activity, String time, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(color);
        colorIndicator.setPreferredSize(new Dimension(4, 20));

        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        activityLabel.setForeground(primaryColor);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        timeLabel.setForeground(new Color(0x7F8C8D));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(activityLabel, BorderLayout.NORTH);
        textPanel.add(timeLabel, BorderLayout.SOUTH);

        item.add(colorIndicator, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private JPanel createStatusItem(String label, String status, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelText.setForeground(primaryColor);

        JLabel statusText = new JLabel(status);
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusText.setForeground(color);

        item.add(labelText, BorderLayout.WEST);
        item.add(statusText, BorderLayout.EAST);

        return item;
    }

    private JButton createQuickActionButton(String text, String tooltip, Color color, String panelKey) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener to navigate to panel
        button.addActionListener(e -> {
            if (parentFrame != null && parentFrame instanceof view.frame.HomeFrame) {
                view.frame.HomeFrame homeFrame = (view.frame.HomeFrame) parentFrame;
                navigateToPanel(panelKey);
                
                // Show notification
                JOptionPane.showMessageDialog(
                    parentFrame,
                    "Navigating to " + text + " section...",
                    "Quick Action",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }
    
    private void navigateToPanel(String panelKey) {
        // This will trigger panel navigation
        SwingUtilities.invokeLater(() -> {
            Container parent = this.getParent();
            while (parent != null && !(parent instanceof view.frame.HomeFrame)) {
                parent = parent.getParent();
            }
            
            if (parent instanceof view.frame.HomeFrame) {
                // Use reflection to access the CardLayout and show the panel
                try {
                    java.lang.reflect.Field layoutField = parent.getClass().getDeclaredField("layout");
                    java.lang.reflect.Field contentPanelField = parent.getClass().getDeclaredField("contentPanel");
                    
                    layoutField.setAccessible(true);
                    contentPanelField.setAccessible(true);
                    
                    CardLayout layout = (CardLayout) layoutField.get(parent);
                    JPanel contentPanel = (JPanel) contentPanelField.get(parent);
                    
                    layout.show(contentPanel, panelKey);
                } catch (Exception ex) {
                    // Fallback: just show message
                    System.out.println("Navigating to: " + panelKey);
                }
            }
        });
    }

    private int getLowStockCount() {
        try {
            // Consider products with stock quantity <= 5 as low stock
            return dao.product.ProductDAO.getLowStockCount(5);
        } catch (Exception e) {
            // Log error and return 0 if there's an issue
            System.err.println("Error getting low stock count: " + e.getMessage());
            return 0;
        }
    }

    private void updateTime() {
        if (timeLabel != null) {
            String currentTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            );
            timeLabel.setText(currentTime);
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshDashboard());
            }
        }, 30000, 30000); // Refresh every 30 seconds
    }

    private void refreshDashboard() {
        // Refresh the entire panel to update stats and activities
        removeAll();
        setupDashboard();
        revalidate();
        repaint();
    }

    public void stopTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        if (timeTimer != null) {
            timeTimer.cancel();
        }
    }

    private void startTimeUpdater() {
        timeTimer = new Timer();
        timeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 0, 1000); // Update every second
    }
}
