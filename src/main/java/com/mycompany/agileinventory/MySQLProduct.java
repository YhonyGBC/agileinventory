package com.mycompany.agileinventory;

public class MySQLProduct implements IProduct {

    private int id;
    private String name;
    private int quantity;
    private float pricePerUnit;
    private final String DBMS;

    public MySQLProduct(int id, String name, int quantity, float pricePerUnit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.DBMS = "MySQL";
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public float getPricePerUnit() {
        return this.pricePerUnit;
    }

    public String getDBMS() {
        return this.DBMS;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricePerUnit(float pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
