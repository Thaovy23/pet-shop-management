package controller.product;

import dao.product.ProductDAO;
import model.product.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductController {
    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());
    private final ProductDAO productDao;

    public ProductController() {
        this.productDao = new ProductDAO();
    }

    public boolean addProduct(Product product) {
        try {
            return productDao.saveProduct(product) != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product", e);
            return false;
        }
    }
    
    public boolean updateStock(int productId, int quantityChange) {
    try {
        return productDao.updateStock(productId, quantityChange);
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error updating stock", e);
        return false;
    }
}

    
    public List<Product> getAllProducts() {
        try {
            return ProductDAO.getAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all products", e);
            return List.of();
        }
    }

    public List<Product> getProductsByFilter(String category, String priceOrder) {
        try {
            return productDao.getByCondition(category, priceOrder);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error filtering products", e);
            return List.of();
        }
    }

    public List<Product> getAvailableProducts(String type) {
        try {
            return ProductDAO.getAll().stream()
                    .filter(p -> p.getClass().getSimpleName().equalsIgnoreCase(type))
                    .filter(p -> p.getStockQuantity() > 0)
                    .toList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting available products", e);
            return List.of();
        }
    }

    public boolean updateProduct(Product product) {
        try {
            return productDao.updateProduct(product);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product", e);
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        try {
            return productDao.deleteProduct(productId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product", e);
            return false;
        }
    }
}
