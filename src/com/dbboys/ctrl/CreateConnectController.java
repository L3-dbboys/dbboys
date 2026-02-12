package com.dbboys.ctrl;


import com.dbboys.customnode.CustomInfoCodeArea;
import com.dbboys.customnode.CustomLostFocusCommitTableCell;
import com.dbboys.customnode.CustomResultsetTableView;
import com.dbboys.customnode.CustomTableCell;
import com.dbboys.customnode.CustomUserTextField;
import com.dbboys.db.DbConnectionFactory;
import com.dbboys.util.*;
import com.dbboys.app.Main;
import com.dbboys.vo.ConnectFolder;
import com.dbboys.vo.Connect;
import com.dbboys.vo.Prop;
import com.dbboys.vo.TreeData;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.List;


public class CreateConnectController {
    private static final Logger log = LogManager.getLogger(CreateConnectController.class);
    private final DbConnectionFactory dbConnectionFactory = new DbConnectionFactory();

    @FXML
    public ChoiceBox connectfolder_choicebox;
    @FXML
    private ChoiceBox dbtype_choicebox;
    @FXML
    private ChoiceBox driver_choicebox;
    @FXML
    private CustomUserTextField connectname_textfield;
    @FXML
    private TextField ipaddr_textfield;
    @FXML
    private TextField port_textfield;
    @FXML
    private TextField username_textfield;
    @FXML
    private PasswordField passwd_textfield;
    @FXML
    private CheckBox readonly_checkbox;
    @FXML
    private HBox connecting_hbox;
    @FXML
    private Button connecting_stop_button;
    @FXML
    private ButtonType commit_buttontype;
    @FXML
    private ButtonType test_buttontype;
    @FXML
    private ButtonType cancel_buttontype;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private HBox groupHbox;
    @FXML
    private CustomUserTextField group_textfield;
    public  String choiceName;
    public  TreeData treedataPram;
    public  Boolean isCopy;
    public  String props;
    public  Button cancelButton;
    public  Dialog dialog;

    public CreateConnectController(){


    }
    //public AddInstance(String choiceName){
    //    this.choiceName = choiceName;
    //}

    public void init(TreeData treedataPram,Boolean isCopy,Dialog dialog){
        this.dialog = dialog;
        //默认属性
        this.props="[{\"propName\":\"APPENDISAM\",\"propValue\":\"\"},{\"propName\":\"CLIENT_LOCALE\",\"propValue\":\"\"},{\"propName\":\"CSM\",\"propValue\":\"\"},{\"propName\":\"DBANSIWARN\",\"propValue\":\"\"},{\"propName\":\"DBDATE\",\"propValue\":\"Y4MD-\"},{\"propName\":\"DBSPACETEMP\",\"propValue\":\"\"},{\"propName\":\"DBTEMP\",\"propValue\":\"\"},{\"propName\":\"DBUPSPACE\",\"propValue\":\"\"},{\"propName\":\"DB_LOCALE\",\"propValue\":\"\"},{\"propName\":\"DELIMIDENT\",\"propValue\":\"\"},{\"propName\":\"ENABLE_TYPE_CACHE\",\"propValue\":\"\"},{\"propName\":\"ENABLE_HDRSWITCH\",\"propValue\":\"\"},{\"propName\":\"FET_BUF_SIZE\",\"propValue\":\"\"},{\"propName\":\"GBASEDBTCONRETRY\",\"propValue\":\"\"},{\"propName\":\"GBASEDBTCONTIME\",\"propValue\":\"\"},{\"propName\":\"GBASEDBTOPCACHE\",\"propValue\":\"\"},{\"propName\":\"GBASEDBTSERVER\",\"propValue\":\"\"},{\"propName\":\"GBASEDBTSERVER_SECONDARY\",\"propValue\":\"\"},{\"propName\":\"GBASEDBTSTACKSIZE\",\"propValue\":\"\"},{\"propName\":\"IFX_AUTOFREE\",\"propValue\":\"\"},{\"propName\":\"IFX_BATCHUPDATE_PER_SPEC\",\"propValue\":\"\"},{\"propName\":\"IFX_CODESETLOB\",\"propValue\":\"\"},{\"propName\":\"IFX_DIRECTIVES\",\"propValue\":\"\"},{\"propName\":\"IFX_EXTDIRECTIVES\",\"propValue\":\"\"},{\"propName\":\"IFX_GET_SMFLOAT_AS_FLOAT\",\"propValue\":\"\"},{\"propName\":\"IFX_ISOLATION_LEVEL\",\"propValue\":\"\"},{\"propName\":\"IFX_FLAT_UCSQ\",\"propValue\":\"\"},{\"propName\":\"IFX_LOCK_MODE_WAIT\",\"propValue\":\"10\"},{\"propName\":\"IFX_PAD_VARCHAR\",\"propValue\":\"\"},{\"propName\":\"IFX_SET_FLOAT_AS_SMFLOAT\",\"propValue\":\"\"},{\"propName\":\"IFX_SOC_TIMEOUT\",\"propValue\":\"\"},{\"propName\":\"IFX_TRIMTRAILINGSPACES\",\"propValue\":\"1\"},{\"propName\":\"IFX_USEPUT\",\"propValue\":\"\"},{\"propName\":\"IFX_USE_STRENC\",\"propValue\":\"\"},{\"propName\":\"IFX_XASPEC\",\"propValue\":\"\"},{\"propName\":\"IFX_XASTDCOMPLIANCE_XAEND\",\"propValue\":\"\"},{\"propName\":\"IFXHOST\",\"propValue\":\"\"},{\"propName\":\"IFXHOST_SECONDARY\",\"propValue\":\"\"},{\"propName\":\"JDBCTEMP\",\"propValue\":\"\"},{\"propName\":\"LOBCACHE\",\"propValue\":\"\"},{\"propName\":\"LOGINTIMEOUT\",\"propValue\":\"1000\"},{\"propName\":\"NEWCODESET\",\"propValue\":\"\"},{\"propName\":\"NEWNLSMAP\",\"propValue\":\"\"},{\"propName\":\"NODEFDAC\",\"propValue\":\"\"},{\"propName\":\"OPT_GOAL\",\"propValue\":\"\"},{\"propName\":\"OPTCOMPIND\",\"propValue\":\"\"},{\"propName\":\"OPTOFC\",\"propValue\":\"\"},{\"propName\":\"PATH\",\"propValue\":\"\"},{\"propName\":\"PDQPRIORITY\",\"propValue\":\"\"},{\"propName\":\"PORTNO_SECONDARY\",\"propValue\":\"\"},{\"propName\":\"PROXY\",\"propValue\":\"\"},{\"propName\":\"PSORT_DBTEMP\",\"propValue\":\"\"},{\"propName\":\"PSORT_NPROCS\",\"propValue\":\"\"},{\"propName\":\"SECURITY\",\"propValue\":\"\"},{\"propName\":\"SQLIDEBUG\",\"propValue\":\"\"},{\"propName\":\"SQLMODE\",\"propValue\":\"\"},{\"propName\":\"SRV_FET_BUF_SIZE\",\"propValue\":\"\"},{\"propName\":\"STMT_CACHE\",\"propValue\":\"\"},{\"propName\":\"TRUSTED_CONTEXT\",\"propValue\":\"\"},{\"propName\":\"METADATA_UPPERCASE\",\"propValue\":\"\"}]";
        this.treedataPram = treedataPram;
        if(treedataPram!=null&&treedataPram instanceof Connect){
            this.props=((Connect)treedataPram).getProps();
        }
        this.isCopy=isCopy;
        Connect connect=new Connect();
        ObservableList<TreeData> list = FXCollections.observableArrayList(SqliteDBaccessUtil.getConnectFolders());
        connectfolder_choicebox.setItems(list);
        connectfolder_choicebox.getValue();
        connectfolder_choicebox.getSelectionModel().select(0);
        connect.setParentId(((ConnectFolder)connectfolder_choicebox.getSelectionModel().getSelectedItem()).getId());
        connectfolder_choicebox.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            connect.setParentId(((ConnectFolder)newValue).getId());
        });
        //根据目录里的文件夹，读取数据库种类
        List dbtypes=new ArrayList<String>();
        File folder = new File("extlib");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                dbtypes.add(file.getName());
            }
        }
        Collections.sort(dbtypes);
        ObservableList<Connect> dbtypelist = FXCollections.observableArrayList(dbtypes);
        dbtype_choicebox.setItems(dbtypelist);


        //driver增加监听，发生变化重置connect.driver
        driver_choicebox.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            //driver_choicebox.setItems会触发此事件，此时newvalue==null，需要排除
            if(newValue!=null){
                connect.setDriver(newValue.toString());
            }
        });
        //dbtype发生变化监听，变化后设置connect的dbtype属性，并改变driver驱动列表
        dbtype_choicebox.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            connect.setDbtype(newValue.toString());
            List dirverList=new ArrayList<String>();
            File driverfolder = new File("extlib/"+connect.getDbtype());
            for (File file : driverfolder.listFiles()) {
                if (file.isFile()&&file.getName().toLowerCase().endsWith(".jar")) {
                    dirverList.add(file.getName());
                }
            }
            Collections.sort(dirverList);
            ObservableList<Connect> driverItems = FXCollections.observableArrayList(dirverList);
            driver_choicebox.setItems(driverItems); //触发内容变化监听
            driver_choicebox.getSelectionModel().select(driverItems.size()-1);
        });
        dbtype_choicebox.getSelectionModel().select(0);

        //如果安装了数据库，用最后一次安装的配置填充表单
        if(Main.lastInstallConnect!=null){
            ipaddr_textfield.setText(Main.lastInstallConnect.getIp());
            port_textfield.setText(Main.lastInstallConnect.getPort());
            username_textfield.setText(Main.lastInstallConnect.getUsername());
            passwd_textfield.setText(Main.lastInstallConnect.getPassword());
        }else{
            username_textfield.setText("gbasedbt");
            port_textfield.setText("9088");
        }

        //如果传了参数，可能树分类上右键新建连接或编辑连接，需将已有参数填充到表单
        if(treedataPram!=null){
            String connectFolder=null;
            //如果当前选中的元素级别为2，就是在分类上右键创建连接，默认选择对应的系统分类就行
            if(treedataPram instanceof ConnectFolder){
                connectFolder=treedataPram.getName();
            }else{
                //如果不是分类上右键新建，那就是编辑连接，所有信息填充到表单
                connectFolder=SqliteDBaccessUtil.getConnectType(((Connect) treedataPram));
                connectname_textfield.setText(((Connect)treedataPram).getName());
                if(!((Connect)treedataPram).getPropByName("GBASEDBTSERVER").isEmpty()){
                    group_textfield.setText(((Connect)treedataPram).getPropByName("GBASEDBTSERVER"));
                    switchGroupOrIP();
                }else{
                    ipaddr_textfield.setText(((Connect) treedataPram).getIp());
                    port_textfield.setText(((Connect) treedataPram).getPort());
                }
                username_textfield.setText(((Connect) treedataPram).getUsername());
                passwd_textfield.setText(((Connect) treedataPram).getPassword());
                readonly_checkbox.setSelected(((Connect) treedataPram).getReadonly());
                connect.setId(((Connect) treedataPram).getId()); //用于检查连接名称是否已存在，如果是编辑，要排除自己的名字
                connect.setDatabase(((Connect) treedataPram).getDatabase());
                int j=0;
                ObservableList<String> items = dbtype_choicebox.getItems();
                for (String item : items) {
                    if(item.equals(((Connect) treedataPram).getDbtype())){
                        dbtype_choicebox.getSelectionModel().select(j);
                    }
                    j++;
                }

                int z=0;
                ObservableList<String> driveritems = driver_choicebox.getItems();
                for (String item : driveritems) {
                    if(item.equals(((Connect) treedataPram).getDriver())){
                        driver_choicebox.getSelectionModel().select(z);
                    }
                    z++;
                }
            }

            int i=0;
            for(TreeData treeData:list){
                if(treeData.getName().equals(connectFolder)){
                    connectfolder_choicebox.getSelectionModel().select(i);
                    break;
                }
                i++;
            }

        }

        connectname_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            connectname_textfield.setText(newValue.replace(" ", ""));
        });
        ipaddr_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            ipaddr_textfield.setText(newValue.replace(" ", ""));
        });
        port_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                port_textfield.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        username_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            username_textfield.setText(newValue.replace(" ", ""));
        });
        passwd_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
        });



        Button tryConnectButton = (Button) dialogPane.lookupButton(test_buttontype);
        tryConnectButton.disableProperty().bind(connecting_hbox.visibleProperty());
        tryConnectButton.addEventFilter(ActionEvent.ACTION, event -> {
            if(!checkInput())
            {
                event.consume();
            }else {
                setConnect(connect);
                
                //如果连接信息可正常连接，检查连接名是否已存在
                commitConnecting(connect,false);
            }
            event.consume();
        });


        final Button commitButton = (Button) dialogPane.lookupButton(commit_buttontype);
        commitButton.disableProperty().bind(connecting_hbox.visibleProperty());
        commitButton.addEventFilter(ActionEvent.ACTION, event1 -> {
            if(!checkInput())
            {
                event1.consume();
            }else {
                setConnect(connect);
                //如果连接信息可正常连接，检查连接名是否已存在
                if(SqliteDBaccessUtil.checkConnectLeafNameExists(connect)){
                    AlterUtil.CustomAlert("错误","连接名称\""+connect.getName()+"\"已存在，请输入其他名称。");
                    event1.consume();
                }else {//如果连接名不存在，增加新节点
                    commitConnecting(connect,true);
                    event1.consume();
                }
            }
        });
        //connectname_textfield.requestFocus();

        cancelButton = (Button) dialogPane.lookupButton(cancel_buttontype);

    }

    public void setConnect(Connect connect){

        connect.setProps(props);
                if(groupHbox.isVisible()){
                    connect.setPropByName("GBASEDBTSERVER", group_textfield.getText());
                    if (connectname_textfield.getText().isEmpty()) {
                    connect.setName(group_textfield.getText());
                    }else {
                    connect.setName(connectname_textfield.getText());
                }
                    props=connect.getProps();
                }else{
                    connect.setIp(ipaddr_textfield.getText());
                    connect.setPort(port_textfield.getText());
                    if (connectname_textfield.getText().isEmpty()) {
                    connect.setName("[" + connect.getIp() + "_" + connect.getPort() + "]");
                    }else {
                    connect.setName(connectname_textfield.getText());
                }
                }

                if (connect.getDatabase() == null) {
                    connect.setDatabase("sysmaster");
                }
                connect.setUsername(username_textfield.getText());
                connect.setPassword(passwd_textfield.getText());
                
                connect.setReadonly(readonly_checkbox.isSelected());
            
    }
    public boolean checkInput(){
        if(groupHbox.isVisible()){
                if(group_textfield.getText().isEmpty()){
                    group_textfield.requestFocus();
                    return false;
                }
            }else 
                {
                    if(ipaddr_textfield.getText().isEmpty()){
                        //ipaddr_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                        ipaddr_textfield.requestFocus();
                        return false;
                    }
                    else if(port_textfield.getText().isEmpty()){
                        //port_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                        port_textfield.requestFocus();
                        return false;
                    }
                }
            if(username_textfield.getText().isEmpty()){
                // username_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                username_textfield.requestFocus();
                return false;
            }
            else if(passwd_textfield.getText().isEmpty()){
                //passwd_textfield.setStyle("-fx-border-color: #ff0000;-fx-border-radius: 3");
                passwd_textfield.requestFocus();
                return false;
            }
            return true;
    }

    public void initialize() throws IOException {


    }


        //添加驱动包
        public void addDriverCliecked(){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择驱动程序");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jar Files", "*.jar")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                // 处理选中的文件
                ObservableList<String> items = driver_choicebox.getItems();
                if(items.stream().anyMatch(name -> name.equals(selectedFile.getName()))){
                    AlterUtil.CustomAlert("错误","该驱动包同名文件已存在！");
                }else{

                    Path sourcePath = Paths.get(selectedFile.getAbsolutePath());
                    Path targetPath = Paths.get("extlib/"+dbtype_choicebox.getValue()+"/"+selectedFile.getName());
                    Boolean md5same=false;
                    String sourceSamename=null;
                    try {
                        String sourceMd5=MD5Util.getMD5Checksum(sourcePath.toFile().getAbsolutePath());
                        String targetMd5=null;
                        ObservableList<String> drivers = driver_choicebox.getItems();
                        for (String driver : drivers) {
                            targetMd5=MD5Util.getMD5Checksum(Paths.get("extlib/"+dbtype_choicebox.getValue()+"/"+driver).toFile().getAbsolutePath());
                            if(targetMd5.equals(sourceMd5)){
                                md5same=true;
                                sourceSamename=driver;
                                break;
                            }
                        }
                        if(md5same){
                            AlterUtil.CustomAlert("错误","此驱动程序与已有驱动\""+sourceSamename+"\"MD5一致，为重复文件！");
                        }
                        else{
                            String newItem=selectedFile.getName();
                            driver_choicebox.getItems().add(newItem);
                            driver_choicebox.setValue(newItem);
                            Files.copy(sourcePath, targetPath);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
                    }
                }
            }
        }

        //删除当前驱动包
        public void deleteDriverCliecked(){
            if(driver_choicebox.getItems().size()<=1){
                AlterUtil.CustomAlert("错误","当前仅有一个驱动，不可删除！");
            }else{
                String currItem=(String)driver_choicebox.getValue();
                File file = new File("extlib/"+dbtype_choicebox.getValue()+"/"+currItem);
                if(SqliteDBaccessUtil.checkDriverInUse(currItem)){
                    //如果正在使用，提示是否确认要删除
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("删除驱动确认");
                    alert.setHeaderText("");
                    alert.setContentText("检查到部分连接需要使用此驱动，确认要删除该驱动？");
                    alert.setGraphic(null); //避免显示问号
                    //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    alert.getDialogPane().getScene().getStylesheets().add(getClass().getResource("/com/dbboys/css/app.css").toExternalForm());
                    Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alterstage.getIcons().add(new Image("file:images/logo.png"));

                    // 自定义按钮
                    ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
                    ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
                    Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
                    //button.setDisable(true);
                    //textField.requestFocus();
                    ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
                    if (result == buttonTypeOk) {
                        if(file.delete()) {
                            driver_choicebox.getItems().remove(currItem);
                            driver_choicebox.getSelectionModel().select(0);
                        }else{
                            AlterUtil.CustomAlert("错误","无法删除，该驱动文件已被打开，需重启软件后删除！");
                        }
                    }
                }else{
                    if(file.delete()) {
                        driver_choicebox.getItems().remove(currItem);
                        driver_choicebox.getSelectionModel().select(0);
                    }else{
                        AlterUtil.CustomAlert("错误","无法删除，该驱动文件已被打开，需重启软件后删除！");
                    }
                }

            }
        }


    //编辑当前驱动属性
    public void modifyDriverProps(){
        JSONArray jsonArray =new JSONArray(props);
        // 将JSONArray转换为ObservableList
        List<ObservableList<String>> lastdata=null;//根据确认或取消选择，赋值给lastdata
        List<ObservableList<String>> initdata = FXCollections.observableArrayList();//如果取消，返回最初list
        List<ObservableList<String>> datalist = FXCollections.observableArrayList();//如果确认，返回更新后的list

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(null);
            row.add(jsonObject.getString("propName"));
            row.add(jsonObject.getString("propValue"));
            initdata.add(row);
            datalist.add(row);
        }
        CustomResultsetTableView tableView = new CustomResultsetTableView();
        tableView.setEditable(true);
        tableView.setSortPolicy((param) -> false);//禁用排序

        TableColumn<ObservableList<String>, Object> nameColumn = new TableColumn<ObservableList<String>, Object>("属性名称");
        nameColumn.setCellFactory(col -> new CustomLostFocusCommitTableCell<ObservableList<String>, Object>());
        nameColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(1)));
        nameColumn.setReorderable(false); // 禁用拖动
        nameColumn.setEditable(false);
        nameColumn.setReorderable(false);
        nameColumn.setPrefWidth(220);
        TableColumn<ObservableList<String>, Object> valueColumn = new TableColumn<ObservableList<String>, Object>("属性值");
        valueColumn.setCellFactory(col -> new CustomLostFocusCommitTableCell<ObservableList<String>, Object>());
        valueColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue().get(2)));
        valueColumn.setReorderable(false); // 禁用拖动
        valueColumn.setEditable(true);
        valueColumn.setReorderable(false);
        valueColumn.setPrefWidth(120);

        valueColumn.setOnEditCommit(event -> {
            // 获取当前行的模型数据（ObservableList<String>）
            ObservableList<String> rowData = event.getRowValue();

            // 获取编辑后的新值
            Object newValue = event.getNewValue();
            // 更新ObservableList中索引1的位置（与cellValueFactory对应）
            if (rowData.size() > 2) {  // 确保索引有效
                // 转换为字符串（根据实际需求调整类型）
                rowData.set(2,  newValue.toString());
                tableView.refresh();
            }
        });
        tableView.getColumns().addAll(nameColumn, valueColumn);
        tableView.getItems().clear();
        tableView.getItems().addAll(datalist);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("编辑连接属性");
        alert.setHeaderText("");
        alert.setGraphic(null); //避免显示问号
        //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getScene().getStylesheets().add(getClass().getResource("/com/dbboys/css/app.css").toExternalForm());
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image("file:images/logo.png"));
        HBox hbox = new HBox();
        hbox.setId("modifyProps");
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
            lastdata = datalist;
        }else {
            lastdata=initdata;
        }
        jsonArray.clear();
        for (ObservableList<String> row :lastdata) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("propName", row.get(1));
            jsonObject.put("propValue", row.get(2));
            jsonArray.put(jsonObject);
        }
        props=jsonArray.toString();
    }


    //编辑组信息
    public void modifyGroupProps(){
    
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("编辑组信息");
        alert.setHeaderText("");
        alert.setGraphic(null); //避免显示问号
        //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getScene().getStylesheets().add(getClass().getResource("/com/dbboys/css/app.css").toExternalForm());
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image("file:images/logo.png"));
        HBox hbox = new HBox();
        hbox.setId("modifyProps");
        hbox.setAlignment(Pos.CENTER_LEFT);
        Path file = Paths.get("extlib/GBase 8S/sqlhosts");
        String content="";
        String defaultContent="db_group\tgroup\t-\t-\ngbase01\tonsoctcp\t192.168.1.1\t9088\tg=db_group\ngbase02\tonsoctcp\t192.168.1.2\t9088\tg=db_group";
        try {
            // 1. 判断文件是否存在
            if (Files.exists(file)) {
                content = Files.readString(file, StandardCharsets.UTF_8);
            } else {
                Files.writeString(file, defaultContent, StandardCharsets.UTF_8);
                content = defaultContent; // 写入后返回默认内容
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        CustomInfoCodeArea codeArea = new CustomInfoCodeArea();
        VirtualizedScrollPane virtualizedScrollPane=new VirtualizedScrollPane<>(codeArea);
        hbox.getChildren().add(virtualizedScrollPane);
        codeArea.setPrefWidth(400);
        codeArea.setPrefHeight(100);
        codeArea.setEditable(true);
        codeArea.replaceText(content);
        alert.getDialogPane().setContent(hbox);

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
        ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
        if (result == buttonTypeOk) {
            try {
                Files.writeString(file, codeArea.getText(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    public void switchGroupOrIP(){
        groupHbox.setVisible(!groupHbox.isVisible());
    }


    public void commitConnecting(Connect connect,boolean isCommit){
        try{
            connecting_hbox.setVisible(true);
            Task task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Long start = System.currentTimeMillis();
                    Long end = System.currentTimeMillis();

                    try {
                        connect.setConn(dbConnectionFactory.getConnection(connect));
                       // connectionService.getConnection(connect);

                        end=System.currentTimeMillis();
                        if(isCancelled()) return  null;

                    } catch (SQLException e1) {
                        log.error(e1.getMessage(),e1);
                        if(isCancelled())return  null;

                        end=System.currentTimeMillis();
                        Long finalEnd = end;
                        Platform.runLater(()-> {
                            AlterUtil.CustomAlert("错误", "[" + e1.getErrorCode() + "]" + e1.getMessage()+"用时【"+(finalEnd -start)+"ms】。");
                            connecting_hbox.setVisible(false);
                        });
                        return null;
                    }catch (Exception e1){
                        log.error(e1.getMessage(),e1);
                        return null;
                    }finally {
                        if(connect.getConn()!=null)
                            connect.getConn().close();
                    }

                    Long finalEnd = end;
                    if(isCancelled())
                        return  null;

                    //确认提交连接
                    if(isCommit){

                        String result=SqliteDBaccessUtil.createConnectLeaf(connect);
                        if(result.equals("")){
                            Platform.runLater(()-> {
                               connecting_hbox.setVisible(false);

                                cancelButton.fire();
                            });

                            TreeItem<TreeData> treeItem=MetadataTreeviewUtil.createTreeItem(connect);


                            //判断是否为编辑连接，符合条件表示为编辑连接
                            if (treedataPram != null && treedataPram instanceof Connect &&!isCopy) {
                                Platform.runLater(()->{
                                    TreeItem<TreeData> currItem = new TreeItem<>();
                                    currItem = Main.mainController.databasemeta_treeview.getSelectionModel().getSelectedItem();
                                    currItem.getParent().getChildren().remove(currItem);
                                    MetadataTreeviewUtil.createConnectLeaf(Main.mainController.databasemeta_treeview, treeItem);
                                    SqliteDBaccessUtil.deleteConnectLeaf((Connect) treedataPram);//删除数据库中老节点

                                    //如果当前编辑的连接为空或已断开，不处理
                                    try {
                                        if (((Connect)currItem.getValue()).getConn() == null||((Connect)currItem.getValue()).getConn().isClosed()) {
                                        } else {//如果当前编辑的连接已连接，关闭原老节点连接后展开触发连接数据库
                                            ((Connect)currItem.getValue()).getConn().close();

                                        }
                                    } catch (SQLException e) {
                                        log.error(e.getMessage(),e);
                                    }
                                    Platform.runLater(()-> {
                                        //这里会自动连接数据库
                                        treeItem.setExpanded(true);
                                        treeItem.setExpanded(false);
                                    });
                                });

                            } else { //否则为新建连接或复制连接
                                Platform.runLater(()-> {
                                    MetadataTreeviewUtil.createConnectLeaf(Main.mainController.databasemeta_treeview, treeItem);
                                    //展开触发展开事件，展开事件会连接数据库，改变连接状态
                                    treeItem.setExpanded(true);
                                    //数据库连接后，默认折叠
                                    treeItem.setExpanded(false);

                                });
                            }
                            Platform.runLater(()-> {
                                TabpaneUtil.isRefreshConnectList();
                            });





                        }else{
                            AlterUtil.CustomAlert("错误",result);
                        }

                    //如果不是提交连接，那就是点击了测试连接,需要
                    }else{
                        Platform.runLater(()->{
                            AlterUtil.CustomAlert("通知","测试连接成功，用时【"+(finalEnd -start)+"ms】。");
                            connecting_hbox.setVisible(false);
                        });
                        try {
                            if(connect.getConn()!=null)
                                connect.getConn().close();
                        } catch (SQLException e) {
                            log.error(e.getMessage(),e);
                            throw new RuntimeException(e);
                        }
                    }

                    return null;
                }
            };

            cancelButton.setOnAction(event -> {
                task.cancel();
            });
            connecting_stop_button.setOnAction(event1 -> {
                //MetadataTreeviewUtil.testConnThread.interrupt();
                task.cancel();
                connecting_hbox.setVisible(false);
            });
            dialog.setOnCloseRequest(event -> {
                task.cancel();
            });
            MetadataTreeviewUtil.testConnThread= new Thread(task);
            MetadataTreeviewUtil.testConnThread.setDaemon(true);
            MetadataTreeviewUtil.testConnThread.start();

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }



    }
}
