package com.dbboys.service;

import com.dbboys.db.MetadataRepository;
import com.dbboys.impl.MetaObjectImpl;
import com.dbboys.db.DDLRepository;
import com.dbboys.vo.ObjectList;
import com.dbboys.vo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService implements MetaObjectImpl {
    private final MetadataRepository metadataRepository = new MetadataRepository();


    @Override
    public DdlFetcher ddlFetcher() {
        return null;
    }

    public ObjectList loadObjects(Connection conn, String databaseName) throws SQLException {
        return null;
    }

    public List<User> getUsers(Connection conn) throws SQLException {
        return metadataRepository.getUsers(conn);
    }
}



