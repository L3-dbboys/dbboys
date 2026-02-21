package com.dbboys.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PrimaryKeyInfo {
    private StringProperty constrName = new SimpleStringProperty();
    private StringProperty constrType = new SimpleStringProperty();
    private StringProperty idxCols = new SimpleStringProperty();

    public String getConstrName() {
        return constrName.get();
    }

    public StringProperty constrNameProperty() {
        return constrName;
    }

    public void setConstrName(String constrName) {
        this.constrName.set(constrName);
    }

    public String getConstrType() {
        return constrType.get();
    }

    public StringProperty constrTypeProperty() {
        return constrType;
    }

    public void setConstrType(String constrType) {
        this.constrType.set(constrType);
    }

    public String getIdxCols() {
        return idxCols.get();
    }

    public StringProperty idxColsProperty() {
        return idxCols;
    }

    public void setIdxCols(String idxCols) {
        this.idxCols.set(idxCols);
    }
}

