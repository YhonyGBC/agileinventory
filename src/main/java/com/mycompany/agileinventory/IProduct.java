package com.mycompany.agileinventory;

public interface IProduct {
    public int getId();
    public String getName();
    public int getCount();
    public float getUnitValue();
    public String getDatabaseName();

    public void setName(String name);
    public void setCount(int count);
    public void setUnitValue(float unitValue);
}
