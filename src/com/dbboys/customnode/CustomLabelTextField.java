package com.dbboys.customnode;

import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.util.MenuItemUtil;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class CustomLabelTextField extends TextField {
    private static final double MIN_PREF_WIDTH = 40;
    private static final double EXTRA_WIDTH = 20;
    private final Text measuringText = new Text();

    public CustomLabelTextField() {
        super();
        setFocusTraversable(false);
        CustomShortcutMenuItem copyMenuItem = MenuItemUtil.createMenuItemI18n(
                "genericstyled.menu.copy",
                "Ctrl+C",
                IconFactory.group(IconPaths.COPY, 0.7)
        );
        copyMenuItem.setOnAction(event -> copy());

        ContextMenu contextMenu = new ContextMenu(copyMenuItem);
        contextMenu.setOnShowing(event -> copyMenuItem.setDisable(getSelectedText().isEmpty()));
        setContextMenu(contextMenu);

        textProperty().addListener((observable, oldValue, newValue) -> updatePrefWidth());
        fontProperty().addListener((observable, oldValue, newValue) -> updatePrefWidth());
        updatePrefWidth();

        getStyleClass().add("label-text");
        setEditable(false);
    }

    private void updatePrefWidth() {
        measuringText.setFont(getFont());
        measuringText.setText(getText() == null ? "" : getText());
        double textWidth = measuringText.getBoundsInLocal().getWidth();
        setPrefWidth(Math.max(MIN_PREF_WIDTH, textWidth + EXTRA_WIDTH));
    }

}
