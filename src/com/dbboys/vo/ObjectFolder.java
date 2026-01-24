package com.dbboys.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObjectFolder extends TreeData{

    private StringProperty description=new SimpleStringProperty();
    public ObjectFolder() {}
    public ObjectFolder(String name) {
        super(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
