package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.customnode.*;
import com.dbboys.vo.Connect;
import com.dbboys.vo.ConnectFolder;
import com.dbboys.vo.TreeData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class TabpaneUtil {
    private static final Logger log = LogManager.getLogger(CustomGenericStyledArea.class);

    private static TabPane tabpane= Main.mainController.sql_tabpane;

    public static void addCustomSqlTab(Connect connect){
        String tabName = "script";
        for (int i = 1; i <= 9999999; i++) {
            Boolean isContrained = false;
            for (Tab tab : tabpane.getTabs()) {
                if (((CustomTab)tab).getTitle().replaceAll("\\*","").equals("script" + i + ".sql")) {
                    isContrained = true;
                    break;
                }
            }
            if (!isContrained) {
                tabName = "script" + i + ".sql";
                break;
            }
        }
        CustomSqlTab newtab = new CustomSqlTab(tabName);
        //newtab.setContent(new CustomSqlTab().getSqlTab());
        tabpane.getTabs().add(newtab);
        tabpane.getSelectionModel().select(newtab);
        //设置一个id，用于生效分隔条CSS
        //newtab.data_manager_splitpane.setId("runSplitPane");

        //双击新建sql面板，connect是null
        if(connect!=null){
            newtab.sqlTabController.sql_connect_choicebox.setValue(connect);
        }
    }


    public static void refreshConnectList(){
            for (Tab tab : tabpane.getTabs()) {
                if (tab instanceof  CustomSqlTab) {
                    ((CustomSqlTab)(tab)).sqlTabController.initConnectList();
                }
        }

    }


    public static void addConnectsInfoTab(ConnectFolder connect){
        for (Tab tab : tabpane.getTabs()) {
            if (((CustomTab)tab).getTitle().equals("[connectsInfo]"+connect.getName())) {
                tabpane.getSelectionModel().select(tab);
                return;
            }
        }

        CustomTab newtab=new CustomTab("[connectsInfo]"+connect.getName());
        tabpane.getTabs().add(newtab);
        tabpane.getSelectionModel().select(newtab);

        // 分类实例信息列表
        TableView<TreeData> instance_info_tableview = new CustomInstanceInfoTableView();

        newtab.setContent(instance_info_tableview);
        //如果分类下有连接，显示所有连接信息
        ObservableList<TreeData> data = FXCollections.observableArrayList(SqliteDBaccessUtil.getFolderConnect(connect));
        // 设置数据
        instance_info_tableview.setItems(data);
        //默认选中第一行，保证右键事件当前行不为null
        instance_info_tableview.getSelectionModel().selectFirst();
        //按名称排序
        //name.setSortType(TableColumn.SortType.ASCENDING);
        //instance_info_tableview.getSortOrder().add(name);  // 将列添加到排序列表中
        instance_info_tableview.sort();
    }

    public static void addCustomInstanceTab(Connect connect,int tabNum){
        Platform.runLater(() -> {
            Boolean isContrained = false;
            for (Tab tab : tabpane.getTabs()) {
                if (tab instanceof CustomInstanceTab) {
                    if (((CustomInstanceTab) tab).getTitle().equals("[instance]"+connect.getName())) {
                        isContrained = true;
                        tabpane.getSelectionModel().select(tab);
                        ((CustomInstanceTab) tab).mainTabPane.getSelectionModel().select(tabNum);
                        break;
                    }
                }
            }
            if (!isContrained) {
                CustomInstanceTab newtab = new CustomInstanceTab(connect,tabNum);
                tabpane.getTabs().add(newtab);
                tabpane.getSelectionModel().select(newtab);
            }
        });
    }



    public static void addCustomMarkdownTab(File file,boolean modifiable) {
        if(!file.exists()){
            AlterUtil.CustomAlert("错误","文件不存在！");
            return;
        }
        Platform.runLater(() -> {
            Boolean isContrained = false;
            for (Tab tab : tabpane.getTabs()) {
                if (tab instanceof CustomMarkdownTab) {
                    if (((CustomMarkdownTab) tab).sql_file_path.equals(file.getAbsolutePath())) {
                        isContrained = true;
                        tabpane.getSelectionModel().select(tab);
                        break;
                    }
                }
            }
            if (!isContrained) {

                CustomMarkdownTab newtab = new CustomMarkdownTab(file,modifiable);
                tabpane.getTabs().add(newtab);
                tabpane.getSelectionModel().select(newtab);
            }
        });
    }

    public static CustomMarkdownTab findCustomMarkdownTab(Path path){
        for(Tab t:tabpane.getTabs()){
            if(t instanceof CustomMarkdownTab){
                CustomMarkdownTab tab=((CustomMarkdownTab)t);
                if(tab.sql_file_path.startsWith(path.toString())){
                    return tab;
                }
            }

        }
        return null;
    };
    public static void renameCustomMarkdownTab(Path oldPath, Path newPath) {
        CustomMarkdownTab tab = findCustomMarkdownTab(oldPath);
        if(tab!=null){
            tab.sql_file_path=newPath.toString();
            if(tab.getTitle().startsWith("*")){
                tab.sql_save_button.fire();
            }
            tab.setTitle(newPath.getFileName().toString());
        }
    }


    public static void removeCustomMarkdownTab(Path path) {
        CustomMarkdownTab tab = findCustomMarkdownTab(path);
        if(tab!=null){
            tabpane.getTabs().remove(tab);
        }
    }

    public static void closeOtherTabs(Tab tab){
        Boolean sureToclosed=true;
        for(Tab t:tabpane.getTabs()){
            if(t.getText().startsWith("*")&&!t.equals(tab)) {
                if (!AlterUtil.CustomAlertConfirm("提示", "部分打开的文件未保存，确定要关闭吗？")) {
                    sureToclosed = false;
                }
                break;
            }
        }

        //如果回答确认，执行关闭流程
        if(sureToclosed) {
            List<Tab> tabsToRemove = new ArrayList<>();
            for (Tab t : tabpane.getTabs()) {
                if (t != tab) {
                    tabsToRemove.add(t);
                    if(t instanceof CustomSqlTab)
                        ((CustomSqlTab)t).sqlTabController.closeConn();
                }
            }
            tabpane.getTabs().removeAll(tabsToRemove);
        }
    }


    public static void closeAllTabs(){
        Boolean sureToclosed=true;
        TabPane tabpane= Main.mainController.sql_tabpane;
        for(Tab t:tabpane.getTabs()){
            if(t.getText().startsWith("*")) {
                if (!AlterUtil.CustomAlertConfirm("提示", "部分打开的文件未保存，确定要关闭吗？")) {
                    sureToclosed = false;
                }
                break;
            }
        }

        //如果回答确认，执行关闭流程
        if(sureToclosed) {
            for (Tab t : tabpane.getTabs()) {
                    if(t instanceof CustomSqlTab)
                        ((CustomSqlTab)t).sqlTabController.closeConn();
                }

            tabpane.getTabs().removeAll(tabpane.getTabs());

            //tabpane增加一个监听确保双击增加页面有效
            Main.mainController.sql_tabpane.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY &&event.getClickCount() == 2) {
                    TabpaneUtil.addCustomSqlTab(null);
                }
            });

        }
    }
}
