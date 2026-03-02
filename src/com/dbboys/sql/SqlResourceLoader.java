package com.dbboys.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads named SQL strings from .sql resource files.
 * SQL files use the format: -- @key followed by the SQL text.
 * Blank lines between entries are ignored.
 */
public final class SqlResourceLoader {
    private static final Logger log = LogManager.getLogger(SqlResourceLoader.class);
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;

    private SqlResourceLoader() {}

    public static String get(String key) {
        ensureLoaded();
        String sql = CACHE.get(key);
        if (sql == null) {
            throw new IllegalArgumentException("SQL key not found: " + key);
        }
        return sql;
    }

    public static String getOrNull(String key) {
        ensureLoaded();
        return CACHE.get(key);
    }

    private static void ensureLoaded() {
        if (!loaded) {
            synchronized (SqlResourceLoader.class) {
                if (!loaded) {
                    loadFile("/com/dbboys/sql/metadata.sql");
                    loadFile("/com/dbboys/sql/admin.sql");
                    loadFile("/com/dbboys/sql/ddl.sql");
                    loadFile("/com/dbboys/sql/sqlexe.sql");
                    loaded = true;
                }
            }
        }
    }

    private static void loadFile(String resourcePath) {
        InputStream is = SqlResourceLoader.class.getResourceAsStream(resourcePath);
        if (is == null) {
            log.warn("SQL resource not found: {}", resourcePath);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            String currentKey = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-- @")) {
                    if (currentKey != null && sb.length() > 0) {
                        CACHE.put(currentKey, sb.toString().trim());
                    }
                    currentKey = line.substring(4).trim();
                    sb.setLength(0);
                } else if (currentKey != null) {
                    sb.append(line).append("\n");
                }
            }
            if (currentKey != null && sb.length() > 0) {
                CACHE.put(currentKey, sb.toString().trim());
            }
        } catch (Exception e) {
            log.error("Failed to load SQL resource: {}", resourcePath, e);
        }
    }
}
