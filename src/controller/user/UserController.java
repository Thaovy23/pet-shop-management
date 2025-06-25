package controller.user;

import dao.user.UserDAO;
import model.user.Manager;
import model.user.Staff;
import java.util.List;
import java.sql.*;
import model.user.SysUser;

public class UserController {
    private final UserDAO userDao = new UserDAO();

    public boolean addStaff(Staff staff) {
        try {
            return userDao.saveStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Staff> getAllStaff() {
        try {
            return userDao.getAllStaff();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean updateStaff(Staff staff) {
        if (!(AuthController.currentUser instanceof Manager)) return false;

        try {
            return userDao.updateStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUser(SysUser user) {
        try {
            return userDao.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStaff(int id) {
        if (!(AuthController.currentUser instanceof Manager)) return false;

        try {
            return userDao.deleteStaff(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
