package com.mycompany.agileinventory;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CustomObserver implements IObserver {

    private JComponent component;

    public CustomObserver(JComponent component) {
        this.component = component;
    }

    public void update(Object property) {
        float total = 0;

        for (IProduct product : (ArrayList<IProduct>) property) {
            total += product.getPricePerUnit() * product.getQuantity();
            System.out.println(total);
        }

        String totalText = new DecimalFormat().format(total);

        if (component instanceof JLabel label) {
            label.setText(totalText);
        } else if (component instanceof JTextField textField) {
            textField.setText(totalText);
        }
    }
}
