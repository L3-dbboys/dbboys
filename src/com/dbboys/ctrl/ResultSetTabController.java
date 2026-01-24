package com.dbboys.ctrl;

import com.dbboys.app.Main;
import com.dbboys.customnode.CustomLabelTextField;
import com.dbboys.customnode.CustomResultsetTableView;
import com.dbboys.customnode.CustomTableCell;
import com.dbboys.customnode.CustomUserTextField;
import com.dbboys.service.ConnectDBaccessService;
import com.dbboys.service.SqlparserService;
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
    public CustomLabelTextField lastsql_textfield;
    @FXML
    public Button lastsql_refresh_button;
    @FXML
    public VBox resultset_vbox;
    @FXML
    public CustomResultsetTableView resultset_tableview;
    @FXML
    public HBox resultset_button_hbox;
    @FXML
    public Label resultset_editable_enabled_label;
    @FXML
    public Label resultset_editable_disabled_label;
    @FXML
    public CustomUserTextField resultset_per_time_textfield;
    @FXML
    public Button resultset_nextpage_button;
    @FXML
    public Button resultset_allrows_button;
    @FXML
    public Button resultset_count_button;
    @FXML
    public Label resultset_fetched_rows_label;
    @FXML
    public Label resultset_total_rows_label;
    @FXML
    public Label sql_used_time_label;
    @FXML
    public Button resultset_export_button;
    public Button hidden_disconnected_button = new Button();
    public StackPane sql_execute_process_stackpane;
    public Connect SQLConnect;
    public ResultSet sql_resultset;
    public long sql_fetched_time = 0;
    public long sql_fetch_start_time = 0;
    public long sql_fetch_end_time = 0;
    public Integer sql_fetched_rows = 0;
    public List<ObservableList<String>> sql_resultset_list = new ArrayList<>();
    public PreparedStatement sql_statement;
    public ParameterMetaData paramMetaData;
    public CallableStatement sql_cstmt;
    public Boolean callHasResultset = false;
    public List<TableColumn<ObservableList<String>, Object>> colList = new ArrayList<>();
    public Label sql_execute_process_label;
    public Task<Void> SQLTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            return null;
        }
    };
    //获取主键相关变量
    public List<Integer> result_table_pri_num = new ArrayList();
    public String result_from_table;
    public List result_table_cols = new ArrayList();
    public ResultSet pri_sql_result; //获取主键结果集
    public List sql_param_list = new ArrayList();
    public ResultSetMetaData sql_metaData;
    public Runnable eventEnd = () -> {
        Platform.runLater(() -> {
            sql_execute_process_stackpane.setVisible(false);
            sql_fetch_end_time = System.currentTimeMillis();
            sql_fetched_time += sql_fetch_end_time - sql_fetch_start_time;
            resultset_tableview.getItems().addAll(sql_resultset_list);
            if (sql_fetched_rows > 0) {
                resultset_fetched_rows_label.setText(String.valueOf((Integer.parseInt(resultset_fetched_rows_label.getText()) + sql_fetched_rows)));
                sql_used_time_label.setText(String.valueOf(sql_fetched_time / 1000.0));
            }
            sql_resultset_list.clear();
        });
    };
    //最后一次执行的sql
    @FXML
    private Button lastsql_copy_button;
    private String sql_result_count;


    public ResultSetTabController(Connect SQLConnect, StackPane sql_execute_process_stackpane) {
        this.sql_execute_process_stackpane = sql_execute_process_stackpane;
        this.SQLConnect = SQLConnect;
    }

    public void initialize() {
        resultset_editable_disabled_label.visibleProperty().bind(resultset_editable_enabled_label.visibleProperty().not());
        sql_execute_process_label = (Label) ((HBox) sql_execute_process_stackpane.getChildren().get(0)).getChildren().get(1);
        resultset_nextpage_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());
        resultset_allrows_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());
        lastsql_refresh_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());
        resultset_export_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());
        resultset_count_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());

        //执行计划按钮事件


        //连接变更响应事件

        //最后sql记录
        lastsql_textfield.getTooltip().setShowDelay(Duration.millis(100));

        //复制按钮
        lastsql_copy_button.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(lastsql_textfield.getTooltip().getText());
            clipboard.setContent(content);
            NotificationUtil.showNotification(Main.mainController.notice_pane, "已复制！");
        });

        //刷新按钮
        lastsql_refresh_button.setOnAction(event -> {
        });

        //结果集表格
        Label tableview_empty_label = new Label("");
        resultset_tableview.setPlaceholder(tableview_empty_label);//设置空白表格提示
        resultset_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultset_tableview.getSelectionModel().setCellSelectionEnabled(true);
        resultset_tableview.prefWidthProperty().bind(resultset_vbox.widthProperty());
        resultset_tableview.prefHeightProperty().bind(resultset_vbox.heightProperty());
        resultset_tableview.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        resultset_tableview.setSortPolicy(tv -> {
            TableView.DEFAULT_SORT_POLICY.call((TableView) tv);
            // 使用 Platform.runLater 在排序完成后恢复选中的单元格
            Platform.runLater(() -> {
                resultset_tableview.getSelectionModel().clearSelection();
            });
            return true;
        });

        //结果集处理按钮
        resultset_per_time_textfield.setText(ConfigManagerUtil.getProperty("RESULT_FETCH_PER_TIME"));
        resultset_per_time_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                resultset_per_time_textfield.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });


        lastsql_textfield.getTooltip().setShowDelay(Duration.millis(100));

        //复制按钮
        lastsql_copy_button.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(lastsql_textfield.getTooltip().getText());
            clipboard.setContent(content);
            NotificationUtil.showNotification(Main.mainController.notice_pane, "已复制！");
        });
        //获取下一个结果集
        resultset_nextpage_button.setOnAction(event -> {
            SQLTask = createGetNextPageResultSetTask();
            new Thread(SQLTask).start();
        });


        //获取全部结果集
        resultset_allrows_button.setOnAction(event -> {
            SQLTask = createGetAllResultSetTask();
            new Thread(SQLTask).start();

        });

        //获取结果集总数

        resultset_count_button.setOnAction(event -> {
            SQLTask = createGetResultSetCountTask();
            new Thread(SQLTask).start();
        });

        resultset_export_button.setOnAction(event -> {
            if (resultset_tableview.getItems().size() == 0) {
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
                    DownloadManagerUtil.addDownload(resultset_tableview, file, true, sql_metaData);
                }
            } else {

            }
        });


    }

    public void init() {
        callHasResultset = false;
        closeResultSet();
        sql_fetched_time = 0;
        sql_fetched_rows = 0;
        Platform.runLater(() -> {
            resultset_editable_enabled_label.setVisible(false);
            resultset_tableview.setEditable(false);
            resultset_tableview.getColumns().setAll(resultset_tableview.getColumns().get(0));
            resultset_tableview.getItems().clear();
            resultset_fetched_rows_label.setText("0");
            resultset_total_rows_label.setText("?");
            sql_used_time_label.setText("0");
        });

    }

    public Task<Void> createGetNextPageResultSetTask() {
        sql_execute_process_stackpane.setVisible(true);
        sql_fetched_rows = 0;
        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    //updateMessage("Fetching... ");
                    //游标滚动到上次取数的位置
                    //sql_result.absolute(sql_fetched_rows);
                    ResultSetMetaData metaData = sql_resultset.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    // 添加数据到 TableView
                    Integer j = 1;
                    sql_resultset_list.clear();
                    Platform.runLater(() -> {
                        sql_execute_process_label.setText(" 正在获取结果集...");
                    });
                    sql_fetch_start_time = System.currentTimeMillis();
                    while (j <= Integer.parseInt(resultset_per_time_textfield.getText()) && sql_resultset.next()) {
                        if (isCancelled()) {
                            break;
                        }
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(null);
                        for (int i = 1; i <= columnCount; i++) {
                            row.add(sql_resultset.getString(i));
                        }
                        sql_resultset_list.add(row);
                        j++;
                        sql_fetched_rows++;
                        if (sql_fetched_rows > 0 && sql_fetched_rows % 200 == 0) {
                            final int sql_fetched_rows_final = sql_fetched_rows;
                            Platform.runLater(() -> {
                                sql_execute_process_label.setText(" 已获取结果集[ " + sql_fetched_rows_final + " ]行，");
                            });
                        }
                    }

                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        hidden_disconnected_button.fire();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                    }
                }
                return null;
            }

        };
        SQLTask.setOnSucceeded(event1 -> eventEnd.run());
        SQLTask.setOnCancelled(event1 -> eventEnd.run());
        SQLTask.setOnFailed(event1 -> eventEnd.run());
        return SQLTask;
    }


    public Task<Void> createGetAllResultSetTask() {
        sql_fetched_rows = 0;
        sql_execute_process_stackpane.setVisible(true);
        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    //updateMessage("Fetching... ");
                    //游标滚动到上次取数之后位置
                    //sql_result.absolute(sql_fetched_rows);
                    ResultSetMetaData metaData = sql_resultset.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    // 添加数据到 TableView
                    sql_resultset_list.clear();
                    Platform.runLater(() -> {
                        sql_execute_process_label.setText(" 正在获取结果集...");
                    });
                    sql_fetch_start_time = System.currentTimeMillis();
                    while (sql_resultset.next()) {
                        if (isCancelled()) {
                            break;
                        }
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(null);
                        for (int i = 1; i <= columnCount; i++) {
                            row.add(sql_resultset.getString(i));
                        }
                        sql_resultset_list.add(row);
                        sql_fetched_rows++;
                        if (sql_fetched_rows > 0 && sql_fetched_rows % 200 == 0) {
                            final int sql_fetched_rows_final = sql_fetched_rows;
                            Platform.runLater(() -> {
                                sql_execute_process_label.setText(" 已获取结果集[ " + sql_fetched_rows_final + " ]行，");
                            });
                        }
                    }
                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        hidden_disconnected_button.fire();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                    }
                }
                return null;
            }

        };
        SQLTask.setOnSucceeded(event1 -> {
            eventEnd.run();
            Platform.runLater(() -> {
                resultset_total_rows_label.setText(resultset_fetched_rows_label.getText());
            });
        });
        SQLTask.setOnCancelled(event1 -> eventEnd.run());
        SQLTask.setOnFailed(event1 -> eventEnd.run());
        return SQLTask;
    }


    public Task<Void> createGetResultSetCountTask() {
        //sql_is_count=true;
        sql_execute_process_stackpane.setVisible(true);
        Platform.runLater(() -> {
            sql_execute_process_label.setText(" 正在获取结果集总数...");
        });
        String sql_count = "select count(*) from (" + lastsql_textfield.getTooltip().getText().replaceFirst(";\\s*$", "") + ")";
        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ResultSet rs = null;
                try {
                    sql_statement = SQLConnect.getConn().prepareStatement(sql_count);
                    rs = sql_statement.executeQuery();
                    rs.next();
                    sql_result_count = String.valueOf(rs.getInt(1));
                    rs.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        //把结果集游标关闭，避免在activedatabase时关闭游标触发79730错误导致重复弹出是否需要重新连接。
                        if (rs != null) {
                            rs = null;
                        }
                        hidden_disconnected_button.fire();
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

        SQLTask.setOnSucceeded(event1 -> {
            sql_execute_process_stackpane.setVisible(false);
            resultset_total_rows_label.setText(sql_result_count);
        });
        SQLTask.setOnCancelled(event1 -> {
            sql_execute_process_stackpane.setVisible(false);
        });
        SQLTask.setOnFailed(event1 -> {
            sql_execute_process_stackpane.setVisible(false);
        });

        return SQLTask;
    }


    public long executeSelect(String sql_exe, Task SQLTask, Boolean showFething, SimpleStringProperty sql_trans, ChoiceBox commitmode) throws SQLException {
        init();
        sql_fetched_rows = 0;
        //SQLConnect.getConn().prepareStatement(sql_exe);
        //如果上一次绑定变量未输入，下一次执行到这里会报213错误（SQLConnect.getConn().prepareStatement(sql_exe)报213错误）
        /*
        Connection conn=SQLConnect.getConn();
        if(conn==null){
            System.out.println("conn is null");
        }
        if(conn.isClosed()){
            System.out.println("conn is closeed");

        }
        conn.prepareStatement("select * from systables");

         */
        //连接断开后，r28、r61驱动会卡死在这里
        sql_statement = SQLConnect.getConn().prepareStatement(sql_exe);

        paramMetaData = sql_statement.getParameterMetaData();

        int paramCount = paramMetaData.getParameterCount();

        if (paramCount > 0) {

            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                sql_param_list.clear();
                sql_param_list = PopupWindowUtil.openParamWindow(paramCount);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        long sql_start_time = System.currentTimeMillis();

        //使用sql_param_list.size()>0会导致连接断开后继续执行，r28，r61驱动卡在sql_resultset.close()，有点奇怪,增加sql_param_list!=null后正常
        if (sql_param_list != null && !sql_param_list.isEmpty()) {
            for (int z = 1; z <= sql_param_list.size(); z++) {
                sql_statement.setObject(z, (sql_param_list.get(z - 1)));
            }
        }

        sql_statement.setFetchSize(200);
        sql_resultset = sql_statement.executeQuery();
        long sql_end_time = System.currentTimeMillis();
        sql_fetched_time = sql_end_time - sql_start_time;
        //updateMessage("Fetching... ");
        //获取结果集

        sql_metaData = sql_resultset.getMetaData();
        int columnCount = sql_metaData.getColumnCount();
        //resultset_tableview.refresh();
        // 动态生成列

        colList.clear();
        double avgColWidth = (resultset_tableview.getWidth() - 30) / columnCount;


        for (int j = 1; j <= columnCount; j++) {

            final int columnIndex = j;
            String colTypeName = sql_metaData.getColumnTypeName(j);
            Integer length = sql_metaData.getColumnDisplaySize(j);


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
            if (showFething) {


                column.setOnEditCommit(event -> {
                    Object oldvalue = event.getOldValue();
                    //Object colvalue = event.getNewValue();
                    //替换换行
                    Object colvalue = event.getNewValue().toString().replaceAll("\u21B5", "\n");

                    //替换换行后显示
                    event.getRowValue().set(columnIndex, colvalue.toString());

                    String update_col = String.valueOf(result_table_cols.get(columnIndex - 1));
                    String update_sql = "update " + result_from_table + " set " + update_col + "=? where 1=1 ";
                    String sql_params = "";

                    try {

                        if (result_table_pri_num != null && !result_table_pri_num.isEmpty()) {
                            for (Integer colnum : result_table_pri_num) {
                                update_sql += " and " + result_table_cols.get(colnum) + "=?";
                            }
                        }
                        //此处会复现断链卡死，增加tab缩进后正常

                        sql_statement = SQLConnect.getConn().prepareStatement(update_sql);

                        sql_statement.setObject(1, colvalue.equals("[NULL]") ? null : colvalue);


                        if (colvalue.equals("[NULL]")) {
                            sql_statement.setObject(1, null);
                            sql_params += "(null";
                            Platform.runLater(() -> {
                                event.getRowValue().set(columnIndex, null);
                                event.getTableView().refresh();
                            });
                        } else {
                            sql_params += "[" + colvalue;
                            sql_statement.setObject(1, colvalue);
                        }


                        Integer rownumber = event.getTablePosition().getRow();
                        List selectedData = (List) resultset_tableview.getItems().get(rownumber);
                        int prepareNum = 2;
                        for (Integer colnum : result_table_pri_num) {
                            TablePosition<?, ?> position = (TablePosition<?, ?>) resultset_tableview.getSelectionModel().getSelectedCells().get(0);
                            if (position.getColumn() == colnum + 1) {
                                //如果编辑的是主键列，绑定变量为修改前的值
                                sql_statement.setObject(prepareNum, oldvalue);
                                sql_params += "," + oldvalue;
                            } else {
                                sql_statement.setObject(prepareNum, selectedData.get(colnum + 1));
                                sql_params += "," + selectedData.get(colnum + 1);
                            }
                            prepareNum++;
                        }

                        UpdateResult updateResult = new UpdateResult();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        int sql_affect = 0;
                        long sql_begin_time = System.currentTimeMillis();
                        sql_affect = sql_statement.executeUpdate();
                        long sql_finish_time = System.currentTimeMillis();
                        updateResult.setConnectId(SQLConnect.getId());
                        updateResult.setStartTime(sdf.format(sql_begin_time));
                        updateResult.setEndTime(sdf.format(sql_finish_time));
                        updateResult.setElapsedTime(String.format("%.3f", (sql_finish_time - sql_begin_time) / 1000.0) + " sec");
                        updateResult.setAffectedRows(sql_affect);
                        updateResult.setDatabase(SQLConnect.getDatabase());
                        sql_params += "]";
                        updateResult.setUpdateSql(update_sql);
                        if (commitmode.getValue().toString().equals("手动提交")) {
                            updateResult.setMark("手动提交，查询结果集编辑，参数" + sql_params);
                            sql_trans.set(sql_trans.get() + update_sql + "\n");
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "当前连接为手动提交，修改暂未提交，请点击提交或回滚！");
                        } else {
                            updateResult.setMark("自动提交，查询结果集编辑，参数" + sql_params);
                        }
                        SqliteDBaccessUtil.saveSqlHistory(updateResult);


                    } catch (SQLException e) {
                        Platform.runLater(() -> {
                            event.getRowValue().set(columnIndex, oldvalue == null ? null : oldvalue.toString());
                            event.getTableView().refresh();
                            if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                                hidden_disconnected_button.fire();
                            } else {
                                AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                            }
                        });
                    } finally {
                        try {
                            sql_statement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            colList.add(column);
            StackPane colheader = new StackPane();
            //此处会复现断链卡死

            String colName = sql_metaData.getColumnLabel(j);
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
                resultset_tableview.getSelectionModel().clearSelection();
                for (int rowIndex = 0; rowIndex < resultset_tableview.getItems().size(); rowIndex++) {
                    resultset_tableview.getSelectionModel().select(rowIndex, column);
                }
                event.consume();
            });
        }
        // 添加数据到 TableView
        sql_start_time = System.currentTimeMillis();
        Integer perpage = 0;
        if (resultset_per_time_textfield.getText().equals("") || resultset_per_time_textfield.getText().equals("0") || resultset_per_time_textfield.getText().startsWith("-")) {
            perpage = 999999999;
        } else {
            perpage = Integer.parseInt(resultset_per_time_textfield.getText());
        }
        if (showFething) {
            Platform.runLater(() -> {
                sql_execute_process_label.setText(" 正在获取结果集...");
            });
        }


        while (sql_fetched_rows < perpage && sql_resultset.next()) {
            //如果被取消了，退出循环不再执行
            if (SQLTask.isCancelled()) {
                break;
            } else {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(null);  //第一列为序号，不许要数据
                for (int z = 1; z <= columnCount; z++) {
                    row.add(sql_resultset.getString(z));
                }
                //不在主线程中加载列可能导致数据错误
                sql_resultset_list.add(row);
                sql_fetched_rows++;
                if (showFething) {
                    if (sql_fetched_rows > 0 && sql_fetched_rows % 200 == 0) {
                        final int sql_fetched_rows_final = sql_fetched_rows;
                        Platform.runLater(() -> {
                            sql_execute_process_label.setText(" 已获取结果集[ " + sql_fetched_rows_final + " ]行，");
                        });
                    }
                }
            }

        }


        Platform.runLater(() -> {
            lastsql_textfield.setText(sql_exe);
            lastsql_textfield.getTooltip().setText(sql_exe);
            resultset_tableview.getColumns().addAll(colList);
            resultset_tableview.getItems().addAll(sql_resultset_list);
            resultset_tableview.refresh();//此处不刷新可能导致部分行显示空白
            resultset_fetched_rows_label.setText(sql_fetched_rows.toString());
            sql_used_time_label.setText(String.format("%.3f", sql_fetched_time / 1000.0));
            sql_resultset_list.clear();
        });

        sql_end_time = System.currentTimeMillis();
        sql_fetched_time += sql_end_time - sql_start_time;
        //sql_statement.close();


        return sql_fetched_time;


    }


    public long executeCall(String sql_exe, Task SQLTask, Boolean showFething) throws SQLException {
        init();
        sql_fetched_rows = 0;
        sql_cstmt = SQLConnect.getConn().prepareCall(sql_exe);
        paramMetaData = sql_cstmt.getParameterMetaData();
        int paramCount = paramMetaData.getParameterCount();
        long sql_start_time = System.currentTimeMillis();
        if (paramCount > 0) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                sql_param_list.clear();
                sql_param_list = PopupWindowUtil.openParamWindow(paramCount);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sql_start_time = System.currentTimeMillis();
            if (sql_param_list.size() > 0) {
                for (int z = 1; z <= sql_param_list.size(); z++) {
                    sql_cstmt.setObject(z, (sql_param_list.get(z - 1)));
                }
            }
        }

        callHasResultset = sql_cstmt.execute();
        if (callHasResultset) {
            sql_resultset = sql_cstmt.getResultSet();
            long sql_end_time = System.currentTimeMillis();
            sql_fetched_time = sql_end_time - sql_start_time;
            //updateMessage("Fetching... ");
            //获取结果集
            sql_metaData = sql_resultset.getMetaData();
            int columnCount = sql_metaData.getColumnCount();
            //resultset_tableview.refresh();
            // 动态生成列
            colList.clear();
            double avgColWidth = (resultset_tableview.getWidth() - 30) / columnCount;
            for (int j = 1; j <= columnCount; j++) {
                final int columnIndex = j;
                String colTypeName = sql_metaData.getColumnTypeName(j);
                Integer length = sql_metaData.getColumnDisplaySize(j);
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

                colList.add(column);
                Label colLabel = new Label(sql_metaData.getColumnName(j));
                //Label colLabel = new Label(sql_metaData.getColumnLabel(j));
                Tooltip tp = new Tooltip(colTypeName);
                tp.setShowDelay(Duration.millis(100));
                colLabel.setTooltip(tp);

                column.setPrefWidth(Math.max(colLabel.getText().length() * 15, avgColWidth));
                colLabel.setMaxWidth(Double.MAX_VALUE);
                column.setReorderable(false);
                column.setGraphic(colLabel);
                //column.setGraphic(new javafx.scene.control.Label(colLabel.getText()));  // Ensure a graphic node is present

                column.getGraphic().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    resultset_tableview.getSelectionModel().clearSelection();
                    for (int rowIndex = 0; rowIndex < resultset_tableview.getItems().size(); rowIndex++) {
                        resultset_tableview.getSelectionModel().select(rowIndex, column);
                    }
                    event.consume();
                });

            }
            // 添加数据到 TableView
            sql_start_time = System.currentTimeMillis();
            Integer perpage = 0;
            if (resultset_per_time_textfield.getText().equals("") || resultset_per_time_textfield.getText().equals("0") || resultset_per_time_textfield.getText().startsWith("-")) {
                perpage = 999999999;
            } else {
                perpage = Integer.parseInt(resultset_per_time_textfield.getText());
            }
            if (showFething) {
                Platform.runLater(() -> {
                    sql_execute_process_label.setText(" 正在获取结果集...");
                });
            }
            while (sql_fetched_rows < perpage && sql_resultset.next()) {
                //如果被取消了，退出循环不再执行
                if (SQLTask.isCancelled()) {
                    break;
                } else {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(null);  //第一列为序号，不许要数据
                    for (int z = 1; z <= columnCount; z++) {
                        row.add(sql_resultset.getString(z));
                    }
                    //不在主线程中加载列可能导致数据错误
                    sql_resultset_list.add(row);
                    sql_fetched_rows++;
                    if (showFething) {
                        if (sql_fetched_rows > 0 && sql_fetched_rows % 200 == 0) {
                            final int sql_fetched_rows_final = sql_fetched_rows;
                            Platform.runLater(() -> {
                                sql_execute_process_label.setText(" 已获取结果集[ " + sql_fetched_rows_final + " ]行，");
                            });
                        }
                    }
                }

            }
            Platform.runLater(() -> {
                resultset_button_hbox.getChildren().remove(resultset_count_button);
                lastsql_textfield.setText(sql_exe);
                lastsql_textfield.getTooltip().setText(sql_exe);
                resultset_tableview.getColumns().addAll(colList);
                resultset_tableview.getItems().addAll(sql_resultset_list);
                resultset_tableview.refresh();//此处不刷新可能导致部分行显示空白
                resultset_fetched_rows_label.setText(sql_fetched_rows.toString());
                sql_used_time_label.setText(String.format("%.3f", sql_fetched_time / 1000.0));
                sql_resultset_list.clear();
            });

            sql_end_time = System.currentTimeMillis();
            sql_fetched_time += sql_end_time - sql_start_time;
        }
        //sql_cstmt.close();
        return sql_fetched_time;
    }


    public void getPrimaryKeys(String sql_exe) {
        result_table_pri_num.clear();
        result_from_table = new SqlparserService().getFromTable(sql_exe);
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

        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (sql_statement != null) {
                    try {
                        sql_statement.cancel();
                    } catch (SQLException e) {
                    }
                }


                try {
                    //获取主键
                    String pri = null;
                    //Connection conn=new ConnectDBaccessService().getConnection((Connect) SQLConnect.clone());
                    //sql_statement=conn.prepareStatement(finalSql_get_primary);
                    sql_statement = SQLConnect.getConn().prepareStatement(finalSql_get_primary);
                    sql_statement.setObject(1, result_from_table);
                    pri_sql_result = sql_statement.executeQuery(); //一个statment只能打开一个结果集游标
                    if (pri_sql_result.next()) {
                        pri = pri_sql_result.getString(1);
                    } else {
                    }
                    //过程中有多个结果集，必须先关闭，避免执行database切换时报错-267
                    pri_sql_result.close();

                    //pri_sql_result.close();
                    //sql_statement.close();

                    //获取*所有字段
                    List cols = new ArrayList();
                    //sql_statement=conn.prepareStatement("select colname from syscolumns c,systables t where  t.tabid=c.tabid and tabname=?");

                    sql_statement = SQLConnect.getConn().prepareStatement("select colname from syscolumns c,systables t where  t.tabid=c.tabid and tabname=?");
                    sql_statement.setObject(1, result_from_table);
                    pri_sql_result = sql_statement.executeQuery();
                    while (pri_sql_result.next()) {
                        cols.add(pri_sql_result.getObject(1));
                    }
                    pri_sql_result.close();
                    //sql_statement.close();
                    //解析sql，并替换*为所有字段
                    result_table_cols = new SqlparserService().getSelectedCols(sql_exe, cols);
                    //如果有主键而且select字段包含所有主键
                    if (pri != null && result_table_cols.containsAll(List.of(pri.split(",")))) {
                        for (String key : pri.split(",")) {
                            int columnIndex = result_table_cols.indexOf(key);
                            result_table_pri_num.add(columnIndex);
                            Platform.runLater(() -> {
                                resultset_editable_enabled_label.setVisible(true);
                                //这里如果执行sql结果集表格刷新太快，可能报错，但不影响，表明主键还未获取到就已经开始执行其他sql了
                                TableColumn<String, Object> column = (TableColumn<String, Object>) resultset_tableview.getColumns().get(columnIndex + 1);
                                StackPane sp = new StackPane();
                                Label priLabel = new Label("PRI");
                                priLabel.setStyle("-fx-font-size: 8;-fx-text-fill: #9f453c");
                                sp.getChildren().add(priLabel);
                                StackPane.setAlignment(priLabel, Pos.BOTTOM_LEFT);
                                column.getGraphic().setStyle("-fx-text-fill:#9f453c ");
                                sp.getChildren().add(column.getGraphic()); // 添加现有的列头内容
                                column.setGraphic(sp); // 将按钮设置为列头的 graphic
                                if (!SQLConnect.getReadonly()) {
                                    resultset_tableview.setEditable(true);
                                }
                            });
                        }
                        //如果select字段中没有主键，检查是否有rowid代替主键
                    } else if (result_table_cols.contains("rowid")) {
                        int columnIndex = result_table_cols.indexOf("rowid");
                        result_table_pri_num.add(columnIndex);
                        System.out.println(columnIndex);
                        Platform.runLater(() -> {
                            resultset_editable_enabled_label.setVisible(true);
                            TableColumn<String, Object> column = (TableColumn<String, Object>) resultset_tableview.getColumns().get(columnIndex + 1);
                            StackPane sp = new StackPane();
                            Label priLabel = new Label("ROWID");
                            priLabel.setStyle("-fx-font-size: 5;-fx-text-fill: #9f453c");
                            sp.getChildren().add(priLabel);
                            StackPane.setAlignment(priLabel, Pos.BOTTOM_LEFT);
                            column.getGraphic().setStyle("-fx-text-fill:#9f453c ");
                            sp.getChildren().add(column.getGraphic()); // 添加现有的列头内容
                            column.setGraphic(sp); // 将按钮设置为列头的 graphic
                            if (!SQLConnect.getReadonly()) {
                                resultset_tableview.setEditable(true);
                            }
                        });
                    } else {
                        resultset_tableview.setEditable(false);
                    }
                } catch (SQLException e) {
                    //System.out.println("error no:"+e.getErrorCode());
                    //出现错误不做处理
                    //log.error(e.getMessage(), e);
                    //throw new Exception("ERROR");
                }
                sql_statement.close();
                return null;
            }

        };

        new Thread(SQLTask).start();
    }

    public void closeResultSet() {
        try {
            if (sql_resultset != null) {
                sql_resultset.close();
            }
            if (pri_sql_result != null) {
                pri_sql_result.close();
            }
        } catch (SQLException e) {
            sql_resultset = null;
            pri_sql_result = null;
            log.error(e.getMessage(), e);
        }
    }

    public void cancel() {
        if (SQLTask != null && SQLTask.isRunning()) {
            SQLTask.cancel();

        }
        try {
            if (sql_statement != null && !sql_statement.isClosed()) {
                sql_statement.cancel();
                sql_statement = null; //必须设置为null，下次执行SQLConnect.getConn().prepareStatement(sql_exe);才不会报213错误
            }
            if (this.sql_cstmt != null && !sql_cstmt.isClosed()) {
                sql_cstmt.cancel();
                sql_cstmt = null;
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
