package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.util.AlterUtil;
import com.dbboys.util.DownloadManagerUtil;
import com.dbboys.util.NotificationUtil;
import com.dbboys.util.TabpaneUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CharacterHit;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyledSegment;
import org.fxmisc.richtext.model.TextOps;
import org.reactfx.util.Either;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomGenericStyledArea extends GenericStyledArea {
    private static final Logger log = LogManager.getLogger(CustomGenericStyledArea.class);
    private int[] headingCounters = new int[6]; // 索引0对应H1，1对应H2，以此类推
    public CustomSearchReplaceVbox customSearchReplaceVbox;
    private static List docType=new ArrayList();
    public  MenuItem codeAreaSearchItem = new javafx.scene.control.MenuItem("查找 ( Search )                               Ctrl+F");
    public  ContextMenu contextMenu = new ContextMenu();
    public  MenuItem modifyItem = new  MenuItem("编辑 ( Modify )                        Ctrl+Enter");
    private  MenuItem copyItem = new MenuItem("复制 ( Copy )                                 Ctrl+C");
    private  MenuItem imageCopyItem = new MenuItem("复制图片 ( Copy Image ) ");
    public  ContextMenu imageContextMenu = new ContextMenu();
    private MenuItem imageSaveAsItem = new javafx.scene.control.MenuItem("图片另存为 ( Image Save As )");
    private File markdownFile;

    public static class NodeSegmentOps implements SegmentOps<Node, String> {
        @Override
        public int length(javafx.scene.Node seg) {
            return 1;
        }

        @Override
        public char charAt(javafx.scene.Node seg, int index) {
            return '\ufffc';
        }

        @Override
        public String getText(javafx.scene.Node seg) {
            return "\ufffc";
        }

        @Override
        public javafx.scene.Node subSequence(javafx.scene.Node seg, int start, int end) {
            return seg;
        }

        @Override
        public javafx.scene.Node subSequence(javafx.scene.Node seg, int index) {
            return seg;
        }

        @Override
        public Optional<Node> joinSeg(javafx.scene.Node left, javafx.scene.Node right) {
            return Optional.empty();
        }

        @Override
        public javafx.scene.Node createEmptySeg() {
            return new Text("");
        }
    }

    static{
        docType=List.of(
                "zip", "rar", "7z", "exe", "pdf", "doc", "docx", "xls", "xlsx",
                "png", "jpg", "jpeg", "gif", "bmp", "mp3", "mp4", "avi",
                "mkv", "txt", "csv", "json", "xml", "iso", "tar", "gz","iso","tar","tar.gz"
                ,"sh","chm","jar","yml"
        );








    }

    public CustomGenericStyledArea(File markdownFile){
        this.markdownFile=markdownFile;

        TextOps<String, String> textOps = SegmentOps.styledTextOps();
        NodeSegmentOps nodeOps = new CustomGenericStyledArea.NodeSegmentOps();
        TextOps<Either<String, javafx.scene.Node>, String> segmentOps = textOps._or(nodeOps, (node, style) -> Optional.empty());

        // 使用集合来存储段落样式
        BiConsumer<TextFlow, String> paragraphStyler = (textFlow, style) -> {
            if (style != null && !style.isEmpty()) {
                textFlow.setStyle(style); // 例如行间距
            }
        };
        Function<StyledSegment<Either<String, Node>, String>, Node> nodeFactory = seg -> {
            Either<String, javafx.scene.Node> e = seg.getSegment();
            if (e.isLeft()) {
                Text t = new Text(e.getLeft());

                // 应用内联样式
                if (seg.getStyle() != null && !seg.getStyle().isEmpty()) {
                    if (seg.getStyle().contains("link")) {
                        String urlInit = seg.getStyle().split("link:")[1].split(";")[0];
                        String tmpUrl=urlInit;
                        if(!urlInit.toLowerCase().startsWith("http")){
                            try {
                                Path path = getAbsPath(markdownFile, urlInit);
                                tmpUrl=path.toString();
                            } catch (Exception ex) {
                                log.error(ex.getMessage(), ex);
                            }
                        }
                        String url=tmpUrl;
                        String ext = url.substring(url.lastIndexOf('.') + 1).toLowerCase();
                        t.setStyle( "-fx-fill: #0066cc; -fx-underline: true; -fx-cursor: hand;");


                        if(url.toLowerCase().startsWith("http")) {
                            new Thread(() -> {
                                try {
                                    //如果开了vpn网络又不通，可能需要等10秒左右才会变红，其他场景很快会标红
                                    //log.info(DownloadManagerUtil.encodeUrl(url));
                                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                                    conn.setRequestMethod("HEAD");
                                    conn.setInstanceFollowRedirects(true);
                                    conn.setConnectTimeout(2000);
                                    conn.setReadTimeout(2000);

                                    int code = conn.getResponseCode();
                                    if( code >= 200 && code < 400) {

                                    }else{
                                        Platform.runLater(()->{
                                            t.setStyle("-fx-fill: #f00; -fx-underline: true;-fx-cursor: hand;-fx-strikethrough: true");
                                        });
                                    }
                                } catch (Exception ex) {
                                    log.error(ex.getMessage(),ex);
                                    Platform.runLater(()-> {
                                        t.setStyle("-fx-fill: #f00; -fx-underline: true;-fx-cursor: hand;-fx-strikethrough: true");
                                    });

                                }
                            }).start();

                        }else if (!new File(url).exists()) {
                            t.setStyle("-fx-fill: #f00; -fx-underline: true;-fx-cursor: hand;-fx-strikethrough: true");
                        }

                        t.setOnContextMenuRequested(event -> {
                            MenuItem saveAsItem = new javafx.scene.control.MenuItem("另存为 ( Save As )");
                            MenuItem copyLinkItem = new MenuItem("复制链接 ( Copy Link )");
                            SVGPath saveAsItemIcon = new SVGPath();
                            saveAsItemIcon.setScaleX(0.6);
                            saveAsItemIcon.setScaleY(0.6);
                            saveAsItemIcon.setContent("M20.3438 6.0938 Q21 6.75 21 7.6875 L21 20.25 Q21 21.1875 20.3438 21.8438 Q19.6875 22.5 18.75 22.5 L2.25 22.5 Q1.3125 22.5 0.6562 21.8438 Q0 21.1875 0 20.25 L0 3.75 Q0 2.8125 0.6562 2.1562 Q1.3125 1.5 2.25 1.5 L14.8125 1.5 Q15.75 1.5 16.4062 2.1562 L20.3438 6.0938 ZM8.3594 18.6406 Q9.2344 19.5 10.5 19.5 Q11.7656 19.5 12.625 18.6406 Q13.5 17.7656 13.5 16.5 Q13.5 15.2344 12.625 14.375 Q11.7656 13.5 10.5 13.5 Q9.2344 13.5 8.3594 14.375 Q7.5 15.2344 7.5 16.5 Q7.5 17.7656 8.3594 18.6406 ZM15 5.2031 Q15 5.0156 14.8125 4.8281 L14.6719 4.6875 Q14.4844 4.5 14.2969 4.5 L3.5625 4.5 Q3 4.5 3 5.0625 L3 9.9375 Q3 10.5 3.5625 10.5 L14.4375 10.5 Q15 10.5 15 9.9375 L15 5.2031 Z");
                            saveAsItemIcon.setFill(Color.valueOf("#074675"));
                            saveAsItem.setGraphic(new Group(saveAsItemIcon));

                            SVGPath copyLinkItemIcon = new SVGPath();
                            copyLinkItemIcon.setScaleX(0.7);
                            copyLinkItemIcon.setScaleY(0.7);
                            copyLinkItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
                            copyLinkItemIcon.setFill(Color.valueOf("#074675"));
                            copyLinkItem.setGraphic(new Group(copyLinkItemIcon));
                            if(docType.contains(ext)||ext.equals("md")){
                                saveAsItem.setOnAction(null);
                                saveAsItem.setOnAction(ev -> {
                                    FileChooser fileChooser = new FileChooser();
                                    fileChooser.setTitle("保存链接内容");
                                    String defaultName = "downloaded.file";
                                    if(url.toLowerCase().startsWith("http")){
                                        try {
                                            //URI uri = new URI(url);
                                            //String path = uri.getPath();
                                            //defaultName = Paths.get(path).getFileName().toString();
                                            defaultName=DownloadManagerUtil.getRealFileNameFromRedirect(url);
                                            if (defaultName.isEmpty()) defaultName = "downloaded.file";
                                        } catch (Exception ex) {
                                            log.error(ex.getMessage(),ex);
                                            AlterUtil.CustomAlert("下载错误", ex.getMessage());
                                            return;
                                        }
                                    }else{
                                        defaultName = url.substring(url.lastIndexOf('/') + 1);
                                    }
                                    fileChooser.setInitialFileName(defaultName);
                                    File file = fileChooser.showSaveDialog(Main.scene.getWindow());

                                    if (file != null) {
                                        if(file.exists()){
                                            file.delete();
                                        }
                                        DownloadManagerUtil.addDownload(url, file, true,null);
                                    }
                                });

                                copyLinkItem.setOnAction(ev -> {
                                    Clipboard clipboard = Clipboard.getSystemClipboard();
                                    ClipboardContent content = new ClipboardContent();
                                    content.putString(url);
                                    clipboard.setContent(content);
                                    NotificationUtil.showNotification(Main.mainController.notice_pane, "链接已复制！");
                                });
                            }
                            ContextMenu linkContextMenu = new ContextMenu();
                            linkContextMenu.getItems().addAll(saveAsItem,copyLinkItem);
                            linkContextMenu.show(t, event.getScreenX(), event.getScreenY());
                            event.consume();
                        });
                        t.setOnMouseClicked(event -> {
                            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                                // 常见文件扩展名集合
                                if (docType.contains(ext)) {
                                    File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
                                    String defaultName = "downloaded.file";
                                    if (url.toLowerCase().startsWith("http")) {
                                        //URI uri = null;
                                        try {
                                           // uri = new URI(url);
                                            defaultName=DownloadManagerUtil.getRealFileNameFromRedirect(url);

                                        } catch (Exception ex) {
                                            log.error(ex.getMessage(),ex);
                                            AlterUtil.CustomAlert("下载错误", ex.getMessage());
                                            return;
                                            //throw new RuntimeException(ex);
                                        }
                                        //String path = uri.getPath();
                                        //defaultName = Paths.get(path).getFileName().toString();
                                    } else {
                                        defaultName = url.substring(url.lastIndexOf('/') + 1);
                                    }

                                    File saveFile = new File(desktopDir, defaultName);  // 自动拼接路径

                                    //这里增加判断是避免弹出通知 “文件将下载到桌面”后又报错！
                                    File saveFileTemp = new File(desktopDir, defaultName + ".download");
                                    if (saveFile.exists()) {
                                        AlterUtil.CustomAlert("错误", "桌面已存在同名文件！");
                                    } else if (saveFileTemp.exists()) {
                                        AlterUtil.CustomAlert("错误", "该文件正在下载，请勿重复下载！");
                                    } else {
                                        NotificationUtil.showNotification(Main.mainController.notice_pane, "文件将下载到桌面！");
                                        DownloadManagerUtil.addDownload(url, saveFile, true, null);
                                    }


                                } else {
                                    openUrl(url);
                                }
                                event.consume();
                            }
                        });


                        /*


                        t.setOnMouseClicked(evt -> {
                            try {
                                Desktop.getDesktop().browse(new URI(url));
                            } catch (Exception ignored) {
                            }
                        });

                         */
                    } else if (seg.getStyle().contains("code-inline")) {
                        t.setStyle(
                                "-fx-fill: #9f453c; " +
                                        "-fx-font-family: 'SimSun'; "

                        );

                    } else if (seg.getStyle().contains("code-block")) {
                        ContextMenu textAreaContextMenu = new ContextMenu();
                        MenuItem textAreaCopyItem = new MenuItem("复制 ( Copy )                                 Ctrl+C");
                        SVGPath textAreaCopyItemIcon = new SVGPath();
                        textAreaCopyItemIcon.setScaleX(0.7);
                        textAreaCopyItemIcon.setScaleY(0.7);
                        textAreaCopyItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
                        textAreaCopyItemIcon.setFill(Color.valueOf("#074675"));
                        textAreaCopyItem.setGraphic(new Group(textAreaCopyItemIcon));
                        textAreaContextMenu.getItems().addAll(textAreaCopyItem);
                        if (e.getLeft().trim().isEmpty()) {
                            return new Text(""); // appendtext("\n")会继承上一个style，空段落不生成 TextArea，解决最后一个如果是```出现一个空白text
                        }
                        TextArea textArea = new TextArea();
                        textArea.setWrapText(true);
                        textArea.setMaxHeight(500);
                        textArea.setMinHeight(14);
                        textArea.setEditable(false);
                        ChangeListener<Object> listener = (obs, oldVal, newVal) -> {
                            double textHeight = computeTextHeight(textArea);
                            textArea.setPrefHeight(textHeight);
                        };
                        textArea.textProperty().addListener(listener);
                        textArea.widthProperty().addListener(listener);
                        textArea.setText(e.getLeft());
                        textArea.prefWidthProperty().bind(
                                //Bindings.subtract(customGenericStyledArea.widthProperty(), 27)
                                Bindings.subtract(Main.mainController.sql_tabpane.widthProperty(), 37)
                        );

                        textArea.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
                            if (!newFocus) {
                                //textAreaContextMenu.hide();
                                // TextArea 获取焦点时，取消 GenericStyledArea 的选择
                                textArea.deselect();
                                //customGenericStyledArea.deselect(); // 清除选区
                            }else{
                                ((CustomGenericStyledArea)textArea.getParent().getParent().getParent().getParent().getParent()).deselect();
                            }
                        });

                        textArea.setContextMenu(textAreaContextMenu);
                        textAreaCopyItem.setOnAction(event1->{
                            if(!textArea.getSelectedText().isEmpty()){
                                textArea.copy();
                            }else  {
                                Clipboard clipboard = Clipboard.getSystemClipboard();
                                ClipboardContent content = new ClipboardContent();
                                content.putString(textArea.getText());
                                clipboard.setContent(content);
                                NotificationUtil.showNotification(Main.mainController.notice_pane, "代码块已复制！");
                            }
                        });


                        return textArea;

                    }else if (seg.getStyle().contains("bold")) {
                        t.setStyle("-fx-font-family: system;-fx-font-weight: bold;-fx-fill: #9f453c");
                    }else if (seg.getStyle() != null) {
                        if (seg.getStyle().contains("title")) {
                            ContextMenu contextMenu = new ContextMenu();
                            MenuItem copyItem = new MenuItem("复制 ( Copy )");
                            SVGPath copyItemIcon = new SVGPath();
                            copyItemIcon.setScaleX(0.7);
                            copyItemIcon.setScaleY(0.7);
                            copyItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
                            copyItemIcon.setFill(Color.valueOf("#074675"));
                            copyItem.setGraphic(new Group(copyItemIcon));
                            contextMenu.getItems().addAll(copyItem);
                            CustomUserTextField customUserTextField = new CustomUserTextField();
                            // textArea.setWrapText(true);
                            customUserTextField.setEditable(false);
                            customUserTextField.setText(e.getLeft());
                            customUserTextField.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;-fx-background-color:none;-fx-border-width: 0;-fx-effect:none");
                            customUserTextField.prefWidthProperty().bind(
                                    Bindings.subtract(Main.mainController.sql_tabpane.widthProperty(), 36)
                            );
                            customUserTextField.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
                                if (!newFocus) {
                                    //textAreaContextMenu.hide();
                                    // TextArea 获取焦点时，取消 GenericStyledArea 的选择
                                    customUserTextField.deselect();
                                    //customGenericStyledArea.deselect(); // 清除选区
                                }else{
                                    ((CustomGenericStyledArea)customUserTextField.getParent().getParent().getParent().getParent().getParent()).deselect();
                                }
                            });
                            customUserTextField.setContextMenu(contextMenu);
                            copyItem.setOnAction(event1->{
                                if(!customUserTextField.getSelectedText().isEmpty()){
                                    customUserTextField.copy();
                                }else  {
                                    Clipboard clipboard = Clipboard.getSystemClipboard();
                                    ClipboardContent content = new ClipboardContent();
                                    content.putString(customUserTextField.getText());
                                    clipboard.setContent(content);
                                    NotificationUtil.showNotification(Main.mainController.notice_pane, "标题已复制！");
                                }
                            });

                            customUserTextField.setAlignment(Pos.CENTER);
                            customUserTextField.setContextMenu(contextMenu);

                            return customUserTextField;

                        }
                        if (seg.getStyle().contains("heading-1")) {
                            t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
                        } else if (seg.getStyle().contains("heading-2")) {
                            t.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 0 4 0;");
                        } else if (seg.getStyle().contains("heading-3")) {
                            t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 0 3 0;");
                        } else if (seg.getStyle().contains("heading-4")) {
                            t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 0 2 0;");
                        } else if (seg.getStyle().contains("heading-5")) {
                            t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 2 0 1 0;");
                        } else if (seg.getStyle().contains("heading-6")) {
                            t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 1 0 1 0;");
                        }
                    }

                }
                return t;
            } else {
                javafx.scene.Node n = e.getRight();
                // 节点也可以有样式
                return e.getRight();
            }
        };
        super("",// 默认段落样式
                paragraphStyler,
                "",
                segmentOps,
                nodeFactory);

        //setParagraphGraphicFactory(LineNumberFactory.get(this));
        getStyleClass().add("CustomGenericStyledArea");
        setEditable(false);
        setWrapText(true);
        setStyle("-fx-font-family: system; -fx-font-size: 11px;");
        setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                modifyItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.F){
                codeAreaSearchItem.fire();
                customSearchReplaceVbox.findField.requestFocus();
            }
        });
        SVGPath codeAreaSearchItemIcon = new SVGPath();
        codeAreaSearchItemIcon.setScaleX(0.6);
        codeAreaSearchItemIcon.setScaleY(0.6);
        codeAreaSearchItemIcon.setContent("M9.8438 1.7184 Q12.0469 1.7184 13.9219 2.7965 Q15.7969 3.8746 16.8906 5.7496 Q18 7.609 18 9.8278 Q18 12.4684 16.4688 14.6246 L21.8906 20.0934 Q22.2656 20.4371 22.2812 20.9684 Q22.3125 21.484 21.9531 21.8746 Q21.5938 22.2496 21.0938 22.2809 Q20.5938 22.2965 20.2031 21.9684 L14.6406 16.4528 Q12.4844 17.984 9.8438 17.984 Q7.625 17.984 5.75 16.8903 Q3.8906 15.7809 2.8125 13.9059 Q1.7344 12.0309 1.7344 9.8278 Q1.7344 7.609 2.8125 5.7496 Q3.8906 3.8746 5.75 2.7965 Q7.625 1.7184 9.8438 1.7184 ZM9.8438 4.2496 Q8.3594 4.2496 7.0625 4.9996 Q5.7656 5.7496 5.0156 7.0465 Q4.2656 8.3434 4.2656 9.8278 Q4.2656 11.3121 5.0156 12.609 Q5.7656 13.9059 7.0625 14.6559 Q8.3594 15.3903 9.8594 15.3903 Q11.375 15.3903 12.6406 14.6559 Q13.9219 13.9059 14.6562 12.6403 Q15.4062 11.359 15.4062 9.859 Q15.4062 8.3434 14.6562 7.0778 Q13.9219 5.7965 12.6406 5.0309 Q11.375 4.2496 9.8438 4.2496 Z");
        codeAreaSearchItemIcon.setFill(Color.valueOf("#074675"));



        SVGPath modifyItemIcon = new SVGPath();
        modifyItemIcon.setScaleX(0.65);
        modifyItemIcon.setScaleY(0.65);
        modifyItemIcon.setContent("M20.625 19.5938 L3.375 19.5938 Q3.0781 19.5938 2.8438 19.8125 Q2.625 20.0312 2.625 20.3438 L2.625 21.1875 Q2.625 21.25 2.6875 21.3125 Q2.75 21.375 2.8125 21.375 L21.1875 21.375 Q21.2656 21.375 21.3125 21.3125 Q21.375 21.25 21.375 21.1875 L21.375 20.3438 Q21.375 20.0312 21.1562 19.8125 Q20.9375 19.5938 20.625 19.5938 ZM6.0469 17.625 Q6.0781 17.625 6.1094 17.625 Q6.1406 17.625 6.1875 17.625 L10.125 16.9219 Q10.1562 16.9219 10.1875 16.9062 Q10.2188 16.875 10.25 16.8438 L20.1875 6.9062 Q20.2031 6.8906 20.2188 6.8438 Q20.25 6.7969 20.25 6.75 Q20.25 6.7031 20.2188 6.6719 Q20.2031 6.625 20.1875 6.5781 L20.1875 6.5781 L16.2969 2.6875 Q16.25 2.6406 16.2031 2.6406 Q16.1719 2.625 16.125 2.625 Q16.0781 2.625 16.0312 2.6406 Q15.9844 2.6406 15.9688 2.6875 L6.0312 12.625 Q6 12.6562 5.9688 12.6875 Q5.9531 12.7188 5.9531 12.75 L5.25 16.6875 Q5.25 16.7344 5.25 16.7656 Q5.25 16.7969 5.25 16.8281 Q5.25 16.9844 5.3125 17.1406 Q5.375 17.2969 5.4844 17.3906 L5.4844 17.3906 Q5.6094 17.5 5.75 17.5625 Q5.8906 17.625 6.0469 17.625 L6.0469 17.625 Z");
        modifyItemIcon.setFill(Color.valueOf("#074675"));
        modifyItem.setGraphic(new Group(modifyItemIcon));

        SVGPath copyItemIcon = new SVGPath();
        copyItemIcon.setScaleX(0.7);
        copyItemIcon.setScaleY(0.7);
        copyItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        copyItemIcon.setFill(Color.valueOf("#074675"));
        copyItem.setGraphic(new Group(copyItemIcon));

        SVGPath imageCopyItemIcon = new SVGPath();
        imageCopyItemIcon.setScaleX(0.7);
        imageCopyItemIcon.setScaleY(0.7);
        imageCopyItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        imageCopyItemIcon.setFill(Color.valueOf("#074675"));
        imageCopyItem.setGraphic(new Group(imageCopyItemIcon));

        SVGPath imageSaveAsItemIcon = new SVGPath();
        imageSaveAsItemIcon.setScaleX(0.6);
        imageSaveAsItemIcon.setScaleY(0.6);
        imageSaveAsItemIcon.setContent("M20.3438 6.0938 Q21 6.75 21 7.6875 L21 20.25 Q21 21.1875 20.3438 21.8438 Q19.6875 22.5 18.75 22.5 L2.25 22.5 Q1.3125 22.5 0.6562 21.8438 Q0 21.1875 0 20.25 L0 3.75 Q0 2.8125 0.6562 2.1562 Q1.3125 1.5 2.25 1.5 L14.8125 1.5 Q15.75 1.5 16.4062 2.1562 L20.3438 6.0938 ZM8.3594 18.6406 Q9.2344 19.5 10.5 19.5 Q11.7656 19.5 12.625 18.6406 Q13.5 17.7656 13.5 16.5 Q13.5 15.2344 12.625 14.375 Q11.7656 13.5 10.5 13.5 Q9.2344 13.5 8.3594 14.375 Q7.5 15.2344 7.5 16.5 Q7.5 17.7656 8.3594 18.6406 ZM15 5.2031 Q15 5.0156 14.8125 4.8281 L14.6719 4.6875 Q14.4844 4.5 14.2969 4.5 L3.5625 4.5 Q3 4.5 3 5.0625 L3 9.9375 Q3 10.5 3.5625 10.5 L14.4375 10.5 Q15 10.5 15 9.9375 L15 5.2031 Z");
        imageSaveAsItemIcon.setFill(Color.valueOf("#074675"));
        imageSaveAsItem.setGraphic(new Group(imageSaveAsItemIcon));

        codeAreaSearchItem.setGraphic(new Group(codeAreaSearchItemIcon));
        contextMenu.getItems().addAll(modifyItem,codeAreaSearchItem,copyItem);
        imageContextMenu.getItems().addAll(imageSaveAsItem,imageCopyItem);
        //正文内容邮件后，在textarea右键，正文右键菜单不会自动隐藏，加此监听
        focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) {
                contextMenu.hide();
            }
        });

        copyItem.setOnAction(event -> {
            if(!getSelectedText().isEmpty()){
                copy();
            }
        });
        setContextMenu(contextMenu);

        contextMenu.setOnShowing(event -> {
            if(getSelectedText().isEmpty()){
                copyItem.setDisable(true);
            }else{
                copyItem.setDisable(false);
            }
        });
        codeAreaSearchItem.setOnAction(event->{
            customSearchReplaceVbox.setVisible(true);
        });

    }

    public void parseMarkdownWithStyles(String markdown) {
        headingCounters=new int[6];
        String[] lines = markdown.split("\n");
        Pattern imgPattern = Pattern.compile("!\\[.*?]\\((.*?)\\)");
        Pattern linkPattern = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");
        Pattern inlineCodePattern = Pattern.compile("`(.*?)`");
        Pattern boldPattern = Pattern.compile("\\*\\*([^*]+)\\*\\*");
        Pattern tableLinePattern = Pattern.compile("^\\|.*\\|$"); // 表格行（以|开头和结尾）
        //Pattern tableSeparatorPattern = Pattern.compile("^\\|(\\s*-+\\s*\\|)+$"); // 表头分隔线（|----|----|）
        Pattern tableSeparatorPattern = Pattern.compile("^\\s*\\|(\\s*-+\\s*\\|)+\\s*$");



        List<List<String>> tableRows = new ArrayList<>(); // 存储表格行数据
        boolean inTable = false; // 是否处于表格解析中

        String fileName=markdownFile.getName();
        String title=fileName.substring(0,fileName.lastIndexOf('.'));
        //添加标题
        if(title!=null&&!title.isEmpty()){
            append(Either.left(title), "title");
            appendText("\n");
        }

        boolean inCodeBlock = false;
        String codeLanguage = "";
        StringBuilder codeBlock = new StringBuilder();
        int currentParagraph = 0;

        for (String line : lines) {
            //去掉空行
            if(line.trim().isEmpty()){
                continue;
            }
            // 处理代码块
            if (line.startsWith("```")) {

                //结束上一个未完成的table
                if (inTable) {
                    // 表格结束，生成TableView并添加到内容中
                    inTable = false;
                    if (tableRows.size() >= 1) { // 至少需要表头行
                        Node tableNode = createTableView(tableRows);
                        append(Either.right(tableNode), "");
                        appendText("\n");
                    }
                    // 清空表格数据，准备下一个表格
                    tableRows.clear();
                }
                //上一个未完成的table结束

                if (!inCodeBlock) {
                    // 开始代码块
                    inCodeBlock = true;
                    codeLanguage = line.substring(3).trim();
                    codeBlock.setLength(0);

                    // 添加代码语言提示
                    if (!codeLanguage.isEmpty()) {
                        //append(Either.left("// " + codeLanguage.toUpperCase()), "");
                        //appendText("\n");
                    }
                } else {
                    // 结束代码块
                    inCodeBlock = false;

                    // 添加代码内容
                    if (codeBlock.length() > 0) {
                        append(Either.left(codeBlock.toString().trim()), "code-block");
                        appendText("\n");
                    }
                    codeBlock.setLength(0);
                }
                continue;
            }

            if (inCodeBlock) {
                codeBlock.append(line).append("\n");
                continue;
            }

            //表格
            if (inTable && tableSeparatorPattern.matcher(line).matches()) {
                // 识别为表头分隔线，不添加到数据行，仅作为表格结构标识
                continue;
            }else if (tableLinePattern.matcher(line).matches()) {
                // 识别为表格行
                inTable = true;
                // 分割单元格（去除首尾|，再按|分割）
                String[] cells = line.trim().replaceAll("^\\||\\|$", "").split("\\|");
                // 处理单元格内容（ trim 空格）
                List<String> row = new ArrayList<>();
                for (String cell : cells) {
                    row.add(cell.trim());
                }
                tableRows.add(row);
                continue;
            }else if (inTable) {
                // 表格结束，生成TableView并添加到内容中
                inTable = false;
                if (tableRows.size() >= 1) { // 至少需要表头行
                    Node tableNode = createTableView(tableRows);
                    append(Either.right(tableNode), "");
                    appendText("\n");
                }
                // 清空表格数据，准备下一个表格
                tableRows.clear();
            }

            // 标题计数器
            if (line.matches("^#{1,6} .*$")) {
                int level = 0;
                // 计算标题级别（#数量，最多6级）
                while (level < line.length() && line.charAt(level) == '#') {
                    level++;
                }
                level = Math.min(level, 6); // 限制为H1~H6（1~6）
                if (level == 0) {
                    level = 1; // 避免0级标题
                }

                // 关键：更新计数器
                int index = level - 1; // 转换为数组索引（0~5）
                headingCounters[index]++; // 当前级别计数器+1

                // 重置所有子级计数器（比当前级别低的级，如H2的子级是H3~H6）
                for (int i = index + 1; i < headingCounters.length; i++) {
                    headingCounters[i] = 0;
                }

                // 生成编号（如1.1.2）
                StringBuilder number = new StringBuilder();
                for (int i = 0; i <= index; i++) { // 只拼接当前级别及以上的计数器
                    if (headingCounters[i] > 0) {
                        if (number.length() > 0) {
                            number.append("."); // 各级之间加"."
                        }
                        number.append(headingCounters[i]);
                    }
                }
                number.append(".");


                // 核心修改：移除标题中的**标记
                String titleContent = line.substring(level).trim()
                        .replace("**", ""); // 过滤所有**，不保留加粗
                String numberedTitle = number + " " + titleContent;
                String styleClass = "heading-" + level;

                append(Either.left(numberedTitle ), styleClass);
                appendText("\n");
                continue;

            }

            // 处理图片
            if (line.contains("![") && line.contains("](")) {
                Matcher imgMatcher = imgPattern.matcher(line);
                if (imgMatcher.find()) {
                    String imgUrl = imgMatcher.group(1);
                    //imgUrl="docs"+imgUrl;

                    ImageView imgView = new ImageView(new Image("file:images/failed.png"));
                    //imgView.setFitHeight(Math.min(300, imgView));
                    imgView.setPreserveRatio(true);
                    imgView.setFitWidth(500);
                    StackPane pane = new StackPane(imgView);
                    pane.prefWidthProperty().bind(widthProperty());
                    try {
                        Path path=getAbsPath(markdownFile,imgUrl);

                        if(Files.exists(path)){
                            imgView.setImage(new Image("file:"+path));
                        }
                        Image image=imgView.getImage();
                        if(Files.exists(path)) {
                            pane.setOnContextMenuRequested(event -> {
                                imageSaveAsItem.setOnAction(event1 -> {
                                    FileChooser fileChooser = new FileChooser();
                                    fileChooser.setTitle("图片另存为");

                                    // 2. 设置默认文件名（与原始文件同名）和格式过滤
                                    String originalFileName = new File(path.toUri()).getName();
                                    fileChooser.setInitialFileName(originalFileName);
                                    fileChooser.getExtensionFilters().addAll(
                                            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                                            new FileChooser.ExtensionFilter("所有文件", "*.*")
                                    );

                                    // 3. 选择保存路径
                                    File targetFile = fileChooser.showSaveDialog(Main.scene.getWindow());
                                    if (targetFile == null) {
                                        return; // 用户取消
                                    }

                                    // 4. 用 Files.copy() 复制文件（支持覆盖已存在文件）
                                    try {
                                        // 复制原始文件到目标路径，若目标存在则覆盖
                                        Files.copy(
                                                path,
                                                targetFile.toPath(),
                                                StandardCopyOption.REPLACE_EXISTING // 覆盖选项
                                        );
                                        NotificationUtil.showNotification(Main.mainController.notice_pane, "图片已保存！");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                imageCopyItem.setOnAction(event1 -> {
                                    Clipboard clipboard = Clipboard.getSystemClipboard();

                                    // 创建剪贴板内容并放入图像
                                    ClipboardContent content = new ClipboardContent();
                                    content.putImage(image); // 直接放入 Image 对象

                                    // 复制到剪贴板
                                    clipboard.setContent(content);
                                    NotificationUtil.showNotification(Main.mainController.notice_pane, "图片已复制到剪切板！");

                                });
                                imageContextMenu.show(pane, event.getScreenX(), event.getScreenY());
                                event.consume();
                            });
                        }
                        append(Either.right(pane), "");
                        appendText("\n");
                        //append(Either.left("\n"), "");
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        append(Either.right(pane), "");
                            //append(Either.left("[图片加载失败: " + imgUrl + "]"), "");
                            appendText("\n");

                    }
                }
                continue;
            }

            // 处理普通文本行（包含链接、行内代码、粗体等）
            if (!line.trim().isEmpty()) {
                processTextLine(line,  linkPattern, inlineCodePattern, boldPattern);
               // append(Either.left("\n"), "");
                appendText("\n");
            } else {
                // 空行
                //append(Either.left("\n"), "");
                appendText("\n");

            }


        }
        // 在parseMarkdownWithStyles方法的循环结束后添加
        //在循环结束后，检查是否还有未处理的表格数据（避免表格结束在文档末尾）：
        if (inTable && tableRows.size() >= 1) {
            Node tableNode = createTableView(tableRows);
            append(Either.right(tableNode), "");
            appendText("\n");
            tableRows.clear();
        }

        // 如果代码块没有正确结束，确保添加剩余内容
        if (inCodeBlock && codeBlock.length() > 0) {
            append(Either.left(codeBlock.toString()), "");
            currentParagraph = getParagraphs().size() - 1;
            setParagraphStyle(currentParagraph, ("code-block"));
        }
        for(int i=0;i<getParagraphs().size();i++) {
            setParagraphStyle(i, "-fx-line-spacing: 10px");
        }
    }

    private void processTextLine(String line,Pattern linkPattern, Pattern inlineCodePattern, Pattern boldPattern) {
        if(!line.startsWith("\t")){
            line = "\t" + line;
        }
        // 首先处理行内代码，因为它们不应该包含其他格式
        List<TextSegment> segments = new ArrayList<>();
        int lastIndex = 0;

        // 查找所有行内代码
        Matcher codeMatcher = inlineCodePattern.matcher(line);
        while (codeMatcher.find()) {
            // 添加代码前的普通文本
            if (codeMatcher.start() > lastIndex) {
                String normalText = line.substring(lastIndex, codeMatcher.start());
                segments.add(new TextSegment(normalText, ""));
            }

            // 添加代码文本
            String codeText = codeMatcher.group(1);
            segments.add(new TextSegment(codeText, "code-inline"));

            lastIndex = codeMatcher.end();
        }

        // 添加剩余文本
        if (lastIndex < line.length()) {
            String remainingText = line.substring(lastIndex);
            segments.add(new TextSegment(remainingText, ""));
        }

        // 处理每个段落的链接和粗体
        for (TextSegment segment : segments) {
            if ("code-inline".equals(segment.style)) {
                // 代码段不处理链接和粗体
                append(Either.left(segment.text), segment.style);
            } else {
                // 普通文本段处理链接和粗体
                processFormattedText(segment.text,  linkPattern, boldPattern);
            }
        }
    }

    private void processFormattedText(String text, Pattern linkPattern, Pattern boldPattern) {

        int lastIndex = 0;
        // 同时匹配链接和粗体（优先匹配链接，避免冲突）
        Pattern combinedPattern = Pattern.compile("(\\[.*?]\\(.*?\\)|\\*\\*[^*]+\\*\\*)");
        Matcher matcher = combinedPattern.matcher(text);

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String normalText = text.substring(lastIndex, matcher.start());
                append(Either.left(normalText), "");
            }

            String matched = matcher.group();
            if (matched.startsWith("[")) {
                // 链接处理（不变）
                Matcher linkMatcher = linkPattern.matcher(matched);
                if (linkMatcher.find()) {
                    String linkText = linkMatcher.group(1);
                    String linkUrl = linkMatcher.group(2);
                    append(Either.left(linkText), "link:" + linkUrl + ";");
                }
            } else if (matched.startsWith("**")) {
                // 加粗处理：确保正确提取内容
                Matcher boldMatcher = boldPattern.matcher(matched);
                if (boldMatcher.find()) {
                    String boldText = boldMatcher.group(1); // 提取** 之间的内容
                    append(Either.left(boldText), "bold"); // 应用bold样式
                }
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            append(Either.left(text.substring(lastIndex)), "");
        }
    }


    private static double computeTextHeight(TextArea textArea) {
        int lines = textArea.getParagraphs().size();
        //Insets padding = textArea.getInsets();
        //System.out.println("textArea.getInsets():"+padding.getTop()+" "+padding.getBottom());
        double lineHeight = 14; // 1.2 行高系数
        //Insets padding = textArea.getInsets();
        //double height = lines * lineHeight + padding.getTop() + padding.getBottom();
        //double minHeight = lineHeight + padding.getTop() + padding.getBottom();
        //double maxHeight = 300; // 最大高度，可自定义
        //return Math.min(Math.max(height, minHeight), maxHeight);
        return lines * lineHeight+8;
    }

    // 辅助类，用于存储文本段及其样式
    private static class TextSegment {
        String text;
        String style;

        TextSegment(String text, String style) {
            this.text = text;
            this.style = style;
        }
    }

    public static void openUrl(String url) {
        try {
            if (url == null || url.isBlank()) return;

            // 清理掉前后空格、换行符、隐藏字符

            url = url.strip();
            url = url.replaceAll("[\\r\\n\\t]", "");

            if(url.toLowerCase().startsWith("http"))
            {
                URI uri = new URI(url);
                java.awt.Desktop.getDesktop().browse(uri);
            }else{
                if(Files.exists(Paths.get(url))){
                    //markdown里点击的链接打开的文件是相对路径，比对文档里的相对路径会出现问题，这里需要转换为使用绝对路径
                    TabpaneUtil.addCustomMarkdownTab(new File(new File(url).getAbsolutePath()),false);
                }else{
                    String finalUrl = url;
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误","文件【"+ finalUrl +"】不存在！");
                    });
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据表格数据生成JavaFX TableView组件
     */
    private Node createTableView(List<List<String>> tableRows) {
        // 创建表格视图
        CustomResultsetTableView tableView = new CustomResultsetTableView();
        tableView.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1px;");
        tableView.prefWidthProperty().bind(widthProperty().subtract(20)); // 自适应宽度
        //tableView.prefWidthProperty().bind(Main.mainController.sql_tabpane.widthProperty().subtract(20)); // 自适应宽度

        // 获取表头行（第一行）
        List<String> headers = tableRows.get(0);

        //如果只有一列，宽度计算不对单独处理
        if(headers.size()==1){

        }
        // 创建表格列（根据表头）
        for (int i = 0; i < headers.size(); i++) {
            final int columnIndex = i;
            TableColumn<List<String>, String> column = new TableColumn<>(headers.get(i));
            column.setCellFactory(col -> new CustomTableCell<>());
            column.setCellValueFactory(cellData -> {
                // 获取当前行数据，若索引越界则返回空字符串
                List<String> rowData = cellData.getValue();
                String value = (columnIndex < rowData.size()) ? rowData.get(columnIndex) : "";
                return new SimpleStringProperty(value);
            });
            // 列宽自适应内容（也可设置固定宽度）
            column.prefWidthProperty().bind(tableView.widthProperty().subtract(33).divide(headers.size()));
           // double avgColWidth = (tableView.getWidth() - 30) / headers.size();
            //Sstem.out.println("tableView.getWidth():"+tableView.getWidth()
            // ;

            //System.out.println("avgColWidth:"+avgColWidth);
            //column.setPrefWidth(Math.max(80,avgColWidth));
            tableView.getColumns().add(column);
            column.setSortable(false);
            column.setReorderable(false);
        }

        // 添加表格内容行（跳过表头行）
        for (int i = 1; i < tableRows.size(); i++) {
            tableView.getItems().add(tableRows.get(i));
        }

        // 关键：计算表格总高度（表头高度 + 内容行高度 + 边框）
        double rowHeight = 21; // 单行高度（可根据字体调整）
        double headerHeight = 18; // 表头高度
        int dataRowCount = tableView.getItems().size(); // 实际数据行数
        double totalHeight = headerHeight + (dataRowCount * rowHeight) +2; // +4是边框高度

        // 限制最大高度（避免表格过高超出容器）
        double maxHeight = 300; // 最大高度阈值
        totalHeight = Math.min(totalHeight, maxHeight);

        // 设置表格固定高度（根据内容计算）
        tableView.setPrefHeight(totalHeight);
        tableView.setMaxHeight(totalHeight);
        tableView.setMinHeight(totalHeight);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("MarkdownTableView");
        tableView.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) {
                //textAreaContextMenu.hide();
                // TextArea 获取焦点时，取消 GenericStyledArea 的选择
                tableView.getSelectionModel().clearSelection();
                //customGenericStyledArea.deselect(); // 清除选区
            }else{
                ((CustomGenericStyledArea)tableView.getParent().getParent().getParent().getParent().getParent()).deselect();
            }
        });

        return tableView;
    }

    private static Path getAbsPath(File file,String url){
        Path appDir = Path.of(System.getProperty("user.dir"));
        Path baseFile = Path.of(file.getPath());
        Path relative  = Paths.get(url);
        Path absolutePath = baseFile
                .getParent()      // 以“文件所在目录”为基准
                .resolve(relative)
                .normalize();
        Path path = absolutePath.subpath(
                appDir.getNameCount(),
                absolutePath.getNameCount()
        );
        return path;
    }




}
