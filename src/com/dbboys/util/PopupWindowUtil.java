package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.customnode.*;
import com.dbboys.vo.BackgroundSqlTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PopupWindowUtil {
    public static StackPane notice_pane=new StackPane();

    //about
    public static Stage about_pupupstage = new Stage();
    public static Label about_pupupstage_label=new Label(Main.VERSION.getVersion());
    public static StackPane about_pupupstage_stackpane=new StackPane(about_pupupstage_label);
    private static Scene about_pupupstage_sence = new Scene(about_pupupstage_stackpane, 400, 300);
    private static Image about_pupupstage_icon = new Image("file:images/logo.png");

    //sql历史记录
    private static Stage sql_his_popupstage = new Stage();
    private static CustomResultsetTableView sql_his_tableview = new CustomResultsetTableView();
    private static StackPane sql_his_popupstage_stackpane = new StackPane(sql_his_tableview);
    private static Scene sql_his_popupstage_sence = new Scene(sql_his_popupstage_stackpane, 1000, 500);
    private static Image sql_his_popupstage_icon = new Image("file:images/logo.png");
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_begin_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_stop_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_drution_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_affect_tablecolumn ;
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_sqlcol_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_markcol_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_his_tableview_database_tablecolumn;

    //后台sql
    public static TableView sql_task_tableview = new TableView();
    private static Stage backsql_popupstage = new Stage();
    private static StackPane backsql_popupstage_stackpane = new StackPane(sql_task_tableview);
    private static Scene backsql_popupstage_sence = new Scene(backsql_popupstage_stackpane, 1000, 500);
    private static Image backsql_popupstage_icon = new Image("file:images/logo.png");
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_rownum_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_id_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_begin_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_connname_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_database_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_sql_tablecolumn;
    private static TableColumn<ObservableList<String>, Object> sql_task_tableview_operater_tablecolumn ;

    //显示DDL
    private static Stage ddl_popupstage = new Stage();
    private static CustomInfoStackPane ddl_popupstage_stackpane = new CustomInfoStackPane(new CustomInfoCodeArea());
    private static Scene ddl_popupstage_sence = new Scene(ddl_popupstage_stackpane, 400, 300);
    private static Image ddl_popupstage_icon = new Image("file:images/logo.png");
    private static String ddl_popupstage_ddlsql="";
    private static Task ddl_popupstage_task;
    private static Label ddl_popupstage_loading_label=new Label();

    //显示巡检命令输出
    private static Stage checkoutput_popupstage = new Stage();
    private static CustomInfoStackPane checkoutput_popupstage_stackpane = new CustomInfoStackPane(new CustomInfoCodeArea());
    private static Scene checkoutput_popupstage_sence = new Scene(checkoutput_popupstage_stackpane, 600, 400);
    private static Image checkoutput_popupstage_icon = new Image("file:images/logo.png");

    //初始化
    static {
        //关于弹出面板
        about_pupupstage_stackpane.setAlignment(Pos.CENTER);
        about_pupupstage_sence.getStylesheets().add(PopupWindowUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        about_pupupstage.getIcons().add(about_pupupstage_icon);
        about_pupupstage.setScene(about_pupupstage_sence);
        about_pupupstage.setTitle("关于DBboys");
        about_pupupstage.initModality(Modality.APPLICATION_MODAL);

        //巡检双击弹出命令输出
        checkoutput_popupstage_sence.getStylesheets().add(PopupWindowUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        checkoutput_popupstage.getIcons().add(checkoutput_popupstage_icon);
        checkoutput_popupstage.setScene(checkoutput_popupstage_sence);
        checkoutput_popupstage.setTitle("命令输出");
        checkoutput_popupstage.initModality(Modality.APPLICATION_MODAL);

        //初始化通知面板
        notice_pane.setStyle("-fx-background-color: none;-fx-alignment: center");
        notice_pane.setMaxWidth(360);
        notice_pane.setMaxHeight(25);
        notice_pane.setVisible(false);

        //初始化sql历史记录表格
        //sql_his_tableview.setStyle("");
        //sql_his_tableview.getStyleClass().clear();
        //sql_his_tableview.getStylesheets().add(PopupWindowUtil.class.getResource("/com/dbboys/css/test.css").toExternalForm());

        sql_his_popupstage.initModality(Modality.APPLICATION_MODAL);

        sql_his_popupstage_sence.getStylesheets().add(PopupWindowUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        sql_his_popupstage.getIcons().add(sql_his_popupstage_icon);
        sql_his_popupstage.setScene(sql_his_popupstage_sence);
        sql_his_popupstage.setTitle("当前连接变更SQL执行历史记录");

        //sql_his_tableview.getSelectionModel().setCellSelectionEnabled(true);
        //sql_his_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        //定义结果集表结构
        sql_his_tableview_markcol_tablecolumn = new TableColumn<ObservableList<String>, Object>("备注");
        sql_his_tableview_markcol_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> result = new TableColumn<>("Execute Result");
        sql_his_tableview_markcol_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("mark"));
        //result.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(1)));
        sql_his_tableview_markcol_tablecolumn.setPrefWidth(120);
        sql_his_tableview_markcol_tablecolumn.setReorderable(false);
        sql_his_tableview_markcol_tablecolumn.setSortable(false);

        sql_his_tableview_begin_tablecolumn = new TableColumn<ObservableList<String>, Object>("开始时间");
        sql_his_tableview_begin_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> begin = new TableColumn<>("Begin Time");
        sql_his_tableview_begin_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        //begin.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(2)));
        sql_his_tableview_begin_tablecolumn.setPrefWidth(190);
        sql_his_tableview_begin_tablecolumn.setReorderable(false);
        sql_his_tableview_begin_tablecolumn.setSortable(false);

        sql_his_tableview_stop_tablecolumn = new TableColumn<ObservableList<String>, Object>("结束时间");
        sql_his_tableview_stop_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> stop= new TableColumn<>("End Time");
        sql_his_tableview_stop_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        //stop.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(3)));
        sql_his_tableview_stop_tablecolumn.setPrefWidth(190);
        sql_his_tableview_stop_tablecolumn.setReorderable(false);
        sql_his_tableview_stop_tablecolumn.setSortable(false);

        sql_his_tableview_drution_tablecolumn = new TableColumn<ObservableList<String>, Object>("执行耗时");
        sql_his_tableview_drution_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> drution = new TableColumn<>("Elapsed Time");
        sql_his_tableview_drution_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("elapsedTime"));
        //drution.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(4)));
        sql_his_tableview_drution_tablecolumn.setPrefWidth(100);
        sql_his_tableview_drution_tablecolumn.setReorderable(false);
        sql_his_tableview_drution_tablecolumn.setSortable(false);

        sql_his_tableview_database_tablecolumn = new TableColumn<ObservableList<String>, Object>("库/模式");
        sql_his_tableview_database_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> drution = new TableColumn<>("Elapsed Time");
        sql_his_tableview_database_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("database"));
        //drution.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(4)));
        sql_his_tableview_database_tablecolumn.setPrefWidth(100);
        sql_his_tableview_database_tablecolumn.setReorderable(false);
        sql_his_tableview_database_tablecolumn.setSortable(false);

        sql_his_tableview_affect_tablecolumn = new TableColumn<ObservableList<String>, Object>("更新行数");
        sql_his_tableview_affect_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, Integer> affect = new TableColumn<>("Affect Rows");
        sql_his_tableview_affect_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("affectedRows"));
        //affect.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(5)));
        sql_his_tableview_affect_tablecolumn.setPrefWidth(100);
        sql_his_tableview_affect_tablecolumn.setReorderable(false);
        sql_his_tableview_affect_tablecolumn.setSortable(false);

        sql_his_tableview_sqlcol_tablecolumn = new TableColumn<ObservableList<String>, Object>("执行语句");
        sql_his_tableview_sqlcol_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        //TableColumn<ObservableList<UpdateResult>, String> sqlcol = new TableColumn<>("Execute SQL");
        sql_his_tableview_sqlcol_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("updateSql"));
        //sqlcol.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(() -> data.getValue().get(6)));

        sql_his_tableview_sqlcol_tablecolumn.setPrefWidth(300);
        sql_his_tableview_sqlcol_tablecolumn.setReorderable(false);
        sql_his_tableview_sqlcol_tablecolumn.setSortable(false);
        sql_his_tableview.getColumns().addAll(sql_his_tableview_database_tablecolumn, sql_his_tableview_sqlcol_tablecolumn,sql_his_tableview_affect_tablecolumn,sql_his_tableview_drution_tablecolumn,sql_his_tableview_begin_tablecolumn, sql_his_tableview_stop_tablecolumn, sql_his_tableview_markcol_tablecolumn);


        //初始化后台任务表格
        backsql_popupstage.initModality(Modality.APPLICATION_MODAL);
        backsql_popupstage_sence.getStylesheets().add(PopupWindowUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        backsql_popupstage.getIcons().add(backsql_popupstage_icon);
        backsql_popupstage.setScene(backsql_popupstage_sence);
        backsql_popupstage_stackpane.getChildren().add(notice_pane);
        backsql_popupstage.setTitle("后台正在执行的sql任务");
        sql_task_tableview_rownum_tablecolumn = new TableColumn<>("");
        sql_task_tableview_rownum_tablecolumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ObservableList<String>, Object> call(TableColumn<ObservableList<String>, Object> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null); // 空行不显示行号
                        } else {
                            setText(String.valueOf(getIndex() + 1)); // 行号从 1 开始
                            setStyle("-fx-background-color: #f2f2f2;-fx-text-fill: black");
                            setOnMouseClicked(event -> {
                                int rowIndex = getIndex();
                                sql_task_tableview.getSelectionModel().clearAndSelect(rowIndex);
                            });
                        }
                    }
                };
            }
        });
        sql_task_tableview_rownum_tablecolumn.setSortable(false);
        sql_task_tableview_rownum_tablecolumn.setPrefWidth(30);

        sql_task_tableview_id_tablecolumn = new TableColumn<ObservableList<String>, Object>("ID");
        sql_task_tableview_id_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        sql_task_tableview_id_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sql_task_tableview_id_tablecolumn.setVisible(false);

        sql_task_tableview_begin_tablecolumn = new TableColumn<ObservableList<String>, Object>("开始时间");
        sql_task_tableview_begin_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        sql_task_tableview_begin_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("beginTime"));
        sql_task_tableview_begin_tablecolumn.setPrefWidth(200);
        sql_task_tableview_begin_tablecolumn.setReorderable(false);
        sql_task_tableview_begin_tablecolumn.setSortable(false);

        sql_task_tableview_connname_tablecolumn = new TableColumn<ObservableList<String>, Object>("连接名称");
        sql_task_tableview_connname_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        sql_task_tableview_connname_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("connName"));
        sql_task_tableview_connname_tablecolumn.setPrefWidth(300);
        sql_task_tableview_connname_tablecolumn.setReorderable(false);
        sql_task_tableview_connname_tablecolumn.setSortable(false);

        sql_task_tableview_database_tablecolumn = new TableColumn<ObservableList<String>, Object>("库名");
        sql_task_tableview_database_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        sql_task_tableview_database_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("databaseName"));
        sql_task_tableview_database_tablecolumn.setPrefWidth(100);
        sql_task_tableview_database_tablecolumn.setReorderable(false);
        sql_task_tableview_database_tablecolumn.setSortable(false);

        sql_task_tableview_sql_tablecolumn = new TableColumn<ObservableList<String>, Object>("SQL任务");
        sql_task_tableview_sql_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        sql_task_tableview_sql_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("sql"));
        sql_task_tableview_sql_tablecolumn.setPrefWidth(300);
        sql_task_tableview_sql_tablecolumn.setReorderable(false);
        sql_task_tableview_sql_tablecolumn.setSortable(false);

        sql_task_tableview_operater_tablecolumn = new TableColumn<ObservableList<String>, Object>("操作");
        sql_task_tableview_operater_tablecolumn.setCellFactory(col -> new CustomTableCell<ObservableList<String>, Object>());
        sql_task_tableview_operater_tablecolumn.setCellValueFactory(new PropertyValueFactory<>("operate"));
        sql_task_tableview_operater_tablecolumn.setPrefWidth(100);
        sql_task_tableview_operater_tablecolumn.setReorderable(false);
        sql_task_tableview_operater_tablecolumn.setSortable(false);

        sql_task_tableview_operater_tablecolumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ObservableList<String>, Object> call(TableColumn<ObservableList<String>, Object> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(null);
                        if(!empty) {
                            Button status_sql_stop_button = new Button("");
                            status_sql_stop_button.getStyleClass().add("little-custom-button");
                            status_sql_stop_button.setTooltip(new Tooltip("停止此任务"));
                            SVGPath status_sql_stop_button_icon = new SVGPath();
                            status_sql_stop_button_icon.setContent("M19.2031 6.0078 L19.2031 17.7734 Q19.2031 18.3516 18.7812 18.7734 Q18.3594 19.1953 17.7656 19.1953 L6 19.1953 Q5.5156 19.1953 5.1562 18.8516 Q4.8125 18.4922 4.8125 18.0078 L4.8125 6.2422 Q4.8125 5.6484 5.2344 5.2266 Q5.6562 4.8047 6.2344 4.8047 L18 4.8047 Q18.5 4.8047 18.8438 5.1641 Q19.2031 5.5078 19.2031 6.0078 L19.2031 6.0078 Z");
                            status_sql_stop_button_icon.setScaleX(0.5);
                            status_sql_stop_button_icon.setScaleY(0.5);
                            status_sql_stop_button_icon.setFill(Color.valueOf("#9f453c"));
                            status_sql_stop_button.setGraphic(new Group(status_sql_stop_button_icon));
                            status_sql_stop_button.setFocusTraversable(false);
                            setGraphic(status_sql_stop_button);
                            status_sql_stop_button.setOnAction(event -> {
                                try {
                                    ((BackgroundSqlTask)getTableRow().getItem()).getStmt().cancel();
                                    NotificationUtil.showNotification(notice_pane,"任务已取消！");
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                };
            }
        });


        sql_task_tableview.getColumns().addAll(sql_task_tableview_id_tablecolumn,sql_task_tableview_rownum_tablecolumn,sql_task_tableview_begin_tablecolumn,sql_task_tableview_connname_tablecolumn,sql_task_tableview_database_tablecolumn,sql_task_tableview_sql_tablecolumn,sql_task_tableview_operater_tablecolumn);
        sql_task_tableview.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE);
        sql_task_tableview.getSelectionModel().setCellSelectionEnabled(true);
        sql_task_tableview.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        //初始化ddl显示面板
        ddl_popupstage.initModality(Modality.APPLICATION_MODAL);
        ddl_popupstage_sence.getStylesheets().add(PopupWindowUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        ddl_popupstage.getIcons().add(ddl_popupstage_icon);
        ddl_popupstage.setScene(ddl_popupstage_sence);
        ddl_popupstage.setTitle("结构定义语句");
        ddl_popupstage_stackpane.showNoticeInMain=false;
        ImageView loadingImage = new ImageView(new Image("file:images/loading.gif"));
        loadingImage.setFitWidth(13);
        loadingImage.setFitHeight(13);
        ddl_popupstage_loading_label.setGraphic(loadingImage);
        ddl_popupstage.setOnCloseRequest(event -> {
            ddl_popupstage_stackpane.codeArea.replaceText("");
        });


    }

    public static void openSqlHistoryPopupWindow(Integer Id) {
        sql_his_tableview.getItems().clear();
        sql_his_tableview.getItems().addAll(SqliteDBaccessUtil.getSqlHistoryList(Id));
        sql_his_tableview.scrollTo(sql_his_tableview.getItems().size());
        sql_his_popupstage.show();
    }

    public static void openCmdoutputPopupWindow(String output) {
        checkoutput_popupstage_stackpane.codeArea.replaceText(output);
        checkoutput_popupstage_stackpane.showNoticeInMain=false;
        checkoutput_popupstage.show();
    }

    public static void openSqlTaskPopupWindow() {
        backsql_popupstage.show();
    }

    public static void openDDLWindow(String ddl_popupstage_ddlsql) {
        if(ddl_popupstage_ddlsql!=null&&!ddl_popupstage_ddlsql.isEmpty()){
            Platform.runLater(() -> {//两层确保按顺序执行，text加载完成后才开始设置高亮，避免未渲染完成就高量出现不可预知问题，如当前行莫名其妙被加粗
                ddl_popupstage_stackpane.codeArea.replaceText(ddl_popupstage_ddlsql);
                Platform.runLater(() -> {
                    ddl_popupstage_stackpane.codeArea.setStyleSpans(0,KeywordsHighlightUtil.applyHighlighting(ddl_popupstage_stackpane.codeArea.getText()));
                });
            });
            ddl_popupstage.show();


            //ddl_popupstage_stackpane.codeArea.showParagraphAtTop(0);
        }

    }

    public static void openAboutWindow() {
        about_pupupstage.show();

    }

    public static List openParamWindow( int paramCount) {
        List returnList=new ArrayList();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("输入SQL绑定变量参数");
        alert.setHeaderText("");
        alert.setGraphic(null);
        alert.getDialogPane().getScene().getStylesheets().add(
                PopupWindowUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm()
        );
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image("file:images/logo.png"));

        HBox hbox = new HBox();
        hbox.setId("modifyProps");
        hbox.setAlignment(Pos.CENTER_LEFT);

        CustomResultsetTableView tableView = new CustomResultsetTableView();
        TableColumn<ObservableList<String>, Object> paramcol = new TableColumn<>("参数值");
        paramcol.setCellValueFactory(data -> Bindings.createObjectBinding(() ->
                data.getValue().size() > 1 ? data.getValue().get(1) : null
        ));
        paramcol.setPrefWidth(350);
        paramcol.setCellFactory(col -> new CustomLostFocusCommitTableCell<>());
        paramcol.setOnEditCommit(event -> {
            ObservableList<String> rowData = event.getRowValue();
            Object newValue = event.getNewValue();
            if (rowData.size() > 1) {
                rowData.set(1, newValue.equals("[NULL]") ? null : newValue.toString());
                tableView.refresh();
            }
        });

        tableView.getColumns().add(paramcol);
        tableView.setEditable(true);
        ObservableList<ObservableList<String>> observableData= FXCollections.observableArrayList();
        tableView.setItems(observableData);
        for (int x = 0; x < paramCount; x++) {
            observableData.add(FXCollections.observableArrayList(null, null));
        }
        hbox.getChildren().add(tableView);
        alert.getDialogPane().setContent(hbox);

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        // 等待用户点击并获取结果
        ButtonType clicked = alert.showAndWait().orElse(buttonTypeCancel);
        // 将结果存入 AtomicBoolean
        if(clicked==buttonTypeOk){


            StringBuilder sb = new StringBuilder();
            //sb.append("，参数值[");
            for (ObservableList<String> row : observableData) {
                // 确保行数据不为null，且至少有2列（索引1存在）
                if (row != null && row.size() > 1) {
                    String secondColumn = row.get(1);
                    // 处理null值（转为空字符串或其他标识）
                    sb.append(secondColumn != null ? secondColumn : "[NULL]");
                    returnList.add(secondColumn);
                }
                // 每行的第二列之间用逗号分隔（最后一行后不加逗号）
                if (observableData.indexOf(row) != observableData.size() - 1) {
                    sb.append(",");
                }
            }

        }

        //sb.append("]");
        return returnList;
    }


}
