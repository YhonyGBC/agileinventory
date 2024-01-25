package com.mycompany.agileinventory;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class AgileInventory {

    public static void testDBConnection() {
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

    public static void testObserverPattern() {
        ArrayList<IProduct> products = new ArrayList<IProduct>(Arrays.asList(
                ProductFactory.factory("product1", 1, 200000),
                ProductFactory.factory("product2", 2, 20000),
                ProductFactory.factory("product3", 3, 50000),
                ProductFactory.factory("product4", 4, 5000000),
                ProductFactory.factory("product5", 5, 100000)));

        JLabel label = new JLabel("null");
        JTextField field = new JTextField("");
        JButton button = new JButton("change");
        JFrame frame = new JFrame();

        CustomObservable observable = new CustomObservable(products);
        QuantityObserver quantityObserver = new QuantityObserver(label);
        TotalPriceObserver totalPriceObserver = new TotalPriceObserver(field);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(label);
        frame.getContentPane().add(field);
        frame.getContentPane().add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                products.get(1).setQuantity(6);
                products.get(3).setPricePerUnit(10000);
                observable.notifyAllObservers();
            }
        });

        observable.addObserver(quantityObserver);
        observable.addObserver(totalPriceObserver);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        testObserverPattern();
    }
}
