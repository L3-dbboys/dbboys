package com.dbboys.customnode;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class CustomInstallStepHbox extends HBox {
    public Label nameLabel=new Label();
    public Label iconLabel=new Label();
    public Label descLabel=new Label();
    public CustomInstallStepHbox(String name,String desc) {

        SVGPath iconLabelIcon = new SVGPath();
        iconLabelIcon.setContent("M13.2031 19.8047 L13.2031 15.6172 L3.6094 15.6172 L3.6094 8.3984 L13.2031 8.3984 L13.2031 4.1953 L21 12.0078 L13.2031 19.8047 Z");
        iconLabelIcon.setScaleX(0.6);
        iconLabelIcon.setScaleY(0.6);
        iconLabelIcon.setFill(Color.valueOf("#074675"));
        iconLabel.setGraphic(iconLabelIcon);
        iconLabel.setVisible(false);

        setPadding(new Insets(0,0,0,20));
        nameLabel.setPrefWidth(100);
        nameLabel.setText(name);
        nameLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 9");
        descLabel.setText(desc);
        descLabel.setStyle("-fx-text-fill: #888;-fx-font-size: 9");
        getChildren().addAll(iconLabel,nameLabel,descLabel);
        setAlignment(Pos.CENTER_LEFT);

    }
}
