package view.panel;

import controller.product.ProductController;
import controller.pet.PetController;
import controller.customer.CustomerController;
import controller.bill.BillingController;
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
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportsPanel extends JPanel {
    
    // Colors for consistent theme
    private final Color primaryColor = new Color(0x2C3E50);
    private final Color successColor = new Color(0x27AE60);
    private final Color infoColor = new Color(0x3498DB);
    private final Color warningColor = new Color(0xF39C12);
    private final Color dangerColor = new Color(0xE74C3C);
    private final Color cardBg = Color.WHITE;
    private final Color lightBg = new Color(0xF8F9FA);

    private JTabbedPane tabbedPane;
    private BillingController billingController;
    private UserDAO userDAO;
    
    private PetController petController = new PetController();
    private ProductController productController = new ProductController();

    public ReportsPanel() {
        billingController = new BillingController();
        userDAO = new UserDAO();
        
        initializePanel();
        setupReportTabs();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(lightBg);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void setupReportTabs() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Tabbed pane for different reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(cardBg);
        
        // Add report tabs
        tabbedPane.addTab("Sales Report", createSalesReportPanel());
        tabbedPane.addTab("Financial Report", createFinancialReportPanel());
        tabbedPane.addTab("Pet Statistics", createPetReportPanel());
        tabbedPane.addTab("Inventory Report", createInventoryReportPanel());
        tabbedPane.addTab("Customer Report", createCustomerReportPanel());
        tabbedPane.addTab("Staff Report", createStaffReportPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("Business Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Comprehensive reporting dashboard for business insights");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(0xBDC3C7));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Export buttons
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setOpaque(false);
        
        JButton exportBtn = createStyledButton("Export Data", "Export current data to CSV");
        exportBtn.addActionListener(e -> exportCurrentData());
        
        JButton refreshBtn = createStyledButton("Refresh", "Refresh all reports");
        refreshBtn.addActionListener(e -> refreshAllReports());

        exportPanel.add(refreshBtn);
        exportPanel.add(exportBtn);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(exportPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Summary cards
        JPanel summaryPanel = createSalesSummaryCards();
        
        // Charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.add(createMonthlySalesChart());
        chartsPanel.add(createTopProductsChart());

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSalesSummaryCards() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        int totalOrders = billingController.getTotalOrders();
        BigDecimal totalRevenue = billingController.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        BigDecimal avgOrderValue = totalOrders > 0 && totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        summaryPanel.add(createSummaryCard("Total Orders", String.valueOf(totalOrders), successColor));
        summaryPanel.add(createSummaryCard("Total Revenue", "$" + totalRevenue, infoColor));
        summaryPanel.add(createSummaryCard("Average Order", "$" + avgOrderValue, warningColor));
        summaryPanel.add(createSummaryCard("Best Month", billingController.getBestMonth(), primaryColor));

        return summaryPanel;
    }

    private JPanel createFinancialReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Financial metrics
        JPanel metricsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(0, 250));

        BigDecimal totalRevenue = billingController.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        // Calculate financial metrics dynamically
        BigDecimal estimatedCosts = totalRevenue.multiply(BigDecimal.valueOf(0.6)); // 60% estimated costs
        BigDecimal estimatedProfit = totalRevenue.subtract(estimatedCosts);
        
        // Calculate profit margin
        String profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
            String.format("%.1f%%", estimatedProfit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()) : "0%";

        metricsPanel.add(createSummaryCard("Total Revenue", "$" + totalRevenue, successColor));
        metricsPanel.add(createSummaryCard("Estimated Costs", "$" + estimatedCosts, dangerColor));
        metricsPanel.add(createSummaryCard("Estimated Profit", "$" + estimatedProfit, infoColor));
        metricsPanel.add(createSummaryCard("Profit Margin", profitMargin, warningColor));
        metricsPanel.add(createSummaryCardWithTooltip("Growth Rate", "Pending Analysis", successColor, "Requires minimum 3 months historical data for accurate trend calculation"));
        metricsPanel.add(createSummaryCardWithTooltip("ROI", "Under Development", primaryColor, "Comprehensive cost tracking module implementation in progress"));

        // Revenue trend chart
        JPanel chartPanel = createRevenueFlowChart();

        panel.add(metricsPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPetReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Pet statistics with actual sold pet tracking
        int totalPets = petController.getAllPets().size();
        int soldPets = billingController.getSoldPetsCount();
        int availablePets = totalPets; // Current pets available in system

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        statsPanel.add(createSummaryCard("Available Pets", String.valueOf(availablePets), infoColor));
        statsPanel.add(createSummaryCard("Sold Pets", String.valueOf(soldPets), successColor));
        statsPanel.add(createSummaryCard("Total Processed", String.valueOf(availablePets + soldPets), warningColor));

        // Pet distribution charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.add(createPetTypeChart());
        chartsPanel.add(createPetAgeChart());

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInventoryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Inventory summary - get actual stock data
        int totalProducts = productController.getAllProducts().size();
        int lowStockItems = 0;
        int outOfStockItems = 0;
        
        try {
            // Get low stock items (stock quantity <= 5)
            lowStockItems = dao.product.ProductDAO.getLowStockCount(5);
            // Get out of stock items (stock quantity = 0)
            outOfStockItems = dao.product.ProductDAO.getLowStockCount(0);
            // Subtract out of stock from low stock to avoid double counting
            lowStockItems = lowStockItems - outOfStockItems;
        } catch (Exception e) {
            System.err.println("Error getting stock information: " + e.getMessage());
        }

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        summaryPanel.add(createSummaryCard("Total Products", String.valueOf(totalProducts), infoColor));
        summaryPanel.add(createSummaryCard("Low Stock", String.valueOf(lowStockItems), warningColor));
        summaryPanel.add(createSummaryCard("Out of Stock", String.valueOf(outOfStockItems), dangerColor));
        summaryPanel.add(createSummaryCard("Well Stocked", String.valueOf(totalProducts - lowStockItems - outOfStockItems), successColor));

        // Inventory table
        JPanel tablePanel = createInventoryTable();

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        int totalCustomers = CustomerController.getAllCustomers().size();
        int activeCustomers = billingController.getActiveCustomersCount();
        int newCustomers = billingController.getNewCustomersThisMonth();

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        summaryPanel.add(createSummaryCard("Total Customers", String.valueOf(totalCustomers), infoColor));
        summaryPanel.add(createSummaryCard("Active Customers", String.valueOf(activeCustomers), successColor));
        summaryPanel.add(createSummaryCard("New This Month", String.valueOf(newCustomers), warningColor));

        // Customer activity chart
        JPanel chartPanel = createCustomerActivityChart();

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStaffReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        int totalStaff = 0;
        int totalOrders = billingController.getTotalOrders();
        BigDecimal totalRevenue = billingController.getTotalStaffRevenue();
        
        try {
            totalStaff = userDAO.getAllStaff().size();
        } catch (Exception e) {
            totalStaff = 0;
        }

        // Calculate average performance
        String avgPerformance = totalStaff > 0 ? 
            String.format("%.1f orders/staff", (double)totalOrders / totalStaff) : "No data";

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        summaryPanel.add(createSummaryCard("Total Staff", String.valueOf(totalStaff), primaryColor));
        summaryPanel.add(createSummaryCard("Total Orders", String.valueOf(totalOrders), successColor));
        summaryPanel.add(createSummaryCard("Avg Performance", avgPerformance, infoColor));

        // Staff performance chart
        JPanel chartPanel = createStaffPerformanceChart();

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    // Helper method to create summary cards
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter(), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(primaryColor);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Helper method to create summary cards with tooltip
    private JPanel createSummaryCardWithTooltip(String title, String value, Color color, String tooltip) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter(), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(primaryColor);
        titleLabel.setToolTipText(tooltip);

        JLabel valueLabel = new JLabel(value + " ℹ️", SwingConstants.CENTER); // Add info icon
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Slightly smaller for "Coming Soon"
        valueLabel.setForeground(new Color(0x95A5A6)); // Gray color for future features
        valueLabel.setToolTipText(tooltip);

        // Add explanatory text below
        JLabel explanationLabel = new JLabel("<html><center><small>" + tooltip + "</small></center></html>", SwingConstants.CENTER);
        explanationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        explanationLabel.setForeground(new Color(0x7F8C8D));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(explanationLabel, BorderLayout.SOUTH);

        return card;
    }

    // Chart creation methods
    private JPanel createMonthlySalesChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual monthly sales data from database
        try {
            java.util.Map<String, BigDecimal> monthlySales = billingController.getMonthlySales();
            
            for (java.util.Map.Entry<String, BigDecimal> entry : monthlySales.entrySet()) {
                String month = entry.getKey();
                BigDecimal sales = entry.getValue();
                dataset.addValue(sales, "Sales", month);
            }
            
        } catch (Exception e) {
            // Fallback to empty data if database error
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (String month : months) {
                dataset.addValue(0, "Sales", month);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Sales Trend", "Month", "Sales ($)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, successColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Sales Performance"));
        return chartPanel;
    }

    private JPanel createTopProductsChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Get actual product type distribution from database
        try {
            List<model.product.Product> products = productController.getAllProducts();
            java.util.Map<String, Integer> distribution = new java.util.HashMap<>();
            
            // Count products by type
            for (model.product.Product product : products) {
                String type = product.getClass().getSimpleName().toUpperCase();
                distribution.put(type, distribution.getOrDefault(type, 0) + 1);
            }
            
            int foodCount = distribution.getOrDefault("FOOD", 0);
            int toyCount = distribution.getOrDefault("TOY", 0);
            int medicineCount = distribution.getOrDefault("MEDICINE", 0);
            
            // Only add non-zero values to avoid cluttering the chart
            if (foodCount > 0) dataset.setValue("Food", foodCount);
            if (toyCount > 0) dataset.setValue("Toys", toyCount);
            if (medicineCount > 0) dataset.setValue("Medicine", medicineCount);
            
            // If no products exist, show empty message
            if (foodCount == 0 && toyCount == 0 && medicineCount == 0) {
                dataset.setValue("No products available", 1);
            }
            
        } catch (Exception e) {
            // Fallback to empty data if database error
            dataset.setValue("Error loading data", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Product Type Distribution", dataset, true, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Food", successColor);
        plot.setSectionPaint("Toys", infoColor);
        plot.setSectionPaint("Medicine", warningColor);
        plot.setSectionPaint("No products available", new Color(0x95A5A6));
        plot.setSectionPaint("Error loading data", new Color(0xE74C3C));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Product Distribution"));
        return chartPanel;
    }

    private JPanel createRevenueFlowChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual weekly revenue data from database
        try {
            java.util.Map<String, BigDecimal> weeklyRevenue = billingController.getWeeklyRevenue();
            
            for (java.util.Map.Entry<String, BigDecimal> entry : weeklyRevenue.entrySet()) {
                String week = entry.getKey();
                BigDecimal revenue = entry.getValue();
                dataset.addValue(revenue, "Revenue", week);
            }
            
        } catch (Exception e) {
            // Fallback to empty data if database error
            for (int i = 1; i <= 4; i++) {
                dataset.addValue(0, "Revenue", "Week " + i);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Weekly Revenue Flow", "Week", "Revenue ($)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, infoColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Revenue Trend"));
        return chartPanel;
    }

    private JPanel createPetTypeChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Get actual pet type distribution from database
        try {
            dao.pet.PetDAO petDAO = new dao.pet.PetDAO();
            java.util.Map<String, Integer> distribution = petDAO.getPetDistribution();
            
            int dogCount = distribution.getOrDefault("DOG", 0);
            int catCount = distribution.getOrDefault("CAT", 0);
            int otherCount = 0; // Currently system only supports DOG and CAT
            
            // Only add non-zero values to avoid cluttering the chart
            if (dogCount > 0) dataset.setValue("Dogs", dogCount);
            if (catCount > 0) dataset.setValue("Cats", catCount);
            if (otherCount > 0) dataset.setValue("Other", otherCount);
            
            // If no pets exist, show empty message
            if (dogCount == 0 && catCount == 0 && otherCount == 0) {
                dataset.setValue("No pets available", 1);
            }
            
        } catch (Exception e) {
            // Fallback to empty data if database error
            dataset.setValue("Error loading data", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Pet Type Distribution", dataset, true, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Dogs", infoColor);
        plot.setSectionPaint("Cats", dangerColor);
        plot.setSectionPaint("Other", warningColor);
        plot.setSectionPaint("No pets available", new Color(0x95A5A6));
        plot.setSectionPaint("Error loading data", new Color(0xE74C3C));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Pet Types"));
        return chartPanel;
    }

    private JPanel createPetAgeChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual pet age distribution from database
        try {
            List<model.pet.Pet> pets = petController.getAllPets();
            
            int puppy = 0, young = 0, adult = 0, senior = 0;
            
            for (model.pet.Pet pet : pets) {
                int age = pet.getAge();
                if (age <= 1) {
                    puppy++;
                } else if (age <= 3) {
                    young++;
                } else if (age <= 7) {
                    adult++;
                } else {
                    senior++;
                }
            }
            
            // Only add non-zero values
            if (puppy > 0) dataset.addValue(puppy, "Count", "Puppy/Kitten (≤1yr)");
            if (young > 0) dataset.addValue(young, "Count", "Young (2-3yrs)");
            if (adult > 0) dataset.addValue(adult, "Count", "Adult (4-7yrs)");
            if (senior > 0) dataset.addValue(senior, "Count", "Senior (8+yrs)");
            
            // If no pets exist, show message
            if (puppy == 0 && young == 0 && adult == 0 && senior == 0) {
                dataset.addValue(1, "Count", "No pets available");
            }
            
        } catch (Exception e) {
            dataset.addValue(1, "Count", "Error loading data");
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Pet Age Distribution", "Age Group", "Count", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, warningColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Age Groups"));
        return chartPanel;
    }

    private JPanel createInventoryTable() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        
        String[] columns = {"Product", "Type", "Price", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Get actual product data from database
        try {
            List<model.product.Product> products = productController.getAllProducts();
            
            if (products.isEmpty()) {
                model.addRow(new Object[]{"No products available", "N/A", "$0.00", "0"});
            } else {
                for (model.product.Product product : products) {
                    String type = product.getClass().getSimpleName();
                    model.addRow(new Object[]{
                        product.getName(),
                        type,
                        "$" + product.getPrice(),
                        "Available" // Since no stock tracking implemented yet
                    });
                }
            }
            
        } catch (Exception e) {
            model.addRow(new Object[]{"Error loading data", "Error", "$0.00", "N/A"});
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Make table non-editable
        table.setDefaultEditor(Object.class, null);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Product Inventory"));
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createCustomerActivityChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get actual customer activity data from database
        try {
            java.util.Map<String, Integer> weeklyActivity = billingController.getCustomerActivityByWeek();
            
            for (java.util.Map.Entry<String, Integer> entry : weeklyActivity.entrySet()) {
                String day = entry.getKey();
                Integer activeCustomers = entry.getValue();
                dataset.addValue(activeCustomers, "Active Customers", day);
            }
            
        } catch (Exception e) {
            // Fallback to empty data if database error
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String day : days) {
                dataset.addValue(0, "Active Customers", day);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Customer Activity (Last 1 Week)", "Day", "Active Customers", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, successColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Customer Activity (Last 1 Week)"));
        return chartPanel;
    }

    private JPanel createStaffPerformanceChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Calculate actual staff performance metrics from database
        try {
            List<model.user.Staff> allStaff = userDAO.getAllStaff();
            int totalOrders = billingController.getTotalOrders();
            BigDecimal totalRevenue = billingController.getTotalRevenue();
            
            // Calculate performance metrics
            double salesScore = totalRevenue != null ? Math.min(100, totalRevenue.doubleValue() / 10) : 0; // Scale to 0-100
            double efficiencyScore = allStaff.size() > 0 ? Math.min(100, (double)totalOrders / allStaff.size() * 10) : 0;
            double serviceScore = totalOrders > 0 ? 85.0 : 0; // Estimated score - will be replaced with customer feedback system
            
            dataset.addValue(salesScore, "Performance", "Sales");
            dataset.addValue(serviceScore, "Performance", "Service");  
            dataset.addValue(efficiencyScore, "Performance", "Efficiency");
            
        } catch (Exception e) {
            // Fallback to default values if there's an error
            dataset.addValue(0, "Performance", "Sales");
            dataset.addValue(0, "Performance", "Service");
            dataset.addValue(0, "Performance", "Efficiency");
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Staff Performance Metrics", "Metric", "Score (%)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, primaryColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Performance Overview (Service Score: Estimated)"));
        
        // Add explanation panel below chart
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        JLabel explanation = new JLabel("<html><center><small><i>Note: Service Score is estimated. Customer feedback system will be added in future updates.</i></small></center></html>");
        explanation.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        explanation.setForeground(new Color(0x7F8C8D));
        explanation.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        chartContainer.add(explanation, BorderLayout.SOUTH);
        return chartContainer;
    }

    private JButton createStyledButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(infoColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        return btn;
    }

private void exportCurrentData() {
    int selectedTab = tabbedPane.getSelectedIndex();
    String tabName = tabbedPane.getTitleAt(selectedTab);
    
    try {
        String fileName = "report_" + System.currentTimeMillis() + ".csv";
        FileWriter writer = new FileWriter(fileName);
        
        writer.write("Pet Shop Report - " + tabName + "\n");
        writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
        
        switch (selectedTab) {
            case 0: // Sales Report
                writer.write("Metric,Value\n");
                writer.write("Total Orders," + billingController.getTotalOrders() + "\n");
                writer.write("Total Revenue,$" + billingController.getTotalRevenue() + "\n");
                writer.write("Average Order Value,$" + 
                    (billingController.getTotalOrders() > 0 && billingController.getTotalRevenue() != null ? 
                        billingController.getTotalRevenue().divide(BigDecimal.valueOf(billingController.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO) 
                + "\n");
                writer.write("Best Month," + billingController.getBestMonth() + "\n");
                break;
            
            case 1: // Financial Report
                BigDecimal totalRevenue = billingController.getTotalRevenue();
                if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
                BigDecimal estimatedCosts = totalRevenue.multiply(BigDecimal.valueOf(0.6));
                BigDecimal estimatedProfit = totalRevenue.subtract(estimatedCosts);
                String profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                    String.format("%.1f%%", estimatedProfit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()) : "0%";

                writer.write("Financial Metric,Amount\n");
                writer.write("Total Revenue,$" + totalRevenue + "\n");
                writer.write("Estimated Costs,$" + estimatedCosts + "\n");
                writer.write("Estimated Profit,$" + estimatedProfit + "\n");
                writer.write("Profit Margin," + profitMargin + "\n");
                writer.write("Growth Rate,Pending Analysis\n");
                writer.write("ROI,Under Development\n");
                break;

            case 2: // Pet Statistics
                int totalPets = petController.getAllPets().size();
                int soldPets = billingController.getSoldPetsCount();
                int availablePets = totalPets; // As per your code

                writer.write("Pet Statistic,Count\n");
                writer.write("Available Pets," + availablePets + "\n");
                writer.write("Sold Pets," + soldPets + "\n");
                writer.write("Total Processed," + (availablePets + soldPets) + "\n");
                break;

            case 3: // Inventory Report
                int totalProducts = productController.getAllProducts().size();
                int lowStockItems = 0;
                int outOfStockItems = 0;

                try {
                    lowStockItems = dao.product.ProductDAO.getLowStockCount(5);
                    outOfStockItems = dao.product.ProductDAO.getLowStockCount(0);
                    lowStockItems = lowStockItems - outOfStockItems;
                } catch (Exception e) {
                    // ignore errors
                }

                writer.write("Inventory Metric,Count\n");
                writer.write("Total Products," + totalProducts + "\n");
                writer.write("Low Stock," + lowStockItems + "\n");
                writer.write("Out of Stock," + outOfStockItems + "\n");
                writer.write("Well Stocked," + (totalProducts - lowStockItems - outOfStockItems) + "\n");
                break;

            case 4: // Customer Report
                int totalCustomers = CustomerController.getAllCustomers().size();
                int activeCustomers = billingController.getActiveCustomersCount();
                int newCustomers = billingController.getNewCustomersThisMonth();

                writer.write("Customer Metric,Count\n");
                writer.write("Total Customers," + totalCustomers + "\n");
                writer.write("Active Customers," + activeCustomers + "\n");
                writer.write("New This Month," + newCustomers + "\n");
                break;

            case 5: // Staff Report
                int totalStaff = 0;
                int totalOrders = billingController.getTotalOrders();
                BigDecimal totalStaffRevenue = billingController.getTotalStaffRevenue();

                try {
                    totalStaff = userDAO.getAllStaff().size();
                } catch (Exception e) {
                    totalStaff = 0;
                }

                writer.write("Staff Metric,Value\n");
                writer.write("Total Staff," + totalStaff + "\n");
                writer.write("Total Orders," + totalOrders + "\n");
                writer.write("Total Revenue," + (totalStaffRevenue != null ? "$" + totalStaffRevenue : "$0.00") + "\n");
                break;

            default:
                writer.write("Data exported for " + tabName + "\n");
        }
        
        writer.close();
        JOptionPane.showMessageDialog(this, "Data exported to " + fileName, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void refreshAllReports() {
        // Refresh all report data
        SwingUtilities.invokeLater(() -> {
            // Remove and re-add all tabs to refresh content
            tabbedPane.removeAll();
            tabbedPane.addTab("Sales Report", createSalesReportPanel());
            tabbedPane.addTab("Financial Report", createFinancialReportPanel());
            tabbedPane.addTab("Pet Statistics", createPetReportPanel());
            tabbedPane.addTab("Inventory Report", createInventoryReportPanel());
            tabbedPane.addTab("Customer Report", createCustomerReportPanel());
            tabbedPane.addTab("Staff Report", createStaffReportPanel());
            
            JOptionPane.showMessageDialog(this, "All reports refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}