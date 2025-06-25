package model.pet;

import java.math.BigDecimal;

public class Dog extends Pet 
{
    public Dog(String name, String breed, int age, BigDecimal price) 
    {
        super(name, breed, age, price);
    }
    
    @Override
    public String getDisplayName() 
    {
        return "Dog - " + getName() + " (" + getBreed() + ")";
    }
}
