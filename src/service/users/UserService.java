/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.users;

import model.user.Manager;
import model.user.Staff;
import model.user.SysUser;
import util.hash.BCrypt;

public class UserService {

    public SysUser updateProfile(SysUser currentUser, String name, String email, String phone, String username, String newPassword) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("Email is required");
        if (phone == null || phone.isEmpty()) throw new IllegalArgumentException("Phone is required");
        if (username == null || username.isEmpty()) throw new IllegalArgumentException("Username is required");

        SysUser updatedUser;

        if (currentUser instanceof Staff staff) {
            updatedUser = new Staff(name, email, phone, username, staff.getPasswordHash(), staff.getSalary());
        } else if (currentUser instanceof Manager manager) {
            updatedUser = new Manager(name, email, phone, username, manager.getPasswordHash());
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }

        updatedUser.setId(currentUser.getId());

        // Hash password if user entered new one
        if (newPassword != null && !newPassword.isBlank()) {
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            updatedUser.setPasswordHash(hashed);
        }

        return updatedUser; // Không gọi controller ở đây → clean hơn
    }
}
