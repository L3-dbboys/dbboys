package com.dbboys.customnode;

import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import javafx.scene.input.Clipboard;

import java.util.Stack;


public class CustomUserTextField extends TextField {
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();

    public CustomUserTextField() {
        //避免与CustomTextField重名
        super();
        SVGPath copyIcon = new SVGPath();
        copyIcon.setScaleX(0.7);
        copyIcon.setScaleY(0.7);
        copyIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        copyIcon.setFill(Color.valueOf("#074675"));
        MenuItem copyItem = new MenuItem("复制 ( Copy )                                Ctrl+C");
        copyItem.setGraphic(new Group(copyIcon));

        SVGPath cutIcon = new SVGPath();
        cutIcon.setScaleX(0.65);
        cutIcon.setScaleY(0.65);
        cutIcon.setContent("M9.6562 7.6406 L21.9844 20.0156 L21.9844 21 L18.9844 21 L12 14.0156 L9.6562 16.3594 Q9.9844 17.1562 9.9844 18 Q9.9844 19.6406 8.8125 20.8125 Q7.6406 21.9844 6 21.9844 Q4.3594 21.9844 3.1875 20.8125 Q2.0156 19.6406 2.0156 18 Q2.0156 16.3594 3.1875 15.1875 Q4.3594 14.0156 6 14.0156 Q6.8438 14.0156 7.6406 14.3438 L9.9844 12 L7.6406 9.6562 Q6.8438 9.9844 6 9.9844 Q4.3594 9.9844 3.1875 8.8125 Q2.0156 7.6406 2.0156 6 Q2.0156 4.3594 3.1875 3.1875 Q4.3594 2.0156 6 2.0156 Q7.6406 2.0156 8.8125 3.1875 Q9.9844 4.3594 9.9844 6 Q9.9844 6.8438 9.6562 7.6406 ZM6 8.0156 Q6.8438 8.0156 7.4219 7.4375 Q8.0156 6.8438 8.0156 6 Q8.0156 5.1562 7.4219 4.5781 Q6.8438 3.9844 6 3.9844 Q5.1562 3.9844 4.5625 4.5781 Q3.9844 5.1562 3.9844 6 Q3.9844 6.8438 4.5625 7.4375 Q5.1562 8.0156 6 8.0156 ZM6 20.0156 Q6.8438 20.0156 7.4219 19.4375 Q8.0156 18.8438 8.0156 18 Q8.0156 17.1562 7.4219 16.5781 Q6.8438 15.9844 6 15.9844 Q5.1562 15.9844 4.5625 16.5781 Q3.9844 17.1562 3.9844 18 Q3.9844 18.8438 4.5625 19.4375 Q5.1562 20.0156 6 20.0156 ZM12 12.5156 Q12.1875 12.5156 12.3438 12.3594 Q12.5156 12.1875 12.5156 12 Q12.5156 11.8125 12.3438 11.6562 Q12.1875 11.4844 12 11.4844 Q11.8125 11.4844 11.6406 11.6562 Q11.4844 11.8125 11.4844 12 Q11.4844 12.1875 11.6406 12.3594 Q11.8125 12.5156 12 12.5156 ZM18.9844 3 L21.9844 3 L21.9844 3.9844 L15 11.0156 L12.9844 9 L18.9844 3 Z");
        cutIcon.setFill(Color.valueOf("#074675"));
        MenuItem cutItem = new MenuItem("剪切 ( Cut )                                   Ctrl+X");
        cutItem.setGraphic(new Group(cutIcon));

        SVGPath PasteIcon = new SVGPath();
        PasteIcon.setScaleX(0.65);
        PasteIcon.setScaleY(0.65);
        PasteIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        PasteIcon.setFill(Color.valueOf("#074675"));
        MenuItem pasteItem = new MenuItem("粘贴 ( Paste )                                Ctrl+V");
        pasteItem.setGraphic(new Group(PasteIcon));

        SVGPath undoIcon = new SVGPath();
        undoIcon.setScaleX(0.65);
        undoIcon.setScaleY(0.65);
        undoIcon.setContent("M14.3906 6.6094 L8.3906 6.6094 L8.3906 3 L1.2188 7.8281 L8.3906 12.6094 L8.3906 9 L14.3906 9 Q16.3594 9 17.7812 10.4062 Q19.2188 11.8125 19.2188 13.8125 Q19.2188 15.7969 17.7812 17.2031 Q16.3594 18.6094 14.3906 18.6094 L8.3906 18.6094 L8.3906 21 L14.3906 21 Q16.3594 21 18 20.0469 Q19.6406 19.0781 20.625 17.4219 Q21.6094 15.75 21.6094 13.8125 Q21.6094 11.8594 20.625 10.2031 Q19.6406 8.5312 18 7.5781 Q16.3594 6.6094 14.3906 6.6094 Z");
        undoIcon.setFill(Color.valueOf("#074675"));
        MenuItem undoItem = new MenuItem("撤销(Undo)            (Ctrl+Z)");
        undoItem.setGraphic(new Group(undoIcon));

        ContextMenu connectMenu = new ContextMenu();
        connectMenu.getItems().addAll(copyItem,cutItem,pasteItem);
        setContextMenu(connectMenu);
        copyItem.setOnAction(event -> {
            copy();
        });
        cutItem.setOnAction(event -> {
            cut();
        });
        pasteItem.setOnAction(event -> {
            paste();
        });



        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {  // 判断是否是右键
                if(getSelectedText().equals("")){
                    copyItem.setDisable(true);
                    cutItem.setDisable(true);
                }else{
                    copyItem.setDisable(false);
                    cutItem.setDisable(false);
                }
                if (Clipboard.getSystemClipboard().hasString()) {
                    pasteItem.setDisable(false);
                }else{
                    pasteItem.setDisable(true);
                }

            }
        });
    }

}
