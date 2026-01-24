package com.dbboys.util;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class MenuItemUtil {
    public MenuItemUtil() {}
    public static MenuItem createCopyMenuItem(boolean hasControlKey) {
        MenuItem menuItem = new MenuItem();
        if (hasControlKey) {
            menuItem.setText("复制 ( Copy )                                Ctrl+C");
        } else {
            menuItem.setText("复制 ( Copy )");
        }
        SVGPath menuItemIcon = new SVGPath();
        menuItemIcon.setScaleX(0.7);
        menuItemIcon.setScaleY(0.7);
        menuItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        menuItemIcon.setFill(Color.valueOf("#074675"));
        menuItem.setGraphic(new Group(menuItemIcon));
        return menuItem;
    }

    public static MenuItem createCutMenuItem(boolean hasControlKey) {
        MenuItem menuItem = new MenuItem();
        if (hasControlKey) {
            menuItem.setText("剪切 ( Cut )                                   Ctrl+X");
        } else {
            menuItem.setText("剪切 ( Cut ) ");
        }
        SVGPath menuItemIcon = new SVGPath();
        menuItemIcon.setScaleX(0.6);
        menuItemIcon.setScaleY(0.6);
        menuItemIcon.setContent("M9.6562 7.6406 L21.9844 20.0156 L21.9844 21 L18.9844 21 L12 14.0156 L9.6562 16.3594 Q9.9844 17.1562 9.9844 18 Q9.9844 19.6406 8.8125 20.8125 Q7.6406 21.9844 6 21.9844 Q4.3594 21.9844 3.1875 20.8125 Q2.0156 19.6406 2.0156 18 Q2.0156 16.3594 3.1875 15.1875 Q4.3594 14.0156 6 14.0156 Q6.8438 14.0156 7.6406 14.3438 L9.9844 12 L7.6406 9.6562 Q6.8438 9.9844 6 9.9844 Q4.3594 9.9844 3.1875 8.8125 Q2.0156 7.6406 2.0156 6 Q2.0156 4.3594 3.1875 3.1875 Q4.3594 2.0156 6 2.0156 Q7.6406 2.0156 8.8125 3.1875 Q9.9844 4.3594 9.9844 6 Q9.9844 6.8438 9.6562 7.6406 ZM6 8.0156 Q6.8438 8.0156 7.4219 7.4375 Q8.0156 6.8438 8.0156 6 Q8.0156 5.1562 7.4219 4.5781 Q6.8438 3.9844 6 3.9844 Q5.1562 3.9844 4.5625 4.5781 Q3.9844 5.1562 3.9844 6 Q3.9844 6.8438 4.5625 7.4375 Q5.1562 8.0156 6 8.0156 ZM6 20.0156 Q6.8438 20.0156 7.4219 19.4375 Q8.0156 18.8438 8.0156 18 Q8.0156 17.1562 7.4219 16.5781 Q6.8438 15.9844 6 15.9844 Q5.1562 15.9844 4.5625 16.5781 Q3.9844 17.1562 3.9844 18 Q3.9844 18.8438 4.5625 19.4375 Q5.1562 20.0156 6 20.0156 ZM12 12.5156 Q12.1875 12.5156 12.3438 12.3594 Q12.5156 12.1875 12.5156 12 Q12.5156 11.8125 12.3438 11.6562 Q12.1875 11.4844 12 11.4844 Q11.8125 11.4844 11.6406 11.6562 Q11.4844 11.8125 11.4844 12 Q11.4844 12.1875 11.6406 12.3594 Q11.8125 12.5156 12 12.5156 ZM18.9844 3 L21.9844 3 L21.9844 3.9844 L15 11.0156 L12.9844 9 L18.9844 3 Z");
        menuItemIcon.setFill(Color.valueOf("#074675"));
        menuItem.setGraphic(new Group(menuItemIcon));
        return menuItem;
    }

    public static MenuItem createPasteMenuItem(boolean hasControlKey) {
        MenuItem menuItem = new MenuItem();
        if (hasControlKey) {
            menuItem.setText("粘贴 ( Paste )                                Ctrl+V");
        } else {
            menuItem.setText("粘贴 ( Paste )");
        }
        SVGPath menuItemIcon = new SVGPath();
        menuItemIcon.setScaleX(0.65);
        menuItemIcon.setScaleY(0.65);
        menuItemIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        menuItemIcon.setFill(Color.valueOf("#074675"));
        menuItem.setGraphic(new Group(menuItemIcon));
        return menuItem;
    }

    public static Button createModifyButton(String tooltipText) {
        Button button = new Button();
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
        SVGPath buttonIcon = new SVGPath();
        buttonIcon.setScaleX(0.4);
        buttonIcon.setScaleY(0.4);
        buttonIcon.setContent("M7.6875 16.9922 L9.2344 18.5391 L17.5312 10.2891 L15.9375 8.6953 L7.6875 16.9922 ZM15.2344 7.9922 L13.6875 6.4453 L5.3906 14.6953 L7.0781 16.3828 L15.2344 7.9922 ZM17.7656 2.3672 L21.7031 6.3047 Q22.2188 6.8203 22.1875 7.5234 Q22.1719 8.2266 21.7031 8.6953 L9.8438 20.6016 L1.5469 22.2891 L3.2344 13.9922 Q14.3906 2.6484 15.1406 2.1328 Q15.7031 1.7109 16.4688 1.7891 Q17.25 1.8516 17.7656 2.3672 Z");
        buttonIcon.setFill(Color.valueOf("#074675"));
        button.setGraphic(new Group(buttonIcon));
        button.getStyleClass().add("little-custom-button");
        button.setStyle("-fx-padding: 1 3 1 3");
        button.setFocusTraversable(false);
        return button;
    }
}
