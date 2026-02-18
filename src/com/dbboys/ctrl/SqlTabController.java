package com.dbboys.ctrl;

import com.dbboys.app.Main;
import com.dbboys.customnode.*;
import com.dbboys.service.ConnectionService;
import com.dbboys.i18n.I18n;
import com.dbboys.service.SqlexeService;
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
import javafx.scene.paint.Color;
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
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;

public class SqlTabController {
    private static final Logger log = LogManager.getLogger(SqlTabController.class);
    private static final String COMMIT_AUTO = "AUTO";
    private static final String COMMIT_MANUAL = "MANUAL";
    @FXML
    public Button sqlRunButton;
    @FXML
    public ChoiceBox<Connect> sqlConnectChoiceBox;
    @FXML
    public ChoiceBox<Database> sqlDbChoiceBox;
    @FXML
    public ChoiceBox sqlCommitModeChoiceBox;
    //sql编辑框及结果集
    @FXML
    public SplitPane sqlSplitPane;
    @FXML
    public StackPane sqlEditStackPane;
    @FXML
    public CustomSqlEditCodeArea sqlEditCodeArea;
    //
    public boolean isRefreshConnectList=false;
    //分隔符位置
    public Double sqlSplitPaneDividerPosition = Main.split2Pos;
    public Connect sqlConnect = new Connect();
    public Task<Void> sqlTask = new Task<>() {
        @Override
        protected Void call() {
            return null;
        }
    };
    private Thread sqlTaskThread;
    public ObservableList<Database> databaseChoiceBoxList = FXCollections.observableArrayList();
    public CustomSearchReplaceVbox searchReplaceBox = new CustomSearchReplaceVbox(null);
    public VBox resultSetVBox = new VBox();
    public ResultSetTabController currentResultSetTabController;
    public CustomResultsetTab customResultsetTab;
    //结果集表格相关列表
    public ObservableList<UpdateResult> updateResults;
    public UpdateResult updateResult;
    public String sqlInit = "";  //表或视图拖动新建tab后自动执行的sql
    public String sqlExe;
    public Integer sqlAffect;
    public PreparedStatement sqlStatement;
    public ParameterMetaData parameterMetaData;
    public CallableStatement sqlCallableStatement = null;
    public long sqlUsedTime;
    public long sqlStartTime;
    public long sqlEndTime;
    public long sqlTotalTime;
    public int sqlStatementCount = 0;
    public SimpleStringProperty sqlTransactionText = new SimpleStringProperty("");
    boolean isSingleSql = true;  //判断是否只有一条sql，如果是，select要执行并弹出报错信息，update要弹出报错，且用于是否在任务完成后获取主键
    @FXML
    private VBox sqlTab;
    @FXML
    private Pane topPane;
    @FXML
    private Button sqlExplainButton;
    @FXML
    private Button sqlStopButton;
    @FXML
    private StackPane sqlConnectChoiceBoxIconStackPane;
    @FXML
    private StackPane sqlDbIconPane;
    @FXML
    private StackPane sqlUserIconPane;
    @FXML
    private CustomLabelTextField sqlUserTextField;
    @FXML
    private ChoiceBox<String> sqlSqlModeChoiceBox;
    @FXML
    private Button sqlRecordButton;
    @FXML
    private Label sqlReadOnlyLabel;
    @FXML
    private VirtualizedScrollPane sqlEditScrollPane;
    //事务未提交hbox
    @FXML
    private HBox transactionBox;
    @FXML
    private Button transactionCommitButton;
    @FXML
    private Button transactionRollbackButton;
    //结果集
    @FXML
    private Pane bottomPane;
    @FXML
    private StackPane bottomPaneStackPane;
    @FXML
    private TabPane resultsetTabPane;
    @FXML
    private Tab resultsetSummaryTab;
    @FXML
    private CustomResultsetTableView resultsetTotalTableView;
    @FXML
    private StackPane resultsetStackPane;
    //执行过程中提示面板
    @FXML
    private StackPane sqlExecuteProcessStackPane;
    @FXML
    private Label sqlExecuteLoadingLabel;
    @FXML
    private Label sqlExecuteTaskInfo;
    @FXML
    private Label sqlExecuteTimeInfo;
    private CustomInfoStackPane explain_result_stackpane;
    private Boolean isSqlRefresh = false;
    private final ConnectionService connectionService = new ConnectionService();
    private final SqlexeService sqlexeService = new SqlexeService();
    private final Connect defaultConnect = new Connect();
    private final Database defaultDatabase = new Database();
    private List sqlParamList = new ArrayList();
    private String sqlExecutionResult = "";
    private boolean sqlExecutionSuccess = false;
    //执行sql所需相关变量
    private String sqlText = "";
    private final Tooltip commitButtonTooltip = new Tooltip();
    private String newResultsetTabName;
    private final int[] sqlSelectionRange = {0, 0};
    private boolean suppressConnectChange = false;
    private boolean suppressDbChange = false;
    private boolean suppressCommitModeChange = false;
    private ResultSetTabController activeResultSetController;
    private Label sqlConnectChoiceBoxDbIcon;
    private Label sqlConnectChoiceBoxLoadingIcon;
    private SVGPath sqlConnectIconPath;

    private void clearUpdateResults() {
        Platform.runLater(() -> updateResults.clear());
    }

    private void finishExecution(int selectionStart, int selectionEnd) {
        Platform.runLater(() -> {
            sqlExecuteProcessStackPane.setVisible(false);
            explain_result_stackpane.setVisible(false);
            sqlEditCodeArea.selectRange(selectionStart, selectionEnd);
        });
    }

    private void hideExecuteProcess() {
        Platform.runLater(() -> sqlExecuteProcessStackPane.setVisible(false));
    }

    private void setResultsetVisible(boolean visible) {
        Platform.runLater(() -> resultSetVBox.setVisible(visible));
    }

    private void setExplainVisible(boolean visible) {
        Platform.runLater(() -> explain_result_stackpane.setVisible(visible));
    }

    private void showExplainText(String text) {
        Platform.runLater(() -> {
            resultSetVBox.setVisible(false);
            explain_result_stackpane.setVisible(true);
            explain_result_stackpane.codeArea.replaceText(text);
        });
    }

    private void selectRangeAndFollow(int start, int end) {
        Platform.runLater(() -> {
            sqlEditCodeArea.selectRange(start, end);
            sqlEditCodeArea.requestFollowCaret();
        });
    }

    private void addUpdateResult(UpdateResult result, boolean clearFirst) {
        Platform.runLater(() -> {
            if (clearFirst) {
                updateResults.clear();
            }
            updateResults.add(result);
        });
    }


    private void updateSqlModeChoicebox(List sqlmodes) {
        if (sqlConnect.getConn() == null) {
            Platform.runLater(() -> sqlSqlModeChoiceBox.setVisible(false));
            return;
        }
        if (sqlmodes == null || sqlmodes.isEmpty()) {
            Platform.runLater(() -> sqlSqlModeChoiceBox.setVisible(false));
            return;
        }
        if ("sqlmode=none".equals(sqlmodes.get(0))) {
            Platform.runLater(() -> sqlSqlModeChoiceBox.setVisible(false));
            return;
        }
        Platform.runLater(() -> {
            sqlSqlModeChoiceBox.setVisible(true);
            sqlSqlModeChoiceBox.getItems().clear();
            sqlSqlModeChoiceBox.getItems().add("sqlmode=gbase");
            sqlSqlModeChoiceBox.getItems().addAll(sqlmodes.subList(1, sqlmodes.size()));
            for (String item : sqlSqlModeChoiceBox.getItems()) {
                if (item.equals(sqlmodes.get(0))) {
                    sqlSqlModeChoiceBox.setValue(item);
                    break;
                }
            }
        });
    }

    private void cancelCurrentExecution() {
        if (sqlTask != null) {
            sqlTask.cancel();
        }
        try {
            if (sqlStatement != null) {
                sqlStatement.cancel();
                sqlStatement = null;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        if (currentResultSetTabController != null) {
            currentResultSetTabController.cancel();
        }
        if (activeResultSetController != null && activeResultSetController != currentResultSetTabController) {
            activeResultSetController.cancel();
        }
        if (resultsetTabPane != null) {
            for (Tab tab : resultsetTabPane.getTabs()) {
                if (tab instanceof CustomResultsetTab) {
                    ((CustomResultsetTab) tab).resultSetTabController.cancel();
                }
            }
        }
        //closeResultSet();
    }


    private String resolveSqlText(boolean allowRefresh) {
        if (allowRefresh && isSqlRefresh && currentResultSetTabController != null
                && currentResultSetTabController.lastSqlTextField.getTooltip() != null) {
            return currentResultSetTabController.lastSqlTextField.getTooltip().getText();
        }
        if (sqlEditCodeArea.getSelectedText().isEmpty()) {
            return sqlEditCodeArea.getText();
        }
        return sqlEditCodeArea.getSelectedText();
    }

    public void initialize() throws IOException {

        setupTransactionTooltips();
        initI18nBindings();
        setupSqlTabIcons();
        setupSearchReplacePanel();
        setupResultSetView();
        setupSplitPaneBehavior();
        bindEditorSizeToPane();
        setupConnectIcons();
        setupDefaultConnectionState();
        loadConnectChoices();
        /*
        List connect_list = new ArrayList<Connect>();
        for (TreeItem<TreeData> ti : Main.mainController.databaseMetaTreeView.getRoot().getChildren()) {
            for (TreeItem<TreeData> t : ti.getChildren()) {
                Connect newConnect = new Connect((Connect) t.getValue());
                newConnect.setConn(null);
                connect_list.add(newConnect);
            }
        }


        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(connect_list);
        sqlConnectChoiceBox.setItems(dbtypelist);

         */




        bindHeaderControls();
        setupResultsetTotalTable();
        setupRunStopExplainActions();


        //连接变更响应事件
        sqlConnectChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (suppressConnectChange) {
                        return;
                    }
                    if (newVal == defaultConnect) {
                        sqlConnect.setConn(null);
                        sqlConnectIconPath.setContent(IconPaths.CONNECTION_LINK);
                        sqlConnectChoiceBoxDbIcon.setGraphic(sqlConnectIconPath);
                        sqlConnectIconPath.setScaleX(0.6);
                        sqlConnectIconPath.setScaleY(0.6);
                        sqlConnectChoiceBoxDbIcon.setVisible(true);
                        sqlConnectChoiceBoxLoadingIcon.setVisible(false);
                        //sqlConnectChoiceBox_icon.setContent("M21.2812 3.3281 Q22.2656 4.3125 22.6094 5.6562 Q22.9688 6.9844 22.6094 8.3281 Q22.2656 9.6562 21.2812 10.6406 L19.4531 12.4688 Q18.4688 13.4531 17.125 13.7812 Q15.7969 14.1094 14.4844 13.7812 L19.4531 8.8125 Q20.2031 8.0625 20.2031 6.9844 Q20.2031 5.9062 19.4531 5.1562 Q18.7031 4.4062 17.625 4.4062 Q16.5469 4.4062 15.7969 5.1562 L10.8281 10.125 Q10.5 8.8125 10.8281 7.4844 Q11.1562 6.1406 12.1406 5.1562 L13.9688 3.3281 Q14.9531 2.3438 16.2812 2 Q17.625 1.6406 18.9531 2 Q20.2969 2.3438 21.2812 3.3281 ZM10.3125 16.125 L16.7344 9.7031 Q17.1094 9.3281 17.1094 8.7969 Q17.1094 8.25 16.7031 7.875 Q16.3125 7.5 15.7656 7.5312 Q15.2344 7.5469 14.8594 7.875 L8.4844 14.2969 Q8.1094 14.6719 8.1094 15.2188 Q8.1094 15.75 8.4844 16.125 Q8.8594 16.5 9.3906 16.4844 Q9.9375 16.4531 10.3125 16.125 ZM9.375 18.8438 L14.3438 13.875 Q14.7188 15.1875 14.3906 16.5312 Q14.0625 17.8594 13.0781 18.8438 L11.25 20.6719 Q10.2188 21.7031 8.9062 22.0312 Q7.5938 22.3594 6.25 22.0312 Q4.9219 21.7031 3.9062 20.7031 Q2.9062 19.6875 2.5469 18.3594 Q2.2031 17.0156 2.5469 15.6875 Q2.9062 14.3438 3.8906 13.3594 L5.7656 11.5312 Q6.75 10.5469 8.0625 10.2188 Q9.375 9.8906 10.7344 10.2188 L5.7656 15.1875 Q4.9688 15.9375 4.9688 17.0156 Q4.9688 18.0938 5.7344 18.875 Q6.5156 19.6406 7.5625 19.6406 Q8.625 19.6406 9.375 18.8438 Z");
                        try {
                            sqlDbChoiceBox.getItems().clear();
                            sqlDbChoiceBox.setValue(defaultDatabase);
                        } finally {
                        }
                        sqlUserTextField.setText("N/A");
                        sqlSqlModeChoiceBox.setVisible(false);
                        sqlCommitModeChoiceBox.getSelectionModel().select(0);
                        sqlCommitModeChoiceBox.setVisible(true);
                        sqlRecordButton.setVisible(true);
                        sqlReadOnlyLabel.setVisible(false);
                    }
                    //如果是切换其他连接，进入处理，连不上(newVal.equals(sqlConnect)），回到原连接，不进入处理，如果前一个是默认空值，切换到当前值，是重连，进入处理
                    //if ((newVal != null && !newVal.equals(oldVal)&&!newVal.equals(defaultConnect)&&!newVal.equals(sqlConnect))||(newVal.equals(sqlConnect)&&oldVal.equals(defaultConnect))) {
                    else {
                        //boolean allowed = confirmChange(newVal);

                        //在线程中执行，避免切换连接长时间连不上时主界面卡死
                        new Thread(() -> {
                            Platform.runLater(() -> {
                                sqlConnectChoiceBoxDbIcon.setVisible(false);
                                sqlConnectChoiceBoxLoadingIcon.setVisible(true);
                            });
                            Connection conn = null;
                            try {
                                conn = connectionService.getConnection(newVal);
                                connectionService.changeCommitMode(conn, sqlCommitModeChoiceBox.getSelectionModel().getSelectedIndex());
                        
                            } catch (Exception e) {
                                GlobalErrorHandlerUtil.handle(e);
                            }
                            if (conn != null) {
                                if (sqlConnect.getConn() != null)
                                    try {
                                        closeResultSet();
                                        sqlConnect.getConn().close();
                                    } catch (SQLException e) {
                                        GlobalErrorHandlerUtil.handle(e);
                                    }
                                sqlConnect = newVal;
                                sqlConnect.setConn(conn);
                                //默认结果集面板中的连接同步更改，保证结果集中相关需要连接的操作正常
                                currentResultSetTabController.sqlConnect = sqlConnect;

                                //如果连接成功，刷新数据库元数据树，展开当前连接节点
                                for (TreeItem<TreeData> ti : Main.mainController.databaseMetaTreeView.getRoot().getChildren()) {
                                    for (TreeItem<TreeData> t : ti.getChildren()) {
                                        Connect connect = (Connect) t.getValue();
                                        if (t.getValue().getName().equals(sqlConnect.getName())) {
                                            try {
                                                if (connect.getConn() == null||connect.getConn().isClosed()) {
                                                    t.setExpanded(false);
                                                    t.setExpanded(true);
                                                } else {
                                                    ResultSet rs = null;
                                                    try {
                                                        rs = connect.getConn().createStatement().executeQuery("select first 1 tabid from systables");
                                                    } catch (SQLException e) {
                                                        //如果报错，表示该连接已断开，自动重连
                                                        connect.setConn(null);
                                                        t.setExpanded(false);
                                                        t.setExpanded(true);
                                                    } finally {
                                                        if (rs != null) {
                                                            try {
                                                                rs.close();
                                                            } catch (SQLException e) {
                                                                rs = null;
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (SQLException e) {
                                                // TODO Auto-generated catch block
                                                GlobalErrorHandlerUtil.handle(e);
                                            }

                                        }
                                    }
                                }

                                //更换数据库类型图标
                                Platform.runLater(() -> {
                                    if (sqlConnect.getDbtype().equals("GBASE 8S")) {
                                        sqlConnectIconPath.setContent(IconPaths.GBASE_LOGO);
                                        sqlConnectChoiceBoxDbIcon.setGraphic(new Group(sqlConnectIconPath));
                                        sqlConnectIconPath.setScaleX(0.2);
                                        sqlConnectIconPath.setScaleY(0.2);
                                    } else {
                                        sqlConnectIconPath.setContent(IconPaths.CONNECTION_LINK);
                                        sqlConnectChoiceBoxDbIcon.setGraphic(new Group(sqlConnectIconPath));
                                        sqlConnectIconPath.setScaleX(0.6);
                                        sqlConnectIconPath.setScaleY(0.6);
                                    }

                                    if (sqlConnect.getReadonly()) {
                                        sqlRecordButton.setVisible(false);
                                        sqlCommitModeChoiceBox.setVisible(false);
                                        sqlReadOnlyLabel.setVisible(true);
                                    } else {
                                        sqlRecordButton.setVisible(true);
                                        sqlCommitModeChoiceBox.setVisible(true);
                                        sqlReadOnlyLabel.setVisible(false);
                                    }
                                });
                                //更新数据库

                                List db_names = sqlexeService.getDatabases(sqlConnect);
                                databaseChoiceBoxList = FXCollections.observableArrayList(db_names);
                                Platform.runLater(() -> {
                                    try {
                                        sqlDbChoiceBox.setValue(defaultDatabase); //设置一个默认值，避免连接不同但库名相同不触发激活数据库
                                        sqlDbChoiceBox.setItems(databaseChoiceBoxList);
                                        int i = 0;
                                        for (Database item : databaseChoiceBoxList) {
                                            if (item.getName().equals(sqlConnect.getDatabase())) {
                                                sqlDbChoiceBox.getSelectionModel().select(i);
                                                break;
                                            }
                                            i++;
                                        }
                                    } finally {
                                    }
                                    sqlUserTextField.setText(sqlConnect.getUsername());
                                    sqlConnectChoiceBoxDbIcon.setVisible(true);
                                    sqlConnectChoiceBoxLoadingIcon.setVisible(false);
                                });
                            } else {
                                // 延迟恢复旧值，避免在监听器里直接修改导致冲突
                                if (oldVal == defaultConnect)
                                    Platform.runLater(() -> sqlConnectChoiceBox.setValue(defaultConnect));
                                else
                                    Platform.runLater(() -> sqlConnectChoiceBox.setValue(sqlConnect));
                            }
                        }).start();

                    }
                });

        //数据库变更响应事件
        sqlDbChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.info("oldvalue is:"+oldValue);
            log.info("newValue is:"+newValue);
            //如果变更值和当前值一样，表示上次切换失败，无需处理
            if(suppressDbChange) {
                return;
            }
            if (newValue == null) {
                return;
            }
            if (oldValue != null) {
            }


            if ( !newValue.equals(oldValue) && !newValue.equals(defaultDatabase)) {
                String result = "success";
                //!oldValue.equals(defaultDatabase)表示重连，不触发激活操作，避免出现激活时关闭sql_result导致如果自动重做sql任务报结果集已关闭错误
                if (oldValue != null && !oldValue.equals(defaultDatabase)) {
                    result = sqlexeService.activeDatabase(sqlConnect, newValue, this);
                }
                if (result == null) { //如果是空值，切换失败，设置为原值
                    
                    Platform.runLater(() -> {
                        suppressDbChange=true;
                        sqlDbChoiceBox.setValue(oldValue);
                        suppressDbChange=false;
                });
                } else if (result.equals("success")) {
                    updateSqlModeChoicebox(sqlexeService.getSqlMode(sqlConnect.getConn()));
                    if(sqlCommitModeChoiceBox.getSelectionModel().getSelectedIndex()==1){
                        try {
                            sqlConnect.getConn().setAutoCommit(false);
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                        }
                    }
                    //如果是sqlInit不是空，表示当前窗口是拖动表或视图在此显示新建，需要执行查询sql
                    if (!sqlInit.isEmpty()) {
                        Platform.runLater(() -> {
                            sqlEditCodeArea.appendText(sqlInit);
                            sqlEditCodeArea.selectRange(0, sqlEditCodeArea.getLength());
                            sqlRunButton.fire();
                            sqlInit = "";
                        });

                    }
                } else if (result.equals("disconnected")) {
                    connectionDisconnected();
                }
            }

        });

        //提交模式变更事件
        sqlCommitModeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //切换自动提交和手动提交不受连接是否已中断影响
            if (suppressCommitModeChange) {
                return;
            }
            if (sqlConnect.getConn() != null) {
                try {
                    sqlConnect.getConn().setAutoCommit(sqlCommitModeChoiceBox.getSelectionModel().getSelectedIndex()==0);
                } catch (SQLException e) {
                    log.error(e.getMessage(),e);
                    if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                        connectionDisconnected();
                    } else {
                        GlobalErrorHandlerUtil.handle(e);
                        Platform.runLater(() -> {
                            suppressCommitModeChange = true;
                            sqlCommitModeChoiceBox.getSelectionModel().select(oldValue);
                            suppressCommitModeChange = false;
                            
                            //如果是提示不支持事务，切回自动提交
                            //if(e.getErrorCode()==-79744){
                            //     sqlCommitModeChoiceBox.getSelectionModel().select(0);
                            //}
                        });
                    }
                }
            } else {
            }
        });

        //变更历史记录
        sqlRecordButton.setOnAction(envent -> {
            PopupWindowUtil.openSqlHistoryPopupWindow(sqlConnect.getId());
        });

        //事务未提交面板事件
        transactionCommitButton.setOnAction(event -> {
            int start = sqlEditCodeArea.getLength();
            sqlEditCodeArea.appendText("\ncommit;");
            sqlEditCodeArea.moveTo(start);
            sqlEditCodeArea.requestFollowCaret();
            sqlEditCodeArea.selectRange(sqlEditCodeArea.getLength() - "commit;".length(), sqlEditCodeArea.getLength());
            sqlRunButton.fire();

        });
        transactionRollbackButton.setOnAction(event -> {
            int start = sqlEditCodeArea.getLength();
            sqlEditCodeArea.appendText("\nrollback;");
            sqlEditCodeArea.moveTo(start);
            sqlEditCodeArea.requestFollowCaret();
            sqlEditCodeArea.selectRange(sqlEditCodeArea.getLength() - "rollback;".length(), sqlEditCodeArea.getLength());
            sqlRunButton.fire();
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
                new KeyFrame(Duration.ZERO, e -> transactionCommitButton.setStyle("-fx-border-color: red;")),
                new KeyFrame(Duration.ZERO, e -> transactionRollbackButton.setStyle("-fx-border-color: #2871a8;")),
                new KeyFrame(Duration.seconds(0.5), e -> transactionRollbackButton.setStyle("-fx-border-color: red;")),
                new KeyFrame(Duration.seconds(0.5), e -> transactionCommitButton.setStyle("-fx-border-color: #2871a8;"))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
*/
        //sqlmode改变事件
        sqlSqlModeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (!sqlTask.isRunning() && newValue != null) {
                sqlTask = createSqlModeTask(sqlConnect, newValue);
                new Thread(sqlTask).start();
            }
        });

        //进度条
        //执行过程面板动画

        final Double[] secondsElapsed = {0.0};
        Timeline taskTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event1 -> {
            secondsElapsed[0] += 0.1;
            sqlExecuteTimeInfo.setText(formatExecuteTime(secondsElapsed[0]));
        }));
        taskTimeline.setCycleCount(Timeline.INDEFINITE);


        //进度面板显示监听事件
        sqlExecuteProcessStackPane.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    sqlExecuteTaskInfo.setText(I18n.t("sql.exec.running"));
                    secondsElapsed[0] = 0.0;
                    sqlExecuteTimeInfo.setText(formatExecuteTime(secondsElapsed[0]));
                    taskTimeline.playFromStart();
                } else {
                    taskTimeline.stop();
                    secondsElapsed[0] = 0.0;
                    sqlExecuteTimeInfo.setText(formatExecuteTime(secondsElapsed[0]));
                }
            }
        });

        //sqlEditCodeArea初始化以后才能赋值给searchReplaceBox，否则sqlEditCodeArea是null
        searchReplaceBox.setCodeArea(sqlEditCodeArea);


    }

    private void setupTransactionTooltips() {
        commitButtonTooltip.textProperty().bind(sqlTransactionText);
        commitButtonTooltip.setShowDelay(Duration.millis(100));
        transactionCommitButton.setTooltip(commitButtonTooltip);
        transactionRollbackButton.setTooltip(commitButtonTooltip);
    }

    private void setupSqlTabIcons() {
        sqlExecuteLoadingLabel.setGraphic(IconFactory.imageView(IconPaths.LOADING_GIF, 12, 12, true));
        sqlRunButton.setGraphic(IconFactory.group(IconPaths.SQL_RUN, 0.7, Color.valueOf("#074675")));
        sqlExplainButton.setGraphic(IconFactory.group(IconPaths.SQL_EXPLAIN, 0.7, Color.valueOf("#074675")));
        sqlStopButton.setGraphic(IconFactory.group(IconPaths.SQL_STOP, 0.75, Color.valueOf("#9f453c")));
        sqlRecordButton.setGraphic(IconFactory.group(IconPaths.SQL_HISTORY, 0.8, Color.valueOf("#074675")));
        sqlReadOnlyLabel.setGraphic(IconFactory.group(IconPaths.SQL_READONLY, 0.5, Color.valueOf("#9f453c")));

        if (sqlDbIconPane != null) {
            sqlDbIconPane.getChildren().setAll(IconFactory.group(IconPaths.SQL_DATABASE, 0.4, Color.valueOf("#888")));
        }
        if (sqlUserIconPane != null) {
            sqlUserIconPane.getChildren().setAll(IconFactory.group(IconPaths.SQL_USER, 0.55, Color.valueOf("#888")));
        }
    }

    private void initI18nBindings() {
        bindTooltip(sqlRunButton, "sql.tooltip.run");
        bindTooltip(sqlExplainButton, "sql.tooltip.explain");
        bindTooltip(sqlStopButton, "sql.tooltip.stop");
        bindTooltip(sqlRecordButton, "sql.tooltip.history");

        bindText(transactionCommitButton, "sql.transaction.commit");
        bindText(transactionRollbackButton, "sql.transaction.rollback");
        bindText(sqlReadOnlyLabel, "sql.label.readonly");
        bindTabText(resultsetSummaryTab, "sql.tab.result");

        setupConnectChoiceBoxI18n();
        setupChoiceBoxConverter(sqlDbChoiceBox);
        sqlCommitModeChoiceBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Object object) {
                if (COMMIT_MANUAL.equals(object)) {
                    return I18n.t("sql.commit.manual");
                }
                if (COMMIT_AUTO.equals(object)) {
                    return I18n.t("sql.commit.auto");
                }
                return object == null ? "" : object.toString();
            }

            @Override
            public Object fromString(String string) {
                return string;
            }
        });
        refreshCommitModeItems();

        sqlExecuteTimeInfo.setText(formatExecuteTime(0));

        I18n.localeProperty().addListener((obs, oldVal, newVal) -> {
            defaultConnect.setName(I18n.t("sql.connect.select_prompt"));
            defaultDatabase.setName(I18n.t("common.na"));
            // Ensure button text refresh when default items are selected
            refreshConnectChoiceBoxItems();
            refreshDefaultConnectDisplay();
            refreshDbChoiceBoxDisplay();
            sqlExecuteTimeInfo.setText(formatExecuteTime(0));
            refreshCommitModeItems();
            sqlCommitModeChoiceBox.setValue(sqlCommitModeChoiceBox.getValue());
        });
    }

    private void setupConnectChoiceBoxI18n() {
        setupChoiceBoxConverter(sqlConnectChoiceBox);
        refreshConnectChoiceBoxItems();
    }

    private <T extends TreeData> void setupChoiceBoxConverter(ChoiceBox<T> choiceBox) {
        choiceBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.getName();
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
    }

    private void refreshConnectChoiceBoxItems() {
        if (sqlConnectChoiceBox.getItems() == null || sqlConnectChoiceBox.getItems().isEmpty()) {
            return;
        }
        suppressConnectChange = true;
        int selectedIndex = sqlConnectChoiceBox.getSelectionModel().getSelectedIndex();
        Connect selectedValue = sqlConnectChoiceBox.getValue();
        List<Connect> snapshot = new ArrayList<>(sqlConnectChoiceBox.getItems());
        sqlConnectChoiceBox.getItems().setAll(snapshot);
        if (selectedIndex >= 0 && selectedIndex < sqlConnectChoiceBox.getItems().size()) {
            sqlConnectChoiceBox.getSelectionModel().select(selectedIndex);
        } else {
            if (selectedValue != null && sqlConnectChoiceBox.getItems().contains(selectedValue)) {
                sqlConnectChoiceBox.getSelectionModel().select(selectedValue);
            } else if (selectedValue != null) {
                sqlConnectChoiceBox.setValue(selectedValue);
            } else {
                sqlConnectChoiceBox.setValue(defaultConnect);
            }
        }
        suppressConnectChange = false;
    }

    private void refreshDefaultConnectDisplay() {
        if (sqlConnectChoiceBox.getValue() != defaultConnect) {
            return;
        }
        suppressConnectChange = true;
        sqlConnectChoiceBox.setValue(null);
        sqlConnectChoiceBox.setValue(defaultConnect);
        suppressConnectChange = false;
    }


    private void refreshDbChoiceBoxDisplay() {
        if (sqlDbChoiceBox.getValue() == null) {
            return;
        }
        suppressDbChange = true;
        Database selected = sqlDbChoiceBox.getValue();
        sqlDbChoiceBox.getSelectionModel().clearSelection();
        sqlDbChoiceBox.setValue(selected);
        suppressDbChange = false;
    }

    private void refreshCommitModeItems() {
        Object selected = sqlCommitModeChoiceBox.getValue();
        sqlCommitModeChoiceBox.getItems().clear();
        sqlCommitModeChoiceBox.getItems().add(COMMIT_AUTO);
        sqlCommitModeChoiceBox.getItems().add(COMMIT_MANUAL);
        if (selected == null) {
            selected = COMMIT_AUTO;
        }
        if (!sqlCommitModeChoiceBox.getItems().contains(selected)) {
            selected = COMMIT_AUTO;
        }
        sqlCommitModeChoiceBox.setValue(selected);
    }

    private boolean isDefaultConnectSelected() {
        return sqlConnectChoiceBox.getValue() == defaultConnect;
    }

    private void setupSearchReplacePanel() {
        searchReplaceBox.setMaxWidth(300);
        searchReplaceBox.setMaxHeight(26);
        StackPane.setAlignment(searchReplaceBox, Pos.TOP_RIGHT);
        StackPane.setMargin(searchReplaceBox, new Insets(2, 17, 0, 0));
        sqlEditStackPane.getChildren().add(searchReplaceBox);
        sqlEditCodeArea.setOnShowFindPanel(searchReplaceBox::showFindPanel);
        sqlEditCodeArea.setOnShowReplacePanel(searchReplaceBox::showReplacePanel);
    }

    private void setupResultSetView() throws IOException {
        sqlSqlModeChoiceBox.setVisible(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/ResultSetTab.fxml"));
        loader.setControllerFactory(clazz -> {
            if (clazz == ResultSetTabController.class) {
                return new ResultSetTabController(sqlConnect, sqlExecuteProcessStackPane);
            }
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        resultSetVBox = loader.load();
        resultsetStackPane.getChildren().add(resultSetVBox);
        currentResultSetTabController = loader.getController();
        currentResultSetTabController.hiddenDisconnectedButton.setOnAction(event -> connectionDisconnected());

        explain_result_stackpane = new CustomInfoStackPane(new CustomInfoCodeArea());
        explain_result_stackpane.setVisible(false);
        resultsetStackPane.getChildren().add(explain_result_stackpane);

        currentResultSetTabController.lastSqlRefreshButton.setOnAction(event -> {
            isSqlRefresh = true;
            sqlRunButton.fire();
        });
    }

    private void setupSplitPaneBehavior() {
        sqlSplitPane.setDividerPositions(Main.sqledit_codearea_is_max == 1 ? 1 : sqlSplitPaneDividerPosition);
        sqlSplitPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (Main.sqledit_codearea_is_max == 1) {
                Platform.runLater(() -> sqlSplitPane.setDividerPositions(1));
            } else {
                Platform.runLater(() -> sqlSplitPane.setDividerPositions(sqlSplitPaneDividerPosition));
            }
        });

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sqlSplitPane.lookupAll(".split-pane-divider").forEach(divider -> {
                divider.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
                    sqlSplitPaneDividerPosition = sqlSplitPane.getDividers().get(0).getPosition();
                });
            });
        }).start();
    }

    private void bindEditorSizeToPane() {
        sqlEditCodeArea.prefWidthProperty().bind(topPane.widthProperty());
        sqlEditCodeArea.prefHeightProperty().bind(topPane.heightProperty());
    }

    private void setupConnectIcons() {
        sqlConnectChoiceBoxDbIcon = new Label();
        sqlConnectChoiceBoxLoadingIcon = new Label();
        sqlConnectChoiceBoxLoadingIcon.setVisible(false);
        sqlConnectChoiceBoxIconStackPane.getChildren().addAll(sqlConnectChoiceBoxDbIcon, sqlConnectChoiceBoxLoadingIcon);

        sqlConnectIconPath = IconFactory.create(IconPaths.CONNECTION_LINK, 0.6, 0.6, Color.valueOf("#888"));
        sqlConnectChoiceBoxDbIcon.setGraphic(new Group(sqlConnectIconPath));

        ImageView loadingIcon = IconFactory.loadingImageView(0.7);
        sqlConnectChoiceBoxLoadingIcon.setGraphic(loadingIcon);
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(I18n.bind("sql.tooltip.connecting"));
        tooltip.setShowDelay(Duration.millis(100));
        sqlConnectChoiceBoxLoadingIcon.setTooltip(tooltip);

        sqlConnectIconPath.setFill(Paint.valueOf("#888"));
    }

    private void bindText(Labeled labeled, String key) {
        if (labeled != null) {
            labeled.textProperty().bind(I18n.bind(key));
        }
    }

    private void bindTabText(Tab tab, String key) {
        if (tab != null) {
            tab.textProperty().bind(I18n.bind(key));
        }
    }

    private void bindColumnText(TableColumn<?, ?> column, String key) {
        if (column != null) {
            column.textProperty().bind(I18n.bind(key));
        }
    }

    private void bindTooltip(Control control, String key) {
        if (control == null) {
            return;
        }
        Tooltip tooltip = control.getTooltip();
        if (tooltip == null) {
            tooltip = new Tooltip();
            control.setTooltip(tooltip);
        }
        tooltip.textProperty().bind(I18n.bind(key));
    }

    private String formatExecuteTime(double seconds) {
        String value = String.valueOf(Math.round(seconds * 10.0) / 10.0);
        return String.format(I18n.t("sql.exec.time"), value);
    }

    private String formatElapsedSeconds(long millis) {
        String value = String.format("%.3f", millis / 1000.0);
        return String.format(I18n.t("sql.exec.elapsed"), value);
    }

    private String buildExecutionMark() {
        String mark = getCommitModeLabel();
        if (sqlSqlModeChoiceBox.isVisible()) {
            mark += I18n.t("sql.mark.sep") + sqlSqlModeChoiceBox.getValue();
        }
        if (!sqlParamList.isEmpty()) {
            mark += I18n.t("sql.mark.params") + sqlParamList;
        }
        return mark;
    }

    private String getCommitModeLabel() {
        Object value = sqlCommitModeChoiceBox.getValue();
        if (COMMIT_MANUAL.equals(value)) {
            return I18n.t("sql.commit.manual");
        }
        return I18n.t("sql.commit.auto");
    }

    private void setupDefaultConnectionState() {
        defaultConnect.setName(I18n.t("sql.connect.select_prompt"));
        defaultDatabase.setName(I18n.t("common.na"));
        sqlConnectChoiceBox.setValue(defaultConnect);
        sqlDbChoiceBox.setValue(defaultDatabase);
        sqlUserTextField.setText(I18n.t("common.na"));
        sqlCommitModeChoiceBox.getSelectionModel().selectFirst();

        sqlEditCodeArea.setOnExecuteRequest(() -> sqlRunButton.fire());
        sqlEditCodeArea.setExecuteDisabledSupplier(sqlRunButton::isDisable);
        sqlConnect = defaultConnect;
    }

    private void loadConnectChoices() {
        List connect_list = new ArrayList<Connect>();
        for (TreeItem<TreeData> ti : Main.mainController.databaseMetaTreeView.getRoot().getChildren()) {
            for (TreeItem<TreeData> t : ti.getChildren()) {
                Connect newConnect = new Connect((Connect) t.getValue());
                newConnect.setConn(null);
                connect_list.add(newConnect);
            }
        }
        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(connect_list);
        sqlConnectChoiceBox.setItems(dbtypelist);
        if (sqlConnectChoiceBox.getValue() == null) {
            sqlConnectChoiceBox.setValue(defaultConnect);
        }
    }

    private void bindHeaderControls() {
        sqlConnectChoiceBox.disableProperty().bind(Bindings.or(
                transactionBox.visibleProperty(),
                sqlExecuteProcessStackPane.visibleProperty()
        ));
        transactionBox.visibleProperty().bind(
                Bindings.notEqual("", sqlTransactionText)
        );
        sqlDbChoiceBox.disableProperty().bind(sqlConnectChoiceBox.disableProperty());
        sqlSqlModeChoiceBox.disableProperty().bind(sqlConnectChoiceBox.disableProperty());
        sqlCommitModeChoiceBox.disableProperty().bind(sqlConnectChoiceBox.disableProperty());
        sqlUserTextField.disableProperty().bind(sqlConnectChoiceBox.disableProperty());

        sqlStopButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty().not());
        sqlRunButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());
        sqlExplainButton.disableProperty().bind(sqlExecuteProcessStackPane.visibleProperty());

        resultsetTabPane.prefWidthProperty().bind(bottomPane.widthProperty());
        resultsetTabPane.prefHeightProperty().bind(bottomPane.heightProperty());
        resultsetTotalTableView.prefWidthProperty().bind(bottomPane.widthProperty());
        resultsetTotalTableView.prefHeightProperty().bind(bottomPane.heightProperty());
        resultsetTotalTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultsetTotalTableView.getSelectionModel().setCellSelectionEnabled(true);
        resultsetTotalTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    private void setupResultsetTotalTable() {
        TableColumn<ObservableList<String>, Object> resultcol;
        TableColumn<ObservableList<String>, Object> begin;
        TableColumn<ObservableList<String>, Object> stop;
        TableColumn<ObservableList<String>, Object> drution;
        TableColumn<ObservableList<String>, Object> affect;
        TableColumn<ObservableList<String>, Object> sqlcol;
        TableColumn<ObservableList<String>, Object> databasecol;
        TableColumn<ObservableList<String>, Object> markcol;

        resultcol = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(resultcol, "sql.table.result");
        resultcol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        resultcol.setCellValueFactory(new PropertyValueFactory<>("result"));
        resultcol.setPrefWidth(120);
        resultcol.setReorderable(false);
        resultcol.setSortable(false);

        begin = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(begin, "sql.table.start_time");
        begin.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        begin.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        begin.setPrefWidth(190);
        begin.setReorderable(false);
        begin.setSortable(false);

        stop = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(stop, "sql.table.end_time");
        stop.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        stop.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        stop.setPrefWidth(190);
        stop.setReorderable(false);
        stop.setSortable(false);

        drution = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(drution, "sql.table.elapsed");
        drution.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        drution.setCellValueFactory(new PropertyValueFactory<>("elapsedTime"));
        drution.setPrefWidth(100);
        drution.setReorderable(false);
        drution.setSortable(false);

        databasecol = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(databasecol, "sql.table.database");
        databasecol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        databasecol.setCellValueFactory(new PropertyValueFactory<>("database"));
        databasecol.setPrefWidth(100);
        databasecol.setReorderable(false);
        databasecol.setSortable(false);

        markcol = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(markcol, "sql.table.note");
        markcol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        markcol.setCellValueFactory(new PropertyValueFactory<>("mark"));
        markcol.setPrefWidth(120);
        markcol.setReorderable(false);
        markcol.setSortable(false);

        affect = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(affect, "sql.table.affected");
        affect.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        affect.setCellValueFactory(new PropertyValueFactory<>("affectedRows"));
        affect.setPrefWidth(100);
        affect.setReorderable(false);
        affect.setSortable(false);

        sqlcol = new TableColumn<ObservableList<String>, Object>();
        bindColumnText(sqlcol, "sql.table.sql");
        sqlcol.setCellFactory(col -> new CustomResultsetTableCell<ObservableList<String>, Object>());
        sqlcol.setCellValueFactory(new PropertyValueFactory<>("updateSql"));
        sqlcol.setPrefWidth(300);
        sqlcol.setReorderable(false);
        sqlcol.setSortable(false);

        resultsetTotalTableView.getColumns().addAll(resultcol, databasecol, sqlcol, affect, drution, begin, stop, markcol);
        updateResults = FXCollections.observableArrayList();
        UpdateResult updateResult = new UpdateResult();
        updateResult.setResult(I18n.t("sql.table.sample.result"));
        updateResult.setDatabase(I18n.t("sql.table.sample.database"));
        updateResult.setUpdateSql(I18n.t("sql.table.sample.sql"));
        updateResult.setStartTime(I18n.t("sql.table.sample.start_time"));
        updateResult.setEndTime(I18n.t("sql.table.sample.end_time"));
        updateResult.setElapsedTime(I18n.t("sql.table.sample.elapsed"));
        updateResult.setAffectedRows(0);
        resultsetTotalTableView.setItems(updateResults);
    }

    private void setupRunStopExplainActions() {
        sqlStopButton.setOnAction(event -> cancelCurrentExecution());

        sqlRunButton.setOnAction(event -> {
            if (isDefaultConnectSelected()) {
                NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("sql.notice.select_connection"));
            } else {
                sqlText = resolveSqlText(true);
                if (sqlText.isEmpty()) {
                    NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("sql.notice.enter_sql"));
                } else {
                    if (sqlTask != null && sqlTask.isRunning()) {
                        cancelCurrentExecution();
                    }

                    sqlExecuteProcessStackPane.setVisible(true);
                    sqlTask = createExecuteSqlTask();
                    closeResultSet();
                    resultsetTabPane.getTabs().subList(1, resultsetTabPane.getTabs().size()).clear();
                    new Thread(sqlTask).start();
                    if (sqlSplitPane.getDividers().get(0).getPosition() > Main.split2Pos) {
                        sqlSplitPane.getDividers().get(0).setPosition(Main.split2Pos);
                    }
                }
            }
        });

        sqlExplainButton.setOnAction(event -> {
            if (isDefaultConnectSelected()) {
                NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("sql.notice.select_connection"));
            } else {
                sqlText = resolveSqlText(false);
                if (sqlText.isEmpty()) {
                    NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("sql.notice.enter_explain_sql"));
                } else {
                    if (sqlTask != null && sqlTask.isRunning()) {
                        cancelCurrentExecution();
                    }
                    sqlExecuteProcessStackPane.setVisible(true);
                    sqlTask = createExplainTask();
                    closeResultSet();
                    resultsetTabPane.getTabs().subList(1, resultsetTabPane.getTabs().size()).clear();
                    new Thread(sqlTask).start();
                    if (sqlSplitPane.getDividers().get(0).getPosition() > Main.split2Pos) {
                        sqlSplitPane.getDividers().get(0).setPosition(Main.split2Pos);
                    }
                }
            }
        });
    }


    //连接断开处理
    public void connectionDisconnected() {
        Platform.runLater(() -> {
            sqlTransactionText.set("");
            //transactionBox.setVisible(false);
            if (AlterUtil.CustomAlertConfirm(I18n.t("common.error"), I18n.t("sql.confirm.reconnect"))) {
                //嵌套Platform保证前一步完成后执行下一步，避免渲染延迟导致前后顺序错误
                Platform.runLater(() -> {
                    sqlConnectChoiceBox.setValue(defaultConnect);
                    Platform.runLater(() -> {
                        sqlConnectChoiceBox.setValue(sqlConnect);
                        Platform.runLater(() -> {
                            if (MetadataTreeviewUtil.metadataService.testConn(sqlConnect)) {
                                NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("sql.notice.reconnect_success"));
                                //    data_manager_sqlRunButton.fire();
                            }
                        });
                    });
                });
            } else {
                sqlConnectChoiceBox.setValue(defaultConnect);
            }
        });
    }

    public void closeResultSet() {
        try {
            if (currentResultSetTabController.sqlResultSet != null) {

                //如果连接已断开，mysql r28 r61驱动在这里会卡死，结果集关闭有问题,重现方式未双击打开sql面板，执行一次select后killsession再次执行会卡死
                currentResultSetTabController.sqlResultSet.close();

            }
            if (currentResultSetTabController.priSqlResult != null) {
                currentResultSetTabController.priSqlResult.close();

            }
            for (Tab tab : resultsetTabPane.getTabs()) {
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
            if (!sqlStopButton.isDisable()) {
                sqlStopButton.fire();
            }
            closeResultSet();
            if (sqlConnect.getConn() != null && !sqlConnect.getConn().isClosed()) {
                sqlConnect.getConn().close();  //如果延迟高，如50ms以上，关闭连接可能会需要2-3秒
                sqlConnect = defaultConnect;  //恢复sqlConnect为初始值，避免连接断开后窗口切换连接失败自动连接到刚断开的连接
                sqlConnectChoiceBox.setValue(defaultConnect);
                sqlSqlModeChoiceBox.setVisible(false);
                sqlTransactionText.set("");
                //transactionBox.setVisible(false);
                databaseChoiceBoxList.clear();
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            AlterUtil.CustomAlert(I18n.t("common.error"), "[" + e.getErrorCode() + "]" + e.getMessage());
        }
    }

    public Task<Void> createExecuteSqlTask() {

        if (sqlEditCodeArea.getSelectedText().isEmpty()) {
            sqlSelectionRange[0] = 0;
            sqlSelectionRange[1] = 0;
        } else {
            sqlSelectionRange[0] = sqlEditCodeArea.getSelection().getStart();
            sqlSelectionRange[1] = sqlEditCodeArea.getSelection().getEnd();
        }

        sqlUsedTime = 0;
        isSingleSql = SqlParserUtil.isSingleStatement(sqlText);
        clearUpdateResults();
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                sqlExecutionSuccess = false;
                sqlExecutionResult = "";
                updateMessage(I18n.t("sql.exec.running"));

                Sql sql = new Sql();
                sqlStatementCount = 0;
                for (SqlParserUtil.Segment segment : SqlParserUtil.split(sqlText)) {
                   
                    String sqlChunk = segment.getText();
                    if (SqlParserUtil.sqlContrainMoreThanOneCommit(sqlChunk)) {
                        isSingleSql = false;
                    }

                    Boolean sqlContainsCommit = false;
                    do {
                        
                        if(isCancelled()){
                            return null;
                        }
                        sql = SqlParserUtil.modifySql(sql, sqlChunk);
                        if (sql.getSqlEnd() && !sql.getSqlstr().trim().isEmpty()) {
                        
                            sqlParamList.clear();
                            sqlExe = sql.getSqlstr();
                            sqlStatementCount++;
                            int finalI = segment.getEndIndex();
                            String finalsqlExe = sqlExe;
                            if (sql.getSqlRemainder() != null && !sql.getSqlRemainder().isEmpty()) {
                                final int remaindersize = sql.getSqlRemainder().length();
                                final int sqllength = finalsqlExe.length();
                                final int whitespacelength = getWhitespaceLength(finalsqlExe);
                                if (!isSqlRefresh) {
                                    selectRangeAndFollow(sqlSelectionRange[0] + finalI + 1 - remaindersize - sqllength + whitespacelength, sqlSelectionRange[0] + finalI + 1 - remaindersize);
                                }
                            } else {
                                final int sqllength = finalsqlExe.length();
                                final int whitespacelength = getWhitespaceLength(finalsqlExe);
                                if (!isSqlRefresh) {
                                    selectRangeAndFollow(sqlSelectionRange[0] + finalI + 1 - sqllength + whitespacelength, sqlSelectionRange[0] + finalI + 1);
                                }
                            }


                            updateResult = new UpdateResult();
                            //执行select
                            if (isSingleSql && (sql.getSqlType().equals("SELECT"))) {
                                try {
                                    
                                    sqlStartTime = System.currentTimeMillis();
                                    //if(sql.getSqlType().equals("SELECT")) {
                                    //sqlTransactionText,sqlCommitModeChoiceBox传入用于结果集编辑更新事务sql及更新结果备注

                                    activeResultSetController = currentResultSetTabController;
                                    sqlUsedTime = currentResultSetTabController.executeSelect(sqlExe, sqlTask, true, sqlTransactionText, sqlCommitModeChoiceBox);
                                    sqlExecutionSuccess = true;
                                    sqlExecutionResult = I18n.t("sql.exec.success");
                                    setResultsetVisible(true);
                                    if(isCancelled()){
                                        setResultsetVisible(false);
                                    }
                                    
                                    
                                } catch (SQLException e) {
                                    
                                    //关闭sqlStatement，避免下次执行报213错误，如绑定变量点击了取消场景
                                    log.error(e.getMessage(), e);
                                    setResultsetVisible(false);
                                    sqlExecutionSuccess = false;
                                    sqlExecutionResult = "[" + e.getErrorCode() + "]" + e.getMessage();
                                    if (e.getErrorCode() == -254) {  //如果没有设置绑定变量参数，需要关闭sqlStatement，否则下次执行会报213
                                        try {
                                            if (currentResultSetTabController.sqlStatement != null)
                                                currentResultSetTabController.sqlStatement.close();
                                            if (currentResultSetTabController.sqlCstmt != null)
                                                currentResultSetTabController.sqlCstmt.close();
                                        } catch (SQLException ex) {
                                            log.error(ex.getMessage(),ex);
                                        }
                                    }
                                    //创建结果表显示错误信息
                                    Platform.runLater(() -> {
                                        if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                                            connectionDisconnected();
                                        } else {
                                            AlterUtil.CustomAlert(I18n.t("common.error"), "[" + e.getErrorCode() + "]" + e.getMessage());
                                        }
                                    });
                                    
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                } finally {
                                    activeResultSetController = null;
                                    sqlParamList = currentResultSetTabController.sqlParamList;
                                }
                                sqlEndTime = System.currentTimeMillis();
                                if (isCancelled() ) {
                                    sqlExecutionSuccess = false;
                                    sqlExecutionResult = I18n.t("sql.exec.cancelled");
                                }
                                //sqlUsedTime = sqlEndTime - sqlStartTime;
                                sqlTotalTime += sqlUsedTime;
                                updateResult.setResult(sqlExecutionResult);
                                updateResult.setDatabase(sqlConnect.getDatabase());
                                updateResult.setUpdateSql(sqlExe.trim());
                                updateResult.setStartTime(sdf.format(sqlStartTime));
                                updateResult.setEndTime(sdf.format(sqlEndTime));
                                updateResult.setElapsedTime(formatElapsedSeconds(sqlUsedTime));
                                updateResult.setAffectedRows(0);
                                updateResult.setMark(buildExecutionMark());
                                addUpdateResult(updateResult, true);
                                if (isCancelled()) {
                                    return null;
                                }
                            
                            }

                            //如果不是一条select sql，且为readonly，报错
                            else if (sqlConnect.getReadonly()) {
                            
                                Platform.runLater(() -> {
                                    AlterUtil.CustomAlert(I18n.t("common.error"), I18n.t("sql.error.readonly_select_only"));
                                });
                            }
                            //如果以上都不是，那就是单条更新或多条语句
                            else {
                                
                                setResultsetVisible(false);
                                setExplainVisible(false);
                                sqlStartTime = System.currentTimeMillis();
                                sqlUsedTime = 0;
                                sqlAffect = 0;
                                sqlExecutionSuccess = true;
                                sqlExecutionResult = I18n.t("sql.exec.success");
                                updateResult.setDatabase(sqlConnect.getDatabase());
                                //开始执行update
                                if (sql.getSqlType().equals("SELECT") || sql.getSqlType().equals("CALL")) {
                                    customResultsetTab = new CustomResultsetTab(sqlConnect, sqlExecuteProcessStackPane);
                                                                        activeResultSetController = customResultsetTab.resultSetTabController;
                                    sqlStartTime = System.currentTimeMillis();
                                    try {
                                        if (sql.getSqlType().equals("SELECT")) {
                                            sqlUsedTime = customResultsetTab.resultSetTabController.executeSelect(sqlExe, sqlTask, false, null, null);
                                        } else {
                                            sqlUsedTime = customResultsetTab.resultSetTabController.executeCall(sqlExe, sqlTask, false);
                                        }
                                    } catch (SQLException e) {
                                        
                                        log.error(e.getMessage(), e);
                                        sqlExecutionSuccess = false;
                                        sqlExecutionResult = "[" + e.getErrorCode() + "]" + e.getMessage();
                                        
                                    } finally {
                                        activeResultSetController = null;
                                        sqlParamList = customResultsetTab.resultSetTabController.sqlParamList;
                                    }
                                    
                                    sqlEndTime = System.currentTimeMillis();
                                    CountDownLatch latch = new CountDownLatch(1);
                                    if(isCancelled()){
                                        sqlExecutionSuccess = false;
                                        sqlExecutionResult = I18n.t("sql.exec.cancelled");
                                    }
                                    if (sqlExecutionSuccess && ((sql.getSqlType().equals("SELECT") || customResultsetTab.resultSetTabController.callHasResultSet))) {
                                        Platform.runLater(() -> {
                                            String baseTitle = I18n.t("sql.tab.resultset_prefix");
                                            String tabName;
                                            int nextNumber = 1;
                                            // 查找可用的Tab名称
                                            while (true) {
                                                newResultsetTabName = baseTitle + nextNumber;
                                                boolean exists = resultsetTabPane.getTabs().stream()
                                                        .anyMatch(tab -> tab.getText().equals(newResultsetTabName));
                                                if (!exists) {
                                                    break;
                                                }
                                                nextNumber++;
                                            }
                                            sqlExecutionResult += (I18n.t("sql.mark.sep") + newResultsetTabName);
                                            customResultsetTab.setText(newResultsetTabName);
                                            resultsetTabPane.getTabs().add(customResultsetTab);
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
                                        sqlStatement = sqlConnect.getConn().prepareStatement(sqlExe);

                                        parameterMetaData = sqlStatement.getParameterMetaData();
                                        int paramCount = parameterMetaData.getParameterCount();
                                        if (paramCount > 0) {
                                            CountDownLatch latch = new CountDownLatch(1);
                                            Platform.runLater(() -> {
                                                //sqlParamList.clear();;
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
                                                    sqlStatement.setObject(z, (sqlParamList.get(z - 1)));
                                                }
                                            }
                                            //System.out.println(sql_param_string);
                                        }
                                        sqlAffect = sqlStatement.executeUpdate();
                                        sqlExecutionSuccess = true;
                                        sqlExecutionResult = I18n.t("sql.exec.success");
                                        
                                    } catch (SQLException e) {
                                        
                                        log.error(e.getMessage(), e);
                                        //如果是执行单条sql，弹出报错信息，如果是批量，不弹出。
                                        if (isSingleSql) {
                                            Platform.runLater(() -> {
                                                if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                                                    connectionDisconnected();
                                                } else if (e.getErrorCode() == -329 || e.getErrorCode() == -23197 || e.getErrorCode() == -349) {
                                                    try {
                                                        sqlStatement = sqlConnect.getConn().prepareStatement("database " + sqlConnect.getDatabase());
                                                        sqlStatement.executeUpdate();
                                                    } catch (SQLException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                    AlterUtil.CustomAlert(I18n.t("common.error"), "[" + e.getErrorCode() + "]" + e.getMessage());
                                                } else {
                                                    AlterUtil.CustomAlert(I18n.t("common.error"), "[" + e.getErrorCode() + "]" + e.getMessage());
                                                }
                                            });
                                        }
                                        sqlExecutionSuccess = false;
                                        sqlExecutionResult = "[" + e.getErrorCode() + "]" + e.getMessage();
                                        
                                    }

                                    sqlEndTime = System.currentTimeMillis();
                                }

                                if (sql.getSqlType().equals("SELECT") || sql.getSqlType().equals("CALL")) {
                                } else {
                                    sqlUsedTime = sqlEndTime - sqlStartTime;
                                }
                                if (isCancelled()) {
                                    sqlExecutionSuccess = false;
                                    sqlExecutionResult = I18n.t("sql.exec.cancelled");
                                }
                                sqlTotalTime += sqlUsedTime;
                                updateResult.setResult(sqlExecutionResult);
                                updateResult.setConnectId(sqlConnect.getId());
                                updateResult.setStartTime(sdf.format(sqlStartTime));
                                updateResult.setEndTime(sdf.format(sqlEndTime));
                                updateResult.setElapsedTime(formatElapsedSeconds(sqlUsedTime));
                                updateResult.setAffectedRows(sqlAffect);
                                updateResult.setUpdateSql(sqlExe.trim());
                                updateResult.setMark(buildExecutionMark());
                              

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
                                if (isCancelled()) {
                                    return null;
                                }
                       
                            }

                            if (sqlExecutionSuccess) {
                                //执行记录保存
                                if (!sql.getSqlType().equals("SELECT")) {
                                    SqliteDBaccessUtil.saveSqlHistory(updateResult);

                                    //如果的切库或建库语句，改变当前连接的库
                                    if (sql.getSqlType().startsWith("DATABASE")) {
                                        sqlConnect.setDatabase(sql.getSqlType().split(" ")[1]);
                                        Platform.runLater(() -> {
                                            Database db = new Database();
                                            db.setName(sqlConnect.getDatabase());
                                            sqlDbChoiceBox.setValue(db);
                                            //ConnectTreeViewUtil.refreshCurrentDatabase(Main.mainController.connect_list_treeview,sqlConnect);
                                        });
                                    }

                                    //判断是否为改变sql模式语句，如果是，改变sqlmode按钮文字
                                    if (sqlExe.toLowerCase().trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").startsWith("setenvironmentsqlmode'oracle'")) {
                                        Platform.runLater(() -> {
                                            sqlSqlModeChoiceBox.setValue("sqlmode=oracle");
                                        });
                                    } else if (sqlExe.toLowerCase().trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").startsWith("setenvironmentsqlmode'gbase'")) {
                                        Platform.runLater(() -> {
                                            sqlSqlModeChoiceBox.setValue("sqlmode=gbase");
                                        });
                                    } else if (sqlExe.toLowerCase().trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").startsWith("setenvironmentsqlmode'mysql'")) {
                                        if (sqlSqlModeChoiceBox.getItems().contains("sqlmode=mysql")) {
                                            Platform.runLater(() -> {
                                                sqlSqlModeChoiceBox.setValue("sqlmode=mysql");
                                            });
                                        }

                                    }

                                    //判断是否在事务中未提交
                                    if (sqlConnect.getConn().getAutoCommit()) { //自动提交场景
                                        if (sqlExe.toUpperCase().trim().startsWith("BEGIN WORK")) {
                                            if (sqlExe.endsWith(";")) {
                                                sqlTransactionText.set(sqlExe.trim() + "\n");
                                            } else {
                                                sqlTransactionText.set(sqlExe.trim() + ";\n");
                                            }
                                        } else if (sqlExe.toUpperCase().trim().startsWith("COMMIT") || sqlExe.toUpperCase().trim().startsWith("ROLLBACK")) {
                                            sqlTransactionText.set("");
                                        } else if (!sqlTransactionText.get().equals("")) {
                                            if (sqlExe.endsWith(";")) {
                                                sqlTransactionText.set(sqlTransactionText.get() + sqlExe.trim() + "\n");
                                            } else {
                                                sqlTransactionText.set(sqlTransactionText.get() + sqlExe.trim() + ";\n");
                                            }
                                        }
                                    } else if(sqlSqlModeChoiceBox.getValue().equals("sqlmode=oracle")){ //oracle手动提交


                                            if (sqlExe.endsWith(";")) {
                                                sqlTransactionText.set(sqlTransactionText.get() + sqlExe.trim() + "\n");
                                            } else {
                                                sqlTransactionText.set(sqlTransactionText.get() + sqlExe.trim() + ";\n");
                                            }
                                            if (sqlExe.toUpperCase().trim().startsWith("ALTER")||sqlExe.toUpperCase().trim().startsWith("CREATE")||sqlExe.toUpperCase().trim().startsWith("DROP")||sqlExe.toUpperCase().trim().startsWith("COMMIT") || sqlExe.toUpperCase().trim().startsWith("ROLLBACK")) {
                                                sqlTransactionText.set("");
                                            }
                                            //System.out.println("sqlTransactionText:"+sqlTransactionText);

                                        
                                    }else{  //gbase
                                    /*
                                    if (sqlTransactionText.get().equals("")) {
                                        sqlTransactionText.set("begin work;\n");
                                    }
                                     */

                                            if (sqlExe.endsWith(";")) {
                                                sqlTransactionText.set(sqlTransactionText.get() + sqlExe.trim() + "\n");
                                            } else {
                                                sqlTransactionText.set(sqlTransactionText.get() + sqlExe.trim() + ";\n");
                                            }
                                            if (sqlExe.toUpperCase().trim().startsWith("COMMIT") || sqlExe.toUpperCase().trim().startsWith("ROLLBACK")) {
                                                sqlTransactionText.set("");
                                            }
                                            //System.out.println("sqlTransactionText:"+sqlTransactionText);

                            
                                    }
                                }


                            }
                            //oracle模式ddl执行失败也会把之前的事务提交了
                            else if(sqlSqlModeChoiceBox.getValue().equals("sqlmode=oracle")&&(sqlExe.toUpperCase().trim().startsWith("ALTER")||sqlExe.toUpperCase().trim().startsWith("CREATE")||sqlExe.toUpperCase().trim().startsWith("DROP"))){
                                sqlTransactionText.set("");
                            }


                            //sql执行完恢复初始状态

                            sql.setSqlStr("");
                            sql.setSqlEnd(false);
                            sql.setSqlType("");
                        }
                        //如果上一个以分号分隔的语句包含包的最后一句和下一个包的第一句，包的最后一句可能没有分号，那么分隔的两句后一句放入remainder作为下一个sql的开始
                        sqlChunk = "";
                        sqlContainsCommit = SqlParserUtil.sqlContrainCommit(sql.getSqlRemainder());
                    } while (sqlContainsCommit);
                }

                return null;
            }

        };

        // 任务完成时关闭进度窗口

        //所有任务都会成功，没有在线程中抛出错误表示失败，抛出错误后线程不再执行后续sql？
        sqlTask.setOnSucceeded(event1 -> {
            //sqlExecute_process_hbox显示可能会导致下层tableview列拖动卡死,已在外层添加一个stackpane
            isSqlRefresh = false;
            finishExecution(sqlSelectionRange[0], sqlSelectionRange[1]);


            //检查主键或rowid，表格是select而且当前不在事务中才检查主键并启用编辑
            //if(isSingleSql&&resultSetVBox.isVisible()&&sqlTransactionText.get().equals("")) {
            if (isSingleSql && resultSetVBox.isVisible()) {
                currentResultSetTabController.getPrimaryKeys(sqlExe);
            }
            //检查主键结束

        });
        sqlTask.setOnCancelled(event1 -> {
            isSqlRefresh = false;

            //sqlExecute_process_hbox显示可能会导致下层tableview列拖动卡死,已在外层添加一个stackpane
            finishExecution(sqlSelectionRange[0], sqlSelectionRange[1]);

        });

        sqlTask.setOnFailed(event1 -> {
            isSqlRefresh = false;

        });

        return sqlTask;
    }

    public Task<Void> createExplainTask() {

        sqlExecuteProcessStackPane.setVisible(true);
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {

        if (!SqlParserUtil.isSingleStatement(sqlText)) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert(I18n.t("common.error"), I18n.t("sql.explain.single_only"));
                    });

                } else {

                    try {
                        //updateMessage("Fetching... ");
                        //游标滚动到上次取数的位置
                        //sql_result.absolute(sqlFetchedRows);
                        sqlStatement = sqlConnect.getConn().prepareStatement("execute function ifx_explain(?)");

                        sqlStatement.setObject(1, sqlText);
                        ResultSet rs = sqlStatement.executeQuery();
                        rs.next();
                        Platform.runLater(() -> {
                            try {
                                    if (rs.getString(1) != null) {
                                        if (rs.getString(1).equals("Error 0")) {
                                            AlterUtil.CustomAlert(I18n.t("common.error"), I18n.t("sql.explain.not_supported"));
                                        } else {
                                            showExplainText(rs.getString(1));
                                            rs.close();
                                        }
                                    } else {
                                        AlterUtil.CustomAlert(I18n.t("common.error"), I18n.t("sql.explain.not_supported"));
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
                                AlterUtil.CustomAlert(I18n.t("common.error"), "[" + e.getErrorCode() + "]" + e.getMessage());
                            });
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                return null;
            }

        };

        sqlTask.setOnSucceeded(event1 -> {
            hideExecuteProcess();
        });
        sqlTask.setOnCancelled(event1 -> {
            hideExecuteProcess();
        });
        sqlTask.setOnFailed(event1 -> {
            hideExecuteProcess();
        });


        return sqlTask;
    }

    public Task<Void> createSqlModeTask(Connect sqlConnect, String sqlmode) {
        String sql = "";
        if (sqlmode.equals("sqlmode=gbase")) {
            sql = "set environment sqlmode 'gbase'";
        } else if (sqlmode.equals("sqlmode=oracle")) {
            sql = "set environment sqlmode 'oracle'";
        } else {
            sql = "set environment sqlmode 'mysql'";
        }
        String finalSql = sql;
        sqlTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    sqlStatement = sqlConnect.getConn().prepareStatement(finalSql);
                    sqlStatement.executeUpdate();
                } catch (SQLException e) {
                    if (e.getErrorCode() == -79730 || e.getErrorCode() == -79716) {
                        connectionDisconnected();
                    } else {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert(I18n.t("common.error"), "[" + e.getErrorCode() + "]" + e.getMessage());
                        });
                        log.error(e.getMessage(), e);
                    }
                    throw new Exception("ERROR");
                }
                return null;
            }
        };
        return sqlTask;
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

    public void initializeConnectList(){



        List connect_list = new ArrayList<Connect>();
        for (TreeItem<TreeData> ti : Main.mainController.databaseMetaTreeView.getRoot().getChildren()) {
            for (TreeItem<TreeData> t : ti.getChildren()) {
                if(t.getValue().getName().equals(sqlConnectChoiceBox.getSelectionModel().getSelectedItem().getName())){
                }else{
                    Connect newConnect = new Connect((Connect) t.getValue());
                    newConnect.setConn(null);
                    connect_list.add(newConnect);
                }
            }
        }
        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(connect_list);
        sqlConnectChoiceBox.getItems().retainAll(sqlConnectChoiceBox.getSelectionModel().getSelectedItem());
        sqlConnectChoiceBox.getItems().addAll(dbtypelist);

    }


}





