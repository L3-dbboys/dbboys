package com.dbboys.vo;

public class Sql {
    private String sql_type="";
    private String sql_str="";
    private String sql_remainder="";
    private Boolean sql_end=true;
    public String getSqlstr() {
        return sql_str;
    }

    public void setSqlStr(String sql_str) {
        this.sql_str = sql_str;
    }

    public String getSqlType() {
        return sql_type;
    }

    public void setSqlType(String sql_type) {
        this.sql_type = sql_type;
    }

    public Boolean getSqlEnd() {
        return sql_end;
    }

    public void setSqlEnd(Boolean sql_end) {
        this.sql_end = sql_end;
    }

    public String getSqlRemainder() {
        return sql_remainder;
    }

    public void setSqlRemainder(String sql_remainder) {
        this.sql_remainder = sql_remainder;
    }
}
