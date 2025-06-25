package model.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bill {
    private int id;
    private int customerId;
    private int staffId;
    private String paymentMethod;
    private LocalDateTime transactionTime;
    private final List<BillItem> items = new ArrayList<>();

    public Bill(int customerId, int staffId, String paymentMethod) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.paymentMethod = paymentMethod;
        this.transactionTime = LocalDateTime.now();
    }

    public void addItem(BillItem item) {
        if (item == null) {
            throw new IllegalArgumentException("BillItem không được null");
        }
        items.add(item);
    }

    public boolean removeItem(BillItem item) {
        return items.remove(item);
    }

    public List<BillItem> getItems() {
        // Trả về bản sao để tránh người ngoài sửa trực tiếp
        return Collections.unmodifiableList(items);
    }

    /**
     * Tính tổng tiền động, tránh lỗi cộng dồn không chính xác
     */
    public BigDecimal getTotalAmount() {
        return items.stream()
            .map(BillItem::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters và setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("ID đã được thiết lập");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getStaffId() {
        return staffId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        // This is for loading from database only
        // In normal operations, total is calculated from items
    }

    @Override
    public String toString() {
        return "Bill{id=" + id + ", customerId=" + customerId +
               ", staffId=" + staffId + ", totalAmount=" + getTotalAmount() +
               ", paymentMethod='" + paymentMethod + '\'' +
               ", transactionTime=" + transactionTime +
               ", items=" + items.size() + "}";
    }
}
