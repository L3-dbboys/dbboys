package com.dbboys.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Trigger extends TreeData{
    private StringProperty database=new SimpleStringProperty();
    private StringProperty tableName=new SimpleStringProperty();
    private StringProperty triggerType=new SimpleStringProperty();
    private BooleanProperty isdisabled=new SimpleBooleanProperty();
    public Trigger(){};
    public Trigger(String name) {
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

    public String getTableName() {
        return tableName.get();
    }

    public StringProperty tableNameProperty() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName.set(tableName);
    }


    public String getTriggerType() {
        return triggerType.get();
    }

    public StringProperty triggerTypeProperty() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType.set(triggerType);
    }

    public boolean isIsdisabled() {
        return isdisabled.get();
    }

    public BooleanProperty isdisabledProperty() {
        return isdisabled;
    }

    public void setIsdisabled(boolean isdisabled) {
        this.isdisabled.set(isdisabled);
    }
}
