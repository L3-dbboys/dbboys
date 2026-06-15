package com.dbboys.service;

import com.dbboys.core.DatabasePlatformResolver;
import com.dbboys.core.MetaObjectService;
import com.dbboys.core.MetaObjectService.DdlFetcher;
import com.dbboys.model.Connect;
import com.dbboys.model.Queue;
import com.dbboys.model.ObjectList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueueService implements MetaObjectService {
    private final DatabasePlatformResolver platformResolver;

    public QueueService() {
        this(com.dbboys.app.AppContext.get(DatabasePlatformResolver.class));
    }

    public QueueService(DatabasePlatformResolver platformResolver) {
        this.platformResolver = platformResolver;
    }

    @Override
    public ObjectList loadObjects(Connect connect, Connection conn, String databaseName) throws SQLException {
        var repo = platformResolver.metadata(connect);
        ObjectList objectList = new ObjectList();
        List<Queue> result = new ArrayList<>();
        objectList.setItems(result);
        int count = repo.getQueueCount(conn, databaseName);
        objectList.setInfo(count + "个");
        result.addAll(repo.getQueues(conn, databaseName));
        return objectList;
    }

    @Override
    public DdlFetcher ddlFetcher() {
        return (connect, conn, objectName) -> platformResolver.ddl(connect).printQueue(conn, objectName);
    }
}
