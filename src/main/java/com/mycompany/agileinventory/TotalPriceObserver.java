package com.mycompany.agileinventory;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JComponent;

public class TotalPriceObserver extends CustomObserver {

    public TotalPriceObserver(JComponent component) {
        super(component);
    }

    public void update(Object property) {
        float total = 0;
        for (IProduct product : (ArrayList<IProduct>) property) {
            total += product.getPricePerUnit() * product.getQuantity();
            System.out.println(total);
        }
        String totalText = new DecimalFormat().format(total);

        super.update(totalText);
    }
}
