package com.dbboys.service;

import com.dbboys.impl.MetaObjectImpl;
import com.dbboys.impl.MetaObjectImpl.DdlFetcher;
import com.dbboys.db.MetadataRepository;
import com.dbboys.db.DDLRepository;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;
import com.dbboys.vo.ObjectList;
import com.dbboys.vo.Synonym;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SynonymService implements MetaObjectImpl {
    private final MetadataRepository metadataRepository = new MetadataRepository();

    public ObjectList loadObjects(Connection conn, String databaseName) throws SQLException {
        ObjectList objectList = new ObjectList();
        List<Synonym> result = new ArrayList<>();
        objectList.setItems(result);
        int count = metadataRepository.getSynonymCount(conn);
        objectList.setInfo(count + "个");
        result.addAll(metadataRepository.getSynonyms(conn, databaseName));
        return objectList;
    }
    @Override
    public DdlFetcher ddlFetcher() {
        return DDLRepository::printSynonym;
    }
}



