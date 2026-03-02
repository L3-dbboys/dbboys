package com.dbboys.service;

import com.dbboys.impl.IMetaObjectService;
import com.dbboys.impl.IMetaObjectService.DdlFetcher;
import com.dbboys.db.MetadataRepository;
import com.dbboys.db.DDLRepository;
import com.dbboys.util.SqlParserUtil;
import com.dbboys.vo.Connect;
import com.dbboys.vo.DBPackage;
import com.dbboys.vo.Database;
import com.dbboys.vo.ObjectList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageService implements IMetaObjectService {
    private final MetadataRepository metadataRepository;

    public PackageService() {
        this(new MetadataRepository());
    }

    public PackageService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public ObjectList loadObjects(Connection conn, String databaseName) throws SQLException {
        ObjectList objectList = new ObjectList();
        List<DBPackage> result = new ArrayList<>();
        objectList.setItems(result);
        int count = metadataRepository.getPackageCount(conn);
        objectList.setInfo(count + "个");
        result.addAll(metadataRepository.getPackages(conn, databaseName));
        return objectList;
    }
    @Override
    public DdlFetcher ddlFetcher() {
        return DDLRepository::printPackage;
    }

    public String getChildrenDDL(String packageDDL,String objectName) throws SQLException {
        return SqlParserUtil.printPackageFunction(packageDDL, objectName);
    }

}



