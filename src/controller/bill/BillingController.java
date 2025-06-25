package controller.bill;

import dao.bill.BillDAO;
import dao.customer.CustomerDAO;
import dao.pet.PetDAO;
import dao.product.ProductDAO;

import model.user.Customer;
import model.billing.ShoppingCart;
import model.product.Product;
import model.billing.Bill;
import model.billing.BillItem;
import model.pet.Pet;

import service.billing.PdfGenerator;
import service.billing.BillingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class BillingController {
    private final ShoppingCart cart = new ShoppingCart();
    private final BillingService billingService = new BillingService();

    private final CustomerDAO customerDao;
    private final BillDAO billDao;
    private final PetDAO petDao;
    private final ProductDAO productDao;

    public BillingController() {
        this.customerDao = new CustomerDAO();
        this.billDao = new BillDAO();
        this.petDao = new PetDAO();
        this.productDao = new ProductDAO();
    }

    // Constructor cho DI (test dễ hơn)
    public BillingController(CustomerDAO customerDao, BillDAO billDao, PetDAO petDao, ProductDAO productDao) {
        this.customerDao = customerDao;
        this.billDao = billDao;
        this.petDao = petDao;
        this.productDao = productDao;
    }

    public void addProductToCart(Product product, int quantity) {
        // Kiểm tra xem sản phẩm đã có trong cart chưa
        BillItem existingItem = cart.getAllItems().stream()
            .filter(i -> i.getItemType() == BillItem.ItemType.PRODUCT && i.getProductId() == product.getId())
            .findFirst()
            .orElse(null);

        int currentInCart = existingItem != null ? existingItem.getQuantity() : 0;
        int remainingStock = product.getStockQuantity() - currentInCart;

        if (quantity > remainingStock) {
            throw new IllegalArgumentException("Only " + remainingStock + " items available for " + product.getName());
        }

        cart.addProduct(product, quantity);
    }

    public void addPetToCart(Pet pet) {
        // Chỉ thêm thú cưng nếu chưa có trong cart
        boolean petInCart = cart.getAllItems().stream()
            .anyMatch(i -> i.getItemType() == BillItem.ItemType.PET && i.getPetId() == pet.getId());

        if (!petInCart) {
            cart.addPet(pet);
        }
    }

    public BigDecimal getCartTotal() {
        return cart.getTotal();
    }

    public List<BillItem> getCartItemsAsList() {
        return cart.getAllItems();
    }

    public BigDecimal getTotalRevenue() {
        return billDao.getTotalRevenue();
    }

    public int getTotalOrders() {
        return billDao.getTotalOrderCount();
    }

    public void updateCartItem(int productId, int newQuantity) {
        try {
            Product product = productDao.getById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found.");
            }
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Stock exceeded. Only " + product.getStockQuantity() + " available.");
            }
            cart.updateProductQuantity(productId, newQuantity);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching product data", e);
        }
    }

    public void removeCartItem(int itemId) {
        // Cần kiểm tra itemId thuộc loại nào: Product hay Pet
        BillItem item = cart.getAllItems().stream()
            .filter(i -> i.getItemId() == itemId)
            .findFirst()
            .orElse(null);

        if (item == null) return;

        if (item.getItemType() == BillItem.ItemType.PRODUCT) {
            cart.removeProduct(itemId);
        } else if (item.getItemType() == BillItem.ItemType.PET) {
            cart.removePet(itemId);
        }
    }

    public Bill finalizeBill(int customerId, int staffId, String paymentMethod) {
        Bill bill = new Bill(customerId, staffId, paymentMethod);
        getCartItemsAsList().forEach(bill::addItem);
        return bill;
    }

    public boolean processBill(Bill bill) throws SQLException {
        List<BillItem> items = bill.getItems();

        for (BillItem item : items) {
            if (item.getItemType() == BillItem.ItemType.PRODUCT) {
                Product product = productDao.getById(item.getProductId());
                if (product == null || product.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Not enough stock for " + (product != null ? product.getName() : "Unknown product") +
                            ". Required: " + item.getQuantity() + ", Available: " + (product != null ? product.getStockQuantity() : 0));
                }
            }
        }

        boolean success = billingService.processPayment(bill, items);
        if (!success) return false;

        for (BillItem item : items) {
            if (item.getItemType() == BillItem.ItemType.PET) {
                petDao.deletePet(item.getPetId());
            }
        }
        return true;
    }

    public void applyLoyaltyPoints(Customer customer, BigDecimal total) throws SQLException {
        int points = total.divide(BigDecimal.TEN, RoundingMode.DOWN).intValue();
        if (points > 0) {
            customer.addLoyaltyPoints(points);
            customerDao.updateCustomer(customer);
        }
    }

    public void exportBillAsPdf(Bill bill) {
        PdfGenerator.generateBillPdf(bill);
        cart.clear();
    }

    public void clearCart() {
        cart.clear();
    }
    
    // Staff performance methods
    public int getOrdersByStaff(int staffId) {
        return billDao.getOrdersByStaff(staffId);
    }
    
    public BigDecimal getRevenueByStaff(int staffId) {
        return billDao.getRevenueByStaff(staffId);
    }
    
    public BigDecimal getTotalStaffRevenue() {
        return billDao.getTotalStaffRevenue();
    }
    
    // Customer activity methods
    public java.util.Map<String, Integer> getCustomerActivityByMonth() {
        return billDao.getCustomerActivityByMonth();
    }
    
    public int getActiveCustomersCount() {
        return billDao.getActiveCustomersCount();
    }
    
    public int getNewCustomersThisMonth() {
        return billDao.getNewCustomersThisMonth();
    }
    
    // Pet tracking methods
    public int getSoldPetsCount() {
        return billDao.getSoldPetsCount();
    }
    
    public java.util.Map<String, Integer> getSoldPetsByType() {
        return billDao.getSoldPetsByType();
    }
    
    // Sales tracking methods
    public java.util.Map<String, BigDecimal> getMonthlySales() {
        return billDao.getMonthlySales();
    }
    
    public String getBestMonth() {
        return billDao.getBestMonth();
    }
    
    public java.util.Map<String, BigDecimal> getWeeklyRevenue() {
        return billDao.getWeeklyRevenue();
    }
}
