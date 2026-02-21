package com.dbboys.service;

import com.dbboys.impl.MetaObjectImpl;
import com.dbboys.impl.MetaObjectImpl.DdlFetcher;
import com.dbboys.db.MetadataRepository;
import com.dbboys.db.DDLRepository;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;
import com.dbboys.vo.ObjectList;
import com.dbboys.vo.Procedure;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcedureService implements MetaObjectImpl {
    private final MetadataRepository metadataRepository = new MetadataRepository();

    public ObjectList loadObjects(Connection conn, String databaseName) throws SQLException {
        ObjectList objectList = new ObjectList();
        List<Procedure> result = new ArrayList<>();
        objectList.setItems(result);
        boolean filterType = metadataRepository.hasSysProcTypeColumn(conn);
        int count = metadataRepository.getProcedureCount(conn, filterType);
        objectList.setInfo(count + "个");
        result.addAll(metadataRepository.getProcedures(conn, databaseName, filterType));
        return objectList;
    }
    @Override
    public DdlFetcher ddlFetcher() {
        return DDLRepository::printProcedure;
    }
    public void updateStatistics(Connect connect, String sql, Runnable onSucceededUi) {
        executeObjectSql(connect, sql, onSucceededUi);
    }

}



