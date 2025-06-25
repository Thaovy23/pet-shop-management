package model.billing;

import java.math.BigDecimal;

public class BillItem {
    public enum ItemType { PRODUCT, PET }

    private ItemType itemType;
    private Integer productId;
    private Integer petId;
    private String itemName;
    private BigDecimal unitPrice;
    private int quantity;

    private String productType; // FOOD, MEDICINE, TOY
    private String petType;     // DOG, CAT

    // Constructor cho sản phẩm
    public BillItem(int productId, String name, BigDecimal unitPrice, int quantity, String productType) {
        validateFields(name, unitPrice, productType, "sản phẩm");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
        }

        this.itemType = ItemType.PRODUCT;
        this.productId = productId;
        this.itemName = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.productType = productType;
    }

    // Constructor cho thú cưng
    public BillItem(int petId, String name, BigDecimal unitPrice, String petType) {
        validateFields(name, unitPrice, petType, "thú cưng");

        this.itemType = ItemType.PET;
        this.petId = petId;
        this.itemName = name;
        this.unitPrice = unitPrice;
        this.quantity = 1; // cố định
        this.petType = petType;
    }

    private void validateFields(String name, BigDecimal price, String type, String context)
    {
        if (name == null || name.trim().isEmpty()) 
            throw new IllegalArgumentException("Tên không được để trống cho " + context);
        
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Giá phải lớn hơn hoặc bằng 0 cho " + context);
        
        if (type == null || type.trim().isEmpty())
            throw new IllegalArgumentException("Loại không được để trống cho " + context);
    }


    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public ItemType getItemType() { return itemType; }
    public Integer getProductId() { return productId; }
    public Integer getPetId() { return petId; }
    public String getItemName() { return itemName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (itemType == ItemType.PET) {
            throw new UnsupportedOperationException("Số lượng thú cưng cố định là 1");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.quantity = quantity;
    }

    public String getItemTypeString() {
        return itemType.name();
    }

    public String getType() {
        return itemType == ItemType.PRODUCT ? productType : petType;
    }

    public int getItemId() {
        return itemType == ItemType.PRODUCT ? productId : petId;
    }
}
