package com.dbboys.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Index extends TreeData {
    private StringProperty tabname=new SimpleStringProperty();
    private StringProperty cols=new SimpleStringProperty();
    private StringProperty idxtype=new SimpleStringProperty();
    private StringProperty levels=new SimpleStringProperty();
    private StringProperty uniqvalues=new SimpleStringProperty();
    private StringProperty pagesize=new SimpleStringProperty();
    private StringProperty totalpages=new SimpleStringProperty();
    private StringProperty totalsize=new SimpleStringProperty();
    private StringProperty database=new SimpleStringProperty();
    private BooleanProperty isdisabled=new SimpleBooleanProperty();
    public Index() {}
    public Index(String name) {
        super(name);
    }
    public String getTabname() {
        return tabname.get();
    }

    public StringProperty tabnameProperty() {
        return tabname;
    }

    public void setTabname(String tabname) {
        this.tabname.set(tabname);
    }

    public String getCols() {
        return cols.get();
    }

    public StringProperty colsProperty() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols.set(cols);
    }

    public String getIdxtype() {
        return idxtype.get();
    }

    public StringProperty idxtypeProperty() {
        return idxtype;
    }

    public void setIdxtype(String idxtype) {
        this.idxtype.set(idxtype);
    }

    public String getLevels() {
        return levels.get();
    }

    public StringProperty levelsProperty() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels.set(levels);
    }

    public String getUniqvalues() {
        return uniqvalues.get();
    }

    public StringProperty uniqvaluesProperty() {
        return uniqvalues;
    }

    public void setUniqvalues(String uniqvalues) {
        this.uniqvalues.set(uniqvalues);
    }

    public String getPagesize() {
        return pagesize.get();
    }

    public StringProperty pagesizeProperty() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize.set(pagesize);
    }

    public String getTotalpages() {
        return totalpages.get();
    }

    public StringProperty totalpagesProperty() {
        return totalpages;
    }

    public void setTotalpages(String totalpages) {
        this.totalpages.set(totalpages);
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

    public String getDatabase() {
        return database.get();
    }

    public StringProperty databaseProperty() {
        return database;
    }

    public void setDatabase(String database) {
        this.database.set(database);
    }

    public boolean getIsdisabled() {
        return isdisabled.get();
    }

    public BooleanProperty isdisabledProperty() {
        return isdisabled;
    }

    public void setIsdisabled(boolean isdisabled) {
        this.isdisabled.set(isdisabled);
    }
}
