package service.billing;

import dao.bill.BillDAO;
import dao.product.ProductDAO;

import model.billing.Bill;
import model.billing.BillItem;

import java.math.BigDecimal;

import java.sql.SQLException;

import java.util.List;

public class BillingService {
    
    private final ProductDAO productDao = new ProductDAO();

    public BigDecimal calculateTotal(List<BillItem> items)
    {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean processPayment(Bill bill, List<BillItem> items) throws SQLException 
    {
        boolean saved = BillDAO.createBill(bill, items);
        
        if (!saved) return false;

        // Reduce product stock
        for (BillItem item : items) 
        {
            if (item.getItemType() == BillItem.ItemType.PRODUCT) 
            {
                productDao.updateStock(item.getProductId(), -item.getQuantity());
            }
        }
        return true;
    }
}
