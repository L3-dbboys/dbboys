package com.dbboys.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ISqlexeRepository {

    void setDatabase(Connection conn, String databaseName) throws SQLException;

    List<String> getSqlMode(Connection conn) throws SQLException;
}
