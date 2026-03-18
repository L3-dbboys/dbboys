package com.dbboys.customnode;

import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.util.MenuItemUtil;
import com.dbboys.util.NotificationUtil;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


public class CustomInfoCodeArea extends CodeArea {
    public CustomInfoCodeArea() {
        super();
        setEditable(false);

        CustomShortcutMenuItem copyMenuItem = MenuItemUtil.createMenuItemI18n(
                "genericstyled.menu.copy",
                "Ctrl+C",
                IconFactory.group(IconPaths.COPY, 0.7)
        );

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(copyMenuItem);
        setContextMenu(contextMenu);

        copyMenuItem.setOnAction(event -> {
            
            if(getSelection().getLength() == 0){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(getText());
                clipboard.setContent(content);
                NotificationUtil.showMainNotification(I18n.t("resultset.notice.copied"));
            }else{
                copy();
            }
        });
        //contextMenu.setOnShowing(event -> copyMenuItem.setDisable(getSelection().getLength() == 0));

        setParagraphGraphicFactory(LineNumberFactory.get(this));

    }
}
