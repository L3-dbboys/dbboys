package com.dbboys.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeData {
    public TreeData(){}
    public TreeData(String name){
        this.name.set(name);
    }
    private StringProperty name=new SimpleStringProperty();
    private BooleanProperty running=new SimpleBooleanProperty(false);
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
    public String toString(){
        return name.get();
    }

    public boolean isRunning() {
        return running.get();
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }
}
