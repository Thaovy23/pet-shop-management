package dao.product;

import model.product.Toy;
import model.product.Medicine;
import model.product.Product;
import model.product.Food;
import database.connection_provider;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.Date;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final String TABLE = "products";

    private static Product createProductFromResultSet(ResultSet rs) throws SQLException
    {
        String type = rs.getString("type");
        Product product;
        
        switch (type.toUpperCase()) 
        {
            case "TOY":
                product = new Toy
                (
                    rs.getString("name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("material")
                );
                break;
                
            case "FOOD":
                product = new Food
                (
                    rs.getString("name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),
                    rs.getDate("expiration_date").toLocalDate(),
                    rs.getString("nutritional_info")
                );
                break;
                
            case "MEDICINE":
                product = new Medicine 
                (
                    rs.getString("name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("dosage"),
                    rs.getDate("manufacture_date").toLocalDate(),
                    rs.getDate("expiration_date").toLocalDate()
                );
                
                break;
                
            default:
                
                throw new SQLException("Unknown product type: " + type);
        }
        product.setId(rs.getInt("id")); // Set ID after object created
        
        return product;
    }
    
    // CRUD operations
    //CREATE
     public Product saveProduct(Product product) throws SQLException
    {
        String sql = "INSERT INTO " + TABLE + " (name, price, stock_quantity, type, material, expiration_date, nutritional_info, manufacture_date, dosage) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connection_provider.getCon();
                
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getStockQuantity());
            ps.setString(4, product.getClass().getSimpleName().toUpperCase());
            
            // Set type-specific parameters
            if (product instanceof Toy toy)
            {
                ps.setString(5, toy.getMaterial());
                ps.setNull(6, Types.DATE);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.DATE);
                ps.setNull(9, Types.VARCHAR);
            } 
            else if (product instanceof Food food) 
            {
                ps.setNull(5, Types.VARCHAR);
                ps.setDate(6, Date.valueOf(food.getExpirationDate()));
                ps.setString(7, food.getNutritionalInfo());
                ps.setNull(8, Types.DATE);
                ps.setNull(9, Types.VARCHAR);
            } 
            else if (product instanceof Medicine med) 
            {
                ps.setNull(5, Types.VARCHAR);
                ps.setDate(6, Date.valueOf(med.getExpirationDate()));
                ps.setNull(7, Types.VARCHAR);
                ps.setDate(8, Date.valueOf(med.getManufactureDate()));
                ps.setString(9, med.getDosage());
            } 
            else 
            {
                throw new SQLException("Unknown product type");
            }
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) 
            {
                if (rs.next()) 
                {
                    product.setId(rs.getInt(1));
                }
            }
            return product;
        }
    }

    //SELECT ALL
    public static List<Product> getAll() throws SQLException 
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products WHERE status = 1"; // Thêm điều kiện lọc status

        try (Connection conn = connection_provider.getCon();

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next()) 
            {
                products.add(createProductFromResultSet(rs));
            }
        }
        return products;
    }


    //SELECT BY ID
    public Product getById(int id) throws SQLException
    { 
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ? AND status = 1";
        
        try(Connection conn = connection_provider.getCon();
                
            PreparedStatement ps = conn.prepareStatement(sql))
        {            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) 
                {
                    return createProductFromResultSet(rs);                    
                }
            }
        }
        return null;
    }
    
    //SELECT BY CONDITION
    public List<Product> getByCondition(String category, String priceOrder) throws SQLException 
    {
        List<Product> products = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLE + " WHERE 1=1 AND status = 1");
        
        if (category != null)
        {
            sql.append(" AND type = ?");
        }
        if (priceOrder != null) 
        {
            sql.append(" ORDER BY price ").append(priceOrder.equals("DESC") ? "DESC" : "ASC");
        }

        try (Connection conn = connection_provider.getCon();
                
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (category != null) 
            {
                ps.setString(paramIndex++, category.toUpperCase());
            }
            
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next())
                {
                    products.add(createProductFromResultSet(rs));
                }
            }
        }
        return products;
    }
    
    //UPDATE
    public boolean updateProduct(Product product) throws SQLException 
    {
        String sql = "UPDATE products SET name = ?, price = ?, stock_quantity = ?, status = ?, type = ?, " +
                    "material = ?, expiration_date = ?, nutritional_info = ?, " +
                    "manufacture_date = ?, dosage = ? WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
            PreparedStatement ps = conn.prepareStatement(sql))
        {

            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getStockQuantity());

            // Automatically set status = 1 if stock > 0, else keep it 0
            int newStatus = product.getStockQuantity() > 0 ? 1 : 0;
            
            ps.setInt(4, newStatus);
            ps.setString(5, product.getClass().getSimpleName().toUpperCase());

            if (product instanceof Toy toy) 
            {
                ps.setString(6, toy.getMaterial());
                ps.setNull(7, Types.DATE);
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.DATE);
                ps.setNull(10, Types.VARCHAR);
            } 
            else if (product instanceof Food food) 
            {
                ps.setNull(6, Types.VARCHAR);
                ps.setDate(7, Date.valueOf(food.getExpirationDate()));
                ps.setString(8, food.getNutritionalInfo());
                ps.setNull(9, Types.DATE);
                ps.setNull(10, Types.VARCHAR);
            } 
            else if (product instanceof Medicine med) 
            {
                ps.setNull(6, Types.VARCHAR);
                ps.setDate(7, Date.valueOf(med.getExpirationDate()));
                ps.setNull(8, Types.VARCHAR);
                ps.setDate(9, Date.valueOf(med.getManufactureDate()));
                ps.setString(10, med.getDosage());
            }
            ps.setInt(11, product.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    // UPDATE STOCK
public boolean updateStock(int productId, int quantityChange) throws SQLException {
    // Lấy stock hiện tại trước
    int currentStock = getCurrentStock(productId); // bạn cần viết method lấy stock hiện tại

    int newStock = currentStock + quantityChange;
    int newStatus = newStock > 0 ? 1 : 0;

    String updateSql = "UPDATE products SET stock_quantity = ?, status = ? WHERE id = ?";

    try (Connection conn = connection_provider.getCon();
         PreparedStatement ps = conn.prepareStatement(updateSql)) {
        ps.setInt(1, newStock);
        ps.setInt(2, newStatus);
        ps.setInt(3, productId);

        return ps.executeUpdate() > 0;
    }
}

private int getCurrentStock(int productId) throws SQLException {
    String sql = "SELECT stock_quantity FROM products WHERE id = ?";
    try (Connection conn = connection_provider.getCon();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, productId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("stock_quantity");
            }
            throw new SQLException("Product not found with id " + productId);
        }
    }
}
    
    //DELETE
    public boolean deleteProduct(int id) throws SQLException 
    {
        String sql = "UPDATE " + TABLE + " SET status = 0 WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    // GET LOW STOCK PRODUCTS
    public static List<Product> getLowStockProducts(int threshold) throws SQLException {
        List<Product> lowStockProducts = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE stock_quantity <= ? AND status = 1 ORDER BY stock_quantity ASC";
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lowStockProducts.add(createProductFromResultSet(rs));
                }
            }
        }
        return lowStockProducts;
    }
    
    // GET LOW STOCK COUNT
    public static int getLowStockCount(int threshold) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE + " WHERE stock_quantity <= ? AND status = 1";
        
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}