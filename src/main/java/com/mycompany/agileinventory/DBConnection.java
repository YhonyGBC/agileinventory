package com.mycompany.agileinventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBConnection {

    private static DBConnection connection;
    private String dbName = "agileinventorydb";

    private String MySQLURL = "jdbc:mysql://localhost:3306/" + this.dbName;
    private String PosgreSQLURL = "jdbc:postgresql://localhost:5432/" + this.dbName;
    private String username = "root";
    private String password = "";

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        if (connection == null)
            return new DBConnection();

        return connection;
    }

    private Connection getDriverManager(String DBMS) {
        Connection connection = null;

        String JDBC_URL = switch(DBMS) {
            case "MySQL" -> this.MySQLURL;
            case "PostgreSQL" -> this.PosgreSQLURL;
            default -> "Invalid DBMS";
        }; 

        try {
            connection = DriverManager.getConnection(JDBC_URL, this.username, this.password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public void insertProduct(IProduct product) {

        try (Connection connection = this.getDriverManager(product.getDBMS())) {
            String sql = "INSERT INTO products (name, quantity, price_per_unit, DBMS) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, product.getName());
                preparedStatement.setInt(2, product.getQuantity());
                preparedStatement.setFloat(3, product.getPricePerUnit());
                preparedStatement.setString(4, product.getDBMS());

                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<IProduct> selectProducts() {
        return null;
    }

    public IProduct selectProduct(int id) {
        return null;
    }

    public void updateProduct(int id) {
    }

    public void deleteProduct(int id) {
    }
}
