package com.dbboys.customnode;


import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomMarkdownEditCodeArea extends CodeArea {
    private static final Logger log = LogManager.getLogger(CustomMarkdownEditCodeArea.class);
    public Button sql_save_button=new Button();  //保存文件路径
    public CustomSearchReplaceVbox searchReplaceBox;
    public MenuItem viewItem = new  MenuItem("保存并预览 ( Save&View )      Ctrl+Enter");
    public MenuItem codeAreaPasteItem;
    public CustomMarkdownEditCodeArea() {
        super();
        SVGPath viewItemIcon = new SVGPath();
        viewItemIcon.setScaleX(0.6);
        viewItemIcon.setScaleY(0.6);
        viewItemIcon.setContent("M4.5156 1.5312 L21 1.5312 L22.5156 3.0469 L22.5156 10.9844 Q21.7969 10.6562 21 10.5312 L21 3.0469 L13.5156 3.0469 L13.5156 19.3594 L11.8281 21.0469 L4.5156 21.0469 L3 19.5312 L3 3.0469 L4.5156 1.5312 ZM4.5156 19.5312 L12 19.5312 L12 3.0469 L4.5156 3.0469 L4.5156 19.5312 ZM20.0625 12.0469 Q19.1094 12.0938 18.2812 12.5781 L18.2344 12.5781 Q17.5938 12.9531 17.1406 13.5781 Q16.6875 14.2031 16.5156 14.9688 Q16.3438 15.7188 16.4688 16.4844 Q16.6094 17.2344 17.0469 17.875 L13.5156 21.3906 L14.5625 22.4688 L18.0938 18.9062 Q18.8438 19.4219 19.7188 19.5312 Q20.5938 19.625 21.4375 19.3281 Q22.2969 19.0312 22.9219 18.3906 Q23.5469 17.7344 23.7969 16.8906 Q24.0469 16.0312 23.9062 15.1562 Q23.7656 14.2812 23.2188 13.5625 Q22.6875 12.8438 21.8906 12.4375 Q21.0312 12.0156 20.0625 12.0469 ZM20.4219 18.0469 Q19.625 18.125 18.9531 17.6562 Q18.3906 17.2812 18.125 16.6562 Q17.8594 16.0312 17.9844 15.3594 Q18.125 14.6875 18.6094 14.2188 Q19.1094 13.7344 19.7656 13.6094 Q20.4219 13.4688 21.0625 13.7344 Q21.7031 13.9844 22.0781 14.5469 Q22.4219 15.0469 22.4531 15.6406 Q22.4844 16.2188 22.2344 16.7656 Q21.9844 17.2969 21.5 17.6406 Q21.0312 17.9688 20.4219 18.0469 Z");
        viewItemIcon.setFill(Color.valueOf("#074675"));
        viewItem.setGraphic(new Group(viewItemIcon));

        SVGPath codeAreaSearchItemIcon = new SVGPath();
        codeAreaSearchItemIcon.setScaleX(0.6);
        codeAreaSearchItemIcon.setScaleY(0.6);
        codeAreaSearchItemIcon.setContent("M9.8438 1.7184 Q12.0469 1.7184 13.9219 2.7965 Q15.7969 3.8746 16.8906 5.7496 Q18 7.609 18 9.8278 Q18 12.4684 16.4688 14.6246 L21.8906 20.0934 Q22.2656 20.4371 22.2812 20.9684 Q22.3125 21.484 21.9531 21.8746 Q21.5938 22.2496 21.0938 22.2809 Q20.5938 22.2965 20.2031 21.9684 L14.6406 16.4528 Q12.4844 17.984 9.8438 17.984 Q7.625 17.984 5.75 16.8903 Q3.8906 15.7809 2.8125 13.9059 Q1.7344 12.0309 1.7344 9.8278 Q1.7344 7.609 2.8125 5.7496 Q3.8906 3.8746 5.75 2.7965 Q7.625 1.7184 9.8438 1.7184 ZM9.8438 4.2496 Q8.3594 4.2496 7.0625 4.9996 Q5.7656 5.7496 5.0156 7.0465 Q4.2656 8.3434 4.2656 9.8278 Q4.2656 11.3121 5.0156 12.609 Q5.7656 13.9059 7.0625 14.6559 Q8.3594 15.3903 9.8594 15.3903 Q11.375 15.3903 12.6406 14.6559 Q13.9219 13.9059 14.6562 12.6403 Q15.4062 11.359 15.4062 9.859 Q15.4062 8.3434 14.6562 7.0778 Q13.9219 5.7965 12.6406 5.0309 Q11.375 4.2496 9.8438 4.2496 Z");
        codeAreaSearchItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaSearchItem = new  MenuItem("查找/替换 ( Search )                  Ctrl+F/R");
        codeAreaSearchItem.setGraphic(new Group(codeAreaSearchItemIcon));

        SVGPath codeAreaCopyIcon = new SVGPath();
        codeAreaCopyIcon.setScaleX(0.7);
        codeAreaCopyIcon.setScaleY(0.7);
        codeAreaCopyIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        codeAreaCopyIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaCopyItem = new  MenuItem("复制 ( Copy )                                Ctrl+C");
        codeAreaCopyItem.setGraphic(new Group(codeAreaCopyIcon));

        SVGPath codeAreaCutIcon = new SVGPath();
        codeAreaCutIcon.setScaleX(0.60);
        codeAreaCutIcon.setScaleY(0.60);
        codeAreaCutIcon.setContent("M9.6562 7.6406 L21.9844 20.0156 L21.9844 21 L18.9844 21 L12 14.0156 L9.6562 16.3594 Q9.9844 17.1562 9.9844 18 Q9.9844 19.6406 8.8125 20.8125 Q7.6406 21.9844 6 21.9844 Q4.3594 21.9844 3.1875 20.8125 Q2.0156 19.6406 2.0156 18 Q2.0156 16.3594 3.1875 15.1875 Q4.3594 14.0156 6 14.0156 Q6.8438 14.0156 7.6406 14.3438 L9.9844 12 L7.6406 9.6562 Q6.8438 9.9844 6 9.9844 Q4.3594 9.9844 3.1875 8.8125 Q2.0156 7.6406 2.0156 6 Q2.0156 4.3594 3.1875 3.1875 Q4.3594 2.0156 6 2.0156 Q7.6406 2.0156 8.8125 3.1875 Q9.9844 4.3594 9.9844 6 Q9.9844 6.8438 9.6562 7.6406 ZM6 8.0156 Q6.8438 8.0156 7.4219 7.4375 Q8.0156 6.8438 8.0156 6 Q8.0156 5.1562 7.4219 4.5781 Q6.8438 3.9844 6 3.9844 Q5.1562 3.9844 4.5625 4.5781 Q3.9844 5.1562 3.9844 6 Q3.9844 6.8438 4.5625 7.4375 Q5.1562 8.0156 6 8.0156 ZM6 20.0156 Q6.8438 20.0156 7.4219 19.4375 Q8.0156 18.8438 8.0156 18 Q8.0156 17.1562 7.4219 16.5781 Q6.8438 15.9844 6 15.9844 Q5.1562 15.9844 4.5625 16.5781 Q3.9844 17.1562 3.9844 18 Q3.9844 18.8438 4.5625 19.4375 Q5.1562 20.0156 6 20.0156 ZM12 12.5156 Q12.1875 12.5156 12.3438 12.3594 Q12.5156 12.1875 12.5156 12 Q12.5156 11.8125 12.3438 11.6562 Q12.1875 11.4844 12 11.4844 Q11.8125 11.4844 11.6406 11.6562 Q11.4844 11.8125 11.4844 12 Q11.4844 12.1875 11.6406 12.3594 Q11.8125 12.5156 12 12.5156 ZM18.9844 3 L21.9844 3 L21.9844 3.9844 L15 11.0156 L12.9844 9 L18.9844 3 Z");
        codeAreaCutIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaCutItem = new  MenuItem("剪切 ( Cut )                                   Ctrl+X");
        codeAreaCutItem.setGraphic(new Group(codeAreaCutIcon));

        SVGPath codeAreaCopyAllIcon = new SVGPath();
        codeAreaCopyAllIcon.setScaleX(0.7);
        codeAreaCopyAllIcon.setScaleY(0.7);
        codeAreaCopyAllIcon.setContent("M7 6 L7 3 Q7 2.5781 7.2812 2.2969 Q7.5781 2 8 2 L20 2 Q20.4219 2 20.7031 2.2969 Q21 2.5781 21 3 L21 17 Q21 17.4219 20.7031 17.7188 Q20.4219 18 20 18 L17 18 L17 21 Q17 21.4219 16.7031 21.7188 Q16.4219 22 16 22 L4 22 Q3.5938 22 3.2969 21.7188 Q3 21.4219 3 21 L3 7 Q3 6.5781 3.2969 6.2969 Q3.5938 6 4 6 L7 6 ZM5 8 L5 20 L15 20 L15 8 L5 8 ZM9 6 L17 6 L17 16 L19 16 L19 4 L9 4 L9 6 ZM7 11 L13 11 L13 13 L7 13 L7 11 ZM7 15 L13 15 L13 17 L7 17 L7 15 Z");
        codeAreaCopyAllIcon.setFill(Color.valueOf("#074675"));

        SVGPath codeAreaPasteIcon = new SVGPath();
        codeAreaPasteIcon.setScaleX(0.65);
        codeAreaPasteIcon.setScaleY(0.65);
        codeAreaPasteIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        codeAreaPasteIcon.setFill(Color.valueOf("#074675"));
        codeAreaPasteItem = new MenuItem("粘贴 ( Paste )                                Ctrl+V");
        codeAreaPasteItem.setGraphic(new Group(codeAreaPasteIcon));

        SVGPath codeAreaUndoIcon = new SVGPath();
        codeAreaUndoIcon.setScaleX(0.60);
        codeAreaUndoIcon.setScaleY(0.60);
        codeAreaUndoIcon.setContent("M14.3906 6.6094 L8.3906 6.6094 L8.3906 3 L1.2188 7.8281 L8.3906 12.6094 L8.3906 9 L14.3906 9 Q16.3594 9 17.7812 10.4062 Q19.2188 11.8125 19.2188 13.8125 Q19.2188 15.7969 17.7812 17.2031 Q16.3594 18.6094 14.3906 18.6094 L8.3906 18.6094 L8.3906 21 L14.3906 21 Q16.3594 21 18 20.0469 Q19.6406 19.0781 20.625 17.4219 Q21.6094 15.75 21.6094 13.8125 Q21.6094 11.8594 20.625 10.2031 Q19.6406 8.5312 18 7.5781 Q16.3594 6.6094 14.3906 6.6094 Z");
        codeAreaUndoIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaUndoItem = new MenuItem("撤销 ( Undo )                                Ctrl+Z");
        codeAreaUndoItem.setGraphic(new Group(codeAreaUndoIcon));

        SVGPath codeAreaRedoIcon = new SVGPath();
        codeAreaRedoIcon.setScaleX(0.60);
        codeAreaRedoIcon.setScaleY(0.60);
        codeAreaRedoIcon.setContent("M9.6094 6.6094 L15.6094 6.6094 L15.6094 3 L22.7812 7.8281 L15.6094 12.6094 L15.6094 9 L9.6094 9 Q7.6406 9 6.2031 10.4062 Q4.7812 11.8125 4.7812 13.8125 Q4.7812 15.7969 6.2031 17.2031 Q7.6406 18.6094 9.6094 18.6094 L15.6094 18.6094 L15.6094 21 L9.6094 21 Q7.6406 21 6 20.0469 Q4.3594 19.0781 3.375 17.4219 Q2.3906 15.75 2.3906 13.8125 Q2.3906 11.8594 3.375 10.2031 Q4.3594 8.5312 6 7.5781 Q7.6406 6.6094 9.6094 6.6094 Z");
        codeAreaRedoIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaRedoItem = new MenuItem("重做 ( Redo )                                Ctrl+Y");
        codeAreaRedoItem.setGraphic(new Group(codeAreaRedoIcon));

        SVGPath codeAreaSaveIcon = new SVGPath();
        codeAreaSaveIcon.setScaleX(0.60);
        codeAreaSaveIcon.setScaleY(0.60);
        codeAreaSaveIcon.setContent("M20.3438 6.0938 Q21 6.75 21 7.6875 L21 20.25 Q21 21.1875 20.3438 21.8438 Q19.6875 22.5 18.75 22.5 L2.25 22.5 Q1.3125 22.5 0.6562 21.8438 Q0 21.1875 0 20.25 L0 3.75 Q0 2.8125 0.6562 2.1562 Q1.3125 1.5 2.25 1.5 L14.8125 1.5 Q15.75 1.5 16.4062 2.1562 L20.3438 6.0938 ZM8.3594 18.6406 Q9.2344 19.5 10.5 19.5 Q11.7656 19.5 12.625 18.6406 Q13.5 17.7656 13.5 16.5 Q13.5 15.2344 12.625 14.375 Q11.7656 13.5 10.5 13.5 Q9.2344 13.5 8.3594 14.375 Q7.5 15.2344 7.5 16.5 Q7.5 17.7656 8.3594 18.6406 ZM15 5.2031 Q15 5.0156 14.8125 4.8281 L14.6719 4.6875 Q14.4844 4.5 14.2969 4.5 L3.5625 4.5 Q3 4.5 3 5.0625 L3 9.9375 Q3 10.5 3.5625 10.5 L14.4375 10.5 Q15 10.5 15 9.9375 L15 5.2031 Z");
        codeAreaSaveIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaSaveItem = new MenuItem("保存 ( Save )                                 Ctrl+S");
        codeAreaSaveItem.setGraphic(new Group(codeAreaSaveIcon));

        ContextMenu codeAreaMenu = new ContextMenu();
        codeAreaMenu.getItems().addAll(viewItem,codeAreaSearchItem,codeAreaCopyItem,codeAreaCutItem,codeAreaPasteItem,codeAreaUndoItem,codeAreaRedoItem,codeAreaSaveItem);
        setContextMenu(codeAreaMenu);

        codeAreaSearchItem.setOnAction(event->{
            searchReplaceBox.setVisible(true);
        });

        codeAreaCopyItem.setDisable(true);

        codeAreaCopyItem.setOnAction(event -> {
            copy();
        });
        codeAreaCutItem.setOnAction(event -> {
            cut();
        });


        codeAreaUndoItem.setOnAction(event -> {
            undo();
        });
        codeAreaRedoItem.setOnAction(event -> {
            redo();
        });


        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                event.consume();   // 🔥 阻止默认粘贴
                codeAreaPasteItem.fire();
            }
        });

        // ctrl+s保存
        setOnKeyPressed(event -> {
            if(event.isControlDown()&&event.getCode() == KeyCode.ENTER){
                viewItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.S){
                sql_save_button.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.F){
                codeAreaSearchItem.fire();
                searchReplaceBox.findField.requestFocus();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.R){
                codeAreaSearchItem.fire();
                if(searchReplaceBox.tobottomBtn.isVisible()){
                    searchReplaceBox.tobottomBtn.fire();
                    searchReplaceBox.findField.requestFocus();
                }
            }
        });
        setOnContextMenuRequested((ContextMenuEvent event) -> {
            if(sql_save_button.isDisable()){
                codeAreaSaveItem.setDisable(true);
            }else{
                codeAreaSaveItem.setDisable(false);
            }

            if(getSelectedText().isEmpty()){
                codeAreaCopyItem.setDisable(true);
                codeAreaCutItem.setDisable(true);
            }else{
                codeAreaCopyItem.setDisable(false);
                codeAreaCutItem.setDisable(false);
            }

            Clipboard clipboard = Clipboard.getSystemClipboard();
            if(clipboard.hasString()){
                //codeAreaPasteItem.setDisable(false);
            }else{
                //codeAreaPasteItem.setDisable(true);
            }

        });
        //设置行号
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        codeAreaSaveItem.setOnAction(event->{
            sql_save_button.fire();
        });

        //监听输入变化
        richChanges()
                //.filter(change ->  !change.isPlainTextIdentity()) // 只处理文本的变化,否则setStyleSpans也会触发事件
                .subscribe(change -> {
                    sql_save_button.setDisable(false);
                });
    }
}
