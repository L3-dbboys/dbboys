package com.dbboys.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExtTableInfo {
    private StringProperty tableName = new SimpleStringProperty();
    private StringProperty formatType = new SimpleStringProperty();
    private StringProperty codeSet = new SimpleStringProperty();
    private StringProperty recordDelimiter = new SimpleStringProperty();
    private StringProperty fieldDelimiter = new SimpleStringProperty();
    private StringProperty dateFormat = new SimpleStringProperty();
    private StringProperty moneyFormat = new SimpleStringProperty();
    private IntegerProperty maxErrors = new SimpleIntegerProperty();
    private StringProperty rejectFile = new SimpleStringProperty();
    private IntegerProperty flags = new SimpleIntegerProperty();
    private IntegerProperty numDfiles = new SimpleIntegerProperty();

    public String getTableName() {
        return tableName.get();
    }

    public StringProperty tableNameProperty() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName.set(tableName);
    }

    public String getFormatType() {
        return formatType.get();
    }

    public StringProperty formatTypeProperty() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType.set(formatType);
    }

    public String getCodeSet() {
        return codeSet.get();
    }

    public StringProperty codeSetProperty() {
        return codeSet;
    }

    public void setCodeSet(String codeSet) {
        this.codeSet.set(codeSet);
    }

    public String getRecordDelimiter() {
        return recordDelimiter.get();
    }

    public StringProperty recordDelimiterProperty() {
        return recordDelimiter;
    }

    public void setRecordDelimiter(String recordDelimiter) {
        this.recordDelimiter.set(recordDelimiter);
    }

    public String getFieldDelimiter() {
        return fieldDelimiter.get();
    }

    public StringProperty fieldDelimiterProperty() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter.set(fieldDelimiter);
    }

    public String getDateFormat() {
        return dateFormat.get();
    }

    public StringProperty dateFormatProperty() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat.set(dateFormat);
    }

    public String getMoneyFormat() {
        return moneyFormat.get();
    }

    public StringProperty moneyFormatProperty() {
        return moneyFormat;
    }

    public void setMoneyFormat(String moneyFormat) {
        this.moneyFormat.set(moneyFormat);
    }

    public int getMaxErrors() {
        return maxErrors.get();
    }

    public IntegerProperty maxErrorsProperty() {
        return maxErrors;
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors.set(maxErrors);
    }

    public String getRejectFile() {
        return rejectFile.get();
    }

    public StringProperty rejectFileProperty() {
        return rejectFile;
    }

    public void setRejectFile(String rejectFile) {
        this.rejectFile.set(rejectFile);
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

    public int getNumDfiles() {
        return numDfiles.get();
    }

    public IntegerProperty numDfilesProperty() {
        return numDfiles;
    }

    public void setNumDfiles(int numDfiles) {
        this.numDfiles.set(numDfiles);
    }
}

