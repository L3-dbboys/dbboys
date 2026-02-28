package com.dbboys.ctrl;

import com.dbboys.app.Main;
import com.dbboys.customnode.CustomInfoCodeArea;
import com.dbboys.customnode.CustomInfoStackPane;
import com.dbboys.customnode.CustomLabelTextField;
import com.dbboys.customnode.CustomResultsetTableView;
import com.dbboys.customnode.CustomTableCell;
import com.dbboys.customnode.CustomUserTextField;
import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.util.*;
import com.dbboys.vo.Connect;
import com.dbboys.vo.UpdateResult;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ResultSetTabController {
    private static final Logger log = LogManager.getLogger(ResultSetTabController.class);

    @FXML
    public CustomLabelTextField lastSqlTextField;
    @FXML
    public Button lastSqlRefreshButton;
    @FXML
    public VBox resultSetVBox;
    @FXML
    public CustomResultsetTableView resultSetTableView;
    @FXML
    public HBox resultSetButtonHBox;
    @FXML
    public Label resultSetEditableEnabledLabel;
    @FXML
    public Label resultSetEditableDisabledLabel;
    @FXML
    public Label resultSetLabelLeftBracket;
    @FXML
    public Label resultSetLabelRightBracket;
    @FXML
    public Label resultSetLabelPerTimePrefix;
    @FXML
    public Label resultSetLabelBetweenNext;
    @FXML
    public Label resultSetLabelBetweenAll;
    @FXML
    public Label resultSetLabelFetchedPrefix;
    @FXML
    public Label resultSetLabelFetchedSuffix;
    @FXML
    public Label resultSetLabelTotalPrefix;
    @FXML
    public Label resultSetLabelTotalSuffix;
    @FXML
    public Label resultSetLabelTimeSuffix;
    @FXML
    public Label resultSetLabelEnd;
    @FXML
    public CustomUserTextField resultSetPerTimeTextField;
    @FXML
    public Button resultSetNextPageButton;
    @FXML
    public Button resultSetAllRowsButton;
    @FXML
    public Button resultSetCountButton;
    @FXML
    public Label resultSetFetchedRowsLabel;
    @FXML
    public Label resultSetTotalRowsLabel;
    @FXML
    public Label sqlUsedTimeLabel;
    @FXML
    public Button resultSetExportButton;
    public Button hiddenDisconnectedButton = new Button();
    public StackPane sqlExecuteProcessStackPane;
    public Connect sqlConnect;
    public ResultSet sqlResultSet;
    public long sqlFetchedTime = 0;
    public long sqlFetchStartTime = 0;
    public long sqlFetchEndTime = 0;
    public Integer sqlFetchedRows = 0;
    public List<ObservableList<String>> sqlResultSetList = new ArrayList<>();
    public PreparedStatement sqlStatement;
    public ParameterMetaData parameterMetaData;
    public CallableStatement sqlCstmt;
    public Boolean callHasResultSet = false;
    public List<TableColumn<ObservableList<String>, Object>> colList = new ArrayList<>();
    public Label sqlExecuteProcessLabel;
    public Task<Void> sqlTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            return null;
        }
    };
//获取主键相关变量
    public List<Integer> resultTablePriNum = new ArrayList();
    public String resultFromTable;
    public List resultTableCols = new ArrayList();
    public ResultSet priSqlResult; //获取主键结果集
    public List sqlParamList = new ArrayList();
    public ResultSetMetaData sqlMetaData;
    public Runnable eventEnd = () -> Platform.runLater(this::finishFetch);
    //最后一次执行的sql
    @FXML
    private Button lastSqlCopyButton;
    private String sqlResultCount;


    public ResultSetTabController(Connect sqlConnect, StackPane sqlExecuteProcessStackPane) {
        this.sqlExecuteProcessStackPane = sqlExecuteProcessStackPane;
        this.sqlConnect = sqlConnect;
    }

    public void initialize() {
        initI18nBindings();
        setupIcons();
        bindUiState();
        initExecuteProcessLabel();
        setupLastSql();
        setupTableView();
        setupPerTimeField();
        setupButtons();
    }

    private void bindUiState() {
        resultSetEditableDisabledLabel.visibleProperty().bind(resultSetEditableEnabledLabel.visibleProperty().not());
        resultSetNextPageButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        resultSetAllRowsButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        lastSqlRefreshButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        resultSetExportButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        resultSetCountButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
    }

    private void initI18nBindings() {
        bindTooltip(lastSqlCopyButton, "resultset.tooltip.copy_sql");
        bindTooltip(lastSqlRefreshButton, "resultset.tooltip.refresh_sql");
        bindTooltip(resultSetEditableEnabledLabel, "resultset.tooltip.editable_enabled");
        bindTooltip(resultSetEditableDisabledLabel, "resultset.tooltip.editable_disabled");
        bindTooltip(resultSetNextPageButton, "resultset.tooltip.next_page");
        bindTooltip(resultSetAllRowsButton, "resultset.tooltip.all_rows");
        bindTooltip(resultSetCountButton, "resultset.tooltip.count_rows");
        bindTooltip(resultSetExportButton, "resultset.tooltip.export_loaded");

        bindText(resultSetLabelLeftBracket, "resultset.label.left_bracket");
        bindText(resultSetLabelRightBracket, "resultset.label.right_bracket");
        bindText(resultSetLabelPerTimePrefix, "resultset.label.per_time_prefix");
        bindText(resultSetLabelBetweenNext, "resultset.label.between_next");
        bindText(resultSetLabelBetweenAll, "resultset.label.between_all");
        bindText(resultSetLabelFetchedPrefix, "resultset.label.fetched_prefix");
        bindText(resultSetLabelFetchedSuffix, "resultset.label.fetched_suffix");
        bindText(resultSetLabelTotalPrefix, "resultset.label.total_prefix");
        bindText(resultSetLabelTotalSuffix, "resultset.label.total_suffix");
        bindText(resultSetLabelTimeSuffix, "resultset.label.time_suffix");
        bindText(resultSetLabelEnd, "resultset.label.end");

        ensureLastSqlTooltip();
        if (lastSqlTextField.getText() == null || lastSqlTextField.getText().isBlank()) {
            lastSqlTextField.setText(I18n.t("resultset.sample.sql"));
        }
        if (lastSqlTextField.getTooltip().getText() == null || lastSqlTextField.getTooltip().getText().isBlank()) {
            lastSqlTextField.getTooltip().setText(I18n.t("resultset.sample.sql"));
        }
    }

    private void setupIcons() {
        lastSqlCopyButton.setGraphic(IconFactory.group(IconPaths.COPY, 0.6, Color.valueOf("#074675")));
        lastSqlRefreshButton.setGraphic(IconFactory.group(IconPaths.MAIN_REBUILD, 0.6, Color.valueOf("#074675")));
        resultSetEditableEnabledLabel.setGraphic(IconFactory.group(IconPaths.RESULTSET_EDITABLE, 0.6, Color.valueOf("#074675")));
        resultSetEditableDisabledLabel.setGraphic(IconFactory.group(IconPaths.RESULTSET_EDITABLE_DISABLED, 0.45, Color.valueOf("#9f453c")));
        resultSetNextPageButton.setGraphic(IconFactory.group(IconPaths.RESULTSET_NEXT_PAGE, 0.6, Color.valueOf("#074675")));
        resultSetAllRowsButton.setGraphic(IconFactory.group(IconPaths.RESULTSET_ALL_ROWS, 0.5, Color.valueOf("#074675")));
        resultSetCountButton.setGraphic(IconFactory.group(IconPaths.RESULTSET_COUNT, 0.5, Color.valueOf("#074675")));
        resultSetExportButton.setGraphic(IconFactory.group(IconPaths.RESULTSET_EXPORT, 0.5, Color.valueOf("#074675")));
    }

    private void bindText(Labeled labeled, String key) {
        labeled.textProperty().bind(I18n.bind(key));
    }

    private void bindTooltip(Control control, String key) {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(I18n.bind(key));
        tooltip.setShowDelay(Duration.millis(100));
        control.setTooltip(tooltip);
    }

    private void ensureLastSqlTooltip() {
        Tooltip tooltip = lastSqlTextField.getTooltip();
        if (tooltip == null) {
            tooltip = new Tooltip();
            lastSqlTextField.setTooltip(tooltip);
        }
        tooltip.setShowDelay(Duration.millis(100));
    }

    private void initExecuteProcessLabel() {
        sqlExecuteProcessLabel = (Label) ((HBox) sqlExecuteProcessStackPane.getChildren().get(0)).getChildren().get(1);
    }

    private void setupLastSql() {
        lastSqlTextField.getTooltip().setShowDelay(Duration.millis(100));
        lastSqlCopyButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(lastSqlTextField.getTooltip().getText());
            clipboard.setContent(content);
            NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("resultset.notice.copied", "已复制！"));
        });
        lastSqlRefreshButton.setOnAction(event -> {
        });
    }

    private void setupTableView() {
        Label tableviewEmptyLabel = new Label();
        tableviewEmptyLabel.textProperty().bind(I18n.bind("resultset.placeholder.empty"));
        resultSetTableView.getStyleClass().add("resultset-table-view");
        resultSetTableView.setPlaceholder(tableviewEmptyLabel);
        resultSetTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultSetTableView.getSelectionModel().setCellSelectionEnabled(true);
        resultSetTableView.prefWidthProperty().bind(resultSetVBox.widthProperty());
        resultSetTableView.prefHeightProperty().bind(resultSetVBox.heightProperty());
        resultSetTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        resultSetTableView.setSortPolicy(tv -> {
            TableView.DEFAULT_SORT_POLICY.call((TableView) tv);
            Platform.runLater(() -> resultSetTableView.getSelectionModel().clearSelection());
            return true;
        });
    }

    private void setupPerTimeField() {
        resultSetPerTimeTextField.setText(ConfigManagerUtil.getProperty("RESULT_FETCH_PER_TIME", "200"));
        resultSetPerTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                resultSetPerTimeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setupButtons() {
        resultSetNextPageButton.setOnAction(event -> {
            sqlTask = createGetNextPageResultSetTask();
            new Thread(sqlTask).start();
        });
        resultSetAllRowsButton.setOnAction(event -> {
            sqlTask = createGetAllResultSetTask();
            new Thread(sqlTask).start();
        });
        resultSetCountButton.setOnAction(event -> {
            sqlTask = createGetResultSetCountTask();
            new Thread(sqlTask).start();
        });
        resultSetExportButton.setOnAction(event -> {
            if (resultSetTableView.getItems().size() == 0) {
                AlterUtil.CustomAlert(
                        I18n.t("common.error", "错误"),
                        I18n.t("resultset.export.empty", "当前结果集为空，无数据需要导出！")
                );
                return;
            }
            if (!AlterUtil.CustomAlertConfirm(
                    I18n.t("resultset.export.title", "结果集导出"),
                    I18n.t("resultset.export.confirm", "导出程序只导出已加载到结果集表格的数据，确定要执行导出吗?")
            )) {
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18n.t("resultset.export.title", "结果集导出"));
            String defaultName = I18n.t("resultset.export.filename_prefix", "结果集导出")
                    + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                    + ".xlsx";
            fileChooser.setInitialFileName(defaultName);
            File file = fileChooser.showSaveDialog(Main.scene.getWindow());

            if (file != null) {
                if (file.exists()) {
                    file.delete();
                }
                DownloadManagerUtil.addDownload(resultSetTableView, file, true, sqlMetaData);
            }
        });
    }

    private void finishFetch() {
        sqlExecuteProcessStackPane.setVisible(false);
        sqlFetchEndTime = System.currentTimeMillis();
        sqlFetchedTime += sqlFetchEndTime - sqlFetchStartTime;
        resultSetTableView.getItems().addAll(sqlResultSetList);
        if (sqlFetchedRows > 0) {
            int current = Integer.parseInt(resultSetFetchedRowsLabel.getText());
            resultSetFetchedRowsLabel.setText(String.valueOf(current + sqlFetchedRows));
            sqlUsedTimeLabel.setText(String.valueOf(sqlFetchedTime / 1000.0));
        }
        sqlResultSetList.clear();
    }

    public void init() {
        callHasResultSet = false;
        closeResultSet();
        sqlFetchedTime = 0;
        sqlFetchedRows = 0;
        Platform.runLater(() -> {
            resultSetEditableEnabledLabel.setVisible(false);
            resultSetTableView.setEditable(false);
            resultSetTableView.getColumns().setAll(resultSetTableView.getColumns().get(0));
            resultSetTableView.getItems().clear();
            resultSetFetchedRowsLabel.setText("0");
            resultSetTotalRowsLabel.setText("?");
            sqlUsedTimeLabel.setText("0");
        });

    }

    public Task<Void> createGetNextPageResultSetTask() {
        return createFetchTask(false);
    }


    public Task<Void> createGetAllResultSetTask() {
        return createFetchTask(true);
    }


    public Task<Void> createGetResultSetCountTask() {
        //sql_is_count=true;
        sqlExecuteProcessStackPane.setVisible(true);
        setFetchStatus(" " + I18n.t("sql.result.fetching_total"));
        String sqlCount = "select count(*) from (" + lastSqlTextField.getTooltip().getText().replaceFirst(";\\s*$", "") + ")";
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ResultSet rs = null;
                try {
                    sqlStatement = sqlConnect.getConn().prepareStatement(sqlCount);
                    rs = sqlStatement.executeQuery();
                    rs.next();
                    sqlResultCount = String.valueOf(rs.getInt(1));
                    rs.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        //把结果集游标关闭，避免在activedatabase时关闭游标触发79730错误导致重复弹出是否需要重新连接。
                        if (rs != null) {
                            rs = null;
                        }
                        hiddenDisconnectedButton.fire();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    if (rs != null) {
                        rs = null;
                    }
                }
                return null;
            }
        };

        sqlTask.setOnSucceeded(event1 -> {
            sqlExecuteProcessStackPane.setVisible(false);
            resultSetTotalRowsLabel.setText(sqlResultCount);
        });
        sqlTask.setOnCancelled(event1 -> sqlExecuteProcessStackPane.setVisible(false));
        sqlTask.setOnFailed(event1 -> sqlExecuteProcessStackPane.setVisible(false));

        return sqlTask;
    }


    public long executeSelect(String sqlExe, Task sqlTask, Boolean showFething, SimpleStringProperty sqlTransactionText, ChoiceBox commitmode) throws SQLException {
        init();
        sqlFetchedRows = 0;
        final boolean showFetching = Boolean.TRUE.equals(showFething);
        sqlStatement = sqlConnect.getConn().prepareStatement(sqlExe);
        parameterMetaData = sqlStatement.getParameterMetaData();

        if (!prepareParams(parameterMetaData, sqlStatement)) {
            return 0;
        }

        long sqlStartTime = System.currentTimeMillis();
        bindParams(sqlStatement);

        sqlStatement.setFetchSize(200);
        try {
            sqlResultSet = sqlStatement.executeQuery();
        } catch (SQLException e) {
            if (sqlTask != null && sqlTask.isCancelled()) {
                return 0;
            }
            throw e;
        }
        long sqlEndTime = System.currentTimeMillis();
        sqlFetchedTime = sqlEndTime - sqlStartTime;
        //updateMessage("Fetching... ");
        //获取结果集
        sqlMetaData = sqlResultSet.getMetaData();
        int columnCount = sqlMetaData.getColumnCount();
        buildColumns(sqlMetaData, showFetching, sqlTransactionText, commitmode);
        // 添加数据到 TableView
        sqlStartTime = System.currentTimeMillis();
        int perpage = getPerPageLimit();
        if (showFetching) {
            setFetchStatus(" " + I18n.t("sql.result.fetching"));
        }

        List<ObservableList<String>> fetchedRows = fetchRows(
                sqlResultSet,
                perpage,
                sqlTask, columnCount,
                showFetching ? fetched -> setFetchStatus(formatFetchedRows(fetched)) : null
        );
        sqlResultSetList.addAll(fetchedRows);
        sqlFetchedRows += fetchedRows.size();

        applyFetchedRows(sqlExe);

        sqlEndTime = System.currentTimeMillis();
        sqlFetchedTime += sqlEndTime - sqlStartTime;
        //sqlStatement.close();


        return sqlFetchedTime;


    }


    public long executeCall(String sqlExe, Task sqlTask, Boolean showFething) throws SQLException {
        init();
        sqlFetchedRows = 0;
        final boolean showFetching = Boolean.TRUE.equals(showFething);
        sqlCstmt = sqlConnect.getConn().prepareCall(sqlExe);

        parameterMetaData = sqlCstmt.getParameterMetaData();
        long sqlStartTime = System.currentTimeMillis();
        if (!prepareParams(parameterMetaData, sqlCstmt)) {
            return 0;
        }
        sqlStartTime = System.currentTimeMillis();
        bindParams(sqlCstmt);

        try {
            callHasResultSet = sqlCstmt.execute();
        } catch (SQLException e) {
            if (sqlTask != null && sqlTask.isCancelled()) {
                return 0;
            }
            throw e;
        }

        if (callHasResultSet) {
            sqlResultSet = sqlCstmt.getResultSet();
            long sqlEndTime = System.currentTimeMillis();
            sqlFetchedTime = sqlEndTime - sqlStartTime;
            sqlMetaData = sqlResultSet.getMetaData();
            int columnCount = sqlMetaData.getColumnCount();
            buildColumns(sqlMetaData, false, null, null);

            sqlStartTime = System.currentTimeMillis();
        int perpage = getPerPageLimit();
        if (showFetching) {
            setFetchStatus(" " + I18n.t("sql.result.fetching"));
        }

            List<ObservableList<String>> fetchedRows = fetchRows(
                    sqlResultSet,
                    perpage,
                    sqlTask,
                    columnCount,
                    showFetching ? fetched -> setFetchStatus(formatFetchedRows(fetched)) : null
            );
            sqlResultSetList.addAll(fetchedRows);
            sqlFetchedRows += fetchedRows.size();
            Platform.runLater(() -> resultSetButtonHBox.getChildren().remove(resultSetCountButton));
            applyFetchedRows(sqlExe);

            sqlEndTime = System.currentTimeMillis();
            sqlFetchedTime += sqlEndTime - sqlStartTime;
        }
        return sqlFetchedTime;
    }


    public void getPrimaryKeys(String sqlExe) {
        resultTablePriNum.clear();
        resultFromTable = SqlParserUtil.getFromTable(sqlExe);
        String sql_get_primary = """
                select trim(case when i.part1>0 then (select colname from syscolumns where colno=i.part1 and tabid=i.tabid) else '' end)||
                trim(case when i.part2>0 then (select ','||colname from syscolumns where colno=i.part2 and tabid=i.tabid) else '' end)||
                trim(case when i.part3>0 then (select ','||colname from syscolumns where colno=i.part3 and tabid=i.tabid) else '' end)||
                trim(case when i.part4>0 then (select ','||colname from syscolumns where colno=i.part4 and tabid=i.tabid) else '' end)||
                trim(case when i.part5>0 then (select ','||colname from syscolumns where colno=i.part5 and tabid=i.tabid) else '' end)||
                trim(case when i.part6>0 then (select ','||colname from syscolumns where colno=i.part6 and tabid=i.tabid) else '' end)||
                trim(case when i.part7>0 then (select ','||colname from syscolumns where colno=i.part7 and tabid=i.tabid) else '' end)||
                trim(case when i.part8>0 then (select ','||colname from syscolumns where colno=i.part8 and tabid=i.tabid) else '' end)||
                trim(case when i.part9>0 then (select ','||colname from syscolumns where colno=i.part9 and tabid=i.tabid) else '' end)||
                trim(case when i.part10>0 then (select ','||colname from syscolumns where colno=i.part10 and tabid=i.tabid) else '' end)||
                trim(case when i.part11>0 then (select ','||colname from syscolumns where colno=i.part11 and tabid=i.tabid) else '' end)||
                trim(case when i.part12>0 then (select ','||colname from syscolumns where colno=i.part12 and tabid=i.tabid) else '' end)||
                trim(case when i.part13>0 then (select ','||colname from syscolumns where colno=i.part13 and tabid=i.tabid) else '' end)||
                trim(case when i.part14>0 then (select ','||colname from syscolumns where colno=i.part14 and tabid=i.tabid) else '' end)||
                trim(case when i.part15>0 then (select ','||colname from syscolumns where colno=i.part15 and tabid=i.tabid) else '' end)||
                trim(case when i.part16>0 then (select ','||colname from syscolumns where colno=i.part16 and tabid=i.tabid) else '' end )
                index_cols from systables t ,
                sysconstraints c
                ,sysindexes i
                where
                t.tabid=c.tabid
                and t.tabid=i.tabid
                and tabtype='T'
                and c.constrtype='P'
                and tabname=?
                """;
        String finalSqlGetPrimary = sql_get_primary;

        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.cancel();
                    } catch (SQLException e) {
                    }
                }

                try {
                    PrimaryKeyInfo info = fetchPrimaryKeyInfo(finalSqlGetPrimary, sqlExe);
                    Platform.runLater(() -> applyPrimaryKeyInfo(info));
                } catch (SQLException e) {
                    // ignore primary key failures
                }
                sqlStatement.close();
                return null;
            }
        };

        new Thread(sqlTask).start();
    }

    public void closeResultSet() {
        try {
            if (sqlResultSet != null) {
                sqlResultSet.close();
            }
            if (priSqlResult != null) {
                priSqlResult.close();
            }
        } catch (SQLException e) {
            sqlResultSet = null;
            priSqlResult = null;
            log.error(e.getMessage(), e);
        }
    }

    public void cancel() {
        if (sqlTask != null && sqlTask.isRunning()) {
            sqlTask.cancel();

        }
        if (sqlStatement != null) {
            try {
                sqlStatement.cancel();
            } catch (SQLException e) {
                log.error(e.getErrorCode(), e);
            } finally {
                sqlStatement = null; // Avoid prepareStatement error 213 on next run
            }
        }
        if (sqlCstmt != null) {
            try {
                sqlCstmt.cancel();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            } finally {
                sqlCstmt = null;
            }
        }

    }

    private static List<ObservableList<String>> fetchRows(ResultSet resultSet,
                                                          int perpage,
                                                          Task sqlTask,
                                                          int columnCount,
                                                          java.util.function.IntConsumer progressCallback) throws SQLException {
        List<ObservableList<String>> rows = new ArrayList<>();
        int fetched = 0;
        while (fetched < perpage && resultSet.next()) {
            if (sqlTask != null && sqlTask.isCancelled()) {
                break;
            }
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(null);
            for (int z = 1; z <= columnCount; z++) {
                row.add(resultSet.getString(z));
            }
            rows.add(row);
            fetched++;
            if (progressCallback != null && fetched % 200 == 0) {
                progressCallback.accept(fetched);
            }
        }
        return rows;
    }

    private String formatFetchedRows(int fetched) {
        String template = I18n.t("sql.result.fetched_rows");
        return template.replace("{0}", String.valueOf(fetched));
    }

    private int getPerPageLimit() {
        String text = resultSetPerTimeTextField.getText();
        if (text == null || text.isBlank() || "0".equals(text) || text.startsWith("-")) {
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private void setFetchStatus(String message) {
        Platform.runLater(() -> sqlExecuteProcessLabel.setText(message));
    }

    private void applyFetchedRows(String sqlExe) {
        Platform.runLater(() -> {
            lastSqlTextField.setText(sqlExe);
            lastSqlTextField.getTooltip().setText(sqlExe);
            resultSetTableView.getColumns().addAll(colList);
            resultSetTableView.getItems().addAll(sqlResultSetList);
            resultSetTableView.refresh();
            resultSetFetchedRowsLabel.setText(sqlFetchedRows.toString());
            sqlUsedTimeLabel.setText(String.format("%.3f", sqlFetchedTime / 1000.0));
            sqlResultSetList.clear();
        });
    }

    private void buildColumns(ResultSetMetaData metaData,
                              boolean allowEdit,
                              SimpleStringProperty sqlTransactionText,
                              ChoiceBox commitmode) throws SQLException {
        colList.clear();
        int columnCount = metaData.getColumnCount();
        double avgColWidth = (resultSetTableView.getWidth() - 30) / columnCount;
        for (int j = 1; j <= columnCount; j++) {
            final int columnIndex = j;
            String colTypeName = metaData.getColumnTypeName(j);
            final String colTypeNameFinal = colTypeName;
            Integer length = metaData.getColumnDisplaySize(j);
            final boolean isLob = colTypeName != null && colTypeName.toLowerCase().matches("(blob|clob|text|bytea|image|longvarbinary|longvarchar)");

            TableColumn<ObservableList<String>, Object> column = new TableColumn<>();
            if (colTypeName.equals("int") || colTypeName.equals("serial") || colTypeName.equals("smallint")) {
                column.setCellValueFactory(data -> Bindings.createObjectBinding(() ->
                        data.getValue().get(columnIndex) == null ? null : Integer.parseInt(data.getValue().get(columnIndex))));
            } else if (colTypeName.equals("float") || colTypeName.equals("decimal")) {
                column.setCellValueFactory(data -> Bindings.createObjectBinding(() ->
                        data.getValue().get(columnIndex) == null ? null : Float.parseFloat(data.getValue().get(columnIndex))));
            } else {
                column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex)));
                String displayType = colTypeName;
                if (!displayType.startsWith("date")) {
                    displayType = displayType + "(" + length + ")";
                }
                final String headerType = displayType;
                column.setComparator((str1, str2) -> {
                    if (str1 == null && str2 == null) return 0;
                    if (str1 == null) return -1;
                    if (str2 == null) return 1;
                    return str1.toString().compareToIgnoreCase(str2.toString());
                });

                // set header tooltip later using headerType
                colTypeName = headerType;
            }

            final String headerName = metaData.getColumnLabel(j);
            final String headerTypeFinal = colTypeName;

            column.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>() {
                {
                    setOnMouseClicked(ev -> {
                        if (isLob && ev.getClickCount() == 1 && !isEmpty() && getItem() != null) {
                            Platform.runLater(() -> {
                                Stage lobDataPopupStage = new Stage();
                                CustomInfoCodeArea customInfoCodeArea=new CustomInfoCodeArea();
                                CustomInfoStackPane customInfoStackPane=new CustomInfoStackPane(customInfoCodeArea);
                                customInfoCodeArea.setWrapText(false);
                                customInfoCodeArea.setEditable(resultSetEditableEnabledLabel.isVisible());
                                customInfoStackPane.codeAreaSnapshotButton.setVisible(false);
                                Button saveBtn = new Button(I18n.t("common.save", "保存"));
                                VBox root = new VBox();
                                if (resultSetEditableEnabledLabel.isVisible()) {
                                    saveBtn.setOnAction(e -> {
                                        try {
                                            ObservableList<String> row = getTableView().getItems().get(getIndex());
                                            updateCellValue(columnIndex, customInfoCodeArea.getText(), row, row.get(columnIndex), sqlTransactionText, commitmode);
                                            // 保存成功后同步更新当前行的显示值
                                            row.set(columnIndex, customInfoCodeArea.getText());
                                            resultSetTableView.refresh();
                                            lobDataPopupStage.close();
                                        } catch (Exception ex) {
                                            log.error(ex.getMessage(), ex);
                                            AlterUtil.CustomAlert(I18n.t("common.error"), ex.getMessage());
                                        }
                                    });
                                    saveBtn.setMaxWidth(Double.MAX_VALUE);
                                }
                                VBox.setVgrow(customInfoStackPane, Priority.ALWAYS);

                                root.getChildren().add(customInfoStackPane);
                                if(resultSetEditableEnabledLabel.isVisible()){
                                    root.getChildren().add(saveBtn);
                                }
                                root.setPrefHeight(Double.MAX_VALUE);
                                Scene lobDataPopupStageScene = new Scene(root, 800, 400);
                                Image lobDataPopupStageIcon = new Image(IconPaths.MAIN_LOGO);
                                lobDataPopupStage.setScene(lobDataPopupStageScene);
                                lobDataPopupStage.getIcons().add(lobDataPopupStageIcon);
                                lobDataPopupStageScene.getStylesheets().add(Main.class.getResource("/com/dbboys/css/app.css").toExternalForm());
                                lobDataPopupStage.initModality(Modality.APPLICATION_MODAL);
                                lobDataPopupStage.show();
                                customInfoCodeArea.replaceText(getItem().toString());
                                lobDataPopupStage.setOnCloseRequest(event -> {
                                    lobDataPopupStage.close();
                                });
                                lobDataPopupStage.showAndWait();
                            
                            });
                            //showLargeObject(headerName, getItem().toString());
                            ev.consume();
                        }
                    });
                }

                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) return;
                    if (item == null) {
                        setText(getNullLabel());
                        setTooltip(null);
                    } else if (isLob) {
                        setText(colTypeNameFinal.toUpperCase());
                        Tooltip tp = new Tooltip(I18n.t("resultset.tooltip.double_click_lob"));
                        tp.setShowDelay(Duration.millis(80));
                        setTooltip(tp);
                    } else {
                        setText(item.toString().replace("\n", "\u21B5"));
                        setTooltip(null);
                    }
                }
            });

            if (allowEdit && sqlTransactionText != null && commitmode != null) {
                bindEditableColumn(column, columnIndex, sqlTransactionText, commitmode);
            }
            colList.add(column);

            StackPane colheader = new StackPane();
            String colName = metaData.getColumnLabel(j);
            Label colLabel = new Label(colName);
            Label colType = new Label(headerTypeFinal);
            colheader.getChildren().addAll(colLabel);
            colType.setStyle("-fx-font-size: 5");
            StackPane.setAlignment(colType, Pos.BOTTOM_LEFT);
            Tooltip tp = new Tooltip(headerTypeFinal);
            tp.setShowDelay(Duration.millis(100));
            colLabel.setTooltip(tp);
            column.setPrefWidth(Math.max(colLabel.getText().length() * 15, avgColWidth));
            colLabel.setMaxWidth(Double.MAX_VALUE);
            column.setReorderable(false);
            column.setGraphic(colheader);
            column.getGraphic().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                resultSetTableView.getSelectionModel().clearSelection();
                for (int rowIndex = 0; rowIndex < resultSetTableView.getItems().size(); rowIndex++) {
                    resultSetTableView.getSelectionModel().select(rowIndex, column);
                }
                event.consume();
            });
        }
    }

    private void bindEditableColumn(TableColumn<ObservableList<String>, Object> column,
                                    int columnIndex,
                                    SimpleStringProperty sqlTransactionText,
                                    ChoiceBox commitmode) {
        column.setOnEditCommit(event -> {
            Object oldvalue = event.getOldValue();
            Object colvalue = event.getNewValue().toString().replaceAll("\u21B5", "\n");
            event.getRowValue().set(columnIndex, colvalue.toString());

            try {
                updateCellValue(columnIndex, colvalue.toString(), event.getRowValue(), oldvalue, sqlTransactionText, commitmode);
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    event.getRowValue().set(columnIndex, oldvalue == null ? null : oldvalue.toString());
                    event.getTableView().refresh();
                    if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                        hiddenDisconnectedButton.fire();
                    } else {
                        AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                    }
                });
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 执行单元格更新（结果集可编辑及 LOB 弹窗公用）。
     */
    private void updateCellValue(int columnIndex,
                                 String newValue,
                                 ObservableList<String> row,
                                 Object oldValue,
                                 SimpleStringProperty sqlTransactionText,
                                 ChoiceBox commitmode) throws SQLException {
        String updateCol = String.valueOf(resultTableCols.get(columnIndex - 1));
        String updateSql = "update " + resultFromTable + " set " + updateCol + "=? where 1=1 ";
        if (resultTablePriNum != null && !resultTablePriNum.isEmpty()) {
            for (Integer colnum : resultTablePriNum) {
                updateSql += " and " + resultTableCols.get(colnum) + "=?";
            }
        }

        sqlStatement = sqlConnect.getConn().prepareStatement(updateSql);
        boolean isNull = "[NULL]".equals(newValue);
        sqlStatement.setObject(1, isNull ? null : newValue);

        int prepareNum = 2;
        StringBuilder sqlParams = new StringBuilder();
        if (resultTablePriNum != null) {
            for (Integer colnum : resultTablePriNum) {
                Object pkVal = (columnIndex - 1 == colnum) ? oldValue : row.get(colnum + 1);
                sqlStatement.setObject(prepareNum++, pkVal);
                if (sqlParams.length() > 0) {
                    sqlParams.append(",");
                }
                sqlParams.append(pkVal == null ? "null" : pkVal);
            }
        }

        //如果是修改为[NULL]，则刷新结果集表格以显示灰色表示null
        if(isNull){
            resultSetTableView.refresh();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long sql_begin_time = System.currentTimeMillis();
        int sqlAffect = sqlStatement.executeUpdate();
        long sql_finish_time = System.currentTimeMillis();

        //if (commitmode != null && sqlTransactionText != null) {
            UpdateResult updateResult = new UpdateResult();
            updateResult.setConnectId(sqlConnect.getId());
            updateResult.setStartTime(sdf.format(sql_begin_time));
            updateResult.setEndTime(sdf.format(sql_finish_time));
            updateResult.setElapsedTime(String.format("%.3f", (sql_finish_time - sql_begin_time) / 1000.0) + " sec");
            updateResult.setAffectedRows(sqlAffect);
            updateResult.setDatabase(sqlConnect.getDatabase());
            updateResult.setUpdateSql(updateSql);
            if (commitmode.getSelectionModel().getSelectedIndex() == 1) {
                updateResult.setMark("手动提交，查询结果集编辑，参数[" +newValue+","+ sqlParams + "]");
                sqlTransactionText.set(sqlTransactionText.get() + updateSql + "\n");
                NotificationUtil.showNotification(Main.mainController.noticePane, "当前连接为手动提交，修改暂未提交，请点击提交或回滚！");
            } else {
                updateResult.setMark("自动提交，查询结果集编辑，参数[" +newValue+","+ sqlParams + "]");
            }
            SqliteDBaccessUtil.saveSqlHistory(updateResult);
    
        sqlStatement.close();
    }

    private boolean prepareParams(ParameterMetaData meta, Statement stmt) {
        if (meta == null) {
            return true;
        }
        int paramCount = 0;
        try {
            paramCount = meta.getParameterCount();
        } catch (SQLException e) {
            return true;
        }
        if (paramCount <= 0) {
            return true;
        }
        final int paramCountFinal = paramCount;
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            sqlParamList.clear();
            sqlParamList = PopupWindowUtil.openParamWindow(paramCountFinal);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    private void bindParams(PreparedStatement stmt) throws SQLException {
        if (sqlParamList == null || sqlParamList.isEmpty()) {
            return;
        }
        for (int z = 1; z <= sqlParamList.size(); z++) {
            stmt.setObject(z, (sqlParamList.get(z - 1)));
        }
    }

    private void bindParams(CallableStatement stmt) throws SQLException {
        if (sqlParamList == null || sqlParamList.isEmpty()) {
            return;
        }
        for (int z = 1; z <= sqlParamList.size(); z++) {
            stmt.setObject(z, (sqlParamList.get(z - 1)));
        }
    }

    private Task<Void> createFetchTask(boolean fetchAll) {
        sqlExecuteProcessStackPane.setVisible(true);
        sqlFetchedRows = 0;
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    int columnCount = sqlResultSet.getMetaData().getColumnCount();
                    sqlResultSetList.clear();
                    setFetchStatus(" " + I18n.t("sql.result.fetching"));
                    sqlFetchStartTime = System.currentTimeMillis();
                    int perPage = fetchAll ? Integer.MAX_VALUE : getPerPageLimit();
                    List<ObservableList<String>> fetchedRows = fetchRows(
                            sqlResultSet,
                            perPage,
                            this,
                            columnCount,
                            fetched -> setFetchStatus(formatFetchedRows(fetched))
                    );
                    sqlResultSetList.addAll(fetchedRows);
                    sqlFetchedRows += fetchedRows.size();
                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        hiddenDisconnectedButton.fire();
                    } else {
                        Platform.runLater(() -> AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage()));
                    }
                }
                return null;
            }
        };
        if (fetchAll) {
            sqlTask.setOnSucceeded(event1 -> {
                eventEnd.run();
                Platform.runLater(() -> resultSetTotalRowsLabel.setText(resultSetFetchedRowsLabel.getText()));
            });
        } else {
            sqlTask.setOnSucceeded(event1 -> eventEnd.run());
        }
        sqlTask.setOnCancelled(event1 -> eventEnd.run());
        sqlTask.setOnFailed(event1 -> eventEnd.run());
        return sqlTask;
    }

    private PrimaryKeyInfo fetchPrimaryKeyInfo(String finalSqlGetPrimary, String sqlExe) throws SQLException {
        String pri = null;
        sqlStatement = sqlConnect.getConn().prepareStatement(finalSqlGetPrimary);
        sqlStatement.setObject(1, resultFromTable);
        priSqlResult = sqlStatement.executeQuery();
        if (priSqlResult.next()) {
            pri = priSqlResult.getString(1);
        }
        priSqlResult.close();

        List cols = new ArrayList();
        sqlStatement = sqlConnect.getConn().prepareStatement("select colname from syscolumns c,systables t where  t.tabid=c.tabid and tabname=?");
        sqlStatement.setObject(1, resultFromTable);
        priSqlResult = sqlStatement.executeQuery();
        while (priSqlResult.next()) {
            cols.add(priSqlResult.getObject(1));
        }
        priSqlResult.close();
        resultTableCols = SqlParserUtil.getSelectedCols(sqlExe, cols);
        return new PrimaryKeyInfo(pri, resultTableCols);
    }

    private void applyPrimaryKeyInfo(PrimaryKeyInfo info) {
        if (info == null) {
            resultSetTableView.setEditable(false);
            return;
        }
        String pri = info.primaryKeys;
        List selectedCols = info.selectedColumns;
        resultTableCols = selectedCols;
        if (pri != null && selectedCols.containsAll(List.of(pri.split(",")))) {
            for (String key : pri.split(",")) {
                int columnIndex = selectedCols.indexOf(key);
                resultTablePriNum.add(columnIndex);
                markPrimaryKeyColumn(columnIndex, "PRI", "-fx-font-size: 8;-fx-text-fill: #9f453c");
            }
            return;
        }
        if (selectedCols.contains("rowid")) {
            int columnIndex = selectedCols.indexOf("rowid");
            resultTablePriNum.add(columnIndex);
            markPrimaryKeyColumn(columnIndex, "ROWID", "-fx-font-size: 5;-fx-text-fill: #9f453c");
            return;
        }
        resultSetTableView.setEditable(false);
    }

    private void markPrimaryKeyColumn(int columnIndex, String labelText, String labelStyle) {
        resultSetEditableEnabledLabel.setVisible(true);
        TableColumn<String, Object> column = (TableColumn<String, Object>) resultSetTableView.getColumns().get(columnIndex + 1);
        StackPane sp = new StackPane();
        Label priLabel = new Label(labelText);
        priLabel.setStyle(labelStyle);
        sp.getChildren().add(priLabel);
        StackPane.setAlignment(priLabel, Pos.BOTTOM_LEFT);
        column.getGraphic().setStyle("-fx-text-fill:#9f453c ");
        sp.getChildren().add(column.getGraphic());
        column.setGraphic(sp);
        if (!sqlConnect.getReadonly()) {
            resultSetTableView.setEditable(true);
        }
    }

    private static class PrimaryKeyInfo {
        final String primaryKeys;
        final List selectedColumns;

        PrimaryKeyInfo(String primaryKeys, List selectedColumns) {
            this.primaryKeys = primaryKeys;
            this.selectedColumns = selectedColumns;
        }
    }

    /**
     * Show large object content in a dialog when user double-clicks LOB cell.
     */
    private void showLargeObject(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(I18n.t("resultset.lob.title", "大对象查看"));
            alert.setHeaderText(title);
            TextArea area = new TextArea(content);
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefSize(800, 500);
            alert.getDialogPane().setContent(area);
            alert.getDialogPane().setPrefSize(820, 550);
            alert.showAndWait();
        });
    }

    private String getNullLabel() {
        return I18n.t("customtablecell.null", "[NULL]");
    }
}

