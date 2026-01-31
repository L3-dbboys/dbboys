package com.dbboys.ctrl;

import com.dbboys.app.*;
import com.dbboys.customnode.*;
import com.dbboys.util.*;
import com.dbboys.vo.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;


public class MainController {
    private static final Logger log = LogManager.getLogger(MainController.class);

    @FXML
    private Region window_title_blank;
    @FXML
    private Button window_minsize_button;
    @FXML
    public Button window_maxsize_button;
    @FXML
    private Button window_close_button;
    @FXML
    private Pane resize_layer_right;
    @FXML
    private Pane resize_layer_left;
    @FXML
    private Pane resize_layer_bottom;
    @FXML
    private Pane resize_layer_top;
    @FXML
    private Pane resize_layer_bottom_left;
    @FXML
    private Pane resize_layer_bottom_right;
    @FXML
    private StackPane root;
    @FXML
    public VBox main_vbox;
    @FXML
    public TabPane treeview_tabpane;
    @FXML
    public CustomTreeviewTab connect_tab;
    @FXML
    public CustomTreeviewTab markdown_tab;
    @FXML
    public CustomUserTextField connect_search_textfield;
    @FXML
    public CustomUserTextField markdown_search_textfield;
    @FXML
    public Button connect_search_button;
    @FXML
    public Button markdown_search_button;
    @FXML
    public TreeView<TreeData> databasemeta_treeview;
    @FXML
    public VBox markdown_treeview_vbox;
    @FXML
    public MenuItem new_sqlfile_menuitem;
    @FXML
    public SplitPane main_splitpane;
    @FXML
    public TabPane sql_tabpane;
    @FXML
    public StackPane notice_pane;
    @FXML
    public HBox status_hbox;
    @FXML
    public Button status_backsql_stop_button;
    @FXML
    public Label status_backsql_count_label;
    @FXML
    public Button status_backsql_list_button;
    @FXML
    public ProgressIndicator status_backsql_progress;
    @FXML
    public Button snapshot_root_button;
    @FXML
    public StackPane rebuild_markdown_index_button_stackpane;
    @FXML
    public Button rebuild_markdown_index_button;
    @FXML
    public StackPane markdown_search_icon_stackpane;
    @FXML
    public StackPane download_stackpane;

    private Boolean window_max_maximized=false;
    private Double window_max_prevX=0.0;
    private Double window_max_prevY=0.0;
    private Double window_max_prevWidth=800.0;
    private Double window_max_prevHeight=600.0;
    private Double window_X;  //拖动前X坐标
    private Double window_X_POS;  //最大化的时候拖动记录鼠标位置，自动缩小后需要自动设置坐标保持鼠标在相同的相对位置
    private Double window_Y;  //拖动前Y坐标


    //public Connect SQLConnect=new Connect();

    public void initialize() {
        //如果没有下载，界面不显示
        download_stackpane.visibleProperty().bind(
                Bindings.size(download_stackpane.getChildren()).greaterThan(0)
        );
        //download_stackpane.managedProperty().bind(download_stackpane.visibleProperty());


        //拖动表或视图响应
        sql_tabpane.setOnDragOver(event -> {
            if (event.getGestureSource() != sql_tabpane && event.getDragboard().hasString()) {
                Dragboard db = event.getDragboard();
                if (MarkdownUtil.sourceTreeItems==null||(MarkdownUtil.sourceTreeItems.size()==1)&&(!new File(db.getString().replace(";","")).isDirectory())) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });

        sql_tabpane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {  //拖入表或库
                event.setDropCompleted(true);
                if(!db.getString().equals("DATABASEOBJECTDRAG")){
                    if(db.getString().endsWith(".md")){
                        TabpaneUtil.addCustomMarkdownTab(new File(db.getString()),false);
                    }else{
                        NotificationUtil.showNotification(Main.mainController.notice_pane,"不支持Markdown以外的文件格式编辑！");
                    }
                }

            }else{
                event.setDropCompleted(false);
            }
            event.consume();
        });

        //拖动表或视图响应结束

        //左侧连接面板
        connect_tab.setTitle(connect_tab.getText());
        //connect_tab.titleToggle.setSelected(true);

        //知识库面板
        markdown_tab.setTitle(markdown_tab.getText());



        markdown_tab.titleToggle.setOnContextMenuRequested(event -> {
            if(markdown_tab.titleToggle.isSelected()){
            MarkdownUtil.treeView.getSelectionModel().clearSelection();
            MarkdownUtil.contextMenu.show(markdown_tab.titleToggle, event.getScreenX(), event.getScreenY());
            }
        });
        markdown_tab.titleToggle.setTooltip(new Tooltip("数据库知识库"));
        markdown_tab.titleToggleIcon.setContent("M21.0469 4.4844 Q21.5938 4.4844 22.0312 4.9219 Q22.4844 5.3594 22.4844 6 L22.4844 18 Q22.4844 18.6406 22.0312 19.0781 Q21.5938 19.5156 21.0469 19.5156 L3.0469 19.5156 Q2.4062 19.5156 1.9531 19.0781 Q1.5156 18.6406 1.5156 18 L1.5156 6 Q1.5156 5.3594 1.9531 4.9219 Q2.4062 4.4844 2.9531 4.4844 L21.0469 4.4844 ZM3.0469 2.9531 Q1.7656 2.9531 0.875 3.8438 Q0 4.7188 0 6 L0 18 Q0 19.2812 0.875 20.1719 Q1.7656 21.0469 2.9531 21.0469 L21.0469 21.0469 Q22.2344 20.9531 23.1094 20.125 Q24 19.2812 24 18 L24 6 Q24 4.7188 23.1094 3.8438 Q22.2344 2.9531 20.9531 2.9531 L3.0469 2.9531 ZM13.6875 12.2344 Q13.9219 12 14.2344 12 Q14.5625 12 14.7969 12.2344 L17.2812 14.7188 L19.6875 12.2344 Q19.9219 12 20.2344 12 Q20.5625 12 20.7969 12.2031 Q21.0469 12.4062 21.0469 12.7344 Q21.0469 13.0469 20.7969 13.2812 L17.7656 16.3125 Q17.5938 16.4844 17.2656 16.4844 Q16.9531 16.4844 16.7188 16.3125 L13.6875 13.2812 Q13.5156 13.0469 13.5156 12.7344 Q13.5156 12.4062 13.6875 12.2344 ZM17.2812 7.5156 Q17.5938 7.5156 17.7969 7.7188 Q18 7.9219 18 8.2344 L18 14.2344 Q18 14.5625 17.7969 14.8125 Q17.5938 15.0469 17.2656 15.0469 Q16.9531 15.0469 16.7188 14.8125 Q16.4844 14.5625 16.4844 14.2344 L16.4844 8.2344 Q16.4844 7.9219 16.7188 7.7188 Q16.9531 7.5156 17.2812 7.5156 ZM5.3594 16.4844 L5.3594 10.4844 L5.4375 10.4844 L7.5938 15.3594 L8.7188 15.3594 L10.875 10.4844 L10.9531 10.4844 L10.9531 16.4844 L12.5625 16.4844 L12.5625 7.5156 L10.7188 7.5156 L8.1562 13.3594 L8.1562 13.3594 L5.5938 7.5156 L3.7656 7.5156 L3.7656 16.4844 L5.3594 16.4844 Z");
        //搜索事件
        connect_search_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            connect_search_textfield.setText(newValue.replace(" ", ""));
            if(!connect_search_textfield.getText().equals(oldValue.replace(" ", ""))){
                String searchText=connect_search_textfield.getText();
                if (!searchText.isEmpty()&&searchText.length()>=2) {
                    MetadataTreeviewUtil.searchTree(databasemeta_treeview,searchText,connect_search_button);
                }else{
                    connect_search_button.setDisable(true);
                }
            }

        });

        //左侧tabpane默认选中上次关闭前tab
        for(Tab tab:treeview_tabpane.getTabs()){
            if(((CustomTreeviewTab)tab).getTitle().equals(ConfigManagerUtil.getProperty("DEFAULT_LISTVIEW_TAB"))){
                treeview_tabpane.getSelectionModel().select(tab);
                ((CustomTreeviewTab)tab).titleToggle.setSelected(true);
                break;
            }
        }

        markdown_treeview_vbox.getChildren().add(MarkdownUtil.treeView);

        Label rebuild_index_running_icon=new Label();
        ImageView loading_icon=new ImageView(new Image("file:images/loading.gif"));
        loading_icon.setScaleX(0.7);
        loading_icon.setScaleY(0.7);
        rebuild_index_running_icon.setGraphic(loading_icon);
        Tooltip tooltip=new Tooltip("正在重建索引");
        tooltip.setShowDelay(Duration.millis(100));
        rebuild_index_running_icon.setTooltip(tooltip);
        rebuild_markdown_index_button_stackpane.getChildren().add(rebuild_index_running_icon);
        rebuild_index_running_icon.visibleProperty().bind(rebuild_markdown_index_button.visibleProperty().not());

        Label sreach_running_label=new Label();
        ImageView sreach_running_icon=new ImageView(new Image("file:images/loading.gif"));
        sreach_running_icon.setScaleX(0.7);
        sreach_running_icon.setScaleY(0.7);
        sreach_running_label.setGraphic(sreach_running_icon);
        Tooltip sreach_running_tooltip=new Tooltip("正在搜索");
        sreach_running_tooltip.setShowDelay(Duration.millis(100));
        sreach_running_label.setTooltip(tooltip);
        markdown_search_icon_stackpane.getChildren().add(sreach_running_label);
        sreach_running_label.visibleProperty().bind(markdown_search_button.visibleProperty().not());

        markdown_search_button.setOnAction(event -> {

            new Thread(()->{
                Platform.runLater(()->{
                    markdown_search_button.setVisible(false);
                });
                MarkdownSearchUtil.performSearch(markdown_search_textfield.getText());
                Platform.runLater(()->{
                        markdown_search_button.setVisible(true);
                    });
            }).start();

        });

        markdown_search_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if(markdown_search_textfield.getText().isEmpty()){
                markdown_search_button.setDisable(true);
            }else{
                markdown_search_button.setDisable(false);
            }
                });

        markdown_search_textfield.setOnKeyPressed(event -> {
            // 判断按下的是否为 Enter 键（包含普通 Enter 和小键盘 Enter）
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if(!markdown_search_button.isDisable()&&markdown_search_button.isVisible()){
                    markdown_search_button.fire();
                }
            }
        });

        //切换tab时，sql编辑器获取焦点，以确保响应各类鼠标事件
        sql_tabpane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                if(newTab instanceof CustomSqlTab){
                    Platform.runLater(() -> {
                        ((CustomSqlTab)newTab).sqlTabController.sql_edit_codearea.requestFocus();                        // 可选：将光标移动到文本末尾
                    });
                }
            }
        });


        sql_tabpane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY &&event.getClickCount() == 2) {
                TabpaneUtil.addCustomSqlTab(null);
            }
        });



        new_sqlfile_menuitem.setOnAction(event -> {
            TabpaneUtil.addCustomSqlTab(null);});
        main_splitpane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(Main.sqledit_codearea_is_max==0) {
                //保留两位小数设置，否则可能因为小数过多而设置不准
                Platform.runLater(() -> {
                    main_splitpane.setDividerPositions(Main.split1Pos);
                });
            }else{
                Platform.runLater(() -> {
                    main_splitpane.setDividerPositions(0);
                });
            }
        });


        window_title_blank.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                window_maxsize_button.fire();
                // 你可以在这里执行最大化窗口、缩放等操作
            }
        });

        window_title_blank.setOnMousePressed(event -> {
            Stage stage = (Stage) window_title_blank.getScene().getWindow();
            window_X = event.getScreenX() - stage.getX();
            window_Y = event.getScreenY() - stage.getY();
            window_X_POS=event.getScreenX()/stage.getWidth();
        });

        window_title_blank.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if(window_max_maximized){
                window_maxsize_button.fire();
                stage.setX(event.getScreenX()-stage.getWidth()*window_X_POS);
                window_X = event.getScreenX() - stage.getX();
                //window_Y = event.getScreenY() - stage.getY();
            }
            else {
                stage.setX(event.getScreenX() - window_X);
                stage.setY(event.getScreenY() - window_Y);
            }
        });

        //上下左右隐藏拖动边框事件
        resize_layer_right.prefHeightProperty().bind(root.heightProperty());
        resize_layer_right.setOnMouseDragged(event -> {
            if(!window_max_maximized) {
                resize_layer_right.setCursor(Cursor.E_RESIZE);
                Stage stage = (Stage) root.getScene().getWindow();
                double newWidth = event.getSceneX();
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                }
            }
        });

        resize_layer_right.setOnMouseMoved(event -> {
                    if(!window_max_maximized) {
                        resize_layer_right.setCursor(Cursor.E_RESIZE);
                    }
                });

        resize_layer_right.setOnMouseExited(event -> {
            resize_layer_right.setCursor(Cursor.DEFAULT);
        });

        resize_layer_bottom.prefWidthProperty().bind(root.widthProperty());
        resize_layer_bottom.setOnMouseDragged(event -> {
            if(!window_max_maximized) {
                resize_layer_bottom.setCursor(Cursor.S_RESIZE);
                Stage stage = (Stage) root.getScene().getWindow();
                double newHeight = event.getSceneY();
                //拖动不超过任务栏
                if (newHeight > stage.getMinHeight()&&(event.getSceneY()+stage.getY()<Screen.getPrimary().getVisualBounds().getHeight())) {
                    stage.setHeight(newHeight);
                }
            }
        });

        resize_layer_bottom.setOnMouseMoved(event -> {
            if(!window_max_maximized) {
                resize_layer_bottom.setCursor(Cursor.S_RESIZE);
            }
        });

        resize_layer_bottom.setOnMouseExited(event -> {
            resize_layer_bottom.setCursor(Cursor.DEFAULT);
        });

        resize_layer_left.prefHeightProperty().bind(root.heightProperty());
        resize_layer_left.setOnMouseDragged(event -> {
            if(!window_max_maximized) {
                resize_layer_left.setCursor(Cursor.W_RESIZE);
                Stage stage = (Stage) root.getScene().getWindow();
                double deltaX = event.getScreenX() - stage.getX();
                double newWidth = stage.getWidth() - deltaX; // 新宽度，保持右边界不变

                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);  // 设置新的宽度
                    stage.setX(event.getScreenX()); // 调整窗口位置，保持左边界与鼠标对齐
                }
            }
        });

        resize_layer_left.setOnMouseMoved(event -> {
            if(!window_max_maximized) {
                resize_layer_left.setCursor(Cursor.W_RESIZE);
            }
        });

        resize_layer_left.setOnMouseExited(event -> {
            resize_layer_left.setCursor(Cursor.DEFAULT);
        });

        resize_layer_top.prefWidthProperty().bind(root.widthProperty());
        resize_layer_top.setOnMouseDragged(event -> {
            if(!window_max_maximized) {
                resize_layer_top.setCursor(Cursor.N_RESIZE);
                Stage stage = (Stage) root.getScene().getWindow();
                double deltaY = event.getScreenY() - stage.getY();
                double newHeight = stage.getHeight() - deltaY; // 新宽度，保持右边界不变
                if (newHeight > stage.getMinHeight()) {
                    stage.setHeight(newHeight);  // 设置新的宽度
                    stage.setY(event.getScreenY()); // 调整窗口位置，保持左边界与鼠标对齐
                }

            }
        });

        resize_layer_top.setOnMouseMoved(event -> {
            if(!window_max_maximized) {
                resize_layer_top.setCursor(Cursor.N_RESIZE);
            }
        });

        resize_layer_top.setOnMouseExited(event -> {
            resize_layer_top.setCursor(Cursor.DEFAULT);
        });

        resize_layer_bottom_left.setOnMouseDragged(event -> {
            if(!window_max_maximized) {
                resize_layer_bottom_left.setCursor(Cursor.SW_RESIZE);
                Stage stage = (Stage) root.getScene().getWindow();
                double newHeight = event.getSceneY();
                //拖动不超过任务栏
                if (newHeight > stage.getMinHeight()&&(event.getSceneY()+stage.getY()<Screen.getPrimary().getVisualBounds().getHeight())) {
                    stage.setHeight(newHeight);
                }
                double deltaX = event.getScreenX() - stage.getX();
                double newWidth = stage.getWidth() - deltaX; // 新宽度，保持右边界不变

                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);  // 设置新的宽度
                    stage.setX(event.getScreenX()); // 调整窗口位置，保持左边界与鼠标对齐
                }
            }
        });

        resize_layer_bottom_left.setOnMouseMoved(event -> {
            if(!window_max_maximized) {
                resize_layer_bottom_left.setCursor(Cursor.SW_RESIZE);
            }
        });

        resize_layer_bottom_left.setOnMouseExited(event -> {
            resize_layer_bottom_left.setCursor(Cursor.DEFAULT);
        });

        resize_layer_bottom_right.setOnMouseDragged(event -> {
            if(!window_max_maximized) {
                resize_layer_bottom_right.setCursor(Cursor.SE_RESIZE);
                Stage stage = (Stage) root.getScene().getWindow();
                double newHeight = event.getSceneY();
                //拖动不超过任务栏
                if (newHeight > stage.getMinHeight()&&(event.getSceneY()+stage.getY()<Screen.getPrimary().getVisualBounds().getHeight())) {
                    stage.setHeight(newHeight);
                }
                double newWidth = event.getSceneX();
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                }
            }
        });

        resize_layer_bottom_right.setOnMouseMoved(event -> {
            if(!window_max_maximized) {
                resize_layer_bottom_right.setCursor(Cursor.SE_RESIZE);
            }
        });

        resize_layer_bottom_right.setOnMouseExited(event -> {
            resize_layer_bottom_right.setCursor(Cursor.DEFAULT);
        });

        //窗口最大化按钮
        SVGPath window_minsize_button_icon = new SVGPath();
        window_minsize_button_icon.setContent("M2.25 11.25 L21.75 11.25 L21.75 12.75 L2.25 12.75 L2.25 11.25 Z");
        window_minsize_button_icon.setScaleX(0.45);
        window_minsize_button_icon.setScaleY(0.45);
        window_minsize_button_icon.setFill(Color.valueOf("#000"));
        SVGPath window_maxsize_button_max = new SVGPath();
        window_maxsize_button_max.setContent("M4.5156 4.5 L4.5156 19.5 L19.5156 19.5 L19.5156 4.5 L4.5156 4.5 ZM18 18.0156 L6 18.0156 L6 6.0156 L18 6.0156 L18 18.0156 Z");
        window_maxsize_button_max.setScaleX(0.55);
        window_maxsize_button_max.setScaleY(0.55);
        window_maxsize_button_max.setFill(Color.valueOf("#000"));
        SVGPath window_maxsize_button_recover = new SVGPath();
        window_maxsize_button_recover.setContent("M22.9688 0 L5.625 0 Q5.2031 0 4.8906 0.3125 Q4.5938 0.6094 4.5938 1.0312 L4.5938 4.5938 L1.0312 4.5938 Q0.6094 4.5938 0.2969 4.9062 Q0 5.2031 0 5.625 L0 22.9688 Q0 23.3906 0.2969 23.6875 Q0.6094 24 1.0312 24 L18.375 24 Q18.7969 24 19.0938 23.6875 Q19.4062 23.3906 19.4062 22.9688 L19.4062 19.4062 L22.9688 19.4062 Q23.3906 19.4062 23.6875 19.1094 Q24 18.7969 24 18.375 L24 1.0312 Q24 0.6094 23.6875 0.3125 Q23.3906 0 22.9688 0 L22.9688 0 ZM17.5312 22.125 L1.875 22.125 L1.875 6.4688 L17.5312 6.4688 L17.5312 22.125 ZM22.125 17.5312 L19.4062 17.5312 L19.4062 5.625 Q19.4062 5.2031 19.0938 4.9062 Q18.7969 4.5938 18.375 4.5938 L6.4688 4.5938 L6.4688 1.875 L22.125 1.875 L22.125 17.5312 Z");
        window_maxsize_button_recover.setScaleX(0.4);
        window_maxsize_button_recover.setScaleY(0.4);
        window_maxsize_button_recover.setFill(Color.valueOf("#000"));
        window_maxsize_button.setGraphic(new Group(window_maxsize_button_recover));
        window_minsize_button.setGraphic(new Group(window_minsize_button_icon));
        window_minsize_button.setOnAction(event->{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setIconified(true);
        });
        window_maxsize_button.setOnAction(event -> {
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window_max_maximized) {
                // 还原

                    window_maxsize_button.setGraphic(new Group(window_maxsize_button_max));
                    primaryStage.setX(window_max_prevX);
                    primaryStage.setY(window_max_prevY);
                    primaryStage.setWidth(window_max_prevWidth);
                    primaryStage.setHeight(window_max_prevHeight);

            } else {
                // 记录原始位置和大小

                    window_maxsize_button.setGraphic(new Group(window_maxsize_button_recover));

                    window_max_prevX = primaryStage.getX();
                    window_max_prevY = primaryStage.getY();
                    window_max_prevWidth = primaryStage.getWidth();
                    window_max_prevHeight = primaryStage.getHeight();

                    // 最大化为屏幕尺寸
                    primaryStage.setX(0);
                    primaryStage.setY(0);
                    primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                    primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());

            }
            window_max_maximized = !window_max_maximized;
        });

        //窗口关闭按钮

        window_close_button.setOnAction(event->{
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Boolean sureToclosed=true;
            for (Tab tab : sql_tabpane.getTabs()) {
                if (tab.getText().startsWith("*")) {
                    if(!AlterUtil.CustomAlertConfirm("提示","部分打开的SQL文件未保存，确定要关闭软件吗？")){
                        sureToclosed=false;
                        event.consume();
                    }
                    break;
                }
            }

            //如果回答确认，执行关闭流程
            if(sureToclosed) {
                //disconnectAll(); //关闭所有连接,取消此操作，如果连接已中断会导致关闭卡顿，软件关闭会自动关闭连接
                String split1Content = String.valueOf(Main.split1Pos);
                String split2Content = String.valueOf(Main.split2Pos);
                ConfigManagerUtil.setProperty("SPLIT_DRIVER_MAIN", split1Content);
                ConfigManagerUtil.setProperty("SPLIT_DRIVER_SQL", split2Content);
                //log.info("开始执行primaryStage.close()。");
                primaryStage.close();
                //log.info("结束执行primaryStage.close()，开始执行System.exit(0)。");
                System.exit(0); //避免executorService线程未关闭
                log.info("dbboys已关闭。");
            }
        });



        Main.loadProgressBar.setProgress(0.2);


        //连接列表上面的连接管理面板结束

        //设置树根节点
        // 初始化显示连接树
        try {
            MetadataTreeviewUtil.initDatabaseObjectsTreeview(databasemeta_treeview);
        }catch (Exception e){
        }

        Main.loadProgressBar.setProgress(0.4);


        //状态栏
        snapshot_root_button.setOnAction(event->{
            SnapshotUtil.snapshotRoot();

        });


        status_backsql_stop_button.setOnAction(event->{
            Iterator<BackgroundSqlTask> iterator = MetadataTreeviewUtil.metaDBaccessService.backSqlTask.iterator();
            while (iterator.hasNext()) {
                BackgroundSqlTask bgsql = iterator.next();
                try {
                    bgsql.getStmt().cancel();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            NotificationUtil.showNotification(notice_pane,"后台任务已全部取消！");

        });
        Main.loadProgressBar.setProgress(0.6);

        status_backsql_list_button.setOnAction(event->{
            PopupWindowUtil.openSqlTaskPopupWindow();
        });
        status_backsql_list_button.disableProperty().bind(status_backsql_stop_button.disableProperty());

        status_backsql_progress.visibleProperty().bind(status_backsql_stop_button.disableProperty().not());

        //清理sql历史记录保留1000行
        new Thread( () -> { SqliteDBaccessUtil.deleteSqlHistory();} ).start();
        //运行一次搜索，避免首次搜索慢
        new Thread( () -> { MarkdownSearchUtil.warmUpIndex();}).start();
        Main.loadProgressBar.setProgress(1);

    }
    //初始化界面完成

    //初始化数据库，响应恢复出厂设置
    public void initDB() {
        UpgradeUtil.initDB();
    }
    public void community()  {
        CustomGenericStyledArea.openUrl("https://www.dbboys.com");
    }
    public void checkVersion()  {
        UpgradeUtil.checkVersion();
    }




    public void aboutDBboys() {
        PopupWindowUtil.openAboutWindow();
    }

    //文件-新建连接响应函数

    public void createConnectLeaf(){
        MetadataTreeviewUtil.showCreateConnectDialog(null,false);
    }


    public void rebuildMarkdownIndex(){
        MarkdownSearchUtil.buildIndex();
    }



    //文件-新建连接分类响应函数

    public void createConnectFolder() {
        MetadataTreeviewUtil.createConnectFolder(databasemeta_treeview);
    }


    //文件-断开所有连接响应函数
    public void disconnectAll() {
        for (TreeItem<TreeData> ti : Main.mainController.databasemeta_treeview.getRoot().getChildren()) {
            MetadataTreeviewUtil.disconnectFolder(ti);
        }
    };


    public void openSqlFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择SQL文件");
        File selectedFile = fileChooser.showOpenDialog(window_close_button.getScene().getWindow());
        if (selectedFile != null) {
            String tabName = selectedFile.getName();
            Boolean isOpened = false;
            for (Tab tab : sql_tabpane.getTabs()) {
                if (((CustomSqlTab) tab).sql_file_path.equals(selectedFile.getAbsolutePath())) {
                    isOpened = true;
                    sql_tabpane.getSelectionModel().select(tab);
                    break;
                }
            }
            if (!isOpened) {
                CustomSqlTab newtab = new CustomSqlTab(tabName);
                newtab.sql_file_path=selectedFile.getAbsolutePath();
                newtab.openSqlFile();
                sql_tabpane.getTabs().add(newtab);
                sql_tabpane.getSelectionModel().select(newtab);
            }
        }
    }


    public void checkInstallEnv(){
        RemoteCheckEnvUtil.startWizard((Stage) Main.scene.getWindow());
    }
    public void installGBase8S(){
        RemoteInstallerUtil.startWizard((Stage) Main.scene.getWindow());
    }

    public void unInstallGBase8S(){
        RemoteUninstallerUtil.startWizard((Stage) Main.scene.getWindow());
    }


}

