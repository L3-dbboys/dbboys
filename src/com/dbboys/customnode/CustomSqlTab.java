package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.ctrl.SqlTabController;
import com.dbboys.util.AlterUtil;
import com.dbboys.util.TabpaneUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomSqlTab extends CustomTab{
    //sql编辑框以上控件
    public SqlTabController sqlTabController;
    public CustomSqlTab(String title) {
        super(title);
        setTooltip(new Tooltip(sql_file_path.equals("")?"新建脚本未保存到磁盘":sql_file_path));

        //加载图形界面
        VBox contentVbox=new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/SqlTab.fxml"));
        try {
            contentVbox= loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setContent(contentVbox);

        //获取控制器实例
        sqlTabController=loader.getController();
        sqlTabController.sqlEditCodeArea.sql_save_button=sql_save_button;
        //sql_save_button.disableProperty().bind(sqlTabController.sqlEditCodeArea.sql_save_button.disableProperty());

        //设置标题提示
        setTooltip(new Tooltip(sql_file_path.equals("")?"新建脚本未保存到磁盘":sql_file_path));
        //增加最大化时SQL编辑分隔栏到最底下，addEventHandler不会覆盖父类事件响应，而是累加
        getTitleLabel().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (Main.sqledit_codearea_is_max == 1) {
                    sqlTabController.sqlSplitPane.setDividerPositions(1);
                } else {
                    sqlTabController.sqlSplitPane.setDividerPositions(sqlTabController.sqlSplitPaneDividerPosition);
                }
            }
        });

        //保存按钮事件
        sql_save_button.setOnAction(event->{
                    String content=sqlTabController.sqlEditCodeArea.getText();
                    if(sql_file_path.equals("")){
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("保存文件");
                        fileChooser.setInitialFileName(getTitle().replaceAll("\\*",""));
                        File file = fileChooser.showSaveDialog(Main.scene.getWindow());
                        if (file != null) { //用户选择了确认
                            try (FileWriter writer = new FileWriter(file)) {
                                writer.write(content);
                                setTitle(file.getName());
                                sql_file_path=file.getAbsolutePath();
                                sql_save_button.setDisable(true);
                                setTooltip(new Tooltip(sql_file_path));
                            } catch (IOException e) {
                                AlterUtil.CustomAlert("错误",e.getMessage());
                            }

                        }
                    }else{
                        try {
                            Files.writeString(Paths.get(sql_file_path), content);
                            setTitle(getTitle().replaceAll("\\*",""));
                            sql_save_button.setDisable(true);
                        } catch (IOException e) {
                            AlterUtil.CustomAlert("错误",e.getMessage());
                        }
                    }

                });



        //关闭窗口事件响应

        setOnCloseRequest(event1 -> {
            /*避免关闭后双击无响应*/
            if (Main.mainController.sqlTabPane.getTabs().size() == 1) {
                Main.mainController.sqlTabPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        TabpaneUtil.addCustomSqlTab(null);
                    }
                });
            }
            if(getTitle().startsWith("*")){
                if(AlterUtil.CustomAlertConfirm("关闭文件","文件【"+getTitle().replaceAll("\\*","")+"】未保存，确定要关闭吗？")){
                    sqlTabController.closeConn();
                }else{
                    event1.consume(); // 取消关闭
                }
            }else{
                sqlTabController.closeConn();
                // sql_tabpane.getTabs().remove(newtab);
            }

        });

    }
    //打开sql文件
    public void openSqlFile() {
        try {
            sqlTabController.sqlEditCodeArea.replaceText(Files.readString(Path.of(sql_file_path)));
            getTitle().replaceAll("\\*","");
            sql_save_button.setDisable(true);
            setTooltip(new Tooltip(sql_file_path));
        } catch (IOException e) {
            e.printStackTrace();
            AlterUtil.CustomAlert("错误",e.getMessage());
        }
    }


}

