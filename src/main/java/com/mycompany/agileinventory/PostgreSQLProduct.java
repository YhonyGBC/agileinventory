package com.mycompany.agileinventory;

public class PostgreSQLProduct extends AbstractProduct {

    public PostgreSQLProduct(int id, String name, int quantity, float pricePerUnit) {
        super(id, name, quantity, pricePerUnit);
        this.DBMS = "PostgreSQL";
    }
}