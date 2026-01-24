package com.dbboys.util;

import com.dbboys.app.Main;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.sf.jsqlparser.statement.alter.Alter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AlterUtil {
    private static final Logger log = LogManager.getLogger(AlterUtil.class);

    private static Alert Alert ;
    private static Stage alterstage ;
    private static ButtonType buttonTypeOk;

    private static Alert confirmAlert ;
    private static Stage confirmAlertStage ;
    private static ButtonType confirmAlertButtonTypeOk;
    private static ButtonType  confirmAlertButtonTypeCancel ;
    private static ButtonType  confirmAlertButtonTypeCommit ;
    private static Boolean  confirmAlertconfirm ;
    private static Text contentText = new Text();
    private static StackPane centeredContainer;


    static{
        Alert = new Alert(javafx.scene.control.Alert.AlertType.NONE);
        Alert.getDialogPane().getScene().getStylesheets().add(AlterUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        Alert.setHeaderText("");
        alterstage = (Stage) Alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image("file:images/logo.png"));
        buttonTypeOk = new ButtonType("确认");
        Alert.getButtonTypes().setAll(buttonTypeOk);
        Alert.getDialogPane().setPrefWidth(360);
        Alert.getDialogPane().setPrefHeight(80);
        contentText.wrappingWidthProperty().bind(Alert.getDialogPane().widthProperty().subtract(40));
        centeredContainer = new StackPane();
        //centeredContainer.getChildren().add(new ImageView(new Image("file:images/dialog-error.png")));
        centeredContainer.getChildren().add(contentText);
        Alert.getDialogPane().setContent(centeredContainer);


    }
    public static void CustomAlert(String title, String alertText){


        Alert.setTitle(title);
        Text text = new Text(alertText);
        //Alert.setWidth(360);//默认宽度
        text.wrappingWidthProperty().bind(Alert.getDialogPane().widthProperty().subtract(40));
        VBox contentBox = new VBox(text);
        contentBox.setPadding(new Insets(10, 20, 10, 20)); // 上, 右, 下, 左
        Alert.getDialogPane().setContent(contentBox);
        //Alert.setContentText(alertText);
        Alert.showAndWait();
    }

    public static boolean CustomAlertConfirm(String title, String contentText) {
        confirmAlert=new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("");
        confirmAlert.setTitle(title);
        confirmAlert.setGraphic(null);
        //confirmAlert.setWidth(360);
        Text text = new Text(contentText);
        text.wrappingWidthProperty().bind(confirmAlert.getDialogPane().widthProperty().subtract(40));
        VBox contentBox = new VBox(text);
        contentBox.setPadding(new Insets(10, 20, 10, 20)); // 上, 右, 下, 左
        confirmAlert.getDialogPane().setContent(contentBox);

        confirmAlert.getDialogPane().getScene().getStylesheets().add(AlterUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        confirmAlertStage = (Stage) confirmAlert.getDialogPane().getScene().getWindow();
        confirmAlertButtonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        confirmAlertButtonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlertStage.getIcons().add(new Image("file:images/logo.png"));
        confirmAlert.getButtonTypes().setAll(confirmAlertButtonTypeOk, confirmAlertButtonTypeCancel);
        confirmAlertconfirm=false;

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeOk;
    }


}
