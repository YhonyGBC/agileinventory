package com.mycompany.agileinventory;

public abstract class AbstractProduct implements IProduct {

    protected int id;
    protected String name;
    protected int quantity;
    protected float pricePerUnit;
    protected String DBMS;
    
    public AbstractProduct(int id, String name, int quantity, float pricePerUnit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.DBMS = "Unknown DBMS";
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public float getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public String getDBMS() {
        return this.DBMS;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void setPricePerUnit(float pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public String toString() {
        return "Product [ id: " + this.getId()
                + ", name: " + this.getName()
                + ", quantity: " + this.getQuantity()
                + ", pricePerUnit: " + this.getPricePerUnit()
                + ", DBMS: " + this.getDBMS()
                + " ]";
    }

    @Override
    public IProduct clone() throws CloneNotSupportedException {
        return (IProduct) super.clone();
    }
}
