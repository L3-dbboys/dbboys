package com.dbboys.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigInteger;

public class Sequence extends TreeData{
    private StringProperty database = new SimpleStringProperty();
    private BigIntegerProperty minValue= new BigIntegerProperty();
    private BigIntegerProperty maxValue= new BigIntegerProperty();
    private BigIntegerProperty incValue= new BigIntegerProperty();
    private BigIntegerProperty nextval= new BigIntegerProperty();
    private IntegerProperty cache = new SimpleIntegerProperty();
    private StringProperty created = new SimpleStringProperty();
    public Sequence(String name) {
        super(name);
    }

    public String getDatabase() {
        return database.get();
    }

    public StringProperty databaseProperty() {
        return database;
    }

    public void setDatabase(String database) {
        this.database.set(database);
    }

    public BigInteger getMinValue() {
        return minValue.get();
    }

    public BigIntegerProperty minValueProperty() {
        return minValue;
    }

    public void setMinValue(BigInteger minValue) {
        this.minValue.set(minValue);
    }

    public BigInteger getMaxValue() {
        return maxValue.get();
    }

    public BigIntegerProperty maxValueProperty() {
        return maxValue;
    }

    public void setMaxValue(BigInteger maxValue) {
        this.maxValue.set(maxValue);
    }

    public BigInteger getIncValue() {
        return incValue.get();
    }

    public BigIntegerProperty incValueProperty() {
        return incValue;
    }

    public void setIncValue(BigInteger incValue) {
        this.incValue.set(incValue);
    }

    public BigInteger getNextval() {
        return nextval.get();
    }

    public BigIntegerProperty nextvalProperty() {
        return nextval;
    }

    public void setNextval(BigInteger nextval) {
        this.nextval.set(nextval);
    }

    public int getCache() {
        return cache.get();
    }

    public IntegerProperty cacheProperty() {
        return cache;
    }

    public void setCache(int cache) {
        this.cache.set(cache);
    }

    public String getCreated() {
        return created.get();
    }

    public StringProperty createdProperty() {
        return created;
    }

    public void setCreated(String created) {
        this.created.set(created);
    }
}
