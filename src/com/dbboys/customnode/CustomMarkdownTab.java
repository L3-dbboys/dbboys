package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.ctrl.SqlTabController;
import com.dbboys.util.AlterUtil;
import com.dbboys.util.TabpaneUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.model.StyledSegment;
import org.reactfx.util.Either;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

public class CustomMarkdownTab extends CustomTab{
    private static final Logger log = LogManager.getLogger(CustomMarkdownTab.class);

    public CustomMarkdownEditCodeArea customMarkdownEditCodeArea;
    //public CustomInlineCssTextArea customInlineCssTextArea;
    public CustomGenericStyledArea customGenericStyledArea;

    public CustomSearchReplaceVbox searchReplaceBox=new CustomSearchReplaceVbox(null);
    public CustomSearchReplaceVbox customSearchVbox=new CustomSearchReplaceVbox(null);

    //sql编辑框以上控件
    public CustomMarkdownTab(File file,boolean modifiable) {
        super(file.getName());
        sql_file_path=file.getAbsolutePath();
        setTooltip(new Tooltip(sql_file_path.equals("")?"新建脚本未保存到磁盘":sql_file_path));




        customGenericStyledArea=new CustomGenericStyledArea(file);

        CustomInfoStackPane markdown = new CustomInfoStackPane(customGenericStyledArea);
        customMarkdownEditCodeArea=new CustomMarkdownEditCodeArea();
        VirtualizedScrollPane virtualizedScrollPane=new VirtualizedScrollPane(customMarkdownEditCodeArea);
        markdown.getChildren().add(0,virtualizedScrollPane);
        virtualizedScrollPane.visibleProperty().bind(customGenericStyledArea.getParent().visibleProperty().not());
        Button modifyBtn = new Button("");
        //customMarkdownEditCodeArea.setStyle("-fx-font-family: system;");


        //编辑界面加载搜索面板
        searchReplaceBox.setMaxWidth(300);
        searchReplaceBox.setMaxHeight(26);
        searchReplaceBox.codeArea=customMarkdownEditCodeArea;
        StackPane.setAlignment(searchReplaceBox,Pos.TOP_RIGHT);
        markdown.getChildren().add(1,searchReplaceBox);
        customMarkdownEditCodeArea.searchReplaceBox=searchReplaceBox;
        StackPane.setMargin(searchReplaceBox, new Insets(2, 17, 0, 0));

        //浏览界面加搜索面板
        customSearchVbox.setMaxWidth(280);
        customSearchVbox.setMaxHeight(26);

        customSearchVbox.tobottomBtn.visibleProperty().unbind();
        customSearchVbox.tobottomBtn.setVisible(false);
        customSearchVbox.tobottomBtn.setManaged(false);

        customSearchVbox.totopBtn.visibleProperty().unbind();
        customSearchVbox.totopBtn.setVisible(false);
        customSearchVbox.totopBtn.setManaged(false);


        customSearchVbox.codeArea=customGenericStyledArea;
        StackPane.setAlignment(customSearchVbox,Pos.TOP_RIGHT);
        markdown.getChildren().add(4,customSearchVbox);
        customGenericStyledArea.searchReplaceBox=customSearchVbox;
        StackPane.setMargin(customSearchVbox, new Insets(2, 17, 0, 0));

        SVGPath modifybtn_icon = new SVGPath();
        modifybtn_icon.setContent("M20.625 19.5938 L3.375 19.5938 Q3.0781 19.5938 2.8438 19.8125 Q2.625 20.0312 2.625 20.3438 L2.625 21.1875 Q2.625 21.25 2.6875 21.3125 Q2.75 21.375 2.8125 21.375 L21.1875 21.375 Q21.2656 21.375 21.3125 21.3125 Q21.375 21.25 21.375 21.1875 L21.375 20.3438 Q21.375 20.0312 21.1562 19.8125 Q20.9375 19.5938 20.625 19.5938 ZM6.0469 17.625 Q6.0781 17.625 6.1094 17.625 Q6.1406 17.625 6.1875 17.625 L10.125 16.9219 Q10.1562 16.9219 10.1875 16.9062 Q10.2188 16.875 10.25 16.8438 L20.1875 6.9062 Q20.2031 6.8906 20.2188 6.8438 Q20.25 6.7969 20.25 6.75 Q20.25 6.7031 20.2188 6.6719 Q20.2031 6.625 20.1875 6.5781 L20.1875 6.5781 L16.2969 2.6875 Q16.25 2.6406 16.2031 2.6406 Q16.1719 2.625 16.125 2.625 Q16.0781 2.625 16.0312 2.6406 Q15.9844 2.6406 15.9688 2.6875 L6.0312 12.625 Q6 12.6562 5.9688 12.6875 Q5.9531 12.7188 5.9531 12.75 L5.25 16.6875 Q5.25 16.7344 5.25 16.7656 Q5.25 16.7969 5.25 16.8281 Q5.25 16.9844 5.3125 17.1406 Q5.375 17.2969 5.4844 17.3906 L5.4844 17.3906 Q5.6094 17.5 5.75 17.5625 Q5.8906 17.625 6.0469 17.625 L6.0469 17.625 Z");
        modifybtn_icon.setScaleX(0.6);
        modifybtn_icon.setScaleY(0.6);
        modifybtn_icon.setFill(Color.valueOf("#074675"));
        modifyBtn.setGraphic(new Group(modifybtn_icon));
        modifyBtn.setFocusTraversable(false);
        modifyBtn.getStyleClass().add("codearea-camera-button");
        markdown.getChildren().add(modifyBtn);
        StackPane.setMargin(modifyBtn, new Insets(0, 15, 15, 0));
        StackPane.setAlignment(modifyBtn, Pos.BOTTOM_RIGHT);
        markdown.codearea_snap_button.visibleProperty().bind(modifyBtn.visibleProperty());

                /*
                Tooltip modifytooltip = new Tooltip();
                modifytooltip.setText("编辑");
                modifyBtn.setTooltip(modifytooltip);

                 */

        Button viewBtn = new Button("");
        SVGPath viewbtn_icon = new SVGPath();
        viewbtn_icon.setContent("M4.5156 1.5312 L21 1.5312 L22.5156 3.0469 L22.5156 10.9844 Q21.7969 10.6562 21 10.5312 L21 3.0469 L13.5156 3.0469 L13.5156 19.3594 L11.8281 21.0469 L4.5156 21.0469 L3 19.5312 L3 3.0469 L4.5156 1.5312 ZM4.5156 19.5312 L12 19.5312 L12 3.0469 L4.5156 3.0469 L4.5156 19.5312 ZM20.0625 12.0469 Q19.1094 12.0938 18.2812 12.5781 L18.2344 12.5781 Q17.5938 12.9531 17.1406 13.5781 Q16.6875 14.2031 16.5156 14.9688 Q16.3438 15.7188 16.4688 16.4844 Q16.6094 17.2344 17.0469 17.875 L13.5156 21.3906 L14.5625 22.4688 L18.0938 18.9062 Q18.8438 19.4219 19.7188 19.5312 Q20.5938 19.625 21.4375 19.3281 Q22.2969 19.0312 22.9219 18.3906 Q23.5469 17.7344 23.7969 16.8906 Q24.0469 16.0312 23.9062 15.1562 Q23.7656 14.2812 23.2188 13.5625 Q22.6875 12.8438 21.8906 12.4375 Q21.0312 12.0156 20.0625 12.0469 ZM20.4219 18.0469 Q19.625 18.125 18.9531 17.6562 Q18.3906 17.2812 18.125 16.6562 Q17.8594 16.0312 17.9844 15.3594 Q18.125 14.6875 18.6094 14.2188 Q19.1094 13.7344 19.7656 13.6094 Q20.4219 13.4688 21.0625 13.7344 Q21.7031 13.9844 22.0781 14.5469 Q22.4219 15.0469 22.4531 15.6406 Q22.4844 16.2188 22.2344 16.7656 Q21.9844 17.2969 21.5 17.6406 Q21.0312 17.9688 20.4219 18.0469 Z");
        viewbtn_icon.setScaleX(0.5);
        viewbtn_icon.setScaleY(0.5);
        viewbtn_icon.setFill(Color.valueOf("#074675"));
        viewBtn.setGraphic(new Group(viewbtn_icon));
        viewBtn.setFocusTraversable(false);
        viewBtn.getStyleClass().add("codearea-camera-button");
        Tooltip viewtooltip = new Tooltip();
                /*
                viewtooltip.setText("预览");
                viewBtn.setTooltip(viewtooltip);

                 */


        customGenericStyledArea.modifyItem.setOnAction(event -> {
            modifyBtn.fire();
        });


        customMarkdownEditCodeArea.viewItem.setOnAction(event -> {
            viewBtn.fire();
        });
        markdown.getChildren().add(viewBtn);
        StackPane.setMargin(viewBtn, new Insets(0, 15, 15, 0));
        StackPane.setAlignment(viewBtn, Pos.BOTTOM_RIGHT);
        viewBtn.visibleProperty().bind(modifyBtn.visibleProperty().not());


        modifyBtn.setOnAction(event -> {
            customGenericStyledArea.getParent().setVisible(!customGenericStyledArea.getParent().isVisible());
            customMarkdownEditCodeArea.requestFocus();
            modifyBtn.setVisible(false);
            customSearchVbox.setVisible(false);
            //customInlineCssTextArea.setVisible(false);
        });

        viewBtn.setOnAction(event -> {
            if(getTitle().startsWith("*")){
                sql_save_button.fire();
            }
            customGenericStyledArea.getParent().setVisible(!customGenericStyledArea.getParent().isVisible());
            modifyBtn.setVisible(true);
            String content;
            try {
                content = Files.readString(Paths.get(sql_file_path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            customGenericStyledArea.clear();
            customGenericStyledArea.parseMarkdownWithStyles(content);
            customGenericStyledArea.showParagraphAtTop(0);
        });
        String content;
        try {
            content = Files.readString(Paths.get(sql_file_path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        customMarkdownEditCodeArea.replaceText(content);
        customMarkdownEditCodeArea.showParagraphAtTop(0);

        //customGenericStyledArea.replaceText(content);
        customGenericStyledArea.clear();
        customGenericStyledArea.parseMarkdownWithStyles(content);
        customGenericStyledArea.showParagraphAtTop(0);
        customMarkdownEditCodeArea.sql_save_button=sql_save_button;


        //保存按钮事件
        sql_save_button.setOnAction(event->{

            String codeAreaText=customMarkdownEditCodeArea.getText();

            Platform.runLater(()->{
                //先删除鼠标点击响应事件，避免重复添加导致编辑后点击弹出多次
                //customGenericStyledArea.removeEventHandler(MouseEvent.MOUSE_CLICKED, customGenericStyledArea.clickHandler);
                //重置css避免重复
                //customGenericStyledArea.setStyle(0,customGenericStyledArea.getText().length(),"");
                //customGenericStyledArea.replaceText(customMarkdownEditCodeArea.getText());
                //customGenericStyledArea.renderMarkdown();
            });
            if(sql_file_path.equals("")){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存文件");
                fileChooser.setInitialFileName(getTitle().replaceAll("\\*",""));
                File filename = fileChooser.showSaveDialog(Main.scene.getWindow());
                if (filename != null) { //用户选择了确认
                    try (FileWriter writer = new FileWriter(filename)) {
                        writer.write(codeAreaText);
                        setTitle(filename.getName());
                        sql_file_path=filename.getAbsolutePath();
                        sql_save_button.setDisable(true);
                        setTooltip(new Tooltip(sql_file_path));
                    } catch (IOException e) {
                        AlterUtil.CustomAlert("错误",e.getMessage());
                    }

                }
            }else{
                try {
                    Files.writeString(Paths.get(sql_file_path), codeAreaText);
                    setTitle(getTitle().replaceAll("\\*",""));
                    sql_save_button.setDisable(true);
                } catch (IOException e) {
                    AlterUtil.CustomAlert("错误",e.getMessage());
                }
            }

        });
        setContent(markdown);

        //关闭窗口事件响应
        setOnCloseRequest(event1 -> {
            /*避免关闭后双击无响应*/
            if(Main.mainController.sqlTabPane.getTabs().size()==1){
                Main.mainController.sqlTabPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY &&event.getClickCount() == 2) {
                        TabpaneUtil.addCustomSqlTab(null);
                    }
                });
            }

            if(getTitle().startsWith("*")) {
                boolean confirmClose = AlterUtil.CustomAlertConfirm(
                        "关闭文件",
                        "文件【" + getTitle().replaceAll("\\*", "") + "】未保存，确定要关闭吗？"
                );
                if (!confirmClose) { // 若用户取消，则阻止关闭
                    event1.consume();
                }
            }

        });

        if(modifiable){
            modifyBtn.fire();
            customMarkdownEditCodeArea.requestFocus();
        }

        customMarkdownEditCodeArea.codeAreaPasteItem.setOnAction(event -> {
            Clipboard fxClipboard = Clipboard.getSystemClipboard();
            java.awt.datatransfer.Clipboard awtClipboard =
                    Toolkit.getDefaultToolkit().getSystemClipboard();

            try {
                // 关键：只要剪贴板里有“文字语义”，就不要当图片
                boolean hasText =
                        fxClipboard.hasString()
                                || fxClipboard.hasHtml();

                // 只有“纯图片”才处理为截图
                if (!hasText && handleAwtImagePaste()) {
                    return;
                }

                // 系统复制的图片文件（不影响 Word）
                if (handleFilePaste()) {
                    return;
                }

                // URL 图片兜底
                if (handleUrlImagePaste()) {
                    return;
                }

                // 兜底：普通粘贴（Word / 文本 / HTML）
                customMarkdownEditCodeArea.paste();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                customMarkdownEditCodeArea.paste();
            }
        });


    }

    private static int getNextIndex(File folder) {
        int maxIndex = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                if (name.startsWith("img")) {
                    String numPart = name.substring(3); // 去掉 "img"
                    int dotIndex = numPart.lastIndexOf('.');
                    if (dotIndex > 0) {
                        numPart = numPart.substring(0, dotIndex); // 去掉扩展名
                    }
                    try {
                        int num = Integer.parseInt(numPart);
                        if (num > maxIndex) maxIndex = num;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return maxIndex + 1;
    }


    private boolean handleAwtImagePaste() throws Exception {
        java.awt.datatransfer.Clipboard awtClipboard =
                Toolkit.getDefaultToolkit().getSystemClipboard();

        if (!awtClipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
            return false;
        }

        java.awt.Image awtImage = (java.awt.Image) awtClipboard.getData(DataFlavor.imageFlavor);
        if (awtImage == null) return false;

        BufferedImage bufferedImage = new BufferedImage(
                awtImage.getWidth(null),
                awtImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(awtImage, 0, 0, null);
        g.dispose();

        File targetFile = createNextImageFile("png");
        ImageIO.write(bufferedImage, "png", targetFile);
        insertMarkdownImage(targetFile.getName());
        return true;
    }

    private boolean handleFilePaste() throws IOException {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (!clipboard.hasFiles()) return false;

        java.util.List<File> files = clipboard.getFiles();
        if (files.size() != 1) return false;

        File src = files.get(0);
        if (!isImageFile(src)) return false;

        File target = createNextImageFile(getExt(src.getName()));
        Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        insertMarkdownImage(target.getName());
        return true;
    }

    private boolean handleUrlImagePaste() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasUrl()) return false;

        try {
            URI uri = new URI(clipboard.getUrl());
            if (!"file".equalsIgnoreCase(uri.getScheme())) return false;

            File file = new File(uri);
            if (!file.exists() || !isImageFile(file)) return false;

            File target = createNextImageFile(getExt(file.getName()));
            Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            insertMarkdownImage(target.getName());
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    private File createNextImageFile(String ext) {
        File imgFolder = new File(new File(sql_file_path).getParent(), "img");
        if (!imgFolder.exists()) imgFolder.mkdirs();

        String name = "img" + getNextIndex(imgFolder) + "." + ext;
        return new File(imgFolder, name);
    }

    private void insertMarkdownImage(String fileName) {
        IndexRange sel = customGenericStyledArea.getSelection();
        if(sel != null){
            customMarkdownEditCodeArea.replaceSelection(
                    "![" + fileName + "](img/" + fileName + ")"
            );
        }else{
            int pos = customMarkdownEditCodeArea.getCaretPosition();
            customMarkdownEditCodeArea.insertText(
                    pos,
                    "![" + fileName + "](img/" + fileName + ")"
            );
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg")
                || name.endsWith(".jpeg") || name.endsWith(".gif")
                || name.endsWith(".bmp");
    }

    private String getExt(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }


    private boolean isPureScreenshot(Clipboard fxClipboard,
                                     java.awt.datatransfer.Clipboard awtClipboard) {

        // Word / 富文本一定有文字
        if (fxClipboard.hasString() || fxClipboard.hasHtml()) {
            return false;
        }

        // 只有“没有任何文字”的 image，才认为是截图
        return awtClipboard.isDataFlavorAvailable(DataFlavor.imageFlavor);
    }



}

