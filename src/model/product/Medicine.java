package model.product;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Medicine extends Product 
{
    private String dosage;
    private LocalDate manufactureDate;
    private LocalDate expirationDate;

    public Medicine(String name, BigDecimal price, int stockQuantity,
                    String dosage, LocalDate manufactureDate, LocalDate expirationDate) 
    {
        super(name, price, stockQuantity);
        setDosage(dosage);
        setManufactureDate(manufactureDate);
        setExpirationDate(expirationDate);
    }

    public String getDosage() { return dosage; }
    public LocalDate getManufactureDate() { return manufactureDate; }
    public LocalDate getExpirationDate() { return expirationDate; }

    public void setDosage(String dosage) 
    {
        if (dosage == null || dosage.trim().isEmpty())
            throw new IllegalArgumentException("Dosage is required");
        this.dosage = dosage.trim();
    }

    public void setManufactureDate(LocalDate manufactureDate)
    {
        if (manufactureDate == null || manufactureDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Manufacture date must be in the past");
        this.manufactureDate = manufactureDate;
    }

    public void setExpirationDate(LocalDate expirationDate)
    {
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Expiration date must be in the future");
        this.expirationDate = expirationDate;
    }
}
