package com.dbboys.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Table extends TreeData{
    private StringProperty dbname=new SimpleStringProperty();
    private StringProperty owner=new SimpleStringProperty();
    private StringProperty createTime=new SimpleStringProperty();
    private StringProperty tableType=new SimpleStringProperty();
    private StringProperty lockMode=new SimpleStringProperty();
    private IntegerProperty isfragment=new SimpleIntegerProperty();
    private IntegerProperty extents=new SimpleIntegerProperty();
    private IntegerProperty nrows=new SimpleIntegerProperty();
    private IntegerProperty pagesize=new SimpleIntegerProperty();
    private IntegerProperty nptotal=new SimpleIntegerProperty();
    private StringProperty totalsize=new SimpleStringProperty();
    private IntegerProperty npdata=new SimpleIntegerProperty();
    private StringProperty usedsize=new SimpleStringProperty();
    public Table() {}
    public Table(String name) {
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

    public String getTableType() {
        return tableType.get();
    }

    public StringProperty tableTypeProperty() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType.set(tableType);
    }

    public String getLockMode() {
        return lockMode.get();
    }

    public StringProperty lockModeProperty() {
        return lockMode;
    }

    public void setLockMode(String lockMode) {
        this.lockMode.set(lockMode);
    }

    public int getIsfragment() {
        return isfragment.get();
    }

    public IntegerProperty isfragmentProperty() {
        return isfragment;
    }

    public void setIsfragment(int isfragment) {
        this.isfragment.set(isfragment);
    }

    public int getExtents() {
        return extents.get();
    }

    public IntegerProperty extentsProperty() {
        return extents;
    }

    public void setExtents(int extents) {
        this.extents.set(extents);
    }

    public int getNrows() {
        return nrows.get();
    }

    public IntegerProperty nrowsProperty() {
        return nrows;
    }

    public void setNrows(int nrows) {
        this.nrows.set(nrows);
    }

    public int getPagesize() {
        return pagesize.get();
    }

    public IntegerProperty pagesizeProperty() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize.set(pagesize);
    }

    public int getNptotal() {
        return nptotal.get();
    }

    public IntegerProperty nptotalProperty() {
        return nptotal;
    }

    public void setNptotal(int nptotal) {
        this.nptotal.set(nptotal);
    }

    public String getTotalsize() {
        return totalsize.get();
    }

    public StringProperty totalsizeProperty() {
        return totalsize;
    }

    public void setTotalsize(String totalsize) {
        this.totalsize.set(totalsize);
    }

    public int getNpdata() {
        return npdata.get();
    }

    public IntegerProperty npdataProperty() {
        return npdata;
    }

    public void setNpdata(int npdata) {
        this.npdata.set(npdata);
    }

    public String getUsedsize() {
        return usedsize.get();
    }

    public StringProperty usedsizeProperty() {
        return usedsize;
    }

    public void setUsedsize(String usedsize) {
        this.usedsize.set(usedsize);
    }
}
