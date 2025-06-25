
package service.pet;

import java.math.BigDecimal;
import model.pet.Pet;
import util.factory.PetFactory;

public class PetService {
    public Pet createPet(String type, String name, String breed, int age, BigDecimal price) {
        if (type == null || type.isEmpty()) throw new IllegalArgumentException("Pet type is required");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");
        if (breed == null || breed.isEmpty()) throw new IllegalArgumentException("Breed is required");
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price must be positive");

        return PetFactory.create(type, name, breed, age, price);
    }
}

