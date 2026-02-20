package com.dbboys.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class View extends TreeData{
    //created by liaosnet
    private StringProperty viewBoday = new SimpleStringProperty();
    /*
     * 表标识(smallint两字节)：
     * 1, 最高位开始第一位是1时（位与16384值为16384时），SQLMODE=Oracle，
     */
    private IntegerProperty flags = new SimpleIntegerProperty();

    /**
     * 返回视图的数据库模式
     * @return
     */
    public String getViewSqlMode(){
        if ((this.flags.get() & 16384) == 16384) {
            return "Oracle";
        }
        return "GBase";
    }

    @Override
    public String toString(){
        return "ViewName: " + this.getName() + "\n" +
                "ViewBody: " + this.viewBoday.get();
    }


    //created by L3

    private StringProperty dbname=new SimpleStringProperty();
    private StringProperty owner=new SimpleStringProperty();
    private StringProperty createTime=new SimpleStringProperty();
    public View() {}
    public View(String name) {
        super(name);
    }

    public String getViewBoday() {
        return viewBoday.get();
    }

    public StringProperty viewBodayProperty() {
        return viewBoday;
    }

    public void setViewBoday(String viewBoday) {
        this.viewBoday.set(viewBoday);
    }

    public int getFlags() {
        return flags.get();
    }

    public IntegerProperty flagsProperty() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags.set(flags);
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
