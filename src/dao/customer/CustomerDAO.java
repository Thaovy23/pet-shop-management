package dao.customer;

import model.user.Customer;

import database.connection_provider;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class CustomerDAO {
    private static final String TABLE = "customers";

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getInt("loyalty_points")
        );
    }

    public Customer saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (name, email, phone, loyalty_points) VALUES (?, ?, ?, ?)";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setInt(4, customer.getLoyaltyPoints());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) customer.setId(rs.getInt(1));
                else throw new SQLException("Creating customer failed, no ID obtained.");
            }

            return customer;
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE;

        try (Connection conn = connection_provider.getCon();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }

        return customers;
    }

    public Optional<Customer> getCustomerById(int id) throws SQLException {
        if (id <= 0) return Optional.empty();

        String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";
    
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Customer> getByLoyaltyPoints(String order) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        
        String sql = "SELECT * FROM " + TABLE + " ORDER BY loyalty_points " +
            ("DESC".equalsIgnoreCase(order) ? "DESC" : "ASC");


        try (Connection conn = connection_provider.getCon();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        if (customer.getId() <= 0) return false;

        String sql = "UPDATE " + TABLE + " SET name = ?, email = ?, phone = ?, loyalty_points = ? WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setInt(4, customer.getLoyaltyPoints());
            ps.setInt(5, customer.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        if (id <= 0) return false;

        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
