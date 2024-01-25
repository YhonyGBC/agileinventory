package com.mycompany.agileinventory;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public abstract class CustomObserver implements IObserver {

    protected JComponent component;

    public CustomObserver(JComponent component) {
        this.component = component;
    }

    public void update(Object property) {
        if (component instanceof JLabel label) {
            label.setText("" + property);
        } else if (component instanceof JTextField textField) {
            textField.setText("" + property);
        }
    }
}
