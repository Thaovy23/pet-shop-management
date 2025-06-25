package util.factory;

import java.math.BigDecimal;
import java.time.LocalDate;

import model.product.Product;
import model.product.Food;
import model.product.Medicine;
import model.product.Toy;


public class ProductFactory {

    public static Product createToy(String name, BigDecimal price, int quantity, String material) {
        return new Toy(name, price, quantity, material);
    }

    public static Product createFood(String name, BigDecimal price, int quantity, LocalDate expirationDate, String nutritional) {
        return new Food(name, price, quantity, expirationDate, nutritional);
    }

    public static Product createMedicine(String name, BigDecimal price, int quantity, String dosage, LocalDate manufactureDate, LocalDate expirationDate) {
        return new Medicine(name, price, quantity, dosage, manufactureDate, expirationDate);
    }
}
