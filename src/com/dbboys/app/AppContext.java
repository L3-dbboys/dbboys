package com.dbboys.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AppContext {
    private static final Map<Class<?>, Object> REGISTRY = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    private AppContext() {}

    public static void init() {
        if (initialized) return;
        synchronized (AppContext.class) {
            if (initialized) return;

            var dialectRegistry = com.dbboys.impl.dialect.DatabaseDialectRegistry.createDefault();
            var metaProvider = new com.dbboys.impl.DefaultMetadataRepositoryProvider(dialectRegistry);
            var sqlexeProvider = new com.dbboys.impl.DefaultSqlexeRepositoryProvider(dialectRegistry);
            var adminRepo = new com.dbboys.db.AdminRepository();

            register(com.dbboys.api.MetadataRepositoryProvider.class, metaProvider);
            register(com.dbboys.api.SqlexeRepositoryProvider.class, sqlexeProvider);
            register(com.dbboys.db.AdminRepository.class, adminRepo);

            var connService = new com.dbboys.impl.ConnectionServiceImpl(metaProvider, dialectRegistry);
            register(com.dbboys.api.ConnectionService.class, connService);

            register(com.dbboys.service.DatabaseService.class, new com.dbboys.service.DatabaseService(metaProvider));
            register(com.dbboys.service.TableService.class, new com.dbboys.service.TableService(metaProvider));
            register(com.dbboys.service.IndexService.class, new com.dbboys.service.IndexService(metaProvider));
            register(com.dbboys.service.ViewService.class, new com.dbboys.service.ViewService(metaProvider));
            register(com.dbboys.service.SequenceService.class, new com.dbboys.service.SequenceService(metaProvider));
            register(com.dbboys.service.SynonymService.class, new com.dbboys.service.SynonymService(metaProvider));
            register(com.dbboys.service.FunctionService.class, new com.dbboys.service.FunctionService(metaProvider));
            register(com.dbboys.service.ProcedureService.class, new com.dbboys.service.ProcedureService(metaProvider));
            register(com.dbboys.service.TriggerService.class, new com.dbboys.service.TriggerService(metaProvider));
            register(com.dbboys.service.PackageService.class, new com.dbboys.service.PackageService(metaProvider));
            register(com.dbboys.service.UserService.class, new com.dbboys.service.UserService(metaProvider));

            var dbService = get(com.dbboys.service.DatabaseService.class);
            register(com.dbboys.service.SqlexeService.class, new com.dbboys.service.SqlexeService(connService, dbService, sqlexeProvider));
            register(com.dbboys.service.AdminService.class, new com.dbboys.service.AdminService(connService, adminRepo));

            initialized = true;
        }
    }

    public static <T> void register(Class<T> type, T instance) {
        REGISTRY.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        T instance = (T) REGISTRY.get(type);
        if (instance == null) {
            throw new IllegalStateException("No instance registered for " + type.getName() + ". Call AppContext.init() first.");
        }
        return instance;
    }
}
