package model.user;

import java.math.BigDecimal;
import java.util.Objects;

public class Manager extends SysUser {

    public Manager(String name, String email, String phone, String username, String passwordHash) {
        super(name, email, phone, username, passwordHash);
    }

    public void adjustStaffSalary(Staff staff, BigDecimal newSalary) {
        if (newSalary == null || newSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary must be non-negative");
        }
        staff.setSalary(newSalary);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        
        if (!(o instanceof Manager)) return false;
        
        return super.equals(o);
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", username='" + getUsername() + '\'' +
                '}';
    }
}
