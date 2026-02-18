package com.dbboys.customnode;


import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.util.*;
import javafx.scene.control.ContextMenu;

import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;




public class CustomInlineCssTextArea extends InlineCssTextArea {
    private CustomShortcutMenuItem copyItem ;
    public ContextMenu inlineCssMenu = new ContextMenu();

    public CustomInlineCssTextArea() {
        super();
        setEditable(false);
        copyItem = MenuItemUtil.createMenuItemI18n(
                "genericstyled.menu.copy",
                "Ctrl+C",
                IconFactory.group(IconPaths.COPY, 0.7)
        );

        setContextMenu(inlineCssMenu);

        inlineCssMenu.getItems().addAll(copyItem);
        inlineCssMenu.setOnShowing((event) -> {
            if(getSelectedText().isEmpty()){
                copyItem.setDisable(true);
            }else{
                copyItem.setDisable(false);
            }
        });
        copyItem.setOnAction(event -> {
            if(!getSelectedText().isEmpty()){
                copy();
            }
        });

        setParagraphGraphicFactory(LineNumberFactory.get(this));


    }



}
