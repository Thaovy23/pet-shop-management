
package service.human;

import model.user.Staff;
import util.hash.BCrypt;

import java.math.BigDecimal;

public class StaffService {
    public Staff createStaff(String name, String email, String phone,
                             String username, String rawPassword, BigDecimal salary) {
        if (username.isBlank() || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        return new Staff(name, email, phone, username, hashed, salary);
    }

    public Staff updateStaff(Staff staff, String name, String email, String phone,
                              String username, String rawPassword, BigDecimal salary) {
        staff.setName(name);
        staff.setEmail(email);
        staff.setPhone(phone);
        staff.setUsername(username);
        staff.setSalary(salary);

        if (!rawPassword.equals("********")) {
            String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            staff.setPasswordHash(hashed);
        }

        return staff;
    }
}
