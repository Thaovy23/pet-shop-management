package model.user;

public class Customer extends Human {
    private int loyaltyPoints;

    public Customer(String name, String email, String phone) {
        super(name, email, phone);
        this.loyaltyPoints = 0;
    }

    public Customer(int id, String name, String email, String phone, int loyaltyPoints) {
        super(name, email, phone);
        setId(id);
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addLoyaltyPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Invalid points");
        loyaltyPoints += points;
    }

    public void setLoyaltyPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Points cannot be negative");
        loyaltyPoints = points;
    }

    @Override
    public String toString() {
        return String.format("ID: %d - %s (%s) - Loyalty: %d pts", 
            getId(), getName(), getPhone(), loyaltyPoints);
    }
}
