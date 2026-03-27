package com.dbboys.service;

import com.dbboys.api.DdlRepositoryProvider;
import com.dbboys.api.MetaObjectService;
import com.dbboys.api.MetaObjectService.DdlFetcher;
import com.dbboys.api.MetadataRepositoryProvider;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Database;
import com.dbboys.vo.ObjectList;
import com.dbboys.vo.Synonym;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SynonymService implements MetaObjectService {
    private final MetadataRepositoryProvider metadataRepositoryProvider;
    private final DdlRepositoryProvider ddlRepositoryProvider;

    public SynonymService() {
        this(com.dbboys.app.AppContext.get(MetadataRepositoryProvider.class),
                com.dbboys.app.AppContext.get(DdlRepositoryProvider.class));
    }

    public SynonymService(MetadataRepositoryProvider metadataRepositoryProvider) {
        this(metadataRepositoryProvider, com.dbboys.app.AppContext.get(DdlRepositoryProvider.class));
    }

    public SynonymService(MetadataRepositoryProvider metadataRepositoryProvider, DdlRepositoryProvider ddlRepositoryProvider) {
        this.metadataRepositoryProvider = metadataRepositoryProvider;
        this.ddlRepositoryProvider = ddlRepositoryProvider;
    }

    public ObjectList loadObjects(Connect connect, Connection conn, String databaseName) throws SQLException {
        var repo = metadataRepositoryProvider.metadata(connect);
        ObjectList objectList = new ObjectList();
        List<Synonym> result = new ArrayList<>();
        objectList.setItems(result);
        int count = repo.getSynonymCount(conn);
        objectList.setInfo(count + "个");
        result.addAll(repo.getSynonyms(conn, databaseName));
        return objectList;
    }
    @Override
    public DdlFetcher ddlFetcher() {
        return (connect, conn, objectName) -> ddlRepositoryProvider.ddl(connect).printSynonym(conn, objectName);
    }
}

