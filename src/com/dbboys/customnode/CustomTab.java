package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.util.MenuItemUtil;
import com.dbboys.util.TabpaneUtil;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class CustomTab extends Tab {
    private static final String DIRTY_PREFIX = "*";
    private final Label titleLabel = new Label();
    private final HBox header = new HBox(titleLabel);
    public String filePath="";
    private boolean dirty = false;

    public CustomTab(String title) {
        //如果有面板，去掉双击响应事件
        Main.mainController.sqlTabPane.setOnMouseClicked(null);
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
                    for(Tab tab:Main.mainController.treeviewTabPane.getTabs()){
                        if(((CustomTreeviewTab)tab).titleToggle.isSelected()){
                            ((CustomTreeviewTab)tab).titleToggle.setSelected(false);
                        }
                    }
                    Main.mainController.mainSplitPane.setDividerPositions(0);
                    Main.sqledit_codearea_is_max = 1;
                }else{
                    for(Tab tab:Main.mainController.treeviewTabPane.getTabs()){
                        if(((CustomTreeviewTab)tab).isSelected()){
                            ((CustomTreeviewTab)tab).titleToggle.setSelected(true);
                        }
                    }
                    Main.mainController.mainSplitPane.setDividerPositions(Main.split1Pos);
                    Main.sqledit_codearea_is_max = 0;
                }
                if(this instanceof CustomSqlTab){
                    Platform.runLater(()->{
                        ((CustomSqlTab) this).sqlTabController.sqlEditCodeArea.requestFocus();
                    });
                }
            }
        });



        ContextMenu tabMenu = new ContextMenu();
        titleLabel.setContextMenu(tabMenu);
        CustomShortcutMenuItem closeAllItem = MenuItemUtil.createMenuItemI18n(
                "customtab.menu.close_all",
                IconFactory.group(IconPaths.TAB_CLOSE_MENU_ITEM, 0.5, 0.5, Color.web("#e81123"))
        );
        CustomShortcutMenuItem closeOthersItem = MenuItemUtil.createMenuItemI18n(
                "customtab.menu.close_others",
                IconFactory.group(IconPaths.TAB_CLOSE_MENU_ITEM, 0.5, 0.5, Color.web("#e81123"))
        );

        tabMenu.getItems().addAll(closeOthersItem,closeAllItem);



        closeAllItem.setOnAction(event -> {
            TabpaneUtil.closeAllTabs();
        });

        closeOthersItem.setOnAction(event -> {
            TabpaneUtil.closeOtherTabs(this);
        });


        setOnCloseRequest(event1 -> {
            /*避免关闭后双击无响应*/
            if (Main.mainController.sqlTabPane.getTabs().size() == 1) {
                Main.mainController.sqlTabPane.setOnMouseClicked(event -> {
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
        String baseTitle = title == null ? "" : title.replace(DIRTY_PREFIX, "");
        String displayTitle = dirty ? DIRTY_PREFIX + baseTitle : baseTitle;
        titleLabel.setText(displayTitle);
        setText(displayTitle);
        //setGraphic(header);
    }
    public Label getTitleLabel(){
        return titleLabel;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        setDirty(true);
    }

    public void markSaved() {
        setDirty(false);
    }

    protected void setDirty(boolean dirty) {
        if (this.dirty == dirty) {
            return;
        }
        this.dirty = dirty;
        setTitle(getTitle());
    }

    public void requestSave() {
        // Override in subclasses that support save.
    }


}

