package com.dbboys.ctrl;

import com.dbboys.app.Main;
import com.dbboys.customnode.*;
import com.dbboys.service.ConnectDBaccessService;
import com.dbboys.service.SqlparserService;
import com.dbboys.util.*;
import com.dbboys.vo.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SqlTabController {
    private static final Logger log = LogManager.getLogger(SqlTabController.class);
    @FXML
    public Button sql_run_button;
    @FXML
    public ChoiceBox<Connect> sql_connect_choicebox;
    @FXML
    public ChoiceBox<Database> sql_db_choicebox;
    @FXML
    public ChoiceBox sql_commitmode_choicebox;
    //sql编辑框及结果集
    @FXML
    public SplitPane sql_splitpane;
    @FXML
    public StackPane sql_edit_stackpane;
    @FXML
    public CustomSqlEditCodeArea sql_edit_codearea;
    //
    public boolean isRefreshConnectList=false;
    //分隔符位置
    public Double sql_splitpane_driver = Main.split2Pos;
    public Connect SQLConnect = new Connect();
    public Task<Void> SQLTask = new Task<>() {
        @Override
        protected Void call() {
            return null;
        }
    };
    public ObservableList<Database> database_choicebox_list = FXCollections.observableArrayList();
    public CustomSearchReplaceVbox customSearchReplaceVbox = new CustomSearchReplaceVbox(null);
    public VBox resultset_vbox = new VBox();
    public ResultSetTabController currentResultSetTabController;
    public CustomResultsetTab customResultsetTab;
    //结果集表格相关列表
    public ObservableList<UpdateResult> updateResults;
    public UpdateResult updateResult;
    public String sql_init = "";  //表或视图拖动新建tab后自动执行的sql
    public String sql_exe;
    public Integer sql_affect;
    public PreparedStatement sql_statement;
    public ParameterMetaData paramMetaData;
    public CallableStatement sql_callablestmt = null;
    public long sql_used_time;
    public long sql_start_time;
    public long sql_end_time;
    public long sql_total_time;
    public int sql_num = 0;
    public SimpleStringProperty sql_trans = new SimpleStringProperty("");
    boolean onlyOneSql = true;  //判断是否只有一条sql，如果是，select要执行并弹出报错信息，update要弹出报错，且用于是否在任务完成后获取主键
    @FXML
    private VBox sqlTab;
    @FXML
    private Pane pane_top;
    @FXML
    private Button sql_explain_button;
    @FXML
    private Button sql_stop_button;
    @FXML
    private StackPane sql_connect_choicebox_icon_stackpane;
    @FXML
    private CustomLabelTextField sql_user_textfield;
    @FXML
    private ChoiceBox<String> sql_sqlmode_choicebox;
    @FXML
    private Button sql_recode_button;
    @FXML
    private Label sql_readonly_label;
    @FXML
    private VirtualizedScrollPane sql_edit_scollpane;
    //事务未提交hbox
    @FXML
    private HBox trans_hbox;
    @FXML
    private Button trans_commit_button;
    @FXML
    private Button trans_rollback_button;
    //结果集
    @FXML
    private Pane pane_bottom;
    @FXML
    private StackPane pane_bottom_stackpane;
    @FXML
    private TabPane resultset_tabpane;
    @FXML
    private CustomResultsetTableView resultset_total_tableview;
    @FXML
    private StackPane resultset_stackpane;
    //执行过程中提示面板
    @FXML
    private StackPane sql_execute_process_stackpane;
    @FXML
    private Label sql_execute_taskInfo;
    @FXML
    private Label sql_execute_timeInfo;
    private CustomInfoStackPane explain_result_stackpane;
    private Boolean sql_is_refresh = false;
    private final ConnectDBaccessService connectDBaccessService = new ConnectDBaccessService();
    private final Connect defalutConnect = new Connect();
    private final Database defalutDatabase = new Database();
    private List sql_param_list = new ArrayList();
    private String sql_exe_result = "";
    //执行sql所需相关变量
    private String sql_str = "";
    private final Tooltip commit_btn_tip = new Tooltip();
    private String newResultsetTabName;
    private final int[] sql_select_pos = {0, 0};


    public void initialize() throws IOException {

        //提交回滚按钮提示绑定
        commit_btn_tip.textProperty().bind(sql_trans);
        commit_btn_tip.setShowDelay(Duration.millis(100));
        trans_commit_button.setTooltip(commit_btn_tip);
        trans_rollback_button.setTooltip(commit_btn_tip);

        //加载搜索面板
        customSearchReplaceVbox.setMaxWidth(300);
        customSearchReplaceVbox.setMaxHeight(26);
        StackPane.setAlignment(customSearchReplaceVbox, Pos.TOP_RIGHT);
        StackPane.setMargin(customSearchReplaceVbox, new Insets(2, 17, 0, 0));
        sql_edit_stackpane.getChildren().add(customSearchReplaceVbox);
        sql_edit_codearea.customSearchReplaceVbox = customSearchReplaceVbox;


        //隐藏sqlmode选框
        sql_sqlmode_choicebox.setVisible(false);
        //加载结果集界面

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/ResultSetTab.fxml"));

        loader.setControllerFactory(clazz -> {
            if (clazz == ResultSetTabController.class) {
                return new ResultSetTabController(SQLConnect, sql_execute_process_stackpane);
            } else {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }
        });
        resultset_vbox = loader.load();
        resultset_stackpane.getChildren().add(resultset_vbox);
        currentResultSetTabController = loader.getController();
        currentResultSetTabController.hidden_disconnected_button.setOnAction(event -> {
            connectionDisconnected();
        });

        //增加执行计划面板
        explain_result_stackpane = new CustomInfoStackPane(new CustomInfoCodeArea());
        explain_result_stackpane.setVisible(false);
        resultset_stackpane.getChildren().add(explain_result_stackpane);

        currentResultSetTabController.lastsql_refresh_button.setOnAction(event -> {
            sql_is_refresh = true;
            sql_run_button.fire();
        });

        //splitpane分隔符
        sql_splitpane.setDividerPositions(Main.sqledit_codearea_is_max == 1 ? 1 : sql_splitpane_driver);
        sql_splitpane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (Main.sqledit_codearea_is_max == 1) {
                //保留两位小数设置，否则可能因为小数过多而设置不准
                Platform.runLater(() -> {
                    sql_splitpane.setDividerPositions(1);
                });
            } else {
                Platform.runLater(() -> {
                    sql_splitpane.setDividerPositions(sql_splitpane_driver);
                });
            }
        });

        //sql_splitpane被加载后，等split-pane-divider渲染完成后才能增加监听，否则找不到split-pane-divider
        //该事件用于最大化后恢复原窗口大小时的分隔符位置
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sql_splitpane.lookupAll(".split-pane-divider").forEach(divider -> {
                // 鼠标拖动事件
                divider.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
                    sql_splitpane_driver = sql_splitpane.getDividers().get(0).getPosition();
                });
            });
        }).start();

        //绑定才能让pane填充整个界面，必须使用pane，避免分隔栏拖动被阻挡
        sql_edit_codearea.prefWidthProperty().bind(pane_top.widthProperty());
        sql_edit_codearea.prefHeightProperty().bind(pane_top.heightProperty());

        //初始化连接图标
        Label sql_connect_choicebox_db_icon = new Label();
        Label sql_connect_choicebox_loading_icon = new Label();
        sql_connect_choicebox_loading_icon.setVisible(false);
        sql_connect_choicebox_icon_stackpane.getChildren().addAll(sql_connect_choicebox_db_icon, sql_connect_choicebox_loading_icon);
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M21.2812 3.3281 Q22.2656 4.3125 22.6094 5.6562 Q22.9688 6.9844 22.6094 8.3281 Q22.2656 9.6562 21.2812 10.6406 L19.4531 12.4688 Q18.4688 13.4531 17.125 13.7812 Q15.7969 14.1094 14.4844 13.7812 L19.4531 8.8125 Q20.2031 8.0625 20.2031 6.9844 Q20.2031 5.9062 19.4531 5.1562 Q18.7031 4.4062 17.625 4.4062 Q16.5469 4.4062 15.7969 5.1562 L10.8281 10.125 Q10.5 8.8125 10.8281 7.4844 Q11.1562 6.1406 12.1406 5.1562 L13.9688 3.3281 Q14.9531 2.3438 16.2812 2 Q17.625 1.6406 18.9531 2 Q20.2969 2.3438 21.2812 3.3281 ZM10.3125 16.125 L16.7344 9.7031 Q17.1094 9.3281 17.1094 8.7969 Q17.1094 8.25 16.7031 7.875 Q16.3125 7.5 15.7656 7.5312 Q15.2344 7.5469 14.8594 7.875 L8.4844 14.2969 Q8.1094 14.6719 8.1094 15.2188 Q8.1094 15.75 8.4844 16.125 Q8.8594 16.5 9.3906 16.4844 Q9.9375 16.4531 10.3125 16.125 ZM9.375 18.8438 L14.3438 13.875 Q14.7188 15.1875 14.3906 16.5312 Q14.0625 17.8594 13.0781 18.8438 L11.25 20.6719 Q10.2188 21.7031 8.9062 22.0312 Q7.5938 22.3594 6.25 22.0312 Q4.9219 21.7031 3.9062 20.7031 Q2.9062 19.6875 2.5469 18.3594 Q2.2031 17.0156 2.5469 15.6875 Q2.9062 14.3438 3.8906 13.3594 L5.7656 11.5312 Q6.75 10.5469 8.0625 10.2188 Q9.375 9.8906 10.7344 10.2188 L5.7656 15.1875 Q4.9688 15.9375 4.9688 17.0156 Q4.9688 18.0938 5.7344 18.875 Q6.5156 19.6406 7.5625 19.6406 Q8.625 19.6406 9.375 18.8438 Z");
        sql_connect_choicebox_db_icon.setGraphic(new Group(svgPath));
        svgPath.setScaleX(0.6);
        svgPath.setScaleY(0.6);

        ImageView loading_icon = new ImageView(new Image("file:images/loading.gif"));
        loading_icon.setScaleX(0.7);
        loading_icon.setScaleY(0.7);
        sql_connect_choicebox_loading_icon.setGraphic(loading_icon);
        Tooltip tooltip = new Tooltip("正在连接数据库");
        tooltip.setShowDelay(Duration.millis(100));
        sql_connect_choicebox_loading_icon.setTooltip(tooltip);

        svgPath.setFill(Paint.valueOf("#888"));

        //初始化数据库连接相关信息，初始化数据
        defalutConnect.setName("请选择数据库连接");
        defalutDatabase.setName("N/A");
        sql_connect_choicebox.setValue(defalutConnect);
        sql_db_choicebox.setValue(defalutDatabase);
        sql_user_textfield.setText("N/A");
        sql_commitmode_choicebox.getSelectionModel().selectFirst();

        sql_edit_codearea.sql_run_button = sql_run_button;
        SQLConnect = defalutConnect;

        //初始化连接数据
        List connect_list = new ArrayList<Connect>();
        for (TreeItem<TreeData> ti : Main.mainController.databasemeta_treeview.getRoot().getChildren()) {
            for (TreeItem<TreeData> t : ti.getChildren()) {
                Connect newConnect = new Connect((Connect) t.getValue());
                newConnect.setConn(null);
                connect_list.add(newConnect);
            }
        }
        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(connect_list);
        sql_connect_choicebox.setItems(dbtypelist);
        /*
        List connect_list = new ArrayList<Connect>();
        for (TreeItem<TreeData> ti : Main.mainController.databasemeta_treeview.getRoot().getChildren()) {
            for (TreeItem<TreeData> t : ti.getChildren()) {
                Connect newConnect = new Connect((Connect) t.getValue());
                newConnect.setConn(null);
                connect_list.add(newConnect);
            }
        }


        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(connect_list);
        sql_connect_choicebox.setItems(dbtypelist);

         */




        //头部按钮设置绑定关系
        sql_connect_choicebox.disableProperty().bind(Bindings.or(
                trans_hbox.visibleProperty(),
                sql_execute_process_stackpane.visibleProperty()
        ));
        trans_hbox.visibleProperty().bind(
                Bindings.notEqual("", sql_trans) // 对比字符串与空值，取反后绑定可见性
        );
        sql_db_choicebox.disableProperty().bind(sql_connect_choicebox.disableProperty());
        sql_sqlmode_choicebox.disableProperty().bind(sql_connect_choicebox.disableProperty());
        sql_commitmode_choicebox.disableProperty().bind(sql_connect_choicebox.disableProperty());
        sql_user_textfield.disableProperty().bind(sql_connect_choicebox.disableProperty());

        sql_stop_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty().not());
        sql_run_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());
        sql_explain_button.disableProperty().bind(sql_execute_process_stackpane.visibleProperty());

        resultset_tabpane.prefWidthProperty().bind(pane_bottom.widthProperty());
        resultset_tabpane.prefHeightProperty().bind(pane_bottom.heightProperty());
        resultset_total_tableview.prefWidthProperty().bind(pane_bottom.widthProperty());
        resultset_total_tableview.prefHeightProperty().bind(pane_bottom.heightProperty());
        resultset_total_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultset_total_tableview.getSelectionModel().setCellSelectionEnabled(true);
        resultset_total_tableview.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        //初始化结果集总表
        TableColumn<ObservableList<String>, Object> resultcol;
        TableColumn<ObservableList<String>, Object> begin;
        TableColumn<ObservableList<String>, Object> stop;
        TableColumn<ObservableList<String>, Object> drution;
        TableColumn<ObservableList<String>, Object> affect;
        TableColumn<ObservableList<String>, Object> sqlcol;
        TableColumn<ObservableList<String>, Object> databasecol;
        TableColumn<ObservableList<String>, Object> markcol;
        //定义结果集表结构
        resultcol = new TableColumn<ObservableList<String>, Object>("执行结果");
        resultcol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> result = new TableColumn<>("Execute Result");
        resultcol.setCellValueFactory(new PropertyValueFactory<>("result"));
        //result.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(1)));
        resultcol.setPrefWidth(120);
        resultcol.setReorderable(false);
        resultcol.setSortable(false);

        begin = new TableColumn<ObservableList<String>, Object>("开始时间");
        begin.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> begin = new TableColumn<>("Begin Time");
        begin.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        //begin.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(2)));
        begin.setPrefWidth(190);
        begin.setReorderable(false);
        begin.setSortable(false);

        stop = new TableColumn<ObservableList<String>, Object>("结束时间");
        stop.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> stop= new TableColumn<>("End Time");
        stop.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        //stop.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(3)));
        stop.setPrefWidth(190);
        stop.setReorderable(false);
        stop.setSortable(false);

        drution = new TableColumn<ObservableList<String>, Object>("执行耗时");
        drution.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> drution = new TableColumn<>("Elapsed Time");
        drution.setCellValueFactory(new PropertyValueFactory<>("elapsedTime"));
        //drution.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(4)));
        drution.setPrefWidth(100);
        drution.setReorderable(false);
        drution.setSortable(false);

        databasecol = new TableColumn<ObservableList<String>, Object>("库/模式");
        databasecol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> drution = new TableColumn<>("Elapsed Time");
        databasecol.setCellValueFactory(new PropertyValueFactory<>("database"));
        //drution.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(4)));
        databasecol.setPrefWidth(100);
        databasecol.setReorderable(false);
        databasecol.setSortable(false);


        markcol = new TableColumn<ObservableList<String>, Object>("备注");
        markcol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> result = new TableColumn<>("Execute Result");
        markcol.setCellValueFactory(new PropertyValueFactory<>("mark"));
        //result.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(1)));
        markcol.setPrefWidth(120);
        markcol.setReorderable(false);
        markcol.setSortable(false);

        affect = new TableColumn<ObservableList<String>, Object>("更新行数");
        affect.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, Integer> affect = new TableColumn<>("Affect Rows");
        affect.setCellValueFactory(new PropertyValueFactory<>("affectedRows"));
        //affect.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(5)));
        affect.setPrefWidth(100);
        affect.setReorderable(false);
        affect.setSortable(false);

        sqlcol = new TableColumn<ObservableList<String>, Object>("执行语句");
        sqlcol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> sqlcol = new TableColumn<>("Execute SQL");
        sqlcol.setCellValueFactory(new PropertyValueFactory<>("updateSql"));
        //sqlcol.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(6)));

        sqlcol.setPrefWidth(300);
        sqlcol.setReorderable(false);
        sqlcol.setSortable(false);

        resultset_total_tableview.getColumns().addAll(resultcol, databasecol, sqlcol, affect, drution, begin, stop, markcol);
        updateResults = FXCollections.observableArrayList();
        UpdateResult updateResult = new UpdateResult();
        updateResult.setResult("执行结果，此处显示执行成功或执行失败");
        updateResult.setDatabase("库名");
        updateResult.setUpdateSql("执行的SQL语句");
        updateResult.setStartTime("SQL开始执行的时间");
        updateResult.setEndTime("SQL完成执行的时间");
        updateResult.setElapsedTime("执行耗时");
        updateResult.setAffectedRows(0);
        //updateResult.setMark(sql_commitmode_choicebox.getValue().toString()+"，"+sql_sqlmode_choicebox.getValue()==null?"":sql_sqlmode_choicebox.getValue()+sql_param_string);
        resultset_total_tableview.setItems(updateResults);


        //停止按钮事件

        sql_stop_button.setOnAction(event -> {
            SQLTask.cancel();
            try {
                if (sql_statement != null) {
                    sql_statement.cancel();
                    sql_statement = null;
                }
            } catch (SQLException e) {
            }
            currentResultSetTabController.cancel();
            for (Tab tab : resultset_tabpane.getTabs()) {
                if (tab instanceof CustomResultsetTab) {
                    ((CustomResultsetTab) tab).resultSetTabController.cancel();
                }
            }


        });

        //运行按钮事件
        sql_run_button.setOnAction(event -> {

            if (sql_connect_choicebox.getValue().getName().equals("请选择数据库连接")) {
                NotificationUtil.showNotification(Main.mainController.notice_pane,"请选择数据库连接！" );
            } else {

                //result_list=new ArrayList<>();
                if (sql_is_refresh) {
                    sql_str = currentResultSetTabController.lastsql_textfield.getTooltip().getText();
                } else {
                    if (sql_edit_codearea.getSelectedText().isEmpty()) {
                        sql_str = sql_edit_codearea.getText();
                    } else {
                        sql_str = sql_edit_codearea.getSelectedText();
                    }
                }

                if (sql_str.isEmpty()) {
                    NotificationUtil.showNotification(Main.mainController.notice_pane,"请输入需要执行的SQL语句！" );
                } else {
                    //执行前取消可能还没完成的任务
                    if (SQLTask != null && SQLTask.isRunning()) {
                        SQLTask.cancel();
                        try {
                            if (sql_statement != null) {
                                sql_statement.cancel();
                                sql_statement = null;
                            }
                        } catch (SQLException e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    //执行前取消正在执行获取主键的任务
                    currentResultSetTabController.cancel();
                    sql_execute_process_stackpane.setVisible(true);
                    SQLTask = createExecuteSqlsTask();
                    closeResultSet(); //关闭结果集在删除结果集表格，避免游标未释放无法关闭连接
                    resultset_tabpane.getTabs().subList(1, resultset_tabpane.getTabs().size()).clear();
                    // 启动任务
                    new Thread(SQLTask).start();
                    if (sql_splitpane.getDividers().get(0).getPosition() > Main.split2Pos) {
                        sql_splitpane.getDividers().get(0).setPosition(Main.split2Pos);
                    }
                }
            }


        });

        //执行计划按钮事件
        sql_explain_button.setOnAction(event -> {
            if (sql_connect_choicebox.getValue().getName().equals("请选择数据库连接")) {
                NotificationUtil.showNotification(Main.mainController.notice_pane,"请选择数据库连接！" );
            } else {
                if (sql_edit_codearea.getSelectedText().isEmpty()) {
                    sql_str = sql_edit_codearea.getText();
                } else {
                    sql_str = sql_edit_codearea.getSelectedText();
                }
                if (sql_str.isEmpty()) {
                    NotificationUtil.showNotification(Main.mainController.notice_pane,"请输入需要查看执行计划的SQL语句！" );
                } else {
                    //执行前取消可能还没完成的任务
                    if (SQLTask != null && SQLTask.isRunning()) {
                        SQLTask.cancel();
                        try {
                            if (sql_statement != null) {
                                sql_statement.cancel();
                                sql_statement = null;
                            }
                        } catch (SQLException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //执行前取消正在执行获取主键的任务
                    currentResultSetTabController.cancel();
                    sql_execute_process_stackpane.setVisible(true);
                    SQLTask = createGetExplainTask();
                    closeResultSet();
                    resultset_tabpane.getTabs().subList(1, resultset_tabpane.getTabs().size()).clear();
                    // 启动任务
                    new Thread(SQLTask).start();
                    if (sql_splitpane.getDividers().get(0).getPosition() > Main.split2Pos) {
                        sql_splitpane.getDividers().get(0).setPosition(Main.split2Pos);
                    }
                }
            }
        });


        //连接变更响应事件
        sql_connect_choicebox.getSelectionModel().selectedItemProperty().addListener(

                (obs, oldVal, newVal) -> {
                    if (newVal == defalutConnect) {
                        SQLConnect.setConn(null);
                        svgPath.setContent("M21.2812 3.3281 Q22.2656 4.3125 22.6094 5.6562 Q22.9688 6.9844 22.6094 8.3281 Q22.2656 9.6562 21.2812 10.6406 L19.4531 12.4688 Q18.4688 13.4531 17.125 13.7812 Q15.7969 14.1094 14.4844 13.7812 L19.4531 8.8125 Q20.2031 8.0625 20.2031 6.9844 Q20.2031 5.9062 19.4531 5.1562 Q18.7031 4.4062 17.625 4.4062 Q16.5469 4.4062 15.7969 5.1562 L10.8281 10.125 Q10.5 8.8125 10.8281 7.4844 Q11.1562 6.1406 12.1406 5.1562 L13.9688 3.3281 Q14.9531 2.3438 16.2812 2 Q17.625 1.6406 18.9531 2 Q20.2969 2.3438 21.2812 3.3281 ZM10.3125 16.125 L16.7344 9.7031 Q17.1094 9.3281 17.1094 8.7969 Q17.1094 8.25 16.7031 7.875 Q16.3125 7.5 15.7656 7.5312 Q15.2344 7.5469 14.8594 7.875 L8.4844 14.2969 Q8.1094 14.6719 8.1094 15.2188 Q8.1094 15.75 8.4844 16.125 Q8.8594 16.5 9.3906 16.4844 Q9.9375 16.4531 10.3125 16.125 ZM9.375 18.8438 L14.3438 13.875 Q14.7188 15.1875 14.3906 16.5312 Q14.0625 17.8594 13.0781 18.8438 L11.25 20.6719 Q10.2188 21.7031 8.9062 22.0312 Q7.5938 22.3594 6.25 22.0312 Q4.9219 21.7031 3.9062 20.7031 Q2.9062 19.6875 2.5469 18.3594 Q2.2031 17.0156 2.5469 15.6875 Q2.9062 14.3438 3.8906 13.3594 L5.7656 11.5312 Q6.75 10.5469 8.0625 10.2188 Q9.375 9.8906 10.7344 10.2188 L5.7656 15.1875 Q4.9688 15.9375 4.9688 17.0156 Q4.9688 18.0938 5.7344 18.875 Q6.5156 19.6406 7.5625 19.6406 Q8.625 19.6406 9.375 18.8438 Z");
                        sql_connect_choicebox_db_icon.setGraphic(svgPath);
                        svgPath.setScaleX(0.6);
                        svgPath.setScaleY(0.6);
                        sql_connect_choicebox_db_icon.setVisible(true);
                        sql_connect_choicebox_loading_icon.setVisible(false);
                        //sql_connect_choicebox_icon.setContent("M21.2812 3.3281 Q22.2656 4.3125 22.6094 5.6562 Q22.9688 6.9844 22.6094 8.3281 Q22.2656 9.6562 21.2812 10.6406 L19.4531 12.4688 Q18.4688 13.4531 17.125 13.7812 Q15.7969 14.1094 14.4844 13.7812 L19.4531 8.8125 Q20.2031 8.0625 20.2031 6.9844 Q20.2031 5.9062 19.4531 5.1562 Q18.7031 4.4062 17.625 4.4062 Q16.5469 4.4062 15.7969 5.1562 L10.8281 10.125 Q10.5 8.8125 10.8281 7.4844 Q11.1562 6.1406 12.1406 5.1562 L13.9688 3.3281 Q14.9531 2.3438 16.2812 2 Q17.625 1.6406 18.9531 2 Q20.2969 2.3438 21.2812 3.3281 ZM10.3125 16.125 L16.7344 9.7031 Q17.1094 9.3281 17.1094 8.7969 Q17.1094 8.25 16.7031 7.875 Q16.3125 7.5 15.7656 7.5312 Q15.2344 7.5469 14.8594 7.875 L8.4844 14.2969 Q8.1094 14.6719 8.1094 15.2188 Q8.1094 15.75 8.4844 16.125 Q8.8594 16.5 9.3906 16.4844 Q9.9375 16.4531 10.3125 16.125 ZM9.375 18.8438 L14.3438 13.875 Q14.7188 15.1875 14.3906 16.5312 Q14.0625 17.8594 13.0781 18.8438 L11.25 20.6719 Q10.2188 21.7031 8.9062 22.0312 Q7.5938 22.3594 6.25 22.0312 Q4.9219 21.7031 3.9062 20.7031 Q2.9062 19.6875 2.5469 18.3594 Q2.2031 17.0156 2.5469 15.6875 Q2.9062 14.3438 3.8906 13.3594 L5.7656 11.5312 Q6.75 10.5469 8.0625 10.2188 Q9.375 9.8906 10.7344 10.2188 L5.7656 15.1875 Q4.9688 15.9375 4.9688 17.0156 Q4.9688 18.0938 5.7344 18.875 Q6.5156 19.6406 7.5625 19.6406 Q8.625 19.6406 9.375 18.8438 Z");
                        sql_db_choicebox.getItems().clear();
                        sql_db_choicebox.setValue(defalutDatabase);
                        sql_user_textfield.setText("N/A");
                        sql_sqlmode_choicebox.setVisible(false);
                        sql_commitmode_choicebox.getSelectionModel().select(0);
                        sql_commitmode_choicebox.setVisible(true);
                        sql_recode_button.setVisible(true);
                        sql_readonly_label.setVisible(false);
                    }
                    //如果是切换其他连接，进入处理，连不上(newVal.equals(SQLConnect)），回到原连接，不进入处理，如果前一个是默认空值，切换到当前值，是重连，进入处理
                    //if ((newVal != null && !newVal.equals(oldVal)&&!newVal.equals(defalutConnect)&&!newVal.equals(SQLConnect))||(newVal.equals(SQLConnect)&&oldVal.equals(defalutConnect))) {
                    else {
                        //boolean allowed = confirmChange(newVal);

                        //在线程中执行，避免切换连接长时间连不上时主界面卡死
                        new Thread(() -> {
                            Platform.runLater(() -> {
                                sql_connect_choicebox_db_icon.setVisible(false);
                                sql_connect_choicebox_loading_icon.setVisible(true);
                            });
                            Connection conn = null;
                            try {
                                conn = connectDBaccessService.getConnection(newVal);
                                connectDBaccessService.changeCommitMode(conn, sql_commitmode_choicebox.getValue().toString());
                            } catch (SQLException e) {
                                log.error(e.getMessage(),e);
                                Platform.runLater(() -> {
                                    AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                                });
                            } catch (Exception e) {
                                log.error(e.getMessage(),e);
                                Platform.runLater(() -> {
                                    AlterUtil.CustomAlert("错误", e.getMessage());
                                });
                            }
                            if (conn != null) {
                                if (SQLConnect.getConn() != null)
                                    try {
                                        closeResultSet();
                                        SQLConnect.getConn().close();
                                    } catch (SQLException e) {
                                        log.error(e.getMessage(), e);
                                    }
                                SQLConnect = newVal;
                                SQLConnect.setConn(conn);
                                //默认结果集面板中的连接同步更改，保证结果集中相关需要连接的操作正常
                                currentResultSetTabController.SQLConnect = SQLConnect;
                                for (TreeItem<TreeData> ti : Main.mainController.databasemeta_treeview.getRoot().getChildren()) {
                                    for (TreeItem<TreeData> t : ti.getChildren()) {
                                        Connect connect = (Connect) t.getValue();
                                        if (t.getValue().getName().equals(SQLConnect.getName())) {
                                            if (connect.getConn() == null) {
                                                t.setExpanded(false);
                                                t.setExpanded(true);
                                            } else {
                                                ResultSet rs = null;
                                                try {
                                                    rs = connect.getConn().createStatement().executeQuery("select first 1 tabid from systables");
                                                } catch (SQLException e) {
                                                    log.error(e.getMessage(),e);
                                                    //如果报错，表示该连接已断开，自动重连
                                                    connect.setConn(null);
                                                    t.setExpanded(false);
                                                    t.setExpanded(true);
                                                } finally {
                                                    if (rs != null) {
                                                        try {
                                                            rs.close();
                                                        } catch (SQLException e) {
                                                            log.error(e.getMessage(),e);
                                                            rs = null;
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }

                                //更换数据库类型图标
                                Platform.runLater(() -> {
                                    if (SQLConnect.getDbtype().equals("GBASE 8S")) {
                                        svgPath.setContent("M194.66509,348.01735h-.00287a5.08422,5.08422,0,0,0-5.06208-5.01688H168.04l.02117,8.74355,17.86467.0072v5.689H159.89729c-.01728,0-.03124.0036-.05527.0036a3.14023,3.14023,0,0,1-3.13816-3.13668v-.00713h-.014V340.521h.014v-.009a3.14519,3.14519,0,0,1,3.13816-3.13844c.038,0,.07967.01323.1184.01323h29.59451l5.089-8.79625s-36.00025-.00433-36.08815,0a11.3304,11.3304,0,0,0-10.73466,11.31421c0,.21932.00647.42674.02227.63812V354.1787c-.00581.206-.02227.407-.02227.62085a11.34988,11.34988,0,0,0,11.353,11.348c.23616,0,1.21231-.0086,1.5066-.0086l28.93911.0036v-.00433a5.08677,5.08677,0,0,0,5.04239-5.07539V361.05h.00287Z");
                                        sql_connect_choicebox_db_icon.setGraphic(new Group(svgPath));
                                        svgPath.setScaleX(0.2);
                                        svgPath.setScaleY(0.2);
                                    } else {
                                        svgPath.setContent("M21.2812 3.3281 Q22.2656 4.3125 22.6094 5.6562 Q22.9688 6.9844 22.6094 8.3281 Q22.2656 9.6562 21.2812 10.6406 L19.4531 12.4688 Q18.4688 13.4531 17.125 13.7812 Q15.7969 14.1094 14.4844 13.7812 L19.4531 8.8125 Q20.2031 8.0625 20.2031 6.9844 Q20.2031 5.9062 19.4531 5.1562 Q18.7031 4.4062 17.625 4.4062 Q16.5469 4.4062 15.7969 5.1562 L10.8281 10.125 Q10.5 8.8125 10.8281 7.4844 Q11.1562 6.1406 12.1406 5.1562 L13.9688 3.3281 Q14.9531 2.3438 16.2812 2 Q17.625 1.6406 18.9531 2 Q20.2969 2.3438 21.2812 3.3281 ZM10.3125 16.125 L16.7344 9.7031 Q17.1094 9.3281 17.1094 8.7969 Q17.1094 8.25 16.7031 7.875 Q16.3125 7.5 15.7656 7.5312 Q15.2344 7.5469 14.8594 7.875 L8.4844 14.2969 Q8.1094 14.6719 8.1094 15.2188 Q8.1094 15.75 8.4844 16.125 Q8.8594 16.5 9.3906 16.4844 Q9.9375 16.4531 10.3125 16.125 ZM9.375 18.8438 L14.3438 13.875 Q14.7188 15.1875 14.3906 16.5312 Q14.0625 17.8594 13.0781 18.8438 L11.25 20.6719 Q10.2188 21.7031 8.9062 22.0312 Q7.5938 22.3594 6.25 22.0312 Q4.9219 21.7031 3.9062 20.7031 Q2.9062 19.6875 2.5469 18.3594 Q2.2031 17.0156 2.5469 15.6875 Q2.9062 14.3438 3.8906 13.3594 L5.7656 11.5312 Q6.75 10.5469 8.0625 10.2188 Q9.375 9.8906 10.7344 10.2188 L5.7656 15.1875 Q4.9688 15.9375 4.9688 17.0156 Q4.9688 18.0938 5.7344 18.875 Q6.5156 19.6406 7.5625 19.6406 Q8.625 19.6406 9.375 18.8438 Z");
                                        sql_connect_choicebox_db_icon.setGraphic(new Group(svgPath));
                                        svgPath.setScaleX(0.6);
                                        svgPath.setScaleY(0.6);
                                    }

                                    if (SQLConnect.getReadonly()) {
                                        sql_recode_button.setVisible(false);
                                        sql_commitmode_choicebox.setVisible(false);
                                        sql_readonly_label.setVisible(true);
                                    } else {
                                        sql_recode_button.setVisible(true);
                                        sql_commitmode_choicebox.setVisible(true);
                                        sql_readonly_label.setVisible(false);
                                    }
                                });
                                //更新数据库

                                List db_names = connectDBaccessService.getDatabases(SQLConnect);
                                database_choicebox_list = FXCollections.observableArrayList(db_names);
                                Platform.runLater(() -> {
                                    sql_db_choicebox.setValue(defalutDatabase); //设置一个默认值，避免连接不同但库名相同不触发激活数据库
                                    sql_db_choicebox.setItems(database_choicebox_list);
                                    int i = 0;
                                    for (Database item : database_choicebox_list) {
                                        if (item.getName().equals(SQLConnect.getDatabase())) {
                                            sql_db_choicebox.getSelectionModel().select(i);
                                            break;
                                        }
                                        i++;
                                    }
                                    sql_user_textfield.setText(SQLConnect.getUsername());
                                    sql_connect_choicebox_db_icon.setVisible(true);
                                    sql_connect_choicebox_loading_icon.setVisible(false);
                                });
                            } else {
                                // 延迟恢复旧值，避免在监听器里直接修改导致冲突
                                if (oldVal == defalutConnect)
                                    Platform.runLater(() -> sql_connect_choicebox.setValue(defalutConnect));
                                else
                                    Platform.runLater(() -> sql_connect_choicebox.setValue(SQLConnect));
                            }
                        }).start();

                    }
                });

        //数据库变更响应事件
        sql_db_choicebox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue) && !newValue.equals(defalutDatabase)) {
                String result = "success";
                //!oldValue.equals(defalutDatabase)表示重连，不触发激活操作，避免出现激活时关闭sql_result导致如果自动重做sql任务报结果集已关闭错误
                if (oldValue != null && !oldValue.equals(defalutDatabase)) {
                    result = connectDBaccessService.activeDatabase(SQLConnect, newValue, this);
                }
                if (result == null) { //如果是空值，切换失败，设置为原值
                    Platform.runLater(() -> sql_db_choicebox.setValue(oldValue));
                } else if (result.equals("success")) {
                    List sqlmodes = connectDBaccessService.getSqlMode(SQLConnect.getConn());
                    //String sqlmode=connectDBaccessService.getSqlMode(SQLConnect.getConn());
                    if (sqlmodes.get(0).equals("sqlmode=none")) {
                        Platform.runLater(() -> {
                            sql_sqlmode_choicebox.setVisible(false);
                        });
                    }
                    //data_manager_sqlmode_button.setText(connectDBaccessService.getSqlMode(SQLConnect.getConn()));
                    else {
                        Platform.runLater(() -> {
                            sql_sqlmode_choicebox.setVisible(true);
                            sql_sqlmode_choicebox.getItems().clear();  //此处会触发sqlmode变为null事件
                            sql_sqlmode_choicebox.getItems().add("sqlmode=gbase");
                            sql_sqlmode_choicebox.getItems().addAll(sqlmodes.subList(1, sqlmodes.size()));
                            for (String item : sql_sqlmode_choicebox.getItems()) {
                                if (item.equals(sqlmodes.get(0))) {
                                    sql_sqlmode_choicebox.setValue(item); // 选中匹配项
                                    break; // 找到后退出循环
                                }
                            }

                        });
                    }
                    //如果是sql_init不是空，表示当前窗口是拖动表或视图在此显示新建，需要执行查询sql
                    if (!sql_init.isEmpty()) {
                        Platform.runLater(() -> {
                            sql_edit_codearea.appendText(sql_init);
                            sql_edit_codearea.selectRange(0, sql_edit_codearea.getLength());
                            sql_run_button.fire();
                            sql_init = "";
                        });

                    }
                } else if (result.equals("disconnected")) {
                    connectionDisconnected();
                }
            }

        });

        //提交模式变更事件
        sql_commitmode_choicebox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //切换自动提交和手动提交不受连接是否已中断影响
            if (SQLConnect.getConn() != null) {
                try {
                    SQLConnect.getConn().setAutoCommit(newValue.equals("自动提交"));
                } catch (SQLException e) {
                    log.error(e.getMessage(),e);
                    if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                        connectionDisconnected();
                    } else {
                        Platform.runLater(() -> {
                            sql_commitmode_choicebox.getSelectionModel().select(oldValue);
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                            //如果是提示不支持事务，切回自动提交
                            //if(e.getErrorCode()==-79744){
                            //     sql_commitmode_choicebox.getSelectionModel().select(0);
                            //}
                        });
                    }
                }
            } else {
            }
        });

        //变更历史记录
        sql_recode_button.setOnAction(envent -> {
            PopupWindowUtil.openSqlHistoryPopupWindow(SQLConnect.getId());
        });

        //事务未提交面板事件
        trans_commit_button.setOnAction(event -> {
            int start = sql_edit_codearea.getLength();
            sql_edit_codearea.appendText("\ncommit;");
            sql_edit_codearea.moveTo(start);
            sql_edit_codearea.requestFollowCaret();
            sql_edit_codearea.selectRange(sql_edit_codearea.getLength() - "commit;".length(), sql_edit_codearea.getLength());
            sql_run_button.fire();

        });
        trans_rollback_button.setOnAction(event -> {
            int start = sql_edit_codearea.getLength();
            sql_edit_codearea.appendText("\nrollback;");
            sql_edit_codearea.moveTo(start);
            sql_edit_codearea.requestFollowCaret();
            sql_edit_codearea.selectRange(sql_edit_codearea.getLength() - "rollback;".length(), sql_edit_codearea.getLength());
            sql_run_button.fire();
        });

        //事务未提交警告面板告警动画
        /*
        Timeline trans_warning_button_timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e ->
                        trans_warning_button.setVisible(true)
                ),
                new KeyFrame(Duration.seconds(1), e ->
                        trans_warning_button.setVisible(false)
                )
        );
        trans_warning_button_timeline.setCycleCount(Timeline.INDEFINITE);
        trans_warning_button_timeline.play();





        Timeline blink = new Timeline(
                new KeyFrame(Duration.ZERO, e -> trans_commit_button.setStyle("-fx-border-color: red;")),
                new KeyFrame(Duration.ZERO, e -> trans_rollback_button.setStyle("-fx-border-color: #2871a8;")),
                new KeyFrame(Duration.seconds(0.5), e -> trans_rollback_button.setStyle("-fx-border-color: red;")),
                new KeyFrame(Duration.seconds(0.5), e -> trans_commit_button.setStyle("-fx-border-color: #2871a8;"))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
*/
        //sqlmode改变事件
        sql_sqlmode_choicebox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (!SQLTask.isRunning() && newValue != null) {
                SQLTask = createSqlmodeTask(SQLConnect, newValue);
                new Thread(SQLTask).start();
            }
        });

        //进度条
        //执行过程面板动画

        final Double[] secondsElapsed = {0.0};
        Timeline taskTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event1 -> {
            secondsElapsed[0] += 0.1;
            sql_execute_timeInfo.setText("耗时 " + Math.round(secondsElapsed[0] * 10.0) / 10.0 + " 秒");
        }));
        taskTimeline.setCycleCount(Timeline.INDEFINITE);


        //进度面板显示监听事件
        sql_execute_process_stackpane.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    sql_execute_taskInfo.setText(" 正在执行...");
                    secondsElapsed[0] = 0.0;
                    sql_execute_timeInfo.setText("耗时 " + Math.round(secondsElapsed[0] * 10.0) / 10.0 + " 秒");
                    taskTimeline.playFromStart();
                } else {
                    taskTimeline.stop();
                    secondsElapsed[0] = 0.0;
                    sql_execute_timeInfo.setText("耗时 " + Math.round(secondsElapsed[0] * 10.0) / 10.0 + " 秒");
                }
            }
        });

        //sql_edit_codearea初始化以后才能赋值给customSearchReplaceVbox，否则sql_edit_codearea是null
        customSearchReplaceVbox.codeArea = sql_edit_codearea;


    }


    //连接断开处理
    public void connectionDisconnected() {
        Platform.runLater(() -> {
            sql_trans.set("");
            //trans_hbox.setVisible(false);
            if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                //嵌套Platform保证前一步完成后执行下一步，避免渲染延迟导致前后顺序错误
                Platform.runLater(() -> {
                    sql_connect_choicebox.setValue(defalutConnect);
                    Platform.runLater(() -> {
                        sql_connect_choicebox.setValue(SQLConnect);
                        Platform.runLater(() -> {
                            if (connectDBaccessService.testConn(SQLConnect)) {
                                NotificationUtil.showNotification(Main.mainController.notice_pane, "重连数据库成功！");
                                //    data_manager_sql_run_button.fire();
                            }
                        });
                    });
                });
            } else {
                sql_connect_choicebox.setValue(defalutConnect);
            }
        });
    }

    public void closeResultSet() {
        try {
            if (currentResultSetTabController.sql_resultset != null) {

                //如果连接已断开，mysql r28 r61驱动在这里会卡死，结果集关闭有问题,重现方式未双击打开sql面板，执行一次select后killsession再次执行会卡死
                currentResultSetTabController.sql_resultset.close();

            }
            if (currentResultSetTabController.pri_sql_result != null) {
                currentResultSetTabController.pri_sql_result.close();

            }
            for (Tab tab : resultset_tabpane.getTabs()) {
                if (tab instanceof CustomResultsetTab) {
                    currentResultSetTabController.closeResultSet();
                    ((CustomResultsetTab) tab).resultSetTabController.closeResultSet();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
        }
    }


    public void closeConn() {
        try {
            if (!sql_stop_button.isDisable()) {
                sql_stop_button.fire();
            }
            closeResultSet();
            if (SQLConnect.getConn() != null && !SQLConnect.getConn().isClosed()) {
                SQLConnect.getConn().close();  //如果延迟高，如50ms以上，关闭连接可能会需要2-3秒
                SQLConnect = defalutConnect;  //恢复SQLConnect为初始值，避免连接断开后窗口切换连接失败自动连接到刚断开的连接
                sql_connect_choicebox.setValue(defalutConnect);
                sql_sqlmode_choicebox.setVisible(false);
                sql_trans.set("");
                //trans_hbox.setVisible(false);
                database_choicebox_list.clear();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
        }
    }

    public Task<Void> createExecuteSqlsTask() {
        if (sql_edit_codearea.getSelectedText().isEmpty()) {
            sql_select_pos[0] = 0;
            sql_select_pos[1] = 0;
        } else {
            sql_select_pos[0] = sql_edit_codearea.getSelection().getStart();
            sql_select_pos[1] = sql_edit_codearea.getSelection().getEnd();
        }

        sql_used_time = 0;
        onlyOneSql = true;
        Platform.runLater(() -> {
            updateResults.clear();
        });
        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                sql_exe_result = "执行成功";
                updateMessage("Executing... ");

                boolean inSingleQuote = false;  // 是否在单引号中
                boolean inDoubleQuote = false;  // 是否在双引号中
                boolean inLineComment = false;  // 是否在单行注释中
                boolean inBlockComment = false; // 是否在多行注释中
                boolean inBrackets = false;     // 是否在一层大括号中

                SqlparserService sqlparser = new SqlparserService();
                Sql sql = new Sql();
                String add_sql = "";
                sql_num = 0;
                for (int i = 0; i < sql_str.length(); i++) {
                    if (isCancelled()) {
                        break;
                    }

                    char current = sql_str.charAt(i);
                    char next = (i + 1 < sql_str.length()) ? sql_str.charAt(i + 1) : '\0';
                    add_sql += current;

                    // 检查是否进入/退出单行注释
                    if (!inSingleQuote && !inDoubleQuote && !inBlockComment && !inBrackets) {
                        if (!inLineComment && current == '-' && next == '-') {
                            inLineComment = true;
                            add_sql += next;
                            i++; // 跳过下一个字符
                        } else if (inLineComment && current == '\n') {
                            inLineComment = false;
                        }
                    }

                    // 检查是否进入/退出多行注释
                    if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBrackets) {
                        if (!inBlockComment && current == '/' && next == '*') {
                            inBlockComment = true;
                            add_sql += next;
                            i++; // 跳过下一个字符
                        } else if (inBlockComment && current == '*' && next == '/') {
                            inBlockComment = false;
                            add_sql += next;
                            i++; // 跳过下一个字符
                        }
                    }


                    // 检查是否进入/退出单引号
                    if (!inDoubleQuote && !inLineComment && !inBlockComment && !inBrackets && current == '\'') {
                        inSingleQuote = !inSingleQuote;
                    }

                    // 检查是否进入/退出双引号
                    if (!inSingleQuote && !inLineComment && !inBlockComment && !inBrackets && current == '\"') {
                        inDoubleQuote = !inDoubleQuote;
                    }

                    // 检查是否进入/退出大括号
                    if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment) {
                        if (current == '{') {
                            inBrackets = true;
                        } else if (current == '}') {
                            inBrackets = false;
                        }
                    }
                    // 检查非注释、非引号、非大括号的分号,最后一个字符或者为";",sql语句结束
                    if (i == sql_str.length() - 1 || (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment && !inBrackets && current == ';')) {
                        //判断是否只有一条sql
                        if (onlyOneSql && !add_sql.equals(sql_str)) {
                            for (int j = i + 1; j < sql_str.length(); j++) {
                                //判断分号后面是否为空白，遇到非空白表示还有其他sql语句
                                if (sql_str.charAt(j) != ' ' && sql_str.charAt(j) != '\t' && sql_str.charAt(j) != '\n') {
                                    onlyOneSql = false;
                                    break;
                                }
                            }
                        }
                        //如果sql包含多个提交符号/，也不是单一sql
                        if (sqlparser.sqlContrainMoreThanOneCommit(add_sql)) {
                            onlyOneSql = false;
                        }


                        //将拼接好的sql进行处理，如果是存储过程函数等和上一条sql拼接，sql的标志isend为true时，表示完整sql，开始执行
                        Boolean sql_contrain_commit = false; //根据当前sql是否包含提交符号/来判断是否需要切分循环执行，如以下sql没有分号实际上是多条sql
                        /*
                             CREATE OR REPLACE PACKAGE EMPLOYEE_MANAGER AS
                                end
                                 /
                                  CREATE OR REPLACE PACKAGE EMPLOYEE_MANAGER AS
                                end
                                 /
                                  CREATE OR REPLACE PACKAGE EMPLOYEE_MANAGER AS
                                end
                                 /
                                  CREATE OR REPLACE PACKAGE EMPLOYEE_MANAGER AS
                                end
                                 /
                         */
                        //执行完一个提交后，被/切分的后半部分，放入sql.remainder，在modifySql中实现，下次执行
                        do {
                            sql = sqlparser.modifySql(sql, add_sql);
                            //如果sql标记为已结束，开始执行
                            if (sql.getSqlEnd() && !sql.getSqlstr().trim().isEmpty()) {
                                sql_param_list.clear();
                                sql_exe = sql.getSqlstr();
                                sql_num++;
                                int finalI = i;
                                String finalsql_exe = sql_exe;
                                if (sql.getSqlRemainder() != null && !sql.getSqlRemainder().isEmpty()) {
                                    final int remaindersize = sql.getSqlRemainder().length();
                                    final int sqllength = finalsql_exe.length();
                                    final int whitespacelength = getWhitespaceLength(finalsql_exe);
                                    if (!sql_is_refresh) {
                                        Platform.runLater(() -> {
                                            sql_edit_codearea.selectRange(sql_select_pos[0] + finalI + 1 - remaindersize - sqllength + whitespacelength, sql_select_pos[0] + finalI + 1 - remaindersize);
                                            sql_edit_codearea.requestFollowCaret();
                                        });
                                    }
                                } else {
                                    final int sqllength = finalsql_exe.length();
                                    final int whitespacelength = getWhitespaceLength(finalsql_exe);
                                    if (!sql_is_refresh) {
                                        Platform.runLater(() -> {
                                            sql_edit_codearea.selectRange(sql_select_pos[0] + finalI + 1 - sqllength + whitespacelength, sql_select_pos[0] + finalI + 1);
                                            sql_edit_codearea.requestFollowCaret();
                                        });
                                    }
                                }


                                updateResult = new UpdateResult();
                                //执行select
                                if (onlyOneSql && (sql.getSqlType().equals("SELECT"))) {
                                    try {
                                        sql_start_time = System.currentTimeMillis();
                                        //if(sql.getSqlType().equals("SELECT")) {
                                        //sql_trans,sql_commitmode_choicebox传入用于结果集编辑更新事务sql及更新结果备注
                                        sql_used_time = currentResultSetTabController.executeSelect(sql_exe, SQLTask, true, sql_trans, sql_commitmode_choicebox);
                                        Platform.runLater(() -> {
                                            resultset_vbox.setVisible(true);
                                        });
                                        //}
                                        /* 执行单个存储过程，已取消单独显示
                                        else if(SQLConnect.getReadonly()){
                                                cancel();
                                                Platform.runLater(() -> {
                                                    AlterUtil.CustomAlert("错误","当前连接只读，只允许执行单条SELECT语句！");
                                                });
                                        }else {

                                            sql_used_time=currentResultSetTabController.executeCall(sql_exe, SQLTask, true);
                                            if(currentResultSetTabController.callHasResultset) {
                                                Platform.runLater(() -> {
                                                    resultset_vbox.setVisible(true);
                                                });
                                            }else{
                                                Platform.runLater(() -> {
                                                    resultset_vbox.setVisible(false);
                                                });
                                            }
                                        }
                                        */
                                    } catch (SQLException e) {
                                        //关闭sql_statement，避免下次执行报213错误，如绑定变量点击了取消场景
                                        log.error(e.getMessage(), e);
                                        Platform.runLater(() -> {
                                            resultset_vbox.setVisible(false);
                                        });
                                        sql_exe_result = "[" + e.getErrorCode() + "]" + e.getMessage();
                                        if (e.getErrorCode() == -254) {  //如果没有设置绑定变量参数，需要关闭sql_statement，否则下次执行会报213
                                            try {
                                                if (currentResultSetTabController.sql_statement != null)
                                                    currentResultSetTabController.sql_statement.close();
                                                if (currentResultSetTabController.sql_cstmt != null)
                                                    currentResultSetTabController.sql_cstmt.close();
                                            } catch (SQLException ex) {
                                                log.error(ex.getMessage(),ex);
                                            }
                                        }
                                        //创建结果表显示错误信息
                                        Platform.runLater(() -> {
                                            if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                                                connectionDisconnected();
                                            } else {
                                                AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                                            }
                                        });
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                    } finally {
                                        sql_param_list = currentResultSetTabController.sql_param_list;
                                    }
                                    sql_end_time = System.currentTimeMillis();
                                    //sql_used_time = sql_end_time - sql_start_time;
                                    sql_total_time += sql_used_time;
                                    updateResult.setResult(sql_exe_result);
                                    updateResult.setDatabase(SQLConnect.getDatabase());
                                    updateResult.setUpdateSql(sql_exe.trim());
                                    updateResult.setStartTime(sdf.format(sql_start_time));
                                    updateResult.setEndTime(sdf.format(sql_end_time));
                                    updateResult.setElapsedTime(String.format("%.3f", sql_used_time / 1000.0) + " sec");
                                    updateResult.setAffectedRows(0);
                                    updateResult.setMark(sql_commitmode_choicebox.getValue().toString() + (!sql_sqlmode_choicebox.isVisible() ? "" : "，" + sql_sqlmode_choicebox.getValue()) + (sql_param_list.isEmpty() ? "" : "，参数" + sql_param_list));
                                    Platform.runLater(() -> {
                                        updateResults.clear();
                                        updateResults.add(updateResult);
                                    });
                                }

                                //如果不是一条select sql，且为readonly，报错
                                else if (SQLConnect.getReadonly()) {
                                    cancel();
                                    sql_exe_result = "执行失败";
                                    Platform.runLater(() -> {
                                        AlterUtil.CustomAlert("错误", "当前连接只读，只允许执行单条SELECT语句！");
                                    });
                                }
                                //如果以上都不是，那就是单条更新或多条语句
                                else {
                                    resultset_vbox.setVisible(false);
                                    explain_result_stackpane.setVisible(false);
                                    sql_start_time = System.currentTimeMillis();
                                    sql_used_time = 0;
                                    sql_affect = 0;
                                    sql_exe_result = "执行成功";
                                    updateResult.setDatabase(SQLConnect.getDatabase());
                                    //开始执行update
                                    if (sql.getSqlType().equals("SELECT") || sql.getSqlType().equals("CALL")) {
                                        customResultsetTab = new CustomResultsetTab(SQLConnect, sql_execute_process_stackpane);
                                        sql_start_time = System.currentTimeMillis();
                                        try {
                                            if (sql.getSqlType().equals("SELECT")) {
                                                sql_used_time = customResultsetTab.resultSetTabController.executeSelect(sql_exe, SQLTask, false, null, null);
                                            } else {
                                                sql_used_time = customResultsetTab.resultSetTabController.executeCall(sql_exe, SQLTask, false);
                                            }
                                        } catch (SQLException e) {
                                            log.error(e.getMessage(), e);
                                            sql_exe_result = "[" + e.getErrorCode() + "]" + e.getMessage();
                                        } finally {
                                            sql_param_list = customResultsetTab.resultSetTabController.sql_param_list;
                                        }
                                        sql_end_time = System.currentTimeMillis();
                                        CountDownLatch latch = new CountDownLatch(1);
                                        if (sql_exe_result.equals("执行成功") && ((sql.getSqlType().equals("SELECT") || customResultsetTab.resultSetTabController.callHasResultset))) {
                                            Platform.runLater(() -> {
                                                String baseTitle = "结果集";
                                                String tabName;
                                                int nextNumber = 1;
                                                // 查找可用的Tab名称
                                                while (true) {
                                                    newResultsetTabName = baseTitle + nextNumber;
                                                    boolean exists = resultset_tabpane.getTabs().stream()
                                                            .anyMatch(tab -> tab.getText().equals(newResultsetTabName));
                                                    if (!exists) {
                                                        break;
                                                    }
                                                    nextNumber++;
                                                }
                                                sql_exe_result += ("，" + newResultsetTabName);
                                                customResultsetTab.setText(newResultsetTabName);
                                                resultset_tabpane.getTabs().add(customResultsetTab);
                                                latch.countDown();
                                            });
                                        } else {
                                            latch.countDown();
                                        }
                                        try {
                                            latch.await(); // 等 UI 更新完成再继续下一轮
                                        } catch (InterruptedException e) {

                                        }

                                    } else {
                                        try {
                                            sql_statement = SQLConnect.getConn().prepareStatement(sql_exe);
                                            paramMetaData = sql_statement.getParameterMetaData();
                                            int paramCount = paramMetaData.getParameterCount();
                                            if (paramCount > 0) {
                                                CountDownLatch latch = new CountDownLatch(1);
                                                Platform.runLater(() -> {
                                                    //sql_param_list.clear();;
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
                                                        sql_statement.setObject(z, (sql_param_list.get(z - 1)));
                                                    }
                                                }
                                                //System.out.println(sql_param_string);
                                            }
                                            sql_affect = sql_statement.executeUpdate();
                                            sql_exe_result = "执行成功";
                                        } catch (SQLException e) {
                                            log.error(e.getMessage(), e);
                                            //如果是执行单条sql，弹出报错信息，如果是批量，不弹出。
                                            if (onlyOneSql) {
                                                Platform.runLater(() -> {
                                                    if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                                                        connectionDisconnected();
                                                    } else if (e.getErrorCode() == -329 || e.getErrorCode() == -23197 || e.getErrorCode() == -349) {
                                                        try {
                                                            sql_statement = SQLConnect.getConn().prepareStatement("database " + SQLConnect.getDatabase());
                                                            sql_statement.executeUpdate();
                                                        } catch (SQLException ex) {
                                                            ex.printStackTrace();
                                                        }
                                                        AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                                                    } else {
                                                        AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                                                    }
                                                });
                                            }
                                            sql_exe_result = "[" + e.getErrorCode() + "]" + e.getMessage();
                                        }
                                        sql_end_time = System.currentTimeMillis();
                                    }

                                    if (sql.getSqlType().equals("SELECT") || sql.getSqlType().equals("CALL")) {
                                    } else {
                                        sql_used_time = sql_end_time - sql_start_time;
                                    }
                                    sql_total_time += sql_used_time;
                                    updateResult.setResult(sql_exe_result);
                                    updateResult.setConnectId(SQLConnect.getId());
                                    updateResult.setStartTime(sdf.format(sql_start_time));
                                    updateResult.setEndTime(sdf.format(sql_end_time));
                                    updateResult.setElapsedTime(String.format("%.3f", sql_used_time / 1000.0) + " sec");
                                    updateResult.setAffectedRows(sql_affect);
                                    updateResult.setUpdateSql(sql_exe.trim());
                                    updateResult.setMark(sql_commitmode_choicebox.getValue().toString() + (!sql_sqlmode_choicebox.isVisible() ? "" : "，" + sql_sqlmode_choicebox.getValue()) + (sql_param_list.isEmpty() ? "" : "，参数" + sql_param_list));

                                    //updateResults.add(updateResult);
                                    CountDownLatch latch = new CountDownLatch(1);

                                    //Platform.runLater可能因为脚本循环太快输出错乱
                                    Platform.runLater(() -> {
                                        updateResults.add(updateResult);
                                        latch.countDown();
                                    });
                                    try {
                                        latch.await(); // 等 UI 更新完成再继续下一轮
                                    } catch (InterruptedException e) {

                                    }
                                }

                                if (sql_exe_result.startsWith("执行成功")) {
                                    //执行记录保存
                                    if (!sql.getSqlType().equals("SELECT")) {
                                        SqliteDBaccessUtil.saveSqlHistory(updateResult);

                                        //如果的切库或建库语句，改变当前连接的库
                                        if (sql.getSqlType().startsWith("DATABASE")) {
                                            SQLConnect.setDatabase(sql.getSqlType().split(" ")[1]);
                                            Platform.runLater(() -> {
                                                Database db = new Database();
                                                db.setName(SQLConnect.getDatabase());
                                                sql_db_choicebox.setValue(db);
                                                //ConnectTreeViewUtil.refreshCurrentDatabase(Main.mainController.connect_list_treeview,SQLConnect);
                                            });
                                        }

                                        //判断是否为改变sql模式语句，如果是，改变sqlmode按钮文字
                                        if (sql_exe.toLowerCase().trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").startsWith("setenvironmentsqlmode'oracle'")) {
                                            Platform.runLater(() -> {
                                                sql_sqlmode_choicebox.setValue("sqlmode=oracle");
                                            });
                                        } else if (sql_exe.toLowerCase().trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").startsWith("setenvironmentsqlmode'gbase'")) {
                                            Platform.runLater(() -> {
                                                sql_sqlmode_choicebox.setValue("sqlmode=gbase");
                                            });
                                        } else if (sql_exe.toLowerCase().trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").startsWith("setenvironmentsqlmode'mysql'")) {
                                            if (sql_sqlmode_choicebox.getItems().contains("sqlmode=mysql")) {
                                                Platform.runLater(() -> {
                                                    sql_sqlmode_choicebox.setValue("sqlmode=mysql");
                                                });
                                            }

                                        }

                                        //判断是否在事务中未提交
                                        if (SQLConnect.getConn().getAutoCommit()) { //自动提交场景
                                            if (sql_exe.toUpperCase().trim().startsWith("BEGIN")) {
                                                if (sql_exe.endsWith(";")) {
                                                    sql_trans.set(sql_exe.trim() + "\n");
                                                } else {
                                                    sql_trans.set(sql_exe.trim() + ";\n");
                                                }
                                            } else if (sql_exe.toUpperCase().trim().startsWith("COMMIT") || sql_exe.toUpperCase().trim().startsWith("ROLLBACK")) {
                                                sql_trans.set("");
                                            } else if (!sql_trans.get().equals("")) {
                                                if (sql_exe.endsWith(";")) {
                                                    sql_trans.set(sql_trans.get() + sql_exe.trim() + "\n");
                                                } else {
                                                    sql_trans.set(sql_trans.get() + sql_exe.trim() + ";\n");
                                                }
                                            }
                                        } else { //手动提交场景
                                        /*
                                        if (sql_trans.get().equals("")) {
                                            sql_trans.set("begin work;\n");
                                        }
                                         */
                                            if (sql_exe.endsWith(";")) {
                                                sql_trans.set(sql_trans.get() + sql_exe.trim() + "\n");
                                            } else {
                                                sql_trans.set(sql_trans.get() + sql_exe.trim() + ";\n");
                                            }
                                            if (sql_exe.toUpperCase().trim().startsWith("COMMIT") || sql_exe.toUpperCase().trim().startsWith("ROLLBACK")) {
                                                sql_trans.set("");
                                            }
                                            //System.out.println("sql_trans:"+sql_trans);

                                        }
                                    }


                                }


                                //sql执行完恢复初始状态

                                sql.setSqlStr("");
                                sql.setSqlEnd(false);
                                sql.setSqlType("");
                            }
                            //如果上一个以分号分隔的语句包含包的最后一句和下一个包的第一句，包的最后一句可能没有分号，那么分隔的两句后一句放入remainder作为下一个sql的开始
                            add_sql = "";
                            sql_contrain_commit = sqlparser.sqlContrainCommit(sql.getSqlRemainder());
                        } while (sql_contrain_commit);
                    }

                }

                return null;
            }

        };

        // 任务完成时关闭进度窗口

        //所有任务都会成功，没有在线程中抛出错误表示失败，抛出错误后线程不再执行后续sql？
        SQLTask.setOnSucceeded(event1 -> {
            //sql_execute_process_hbox显示可能会导致下层tableview列拖动卡死,已在外层添加一个stackpane
            sql_is_refresh = false;
            Platform.runLater(() -> {
                sql_execute_process_stackpane.setVisible(false);
                explain_result_stackpane.setVisible(false);
                sql_edit_codearea.selectRange(sql_select_pos[0], sql_select_pos[1]);
            });


            //检查主键或rowid，表格是select而且当前不在事务中才检查主键并启用编辑
            //if(onlyOneSql&&resultset_vbox.isVisible()&&sql_trans.get().equals("")) {
            if (onlyOneSql && resultset_vbox.isVisible()) {
                currentResultSetTabController.getPrimaryKeys(sql_exe);
            }
            //检查主键结束

        });
        SQLTask.setOnCancelled(event1 -> {
            sql_is_refresh = false;

            //sql_execute_process_hbox显示可能会导致下层tableview列拖动卡死,已在外层添加一个stackpane
            Platform.runLater(() -> {
                sql_execute_process_stackpane.setVisible(false);
                explain_result_stackpane.setVisible(false);
                sql_edit_codearea.selectRange(sql_select_pos[0], sql_select_pos[1]);
            });

        });

        SQLTask.setOnFailed(event1 -> {
            sql_is_refresh = false;

        });

        return SQLTask;
    }


    public Task<Void> createGetExplainTask() {
        sql_execute_process_stackpane.setVisible(true);
        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {

                boolean onlyOneSql = true;  //判断是否只有一条sql，如果是，select要执行并弹出报错信息，update要弹出报错
                boolean inSingleQuote = false;  // 是否在单引号中
                boolean inDoubleQuote = false;  // 是否在双引号中
                boolean inLineComment = false;  // 是否在单行注释中
                boolean inBlockComment = false; // 是否在多行注释中
                boolean inBrackets = false;     // 是否在一层大括号中
                String add_sql = "";
                for (int i = 0; i < sql_str.length(); i++) {
                    if (isCancelled()) {
                        break;
                    }

                    char current = sql_str.charAt(i);
                    char next = (i + 1 < sql_str.length()) ? sql_str.charAt(i + 1) : '\0';

                    // 检查是否进入/退出单行注释
                    if (!inSingleQuote && !inDoubleQuote && !inBlockComment && !inBrackets) {
                        if (!inLineComment && current == '-' && next == '-') {
                            inLineComment = true;
                            add_sql += next;
                            i++; // 跳过下一个字符
                        } else if (inLineComment && current == '\n') {
                            inLineComment = false;
                        }
                    }

                    // 检查是否进入/退出多行注释
                    if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBrackets) {
                        if (!inBlockComment && current == '/' && next == '*') {
                            inBlockComment = true;
                            add_sql += next;
                            i++; // 跳过下一个字符
                        } else if (inBlockComment && current == '*' && next == '/') {
                            inBlockComment = false;
                            add_sql += next;
                            i++; // 跳过下一个字符
                        }
                    }


                    // 检查是否进入/退出单引号
                    if (!inDoubleQuote && !inLineComment && !inBlockComment && !inBrackets && current == '\'') {
                        inSingleQuote = !inSingleQuote;
                    }

                    // 检查是否进入/退出双引号
                    if (!inSingleQuote && !inLineComment && !inBlockComment && !inBrackets && current == '\"') {
                        inDoubleQuote = !inDoubleQuote;
                    }

                    // 检查是否进入/退出大括号
                    if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment) {
                        if (current == '{') {
                            inBrackets = true;
                        } else if (current == '}') {
                            inBrackets = false;
                        }
                    }
                    // 检查非注释、非引号、非大括号的分号
                    if (i == sql_str.length() - 1 || (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment && !inBrackets && current == ';')) {
                        //System.out.println("checked sql end in");
                        //判断是否只有一条sql
                        if (onlyOneSql && !add_sql.equals(sql_str)) {
                            for (int j = i + 1; j < sql_str.length(); j++) {
                                //System.out.println("i is:"+i);
                                //System.out.println("j is:"+j);
                                if (sql_str.charAt(j) != ' ' && sql_str.charAt(j) != '\t' && sql_str.charAt(j) != '\n') {
                                    onlyOneSql = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!onlyOneSql) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", "只允许单条SQL查看执行计划，请选择单条SQL语句！");
                    });

                } else {

                    try {
                        //updateMessage("Fetching... ");
                        //游标滚动到上次取数的位置
                        //sql_result.absolute(sql_fetched_rows);
                        sql_statement = SQLConnect.getConn().prepareStatement("execute function ifx_explain(?)");
                        sql_statement.setObject(1, sql_str);
                        ResultSet rs = sql_statement.executeQuery();
                        rs.next();
                        Platform.runLater(() -> {
                            try {
                                if (rs.getString(1) != null) {
                                    if (rs.getString(1).equals("Error 0")) {
                                        AlterUtil.CustomAlert("错误", "DDL或语法错误SQL不支持查看执行计划！请检查SQL类型或SQL语法是否存在错误！");
                                    } else {
                                        resultset_vbox.setVisible(false);
                                        explain_result_stackpane.setVisible(true);
                                        explain_result_stackpane.codeArea.replaceText(rs.getString(1));
                                        rs.close();
                                    }
                                } else {
                                    AlterUtil.CustomAlert("错误", "DDL或语法错误SQL不支持查看执行计划！请检查SQL类型或SQL语法是否存在错误！");
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }


                        });
                    } catch (SQLException e) {
                        if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                            connectionDisconnected();
                        } else {
                            Platform.runLater(() -> {
                                AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                            });
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                return null;
            }

        };

        SQLTask.setOnSucceeded(event1 -> {
            sql_execute_process_stackpane.setVisible(false);
        });
        SQLTask.setOnCancelled(event1 -> {
            sql_execute_process_stackpane.setVisible(false);
        });
        SQLTask.setOnFailed(event1 -> {
            sql_execute_process_stackpane.setVisible(false);
        });


        return SQLTask;
    }

    public Task<Void> createSqlmodeTask(Connect SQLConnect, String sqlmode) {
        String sql = "";
        if (sqlmode.equals("sqlmode=gbase")) {
            sql = "set environment sqlmode 'gbase'";
        } else if (sqlmode.equals("sqlmode=oracle")) {
            sql = "set environment sqlmode 'oracle'";
        } else {
            sql = "set environment sqlmode 'mysql'";
        }
        String finalSql = sql;
        SQLTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    sql_statement = SQLConnect.getConn().prepareStatement(finalSql);
                    sql_statement.executeUpdate();
                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        connectionDisconnected();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                        log.error(e.getMessage(), e);
                    }
                    throw new Exception("ERROR");
                }
                return null;
            }
        };
        return SQLTask;
    }


    public int getWhitespaceLength(String str) {
        if (str == null || str.isEmpty()) {
            return 0; // 空字符串或null，返回0
        }
        int length = 0;
        // 遍历字符串，统计开头的空白字符
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                length++;
            } else {
                break; // 遇到非空白字符，停止统计
            }
        }
        return length;
    }

    public void initConnectList(){



        List connect_list = new ArrayList<Connect>();
        for (TreeItem<TreeData> ti : Main.mainController.databasemeta_treeview.getRoot().getChildren()) {
            for (TreeItem<TreeData> t : ti.getChildren()) {
                if(t.getValue().getName().equals(sql_connect_choicebox.getSelectionModel().getSelectedItem().getName())){
                }else{
                    Connect newConnect = new Connect((Connect) t.getValue());
                    newConnect.setConn(null);
                    connect_list.add(newConnect);
                }
            }
        }
        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(connect_list);
        sql_connect_choicebox.getItems().retainAll(sql_connect_choicebox.getSelectionModel().getSelectedItem());
        sql_connect_choicebox.getItems().addAll(dbtypelist);

    }


}
