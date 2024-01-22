package com.mycompany.agileinventory;

public class MySQLProduct implements IProduct {

    private int id;
    private String name;
    private int count;
    private float unitValue;
    private final String databaseName = "MySQL";

    public MySQLProduct(int id, String name, int count, float unitValue) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.unitValue = unitValue;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public float getUnitValue() {
        return this.unitValue;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setUnitValue(float unitValue) {
        this.unitValue = unitValue;
    }
}
