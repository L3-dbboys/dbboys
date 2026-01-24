package com.dbboys.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HealthCheck {
    private StringProperty entry=new SimpleStringProperty();
    private StringProperty cmd=new SimpleStringProperty();
    private StringProperty healthValue=new SimpleStringProperty();
    private StringProperty currentValue=new SimpleStringProperty();
    private StringProperty status=new SimpleStringProperty();
    private String cmdOutput;
    public HealthCheck() {}
    public HealthCheck(String entry,String cmd,String healthValue,String currentValue,String status,String cmdOutput) {
        this.entry.set(entry);
        this.cmd.set(cmd);
        this.healthValue.set(healthValue);
        this.currentValue.set(currentValue);
        this.status.set(status);
        this.cmdOutput = cmdOutput;

    }

    public String getCmdOutput() {
        return cmdOutput;
    }

    public void setCmdOutput(String cmdOutput) {
        this.cmdOutput = cmdOutput;
    }

    public String getEntry() {
        return entry.get();
    }

    public StringProperty entryProperty() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry.set(entry);
    }

    public String getCmd() {
        return cmd.get();
    }

    public StringProperty cmdProperty() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd.set(cmd);
    }

    public String getHealthValue() {
        return healthValue.get();
    }

    public StringProperty healthValueProperty() {
        return healthValue;
    }

    public void setHealthValue(String healthValue) {
        this.healthValue.set(healthValue);
    }

    public String getCurrentValue() {
        return currentValue.get();
    }

    public StringProperty currentValueProperty() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue.set(currentValue);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
