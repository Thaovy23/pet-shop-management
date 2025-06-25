package controller.user;

import dao.user.UserDAO;

import model.user.SysUser;
import model.user.Staff;
import model.user.Manager;

import util.hash.BCrypt;

import java.sql.SQLException;

public class AuthController {
    private final UserDAO userDao = new UserDAO();
    public static SysUser currentUser;

    public boolean login(String email, String password) {
        if (email == null || email.isBlank()) {
            System.err.println("Login failed: Email không được để trống.");
            return false;
        }
        if (password == null || password.isBlank()) {
            System.err.println("Login failed: Mật khẩu không được để trống.");
            return false;
        }

        try {
            SysUser user = userDao.getByEmail(email);
            if (user == null) {
                System.err.println("Login failed: Email chưa đăng ký.");
                return false;
            }
            if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                System.err.println("Login failed: Mật khẩu không đúng.");
                return false;
            }
            currentUser = user;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signup(Staff staff, String rawPassword) {
        if (staff == null) {
            System.err.println("Signup failed: Thông tin nhân viên không hợp lệ.");
            return false;
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            System.err.println("Signup failed: Mật khẩu không được để trống.");
            return false;
        }
        try {
            if (userDao.emailExists(staff.getEmail())) {
                System.err.println("Signup failed: Email đã được đăng ký.");
                return false;
            }
            staff.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            return userDao.saveStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isManager() {
        return currentUser instanceof Manager;
    }
}

