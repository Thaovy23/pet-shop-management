package dao.user;

import model.user.Manager;
import model.user.Staff;
import model.user.SysUser;
import database.connection_provider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Tên bảng và các cột dùng trong DB
    private static final String TABLE = "staff";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE = "phone";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD_HASH = "password_hash";
    private static final String COL_ROLE = "role";
    private static final String COL_SALARY = "salary";

    private static final String ROLE_STAFF = "STAFF";
    private static final String ROLE_MANAGER = "MANAGER";

    // Save staff user
    public boolean saveStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (" +
                COL_NAME + ", " +
                COL_EMAIL + ", " +
                COL_PHONE + ", " +
                COL_USERNAME + ", " +
                COL_PASSWORD_HASH + ", " +
                COL_ROLE + ", " +
                COL_SALARY + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getUsername());
            ps.setString(5, staff.getPasswordHash());
            ps.setString(6, ROLE_STAFF);
            ps.setBigDecimal(7, staff.getSalary());

            int rows = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) staff.setId(rs.getInt(1));
            }
            return rows > 0;
        }
    }

    // Get staff by ID
    public Staff getStaffById(int id) throws SQLException {
        String sql = "SELECT " +
        COL_ID + ", " +
        COL_NAME + ", " +
        COL_EMAIL + ", " +
        COL_PHONE + ", " +
        COL_USERNAME + ", " +
        COL_PASSWORD_HASH + ", " +
        COL_SALARY +
        " FROM " + TABLE +
        " WHERE " + COL_ID + " = ? AND " + COL_ROLE + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, ROLE_STAFF);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff(
                            rs.getString(COL_NAME),
                            rs.getString(COL_EMAIL),
                            rs.getString(COL_PHONE),
                            rs.getString(COL_USERNAME),
                            rs.getString(COL_PASSWORD_HASH),
                            rs.getBigDecimal(COL_SALARY)
                    );
                    staff.setId(rs.getInt(COL_ID));
                    return staff;
                }
            }
        }
        return null;
    }

    // Get all staff
    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT " +
        COL_ID + ", " +
        COL_NAME + ", " +
        COL_EMAIL + ", " +
        COL_PHONE + ", " +
        COL_USERNAME + ", " +
        COL_PASSWORD_HASH + ", " +
        COL_SALARY +
        " FROM " + TABLE + " WHERE " + COL_ROLE + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ROLE_STAFF);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String passwordHash = rs.getString(COL_PASSWORD_HASH);
                    Staff staff;
                    if (passwordHash != null && passwordHash.length() == 60) {
                        staff = new Staff(
                                rs.getString(COL_NAME),
                                rs.getString(COL_EMAIL),
                                rs.getString(COL_PHONE),
                                rs.getString(COL_USERNAME),
                                passwordHash,
                                rs.getBigDecimal(COL_SALARY)
                        );
                    } else {
                        System.err.println("[WARN] Invalid or missing password hash for user: " + rs.getString(COL_USERNAME));
                        staff = new Staff(
                                rs.getString(COL_NAME),
                                rs.getString(COL_EMAIL),
                                rs.getString(COL_PHONE),
                                rs.getString(COL_USERNAME),
                                rs.getBigDecimal(COL_SALARY)
                        );
                    }
                    staff.setId(rs.getInt(COL_ID));
                    list.add(staff);
                }
            }
        }
        return list;
    }

    // Get user by email, return Manager or Staff depending on role
    public SysUser getByEmail(String email) throws SQLException {
        String sql = "SELECT " +
                COL_ID + ", " +
                COL_NAME + ", " +
                COL_EMAIL + ", " +
                COL_PHONE + ", " +
                COL_USERNAME + ", " +
                COL_PASSWORD_HASH + ", " +
                COL_ROLE + ", " +
                COL_SALARY +
                " FROM " + TABLE + " WHERE " + COL_EMAIL + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString(COL_ROLE);
                    if (ROLE_MANAGER.equalsIgnoreCase(role)) {
                        Manager manager = new Manager(
                                rs.getString(COL_NAME),
                                rs.getString(COL_EMAIL),
                                rs.getString(COL_PHONE),
                                rs.getString(COL_USERNAME),
                                rs.getString(COL_PASSWORD_HASH)
                        );
                        manager.setId(rs.getInt(COL_ID));
                        return manager;
                    } else {
                        Staff staff = new Staff(
                                rs.getString(COL_NAME),
                                rs.getString(COL_EMAIL),
                                rs.getString(COL_PHONE),
                                rs.getString(COL_USERNAME),
                                rs.getString(COL_PASSWORD_HASH),
                                rs.getBigDecimal(COL_SALARY)
                        );
                        staff.setId(rs.getInt(COL_ID));
                        return staff;
                    }
                }
            }
        }
        return null;
    }

    // Check if email exists
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE + " WHERE " + COL_EMAIL + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Update staff details
    public boolean updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET " +
                COL_NAME + " = ?, " +
                COL_EMAIL + " = ?, " +
                COL_PHONE + " = ?, " +
                COL_USERNAME + " = ?, " +
                COL_PASSWORD_HASH + " = ?, " +
                COL_SALARY + " = ? " +
                "WHERE " + COL_ID + " = ? AND " + COL_ROLE + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getUsername());
            ps.setString(5, staff.getPasswordHash());
            ps.setBigDecimal(6, staff.getSalary());
            ps.setInt(7, staff.getId());
            ps.setString(8, ROLE_STAFF);

            return ps.executeUpdate() > 0;
        }
    }

    // Update any SysUser (Manager or Staff)
    public boolean updateUser(SysUser user) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET " +
                COL_NAME + " = ?, " +
                COL_EMAIL + " = ?, " +
                COL_PHONE + " = ?, " +
                COL_USERNAME + " = ?, " +
                COL_PASSWORD_HASH + " = ? " +
                "WHERE " + COL_ID + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPasswordHash());
            ps.setInt(6, user.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete staff by id (hard delete since no status column)
    public boolean deleteStaff(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE " + COL_ID + " = ? AND " + COL_ROLE + " = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, ROLE_STAFF);

            return ps.executeUpdate() > 0;
        }
    }

}
