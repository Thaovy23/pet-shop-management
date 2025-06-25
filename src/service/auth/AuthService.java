package service.auth;

import dao.user.UserDAO;

import model.user.SysUser;

import model.user.Staff;

import util.hash.BCrypt;

import java.sql.SQLException;

public class AuthService {
    
    private final UserDAO userDao = new UserDAO();

    public SysUser authenticate(String email, String password) {
        try {
            SysUser user = userDao.getByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerStaff(Staff staff, String password) {
        try {
            if (userDao.emailExists(staff.getEmail())) return false; // Tránh trùng email
            staff.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
            return userDao.saveStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
