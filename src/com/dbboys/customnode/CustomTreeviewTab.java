package com.dbboys.customnode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbboys.app.Main;
import com.dbboys.util.ConfigManagerUtil;
import com.dbboys.util.MarkdownUtil;
import com.dbboys.vo.Markdown;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class CustomTreeviewTab extends Tab {
    private static final Logger log = LogManager.getLogger(CustomTreeviewTab.class);

    public ToggleButton titleToggle = new ToggleButton();
    private StackPane header = new StackPane(titleToggle);
    public ContextMenu contextMenu=new ContextMenu();
    public SVGPath titleToggleIcon = new SVGPath();

    public CustomTreeviewTab() {
        setStyle("-fx-padding: 0;");
        titleToggle.setRotate(-90);
        //header.setRotate(-90);
        //header.setSpacing(5);
        //header.setStyle("-fx-background-color: red");
        
        titleToggleIcon.setContent("M3 4 Q2.5781 4 2.2812 4.2969 Q2 4.5938 2 4.9844 L2 4.9844 L2 19 Q2 19.6094 2.3281 20.1094 Q2.6562 20.5938 3.0781 20.9531 Q3.5156 21.2969 4.0781 21.5781 Q4.6406 21.8438 5.2969 22.0781 Q6.6406 22.5312 8.3438 22.7656 Q10.0625 23 12 23 Q13.9531 23 15.6562 22.7656 Q17.375 22.5312 18.6875 22.0781 Q19.3594 21.8438 19.9219 21.5781 Q20.4844 21.2969 20.9062 20.9531 Q21.3594 20.5938 21.6875 20.1094 Q22.0156 19.6094 22.0156 19 L22.0156 4.9844 Q22.0156 4.5938 21.7188 4.2969 Q21.4219 4 21 4 L21 4 Q20.5781 4 20.2812 4.2969 Q20 4.5938 20 4.9844 L20 4.9844 L20 19 Q20 19 19.9531 19.0938 Q19.9219 19.1875 19.6875 19.375 Q19.4375 19.5625 19.0156 19.7812 Q18.6094 19.9844 18.0781 20.1719 Q16.9531 20.5469 15.375 20.7812 Q13.8125 21 12 21 Q10.2031 21 8.625 20.7812 Q7.0625 20.5469 5.9531 20.1719 Q5.3906 19.9844 4.9844 19.7812 Q4.5781 19.5625 4.3438 19.375 Q4.0781 19.1875 4.0469 19.0938 Q4.0156 19 4.0156 19 L4.0156 4.9844 Q4.0156 4.5938 3.7188 4.2969 Q3.4219 4 3 4 L3 4 L3 4 ZM3 10.9844 Q2.5781 10.9844 2.2812 11.2812 Q2 11.5781 2 12 L2 12 Q2 12.6094 2.3281 13.1094 Q2.6562 13.5938 3.0781 13.9375 Q3.5156 14.2969 4.0781 14.5781 Q4.6406 14.8594 5.2969 15.0625 Q6.6406 15.5156 8.3438 15.7656 Q10.0625 16 12 16 Q13.9531 16 15.6562 15.7656 Q17.375 15.5156 18.6875 15.0625 Q19.3594 14.8594 19.9219 14.5781 Q20.4844 14.2969 20.9062 13.9375 Q21.3594 13.5938 21.6875 13.1094 Q22.0156 12.6094 22.0156 12 Q22.0156 11.5781 21.7188 11.2812 Q21.4219 10.9844 21 10.9844 L21 10.9844 Q20.5781 10.9844 20.2812 11.2812 Q20 11.5781 20 12 L20 12 Q20 12 19.9531 12.0938 Q19.9219 12.1875 19.6875 12.375 Q19.4375 12.5625 19.0156 12.7812 Q18.6094 12.9844 18.0781 13.1719 Q16.9531 13.5469 15.375 13.7656 Q13.8125 13.9844 12 13.9844 Q10.2031 13.9844 8.625 13.7656 Q7.0625 13.5469 5.9531 13.1719 Q5.3906 12.9844 4.9844 12.7812 Q4.5781 12.5625 4.3438 12.375 Q4.0781 12.1875 4.0469 12.0938 Q4.0156 12 4.0156 12 Q4.0156 11.5781 3.7188 11.2812 Q3.4219 10.9844 3 10.9844 L3 10.9844 L3 10.9844 ZM12 1 Q10.0781 1 8.3594 1.2344 Q6.6562 1.4688 5.3281 1.9219 Q4.6719 2.1562 4.1094 2.4219 Q3.5469 2.6875 3.0938 3.0469 Q2.6562 3.3906 2.3281 3.8906 Q2 4.375 2 4.9844 Q2 5.625 2.3281 6.125 Q2.6562 6.6094 3.0938 6.9531 Q3.5469 7.3125 4.1094 7.5781 Q4.6719 7.8438 5.3281 8.0625 Q6.6562 8.5 8.3594 8.75 Q10.0781 9 12 9 Q13.9219 9 15.625 8.75 Q17.3438 8.5 18.6875 8.0625 Q19.3438 7.8438 19.9062 7.5781 Q20.4688 7.3125 20.9062 6.9531 Q21.2656 6.6719 21.5 6.3125 Q21.7344 5.9531 21.8438 5.5312 Q21.9219 5.4062 21.9688 5.2812 Q22.0156 5.1562 22.0156 4.9844 L22.0156 4.9844 Q22.0156 4.8438 21.9688 4.7031 Q21.9219 4.5625 21.8438 4.4531 L21.8438 4.4688 Q21.7344 4.0469 21.5 3.6719 Q21.2656 3.2969 20.9062 3.0469 Q20.4688 2.6875 19.9062 2.4219 Q19.3438 2.1562 18.6875 1.9219 Q17.3438 1.4688 15.625 1.2344 Q13.9219 1 12 1 L12 1 ZM12 3 Q13.8125 3 15.375 3.2344 Q16.9531 3.4688 18.0469 3.8125 Q18.6094 4 19.0156 4.2188 Q19.4375 4.4219 19.6719 4.6094 Q19.9219 4.7969 19.9531 4.8906 Q20 4.9844 20 4.9844 Q20 5.0156 19.9531 5.1094 Q19.9219 5.2031 19.6719 5.3906 Q19.4375 5.5781 19.0156 5.7812 Q18.6094 5.9688 18.0469 6.1562 Q16.9531 6.5312 15.375 6.7656 Q13.8125 7 12 7 Q10.2031 7 8.625 6.7656 Q7.0625 6.5312 5.9531 6.1562 Q5.3906 5.9688 4.9844 5.7812 Q4.5781 5.5781 4.3438 5.3906 Q4.0781 5.2031 4.0469 5.1094 Q4.0156 5.0156 4.0156 4.9844 Q4.0156 4.9844 4.0469 4.8906 Q4.0781 4.7969 4.3438 4.6094 Q4.5781 4.4219 4.9844 4.2188 Q5.3906 4 5.9531 3.8125 Q7.0625 3.4688 8.625 3.2344 Q10.2031 3 12 3 Z");
        titleToggleIcon.setScaleX(0.5);
        titleToggleIcon.setScaleY(0.5);
        titleToggleIcon.setFill(Color.valueOf("#074675"));
        titleToggleIcon.setRotate(90);
        titleToggle.setGraphic(new Group(titleToggleIcon));
        titleToggle.setFocusTraversable(false);
        titleToggle.setTooltip(new Tooltip("数据库连接"));

        //设置图标保证响应双击最大化事件
        setGraphic(header);
        titleToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    titleToggleIcon.setFill(Color.valueOf("#074675"));
                });
                getTabPane().getSelectionModel().select(this);
                ConfigManagerUtil.setProperty("DEFAULT_LISTVIEW_TAB",this.getTitle());
                for(Tab tab:getTabPane().getTabs()){
                    //tab=(CustomTreeviewTab)tab;
                    if(!((CustomTreeviewTab)tab).getTitle().equals(getTitle())){
                        log.info("!((CustomTreeviewTab)tab).getTitle().equals(getTitle())");
                        ((CustomTreeviewTab)tab).titleToggle.setSelected(false);
                        Platform.runLater(() -> {
                                ((CustomTreeviewTab)tab).titleToggleIcon.setFill(Color.valueOf("#ddd"));
                        });
                    }
                }
                if(Main.mainController!=null){
                    Main.mainController.main_splitpane.setDividerPositions(Main.split1Pos);
                    Main.sqledit_codearea_is_max=0;
                }

            } else {
                Main.mainController.main_splitpane.setDividerPositions(0);
                Main.sqledit_codearea_is_max=1;
                Platform.runLater(() -> {
                titleToggleIcon.setFill(Color.valueOf("#ddd"));
                });
            }
        });
        /*

        SVGPath newRootFolderItemIcon = new SVGPath();
        newRootFolderItemIcon.setContent("M18.4375 12.0234 Q19.6875 12.0234 20.7812 12.6484 Q21.8906 13.2734 22.5156 14.3828 Q23.1406 15.4766 23.1406 16.7266 Q23.1406 17.9766 22.5156 19.0859 Q21.8906 20.1953 20.7812 20.8203 Q19.6875 21.4297 18.4375 21.4297 Q16.4688 21.4297 15.0938 20.0703 Q13.7344 18.6953 13.7344 16.7266 Q13.7344 14.7578 15.0938 13.3984 Q16.4688 12.0234 18.4375 12.0234 ZM18.4375 13.7578 L18.3438 13.7578 Q18.0469 13.8047 18 14.0859 L18 16.3047 L15.7969 16.3047 Q15.5 16.3516 15.4531 16.6328 L15.4531 16.8203 Q15.5 17.1172 15.7969 17.1641 L18 17.1641 L18 19.3672 Q18.0469 19.6641 18.3438 19.7109 L18.5312 19.7109 Q18.8125 19.6641 18.8594 19.3672 L18.8594 17.1641 L21.0781 17.1641 Q21.3594 17.1172 21.4062 16.8203 L21.4062 16.6328 Q21.3594 16.3516 21.0781 16.3047 L18.8594 16.3047 L18.8594 14.0859 Q18.8125 13.8047 18.5312 13.7578 L18.4375 13.7578 ZM8.7344 2.5703 Q9.3594 2.5703 9.8438 2.9609 L12 4.7266 L20.3594 4.7266 Q21.0781 4.7266 21.625 5.2109 Q22.1719 5.6953 22.2656 6.4141 L22.2656 12.6953 Q21.7031 12.1797 21.0312 11.7891 L21.0312 6.6484 Q20.9688 6.4141 20.8281 6.2422 Q20.6875 6.0703 20.4531 6.0234 L12 6.0234 L9.9844 7.7109 Q9.5 8.0859 8.9219 8.1328 L3.0312 8.1328 L3.0312 17.7891 Q3.0312 18.0234 3.1719 18.2266 Q3.3125 18.4141 3.5469 18.4609 L13.1094 18.4609 Q13.3438 19.1328 13.7344 19.7578 L3.6406 19.7578 Q2.875 19.7578 2.3281 19.2266 Q1.7812 18.6953 1.7344 17.9297 L1.7344 4.5391 Q1.7344 3.7734 2.2344 3.2266 Q2.7344 2.6641 3.5 2.6172 L8.7344 2.5703 ZM8.7344 3.8672 L3.6406 3.8672 Q3.4062 3.8672 3.2188 4.0391 Q3.0312 4.2109 3.0312 4.4453 L3.0312 6.8984 L8.7344 6.8984 Q8.9219 6.8984 9.0781 6.7891 L10.7969 5.3516 L9.1719 4.0078 Q9.0312 3.9141 8.8281 3.8672 L8.7344 3.8672 Z");
        newRootFolderItemIcon.setScaleX(0.7);
        newRootFolderItemIcon.setScaleY(0.7);
        newRootFolderItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem newRootFolderItem = new MenuItem("新建文件夹 ( New Folder )",new Group(newRootFolderItemIcon));
        newRootFolderItem.setOnAction(e -> {
            MarkdownUtil.createNewFile(MarkdownUtil.treeView.getRoot(), true);
        });

        SVGPath newFileItemIcon = new SVGPath();
        newFileItemIcon.setContent("M12 9.7656 Q12.3125 9.7656 12.5156 9.9688 Q12.7188 10.1562 12.7188 10.4844 L12.7188 12.7188 L15.0469 12.7188 Q15.2812 12.7188 15.5156 12.9688 Q15.7656 13.2031 15.7656 13.5312 Q15.7656 13.8438 15.5156 14.0469 Q15.2812 14.2344 15.0469 14.2344 L12.7188 14.2344 L12.7188 16.4844 Q12.7188 16.7969 12.5156 17.0469 Q12.3125 17.2812 12 17.2812 Q11.6875 17.2812 11.4844 17.0469 Q11.2812 16.7969 11.2812 16.4844 L11.2812 14.2344 L9.0469 14.2344 Q8.7188 14.2344 8.4688 14.0469 Q8.2344 13.8438 8.2344 13.5312 Q8.2344 13.2031 8.4688 12.9688 Q8.7188 12.7188 9.0469 12.7188 L11.2812 12.7188 L11.2812 10.4844 Q11.2812 10.1562 11.4844 9.9688 Q11.6875 9.7656 12 9.7656 ZM21.0469 6.7188 L21.0469 20.9531 Q21.0469 22.2344 20.1562 23.125 Q19.2812 24 18 24 L6 24 Q4.7188 24 3.8281 23.125 Q2.9531 22.2344 2.9531 21.0469 L2.9531 2.9531 Q3.0469 1.7656 3.875 0.8906 Q4.7188 0 6 0 L14.2344 0 L21.0469 6.7188 ZM16.4844 6.7188 Q15.5938 6.7188 14.9062 6.0781 Q14.2344 5.4375 14.2344 4.4844 L14.2344 1.5156 L6 1.5156 Q5.3594 1.5156 4.9219 1.9688 Q4.4844 2.4062 4.4844 2.9531 L4.4844 20.9531 Q4.4844 21.5938 4.9219 22.0469 Q5.3594 22.4844 6 22.4844 L18 22.4844 Q18.6406 22.4844 19.0781 22.0469 Q19.5156 21.5938 19.5156 20.9531 L19.5156 6.7188 L16.4844 6.7188 Z");
        newFileItemIcon.setScaleX(0.65);
        newFileItemIcon.setScaleY(0.6);
        newFileItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem newFileItem = new MenuItem("新建文件 ( New File )",new Group(newFileItemIcon));
        newFileItem.setOnAction(e -> {
            MarkdownUtil.createNewFile(MarkdownUtil.treeView.getRoot(), false);
        });

        SVGPath copyItemIcon = new SVGPath();
        copyItemIcon.setScaleX(0.65);
        copyItemIcon.setScaleY(0.65);
        copyItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        copyItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath refreshItemIcon = new SVGPath();
        refreshItemIcon.setContent("M17.6719 6.3281 L20.0156 3.9844 L20.0156 11.0156 L12.9844 11.0156 L16.2188 7.7812 Q15.375 6.9375 14.2969 6.4688 Q13.2188 6 12 6 Q10.3594 6 8.9688 6.7969 Q7.5938 7.5938 6.7969 8.9844 Q6 10.3594 6 12 Q6 13.6406 6.7969 15.0312 Q7.5938 16.4062 8.9688 17.2031 Q10.3594 18 12 18 L12 18 Q13.7344 18 15.3906 16.8281 Q17.0625 15.6562 17.6719 14.0156 L19.7344 14.0156 Q19.0312 16.6406 16.8906 18.3281 Q14.7656 20.0156 12 20.0156 Q9.8438 20.0156 7.9844 18.9375 Q6.1406 17.8594 5.0781 16.0156 Q4.0312 14.1562 4.0312 12 Q4.0312 9.8438 5.0781 8 Q6.1406 6.1406 7.9844 5.0625 Q9.8438 3.9844 12 3.9844 L12 3.9844 Q13.3594 3.9844 15.0156 4.6875 Q16.6875 5.3906 17.6719 6.3281 Z");
        refreshItemIcon.setScaleX(0.7);
        refreshItemIcon.setScaleY(0.7);
        refreshItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath pasteItemIcon = new SVGPath();
        pasteItemIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        pasteItemIcon.setScaleX(0.65);
        pasteItemIcon.setScaleY(0.65);
        pasteItemIcon.setFill(Color.valueOf("#074675"));

        MenuItem copyItem = new MenuItem("复制根目录 ( Copy Root Dir )",new Group(copyItemIcon));
        MenuItem pasteItem = new MenuItem("粘贴 ( Paste )",new Group(pasteItemIcon));
        MenuItem refreshItem = new MenuItem("刷新 ( Refresh)",new Group(refreshItemIcon));

        copyItem.setOnAction(event -> {
            ObservableList<TreeItem<Markdown>> observableList= FXCollections.observableArrayList();
            observableList.add(MarkdownUtil.treeView.getRoot());
            MarkdownUtil.copyFiles(observableList);
        });
        refreshItem.setOnAction(
                event -> {
                    MarkdownUtil.refreshNode(MarkdownUtil.treeView.getRoot());
                }
        );
        pasteItem.setOnAction(event -> {
            MarkdownUtil.pasteFiles(MarkdownUtil.treeView.getRoot());
        });
        contextMenu.getItems().addAll(newFileItem,newRootFolderItem,copyItem,pasteItem,refreshItem);


         */
        /*
        titleLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if(Main.sqledit_codearea_is_max==1){
                    Main.mainController.main_splitpane.setDividerPositions(Main.split1Pos);
                    Main.sqledit_codearea_is_max=0;
                }else{
                    Main.mainController.main_splitpane.setDividerPositions(0);
                    Main.sqledit_codearea_is_max=1;
                }
            }
        });

         */


    }
    public String getTitle(){
        return getText();
    }
    public void setTitle(String title){
        //titleToggle.setText(title);
        setText(title);
        //setGraphic(header);
    }

}
