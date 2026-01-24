package com.dbboys.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//巡检文件夹
public class PackageProcedure extends TreeData{

    public PackageProcedure() {}
    public PackageProcedure(String name) {
        super(name);
    }
    private StringProperty DDL = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();

    public String getDDL() {
        return DDL.get();
    }

    public StringProperty DDLProperty() {
        return DDL;
    }

    public void setDDL(String DDL) {
        this.DDL.set(DDL);
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
