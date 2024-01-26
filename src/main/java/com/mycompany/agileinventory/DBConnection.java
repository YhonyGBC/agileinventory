package com.mycompany.agileinventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnection {

    private static DBConnection connection;

    private final String dbName;
    private final String MYSQL_JDBC_URL;
    private final String POSTGRESQL_JDBC_URL;
    private final String MYSQL_USERNAME;
    private final String MYSQL_PASSWORD;
    private final String POSTGRESQL_USERNAME;
    private final String POSTGRESQL_PASSWORD;

    private DBConnection() {
        this.dbName = "agileinventorydb";
        this.MYSQL_JDBC_URL = "jdbc:mysql://localhost:3306/" + this.dbName;
        this.POSTGRESQL_JDBC_URL = "jdbc:postgresql://localhost:5432/" + this.dbName;
        this.MYSQL_USERNAME = "root";
        this.MYSQL_PASSWORD = "";
        this.POSTGRESQL_USERNAME = "postgres";
        this.POSTGRESQL_PASSWORD = "123";
    }

    public static DBConnection getInstance() {
        if (connection == null)
            return new DBConnection();

        return connection;
    }

    private Connection getDriverManager(String DBMS) throws SQLException {
        String JDBC_URL = "", username = "", password = "";

        if (DBMS == "MySQL") {
            JDBC_URL = this.MYSQL_JDBC_URL;
            username = this.MYSQL_USERNAME;
            password = this.MYSQL_PASSWORD;
        } else if (DBMS == "PostgreSQL") {
            JDBC_URL = this.POSTGRESQL_JDBC_URL;
            username = this.POSTGRESQL_USERNAME;
            password = this.POSTGRESQL_PASSWORD;
        }

        return DriverManager.getConnection(JDBC_URL, username, password);
    }

    public void insertProduct(IProduct product) throws SQLException {
        Connection connection = this.getDriverManager(product.getDBMS());
        String sql = "INSERT INTO products (name, quantity, price_per_unit, DBMS) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, product.getName());
        preparedStatement.setInt(2, product.getQuantity());
        preparedStatement.setFloat(3, product.getPricePerUnit());
        preparedStatement.setString(4, product.getDBMS());
        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

        if (generatedKeys.next())
            product.setId(generatedKeys.getInt(1));
    }

    private ArrayList<IProduct> selectProductsFromDBMS(String DBMS) throws SQLException {
        ArrayList<IProduct> productList = new ArrayList<>();
        String sql = "SELECT * FROM products";

        Connection connection = getDriverManager(DBMS);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            if (DBMS == "MySQL") {
                productList.add(new MySQLProduct(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getFloat("price_per_unit")));

            } else {
                productList.add(new PostgreSQLProduct(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getFloat("price_per_unit")));
            }
        }

        return productList;
    }

    public ArrayList<IProduct> selectProducts() throws SQLException {
        ArrayList<IProduct> products = selectProductsFromDBMS("MySQL");
        products.addAll(selectProductsFromDBMS("PostgreSQL"));

        return products;
    }

    public void updateProduct(IProduct product) throws SQLException {
        Connection connection = getDriverManager(product.getDBMS());
        String sql = "UPDATE products SET name = ?, quantity = ?, price_per_unit = ?, DBMS = ? WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, product.getName());
        preparedStatement.setInt(2, product.getQuantity());
        preparedStatement.setDouble(3, product.getPricePerUnit());
        preparedStatement.setString(4, product.getDBMS());
        preparedStatement.setInt(5, product.getId());

        preparedStatement.executeUpdate();
    }

    public void deleteProduct(IProduct product) throws SQLException {
        Connection connection = getDriverManager(product.getDBMS());
        String sql = "DELETE FROM products WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, product.getId());
        preparedStatement.executeUpdate();
    }

    public void resetDatabases() throws SQLException {
        Connection connection;

        connection = getDriverManager("MySQL");
        connection.prepareStatement("DELETE FROM products").executeUpdate();
        connection.prepareStatement("ALTER TABLE products AUTO_INCREMENT=1").executeUpdate();

        connection = getDriverManager("PostgreSQL");
        connection.prepareStatement("DELETE FROM products").executeUpdate();
        connection.prepareStatement("ALTER SEQUENCE products_id_seq RESTART WITH 1").executeUpdate();
    }
}
