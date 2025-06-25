package model.pet;

import java.math.BigDecimal;

import model.billing.Sellable;

public abstract class Pet implements Sellable 
{
    private int id; // Set after DB insert
    private String name;
    private String breed;
    private int age;
    private BigDecimal price;

    public Pet(String name, String breed, int age, BigDecimal price)
    {
        setName(name);
        setBreed(breed);
        setAge(age);
        setPrice(price);
    }

    // ID setter with control (called only by DAO)
    public final void setId(int id) 
    {
        if (this.id != 0) throw new IllegalStateException("ID already set");
        
        if (id <= 0) throw new IllegalArgumentException("Invalid ID");
        
        this.id = id;
    }

    // Getters
    public int getId() { return id; }

    public String getName() { return name; }

    public String getBreed() { return breed; }

    public int getAge() { return age; }

    @Override
    public BigDecimal getPrice() { return price; }

    
    // Setters with validation
    public void setName(String name) 
    {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Pet name is required");
        this.name = name.trim();
    }

    public void setBreed(String breed) 
    {
        if (breed == null || breed.trim().isEmpty())
            throw new IllegalArgumentException("Breed is required");
        this.breed = breed.trim();
    }

    public void setAge(int age) 
    {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        this.age = age;
    }

    public void setPrice(BigDecimal price)
    {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
    }

    // Default displayName (can be overridden)
    @Override
    public String getDisplayName() 
    {
        return getClass().getSimpleName() + " - " + name + " (" + breed + ")";
    }
}
