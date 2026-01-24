package com.dbboys.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Synonym extends TreeData{
    private StringProperty database=new SimpleStringProperty();
    private StringProperty synonymType = new SimpleStringProperty();
    private StringProperty created = new SimpleStringProperty();
    public Synonym(String name) {
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


    public String getSynonymType() {
        return synonymType.get();
    }

    public StringProperty synonymTypeProperty() {
        return synonymType;
    }

    public void setSynonymType(String synonymType) {
        this.synonymType.set(synonymType);
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
