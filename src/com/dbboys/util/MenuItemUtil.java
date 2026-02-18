package com.dbboys.util;

import com.dbboys.customnode.CustomShortcutMenuItem;
import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MenuItemUtil {
    private static final String DEFAULT_COLOR = "#074675";


    public MenuItemUtil() {}



    public static CustomShortcutMenuItem createMenuItemI18n(String key, Node graphic) {
        return createMenuItemI18n(key, null, graphic);
    }

    public static CustomShortcutMenuItem createMenuItemI18n(String key, String shortcut, Node graphic) {
        CustomShortcutMenuItem menuItem = createShortcutMenuItem("", shortcut);
        menuItem.textProperty().bind(I18n.bind(key));
        menuItem.setGraphic(graphic == null ? new StackPane() : graphic);
        return menuItem;
    }

    private static CustomShortcutMenuItem createShortcutMenuItem(String text, String shortcut) {
        return new CustomShortcutMenuItem(
                text,
                shortcut == null ? "" : shortcut
        );
    }


    public static Button createModifyButton(String tooltipText) {
        Button button = new Button();
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
        button.setGraphic(IconFactory.group(IconPaths.RESULTSET_EDITABLE, 0.4, Color.valueOf(DEFAULT_COLOR)));
        button.getStyleClass().add("little-custom-button");
        button.setStyle("-fx-padding: 1 3 1 3");
        button.setFocusTraversable(false);
        return button;
    }



}
