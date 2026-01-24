package com.dbboys.customnode;


import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


public class CustomInfoCodeArea extends CodeArea {
    public CustomInfoCodeArea() {
        super();
        setEditable(false);
        SVGPath instance_info_codearea_menu_copy_icon = new SVGPath();
        instance_info_codearea_menu_copy_icon.setScaleX(0.7);
        instance_info_codearea_menu_copy_icon.setScaleY(0.7);
        instance_info_codearea_menu_copy_icon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        instance_info_codearea_menu_copy_icon.setFill(Color.valueOf("#074675"));
        MenuItem instance_info_codearea_menu_copy = new MenuItem("复制 ( Copy )                                Ctrl+C");
        instance_info_codearea_menu_copy.setGraphic(new Group(instance_info_codearea_menu_copy_icon));

        ContextMenu instance_info_codearea_menu = new ContextMenu();
        setContextMenu(instance_info_codearea_menu);

        instance_info_codearea_menu_copy.setDisable(true);
        instance_info_codearea_menu.getItems().add(instance_info_codearea_menu_copy);

        selectionProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection.getLength()==0) {
                instance_info_codearea_menu_copy.setDisable(true);
            }else{
                instance_info_codearea_menu_copy.setDisable(false);
            }
        });
        instance_info_codearea_menu_copy.setOnAction(event -> {
            copy();
        });

        setParagraphGraphicFactory(LineNumberFactory.get(this));

    }
}
