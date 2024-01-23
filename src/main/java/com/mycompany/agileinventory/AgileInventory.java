package com.mycompany.agileinventory;

import java.util.ArrayList;
import java.util.Arrays;

public class AgileInventory {

    public static void main(String[] args) {
        try {
            // Test singleton dbconnection and products factory
            DBConnection conn = DBConnection.getInstance();
            conn.resetDatabases();

            ArrayList<IProduct> products = new ArrayList<IProduct>(Arrays.asList(
                    ProductFactory.factory("product1", 1, 200000),
                    ProductFactory.factory("product2", 2, 20000),
                    ProductFactory.factory("product3", 3, 50000),
                    ProductFactory.factory("product4", 4, 5000000),
                    ProductFactory.factory("product5", 5, 100000)));
            
            // Test prototype products
            IProduct p1 = ProductFactory.factory("cloneable-product", 1, 1000000);
            IProduct p2 = ((AbstractProduct) p1).clone();
            p2.setName("cloned-product");
            
            System.out.println("Cloneable 1: " + p1);
            System.out.println("Cloned 1: " + p2);

            for (IProduct product : products)
                conn.insertProduct(product);

            for (IProduct product : conn.selectProducts())
                System.out.println(product);
            
            IProduct up = products.get(0); 
            up.setPricePerUnit(400000);
            
            conn.updateProduct(up);
            conn.deleteProduct(products.get(1));

            System.out.println("After update 1 and remove 2");

            for (IProduct product : conn.selectProducts())
                System.out.println(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
