package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.customnode.CustomSpaceChart;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SnapshotUtil {
    private static final Logger log = LogManager.getLogger(SnapshotUtil.class);

    //设置实例信息显示样式

    public static void copyToClipboard(WritableImage image, StackPane noticPane) {
        try {
            // 1. 创建临时文件
            File tempFile = new File(System.getProperty("java.io.tmpdir"), "dbboys_screenshot.png");
            tempFile.deleteOnExit();

            // 2. 写入文件
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, "png", tempFile);

            //快速释放内存，这一步很重要，避免内存大量占用
            image=null;
            bufferedImage = null;
            System.gc();

            // 3. 把文件放入剪切板（支持文件粘贴）
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] { DataFlavor.javaFileListFlavor };
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.javaFileListFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor)
                        throws UnsupportedFlavorException, IOException {
                    if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                        // 兼容 Java 8
                        return Collections.singletonList(tempFile);
                    }
                    throw new UnsupportedFlavorException(flavor);
                }
            };

            clipboard.setContents(transferable, null);

            NotificationUtil.showNotification(
                    noticPane,
                    "截图已保存到临时文件并复制到剪切板"
            );

        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.showNotification(
                    Main.mainController.noticePane,
                    "截图写入临时文件失败：" + e.getMessage()
            );
        }
    }




    public static void snapshotRoot(){
        double scale = 2.0; // 比如 2 倍分辨率
        WritableImage image = new WritableImage(
                (int)(Main.scene.getRoot().getBoundsInParent().getWidth() * scale),
                (int)(Main.scene.getRoot().getBoundsInParent().getHeight() * scale)
        );
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(scale, scale));
        Main.scene.getRoot().snapshot(params,image);
        //WritableImage image = snapshot_root_button.getScene().getRoot().snapshot(null,null);
        copyToClipboard(image,Main.mainController.noticePane);

    }

    public static void snapshotNode(Node node){
        double scale = 2.0; // 比如 2 倍分辨率
        WritableImage image = new WritableImage(
                (int)(node.getBoundsInParent().getWidth() * scale),
                (int)(node.getBoundsInParent().getHeight() * scale)
        );
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(scale, scale));
        node.snapshot(params,image);
        //WritableImage image = snapshot_root_button.getScene().getRoot().snapshot(null,null);
        copyToClipboard(image,Main.mainController.noticePane);

    }




    public static void snapshotTableView(TableView<?> tableView) {

        // 1. 关闭虚拟化
        tableView.setFixedCellSize(21); // 必须设置
        int rowCount = tableView.getItems().size();


        double originalPrefHeight = tableView.getPrefHeight();
        double originalMinHeight  = tableView.getMinHeight();
        double originalMaxHeight  = tableView.getMaxHeight();

        double headerHeight = 21; // 表头高度（经验值）
        double newHeight = headerHeight + rowCount * tableView.getFixedCellSize();

        tableView.setPrefHeight(newHeight);
        tableView.setMinHeight(newHeight);
        tableView.setMaxHeight(newHeight);

        // 2. 强制 layout（非常关键）
        tableView.applyCss();
        tableView.layout();

        // 3. 高清截图
        double scale = 2.0;
        WritableImage image = new WritableImage(
                (int) (tableView.getWidth() * scale),
                (int) (newHeight * scale)
        );

        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(scale, scale));

        tableView.snapshot(params, image);


        copyToClipboard(image,Main.mainController.noticePane);

        // 4. 恢复原状态
        tableView.setPrefHeight(originalPrefHeight);
        tableView.setMinHeight(originalMinHeight);
        tableView.setMaxHeight(originalMaxHeight);
    }




}

