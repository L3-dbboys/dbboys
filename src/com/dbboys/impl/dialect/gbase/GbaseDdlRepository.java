package com.dbboys.impl.dialect.gbase;

import com.dbboys.api.DdlRepository;

import java.sql.Connection;

/**
 * GBase DDL 实现。
 */
public final class GbaseDdlRepository implements DdlRepository {

    @Override
    public String printTable(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printTable(conn, objectName);
    }

    @Override
    public String printView(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printView(conn, objectName);
    }

    @Override
    public String printIndex(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printIndex(conn, objectName);
    }

    @Override
    public String printSequence(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printSequence(conn, objectName);
    }

    @Override
    public String printSynonym(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printSynonym(conn, objectName);
    }

    @Override
    public String printFunction(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printProcedure(conn, objectName);
    }

    @Override
    public String printProcedure(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printProcedure(conn, objectName);
    }

    @Override
    public String printTrigger(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printTrigger(conn, objectName);
    }

    @Override
    public String printPackage(Connection conn, String objectName) throws Exception {
        return GbaseDdlSupport.printPackage(conn, objectName);
    }
}
