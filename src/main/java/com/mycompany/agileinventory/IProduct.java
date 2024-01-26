package com.mycompany.agileinventory;

public interface IProduct extends Cloneable {
    public int getId();

    public String getName();

    public int getQuantity();

    public float getPricePerUnit();

    public String getDBMS();

    public void setId(int id);

    public void setName(String name);

    public void setQuantity(int quantity);

    public void setPricePerUnit(float pricePerUnit);

    public void setDBMS(String DBMS);

    public IProduct clone() throws CloneNotSupportedException;
}
