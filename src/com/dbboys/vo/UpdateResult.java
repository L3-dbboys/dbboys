package com.dbboys.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UpdateResult {
    private IntegerProperty connectId=new SimpleIntegerProperty();
    private StringProperty result=new SimpleStringProperty();
    private StringProperty startTime=new SimpleStringProperty();
    private StringProperty endTime=new SimpleStringProperty();
    private StringProperty elapsedTime=new SimpleStringProperty();
    private IntegerProperty affectedRows=new SimpleIntegerProperty();
    private StringProperty updateSql=new SimpleStringProperty();
    private StringProperty database=new SimpleStringProperty();
    private StringProperty mark=new SimpleStringProperty();

    public int getConnectId() {
        return connectId.get();
    }

    public IntegerProperty connectIdProperty() {
        return connectId;
    }

    public void setConnectId(int connectId) {
        this.connectId.set(connectId);
    }

    public String getResult() {
        return result.get();
    }

    public StringProperty resultProperty() {
        return result;
    }

    public void setResult(String result) {
        this.result.set(result);
    }

    public String getStartTime() {
        return startTime.get();
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    public String getEndTime() {
        return endTime.get();
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }

    public String getElapsedTime() {
        return elapsedTime.get();
    }

    public StringProperty elapsedTimeProperty() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime.set(elapsedTime);
    }

    public int getAffectedRows() {
        return affectedRows.get();
    }

    public IntegerProperty affectedRowsProperty() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows.set(affectedRows);
    }

    public String getUpdateSql() {
        return updateSql.get();
    }

    public StringProperty updateSqlProperty() {
        return updateSql;
    }

    public void setUpdateSql(String updateSql) {
        this.updateSql.set(updateSql);
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

    public String getMark() {
        return mark.get();
    }

    public StringProperty markProperty() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark.set(mark);
    }
}
