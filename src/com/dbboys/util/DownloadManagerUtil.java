package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.customnode.CustomInstanceTab;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DownloadTaskWrapper {
    private static final Logger log = LogManager.getLogger(DownloadTaskWrapper.class);

    private Task<Void> task;
    private Object param;
    private  String url;
    private final File file;
    private final File tempFile; // 临时文件
    private  TableView tableView;
    private  ResultSetMetaData metaData;


    private final HBox rootPane; // StackPane 的子节点
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

    public DownloadTaskWrapper(Object param, File file, boolean autoCloseOnComplete,ResultSetMetaData metaData) {

        this.param=param;
        this.file = file;
        this.metaData=metaData;
        this.tempFile = new File(file.getAbsolutePath() + ".download"); // 临时文件
        this.autoCloseOnComplete = autoCloseOnComplete;

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(100);
        nameLabel = new Label("正在下载：" + file.getName());
        nameLabel.setTranslateY(-0.5);
        progressLabel = new Label("0%");
        progressLabel.setMinWidth(20);
        progressLabel.setTranslateY(-0.5);
        speedLabel = new Label("等待开始...");
        speedLabel.setTranslateY(-0.5);
        nameLabel.getStyleClass().add("download");
        progressLabel.getStyleClass().add("download");
        speedLabel.getStyleClass().add("download");

        pauseButton = new Button("");
        pauseButton.setTooltip(new Tooltip("暂停下载"));
        resumeButton = new Button("");
        resumeButton.setTooltip(new Tooltip("恢复下载"));
        stopButton = new Button("");
        stopButton.setTooltip(new Tooltip("取消下载并删除未完成文件"));
        StackPane pauseStackPane=new StackPane();
        pauseStackPane.getChildren().addAll(pauseButton,resumeButton);

        HBox buttonBox = new HBox(5,pauseStackPane, stopButton);
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

        SVGPath resumeButtonIcon = new SVGPath();
        resumeButtonIcon.setContent("M17.3594 13.0469 L7.8438 18.5625 Q7.2031 18.9531 6.5938 18.5938 Q6 18.2344 6 17.5156 L6 6.4844 Q6 5.7656 6.5938 5.4062 Q7.2031 5.0469 7.8438 5.4375 L17.3594 10.9531 Q18 11.2812 18 12 Q18 12.7188 17.3594 13.0469 Z");
        resumeButtonIcon.setScaleX(0.5);
        resumeButtonIcon.setScaleY(0.5);
        resumeButtonIcon.setFill(Color.valueOf("#074675"));
        resumeButton.setGraphic(new Group(resumeButtonIcon));
        resumeButton.getStyleClass().add("little-custom-button");
        resumeButton.setFocusTraversable(false);

        SVGPath pauseButtonIcon = new SVGPath();
        pauseButtonIcon.setContent("M8 7.0078 L11 7.0078 L11 16.9922 L8 16.9922 L8 7.0078 ZM13.0156 7.0078 L16.0156 7.0078 L16.0156 16.9922 L13.0156 16.9922 L13.0156 7.0078 Z");
        pauseButtonIcon.setScaleX(0.6);
        pauseButtonIcon.setScaleY(0.6);
        pauseButtonIcon.setFill(Color.valueOf("#074675"));
        pauseButton.setGraphic(new Group(pauseButtonIcon));
        pauseButton.getStyleClass().add("little-custom-button");
        pauseButton.setFocusTraversable(false);

        SVGPath stopButtonIcon = new SVGPath();
        stopButtonIcon.setContent("M19.2031 6.0078 L19.2031 17.7734 Q19.2031 18.3516 18.7812 18.7734 Q18.3594 19.1953 17.7656 19.1953 L6 19.1953 Q5.5156 19.1953 5.1562 18.8516 Q4.8125 18.4922 4.8125 18.0078 L4.8125 6.2422 Q4.8125 5.6484 5.2344 5.2266 Q5.6562 4.8047 6.2344 4.8047 L18 4.8047 Q18.5 4.8047 18.8438 5.1641 Q19.2031 5.5078 19.2031 6.0078 L19.2031 6.0078 Z");
        stopButtonIcon.setScaleX(0.5);
        stopButtonIcon.setScaleY(0.5);
        stopButtonIcon.setFill(Color.valueOf("#9f453c"));
        stopButton.setGraphic(new Group(stopButtonIcon));
        stopButton.getStyleClass().add("little-custom-button");
        stopButton.setFocusTraversable(false);
        stopButton.setOnAction(e -> cancelDownload());
        if(param instanceof String) {
            this.url = (String)param;
            rootPane = new HBox(6, nameLabel,   speedLabel,progressBar,progressLabel, buttonBox);

        }else{
            this.tableView=(TableView)param;
            rootPane = new HBox(6, nameLabel,progressBar,progressLabel, stopButton);
            nameLabel .setText("正在导出：" + file.getName());

        }
        rootPane.setAlignment(Pos.CENTER_RIGHT);

    }

    public HBox getRootPane() {
        return rootPane;
    }

    public void start() {
        startNewTask(false);
    }

    private synchronized void startNewTask(boolean isResume) {
        if (task != null && task.isRunning()) return;

        cancelled = false;
        if(this.param instanceof String) {
            task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateProgress(0, 1);
                    InputStream in = null;
                    RandomAccessFile out = null;
                    try {
                        long start = downloadedBytes;
                        if (url.toLowerCase().startsWith("http")) {
                            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                            conn.setRequestProperty("User-Agent", "JavaFX Downloader");
                            if (isResume && start > 0) conn.setRequestProperty("Range", "bytes=" + start + "-");
                            conn.connect();

                            int code = conn.getResponseCode();
                            if (code != 200 && code != 206) throw new IOException("连接失败: HTTP " + code);
                            if (totalBytes == 0) totalBytes = conn.getContentLengthLong() + start;
                            in = conn.getInputStream();
                        } else {
                            Path src = Paths.get(url);
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
                                        "已下载: %.2f / %.2f MB  速度: %s",
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
                            updateMessage("下载已停止并删除文件");
                        } else if (paused) {
                            updateMessage("已暂停");
                        } else {
                            updateMessage("下载完成");
                            updateProgress(1, 1);
                            Platform.runLater(() -> {
                                if (tempFile.exists()) {
                                    boolean renamed = tempFile.renameTo(file);
                                    if (!renamed) {
                                        updateMessage("下载完成，但重命名失败");
                                        return;
                                    }
                                }
                                if (autoCloseOnComplete) stackPaneRemoveSelf();
                                if(file.getName().contains("dbboys.upgrade.")){
                                    Main.mainController.checkVersion();
                                }else{
                                    NotificationUtil.showNotification(Main.mainController.notice_pane, "下载已完成！");
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
                    AlterUtil.CustomAlert("下载失败", task.getException().getMessage());
                });
            });

            progressBar.progressProperty().bind(task.progressProperty());
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
                Sheet sheet = workbook.createSheet("数据");

                ObservableList<TableColumn<T, ?>> columns = tableView.getColumns();
                ObservableList<T> items = tableView.getItems();
                int rowsTotal = items.size();

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
                        cell.setCellValue(value == null ? "" : value.toString());
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

                        NotificationUtil.showNotification(Main.mainController.notice_pane, "导出已完成！");
                        //rootPane.setStyle("-fx-background-color: #c8e6c9; -fx-padding: 10;");
                        if (autoCloseOnComplete) stackPaneRemoveSelf();
                    });
                }
                workbook.close();
                return null;
            }
        };
    }


    private void stackPaneRemoveSelf() {
        //从list里移除当前对象，避免取消后有空白轮询显示
        DownloadManagerUtil.removeDownload(this);
        Platform.runLater(() -> {
            StackPane parent = (StackPane) rootPane.getParent();
            if (parent != null) parent.getChildren().remove(rootPane);
        });
    }

    public void pauseDownload() {
        if (!paused) {
            paused = true;
            Platform.runLater(() -> nameLabel.setText(nameLabel.getText().replace("正在下载","已暂停下载")));
        }
    }

    public void resumeDownload() {
        if (paused) {
            paused = false;
            Platform.runLater(() -> nameLabel.setText(nameLabel.getText().replace("已暂停下载","正在下载")));
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
                if(param instanceof String) {
                    NotificationUtil.showNotification(
                            Main.mainController.notice_pane,
                            // success ? "文件【" + file.getName() + "】下载已取消！" :
                            success ? "下载已取消！" :"文件【" + file.getName() + "】删除失败，可能被占用！"
                    );
                }else{
                    NotificationUtil.showNotification(Main.mainController.notice_pane,"导出已取消！");
                }
            });
        }).start();
    }



}


public class DownloadManagerUtil {
    private static final Logger log = LogManager.getLogger(DownloadManagerUtil.class);

    public static StackPane stackPane; // 替代 TabPane
    private static List<DownloadTaskWrapper> downloads = new ArrayList<>();
    private static int currentIndex = 0;
    static {
        stackPane= Main.mainController.download_stackpane;
        // 自动轮播
        Thread switcher = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(3000); // 每3秒切换
                    Platform.runLater(DownloadManagerUtil::showNext);
                }
            } catch (InterruptedException ignored) {}
        });
        switcher.setDaemon(true);
        switcher.start();
    }


    /** 添加下载任务 */
    public static void addDownload(Object url, File file, boolean autoCloseOnComplete, ResultSetMetaData metaData) {
        if(file.exists()){
            AlterUtil.CustomAlert("下载错误","文件\""+file.getAbsolutePath()+"\"已存在，无需重复下载！");

            return;
        }

        File tempFile=new File(file.getAbsolutePath()+".download");
        if(tempFile.exists()){
            Platform.runLater(() -> {
               AlterUtil.CustomAlert("下载错误","该文件已在下载，无需重复下载！");
            });
            return;
        }
        DownloadTaskWrapper wrapper = new DownloadTaskWrapper(url, file, autoCloseOnComplete,metaData);
        downloads.add(wrapper);

        Platform.runLater(() -> {
            stackPane.getChildren().add(wrapper.getRootPane());
            wrapper.getRootPane().setVisible(false); // 默认隐藏
            if (downloads.size() == 1) {
                wrapper.getRootPane().setVisible(true); // 第一个显示
            }
        });

        wrapper.start(); // 启动下载
    }

    /** 显示下一个任务 */
    private static void showNext() {
        if (downloads.isEmpty()) return;

        // 隐藏当前显示
        if (currentIndex < downloads.size()) {
            downloads.get(currentIndex).getRootPane().setVisible(false);
        }

        currentIndex = (currentIndex + 1) % downloads.size();

        // 显示下一个
        downloads.get(currentIndex).getRootPane().setVisible(true);
    }

    /** 停止所有任务 */
    public void stopAll() {
        downloads.forEach(DownloadTaskWrapper::cancelDownload);
    }

    /** 清除所有任务 */
    public void clearAll() {
        stopAll();
        Platform.runLater(stackPane.getChildren()::clear);
        downloads.clear();
        currentIndex = 0;
    }

    public static void removeDownload(DownloadTaskWrapper wrapper) {
        int index = downloads.indexOf(wrapper);
        if (index == -1) return;

        downloads.remove(wrapper);

        // 修正 currentIndex，避免越界
        if (currentIndex >= downloads.size()) {
            currentIndex = 0;
        }

        // 如果移除的是当前显示的任务，需要显示下一个
        if (!downloads.isEmpty()) {
            downloads.get(currentIndex).getRootPane().setVisible(true);
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
