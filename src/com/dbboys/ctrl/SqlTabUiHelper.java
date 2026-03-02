package com.dbboys.ctrl;

import com.dbboys.app.Main;
import com.dbboys.customnode.*;
import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.vo.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.IOException;

/**
 * Handles UI setup logic extracted from SqlTabController.
 * Manages icon setup, split pane behavior, result set view, etc.
 */
public class SqlTabUiHelper {
    private static final Logger log = LogManager.getLogger(SqlTabUiHelper.class);

    private final SqlTabController ctrl;

    public SqlTabUiHelper(SqlTabController ctrl) {
        this.ctrl = ctrl;
    }

    public void setupTransactionTooltips() {
        ctrl.commitButtonTooltip.textProperty().bind(ctrl.sqlTransactionText);
        ctrl.commitButtonTooltip.setShowDelay(javafx.util.Duration.millis(100));
        ctrl.transactionCommitButton.setTooltip(ctrl.commitButtonTooltip);
        ctrl.transactionRollbackButton.setTooltip(ctrl.commitButtonTooltip);
    }

    public void setupSqlTabIcons() {
        ctrl.sqlExecuteLoadingLabel.setGraphic(IconFactory.imageView(IconPaths.LOADING_GIF, 12, 12, true));
        ctrl.sqlRunButton.setGraphic(IconFactory.group(IconPaths.SQL_RUN, 0.7, Color.valueOf("#074675")));
        ctrl.sqlExplainButton.setGraphic(IconFactory.group(IconPaths.SQL_EXPLAIN, 0.7, Color.valueOf("#074675")));
        ctrl.sqlStopButton.setGraphic(IconFactory.group(IconPaths.SQL_STOP, 0.75, Color.valueOf("#9f453c")));
        ctrl.sqlRecordButton.setGraphic(IconFactory.group(IconPaths.SQL_HISTORY, 0.8, Color.valueOf("#074675")));
        ctrl.sqlReadOnlyLabel.setGraphic(IconFactory.group(IconPaths.SQL_READONLY, 0.5, Color.valueOf("#9f453c")));

        if (ctrl.sqlDbIconPane != null) {
            ctrl.sqlDbIconPane.getChildren().setAll(IconFactory.group(IconPaths.SQL_DATABASE, 0.4, Color.valueOf("#888")));
        }
        if (ctrl.sqlUserIconPane != null) {
            ctrl.sqlUserIconPane.getChildren().setAll(IconFactory.group(IconPaths.SQL_USER, 0.55, Color.valueOf("#888")));
        }
    }
}
