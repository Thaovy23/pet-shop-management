package model.product;

import java.math.BigDecimal;

public class Toy extends Product {
    private String material;

    public Toy(String name, BigDecimal price, int stockQuantity, String material)
    {
        super(name, price, stockQuantity);
        setMaterial(material);
    }

    public String getMaterial() { return material; }

    public void setMaterial(String material)
    {
        if (material == null || material.trim().isEmpty())
            throw new IllegalArgumentException("Material is required");
        this.material = material.trim();
    }
}
