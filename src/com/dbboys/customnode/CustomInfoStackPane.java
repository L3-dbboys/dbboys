package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.util.NotificationUtil;
import com.dbboys.util.SnapshotUtil;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;

import java.util.ArrayList;
import java.util.List;

public class CustomInfoStackPane extends StackPane {
    public GenericStyledArea codeArea;
    public VirtualizedScrollPane codearea_scollpane;
    public Button codearea_snap_button=new Button();
    public StackPane notice_pane=new StackPane();
    private  Integer totalHeight=0;
    public Boolean showNoticeInMain=true;

    public CustomInfoStackPane(GenericStyledArea styledTextArea) {
        super();
        codeArea=styledTextArea;
        codeArea.setWrapText(true);
        codearea_scollpane = new VirtualizedScrollPane(codeArea);
        getChildren().add(codearea_scollpane);

        notice_pane.setStyle("-fx-background-color: none;-fx-alignment: center");
        notice_pane.setMaxWidth(360);
        notice_pane.setMaxHeight(25);
        notice_pane.setVisible(false);
        SVGPath codearea_snap_button_icon = new SVGPath();
        codearea_snap_button_icon.setContent("M10.125 10.9922 Q11.2656 9.8516 12.8594 9.8516 Q14.4531 9.8516 15.5781 10.9922 Q16.7188 12.1172 16.7188 13.7109 Q16.7188 15.3047 15.5781 16.4453 Q14.4531 17.5703 12.8594 17.5703 Q11.2656 17.5703 10.125 16.4453 Q9 15.3047 9 13.7109 Q9 12.1172 10.125 10.9922 ZM22.2812 4.2891 Q23.7031 4.2891 24.7031 5.2891 Q25.7188 6.2891 25.7188 7.7109 L25.7188 19.7109 Q25.7188 21.1328 24.7031 22.1328 Q23.7031 23.1484 22.2812 23.1484 L3.4219 23.1484 Q2.0156 23.1484 1 22.1328 Q0 21.1328 0 19.7109 L0 7.7109 Q0 6.2891 1 5.2891 Q2.0156 4.2891 3.4219 4.2891 L6.4219 4.2891 L7.1094 2.4609 Q7.3594 1.8047 8.0312 1.3359 Q8.7188 0.8516 9.4219 0.8516 L16.2812 0.8516 Q17 0.8516 17.6719 1.3359 Q18.3438 1.8047 18.6094 2.4609 L19.2812 4.2891 L22.2812 4.2891 ZM8.6094 17.9609 Q10.375 19.7109 12.8438 19.7109 Q15.3281 19.7109 17.0938 17.9609 Q18.8594 16.1953 18.8594 13.7266 Q18.8594 11.2422 17.0938 9.4766 Q15.3281 7.7109 12.8438 7.7109 Q10.375 7.7109 8.6094 9.4766 Q6.8594 11.2422 6.8594 13.7266 Q6.8594 16.1953 8.6094 17.9609 Z");
        codearea_snap_button_icon.setScaleX(0.35);
        codearea_snap_button_icon.setScaleY(0.35);
        codearea_snap_button_icon.setFill(Color.valueOf("#074675"));
        codearea_snap_button.setGraphic(new Group(codearea_snap_button_icon));
        codearea_snap_button.setFocusTraversable(false);
        codearea_snap_button.getStyleClass().add("codearea-camera-button");
        getChildren().add(codearea_snap_button);
        setAlignment(codearea_snap_button, Pos.TOP_RIGHT);
        getChildren().add(notice_pane);
        setAlignment(notice_pane, Pos.CENTER);
        setMargin(codearea_snap_button, new javafx.geometry.Insets(0, 15, 20, 20));
        codearea_snap_button.setOnAction(e -> {


            new Thread(createSnapshotInfoCodeAreaTask()).start();

            /*
            captureFullCodeArea(codeArea, 2.0, image -> {
                try {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(image);
                    clipboard.setContent(content);
                    NotificationUtil.showNotification(notice_pane, "截图成功复制到剪切板！");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

             */
        });

    }

    public Task createSnapshotInfoCodeAreaTask(){
        totalHeight= (int) codeArea.getTotalHeightEstimate();
        int viewportHeight = (int)codeArea.getHeight();
        Task scrollTask=new Task() {
            @Override
            protected Object call() throws Exception {
                for(int y=0;y<totalHeight;y+=10) {
                    int finalY = y;
                    Platform.runLater(() -> {
                        codeArea.scrollYToPixel(finalY);
                    });
                    Thread.sleep(10);
                }

                Platform.runLater(() -> {
                    codeArea.scrollYToPixel(Double.MAX_VALUE);
                });
                Thread.sleep(150);
                Platform.runLater(() -> {
                    codeArea.scrollYToPixel(Double.MAX_VALUE);
                });
                Thread.sleep(150);
                Platform.runLater(() -> {  //似乎这一步是关键，需要执行才能正确获取高度
                    codeArea.scrollYToPixel(0);
                });
                Thread.sleep(150);


                return null;
            }

        };

        scrollTask.setOnSucceeded(event -> {
            captureFullCodeArea(codeArea, 2.0, image -> {
                /*复制到剪切板，会占用很大内存，改为复制到文件
                try {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(image);
                    clipboard.setContent(content);
                    if(showNoticeInMain){
                        NotificationUtil.showNotification(Main.mainController.noticePane, "截图成功复制到剪切板！可在微信、word、画图等软件粘贴！");
                    }else{
                        NotificationUtil.showNotification(notice_pane, "截图成功复制到剪切板！");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                 */
                SnapshotUtil.copyToClipboard(image,showNoticeInMain?Main.mainController.noticePane:notice_pane);
            });
        });
        return scrollTask;

    }

    private void captureFullCodeArea(GenericStyledArea codeArea, double scale, java.util.function.Consumer<WritableImage> callback) {
        Platform.runLater(() -> {
            totalHeight =(int) codeArea.getTotalHeightEstimate();
            double viewportHeight = codeArea.getHeight();
            int pages = (int) Math.ceil(totalHeight / viewportHeight);

            List<WritableImage> images = new ArrayList<>();
            capturePages(codeArea, 0, pages, scale, images, callback);
        });
    }

    private void capturePages(GenericStyledArea codeArea,
                              int page, int totalPages, double scale,
                              List<WritableImage> images,
                              java.util.function.Consumer<WritableImage> onFinish) {

        if (page >= totalPages) {
            WritableImage finalImage = mergeImagesUseBottomOfLast(images,(int)(totalHeight*scale));
            onFinish.accept(finalImage);
            return;
        }

        double y = page * codeArea.getHeight();
        codeArea.scrollYToPixel(y);

        PauseTransition delay = new PauseTransition(Duration.millis(150));
        delay.setOnFinished(e -> {
            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(Transform.scale(scale, scale));

            WritableImage image = codeArea.snapshot(params, null);
            images.add(image);

            capturePages(codeArea, page + 1, totalPages, scale, images, onFinish);
        });
        delay.play();
    }

    WritableImage mergeImagesUseBottomOfLast(List<WritableImage> images, int totalContentHeight) {
        if (images == null || images.isEmpty()) return null;

        int width = (int) images.get(0).getWidth();
        int accumulatedHeight = 0;

        List<WritableImage> adjustedImages = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            WritableImage img = images.get(i);
            int height = (int) img.getHeight();

            // 如果是最后一张，只保留底部剩余的部分
            if (i == images.size() - 1&&i>0) {
                int remaining = totalContentHeight - accumulatedHeight;
                height = Math.min(height, remaining);
                img = cropBottom(img, height);  // ✅ 使用底部！
            }

            adjustedImages.add(img);
            accumulatedHeight += img.getHeight();
        }

        // 合并所有图像
        WritableImage result = new WritableImage(width, accumulatedHeight);
        PixelWriter writer = result.getPixelWriter();

        int yOffset = 0;
        for (WritableImage img : adjustedImages) {
            writer.setPixels(0, yOffset, (int) img.getWidth(), (int) img.getHeight(), img.getPixelReader(), 0, 0);
            yOffset += img.getHeight();
        }

        return result;
    }


    private WritableImage cropBottom(WritableImage src, int cropHeight) {
        int width = (int) src.getWidth();
        int srcHeight = (int) src.getHeight();
        cropHeight = Math.min(srcHeight, cropHeight);

        PixelReader reader = src.getPixelReader();
        WritableImage cropped = new WritableImage(width, cropHeight);
        cropped.getPixelWriter().setPixels(
                0, 0,                           // 目标图起点
                width, cropHeight,             // 裁剪宽高
                reader,
                0, srcHeight - cropHeight      // 从原图底部开始复制
        );
        return cropped;
    }

}

