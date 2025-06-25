package service.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import model.product.Product;
import util.factory.ProductFactory;

public class ProductService {

    public Product createToy(String name, BigDecimal price, int quantity, String material) {
        validateCommonFields(name, price, quantity);
        if (material == null || material.isEmpty()) {
            throw new IllegalArgumentException("Material is required for Toy.");
        }
        return ProductFactory.createToy(name, price, quantity, material);
    }

    public Product createFood(String name, BigDecimal price, int quantity, String expiration, String nutritional) {
        validateCommonFields(name, price, quantity);
        LocalDate expirationDate = parseDate(expiration, "Expiration Date");
        if (nutritional == null || nutritional.isEmpty()) {
            throw new IllegalArgumentException("Nutritional info is required for Food.");
        }
        return ProductFactory.createFood(name, price, quantity, expirationDate, nutritional);
    }

    public Product createMedicine(String name, BigDecimal price, int quantity, String dosage, String manufacture, String expiration) {
        validateCommonFields(name, price, quantity);
        if (dosage == null || dosage.isEmpty()) {
            throw new IllegalArgumentException("Dosage is required for Medicine.");
        }
        LocalDate manufactureDate = parseDate(manufacture, "Manufacture Date");
        LocalDate expirationDate = parseDate(expiration, "Expiration Date");
        return ProductFactory.createMedicine(name, price, quantity, dosage, manufactureDate, expirationDate);
    }

    private void validateCommonFields(String name, BigDecimal price, int quantity) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " format is invalid. Use YYYY-MM-DD.");
        }
    }
}

