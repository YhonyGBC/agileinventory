package com.mycompany.agileinventory;

import java.util.ArrayList;

import javax.swing.JComponent;

public class QuantityObserver extends CustomObserver {

    public QuantityObserver(JComponent component) {
        super(component);
    }

    public void update(Object property) {
        int count = ((ArrayList<IProduct>) property).size();
        super.update(count);
    }
}
