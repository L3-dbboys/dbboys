package com.dbboys.util;

import com.dbboys.customnode.*;
import com.jcraft.jsch.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Properties;

public class RemoteCheckEnvUtil {
    // 存储用户输入信息
    private static String hostname;
    private static int port = 22;
    private static String username = "root";
    private static String password;
    private static File selectedFile;
    private static String remoteFilePath;

    // SSH相关对象
    private static JSch jsch = new JSch();
    private static Session session;

    // 进度属性
    private static final DoubleProperty progress = new SimpleDoubleProperty(0);
    private static final long DIALOG_WIDTH = 600;
    private static final long DIALOG_HEIGHT = 400;




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
        mainDialog.setTitle("安装环境检查 - 步骤 1/2");
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
        contentStack.getChildren().addAll(step1Pane, step2Pane,backgroupHbox);


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
                                        String machineInfo = executeCommand("dmidecode -s system-product-name");
                                        String osInfo;
                                        if (isCommandExists("nkvers")) {
                                            osInfo = executeCommand("nkvers");
                                        } else if (executeCommandWithExitStatus("test -f /etc/redhat-release") == 0) {
                                            osInfo = executeCommand("cat /etc/redhat-release");
                                        } else {
                                            osInfo = executeCommand("cat /etc/os-release");
                                        }
                                        String cpuInfo = executeCommand("lscpu");
                                        String memInfo = executeCommand("free -h");
                                        String fileSystemInfo = executeCommand("df -h");
                                        String diskInfo = executeCommand("lsblk");
                                        String kernelInfo = executeCommand("uname -a");
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
        });

        // 更新按钮状态
        updateButtonStates(previousBtn, nextBtn, finishBtn,cancelBtn);

    }

    // 初始化所有步骤面板
    private static void initStepPanes(Stage parent) {
        step1Pane = createStep1Content(parent);
        step2Pane = createStep2Content(parent);
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
        Label descbefore=new Label("请填写需要检查安装环境服务器信息：");

        Label desc=new Label("说明：安装环境检查仅用于Linux或Unix系统远程安装，不适用于Windows系统。");
        VBox vBox=new VBox(10);
        vBox.setStyle("-fx-padding: 10 0 0 30");
        grid.setStyle("-fx-padding: 10 0 10 0;");
        vBox.getChildren().addAll(descbefore,grid,desc);
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


    // 步骤2：系统信息面板




    // 更新向导状态（标题、显示步骤）
    private static void updateWizardState() {
        mainDialog.setTitle("安装环境检查 - 步骤 " + currentStep + "/2");
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
    }

    // 更新按钮状态（显示/隐藏及布局占用）
    private static void updateButtonStates(Button previous, Button next, Button finish,Button cancel) {

        // 上一步：当前步骤>1时显示并参与布局，否则隐藏且不占空间

        boolean showPrevious = currentStep > 1;
        previous.setVisible(showPrevious);
        previous.setManaged(showPrevious);

        // 下一步：当前步骤<5时显示并参与布局，否则隐藏且不占空间
        boolean showNext = currentStep < 2;
        next.setVisible(showNext);
        next.setManaged(showNext);
        if(!next.isDisable()&&next.isVisible())next.requestFocus();

        // 完成：当前步骤=5时显示并参与布局，否则隐藏且不占空间
        boolean showFinish = currentStep == 2;
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
}