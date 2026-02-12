package com.dbboys.ctrl;

import com.dbboys.app.Main;
import com.dbboys.customnode.CustomLabelTextField;
import com.dbboys.customnode.CustomResultsetTableView;
import com.dbboys.customnode.CustomTableCell;
import com.dbboys.customnode.CustomUserTextField;
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
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
    public Runnable eventEnd = () -> {
        Platform.runLater(() -> {
            sqlExecuteProcessStackPane.setVisible(false);
            sqlFetchEndTime = System.currentTimeMillis();
            sqlFetchedTime += sqlFetchEndTime - sqlFetchStartTime;
            resultSetTableView.getItems().addAll(sqlResultSetList);
            if (sqlFetchedRows > 0) {
                resultSetFetchedRowsLabel.setText(String.valueOf((Integer.parseInt(resultSetFetchedRowsLabel.getText()) + sqlFetchedRows)));
                sqlUsedTimeLabel.setText(String.valueOf(sqlFetchedTime / 1000.0));
            }
            sqlResultSetList.clear();
        });
    };
    //最后一次执行的sql
    @FXML
    private Button lastSqlCopyButton;
    private String sqlResultCount;


    public ResultSetTabController(Connect sqlConnect, StackPane sqlExecuteProcessStackPane) {
        this.sqlExecuteProcessStackPane = sqlExecuteProcessStackPane;
        this.sqlConnect = sqlConnect;
    }

    public void initialize() {
        resultSetEditableDisabledLabel.visibleProperty().bind(resultSetEditableEnabledLabel.visibleProperty().not());
        sqlExecuteProcessLabel = (Label) ((HBox) sqlExecuteProcessStackPane.getChildren().get(0)).getChildren().get(1);
        resultSetNextPageButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        resultSetAllRowsButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        lastSqlRefreshButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        resultSetExportButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        resultSetCountButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());

        //执行计划按钮事件


        //连接变更响应事件

        //最后sql记录
        lastSqlTextField.getTooltip().setShowDelay(Duration.millis(100));

        //复制按钮
        lastSqlCopyButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(lastSqlTextField.getTooltip().getText());
            clipboard.setContent(content);
            NotificationUtil.showNotification(Main.mainController.notice_pane, "已复制！");
        });

        //刷新按钮
        lastSqlRefreshButton.setOnAction(event -> {
        });

        //结果集表格
        Label tableview_empty_label = new Label("");
        resultSetTableView.setPlaceholder(tableview_empty_label);//设置空白表格提示
        resultSetTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultSetTableView.getSelectionModel().setCellSelectionEnabled(true);
        resultSetTableView.prefWidthProperty().bind(resultSetVBox.widthProperty());
        resultSetTableView.prefHeightProperty().bind(resultSetVBox.heightProperty());
        resultSetTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //resultSetTableView.setStyle("-fx-background-color: red;");
        resultSetTableView.setSortPolicy(tv -> {
            TableView.DEFAULT_SORT_POLICY.call((TableView) tv);
            // 使用 Platform.runLater 在排序完成后恢复选中的单元格
            Platform.runLater(() -> {
                resultSetTableView.getSelectionModel().clearSelection();
            });
            return true;
        });

        //结果集处理按钮
        resultSetPerTimeTextField.setText(ConfigManagerUtil.getProperty("RESULT_FETCH_PER_TIME"));
        resultSetPerTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                resultSetPerTimeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });


        lastSqlTextField.getTooltip().setShowDelay(Duration.millis(100));

        //复制按钮
        lastSqlCopyButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(lastSqlTextField.getTooltip().getText());
            clipboard.setContent(content);
            NotificationUtil.showNotification(Main.mainController.notice_pane, "已复制！");
        });
        //获取下一个结果集
        resultSetNextPageButton.setOnAction(event -> {
            sqlTask = createGetNextPageResultSetTask();
            new Thread(sqlTask).start();
        });


        //获取全部结果集
        resultSetAllRowsButton.setOnAction(event -> {
            sqlTask = createGetAllResultSetTask();
            new Thread(sqlTask).start();

        });

        //获取结果集总数

        resultSetCountButton.setOnAction(event -> {
            sqlTask = createGetResultSetCountTask();
            new Thread(sqlTask).start();
        });

        resultSetExportButton.setOnAction(event -> {
            if (resultSetTableView.getItems().size() == 0) {
                AlterUtil.CustomAlert("错误", "当前结果集为空，无数据需要导出！");
            } else if (AlterUtil.CustomAlertConfirm("结果集导出", "导出程序只导出已加载到结果集表格的数据，确定要执行导出吗?")) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("结果集导出");
                String defaultName = "结果集导出_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xlsx";
                fileChooser.setInitialFileName(defaultName);
                File file = fileChooser.showSaveDialog(Main.scene.getWindow());

                if (file != null) {
                    if (file.exists()) {
                        file.delete();
                    }
                    DownloadManagerUtil.addDownload(resultSetTableView, file, true, sqlMetaData);
                }
            } else {

            }
        });


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
        sqlExecuteProcessStackPane.setVisible(true);
        sqlFetchedRows = 0;
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    //updateMessage("Fetching... ");
                    //游标滚动到上次取数的位置
                    //sql_result.absolute(sqlFetchedRows);
                    ResultSetMetaData metaData = sqlResultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    // 添加数据到 TableView
                    Integer j = 1;
                    sqlResultSetList.clear();
                    Platform.runLater(() -> {
                        sqlExecuteProcessLabel.setText(" 正在获取结果集...");
                    });
                    sqlFetchStartTime = System.currentTimeMillis();
                    while (j <= Integer.parseInt(resultSetPerTimeTextField.getText()) && sqlResultSet.next()) {
                        if (isCancelled()) {
                            break;
                        }
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(null);
                        for (int i = 1; i <= columnCount; i++) {
                            row.add(sqlResultSet.getString(i));
                        }
                        sqlResultSetList.add(row);
                        j++;
                        sqlFetchedRows++;
                        if (sqlFetchedRows > 0 && sqlFetchedRows % 200 == 0) {
                            final int sqlFetchedRowsFinal = sqlFetchedRows;
                            Platform.runLater(() -> {
                                sqlExecuteProcessLabel.setText(" 已获取结果集[ " + sqlFetchedRowsFinal + " ]行，");
                            });
                        }
                    }

                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        hiddenDisconnectedButton.fire();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                    }
                }
                return null;
            }

        };
        sqlTask.setOnSucceeded(event1 -> eventEnd.run());
        sqlTask.setOnCancelled(event1 -> eventEnd.run());
        sqlTask.setOnFailed(event1 -> eventEnd.run());
        return sqlTask;
    }


    public Task<Void> createGetAllResultSetTask() {
        sqlFetchedRows = 0;
        sqlExecuteProcessStackPane.setVisible(true);
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    //updateMessage("Fetching... ");
                    //游标滚动到上次取数之后位置
                    //sql_result.absolute(sqlFetchedRows);
                    ResultSetMetaData metaData = sqlResultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    // 添加数据到 TableView
                    sqlResultSetList.clear();
                    Platform.runLater(() -> {
                        sqlExecuteProcessLabel.setText(" 正在获取结果集...");
                    });
                    sqlFetchStartTime = System.currentTimeMillis();
                    while (sqlResultSet.next()) {
                        if (isCancelled()) {
                            break;
                        }
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(null);
                        for (int i = 1; i <= columnCount; i++) {
                            row.add(sqlResultSet.getString(i));
                        }
                        sqlResultSetList.add(row);
                        sqlFetchedRows++;
                        if (sqlFetchedRows > 0 && sqlFetchedRows % 200 == 0) {
                            final int sqlFetchedRowsFinal = sqlFetchedRows;
                            Platform.runLater(() -> {
                                sqlExecuteProcessLabel.setText(" 已获取结果集[ " + sqlFetchedRowsFinal + " ]行，");
                            });
                        }
                    }
                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        hiddenDisconnectedButton.fire();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                    }
                }
                return null;
            }

        };
        sqlTask.setOnSucceeded(event1 -> {
            eventEnd.run();
            Platform.runLater(() -> {
                resultSetTotalRowsLabel.setText(resultSetFetchedRowsLabel.getText());
            });
        });
        sqlTask.setOnCancelled(event1 -> eventEnd.run());
        sqlTask.setOnFailed(event1 -> eventEnd.run());
        return sqlTask;
    }


    public Task<Void> createGetResultSetCountTask() {
        //sql_is_count=true;
        sqlExecuteProcessStackPane.setVisible(true);
        Platform.runLater(() -> {
            sqlExecuteProcessLabel.setText(" 正在获取结果集总数...");
        });
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
        sqlTask.setOnCancelled(event1 -> {
            sqlExecuteProcessStackPane.setVisible(false);
        });
        sqlTask.setOnFailed(event1 -> {
            sqlExecuteProcessStackPane.setVisible(false);
        });

        return sqlTask;
    }


    public long executeSelect(String sqlExe, Task sqlTask, Boolean showFething, SimpleStringProperty sqlTransactionText, ChoiceBox commitmode) throws SQLException {
        init();
        sqlFetchedRows = 0;
        final boolean showFetching = Boolean.TRUE.equals(showFething);
        sqlStatement = sqlConnect.getConn().prepareStatement(sqlExe);
        parameterMetaData = sqlStatement.getParameterMetaData();

        int paramCount = parameterMetaData.getParameterCount();
        if (paramCount > 0) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                sqlParamList.clear();
                sqlParamList = PopupWindowUtil.openParamWindow(paramCount);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long sqlStartTime = System.currentTimeMillis();
        if (sqlParamList != null && !sqlParamList.isEmpty()) {
            for (int z = 1; z <= sqlParamList.size(); z++) {
                sqlStatement.setObject(z, (sqlParamList.get(z - 1)));
            }
        }

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
        //resultSetTableView.refresh();
        // 动态生成列

        colList.clear();
        double avgColWidth = (resultSetTableView.getWidth() - 30) / columnCount;


        for (int j = 1; j <= columnCount; j++) {

            final int columnIndex = j;
            String colTypeName = sqlMetaData.getColumnTypeName(j);
            Integer length = sqlMetaData.getColumnDisplaySize(j);


            TableColumn<ObservableList<String>, Object> column = new TableColumn<ObservableList<String>, Object>();
            if (colTypeName.equals("int") || colTypeName.equals("serial") || colTypeName.equals("smallint")) {
                column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex) == null ? null : Integer.parseInt(data.getValue().get(columnIndex))));
            } else if (colTypeName.equals("float") || colTypeName.equals("decimal")) {
                column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex) == null ? null : Float.parseFloat(data.getValue().get(columnIndex))));
            } else {
                column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex)));
                if (!colTypeName.startsWith("date")) {
                    colTypeName = colTypeName + "(" + length + ")";
                }
                //字符串排序不忽略前导空格并忽略大小写
                column.setComparator((str1, str2) -> str1.toString().toLowerCase().compareTo(str2.toString().toLowerCase()));
            }

            column.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());


            //如果showFething是单独查询
            if (showFetching) {


                column.setOnEditCommit(event -> {
                    Object oldvalue = event.getOldValue();
                    //Object colvalue = event.getNewValue();
                    //替换换行
                    Object colvalue = event.getNewValue().toString().replaceAll("\u21B5", "\n");

                    //替换换行后显示
                    event.getRowValue().set(columnIndex, colvalue.toString());

                    String updateCol = String.valueOf(resultTableCols.get(columnIndex - 1));
                    String updateSql = "update " + resultFromTable + " set " + updateCol + "=? where 1=1 ";
                    String sqlParams = "";

                    try {

                        if (resultTablePriNum != null && !resultTablePriNum.isEmpty()) {
                            for (Integer colnum : resultTablePriNum) {
                                updateSql += " and " + resultTableCols.get(colnum) + "=?";
                            }
                        }
                        //此处会复现断链卡死，增加tab缩进后正常

                        sqlStatement = sqlConnect.getConn().prepareStatement(updateSql);

                        sqlStatement.setObject(1, colvalue.equals("[NULL]") ? null : colvalue);


                        if (colvalue.equals("[NULL]")) {
                            sqlStatement.setObject(1, null);
                            sqlParams += "(null";
                            Platform.runLater(() -> {
                                event.getRowValue().set(columnIndex, null);
                                event.getTableView().refresh();
                            });
                        } else {
                            sqlParams += "[" + colvalue;
                            sqlStatement.setObject(1, colvalue);
                        }


                        Integer rownumber = event.getTablePosition().getRow();
                        List selectedData = (List) resultSetTableView.getItems().get(rownumber);
                        int prepareNum = 2;
                        for (Integer colnum : resultTablePriNum) {
                            TablePosition<?, ?> position = (TablePosition<?, ?>) resultSetTableView.getSelectionModel().getSelectedCells().get(0);
                            if (position.getColumn() == colnum + 1) {
                                //如果编辑的是主键列，绑定变量为修改前的值
                                sqlStatement.setObject(prepareNum, oldvalue);
                                sqlParams += "," + oldvalue;
                            } else {
                                sqlStatement.setObject(prepareNum, selectedData.get(colnum + 1));
                                sqlParams += "," + selectedData.get(colnum + 1);
                            }
                            prepareNum++;
                        }

                        UpdateResult updateResult = new UpdateResult();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        int sqlAffect = 0;
                        long sql_begin_time = System.currentTimeMillis();
                        sqlAffect = sqlStatement.executeUpdate();
                        long sql_finish_time = System.currentTimeMillis();
                        updateResult.setConnectId(sqlConnect.getId());
                        updateResult.setStartTime(sdf.format(sql_begin_time));
                        updateResult.setEndTime(sdf.format(sql_finish_time));
                        updateResult.setElapsedTime(String.format("%.3f", (sql_finish_time - sql_begin_time) / 1000.0) + " sec");
                        updateResult.setAffectedRows(sqlAffect);
                        updateResult.setDatabase(sqlConnect.getDatabase());
                        sqlParams += "]";
                        updateResult.setUpdateSql(updateSql);
                        if (commitmode.getValue().toString().equals("手动提交")) {
                            updateResult.setMark("手动提交，查询结果集编辑，参数" + sqlParams);
                            sqlTransactionText.set(sqlTransactionText.get() + updateSql + "\n");
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "当前连接为手动提交，修改暂未提交，请点击提交或回滚！");
                        } else {
                            updateResult.setMark("自动提交，查询结果集编辑，参数" + sqlParams);
                        }
                        SqliteDBaccessUtil.saveSqlHistory(updateResult);


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
            colList.add(column);
            StackPane colheader = new StackPane();
            //此处会复现断链卡死

            String colName = sqlMetaData.getColumnLabel(j);
            Label colLabel = new Label(colName);
            Label colType = new Label(colTypeName);
            colheader.getChildren().addAll(colLabel);
            colType.setStyle("-fx-font-size: 5");
            StackPane.setAlignment(colType, Pos.BOTTOM_LEFT);
            Tooltip tp = new Tooltip(colTypeName);
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
        // 添加数据到 TableView
        sqlStartTime = System.currentTimeMillis();
        Integer perpage = 0;
        if (resultSetPerTimeTextField.getText().equals("") || resultSetPerTimeTextField.getText().equals("0") || resultSetPerTimeTextField.getText().startsWith("-")) {
            perpage = 999999999;
        } else {
            perpage = Integer.parseInt(resultSetPerTimeTextField.getText());
        }
        if (showFetching) {
            Platform.runLater(() -> {
                sqlExecuteProcessLabel.setText(" 正在获取结果集...");
            });
        }

        List<ObservableList<String>> fetchedRows = fetchRows(
                sqlResultSet,
                perpage,
                sqlTask, columnCount,
                showFetching ? fetched -> Platform.runLater(() ->
                        sqlExecuteProcessLabel.setText(" 已获取结果集[ " + fetched + " ]行，")) : null
        );
        sqlResultSetList.addAll(fetchedRows);
        sqlFetchedRows += fetchedRows.size();


        Platform.runLater(() -> {
            lastSqlTextField.setText(sqlExe);
            lastSqlTextField.getTooltip().setText(sqlExe);
            resultSetTableView.getColumns().addAll(colList);
            resultSetTableView.getItems().addAll(sqlResultSetList);
            resultSetTableView.refresh();//此处不刷新可能导致部分行显示空白
            resultSetFetchedRowsLabel.setText(sqlFetchedRows.toString());
            sqlUsedTimeLabel.setText(String.format("%.3f", sqlFetchedTime / 1000.0));
            sqlResultSetList.clear();
        });

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
        int paramCount = parameterMetaData.getParameterCount();
        long sqlStartTime = System.currentTimeMillis();
        if (paramCount > 0) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                sqlParamList.clear();
                sqlParamList = PopupWindowUtil.openParamWindow(paramCount);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sqlStartTime = System.currentTimeMillis();
            if (sqlParamList.size() > 0) {
                for (int z = 1; z <= sqlParamList.size(); z++) {
                    sqlCstmt.setObject(z, (sqlParamList.get(z - 1)));
                }
            }
        }

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
            colList.clear();
            double avgColWidth = (resultSetTableView.getWidth() - 30) / columnCount;
            for (int j = 1; j <= columnCount; j++) {
                final int columnIndex = j;
                String colTypeName = sqlMetaData.getColumnTypeName(j);
                Integer length = sqlMetaData.getColumnDisplaySize(j);
                TableColumn<ObservableList<String>, Object> column = new TableColumn<ObservableList<String>, Object>();
                if (colTypeName.equals("int") || colTypeName.equals("serial") || colTypeName.equals("smallint")) {
                    column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex) == null ? null : Integer.parseInt(data.getValue().get(columnIndex))));
                } else if (colTypeName.equals("float") || colTypeName.equals("decimal")) {
                    column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex) == null ? null : Float.parseFloat(data.getValue().get(columnIndex))));
                } else {
                    column.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(columnIndex)));
                    if (!colTypeName.startsWith("date")) {
                        colTypeName = colTypeName + "(" + length + ")";
                    }
                    column.setComparator((str1, str2) -> str1.toString().toLowerCase().compareTo(str2.toString().toLowerCase()));
                }

                column.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
                colList.add(column);
                Label colLabel = new Label(sqlMetaData.getColumnName(j));
                Tooltip tp = new Tooltip(colTypeName);
                tp.setShowDelay(Duration.millis(100));
                colLabel.setTooltip(tp);
                column.setPrefWidth(Math.max(colLabel.getText().length() * 15, avgColWidth));
                colLabel.setMaxWidth(Double.MAX_VALUE);
                column.setReorderable(false);
                column.setGraphic(colLabel);

                column.getGraphic().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    resultSetTableView.getSelectionModel().clearSelection();
                    for (int rowIndex = 0; rowIndex < resultSetTableView.getItems().size(); rowIndex++) {
                        resultSetTableView.getSelectionModel().select(rowIndex, column);
                    }
                    event.consume();
                });
            }

            sqlStartTime = System.currentTimeMillis();
            Integer perpage = 0;
            if (resultSetPerTimeTextField.getText().equals("") || resultSetPerTimeTextField.getText().equals("0") || resultSetPerTimeTextField.getText().startsWith("-")) {
                perpage = 999999999;
            } else {
                perpage = Integer.parseInt(resultSetPerTimeTextField.getText());
            }
            if (showFetching) {
                Platform.runLater(() -> {
                    sqlExecuteProcessLabel.setText(" \u6b63\u5728\u83b7\u53d6\u7ed3\u679c\u96c6...");
                });
            }

            List<ObservableList<String>> fetchedRows = fetchRows(
                    sqlResultSet,
                    perpage,
                    sqlTask,
                    columnCount,
                    showFetching ? fetched -> Platform.runLater(() ->
                            sqlExecuteProcessLabel.setText(" 已获取结果集[ " + fetched + " ]行，")) : null
            );
            sqlResultSetList.addAll(fetchedRows);
            sqlFetchedRows += fetchedRows.size();
            Platform.runLater(() -> {
                resultSetButtonHBox.getChildren().remove(resultSetCountButton);
                lastSqlTextField.setText(sqlExe);
                lastSqlTextField.getTooltip().setText(sqlExe);
                resultSetTableView.getColumns().addAll(colList);
                resultSetTableView.getItems().addAll(sqlResultSetList);
                resultSetTableView.refresh();
                resultSetFetchedRowsLabel.setText(sqlFetchedRows.toString());
                sqlUsedTimeLabel.setText(String.format("%.3f", sqlFetchedTime / 1000.0));
                sqlResultSetList.clear();
            });

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
        String finalSql_get_primary = sql_get_primary;

        //获取主键任务

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
                    //获取主键
                    String pri = null;
                    //sqlStatement=conn.prepareStatement(finalSql_get_primary);
                    sqlStatement = sqlConnect.getConn().prepareStatement(finalSql_get_primary);
                    sqlStatement.setObject(1, resultFromTable);
                    priSqlResult = sqlStatement.executeQuery(); //一个statment只能打开一个结果集游标
                    if (priSqlResult.next()) {
                        pri = priSqlResult.getString(1);
                    } else {
                    }
                    //过程中有多个结果集，必须先关闭，避免执行database切换时报错-267
                    priSqlResult.close();

                    //priSqlResult.close();
                    //sqlStatement.close();

                    //获取*所有字段
                    List cols = new ArrayList();
                    //sqlStatement=conn.prepareStatement("select colname from syscolumns c,systables t where  t.tabid=c.tabid and tabname=?");

                    sqlStatement = sqlConnect.getConn().prepareStatement("select colname from syscolumns c,systables t where  t.tabid=c.tabid and tabname=?");
                    sqlStatement.setObject(1, resultFromTable);
                    priSqlResult = sqlStatement.executeQuery();
                    while (priSqlResult.next()) {
                        cols.add(priSqlResult.getObject(1));
                    }
                    priSqlResult.close();
                    //sqlStatement.close();
                    //解析sql，并替换*为所有字段
                    resultTableCols = SqlParserUtil.getSelectedCols(sqlExe, cols);
                    //如果有主键而且select字段包含所有主键
                    if (pri != null && resultTableCols.containsAll(List.of(pri.split(",")))) {
                        for (String key : pri.split(",")) {
                            int columnIndex = resultTableCols.indexOf(key);
                            resultTablePriNum.add(columnIndex);
                            Platform.runLater(() -> {
                                resultSetEditableEnabledLabel.setVisible(true);
                                //这里如果执行sql结果集表格刷新太快，可能报错，但不影响，表明主键还未获取到就已经开始执行其他sql了
                                TableColumn<String, Object> column = (TableColumn<String, Object>) resultSetTableView.getColumns().get(columnIndex + 1);
                                StackPane sp = new StackPane();
                                Label priLabel = new Label("PRI");
                                priLabel.setStyle("-fx-font-size: 8;-fx-text-fill: #9f453c");
                                sp.getChildren().add(priLabel);
                                StackPane.setAlignment(priLabel, Pos.BOTTOM_LEFT);
                                column.getGraphic().setStyle("-fx-text-fill:#9f453c ");
                                sp.getChildren().add(column.getGraphic()); // 添加现有的列头内容
                                column.setGraphic(sp); // 将按钮设置为列头的 graphic
                                if (!sqlConnect.getReadonly()) {
                                    resultSetTableView.setEditable(true);
                                }
                            });
                        }
                        //如果select字段中没有主键，检查是否有rowid代替主键
                    } else if (resultTableCols.contains("rowid")) {
                        int columnIndex = resultTableCols.indexOf("rowid");
                        resultTablePriNum.add(columnIndex);
                        System.out.println(columnIndex);
                        Platform.runLater(() -> {
                            resultSetEditableEnabledLabel.setVisible(true);
                            TableColumn<String, Object> column = (TableColumn<String, Object>) resultSetTableView.getColumns().get(columnIndex + 1);
                            StackPane sp = new StackPane();
                            Label priLabel = new Label("ROWID");
                            priLabel.setStyle("-fx-font-size: 5;-fx-text-fill: #9f453c");
                            sp.getChildren().add(priLabel);
                            StackPane.setAlignment(priLabel, Pos.BOTTOM_LEFT);
                            column.getGraphic().setStyle("-fx-text-fill:#9f453c ");
                            sp.getChildren().add(column.getGraphic()); // 添加现有的列头内容
                            column.setGraphic(sp); // 将按钮设置为列头的 graphic
                            if (!sqlConnect.getReadonly()) {
                                resultSetTableView.setEditable(true);
                            }
                        });
                    } else {
                        resultSetTableView.setEditable(false);
                    }
                } catch (SQLException e) {
                    //System.out.println("error no:"+e.getErrorCode());
                    //出现错误不做处理
                    //log.error(e.getMessage(), e);
                    //throw new Exception("ERROR");
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
        closeResultSet();
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
}

