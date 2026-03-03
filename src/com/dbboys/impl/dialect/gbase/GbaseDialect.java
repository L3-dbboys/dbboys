package com.dbboys.impl.dialect.gbase;

import com.dbboys.api.DatabaseDialect;
import com.dbboys.api.MetadataRepository;
import com.dbboys.api.SqlexeRepository;
import com.dbboys.vo.Connect;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * GBase 8S 方言：建连 URL/驱动、会话 sqlmode 初始化。
 */
public final class GbaseDialect implements DatabaseDialect {

    private static final String DB_TYPE = "GBASE 8S";
    private static final String DRIVER_CLASS = "com.gbasedbt.jdbc.Driver";

    private final MetadataRepository metadataRepository = new GbaseMetadataRepository();
    private final SqlexeRepository sqlexeRepository = new GbaseSqlexeRepository();

    @Override
    public String getDbType() {
        return DB_TYPE;
    }

    @Override
    public ConnectionParams getConnectionParams(Connect connect) throws Exception {
        String url;
        if (connect.getPropByName("GBASEDBTSERVER").isEmpty()) {
            url = "jdbc:gbasedbt-sqli://" + connect.getIp() + ":" + connect.getPort() + "/" + connect.getDatabase();
        } else {
            url = "jdbc:gbasedbt-sqli:/" + connect.getDatabase() + ":SQLH_TYPE=FILE;SQLH_FILE=extlib/GBASE 8S/sqlhosts;";
        }
        String jarFilePath = "file:extlib/" + connect.getDbtype() + "/" + connect.getDriver();
        return new com.dbboys.api.DatabaseDialect.ConnectionParams(url, DRIVER_CLASS, jarFilePath);
    }

    @Override
    public void sessionInit(Connection conn, Connect connect) throws Exception {
        try {
            conn.createStatement().execute("set environment sqlmode 'gbase'");
        } catch (SQLException e) {
            // ignore
        }
    }

    @Override
    public boolean supportsSessionInit() {
        return true;
    }

    @Override
    public String adjustProps(Connect connect, String dbLocale) {
        if (connect == null) {
            return null;
        }
        if (dbLocale == null || dbLocale.trim().isEmpty()) {
            return connect.getProps();
        }
        String normalized = dbLocale
                .replaceAll("(?i)" + "UTF8", "57372")
                .replaceAll("(?i)" + "GB18030-2000", "5488")
                .trim();
        JSONArray jsonArray = new JSONArray(connect.getProps());
        JSONArray jsonArrayNew = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!"DB_LOCALE".equals(jsonObject.getString("propName"))) {
                jsonArrayNew.put(jsonObject);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("propName", "DB_LOCALE");
        jsonObject.put("propValue", normalized);
        jsonArrayNew.put(jsonObject);
        return jsonArrayNew.toString();
    }

    @Override
    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    @Override
    public SqlexeRepository getSqlexeRepository() {
        return sqlexeRepository;
    }
}
