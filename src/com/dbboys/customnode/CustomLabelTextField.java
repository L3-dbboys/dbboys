package com.dbboys.customnode;

import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class CustomLabelTextField extends TextField {
    public CustomLabelTextField() {
        super();
        setFocusTraversable(false);
        SVGPath copyIcon = new SVGPath();
        copyIcon.setScaleX(0.7);
        copyIcon.setScaleY(0.7);
        copyIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        copyIcon.setFill(Color.valueOf("#074675"));
        MenuItem copyItem = new MenuItem("复制(Copy)            (Ctrl+C)");
        copyItem.setGraphic(new Group(copyIcon));

        ContextMenu connectMenu = new ContextMenu();
        connectMenu.getItems().addAll(copyItem);
        setContextMenu(connectMenu);
        copyItem.setOnAction(event -> {
            copy();
        });
        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {  // 判断是否是右键
                if(getSelectedText().equals("")){
                    copyItem.setDisable(true);
                }else{
                    copyItem.setDisable(false);
                }
            }
        });
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            // 使用 Text 来计算当前文本所需的宽度
            Text text = new Text();
            text.setFont(Font.font("Courier New"));
            //text.setFont(F);  // 设置与 TextField 相同的字体，确保宽度准确
            text.setText(newValue);

            double textWidth = text.getBoundsInLocal().getWidth();
            // 设置 TextField 的宽度，添加一些额外的空间以避免内容过于紧凑
            setPrefWidth(textWidth+20);
        });

        getStyleClass().add("label-text");
        setEditable(false);
    }

}
