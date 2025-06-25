package model.user;

import java.math.BigDecimal;
import java.util.Objects;

public class Staff extends SysUser {

    private BigDecimal salary;

    public Staff(String name, String email, String phone, String username, String passwordHash, BigDecimal salary) {
        super(name, email, phone, username, passwordHash);
        setSalary(salary);
    }

    public Staff(String name, String email, String phone, String username, BigDecimal salary) {
        super(name, email, phone, username, null);
        setSalary(salary);
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Invalid salary");
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        if (!super.equals(o)) return false;
        Staff staff = (Staff) o;
        return Objects.equals(salary, staff.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), salary);
    }

    @Override
    public String toString() {
        return "Staff{" +
            "id=" + getId() +
            ", name='" + getName() + '\'' +
            ", email='" + getEmail() + '\'' +
            ", phone='" + getPhone() + '\'' +
            ", username='" + getUsername() + '\'' +
            ", salary=" + salary +
            '}';
    }
}
