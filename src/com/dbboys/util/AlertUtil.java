package com.dbboys.util;

import com.dbboys.app.AppState;
import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconPaths;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.concurrent.atomic.AtomicReference;

public final class AlertUtil {
    private static final double DIALOG_WIDTH = 460;
    private static final Insets CONTENT_PADDING = new Insets(10, 20, 10, 20);
    private static final String BODY_STYLE =
            "-fx-background-color: #151a1f;" +
            "-fx-padding: 16 20 18 20;" +
            "-fx-spacing: 16;";
    private static final String PRIMARY_BUTTON_STYLE =
            "-fx-background-color: #2d6f9f;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #2d6f9f;" +
            "-fx-background-radius: 3;" +
            "-fx-border-radius: 3;" +
            "-fx-padding: 6 18 6 18;";
    private static final String SECONDARY_BUTTON_STYLE =
            "-fx-background-color: #2b2b2b;" +
            "-fx-text-fill: #e6e6e6;" +
            "-fx-border-color: #575757;" +
            "-fx-background-radius: 3;" +
            "-fx-border-radius: 3;" +
            "-fx-padding: 6 18 6 18;";

    private AlertUtil() {
    }

    public static void showAlert(String title, String message) {
        ButtonType confirmButton = new ButtonType(I18n.t("common.confirm", "确认"), ButtonBar.ButtonData.OK_DONE);
        showDialog(title, message, confirmButton);
    }

    public static boolean showConfirm(String title, String message) {
        ButtonType confirmButton = new ButtonType(I18n.t("common.confirm", "确认"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(I18n.t("common.cancel", "取消"), ButtonBar.ButtonData.CANCEL_CLOSE);
        return showDialog(title, message, confirmButton, cancelButton) == confirmButton;
    }

    public static void showAlertI18n(String titleKey, String titleFallback, String messageKey, String messageFallback, Object... args) {
        String title = I18n.t(titleKey, titleFallback);
        String message = I18n.t(messageKey, messageFallback).formatted(args);
        showAlert(title, message);
    }

    public static boolean showConfirmI18n(String titleKey, String titleFallback, String messageKey, String messageFallback, Object... args) {
        String title = I18n.t(titleKey, titleFallback);
        String message = I18n.t(messageKey, messageFallback).formatted(args);
        return showConfirm(title, message);
    }

    // Backward-compatible wrappers for existing call sites.
    public static void CustomAlert(String title, String alertText) {
        showAlert(title, alertText);
    }

    // Backward-compatible wrappers for existing call sites.
    public static boolean CustomAlertConfirm(String title, String contentText) {
        return showConfirm(title, contentText);
    }

    private static ButtonType showDialog(String title, String message, ButtonType... buttonTypes) {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle(title == null ? "" : title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        Window owner = AppState.getWindow();
        if (owner != null) {
            stage.initOwner(owner);
        }
        if (stage.getIcons().isEmpty()) {
            stage.getIcons().add(new javafx.scene.image.Image(IconPaths.MAIN_LOGO));
        }

        AtomicReference<ButtonType> resultRef = new AtomicReference<>();
        Text text = new Text(message == null ? "" : message);
        text.setStyle("-fx-fill: #e6e6e6; -fx-font-size: 12px;");
        text.setWrappingWidth(DIALOG_WIDTH - 40);

        HBox buttonBar = new HBox(10);
        buttonBar.setStyle("-fx-alignment: center-right;");
        ButtonType defaultButtonType = findDefaultButton(buttonTypes);
        ButtonType cancelButtonType = findCancelButton(buttonTypes);
        for (ButtonType buttonType : buttonTypes) {
            Button button = new Button(buttonType.getText());
            button.setFocusTraversable(false);
            button.setDefaultButton(buttonType == defaultButtonType);
            button.setCancelButton(buttonType == cancelButtonType);
            button.setStyle(buttonType == cancelButtonType ? SECONDARY_BUTTON_STYLE : PRIMARY_BUTTON_STYLE);
            button.setOnAction(event -> {
                resultRef.set(buttonType);
                stage.close();
            });
            buttonBar.getChildren().add(button);
        }

        VBox body = new VBox(text, buttonBar);
        body.setPadding(CONTENT_PADDING);
        body.setStyle(BODY_STYLE);
        VBox.setVgrow(text, Priority.ALWAYS);

        CustomWindowFrameUtil.Frame frame = CustomWindowFrameUtil.create(
                stage,
                stage.titleProperty(),
                body,
                DIALOG_WIDTH,
                220
        );
        frame.root.setMinWidth(DIALOG_WIDTH);
        frame.closeButton.setOnAction(event -> {
            resultRef.set(cancelButtonType != null ? cancelButtonType : defaultButtonType);
            stage.close();
        });
        frame.scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                resultRef.set(cancelButtonType != null ? cancelButtonType : defaultButtonType);
                stage.close();
            } else if (event.getCode() == KeyCode.ENTER) {
                resultRef.set(defaultButtonType);
                stage.close();
            }
        });

        stage.setScene(frame.scene);
        stage.showAndWait();
        return resultRef.get();
    }

    private static ButtonType findDefaultButton(ButtonType[] buttonTypes) {
        for (ButtonType buttonType : buttonTypes) {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return buttonType;
            }
        }
        return buttonTypes.length > 0 ? buttonTypes[0] : null;
    }

    private static ButtonType findCancelButton(ButtonType[] buttonTypes) {
        for (ButtonType buttonType : buttonTypes) {
            if (buttonType.getButtonData().isCancelButton()) {
                return buttonType;
            }
        }
        return null;
    }
}
