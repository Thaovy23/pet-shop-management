package model.pet;

import java.math.BigDecimal;

public class Cat extends Pet 
{
    public Cat(String name, String breed, int age, BigDecimal price)
    {
        super(name, breed, age, price);
    }

    @Override
    public String getDisplayName()
    {
        return "Cat - " + getName() + " (" + getBreed() + ")";
    }
}
