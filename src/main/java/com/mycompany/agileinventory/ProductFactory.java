package com.mycompany.agileinventory;

public class ProductFactory extends AbstractFactory {

    public IProduct factory(String name, int count, float unitValue) {
        if (unitValue > 100000)
            return createMySQLProduct(name, count, unitValue);

        return createPostgreSQLProduct(name, count, unitValue);
    }

    private MySQLProduct createMySQLProduct(String name, int count, float unitValue) {
        return null;
    }

    private PostgreSQLProduct createPostgreSQLProduct(String name, int count, float unitValue) {
        return null;
    }
}
