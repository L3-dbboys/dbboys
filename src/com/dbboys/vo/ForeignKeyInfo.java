package com.dbboys.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ForeignKeyInfo {
    private StringProperty fkName = new SimpleStringProperty();
    private StringProperty fkOwner = new SimpleStringProperty();
    private StringProperty fkTabname = new SimpleStringProperty();
    private StringProperty fkCols = new SimpleStringProperty();
    private StringProperty fkIdxName = new SimpleStringProperty();
    private StringProperty pkOwner = new SimpleStringProperty();
    private StringProperty pkTabname = new SimpleStringProperty();
    private StringProperty pkCols = new SimpleStringProperty();
    private StringProperty pkIdxName = new SimpleStringProperty();

    public String getFkName() {
        return fkName.get();
    }

    public StringProperty fkNameProperty() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName.set(fkName);
    }

    public String getFkOwner() {
        return fkOwner.get();
    }

    public StringProperty fkOwnerProperty() {
        return fkOwner;
    }

    public void setFkOwner(String fkOwner) {
        this.fkOwner.set(fkOwner);
    }

    public String getFkTabname() {
        return fkTabname.get();
    }

    public StringProperty fkTabnameProperty() {
        return fkTabname;
    }

    public void setFkTabname(String fkTabname) {
        this.fkTabname.set(fkTabname);
    }

    public String getFkCols() {
        return fkCols.get();
    }

    public StringProperty fkColsProperty() {
        return fkCols;
    }

    public void setFkCols(String fkCols) {
        this.fkCols.set(fkCols);
    }

    public String getFkIdxName() {
        return fkIdxName.get();
    }

    public StringProperty fkIdxNameProperty() {
        return fkIdxName;
    }

    public void setFkIdxName(String fkIdxName) {
        this.fkIdxName.set(fkIdxName);
    }

    public String getPkOwner() {
        return pkOwner.get();
    }

    public StringProperty pkOwnerProperty() {
        return pkOwner;
    }

    public void setPkOwner(String pkOwner) {
        this.pkOwner.set(pkOwner);
    }

    public String getPkTabname() {
        return pkTabname.get();
    }

    public StringProperty pkTabnameProperty() {
        return pkTabname;
    }

    public void setPkTabname(String pkTabname) {
        this.pkTabname.set(pkTabname);
    }

    public String getPkCols() {
        return pkCols.get();
    }

    public StringProperty pkColsProperty() {
        return pkCols;
    }

    public void setPkCols(String pkCols) {
        this.pkCols.set(pkCols);
    }

    public String getPkIdxName() {
        return pkIdxName.get();
    }

    public StringProperty pkIdxNameProperty() {
        return pkIdxName;
    }

    public void setPkIdxName(String pkIdxName) {
        this.pkIdxName.set(pkIdxName);
    }
}

