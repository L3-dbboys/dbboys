package com.dbboys.util;

import com.dbboys.app.Main;
import com.dbboys.customnode.*;
import com.dbboys.vo.Connect;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.jcraft.jsch.*;
import net.sf.jsqlparser.statement.alter.Alter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.InlineCssTextArea;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteInstallerUtil {
    private static final Logger log = LogManager.getLogger(RemoteInstallerUtil.class);

    // 存储用户输入信息
    private static String hostname;
    private static int port = 22;
    private static String username = "root";
    private static String password;
    private static File selectedFile;
    private static String remoteFilePath;
    private static Double freeDiskSize;

    //系统信息
    private static String machineInfo;
    private static String osInfo;
    private static String kernelInfo;
    private static String cpuInfo;
    private static String memInfo;
    private static String diskInfo;
    private static String fileSystemInfo;


    // SSH相关对象
    private static JSch jsch = new JSch();
    private static Session session;

    // 进度属性
    private static final DoubleProperty progress = new SimpleDoubleProperty(0);
    private static final long DIALOG_WIDTH = 600;
    private static final long DIALOG_HEIGHT = 400;

    //安装面板
    private static CustomInstallStepHbox customInstallStepHbox1;
    private static CustomInstallStepHbox customInstallStepHbox2;
    private static CustomInstallStepHbox customInstallStepHbox3;
    private static CustomInstallStepHbox customInstallStepHbox4;
    private static CustomInstallStepHbox customInstallStepHbox5;
    private static CustomInstallStepHbox customInstallStepHbox6;
    private static CustomInstallStepHbox customInstallStepHbox7;
    private static CustomInstallStepHbox customInstallStepHbox8;
    private static CustomInstallStepHbox customInstallStepHbox9;
    private static CustomInstallStepHbox customInstallStepHbox10;
    private static CustomInstallStepHbox customInstallStepHbox11;
    private static CustomInstallStepHbox customInstallStepHbox12;
    private static CustomInstallStepHbox customInstallStepHbox13;
    private static CustomInstallStepHbox customInstallStepHbox14;


    // 步骤管理
    private static int currentStep = 1;
    private static StackPane contentStack; // 用于切换步骤内容
    private static Dialog<ButtonType> mainDialog; // 主对话框

    // 步骤内容面板（保存引用，用于状态保持）
    private static Node step1Pane, step2Pane, step3Pane, step4Pane, step5Pane;

    // 输入组件引用（用于跨步骤获取数据）
    private static CustomUserTextField hostField;
    private static CustomUserTextField portField;
    private static CustomPasswordField passField;
    private static CustomUserTextField remotePathField;
    private static CustomUserTextField installFilePathField;
    private static CustomInlineCssTextArea systemInfoArea;
    private static CustomInlineCssTextArea databaseInfoArea;
    private static HBox backgroupHbox;
    private static Button stopButton;
    private static Label runningLabel;
    private static ToggleGroup uploadToggleGroup = new ToggleGroup();
    private static RadioButton notUploadedRadioButton = new RadioButton("我没有上传数据库安装包");
    private static RadioButton uploadedRadioButton = new RadioButton("我已经上传了数据库安装包");
    private static List<ObservableList<String>> configList = FXCollections.observableArrayList();

    // 入口方法：启动向导
    public static void startWizard(Stage parent) {
        initMainDialog(parent);
        currentStep=1;
        updateWizardState();
        mainDialog.showAndWait();
    }

    // 初始化主对话框
    private static void initMainDialog(Stage parent) {
        mainDialog = new Dialog<>();
        mainDialog.setTitle("远程安装向导 - 步骤 1/5");
        mainDialog.setWidth(DIALOG_WIDTH);
        mainDialog.setHeight(DIALOG_HEIGHT);
        mainDialog.initOwner(parent);



        // 创建按钮
        mainDialog.getDialogPane().getButtonTypes().addAll(ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.FINISH, ButtonType.CANCEL);

        // 获取按钮实例
        Button previousBtn = (Button) mainDialog.getDialogPane().lookupButton(ButtonType.PREVIOUS);
        Button nextBtn = (Button) mainDialog.getDialogPane().lookupButton(ButtonType.NEXT);
        Button finishBtn = (Button) mainDialog.getDialogPane().lookupButton(ButtonType.FINISH);
        Button cancelBtn = (Button) mainDialog.getDialogPane().lookupButton(ButtonType.CANCEL);



        // 加载指示器
        ImageView imageView = new ImageView(new Image("file:images/loading.gif"));
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        stopButton = new Button("");
        SVGPath stopButtonIcon = new SVGPath();
        stopButtonIcon.setScaleX(0.7);
        stopButtonIcon.setScaleY(0.7);
        stopButtonIcon.setContent("M19.2031 6.0078 L19.2031 17.7734 Q19.2031 18.3516 18.7812 18.7734 Q18.3594 19.1953 17.7656 19.1953 L6 19.1953 Q5.5156 19.1953 5.1562 18.8516 Q4.8125 18.4922 4.8125 18.0078 L4.8125 6.2422 Q4.8125 5.6484 5.2344 5.2266 Q5.6562 4.8047 6.2344 4.8047 L18 4.8047 Q18.5 4.8047 18.8438 5.1641 Q19.2031 5.5078 19.2031 6.0078 L19.2031 6.0078 Z");
        stopButtonIcon.setFill(Color.valueOf("#9f453c"));
        stopButton.setGraphic(new Group(stopButtonIcon));
        runningLabel=new Label("");
        HBox imageHBox = new HBox(imageView, runningLabel, stopButton);
        imageHBox.setStyle("-fx-background-color: white;-fx-background-radius: 2;-fx-padding: 0 0 0 5");
        imageHBox.setAlignment(Pos.CENTER);
        imageHBox.setMaxHeight(15);
        //imageHBox.setMaxWidth(100);
        stopButton.setFocusTraversable(false);
        stopButton.getStyleClass().add("little-custom-button");
        backgroupHbox=new HBox(imageHBox);
        backgroupHbox.setAlignment(Pos.CENTER);
        backgroupHbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1);-fx-background-radius: 2;");
        backgroupHbox.setVisible(false);


        //绑定属性
        previousBtn.disableProperty().bind(backgroupHbox.visibleProperty());
        nextBtn.disableProperty().bind(backgroupHbox.visibleProperty());
        finishBtn.disableProperty().bind(backgroupHbox.visibleProperty());


        // 初始化步骤内容
        initStepPanes(parent);

        // 初始化StackPane
        contentStack = new StackPane();
        contentStack.getChildren().addAll(step1Pane, step2Pane, step3Pane, step4Pane, step5Pane,backgroupHbox);


        // 显示初始步骤
        showCurrentStep();



        // 设置对话框内容
        mainDialog.getDialogPane().setContent(contentStack);
        centerDialogToParent(mainDialog, parent);

        // 按钮事件
        previousBtn.addEventFilter(ActionEvent.ACTION, event -> {
            if (currentStep > 1) {
                currentStep--;
                updateWizardState(); // 触发按钮状态更新
            }
            event.consume();
        });

        progress.addListener((obs, old, val) -> {
            int percentage = (int) (val.doubleValue() * 100);
            runningLabel.setText(" 正在上传安装包... "+percentage + "%");
        });
        nextBtn.addEventFilter(ActionEvent.ACTION, event -> {
            switch (currentStep){
                case 1:
                    if(hostField.getText().trim().isEmpty()){
                        event.consume();
                        //ipaddr_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                        hostField.requestFocus();
                    }
                    else if(portField.getText().trim().isEmpty()){
                        event.consume();
                        //port_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                        portField.requestFocus();
                    }
                    else if(passField.getText().trim().isEmpty()){
                        event.consume();
                        // username_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                        passField.requestFocus();
                    }else {
                        backgroupHbox.setVisible(true);
                        hostname = hostField.getText().trim();
                        try {
                            port = Integer.parseInt(portField.getText().trim());
                        } catch (NumberFormatException e) {
                            port = 22;
                        }
                        password = passField.getText();

                        // 连接测试任务
                        Task<Void> runningTask = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                try {
                                    if (session != null && session.isConnected()) {
                                        session.disconnect();
                                    }
                                    session = jsch.getSession(username, hostname, port);
                                    session.setPassword(password);
                                    Properties config = new Properties();
                                    config.put("StrictHostKeyChecking", "no");
                                    session.setConfig(config);
                                    session.connect(5000); // 5秒超时
                                    return null;
                                } catch (JSchException e) {
                                    throw new Exception("连接失败: " + e.getMessage());
                                }
                            }
                        };

                        // 任务完成处理
                        runningTask.setOnSucceeded(e -> {
                            //backgroupHbox.setVisible(false);
                            runningLabel.setText(" 正在获取系统信息...");
                            systemInfoArea.setStyle(0,systemInfoArea.getLength(),"");
                            systemInfoArea.replaceText("");
                            // 自动进入下一步
                            currentStep ++;
                            updateWizardState();

                            //第二步自动执行，并显示执行结果
                            Task<Void> systeminfoTask = new Task<>() {
                                @Override
                                protected Void call() throws Exception {
                                    try {
                                        // 收集系统信息
                                        machineInfo = executeCommand("dmidecode -s system-product-name");
                                        if (isCommandExists("nkvers")) {
                                            osInfo = executeCommand("nkvers");
                                        } else if (executeCommandWithExitStatus("test -f /etc/redhat-release") == 0) {
                                            osInfo = executeCommand("cat /etc/redhat-release");
                                        } else {
                                            osInfo = executeCommand("cat /etc/os-release");
                                        }
                                        cpuInfo = executeCommand("lscpu");
                                        memInfo = executeCommand("free -h");
                                        fileSystemInfo = executeCommand("df -h");
                                        diskInfo = executeCommand("lsblk");
                                        kernelInfo = executeCommand("uname -a");
                                        String kernelParams = executeCommand("sysctl -p");

                                        // 显示信息
                                        Platform.runLater(() -> {
                                            systemInfoArea.replaceText("");
                                            int start = 0;

                                            systemInfoArea.append("服务器型号\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(machineInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            // 省略其他信息的显示代码（与原逻辑相同）
                                            systemInfoArea.append("操作系统版本\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(osInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            systemInfoArea.append("内核版本\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(kernelInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            systemInfoArea.append("CPU信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(cpuInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            systemInfoArea.append("内存信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(memInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            systemInfoArea.append("磁盘信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(diskInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            systemInfoArea.append("文件系统信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            systemInfoArea.append(fileSystemInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");



                                            //systemInfoArea.append("内核参数\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                                            //systemInfoArea.append(kernelParams + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                                            systemInfoArea.showParagraphAtTop(0);
                                        });
                                    } catch (Exception e) {
                                        throw new Exception("获取系统信息失败: " + e.getMessage());
                                        //String errorMsg = "获取系统信息失败: " + e.getMessage();
                                        //Platform.runLater(() -> {
                                        //    systemInfoArea.replaceText(errorMsg + "\n");
                                        //   showErrorDialog(null, "信息获取失败", errorMsg);
                                        // });
                                    }
                                    return null;
                                }

                            };
                            // 任务完成处理
                            systeminfoTask.setOnSucceeded(e1 -> {
                                backgroupHbox.setVisible(false);

                            });

                            systeminfoTask.setOnFailed(e1 -> {
                                backgroupHbox.setVisible(false);
                                String error = runningTask.getException().getMessage();
                                AlterUtil.CustomAlert("错误", error);
                            });

                            new Thread(systeminfoTask).start();
                            stopButton.setOnAction(event1->{
                                systeminfoTask.cancel();
                                backgroupHbox.setVisible(false);
                            });
                            mainDialog.setOnCloseRequest(event1 -> {
                                systeminfoTask.cancel();
                                if (session != null && session.isConnected()) {
                                    session.disconnect();
                                }
                            });
                            cancelBtn.setOnAction(event1->{
                                systeminfoTask.cancel();
                                if (session != null && session.isConnected()) {
                                    session.disconnect();
                                }
                            });

                        });

                        runningTask.setOnFailed(e -> {
                            backgroupHbox.setVisible(false);
                            String error = runningTask.getException().getMessage();
                            AlterUtil.CustomAlert("错误", error);
                        });


                        new Thread(runningTask).start();
                        stopButton.setOnAction(event1->{
                            runningTask.cancel();
                            backgroupHbox.setVisible(false);
                        });

                        mainDialog.setOnCloseRequest(event1 -> {
                            runningTask.cancel();
                            if (session != null && session.isConnected()) {
                                session.disconnect();
                            }
                        });
                        cancelBtn.setOnAction(event1->{
                            runningTask.cancel();
                            if (session != null && session.isConnected()) {
                                session.disconnect();
                            }
                        });
                    }
                    runningLabel.setText(" 正在连接...");
                    break;
                case 2:
                    currentStep ++;
                    updateWizardState();
                    break;
                case 3:
                    if(notUploadedRadioButton.isSelected()) {
                        if(installFilePathField.getText().isEmpty()){
                            AlterUtil.CustomAlert("错误","需上传的安装包文件路径不能为空！");
                        }else if(!new File(installFilePathField.getText()).exists()){
                            AlterUtil.CustomAlert("错误","需上传的安装包文件不存在！");
                        }else if(systemInfoArea.getText().contains("x86_64")&&!installFilePathField.getText().contains("x86_64")){
                            AlterUtil.CustomAlert("错误","需上传的安装包文件与远程服务器CPU不匹配！");
                        }else{
                            selectedFile=new File(installFilePathField.getText());
                            remoteFilePath=remotePathField.getText();
                            if(remoteFilePath.isEmpty()){
                                remoteFilePath="/tmp/"+selectedFile.getName();
                            }
                            Task<Void> runningTask = new Task<>() {
                                @Override
                                protected Void call() throws Exception {
                                    ChannelSftp channelSftp = null;
                                    try {
                                        channelSftp = (ChannelSftp) session.openChannel("sftp");
                                        channelSftp.connect();

                                        Platform.runLater(() -> {
                                            runningLabel.setText(" 正在上传安装包...");
                                            backgroupHbox.setVisible(true);
                                        });

                                        try (FileInputStream fis = new FileInputStream(selectedFile)) {
                                            ProgressMonitorInputStream monitor = new ProgressMonitorInputStream(
                                                    fis, selectedFile.length(), progress);
                                            channelSftp.put(monitor, remoteFilePath+".upload");
                                        }

                                    } catch (Exception e) {
                                        throw new Exception("上传安装包失败: " + e.getMessage());
                                    } finally {
                                        if (channelSftp != null && channelSftp.isConnected()) {
                                            channelSftp.disconnect();
                                        }
                                    }
                                    return null;

                                }
                            };
                            runningTask.setOnSucceeded(e->{
                                try {
                                    executeCommandWithExitStatus("mv "+remoteFilePath+".upload "+remoteFilePath);
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                                initConfigList();
                                backgroupHbox.setVisible(false);
                                currentStep++;
                                updateWizardState();
                            });
                            runningTask.setOnFailed(e -> {
                                try {
                                    executeCommandWithExitStatus("rm -f "+remoteFilePath+".upload");
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                                backgroupHbox.setVisible(false);
                                String error = runningTask.getException().getMessage();
                                AlterUtil.CustomAlert("错误", error);
                            });
                            stopButton.setOnAction(event1->{
                                runningTask.cancel();
                                backgroupHbox.setVisible(false);
                                try {
                                    executeCommandWithExitStatus("rm -f "+remoteFilePath+".upload");
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                            mainDialog.setOnCloseRequest(event1 -> {
                                runningTask.cancel();
                                if (session != null && session.isConnected()) {
                                    session.disconnect();
                                }
                            });
                            cancelBtn.setOnAction(event1->{
                                runningTask.cancel();
                                if (session != null && session.isConnected()) {
                                    session.disconnect();
                                }
                            });



                            try {
                                if(executeCommandWithExitStatus("test -f "+remoteFilePath) == 0) {
                                    /* 如果存在不执行任何操作
                                    if( AlterUtil.CustomAlertConfirm("文件已存在","安装包在服务器/tmp目录已存在，确定要上传覆盖吗？")){
                                        executeCommandWithExitStatus("cd /tmp");
                                        executeCommandWithExitStatus("rm -rf "+remoteFilePath);
                                        new Thread(runningTask).start();
                                    }
                                     */
                                    initConfigList();
                                    currentStep++;
                                    updateWizardState();
                                }else{
                                    new Thread(runningTask).start();
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else{
                        if(remotePathField.getText().isEmpty()){
                            AlterUtil.CustomAlert("错误","远程服务器上安装包路径不能为空！");
                        }else {
                            remoteFilePath=remotePathField.getText();
                            try {
                                if(executeCommandWithExitStatus("test -f "+remoteFilePath) != 0) {
                                    AlterUtil.CustomAlert("错误", "远程服务器上安装包文件不存在！");
                                }else if(systemInfoArea.getText().contains("x86_64")&&!remoteFilePath.contains("x86_64")){
                                    AlterUtil.CustomAlert("错误","服务器上的安装包文件与CPU不匹配！");
                                }else{
                                    initConfigList();
                                    currentStep++;
                                    updateWizardState();
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    break;
                case 4:
                    Task installTask = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(() -> {
                                customInstallStepHbox1.iconLabel.setVisible(true);
                                runningLabel.setText(" 正在安装...");
                            });
                            executeCommandWithExitStatus("ps -ef |grep gbasedbt |grep -v grep |awk '{print \"kill -9 \"$2}' |sh");
                            executeCommandWithExitStatus("find / -user gbasedbt -exec rm -rf {} +");
                            if((executeCommandWithExitStatus("test -f /GBASEDBTTMP/.infxdirs") == 0)){
                                if(executeCommandWithExitStatus("cat /GBASEDBTTMP/.infxdirs  |awk '{print \"rm -rf \"$1}'|sh")!=0) {
                                    throw new Exception("删除数据库安装目录失败！");
                                }
                                if(executeCommandWithExitStatus("rm -rf /GBASEDBTTMP")!=0) {
                                    throw new Exception("删除目录/GBASEDBTTMP失败！");
                                }
                            }
                            if((executeCommandWithExitStatus("test -d /opt/gbase") == 0)){
                                if(executeCommandWithExitStatus("rm -rf /opt/gbase")!=0) {
                                    throw new Exception("删除数据库安装目录/opt/gbase失败！");
                                }
                            }
                            if((executeCommandWithExitStatus("test -d /etc/gbasedbt") == 0)) {
                                if(executeCommandWithExitStatus("rm -rf /etc/gbasedbt")!=0) {
                                    throw new Exception("删除/etc/gbasedbt目录失败！");
                                }
                            }
                            //麒麟系统可能有下面的错误
                            if((executeCommandWithExitStatus("test -f /usr/lib64/libnsl.so.1") != 0)) {
                                executeCommandWithExitStatus("ln -s /usr/lib64/libnsl.so.2  /usr/lib64/libnsl.so.1");
                            }
                            if((executeCommandWithExitStatus("id gbasedbt") == 0)) {
                                if(executeCommandWithExitStatus("userdel gbasedbt")!=0) {
                                    throw new Exception("删除用户gbasedbt失败！");
                                }
                            }
                            //卸载完成，开始系统检查
                            Platform.runLater(() -> {
                                customInstallStepHbox1.iconLabel.setVisible(false);
                                customInstallStepHbox2.iconLabel.setVisible(true);
                            });

                            if(executeCommandWithExitStatus("chown root:root /opt&&chmod 755 /opt")!=0) {
                                throw new Exception("更改/opt为默认权限失败！");
                            }

                            if(Double.parseDouble(executeCommand("df -m /opt |tail -1 |awk '{print $4/1000}'"))<8) {
                                throw new Exception("空间检查不通过，最小要求/opt可用空间小于8G！");
                            }
                            if(Double.parseDouble(executeCommand("df -m /tmp |tail -1 |awk '{print $4/1000}'"))<1) {
                                throw new Exception("空间检查不通过，最小要求/tmp可用空间小于1G！");
                            }
                            if(executeCommandWithExitStatus("stat -c \"%a\" /tmp | grep -q '777'")!=0) {
                                executeCommandWithExitStatus("chmod 777 /tmp");
                            }
                            if(Double.parseDouble(executeCommand("free -m |sed -n 2p |awk '{print $2/1024}'"))<1) {
                                throw new Exception("内存检查不通过，最小要求1G！");
                            }
                            if((executeCommandWithExitStatus("command -v unzip") != 0)) {
                                throw new Exception("系统缺失unzip！");
                            }
                            if((executeCommandWithExitStatus("systemctl stop firewalld.service") != 0)) {
                                executeCommandWithExitStatus("service iptables stop");
                            }
                            if((executeCommandWithExitStatus("systemctl disable firewalld.service") != 0)) {
                                executeCommandWithExitStatus("chkconfig iptables off");
                            }

                            executeCommandWithExitStatus("sed -i \"s#^hosts.*#hosts:      files#g\" /etc/nsswitch.conf");
                            executeCommandWithExitStatus("sed -i \"s#^SELINUX=.*#SELINUX=disabled#g\" /etc/selinux/config");

                            executeCommandWithExitStatus("sed -i \"s/^#RemoveIPC.*/RemoveIPC=no/g\" /etc/systemd/logind.conf");
                            executeCommandWithExitStatus("systemctl daemon-reload");
                            executeCommandWithExitStatus("systemctl restart systemd-logind");

                            executeCommandWithExitStatus("sed -i '/^[[:space:]]*\\*[[:space:]]\\+\\(soft\\|hard\\)[[:space:]]/d' /etc/security/limits.conf");
                            executeCommandWithExitStatus("sed -i '/^[[:space:]]*\\*[[:space:]]\\+\\(soft\\|hard\\)[[:space:]]/d' /etc/security/limits.d/20-nproc.conf");
                            executeCommandWithExitStatus("echo \"* soft nproc 1048576\">> /etc/security/limits.conf");
                            executeCommandWithExitStatus("echo \"* hard nproc 1048576\">> /etc/security/limits.conf");
                            executeCommandWithExitStatus("echo \"* soft nofile 1048576\">> /etc/security/limits.conf");
                            executeCommandWithExitStatus("echo \"* hard nofile 1048576\">> /etc/security/limits.conf");

                            executeCommandWithExitStatus("sed -i \"/^kernel.shmmni.*/d\" /etc/sysctl.conf");
                            executeCommandWithExitStatus("sed -i \"/^kernel.shmmax.*/d\" /etc/sysctl.conf");
                            executeCommandWithExitStatus("sed -i \"/^kernel.shmall.*/d\" /etc/sysctl.conf");
                            executeCommandWithExitStatus("sed -i \"/^kernel.sem.*/d\" /etc/sysctl.conf");

                            executeCommandWithExitStatus("echo \"kernel.shmmni=4096\">> /etc/sysctl.conf");
                            executeCommandWithExitStatus("echo \"kernel.shmmax=18446744073709547520\">> /etc/sysctl.conf");
                            executeCommandWithExitStatus("echo \"kernel.shmall=18446744073709547520\">> /etc/sysctl.conf");
                            executeCommandWithExitStatus("echo \"kernel.sem=32000 1024000000  500 32000\" >>/etc/sysctl.conf");
                            executeCommandWithExitStatus("sysctl -p");

                            //系统检查完成，开始创建用户和组
                            Platform.runLater(() -> {
                                customInstallStepHbox2.iconLabel.setVisible(false);
                                customInstallStepHbox3.iconLabel.setVisible(true);
                            });
                            System.out.println("change password before");
                            String password=configList.get(0).get(2);
                            System.out.println("change password after");
                            if((executeCommandWithExitStatus("groupadd gbasedbt&&useradd gbasedbt -d /home/gbasedbt -m -g gbasedbt&&echo \"gbasedbt:"+password+"\" | chpasswd") != 0)) {
                                throw new Exception("创建gbasedbt用户或组失败！");
                            }
                            System.out.println("change password after1");

                            executeCommandWithExitStatus("cat >>~gbasedbt/.bash_profile << EOF\nexport GBASEDBTDIR="+configList.get(1).get(2)+
                                    "\nexport GBASEDBTSERVER="+configList.get(2).get(2)+
                                    "\nexport ONCONFIG=onconfig."+configList.get(2).get(2)+
                                    "\nexport GBASEDBTSQLHOSTS=\\$GBASEDBTDIR/etc/sqlhosts."+configList.get(2).get(2)+
                                    "\nexport DB_LOCALE="+configList.get(3).get(2)+
                                    "\nexport CLIENT_LOCALE="+configList.get(3).get(2)+
                                    "\nexport GL_USEGLU="+configList.get(4).get(2)+
                                    "\nexport PATH=\\$GBASEDBTDIR/bin:/usr/bin:\\${PATH}:.\nEOF"
                            );

                            //创建用户和组完成，开始安装
                            Platform.runLater(() -> {
                                customInstallStepHbox3.iconLabel.setVisible(false);
                                customInstallStepHbox4.iconLabel.setVisible(true);
                            });
                            if((executeCommandWithExitStatus("tar -xvf "+remoteFilePath) != 0)) {
                                throw new Exception("解压安装包【"+remoteFilePath+"】失败！");
                            }
                            int status=executeCommandWithExitStatus("source ~gbasedbt/.bash_profile && mkdir -p $GBASEDBTDIR && chown gbasedbt:gbasedbt $GBASEDBTDIR && ./ids_install -i silent -DLICENSE_ACCEPTED=TRUE");
                            if(status!= 0) {
                                throw new Exception("安装数据库到$GBASEDBTDIR失败！");
                            }

                            //安装完成，开始配置并初始化
                            Platform.runLater(() -> {
                                customInstallStepHbox4.iconLabel.setVisible(false);
                                customInstallStepHbox5.iconLabel.setVisible(true);
                            });

                            String cmd=
                                    "source ~gbasedbt/.bash_profile &&" +
                                            "DATADIR="+configList.get(5).get(2)+"&&"+
                                            "mkdir -p ${DATADIR} &&"+
                                            "chown gbasedbt:gbasedbt ${DATADIR} &&"+
                                            "cp $GBASEDBTDIR/etc/onconfig.std  $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "chown gbasedbt:gbasedbt $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "sed -i \"s#^ROOTPATH.*#ROOTPATH ${DATADIR}/rootdbschk001#g\" $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "sed -i \"s#^ROOTSIZE.*#ROOTSIZE "+configList.get(6).get(2)+"#g\" $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "sed -i \"s#^DBSERVERNAME.*#DBSERVERNAME $GBASEDBTSERVER#g\" $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "sed -i \"s#^TAPEDEV.*#TAPEDEV /dev/null#g\" $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "sed -i \"s#^LTAPEDEV.*#LTAPEDEV /dev/null#g\" $GBASEDBTDIR/etc/$ONCONFIG &&"+
                                            "echo \"$GBASEDBTSERVER onsoctcp "+configList.get(7).get(2)+" "+configList.get(8).get(2)+"\" >> $GBASEDBTSQLHOSTS &&"+
                                            "chown gbasedbt:gbasedbt $GBASEDBTSQLHOSTS &&"+
                                            "touch ${DATADIR}/rootdbschk001 &&"+
                                            "chown gbasedbt:gbasedbt ${DATADIR}/rootdbschk001 &&"+
                                            "chmod 660 ${DATADIR}/rootdbschk001 && oninit -ivyw";
                            System.out.println("cmd is:"+cmd);
                            if(executeCommandWithExitStatus(cmd)!=0){
                                throw new Exception("初始化实例失败！");
                            }

                            //初始化完成，优化配置参数
                            Platform.runLater(() -> {
                                customInstallStepHbox5.iconLabel.setVisible(false);
                                customInstallStepHbox6.iconLabel.setVisible(true);
                            });
                            String pramsCmd="""
                                    source ~gbasedbt/.bash_profile &&\
                                    sed -i "s#^PHYSBUFF.*#PHYSBUFF 2048#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^LOGBUFF.*#LOGBUFF 2048#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^NETTYPE.*#NETTYPE soctcp,4,50,NET#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^MULTIPROCESSOR.*#MULTIPROCESSOR 1#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^CLEANERS.*#CLEANERS 128#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^LOCKS.*#LOCKS """
                                    +" "+configList.get(16).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^DEF_TABLE_LOCKMODE.*#DEF_TABLE_LOCKMODE row#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^DS_TOTAL_MEMORY.*#DS_TOTAL_MEMORY """
                                    +" "+configList.get(17).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^DS_NONPDQ_QUERY_MEM.*#DS_NONPDQ_QUERY_MEM """
                                    +" "+configList.get(18).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^SHMVIRTSIZE.*#SHMVIRTSIZE """
                                    +" "+configList.get(19).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^SHMADD.*#SHMADD """
                                    +" "+configList.get(20).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^STACKSIZE.*#STACKSIZE 2048#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^SBSPACENAME.*#SBSPACENAME sbspace01#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^DBSPACETEMP.*#DBSPACETEMP tempdbs01#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^VPCLASS cpu.*#VPCLASS"""
                                    +" "+configList.get(21).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^TEMPTAB_NOLOG.*#TEMPTAB_NOLOG 1#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^NS_CACHE.*#NS_CACHE host=0,service=0,user=0,group=0#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^DUMPSHMEM.*#DUMPSHMEM 0#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^USERMAPPING.*#USERMAPPING ADMIN#g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    sed -i "s#^BUFFERPOOL size=2k.*#BUFFERPOOL """
                                    +" "+configList.get(22).get(2)+
                                    """
                                    #g" $GBASEDBTDIR/etc/$ONCONFIG &&\
                                    echo "BUFFERPOOL """
                                    +" "+configList.get(23).get(2)+
                                    """
                                    ">>$GBASEDBTDIR/etc/$ONCONFIG &&\
                                    touch $GBASEDBTDIR/etc/sysadmin/stop &&\
                                    chown gbasedbt:gbasedbt $GBASEDBTDIR/etc/sysadmin/stop &&\
                                    mkdir -p /etc/gbasedbt &&\
                                    echo "USER:daemon" > /etc/gbasedbt/allowed.surrogates &&\
                                    onmode -ky &&\
                                    su - gbasedbt -c \"oninit\" &&\
                                    echo "CREATE DEFAULT USER WITH PROPERTIES USER 'daemon'" |dbaccess sysuser -
                                    """;
                            System.out.println("pramsCmd is:"+pramsCmd);
                            if(executeCommandWithExitStatus(pramsCmd)!=0){
                                throw new Exception("优化配置参数失败！");
                            }

                            Platform.runLater(() -> {
                                customInstallStepHbox6.iconLabel.setVisible(false);
                                customInstallStepHbox7.iconLabel.setVisible(true);
                            });
                            if(executeCommandWithExitStatus("""
                                    source ~gbasedbt/.bash_profile &&\
                                    DATADIR="""
                                    +configList.get(5).get(2)+
                                    """ 
                                    &&\
                                    touch ${DATADIR}/plogdbschk001 &&\
                                    chown gbasedbt:gbasedbt ${DATADIR}/plogdbschk001 &&\
                                    chmod 660 ${DATADIR}/plogdbschk001 &&\
                                    onspaces -c -d plogdbs -p ${DATADIR}/plogdbschk001 -o 0 -s """
                                    +" "+(Integer.parseInt(configList.get(9).get(2))+10000)+
                                    """
                                     &&\
                                    onparams -p -d plogdbs -s """
                                    +" "+configList.get(9).get(2)+
                                    """
                                     -y
                                    """)!=0){
                                throw new Exception("优化物理日志失败！");
                            }
                            Platform.runLater(() -> {
                                customInstallStepHbox7.iconLabel.setVisible(false);
                                customInstallStepHbox8.iconLabel.setVisible(true);
                            });
                            String logicCmd="""
                                    source ~gbasedbt/.bash_profile &&\
                                    DATADIR="""
                                    +configList.get(5).get(2)+
                                    """
                                     &&\
                                    touch ${DATADIR}/llogdbschk001 &&\
                                    chown gbasedbt:gbasedbt ${DATADIR}/llogdbschk001 &&\
                                    chmod 660 ${DATADIR}/llogdbschk001 &&\
                                    onspaces -c -d llogdbs -p ${DATADIR}/llogdbschk001 -o 0 -s """
                                    +" "+(Integer.parseInt(configList.get(10).get(2))*Integer.parseInt(configList.get(11).get(2))+10240)+
                                    """
                                     &&\
                                    for i in `seq """
                                    +" "+Integer.parseInt(configList.get(11).get(2))+
                                    """
                                    `;do onparams -a -d llogdbs -s """
                                    +" "+Integer.parseInt(configList.get(10).get(2))+
                                    """
                                    ;done &&\
                                    for i in `seq 7`;do onmode -l;done &&\
                                    onmode -c &&\
                                    for i in `seq 6`;do onparams -d -l $i -y;done
                                    """;
                            System.out.println("logiccmd is:"+logicCmd);
                            if(executeCommandWithExitStatus(logicCmd)!=0){
                                throw new Exception("优化逻辑日志失败！");
                            }

                            Platform.runLater(() -> {
                                customInstallStepHbox8.iconLabel.setVisible(false);
                                customInstallStepHbox9.iconLabel.setVisible(true);
                            });
                            String[] parts = configList.get(12).get(2).split("\\*");
                            int tempdbsNum = Integer.parseInt(parts[0]);
                            int tempdbsSize = Integer.parseInt(parts[1]);
                            String onspaceCmd="";
                            String dbspaceTemp="tempdbs01";
                            for (int num = 1; num < tempdbsNum+1; num++) {
                                onspaceCmd+=("touch ${DATADIR}/tempdbs"+String.format("%02d", num)+"chk001 &&" +
                                        "chown gbasedbt:gbasedbt ${DATADIR}/tempdbs"+String.format("%02d", num)+"chk001 &&" +
                                        "chmod 660 ${DATADIR}/tempdbs"+String.format("%02d", num)+"chk001 &&" +
                                        "onspaces -c -d tempdbs"+String.format("%02d", num)+" -p ${DATADIR}/tempdbs"+String.format("%02d", num)+"chk001 -o 0 -s "+tempdbsSize+" -k 16 -t &&");
                                if(num>1) dbspaceTemp+=(",tempdbs"+String.format("%02d", num));
                            };
                            String tempdbsCmd="source ~gbasedbt/.bash_profile && DATADIR="
                                    +configList.get(5).get(2)+"&&"+onspaceCmd+ "onmode -wf DBSPACETEMP="+dbspaceTemp;
                            System.out.println("tempdbsCmd is:"+tempdbsCmd);
                            if(executeCommandWithExitStatus(tempdbsCmd)!=0){
                                throw new Exception("优化临时空间失败！");
                            }


                            Platform.runLater(() -> {
                                customInstallStepHbox9.iconLabel.setVisible(false);
                                customInstallStepHbox10.iconLabel.setVisible(true);
                            });
                            if(executeCommandWithExitStatus("""
                                    source ~gbasedbt/.bash_profile &&\
                                    DATADIR="""
                                    +configList.get(5).get(2)+
                                    """
                                     &&\
                                    touch ${DATADIR}/sbspace01chk001 &&\
                                    chown gbasedbt:gbasedbt ${DATADIR}/sbspace01chk001 &&\
                                    chmod 660 ${DATADIR}/sbspace01chk001 &&\
                                    onspaces -c -S sbspace01 -p ${DATADIR}/sbspace01chk001 -o 0 -s """
                                    +" "+configList.get(13).get(2)+ " "+
                                    """
                                     -Df "LOGGING = ON, AVG_LO_SIZE=1"
                                    """)!=0){
                                throw new Exception("创建智能大对象空间失败！");
                            }

                            Platform.runLater(() -> {
                                customInstallStepHbox10.iconLabel.setVisible(false);
                                customInstallStepHbox11.iconLabel.setVisible(true);
                            });
                            if(executeCommandWithExitStatus("""
                                    source ~gbasedbt/.bash_profile &&\
                                    DATADIR="""
                                    +configList.get(5).get(2)+
                                    """ 
                                    &&\
                                    touch ${DATADIR}/datadbs01chk001 &&\
                                    chown gbasedbt:gbasedbt ${DATADIR}/datadbs01chk001 &&\
                                    chmod 660 ${DATADIR}/datadbs01chk001 &&\
                                    onspaces -c -d datadbs01 -p ${DATADIR}/datadbs01chk001 -o 0 -s """
                                    +" "+configList.get(14).get(2)+ " "+
                                    """
                                    -k 16
                                    """)!=0){
                                throw new Exception("创建用户数据库空间失败！");
                            }

                            Platform.runLater(() -> {
                                customInstallStepHbox11.iconLabel.setVisible(false);
                                customInstallStepHbox12.iconLabel.setVisible(true);
                            });
                            if(executeCommandWithExitStatus("""
                                    source ~gbasedbt/.bash_profile &&\
                                    echo "create database """
                                    +" "+configList.get(15).get(2)+ " "+
                                    """
                                    in datadbs01 with log" |dbaccess - -
                                    """)!=0){
                                throw new Exception("创建默认数据库失败！");
                            }
                            Platform.runLater(() -> {
                                customInstallStepHbox12.iconLabel.setVisible(false);
                                customInstallStepHbox13.iconLabel.setVisible(true);
                            });
                            if(executeCommandWithExitStatus("""
                                    chmod +x /etc/rc.d/rc.local &&\
                                    sed -i '/^su - gbasedbt/d' /etc/rc.local &&\
                                    echo "su - gbasedbt -c \\"oninit\\"" >>/etc/rc.local
                                    """)!=0){
                                throw new Exception("配置开启自启动失败！");
                            }Platform.runLater(() -> {
                                customInstallStepHbox13.iconLabel.setVisible(false);
                                customInstallStepHbox14.iconLabel.setVisible(true);

                            });
                            executeCommandWithExitStatus("""
                                    source ~gbasedbt/.bash_profile &&
                                    mkdir -p $GBASEDBTDIR/scripts &&
                                    chown gbasedbt:gbasedbt $GBASEDBTDIR/scripts &&
                                    touch $GBASEDBTDIR/scripts/backup.sh &&
                                    chown gbasedbt:gbasedbt $GBASEDBTDIR/scripts/backup.sh &&
                                    chmod 775 $GBASEDBTDIR/scripts/backup.sh &&
                                    cat <<EOF >$GBASEDBTDIR/scripts/backup.sh
                                    #!/bin/bash
                                    . ~gbasedbt/.bash_profile
                                    onstat - |grep "On-Line" >/dev/null
                                    if [ \\$? -ne 1 ]
                                    then
                                    DATE=\\`date\\`
                                    echo "Level 0 backup of "\\$GBASEDBTSERVER" strat at "\\$DATE
                                    ontape -s -L 0
                                    DATE=\\`date\\`
                                    echo "Level 0 backup of "\\$GBASEDBTSERVER" completed at "\\$DATE
                                    TAPEDEV=\\`onstat -c|grep ^TAPEDEV |awk '{print \\$2}'\\`
                                    find \\${TAPEDEV} -mtime +7 -type f ! -name *.sh ! -name *.log |xargs rm -rf
                                    fi
                                    exit 0
                                    EOF
                                    """);
executeCommandWithExitStatus("""
source ~gbasedbt/.bash_profile &&\
touch $GBASEDBTDIR/scripts/GBase8schk.sh &&\
chown gbasedbt:gbasedbt $GBASEDBTDIR/scripts/GBase8schk.sh &&\
chmod 775 $GBASEDBTDIR/scripts/GBase8schk.sh &&\
cat <<GBASEEOF >$GBASEDBTDIR/scripts/GBase8schk.sh
#!/bin/bash
###################################################################################
# filename: GBase8schk.sh
# Last modified by: L3 2025-11-25
# support OS: Linux
# support database version: GBase 8s V8.x
# useage: sh GBase8schk.sh [0]
# 0 do not collect statistics,this may take a long time
###################################################################################

if [[ -n "\\${GBASEDBTSERVER}" ]]; then
    INSTANCE=\\${GBASEDBTSERVER}
elif [[ -n "\\${INFORMIXSERVER}" ]]; then
    INSTANCE=\\${INFORMIXSERVER}
else
    echo "ERROR:can't found instance name!"
    exit 1
fi

echo ""
echo "Begin to collect data for INSTANCE:"\\${INSTANCE}
echo ""
mytime=\\`date '+%Y%m%d%H%M%S'\\`
outpath="GBase8schk_\\${INSTANCE}_\\${mytime}"

if [ ! -d \\${outpath} ]; then
mkdir \\${outpath}
fi

###################################################################################
## Machine
###################################################################################
echo "collect machine info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/machine.unl delimiter '|'
select
os_name,os_release,os_nodename,os_version,os_machine,os_num_procs,os_num_olprocs,
os_pagesize,os_mem_total,os_mem_free,os_open_file_lim,os_shmmax
from  sysmachineinfo;
EOF

###################################################################################
## Instance
###################################################################################
echo "collect instance info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/instance.unl delimiter '|'
select
dbinfo('UTC_TO_DATETIME',sh_boottime)||' T' start_time,
(current year to second - dbinfo('UTC_TO_DATETIME',sh_boottime))||' T'  run_time,
sh_maxchunks as maxchunks,
sh_maxdbspaces maxdbspaces,
sh_maxuserthreads maxuserthreads,
sh_maxtrans maxtrans,
sh_maxlocks locks,
sh_longtx longtxs,
dbinfo('UTC_TO_DATETIME',sh_pfclrtime)||' T'  onstat_z_running_time
from sysshmvals;
EOF

###################################################################################
## CPUVP
###################################################################################
echo "collect cpuvp info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/cpuvp.unl delimiter '|'
select vpid,classname class,pid,round(usecs_user,2) user_cpu,round(usecs_sys,2) sys_cpu,num_ready,
total_semops,total_busy_wts,total_yields,total_spins,vp_cache_size,vp_cache_allocs
from sysvplst ;
EOF

###################################################################################
## Memory
###################################################################################
echo "collect instance memory info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/memory.unl delimiter '|'
select
indx,bufsize pagesize,
nbuffs buffers,
round(nbuffs*bufsize/1024/1024/1024,2)||'GB' buffsize,
nlrus,mindirty,maxdirty,
(bufwaits / (bufwrites + pagreads)) * 100.00 buff_wait_rate,
100 * (bufreads-dskreads)/ bufreads buff_read_rate,
100 * (bufwrites-dskwrites)/ bufwrites buff_write_rate,
fgwrites,lruwrites ,chunkwrites
from sysbufpool;
EOF

###################################################################################
## Network
###################################################################################
echo "collect sqlhosts info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./\\${outpath}/sqlhosts.unl delimiter '|'
select dbsvrnm,nettype,hostname,svcname,options,
svrsecurity,netbuf_size,svrgroup
from  syssqlhosts;
EOF

###################################################################################
## Session time
###################################################################################
echo "collect session runtime info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./\\${outpath}/sessiontime.unl delimiter '|'
SELECT first 500 s.sid, s.username, s.hostname, q.odb_dbname database,
dbinfo('UTC_TO_DATETIME',s.connected) conection_time,
dbinfo('UTC_TO_DATETIME',t.last_run_time) last_run_time,
current - dbinfo('UTC_TO_DATETIME',s.connected) connected_since,
current - dbinfo('UTC_TO_DATETIME',t.last_run_time) idle_time
FROM syssessions s, systcblst t, sysrstcb r, sysopendb q
WHERE t.tid = r.tid AND s.sid = r.sid AND s.sid = q.odb_sessionid
ORDER BY 8 DESC;
EOF

###################################################################################
## Session wait
###################################################################################
echo "collect session waits info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./\\${outpath}/sessionwait.unl delimiter '|'
select first 20 sid,pid, username, hostname,
is_wlatch, -- blocked waiting on a latch
is_wlock, -- blocked waiting on a locked record or table
is_wbuff, -- blocked waiting on a buffer
is_wckpt, -- blocked waiting on a checkpoint
is_incrit -- session is in a critical section of transaction-- (e.g writting to disk)
from syssessions
order by  is_wlatch+is_wlock+is_wbuff+is_wckpt+is_incrit desc;
EOF

###################################################################################
## Session IO
###################################################################################
echo "collect session IO info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./\\${outpath}/sessionio.unl delimiter '|'
select first 100 syssesprof.sid,isreads,iswrites,isrewrites,
isdeletes,bufreads,bufwrites,seqscans ,
pagreads ,pagwrites,total_sorts ,dsksorts  ,
max_sortdiskspace,logspused
from syssesprof, syssessions
where syssesprof.sid = syssessions.sid
order by bufreads+bufwrites desc
;
EOF

###################################################################################
## Checkpoint
###################################################################################
echo "collect checkpoint info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/checkpoint.unl delimiter '|'
select
intvl,type,caller,dbinfo('UTC_TO_DATETIME',clock_time)||' T' clock_time,
round(crit_time,4),round(flush_time,4),round(cp_time,4),n_dirty_buffs,
plogs_per_sec,llogs_per_sec,dskflush_per_sec,ckpt_logid,ckpt_logpos,physused,logused,
n_crit_waits,tot_crit_wait,longest_crit_wait,block_time
from syscheckpoint order by intvl;
EOF

###################################################################################
## Database
###################################################################################
echo "collect database info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/database.unl delimiter '|'
SELECT trim(name) dbname,trim(owner) owner, created||' T'  created_time,
TRIM(DBINFO('dbspace',partnum)) AS dbspace,
CASE WHEN is_logging+is_buff_log=1 THEN "Unbuffered logging"
     WHEN is_logging+is_buff_log=2 THEN "Buffered logging"
     WHEN is_logging+is_buff_log=0 THEN "No logging"
ELSE "" END Logging_mode
FROM sysdatabases
where trim(name) not like 'sys%';
EOF

###################################################################################
## DBspace
###################################################################################
echo "collect dbspaces info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/dbspace.unl delimiter '|'
SELECT A.dbsnum as No, trim(B.name) as name,
CASE  WHEN (bitval(B.flags,'0x10')>0 AND bitval(B.flags,'0x2')>0)
  THEN 'MirroredBlobspace'
  WHEN bitval(B.flags,'0x10')>0  THEN 'Blobspace'
  WHEN bitval(B.flags,'0x2000')>0 AND bitval(B.flags,'0x8000')>0
  THEN 'TempSbspace'
  WHEN bitval(B.flags,'0x2000')>0 THEN 'TempDbspace'
  WHEN (bitval(B.flags,'0x8000')>0 AND bitval(B.flags,'0x2')>0)
  THEN 'MirroredSbspace'
  WHEN bitval(B.flags,'0x8000')>0  THEN 'SmartBlobspace'
  WHEN bitval(B.flags,'0x2')>0    THEN 'MirroredDbspace'
        ELSE   'Dbspace'
END  as dbstype,
 round(sum(chksize)*2/1024/1024,2)||'GB'  as DBS_SIZE ,
 round(sum(decode(mdsize,-1,nfree,udfree))*2/1024/1024,2)||'GB' as free_size,
 case when sum(decode(mdsize,-1,nfree,udfree))*100/sum(decode(mdsize,-1,chksize,udsize))
   >sum(decode(mdsize,-1,nfree,nfree))*100/sum(decode(mdsize,-1,chksize,mdsize))
then TRUNC(100-sum(decode(mdsize,-1,nfree,nfree))*100/sum(decode(mdsize,-1,chksize,mdsize)),2)||"%"
else TRUNC(100-sum(decode(mdsize,-1,nfree,udfree))*100/sum(decode(mdsize,-1,chksize,udsize)),2)||"%"
    end  as used,
  TRUNC(MAX(A.pagesize/1024))||"KB" as pgsize,
  MAX(B.nchunks) as nchunks
FROM syschktab A, sysdbstab B
WHERE A.dbsnum = B.dbsnum
 GROUP BY A.dbsnum,name, 3
ORDER BY A.dbsnum;
EOF

###################################################################################
## Chunks
###################################################################################
echo "collect chunk info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/chunks.unl delimiter '|'
SELECT  A.chknum as num, B.name as spacename,
 TRUNC((A.pagesize/1024)) as pgsize,
 A.offset offset,
 round( A.chksize*2/1024/1024,2)||'GB'  as size,
 round(decode(A.mdsize,-1,A.nfree,A.udfree)*2/1024/1024,2)||'GB' as free,
 TRUNC(100 - decode(A.mdsize,-1,A.nfree,A.udfree)*100/A.chksize,2 )  as used,
 A.fname
FROM syschktab A, sysdbstab B
WHERE A.dbsnum = B.dbsnum
order by B.dbsnum;
EOF

###################################################################################
## Chunk IO
###################################################################################
echo "collect chunk IO using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/chunk_io.unl delimiter '|'
select d.name dbspace, fname[1,125] chunk_name,reads read_count,writes write_count,
reads+writes total_count,pagesread,pageswritten,
pagesread+pageswritten total_pg
from sysmaster:syschkio c, sysmaster:syschunks k, sysmaster:sysdbspaces d
where d.dbsnum = k.dbsnum and k.chknum  = c.chunknum
order by 8 desc;
EOF

###################################################################################
## Logical Log
###################################################################################
echo "collect logical log using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/logicallog.unl delimiter '|'
SELECT  A.number as num,  A.uniqid as uid,  round(A.size*2/1024,2)||'MB' as size,
 TRIM( TRUNC(A.used*100/A.size,0)||'%') as used,
d.name as spacename,
 TRIM( A.chunk||'_'||A.offset ) as location,
 decode(A.filltime,0,'NotFull',
 dbinfo('UTC_TO_DATETIME', A.filltime)::varchar(50))||' T' as filltime,
 CASE  WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x4')>0
   THEN 'UsedBackedUp'
   WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x2')>0
   THEN 'UsedCurrent'
   WHEN bitval(A.flags,'0x1') > 0   THEN 'Used'
   ELSE   hex(A.flags)::varchar(50)
 END as flags,
 CASE  WHEN A.filltime-B.filltime > 0 THEN
  round(CAST(TRUNC(A.size/(A.filltime-B.filltime),4)
      as varchar(20))*2/1024,2)||'MB/S'
   ELSE    ' N/A '   END as pps
FROM syslogfil A, syslogfil B,syschktab c, sysdbstab d
WHERE  A.uniqid-1 = B.uniqid
and c.dbsnum = d.dbsnum
and a.chunk=c.chknum
UNION
SELECT  A.number as num,  A.uniqid as uid, round(A.size*2/1024,2)||'MB' as size,
 TRIM( TRUNC(A.used*100/A.size,0)||'%') as used,
 d.name as spacename,
 TRIM( A.chunk||'_'||A.offset ) as location,
 decode(A.filltime,0,'NotFull',
 dbinfo('UTC_TO_DATETIME', A.filltime)::varchar(50))||' T'  as filltime,
 CASE   WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x4')>0
   THEN 'UsedBackedUp'
   WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x2')>0
   THEN 'UsedCurrent'
   WHEN bitval(A.flags,'0x1') > 0  THEN 'Used'
   WHEN bitval(A.flags,'0x8') > 0  THEN 'NewAdd'
   ELSE hex(A.flags)::varchar(50)  END as flags,
   'N/A' as pps
FROM syslogfil A ,syschktab c, sysdbstab d
WHERE ( A.uniqid = (SELECT min(uniqid) FROM syslogfil WHERE uniqid > 0)
   OR A.uniqid = 0  )
and c.dbsnum = d.dbsnum
and a.chunk=c.chknum
ORDER BY A.uniqid ;
EOF

###################################################################################
## Locks on Table
###################################################################################
echo "collect table locks using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/tab_actlock.unl delimiter '|'
select dbsname,tabname,
sum(pf_rqlock) as locks,
sum(pf_wtlock) as lockwaits,
sum(pf_deadlk) as deadlocks
from sysactptnhdr,systabnames
where systabnames.partnum = sysactptnhdr.partnum
group by dbsname,tabname
order by lockwaits,locks desc;
EOF

dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/tab_lock.unl delimiter '|'
select dbsname,tabname,
sum(lockreqs) as lockreqs,
sum(lockwts) as lockwaits,
sum(deadlks) as deadlocks
from sysptprof
group by dbsname,tabname
order by deadlocks desc,lockwaits desc,lockreqs desc;
EOF

###################################################################################
## Databaes Used Space
###################################################################################
echo "collect database used space using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/database_space.unl delimiter '|'
select t1.dbsname,
round(sum(ti_nptotal)*max(ti_pagesize)/1024/1024/1024,2)||'GB' allocated_size,
round(sum(ti_npused)*max(ti_pagesize)/1024/1024/1024,2)||'GB'  used_size
from systabnames t1, systabinfo t2,sysdatabases t3
where t1.partnum = t2.ti_partnum
and trim(t3.name)=trim(t1.dbsname)
group by dbsname
order by sum(ti_nptotal) desc;
EOF

###################################################################################
## Tables Space
###################################################################################
echo "collect table and index used space using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/tab_space.unl delimiter '|'
SELECT  st.dbsname databasename,  st.tabname,
    MAX(dbinfo('UTC_TO_DATETIME',sin.ti_created)) createdtime,
    SUM( sin.ti_nextns ) extents,
    SUM( sin.ti_nrows ) nrows,
    MAX( sin.ti_nkeys ) nkeys,
    MAX( sin.ti_pagesize ) pagesize,
    SUM( sin.ti_nptotal ) nptotal,
    round(SUM( sin.ti_nptotal*sd.pagesize )/1024/1024,2)||'MB' total_size,
    SUM( sin.ti_npused ) npused,
    round(SUM( sin.ti_npused*sd.pagesize )/1024/1024,2)||'MB' used_size,
    SUM( sin.ti_npdata ) npdata,
    round(SUM( sin.ti_npdata*sd.pagesize )/1024/1024,2)||'MB' data_size
FROM
    sysmaster:systabnames st,
    sysmaster:sysdbspaces sd,
    sysmaster:systabinfo sin
WHERE
    sd.dbsnum = trunc(st.partnum / 1048576)
    AND st.partnum = sin.ti_partnum
    AND st.dbsname NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1')
    AND st.tabname[1,3] NOT IN ('sys','TBL')
GROUP BY  1,  2
ORDER BY  8 DESC;
EOF

###################################################################################
## Tables Space By Partition
###################################################################################
echo "collect table and index partition used space using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/tab_space_frag.unl delimiter '|'
SELECT  st.dbsname databasename,  st.tabname,st.partnum partnum,
    dbinfo('UTC_TO_DATETIME',sin.ti_created) createdtime,
    sin.ti_nextns  extents,
    sin.ti_nrows nrows,
    sin.ti_nkeys  nkeys,
    sin.ti_pagesize  pagesize,
    sin.ti_nptotal  nptotal,
    round(( sin.ti_nptotal*sd.pagesize )/1024/1024,2)||'MB' total_size,
    ( sin.ti_npused ) npused,
    round(( sin.ti_npused*sd.pagesize )/1024/1024,2)||'MB' used_size,
    ( sin.ti_npdata ) npdata,
    round(( sin.ti_npdata*sd.pagesize )/1024/1024,2)||'MB' data_size
FROM
    sysmaster:systabnames st,
    sysmaster:sysdbspaces sd,
    sysmaster:systabinfo sin
WHERE
    sd.dbsnum = trunc(st.partnum / 1048576)
    AND st.partnum = sin.ti_partnum
    AND st.dbsname NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1')
    AND st.tabname[1,3] NOT IN ('sys','TBL')
ORDER BY  9 DESC;
EOF

###################################################################################
## Tables and index IO and seqscans
###################################################################################
echo "collect table and index io and seqscans using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/tab_io.unl delimiter '|'
SELECT
    st.dbsname,p.tabname,SUM( sin.ti_nrows ) nrows,
    round(SUM( sin.ti_nptotal*sd.pagesize )/1024/1024,2)||'MB' total_size,
    round(SUM( sin.ti_npused*sd.pagesize )/1024/1024,2)||'MB' used_size,
    SUM( seqscans ) AS seqscans,
    SUM( pagreads ) diskreads,
    SUM( bufreads ) bufreads,
    SUM( bufwrites ) bufwrites,
    SUM( pagwrites ) diskwrites,
    SUM( pagreads )+ SUM( pagwrites ) disk_rsws,
    trunc(decode(SUM( bufreads ),0,0,(100 -((SUM( pagreads )* 100)/ SUM( bufreads + pagreads )))),2) AS rbufhits,
    trunc(decode(SUM( bufwrites ),0,0,(100 -((SUM( pagwrites )* 100)/ SUM( bufwrites + pagwrites )))),2) AS wbufhits
FROM
    sysmaster:sysptprof p,
    sysmaster:systabinfo sin,
    sysmaster:sysdbspaces sd,
    sysmaster:systabnames st
WHERE
    sd.dbsnum = trunc(st.partnum / 1048576)
    AND p.partnum = st.partnum
    AND st.partnum = sin.ti_partnum
    AND st.dbsname NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1')
    AND st.tabname[1,3] NOT IN ('sys','TBL')
GROUP BY 1,  2
ORDER BY 11 DESC;
EOF

###################################################################################
## Current slowest sql
###################################################################################
echo "collect current slowest sql using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/slowsql.unl delimiter '|'
Select first 100 sqx_estcost,sqx_estrows,sqx_sqlstatement
FROM sysmaster:syssqexplain
order by sqx_estcost desc;
EOF

###################################################################################
## Table statistics,lockmode,index keys
###################################################################################
if [[ -z "\\$1" ]]; then
echo "collect tables statistics,lockmode,index keys using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./\\${outpath}/tabstat.sql delimiter ";"
select
"unload to ./\\${outpath}/"||trim(name)||"_stat.unl Select t.tabname,t.created as tabcreated,t.nrows,(select sum( ti_nrows ) from sysmaster:systabnames tn join sysmaster:systabinfo ti on ti.ti_partnum = tn.partnum  where t.tabname=tn.tabname   and dbsname = '"||trim(name)||"' )  as realrows,t.locklevel,t.ustlowts,i.idxname,"||
"trim(case when i.part1>0 then (select colname from "||trim(name)||":syscolumns where colno=i.part1 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part2>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part2 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part3>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part3 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part4>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part4 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part5>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part5 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part6>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part6 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part7>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part7 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part8>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part8 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part9>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part9 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part10>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part10 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part11>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part11 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part12>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part12 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part13>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part13 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part14>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part14 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part15>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part15 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part16>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part16 and tabid=i.tabid) else '' end ) index_cols"||
",i.nunique "||
"from "||trim(name)||":systables t left join "||trim(name)||":sysindexes i on t.tabid=i.tabid "||
"where t.tabid>99 "||
"and t.tabtype='T' "||
"order by 4 desc,1"
from sysdatabases
where name NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1','sys')
and is_logging=1;
EOF
dbaccess sysmaster \\${outpath}/tabstat.sql
fi

###################################################################################
## onstat cmd
###################################################################################
echo "collect instance running status using onstat commands ......"
onstat -b > ./\\${outpath}/onstat_b.unl
onstat -C all > ./\\${outpath}/onstat_C_all.unl
onstat -C > ./\\${outpath}/onstat_bigc.unl
onstat -c > ./\\${outpath}/onstat_c.unl
onstat -D > ./\\${outpath}/onstat_bigd.unl
onstat -d > ./\\${outpath}/onstat_d.unl
onstat -F  > ./\\${outpath}/onstat_F.unl
onstat -g act > ./\\${outpath}/onstat_g_act.unl
onstat -g arc > ./\\${outpath}/onstat_g_arc.unl
onstat -g ath > ./\\${outpath}/onstat_g_ath.unl
onstat -g buf > ./\\${outpath}/onstat_g_buf.unl
onstat -g cluster > ./\\${outpath}/onstat_g_cluster.unl
onstat -g cmsm > ./\\${outpath}/onstat_g_cmsm.unl
onstat -g cfg > ./\\${outpath}/onstat_g_cfg.unl
onstat -g cfg diff > ./\\${outpath}/onstat_g_cfg_diff.unl
onstat -g ckp > ./\\${outpath}/onstat_g_ckp.unl
onstat -g con > ./\\${outpath}/onstat_g_con.unl
onstat -g cpu > ./\\${outpath}/onstat_g_cpu.unl
onstat -g dic > ./\\${outpath}/onstat_g_dic.unl
onstat -g dis > ./\\${outpath}/onstat_g_dis.unl
onstat -g dsc > ./\\${outpath}/onstat_g_dsc.unl
onstat -g env > ./\\${outpath}/onstat_g_env.unl
onstat -g glo > ./\\${outpath}/onstat_g_glo.unl
onstat -g iof > ./\\${outpath}/onstat_g_iof.unl
onstat -g iog > ./\\${outpath}/onstat_g_iog.unl
onstat -g ioq > ./\\${outpath}/onstat_g_ioq.unl
onstat -g iov > ./\\${outpath}/onstat_g_iov.unl
onstat -g lmx > ./\\${outpath}/onstat_g_lmx.unl
#onstat -g mem > ./\\${outpath}/onstat_g_mem.unl
onstat -g mgm > ./\\${outpath}/onstat_g_mgm.unl
onstat -g ntd  > ./\\${outpath}/onstat_g_ntd.unl
onstat -g ntt  > ./\\${outpath}/onstat_g_ntt.unl
onstat -g ntu  > ./\\${outpath}/onstat_g_ntu.unl
onstat -g osi > ./\\${outpath}/onstat_g_osi.unl
onstat -g rea > ./\\${outpath}/onstat_g_rea.unl
onstat -g seg > ./\\${outpath}/onstat_g_seg.unl
onstat -g ses 0 > ./\\${outpath}/onstat_g_ses_0.unl
onstat -g ses > ./\\${outpath}/onstat_g_ses.unl
onstat -g smb s > ./\\${outpath}/onstat_g_smb_s.unl
#onstat -g spi | sort -n -k 2 | tail -200 > ./\\${outpath}/onstat_g_spi.unl
onstat -g sql > ./\\${outpath}/onstat_g_sql.unl
onstat -g sql 0 > ./\\${outpath}/onstat_g_sql_0.unl
#onstat -g ssc > ./\\${outpath}/onstat_g_ssc.unl
#onstat -g stk >onstat_g_stk.unl
#onstat -g sts >onstat_g_sts.unl
onstat -g wai > ./\\${outpath}/onstat_g_wai.unl
onstat -L > ./\\${outpath}/onstat_bigl.unl
onstat -l > ./\\${outpath}/onstat_l.unl
onstat -p > ./\\${outpath}/onstat_p.unl
onstat -R > ./\\${outpath}/onstat_R.unl
onstat -u > ./\\${outpath}/onstat_u.unl
onstat -V > ./\\${outpath}/onstat_V.unl
onstat -x > ./\\${outpath}/onstat_x.unl
onstat -X > ./\\${outpath}/onstat_bigx.unl

###################################################################################
## system cmd
###################################################################################
echo ""
echo "collect instance running status using system command ......"
echo ""
echo "collect cm memory ......"
ps -aux |grep cmsm > ./\\${outpath}/cm_mem.unl

echo ""
echo "collect online.log last 50000 rows......"
onlinefile=\\`onstat -m |grep 'Message Log File' | awk '{print \\$4}'\\`
tail -50000 \\${onlinefile} > ./\\${outpath}/online.log

echo ""
echo "collect current user env ......"
env > ./\\${outpath}/env.unl

echo ""
echo "collect system cpu and memory using vmstat ......"
vmstat 1 5 > ./\\${outpath}/vmstat.unl

cp GBase8schk.sh ./\\${outpath}

echo ""
echo "##################################################################"
echo "GBase 8s Database Health Check Finshed"
echo "tar all of the output files in path: \\${outpath}"
echo "tar -cvf \\${outpath}.tar \\${outpath} "
echo "##################################################################"

###################################################################################
## end of all
###################################################################################
GBASEEOF
""");

executeCommandWithExitStatus("""
source ~gbasedbt/.bash_profile &&\
touch $GBASEDBTDIR/scripts/GBase8smon.sh &&\
chown gbasedbt:gbasedbt $GBASEDBTDIR/scripts/GBase8smon.sh &&\
chmod 775 $GBASEDBTDIR/scripts/GBase8smon.sh &&\
cat <<GBASEEOF >$GBASEDBTDIR/scripts/GBase8smon.sh
#!/bin/bash
###################################################################################
# filename: GBase8smon.sh
# Last modified by: L3 2025-11-25
# support OS: Linux
# support database version: GBase 8s V8.x
# useage: sh GBase8smon.sh 5 100  #每5秒收集一次，收集100次
###################################################################################
# 以下信息，收集一次
if [ \\$# -lt 2 ]; then
  echo "Useage:sh gen.sh <interval> <count>"
  exit 0
else
  INTERVAL=\\$1
  COUNT=\\$2
fi
GENDATADIR=GBase8smon_\\$(date +%Y%m%d%H%M%S)
mkdir -p \\${GENDATADIR}
cd \\${GENDATADIR}
dmesg > dmesg.txt
free -m > free_m.txt
onstat -V > onstat_V.txt
onstat -d > onstat_d.txt
onstat -g seg > onstat_g_seg.txt
onstat -g env > onstat_g_env.txt
onstat -g osi > onstat_g_osi.txt
onstat -c > onstat_c.txt
onstat -g cluster > onstat_g_cluster.txt
onstat -g cmsm > onstat_g_cmsm.txt
ps -aux |grep cmsm > cm_mem.txt

# 以下信息，根据输入参数循环收集
for i in \\`seq \\$COUNT\\`
do
tmpdir=\\$(date +%Y%m%d%H%M%S)
mkdir \\$tmpdir
cd \\$tmpdir
onstat -g ses 0 > onstat_g_ses_0.txt
onstat -g stk > onstat_g_stk.txt
onstat -u > onstat_u.txt
onstat -x > onstat_x.txt
onstat -g ckp > onstat_g_ckp.txt
onstat -g ath > onstat_g_ath.txt
onstat -p > onstat_p.txt
onstat -g sql > onstat_g_sql.txt
vmstat > vmstat.txt
mpstat -P ALL > mpstat_P_ALL.txt
sar -d > sar_d.txt
cd ..
sleep \\$INTERVAL
done
cd ..
tar -cvf \\${GENDATADIR}.tar \\${GENDATADIR} >/dev/null 2>&1
rm -rf \\${GENDATADIR}
echo "GBase8smon.sh finished!"
echo "datafile is:"\\${GENDATADIR}.tar
GBASEEOF
""");


                            if(!configList.get(24).get(2).trim().isEmpty()){
                                String backupCmd="source ~gbasedbt/.bash_profile && mkdir -p "+configList.get(24).get(2)+"&& chown gbasedbt:gbasedbt "+configList.get(24).get(2)+"&& chmod 775 "+configList.get(24).get(2)+
                                        "&& onmode -wf TAPEBLK=2048 && onmode -wf LTAPEBLK=2048 && onmode -wf LTAPEDEV="
                                        +configList.get(24).get(2)+"&& onmode -wf TAPEDEV="+configList.get(24).get(2)+
                                        """
                                        &&\
                                        sed -i "s#^BACKUP_CMD.*#BACKUP_CMD=\\"ontape -a -d\\" #g" $GBASEDBTDIR/etc/log_full.sh &&\
                                        onmode -wf ALARMPROGRAM=$GBASEDBTDIR/etc/log_full.sh &&\
                                        sh -c 'if [ -f /etc/cron.allow ]; then grep -q "^gbasedbt$" /etc/cron.allow || echo "gbasedbt" >> /etc/cron.allow; fi; exit 0' &&\
                                        echo "0 0 * * * $GBASEDBTDIR/scripts/backup.sh >> $GBASEDBTDIR/tmp/backup.log 2>&1" | crontab -u gbasedbt -
                                        """;

                                System.out.println("backupCmd is:\n"+backupCmd);
                                //System.out.println(executeCommandWithExitStatus(backupCmd));

                                if(executeCommandWithExitStatus(backupCmd)!=0) {
                                    throw new Exception("配置备份失败！");
                                }


                            }Platform.runLater(() -> {
                                customInstallStepHbox14.iconLabel.setVisible(false);
                            });
                            return null;
                        }
                    };
                    installTask.setOnSucceeded(event1 -> {
                        backgroupHbox.setVisible(false);
                        customInstallStepHbox1.iconLabel.setVisible(false);
                        customInstallStepHbox2.iconLabel.setVisible(false);
                        customInstallStepHbox3.iconLabel.setVisible(false);
                        customInstallStepHbox4.iconLabel.setVisible(false);
                        customInstallStepHbox5.iconLabel.setVisible(false);
                        customInstallStepHbox6.iconLabel.setVisible(false);
                        customInstallStepHbox7.iconLabel.setVisible(false);
                        customInstallStepHbox8.iconLabel.setVisible(false);
                        customInstallStepHbox9.iconLabel.setVisible(false);
                        customInstallStepHbox10.iconLabel.setVisible(false);
                        customInstallStepHbox11.iconLabel.setVisible(false);
                        customInstallStepHbox12.iconLabel.setVisible(false);
                        customInstallStepHbox13.iconLabel.setVisible(false);
                        customInstallStepHbox14.iconLabel.setVisible(false);
                        currentStep++;
                        updateWizardState();

                        try {
                            databaseInfoArea.replaceText("");
                            databaseInfoArea.append("数据库版本\n", "-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(new File(remoteFilePath).getName() + "\n\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("数据库实例信息\n", "-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append("安装路径："+configList.get(1).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("实例名："+configList.get(2).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("监听IP："+configList.get(7).get(2)  + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("端口："+configList.get(8).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("库名：" +configList.get(15).get(2) +"\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("用户名/密码：gbasedbt/"+configList.get(0).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("字符集："+configList.get(3).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("GL_USEGLU："+configList.get(4).get(2) + "\n\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("空间配置\n", "-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append("数据文件路径："+configList.get(5).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("物理日志大小："+configList.get(9).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("逻辑日志大小："+configList.get(10).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("逻辑日志个数："+configList.get(11).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("临时空间配置："+configList.get(12).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("智能大对象空间大小："+configList.get(13).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("用户空间大小："+configList.get(14).get(2) + "\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append("onstat -d输出：\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");
                            databaseInfoArea.append(executeCommand("source ~gbasedbt/.bash_profile;onstat -d |sed '1,2d'") + "\n\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("参数配置\n", "-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(executeCommand("source ~gbasedbt/.bash_profile;onstat -g cfg |grep -v '^$' |sed '1,5d'") + "\n\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            //databaseInfoArea.insert(databaseInfoArea.getLength(), systemInfoArea.getDocument());
                            databaseInfoArea.append("服务器型号\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(machineInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            // 省略其他信息的显示代码（与原逻辑相同）
                            databaseInfoArea.append("操作系统版本\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(osInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("内核版本\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(kernelInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("CPU信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(cpuInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("内存信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(memInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            databaseInfoArea.append("磁盘信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(diskInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            fileSystemInfo = executeCommand("df -h");
                            databaseInfoArea.append("文件系统信息\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(fileSystemInfo + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            String coreParams= executeCommand("ipcs -l");
                            databaseInfoArea.append("内核参数\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(coreParams + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");

                            String ulimit= executeCommand("su - gbasedbt -c \"ulimit -a\"");
                            databaseInfoArea.append("gbasedt用户限制\n","-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            databaseInfoArea.append(ulimit + "\n\n","-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");


                            // 更新目标 TextArea 的文档
                            //databaseInfoArea.append("系统信息\n", "-fx-fill: #074675;-fx-font-weight: bold;-fx-font-family:system;");
                            //databaseInfoArea.append(systemInfoArea.getText()+ "\n\n", "-fx-fill: #000; -fx-font-weight: normal;-fx-font-family:Courier New;");



                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    });
                    installTask.setOnFailed(event1 -> {
                        backgroupHbox.setVisible(false);
                        backgroupHbox.setVisible(false);
                        customInstallStepHbox1.iconLabel.setVisible(false);
                        customInstallStepHbox2.iconLabel.setVisible(false);
                        customInstallStepHbox3.iconLabel.setVisible(false);
                        customInstallStepHbox4.iconLabel.setVisible(false);
                        customInstallStepHbox5.iconLabel.setVisible(false);
                        customInstallStepHbox6.iconLabel.setVisible(false);
                        customInstallStepHbox7.iconLabel.setVisible(false);
                        customInstallStepHbox8.iconLabel.setVisible(false);
                        customInstallStepHbox9.iconLabel.setVisible(false);
                        customInstallStepHbox10.iconLabel.setVisible(false);
                        customInstallStepHbox11.iconLabel.setVisible(false);
                        customInstallStepHbox12.iconLabel.setVisible(false);
                        customInstallStepHbox13.iconLabel.setVisible(false);
                        customInstallStepHbox14.iconLabel.setVisible(false);

                        String error = installTask.getException().getMessage();
                        AlterUtil.CustomAlert("错误", error);
                    });

                    try {
                        new Thread(installTask).start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    backgroupHbox.setVisible(true);
                    stopButton.setOnAction(event1->{
                        installTask.cancel();
                        backgroupHbox.setVisible(false);
                        customInstallStepHbox1.iconLabel.setVisible(false);
                        customInstallStepHbox2.iconLabel.setVisible(false);
                        customInstallStepHbox3.iconLabel.setVisible(false);
                        customInstallStepHbox4.iconLabel.setVisible(false);
                        customInstallStepHbox5.iconLabel.setVisible(false);
                        customInstallStepHbox6.iconLabel.setVisible(false);
                        customInstallStepHbox7.iconLabel.setVisible(false);
                        customInstallStepHbox8.iconLabel.setVisible(false);
                        customInstallStepHbox9.iconLabel.setVisible(false);
                        customInstallStepHbox10.iconLabel.setVisible(false);
                        customInstallStepHbox11.iconLabel.setVisible(false);
                        customInstallStepHbox12.iconLabel.setVisible(false);
                        customInstallStepHbox13.iconLabel.setVisible(false);
                        customInstallStepHbox14.iconLabel.setVisible(false);

                    });
                    mainDialog.setOnCloseRequest(event1 -> {
                        installTask.cancel();
                        if (session != null && session.isConnected()) {
                            session.disconnect();
                        }

                    });
                    cancelBtn.setOnAction(event1->{
                        installTask.cancel();
                        if (session != null && session.isConnected()) {
                            session.disconnect();
                        }
                    });
                    break;
                default:
                    break;
            }
            //currentStep++;
            //updateWizardState(); // 触发按钮状态更新
            event.consume();
        });


        finishBtn.setOnAction(e -> {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            mainDialog.close();
            Main.lastInstallConnect=new Connect();
            Main.lastInstallConnect.setIp(hostField.getText());
            Main.lastInstallConnect.setPort(configList.get(8).get(2));
            Main.lastInstallConnect.setUsername("gbasedbt");
            Main.lastInstallConnect.setPassword(configList.get(0).get(2));
            Platform.runLater(() -> {
                Main.mainController.createConnectLeaf();
            });
        });

        // 更新按钮状态
        updateButtonStates(previousBtn, nextBtn, finishBtn,cancelBtn);

    }

    // 初始化所有步骤面板
    private static void initStepPanes(Stage parent) {
        step1Pane = createStep1Content(parent);
        step2Pane = createStep2Content(parent);
        step3Pane = createStep3Content(parent);
        step4Pane = createStep4Content(parent);
        step5Pane = createStep5Content(parent);
    }

    // 步骤1：连接设置面板
    private static Node createStep1Content(Stage parent) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // 输入组件（保存引用）
        hostField = new CustomUserTextField();
        hostField.setPrefWidth(450);

        portField = new CustomUserTextField();
        portField.setText("22");

        passField = new CustomPasswordField();

        // 图标和标签
        Label ipLabel = new Label("主机名/IP");
        SVGPath ipIcon = new SVGPath();
        ipIcon.setScaleX(0.6);
        ipIcon.setScaleY(0.6);
        ipIcon.setContent("M6.776,4.72h1.549v6.827H6.776V4.72z M11.751,4.669c-0.942,0-1.61,0.061-2.087,0.143v6.735h1.53 V9.106c0.143,0.02,0.324,0.031,0.527,0.031c0.911,0,1.691-0.224,2.218-0.721c0.405-0.386,0.628-0.952,0.628-1.621 c0-0.668-0.295-1.234-0.729-1.579C13.382,4.851,12.702,4.669,11.751,4.669z M11.709,7.95c-0.222,0-0.385-0.01-0.516-0.041V5.895 c0.111-0.03,0.324-0.061,0.639-0.061c0.769,0,1.205,0.375,1.205,1.002C13.037,7.535,12.53,7.95,11.709,7.95z M10.117,0 C5.523,0,1.8,3.723,1.8,8.316s8.317,11.918,8.317,11.918s8.317-7.324,8.317-11.917S14.711,0,10.117,0z M10.138,13.373 c-3.05,0-5.522-2.473-5.522-5.524c0-3.05,2.473-5.522,5.522-5.522c3.051,0,5.522,2.473,5.522,5.522 C15.66,10.899,13.188,13.373,10.138,13.373z");
        ipIcon.setFill(Color.valueOf("#888"));
        ipLabel.setGraphic(ipIcon);

        Label portLabel = new Label("端口");
        SVGPath portIcon = new SVGPath();
        portIcon.setScaleX(0.7);
        portIcon.setScaleY(0.7);
        portIcon.setContent("M1.625 10.4453 Q1.4375 9.7734 1.5781 9.1016 Q1.7344 8.4297 2.1562 7.8984 Q2.5938 7.3672 3.2188 7.0547 Q3.8438 6.7422 4.5156 6.7422 L19.4844 6.7422 Q20.1562 6.7422 20.7812 7.0547 Q21.4062 7.3672 21.8281 7.8984 Q22.2656 8.4297 22.4062 9.1016 Q22.5625 9.7734 22.4219 10.4453 L21.3125 14.9609 Q21.0781 15.9609 20.25 16.6172 Q19.4375 17.2578 18.3906 17.2578 L5.6094 17.2578 Q4.5625 17.2578 3.7344 16.6172 Q2.9219 15.9609 2.6875 14.9609 L1.625 10.4453 ZM7.4844 11.2578 Q7.8281 11.2578 8.0312 11.0391 Q8.25 10.8203 8.25 10.5078 Q8.25 10.1953 8.0312 9.9922 Q7.8281 9.7734 7.5156 9.7734 Q7.2031 9.7734 6.9844 9.9922 Q6.7656 10.1953 6.7656 10.5078 Q6.7656 10.8203 6.9844 11.0391 Q7.2031 11.2578 7.4844 11.2578 ZM9.75 13.5078 Q9.75 13.1797 9.5312 12.9609 Q9.3125 12.7422 9 12.7422 Q8.6875 12.7422 8.4688 12.9609 Q8.25 13.1797 8.25 13.4922 Q8.25 13.8047 8.4688 14.0547 Q8.6875 14.2891 9 14.2578 Q9.3125 14.2266 9.5312 14.0234 Q9.75 13.8047 9.75 13.5078 ZM12 14.2266 Q12.2812 14.2266 12.5156 14.0234 Q12.7656 13.8047 12.7656 13.4922 Q12.7656 13.1797 12.5156 12.9609 Q12.2812 12.7422 12 12.7422 Q11.7188 12.7422 11.4688 12.9609 Q11.2344 13.1797 11.2344 13.4922 Q11.2344 13.8047 11.4688 14.0547 Q11.7188 14.2891 12 14.2891 L12 14.2266 ZM15.75 13.5078 Q15.75 13.1797 15.5312 12.9609 Q15.3125 12.7422 15 12.7422 Q14.6875 12.7422 14.4688 12.9609 Q14.25 13.1797 14.25 13.4922 Q14.25 13.8047 14.4688 14.0547 Q14.6875 14.2891 15 14.2578 Q15.3125 14.2266 15.5312 14.0234 Q15.75 13.8047 15.75 13.5078 ZM10.5156 11.2578 Q10.7969 11.2578 11.0156 11.0391 Q11.2344 10.8203 11.2344 10.5078 Q11.2344 10.1953 11.0156 9.9922 Q10.7969 9.7734 10.4844 9.7734 Q10.1719 9.7734 9.9531 9.9922 Q9.75 10.1953 9.75 10.5078 Q9.75 10.8203 9.9531 11.0391 Q10.1719 11.2578 10.5156 11.2578 ZM14.25 10.4922 Q14.25 10.1953 14.0312 9.9922 Q13.8281 9.7734 13.5156 9.7734 Q13.2031 9.7734 12.9844 9.9922 Q12.7656 10.1953 12.7656 10.5078 Q12.7656 10.8203 12.9844 11.0391 Q13.2031 11.2578 13.5156 11.2578 Q13.8281 11.2578 14.0312 11.0391 Q14.25 10.8203 14.25 10.4922 ZM16.5156 11.2578 Q16.7969 11.2578 17.0156 11.0391 Q17.2344 10.8203 17.2344 10.5078 Q17.2344 10.1953 17.0156 9.9922 Q16.7969 9.7734 16.4844 9.7734 Q16.1719 9.7734 15.9531 9.9922 Q15.75 10.1953 15.75 10.5078 Q15.75 10.8203 15.9531 11.0391 Q16.1719 11.2578 16.5156 11.2578 Z");
        portIcon.setFill(Color.valueOf("#888"));
        portLabel.setGraphic(portIcon);

        Label passwdLabel = new Label("root密码");
        SVGPath passwdIcon = new SVGPath();
        passwdIcon.setScaleX(0.5);
        passwdIcon.setScaleY(0.5);
        passwdIcon.setContent("M15.75 1.5 Q14.3438 1.5 13.125 2.0312 Q11.8906 2.5469 10.9688 3.4688 Q10.0625 4.375 9.5469 5.625 Q9 6.8438 9 8.25 L9 8.25 Q9 8.25 9 8.25 Q9 8.25 9 8.25 Q9 8.7812 9.0781 9.2812 Q9.1719 9.7656 9.3125 10.2656 L9.2812 10.2188 L1.5 18 L1.5 22.5 L6 22.5 L13.7812 14.7188 Q14.2344 14.8594 14.7344 14.9375 Q15.2344 15 15.7812 15 Q17.1094 15 18.2812 14.5312 Q19.4531 14.0312 20.3594 13.2031 Q21.2656 12.375 21.8281 11.25 Q22.3906 10.125 22.5 8.8281 L22.5 8.8125 Q22.5312 8.6719 22.5312 8.5156 Q22.5312 8.3438 22.5312 8.1719 Q22.5312 7.0938 22.1875 6.1094 Q21.8438 5.1094 21.2656 4.2812 L21.2656 4.3125 Q20.3438 3.0156 18.9062 2.2656 Q17.4688 1.5 15.7969 1.5 Q15.7812 1.5 15.7656 1.5 Q15.75 1.5 15.75 1.5 L15.75 1.5 L15.75 1.5 ZM15.75 13.5 Q15.3594 13.5 14.9688 13.4375 Q14.5781 13.375 14.2031 13.2656 L14.25 13.2656 L13.3906 13 L10.3594 16.0312 L9.3125 15 L8.25 16.0469 L9.2812 17.0781 L8.0938 18.2812 L7.0625 17.25 L6 18.2969 L7.0312 19.3281 L5.375 21 L3 21 L3 18.625 L11 10.6094 L10.7812 9.8906 Q10.6406 9.5312 10.5781 9.125 Q10.5312 8.7188 10.5312 8.2656 Q10.5312 6.75 11.2812 5.5156 Q12.0469 4.2656 13.2969 3.5781 L13.3125 3.5625 Q13.8594 3.2812 14.4688 3.125 Q15.0938 2.9688 15.75 2.9688 Q16.8281 2.9688 17.7656 3.375 Q18.7031 3.7656 19.4062 4.4688 Q20.1094 5.1562 20.5625 6.0625 Q20.9844 7 21 8.0625 L21 8.0625 Q21 8.125 21 8.1875 Q21 8.25 21 8.3125 Q21 9.0938 20.7812 9.7969 Q20.5781 10.5 20.1875 11.0781 L20.1875 11.0625 Q19.4844 12.1562 18.3125 12.8281 Q17.1406 13.5 15.75 13.5 Q15.75 13.5 15.75 13.5 Q15.75 13.5 15.75 13.5 L15.75 13.5 L15.75 13.5 ZM18 7.5 Q18 8.125 17.5625 8.5625 Q17.1406 9 16.5 9 Q15.875 9 15.4375 8.5625 Q15 8.125 15 7.5 Q15 6.8594 15.4375 6.4375 Q15.875 6 16.5 6 Q17.1406 6 17.5625 6.4375 Q18 6.8594 18 7.5 Z");
        passwdIcon.setFill(Color.valueOf("#888"));
        passwdLabel.setGraphic(passwdIcon);



        // 布局
        grid.add(ipLabel, 0, 0);
        grid.add(hostField, 1, 0);
        grid.add(portLabel, 0, 1);
        grid.add(portField, 1, 1);
        grid.add(passwdLabel, 0, 2);
        grid.add(passField, 1, 2);
        Label descbefore=new Label("请填写需要远程安装数据库的服务器信息：");

        Label desc=new Label("说明：");
        Label desc1=new Label("1、远程安装仅用于Linux或Unix系统远程安装，不适用于Windows系统。");
        Label desc2=new Label("2、安装前可准备好已下载的安装包，如未准备，可在安装过程中自动下载。");
        Label desc3=new Label("3、安装前会自动卸载之前已存在的GBase 8s数据库安装，并清理所有相关信息。");
        Label desc4=new Label("4、远程安装向导支持GBase 8s V8.7、GBase 8s V8.8。");
        VBox vBox=new VBox(10);
        vBox.setStyle("-fx-padding: 10 0 0 30");
        grid.setStyle("-fx-padding: 10 0 10 0;");
        vBox.getChildren().addAll(descbefore,grid,desc,desc1,desc2,desc3,desc4);
        StackPane stackPane = new StackPane(vBox);

        // 保存连接任务引用，用于验证步骤1
        //step1ConnectTask = connectTask;

        return stackPane;
    }



    // 步骤2：系统信息面板
    private static Node createStep2Content(Stage parent) {
        systemInfoArea = new CustomInlineCssTextArea();
        //codeArea.appendText("正在获取系统信息...\n");
        CustomInfoStackPane stackPane = new CustomInfoStackPane(systemInfoArea);
        stackPane.showNoticeInMain=false;
        // 进入步骤2时自动加载信息
        //loadSystemInfo();

        return stackPane;
    }



    // 步骤3：文件选择面板
    private static Node createStep3Content(Stage parent) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10,20,10,20));

        Button browseButton = new Button("");
        SVGPath browseButtonIcon = new SVGPath();
        browseButtonIcon.setScaleX(0.6);
        browseButtonIcon.setScaleY(0.6);
        browseButtonIcon.setContent("M9.8438 1.7184 Q12.0469 1.7184 13.9219 2.7965 Q15.7969 3.8746 16.8906 5.7496 Q18 7.609 18 9.8278 Q18 12.4684 16.4688 14.6246 L21.8906 20.0934 Q22.2656 20.4371 22.2812 20.9684 Q22.3125 21.484 21.9531 21.8746 Q21.5938 22.2496 21.0938 22.2809 Q20.5938 22.2965 20.2031 21.9684 L14.6406 16.4528 Q12.4844 17.984 9.8438 17.984 Q7.625 17.984 5.75 16.8903 Q3.8906 15.7809 2.8125 13.9059 Q1.7344 12.0309 1.7344 9.8278 Q1.7344 7.609 2.8125 5.7496 Q3.8906 3.8746 5.75 2.7965 Q7.625 1.7184 9.8438 1.7184 ZM9.8438 4.2496 Q8.3594 4.2496 7.0625 4.9996 Q5.7656 5.7496 5.0156 7.0465 Q4.2656 8.3434 4.2656 9.8278 Q4.2656 11.3121 5.0156 12.609 Q5.7656 13.9059 7.0625 14.6559 Q8.3594 15.3903 9.8594 15.3903 Q11.375 15.3903 12.6406 14.6559 Q13.9219 13.9059 14.6562 12.6403 Q15.4062 11.359 15.4062 9.859 Q15.4062 8.3434 14.6562 7.0778 Q13.9219 5.7965 12.6406 5.0309 Q11.375 4.2496 9.8438 4.2496 Z");
        browseButtonIcon.setFill(Color.valueOf("#074675"));
        browseButton.setGraphic(new Group(browseButtonIcon));
        browseButton.getStyleClass().add("little-custom-button");
        browseButton.setFocusTraversable(false);
        browseButton.setTooltip(new Tooltip("浏览安装包"));

        Button downloadButton = new Button("");
        SVGPath downloadButtonIcon = new SVGPath();
        downloadButtonIcon.setScaleX(0.6);
        downloadButtonIcon.setScaleY(0.6);
        downloadButtonIcon.setContent("M19.0156 9 L15 9 L15 3 L9 3 L9 9 L5 9 L12 16.9844 L19.0156 9 ZM4.0156 19 L20 19 L20 21 L4.0156 21 L4.0156 19 Z");
        downloadButtonIcon.setFill(Color.valueOf("#074675"));
        downloadButton.setGraphic(new Group(downloadButtonIcon));
        downloadButton.getStyleClass().add("little-custom-button");
        downloadButton.setFocusTraversable(false);
        downloadButton.setTooltip(new Tooltip("下载安装包"));



        //selectedFileLabel = new Label("选择已下载的安装包，或点击下载图标自动下载与CPU型号匹配的最新版本。");
        remotePathField = new CustomUserTextField();
        //remotePathField.setText("/tmp");
        installFilePathField= new CustomUserTextField();
        installFilePathField.setMinWidth(450);
        installFilePathField.setMaxWidth(450);
        remotePathField.setMaxWidth(450);
        HBox downloadHBox = new HBox(new Label("选择已下载的安装包，或点击"),downloadButton,new Label("自动下载与CPU型号匹配的最新试用版本，下载到桌面并填充下框。"));
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(installFilePathField,browseButton);
        // 浏览文件事件
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择安装包");
            selectedFile = fileChooser.showOpenDialog(parent);
            if (selectedFile != null) {
                installFilePathField.setText(selectedFile.getAbsolutePath());
                remoteFilePath = "/tmp/" + selectedFile.getName();
                remotePathField.setText(remoteFilePath);

                // 自动建议安装命令
                if (selectedFile.getName().endsWith(".deb")) {
                    installFilePathField.setText("dpkg -i " + remoteFilePath);
                } else if (selectedFile.getName().endsWith(".rpm")) {
                    installFilePathField.setText("rpm -ivh " + remoteFilePath);
                } else if (selectedFile.getName().endsWith(".sh")) {
                    installFilePathField.setText("chmod +x " + remoteFilePath + " && ./" + remoteFilePath);
                }
            }
        });
        StackPane downloadStackPane=new StackPane();
        downloadButton.setOnAction(event -> {
            File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
            String fileName="";
            String url="";
            if(systemInfoArea.getText().contains("x86_64")) {
                url = "https://www.dbboys.com/dl/gbase8s/server/x86/latest.tar";
            }else if(systemInfoArea.getText().contains("aarch64")) {
                url="https://www.dbboys.com/dl/gbase8s/server/arm/latest.tar";
            }else{
                AlterUtil.CustomAlert("错误","未知系统平台，请手动下载数据库安装包！");
                return;
            }
            try {
                /*
                HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                Optional<String> location = response.headers().firstValue("Location");
                String realUrl=location.orElse("");
                fileName=realUrl.substring(realUrl.lastIndexOf("/")+1);

                 */
                fileName=DownloadManagerUtil.getRealFileNameFromRedirect(url);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                AlterUtil.CustomAlert("下载失败",e.getMessage());
                return;
            }

            File saveFile = new File(desktopDir, fileName);
            InstallAutoDownLoadUtil.remotePathField=remotePathField;
            InstallAutoDownLoadUtil.installFilePathField=installFilePathField;
            InstallAutoDownLoadUtil.stackPane=downloadStackPane;
            //if(systemInfoArea.getText().contains("x86_64"))
            InstallAutoDownLoadUtil.addDownload(url,saveFile,true,null);
            //else InstallAutoDownLoadUtil.addDownload("https://www.dbboys.com/dl/gbase8s/server/arm/latest",saveFile,true,null);

        });
        downloadStackPane.setMinHeight(50);
        notUploadedRadioButton.setToggleGroup(uploadToggleGroup);
        notUploadedRadioButton.setSelected(true);
        downloadButton.disableProperty().bind(notUploadedRadioButton.selectedProperty().not());
        installFilePathField.disableProperty().bind(notUploadedRadioButton.selectedProperty().not());
        browseButton.disableProperty().bind(notUploadedRadioButton.selectedProperty().not());
        uploadedRadioButton.setToggleGroup(uploadToggleGroup);
        remotePathField.disableProperty().bind(uploadedRadioButton.selectedProperty().not());
        content.getChildren().addAll(
                new Label("上传安装包到远程服务器："),
                notUploadedRadioButton,
                downloadHBox,
                hBox,
                downloadStackPane,
                uploadedRadioButton,
                new Label("远程服务器上安装包路径，如安装包已上传，请在下框填入安装包绝对路径："),
                remotePathField
        );

        return content;
    }



    // 步骤2：系统信息面板
    private static Node createStep4Content(Stage parent) {
        customInstallStepHbox1=new CustomInstallStepHbox("・卸载现有安装","kill所有gbasedbt用户进程，删除所有安装路径，删除gbasedbt数据文件，删除gbasedbt用户及组。");
        customInstallStepHbox2=new CustomInstallStepHbox("・检查系统依赖","检查/opt不小于8G，权限755，/tmp不小于1G，内存不小于1G，检查所需unzip等依赖包、关闭防火墙等。");
        customInstallStepHbox3=new CustomInstallStepHbox("・创建用户组及用户","创建gbasedbt用户组和gbasedbt用户，配置环境变量GBASEDBTDIR、GBASEDBTSERVER等。");
        customInstallStepHbox4=new CustomInstallStepHbox("・安装数据库软件","安装软件到gbasedbt用户默认环境变量$GBASEDBTDIR指定路径。");
        customInstallStepHbox5=new CustomInstallStepHbox("・初始化数据库实例","初始化数据库实例，数据文件路径$GBASEDBTDIR/dbs，监听IP 0.0.0.0，端口 9088。");
        customInstallStepHbox6=new CustomInstallStepHbox("・优化配置参数","优化CPU、内存等关键参数，启用数据库用户，关闭sysadmin，重启数据库实例。");
        customInstallStepHbox7=new CustomInstallStepHbox("・优化物理日志","创建物理日志空间plogdbs，并将物理日志从rootdbs中移动到plogdbs。");
        customInstallStepHbox8=new CustomInstallStepHbox("・优化逻辑日志","创建逻辑日志空间llogdbs，并将物理日志从rootdbs中移动到llogdbs。");
        customInstallStepHbox9=new CustomInstallStepHbox("・优化临时空间","创建临时数据库空间tmpdbs01，避免在rootdbs中执行排序等操作。");
        customInstallStepHbox10=new CustomInstallStepHbox("・创建大对象空间","创建默认智能大对象空间sbspace01，用于存放blob/clob数据。");
        customInstallStepHbox11=new CustomInstallStepHbox("・创建用户数据空间","创建用户数据空间datadbs01，存放用户数据。");
        customInstallStepHbox12=new CustomInstallStepHbox("・创建默认数据库","创建默认用户数据库gbasedb，存储于datadbs01。");
        customInstallStepHbox13=new CustomInstallStepHbox("・配置开机自启","默认开机自启，自启方式为在/etc/rc.local中添加启动命令。");
        customInstallStepHbox14=new CustomInstallStepHbox("・配置备份","默认不配置备份及逻辑日志归档，可在自定义设置开启，备份脚本位于$GBASEDBTDIR/scripts。");

        HBox titleHBox=new HBox(new Label("点击【下一步】开始安装，如需自定义设置，点击【"));
        Button envButton=MenuItemUtil.createModifyButton("自定义设置");
        titleHBox.getChildren().add(envButton);
        titleHBox.getChildren().add(new Label("】编辑"));

        envButton.setOnAction(event -> {
            modifyEnv();
        });
        titleHBox.setAlignment(Pos.CENTER);
        VBox vBox=new VBox(4,titleHBox,customInstallStepHbox1,customInstallStepHbox2,customInstallStepHbox3,customInstallStepHbox4,customInstallStepHbox5,customInstallStepHbox6,customInstallStepHbox7,customInstallStepHbox8,customInstallStepHbox9,customInstallStepHbox10,customInstallStepHbox11,customInstallStepHbox12,customInstallStepHbox13,customInstallStepHbox14);
        vBox.setAlignment(Pos.TOP_CENTER);
        StackPane stackPane = new StackPane(vBox);


        // 进入步骤2时自动加载信息
        //loadSystemInfo();

        return stackPane;
    }

    // 步骤5：安装结果面板
    private static Node createStep5Content(Stage parent) {
        //codeArea.appendText("正在获取系统信息...\n");
        databaseInfoArea=new CustomInlineCssTextArea();
        CustomInfoStackPane stackPane = new CustomInfoStackPane(databaseInfoArea);
        stackPane.showNoticeInMain=false;

        // 进入步骤2时自动加载信息
        //loadSystemInfo();

        return stackPane;
    }



    // 更新向导状态（标题、显示步骤）
    private static void updateWizardState() {
        mainDialog.setTitle("远程安装向导 - 步骤 " + currentStep + "/5");
        showCurrentStep();
        updateButtonStates(
                (Button) mainDialog.getDialogPane().lookupButton(ButtonType.PREVIOUS),
                (Button) mainDialog.getDialogPane().lookupButton(ButtonType.NEXT),
                (Button) mainDialog.getDialogPane().lookupButton(ButtonType.FINISH),
                        (Button) mainDialog.getDialogPane().lookupButton(ButtonType.CANCEL)
        );
    }

    // 显示当前步骤的面板
    private static void showCurrentStep() {
        step1Pane.setVisible(currentStep == 1);
        step2Pane.setVisible(currentStep == 2);
        step3Pane.setVisible(currentStep == 3);
        step4Pane.setVisible(currentStep == 4);
        step5Pane.setVisible(currentStep == 5);
    }

    // 更新按钮状态（显示/隐藏及布局占用）
    private static void updateButtonStates(Button previous, Button next, Button finish,Button cancel) {

        // 上一步：当前步骤>1时显示并参与布局，否则隐藏且不占空间

        boolean showPrevious = currentStep > 1 && currentStep < 5;
        previous.setVisible(showPrevious);
        previous.setManaged(showPrevious);

        // 下一步：当前步骤<5时显示并参与布局，否则隐藏且不占空间
        boolean showNext = currentStep < 5;
        next.setVisible(showNext);
        next.setManaged(showNext);
        if(!next.isDisable()&&next.isVisible())next.requestFocus();

        // 完成：当前步骤=5时显示并参与布局，否则隐藏且不占空间
        boolean showFinish = currentStep == 5;
        finish.setVisible(showFinish);
        finish.setManaged(showFinish);
        cancel.setVisible(!showFinish);
        cancel.setManaged(!showFinish);
    }

    // 以下为原有工具方法（保持不变）
    private static String executeCommand(String command) throws JSchException, IOException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);

        InputStream in = channelExec.getInputStream();
        channelExec.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        channelExec.disconnect();
        return output.toString().trim();
    }

    private static boolean testConnection() {
        try {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }

            session = jsch.getSession(username, hostname, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect(5000); // 5秒超时
            return true;
        } catch (JSchException e) {
            System.err.println("连接错误: " + e.getMessage());
            return false;
        }
    }

    private static boolean isFileSelectionValid(File file, String remotePath, String command) {
        return file != null && file.exists()
                && remotePath != null && !remotePath.trim().isEmpty()
                && command != null && !command.trim().isEmpty();
    }

    private static void showErrorDialog(Stage parent, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(parent);
        centerDialogToParent(alert, parent);
        alert.showAndWait();
    }

    private static class ProgressMonitorInputStream extends FilterInputStream {
        private final long totalSize;
        private long bytesRead = 0;
        private final DoubleProperty progress;

        public ProgressMonitorInputStream(InputStream in, long totalSize, DoubleProperty progress) {
            super(in);
            this.totalSize = totalSize;
            this.progress = progress;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytes = super.read(b, off, len);
            if (bytes > 0) {
                bytesRead += bytes;
                double currentProgress = (double) bytesRead / totalSize;
                Platform.runLater(() -> progress.set(currentProgress));
            }
            return bytes;
        }
    }

    private static void centerDialogToParent(Dialog<?> dialog, Stage parent) {
        Platform.runLater(() -> {
            if (parent == null || !parent.isShowing()) {
                return;
            }

            dialog.getDialogPane().applyCss();
            dialog.getDialogPane().layout();

            double parentX = parent.getX();
            double parentY = parent.getY();
            double parentWidth = parent.getWidth();
            double parentHeight = parent.getHeight();

            double dialogWidth = dialog.getDialogPane().getWidth();
            double dialogHeight = dialog.getDialogPane().getHeight();

            double dialogX = parentX + (parentWidth - dialogWidth) / 2;
            double dialogY = parentY + (parentHeight - dialogHeight) / 2;

            dialog.setX(dialogX);
            dialog.setY(dialogY);
        });
    }

    private static int executeCommandWithExitStatus(String command) throws JSchException, InterruptedException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);
        channelExec.connect();

        while (!channelExec.isClosed()) {
            Thread.sleep(100);
        }

        int exitStatus = channelExec.getExitStatus();
        channelExec.disconnect();
        return exitStatus;
    }

    private static boolean isCommandExists(String command) throws JSchException, InterruptedException {
        int exitStatus = executeCommandWithExitStatus("command -v " + command);
        return exitStatus == 0;
    }

    public static void modifyEnv(){

        // 将JSONArray转换为ObservableList
        List<ObservableList<String>> datalist = FXCollections.observableArrayList();//如果确认，返回更新后的list

        for (ObservableList<String> row : configList) {
            datalist.add(FXCollections.observableArrayList(row));
        }

        CustomResultsetTableView tableView = new CustomResultsetTableView();
        tableView.setEditable(true);
        tableView.setSortPolicy((param) -> false);//禁用排序

        TableColumn<ObservableList<String>, Object> nameColumn = new TableColumn<ObservableList<String>, Object>("配置项");
        nameColumn.setCellFactory(col -> new CustomLostFocusCommitTableCell<ObservableList<String>, Object>());
        nameColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(1)));
        nameColumn.setReorderable(false); // 禁用拖动
        nameColumn.setEditable(false);
        nameColumn.setReorderable(false);
        nameColumn.setPrefWidth(150);
        TableColumn<ObservableList<String>, Object> valueColumn = new TableColumn<ObservableList<String>, Object>("值（可修改）");
        valueColumn.setCellFactory(col -> new CustomLostFocusCommitTableCell<ObservableList<String>, Object>());
        valueColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(2)));
        valueColumn.setReorderable(false); // 禁用拖动
        valueColumn.setEditable(true);
        valueColumn.setReorderable(false);
        valueColumn.setPrefWidth(120);

        valueColumn.setOnEditCommit(event -> {
            Object oldvalue = (Object) event.getOldValue();

            // 获取当前行的模型数据（ObservableList<String>）
            ObservableList<String> rowData = event.getRowValue();

            // 获取编辑后的新值
            Object newValue = event.getNewValue();

            // 更新ObservableList中索引1的位置（与cellValueFactory对应）
            if (rowData.size() > 2) {  // 确保索引有效
                // 转换为字符串（根据实际需求调整类型）
                rowData.set(2,  newValue.toString());
                if(rowData.get(1).equals("数据文件路径")){
                    try {
                        Path path = Paths.get((String) newValue);
                        String remoteScript =
                                "path=\"" + newValue + "\";" +
                                        "while [ ! -e \"$path\" ]; do " +
                                        "  path=$(dirname \"$path\"); " +
                                        "  [ \"$path\" = \"/\" ] && break; " +
                                        "done;" +
                                        "df -m \"$path\" |tail -1 |awk '{print $4/1000}'";
                        freeDiskSize= Double.valueOf(executeCommand(remoteScript));
                        //System.out.println("checkResult is:"+checkResult);
                        /*
                        if((executeCommandWithExitStatus("test -d "+newValue) == 0)) {
                            diskSize = Double.parseDouble(executeCommand("df -m " + newValue + " |tail -1 |awk '{print $4/1000}'"));
                        }else{
                            event.getRowValue().set(2, oldvalue == null ? null : oldvalue.toString());
                            event.getTableView().refresh();
                        }

                         */
                    } catch (Exception e) {
                        event.getRowValue().set(2, oldvalue == null ? null : oldvalue.toString());
                        event.getTableView().refresh();
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    //diskSize=1024000000.0;
                    int PHYSFILE=1024000;
                    int LOGFILES=10;
                    String TEMPDBS="1*1024000";
                    int SBDBSSIZE=1024000;
                    int DATADBSSIZE=1024000;
                    if(freeDiskSize>100&&freeDiskSize<=200){
                        PHYSFILE=5120000;
                        LOGFILES=50;
                        TEMPDBS="2*5120000";
                        SBDBSSIZE=5120000;
                        DATADBSSIZE=5120000;
                    }else if(freeDiskSize>200){
                        PHYSFILE=10240000;
                        LOGFILES=100;
                        TEMPDBS="2*10240000";
                        SBDBSSIZE=10240000;
                        DATADBSSIZE=10240000;
                    }
                    datalist.get(9).set(2,String.valueOf(PHYSFILE));
                    datalist.get(11).set(2,String.valueOf(LOGFILES));
                    datalist.get(12).set(2,TEMPDBS);
                    datalist.get(13).set(2,String.valueOf(SBDBSSIZE));
                    datalist.get(14).set(2,String.valueOf(DATADBSSIZE));
                }
                tableView.refresh();
            }
        });

        TableColumn<ObservableList<String>, Object> labelColumn = new TableColumn<ObservableList<String>, Object>("说明");
        labelColumn.setCellFactory(col -> new CustomLostFocusCommitTableCell<ObservableList<String>, Object>());
        labelColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(3)));
        labelColumn.setReorderable(false); // 禁用拖动
        labelColumn.setEditable(false);
        labelColumn.setReorderable(false);
        labelColumn.setPrefWidth(420);

        tableView.getColumns().addAll(nameColumn, valueColumn,labelColumn);
        tableView.getItems().clear();
        tableView.getItems().addAll(datalist);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("自定义配置");
        alert.setHeaderText("");
        alert.setGraphic(null); //避免显示问号
        //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getScene().getStylesheets().add(RemoteInstallerUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image("file:images/logo.png"));
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(tableView);
        alert.getDialogPane().setContent(hbox);

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
        ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
        if (result == buttonTypeOk) {
            configList.clear();
            for (ObservableList<String> row : datalist) {
                configList.add(FXCollections.observableArrayList(row));
            }
        }

    }

    public static void initConfigList(){
        //自定义安装配置
        Double totalMem;
        int NUMCPU=1;
        try {
            freeDiskSize=Double.parseDouble(executeCommand("df -m /opt |tail -1 |awk '{print $4/1000}'"));
            totalMem=Double.parseDouble(executeCommand("free -m |sed -n 2p |awk '{print $2/1024}'"));
            NUMCPU=Integer.parseInt(executeCommand("cat /proc/cpuinfo |grep -c processor"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int LOCKS=1000000;
        int SHMVIRTSIZE=102400;
        int DS_TOTAL_MEMORY=102400;
        int K2BUFFERS=51200;
        int K16BUFFERS=51200;
        if(totalMem>2&&totalMem<=4){
            K16BUFFERS=102400;
        }else if(totalMem>4&&totalMem<=8){
            SHMVIRTSIZE=512000;
            DS_TOTAL_MEMORY=512000;
            K2BUFFERS=102400;
            K16BUFFERS=204800;
        }else if(totalMem>8&&totalMem<=16){
            SHMVIRTSIZE=1024000;
            DS_TOTAL_MEMORY=1024000;
            K2BUFFERS=512000;
            K16BUFFERS=409600;
        }else if(totalMem>16&&totalMem<=32){
            LOCKS=10000000;
            SHMVIRTSIZE=2048000;
            DS_TOTAL_MEMORY=2048000;
            K2BUFFERS=512000;
            K16BUFFERS=819200;
        }else if(totalMem>32){
            LOCKS=10000000;
            SHMVIRTSIZE=4096000;
            DS_TOTAL_MEMORY=4096000;
            K2BUFFERS=512000;
            K16BUFFERS=1500000;
        }

        /*
        int PHYSFILE=102400;
        int LOGFILES=3;
        String TEMPDBS="1*102400";
        int SBDBSSIZE=102400;
        int DATADBSSIZE=102400;

        */
        int PHYSFILE=1024000;
        int LOGFILES=10;
        String TEMPDBS="1*1024000";
        int SBDBSSIZE=1024000;
        int DATADBSSIZE=1024000;


        if(freeDiskSize>100&&freeDiskSize<=200){
            PHYSFILE=5120000;
            LOGFILES=50;
            TEMPDBS="2*5120000";
            SBDBSSIZE=5120000;
            DATADBSSIZE=5120000;
        }else if(freeDiskSize>200){
            PHYSFILE=10240000;
            LOGFILES=100;
            TEMPDBS="2*10240000";
            SBDBSSIZE=10240000;
            DATADBSSIZE=10240000;
        }
        configList.clear();
        configList.add(FXCollections.observableArrayList(null, "gbasedbt用户密码", "8S*P)0Od@.&","保持密码强度，部分系统如强度不够可能导致设置密码失败"));
        configList.add(FXCollections.observableArrayList(null, "GBASEDBTDIR", "/opt/gbase","数据库软件安装路径，无特殊要求不修改"));
        configList.add(FXCollections.observableArrayList(null, "GBASEDBTSERVER", "gbase01","数据库实例名，无特殊要求不修改"));
        configList.add(FXCollections.observableArrayList(null, "DB_LOCALE", "zh_CN.utf8","默认字符集推荐utf8，如要兼容GBK使用zh_CN.gb18030-2000"));
        configList.add(FXCollections.observableArrayList(null, "GL_USEGLU", "1","是否开启GLU，建议开启，0关闭"));
        configList.add(FXCollections.observableArrayList(null, "数据文件路径", "$GBASEDBTDIR/dbs","如/data，路径必须存在，修改后相关空间大小根据空间可用量自动重新计算"));
        configList.add(FXCollections.observableArrayList(null, "ROOTSIZE", "1024000","根空间大小，建议不小于1G，固定值。"));
        configList.add(FXCollections.observableArrayList(null, "监听IP", "0.0.0.0","默认监听所有IP，如无特殊要求不修改"));
        configList.add(FXCollections.observableArrayList(null, "监听端口", "9088","默认端口9088，如无特殊要求不修改"));
        configList.add(FXCollections.observableArrayList(null, "PHYSFILE", String.valueOf(PHYSFILE),"物理日志大小，建议不小于10G，默认根据数据文件路径可用空间自动计算"));
        configList.add(FXCollections.observableArrayList(null, "LOGSIZE", "102400","单个逻辑日志大小，建议100MB固定值"));
        configList.add(FXCollections.observableArrayList(null, "LOGFILES", String.valueOf(LOGFILES),"逻辑日志个数，建议不小于100个，默认根据数据文件路径可用空间自动计算"));
        configList.add(FXCollections.observableArrayList(null, "临时空间配置", TEMPDBS,"数量*大小，如1*10240000，建议不小于10G，默认根据数据文件路径可用空间自动计算"));
        configList.add(FXCollections.observableArrayList(null, "智能大对象空间大小",String.valueOf(SBDBSSIZE),"建议不小于10G，默认根据数据文件路径可用空间自动计算"));
        configList.add(FXCollections.observableArrayList(null, "用户数据空间大小", String.valueOf(DATADBSSIZE),"建议不小于10G，默认根据数据文件路径可用空间自动计算"));
        configList.add(FXCollections.observableArrayList(null, "用户默认数据库名", "gbasedb","默认gbasedb，可自定义修改"));
        configList.add(FXCollections.observableArrayList(null, "LOCKS", String.valueOf(LOCKS),"建议不小于10000000，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "DS_TOTAL_MEMORY", String.valueOf(DS_TOTAL_MEMORY),"建议不小于4096000，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "DS_NONPDQ_QUERY_MEM", String.valueOf(DS_TOTAL_MEMORY/4),"建议不小于1024000，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "SHMVIRTSIZE", String.valueOf(SHMVIRTSIZE),"建议不小于4096000，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "SHMADD", String.valueOf(SHMVIRTSIZE/4),"建议不小于1024000，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "VPCLASS", "cpu,num="+NUMCPU+",noage","如是numa架构多路服务器，可绑定CPU，默认等于CPU内核数量"));
        configList.add(FXCollections.observableArrayList(null, "BUFFERPOOL", "size=2k,buffers="+K2BUFFERS+",lrus=32,lru_min_dirty=50,lru_max_dirty=60","建议不小于1G，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "BUFFERPOOL", "size=16k,buffers="+K16BUFFERS+",lrus=128,lru_min_dirty=50,lru_max_dirty=60","建议不超过内存的50%，默认根据内存自动计算"));
        configList.add(FXCollections.observableArrayList(null, "备份路径", "","填写路径后每天0点执行全量备份到填写的指定路径，逻辑日志自动归档，保留7天。"));
    }



}