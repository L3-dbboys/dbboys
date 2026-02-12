package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.service.AdminService;
import com.dbboys.service.MetadataService;
import com.dbboys.util.*;
import com.dbboys.vo.Connect;
import com.dbboys.vo.HealthCheck;
import com.dbboys.vo.Index;
import com.jcraft.jsch.Session;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Transform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomInstanceTab extends CustomTab {
    private static final Logger log = LogManager.getLogger(CustomInstanceTab.class);
    private final AdminService adminService = new AdminService();
    private final MetadataService metadataService = new MetadataService();
    private List instanceInfo=new ArrayList();
    private Connect connect;
    private boolean infoTabClicked=false;
    // 为每个需要懒加载的 Tab 定义「已加载」标记
    private boolean infoTabLoaded = false;
    private boolean checkTabLoaded = false;
    private boolean logTabLoaded = false;
    private boolean spaceTabLoaded = false;
    private boolean paramsTabLoaded = false;
    private boolean startTabLoaded = false;
    // 存储 Tab 引用，用于刷新逻辑
    private CustomTab infoTab;
    private CustomTab checkTab;
    private CustomTab logTab;
    private CustomTab spaceTab;
    private CustomTab paramsTab;
    private CustomTab startTab;
    public TabPane mainTabPane;
    private Label instanceInfoLabel;
    private Button refreshButton;
    //private Session session;
    private String onlinelog;

    //checktab

    private CustomResultsetTableView checkTableView = new CustomResultsetTableView();
    private List<HealthCheck> checkDatalist = FXCollections.observableArrayList();//如果确认，返回更新后的list
    private StackPane checkStackPane;

    //configtab
    private CustomResultsetTableView configTableView = new CustomResultsetTableView();
    private List<ObservableList<String>> configDatalist = FXCollections.observableArrayList();//如果确认，返回更新后的list
    private StackPane configStackPane;

    //spacetab变量
    private CustomSpaceChart.ContextMenuListener menuListener;
    private CustomSpaceChart dbspaceChart;
    private CustomSpaceChart chunkChart;
    private CustomSpaceChart databaseChart;
    private CustomSpaceChart tabChart;
    private StackPane dbspaceStackPane;
    List<CustomSpaceChart.SpaceUsage> dbspaceChartList = new ArrayList<>();
    List<CustomSpaceChart.SpaceUsage> chunkChartList = new ArrayList<>();
    List<CustomSpaceChart.SpaceUsage> databaseChartList = new ArrayList<>();
    List<CustomSpaceChart.SpaceUsage> tabChartList = new ArrayList<>();
    List<List<CustomSpaceChart.SpaceUsage>> dataList = new ArrayList<>();
    String datafilePath="";



    //启停变量
    private String instanceStatus="";
    private Button startButton;
    private Button stopButton;
    private Label statusLabel;
    private StackPane startStackPane;
    private Label ipLabel;




    public CustomInstanceTab(Connect connect,int tabNum) {
        super("[instance]"+connect.getName());
        this.connect=connect;

        //实例信息tab初始化变量
        String instanceName=connect.getInfo().split("GBASEDBTSERVER")[1].split("\n")[0].trim();
        instanceInfoLabel=new Label("当前实例信息 ( IP："+connect.getIp()+"   端口："+connect.getPort()+"   实例名："+instanceName+" )");
        instanceInfoLabel.setStyle("-fx-font-size:9");
        SVGPath lockIcon=new SVGPath();
        lockIcon.setScaleX(0.45);
        lockIcon.setScaleY(0.45);
        lockIcon.setContent("M17 9.0078 L17 7.0078 Q17 5.9609 16.625 5.0391 Q16.2188 4.1328 15.5469 3.4609 Q14.8906 2.7734 13.9688 2.3984 Q13.0625 1.9922 12 1.9922 Q10.9531 1.9922 10.0312 2.3984 Q9.125 2.7734 8.4531 3.4609 Q7.7812 4.1328 7.3906 5.0391 Q7.0156 5.9609 7.0156 7.0078 L7.0156 9.0078 Q5.7188 9.0078 4.8594 9.8828 Q4.0156 10.7422 4.0156 12.0078 L4.0156 19.0078 Q4.0156 20.2734 4.8594 21.1484 Q5.7188 22.0078 7.0156 22.0078 L17 22.0078 Q18.2812 22.0078 19.1406 21.1484 Q20 20.2734 20 19.0078 L20 12.0078 Q20 10.7422 19.1406 9.8828 Q18.2812 9.0078 17 9.0078 L17 9.0078 ZM9 7.0078 Q9 5.7266 9.8594 4.8672 Q10.7344 4.0078 12 4.0078 Q13.2656 4.0078 14.125 4.8672 Q15 5.7266 15 7.0078 L15 9.0078 L9 9.0078 L9 7.0078 L9 7.0078 ZM13.1094 15.4922 Q13.1094 15.4922 13.0625 15.5547 Q13.0156 15.6172 13.0156 15.6172 L13.0156 16.9922 Q13.0156 17.4609 12.7344 17.7422 Q12.4531 18.0078 12 18.0078 Q11.5625 18.0078 11.2812 17.7422 Q11 17.4609 11 16.9922 L11 15.6172 Q10.5469 15.1484 10.5 14.5547 Q10.4531 13.9453 10.9062 13.5078 Q11.3438 13.0547 11.9375 13.0078 Q12.5469 12.9609 13.0156 13.4141 Q13.4531 13.7891 13.5 14.4297 Q13.5469 15.0547 13.1094 15.4922 L13.1094 15.4922 Z");
        lockIcon.setFill(Color.valueOf("#9f453c"));

        if(connect.getReadonly()){
            instanceInfoLabel.setGraphic(new Group(lockIcon));
            instanceInfoLabel.setContentDisplay(ContentDisplay.RIGHT);
            instanceInfoLabel.setTooltip(new Tooltip("当前连接只读，管理操作已禁用！"));
        }

        //UI
        checkTableView.setEditable(false);
        checkTableView.setSortPolicy((param) -> false);//禁用排序
        checkTableView.setRowFactory(tv -> {
            TableRow<HealthCheck> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    HealthCheck rowData = row.getItem();
                    if(rowData.getEntry().equals("实例运行日志")) {
                        mainTabPane.getSelectionModel().select(logTab);
                    }else
                    if(rowData.getCmdOutput()!=null&&!rowData.getCmdOutput().isEmpty()){
                        PopupWindowUtil.openCmdoutputPopupWindow(rowData.getCmdOutput());
                    }
                }
            });
            return row;
        });
        TableColumn<ObservableList<String>, Object> nameColumn = new TableColumn<ObservableList<String>, Object>("巡检项");
        nameColumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("entry"));
        nameColumn.setReorderable(false); // 禁用拖动
        nameColumn.setEditable(false);
        nameColumn.setReorderable(false);
        nameColumn.setPrefWidth(200);
        TableColumn<ObservableList<String>, Object> cmdColumn = new TableColumn<ObservableList<String>, Object>("巡检命令");
        cmdColumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        cmdColumn.setCellValueFactory(new PropertyValueFactory<>("cmd"));
        cmdColumn.setReorderable(false); // 禁用拖动
        cmdColumn.setEditable(false);
        cmdColumn.setReorderable(false);
        cmdColumn.setPrefWidth(100);
        TableColumn<ObservableList<String>, Object> valueColumn = new TableColumn<ObservableList<String>, Object>("正常值");
        valueColumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("healthValue"));
        valueColumn.setReorderable(false); // 禁用拖动
        valueColumn.setEditable(false);
        valueColumn.setReorderable(false);
        valueColumn.setPrefWidth(300);
        TableColumn<ObservableList<String>, Object> currentColumn = new TableColumn<ObservableList<String>, Object>("当前值");
        currentColumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        currentColumn.setCellValueFactory(new PropertyValueFactory<>("currentValue"));
        currentColumn.setReorderable(false); // 禁用拖动
        currentColumn.setEditable(false);
        currentColumn.setReorderable(false);
        currentColumn.setPrefWidth(300);

        TableColumn<ObservableList<String>, Object> resultColumn = new TableColumn<ObservableList<String>, Object>("巡检结论");
        resultColumn.setCellFactory(col -> new CustomCheckTableCell<ObservableList<String>, Object>());
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        resultColumn.setReorderable(false); // 禁用拖动
        resultColumn.setEditable(false);
        resultColumn.setReorderable(false);
        resultColumn.setPrefWidth(100);
        checkTableView.getColumns().addAll(nameColumn, cmdColumn, valueColumn, currentColumn, resultColumn);
        initDatalist(checkDatalist);
        checkTableView.getItems().addAll(checkDatalist);
        checkTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ImageView loading_icon = new ImageView(new Image("file:images/loading.gif"));
        loading_icon.setScaleX(0.7);
        loading_icon.setScaleY(0.7);
        checkStackPane = new StackPane(loading_icon);
        checkStackPane.getChildren().add(checkTableView);
        ((TableColumn<?, ?>) checkTableView.getColumns().get(0)).setMaxWidth(30);
        ((TableColumn<?, ?>) checkTableView.getColumns().get(0)).setMinWidth(30);
        checkStackPane.setStyle("-fx-background-color: #f0f0f0;");
        Button checkshotButton= new Button();
        SVGPath checkshotButtonIcon = new SVGPath();
        checkshotButtonIcon.setContent("M10.125 10.9922 Q11.2656 9.8516 12.8594 9.8516 Q14.4531 9.8516 15.5781 10.9922 Q16.7188 12.1172 16.7188 13.7109 Q16.7188 15.3047 15.5781 16.4453 Q14.4531 17.5703 12.8594 17.5703 Q11.2656 17.5703 10.125 16.4453 Q9 15.3047 9 13.7109 Q9 12.1172 10.125 10.9922 ZM22.2812 4.2891 Q23.7031 4.2891 24.7031 5.2891 Q25.7188 6.2891 25.7188 7.7109 L25.7188 19.7109 Q25.7188 21.1328 24.7031 22.1328 Q23.7031 23.1484 22.2812 23.1484 L3.4219 23.1484 Q2.0156 23.1484 1 22.1328 Q0 21.1328 0 19.7109 L0 7.7109 Q0 6.2891 1 5.2891 Q2.0156 4.2891 3.4219 4.2891 L6.4219 4.2891 L7.1094 2.4609 Q7.3594 1.8047 8.0312 1.3359 Q8.7188 0.8516 9.4219 0.8516 L16.2812 0.8516 Q17 0.8516 17.6719 1.3359 Q18.3438 1.8047 18.6094 2.4609 L19.2812 4.2891 L22.2812 4.2891 ZM8.6094 17.9609 Q10.375 19.7109 12.8438 19.7109 Q15.3281 19.7109 17.0938 17.9609 Q18.8594 16.1953 18.8594 13.7266 Q18.8594 11.2422 17.0938 9.4766 Q15.3281 7.7109 12.8438 7.7109 Q10.375 7.7109 8.6094 9.4766 Q6.8594 11.2422 6.8594 13.7266 Q6.8594 16.1953 8.6094 17.9609 Z");
        checkshotButtonIcon.setScaleX(0.35);
        checkshotButtonIcon.setScaleY(0.35);
        checkshotButtonIcon.setFill(Color.valueOf("#074675"));
        checkshotButton.setGraphic(new Group(checkshotButtonIcon));
        checkshotButton.setFocusTraversable(false);
        checkshotButton.getStyleClass().add("codearea-camera-button");
        checkStackPane.getChildren().add(checkshotButton);
        checkStackPane.setAlignment(checkshotButton, Pos.TOP_RIGHT);
        checkStackPane.setMargin(checkshotButton, new javafx.geometry.Insets(0, 15, 20, 20));
        checkshotButton.setOnAction(e->{
            SnapshotUtil.snapshotTableView(checkTableView);
        });

        //参数优化UI
        configTableView.setEditable(true);
        configTableView.setSortPolicy((param) -> false);//禁用排序
        TableColumn<ObservableList<String>, Object> configNameColumn = new TableColumn<ObservableList<String>, Object>("参数名");
        configNameColumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        configNameColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(1)));
        configNameColumn.setReorderable(false); // 禁用拖动
        configNameColumn.setEditable(false);
        configNameColumn.setReorderable(false);
        configNameColumn.setPrefWidth(300);
        TableColumn<ObservableList<String>, Object> configValueColumn = new TableColumn<ObservableList<String>, Object>("参数值");
        configValueColumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        configValueColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(2)));
        configValueColumn.setReorderable(false); // 禁用拖动
        configValueColumn.setEditable(true);
        configValueColumn.setReorderable(false);
        configValueColumn.setPrefWidth(500);

        configValueColumn.setOnEditCommit(event -> {
            String paramName=event.getRowValue().get(1).toString();
            Object oldvalue = event.getOldValue();
            //Object colvalue = event.getNewValue();
            //替换换行
            Object colvalue = event.getNewValue().toString();

            //替换换行后显示
            if(oldvalue.equals(colvalue)){
                return;
            }
            event.getRowValue().set(2, colvalue.toString());

            event.getTableView().refresh();
            String cmd;
            if(paramName.equals("BUFFERPOOL")||paramName.equals("VPCLASS")){
                cmd="sed -i \"s#^"+paramName+" *"+colvalue.toString().split(",")[0]+".*#"+paramName+" "+colvalue.toString().replace("$","\\$")+"#g\" $GBASEDBTDIR/etc/$ONCONFIG";
            }else{
                cmd="onmode -wf "+paramName+"=\""+colvalue.toString()+"\";sed -i \"s#^"+paramName+".*#"+paramName+" "+colvalue.toString().replace("$","\\$")+"#g\" $GBASEDBTDIR/etc/$ONCONFIG";
            }

            Task task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Session session=JschUtil.getConnect(connect);
                        String result = JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+ cmd,true);
                        JschUtil.disConnect(session);
                        //if (result != 0) throw new Exception("创建数据库空间失败，请检查日志错误！");
                        if(result.contains("has been changed to")){
                            Platform.runLater(()->{
                                NotificationUtil.showNotification(Main.mainController.notice_pane, "参数已修改生效！");
                            });
                        }else if(result.contains("shared memory not initialized")){
                            Platform.runLater(()->{
                                NotificationUtil.showNotification(Main.mainController.notice_pane, "配置文件已修改，数据库未启动，下次启动后生效！");
                            });
                        }else{
                            Platform.runLater(()->{
                                AlterUtil.CustomAlert("提醒", "参数已修改，请重启数据库生效！");
                            });
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);

                        //throw new Exception("ssh登录失败，请检查网络！");
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            };
            task.setOnFailed(event1->{
                event.getRowValue().set(2, oldvalue.toString());
                event.getTableView().refresh();
                String error = task.getException().getMessage();
                AlterUtil.CustomAlert("错误", error);
            });
            new Thread(task).start();

        });

        configTableView.getColumns().addAll(configNameColumn, configValueColumn);
        configTableView.getItems().addAll(configDatalist);
        configTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configStackPane = new StackPane();
        configStackPane.getChildren().add(configTableView);
        ((TableColumn<?, ?>) configTableView.getColumns().get(0)).setMaxWidth(30);
        ((TableColumn<?, ?>) configTableView.getColumns().get(0)).setMinWidth(30);
        configStackPane.setStyle("-fx-background-color: #f0f0f0;");
        Button configshotButton= new Button();
        SVGPath configshotButtonIcon = new SVGPath();
        configshotButtonIcon.setContent("M10.125 10.9922 Q11.2656 9.8516 12.8594 9.8516 Q14.4531 9.8516 15.5781 10.9922 Q16.7188 12.1172 16.7188 13.7109 Q16.7188 15.3047 15.5781 16.4453 Q14.4531 17.5703 12.8594 17.5703 Q11.2656 17.5703 10.125 16.4453 Q9 15.3047 9 13.7109 Q9 12.1172 10.125 10.9922 ZM22.2812 4.2891 Q23.7031 4.2891 24.7031 5.2891 Q25.7188 6.2891 25.7188 7.7109 L25.7188 19.7109 Q25.7188 21.1328 24.7031 22.1328 Q23.7031 23.1484 22.2812 23.1484 L3.4219 23.1484 Q2.0156 23.1484 1 22.1328 Q0 21.1328 0 19.7109 L0 7.7109 Q0 6.2891 1 5.2891 Q2.0156 4.2891 3.4219 4.2891 L6.4219 4.2891 L7.1094 2.4609 Q7.3594 1.8047 8.0312 1.3359 Q8.7188 0.8516 9.4219 0.8516 L16.2812 0.8516 Q17 0.8516 17.6719 1.3359 Q18.3438 1.8047 18.6094 2.4609 L19.2812 4.2891 L22.2812 4.2891 ZM8.6094 17.9609 Q10.375 19.7109 12.8438 19.7109 Q15.3281 19.7109 17.0938 17.9609 Q18.8594 16.1953 18.8594 13.7266 Q18.8594 11.2422 17.0938 9.4766 Q15.3281 7.7109 12.8438 7.7109 Q10.375 7.7109 8.6094 9.4766 Q6.8594 11.2422 6.8594 13.7266 Q6.8594 16.1953 8.6094 17.9609 Z");
        configshotButtonIcon.setScaleX(0.35);
        configshotButtonIcon.setScaleY(0.35);
        configshotButtonIcon.setFill(Color.valueOf("#074675"));
        configshotButton.setGraphic(new Group(configshotButtonIcon));
        configshotButton.setFocusTraversable(false);
        configshotButton.getStyleClass().add("codearea-camera-button");
        configStackPane.getChildren().add(configshotButton);
        configStackPane.setAlignment(configshotButton, Pos.TOP_RIGHT);
        configStackPane.setMargin(configshotButton, new javafx.geometry.Insets(0, 15, 20, 20));
        configshotButton.setOnAction(e->{
            SnapshotUtil.snapshotTableView(configTableView);
        });

        //初始化spacetab UI
        dbspaceChart =new CustomSpaceChart(dbspaceChartList, CustomSpaceChart.ColorMode.DBSPACE);
        Node dbspaceChartLegend = dbspaceChart.createLegend();
        Label spaceType=new Label("[T] 临时空间   [S] 智能大对象空间   [B] 简单大对象空间   [L] 空间大小已限制   [*k] 空间页大小为*KB");
        spaceType.setStyle("-fx-font-size: 9;-fx-padding: 0 0 5 0");
        VBox dbspaceChartVbox = new VBox(dbspaceChart,dbspaceChartLegend,spaceType,new Label("数据库空间使用情况图(GB)"));
        dbspaceChartVbox.setAlignment(Pos.CENTER);
        chunkChart =new CustomSpaceChart(chunkChartList, CustomSpaceChart.ColorMode.CHUNK);
        Node chunkChartLegend = chunkChart.createLegend();
        VBox chunkChartVbox = new VBox(chunkChart,chunkChartLegend,new Label("数据文件使用情况图(GB)"));
        chunkChartVbox.setAlignment(Pos.CENTER);
        databaseChart =new CustomSpaceChart(databaseChartList, CustomSpaceChart.ColorMode.DATABASE);
        Node databaseChartLegend = databaseChart.createLegend();
        VBox databaseChartVbox = new VBox(databaseChart,databaseChartLegend,new Label("数据库使用空间情况(GB)"));
        databaseChartVbox.setAlignment(Pos.CENTER);
        tabChart =new CustomSpaceChart(tabChartList, CustomSpaceChart.ColorMode.TABLE);
        Node tabChartLegend = tabChart.createLegend();
        VBox tabChartVbox = new VBox(tabChart,tabChartLegend,new Label("表/索引空间使用情况图TOP20(GB)"));
        tabChartVbox.setAlignment(Pos.CENTER);
        VBox charts=new VBox(50,dbspaceChartVbox,chunkChartVbox,databaseChartVbox,tabChartVbox);
        charts.setAlignment(Pos.CENTER);
        charts.setStyle("-fx-background-color: #fff;");
        ScrollPane scrollPane = new ScrollPane(charts);
        dbspaceStackPane=new StackPane(scrollPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-padding: 0;-fx-background-color: #f2f2f2");
        dbspaceStackPane.setStyle("-fx-background-color: #f0f0f0;");
        dbspaceStackPane.setAlignment(Pos.CENTER);
        Button spaceshotButton= new Button();
        SVGPath spaceshotButtonIcon = new SVGPath();
        spaceshotButtonIcon.setContent("M10.125 10.9922 Q11.2656 9.8516 12.8594 9.8516 Q14.4531 9.8516 15.5781 10.9922 Q16.7188 12.1172 16.7188 13.7109 Q16.7188 15.3047 15.5781 16.4453 Q14.4531 17.5703 12.8594 17.5703 Q11.2656 17.5703 10.125 16.4453 Q9 15.3047 9 13.7109 Q9 12.1172 10.125 10.9922 ZM22.2812 4.2891 Q23.7031 4.2891 24.7031 5.2891 Q25.7188 6.2891 25.7188 7.7109 L25.7188 19.7109 Q25.7188 21.1328 24.7031 22.1328 Q23.7031 23.1484 22.2812 23.1484 L3.4219 23.1484 Q2.0156 23.1484 1 22.1328 Q0 21.1328 0 19.7109 L0 7.7109 Q0 6.2891 1 5.2891 Q2.0156 4.2891 3.4219 4.2891 L6.4219 4.2891 L7.1094 2.4609 Q7.3594 1.8047 8.0312 1.3359 Q8.7188 0.8516 9.4219 0.8516 L16.2812 0.8516 Q17 0.8516 17.6719 1.3359 Q18.3438 1.8047 18.6094 2.4609 L19.2812 4.2891 L22.2812 4.2891 ZM8.6094 17.9609 Q10.375 19.7109 12.8438 19.7109 Q15.3281 19.7109 17.0938 17.9609 Q18.8594 16.1953 18.8594 13.7266 Q18.8594 11.2422 17.0938 9.4766 Q15.3281 7.7109 12.8438 7.7109 Q10.375 7.7109 8.6094 9.4766 Q6.8594 11.2422 6.8594 13.7266 Q6.8594 16.1953 8.6094 17.9609 Z");
        spaceshotButtonIcon.setScaleX(0.35);
        spaceshotButtonIcon.setScaleY(0.35);
        spaceshotButtonIcon.setFill(Color.valueOf("#074675"));
        spaceshotButton.setGraphic(new Group(spaceshotButtonIcon));
        spaceshotButton.setFocusTraversable(false);
        spaceshotButton.getStyleClass().add("codearea-camera-button");
        dbspaceStackPane.getChildren().add(spaceshotButton);
        dbspaceStackPane.setAlignment(spaceshotButton, Pos.TOP_RIGHT);
        dbspaceStackPane.setMargin(spaceshotButton, new javafx.geometry.Insets(0, 15, 20, 20));
        spaceshotButton.setOnAction(e->{
            SnapshotUtil.snapshotNode(scrollPane.getContent());
        });
        menuListener = new CustomSpaceChart.ContextMenuListener() {
            // -------------------------- onViewDetail 实现 --------------------------
            @Override
            public void onCreateDbspace(CustomSpaceChart.SpaceUsage spaceUsage,boolean isAddFile) {
                //加载指示器
                ImageView imageView = new ImageView(new Image("file:images/loading.gif"));
                imageView.setFitWidth(12);
                imageView.setFitHeight(12);
                Button processStopButton = new Button("");
                SVGPath processStopButtonIcon = new SVGPath();
                processStopButtonIcon.setScaleX(0.7);
                processStopButtonIcon.setScaleY(0.7);
                processStopButtonIcon.setContent("M19.2031 6.0078 L19.2031 17.7734 Q19.2031 18.3516 18.7812 18.7734 Q18.3594 19.1953 17.7656 19.1953 L6 19.1953 Q5.5156 19.1953 5.1562 18.8516 Q4.8125 18.4922 4.8125 18.0078 L4.8125 6.2422 Q4.8125 5.6484 5.2344 5.2266 Q5.6562 4.8047 6.2344 4.8047 L18 4.8047 Q18.5 4.8047 18.8438 5.1641 Q19.2031 5.5078 19.2031 6.0078 L19.2031 6.0078 Z");
                processStopButtonIcon.setFill(Color.valueOf("#9f453c"));
                processStopButton.setGraphic(new Group(processStopButtonIcon));
                Label runningLabel=new Label(" 正在初始化...0.00%");
                HBox imageHBox = new HBox(imageView, runningLabel, processStopButton);
                imageHBox.setStyle("-fx-background-color: white;-fx-background-radius: 2;-fx-padding: 0 0 0 5");
                imageHBox.setAlignment(Pos.CENTER);
                imageHBox.setMaxHeight(15);
                //imageHBox.setMaxWidth(100);
                processStopButton.setFocusTraversable(false);
                processStopButton.getStyleClass().add("little-custom-button");
                HBox backgroupHbox=new HBox(imageHBox);
                backgroupHbox.setAlignment(Pos.CENTER);
                backgroupHbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1);-fx-background-radius: 2;");
                backgroupHbox.setVisible(false);

                Dialog<ButtonType> dialog=new Dialog<>();

                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
                Button commit = (Button) dialog.getDialogPane().lookupButton(ButtonType.FINISH);
                Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                if(isAddFile){
                    dialog.setTitle("增加数据文件");
                    commit.setText("扩容");
                }else{
                    dialog.setTitle("创建数据库空间");
                    commit.setText("创建");
                }
                cancelBtn.setText("取消");
                dialog.initOwner(Main.scene.getWindow());
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(5);
                grid.setPadding(new Insets(10));


                Label spaceTypeLabel=new Label("空间类型");
                SVGPath spaceTypeLabelIcon=new SVGPath();
                spaceTypeLabelIcon.setContent("M12 6 L22.5 6 L22.5 7.5 L12 7.5 L12 6 ZM12 16.5 L22.5 16.5 L22.5 18 L12 18 L12 16.5 ZM7.5 10.5 L3 10.5 Q2.3906 10.5 1.9375 10.0625 Q1.5 9.6094 1.5 9 L1.5 4.5 Q1.5 3.8906 1.9375 3.4531 Q2.3906 3 3 3 L7.5 3 Q8.1094 3 8.5469 3.4531 Q9 3.8906 9 4.5 L9 9 Q9 9.6094 8.5469 10.0625 Q8.1094 10.5 7.5 10.5 L7.5 10.5 ZM3 4.5 L3 9 L7.5 9 L7.5 4.5 L3 4.5 ZM7.5 21 L3 21 Q2.3906 21 1.9375 20.5625 Q1.5 20.1094 1.5 19.5 L1.5 15 Q1.5 14.3906 1.9375 13.9531 Q2.3906 13.5 3 13.5 L7.5 13.5 Q8.1094 13.5 8.5469 13.9531 Q9 14.3906 9 15 L9 19.5 Q9 20.1094 8.5469 20.5625 Q8.1094 21 7.5 21 L7.5 21 ZM3 15 L3 19.5 L7.5 19.5 L7.5 15 L3 15 Z");
                spaceTypeLabelIcon.setScaleX(0.5);
                spaceTypeLabelIcon.setScaleY(0.5);
                spaceTypeLabelIcon.setFill(Color.valueOf("#888"));
                spaceTypeLabel.setGraphic(new Group(spaceTypeLabelIcon));
                ChoiceBox<String> spaceTypeChoiceBox = new ChoiceBox();
                spaceTypeChoiceBox.getItems().addAll("标准空间","临时空间","智能大对象空间");
                spaceTypeChoiceBox.getSelectionModel().select(0);

                Label nameLabel=new Label("空间名称");
                SVGPath nameLabelIcon=new SVGPath();
                nameLabelIcon.setContent("M5.9531 3.5938 Q6.3438 3.5938 6.4844 3.9844 L8.6406 9.9844 Q8.7344 10.3125 8.5156 10.6094 Q8.2969 10.8906 7.9375 10.8281 Q7.5781 10.75 7.4844 10.4219 L7.2031 9.5938 L4.4688 9.5938 L4.1719 10.4219 Q4.0781 10.6562 3.8281 10.7344 Q3.5938 10.7969 3.375 10.7344 Q3.1719 10.6562 3.0625 10.4375 Q2.9688 10.2188 3.0312 9.9844 L5.375 3.9844 Q5.5156 3.5938 5.9531 3.5938 ZM4.9375 8.4062 L6.7656 8.4062 L5.9062 5.9062 L4.9375 8.4062 ZM10.2188 3.5938 L12 3.5938 Q12.7188 3.5938 13.1719 3.8594 Q13.625 4.125 13.8906 4.5938 Q14.1562 5.0469 14.1562 5.7656 Q14.1562 6.4844 13.7812 7.0156 Q14.0625 7.2031 14.25 7.5312 Q14.5469 8.0156 14.5469 8.6875 Q14.5469 9.9375 13.5781 10.4688 Q13.0156 10.7969 12.2812 10.7969 L10.2188 10.7969 Q9.9375 10.7969 9.7656 10.6406 Q9.5938 10.4688 9.5938 10.2188 L9.5938 4.1719 Q9.5938 3.9375 9.7656 3.7656 Q9.9375 3.5938 10.1719 3.5938 L10.2188 3.5938 ZM12 4.7969 L10.7969 4.7969 L10.7969 6.5781 L12.0469 6.5781 Q12.3906 6.5781 12.6719 6.4062 Q12.9531 6.2344 12.9531 5.7188 Q12.9531 5.3281 12.8594 5.1562 Q12.7656 4.9844 12.5938 4.8906 Q12.4375 4.7969 12 4.7969 ZM10.7969 7.7812 L10.7969 9.5938 L12.2812 9.5938 Q12.7188 9.5938 13.0312 9.4062 Q13.3438 9.2188 13.3438 8.6875 Q13.3438 8.5 13.2969 8.2969 L13.2031 8.1562 Q13.1562 8.0156 12.9531 7.9219 Q12.7188 7.7812 12.2344 7.8281 L10.7969 7.8281 L10.7969 7.7812 ZM20.875 5.4219 Q20.4531 4.2656 19.625 3.8438 Q19.1562 3.5938 18.2812 3.5938 L18.2344 3.5938 Q17.3281 3.5938 16.6562 4.1719 Q16.1719 4.6094 15.875 5.375 Q15.5938 6.1406 15.5938 6.9062 L15.5938 7.0156 Q15.5938 8.2969 15.875 9.0312 Q16.1719 9.75 16.7031 10.1719 Q17.2812 10.7031 18.1875 10.7969 Q18.8125 10.8438 19.4844 10.6094 Q20.4531 10.2656 20.875 9.3125 Q21.0312 8.9688 20.8281 8.6875 Q20.6406 8.4062 20.2812 8.4375 Q19.9219 8.4531 19.7812 8.7812 Q19.5312 9.3125 19.0625 9.5 Q18.7188 9.6406 18.3438 9.5938 Q17.7656 9.5469 17.4531 9.2656 Q17.1406 8.9688 17.0469 8.6406 Q16.7969 8.0625 16.7969 6.9531 L16.7969 6.8594 Q16.8438 6.3438 17.0156 5.8438 Q17.1875 5.3281 17.4688 5.0625 Q17.7656 4.7969 18.2344 4.7969 Q18.8594 4.7969 19.1094 4.8906 Q19.4844 5.1406 19.7812 5.8594 Q19.8281 6.0469 20.0625 6.1719 Q20.2969 6.2812 20.5156 6.1875 Q20.7344 6.0938 20.8438 5.875 Q20.9688 5.6562 20.875 5.4219 ZM17.0469 14.6406 Q16.8438 14.8281 16.7656 15.125 Q16.7031 15.4062 16.3594 15.4844 Q16.0312 15.5469 15.7656 15.3125 Q15.5 15.0781 15.5938 14.7344 Q15.7969 14.1562 16.2188 13.7344 Q16.8438 13.2031 17.8281 13.2031 Q18.8125 13.2031 19.4062 13.8125 Q20.0156 14.4062 20.0156 15.2031 Q20.0156 15.9844 19.5781 16.5625 L19.3906 16.75 Q19.5781 16.8906 19.7344 17.0938 Q20.0156 17.5156 20.0156 18.1406 Q20.0156 19.2969 19.2969 19.9219 Q18.7188 20.4062 17.8438 20.4062 Q16.9844 20.4062 16.4375 20.0469 Q15.8906 19.6875 15.6406 18.9062 Q15.5469 18.6719 15.6719 18.4531 Q15.7969 18.2344 16.0312 18.1406 Q16.2656 18.0469 16.4844 18.1719 Q16.7031 18.2812 16.7656 18.5 Q16.8438 18.7188 16.9375 18.8594 L17.0469 19.0156 Q17.1406 19.1094 17.3281 19.1562 Q17.5156 19.2031 17.8125 19.2031 Q18.2812 19.2031 18.5469 18.9688 Q18.8125 18.7188 18.8125 18.1406 Q18.8594 17.9531 18.7344 17.7812 Q18.625 17.6094 18.4375 17.5625 Q18.0938 17.375 17.5625 17.375 Q17.2812 17.375 17.1094 17.2188 Q16.9375 17.0469 16.9375 16.8125 Q16.9375 16.5625 17.1094 16.375 Q17.2812 16.1719 17.5625 16.1719 Q18.3438 16.1719 18.625 15.8438 Q18.8125 15.5938 18.8125 15.1719 Q18.8125 14.9219 18.5781 14.6719 Q18.3438 14.4062 17.8281 14.4062 Q17.3281 14.4062 17.0469 14.6406 ZM10.7969 15.4531 Q10.7969 15.0312 11.0781 14.7188 Q11.375 14.4062 11.8125 14.4062 Q12.4375 14.4062 12.7188 14.8594 Q13.0156 15.3125 12.625 15.9375 Q12.4375 16.2656 11.9531 16.6562 L10.9844 17.3281 Q10.375 17.8125 10.0312 18.2812 Q9.5938 18.9531 9.5938 19.8281 Q9.5938 20.0625 9.7656 20.2344 Q9.9375 20.4062 10.1719 20.4062 L13.4375 20.4062 Q13.6875 20.4062 13.8438 20.2344 Q14.0156 20.0625 14.0156 19.7969 Q14.0156 19.5312 13.8438 19.375 Q13.6875 19.2031 13.4375 19.2031 L10.8906 19.2031 Q11.0938 18.7656 11.7188 18.2812 L12.7188 17.5625 Q13.3438 17.0938 13.625 16.5625 Q14.0156 15.9375 14.0156 15.3125 Q14.0156 14.6875 13.7031 14.2188 Q13.3906 13.7344 12.8906 13.4688 Q12.3906 13.2031 11.8125 13.2031 Q10.8438 13.2031 10.2188 13.9062 Q9.5938 14.5938 9.5938 15.4531 Q9.5938 15.7031 9.7656 15.875 Q9.9375 16.0312 10.2031 16.0312 Q10.4688 16.0312 10.625 15.875 Q10.7969 15.7031 10.7969 15.4531 ZM5.2812 16.4688 Q5.6562 16.2188 6 15.8906 L6 19.7812 Q6 20.0625 6.1719 20.2344 Q6.3438 20.4062 6.5938 20.4062 Q6.8594 20.4062 7.0312 20.2344 Q7.2031 20.0625 7.2031 19.7812 L7.2031 13.7812 Q7.2031 13.5781 7.0469 13.4219 Q6.9062 13.25 6.6719 13.2031 Q6.4375 13.1562 6.2344 13.2969 Q6.0469 13.4375 6 13.6875 Q5.9062 14.1094 5.5156 14.625 Q5.1406 15.125 4.6562 15.5 Q4.3125 15.6406 4.3438 16.0156 Q4.375 16.375 4.6719 16.5469 Q4.9844 16.7031 5.2812 16.4688 Z");
                nameLabelIcon.setScaleX(0.45);
                nameLabelIcon.setScaleY(0.45);
                nameLabelIcon.setFill(Color.valueOf("#888"));
                nameLabel.setGraphic(new Group(nameLabelIcon));
                CustomUserTextField nameTextField = new CustomUserTextField();
                nameTextField.setMinWidth(240);
                nameTextField.setPromptText("字母和数字，不允许空格");


                Label filePathLabel=new Label("文件路径");
                SVGPath filePathLabelIcon=new SVGPath();
                filePathLabelIcon.setContent("M20.0469 6.7656 L14.9844 1.7031 Q14.8906 1.6094 14.75 1.5625 Q14.6094 1.5 14.4688 1.5 L4.5 1.5 Q4.2031 1.5 3.9688 1.7188 Q3.75 1.9375 3.75 2.25 L3.75 21.75 Q3.75 22.0469 3.9688 22.2812 Q4.2031 22.5 4.5 22.5 L19.5 22.5 Q19.8125 22.5 20.0312 22.2812 Q20.25 22.0469 20.25 21.75 L20.25 7.2812 Q20.25 7.1406 20.1875 7 Q20.1406 6.8594 20.0469 6.7656 L20.0469 6.7656 ZM18.5156 7.6406 L14.1094 7.6406 L14.1094 3.2344 L18.5156 7.6406 ZM18.5625 20.8125 L5.4375 20.8125 L5.4375 3.1875 L12.5156 3.1875 L12.5156 8.25 Q12.5156 8.6406 12.8125 8.9375 Q13.1094 9.2344 13.5 9.2344 L13.5 9.2344 L18.5625 9.2344 L18.5625 20.8125 Z");
                filePathLabelIcon.setScaleX(0.5);
                filePathLabelIcon.setScaleY(0.5);
                filePathLabelIcon.setFill(Color.valueOf("#888"));
                filePathLabel.setGraphic(new Group(filePathLabelIcon));
                CustomUserTextField filePathTextField = new CustomUserTextField();
                for(CustomSpaceChart.SpaceUsage u:chunkChartList){
                    int lastSlashIndex = u.getName().lastIndexOf("/");
                    // 若截取后仍只有根目录（如 "/opt//" → 截取后 "/opt" → lastSlashIndex=4 → 返回 "/opt"）
                    datafilePath=u.getName().substring(0, lastSlashIndex + 1);
                    //break;
                }
                filePathTextField.setPromptText("根据空间名称自动填充");

                nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    nameTextField.setText(newValue.replace(" ", ""));
                    filePathTextField.setText(datafilePath+nameTextField.getText()+"chk001");

                });
                filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filePathTextField.setText(newValue.replace(" ", ""));
                });



                Label pagesizeLabel=new Label("页大小");
                SVGPath pagesizeLabelIcon=new SVGPath();
                pagesizeLabelIcon.setContent("M7.2031 2.4062 L16.7969 2.4062 Q17.8125 2.4062 18.5 3.1094 Q19.2031 3.7969 19.2031 4.7969 L19.2031 19.2031 Q19.2031 20.2031 18.5 20.9062 Q17.8125 21.5938 16.7969 21.5938 L7.2031 21.5938 Q6.1875 21.5938 5.4844 20.9062 Q4.7969 20.2031 4.7969 19.2031 L4.7969 4.7969 Q4.7969 3.7969 5.4844 3.1094 Q6.1875 2.4062 7.2031 2.4062 ZM7.2031 3.5938 Q6.7188 3.5938 6.3594 3.9531 Q6 4.3125 6 4.7969 L6 19.2031 Q6 19.6875 6.3594 20.0469 Q6.7188 20.4062 7.2031 20.4062 L16.7969 20.4062 Q17.2812 20.4062 17.6406 20.0469 Q18 19.6875 18 19.2031 L18 4.7969 Q18 4.3125 17.6406 3.9531 Q17.2812 3.5938 16.7969 3.5938 L7.2031 3.5938 ZM15.5938 7.7812 Q15.5938 8.0156 15.4531 8.1875 Q15.3125 8.3594 15.125 8.4062 L9.0312 8.4062 Q8.7812 8.4062 8.5938 8.2344 Q8.4062 8.0625 8.4062 7.8438 Q8.4062 7.625 8.5156 7.4375 Q8.6406 7.25 8.875 7.2031 L15.0312 7.2031 Q15.2656 7.2031 15.4219 7.375 Q15.5938 7.5312 15.5938 7.7812 ZM15.5938 12 Q15.5938 12.2344 15.4531 12.3906 Q15.3125 12.5312 15.125 12.5781 L9.0312 12.5781 Q8.7812 12.625 8.5938 12.4531 Q8.4062 12.2812 8.4062 12.0469 Q8.4062 11.8125 8.5156 11.6406 Q8.6406 11.4688 8.875 11.4219 L15.0312 11.375 Q15.2656 11.375 15.4219 11.5781 Q15.5938 11.7656 15.5938 12 ZM15.5938 16.1719 Q15.5938 16.4219 15.4531 16.5938 Q15.3125 16.75 15.125 16.7969 L9.0312 16.7969 Q8.7812 16.7969 8.5938 16.6406 Q8.4062 16.4688 8.4062 16.25 Q8.4062 16.0312 8.5156 15.8438 Q8.6406 15.6406 8.875 15.5938 L15.0312 15.5938 Q15.2656 15.5938 15.4219 15.7656 Q15.5938 15.9375 15.5938 16.1719 Z");
                pagesizeLabelIcon.setScaleX(0.6);
                pagesizeLabelIcon.setScaleY(0.5);
                pagesizeLabelIcon.setFill(Color.valueOf("#888"));
                pagesizeLabel.setGraphic(new Group(pagesizeLabelIcon));
                ChoiceBox<String> pagesizeChoiceBox = new ChoiceBox();
                pagesizeChoiceBox.getItems().addAll("2k","4k","6k","8k","10k","12k","14k","16k(推荐)");
                pagesizeChoiceBox.getSelectionModel().select(7);


                Label sizeLabel=new Label("大小(KB)");
                SVGPath sizeLabelIcon=new SVGPath();
                sizeLabelIcon.setContent("M7.5 18 L7.5 0 L4.5 0 L4.5 18 L0.75 18 L6 23.25 L11.25 18 L7.5 18 ZM20.25 24 Q19.9531 24 19.7188 23.7812 Q19.5 23.5625 19.5 23.25 L19.5 15 L18.75 15 Q18.4531 15 18.2188 14.7812 Q18 14.5469 18 14.25 Q18 13.9375 18.2188 13.7188 Q18.4531 13.5 18.75 13.5 L20.25 13.5 Q20.5625 13.5 20.7812 13.7188 Q21 13.9375 21 14.25 L21 23.25 Q21 23.5625 20.7812 23.7812 Q20.5625 24 20.25 24 ZM21.75 0 L17.25 0 Q16.9531 0 16.7188 0.2188 Q16.5 0.4375 16.5 0.75 L16.5 5.25 Q16.5 5.5469 16.7188 5.7812 Q16.9531 6 17.25 6 L21 6 L21 9 L17.25 9 Q16.9531 9 16.7188 9.2188 Q16.5 9.4375 16.5 9.75 Q16.5 10.0469 16.7188 10.2812 Q16.9531 10.5 17.25 10.5 L21.75 10.5 Q22.0625 10.5 22.2812 10.2812 Q22.5 10.0469 22.5 9.75 L22.5 0.75 Q22.5 0.4375 22.2812 0.2188 Q22.0625 0 21.75 0 ZM18 1.5 L21 1.5 L21 4.5 L18 4.5 L18 1.5 Z");
                sizeLabelIcon.setScaleX(0.45);
                sizeLabelIcon.setScaleY(0.45);
                sizeLabelIcon.setFill(Color.valueOf("#888"));
                sizeLabel.setGraphic(new Group(sizeLabelIcon));
                CustomUserTextField sizeTextField = new CustomUserTextField();

                sizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        sizeTextField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                });
                sizeTextField.setPromptText("数字");


                if(isAddFile){
                    nameTextField.setText(spaceUsage.getName());
                    String chunkName=spaceUsage.getName()+"chk";

                    for (int i = 1; i <= 9999999; i++) {
                        Boolean isExists = false;
                        for(CustomSpaceChart.SpaceUsage u:chunkChartList){
                            int lastSlashIndex = u.getName().lastIndexOf("/");
                            if (u.getName().substring(lastSlashIndex + 1,u.getName().length()).equals(chunkName+String.format("%03d", i))) {
                                isExists = true;
                                break;
                            }
                        }
                        if (!isExists) {
                            chunkName +=String.format("%03d", i);
                            break;
                        }
                    }

                    int size=0;
                    for(CustomSpaceChart.SpaceUsage u:chunkChartList){
                        if(u.getLabel().trim().endsWith("[ "+spaceUsage.getName()+" ]")){
                            size=  u.getTotalPages();
                           // break;
                        }
                    }
                    sizeTextField.setText(String.valueOf(size*2));
                    filePathTextField.setText(datafilePath+chunkName);
                }

                commit.disableProperty().bind(backgroupHbox.visibleProperty());

                commit.addEventFilter(ActionEvent.ACTION, event -> {
                    event.consume();
                    if (nameTextField.getText().trim().isEmpty()) {
                        nameTextField.requestFocus();
                    } else if (filePathTextField.getText().trim().isEmpty()) {
                        filePathTextField.requestFocus();
                    } else if (sizeTextField.getText().trim().isEmpty()) {
                        sizeTextField.requestFocus();
                    } else {
                        if(!isAddFile) {
                            for (CustomSpaceChart.SpaceUsage u : dbspaceChartList) {
                                if (u.getName().equals(nameTextField.getText())) {
                                    AlterUtil.CustomAlert("错误", "空间\"" + nameTextField.getText() + "\"已存在，请使用其他空间名！");
                                    return;
                                }
                            }
                        }
                        for(CustomSpaceChart.SpaceUsage u:chunkChartList){
                            if(u.getName().equals(filePathTextField.getText())){
                                AlterUtil.CustomAlert("错误","数据文件\""+filePathTextField.getText()+"\"已存在，请使用其他数据文件路径！");
                                return;
                            }
                        }
                        // 加载指示器


                        String cmd="";
                        String pagesize="";
                        switch (pagesizeChoiceBox.getSelectionModel().getSelectedIndex()){
                            case 0:
                                pagesize="2";
                                break;
                            case 1:
                                pagesize="4";
                                break;
                            case 2:
                                pagesize="6";
                                break;
                            case 3:
                                pagesize="8";
                                break;
                            case 4:
                                pagesize="10";
                                break;
                            case 5:
                                pagesize="12";
                                break;
                            case 6:
                                pagesize="14";
                                break;
                            case 7:
                                pagesize="16";
                                break;
                            default:
                                break;
                        }
                        if(isAddFile){
                            cmd="onspaces -a  "+nameTextField.getText()+" -p "+filePathTextField.getText()+" -o 0 -s "+sizeTextField.getText();
                        }else{
                            switch (spaceTypeChoiceBox.getSelectionModel().getSelectedIndex()){
                                case 0:
                                    cmd="onspaces -c -d "+nameTextField.getText()+" -p "+filePathTextField.getText()+" -o 0 -s "+sizeTextField.getText()+" -k "+pagesize;
                                    break;
                                case 1:
                                    cmd="onspaces -c -d "+nameTextField.getText()+" -p "+filePathTextField.getText()+" -o 0 -s "+sizeTextField.getText() +" -k "+pagesize+" -t";
                                    break;
                                case 2:
                                    cmd="onspaces -c -S "+nameTextField.getText()+" -p "+filePathTextField.getText()+" -o 0 -s "+sizeTextField.getText()+" -Df \"LOGGING = ON, AVG_LO_SIZE=1\"";
                                default:
                                    break;
                            }
                        }

                        cmd="touch "+filePathTextField.getText()+"&&chown gbasedbt:gbasedbt "+filePathTextField.getText()+"&&chmod 660 "+filePathTextField.getText()+"&&"+cmd;
                        String finalCmd = cmd;

                        Task task = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                try {
                                    Session session=JschUtil.getConnect(connect);
                                    String result = JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+ finalCmd);
                                    JschUtil.disConnect(session);
                                    //if (result != 0) throw new Exception("创建数据库空间失败，请检查日志错误！");
                                    if(!(result.contains("Space successfully added")||result.contains("Chunk successfully added"))){
                                        throw new Exception(result);
                                    }
                                } catch (Exception e) {
                                    //throw new Exception("ssh登录失败，请检查网络！");
                                    throw new RuntimeException(e);
                                }
                                return null;
                            }
                        };

                        Task processTask = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                try {
                                    Session session=JschUtil.getConnect(connect);
                                    Double currentSize=0.0;
                                    while(currentSize<Double.parseDouble(sizeTextField.getText())){
                                        if(isCancelled())break;
                                        Thread.sleep(100);
                                        String result = JschUtil.executeCommand(session,"/usr/bin/du -s "+filePathTextField.getText()+" |awk '{print $1}'");
                                        try{
                                            currentSize=Double.parseDouble(result);
                                        }catch(Exception e){

                                        }
                                        Double finalResult = currentSize;
                                        Platform.runLater(()->{
                                            runningLabel.setText(" 正在初始化..."+String.format("%.2f",Math.min(1,finalResult/Double.parseDouble(sizeTextField.getText()))*100)+"%");
                                        });

                                    }

                                    JschUtil.disConnect(session);

                                } catch (Exception e) {
                                    //throw new Exception("ssh登录失败，请检查网络！");
                                    throw new RuntimeException(e);
                                }
                                return null;
                            }
                        };
                        processTask.setOnSucceeded(event1->{
                            runningLabel.setText(" 正在准备，请稍等...");
                        });
                        task.setOnSucceeded(event1 -> {
                            backgroupHbox.setVisible(false);
                            processTask.cancel();
                            cancelBtn.fire();
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "空间创建/扩容成功！");
                            refreshButton.fire();
                        });
                        task.setOnFailed(event1 -> {
                            processTask.cancel();
                            backgroupHbox.setVisible(false);
                            String error = task.getException().getMessage();
                            AlterUtil.CustomAlert("错误", error);
                        });
                        processStopButton.setOnAction(event1->{
                            runningLabel.setText(" 正在初始化...0.00%");
                            processTask.cancel();
                            task.cancel();
                            backgroupHbox.setVisible(false);
                            Task stopTask = new Task<>() {
                                @Override
                                protected Void call() throws Exception {
                                    try {
                                        Session session=JschUtil.getConnect(connect);
                                        //取消操作不删除文件，确保弹窗关闭时触发取消操作导致已添加到空间的数据文件被误删除
                                        //int result = JschUtil.executeCommandWithExitStatus(session,"ps -ef |grep onspaces|grep -v grep |awk '{print \"kill -9 \"$2}' |sh && rm -rf "+filePathTextField.getText());
                                        int result = JschUtil.executeCommandWithExitStatus(session,"ps -ef |grep onspaces|grep -v grep |awk '{print \"kill -9 \"$2}' |sh ");

                                        JschUtil.disConnect(session);
                                        //if (result != 0) throw new Exception("创建数据库空间失败，请检查日志错误！");
                                        if(result!=0){
                                            throw new Exception("停止创建空间失败！");
                                        }
                                    } catch (Exception e) {
                                        //throw new Exception("ssh登录失败，请检查网络！");
                                        throw new RuntimeException(e);
                                    }
                                    return null;
                                }
                            };
                            new Thread(stopTask).start();
                        });
                        dialog.setOnCloseRequest(event1 -> {
                            processStopButton.fire();
                        });
                        cancelBtn.setOnAction(event1->{
                            processStopButton.fire();
                        });
                        backgroupHbox.setVisible(true);
                        new Thread(task).start();
                        new Thread(processTask).start();
                    }
                });

                if(isAddFile){
                    grid.add(nameLabel, 0, 0);
                    grid.add(nameTextField, 1, 0);
                    grid.add(filePathLabel, 0, 1);
                    grid.add(filePathTextField, 1, 1);
                    grid.add(sizeLabel, 0, 2);
                    grid.add(sizeTextField, 1, 2);
                }else {
                    grid.add(spaceTypeLabel, 0, 0);
                    grid.add(spaceTypeChoiceBox, 1, 0);
                    grid.add(nameLabel, 0, 1);
                    grid.add(nameTextField, 1, 1);
                    grid.add(filePathLabel, 0, 2);
                    grid.add(filePathTextField, 1, 2);
                    grid.add(pagesizeLabel, 0, 3);
                    grid.add(pagesizeChoiceBox, 1, 3);
                    grid.add(sizeLabel, 0, 4);
                    grid.add(sizeTextField, 1, 4);
                }
                StackPane stackPane = new StackPane(grid, backgroupHbox);
                dialog.getDialogPane().setContent(stackPane);

                dialog.showAndWait();
            }

            // -------------------------- 其他接口方法实现 --------------------------
            @Override
            public void onDropDbspace(CustomSpaceChart.SpaceUsage spaceUsage) {
                // 「删除数据库空间」的业务逻辑（示例：弹出确认弹窗）
                if(AlterUtil.CustomAlertConfirm("删除空间","确定要删除空间\""+spaceUsage.getName()+"\"吗？")){
                    String cmd="onspaces -d "+spaceUsage.getName()+" -y ";

                    for(CustomSpaceChart.SpaceUsage u:chunkChartList){
                        if(u.getLabel().trim().endsWith("[ "+spaceUsage.getName()+" ]")){
                            cmd+="&& rm -rf "+u.getName();
                        }
                    }
                    String finalCmd = cmd;
                    Task task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                Session session=JschUtil.getConnect(connect);
                                String result = JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+ finalCmd);
                                JschUtil.disConnect(session);
                                if(!result.contains("Space successfully dropped")) {
                                        throw new Exception(result);
                                }
                            } catch (Exception e) {
                                //throw new Exception("ssh登录失败，请检查网络！");
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    };
                    task.setOnSucceeded(event1 -> {
                        //cancelBtn.fire();
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "空间\""+spaceUsage.getName()+"\"已删除！");
                        refreshButton.fire();
                    });
                    task.setOnFailed(event1 -> {
                        String error = task.getException().getMessage();
                        AlterUtil.CustomAlert("错误", error);
                    });
                    new Thread(task).start();
                }
                // 后续可添加：调用删除接口、刷新图表数据等逻辑
            }


            @Override
            public void onDropDatafile(CustomSpaceChart.SpaceUsage spaceUsage) {
                // 「删除数据库空间」的业务逻辑（示例：弹出确认弹窗）
                if(AlterUtil.CustomAlertConfirm("删除数据文件","确定要删除数据文件\""+spaceUsage.getName()+"\"吗？")){
                    String dbspace=spaceUsage.getLabel().trim().split(" ")[2].trim();
                    String cmd="onspaces -d "+dbspace+" -p "+spaceUsage.getName()+" -o 0 -y";
                    cmd+="&& rm -rf "+spaceUsage.getName();
                    String finalCmd = cmd;
                    Task task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                Session session=JschUtil.getConnect(connect);
                                String result = JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+ finalCmd);
                                JschUtil.disConnect(session);
                                if(!result.contains("Chunk successfully dropped")) {
                                    throw new Exception(result);
                                }
                            } catch (Exception e) {
                                //throw new Exception("ssh登录失败，请检查网络！");
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    };
                    task.setOnSucceeded(event1 -> {
                        //cancelBtn.fire();
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "数据文件\""+spaceUsage.getName()+"\"已删除！");
                        refreshButton.fire();
                    });
                    task.setOnFailed(event1 -> {
                        String error = task.getException().getMessage();
                        AlterUtil.CustomAlert("错误", error);
                    });
                    new Thread(task).start();
                }
                // 后续可添加：调用删除接口、刷新图表数据等逻辑
            }


            @Override
            public void onExpandDatafile(CustomSpaceChart.SpaceUsage spaceUsage) {
                // 「删除数据库空间」的业务逻辑（示例：弹出确认弹窗）
                if(AlterUtil.CustomAlertConfirm("数据文件自动扩展","确定要设置数据文件\""+spaceUsage.getName()+"\"自动扩展吗？")){
                    int chunkId=spaceUsage.getNumber();
                    Task task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                adminService.modifyChunkExtendable(connect, chunkId, true);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    };
                    task.setOnSucceeded(event1 -> {
                        //cancelBtn.fire();
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "数据文件\""+spaceUsage.getName()+"\"已设置为自动扩展！");
                        refreshButton.fire();
                    });
                    task.setOnFailed(event1 -> {
                        String error = task.getException().getMessage();
                        AlterUtil.CustomAlert("错误", error);
                    });
                    new Thread(task).start();
                }
                // 后续可添加：调用删除接口、刷新图表数据等逻辑
            }


            @Override
            public void onUnExpandDatafile(CustomSpaceChart.SpaceUsage spaceUsage) {
                // 「删除数据库空间」的业务逻辑（示例：弹出确认弹窗）
                if(AlterUtil.CustomAlertConfirm("数据文件关闭自动扩展","确定要关闭数据文件\""+spaceUsage.getName()+"\"自动扩展吗？")){
                    int chunkId=spaceUsage.getNumber();
                    Task task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                adminService.modifyChunkExtendable(connect, chunkId, false);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    };
                    task.setOnSucceeded(event1 -> {
                        //cancelBtn.fire();
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "数据文件\""+spaceUsage.getName()+"\"已关闭自动扩展！");
                        refreshButton.fire();
                    });
                    task.setOnFailed(event1 -> {
                        String error = task.getException().getMessage();
                        AlterUtil.CustomAlert("错误", error);
                    });
                    new Thread(task).start();
                }
                // 后续可添加：调用删除接口、刷新图表数据等逻辑
            }
            @Override
            public void onUnlimitedSpaceSize(CustomSpaceChart.SpaceUsage spaceUsage) {
                // 「删除数据库空间」的业务逻辑（示例：弹出确认弹窗）
                if(AlterUtil.CustomAlertConfirm("解除大小限制","确定要解除数据库空间\""+spaceUsage.getName()+"\"的大小限制吗？")){
                    int chunkId=spaceUsage.getNumber();
                    Task task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                adminService.unLimitedSpaceSize(connect, spaceUsage.getName());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    };
                    task.setOnSucceeded(event1 -> {
                        //cancelBtn.fire();
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "数据库空间\""+spaceUsage.getName()+"\"已解除大小限制！");
                        refreshButton.fire();
                    });
                    task.setOnFailed(event1 -> {
                        String error = task.getException().getMessage();
                        AlterUtil.CustomAlert("错误", error);
                    });
                    new Thread(task).start();
                }
                // 后续可添加：调用删除接口、刷新图表数据等逻辑
            }





        };

        //启停tab初始化变量
        startStackPane=new StackPane();
        SVGPath startItemIcon = new SVGPath();
        startItemIcon.setContent("M20.625 11.2812 L17 9.1719 L14 12.0781 L16.7344 14.7031 L20.625 12.4531 Q20.7969 12.3906 20.8906 12.2344 Q20.9844 12.0625 20.9844 11.875 Q20.9844 11.875 20.9844 11.875 Q20.9844 11.875 20.9844 11.875 L20.9844 11.875 Q20.9844 11.875 20.9844 11.875 Q20.9844 11.875 20.9844 11.875 Q20.9844 11.6875 20.8906 11.5312 Q20.7969 11.375 20.625 11.2812 L20.625 11.2812 L20.625 11.2812 ZM16.3906 8.8281 L12.375 6.5 L3.6094 2 L13.5 11.5938 L16.3906 8.8281 ZM3.75 22 L12.4062 17.2344 L16.125 15.0781 L13.5 12.5469 L3.75 22 ZM3.0312 2.4062 L3.0312 21.7656 L13.0156 12.0781 L3.0312 2.4062 Z");
        startItemIcon.setScaleX(0.7);
        startItemIcon.setScaleY(0.7);
        startItemIcon.setFill(Color.valueOf("#074675"));
        startButton=new Button();
        startButton.getStyleClass().add("custom-button");
        startButton.setGraphic(new Group(startItemIcon));
        startButton.setFocusTraversable(false);
        startButton.setTooltip(new Tooltip("点击启动数据库"));
        SVGPath stopButtonIcon = new SVGPath();
        stopButtonIcon.setContent("M19.2031 6.0078 L19.2031 17.7734 Q19.2031 18.3516 18.7812 18.7734 Q18.3594 19.1953 17.7656 19.1953 L6 19.1953 Q5.5156 19.1953 5.1562 18.8516 Q4.8125 18.4922 4.8125 18.0078 L4.8125 6.2422 Q4.8125 5.6484 5.2344 5.2266 Q5.6562 4.8047 6.2344 4.8047 L18 4.8047 Q18.5 4.8047 18.8438 5.1641 Q19.2031 5.5078 19.2031 6.0078 L19.2031 6.0078 Z");
        stopButtonIcon.setScaleX(0.75);
        stopButtonIcon.setScaleY(0.75);
        stopButtonIcon.setFill(Color.valueOf("#9f453c"));
        stopButton=new Button();
        stopButton.getStyleClass().add("custom-button");
        stopButton.setGraphic(new Group(stopButtonIcon));
        stopButton.setFocusTraversable(false);
        stopButton.setTooltip(new Tooltip("点击关闭数据库"));
        ipLabel=new Label("IP："+connect.getIp());
        statusLabel=new Label("实例状态：未知");
        StackPane startAndStopButton=new StackPane(startButton,stopButton);
        StackPane btnPane=new StackPane(startAndStopButton);
        startButton.setVisible(false);
        stopButton.setVisible(false);
        HBox hbox=new HBox(10,ipLabel,statusLabel,btnPane);
        hbox.setAlignment(Pos.CENTER);
        hbox.setMaxHeight(50);
        startStackPane.getChildren().add(hbox);

        //stopButton.disableProperty().bind(startButton.disableProperty().not());
        startButton.setOnAction(e -> {
            Task instanceInfoTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Session session=JschUtil.getConnect(connect);
                        int result=JschUtil.executeCommandWithExitStatus(session,JschUtil.extractEnvValue(connect.getInfo())+"oninit");
                        JschUtil.disConnect(session);
                        if(result!=0)throw new Exception("启动数据库失败，请检查日志错误！");
                    }catch (Exception e){
                        throw new Exception("ssh登录失败，请检查网络！");
                    }
                    return null;
                }
            };
            instanceInfoTask.setOnSucceeded(event1 -> {
                NotificationUtil.showNotification(Main.mainController.notice_pane,"数据库已启动。");
                startButton.setDisable(false);
                startButton.setVisible(false);
                stopButton.setVisible(true);
                statusLabel.setText("实例状态：在线");

            });
            instanceInfoTask.setOnFailed(event1 -> {
                AlterUtil.CustomAlert("错误","数据库启动出现异常，请查看日志。");

                //NotificationUtil.showNotification(Main.mainController.notice_pane,"数据库启动出现异常，请查看日志。");
                startButton.setDisable(false);
            });
            startButton.setDisable(true);
            connect.executeSqlTask(new Thread(instanceInfoTask));
        });

        stopButton.setOnAction(e -> {
            if(AlterUtil.CustomAlertConfirm("关闭数据库","确定要关闭数据库吗？")) {
                Task instanceInfoTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            Session session=JschUtil.getConnect(connect);
                            int result = JschUtil.executeCommandWithExitStatus(session,JschUtil.extractEnvValue(connect.getInfo())+"onmode -ky&&onclean -ky");
                            JschUtil.disConnect(session);
                            if (result != 0) throw new Exception("关闭数据库失败，请检查日志错误！");
                        } catch (Exception e) {
                            throw new Exception("ssh登录失败，请检查网络！");
                        }
                        return null;
                    }
                };
                instanceInfoTask.setOnSucceeded(event1 -> {
                    stopButton.setDisable(false);
                    stopButton.setVisible(false);
                    startButton.setVisible(true);
                    NotificationUtil.showNotification(Main.mainController.notice_pane, "数据库已关闭。");
                    statusLabel.setText("实例状态：离线");

                });
                instanceInfoTask.setOnFailed(event1 -> {
                    stopButton.setDisable(false);
                    AlterUtil.CustomAlert("错误", "数据库停止出现异常，请查看日志。");
                    // NotificationUtil.showNotification(Main.mainController.notice_pane,"数据库停止出现异常，请查看日志。");
                });
                stopButton.setDisable(true);
                connect.executeSqlTask(new Thread(instanceInfoTask));
            }
        });
        StackPane.setAlignment(hbox, Pos.CENTER);

        //主tabpane初始化
        mainTabPane=new TabPane();
        infoTab=new CustomTab("实例信息");
        checkTab=new CustomTab("一键巡检");
        //checkTab.setContent(createCheckTab());
        logTab=new CustomTab("运行日志");
        spaceTab=new CustomTab("容量管理");
        paramsTab=new CustomTab("参数管理");
        startTab=new CustomTab("实例启停");

        SVGPath refreshItemIcon = new SVGPath();
        refreshItemIcon.setContent("M17.6719 6.3281 L20.0156 3.9844 L20.0156 11.0156 L12.9844 11.0156 L16.2188 7.7812 Q15.375 6.9375 14.2969 6.4688 Q13.2188 6 12 6 Q10.3594 6 8.9688 6.7969 Q7.5938 7.5938 6.7969 8.9844 Q6 10.3594 6 12 Q6 13.6406 6.7969 15.0312 Q7.5938 16.4062 8.9688 17.2031 Q10.3594 18 12 18 L12 18 Q13.7344 18 15.3906 16.8281 Q17.0625 15.6562 17.6719 14.0156 L19.7344 14.0156 Q19.0312 16.6406 16.8906 18.3281 Q14.7656 20.0156 12 20.0156 Q9.8438 20.0156 7.9844 18.9375 Q6.1406 17.8594 5.0781 16.0156 Q4.0312 14.1562 4.0312 12 Q4.0312 9.8438 5.0781 8 Q6.1406 6.1406 7.9844 5.0625 Q9.8438 3.9844 12 3.9844 L12 3.9844 Q13.3594 3.9844 15.0156 4.6875 Q16.6875 5.3906 17.6719 6.3281 Z");
        refreshItemIcon.setScaleX(0.7);
        refreshItemIcon.setScaleY(0.7);
        refreshItemIcon.setFill(Color.valueOf("#074675"));
        refreshButton = new Button();
        refreshButton.getStyleClass().add("codearea-camera-button");
        refreshButton.setGraphic(new Group(refreshItemIcon));
        refreshButton.setFocusTraversable(false);
        refreshButton.setOnAction(e -> {
            refreshCurrentTab();
                });

        // 给每个 Tab 绑定「首次选中加载」逻辑
        bindLazyLoadToTab(infoTab, () -> loadInfoTabContent(infoTab), () -> infoTabLoaded, (loaded) -> infoTabLoaded = loaded);
       bindLazyLoadToTab(checkTab, () -> loadCheckTabContent(checkTab), () -> checkTabLoaded, (loaded) -> checkTabLoaded = loaded);
        bindLazyLoadToTab(logTab, () -> loadLogTabContent(logTab), () -> logTabLoaded, (loaded) -> logTabLoaded = loaded);
        bindLazyLoadToTab(spaceTab, () -> loadSpaceTabContent(spaceTab), () -> spaceTabLoaded, (loaded) -> spaceTabLoaded = loaded);
       bindLazyLoadToTab(paramsTab, () -> loadParamsTabContent(paramsTab), () -> paramsTabLoaded, (loaded) -> paramsTabLoaded = loaded);
        bindLazyLoadToTab(startTab, () -> loadStartTabContent(startTab), () -> startTabLoaded, (loaded) -> startTabLoaded = loaded);

        if(connect.getUsername().equals("gbasedbt")){
            mainTabPane.getTabs().addAll(infoTab,checkTab,logTab,spaceTab,paramsTab,startTab);
        }else{
            mainTabPane.getTabs().addAll(infoTab);
        }
        mainTabPane.setStyle("-fx-background-color: #fff;");
        StackPane  stackPane=new StackPane(mainTabPane,refreshButton,instanceInfoLabel);
        StackPane.setAlignment(refreshButton,Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(instanceInfoLabel,Pos.BOTTOM_RIGHT);
        StackPane.setMargin(refreshButton, new Insets(0, 15, 20, 0));
        StackPane.setMargin(instanceInfoLabel, new Insets(0, 15, 3, 0));

        setContent(stackPane);
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainTabPane.setSide(Side.BOTTOM);

        //refreshButton.fire();
        mainTabPane.setId("instanceManagerTabPane");
        mainTabPane.getSelectionModel().select(tabNum);
        // 方式2.1：直接添加样式字符串

        //如果是只读，禁用一些东西
        if(connect.getReadonly()) {
            configTableView.setEditable(false);
            startButton.setDisable(true);
            stopButton.setDisable(true);
        }


    }



    private void refreshCurrentTab() {
        Tab currentTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (currentTab == null) return;

        // 显示加载占位符
        Node currentContent = currentTab.getContent();
        if (currentContent != null && "loadingNode".equals(currentContent.getId())) {
            // 可选：提示用户正在加载中
            //NotificationUtil.showNotification(Main.mainController.notice_pane, "当前Tab正在加载中，请稍后再试！");
            return;
        }
        currentTab.setContent(createLoadingNode());

        // 异步刷新（避免阻塞 UI）
        new Thread(() -> {
            try {
                updateGroupInstanceInfo();
                // 根据当前 Tab 类型执行刷新
                if (currentTab == infoTab) {
                    loadInfoTabContent(infoTab);
                } else if (currentTab == checkTab) {
                    loadCheckTabContent(checkTab);
                } else if (currentTab == logTab) {
                    loadLogTabContent(logTab);
                } else if (currentTab == spaceTab) {
                   loadSpaceTabContent(spaceTab);
                } else if (currentTab == paramsTab) {
                    loadParamsTabContent(paramsTab);
                } else if (currentTab == startTab) {
                    loadStartTabContent(startTab);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Platform.runLater(() -> {
                    currentTab.setContent(createErrorNode(e.getMessage()));
                });
            }
        }).start();
    }

    private void updateGroupInstanceInfo() {
        if(!connect.getPropByName("GBASEDBTSERVER").isEmpty()){
            try{
            String primaryInstance=metadataService.setConnectInfo(connect);
            String regex = "^" + primaryInstance + "\\s+.*\\s+g=" + connect.getPropByName("GBASEDBTSERVER") + "\\s*$";
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            String sqlhostsContent = Files.readString(Paths.get("extlib/GBASE 8S/sqlhosts"));

            Matcher matcher = pattern.matcher(sqlhostsContent);
            
            // 处理匹配到的行
            while (matcher.find()) {
                String matchedLine = matcher.group();
                log.info("匹配到的sqlhosts行: {}", matchedLine);
                
                // 这里可以添加对匹配行的处理逻辑
                // 例如：解析行中的实例信息、更新UI等
                String[] parts = matchedLine.split("\\s+");
                if (parts.length >= 4) {
                    String instanceName = parts[0];
                    String protocol = parts[1];
                    String host = parts[2];
                    connect.setIp(host);
                    String port = parts[3];
                    connect.setPort(port);
                    connect.setInfo(connect.getInfo().replaceAll("(GBASEDBTSERVER\\s+)\\S+", "$1" + instanceName));
                    Platform.runLater(()->{
        instanceInfoLabel.setText("当前实例信息 ( IP："+connect.getIp()+"   端口："+connect.getPort()+"   实例名："+instanceName+" )");;
                        ipLabel.setText("IP："+connect.getIp());
                    });

                    // 打印解析结果
                    log.info("实例名: {}, 协议: {}, 主机: {}, 端口: {}", 
                            instanceName, protocol, host, port);
                    
                    // 这里可以添加更新UI的逻辑
                    // 例如：将实例信息添加到表格中
                }
            }
        }catch(Exception e){
            log.error(e.getMessage(), e);
        
        }
    }

    }
    /**
     * 通用 Tab 懒加载绑定方法（抽取公共逻辑，避免重复代码）
     * @param tab 目标 Tab
     * @param loadTask 加载内容的任务（无返回值）
     * @param isLoaded 判断是否已加载的回调
     * @param setLoaded 设置已加载状态的回调
     */
    private void bindLazyLoadToTab(Tab tab, Runnable loadTask, Supplier<Boolean> isLoaded, Consumer<Boolean> setLoaded) {
        // 初始化 Tab 内容为「加载占位符」（可选，提升体验）
        tab.setContent(createLoadingNode());

        // 绑定选中状态监听器
        tab.selectedProperty().addListener((obs, oldVal, newVal) -> {
            // 仅当「首次选中」（newVal 为 true，且未加载）时触发加载
            if (newVal && !isLoaded.get()) {
                // 启动线程执行加载任务（避免阻塞 UI）
                new Thread(() -> {
                    try {
                        updateGroupInstanceInfo();
                        setLoaded.accept(true); // 标记为已加载
                        loadTask.run(); // 执行加载逻辑
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        // 加载失败：显示错误信息
                        Platform.runLater(() -> {
                            tab.setContent(createErrorNode(e.getMessage()));
                        });
                    }
                }).start();
            }
        });
    }

    // ------------------------------ 各个 Tab 的加载逻辑 ------------------------------
    /**
     * 加载「实例信息」Tab 内容（示例，需根据实际业务补充）
     */
    private void loadInfoTabContent(CustomTab infoTab) {

        //信息框绑定
        Platform.runLater(() -> {
            String textArarText="";
            textArarText+=("##########################################################################################\n");
            textArarText+="Connection Information\n";
            textArarText+=("##########################################################################################\n");
            textArarText+=String.format("%-30s","Connection Name")+connect.getName()+"\n";
            textArarText+=String.format("%-30s","Database Type")+connect.getDbtype()+"\n";
            textArarText+=String.format("%-30s","JDBC Driver")+connect.getDriver()+"  (MD5:"+connect.getDrivermd5()+")\n";
            textArarText+=String.format("%-30s","IP Address")+connect.getIp()+"\n";
            textArarText+=String.format("%-30s","Port")+connect.getPort()+"\n";
            textArarText+=String.format("%-30s","Database User")+connect.getUsername()+"\n";
            String props="";
            JSONArray jsonArray=new JSONArray(connect.getProps());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                if(jsonObject.getString("propValue")!=null&&(!jsonObject.getString("propValue").equals(""))){
                    props+=jsonObject.getString("propName")+"="+jsonObject.getString("propValue")+";";
                }
            }
            textArarText+=String.format("%-30s","Driver Properties")+props+"\n";
            textArarText+=String.format("%-30s","Database Version")+connect.getDbversion()+"\n";
            textArarText+=(connect.getInfo()+"\n");

            //实例信息界面
            CustomInfoStackPane instance_info_codearea = new CustomInfoStackPane(new CustomInfoCodeArea());

            //实例信息界面结束
            instance_info_codearea.codeArea.replaceText(textArarText); //如果不用settext，而是appendtext追加，会导致setScrollTop无效
            infoTab.setContent(instance_info_codearea);

            instance_info_codearea.codeArea.showParagraphAtTop(0); //此方法有效
            instance_info_codearea.codeArea.setStyleSpans(0, KeywordsHighlightUtil.applyHighlightingInfo(instance_info_codearea.codeArea.getText()));
        });

    }

    private void loadCheckTabContent(CustomTab checkTab)  {
        log.info("loadCheckTabContent in,connect.getip is:"+connect.getIp());

        checkDatalist.clear();
        //initDatalist(checkDatalist);
        String instanceStatus="";
        String onstat_g_osi="";
        String onstat_g_osi_machine="";
        String onstat_g_osi_cpu="";
        String onstat_g_osi_mem="";
        String onstat_V="";
        String onstat_="";
        String onstat_g_seg_greped="";
        String onstat_g_seg="";
        String onstat_g_cluster="";
        String onstat_l="";
        String onstat_l_llog="";
        String onstat_d_greped="";
        String onstat_d="";
        double spaceTopPercent=0;
        String onstat_g_arc_greped="onstat_g_arc_greped";
        String onstat_g_arc="";
        String onstat_m="";
        String onstat_g_sql_greped="";
        String onstat_g_sql="";
        String onstat_g_act_greped="";
        String onstat_g_act="";
        String onstat_g_rea_greped="";
        String onstat_g_rea="";
        String onstat_g_wai="";
        String onstat_g_wai_logio="";
        String onstat_g_wai_lockwait="";
        String onstat_g_wai_bufwait="";
        String onstat_g_wai_iowait="";
        String onstat_x_greped="";
        String onstat_x="";
        String onstat_p="";
        String onstat_p_greped="";
        String onstat_p_deadlks="";
        try {
            Session session= JschUtil.getConnect(connect);
            onstat_g_osi=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g osi");
            onstat_g_osi_machine=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g osi |awk '/OS Machine/ {print $3}'");
            onstat_g_osi_cpu=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g osi |awk '/Number of online processors/{print $5}'");
            onstat_g_osi_mem=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g osi |grep -v 'System memory page size' |awk '/System memory/{print $3$4}'");
            onstat_V=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -V");
            onstat_=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -",true);
            onstat_g_seg_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g seg|grep -c ' V '");
            onstat_g_seg=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g seg");
            onstat_g_cluster=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g cluster");
            onstat_l=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -l");
            onstat_l_llog=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -l |grep -c 'U------'");
            onstat_d_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -d|grep -c PD");
            onstat_d=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -d");
            spaceTopPercent = adminService.getMaxDbspaceUsed(connect);
            onstat_g_arc_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g arc |grep -A 1 ' level ' |sed -n '2p' |awk '{print $4}'");
            onstat_g_arc=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g arc");
            onstat_m=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -c |awk '/^MSGPATH/ {print \"tail -1000 \"$2}' |sh|egrep -ic 'err|failed|warning|allocated|full|long|down|Died|Aborting|Abort'");
            onstat_g_sql_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g sql |egrep -v 'On-Line|Read-Only|Current|Database|^$' |wc -l");
            onstat_g_sql=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g sql");
            onstat_g_act_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g act |grep -v soctcppoll |grep -v '^$'|wc -l");
            onstat_g_act=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g act");
            onstat_g_rea_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g rea|grep -v '^$'| wc -l");
            onstat_g_rea=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g rea");
            onstat_g_wai=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g wai");
            onstat_g_wai_logio=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g wai|grep -c 'logio cond'");
            onstat_g_wai_lockwait=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g wai|grep -c 'yield lockwait'");
            onstat_g_wai_bufwait=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g wai|grep -c 'yield bufwait'");
            onstat_g_wai_iowait=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -g wai|grep -c 'IO Wait'");
            onstat_x_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -x |grep -v '^$' |grep -v ' - ' |wc -l");
            onstat_x=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -x");
            onstat_p=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -p");
            onstat_p_greped=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -p |grep -A 1 rewrite |sed -n '2p' |awk '{print $4\" \"$5\" \"$6\" \"$7\" \"$8\" \"$9}'");
            onstat_p_deadlks=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -p |grep -A 1 deadlks |sed -n '2p' |awk '{print $4}'");
            JschUtil.disConnect(session);
        }catch (SQLException e){
            log.error(e.getMessage(), e);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        String currentValue;
        String status ;
        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_osi_machine.isEmpty()){
            currentValue=onstat_g_osi_machine;
            status="0";
        }
        checkDatalist.add(new HealthCheck("系统架构","onstat -g osi", "x86_64/aarch64",currentValue,status,onstat_g_osi));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_osi_cpu.isEmpty()){
            currentValue=onstat_g_osi_cpu;
            status="0";
        }
        checkDatalist.add(new HealthCheck("CPU数量","onstat -g osi", "1核心以上",currentValue,status,onstat_g_osi));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_osi_mem.isEmpty()){
            currentValue=onstat_g_osi_mem;
            status="0";
        }
        checkDatalist.add(new HealthCheck("内存大小","onstat -g osi", "2GB以上",currentValue,status,onstat_g_osi));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_V.isEmpty()){
            currentValue=onstat_V;
            status="0";
        }
        checkDatalist.add(new HealthCheck("数据库版本","onstat -V", "GBase8sV8.x",currentValue,status,onstat_V));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_.isEmpty()) {
            if (onstat_.contains("Your evaluation license")) {
                currentValue = onstat_.split("Your evaluation license will expire on ")[1];
                status = "1";
            } else {
                currentValue = "永久";
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("软件授权有效期","onstat -", "永久",currentValue,status,onstat_));

        String strictRegex = "((?:On-Line|Read-Only)(?:\\s+\\(.*\\))?)\\s+--"; // 转义\为\\（Java字符串语法）
        Pattern strictPattern = Pattern.compile(strictRegex);
        Matcher strictMatcher = strictPattern.matcher(onstat_);
        currentValue="实例状态异常";
        status="2";
        if(!onstat_.isEmpty()) {
            if (strictMatcher.find()) {
                currentValue = strictMatcher.group(1);
                status = "0";
            } else {
                currentValue = "Off-Line";
                instanceStatus="Off-Line";
                status = "2";
            }
        }
        checkDatalist.add(new HealthCheck("实例状态", "onstat -", "主节点或单机On-Line，集群备机Read-Only", currentValue, status,onstat_));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            strictRegex = "(Blocked:.*)"; // 转义\为\\（Java字符串语法）
            strictPattern = Pattern.compile(strictRegex);
            strictMatcher = strictPattern.matcher(onstat_);
            if (strictMatcher.find()) {
                currentValue = strictMatcher.group(1);
                status = "2";
            } else {
                currentValue = "无Blocked事件";
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例是否BLOCKED", "onstat -", "正常无Blocked:显示", currentValue, status,onstat_));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            strictRegex = "--\\s+Up\\s+(.*)\\s+--"; // 转义\为\\（Java字符串语法）
            strictPattern = Pattern.compile(strictRegex);
            strictMatcher = strictPattern.matcher(onstat_);
            if (strictMatcher.find()) {
                currentValue = strictMatcher.group(1);
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例运行天数", "onstat -", "xxx Days", currentValue, status,onstat_));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            strictRegex = "--\\s+([0-9]*\\s+Kbytes)"; // 转义\为\\（Java字符串语法）
            strictPattern = Pattern.compile(strictRegex);
            strictMatcher = strictPattern.matcher(onstat_);
            if (strictMatcher.find()) {
                currentValue = strictMatcher.group(1);
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例内存总量", "onstat -", "xxx Kbytes", currentValue, status,onstat_));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_seg_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_seg_greped;
            int segments = Integer.parseInt(currentValue);
            if (segments > 3) {
                status = "2";
            }else{
                status="0";
            }
        }
        checkDatalist.add(new HealthCheck("实例内存段数量", "onstat -g seg", "V段不超过3个", currentValue, status,onstat_g_seg));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_cluster.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_cluster;
            if (currentValue.contains("Disconnected")) {
                currentValue = "Disconnected";
                status = "2";
            } else if (currentValue.contains("Connected")) {
                currentValue = "Connected";
                status = "0";
            } else {
                currentValue = "无集群";
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例集群状态", "onstat -g cluster", "无集群或Connected", currentValue, status,onstat_g_cluster));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_l.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            String plogsize = onstat_l.split("Physical Logging")[1].split("Logical Logging")[0];
            strictRegex = "^\\s*\\d+:\\d+\\s+(\\d+)"; // 转义\为\\（Java字符串语法）
            strictPattern = Pattern.compile(strictRegex);
            strictMatcher = strictPattern.matcher(plogsize.split("\n")[4]);
            if (strictMatcher.find()) {
                // 拼接匹配结果，统一格式为 On-Line
                String psize = String.valueOf(Integer.parseInt(strictMatcher.group(1)) * 2);
                currentValue = psize + "k";
                status = "0";
                if (Integer.parseInt(psize) < 512000 * 2) {
                    status = "1";
                }
            }
        }
        checkDatalist.add(new HealthCheck("实例物理日志", "onstat -l", "physize不小于1G", currentValue, status,onstat_l));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_l_llog.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_l_llog;
            if (Integer.parseInt(onstat_l_llog) > 0) {
                status = "1";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例逻辑日志", "onstat -l", "U------状态日志为0", currentValue, status,onstat_l));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_d_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_d_greped;
            if (Integer.parseInt(currentValue) > 0) {
                status = "2";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例空间状态", "onstat -d", "无PD状态", currentValue, status,onstat_d));

        currentValue="实例状态异常";
        status="2";
        if(spaceTopPercent!=0) {
            currentValue = String.valueOf(spaceTopPercent) + "%";
            status = "0";
            if (spaceTopPercent > 80) {
                status = "1";
            }
            if (spaceTopPercent > 90) {
                status = "2";
            }
        }
        checkDatalist.add(new HealthCheck("实例空间使用率", "onstat -d", "使用率小于80%", currentValue, status,onstat_d));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_arc_greped.equals("onstat_g_arc_greped")&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_arc_greped;
            SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy.HH:mm");
            boolean isOver1Day = false;
            if (!currentValue.equals("")) {
                try {
                    Date date = SDF.parse(currentValue);
                    long oneDayMs = 24 * 60 * 60 * 1000L; // 1天的毫秒数
                    long timeDiffMs = new Date().getTime() - date.getTime();
                    isOver1Day = timeDiffMs > oneDayMs;
                } catch (ParseException e1) {
                    throw new RuntimeException(e1);
                }
            }
            status = "0";

            if (isOver1Day || currentValue.equals("")) {
                status = "1";
            }
            if (currentValue.equals("")) {
                currentValue = "未执行过备份";
            }
        }
        checkDatalist.add(new HealthCheck("实例空间备份", "onstat -g arc", "最后一次备份时间在24小时内", currentValue, status,onstat_g_arc));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_m.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_m;
            if (Integer.parseInt(onstat_m) > 0) {
                status = "1";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例运行日志", "onstat -m", "err、failed关键字数量为0", currentValue, status,onstat_m));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_sql_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_sql_greped;
            if (Integer.parseInt(currentValue) >= 10000) {
                status = "1";
            }else{
                status="0";
            }
            currentValue=String.valueOf(((Integer.parseInt(currentValue))));
        }
        checkDatalist.add(new HealthCheck("实例总连接数", "onstat -g sql", "<10000", currentValue, status,onstat_g_sql));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_act_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_act_greped;
            if (Integer.parseInt(currentValue) >= 1003) {
                status = "1";
            } else {
                status = "0";
            }
            currentValue=String.valueOf(((Integer.parseInt(currentValue))-3));
        }
        checkDatalist.add(new HealthCheck("实例活动连接数", "onstat -g act", "<1000", currentValue, status,onstat_g_act));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_rea_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_rea_greped;
            if (Integer.parseInt(currentValue) > 3) {
                status = "1";
            } else {
                status = "0";
            }
            currentValue=String.valueOf(((Integer.parseInt(currentValue))-3));
        }
        checkDatalist.add(new HealthCheck("实例队列数量", "onstat -g rea", "0或少量", currentValue, status,onstat_g_rea));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_wai_logio.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_wai_logio;
            if (Integer.parseInt(currentValue) > 0) {
                status = "1";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例逻辑日志等待logio cond", "onstat -g wai", "0或少量", currentValue, status,onstat_g_wai));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_wai_lockwait.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_wai_lockwait;
            if (Integer.parseInt(currentValue) > 0) {
                status = "1";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例锁等待yield lockwait", "onstat -g wai", "0或少量", currentValue, status,onstat_g_wai));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_wai_bufwait.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_wai_bufwait;
            if (Integer.parseInt(currentValue) > 0) {
                status = "1";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例buf等待yield bufwait", "onstat -g wai", "0或少量", currentValue, status,onstat_g_wai));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_g_wai_iowait.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_g_wai_iowait;
            if (Integer.parseInt(currentValue) > 0) {
                status = "1";
            } else {
                status = "0";
            }
        }
        checkDatalist.add(new HealthCheck("实例IO等待IO Wait", "onstat -g wai", "0或少量", currentValue, status,onstat_g_wai));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_x_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = String.valueOf(Integer.parseInt(onstat_x_greped) - 5);
            status = "0";
        }
        checkDatalist.add(new HealthCheck("实例打开未提交事务数", "onstat -x", "少量", currentValue, status,onstat_x));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_p_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_p_greped.split(" ")[4];
            status="0";
        }
        checkDatalist.add(new HealthCheck("实例已提交事务数", "onstat -p", "业务繁忙度决定", currentValue, status,onstat_p));
        currentValue="实例状态异常";
        status="2";
        if(!onstat_p_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_p_greped.split(" ")[5];
            status="0";
        }
        checkDatalist.add(new HealthCheck("实例回滚事务数", "onstat -p", "少量", currentValue, status,onstat_p));
        currentValue="实例状态异常";
        status="2";
        if(!onstat_p_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_p_greped.split(" ")[1];
            status="0";
        }
        checkDatalist.add(new HealthCheck("实例插入数量", "onstat -p", "业务繁忙度决定", currentValue, status,onstat_p));
        currentValue="实例状态异常";
        status="2";
        if(!onstat_p_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_p_greped.split(" ")[2];
            status="0";
        }
        checkDatalist.add(new HealthCheck("实例更新数量", "onstat -p", "业务繁忙度决定", currentValue, status,onstat_p));
        currentValue="实例状态异常";
        status="2";
        if(!onstat_p_greped.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_p_greped.split(" ")[3];
            status="0";
        }
        checkDatalist.add(new HealthCheck("实例删除数量", "onstat -p", "业务繁忙度决定", currentValue, status,onstat_p));

        currentValue="实例状态异常";
        status="2";
        if(!onstat_p_deadlks.isEmpty()&&!instanceStatus.equals("Off-Line")) {
            currentValue = onstat_p_deadlks;
            status="0";
        }
        checkDatalist.add(new HealthCheck("实例死锁数量", "onstat -p", "业务逻辑决定", currentValue, status,onstat_p));

        Platform.runLater(() -> {
            checkTableView.getItems().clear();
            checkTableView.getItems().setAll(checkDatalist);
            checkTableView.refresh();
            checkTab.setContent(checkStackPane);
        });


    }

    private void    loadParamsTabContent(CustomTab checkTab)  {

        configTableView.getItems().clear();
        String config="";

        try {
            Session session= JschUtil.getConnect(connect);
            config=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -c |grep -v '^$' |grep -v '^#' |sed '1,2d'");
            JschUtil.disConnect(session);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        String finalConfig = config;
        Platform.runLater(() -> {
            configDatalist.clear();
            configDatalist=parseConfigData(finalConfig);
            configTableView.getItems().setAll(configDatalist);
            configTableView.refresh();
            configTableView.setVisible(true);
            paramsTab.setContent(configStackPane);
        });


    }

    private ObservableList<ObservableList<String>> parseConfigData(String rawData) {
        ObservableList<ObservableList<String>> configItems = FXCollections.observableArrayList();

        // 按行分割数据（处理换行符兼容：\n、\r\n）
        String[] lines = rawData.replaceAll("\r\n", "\n").split("\n");

        for (String line : lines) {
            // 去除行首尾空白
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue; // 跳过空行
            }

            // 分割参数名和参数值（按第一个空格/制表符分割，兼容多空格/制表符）
            String[] parts = trimmedLine.split("\\s+", 2); // 分割为2部分，保留值中的空格
            String paramName = parts[0];
            String paramValue = parts.length > 1 ? parts[1] : ""; // 无值则为空字符串

            // 添加到列表
            configItems.add(FXCollections.observableArrayList(null, paramName,paramValue) );
        }

        return configItems;
    }

    private void loadLogTabContent(CustomTab logTab){

        try {
            Session session= JschUtil.getConnect(connect);
            onlinelog=JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -c |awk '/^MSGPATH/ {print \"tail -1000 \"$2}' |sh");
            JschUtil.disConnect(session);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        Platform.runLater(()->{
            CustomInfoCodeArea logCodeArea=new CustomInfoCodeArea();
            CustomInfoStackPane customInfoStackPane=new CustomInfoStackPane(logCodeArea);
            StackPane stackPane=new StackPane();
            stackPane.getChildren().add(customInfoStackPane);
            logCodeArea.replaceText(onlinelog);
            logCodeArea.setStyleSpans(0,KeywordsHighlightUtil.applyHighlightingOnlinelog(logCodeArea.getText()));
            logCodeArea.showParagraphAtBottom(logCodeArea.getParagraphs().size() - 1);
            logTab.setContent(stackPane);
        });

    }


    private void loadSpaceTabContent(CustomTab spaceTab) {
        try {
            dataList = adminService.getInstanceDbspaceInfo(connect);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }


        Platform.runLater(()->{

            dbspaceChartList=dataList.get(0);
            chunkChartList=dataList.get(1);
            databaseChartList=dataList.get(2);
            tabChartList=dataList.get(3);
            dbspaceChart.render(dbspaceChartList);
            chunkChart.render(chunkChartList);
            databaseChart.render(databaseChartList);
            tabChart.render(tabChartList);
            dbspaceChart.setContextMenuListener(menuListener);
            chunkChart.setContextMenuListener(menuListener);


            if(connect.getReadonly()){
                dbspaceChart.setMenuItemsDisabled(true);
                chunkChart.setMenuItemsDisabled(true);
            }



                        /*
            ImageView loading_icon=new ImageView(new Image("file:images/loading.gif"));
            loading_icon.setScaleX(0.7);
            loading_icon.setScaleY(0.7);
            StackPane stackPane=new StackPane(loading_icon);

             */

            // 5. 填充数据+添加百分比

            spaceTab.setContent(dbspaceStackPane);
        });
    }

    private void loadStartTabContent(CustomTab startTab) {


        try {
            Session session=JschUtil.getConnect(connect);
            instanceStatus = JschUtil.executeCommand(session,JschUtil.extractEnvValue(connect.getInfo())+"onstat -");
            JschUtil.disConnect(session);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            if (instanceStatus.contains("On-Line")||instanceStatus.contains("Read-Only")) {
                stopButton.setVisible(true);
                startButton.setVisible(false);
                statusLabel.setText("实例状态：在线");
            } else  {
                startButton.setVisible(true);
                stopButton.setVisible(false);
                statusLabel.setText("实例状态：离线");

            }
            startTab.setContent(startStackPane);
        });
    }
    public void initDatalist(List<HealthCheck> datalist){
        datalist.clear();

    }


    // ------------------------------ 补充必要的函数式接口（若项目未引入 Java 8+ 内置接口） ------------------------------
    @FunctionalInterface
    private interface Supplier<T> {
        T get();
    }

    @FunctionalInterface
    private interface Consumer<T> {
        void accept(T t);
    }

    private Node createLoadingNode() {
        ImageView loadingIcon = new ImageView(new Image("file:images/loading.gif"));
        loadingIcon.setScaleX(0.7);
        loadingIcon.setScaleY(0.7);
        StackPane loadingPane = new StackPane(loadingIcon);
        loadingPane.setId("loadingNode");
        return loadingPane;

    }

    private  Node createErrorNode(String mesg){
        ImageView errorIcon = new ImageView(new Image("file:images/dialog-error.png"));
        errorIcon.setScaleX(0.5);
        errorIcon.setScaleY(0.5);
        HBox hBox=new HBox(5,errorIcon,new Label(mesg));
        hBox.setAlignment(Pos.CENTER);
        StackPane errorPane = new StackPane(hBox);
        return errorPane;
    }
}
