package com.dbboys.app;

import com.dbboys.util.ConfigManagerUtil;
import com.dbboys.ctrl.MainController;
import com.dbboys.customnode.CustomSqlEditCodeArea;
import com.dbboys.util.TabpaneUtil;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Version;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;

public class Main extends Application {
    private static final Logger log = LogManager.getLogger(Main.class);

    //版本号
    public static Version VERSION;
    //静态类实例，用于弹出界面与主程序的交互调用
    public static MainController mainController;
    public static Scene scene;
    public static double split1Pos;  //竖分割线位置
    public static double split2Pos;  //横分割线折叠位置，1为顶，2为实际位置，3为底部
    public static double sqledit_codearea_is_max=0;
    public static ProgressBar loadProgressBar = new ProgressBar(0.1);
    public static Connect lastInstallConnect;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //版本号
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("version", "DBboys V1.0.0beta.20260201");
        jsonObject.put("build", 6);
        jsonObject.put("url", "");
        jsonObject.put("changelog", "");
        VERSION=new Version(jsonObject);

        try {

            // 创建加载窗口
            Stage loadingStage = new Stage(StageStyle.UNDECORATED);
            Label loadLabel = new Label("DBboys Loading...");
            loadLabel.setStyle("-fx-font-weight: normal");
            StackPane loadStackpane = new StackPane(loadLabel);
            loadStackpane.getChildren().add(loadProgressBar);
            loadStackpane.setAlignment(loadProgressBar, Pos.BOTTOM_CENTER);
            //loadStackpane.setStyle("-fx-background-color: green;-fx-background-insets: 0");
            Scene bootscene = new Scene(loadStackpane,180,30);
            bootscene.getStylesheets().add(getClass().getResource("/com/dbboys/css/app.css").toExternalForm());
            loadProgressBar.getStyleClass().add("loadProgressBar");
            loadingStage.setScene(bootscene);

            // 显示加载窗口
            loadingStage.show();

            //使用线程后台加载界面
            new Thread(() -> {

                //从配置文件读取分隔符位置，配置文件保存的是最后一次拖动的位置
                split1Pos= Double.parseDouble(ConfigManagerUtil.getProperty("SPLIT_DRIVER_MAIN"));
                split2Pos= Double.parseDouble(ConfigManagerUtil.getProperty("SPLIT_DRIVER_SQL"));

                //加载主界面
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/Main.fxml"));
                    Pane root= null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                mainController = loader.getController();
                scene = new Scene(root, 800, 600);

                scene.getStylesheets().add(getClass().getResource("/com/dbboys/css/app.css").toExternalForm());

                primaryStage.setTitle("DBboys");
                Image image = new Image("file:images/logo.png");
                primaryStage.getIcons().add(image);
               // primaryStage.initStyle(StageStyle.DECORATED);
                primaryStage.initStyle(StageStyle.UNDECORATED); //UNDECORATED可以避免加载treelist黑块现象

                // 在后台线程中预加载，避免首次点击时卡顿，首次打开sql编辑界面300+ms下降到50+ms
                new Thread(() -> {
                    try {
                        // 预加载FXML文件
                        FXMLLoader sqlTabLoader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/SqlTab.fxml"));
                        sqlTabLoader.load();
                        
                        //FXMLLoader resultSetLoader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/ResultSetTab.fxml"));
                        //resultSetLoader.load();
                        
                        // 预创建CustomSqlEditCodeArea实例
                        new CustomSqlEditCodeArea();
                    } catch (IOException e) {
                        log.error("预加载资源失败", e);
                    }
                }).start();
    


                //窗口切换
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setScene(scene);
                        //打开软件默认打开一个sql编辑面板
                        //Main.mainController.createNewTab(null);
                        //StageStyle.UNDECORATED
                        //StageStyle.DECORATED
                        //primaryStage.setMaximized(true);
                        loadingStage.hide();
                        primaryStage.show();//此处有黑色闪现，不使用系统自带窗口后正常
                        //页面渲染后增加监听,否则无法正常监听
                        mainController.main_splitpane.lookupAll(".split-pane-divider").forEach(divider -> {
                            // 鼠标拖动事件
                            divider.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
                                split1Pos=mainController.main_splitpane.getDividers().get(0).getPosition();
                            });
                        });

                        /*双击空白区域增加tab*/
                        if (mainController.sql_tabpane.lookup(".tab-header-area") != null) {
                            mainController.sql_tabpane.lookup(".tab-header-area").addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                                    // 检查双击的是否是 tab 空白区域（非 tab 或标签）
                                    var targetClass = event.getTarget().getClass().getName();
                                    if (!targetClass.contains("Tab") && !targetClass.contains("Label")) {
                                       // if(mainController.treeview_tabpane.getSelectionModel().getSelectedIndex()==0){
                                            TabpaneUtil.addCustomSqlTab(null);
                                    }
                                }
                            });
                        }

                        //Main.mainController.main_splitpane.prefWidthProperty().bind(primaryStage.widthProperty());

                        //打开软件默认最大化
                        Main.mainController.window_maxsize_button.fire();
                        //ResizeHelper.addResizeListener(primaryStage);
                        log.info("dbboys已启动。");

                    }
                });

            }).start();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    
    public static void main(String[] args) {

        launch(args);
    }
    private static boolean isInResizeZone(MouseEvent e, Stage stage) {
        return e.getX() > stage.getWidth() - 1 &&
                e.getY() > stage.getHeight() - 1;
    }
}
