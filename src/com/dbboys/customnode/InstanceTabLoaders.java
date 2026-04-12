package com.dbboys.customnode;

import com.dbboys.service.AdminService;
import com.dbboys.vo.Connect;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.Logger;
import java.util.List;

final class InstanceTabLoaders {
    private InstanceTabLoaders() {
    }

    record SpaceTabData(List<CustomSpaceChart.SpaceUsage> dbspaceList,
                        List<CustomSpaceChart.SpaceUsage> chunkChartList,
                        List<CustomSpaceChart.SpaceUsage> databaseChartList,
                        List<CustomSpaceChart.SpaceUsage> tabChartList) {
    }

    static SpaceTabData loadSpaceTabData(Logger log, Connect connect, AdminService adminService) {
        try {
            List<List<CustomSpaceChart.SpaceUsage>> dataList = adminService.getStorageSpaceUsage(connect);
            return new SpaceTabData(dataList.get(0), dataList.get(1), dataList.get(2), dataList.get(3));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    static void renderSpaceTab(SpaceTabData data,
                               CustomSpaceChart dbspaceChart,
                               CustomSpaceChart chunkChart,
                               CustomSpaceChart databaseChart,
                               CustomSpaceChart tabChart,
                               CustomSpaceChart.ContextMenuListener menuListener,
                               StackPane dbspaceStackPane,
                               CustomTab spaceTab,
                               boolean disableMenu) {
        Platform.runLater(() -> {
            dbspaceChart.render(data.dbspaceList());
            chunkChart.render(data.chunkChartList());
            databaseChart.render(data.databaseChartList());
            tabChart.render(data.tabChartList());
            dbspaceChart.setContextMenuListener(menuListener);
            chunkChart.setContextMenuListener(menuListener);
            if (disableMenu) {
                dbspaceChart.setMenuItemsDisabled(true);
                chunkChart.setMenuItemsDisabled(true);
                databaseChart.setMenuItemsDisabled(true);
                tabChart.setMenuItemsDisabled(true);
            }
            spaceTab.setContent(dbspaceStackPane);
        });
    }

}
