package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection_provider 
{
    public static Connection getCon() throws SQLException{
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USER,
                DatabaseConfig.PASSWORD
            );
        } 
        catch (ClassNotFoundException | SQLException e)
        {
            System.err.println("DB Error: " + e.getMessage());        
            throw new SQLException("Database connection failed", e);
        }
    }
}