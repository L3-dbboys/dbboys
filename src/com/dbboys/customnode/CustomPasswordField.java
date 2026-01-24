package com.dbboys.customnode;

import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;


public class CustomPasswordField extends PasswordField {


    public CustomPasswordField() {
        super();

        SVGPath PasteIcon = new SVGPath();
        PasteIcon.setScaleX(0.65);
        PasteIcon.setScaleY(0.65);
        PasteIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        PasteIcon.setFill(Color.valueOf("#074675"));
        MenuItem pasteItem = new MenuItem("粘贴 ( Paste )                                Ctrl+V");
        pasteItem.setGraphic(new Group(PasteIcon));

        ContextMenu connectMenu = new ContextMenu();
        connectMenu.getItems().addAll(pasteItem);
        setContextMenu(connectMenu);
        pasteItem.setOnAction(event -> {
            paste();
        });

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {  // 判断是否是右键
                if (Clipboard.getSystemClipboard().hasString()) {
                    pasteItem.setDisable(false);
                }else{
                    pasteItem.setDisable(true);
                }

            }
        });
    }

}
