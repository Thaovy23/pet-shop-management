package model.billing;

import model.product.Product;
import model.pet.Pet;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShoppingCart {
    private final Map<Integer, BillItem> productItems = new HashMap<>();
    private final Map<Integer, BillItem> petItems = new HashMap<>();

    public void addProduct(Product product, int quantity) 
    {
        productItems.compute(product.getId(), (k, v) -> {
            if (v == null) 
            {
                return new BillItem(product.getId(), product.getName(), product.getPrice(), quantity,
                                    product.getClass().getSimpleName().toUpperCase());
            }
            v.setQuantity(v.getQuantity() + quantity);
            return v;
        });
    }

    public void addPet(Pet pet) {
        if (!petItems.containsKey(pet.getId())) {
            petItems.put(pet.getId(), new BillItem(pet.getId(), pet.getName(), pet.getPrice(),
                                                   pet.getClass().getSimpleName().toUpperCase()));
        }
    }

    public void updateProductQuantity(int productId, int newQuantity)
    {
        BillItem item = productItems.get(productId);
        
        if (item != null)
        {
            item.setQuantity(newQuantity);
        }
    }

    public void removeProduct(int productId)
    {
        productItems.remove(productId);
    }

    public void removePet(int petId) 
    {
        petItems.remove(petId);
    }

    public BigDecimal getTotal() 
    {
        return Stream.concat(productItems.values().stream(), petItems.values().stream())
                     .map(BillItem::getTotal)
                     .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<BillItem> getAllItems() 
    {
        return Stream.concat(productItems.values().stream(), petItems.values().stream())
                     .collect(Collectors.toList());
    }

    public void clear() 
    {
        productItems.clear();
        petItems.clear();
    }
}
