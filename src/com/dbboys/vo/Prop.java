package com.dbboys.vo;

public class Prop {
    private String propName;
    private String propValue;
    public Prop(String propName, String propValue) {
        this.propName = propName;
        this.propValue = propValue;
    }
    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }
}
