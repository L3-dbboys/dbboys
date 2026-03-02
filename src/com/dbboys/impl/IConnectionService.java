package com.dbboys.impl;

import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionService {

    Connection createConnection(Connect connect) throws Exception;

    Connection getConnection(Connect connect) throws Exception;

    void changeCommitMode(Connection conn, int commitChoiceBoxIndex) throws SQLException;

    void sessionChangeToGbaseMode(Connection conn);

    com.dbboys.service.ConnectionService.ChangeDefaultDatabaseResult changeDefaultDatabase(Connect connect, Database database);

    String modifyProps(Connect connect, String DBlocale);

    String setConnectInfo(Connect connect) throws Exception;

    Boolean testConn(Connect connect);

    @FunctionalInterface
    interface SqlWork<T> {
        T apply(Connection conn) throws Exception;
    }

    <T> T withMetaSession(Connect connect, Database database, com.dbboys.service.ConnectionService.SqlWork<T> action) throws Exception;
}
