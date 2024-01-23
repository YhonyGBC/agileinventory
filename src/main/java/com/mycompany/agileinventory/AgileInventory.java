package com.mycompany.agileinventory;

public class AgileInventory {

    public static void main(String[] args) {
        DBConnection conn = DBConnection.getInstance();
        IProduct p1 = ProductFactory.factory("product1", 3, 200000);

        conn.insertProduct(p1);
    }
}
