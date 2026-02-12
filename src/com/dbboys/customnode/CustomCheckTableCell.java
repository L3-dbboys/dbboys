package com.dbboys.customnode;

import javafx.scene.paint.Color;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.scene.Group;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;

//巡检结论一列自定义格式
public class CustomCheckTableCell<S,T> extends CustomTableCell<S,T> {

    private static final String STATUS_OK = "0";
    private static final String STATUS_WARN = "1";
    private static final String LABEL_OK = "正常";
    private static final String LABEL_WARN = "关注";
    private static final String LABEL_ERROR = "异常";
    private static final ResourceBundle I18N = loadBundle();

    private final Group groupOk;
    private final Group groupWarn;
    private final Group groupError;

    public CustomCheckTableCell(){
        groupOk = IconFactory.group(IconPaths.CHECK_OK, 0.65, Color.valueOf("#074675"));
        groupWarn = IconFactory.group(IconPaths.CHECK_WARN, 0.5, Color.valueOf("#ffbf00"));
        groupError = IconFactory.group(IconPaths.CHECK_ERROR, 0.05, Color.valueOf("#cf2311"));
    }

    private static ResourceBundle loadBundle() {
        try {
            return ResourceBundle.getBundle("com.dbboys.i18n.messages");
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private static String i18n(String key, String fallback) {
        if (I18N == null) {
            return fallback;
        }
        try {
            String value = I18N.getString(key);
            return value == null || value.isEmpty() ? fallback : value;
        } catch (MissingResourceException e) {
            return fallback;
        }
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        String value = String.valueOf(item);
        if (STATUS_OK.equals(value)) {
            setGraphic(groupOk);
            setText(i18n("check.status.ok", LABEL_OK));
        } else if (STATUS_WARN.equals(value)) {
            setGraphic(groupWarn);
            setText(i18n("check.status.warn", LABEL_WARN));
        } else {
            setGraphic(groupError);
            setText(i18n("check.status.error", LABEL_ERROR));
        }
    }

}
