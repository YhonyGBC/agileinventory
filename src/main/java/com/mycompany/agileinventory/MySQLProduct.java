package com.mycompany.agileinventory;

public class MySQLProduct extends AbstractProduct {

    public MySQLProduct(int id, String name, int quantity, float pricePerUnit) {
        super(id, name, quantity, pricePerUnit);
        this.DBMS = "MySQL";
    }
}
