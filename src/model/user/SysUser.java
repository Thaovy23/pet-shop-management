package model.user;

import java.util.Objects;

public abstract class SysUser extends Human {
    
    private String username;
    private String passwordHash;

    public SysUser(String name, String email, String phone, String username, String passwordHash)
    {
        super(name, email, phone);
        setUsername(username);
        setPasswordHash(passwordHash);
    }

    public String getUsername() { return username; }

    public String getPasswordHash() { return passwordHash; }

    public void setUsername(String username) 
    {
        if (username == null || username.length() < 5) 
        {
            throw new IllegalArgumentException("Invalid username");
        }
        this.username = username;
    }

    public void setPasswordHash(String hash) 
    {
        if (hash != null && hash.length() != 60)
        { 
            throw new IllegalArgumentException("Invalid password hash");
        }
        this.passwordHash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SysUser)) return false;
        if (!super.equals(o)) return false;
        SysUser sysUser = (SysUser) o;
        return username.equals(sysUser.username) &&
               (passwordHash == null ? sysUser.passwordHash == null : passwordHash.equals(sysUser.passwordHash));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, passwordHash);
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
