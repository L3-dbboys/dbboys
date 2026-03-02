package com.dbboys.ctrl;

import com.dbboys.i18n.I18n;
import com.dbboys.vo.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles I18n setup and refresh logic extracted from SqlTabController.
 * Manages locale bindings, choice box converters, text formatting, etc.
 */
public class SqlTabI18nHelper {
    private static final Logger log = LogManager.getLogger(SqlTabI18nHelper.class);
    private static final String COMMIT_AUTO = "AUTO";
    private static final String COMMIT_MANUAL = "MANUAL";

    private final SqlTabController ctrl;

    public SqlTabI18nHelper(SqlTabController ctrl) {
        this.ctrl = ctrl;
    }

    public void bindText(Labeled node, String key) {
        if (node != null) {
            node.textProperty().bind(I18n.bind(key));
        }
    }

    public void bindTabText(Tab tab, String key) {
        if (tab != null) {
            tab.textProperty().bind(I18n.bind(key));
        }
    }

    public void bindColumnText(TableColumn<?, ?> col, String key) {
        if (col != null) {
            col.textProperty().bind(I18n.bind(key));
        }
    }

    public void bindTooltip(Control control, String key) {
        if (control != null) {
            Tooltip tooltip = control.getTooltip();
            if (tooltip == null) {
                tooltip = new Tooltip();
                control.setTooltip(tooltip);
            }
            tooltip.textProperty().bind(I18n.bind(key));
        }
    }

    public String formatExecuteTime(double seconds) {
        return I18n.t("sql.exec.elapsed", "耗时") + ": " + String.format("%.1f", seconds) + "s";
    }

    public String formatElapsedSeconds(long millis) {
        return String.format("%.3f", millis / 1000.0) + " sec";
    }

    public String buildExecutionMark() {
        String commitMode = getCommitModeLabel();
        String sqlMode = ctrl.sqlSqlModeChoiceBox.getValue() != null ? ctrl.sqlSqlModeChoiceBox.getValue() : "";
        return commitMode + (sqlMode.isEmpty() ? "" : "," + sqlMode);
    }

    public String getCommitModeLabel() {
        int idx = ctrl.sqlCommitModeChoiceBox.getSelectionModel().getSelectedIndex();
        if (idx == 0) {
            return I18n.t("sql.commit.auto");
        } else if (idx == 1) {
            return I18n.t("sql.commit.manual");
        }
        return "";
    }

    public void refreshConnectChoiceBoxItems() {
        Platform.runLater(() -> {
            ctrl.defaultConnect.setName(I18n.t("sql.connect.select_prompt"));
            Object current = ctrl.sqlConnectChoiceBox.getValue();
            ctrl.sqlConnectChoiceBox.setItems(null);
            ctrl.sqlConnectChoiceBox.setItems(ctrl.sqlConnectChoiceBox.getItems());
            ctrl.sqlConnectChoiceBox.setValue((Connect) current);
        });
    }

    public void refreshDefaultConnectDisplay() {
        if (ctrl.sqlConnectChoiceBox.getValue() == ctrl.defaultConnect) {
            ctrl.sqlConnectChoiceBox.setValue(null);
            ctrl.sqlConnectChoiceBox.setValue(ctrl.defaultConnect);
        }
    }

    public void refreshDbChoiceBoxDisplay() {
        if (ctrl.sqlDbChoiceBox.getValue() == ctrl.defaultDatabase) {
            ctrl.sqlDbChoiceBox.setValue(null);
            ctrl.sqlDbChoiceBox.setValue(ctrl.defaultDatabase);
        }
    }

    public void refreshCommitModeItems() {
        int selectedIndex = ctrl.sqlCommitModeChoiceBox.getSelectionModel().getSelectedIndex();
        ctrl.sqlCommitModeChoiceBox.getItems().clear();
        ctrl.sqlCommitModeChoiceBox.getItems().addAll(COMMIT_AUTO, COMMIT_MANUAL);
        if (selectedIndex >= 0 && selectedIndex < ctrl.sqlCommitModeChoiceBox.getItems().size()) {
            ctrl.sqlCommitModeChoiceBox.getSelectionModel().select(selectedIndex);
        } else {
            ctrl.sqlCommitModeChoiceBox.getSelectionModel().select(0);
        }
    }

    public <T> void setupChoiceBoxConverter(ChoiceBox<T> choiceBox) {
        choiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
    }
}
