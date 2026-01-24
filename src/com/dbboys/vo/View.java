package com.dbboys.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class View extends TreeData{
    private StringProperty dbname=new SimpleStringProperty();
    private StringProperty owner=new SimpleStringProperty();
    private StringProperty createTime=new SimpleStringProperty();
    public View() {}
    public View(String name) {
        super(name);
    }
    public String getDbname() {
        return dbname.get();
    }

    public StringProperty dbnameProperty() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname.set(dbname);
    }

    public String getOwner() {
        return owner.get();
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public String getCreateTime() {
        return createTime.get();
    }

    public StringProperty createTimeProperty() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime.set(createTime);
    }

}
