package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.ctrl.MainController;
import com.dbboys.vo.Version;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpgradeUtil {
    private static final Logger log = LogManager.getLogger(UpgradeUtil.class);

    //初始化数据库，响应恢复出厂设置
    public static void initDB() {
        log.info("恢复出厂设置");
        if (AlterUtil.CustomAlertConfirm("恢复出厂设置","恢复出厂设置将删除所有数据及配置信息并重启软件，数据库知识不受影响，确定要恢复出厂设置吗？")) {
            //线程后台处理，避免前台界面卡顿
            Task task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    initCfg();
                    return null;
                }
            };
            task.setOnSucceeded(event -> {
                //AlterUtil.CustomAlert("恢复出场设置","恢复出厂设置完成，程序即将关闭，关闭后请手动启动！");
                restartExe();
                //Platform.exit();
            });
            new Thread(task).start();
        }
    }
    public static void checkVersion()  {
        String VERSION_CHECK_URL = "https://www.dbboys.com/dl/dbboys/windows/version.json";
        String VERSION_DOWNLOAD_URL = "https://www.dbboys.com/dl/dbboys/windows/latest.zip"; // 最新版本下载地址
        String osName = System.getProperty("os.name");
        String cpuArch = System.getProperty("os.arch");
        if(!osName.contains("Windows")){
            if(cpuArch.contains("amd64")){
                VERSION_CHECK_URL = "https://www.dbboys.com/dl/dbboys/linux/amd64/version.txt";
            }else{
                VERSION_CHECK_URL = "https://www.dbboys.com/dl/dbboys/linux/aarch64/version.txt";
            }
        }
        try{
            //获取版本信息
            URL url = new URL(VERSION_CHECK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            InputStream in = conn.getInputStream();
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            Version lastVersion=new Version(jsonObject);
            Path softDir = Path.of(System.getProperty("user.dir"));

            //获取upgrade.bat

            DirectoryStream<Path> stream = Files.newDirectoryStream(softDir, "dbboys.upgrade.*.zip");

            Iterator<Path> it = stream.iterator();
            if (it.hasNext()) {
                Path file = it.next();
                if (AlterUtil.CustomAlertConfirm("版本更新", "升级包\"" + file.toAbsolutePath() + "\"已完成下载，确定要升级软件吗？")) {
                    log.info("开始升级版本："+new File(file.toUri()).getName());
                    launchBatUpdater(softDir.resolve("app").resolve("upgrade.bat"),softDir);
                    return;
                }else{
                    return;
                }
            }

            if(lastVersion.getBuild()> Main.VERSION.getBuild()){
                VERSION_DOWNLOAD_URL=lastVersion.getUrl();
                if(AlterUtil.CustomAlertConfirm("版本更新","检查到新版本\""+lastVersion.getVersion()+"\"，确定要升级软件吗？")){
                    String defaultName=DownloadManagerUtil.getRealFileNameFromRedirect(VERSION_DOWNLOAD_URL);
                    File saveFile = new File(softDir.toString(), defaultName);  // 自动拼接路径
                    //下载完后会自动检查文件名，如果是升级包自动升级
                    DownloadManagerUtil.addDownload(VERSION_DOWNLOAD_URL,saveFile,true,null);
                }
            }else{

                NotificationUtil.showNotification(Main.mainController.noticePane,"当前已是最新版本，无需更新！");
            }

        } catch (Exception e) {
            AlterUtil.CustomAlert("错误",e.getMessage());
            log.error(e.getMessage(), e);
        }

    }

    public static void launchBatUpdater(Path batFile, Path appDir) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add("cmd.exe");
        cmd.add("/c"); // 执行完毕后关闭 cmd
        cmd.add(batFile.toAbsolutePath().toString());
        cmd.add(appDir.toAbsolutePath().toString()); // 可作为参数传给 BAT

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO(); // 可选：显示 BAT 输出
        pb.start();

        // 主程序必须退出，否则旧 JRE 无法替换
        Platform.exit();
        System.exit(0);
    }

    private static void restartExe() {
        try {
            // 获取当前运行程序所在目录
            String currentDir = new File(System.getProperty("user.dir")).getAbsolutePath();

            // 你的 exe 文件名（根据实际情况修改）
            String exeName = "dbboys.exe";

            // 构造 exe 路径
            File exeFile = new File(currentDir, exeName);

            if (!exeFile.exists()) {
                AlterUtil.CustomAlert("重启错误","未找到可执行文件！");
                return;
            }

            // 启动新的进程
            new ProcessBuilder(exeFile.getAbsolutePath()).start();

            // 退出当前程序
            Platform.exit();
            System.exit(0);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            e.printStackTrace();
            AlterUtil.CustomAlert("重启错误","重启失败！");
        }
    }

    public static void initCfg(){
        SqliteDBaccessUtil.initDB();
        ConfigManagerUtil.setProperty("DEFAULT_LISTVIEW_TAB","数据库连接");
        ConfigManagerUtil.setProperty("RESULT_FETCH_PER_TIME","200");
        ConfigManagerUtil.setProperty("SPLIT_DRIVER_MAIN","0.2");
        ConfigManagerUtil.setProperty("SPLIT_DRIVER_SQL","0.6");
    }
}

