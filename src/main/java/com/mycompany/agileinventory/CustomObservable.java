package com.mycompany.agileinventory;

import java.util.ArrayList;

public class CustomObservable implements IObservable {

    private Object observableProperty;
    private ArrayList<IObserver> observers;

    public CustomObservable(Object property) {
        this.observableProperty = property;
        this.observers = new ArrayList<>();
    }

    @Override
    public void notifyAllObservers() {
        for (IObserver observer : observers) {
            observer.update(this.observableProperty);
        }
    }

    @Override
    public void addObserver(IObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void removeAllObservers() {
        this.observers.clear();
    }
}
