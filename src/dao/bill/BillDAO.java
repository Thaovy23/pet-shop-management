package dao.bill;

import model.billing.Bill;
import model.billing.BillItem;

import database.connection_provider;

import java.math.BigDecimal;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class BillDAO {
    public static boolean createBill(Bill bill, List<BillItem> items) {
        if (bill == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Bill and items must not be null or empty");
        }

        Connection conn = null;
        try {
            conn = connection_provider.getCon();
            conn.setAutoCommit(false);

            String insertBill = "INSERT INTO bills (customer_id, staff_id, total_amount, payment_method, transaction_time) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bill.getCustomerId());
                ps.setInt(2, bill.getStaffId());
                ps.setBigDecimal(3, bill.getTotalAmount());
                ps.setString(4, bill.getPaymentMethod());
                ps.setTimestamp(5, Timestamp.valueOf(bill.getTransactionTime()));

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating bill failed, no rows affected.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        bill.setId(rs.getInt(1));
                    } else {
                        throw new SQLException("Creating bill failed, no ID obtained.");
                    }
                }
            }

            String insertItem = "INSERT INTO bill_items (bill_id, item_type, pet_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (BillItem item : items) {
                    ps.setInt(1, bill.getId());
                    ps.setString(2, item.getItemType().name());

                    if (item.getItemType() == BillItem.ItemType.PRODUCT) {
                        ps.setNull(3, Types.INTEGER);
                        ps.setInt(4, item.getProductId());
                    } else {
                        ps.setInt(3, item.getPetId());
                        ps.setNull(4, Types.INTEGER);
                    }
                    ps.setInt(5, item.getQuantity());
                    ps.setBigDecimal(6, item.getUnitPrice());

                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);

            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Home panel - tổng doanh thu
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;

        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    // Home panel - tổng số đơn hàng
    public int getTotalOrderCount() {
        String sql = "SELECT COUNT(*) FROM bills";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // Delete bill (hard delete since no status column)
    public boolean deleteBill(int billId) {
        if (billId <= 0) return false;

        String sql = "DELETE FROM bills WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    // Get staff performance metrics
    public int getOrdersByStaff(int staffId) {
        String sql = "SELECT COUNT(*) FROM bills WHERE staff_id = ?";
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public BigDecimal getRevenueByStaff(int staffId) {
        String sql = "SELECT SUM(total_amount) FROM bills WHERE staff_id = ?";
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? (rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getTotalStaffRevenue() {
        String sql = "SELECT staff_id, SUM(total_amount) as revenue FROM bills GROUP BY staff_id";
        BigDecimal total = BigDecimal.ZERO;
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal("revenue");
                if (revenue != null) {
                    total = total.add(revenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // Get recent bills for activity panel
    public List<Bill> getRecentBills(int limit) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT id, customer_id, staff_id, total_amount, payment_method, transaction_time " +
                    "FROM bills ORDER BY transaction_time DESC LIMIT ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill(
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("payment_method")
                    );
                    bill.setId(rs.getInt("id"));
                    bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                    bill.setTransactionTime(rs.getTimestamp("transaction_time").toLocalDateTime());
                    bills.add(bill);
                }
            }
        }
        return bills;
    }

    // Customer activity tracking methods
    public java.util.Map<String, Integer> getCustomerActivityByMonth() {
        java.util.Map<String, Integer> monthlyActivity = new java.util.LinkedHashMap<>();
        String sql = "SELECT " +
                    "MONTH(transaction_time) as month, " +
                    "COUNT(DISTINCT customer_id) as active_customers " +
                    "FROM bills " +
                    "WHERE YEAR(transaction_time) = YEAR(CURRENT_DATE) " +
                    "GROUP BY MONTH(transaction_time) " +
                    "ORDER BY MONTH(transaction_time)";
        
        // Initialize all months with 0
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : monthNames) {
            monthlyActivity.put(month, 0);
        }
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int month = rs.getInt("month");
                int activeCustomers = rs.getInt("active_customers");
                if (month >= 1 && month <= 12) {
                    monthlyActivity.put(monthNames[month - 1], activeCustomers);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyActivity;
    }
    
    public java.util.Map<String, Integer> getCustomerActivityByWeek() {
        java.util.Map<String, Integer> weeklyActivity = new java.util.LinkedHashMap<>();
        String sql = "SELECT " +
                    "DATE(transaction_time) as activity_date, " +
                    "COUNT(DISTINCT customer_id) as active_customers " +
                    "FROM bills " +
                    "WHERE transaction_time >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY) " +
                    "GROUP BY DATE(transaction_time) " +
                    "ORDER BY DATE(transaction_time)";
        
        // Initialize last 7 days with 0
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = currentDate.minusDays(i);
            String dayLabel = dayNames[date.getDayOfWeek().getValue() - 1];
            weeklyActivity.put(dayLabel, 0);
        }
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                java.sql.Date activityDate = rs.getDate("activity_date");
                int activeCustomers = rs.getInt("active_customers");
                
                if (activityDate != null) {
                    java.time.LocalDate localDate = activityDate.toLocalDate();
                    String dayLabel = dayNames[localDate.getDayOfWeek().getValue() - 1];
                    weeklyActivity.put(dayLabel, activeCustomers);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weeklyActivity;
    }
    
    public int getActiveCustomersCount() {
        String sql = "SELECT COUNT(DISTINCT customer_id) FROM bills " +
                    "WHERE MONTH(transaction_time) = MONTH(CURRENT_DATE) " +
                    "AND YEAR(transaction_time) = YEAR(CURRENT_DATE)";
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int getNewCustomersThisMonth() {
        // Count customers who made their first purchase this month
        String sql = "SELECT COUNT(*) FROM ( " +
                    "SELECT customer_id, MIN(transaction_time) as first_purchase " +
                    "FROM bills " +
                    "GROUP BY customer_id " +
                    "HAVING MONTH(first_purchase) = MONTH(CURRENT_DATE) " +
                    "AND YEAR(first_purchase) = YEAR(CURRENT_DATE) " +
                    ") as new_customers";
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // Pet tracking methods
    public int getSoldPetsCount() {
        String sql = "SELECT COUNT(*) FROM bill_items WHERE item_type = 'PET'";
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public java.util.Map<String, Integer> getSoldPetsByType() {
        java.util.Map<String, Integer> soldByType = new java.util.HashMap<>();
        String sql = "SELECT " +
                    "CASE " +
                    "  WHEN p.type = 'DOG' THEN 'Dogs' " +
                    "  WHEN p.type = 'CAT' THEN 'Cats' " +
                    "  ELSE 'Others' " +
                    "END as pet_type, " +
                    "COUNT(*) as sold_count " +
                    "FROM bill_items bi " +
                    "JOIN pets p ON bi.pet_id = p.id " +
                    "WHERE bi.item_type = 'PET' " +
                    "GROUP BY p.type";
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String petType = rs.getString("pet_type");
                int count = rs.getInt("sold_count");
                soldByType.put(petType, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldByType;
    }
    
    // Monthly sales tracking methods
    public java.util.Map<String, BigDecimal> getMonthlySales() {
        java.util.Map<String, BigDecimal> monthlySales = new java.util.LinkedHashMap<>();
        String sql = "SELECT " +
                    "MONTH(transaction_time) as month, " +
                    "SUM(total_amount) as monthly_revenue " +
                    "FROM bills " +
                    "WHERE YEAR(transaction_time) = YEAR(CURRENT_DATE) " +
                    "GROUP BY MONTH(transaction_time) " +
                    "ORDER BY MONTH(transaction_time)";
        
        // Initialize all months with 0
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : monthNames) {
            monthlySales.put(month, BigDecimal.ZERO);
        }
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int month = rs.getInt("month");
                BigDecimal revenue = rs.getBigDecimal("monthly_revenue");
                if (month >= 1 && month <= 12 && revenue != null) {
                    monthlySales.put(monthNames[month - 1], revenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlySales;
    }
    
    public String getBestMonth() {
        String sql = "SELECT " +
                    "MONTH(transaction_time) as month, " +
                    "SUM(total_amount) as monthly_revenue " +
                    "FROM bills " +
                    "WHERE YEAR(transaction_time) = YEAR(CURRENT_DATE) " +
                    "GROUP BY MONTH(transaction_time) " +
                    "ORDER BY monthly_revenue DESC " +
                    "LIMIT 1";
        
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int month = rs.getInt("month");
                BigDecimal revenue = rs.getBigDecimal("monthly_revenue");
                if (month >= 1 && month <= 12 && revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0) {
                    return monthNames[month - 1] + " ($" + revenue + ")";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No data";
    }
    
    public java.util.Map<String, BigDecimal> getWeeklyRevenue() {
        java.util.Map<String, BigDecimal> weeklyRevenue = new java.util.LinkedHashMap<>();
        String sql = "SELECT " +
                    "WEEK(transaction_time, 1) as week_number, " +
                    "SUM(total_amount) as weekly_revenue " +
                    "FROM bills " +
                    "WHERE MONTH(transaction_time) = MONTH(CURRENT_DATE) " +
                    "AND YEAR(transaction_time) = YEAR(CURRENT_DATE) " +
                    "GROUP BY WEEK(transaction_time, 1) " +
                    "ORDER BY WEEK(transaction_time, 1)";
        
        // Initialize weeks
        for (int i = 1; i <= 4; i++) {
            weeklyRevenue.put("Week " + i, BigDecimal.ZERO);
        }
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            // Get current month's first week number for calculation
            java.time.LocalDate firstDayOfMonth = java.time.LocalDate.now().withDayOfMonth(1);
            java.sql.Date firstDay = java.sql.Date.valueOf(firstDayOfMonth);
            
            int weekCounter = 1;
            while (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal("weekly_revenue");
                if (revenue != null && weekCounter <= 4) {
                    weeklyRevenue.put("Week " + weekCounter, revenue);
                    weekCounter++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weeklyRevenue;
    }

}
