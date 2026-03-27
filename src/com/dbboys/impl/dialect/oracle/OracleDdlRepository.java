package com.dbboys.impl.dialect.oracle;

import com.dbboys.api.DdlRepository;

import java.sql.Connection;

/**
 * Oracle DDL 占位实现。
 */
public final class OracleDdlRepository implements DdlRepository {

    private static final String MSG = "Oracle DDL repository not implemented";

    @Override
    public String printTable(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printView(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printIndex(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printSequence(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printSynonym(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printFunction(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printProcedure(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printTrigger(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public String printPackage(Connection conn, String objectName) {
        throw new UnsupportedOperationException(MSG);
    }
}
