package com.dbboys.vo;

import javafx.beans.property.*;

public class DBPackage extends TreeData{
    private StringProperty database=new SimpleStringProperty();
    private StringProperty owner=new SimpleStringProperty();
    private IntegerProperty rows=new SimpleIntegerProperty();
    private BooleanProperty isEmpty = new SimpleBooleanProperty();
    private StringProperty DDL=new SimpleStringProperty();
    private BooleanProperty showDDL = new SimpleBooleanProperty();

    public DBPackage(String name) {
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

    public String getOwner() {
        return owner.get();
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public int getRows() {
        return rows.get();
    }

    public IntegerProperty rowsProperty() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows.set(rows);
    }

    public boolean isIsEmpty() {
        return isEmpty.get();
    }

    public BooleanProperty isEmptyProperty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty.set(isEmpty);
    }

    public String getDDL() {
        return DDL.get();
    }

    public StringProperty DDLProperty() {
        return DDL;
    }

    public void setDDL(String ddl) {
        this.DDL.set(ddl);
    }

    public boolean getShowDDL() {
        return showDDL.get();
    }

    public BooleanProperty showDDLProperty() {
        return showDDL;
    }

    public void setShowDDL(boolean showDDL) {
        this.showDDL.set(showDDL);
    }
}
