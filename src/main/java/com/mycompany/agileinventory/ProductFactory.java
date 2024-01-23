package com.mycompany.agileinventory;

public class ProductFactory {

    public static IProduct factory(String name, int quantity, float pricePerUnit) {
        if (pricePerUnit > 100000)
            return new MySQLProduct(0, name, quantity, pricePerUnit);

        return new PostgreSQLProduct(0, name, quantity, pricePerUnit);
    }
}
