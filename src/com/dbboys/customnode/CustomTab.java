package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.util.AlterUtil;
import com.dbboys.util.TabpaneUtil;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class CustomTab extends Tab {
    private Label titleLabel = new Label();
    private HBox header = new HBox(titleLabel);
    public String sql_file_path="";
    public Button sql_save_button;

    public CustomTab(String title) {
        sql_save_button=new Button("");
        sql_save_button.setDisable(true);
        //如果有面板，去掉双击响应事件
        Main.mainController.sql_tabpane.setOnMouseClicked(null);
        //super(title);
        //设置标题保证标题溢出下拉正常显示标题
        setText(title);
        titleLabel.setText(title);
        header.setSpacing(5);
        //设置图标保证响应双击最大化事件
        setGraphic(header);



        titleLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if(Main.sqledit_codearea_is_max == 0)
                {
                    for(Tab tab:Main.mainController.treeview_tabpane.getTabs()){
                        if(((CustomTreeviewTab)tab).titleToggle.isSelected()){
                            ((CustomTreeviewTab)tab).titleToggle.setSelected(false);
                        }
                    }
                    Main.mainController.main_splitpane.setDividerPositions(0);
                    Main.sqledit_codearea_is_max = 1;
                }else{
                    for(Tab tab:Main.mainController.treeview_tabpane.getTabs()){
                        if(((CustomTreeviewTab)tab).isSelected()){
                            ((CustomTreeviewTab)tab).titleToggle.setSelected(true);
                        }
                    }
                    Main.mainController.main_splitpane.setDividerPositions(Main.split1Pos);
                    Main.sqledit_codearea_is_max = 0;
                }
                if(this instanceof CustomSqlTab){
                    Platform.runLater(()->{
                        ((CustomSqlTab) this).sqlTabController.sql_edit_codearea.requestFocus();
                    });
                }
            }
        });



        ContextMenu tabMenu = new ContextMenu();
        titleLabel.setContextMenu(tabMenu);
        SVGPath closeItemIcon = new SVGPath();
        closeItemIcon.setScaleX(0.6);
        closeItemIcon.setScaleY(0.6);
        closeItemIcon.setContent("M20 3 L4.0156 3 Q3.1719 3 2.5781 3.5938 Q2 4.1719 2 4.9844 L2 19 Q2 19.8281 2.5781 20.4219 Q3.1719 21 4.0156 21 L20 21 Q20.8438 21 21.4219 20.4219 Q22.0156 19.8281 22.0156 19 L22.0156 4.9844 Q22.0156 4.1719 21.4219 3.5938 Q20.8438 3 20 3 ZM4.0156 19 L4.0156 7 L20 7 L20 19 L4.0156 19 L4.0156 19 ZM15.7031 10.7031 L14.2969 9.2969 L12 11.5781 L9.7031 9.2969 L8.2969 10.7031 L10.5938 13 L8.2969 15.2969 L9.7031 16.7031 L12 14.4062 L14.2969 16.7031 L15.7031 15.2969 L13.4062 13 L15.7031 10.7031 Z");
        closeItemIcon.setFill(Color.valueOf("#9f453c"));
        MenuItem closeAllItem = new  MenuItem("关闭所有页面 ( Close All Tabs) ");
        closeAllItem.setGraphic(new Group(closeItemIcon));



        SVGPath closeOthersItemIcon = new SVGPath();
        closeOthersItemIcon.setScaleX(0.6);
        closeOthersItemIcon.setScaleY(0.6);
        closeOthersItemIcon.setContent("M20 3 L4.0156 3 Q3.1719 3 2.5781 3.5938 Q2 4.1719 2 4.9844 L2 19 Q2 19.8281 2.5781 20.4219 Q3.1719 21 4.0156 21 L20 21 Q20.8438 21 21.4219 20.4219 Q22.0156 19.8281 22.0156 19 L22.0156 4.9844 Q22.0156 4.1719 21.4219 3.5938 Q20.8438 3 20 3 ZM4.0156 19 L4.0156 7 L20 7 L20 19 L4.0156 19 L4.0156 19 ZM15.7031 10.7031 L14.2969 9.2969 L12 11.5781 L9.7031 9.2969 L8.2969 10.7031 L10.5938 13 L8.2969 15.2969 L9.7031 16.7031 L12 14.4062 L14.2969 16.7031 L15.7031 15.2969 L13.4062 13 L15.7031 10.7031 Z");
        closeOthersItemIcon.setFill(Color.valueOf("#9f453c"));
        MenuItem closeOthersItem = new  MenuItem("关闭其他页面 ( Close Other Tabs ) ");
        closeOthersItem.setGraphic(new Group(closeOthersItemIcon));

        tabMenu.getItems().addAll(closeOthersItem,closeAllItem);



        closeAllItem.setOnAction(event -> {
            TabpaneUtil.closeAllTabs();
        });

        closeOthersItem.setOnAction(event -> {
            TabpaneUtil.closeOtherTabs(this);
        });


        //根据保存按钮的情况，标题是否显示*提示未保存
        sql_save_button.disableProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal){
                setTitle(getTitle().replaceAll("\\*",""));
            }else{
                setTitle("*"+getTitle());
            }
        });


        setOnCloseRequest(event1 -> {
            /*避免关闭后双击无响应*/
            if (Main.mainController.sql_tabpane.getTabs().size() == 1) {
                Main.mainController.sql_tabpane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        TabpaneUtil.addCustomSqlTab(null);
                    }
                });
            }
        });


    }
    public String getTitle(){
        return titleLabel.getText();
    }
    public void setTitle(String title){
        titleLabel.setText(title);
        setText(title);
        //setGraphic(header);
    }
    public Label getTitleLabel(){
        return titleLabel;
    }


}
