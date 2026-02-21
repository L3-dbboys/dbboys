package com.dbboys.service;

import com.dbboys.impl.MetaObjectImpl;
import com.dbboys.impl.MetaObjectImpl.DdlFetcher;
import com.dbboys.db.MetadataRepository;
import com.dbboys.db.DDLRepository;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;
import com.dbboys.vo.ObjectList;
import com.dbboys.vo.Sequence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SequenceService implements MetaObjectImpl {
    private final MetadataRepository metadataRepository = new MetadataRepository();

    @Override
    public ObjectList loadObjects(Connection conn, String databaseName) throws SQLException {
        ObjectList objectList = new ObjectList();
        List<Sequence> result = new ArrayList<>();
        objectList.setItems(result);
        int count = metadataRepository.getSequenceCount(conn);
        objectList.setInfo(count + "个");
        result.addAll(metadataRepository.getSequences(conn, databaseName));
        return objectList;
    }
    @Override
    public DdlFetcher ddlFetcher() {
        return DDLRepository::printSequence;
    }
}



