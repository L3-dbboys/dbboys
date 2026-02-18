package com.dbboys.customnode;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CustomInstallStepHbox extends HBox {
    public Label nameLabel=new Label();
    public Label iconLabel=new Label();
    public Label descLabel=new Label();
    public CustomInstallStepHbox(String name,String desc) {

        iconLabel.setGraphic(IconFactory.create(IconPaths.INSTALL_STEP_ARROW, 0.6, 0.6));
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
