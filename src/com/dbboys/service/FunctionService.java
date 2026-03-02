package com.dbboys.service;

import com.dbboys.impl.IMetaObjectService;
import com.dbboys.impl.IMetaObjectService.DdlFetcher;
import com.dbboys.db.MetadataRepository;
import com.dbboys.db.DDLRepository;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;
import com.dbboys.vo.Function;
import com.dbboys.vo.ObjectList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FunctionService implements IMetaObjectService {
    private final MetadataRepository metadataRepository;

    public FunctionService() {
        this(new MetadataRepository());
    }

    public FunctionService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public ObjectList loadObjects(Connection conn, String databaseName) throws SQLException {
        ObjectList objectList = new ObjectList();
        List<Function> result = new ArrayList<>();
        objectList.setItems(result);
        boolean filterType = metadataRepository.hasSysProcTypeColumn(conn);
        int count = metadataRepository.getFunctionCount(conn, filterType);
        objectList.setInfo(count + "个");
        result.addAll(metadataRepository.getFunctions(conn, databaseName, filterType));
        return objectList;
    }
    @Override
    public DdlFetcher ddlFetcher() {
        return DDLRepository::printProcedure;
    }
}



