package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.ctrl.CreateConnectController;
import com.dbboys.customnode.*;

import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.service.ConnectionService;
import com.dbboys.service.DatabaseService;
import com.dbboys.service.FunctionService;
import com.dbboys.service.IndexService;
import com.dbboys.service.PackageService;
import com.dbboys.service.ProcedureService;
import com.dbboys.service.SequenceService;
import com.dbboys.service.SynonymService;
import com.dbboys.service.TableService;
import com.dbboys.service.TriggerService;
import com.dbboys.service.UserService;
import com.dbboys.service.ViewService;
import com.dbboys.impl.MetaObjectImpl;
import com.dbboys.vo.*;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;
import java.util.function.BiConsumer;



public class MetadataTreeviewUtil {
    private static final Logger log = LogManager.getLogger(MetadataTreeviewUtil.class);
    private enum ExportFormat {CSV, JSON, SQL}


    //public static ExecutorService executorService;
    public static ConnectionService connectionService;
    public static List<TreeItem<TreeData>> searchResults = new ArrayList<>();
    public static ConnectionService metadataService;
    public static DatabaseService databaseService;
    public static IndexService indexService;
    public static TableService tableService;
    public static TriggerService triggerService;
    public static ViewService viewService;
    public static SequenceService sequenceService;
    public static SynonymService synonymService;
    public static FunctionService functionService;
    public static ProcedureService procedureService;
    public static PackageService packageService;
    public static UserService userService;
    public static CustomShortcutMenuItem refreshItem;
    public static CustomShortcutMenuItem databaseOpenFileItem;
    public static CustomShortcutMenuItem connectFolderInfoItem;
    public static CustomShortcutMenuItem connectInfoItem;
    public   static int currentIndex = -1;
    public static Thread testConnThread;
    public static String ddl="";
    static{
        //executorService = Executors.newSingleThreadExecutor();
        connectionService = new ConnectionService();
        metadataService = new ConnectionService();
        databaseService = new DatabaseService();
        indexService = new IndexService();
        tableService = new TableService();
        triggerService = new TriggerService();
        viewService = new ViewService();
        sequenceService = new SequenceService();
        synonymService = new SynonymService();
        functionService = new FunctionService();
        procedureService = new ProcedureService();
        packageService = new PackageService();
        userService = new UserService();
    }



    public static void initDatabaseObjectsTreeview(TreeView<TreeData> treeView){
        TreeItem<TreeData> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        treeView.getSelectionModel().select(rootItem);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.setShowRoot(false);
        List<TreeData> connectFolders = SqliteDBaccessUtil.getConnectFolders();
        List<TreeData> connectLeafs = SqliteDBaccessUtil.getConnectLeafs();
        //添加分类节点
        for(TreeData connectFolder : connectFolders){
            TreeItem<TreeData> connectFolderItem = createTreeItem(connectFolder);
            if(((ConnectFolder)connectFolder).getExpand()==1){
                connectFolderItem.setExpanded(true);
            }else{
                connectFolderItem.setExpanded(false);
            }
            rootItem.getChildren().add(connectFolderItem);

            //每个分类添加子节点
            for(TreeData connectLeaf : connectLeafs){
                if(((Connect)connectLeaf).getParentId()==((ConnectFolder)connectFolder).getId()){
                    TreeItem<TreeData> connectLeafItem = createTreeItem(connectLeaf);
                    connectFolderItem.getChildren().add(connectLeafItem);
                }
            }

        }

        //右键弹出框
        ContextMenu treeview_menu = new ContextMenu();
        //单独设置宽度
        //treeview_menu.getStyleClass().add("exclude-css");

        CustomShortcutMenuItem addUserItem = MenuItemUtil.createMenuItemI18n("metadata.menu.add_user",
                IconFactory.group(IconPaths.METADATA_ADD_USER, 0.55, 0.55));
        CustomShortcutMenuItem modifyUserItem = MenuItemUtil.createMenuItemI18n("metadata.menu.reset_password",
                IconFactory.group(IconPaths.METADATA_MODIFY_USER, 0.5, 0.5));
        CustomShortcutMenuItem copyItem = MenuItemUtil.createMenuItemI18n("metadata.menu.copy_name", "Ctrl+C",
                IconFactory.group(IconPaths.METADATA_COPY_ITEM, 0.65, 0.65));
        CustomShortcutMenuItem packageDDLItem = MenuItemUtil.createMenuItemI18n("metadata.menu.show_package_ddl",
                IconFactory.group(IconPaths.METADATA_PACKAGE_DDL_ITEM, 0.6, 0.6));
        CustomShortcutMenuItem modifyToRawItem = MenuItemUtil.createMenuItemI18n("metadata.menu.modify_to_raw",
                IconFactory.group(IconPaths.METADATA_MODIFY_TO_RAW_ITEM, 0.6, 0.6));
        CustomShortcutMenuItem modifyToStandardItem = MenuItemUtil.createMenuItemI18n("metadata.menu.modify_to_standard",
                IconFactory.group(IconPaths.METADATA_MODIFY_TO_STANDARD_ITEM, 0.6, 0.6));
        CustomShortcutMenuItem sqlHisItem = MenuItemUtil.createMenuItemI18n("metadata.menu.sql_history",
                IconFactory.group(IconPaths.METADATA_SQL_HIS_ITEM, 0.85, 0.85));
        CustomShortcutMenuItem updateStatisticsItem = MenuItemUtil.createMenuItemI18n("metadata.menu.update_statistics",
                IconFactory.group(IconPaths.METADATA_UPDATE_STATISTICS_ITEM, 0.66, 0.66));
        CustomShortcutMenuItem truncateItem = MenuItemUtil.createMenuItemI18n("metadata.menu.truncate",
                IconFactory.group(IconPaths.METADATA_TRUNCATE_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem enableItem = MenuItemUtil.createMenuItemI18n("metadata.menu.enable",
                IconFactory.group(IconPaths.METADATA_ENABLE_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem disableItem = MenuItemUtil.createMenuItemI18n("metadata.menu.disable",
                IconFactory.group(IconPaths.METADATA_DISABLE_ITEM, 0.06, 0.06));
        connectFolderInfoItem = MenuItemUtil.createMenuItemI18n("metadata.menu.folder_connect_info",
                IconFactory.group(IconPaths.METADATA_CONNECT_FOLDER_INFO_ITEM, 0.55, 0.55));
        CustomShortcutMenuItem addSystemLevel = MenuItemUtil.createMenuItemI18n("metadata.menu.add_system_level",
                IconFactory.group(IconPaths.METADATA_ADD_SYSTEM_LEVEL, 0.7, 0.7));
        CustomShortcutMenuItem createConnectItem = MenuItemUtil.createMenuItemI18n("metadata.menu.create_connection",
                IconFactory.group(IconPaths.METADATA_CREATE_CONNECT_ITEM, 0.6, 0.6));
        CustomShortcutMenuItem connectOpenFileItem = MenuItemUtil.createMenuItemI18n("metadata.menu.new_sql",
                IconFactory.group(IconPaths.METADATA_CONNECT_OPEN_FILE_ITEM, 0.65, 0.65));
        databaseOpenFileItem = MenuItemUtil.createMenuItemI18n("metadata.menu.new_sql", "Ctrl+N",
                IconFactory.group(IconPaths.METADATA_DATABASE_OPEN_FILE_ITEM, 0.6, 0.55));
        //MenuItem disconnectAll = new MenuItem("断开所有连接(Disconnect ALL)",disconnectItemIcon);
        CustomShortcutMenuItem disconnectFolder = MenuItemUtil.createMenuItemI18n("metadata.menu.disconnect_folder",
                IconFactory.group(IconPaths.METADATA_DISCONNECT_FOLDER, 0.6, 0.6));
        CustomShortcutMenuItem renameItem = MenuItemUtil.createMenuItemI18n("metadata.menu.rename", "F2",
                IconFactory.group(IconPaths.METADATA_RENAME_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem deleteItem = MenuItemUtil.createMenuItemI18n("metadata.menu.delete", "Delete",
                IconFactory.group(IconPaths.METADATA_DELETE_ITEM, 0.6, 0.6, IconFactory.dangerColor()));
        CustomShortcutMenuItem expandFolderItem = MenuItemUtil.createMenuItemI18n("metadata.menu.expand_default",
                IconFactory.group(IconPaths.METADATA_EXPAND_FOLDER_ITEM, 0.5, 0.5));
        CustomShortcutMenuItem foldFolderItem = MenuItemUtil.createMenuItemI18n("metadata.menu.collapse_default",
                IconFactory.group(IconPaths.METADATA_FOLD_FOLDER_ITEM, 0.75, 0.75));
        CustomShortcutMenuItem moveItem = MenuItemUtil.createMenuItemI18n("metadata.menu.move_to",
                IconFactory.group(IconPaths.METADATA_MOVE_ITEM, 0.7, 0.7));
        refreshItem = MenuItemUtil.createMenuItemI18n("metadata.menu.refresh", "F5",
                IconFactory.group(IconPaths.METADATA_REFRESH_ITEM, 0.7, 0.7));
        connectInfoItem = MenuItemUtil.createMenuItemI18n("metadata.menu.instance_info",
                IconFactory.group(IconPaths.METADATA_CONNECT_INFO_ITEM, 0.55, 0.55));
        CustomShortcutMenuItem connectItem = MenuItemUtil.createMenuItemI18n("metadata.menu.connect",
                IconFactory.group(IconPaths.METADATA_CONNECT_ITEM, 0.65, 0.65));
        CustomShortcutMenuItem reconnectItem = MenuItemUtil.createMenuItemI18n("metadata.menu.reconnect",
                IconFactory.group(IconPaths.METADATA_RECONNECT_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem disconnectItem = MenuItemUtil.createMenuItemI18n("metadata.menu.disconnect",
                IconFactory.group(IconPaths.METADATA_DISCONNECT_ITEM, 0.6, 0.6));
        CustomShortcutMenuItem copyconnectItem = MenuItemUtil.createMenuItemI18n("metadata.menu.copy_connection",
                IconFactory.group(IconPaths.METADATA_COPY_CONNECT_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem modifyconnectItem = MenuItemUtil.createMenuItemI18n("metadata.menu.modify_connection",
                IconFactory.group(IconPaths.METADATA_MODIFY_CONNECT_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem createDatabaseItem = MenuItemUtil.createMenuItemI18n("metadata.menu.create_database",
                IconFactory.group(IconPaths.METADATA_CREATE_DATABASE_ITEM, 0.7, 0.7));
        CustomShortcutMenuItem createTableItem = MenuItemUtil.createMenuItemI18n("metadata.menu.create_table",
                IconFactory.group(IconPaths.METADATA_CREATE_TABLE_ITEM, 0.6, 0.6));
        CustomShortcutMenuItem setDefaultDatabaseItem = MenuItemUtil.createMenuItemI18n("metadata.menu.set_default_database",
                IconFactory.group(IconPaths.METADATA_SET_DEFAULT_DATABASE_ITEM, 0.6, 0.6));
        //setDefaultDatabaseItem.disableProperty().bind(trans_not_committed_buttons_hbox.visibleProperty());
        SeparatorMenuItem separator1 = new SeparatorMenuItem(); // 第一个分隔线
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        Menu exportMenu = new Menu();
        exportMenu.textProperty().bind(I18n.bind("metadata.menu.export", "导出"));
        exportMenu.setGraphic(IconFactory.group(IconPaths.RESULTSET_EXPORT, 0.55, 0.55));
        CustomShortcutMenuItem exportCsvItem = MenuItemUtil.createMenuItemI18n("metadata.menu.export.csv",null);
        CustomShortcutMenuItem exportJsonItem = MenuItemUtil.createMenuItemI18n("metadata.menu.export.json",null);
        CustomShortcutMenuItem exportSqlItem = MenuItemUtil.createMenuItemI18n("metadata.menu.export.sql",null);
 // 第一个分隔线
        CustomShortcutMenuItem healthCheckItem = MenuItemUtil.createMenuItemI18n("metadata.menu.health_check",
                IconFactory.group(IconPaths.METADATA_HEALTH_CHECK_ITEM, 0.65, 0.65));
        CustomShortcutMenuItem onlinelogItem = MenuItemUtil.createMenuItemI18n("metadata.menu.online_log",
                IconFactory.group(IconPaths.METADATA_ONLINE_LOG_ITEM, 0.5, 0.5));
        CustomShortcutMenuItem spaceManagerItem = MenuItemUtil.createMenuItemI18n("metadata.menu.space_manager",
                IconFactory.group(IconPaths.METADATA_SPACE_MANAGER_ITEM, 0.55, 0.55));
        CustomShortcutMenuItem onconfigItem = MenuItemUtil.createMenuItemI18n("metadata.menu.onconfig",
                IconFactory.group(IconPaths.METADATA_ONCONFIG_ITEM, 0.55, 0.55));
        CustomShortcutMenuItem instanceStopItem = MenuItemUtil.createMenuItemI18n("metadata.menu.instance_start_stop",
                IconFactory.group(IconPaths.METADATA_INSTANCE_STOP_ITEM, 0.65, 0.65, IconFactory.dangerColor()));

        Menu ddlMenu = new Menu();
        ddlMenu.textProperty().bind(I18n.bind("metadata.menu.ddl.title", "查看DDL"));
        ddlMenu.getStyleClass().add("ddlMenu");
        ddlMenu.setGraphic(IconFactory.group(IconPaths.METADATA_DDL_MENU, 0.65, 0.65));

        CustomShortcutMenuItem ddlToFile =
                MenuItemUtil.createMenuItemI18n("metadata.menu.ddl.to_file", null);
        CustomShortcutMenuItem ddlToClipboard  =
                MenuItemUtil.createMenuItemI18n("metadata.menu.ddl.to_clipboard", null);
        CustomShortcutMenuItem ddlToCurrentSqlEditarea =
                MenuItemUtil.createMenuItemI18n("metadata.menu.ddl.to_current_sql", null);
        CustomShortcutMenuItem ddlToNewSqlEditarea =
                MenuItemUtil.createMenuItemI18n("metadata.menu.ddl.to_new_sql", null);
        CustomShortcutMenuItem ddlToPopuWindow =
                MenuItemUtil.createMenuItemI18n("metadata.menu.ddl.to_popup_window", null);
        ddlMenu.getItems().addAll(ddlToClipboard,ddlToPopuWindow,ddlToFile,ddlToCurrentSqlEditarea,ddlToNewSqlEditarea);
        
        
        //右键连接信息点击响应
        connectInfoItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            TabpaneUtil.addCustomInstanceTab(connect,0);
        });


        healthCheckItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            TabpaneUtil.addCustomInstanceTab(connect,1);
        });

        onlinelogItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            TabpaneUtil.addCustomInstanceTab(connect,2);
        });

        spaceManagerItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            TabpaneUtil.addCustomInstanceTab(connect,3);
        });
        onconfigItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            TabpaneUtil.addCustomInstanceTab(connect,4);
        });

        instanceStopItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            TabpaneUtil.addCustomInstanceTab(connect,5);
        });

        //点击鼠标后右键弹出框隐藏
        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1) {
                treeview_menu.hide();
            }
        });

        //加载进度
        Main.loadProgressBar.setProgress(0.8);




        treeView.setOnKeyPressed((KeyEvent event) -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null || selectedItem.getValue() == null) {
                return;
            }
            if (event.getCode() == KeyCode.C && event.isControlDown()) {
                if (canCopyItem(selectedItem)) {
                    copyItem.fire();
                    event.consume();
                }
                return;
            }
            if (event.getCode() == KeyCode.N && event.isControlDown()) {
                if (selectedItem.getValue() instanceof Database) {
                    databaseOpenFileItem.fire();
                    event.consume();
                }
                return;
            }
            if (event.getCode() == KeyCode.F5) {
                if (canRefreshItem(selectedItem)) {
                    refreshItem.fire();
                    event.consume();
                }
                return;
            }
            if (event.getCode() == KeyCode.F2) {
                if (canRenameItem(selectedItem)) {
                    renameItem.fire();
                    event.consume();
                }
                return;
            }
            if (event.getCode() == KeyCode.DELETE) {
                if (canDeleteItem(selectedItem)) {
                    deleteItem.fire();
                    event.consume();
                }
            }
        });

        copyItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedItem.getValue().getName());
            clipboard.setContent(content);
            //NotificationUtil.showNotification(Main.mainController.noticePane, "对象名称已复制");
        });
        packageDDLItem.setOnAction(event-> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeData connect = selectedItem.getValue();
            if (selectedItem.getChildren().size()==0&&!selectedItem.isExpanded()) {
                ((DBPackage)selectedItem.getValue()).setShowDDL(true);
                selectedItem.setExpanded(true);
                selectedItem.setExpanded(false);
            } else {
                PopupWindowUtil.openDDLWindow(((DBPackage)connect).getDDL());
            }
        });


        //右键弹出框及点击响应事件结束

        //改为裸表
        modifyToRawItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeData treeData = selectedItem.getValue();
            Boolean confirm = AlterUtil.CustomAlertConfirm(
                    I18n.t("metadata.alert.modify_to_raw.title", "改为裸表"),
                    I18n.t("metadata.alert.modify_to_raw.content", "确定将表\"%s\"更改为裸表吗？裸表具有更高的性能但不支持事务回滚，不建议在生产环境使用！")
                            .formatted(treeData.getName())
            );
            if(confirm){
                Connect connect = buildObjectConnect(selectedItem, false);
                tableService.modifyTableToRaw(connect, treeData.getName(), () -> 
                {
                    ((Table)treeData).setTableTypeCode("raw");
                    NotificationUtil.showNotification(
                        Main.mainController.noticePane,
                        I18n.t("backsql.notice.table_raw", "表\"%s\"已改为裸表！").formatted(treeData.getName())
                    );
                }
            );
            }
        });

        //改为标准表
        modifyToStandardItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeData treeData = selectedItem.getValue();
            Boolean confirm = AlterUtil.CustomAlertConfirm(
                    I18n.t("metadata.alert.modify_to_standard.title", "改为标准表"),
                    I18n.t("metadata.alert.modify_to_standard.content", "确定将表\"%s\"更改为标准表吗？")
                            .formatted(treeData.getName())
            );
            if(confirm){
                Connect connect = buildObjectConnect(selectedItem, false);
                tableService.modifyTableToStandard(connect, treeData.getName(), () -> 
                {
                    ((Table)treeData).setTableTypeCode("standard");
                    NotificationUtil.showNotification(
                            Main.mainController.noticePane,
                            I18n.t("backsql.notice.table_standard", "表\"%s\"已改为标准表！").formatted(treeData.getName())
                    );
            }
            );
            }
        });

        //清空表事件
        truncateItem.setOnAction(event -> {
            List<TreeItem<TreeData>> selectedItems = new ArrayList<>(treeView.getSelectionModel().getSelectedItems());
            if (isMultiTableSelection(selectedItems)) {
                boolean hasExternalTable = false;
                for (TreeItem<TreeData> item : selectedItems) {
                    if ("external".equals(((Table) item.getValue()).getTableTypeCode())) {
                        hasExternalTable = true;
                        break;
                    }
                }
                if (hasExternalTable) {
                    AlterUtil.CustomAlert(
                            I18n.t("common.error", "错误"),
                            I18n.t("metadata.alert.truncate.external_not_supported", "选中项中包含外部表，无法批量清空！")
                    );
                    return;
                }
                boolean confirmBatch = AlterUtil.CustomAlertConfirm(
                        I18n.t("metadata.alert.truncate.title", "清空表"),
                        I18n.t("metadata.alert.truncate.batch_content", "确定要清空选中的%d个表吗？")
                                .formatted(selectedItems.size())
                );
                if (!confirmBatch) {
                    return;
                }
                Connect connect = buildObjectConnect(selectedItems.get(0), false);
                List<String> sqlList = new ArrayList<>();
                for (TreeItem<TreeData> item : selectedItems) {
                    sqlList.add("truncate table " + item.getValue().getName());
                }
                tableService.executeObjectSqls(connect, sqlList, () -> {
                    for (TreeItem<TreeData> item : selectedItems) {
                        item.getValue().setRunning(true);
                        tableService.refreshTableMeta(
                                MetadataTreeviewUtil.getMetaConnect(item),
                                MetadataTreeviewUtil.getCurrentDatabase(item),
                                item.getValue().getName(),
                                item::setValue,
                                () -> item.getValue().setRunning(false)
                        );
                    }
                    NotificationUtil.showNotification(
                            Main.mainController.noticePane,
                            I18n.t("backsql.notice.batch_table_truncate_submitted", "已提交%d个表的清空任务！")
                                    .formatted(selectedItems.size())
                    );
                });
                return;
            }
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeData treeData = selectedItem.getValue();
            Boolean confirm = AlterUtil.CustomAlertConfirm(
                    I18n.t("metadata.alert.truncate.title", "清空表"),
                    I18n.t("metadata.alert.truncate.content", "确定要清空表\"%s\"吗？")
                            .formatted(treeData.getName())
            );
            if(confirm){
                Connect connect = buildObjectConnect(selectedItem, false);
                tableService.truncateTable(connect, treeData.getName(), () -> {
                    selectedItem.getValue().setRunning(true);
                    tableService.refreshTableMeta(
                            MetadataTreeviewUtil.getMetaConnect(selectedItem),
                            MetadataTreeviewUtil.getCurrentDatabase(selectedItem),
                            selectedItem.getValue().getName(),
                            selectedItem::setValue,
                            () -> selectedItem.getValue().setRunning(false)
                    );

                    NotificationUtil.showNotification(
                        Main.mainController.noticePane,
                        I18n.t("backsql.notice.table_truncated", "表\"%s\"已清空！").formatted(treeData.getName())
                );

                }
            );
            }
        });


        //禁用
        disableItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            toggleObjectEnabled(selectedItem, false);
        });

        //启用
        enableItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            toggleObjectEnabled(selectedItem, true);
        });

        sqlHisItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            PopupWindowUtil.openSqlHistoryPopupWindow(((Connect)selectedItem.getValue()).getId());
        });


        connectFolderInfoItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TabpaneUtil.addConnectsInfoTab((ConnectFolder)selectedItem.getValue());

        });
        /*
        connectOpenFileItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = databasemeta_treeview.getSelectionModel().getSelectedItem();
            ComstomTabUtil.addCustomSqlTab(sql_tabpane,selectedItem.getValue());
        });
    */
        databaseOpenFileItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect(MetadataTreeviewUtil.getMetaConnect(selectedItem));
            Database database=MetadataTreeviewUtil.getCurrentDatabase(selectedItem);
            connect.setProps(metadataService.modifyProps(connect,database.getDbLocale()));
            connect.setDatabase(database.getName());
            TabpaneUtil.addCustomSqlTab(connect);
        });




        //右键连接点击响应
        connectItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            selectedItem.getChildren().clear();
            selectedItem.setExpanded(false);
            selectedItem.setExpanded(true);
        });
        //右键重连点击响应
        reconnectItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            MetadataTreeviewUtil.reconnectItem(selectedItem);
        });
        //右键断开连接点击响应
        disconnectItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            disconnectItem(selectedItem);
        });



        //断开分类下所有连接
        disconnectFolder.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            MetadataTreeviewUtil.disconnectFolder(selectedItem);
        });


        //右键新建连接点击响应
        createConnectItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeData connect= selectedItem.getValue();
            showCreateConnectDialog(connect,false);
        });

        //右键编辑点击响应
        modifyconnectItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=(Connect)selectedItem.getValue();
            showCreateConnectDialog(connect,false);
        });

        //右键复制连接点击响应
        copyconnectItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Connect connect=new Connect((Connect) selectedItem.getValue());
            connect.setConn(null);
            connect.setName(SqliteDBaccessUtil.getCopyName(connect));
            showCreateConnectDialog(connect,true);
            //String result=SqliteDBaccessUtil.createConnect(connect);
            //--------/*

        });


        //右键重命名点击响应
        renameItem.setOnAction(event -> {
            renameTreeItem(treeView);
        });

        //创建用户
        addUserItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Dialog<ButtonType> dialog=new Dialog<>();
            dialog.setTitle(I18n.t("metadata.dialog.create_user.title", "创建用户"));
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
            Button commit = (Button) dialog.getDialogPane().lookupButton(ButtonType.FINISH);
            Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            commit.setText(I18n.t("metadata.button.create", "创建"));
            cancelBtn.setText(I18n.t("common.cancel", "取消"));
            dialog.initOwner(Main.scene.getWindow());


            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(5);
            grid.setPadding(new Insets(10));
            CustomUserTextField userName = new CustomUserTextField();
            CustomPasswordField passwordField1 = new CustomPasswordField();
            CustomPasswordField passwordField2 = new CustomPasswordField();
            userName.requestFocus();
            Label nameLabel=new Label(I18n.t("metadata.label.username", "用户名")) ;
            SVGPath nameLabelIcon = IconFactory.create(IconPaths.METADATA_NAME_LABEL, 0.55, 0.55, Color.valueOf("#888"));
            nameLabel.setGraphic(nameLabelIcon);

            Label passwordLabel=new Label(I18n.t("metadata.label.password", "密码"));

            SVGPath passwordLabelIcon = IconFactory.create(IconPaths.METADATA_PASSWORD_LABEL, 0.5, 0.5, Color.valueOf("#888"));
            passwordLabel.setGraphic(passwordLabelIcon);

            Label confirmPasswordLabel=new Label(I18n.t("metadata.label.confirm_password", "确认密码"));

            SVGPath confirmPasswordLabelIcon = IconFactory.create(IconPaths.METADATA_CONFIRM_PASSWORD_LABEL, 0.5, 0.5, Color.valueOf("#888"));
            confirmPasswordLabel.setGraphic(confirmPasswordLabelIcon);

            grid.add(nameLabel, 0, 0);
            grid.add(userName, 1, 0);
            grid.add(passwordLabel, 0, 1);
            grid.add(passwordField1, 1, 1);
            grid.add(confirmPasswordLabel, 0, 2);
            grid.add(passwordField2, 1, 2);
            dialog.getDialogPane().setContent(grid);


            commit.addEventFilter(ActionEvent.ACTION, event1 -> {
                if (userName.getText().trim().isEmpty()) {
                    userName.requestFocus();
                    event1.consume();
                } else if (passwordField1.getText().trim().isEmpty()) {
                    passwordField1.requestFocus();
                    event1.consume();
                } else if (passwordField2.getText().trim().isEmpty()) {
                    passwordField2.requestFocus();
                    event1.consume();
                } else if (!passwordField1.getText().trim().equals(passwordField2.getText().trim())) {
                    AlterUtil.CustomAlert(I18n.t("common.error", "错误"), I18n.t("metadata.error.password_not_match", "两次密码输入不一致！"));
                    event1.consume();
                } else {
                    event1.consume();
                    Connect connect=buildObjectConnect(selectedItem,true);
                    userService.executeObjectSql(connect, "create user " + userName.getText().trim() + " with password '" + passwordField1.getText().trim() + "'", 
                    () -> {
                        selectedItem.getChildren().clear();
                        selectedItem.setExpanded(false);
                        selectedItem.setExpanded(true);
                        NotificationUtil.showNotification(
                                Main.mainController.noticePane,
                                I18n.t("metadata.success.create_user", "用户创建成功！")
                        );
                        dialog.close();
                    }
                );
                }

            });

            dialog.showAndWait();

        });

        //创建用户
        modifyUserItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Dialog<ButtonType> dialog=new Dialog<>();
            dialog.setTitle(I18n.t("metadata.dialog.reset_password.title", "重置密码"));
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
            Button commit = (Button) dialog.getDialogPane().lookupButton(ButtonType.FINISH);
            Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            commit.setText(I18n.t("metadata.button.reset", "重置"));
            cancelBtn.setText(I18n.t("common.cancel", "取消"));
            dialog.initOwner(Main.scene.getWindow());


            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(5);
            grid.setPadding(new Insets(10));
            CustomUserTextField userName = new CustomUserTextField();
            CustomPasswordField passwordField1 = new CustomPasswordField();
            CustomPasswordField passwordField2 = new CustomPasswordField();


            Label passwordLabel=new Label(I18n.t("metadata.label.new_password", "新密码"));

            SVGPath passwordLabelIcon = IconFactory.create(IconPaths.METADATA_PASSWORD_LABEL, 0.5, 0.5, Color.valueOf("#888"));
            passwordLabel.setGraphic(passwordLabelIcon);

            Label confirmPasswordLabel=new Label(I18n.t("metadata.label.confirm_password", "确认密码"));

            SVGPath confirmPasswordLabelIcon = IconFactory.create(IconPaths.METADATA_CONFIRM_PASSWORD_LABEL, 0.5, 0.5, Color.valueOf("#888"));
            confirmPasswordLabel.setGraphic(confirmPasswordLabelIcon);


            grid.add(passwordLabel, 0, 0);
            grid.add(passwordField1, 1, 0);
            grid.add(confirmPasswordLabel, 0, 1);
            grid.add(passwordField2, 1, 1);
            dialog.getDialogPane().setContent(grid);


            commit.addEventFilter(ActionEvent.ACTION, event1 -> {
                if (passwordField1.getText().trim().isEmpty()) {
                    passwordField1.requestFocus();
                    event1.consume();
                } else if (passwordField2.getText().trim().isEmpty()) {
                    passwordField2.requestFocus();
                    event1.consume();
                } else if (!passwordField1.getText().trim().equals(passwordField2.getText().trim())) {
                    AlterUtil.CustomAlert(I18n.t("common.error", "错误"), I18n.t("metadata.error.password_not_match", "两次密码输入不一致！"));
                    event1.consume();
                } else {
                    event1.consume();
                    Connect connect=buildObjectConnect(selectedItem,true);
                    userService.executeObjectSql(
                            connect,
                            "alter user " + selectedItem.getValue().getName() + " modify password '" + passwordField1.getText().trim() + "'",
                            () -> {
                                NotificationUtil.showNotification(
                                        Main.mainController.noticePane,
                                        I18n.t("backsql.notice.user_password_reset", "用户\"%s\"密码已重置！")
                                                .formatted(selectedItem.getValue().getName())
                                );
                                dialog.close();
                            }
                    );
                }

            });

            dialog.showAndWait();

        });

        updateStatisticsItem.setOnAction(event -> {
            List<TreeItem<TreeData>> selectedItems = new ArrayList<>(treeView.getSelectionModel().getSelectedItems());
            if (isMultiTableSelection(selectedItems)) {
                boolean confirmBatch = AlterUtil.CustomAlertConfirm(
                        I18n.t("backsql.confirm.update_statistics.title", "统计更新"),
                        I18n.t("backsql.confirm.update_statistics.batch_content", "确定要对选中的%d个表执行统计更新吗？")
                                .formatted(selectedItems.size())
                );
                if (!confirmBatch) {
                    return;
                }
                Connect connect = buildObjectConnect(selectedItems.get(0), false);
                List<String> sqlList = new ArrayList<>();
                for (TreeItem<TreeData> item : selectedItems) {
                    sqlList.add("update statistics for table " + item.getValue().getName());
                }
                tableService.executeObjectSqls(connect, sqlList, () -> NotificationUtil.showNotification(
                        Main.mainController.noticePane,
                        I18n.t("backsql.notice.batch_update_statistics_submitted", "%d个表统计更新已完成！")
                                .formatted(selectedItems.size())
                ));
                return;
            }
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeData treeData = selectedItem.getValue();
            Connect connect = buildObjectConnect(selectedItem, false);
            boolean confirm = AlterUtil.CustomAlertConfirm(
                        I18n.t("backsql.confirm.update_statistics.title", "统计更新"),
                        I18n.t("backsql.confirm.update_statistics.content", "确定要执行统计更新吗？")
                );
            if (!confirm) {
                    return;
                }
            if (treeData instanceof Database) {
                databaseService.updateStatistics(connect, "update statistics", ()->{
                    NotificationUtil.showNotification(Main.mainController.noticePane,I18n.t("backsql.notice.update_statistics_done", "统计更新执行完成！"));
                });                
            }
            else if (treeData instanceof ObjectFolder) {
                ObjectFolderKind objectFolderKind = getObjectFolderKind(selectedItem);
                if(objectFolderKind == ObjectFolderKind.SYSTEM_TABLE_VIEW || objectFolderKind == ObjectFolderKind.TABLES){
                    tableService.updateStatistics(connect, "update statistics high for table force", ()->{
                        NotificationUtil.showNotification(Main.mainController.noticePane,I18n.t("backsql.notice.update_statistics_done", "统计更新执行完成！"));
                    });    
                }
                else if(objectFolderKind == ObjectFolderKind.PROCEDURES){
                    procedureService.updateStatistics(connect, "update statistics for procedure", ()->{
                        NotificationUtil.showNotification(Main.mainController.noticePane,I18n.t("backsql.notice.update_statistics_done", "统计更新执行完成！"));
                    });    
                }
            }
            else if(treeData instanceof SysTable||treeData instanceof Table){
                    tableService.updateStatisticsForTable(connect, treeData.getName(), ()->{
                        NotificationUtil.showNotification(Main.mainController.noticePane,I18n.t("backsql.notice.update_statistics_done", "统计更新执行完成！"));
                    });
                    
            }
            else if(treeData instanceof Procedure){
                    procedureService.updateStatistics(connect,"update statistics for procedure "+ treeData.getName(), ()->{
                        NotificationUtil.showNotification(Main.mainController.noticePane,I18n.t("backsql.notice.update_statistics_done", "统计更新执行完成！"));
                    });  
            }
        });

        ddlToFile.setOnAction(event -> handleDdlAction(treeView, (treeData, ddlText) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18n.t("metadata.menu.ddl.to_file", "保存DDL为SQL"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
            fileChooser.setInitialFileName(treeData.getName() + ".sql");
            File file = fileChooser.showSaveDialog(Main.scene.getWindow());
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(ddlText);
                    NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("metadata.notice.ddl_saved", "DDL已保存到文件"));
                } catch (IOException e) {
                    AlterUtil.CustomAlert(I18n.t("common.error", "错误"), e.getMessage());
                }
            }
        }));

        ddlToClipboard.setOnAction(event -> handleDdlAction(treeView, (treeData, ddlText) -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(ddlText);
            clipboard.setContent(content);
            NotificationUtil.showNotification(Main.mainController.noticePane, "DDL已复制到剪切板");
        }));

        ddlToPopuWindow.setOnAction(event -> handleDdlAction(treeView, (treeData, ddlText) -> {
            PopupWindowUtil.openDDLWindow(ddlText);
        }));

        ddlToNewSqlEditarea.setOnAction(event -> handleDdlAction(treeView, (treeData, ddlText) -> {
            Main.mainController.newSqlFileMenuItem.fire();
            if (Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem() instanceof CustomSqlTab currentSqlTab) {
                currentSqlTab.sqlTabController.sqlEditCodeArea.replaceText(ddlText);
            }
        }));

        ddlToCurrentSqlEditarea.setOnAction(event -> handleDdlAction(treeView, (treeData, ddlText) -> {
            if (Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem() instanceof CustomSqlTab currentSqlTab) {
                int currentPos=currentSqlTab.sqlTabController.sqlEditCodeArea.getCaretPosition();
                currentSqlTab.sqlTabController.sqlEditCodeArea.insertText(currentPos, ddlText);
            }else{
                Main.mainController.newSqlFileMenuItem.fire();
                if (Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem() instanceof CustomSqlTab currentSqlTab) {
                    currentSqlTab.sqlTabController.sqlEditCodeArea.replaceText(ddlText);
                }
            }
        }));

        //右键删除连接点击响应
        deleteItem.setOnAction(event -> {
            List<TreeItem<TreeData>> selectedItems = new ArrayList<>(treeView.getSelectionModel().getSelectedItems());
            if (isMultiTableSelection(selectedItems)) {
                boolean confirmBatch = AlterUtil.CustomAlertConfirm(
                        I18n.t("backsql.confirm.delete_table.title", "删除表"),
                        I18n.t("backsql.confirm.delete_table.batch_content", "确定要删除选中的%d个表吗？")
                                .formatted(selectedItems.size())
                );
                if (!confirmBatch) {
                    return;
                }
                Connect connect = buildObjectConnect(selectedItems.get(0), false);
                List<String> sqlList = new ArrayList<>();
                for (TreeItem<TreeData> item : selectedItems) {
                    sqlList.add("drop table " + item.getValue().getName());
                }
                tableService.executeObjectSqls(connect, sqlList, () -> {
                    for (TreeItem<TreeData> item : selectedItems) {
                        TreeItem<TreeData> parent = item.getParent();
                        if (parent != null) {
                            parent.getChildren().remove(item);
                        }
                    }
                    NotificationUtil.showNotification(
                            Main.mainController.noticePane,
                            I18n.t("backsql.notice.batch_table_delete_submitted", "%d个表已删除！")
                                    .formatted(selectedItems.size())
                    );
                });
                return;
            }
            if (isMultiDeleteOnlySelection(selectedItems)) {
                TreeItem<TreeData> firstItem = selectedItems.get(0);
                String objectType = getDeleteObjectType(firstItem.getValue());
                String objectDisplayName = getDeleteObjectDisplayName(objectType);
                boolean confirmBatch = AlterUtil.CustomAlertConfirm(
                        I18n.t(getDeleteConfirmTitleKey(objectType), "删除对象"),
                        I18n.t("metadata.alert.delete_object.batch_content", "确定要删除选中的%d个%s吗？")
                                .formatted(selectedItems.size(), objectDisplayName)
                );
                if (!confirmBatch) {
                    return;
                }
                MetaObjectImpl service = getDeleteService(firstItem.getValue());
                if (service == null) {
                    return;
                }
                Connect connect = buildObjectConnect(firstItem, false);
                List<String> sqlList = new ArrayList<>();
                for (TreeItem<TreeData> item : selectedItems) {
                    sqlList.add("drop " + objectType + " " + item.getValue().getName());
                }
                service.executeObjectSqls(connect, sqlList, () -> {
                    for (TreeItem<TreeData> item : selectedItems) {
                        TreeItem<TreeData> parent = item.getParent();
                        if (parent != null) {
                            parent.getChildren().remove(item);
                        }
                    }
                    NotificationUtil.showNotification(
                            Main.mainController.noticePane,
                            I18n.t("metadata.notice.delete_object_batch_done", "已删除%d个%s！")
                                    .formatted(selectedItems.size(), objectDisplayName)
                    );
                });
                return;
            }
            MetadataTreeviewUtil.deleteTreeItem(treeView);
        });

        //右键移动连接点击响应
        moveItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(I18n.t("metadata.dialog.move_connection.title", "移动数据库连接"));
            alert.setHeaderText("");
            alert.setGraphic(null); //避免显示问号
            //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
            Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
            alterstage.getIcons().add(new Image(IconPaths.MAIN_LOGO));
            HBox hbox = new HBox();
            hbox.getChildren().add(new Label(I18n.t("metadata.dialog.move_connection.target", "请选择移动到  ")));
            hbox.setAlignment(Pos.CENTER_LEFT);
            ChoiceBox choiceBox = new ChoiceBox();
            List<TreeData> list = new ArrayList<>();
            for (TreeItem<TreeData> treeItem : treeView.getRoot().getChildren()) {
                if ( !treeItem.getValue().getName().equals(selectedItem.getParent().getValue().getName())) {
                    list.add(treeItem.getValue());
                }
            }
            choiceBox.setItems(FXCollections.observableArrayList(list));
            choiceBox.getSelectionModel().select(0);
            hbox.getChildren().add(choiceBox);
            alert.getDialogPane().setContent(hbox);

            // 自定义按钮
            ButtonType buttonTypeOk = new ButtonType(I18n.t("common.confirm", "确认"), ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType(I18n.t("common.cancel", "取消"), ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
            Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
            choiceBox.requestFocus();
            choiceBox.setPrefWidth(150);
            Connect connect = (Connect) selectedItem.getValue();
            connect.setParentId(((ConnectFolder) choiceBox.getSelectionModel().getSelectedItem()).getId());
            choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                connect.setParentId(((ConnectFolder) newValue).getId());
            });

            ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
            if (result == buttonTypeOk) {
                selectedItem.setValue(connect);
                SqliteDBaccessUtil.updateConnect(connect);
                selectedItem.getParent().getChildren().remove(selectedItem);
                MetadataTreeviewUtil.treeViewMoveConnectItem(treeView,selectedItem);
            }
        });


        //右键展开层级点击响应
        expandFolderItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            ConnectFolder connectFolder =(ConnectFolder) selectedItem.getValue();
            connectFolder.setExpand(1);
            SqliteDBaccessUtil.updateConnectFolder(connectFolder);
            selectedItem.setExpanded(true);
        });

        //右键折叠点击响应
        foldFolderItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            ConnectFolder connectFolder =(ConnectFolder) selectedItem.getValue();
            connectFolder.setExpand(0);
            SqliteDBaccessUtil.updateConnectFolder(connectFolder);
            selectedItem.setExpanded(false);
        });


        //右键新建数据库点击响应
        createDatabaseItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(I18n.t("metadata.dialog.create_database.title", "新建数据库"));
            alert.setHeaderText("");
            alert.setGraphic(null); //避免显示问号
            //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
            Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
            alterstage.getIcons().add(new Image(IconPaths.MAIN_LOGO));
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10));

            Label nameLabel = new Label(I18n.t("metadata.dialog.create_database.name", "数据库名称 "));
            Label charsetLabel = new Label(I18n.t("metadata.dialog.create_database.charset", "选择字符集 "));
            Label dbspaceLabel = new Label(I18n.t("metadata.dialog.create_database.dbspace", "选存储空间 "));

            nameLabel.setMinWidth(80);
            charsetLabel.setMinWidth(80);
            dbspaceLabel.setMinWidth(80);

            CustomUserTextField textField = new CustomUserTextField();
            // 定义过滤器，只允许 ASCII 字符输入（禁止中文）
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("[\\x00-\\x7F]*")) {
                    return change;  // 如果输入是 ASCII 字符（英文、数字等），则允许修改
                } else {
                    return null;  // 禁止输入中文字符
                }
            };
            // 将过滤器应用到 TextField
            TextFormatter<String> textFormatter = new TextFormatter<>(filter);
            textField.setTextFormatter(textFormatter);
            textField.setTooltip(new Tooltip(I18n.t("metadata.dialog.create_database.name_rule", "不可使用中文或空格或数字开头")));
            textField.setPrefWidth(240);
            ChoiceBox<String> comboBox = new ChoiceBox<>();
            comboBox.getItems().addAll(
                    I18n.t("metadata.dialog.create_database.charset.utf8", "ZH_CN.UTF8(推荐)"),
                    I18n.t("metadata.dialog.create_database.charset.gb18030", "ZH_CN.GB18030-2000(兼容GBK)"),
                    I18n.t("metadata.dialog.create_database.charset.en", "EN_US.819(ISO8859-1)")
            );
            comboBox.setValue(I18n.t("metadata.dialog.create_database.charset.utf8", "ZH_CN.UTF8(推荐)"));
            comboBox.setId("createDatabaseCharset");
            comboBox.setPrefWidth(240);

            ChoiceBox<String> comboBox1 = new ChoiceBox<>();
            comboBox1.setId("createDatabaseDbspace");
            comboBox1.setPrefWidth(240);

            ObservableList<String> list = null;
            try {
                if(selectedItem==null){
                    log.info("selectitem is null");
                }
                else {
                    log.info("selectitem is "+selectedItem.getValue().getName());
                }
                list = FXCollections.observableArrayList(databaseService.getDBspaceForCreateDatabase(((Connect) selectedItem.getParent().getValue()).getConn()));
            }catch (SQLException e){
                GlobalErrorHandlerUtil.handle(e);
            }
            catch (Exception e) {
                GlobalErrorHandlerUtil.handle(e);
            }
            comboBox1.setItems(list);
            comboBox1.setValue(list.get(0));

            grid.add(nameLabel, 0, 0);
            grid.add(textField, 1, 0);
            grid.add(charsetLabel, 0, 1);
            grid.add(comboBox, 1, 1);
            grid.add(dbspaceLabel, 0, 2);
            grid.add(comboBox1, 1, 2);
            alert.getDialogPane().setContent(grid);

            // 自定义按钮
            ButtonType buttonTypeOk = new ButtonType(I18n.t("common.confirm", "确认"), ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType(I18n.t("common.cancel", "取消"), ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
            Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
            button.setDisable(true);
            textField.requestFocus();
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                textField.setText(newValue.replace(" ", ""));
                if (textField.getText().isEmpty()){
                    button.setDisable(true);
                } else {
                    button.setDisable(false);
                }
            });

            ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
            if (result == buttonTypeOk) {
                Connect connect = new Connect((Connect) selectedItem.getParent().getValue());
                String dbLocale = ((String) comboBox.getValue()).replaceAll("\\([^()]*\\)", "");
                connect.setDatabase("sysmaster");
                connect.setProps(connectionService.modifyProps(connect, dbLocale));
                String sql = "create database " + textField.getText() + " in "
                        + ((String) comboBox1.getValue()).replaceAll("\\([^()]*\\)", "")
                        + " with log";
                databaseService.executeObjectSql(connect, sql, () -> {
                    NotificationUtil.showNotification(
                            Main.mainController.noticePane,
                            I18n.t("backsql.notice.database_created", "数据库[%s]创建成功").formatted(textField.getText())
                    );
                    selectedItem.getChildren().clear();
                    selectedItem.setExpanded(false);
                    selectedItem.setExpanded(true);
                });

            }
        });
        refreshItem.setOnAction(event->{
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if(selectedItem.isLeaf()){
                if(selectedItem.getValue() instanceof Table||selectedItem.getValue() instanceof SysTable){
                    selectedItem.getValue().setRunning(true);
                    tableService.refreshTableMeta(
                            MetadataTreeviewUtil.getMetaConnect(selectedItem),
                            MetadataTreeviewUtil.getCurrentDatabase(selectedItem),
                            selectedItem.getValue().getName(),
                            selectedItem::setValue,
                            () -> selectedItem.getValue().setRunning(false)
                    );
                }
                else if(selectedItem.getValue() instanceof Index){
                    selectedItem.getValue().setRunning(true);
                    indexService.refreshIndexMeta(
                            MetadataTreeviewUtil.getMetaConnect(selectedItem),
                            MetadataTreeviewUtil.getCurrentDatabase(selectedItem),
                            selectedItem.getValue().getName(),
                            selectedItem::setValue,
                            () -> selectedItem.getValue().setRunning(false)
                    );
                }
                else if(selectedItem.getValue() instanceof Trigger){
                    selectedItem.getValue().setRunning(true);
                    triggerService.refreshTriggerMeta(
                            MetadataTreeviewUtil.getMetaConnect(selectedItem),
                            MetadataTreeviewUtil.getCurrentDatabase(selectedItem),
                            selectedItem.getValue().getName(),
                            selectedItem::setValue,
                            () -> selectedItem.getValue().setRunning(false)
                    );
                }

            }else{
                selectedItem.getChildren().clear();
                selectedItem.setExpanded(false);
                selectedItem.setExpanded(true);
            }
        });

        createTableItem.setOnAction(event -> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TabpaneUtil.addCustomCreateTableTab(selectedItem);
        });
        //设置默认数据库
        setDefaultDatabaseItem.setOnAction(event-> {
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            ConnectionService.ChangeDefaultDatabaseResult result =
                    metadataService.changeDefaultDatabase(MetadataTreeviewUtil.getMetaConnect(selectedItem),
                            MetadataTreeviewUtil.getCurrentDatabase(selectedItem));
            if (result.isDisconnected()) {
                MetadataTreeviewUtil.connectionDisconnected();
            } else if (result.getErrorCode() != null) {
                AlterUtil.CustomAlert(I18n.t("common.error", "错误"), "[" + result.getErrorCode() + "]" + result.getErrorMessage());
            }
            treeView.refresh();
        });



        //自定义treecell
        treeView.setCellFactory(param -> new CustomTreeCell());

        //右键内容及处理逻辑
        treeView.setOnContextMenuRequested(event -> {
            ObservableList<TreeItem<TreeData>> selectedItems = treeView.getSelectionModel().getSelectedItems();
            if (selectedItems == null || selectedItems.isEmpty()) {
                treeview_menu.hide();
                return;
            }
            if (selectedItems.size() > 1) {
                TreeItem<TreeData> firstSelected = selectedItems.get(0);
                TreeItem<TreeData> anchorParent = firstSelected == null ? null : firstSelected.getParent();
                Class<?> anchorType = firstSelected == null || firstSelected.getValue() == null
                        ? null
                        : firstSelected.getValue().getClass();
                if (anchorType == Database.class || anchorType == ObjectFolder.class) {
                    treeview_menu.hide();
                    return;
                }
                boolean allDatabaseObjects = true;
                for (TreeItem<TreeData> item : selectedItems) {
                    if (item == null
                            || item.getValue() == null
                            || !isDatabaseMenuObject(item.getValue())
                            || item.getParent() != anchorParent
                            || anchorType == null
                            || item.getValue().getClass() != anchorType) {
                        allDatabaseObjects = false;
                        break;
                    }
                }
                if (!allDatabaseObjects) {
                    treeview_menu.hide();
                    return;
                }
            }
            TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                treeview_menu.getItems().clear();
                //设置初始值
                expandFolderItem.setDisable(false);
                foldFolderItem.setDisable(false);
                disconnectItem.setDisable(false);
                reconnectItem.setDisable(false);
                connectItem.setDisable(false);
                moveItem.setDisable(false);
                modifyconnectItem.setDisable(false);
                setDefaultDatabaseItem.setDisable(false);
                connectFolderInfoItem.setDisable(false);
                createDatabaseItem.setDisable(false);
                deleteItem.setDisable(false);
                renameItem.setDisable(false);
                truncateItem.setDisable(false);
                disableItem.setDisable(false);
                enableItem.setDisable(false);
                updateStatisticsItem.setDisable(false);
                sqlHisItem.setDisable(false);
                modifyToRawItem.setDisable(false);
                modifyToStandardItem.setDisable(false);
                createTableItem.setDisable(false);

                if (isMultiTableSelection(selectedItems)) {
                    boolean disableByReadOnlyOrSystem = isReadOnlyConnectionSelection(selectedItems);
                    boolean disableTruncateByExternal = false;
                    for (TreeItem<TreeData> item : selectedItems) {
                        if (isReadOnlyObject(item) || isSystemDatabaseObject(item)) {
                            disableByReadOnlyOrSystem = true;
                        }
                        if ("external".equals(((Table) item.getValue()).getTableTypeCode())) {
                            disableTruncateByExternal = true;
                        }
                    }
                    updateStatisticsItem.setDisable(disableByReadOnlyOrSystem);
                    truncateItem.setDisable(disableByReadOnlyOrSystem || disableTruncateByExternal);
                    deleteItem.setDisable(disableByReadOnlyOrSystem);
                    treeview_menu.getItems().add(updateStatisticsItem);
                    treeview_menu.getItems().add(truncateItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                    treeview_menu.show(treeView, event.getScreenX(), event.getScreenY());
                    return;
                }
                if (isMultiDeleteOnlySelection(selectedItems)) {
                    boolean disableDelete = isReadOnlyConnectionSelection(selectedItems);
                    for (TreeItem<TreeData> item : selectedItems) {
                        if (!canDeleteItem(item)) {
                            disableDelete = true;
                            break;
                        }
                    }
                    deleteItem.setDisable(disableDelete);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                    treeview_menu.show(treeView, event.getScreenX(), event.getScreenY());
                    return;
                }

                //如果是只读连接，禁用右键变更
                if(!(selectedItem.getValue() instanceof ConnectFolder)&&!(selectedItem.getValue() instanceof Connect)) {
                    Connect connectcheck = MetadataTreeviewUtil.getMetaConnect(selectedItem);
                    if (connectcheck.getReadonly() != null && connectcheck.getReadonly()) {
                        deleteItem.setDisable(true);
                        renameItem.setDisable(true);
                        truncateItem.setDisable(true);
                        disableItem.setDisable(true);
                        enableItem.setDisable(true);
                        updateStatisticsItem.setDisable(true);
                        modifyToRawItem.setDisable(true);
                        modifyToStandardItem.setDisable(true);
                        createDatabaseItem.setDisable(true);
                        createTableItem.setDisable(true);
                    }
                }
                if(selectedItem.getValue() instanceof Connect&&((Connect) selectedItem.getValue()).getReadonly()){
                    sqlHisItem.setDisable(true);
                }

                //如果是系统库，禁用变更操作
                if(selectedItem.getValue() instanceof Database||
                        selectedItem.getValue() instanceof ObjectFolder||
                        selectedItem.getValue() instanceof SysTable||
                        selectedItem.getValue() instanceof Table||
                        selectedItem.getValue() instanceof View||
                        selectedItem.getValue() instanceof Index||
                        selectedItem.getValue() instanceof Sequence||
                        selectedItem.getValue() instanceof Synonym||
                        selectedItem.getValue() instanceof Trigger||
                        selectedItem.getValue() instanceof Function||
                        selectedItem.getValue() instanceof Procedure||
                        selectedItem.getValue() instanceof DBPackage
                ) {
                    String database = MetadataTreeviewUtil.getCurrentDatabase(selectedItem).getName();
                    if (database.equals("sysmaster") || database.equals("sysuser") || database.equals("sysadmin") || database.equals("sysutils") || database.equals("sysha") || database.equals("syscdr") || database.equals("syscdcv1") || database.equals("gbasedbt") || database.equals("sys")) {
                        truncateItem.setDisable(true);
                        deleteItem.setDisable(true);
                        renameItem.setDisable(true);
                        enableItem.setDisable(true);
                        disableItem.setDisable(true);
                        modifyToRawItem.setDisable(true);
                        modifyToStandardItem.setDisable(true);
                    }
                }

                //连接分类
                if(selectedItem.getValue() instanceof ConnectFolder){
                    treeview_menu.getItems().add(connectFolderInfoItem);
                    if(selectedItem.getChildren().size()==0){
                        connectFolderInfoItem.setDisable(true);
                    }
                    treeview_menu.getItems().add(createConnectItem);
                    //treeview_menu.getItems().add(addSystemLevel);
                    treeview_menu.getItems().add(disconnectFolder);
                    if (selectedItem.getParent().getChildren().size() <= 1) {
                        deleteItem.setDisable(true);
                    }
                    treeview_menu.getItems().add(expandFolderItem);
                    treeview_menu.getItems().add(foldFolderItem);
                    //根据节点状态设置右键展开和折叠是否disable
                    if (((ConnectFolder)selectedItem.getValue()).getExpand() == 1) {
                        expandFolderItem.setDisable(true);
                        foldFolderItem.setDisable(false);
                    } else {
                        expandFolderItem.setDisable(false);
                        foldFolderItem.setDisable(true);
                    }
                    treeview_menu.getItems().add(renameItem);
                    treeview_menu.getItems().add(deleteItem);
                }
                //连接
                else if(selectedItem.getValue() instanceof Connect){
                    Connect connect =(Connect)selectedItem.getValue();
                    if(!connect.getUsername().equals("gbasedbt")){
                        healthCheckItem.setDisable(true);
                        onlinelogItem.setDisable(true);
                        spaceManagerItem.setDisable(true);
                        onconfigItem.setDisable(true);
                        instanceStopItem.setDisable(true);
                    }else{
                        healthCheckItem.setDisable(false);
                        onlinelogItem.setDisable(false);
                        spaceManagerItem.setDisable(false);
                        onconfigItem.setDisable(false);
                        instanceStopItem.setDisable(false);
                    }
                    //treeview_menu.getItems().add(createConnectItem);
                    treeview_menu.getItems().add(sqlHisItem);
                    treeview_menu.getItems().add(separator1);
                    //treeview_menu.getItems().add(connectOpenFileItem);
                    treeview_menu.getItems().add(connectItem);
                    treeview_menu.getItems().add(reconnectItem);
                    treeview_menu.getItems().add(disconnectItem);
                    treeview_menu.getItems().add(copyconnectItem);
                    treeview_menu.getItems().add(moveItem);
                    treeview_menu.getItems().add(modifyconnectItem);
                    //treeview_menu.getItems().add(refreshItem);
                    treeview_menu.getItems().add(renameItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(separator2);
                    treeview_menu.getItems().add(connectInfoItem);
                    treeview_menu.getItems().add(healthCheckItem);
                    treeview_menu.getItems().add(onlinelogItem);
                    treeview_menu.getItems().add(spaceManagerItem);
                    treeview_menu.getItems().add(onconfigItem);
                    treeview_menu.getItems().add(instanceStopItem);



                    try {
                        if (!(connect.getConn()==null || connect.getConn().isClosed())) {
                            modifyconnectItem.setDisable(true);
                            renameItem.setDisable(true);
                            deleteItem.setDisable(true);
                            connectItem.setDisable(true);
                        }else{
                            reconnectItem.setDisable(true);
                            disconnectItem.setDisable(true);
                        }
                    } catch (SQLException e) {
                        GlobalErrorHandlerUtil.handle(e);

                        throw new RuntimeException(e);
                    }

                    if (treeView.getRoot().getChildren().size() <= 1) {
                        moveItem.setDisable(true);
                    }
                }
                //数据库对象文件夹
                else if(selectedItem.getValue() instanceof DatabaseFolder){
                    treeview_menu.getItems().add(createDatabaseItem);
                    treeview_menu.getItems().add(refreshItem);
                }
                else if(selectedItem.getValue() instanceof UserFolder) {
                    treeview_menu.getItems().add(addUserItem);
                    treeview_menu.getItems().add(refreshItem);
                }
                else if(selectedItem.getValue() instanceof User) {
                    treeview_menu.getItems().add(modifyUserItem);
                    treeview_menu.getItems().add(deleteItem);
                }
                //数据库
                else if(selectedItem.getValue() instanceof Database) {
                    treeview_menu.getItems().add(databaseOpenFileItem);
                    treeview_menu.getItems().add(setDefaultDatabaseItem);
                    treeview_menu.getItems().add(updateStatisticsItem);
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(refreshItem);
                    treeview_menu.getItems().add(renameItem);
                    treeview_menu.getItems().add(deleteItem);
                }
                //对象文件夹
                else if(selectedItem.getValue() instanceof ObjectFolder) {
                    ObjectFolderKind objectFolderKind = getObjectFolderKind(selectedItem);
                    if (objectFolderKind == ObjectFolderKind.TABLES){
                        treeview_menu.getItems().add(createTableItem);
                        treeview_menu.getItems().add(updateStatisticsItem);

                    }else if(objectFolderKind == ObjectFolderKind.PROCEDURES) {
                        treeview_menu.getItems().add(updateStatisticsItem);
                    }
                    treeview_menu.getItems().add(refreshItem);

                }
                //系统表
                else if(selectedItem.getValue() instanceof SysTable) {
                    if(!((SysTable)selectedItem.getValue()).getTableTypeCode().equals("view")){
                        treeview_menu.getItems().add(updateStatisticsItem);
                        treeview_menu.getItems().add(copyItem);
                        treeview_menu.getItems().add(refreshItem);
                    }
                }
                //表
                else if(selectedItem.getValue() instanceof Table) {
                    if(!((Table)selectedItem.getValue()).getTableTypeCode().equals("external")){
                        treeview_menu.getItems().add(updateStatisticsItem);
                        treeview_menu.getItems().add(modifyToRawItem);
                        treeview_menu.getItems().add(modifyToStandardItem);
                        treeview_menu.getItems().add(truncateItem);
                    }
                    if(((Table)selectedItem.getValue()).getTableTypeCode().equals("raw")){
                        modifyToRawItem.setDisable(true);
                    }else{
                        modifyToStandardItem.setDisable(true);
                    }
                    treeview_menu.getItems().add(copyItem);
                    exportMenu.getItems().setAll(exportCsvItem, exportJsonItem, exportSqlItem);
                    exportCsvItem.setOnAction(ev -> exportTableData(selectedItem, ExportFormat.CSV));
                    exportJsonItem.setOnAction(ev -> exportTableData(selectedItem, ExportFormat.JSON));
                    exportSqlItem.setOnAction(ev -> exportTableData(selectedItem, ExportFormat.SQL));
                    treeview_menu.getItems().add(refreshItem);
                    treeview_menu.getItems().add(renameItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(exportMenu);
                    treeview_menu.getItems().add(ddlMenu);

                }
                //视图
                else if(selectedItem.getValue() instanceof View) {
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                }
                //索引
                else if(selectedItem.getValue() instanceof Index) {
                    treeview_menu.getItems().add(enableItem);
                    treeview_menu.getItems().add(disableItem);
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(refreshItem);
                    treeview_menu.getItems().add(renameItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                    if(((Index)selectedItem.getValue()).getIsdisabled()) {
                        disableItem.setDisable(true);
                    }else{
                        enableItem.setDisable(true);
                    }
                    if(selectedItem.getValue().getName().charAt(0)==' '){
                        enableItem.setDisable(true);
                        disableItem.setDisable(true);
                        renameItem.setDisable(true);
                        deleteItem.setDisable(true);
                    }
                }
                //序列
                else if(selectedItem.getValue() instanceof Sequence) {
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(renameItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                }
                //同义词
                else if(selectedItem.getValue() instanceof Synonym) {
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                }
                //触发器
                else if(selectedItem.getValue() instanceof Trigger) {
                    treeview_menu.getItems().add(enableItem);
                    treeview_menu.getItems().add(disableItem);
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(refreshItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                    if(((Trigger)selectedItem.getValue()).isIsdisabled()) {
                        disableItem.setDisable(true);
                    }else{
                        enableItem.setDisable(true);
                    }
                }
                //函数
                else if(selectedItem.getValue() instanceof Function) {
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                }
                //存储过程
                else if(selectedItem.getValue() instanceof Procedure) {
                    treeview_menu.getItems().add(updateStatisticsItem);
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                }
                //包
                else if(selectedItem.getValue() instanceof DBPackage) {
                    //treeview_menu.getItems().add(packageDDLItem);
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(refreshItem);
                    treeview_menu.getItems().add(deleteItem);
                    treeview_menu.getItems().add(ddlMenu);
                }
                else if(selectedItem.getValue() instanceof PackageFunction||selectedItem.getValue() instanceof PackageProcedure) {
                    treeview_menu.getItems().add(copyItem);
                    treeview_menu.getItems().add(ddlMenu);

                }

                // 树中右键框显示
                treeview_menu.show(treeView, event.getScreenX(), event.getScreenY());
            }
        });


    }

    //创建一个TreeItem,重构ifLeaf显示箭头
    public static TreeItem<TreeData> createTreeItem(TreeData treeData) {
        TreeItem<TreeData> treeItem = new TreeItem<>(treeData){
            @Override
            public boolean isLeaf() {
                return false;
            }
        };
        if(!(treeData instanceof ConnectFolder)){
            treeItem.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                if (isNowExpanded&& treeItem.getChildren().isEmpty()){
                    Main.mainController.databaseMetaTreeView.getSelectionModel().clearSelection();;
                    Main.mainController.databaseMetaTreeView.getSelectionModel().select(treeItem);//避免只点击箭头但连接中断，没有选中的节点不报连接中断错误
                    treeItemAddChildrens(treeItem);
                };
            });
        }
        return treeItem;
    }


    //创建一个TreeItem,重构ifLeaf显示箭头
    public static TreeItem<TreeData> createLeafTreeItem(TreeData treeData) {
        TreeItem<TreeData> treeItem = new TreeItem<>(treeData){
            @Override
            public boolean isLeaf() {
                return true;
            }
        };
        return treeItem;

    }

    //创建连接分类文件夹
    public static void createConnectFolder(TreeView<TreeData> treeView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.t("metadata.dialog.create_folder.title", "新建连接分类"));
        alert.setHeaderText("");
        alert.setGraphic(null);
        //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image(IconPaths.MAIN_LOGO));
        HBox hbox = new HBox();
        hbox.getChildren().add(new Label(I18n.t("metadata.dialog.create_folder.name", "请输入连接分类名称  ")));
        hbox.setAlignment(Pos.CENTER_LEFT);
        TextField textField = new TextField();
        textField.setPrefWidth(200);
        hbox.getChildren().add(textField);
        alert.getDialogPane().setContent(hbox);

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType(I18n.t("common.confirm", "确认"), ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType(I18n.t("common.cancel", "取消"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
        button.setDisable(true);
        textField.requestFocus();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replace(" ", ""));
            if (!textField.getText().isEmpty()) {
                Boolean exists = false;
                for (TreeItem<TreeData> treeItem : treeView.getRoot().getChildren()) {
                    if (treeItem.getValue().getName().equals(textField.getText())) {
                        exists = true;
                    }
                }
                if (exists) {
                    button.setDisable(true);
                } else {
                    button.setDisable(false);
                }
            } else {
                button.setDisable(true);
            }
        });

        ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
        if (result == buttonTypeOk) {
            ConnectFolder connectFolder = new ConnectFolder();
            connectFolder.setName(textField.getText());
            connectFolder.setExpand(1);
            SqliteDBaccessUtil.createConnectFolder(connectFolder);
            TreeItem treeItem= MetadataTreeviewUtil.createTreeItem(connectFolder);
            //ConnectTreeViewUtil.treeViewAddItem(databaseobjects_treeview,treeItem);
            treeView.getRoot().getChildren().add(treeItem);
            reorderTreeview(treeView,treeItem);
        }
    }

    //创建一个连接节点
    public static void createConnectLeaf(TreeView<TreeData> treeView,TreeItem<TreeData> treeItem) {
        for (TreeItem<TreeData> folderTreeItem : treeView.getRoot().getChildren()) {
            if (((ConnectFolder)folderTreeItem.getValue()).getId() == ((Connect)treeItem.getValue()).getParentId()) {
                folderTreeItem.getChildren().add(treeItem);
                if(!folderTreeItem.isExpanded()){
                    folderTreeItem.setExpanded(true);
                }
                reorderTreeview(treeView,treeItem);
                break;

            }
        }
    }
    //重命名节点
    public static void renameTreeItem(TreeView<TreeData> treeView) {
        TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String title = buildRenameTitle(selectedItem.getValue());
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setGraphic(null); //避免显示问号
        //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image(IconPaths.MAIN_LOGO));
        HBox hbox = new HBox();
        hbox.getChildren().add(new Label(I18n.t("metadata.dialog.rename.input", "请输入重命名名称  ")));
        hbox.setAlignment(Pos.CENTER_LEFT);
        CustomUserTextField textField = new CustomUserTextField();
        textField.setPrefWidth(200);
        textField.setText(selectedItem.getValue().getName());
        textField.positionCaret(textField.getText().length());
        hbox.getChildren().add(textField);
        alert.getDialogPane().setContent(hbox);

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType(I18n.t("common.confirm", "确认"), ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType(I18n.t("common.cancel", "取消"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
        button.setDisable(true);
        textField.requestFocus();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replace(" ", ""));
            if (!textField.getText().isEmpty() && !textField.getText().equals(selectedItem.getValue().getName())) {
                Boolean exists = false;

                //如果是数据库连接，需要判断所有分类里是否重复
                if(selectedItem.getValue() instanceof Connect){
                    for (TreeItem<TreeData> treeItem : treeView.getRoot().getChildren()){
                        for (TreeItem<TreeData> treeItem1 : treeItem.getChildren()){
                            if (treeItem1.getValue().getName().equals(textField.getText())) {
                                exists = true;
                            }
                        }
                    }
                }else{
                    for (TreeItem<TreeData> treeItem : selectedItem.getParent().getChildren()) {
                        if (treeItem.getValue().getName().equals(textField.getText())) {
                            exists = true;
                        }
                    }
                }
                if (exists) {
                    button.setDisable(true);
                } else {
                    button.setDisable(false);
                }
            } else {
                button.setDisable(true);
            }
        });

        ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
        if (result == buttonTypeOk) {
            String newName = textField.getText();
            TreeData treeData = selectedItem.getValue();
            if (treeData instanceof ConnectFolder) {
                treeData.setName(newName);
                if (selectedItem.getParent().getChildren().size() > 1) { //多于1个分类，重新排序
                    reorderTreeview(treeView, selectedItem);
                }
                SqliteDBaccessUtil.updateConnectFolder((ConnectFolder) treeData);
                NotificationUtil.showNotification(Main.mainController.noticePane,
                        I18n.t("metadata.notice.folder_renamed", "分类已重命名为：%s").formatted(selectedItem.getValue().getName()));
            }else if(treeData instanceof Connect){
                selectedItem.getValue().setName(newName);
                //connect_list_treeview.refresh();
                if(selectedItem.getParent().getChildren().size()>1) {//多于1个连接重新排序
                    MetadataTreeviewUtil.reorderTreeview(treeView, selectedItem);
                }
                SqliteDBaccessUtil.updateConnect((Connect) selectedItem.getValue());
                TabpaneUtil.isRefreshConnectList();
                NotificationUtil.showNotification(Main.mainController.noticePane,
                        I18n.t("metadata.notice.connection_renamed", "连接已重命名为：%s").formatted(selectedItem.getValue().getName()));
            }else if(treeData instanceof Database){
                renameDatabaseObject(databaseService, selectedItem, newName, "database",
                        true);
            }else if(treeData instanceof Table){
                renameDatabaseObject(tableService, selectedItem, newName, "table",
                        false);
            }else if(treeData instanceof Index){
                renameDatabaseObject(indexService, selectedItem, newName, "index",
                        false);
            }else if(treeData instanceof Sequence){
                renameDatabaseObject(sequenceService, selectedItem, newName, "sequence",
                        false);
            }else if(treeData instanceof View){
                //不支持重命名
                renameDatabaseObject(viewService, selectedItem, newName, "view",
                        false);
            }else if(treeData instanceof Synonym){
                //不支持重命名
                renameDatabaseObject(synonymService, selectedItem, newName, "synonym",
                        false);
            }else if(treeData instanceof Trigger){
                //不支持重命名
                renameDatabaseObject(triggerService, selectedItem, newName, "trigger",
                        false);
            }else if(treeData instanceof Function){
                //不支持重命名
                renameDatabaseObject(functionService, selectedItem, newName, "function",
                        false);
            }else if(treeData instanceof Procedure){
                //不支持重命名
                renameDatabaseObject(procedureService, selectedItem, newName, "procedure",
                        false);
            }
        }
    }

    private static String buildRenameTitle(TreeData treeData) {
        if (treeData instanceof ConnectFolder) {
            return I18n.t("metadata.dialog.rename.folder", "重命名连接分类：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Connect) {
            return I18n.t("metadata.dialog.rename.connection", "重命名数据库连接：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Database) {
            return I18n.t("metadata.dialog.rename.database", "重命名数据库：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Table) {
            return I18n.t("metadata.dialog.rename.table", "重命名表：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Index) {
            return I18n.t("metadata.dialog.rename.index", "重命名索引：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Sequence) {
            return I18n.t("metadata.dialog.rename.sequence", "重命名序列：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Trigger) {
            return I18n.t("metadata.dialog.rename.trigger", "重命名触发器：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Function) {
            return I18n.t("metadata.dialog.rename.function", "重命名函数：%s").formatted(treeData.getName());
        }
        if (treeData instanceof Procedure) {
            return I18n.t("metadata.dialog.rename.procedure", "重命名存储过程：%s").formatted(treeData.getName());
        }
        return I18n.t("metadata.dialog.rename.title", "重命名");
    }

    private static void renameDatabaseObject(MetaObjectImpl service,
                                             TreeItem<TreeData> selectedItem,
                                             String newName,
                                             String objectType,
                                             boolean useSysmaster) {
        String oldName = selectedItem.getValue().getName();
        String objectDisplayName = getDeleteObjectDisplayName(objectType);
        String sql = "rename " + objectType + " " + oldName + " to " + newName;
        Connect connect = buildObjectConnect(selectedItem, useSysmaster);
        service.renameObject(connect, sql, () -> {
            selectedItem.getValue().setName(newName);
            NotificationUtil.showNotification(
                    Main.mainController.noticePane,
                    I18n.t("backsql.notice.renamed", "%s\"%s\"已重命名为\"%s\"")
                            .formatted(objectDisplayName, oldName, newName)
            );
        });
    }

    private static void deleteDatabaseObject(MetaObjectImpl service,
                                             TreeItem<TreeData> selectedItem,
                                             String objectType,
                                             boolean useSysmaster) {
        String objectDisplayName = getDeleteObjectDisplayName(objectType);
        String confirmTitleKey = getDeleteConfirmTitleKey(objectType);
        String confirmContentKey = getDeleteConfirmContentKey(objectType);
        String objectName = selectedItem.getValue().getName();
        String confirmContent;
        if (confirmContentKey != null) {
            confirmContent = I18n.t(confirmContentKey, "确定要删除\"%s\"吗？")
                    .formatted(objectName);
        } else {
            confirmContent = I18n.t("metadata.alert.delete_object.content", "确定要删除%s\"%s\"吗？")
                    .formatted(objectDisplayName, objectName);
        }
        boolean confirm = AlterUtil.CustomAlertConfirm(
                I18n.t(confirmTitleKey, "删除对象"),
                confirmContent
        );
        if (!confirm) {
            return;
        }

        String sql = "drop " + objectType + " " + selectedItem.getValue().getName();
        Connect connect = buildObjectConnect(selectedItem, useSysmaster);
        service.deleteObject(connect, sql, () -> {
            TreeItem<TreeData> parent = selectedItem.getParent();
            if (parent != null) {
                parent.getChildren().remove(selectedItem);
            }
            NotificationUtil.showNotification(
                    Main.mainController.noticePane,
                    I18n.t("backsql.notice.deleted", "%s\"%s\"已删除！")
                            .formatted(objectDisplayName, selectedItem.getValue().getName())
            );
        });
    }

    public static Connect buildObjectConnect(TreeItem<TreeData> selectedItem, boolean useSysmaster) {
        Connect connect = new Connect(getMetaConnect(selectedItem));
        Database currentDatabase = getCurrentDatabase(selectedItem);
        connect.setDatabase(useSysmaster ? "sysmaster" : currentDatabase.getName());
        connect.setProps(connectionService.modifyProps(connect, currentDatabase.getDbLocale()));
        return connect;
    }

    private static void toggleObjectEnabled(TreeItem<TreeData> selectedItem, boolean enabled) {
        TreeData treeData = selectedItem.getValue();
        Connect connect = buildObjectConnect(selectedItem, false);
        if (treeData instanceof Index) {
            toggleIndexEnabled(connect, treeData, enabled);
            return;
        }
        if (treeData instanceof Trigger) {
            toggleTriggerEnabled(connect, treeData, enabled);
        }
    }

    private static void toggleIndexEnabled(Connect connect, TreeData treeData, boolean enabled) {
        String action = enabled ? "enable" : "disable";
        boolean confirm = AlterUtil.CustomAlertConfirm(
                I18n.t("metadata.alert." + action + "_index.title", enabled ? "启用索引" : "禁用索引"),
                I18n.t("metadata.alert." + action + "_index.content",
                                enabled ? "确定要启用索引\"%s\"吗？启用索引可能会较长时间锁表！" : "确定要禁用索引\"%s\"吗？索引禁用后启用需要自动重建耗费较长时间！")
                        .formatted(treeData.getName())
        );
        if (!confirm) {
            return;
        }
        String sql = "set indexes " + treeData.getName() + (enabled ? " enabled" : " disabled");
        Runnable onSucceeded = () -> {
            ((Index)treeData).setIsdisabled(!enabled);

            NotificationUtil.showNotification(
                Main.mainController.noticePane,
                I18n.t(
                        enabled ? "backsql.notice.index_enabled" : "backsql.notice.index_disabled",
                        enabled ? "索引\"%s\"已启用！" : "索引\"%s\"已禁用！"
                ).formatted(treeData.getName())
            );
        };
        if (enabled) {
            indexService.enableIndex(connect, sql, onSucceeded);
        } else {
            indexService.disableIndex(connect, sql, onSucceeded);
        }
    }

    private static void toggleTriggerEnabled(Connect connect, TreeData treeData, boolean enabled) {
        String action = enabled ? "enable" : "disable";
        boolean confirm = AlterUtil.CustomAlertConfirm(
                I18n.t("metadata.alert." + action + "_trigger.title", enabled ? "启用触发器" : "禁用触发器"),
                I18n.t(
                        "metadata.alert." + action + "_trigger.content",
                        enabled ? "确定要启用触发器\"%s\"吗？" : "确定要禁用触发器\"%s\"吗？"
                ).formatted(treeData.getName())
        );
        if (!confirm) {
            return;
        }
        String sql = "set triggers " + treeData.getName() + (enabled ? " enabled" : " disabled");
        Runnable onSucceeded = () -> {
            ((Trigger)treeData).setIsdisabled(!enabled);
            NotificationUtil.showNotification(
                Main.mainController.noticePane,
                I18n.t(
                        enabled ? "backsql.notice.trigger_enabled" : "backsql.notice.trigger_disabled",
                        enabled ? "触发器\"%s\"已启用！" : "触发器\"%s\"已禁用！"
                ).formatted(treeData.getName())
            );
        };
        if (enabled) {
            triggerService.enableTrigger(connect, sql, onSucceeded);
        } else {
            triggerService.disableTrigger(connect, sql, onSucceeded);
        }
    }

    private static String getDeleteObjectDisplayName(String objectType) {
        return switch (objectType == null ? "" : objectType.toLowerCase()) {
            case "database" -> I18n.t("backsql.object.database", "数据库");
            case "table" -> I18n.t("backsql.object.table", "表");
            case "view" -> I18n.t("backsql.object.view", "视图");
            case "index", "indexes" -> I18n.t("backsql.object.index", "索引");
            case "sequence" -> I18n.t("backsql.object.sequence", "序列");
            case "synonym" -> I18n.t("backsql.object.synonym", "同义词");
            case "trigger", "triggers" -> I18n.t("backsql.object.trigger", "触发器");
            case "function" -> I18n.t("backsql.object.function", "函数");
            case "procedure" -> I18n.t("backsql.object.procedure", "存储过程");
            case "package" -> I18n.t("backsql.object.package", "包");
            case "user" -> I18n.t("backsql.object.user", "用户");
            default -> I18n.t("backsql.object.default", "对象");
        };
    }

    private static String getDeleteConfirmTitleKey(String objectType) {
        return switch (objectType == null ? "" : objectType.toLowerCase()) {
            case "database" -> "backsql.confirm.delete_database.title";
            case "table" -> "backsql.confirm.delete_table.title";
            case "view" -> "backsql.confirm.delete_view.title";
            case "index", "indexes" -> "backsql.confirm.delete_index.title";
            case "sequence" -> "backsql.confirm.delete_sequence.title";
            case "synonym" -> "backsql.confirm.delete_synonym.title";
            case "trigger", "triggers" -> "backsql.confirm.delete_trigger.title";
            case "function" -> "backsql.confirm.delete_function.title";
            case "procedure" -> "backsql.confirm.delete_procedure.title";
            case "user" -> "backsql.confirm.delete_user.title";
            default -> "backsql.error.title";
        };
    }

    private static String getDeleteConfirmContentKey(String objectType) {
        return switch (objectType == null ? "" : objectType.toLowerCase()) {
            case "database" -> "backsql.confirm.delete_database.content";
            case "table" -> "backsql.confirm.delete_table.content";
            case "view" -> "backsql.confirm.delete_view.content";
            case "index", "indexes" -> "backsql.confirm.delete_index.content";
            case "sequence" -> "backsql.confirm.delete_sequence.content";
            case "synonym" -> "backsql.confirm.delete_synonym.content";
            case "trigger", "triggers" -> "backsql.confirm.delete_trigger.content";
            case "function" -> "backsql.confirm.delete_function.content";
            case "procedure" -> "backsql.confirm.delete_procedure.content";
            case "user" -> "backsql.confirm.delete_user.content";
            default -> null;
        };
    }


    //删除节点
    public static void deleteTreeItem(TreeView<TreeData> treeView) {
        TreeItem<TreeData> selectedItem = treeView.getSelectionModel().getSelectedItem();
        TreeData treeData = selectedItem.getValue();
        if(treeData instanceof  ConnectFolder){
            if (selectedItem.getParent().getChildren().size() <= 1) {
                AlterUtil.CustomAlert(I18n.t("metadata.alert.delete_folder.title", "删除连接分类"),
                        I18n.t("metadata.alert.delete_folder.single", "当前只有一个连接分类，不可删除！"));
            } else if (selectedItem.getChildren().size() > 0) {
                Boolean confirm = AlterUtil.CustomAlertConfirm(
                        I18n.t("metadata.alert.delete_folder.title", "删除连接分类"),
                        I18n.t("metadata.alert.delete_folder.content", "删除连接分类\"%s\"将删除该分类下【%d】个连接，确定要删除该分类吗？")
                                .formatted(selectedItem.getValue().getName(), selectedItem.getChildren().size())
                );
                if (confirm) {
                    disconnectFolder(selectedItem);
                    selectedItem.getParent().getChildren().remove(selectedItem);
                    SqliteDBaccessUtil.deleteConnectFolder((ConnectFolder) selectedItem.getValue());
                    NotificationUtil.showNotification(Main.mainController.noticePane,
                            I18n.t("metadata.notice.folder_deleted", "数据库连接分类\"%s\"已删除！").formatted(selectedItem.getValue().getName()));
                }
            } else {
                SqliteDBaccessUtil.deleteConnectFolder((ConnectFolder)selectedItem.getValue());
                selectedItem.getParent().getChildren().remove(selectedItem);
                NotificationUtil.showNotification(Main.mainController.noticePane,
                        I18n.t("metadata.notice.folder_deleted", "数据库连接分类\"%s\"已删除！").formatted(selectedItem.getValue().getName()));
            }

        }else if(treeData instanceof Connect){
            if (AlterUtil.CustomAlertConfirm(
                    I18n.t("metadata.alert.delete_connection.title", "删除连接"),
                    I18n.t("metadata.alert.delete_connection.content", "确定要删除连接\"%s\"吗？").formatted(selectedItem.getValue().getName()))) {
                Connect connect = (Connect) treeData;
                try {
                    if (!selectedItem.getChildren().isEmpty()) {
                        connect.getConn().close();
                        selectedItem.getChildren().clear();
                    }
                } catch (SQLException e) {
                    GlobalErrorHandlerUtil.handle(e);
                    throw new RuntimeException(e);
                }
                SqliteDBaccessUtil.deleteConnectLeaf(connect);
                selectedItem.getParent().getChildren().remove(selectedItem);
                TabpaneUtil.isRefreshConnectList();
                NotificationUtil.showNotification(Main.mainController.noticePane,
                        I18n.t("metadata.notice.connection_deleted", "数据库连接\"%s\"已删除！").formatted(selectedItem.getValue().getName()));
            }
        }else if(treeData instanceof Database){
            deleteDatabaseObject(databaseService, selectedItem, "database", true);
        }else if(treeData instanceof Table){
            deleteDatabaseObject(tableService, selectedItem, "table", false);
        }else if(treeData instanceof View){
            deleteDatabaseObject(viewService, selectedItem, "view", false);
        }else if(treeData instanceof Index){
            deleteDatabaseObject(indexService, selectedItem, "index", false);
        }else if(treeData instanceof Sequence){
            deleteDatabaseObject(sequenceService, selectedItem, "sequence", false);
        }else if(treeData instanceof Synonym){
            deleteDatabaseObject(synonymService, selectedItem, "synonym", false);
        }else if(treeData instanceof Trigger){
            deleteDatabaseObject(triggerService, selectedItem, "trigger", false);
        }else if(treeData instanceof Function){
            deleteDatabaseObject(functionService, selectedItem, "function", false);
        }else if(treeData instanceof Procedure){
            deleteDatabaseObject(procedureService, selectedItem, "procedure", false);
        }else if(treeData instanceof DBPackage){
            deleteDatabaseObject(packageService, selectedItem, "package", false);
        }else if(treeData instanceof User){
            deleteDatabaseObject(databaseService, selectedItem, "user", false);
        }
    }

    //连接创建后需要重新排序
    public static void reorderTreeview(TreeView<TreeData> treeView,TreeItem<TreeData> treeItem) {
        Comparator<TreeItem<TreeData>> treeItemComparator = (o1, o2) -> o1.getValue().getName().compareTo(o2.getValue().getName());
        treeItem.getParent().getChildren().sort(treeItemComparator);
        //排序后当前选择的元素到了最后一个，需重新设置当前选择项
        treeView.getSelectionModel().clearSelection();
        treeView.getSelectionModel().select(treeItem);
        treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
    }

    //增加子节点
    public static void  treeItemAddChildrens(TreeItem<TreeData> treeItem){
        if(treeItem.getValue() instanceof Connect){
            treeItem.getChildren().add(createLeafTreeItem(new Connecting("Connecting")));
        }else{
            treeItem.getChildren().add(createLeafTreeItem(new Loading("Loading")));
        }
        if(treeItem.getValue() instanceof Connect){
            Connect connect =(Connect)treeItem.getValue();
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                        try{
                              connect.setConn(metadataService.getConnection(connect));
                              //连接之后切换到gbase模式
                              metadataService.sessionChangeToGbaseMode(connect.getConn());


                            //TreeItem<TreeData> scanItem=createTreeItem(checkTreeData);
                            //TreeItem<TreeData> monItem=createTreeItem(monTreeData);

                            //查询到结果后删除loading节点
                            Platform.runLater(() -> {
                                DatabaseFolder databaseTreeData = new DatabaseFolder();
                                bindFolderName(databaseTreeData, "metadata.folder.databases", "数据库");
                                TreeItem<TreeData> databaseItem = createTreeItem(databaseTreeData);
                                UserFolder userTreeData = new UserFolder();
                                bindFolderName(userTreeData, "metadata.folder.users", "用户");
                                TreeItem<TreeData> userItem = createTreeItem(userTreeData);
                                treeItem.getChildren().clear();
                                //查询到的结果添加到数据库条目下
                                treeItem.getChildren().add(databaseItem);
                                if(connect.getUsername().equals("gbasedbt")) {
                                    treeItem.getChildren().add(userItem);
                                }
                                // treeItem.getChildren().add(scanItem);
                                //treeItem.getChildren().add(monItem);
                                //addExpandedPropertyListen(treeItem);
                            });
                        } catch (SQLException e) {
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                            GlobalErrorHandlerUtil.handle(e);
                        }
                        catch (Exception e) {
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                            GlobalErrorHandlerUtil.handle(e);
                        }
                    }));
        }else if(treeItem.getValue() instanceof DatabaseFolder){
            //创建子线程加载数据库
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                          final List<Database> databases = new ArrayList<>();
                          try {
                              databases.addAll(databaseService.getDatabases(getMetaConnect(treeItem).getConn(), false));
                          } catch (SQLException e) {
                              if (e.getErrorCode() == -201) {
                                  try {
                                      databases.clear();
                                      databases.addAll(databaseService.getDatabases(getMetaConnect(treeItem).getConn(), true));
                                  } catch (SQLException ex) {
                                      GlobalErrorHandlerUtil.handle(ex);
                                  }
                              } else {
                                  GlobalErrorHandlerUtil.handle(e);
                              }
                          }
                        //查询到结果后删除loading节点
                        if(databases.size()>0){
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                //查询到的结果添加到数据库条目下
                                for (Database database : databases) {
                                    TreeItem<TreeData> item = createTreeItem(database);
                                    treeItem.getChildren().add(item);
                                }
                            });
                        }else{
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                        }
                    }));
        }else if(treeItem.getValue() instanceof UserFolder){
            //创建子线程加载数据库
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                          final List<User> users = new ArrayList<>();
                          try {
                              users.addAll(userService.getUsers(getMetaConnect(treeItem).getConn()));
                          } catch (SQLException e) {
                              GlobalErrorHandlerUtil.handle(e);
                          }
                        //查询到结果后删除loading节点
                        if(users.size()>0){
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                //查询到的结果添加到数据库条目下
                                for (User user : users) {
                                    TreeItem<TreeData> item = createLeafTreeItem(user);
                                    treeItem.getChildren().add(item);
                                }
                            });
                        }else{
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                        }
                    }));
        }
        else if(treeItem.getValue() instanceof Database){
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                        ObjectList objectList;
                        try {
                            Database database = MetadataTreeviewUtil.getCurrentDatabase(treeItem);
                            objectList = databaseService.loadObjects(getMetaConnect(treeItem), database);
                        } catch (Exception e) {
                            GlobalErrorHandlerUtil.handle(e);
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                            return;
                        }

                        if(objectList.getSuccess()) {

                            if(objectList.getInfo()==null){
                                Platform.runLater(() -> {
                                    NotificationUtil.showNotification(Main.mainController.noticePane,
                                            I18n.t("metadata.notice.database_not_found", "未找到当前数据库，数据库已被删除！"));
                                    treeItem.getParent().getChildren().remove(treeItem);
                                });
                            }else {
                                //查询到结果后删除loading节点
                                Platform.runLater(() -> {
                                    treeItem.setValue((Database) objectList.getInfo());
                                    treeItem.getChildren().clear();
                                    //查询到的结果添加到数据库条目下
                                    ObjectFolder objectFolder = createObjectFolder(ObjectFolderKind.SYSTEM_TABLE_VIEW);
                                    objectFolder.setDescription(objectList.getItems().get(0).toString());
                                    TreeItem<TreeData> systableTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.TABLES);
                                    objectFolder.setDescription(objectList.getItems().get(1).toString());
                                    TreeItem<TreeData> tableTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.VIEWS);
                                    objectFolder.setDescription(objectList.getItems().get(2).toString());
                                    TreeItem<TreeData> viewTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.INDEXES);
                                    objectFolder.setDescription(objectList.getItems().get(3).toString());
                                    TreeItem<TreeData> indexTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.SEQUENCES);
                                    objectFolder.setDescription(objectList.getItems().get(4).toString());
                                    TreeItem<TreeData> sequenceTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.SYNONYMS);
                                    objectFolder.setDescription(objectList.getItems().get(5).toString());
                                    TreeItem<TreeData> synTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.TRIGGERS);
                                    objectFolder.setDescription(objectList.getItems().get(6).toString());
                                    TreeItem<TreeData> triggerTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.FUNCTIONS);
                                    objectFolder.setDescription(objectList.getItems().get(7).toString());
                                    TreeItem<TreeData> functionTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.PROCEDURES);
                                    objectFolder.setDescription(objectList.getItems().get(8).toString());
                                    TreeItem<TreeData> procedureTreeItem = createTreeItem(objectFolder);
                                    objectFolder = createObjectFolder(ObjectFolderKind.PACKAGES);
                                    objectFolder.setDescription(objectList.getItems().get(9).toString());
                                    TreeItem<TreeData> packageTreeItem = createTreeItem(objectFolder);
                                    treeItem.getChildren().add(systableTreeItem);
                                    treeItem.getChildren().add(tableTreeItem);
                                    treeItem.getChildren().add(viewTreeItem);
                                    treeItem.getChildren().add(indexTreeItem);
                                    treeItem.getChildren().add(sequenceTreeItem);
                                    treeItem.getChildren().add(synTreeItem);
                                    treeItem.getChildren().add(triggerTreeItem);
                                    treeItem.getChildren().add(functionTreeItem);
                                    treeItem.getChildren().add(procedureTreeItem);
                                    treeItem.getChildren().add(packageTreeItem);
                                    //addExpandedPropertyListen(treeItem);
                                });
                            }
                        }else{
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                        }
                    }));
        }else if (isObjectFolder(treeItem, ObjectFolderKind.SYSTEM_TABLE_VIEW)) {
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                        ObjectList objectList;
                        try {
                            objectList = tableService.loadSystemTables(getMetaConnect(treeItem), getCurrentDatabase(treeItem));
                        } catch (Exception e) {
                            GlobalErrorHandlerUtil.handle(e);
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                            return;
                        }
                        if(objectList.getInfo()!=null&&!objectList.getItems().isEmpty()) {
                            Platform.runLater(() -> {
                                ((ObjectFolder) treeItem.getValue()).setDescription((String)objectList.getInfo());
                                treeItem.getChildren().clear();
                                List<SysTable> systables = objectList.getItems();
                                for (SysTable tabname : systables) {
                                    TreeItem<TreeData> item = createLeafTreeItem(tabname);
                                    treeItem.getChildren().add(item);
                                }
                            });
                        }else{
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                        }
                    }));
        }else if (isLoadableObjectFolder(treeItem)) {
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                        ObjectList objectList;
                        ObjectFolderKind kind = getObjectFolderKind(treeItem);
                        MetaObjectImpl service = getMetaObjectService(kind);
                        if (service == null) {
                            return;
                        }
                        try {
                            objectList = service.loadObjects(getMetaConnect(treeItem), getCurrentDatabase(treeItem));
                        } catch (Exception e) {
                            GlobalErrorHandlerUtil.handle(e);
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                            return;
                        }
                        if (objectList.getInfo() != null ) {
                            Platform.runLater(() -> {
                                ((ObjectFolder) treeItem.getValue()).setDescription((String) objectList.getInfo());
                                treeItem.getChildren().clear();
                                appendObjectFolderChildren(treeItem, kind, objectList);
                            });
                        } else {
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                        }
                    }));
        }else if(treeItem.getValue() instanceof DBPackage){
            MetadataTreeviewUtil.getMetaConnect(treeItem).executeSqlTask(
                    new Thread(() -> {
                        String packageDDL = "";
                        try {
                            //Object parentValue = treeItem.getParent() == null ? null : treeItem.getParent().getValue();
                            packageDDL = packageService.getDDL(getMetaConnect(treeItem), getCurrentDatabase(treeItem),treeItem.getValue().getName());
                    
                        } catch (Exception e) {
                            GlobalErrorHandlerUtil.handle(e);
                        }
                        if (!packageDDL.isEmpty()) {
                            ((DBPackage) treeItem.getValue()).setDDL(packageDDL);

                            List<SqlParserUtil.PackageMember> members = SqlParserUtil.parsePackageMembers(packageDDL);

                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                            });

                            for (SqlParserUtil.PackageMember member : members) {
                                if ("FUNC".equals(member.getType())) {
                                    String functionname = member.getName();
                                    PackageFunction packageFunction = new PackageFunction(functionname);
                                    packageFunction.setDescription("FUNC");
                                    TreeItem<TreeData> item = createLeafTreeItem(packageFunction);
                                    Platform.runLater(() -> {
                                        treeItem.getChildren().add(item);
                                    });
                                }
                                if ("PROC".equals(member.getType())) {
                                    String functionname = member.getName();
                                    PackageProcedure packageProcedure = new PackageProcedure(functionname);
                                    packageProcedure.setDescription("PROC");
                                    TreeItem<TreeData> item = createLeafTreeItem(packageProcedure);
                                    Platform.runLater(() -> {
                                        treeItem.getChildren().add(item);
                                    });
                                }
                            }

                            if (((DBPackage) treeItem.getValue()).getShowDDL()) {
                                String finalPackageDDL = packageDDL;
                                Platform.runLater(() -> {
                                    PopupWindowUtil.openDDLWindow(finalPackageDDL);
                                    Platform.runLater(() -> {
                                        ((DBPackage) treeItem.getValue()).setShowDDL(false);
                                    });
                                });
                            }
                        } else {
                            Platform.runLater(() -> {
                                treeItem.getChildren().clear();
                                treeItem.setExpanded(false);
                            });
                        }

                    }));

        }



    }

    public static void connectionDisconnected(){
        TreeItem<TreeData> treeItem=getMetaConnTreeItem(Main.mainController.databaseMetaTreeView.getSelectionModel().getSelectedItem());
        try {
            ((Connect)treeItem.getValue()).getConn().close();
        } catch (SQLException e) {
            GlobalErrorHandlerUtil.handle(e);
            ((Connect)treeItem.getValue()).setConn(null);
        }

        Platform.runLater(() -> {

            if (AlterUtil.CustomAlertConfirm(
                    I18n.t("common.error", "错误"),
                    I18n.t("metadata.alert.connection_lost", "数据库已断开连接，是否需要重新连接？"))) {
                treeItem.getChildren().clear();
                treeItem.setExpanded(false);
                treeItem.setExpanded(true);

            }
        });
    }

    public static TreeItem<TreeData> getMetaConnTreeItem(TreeItem<TreeData> treeItem){
        TreeItem<TreeData> retrunTreeItem=treeItem;
        if(retrunTreeItem.getValue() instanceof Connect){
            return  retrunTreeItem;
        }else if(retrunTreeItem.getValue() instanceof DatabaseFolder){
            return  retrunTreeItem.getParent();
        }else if(retrunTreeItem.getValue() instanceof UserFolder){
            return  retrunTreeItem.getParent();
        }else if(retrunTreeItem.getValue() instanceof User){
            return  retrunTreeItem.getParent().getParent();
        }else if(retrunTreeItem.getValue() instanceof Database){
            return  retrunTreeItem.getParent().getParent();
        }else if(retrunTreeItem.getValue() instanceof ObjectFolder){
            return  retrunTreeItem.getParent().getParent().getParent();
        }else if(
                retrunTreeItem.getValue() instanceof SysTable||
                        retrunTreeItem.getValue() instanceof Table||
                        retrunTreeItem.getValue() instanceof View||
                        retrunTreeItem.getValue() instanceof Index||
                        retrunTreeItem.getValue() instanceof Sequence||
                        retrunTreeItem.getValue() instanceof Synonym||
                        retrunTreeItem.getValue() instanceof Trigger||
                        retrunTreeItem.getValue() instanceof Function||
                        retrunTreeItem.getValue() instanceof Procedure||
                        retrunTreeItem.getValue() instanceof DBPackage
        ){
            return  retrunTreeItem.getParent().getParent().getParent().getParent();
        }
        else if(
                retrunTreeItem.getValue() instanceof PackageFunction||
                        retrunTreeItem.getValue() instanceof PackageProcedure

        ){
            return  retrunTreeItem.getParent().getParent().getParent().getParent().getParent();
        }
        else{
            return  retrunTreeItem;
        }
    }

    public static Connect getMetaConnect(TreeItem<TreeData> treeItem){
        return (Connect)getMetaConnTreeItem(treeItem).getValue();
    }


    public static Database getCurrentDatabase(TreeItem<TreeData> treeItem){
        TreeItem<TreeData> retrunTreeItem=treeItem;
        if(retrunTreeItem.getValue() instanceof Database){
            return  (Database) retrunTreeItem.getValue();
        }else if(retrunTreeItem.getValue() instanceof ObjectFolder){
            return  (Database) retrunTreeItem.getParent().getValue();
        }else if(retrunTreeItem.getValue() instanceof UserFolder||retrunTreeItem.getValue() instanceof User){
            Database db=new Database("sysuser");
            db.setDbLocale("en_US.819");
            return db;
        }else if(
                retrunTreeItem.getValue() instanceof SysTable||
                        retrunTreeItem.getValue() instanceof Table||
                        retrunTreeItem.getValue() instanceof View||
                        retrunTreeItem.getValue() instanceof Index||
                        retrunTreeItem.getValue() instanceof Sequence||
                        retrunTreeItem.getValue() instanceof Synonym||
                        retrunTreeItem.getValue() instanceof Trigger||
                        retrunTreeItem.getValue() instanceof Function||
                        retrunTreeItem.getValue() instanceof Procedure||
                        retrunTreeItem.getValue() instanceof DBPackage
        ){
            return  (Database) retrunTreeItem.getParent().getParent().getValue();
        }else if(
                retrunTreeItem.getValue() instanceof PackageFunction|| retrunTreeItem.getValue() instanceof PackageProcedure
        ){
            return  (Database) retrunTreeItem.getParent().getParent().getParent().getValue();
        }
        return  (Database) retrunTreeItem.getValue();

    }

    private static void exportTableData(TreeItem<TreeData> tableItem, ExportFormat format) {
        if (!(tableItem.getValue() instanceof Table)) {
            return;
        }
        Table table = (Table) tableItem.getValue();
        Connect connect = buildObjectConnect(tableItem,false);

        FileChooser chooser = new FileChooser();
        chooser.setTitle(I18n.t("metadata.export.title", "导出表数据"));
        String baseName = table.getName();
        switch (format) {
            case CSV -> {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                chooser.setInitialFileName(baseName + ".csv");
            }
            case JSON -> {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
                chooser.setInitialFileName(baseName + ".json");
            }
            case SQL -> {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL", "*.sql"));
                chooser.setInitialFileName(baseName + ".sql");
            }
        }
        File file = chooser.showSaveDialog(Main.scene.getWindow());
        if (file == null) return;
        if (file.exists()) {
            file.delete();
        }

        // 使用下载管理器流式导出，可暂停/取消，避免一次性占用大量内存
        
        String exportSql = "select * from " + table.getName();
        DownloadManagerUtil.addSqlExportTask(connect, exportSql, file, format.name().toLowerCase(), true);
    
    }
    



    public static void disconnectItem(TreeItem<TreeData> selectedItem) {
        Connect connect =(Connect)selectedItem.getValue();
        try {
            //关闭主连接
            if(connect.getConn()!=null&&!connect.getConn().isClosed()) {
                connect.getConn().close();
                for(Tab tab :Main.mainController.sqlTabPane.getTabs()){
                    if(tab instanceof CustomSqlTab) {
                        if (selectedItem.getValue().getName().equals(((CustomSqlTab) tab).sqlTabController.sqlConnect.getName())) {
                            ((CustomSqlTab) tab).sqlTabController.closeConn();
                        }
                    }

                }
            }
            CustomInstanceTab needToRemove=null;
            for(Tab tab :Main.mainController.sqlTabPane.getTabs()) {
                if (tab instanceof CustomInstanceTab) {
                    if (("[instance check]"+selectedItem.getValue().getName()).equals( ((CustomInstanceTab) tab).getTitle())) {
                        needToRemove=(CustomInstanceTab)tab;
                    }
                }
            }
            if(needToRemove!=null)Main.mainController.sqlTabPane.getTabs().remove(needToRemove);
        } catch (SQLException e) {
            GlobalErrorHandlerUtil.handle(e);
            //new CustomAlert("错误",e.toString());
        }
        selectedItem.setExpanded(false);
        if(selectedItem.getChildren().size()>0) {
            selectedItem.getChildren().clear();
        }



    }


    public static void disconnectFolder(TreeItem<TreeData> selectedItem) {
        for (TreeItem<TreeData> t : selectedItem.getChildren()) {
            disconnectItem(t);
        }
        //Main.mainController.connect_list_treeview.refresh();
        //刷新
        //ConnectTreeViewUtil.reorderTreeview(connect_list_treeview,selectedItem);

    }

    public static void reconnectItem(TreeItem<TreeData> selectedItem) {
        TreeItem connTreeItem=getMetaConnTreeItem(selectedItem);
        disconnectItem(connTreeItem);
        connTreeItem.setExpanded(true);
    }

    public static void treeViewMoveConnectItem(TreeView<TreeData> treeView, TreeItem<TreeData> treeItem) {
        for (TreeItem<TreeData> ti : treeView.getRoot().getChildren()) {
            if (((ConnectFolder)ti.getValue()).getId() == ((Connect)treeItem.getValue()).getParentId()) {
                ti.getChildren().add(treeItem);
            }
        }
        treeView.getSelectionModel().clearSelection();
        treeView.getSelectionModel().select(treeItem);
        reorderTreeview(treeView, treeItem);
    }

    public static void searchTree(TreeView treeView,String searchText,Button nextButton) {
        nextButton.setOnAction(e -> findNext(treeView));
        nextButton.setDisable(true);
        //String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            return;
        }

        // 清空之前的搜索结果
        searchResults.clear();
        currentIndex = -1;

        // 执行搜索
        TreeItem<TreeData> root = treeView.getRoot();
        if (root != null) {
            traverseNode(root, searchText);
        }

        // 处理搜索结果
        if (searchResults.isEmpty()) {
            NotificationUtil.showNotification(Main.mainController.noticePane,
                    I18n.t("metadata.search.no_match", "未搜索到匹配项，请确保需查找的对象已加载！"));
        } else {
            findNext(treeView);
            // 更新按钮状态
            if(searchResults.size()>1){
                nextButton.setDisable(false);
            }
        }
    }

    /**
     * 查找下一个匹配项
     */
    private static void findNext(TreeView treeView) {
        if (searchResults.isEmpty()) {
            return;
        }

        // 更新当前索引
        currentIndex = (currentIndex + 1) % searchResults.size();

        // 获取当前匹配项
        TreeItem<TreeData> currentItem = searchResults.get(currentIndex);

        // 滚动到并选中当前项
        treeView.getSelectionModel().clearSelection();
        treeView.getSelectionModel().select(currentItem);
        treeView.scrollTo(treeView.getRow(currentItem));

        // 如果是最后一个，提示用户下一次将从头开始
        if (currentIndex == searchResults.size() - 1) {
            if(currentIndex==0){
                NotificationUtil.showNotification(Main.mainController.noticePane,
                        I18n.t("metadata.search.only_one", "仅匹配当前一个！"));
            }else{
                NotificationUtil.showNotification(Main.mainController.noticePane,
                        I18n.t("metadata.search.wrap", "搜索已到最后，下一个从头开始搜索！"));
            }
        }
    }

    /**
     * 递归遍历节点及其子节点，收集匹配的叶子节点
     */
    private static void traverseNode(TreeItem<TreeData> node, String searchText) {

        //rootitem未设置值可能为null
        if(node.getValue()!=null&&node.getValue().getName().toLowerCase().contains(searchText.toLowerCase())){
            searchResults.add(node);
        }
        // 如果节点未展开，则不继续遍历其子节点
        if (!node.isExpanded()) {
            if(node.getChildren().size()>0){
                //node.setExpanded(true);
            }else{
                return;
            }
        }

        // 如果是叶子节点，检查其值

        // 不是叶子节点，则递归遍历所有子节点

        for (TreeItem<TreeData> child : node.getChildren()) {
            traverseNode(child, searchText);
        }

    }

    //弹出创建连接对话框
    public static void showCreateConnectDialog(TreeData treeDataParam,Boolean isCopy)  {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.dbboys.i18n.messages", I18n.getLocale());
            FXMLLoader loader = new FXMLLoader(CreateConnectController.class.getResource("/com/dbboys/fxml/CreateConnect.fxml"), bundle);
            DialogPane dialogPane = loader.load();
            CreateConnectController controller = loader.getController();
            Dialog<ButtonType> dialog = new Dialog<>();
            controller.init(treeDataParam, isCopy,dialog);
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(I18n.t("createconnect.dialog.title"));
            Stage alterstage = (Stage) dialog.getDialogPane().getScene().getWindow();
            alterstage.getIcons().add(new Image(IconPaths.MAIN_LOGO));
            dialogPane.getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
            TextField connectNameTextField = (TextField) loader.getNamespace().get("connectNameTextField");
            dialogPane.getScene().getWindow().setOnShown(event -> {
                connectNameTextField.requestFocus();
            });
            dialog.setOnCloseRequest(e -> {
                dialog.close();
            });
            dialog.showAndWait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean canCopyItem(TreeItem<TreeData> selectedItem) {
        TreeData treeData = selectedItem.getValue();
        return selectedItem.isLeaf() || treeData instanceof DBPackage || treeData instanceof Database;
    }

    private static boolean canRefreshItem(TreeItem<TreeData> selectedItem) {
        TreeData treeData = selectedItem.getValue();
        if (treeData instanceof DatabaseFolder || treeData instanceof UserFolder || treeData instanceof Database || treeData instanceof ObjectFolder || treeData instanceof Table || treeData instanceof Index || treeData instanceof Trigger || treeData instanceof DBPackage) {
            return true;
        }
        if (treeData instanceof SysTable) {
            return !"view".equals(((SysTable) treeData).getTableTypeCode());
        }
        return false;
    }

    private static boolean canRenameItem(TreeItem<TreeData> selectedItem) {
        TreeData treeData = selectedItem.getValue();
        if (!(treeData instanceof ConnectFolder
                || treeData instanceof Connect
                || treeData instanceof Database
                || treeData instanceof Table
                || treeData instanceof Index
                || treeData instanceof Sequence)) {
            return false;
        }
        if (treeData instanceof ConnectFolder) {
            return selectedItem.getParent() != null && selectedItem.getParent().getChildren().size() > 1;
        }
        if (treeData instanceof Connect) {
            try {
                Connect connect = (Connect) treeData;
                return connect.getConn() == null || connect.getConn().isClosed();
            } catch (SQLException e) {
                GlobalErrorHandlerUtil.handle(e);
                return false;
            }
        }
        if (isReadOnlyObject(selectedItem) || isSystemDatabaseObject(selectedItem)) {
            return false;
        }
        if (treeData instanceof Index && !treeData.getName().isEmpty() && treeData.getName().charAt(0) == ' ') {
            return false;
        }
        return true;
    }

    private static boolean canDeleteItem(TreeItem<TreeData> selectedItem) {
        TreeData treeData = selectedItem.getValue();
        if (!(treeData instanceof ConnectFolder
                || treeData instanceof Connect
                || treeData instanceof Database
                || treeData instanceof Table
                || treeData instanceof View
                || treeData instanceof Index
                || treeData instanceof Sequence
                || treeData instanceof Synonym
                || treeData instanceof Trigger
                || treeData instanceof Function
                || treeData instanceof Procedure
                || treeData instanceof DBPackage
                || treeData instanceof User)) {
            return false;
        }
        if (treeData instanceof ConnectFolder) {
            return selectedItem.getParent() != null && selectedItem.getParent().getChildren().size() > 1;
        }
        if (treeData instanceof Connect) {
            try {
                Connect connect = (Connect) treeData;
                return connect.getConn() == null || connect.getConn().isClosed();
            } catch (SQLException e) {
                GlobalErrorHandlerUtil.handle(e);
                return false;
            }
        }
        if (isReadOnlyObject(selectedItem) || isSystemDatabaseObject(selectedItem)) {
            return false;
        }
        if (treeData instanceof Index && !treeData.getName().isEmpty() && treeData.getName().charAt(0) == ' ') {
            return false;
        }
        return true;
    }

    private static boolean isReadOnlyObject(TreeItem<TreeData> selectedItem) {
        TreeData treeData = selectedItem.getValue();
        if (treeData instanceof ConnectFolder || treeData instanceof Connect) {
            return false;
        }
        Connect connect = MetadataTreeviewUtil.getMetaConnect(selectedItem);
        return connect.getReadonly() != null && connect.getReadonly();
    }

    private static boolean isSystemDatabaseObject(TreeItem<TreeData> selectedItem) {
        TreeData treeData = selectedItem.getValue();
        if (!(treeData instanceof Database
                || treeData instanceof ObjectFolder
                || treeData instanceof SysTable
                || treeData instanceof Table
                || treeData instanceof View
                || treeData instanceof Index
                || treeData instanceof Sequence
                || treeData instanceof Synonym
                || treeData instanceof Trigger
                || treeData instanceof Function
                || treeData instanceof Procedure
                || treeData instanceof DBPackage)) {
            return false;
        }
        String database = MetadataTreeviewUtil.getCurrentDatabase(selectedItem).getName();
        return database.equals("sysmaster")
                || database.equals("sysuser")
                || database.equals("sysadmin")
                || database.equals("sysutils")
                || database.equals("sysha")
                || database.equals("syscdr")
                || database.equals("syscdcv1")
                || database.equals("gbasedbt")
                || database.equals("sys");
    }

    private static boolean isDatabaseMenuObject(TreeData treeData) {
        return treeData instanceof DatabaseFolder
                || treeData instanceof UserFolder
                || treeData instanceof User
                || treeData instanceof Database
                || treeData instanceof ObjectFolder
                || treeData instanceof SysTable
                || treeData instanceof Table
                || treeData instanceof View
                || treeData instanceof Index
                || treeData instanceof Sequence
                || treeData instanceof Synonym
                || treeData instanceof Trigger
                || treeData instanceof Function
                || treeData instanceof Procedure
                || treeData instanceof DBPackage
                || treeData instanceof PackageFunction
                || treeData instanceof PackageProcedure;
    }

    private static boolean isMultiTableSelection(List<TreeItem<TreeData>> selectedItems) {
        if (selectedItems == null || selectedItems.size() <= 1) {
            return false;
        }
        for (TreeItem<TreeData> item : selectedItems) {
            if (item == null || !(item.getValue() instanceof Table)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isMultiDeleteOnlySelection(List<TreeItem<TreeData>> selectedItems) {
        if (selectedItems == null || selectedItems.size() <= 1 || isMultiTableSelection(selectedItems)) {
            return false;
        }
        for (TreeItem<TreeData> item : selectedItems) {
            if (item == null || item.getValue() == null) {
                return false;
            }
            TreeData value = item.getValue();
            if (!(value instanceof View
                    || value instanceof Index
                    || value instanceof Sequence
                    || value instanceof Synonym
                    || value instanceof Trigger
                    || value instanceof Function
                    || value instanceof Procedure
                    || value instanceof DBPackage
                    || value instanceof User)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isReadOnlyConnectionSelection(List<TreeItem<TreeData>> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return false;
        }
        for (TreeItem<TreeData> item : selectedItems) {
            if (item == null || item.getValue() == null) {
                continue;
            }
            if (item.getValue() instanceof ConnectFolder || item.getValue() instanceof Connect) {
                continue;
            }
            Connect connect = MetadataTreeviewUtil.getMetaConnect(item);
            if (connect.getReadonly() != null && connect.getReadonly()) {
                return true;
            }
        }
        return false;
    }

    private static MetaObjectImpl getDeleteService(TreeData treeData) {
        if (treeData instanceof View) {
            return viewService;
        }
        if (treeData instanceof Index) {
            return indexService;
        }
        if (treeData instanceof Sequence) {
            return sequenceService;
        }
        if (treeData instanceof Synonym) {
            return synonymService;
        }
        if (treeData instanceof Trigger) {
            return triggerService;
        }
        if (treeData instanceof Function) {
            return functionService;
        }
        if (treeData instanceof Procedure) {
            return procedureService;
        }
        if (treeData instanceof DBPackage) {
            return packageService;
        }
        if (treeData instanceof User) {
            return databaseService;
        }
        return null;
    }

    private static String getDeleteObjectType(TreeData treeData) {
        if (treeData instanceof View) {
            return "view";
        }
        if (treeData instanceof Index) {
            return "index";
        }
        if (treeData instanceof Sequence) {
            return "sequence";
        }
        if (treeData instanceof Synonym) {
            return "synonym";
        }
        if (treeData instanceof Trigger) {
            return "trigger";
        }
        if (treeData instanceof Function) {
            return "function";
        }
        if (treeData instanceof Procedure) {
            return "procedure";
        }
        if (treeData instanceof DBPackage) {
            return "package";
        }
        if (treeData instanceof User) {
            return "user";
        }
        return "object";
    }

    private enum ObjectFolderKind {
        SYSTEM_TABLE_VIEW,
        TABLES,
        VIEWS,
        INDEXES,
        SEQUENCES,
        SYNONYMS,
        TRIGGERS,
        FUNCTIONS,
        PROCEDURES,
        PACKAGES,
        UNKNOWN
    }

    private static boolean isLoadableObjectFolder(TreeItem<TreeData> treeItem) {
        ObjectFolderKind kind = getObjectFolderKind(treeItem);
        return kind == ObjectFolderKind.TABLES
                || kind == ObjectFolderKind.VIEWS
                || kind == ObjectFolderKind.INDEXES
                || kind == ObjectFolderKind.SEQUENCES
                || kind == ObjectFolderKind.SYNONYMS
                || kind == ObjectFolderKind.TRIGGERS
                || kind == ObjectFolderKind.FUNCTIONS
                || kind == ObjectFolderKind.PROCEDURES
                || kind == ObjectFolderKind.PACKAGES;
    }

    private static MetaObjectImpl getMetaObjectService(ObjectFolderKind kind) {
        return switch (kind) {
            case TABLES -> tableService;
            case VIEWS -> viewService;
            case INDEXES -> indexService;
            case SEQUENCES -> sequenceService;
            case SYNONYMS -> synonymService;
            case TRIGGERS -> triggerService;
            case FUNCTIONS -> functionService;
            case PROCEDURES -> procedureService;
            case PACKAGES -> packageService;
            default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    private static void appendObjectFolderChildren(TreeItem<TreeData> treeItem, ObjectFolderKind kind, ObjectList objectList) {
        switch (kind) {
            case TABLES -> {
                List<Table> tables = objectList.getItems();
                for (Table tab : tables) {
                    treeItem.getChildren().add(createLeafTreeItem(tab));
                }
            }
            case VIEWS -> {
                List<View> views = objectList.getItems();
                for (View view : views) {
                    treeItem.getChildren().add(createLeafTreeItem(view));
                }
            }
            case INDEXES -> {
                List<Index> indexes = objectList.getItems();
                for (Index index : indexes) {
                    treeItem.getChildren().add(createLeafTreeItem(index));
                }
            }
            case SEQUENCES -> {
                List<Sequence> sequences = objectList.getItems();
                for (Sequence sequence : sequences) {
                    treeItem.getChildren().add(createLeafTreeItem(sequence));
                }
            }
            case SYNONYMS -> {
                List<Synonym> synonyms = objectList.getItems();
                for (Synonym synonym : synonyms) {
                    treeItem.getChildren().add(createLeafTreeItem(synonym));
                }
            }
            case TRIGGERS -> {
                List<Trigger> triggers = objectList.getItems();
                for (Trigger trigger : triggers) {
                    treeItem.getChildren().add(createLeafTreeItem(trigger));
                }
            }
            case FUNCTIONS -> {
                List<Function> functions = objectList.getItems();
                for (Function function : functions) {
                    treeItem.getChildren().add(createLeafTreeItem(function));
                }
            }
            case PROCEDURES -> {
                List<Procedure> procedures = objectList.getItems();
                for (Procedure procedure : procedures) {
                    treeItem.getChildren().add(createLeafTreeItem(procedure));
                }
            }
            case PACKAGES -> {
                List<DBPackage> packages = objectList.getItems();
                for (DBPackage pkg : packages) {
                    treeItem.getChildren().add(createTreeItem(pkg));
                }
            }
            default -> {
            }
        }
    }

    public static void handleDdlAction(TreeView<TreeData> treeView, BiConsumer<TreeData, String> onSuccess) {
        ObservableList<TreeItem<TreeData>> selectedItems = treeView.getSelectionModel().getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) {
            return;
        }

        List<TreeItem<TreeData>> items = new ArrayList<>(selectedItems);
        TreeItem<TreeData> firstItem = items.get(0);
        if (firstItem == null || firstItem.getValue() == null) {
            return;
        }
        TreeData firstData = firstItem.getValue();
        boolean multi = items.size() > 1;

        Task<String> ddlTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                StringBuilder sb = new StringBuilder();
                for (TreeItem<TreeData> item : items) {
                    if (item == null || item.getValue() == null) {
                        continue;
                    }
                    TreeData data = item.getValue();
                    if (data.isRunning()) {
                        continue;
                    }
                    data.setRunning(true);
                    Connect connectParam = MetadataTreeviewUtil.getMetaConnect(item);
                    Database database = MetadataTreeviewUtil.getCurrentDatabase(item);
                    String ddlText = "";
                    if (data instanceof Table) {
                        ddlText = tableService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof Index) {
                        ddlText = indexService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof View) {
                        ddlText = viewService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof Trigger) {
                        ddlText = triggerService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof Sequence) {
                        ddlText = sequenceService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof Synonym) {
                        ddlText = synonymService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof Function) {
                        ddlText = functionService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof Procedure) {
                        ddlText = procedureService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof DBPackage) {
                        ddlText = packageService.getDDL(connectParam, database, data.getName());
                    } else if (data instanceof PackageFunction || data instanceof PackageProcedure) {
                        ddlText = packageService.getChildrenDDL(
                                ((DBPackage) item.getParent().getValue()).getDDL(), data.getName());
                    }

                    if (!multi) {
                        data.setRunning(false);
                        return SqlParserUtil.formatSql(ddlText);
                    }
                    if (ddlText != null && !ddlText.isEmpty()) {
                        sb.append("-- ").append(data.getName()).append(System.lineSeparator());
                        sb.append(ddlText).append(System.lineSeparator()).append(System.lineSeparator());
                    }
                     data.setRunning(false);
                }
                return SqlParserUtil.formatSql(sb.toString());
            }
        };

        ddlTask.setOnSucceeded(event1 -> {
            items.forEach(it -> {
                if (it != null && it.getValue() != null) {
                    it.getValue().setRunning(false);
                }
            });
            String ddlText = ddlTask.getValue();
            onSuccess.accept(firstData, ddlText == null ? "" : ddlText);
        });
        GlobalErrorHandlerUtil.bindTask(ddlTask, () -> items.forEach(it -> {
            if (it != null && it.getValue() != null) {
                it.getValue().setRunning(false);
            }
        }));

        Thread thread = new Thread(ddlTask);
        getMetaConnect(firstItem).executeSqlTask(thread);
    }

    private static void bindFolderName(TreeData treeData, String key, String defaultText) {
        treeData.nameProperty().unbind();
        treeData.nameProperty().bind(Bindings.createStringBinding(
                () -> I18n.t(key, defaultText),
                I18n.localeProperty()
        ));
    }

    private static ObjectFolder createObjectFolder(ObjectFolderKind kind) {
        ObjectFolder objectFolder = new ObjectFolder();
        switch (kind) {
            case SYSTEM_TABLE_VIEW -> bindFolderName(objectFolder, "metadata.folder.system_table_view", "系统表/视图");
            case TABLES -> bindFolderName(objectFolder, "metadata.folder.tables", "表");
            case VIEWS -> bindFolderName(objectFolder, "metadata.folder.views", "视图");
            case INDEXES -> bindFolderName(objectFolder, "metadata.folder.indexes", "索引");
            case SEQUENCES -> bindFolderName(objectFolder, "metadata.folder.sequences", "序列");
            case SYNONYMS -> bindFolderName(objectFolder, "metadata.folder.synonyms", "同义词");
            case TRIGGERS -> bindFolderName(objectFolder, "metadata.folder.triggers", "触发器");
            case FUNCTIONS -> bindFolderName(objectFolder, "metadata.folder.functions", "函数");
            case PROCEDURES -> bindFolderName(objectFolder, "metadata.folder.procedures", "存储过程");
            case PACKAGES -> bindFolderName(objectFolder, "metadata.folder.packages", "包");
            default -> objectFolder.setName("");
        }
        return objectFolder;
    }

    private static boolean isObjectFolder(TreeItem<TreeData> treeItem, ObjectFolderKind kind) {
        return treeItem != null
                && treeItem.getValue() instanceof ObjectFolder
                && getObjectFolderKind(treeItem) == kind;
    }

    private static ObjectFolderKind getObjectFolderKind(TreeItem<TreeData> treeItem) {
        if (treeItem == null || !(treeItem.getValue() instanceof ObjectFolder)) {
            return ObjectFolderKind.UNKNOWN;
        }
        TreeItem<TreeData> parent = treeItem.getParent();
        if (parent == null) {
            return ObjectFolderKind.UNKNOWN;
        }
        int index = parent.getChildren().indexOf(treeItem);
        return switch (index) {
            case 0 -> ObjectFolderKind.SYSTEM_TABLE_VIEW;
            case 1 -> ObjectFolderKind.TABLES;
            case 2 -> ObjectFolderKind.VIEWS;
            case 3 -> ObjectFolderKind.INDEXES;
            case 4 -> ObjectFolderKind.SEQUENCES;
            case 5 -> ObjectFolderKind.SYNONYMS;
            case 6 -> ObjectFolderKind.TRIGGERS;
            case 7 -> ObjectFolderKind.FUNCTIONS;
            case 8 -> ObjectFolderKind.PROCEDURES;
            case 9 -> ObjectFolderKind.PACKAGES;
            default -> ObjectFolderKind.UNKNOWN;
        };
    }

}












