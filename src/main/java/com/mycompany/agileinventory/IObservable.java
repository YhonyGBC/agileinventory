package com.mycompany.agileinventory;

public interface IObservable {

    public void notifyAllObservers();

    public void addObserver(IObserver observer);

    public void removeObserver(IObserver observer);

    public void removeAllObservers();
}
