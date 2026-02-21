package com.dbboys.service;

import com.dbboys.ctrl.SqlTabController;
import com.dbboys.db.SqlexeRepository;
import com.dbboys.util.AlterUtil;
import com.dbboys.util.GlobalErrorHandlerUtil;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;
import javafx.application.Platform;
import com.dbboys.util.MetadataTreeviewUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlexeService {
    private final ConnectionService connectionService = new ConnectionService();
    private final DatabaseService databaseService = new DatabaseService();
    private final SqlexeRepository sqlexeRepository = new SqlexeRepository();

    public List<String> getSqlMode(Connection conn) {
        try {
            return sqlexeRepository.getSqlMode(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String activeDatabase(Connect connect, Database database, SqlTabController sqlTabController) {
        String DBLocale = database.getDbLocale();
        String result = null;
        Connection connection = connect.getConn();
        Connection connection1 = null;
        try {
            sqlexeRepository.setDatabase(connection, database.getName());
            connect.setDatabase(database.getName());
            result = "success";
        } catch (SQLException e) {
            //e.printStackTrace();
            if (e.getErrorCode() == -23197 || e.getErrorCode() == -349) {
                
                Connect connect1=new Connect(connect);
                connect1.setProps(connectionService.modifyProps(connect1, DBLocale));
                connect1.setDatabase(database.getName());
                //connect.setProps(connectionService.modifyProps(connect, DBLocale));
                //connect.setDatabase(database.getName());
                try {
                    connection1 = connectionService.getConnection(connect1);
                    sqlTabController.closeResultSet();
                    connection.close();
                    connect.setConn(connection1);
                    connect.setDatabase(database.getName());
                    result = "success";
                }
                catch (Exception ex) {
                    if(connection1!=null)
                        try {
                            connection1.close();
                        } catch (SQLException e1) {
                        }
                
                    GlobalErrorHandlerUtil.handle(ex);
                    connect.setConn(connection);

                }
            } else if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                result = "disconnected";
                e.printStackTrace();
            } else {
                GlobalErrorHandlerUtil.handle(e);
            }
        }
        return result;
    }

    public List<Database> getDatabases(Connect connect) {
        Connection connection = connect.getConn();
        List<Database> catalogs = new ArrayList<>();
        try {
            catalogs = databaseService.getDatabases(connection, false);
        } catch (SQLException e) {
            if (e.getErrorCode() == -201) {
                try {
                    catalogs = databaseService.getDatabases(connection, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                MetadataTreeviewUtil.connectionDisconnected();
            } else {
                GlobalErrorHandlerUtil.handle(e);
            }
        }
        return catalogs;
    }
}




