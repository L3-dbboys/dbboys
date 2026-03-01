package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.customnode.CustomUserTextField;
import com.dbboys.i18n.I18n;
import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DownloadTaskWrapper {
    private static final Logger log = LogManager.getLogger(DownloadTaskWrapper.class);

    private Task<Void> task;
    private final Object source;
    private String downloadUrl;
    private final File file;
    private final File tempFile; // 临时文件
    private TableView tableView;
    private ResultSetMetaData metaData;


    private final Node rootPane; // StackPane 的子节点
    private final ProgressBar progressBar;
    private final Label nameLabel;
    private final Label progressLabel;
    private final Label speedLabel;
    private final Button pauseButton;
    private final Button resumeButton;
    private final Button stopButton;

    private volatile boolean cancelled = false;
    private volatile boolean paused = false;
    private volatile long downloadedBytes = 0;
    private long totalBytes = 0;

    private final boolean autoCloseOnComplete;
    private final StackPane hostStackPane;
    private final boolean installerMode;
    private final CustomUserTextField installerRemotePathField;
    private final CustomUserTextField installerInstallFilePathField;

    public DownloadTaskWrapper(
            Object source,
            File file,
            boolean autoCloseOnComplete,
            ResultSetMetaData metaData,
            StackPane hostStackPane,
            boolean installerMode,
            CustomUserTextField installerRemotePathField,
            CustomUserTextField installerInstallFilePathField
    ) {
        this.source = source;
        this.file = file;
        this.metaData = metaData;
        this.tempFile = new File(file.getAbsolutePath() + ".download"); // 临时文件
        this.autoCloseOnComplete = autoCloseOnComplete;
        this.hostStackPane = hostStackPane;
        this.installerMode = installerMode;
        this.installerRemotePathField = installerRemotePathField;
        this.installerInstallFilePathField = installerInstallFilePathField;

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(100);
        nameLabel = new Label();
        nameLabel.setTranslateY(-0.5);
        progressLabel = new Label("0%");
        progressLabel.setMinWidth(20);
        progressLabel.setTranslateY(-0.5);
        speedLabel = new Label();
        speedLabel.setTranslateY(-0.5);
        nameLabel.getStyleClass().add("download");
        progressLabel.getStyleClass().add("download");
        speedLabel.getStyleClass().add("download");

        pauseButton = new Button("");
        Tooltip pauseTooltip = new Tooltip();
        pauseTooltip.textProperty().bind(I18n.bind("download.tooltip.pause", "暂停下载"));
        pauseButton.setTooltip(pauseTooltip);
        resumeButton = new Button("");
        Tooltip resumeTooltip = new Tooltip();
        resumeTooltip.textProperty().bind(I18n.bind("download.tooltip.resume", "恢复下载"));
        resumeButton.setTooltip(resumeTooltip);
        stopButton = new Button("");
        Tooltip stopTooltip = new Tooltip();
        stopTooltip.textProperty().bind(I18n.bind("download.tooltip.cancel", "取消下载并删除未完成文件"));
        stopButton.setTooltip(stopTooltip);
        StackPane pauseStackPane = new StackPane();
        pauseStackPane.getChildren().addAll(pauseButton, resumeButton);

        HBox buttonBox = new HBox(5, pauseStackPane, stopButton);
        //rootPane.setStyle("-fx-padding: 10; -fx-background-color: #f8f8f8;");

        pauseButton.setOnAction(e -> {
            pauseButton.setVisible(!pauseButton.isVisible());
            pauseDownload();
        });
        resumeButton.visibleProperty().bind(pauseButton.visibleProperty().not());
        resumeButton.setOnAction(e -> {
            pauseButton.setVisible(!pauseButton.isVisible());
            resumeDownload();
        });

        resumeButton.setGraphic(IconFactory.group(IconPaths.DOWNLOAD_RESUME, 0.5));
        resumeButton.getStyleClass().add("little-custom-button");
        resumeButton.setFocusTraversable(false);

        pauseButton.setGraphic(IconFactory.group(IconPaths.DOWNLOAD_PAUSE, 0.6));
        pauseButton.getStyleClass().add("little-custom-button");
        pauseButton.setFocusTraversable(false);

        stopButton.setGraphic(IconFactory.group(IconPaths.SQL_STOP, 0.5, IconFactory.dangerColor()));
        stopButton.getStyleClass().add("little-custom-button");
        stopButton.setFocusTraversable(false);
        stopButton.setOnAction(e -> cancelDownload());
        if (source instanceof String) {
            this.downloadUrl = (String) source;
            if (installerMode) {
                HBox topLine = new HBox(6, progressBar, progressLabel, buttonBox);
                topLine.setAlignment(Pos.CENTER_LEFT);
                HBox textLine = new HBox(6, nameLabel, speedLabel);
                textLine.setAlignment(Pos.CENTER_LEFT);
                rootPane = new VBox(2, topLine, textLine);
            } else {
                HBox line = new HBox(6, nameLabel, speedLabel, progressBar, progressLabel, buttonBox);
                line.setAlignment(Pos.CENTER_RIGHT);
                rootPane = line;
            }
            nameLabel.textProperty().bind(Bindings.createStringBinding(
                    () -> (pauseButton.isVisible()
                            ? I18n.t("download.label.downloading_prefix", "正在下载：")
                            : I18n.t("download.label.paused_prefix", "已暂停下载：")) + file.getName(),
                    I18n.localeProperty(),
                    pauseButton.visibleProperty()
            ));
            speedLabel.textProperty().bind(I18n.bind("download.label.waiting", "等待开始..."));

        } else {
            this.tableView = (TableView) source;
            HBox line = new HBox(6, nameLabel, progressBar, progressLabel, stopButton);
            line.setAlignment(Pos.CENTER_RIGHT);
            rootPane = line;
            nameLabel.textProperty().bind(Bindings.createStringBinding(
                    () -> I18n.t("download.label.exporting_prefix", "正在导出：") + file.getName(),
                    I18n.localeProperty()
            ));

        }
        if (installerMode) {
            StackPane.setAlignment(rootPane, Pos.CENTER_LEFT);
        }

    }

    public Node getRootPane() {
        return rootPane;
    }

    public void start() {
        startNewTask(false);
    }

    private synchronized void startNewTask(boolean isResume) {
        if (task != null && task.isRunning()) return;

        cancelled = false;
        if (source instanceof String) {
            task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateProgress(0, 1);
                    InputStream in = null;
                    RandomAccessFile out = null;
                    try {
                        long start = downloadedBytes;
                        if (downloadUrl.toLowerCase().startsWith("http")) {
                            HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
                            conn.setRequestProperty("User-Agent", "JavaFX Downloader");
                            if (isResume && start > 0) conn.setRequestProperty("Range", "bytes=" + start + "-");
                            conn.connect();

                            int code = conn.getResponseCode();
                            if (code != 200 && code != 206) throw new IOException(I18n.t("download.error.connection_failed", "连接失败: HTTP ") + code);
                            if (totalBytes == 0) totalBytes = conn.getContentLengthLong() + start;
                            in = conn.getInputStream();
                        } else {
                            Path src = Paths.get(downloadUrl);
                            if (totalBytes == 0) totalBytes = Files.size(src);
                            in = Files.newInputStream(src);
                            if (start > 0) in.skip(start);
                        }

                        out = new RandomAccessFile(tempFile, "rw");
                        out.seek(start);

                        byte[] buffer = new byte[8192];
                        int len;
                        long lastUpdate = System.currentTimeMillis();
                        long lastRead = downloadedBytes;
                        double smoothedSpeed = 0, alpha = 0.3;

                        while (!cancelled && (len = in.read(buffer)) != -1) {
                            while (paused) {
                                Thread.sleep(200);
                                if (cancelled) break;
                            }
                            if (cancelled) break;

                            out.write(buffer, 0, len);
                            downloadedBytes += len;
                            updateProgress(downloadedBytes, totalBytes);

                            long now = System.currentTimeMillis();
                            if (now - lastUpdate >= 1000) {
                                long delta = downloadedBytes - lastRead;
                                double currentSpeed = delta / ((now - lastUpdate) / 1000.0);
                                smoothedSpeed = alpha * currentSpeed + (1 - alpha) * smoothedSpeed;

                                String speedText = smoothedSpeed >= 1024 * 1024 ?
                                        String.format("%.2f MB/s", smoothedSpeed / 1024 / 1024) :
                                        String.format("%.2f KB/s", smoothedSpeed / 1024);

                                updateMessage(String.format(
                                        I18n.t("download.message.progress", "已下载: %.2f / %.2f MB  速度: %s"),
                                        downloadedBytes / 1024.0 / 1024.0,
                                        totalBytes / 1024.0 / 1024.0,
                                        speedText
                                ));
                                lastUpdate = now;
                                lastRead = downloadedBytes;
                            }
                        }



                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (in != null) in.close();
                        if (out != null) out.close();
                        if (cancelled) {
                            updateMessage(I18n.t("download.message.stopped_deleted", "下载已停止并删除文件"));
                        } else if (paused) {
                            updateMessage(I18n.t("download.message.paused", "已暂停"));
                        } else {
                            updateMessage(I18n.t("download.message.completed", "下载完成"));
                            updateProgress(1, 1);
                            boolean moved = true;
                            if (tempFile.exists()) {
                                moved = moveTempToTargetWithRetry();
                                if (!moved) {
                                    updateMessage(I18n.t("download.message.rename_failed", "下载完成，但重命名失败"));
                                }
                            }
                            boolean finalMoved = moved;
                            Platform.runLater(() -> {
                                if (!finalMoved) {
                                    AlterUtil.CustomAlert(
                                            I18n.t("download.error.title", "下载失败"),
                                            I18n.t("download.message.rename_failed", "下载完成，但重命名失败")
                                    );
                                    return;
                                }
                                if (autoCloseOnComplete) stackPaneRemoveSelf();
                                if(file.getName().contains("dbboys.upgrade.")){
                                    Main.mainController.checkVersion();
                                }else{
                                    if (installerMode && installerInstallFilePathField != null && installerRemotePathField != null) {
                                        installerInstallFilePathField.setText(file.getAbsolutePath());
                                        installerRemotePathField.setText("/tmp/" + file.getName());
                                    }
                                    NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("download.notice.completed", "下载已完成！"));
                                }
                                //rootPane.setStyle("-fx-background-color: #c8e6c9; -fx-padding: 10;");
                            });
                        }
                    }
                    return null;
                }
            };

            task.setOnFailed(e -> {
                stackPaneRemoveSelf();
                Platform.runLater(() ->
                {
                    AlterUtil.CustomAlert(I18n.t("download.error.title", "下载失败"), task.getException().getMessage());
                });
            });

            progressBar.progressProperty().bind(task.progressProperty());
            speedLabel.textProperty().unbind();
            speedLabel.textProperty().bind(task.messageProperty());
            progressLabel.textProperty().bind(task.progressProperty().multiply(100).asString("%.0f%%"));
        }else{
            task = createExportTask(tableView,file);
            progressBar.progressProperty().bind(task.progressProperty());
            progressLabel.textProperty().bind(task.progressProperty().multiply(100).asString("%.0f%%"));
        }

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private <T> Task<Void> createExportTask(TableView<T> tableView, File file) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(0,1);
                Workbook workbook = new SXSSFWorkbook(10000);
                Sheet sheet = workbook.createSheet(I18n.t("download.export.sheet_name", "数据"));

                ObservableList<TableColumn<T, ?>> columns = tableView.getColumns();
                ObservableList<T> items = tableView.getItems();
                int rowsTotal = items.size();
                final int excelCellCharLimit = 32767;

                CellStyle headerStyle = workbook.createCellStyle();  // 统一接口，不用 HSSFCellStyle
                Font headerFont = workbook.createFont();             // 统一接口，不用 HSSFFont
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                Row headerRow = sheet.createRow(0);

                for (int j = 1; j < metaData.getColumnCount()+1; j++) {
                    Cell cell = headerRow.createCell(j-1);
                    cell.setCellValue(metaData.getColumnName(j));
                    cell.setCellStyle(headerStyle);
                }


                long lastUpdate = System.currentTimeMillis();

                for (int i = 0; i < items.size(); i++) {
                    if (cancelled) break;
                    if (System.currentTimeMillis() - lastUpdate >= 1000) {
                        updateProgress(i+1,rowsTotal );
                        lastUpdate=System.currentTimeMillis();
                    }
                    Row row = sheet.createRow(i+1);
                    T rowData = items.get(i);
                    for (int j = 1; j < columns.size(); j++) {
                        if (cancelled) break;
                        TableColumn<T, ?> column = columns.get(j);
                        Object value = column.getCellData(rowData);
                        Cell cell = row.createCell(j-1);
                        String text;
                        if (value == null) {
                            text = "";
                        } else {
                            text = value.toString();
                            if (text.length() > excelCellCharLimit) {
                                // 保留前段内容，避免超长 LOB/文本导致 POI/Excel 卡死
                                text = text.substring(0, excelCellCharLimit - 16) +
                                        String.format("...(len=%d)", text.length());
                            }
                        }
                        cell.setCellValue(text);
                    }
                }
                if(cancelled){
                }else{
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        workbook.write(fos);
                    }
                    workbook.close();
                    updateProgress(1,1);
                    Platform.runLater(() -> {

                        NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("download.notice.export_completed", "导出已完成！"));
                        //rootPane.setStyle("-fx-background-color: #c8e6c9; -fx-padding: 10;");
                        if (autoCloseOnComplete) stackPaneRemoveSelf();
                    });
                }
                workbook.close();
                return null;
            }
        };
    }

    private boolean moveTempToTargetWithRetry() {
        Path sourcePath = tempFile.toPath();
        Path targetPath = file.toPath();
        int maxRetries = 6;
        long waitMillis = 120;

        for (int i = 0; i < maxRetries; i++) {
            try {
                Path parent = targetPath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                try {
                    Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException atomicMoveError) {
                    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                return true;
            } catch (IOException e) {
                if (!tempFile.exists() && file.exists()) {
                    return true;
                }
                if (i == maxRetries - 1) {
                    log.warn("Failed to finalize download file after retries. temp={}, target={}", sourcePath, targetPath, e);
                    return false;
                }
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                waitMillis = Math.min(waitMillis * 2, 1000);
            }
        }
        return false;
    }


    private void stackPaneRemoveSelf() {
        //从list里移除当前对象，避免取消后有空白轮询显示
        DownloadManagerUtil.removeDownload(this, hostStackPane);
        Platform.runLater(() -> {
            StackPane parent = (StackPane) rootPane.getParent();
            if (parent != null) parent.getChildren().remove(rootPane);
        });
    }

    public void pauseDownload() {
        if (!paused) {
            paused = true;
        }
    }

    public void resumeDownload() {
        if (paused) {
            paused = false;
            startNewTask(true);
        }
    }

    public void cancelDownload() {
        if (cancelled) return;

        cancelled = true;
        paused = false;

        if (task != null) task.cancel();

        new Thread(() -> {
            try {
                if (task != null) task.get(); // 等待 Task 完全结束
            } catch (Exception ignored) {}

            // Task 完全结束后删除文件
            boolean deleted = false;
            int retries = 5;
            while (!deleted && retries-- > 0) {
                if (tempFile.exists()) deleted = tempFile.delete();
                if (!deleted) {
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                }
            }

            final boolean success = deleted;
            Platform.runLater(() -> {
                stackPaneRemoveSelf();
                if(source instanceof String) {
                    NotificationUtil.showNotification(
                            Main.mainController.noticePane,
                            // success ? "文件【" + file.getName() + "】下载已取消！" :
                            success ? I18n.t("download.notice.cancelled", "下载已取消！")
                                    : I18n.t("download.notice.delete_failed", "文件【%s】删除失败，可能被占用！").formatted(file.getName())
                    );
                }else{
                    NotificationUtil.showNotification(Main.mainController.noticePane, I18n.t("download.notice.export_cancelled", "导出已取消！"));
                }
            });
        }).start();
    }



}


public class DownloadManagerUtil {
    private static final Logger log = LogManager.getLogger(DownloadManagerUtil.class);

    public static StackPane downloadStackPane; // 默认下载容器
    private static final Map<StackPane, DownloadQueue> queueByStackPane = new HashMap<>();

    private static final class DownloadQueue {
        private final List<DownloadTaskWrapper> tasks = new ArrayList<>();
        private int currentIndex = 0;
    }

    static {
        downloadStackPane = Main.mainController.downloadStackPane;
        // 自动轮播
        Thread switcher = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(3000); // 每3秒切换
                    Platform.runLater(DownloadManagerUtil::showNextForAllQueues);
                }
            } catch (InterruptedException ignored) {}
        });
        switcher.setDaemon(true);
        switcher.start();
    }


    /** 添加下载任务 */
    public static void addDownload(Object source, File file, boolean autoCloseOnComplete, ResultSetMetaData metaData) {
        addDownloadInternal(
                source,
                file,
                autoCloseOnComplete,
                metaData,
                downloadStackPane,
                false,
                null,
                null,
                I18n.t("download.error.file_exists", "文件\"%s\"已存在，无需重复下载！"),
                I18n.t("download.error.file_downloading", "该文件已在下载，无需重复下载！"),
                false
        );
    }

    public static void addInstallDownload(
            Object source,
            File file,
            boolean autoCloseOnComplete,
            ResultSetMetaData metaData,
            StackPane hostStackPane,
            CustomUserTextField remotePathField,
            CustomUserTextField installFilePathField
    ) {
        addDownloadInternal(
                source,
                file,
                autoCloseOnComplete,
                metaData,
                hostStackPane,
                true,
                remotePathField,
                installFilePathField,
                I18n.t("install.download.error.file_exists", "该文件在桌面已存在，无需重复下载！"),
                I18n.t("install.download.error.file_downloading", "该文件已在下载，已自动填充路径，无需重复下载！"),
                true
        );
    }

    private static void addDownloadInternal(
            Object source,
            File file,
            boolean autoCloseOnComplete,
            ResultSetMetaData metaData,
            StackPane hostStackPane,
            boolean installerMode,
            CustomUserTextField remotePathField,
            CustomUserTextField installFilePathField,
            String fileExistsMessage,
            String downloadingMessage,
            boolean fillInstallerPathWhenDuplicate
    ) {
        if (hostStackPane == null) {
            AlterUtil.CustomAlert(I18n.t("download.error.title", "下载失败"), I18n.t("download.error.host_missing", "下载容器未初始化"));
            return;
        }
        if(file.exists()){
            if (fillInstallerPathWhenDuplicate && installFilePathField != null && remotePathField != null) {
                installFilePathField.setText(file.getAbsolutePath());
                remotePathField.setText("/tmp/" + file.getName());
            }
            AlterUtil.CustomAlert(
                    I18n.t("download.error.title", "下载失败"),
                    fileExistsMessage.formatted(file.getAbsolutePath())
            );

            return;
        }

        File tempFile=new File(file.getAbsolutePath()+".download");
        if(tempFile.exists()){
            Platform.runLater(() -> {
               AlterUtil.CustomAlert(
                       I18n.t("download.error.title", "下载失败"),
                       downloadingMessage
               );
            });
            return;
        }
        DownloadQueue queue = getOrCreateQueue(hostStackPane);
        DownloadTaskWrapper wrapper = new DownloadTaskWrapper(
                source,
                file,
                autoCloseOnComplete,
                metaData,
                hostStackPane,
                installerMode,
                remotePathField,
                installFilePathField
        );
        queue.tasks.add(wrapper);

        Platform.runLater(() -> {
            hostStackPane.getChildren().add(wrapper.getRootPane());
            wrapper.getRootPane().setVisible(false); // 默认隐藏
            if (queue.tasks.size() == 1) {
                wrapper.getRootPane().setVisible(true); // 第一个显示
            }
        });

        wrapper.start(); // 启动下载
    }

    private static DownloadQueue getOrCreateQueue(StackPane hostStackPane) {
        return queueByStackPane.computeIfAbsent(hostStackPane, key -> new DownloadQueue());
    }

    /** 显示下一个任务 */
    private static void showNextForAllQueues() {
        for (Map.Entry<StackPane, DownloadQueue> entry : queueByStackPane.entrySet()) {
            showNext(entry.getValue());
        }
    }

    private static void showNext(DownloadQueue queue) {
        if (queue.tasks.isEmpty()) return;

        // 隐藏当前显示
        if (queue.currentIndex < queue.tasks.size()) {
            queue.tasks.get(queue.currentIndex).getRootPane().setVisible(false);
        }

        queue.currentIndex = (queue.currentIndex + 1) % queue.tasks.size();

        // 显示下一个
        queue.tasks.get(queue.currentIndex).getRootPane().setVisible(true);
    }

    /** 停止所有任务 */
    public void stopAll() {
        queueByStackPane.values().forEach(queue -> queue.tasks.forEach(DownloadTaskWrapper::cancelDownload));
    }

    /** 清除所有任务 */
    public void clearAll() {
        stopAll();
        Platform.runLater(() -> queueByStackPane.keySet().forEach(pane -> pane.getChildren().clear()));
        queueByStackPane.clear();
    }

    public static void removeDownload(DownloadTaskWrapper wrapper, StackPane hostStackPane) {
        DownloadQueue queue = queueByStackPane.get(hostStackPane);
        if (queue == null) {
            return;
        }
        int index = queue.tasks.indexOf(wrapper);
        if (index == -1) return;

        queue.tasks.remove(wrapper);

        // 修正 currentIndex，避免越界
        if (queue.currentIndex >= queue.tasks.size()) {
            queue.currentIndex = 0;
        }

        // 如果移除的是当前显示的任务，需要显示下一个
        if (!queue.tasks.isEmpty()) {
            queue.tasks.get(queue.currentIndex).getRootPane().setVisible(true);
        } else {
            queueByStackPane.remove(hostStackPane);
        }
    }

    /**
     * 追踪HTTP重定向，获取真实文件名
     * @param originalUrl 原始下载链接
     * @return 真实文件名（解析失败返回原文件名）
     */
    public static String getRealFileNameFromRedirect(String originalUrl) throws Exception {
        String fileName="";
        fileName=originalUrl.substring(originalUrl.lastIndexOf("/")+1);

        HttpURLConnection conn = null;
            URL url = new URL(originalUrl);
            // 手动追踪所有重定向，不依赖自动跳转
            while (true) {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD"); // 仅获取响应头，不下载内容，提升性能
                conn.setInstanceFollowRedirects(false); // 关闭自动重定向，手动处理
                conn.setRequestProperty("User-Agent", "JavaFX Downloader");
                conn.connect();

                int responseCode = conn.getResponseCode();
                // 处理3xx重定向响应
                if (responseCode >= 300 && responseCode < 400) {
                    String redirectUrl = conn.getHeaderField("Location");
                    if (redirectUrl == null) break;
                    // 处理相对路径重定向（如 Location: /file.zip）
                    url = new URL(url, redirectUrl);
                    conn.disconnect();
                } else {
                    break;
                }
            }

            // 优先级1：从 Content-Disposition 响应头解析文件名（标准方式）
            String disposition = conn.getHeaderField("Content-Disposition");
            if (disposition != null && !disposition.isEmpty()) {
                Pattern pattern = Pattern.compile("filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)");
                Matcher matcher = pattern.matcher(disposition);
                if (matcher.find()) {
                    fileName = matcher.group(1).replace("\"", "").replace("'", "");
                    fileName= URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
                    return fileName;
                }
            }

            // 优先级2：从最终重定向的URL中解析文件名
            String finalUrl = url.toString();
            fileName = finalUrl.substring(finalUrl.lastIndexOf('/') + 1);
            // 去除URL参数（如 file.zip?token=xxx → file.zip）
            if (fileName.contains("?")) {
                fileName = fileName.substring(0, fileName.indexOf('?'));
            }
        if (conn != null) conn.disconnect();
        fileName=URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
        return fileName;
        //return fileName; // 解析失败返回原文件名

    }

    public static String encodeUrl(String url) throws Exception {
        URL u = new URL(url);
        URI uri = new URI(
                u.getProtocol(),
                u.getUserInfo(),
                u.getHost(),
                u.getPort(),
                u.getPath(),
                u.getQuery(),
                u.getRef()
        );
        log.info("url is:"+url);
        log.info("return url is:"+uri.toASCIIString());
        return uri.toASCIIString();
    }



}

