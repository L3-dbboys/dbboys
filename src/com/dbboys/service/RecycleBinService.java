package com.dbboys.service;

import com.dbboys.core.DatabasePlatformResolver;
import com.dbboys.core.MetaObjectService;
import com.dbboys.model.Connect;
import com.dbboys.model.ObjectList;
import com.dbboys.model.RecycleBinObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecycleBinService implements MetaObjectService {

    private final DatabasePlatformResolver platformResolver;

    public RecycleBinService() {
        this(com.dbboys.app.AppContext.get(DatabasePlatformResolver.class));
    }

    public RecycleBinService(DatabasePlatformResolver platformResolver) {
        this.platformResolver = platformResolver;
    }

    @Override
    public ObjectList loadObjects(Connect connect, Connection conn, String databaseName) throws SQLException {
        var repo = platformResolver.metadata(connect);
        ObjectList objectList = new ObjectList();
        List<RecycleBinObject> result = new ArrayList<>();
        objectList.setItems(result);
        List<String> names = repo.getRecycleBinDisplayNames(conn);
        for (String n : names) {
            if (n != null && !n.isBlank()) {
                result.add(new RecycleBinObject(n.trim()));
            }
        }
        objectList.setInfo(result.size() + "个");
        return objectList;
    }

    @Override
    public DdlFetcher ddlFetcher() {
        return (connect, conn, objectName) -> "--";
    }
}
