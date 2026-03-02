package com.dbboys.ctrl;

import com.dbboys.app.AppExecutor;
import com.dbboys.i18n.I18n;
import com.dbboys.service.ConnectionService;
import com.dbboys.service.SqlexeService;
import com.dbboys.util.*;
import com.dbboys.vo.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles SQL execution logic extracted from SqlTabController.
 * Manages createExecuteSqlTask, createExplainTask, createSqlModeTask, etc.
 */
public class SqlExecutionHelper {
    private static final Logger log = LogManager.getLogger(SqlExecutionHelper.class);

    private final SqlTabController ctrl;

    public SqlExecutionHelper(SqlTabController ctrl) {
        this.ctrl = ctrl;
    }

    public String resolveSqlText(boolean allowRefresh) {
        if (allowRefresh && ctrl.isSqlRefresh && ctrl.currentResultSetTabController != null
                && ctrl.currentResultSetTabController.lastSqlTextField.getTooltip() != null) {
            return ctrl.currentResultSetTabController.lastSqlTextField.getTooltip().getText();
        }
        if (ctrl.sqlEditCodeArea.getSelectedText().isEmpty()) {
            return ctrl.sqlEditCodeArea.getText();
        }
        return ctrl.sqlEditCodeArea.getSelectedText();
    }

    public void cancelCurrentExecution() {
        if (ctrl.sqlTask != null) {
            ctrl.sqlTask.cancel();
        }
        try {
            if (ctrl.sqlStatement != null) {
                ctrl.sqlStatement.cancel();
                ctrl.sqlStatement = null;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        if (ctrl.currentResultSetTabController != null) {
            ctrl.currentResultSetTabController.cancel();
        }
    }

    public int getWhitespaceLength(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
