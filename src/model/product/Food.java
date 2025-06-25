package model.product;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Food extends Product 
{
    private LocalDate expirationDate;
    private String nutritionalInfo;

    public Food(String name, BigDecimal price, int stockQuantity,
                LocalDate expirationDate, String nutritionalInfo) 
    {
        super(name, price, stockQuantity);
        setExpirationDate(expirationDate);
        setNutritionalInfo(nutritionalInfo);
    }

    public LocalDate getExpirationDate() { return expirationDate; }

    public String getNutritionalInfo() { return nutritionalInfo; }

    public void setExpirationDate(LocalDate expirationDate) 
    {
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Expiration date must be in the future");
        this.expirationDate = expirationDate;
    }

    public void setNutritionalInfo(String nutritionalInfo)
    {
        if (nutritionalInfo == null || nutritionalInfo.trim().isEmpty())
            throw new IllegalArgumentException("Nutritional info required");
        this.nutritionalInfo = nutritionalInfo.trim();
    }
}
