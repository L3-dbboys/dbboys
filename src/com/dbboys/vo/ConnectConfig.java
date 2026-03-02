package com.dbboys.vo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Pure configuration data for a database connection.
 * No runtime resources (Connection, ExecutorService) — those live in Connect/ActiveSession.
 */
public class ConnectConfig implements Cloneable {
    private int id;
    private String name;
    private int parentId;
    private String dbtype;
    private String ip;
    private String port;
    private String database;
    private String username;
    private String password;
    private String driver;
    private String props;
    private String info;
    private String drivermd5;
    private String dbversion;
    private boolean readonly;

    public ConnectConfig() {}

    public ConnectConfig(String name) {
        this.name = name;
    }

    public ConnectConfig(ConnectConfig other) {
        this.id = other.id;
        this.name = other.name;
        this.parentId = other.parentId;
        this.dbtype = other.dbtype;
        this.ip = other.ip;
        this.port = other.port;
        this.database = other.database;
        this.username = other.username;
        this.password = other.password;
        this.driver = other.driver;
        this.props = other.props;
        this.info = other.info;
        this.drivermd5 = other.drivermd5;
        this.dbversion = other.dbversion;
        this.readonly = other.readonly;
    }

    public static ConnectConfig fromConnect(Connect connect) {
        ConnectConfig cfg = new ConnectConfig();
        cfg.id = connect.getId();
        cfg.name = connect.getName();
        cfg.parentId = connect.getParentId();
        cfg.dbtype = connect.getDbtype();
        cfg.ip = connect.getIp();
        cfg.port = connect.getPort();
        cfg.database = connect.getDatabase();
        cfg.username = connect.getUsername();
        cfg.password = connect.getPassword();
        cfg.driver = connect.getDriver();
        cfg.props = connect.getProps();
        cfg.info = connect.getInfo();
        cfg.drivermd5 = connect.getDrivermd5();
        cfg.dbversion = connect.getDbversion();
        cfg.readonly = connect.getReadonly() != null && connect.getReadonly();
        return cfg;
    }

    public void applyTo(Connect connect) {
        connect.setId(id);
        connect.setName(name);
        connect.setParentId(parentId);
        connect.setDbtype(dbtype);
        connect.setIp(ip);
        connect.setPort(port);
        connect.setDatabase(database);
        connect.setUsername(username);
        connect.setPassword(password);
        connect.setDriver(driver);
        connect.setProps(props);
        connect.setInfo(info);
        connect.setDrivermd5(drivermd5);
        connect.setDbversion(dbversion);
        connect.setReadonly(readonly);
    }

    public void setPropByName(String propName, String propValue) {
        if (props == null) return;
        JSONArray jsonArray = new JSONArray(props);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("propName").equals(propName)) {
                jsonObject.put("propValue", propValue);
                props = jsonArray.toString();
                return;
            }
        }
    }

    public String getPropByName(String propName) {
        if (props == null) return "";
        JSONArray jsonArray = new JSONArray(props);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("propName").equals(propName)) {
                return jsonObject.getString("propValue");
            }
        }
        return "";
    }

    @Override
    public ConnectConfig clone() {
        return new ConnectConfig(this);
    }

    @Override
    public String toString() {
        return name;
    }

    // --- Getters/Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }

    public String getDbtype() { return dbtype; }
    public void setDbtype(String dbtype) { this.dbtype = dbtype; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDriver() { return driver; }
    public void setDriver(String driver) { this.driver = driver; }

    public String getProps() { return props; }
    public void setProps(String props) { this.props = props; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public String getDrivermd5() { return drivermd5; }
    public void setDrivermd5(String drivermd5) { this.drivermd5 = drivermd5; }

    public String getDbversion() { return dbversion; }
    public void setDbversion(String dbversion) { this.dbversion = dbversion; }

    public boolean isReadonly() { return readonly; }
    public void setReadonly(boolean readonly) { this.readonly = readonly; }
}
