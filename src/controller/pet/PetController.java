package controller.pet;

import dao.pet.PetDAO;
import model.pet.Pet;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PetController {
    private static final Logger LOGGER = Logger.getLogger(PetController.class.getName());
    private final PetDAO petDao;

    public PetController() {
        this.petDao = new PetDAO();
    }

    public boolean addPet(Pet pet, String type) {
        try {
            return petDao.savePet(pet, type) != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding pet", e);
            return false;
        }
    }

    public List<Pet> getAllPets() {
        try {
            return petDao.getAllPets();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all pets", e);
            return List.of();
        }
    }

    public List<Pet> getPetsByFilter(String type, String priceOrder) {
        try {
            return petDao.getByCondition(type, priceOrder);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtering pets", e);
            return List.of();
        }
    }

    public boolean updatePet(Pet pet) {
        try {
            return petDao.updatePet(pet);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating pet", e);
            return false;
        }
    }

    public boolean deletePet(int petId) {
        try {
            return petDao.deletePet(petId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting pet", e);
            return false;
        }
    }
}
