package util.factory;

import java.math.BigDecimal;
import model.pet.Cat;
import model.pet.Dog;
import model.pet.Pet;

public class PetFactory {
    
public static Pet create(String type, String name, String breed, int age, BigDecimal price) {

    return switch (type.toUpperCase())
    {
        case "DOG" -> new Dog(name, breed, age, price);
        case "CAT" -> new Cat(name, breed, age, price);
        default -> throw new IllegalArgumentException("Invalid pet type: " + type);
    };
}

}

