package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.util.AlterUtil;
import com.dbboys.util.GlobalErrorHandlerUtil;
import com.dbboys.util.NotificationUtil;
import com.dbboys.util.PopupWindowUtil;
import com.dbboys.util.TabpaneUtil;
import com.dbboys.util.MetadataTreeviewUtil;
import javafx.application.Platform;
import com.dbboys.vo.*;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.poi.ss.formula.functions.T;

public class CustomTreeCell extends TreeCell<TreeData> {
    private Integer iconSize = 11;
    private Label nameLabel = new Label();
    private ImageView loadingIcon = new ImageView(new Image("file:images/loading.gif"));
    private ImageView runningIcon = new ImageView(new Image("file:images/loading.gif"));
    public SVGPath nodeIcon = new SVGPath();
    private SVGPath lockIcon = new SVGPath();
    private SVGPath warnIcon = new SVGPath();
    private SVGPath defaultDbIcon = new SVGPath();
    private Group nodeIconGroup=new Group(nodeIcon);
    private Group defaultDbIconGroup=new Group(defaultDbIcon);
    private Group warnIconGroup=new Group(warnIcon);
    private Group lockIconGroup=new Group(lockIcon);

    public StackPane nodeIconStackpane = new StackPane(nodeIconGroup);
    private HBox graphicHbox = new HBox();
    private Region spacer = new Region();
    private Label descripLabel = new Label();
    private Tooltip tooltip = new Tooltip();
    private String ddl_popupstage_ddlsql;
    private Task<Object> DDLTask;

    public CustomTreeCell() {
        loadingIcon.setFitWidth(iconSize);
        loadingIcon.setFitHeight(iconSize);
        runningIcon.setFitWidth(iconSize);
        runningIcon.setFitHeight(iconSize);
        nodeIcon.setFill(Color.valueOf("#074675"));
        lockIcon.setScaleX(0.45);
        lockIcon.setScaleY(0.45);
        lockIcon.setContent("M17 9.0078 L17 7.0078 Q17 5.9609 16.625 5.0391 Q16.2188 4.1328 15.5469 3.4609 Q14.8906 2.7734 13.9688 2.3984 Q13.0625 1.9922 12 1.9922 Q10.9531 1.9922 10.0312 2.3984 Q9.125 2.7734 8.4531 3.4609 Q7.7812 4.1328 7.3906 5.0391 Q7.0156 5.9609 7.0156 7.0078 L7.0156 9.0078 Q5.7188 9.0078 4.8594 9.8828 Q4.0156 10.7422 4.0156 12.0078 L4.0156 19.0078 Q4.0156 20.2734 4.8594 21.1484 Q5.7188 22.0078 7.0156 22.0078 L17 22.0078 Q18.2812 22.0078 19.1406 21.1484 Q20 20.2734 20 19.0078 L20 12.0078 Q20 10.7422 19.1406 9.8828 Q18.2812 9.0078 17 9.0078 L17 9.0078 ZM9 7.0078 Q9 5.7266 9.8594 4.8672 Q10.7344 4.0078 12 4.0078 Q13.2656 4.0078 14.125 4.8672 Q15 5.7266 15 7.0078 L15 9.0078 L9 9.0078 L9 7.0078 L9 7.0078 ZM13.1094 15.4922 Q13.1094 15.4922 13.0625 15.5547 Q13.0156 15.6172 13.0156 15.6172 L13.0156 16.9922 Q13.0156 17.4609 12.7344 17.7422 Q12.4531 18.0078 12 18.0078 Q11.5625 18.0078 11.2812 17.7422 Q11 17.4609 11 16.9922 L11 15.6172 Q10.5469 15.1484 10.5 14.5547 Q10.4531 13.9453 10.9062 13.5078 Q11.3438 13.0547 11.9375 13.0078 Q12.5469 12.9609 13.0156 13.4141 Q13.4531 13.7891 13.5 14.4297 Q13.5469 15.0547 13.1094 15.4922 L13.1094 15.4922 Z");

        graphicHbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        tooltip.setShowDelay(Duration.millis(100));
        descripLabel.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill:#aaa;-fx-font-family:'Courier New';");

        warnIcon.setContent("M13.7188 19.2734 L13.7188 16.7266 Q13.7188 16.5391 13.5781 16.4141 Q13.4531 16.2891 13.2812 16.2891 L10.7188 16.2891 Q10.5469 16.2891 10.4062 16.4141 Q10.2812 16.5391 10.2812 16.7266 L10.2812 19.2734 Q10.2812 19.4609 10.4062 19.5859 Q10.5469 19.7109 10.7188 19.7109 L13.2812 19.7109 Q13.4531 19.7109 13.5781 19.5859 Q13.7188 19.4609 13.7188 19.2734 ZM13.6875 14.2578 L13.9219 8.1172 Q13.9219 7.9609 13.7969 7.8672 Q13.625 7.7109 13.4688 7.7109 L10.5312 7.7109 Q10.375 7.7109 10.2031 7.8672 Q10.0781 7.9609 10.0781 8.1484 L10.2969 14.2578 Q10.2969 14.3984 10.4219 14.4922 Q10.5625 14.5703 10.75 14.5703 L13.2344 14.5703 Q13.4219 14.5703 13.5469 14.4922 Q13.6719 14.3984 13.6875 14.2578 ZM13.5 1.7578 L23.7812 20.6172 Q24.25 21.4609 23.7656 22.3047 Q23.5312 22.6953 23.125 22.9141 Q22.7344 23.1484 22.2812 23.1484 L1.7188 23.1484 Q1.2656 23.1484 0.8594 22.9141 Q0.4688 22.6953 0.2344 22.3047 Q-0.25 21.4609 0.2188 20.6172 L10.5 1.7578 Q10.7344 1.3359 11.125 1.1016 Q11.5312 0.8516 12 0.8516 Q12.4688 0.8516 12.8594 1.1016 Q13.2656 1.3359 13.5 1.7578 Z");
        warnIcon.setScaleX(0.4);
        warnIcon.setScaleY(0.4);
        warnIcon.setFill(Color.valueOf("#9f453c"));

        defaultDbIcon.setScaleX(0.5);
        defaultDbIcon.setScaleY(0.5);
        defaultDbIcon.setContent("M13.5312 3.0938 Q14.2031 2.5156 15.0625 2.5156 Q15.9375 2.5156 16.6094 3.0938 L21.5 8.3281 Q22.2188 9.0469 22.0938 10.0781 Q21.9844 11.1094 21.125 11.7344 L16.0781 14.6094 Q15.8906 14.7031 15.7969 14.8594 L13.9688 19.4688 Q13.8281 19.8438 13.4688 19.9375 Q13.1094 20.0312 12.8125 19.7969 L9.5 16.4844 L4.5625 21.4844 L3.4531 21.4219 L3.4531 20.4219 L8.4531 15.4219 L5.2344 12.2188 Q4.9375 11.9688 4.9844 11.6094 Q5.0469 11.25 5.375 11.0625 L9.9844 9.1875 Q10.1719 9.1406 10.2656 8.9531 L13.1094 3.7188 Q13.2969 3.375 13.5312 3.0938 Z");
        defaultDbIcon.setFill(Color.valueOf("#074675"));


    }

    @Override
    protected void updateItem(TreeData item, boolean empty) {
        super.updateItem(item, empty);
        setOnMouseClicked(null);
        setTooltip(null); //清理元素提示，避免出现错误的提示
        setGraphic(null);
        textProperty().unbind();
        setText(null);
        setStyle(null);
        nameLabel.setStyle("");
        nodeIcon.setFill(Color.valueOf("#074675"));
        lockIcon.setFill(Color.valueOf("#9f453c"));
        warnIcon.visibleProperty().unbind();
        warnIcon.setVisible(false);
        nodeIconGroup.visibleProperty().unbind();
        nodeIconGroup.setVisible(true);
        nodeIconStackpane.getChildren().remove(runningIcon);
        graphicHbox.getChildren().clear();
        if (item == null || empty) {
            return;
        } else {
            if(item instanceof Loading){
                setGraphic(loadingIcon);
                textProperty().unbind();
                setText("Loading");
            }else if(item instanceof Connecting){
                setGraphic(loadingIcon);
                textProperty().unbind();
                setText("Connecting");
            }
            else if(item instanceof ConnectFolder){
                if(getTreeItem().isExpanded()){
                    nodeIcon.setContent("M2.1562 19.5547 Q2.2969 19.7578 2.5156 19.8828 Q2.75 19.9922 3 19.9922 L18 19.9922 Q18.3125 19.9922 18.5469 19.8359 Q18.7969 19.6641 18.9219 19.3828 L21.9219 12.3984 Q22.0156 12.1641 22 11.9297 Q21.9844 11.6797 21.8438 11.4609 Q21.7031 11.2578 21.4844 11.1328 Q21.2656 10.9922 21 10.9922 L20 10.9922 L20 7.9922 Q20 7.1797 19.4062 6.6016 Q18.8281 6.0078 18 6.0078 L11.3438 6.0078 L8.7969 4.0078 L4.0156 4.0078 Q3.1719 4.0078 2.5781 4.6016 Q2 5.1797 2 6.0078 L2 19.0078 L2.0156 19.0078 Q2.0156 19.1484 2.0469 19.2891 Q2.0938 19.4297 2.1562 19.5547 L2.1562 19.5547 ZM18 7.9922 L18 10.9922 L6 10.9922 Q5.7031 10.9922 5.4531 11.1641 Q5.2031 11.3203 5.0938 11.6016 L4.0156 14.1328 L4.0156 7.9922 L18 7.9922 L18 7.9922 Z");
                    nodeIcon.setScaleX(0.66);
                    nodeIcon.setScaleY(0.66);
                    nodeIcon.setFill(Color.valueOf("#074675"));
                }else{
                    nodeIcon.setContent("M22.0781 5.7656 Q21.9844 5.3594 21.5625 5.0781 Q21.1406 4.7969 20.625 4.7969 L12.4688 4.7969 Q11.9531 4.7969 11.3438 4.5625 Q10.7344 4.3125 10.3906 3.9531 L9.6875 3.2344 Q9.3281 2.9062 8.7188 2.6562 Q8.1094 2.3906 7.625 2.3906 L3.7031 2.3906 Q3.2188 2.3906 2.8125 2.75 Q2.4219 3.0938 2.375 3.5781 L2.0156 7.1875 L22.3438 7.1875 L22.0781 5.7656 L22.0781 5.7656 ZM23.2969 8.3906 L0.7031 8.3906 Q0.4062 8.3906 0.1875 8.625 Q-0.0312 8.8594 0.0312 9.1562 L1.125 20.7656 Q1.1562 21.1094 1.4219 21.3594 Q1.6875 21.6094 2.0469 21.6094 L21.9688 21.6094 Q22.3125 21.6094 22.5781 21.3594 Q22.8594 21.1094 22.875 20.7656 L23.9844 9.1562 Q24.0312 8.8594 23.8125 8.625 Q23.6094 8.3906 23.2969 8.3906 L23.2969 8.3906 Z");
                    nodeIcon.setScaleX(0.56);
                    nodeIcon.setScaleY(0.56);
                    nodeIcon.setFill(Color.valueOf("#074675"));
                }
                textProperty().unbind();
                textProperty().bind(item.nameProperty());
                setGraphic(nodeIconStackpane);
            }
            else if(item instanceof Connect){
                Connect connect =(Connect)item;
                nodeIcon.setContent("M194.66509,348.01735h-.00287a5.08422,5.08422,0,0,0-5.06208-5.01688H168.04l.02117,8.74355,17.86467.0072v5.689H159.89729c-.01728,0-.03124.0036-.05527.0036a3.14023,3.14023,0,0,1-3.13816-3.13668v-.00713h-.014V340.521h.014v-.009a3.14519,3.14519,0,0,1,3.13816-3.13844c.038,0,.07967.01323.1184.01323h29.59451l5.089-8.79625s-36.00025-.00433-36.08815,0a11.3304,11.3304,0,0,0-10.73466,11.31421c0,.21932.00647.42674.02227.63812V354.1787c-.00581.206-.02227.407-.02227.62085a11.34988,11.34988,0,0,0,11.353,11.348c.23616,0,1.21231-.0086,1.5066-.0086l28.93911.0036v-.00433a5.08677,5.08677,0,0,0,5.04239-5.07539V361.05h.00287Z");
                nodeIcon.setScaleX(0.22);
                nodeIcon.setScaleY(0.22);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.setStyle("-fx-text-fill:#074675;");
                String status="已连接";

                try {
                    if (connect.getConn() == null|| connect.getConn().isClosed()) {
                        nameLabel.setStyle("-fx-text-fill:#888;");
                        nodeIcon.setFill(Color.valueOf("#888"));
                        lockIcon.setFill(Color.valueOf("#888"));
                        status="未连接";
                    } else {
                        nameLabel.setStyle("-fx-text-fill:#074675;");
                        nodeIcon.setFill(Color.valueOf("#074675"));
                        lockIcon.setFill(Color.valueOf("#9f453c"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(getItem().nameProperty());
                //如果是只读，显示锁定图标
                graphicHbox.getChildren().clear();
                if(connect.getReadonly()) {
                    graphicHbox.getChildren().addAll(nodeIconStackpane, nameLabel, spacer, lockIconGroup);
                }else{
                    graphicHbox.getChildren().addAll(nodeIconStackpane, nameLabel);
                }
                setGraphic(graphicHbox);
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat("DB TYPE: " , connect.dbtypeProperty() , "\nIP ADDR: " , connect.ipProperty(), "\nPORT   : ", connect.portProperty(), "\nUSER   : " , connect.usernameProperty(),"\nSTATUS : ",status)
                );
                setTooltip(tooltip);
            }
            else if(item instanceof DatabaseFolder){
                nodeIcon.setContent("M64.789,76.889c-15.176,0-27.67-4.286-28.764-9.716l-0.02,0.774v13.446c0,5.8,12.889,10.496,28.783,10.496 s28.781-4.696,28.781-10.496V67.948c0-0.261-0.056-0.521-0.107-0.774C92.367,72.602,79.965,76.889,64.789,76.889z M64.789,56.629c-15.176,0-27.68-4.284-28.773-9.719l-0.01,0.777v13.444c0,5.799,12.888,10.495,28.783,10.495 S93.57,66.931,93.57,61.132V47.688c0-0.263-0.055-0.518-0.107-0.777C92.367,52.345,79.965,56.629,64.789,56.629z M64.789,22.011c-15.895,0-28.783,4.697-28.783,10.493v8.612c0,5.798,12.888,10.494,28.783,10.494 S93.57,46.915,93.57,41.116v-8.612C93.57,26.708,80.684,22.011,64.789,22.011z M0.01,46.842L0.004,47.62v13.447c0,5.795,12.885,10.492,28.781,10.492c1.074,0,2.135-0.023,3.18-0.064V60.397v-3.904 c-1.045,0.045-2.105,0.066-3.18,0.066C13.607,56.559,1.103,52.273,0.01,46.842z M0,26.582l0.004,0.777v13.446c0,5.798,12.885,10.495,28.781,10.495c1.074,0,2.135-0.022,3.18-0.065V36.233 c-1.045,0.042-2.105,0.066-3.18,0.066C13.607,36.3,1.094,32.014,0,26.582z M57.566,12.176c0-5.799-12.886-10.496-28.781-10.496c-15.896,0-28.781,4.697-28.781,10.496v8.611 c0,5.797,12.885,10.494,28.781,10.494c1.086,0,2.156-0.023,3.211-0.067c0.559-7.487,12.34-11.897,25.57-12.98V12.176z");
                nodeIcon.setScaleX(0.13);
                nodeIcon.setScaleY(0.13);
                nodeIcon.setFill(Color.valueOf("#074675"));
                textProperty().unbind();
                textProperty().bind(getItem().nameProperty());
                setGraphic(nodeIconStackpane);
            }
            else if(item instanceof Database){
                Database database =(Database)item;
                nodeIcon.setContent("M21 3.4219 L21 5.5781 Q21 6.9844 17.9219 8 Q14.8594 9 10.5 9 Q6.1406 9 3.0625 8 Q0 6.9844 0 5.5781 L0 3.4219 Q0 2.0156 3.0625 1.0156 Q6.1406 0 10.5 0 Q14.8594 0 17.9219 1.0156 Q21 2.0156 21 3.4219 ZM21 8.25 L21 13.0781 Q21 14.4844 17.9219 15.5 Q14.8594 16.5 10.5 16.5 Q6.1406 16.5 3.0625 15.5 Q0 14.4844 0 13.0781 L0 8.25 Q3.3281 10.5469 10.5 10.5469 Q17.6719 10.5469 21 8.25 ZM21 15.75 L21 20.5781 Q21 21.9844 17.9219 22.9844 Q14.8594 24 10.5 24 Q6.1406 24 3.0625 22.9844 Q0 21.9844 0 20.5781 L0 15.75 Q3.3281 18.0469 10.5 18.0469 Q17.6719 18.0469 21 15.75 Z");
                nodeIcon.setScaleX(0.4);
                nodeIcon.setScaleY(0.4);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                String Sys="";
                if(database.getName().equals("sysmaster")|| database.getName().equals("sysuser")|| database.getName().equals("sysadmin")|| database.getName().equals("sysutils")|| database.getName().equals("sysha")|| database.getName().equals("syscdr")|| database.getName().equals("syscdcv1")|| database.getName().equals("gbasedbt")|| database.getName().equals("sys")){
                    Sys="(SYS)";
                }
                if(database.getDbLog().equals("nolog")){
                    warnIcon.setVisible(true);
                }else{
                    warnIcon.setVisible(false);
                }
                descripLabel.textProperty().unbind();
                descripLabel.setText(Sys+ database.getDbSize());
                //根据主连接上的库名标记默认库标记
                graphicHbox.getChildren().clear();
                if(((Connect)getTreeItem().getParent().getParent().getValue()).getDatabase().equals(database.getName())){
                    graphicHbox.getChildren().addAll(nodeIconStackpane,nameLabel, spacer,defaultDbIconGroup,warnIconGroup,descripLabel);
                }else{
                    graphicHbox.getChildren().addAll(nodeIconStackpane,nameLabel, spacer,warnIconGroup,descripLabel);
                }
                setGraphic(graphicHbox);
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                        "DATABASE: ", database.nameProperty(),"\n" ,
                        "OWNER   : ", database.dbOwnerProperty(),"\n" ,
                        "LOG TYPE: ", database.dbLogProperty(),"\n" ,
                        "DBSPACE : ", database.dbSpaceProperty(),"\n" ,
                        "DBSIZE  : ", database.dbSizeProperty(),"\n" ,
                        "CREATED : ", database.dbCreatedProperty(),"\n" ,
                        "CHARSET : ", database.dbLocaleProperty(),"\n" ,
                        "USEGLU  : ", database.dbUseGLUProperty())
                );
                setTooltip(tooltip);
            }
            else if(item instanceof ObjectFolder){
                nodeIcon.setContent("M0.75 9.75 L0.75 5.25 Q0.75 4.9219 0.8594 4.6719 Q0.9844 4.4062 1.1875 4.2031 Q1.4062 3.9844 1.6562 3.875 Q1.9219 3.75 2.25 3.75 L10.5 3.75 L12 6.75 L21 6.75 Q21.3281 6.75 21.5781 6.875 Q21.8438 6.9844 22.0469 7.2031 Q22.2656 7.4062 22.375 7.6719 Q22.5 7.9219 22.5 8.25 L22.5 9.75 L0.75 9.75 L0.75 9.75 ZM0.75 10.5 L0.75 18.75 Q0.75 19.0781 0.8594 19.3438 Q0.9844 19.5938 1.1875 19.8125 Q1.4062 20.0156 1.6562 20.1406 Q1.9219 20.25 2.25 20.25 L21 20.25 Q21.3281 20.25 21.5781 20.1406 Q21.8438 20.0156 22.0469 19.8125 Q22.2656 19.5938 22.375 19.3438 Q22.5 19.0781 22.5 18.75 L22.5 10.5 L0.75 10.5 L0.75 10.5 Z");
                nodeIcon.setScaleX(0.5);
                nodeIcon.setScaleY(0.5);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                descripLabel.textProperty().unbind();
                descripLabel.textProperty().bind(((ObjectFolder) item).descriptionProperty());
                graphicHbox.getChildren().clear();
                graphicHbox.getChildren().addAll(nodeIconStackpane,nameLabel, spacer,descripLabel);
                setGraphic(graphicHbox);
            }
            else if(item instanceof SysTable){
                SysTable sysTable=(SysTable)item;
                nodeIcon.setContent("M0 2.9531 Q0 1.7656 0.875 0.8906 Q1.7656 0 3.0469 0 L21.0469 0 Q22.2344 0 23.1094 0.8906 Q24 1.7656 24 3.0469 L24 20.9531 Q24 22.2344 23.1094 23.125 Q22.2344 24 20.9531 24 L3.0469 24 Q1.7656 24 0.875 23.125 Q0 22.2344 0 21.0469 L0 2.9531 ZM22.4844 6 L16.4844 6 L16.4844 10.4844 L22.4844 10.4844 L22.4844 6 ZM22.4844 12 L16.4844 12 L16.4844 16.4844 L22.4844 16.4844 L22.4844 12 ZM22.4844 18 L16.4844 18 L16.4844 22.4844 L21.0469 22.4844 Q21.5938 22.4844 22.0312 22.0469 Q22.4844 21.5938 22.4844 20.9531 L22.4844 18 ZM15.0469 22.4844 L15.0469 18 L9.0469 18 L9.0469 22.4844 L15.0469 22.4844 ZM7.5156 22.4844 L7.5156 18 L1.5156 18 L1.5156 20.9531 Q1.5156 21.5938 1.9531 22.0469 Q2.4062 22.4844 3.0469 22.4844 L7.5156 22.4844 ZM1.5156 16.4844 L7.5156 16.4844 L7.5156 12 L1.5156 12 L1.5156 16.4844 ZM1.5156 10.4844 L7.5156 10.4844 L7.5156 6 L1.5156 6 L1.5156 10.4844 ZM9.0469 6 L9.0469 10.4844 L15.0469 10.4844 L15.0469 6 L9.0469 6 ZM15.0469 12 L9.0469 12 L9.0469 16.4844 L15.0469 16.4844 L15.0469 12 Z");
                nodeIcon.setScaleX(0.38);
                nodeIcon.setScaleY(0.38);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                if(sysTable.getTableType().equals("raw")||sysTable.getTableType().equals("external")) {
                    warnIcon.setVisible(true);
                }
                descripLabel.textProperty().unbind();
                if(sysTable.getTableType().equals("view")){
                    nodeIcon.setContent("M18.9844 3 Q19.8281 3 20.4062 3.5938 Q21 4.1719 21 5.0156 L21 18.9844 Q21 19.8281 20.4062 20.4219 Q19.8281 21 18.9844 21 L5.0156 21 Q4.1719 21 3.5781 20.4219 Q3 19.8281 3 18.9844 L3 5.0156 Q3 4.1719 3.5781 3.5938 Q4.1719 3 5.0156 3 L18.9844 3 ZM18.9844 18.9844 L18.9844 6.9844 L5.0156 6.9844 L5.0156 18.9844 L18.9844 18.9844 ZM12 10.5 Q10.6406 10.5 9.4688 11.1875 Q8.2969 11.8594 7.6406 12.9844 Q8.2969 14.1562 9.4688 14.8438 Q10.6406 15.5156 12 15.5156 Q13.3594 15.5156 14.5312 14.8438 Q15.7031 14.1562 16.3594 12.9844 Q15.7031 11.8594 14.5312 11.1875 Q13.3594 10.5 12 10.5 ZM12 9 Q14.0156 9 15.6562 10.1094 Q17.2969 11.2031 18 12.9844 Q17.2969 14.7656 15.6562 15.8906 Q14.0156 17.0156 12 17.0156 Q9.9844 17.0156 8.3438 15.8906 Q6.7031 14.7656 6 12.9844 Q6.7031 11.2031 8.3438 10.1094 Q9.9844 9 12 9 ZM12 14.4844 Q11.3906 14.4844 10.9375 14.0625 Q10.5 13.6406 10.5 13.0156 Q10.5 12.375 10.9375 11.9375 Q11.3906 11.4844 12 11.4844 Q12.6094 11.4844 13.0469 11.9375 Q13.5 12.375 13.5 13.0156 Q13.5 13.6406 13.0469 14.0625 Q12.6094 14.4844 12 14.4844 Z");
                    nodeIcon.setScaleX(0.55);
                    nodeIcon.setScaleY(0.55);
                    descripLabel.setText("VIEW");
                }else{
                    descripLabel.setText(sysTable.getNrows()+"行/"+sysTable.getTotalsize());
                }
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE  : " , sysTable.getDbname() , "\n" ,
                                "TABLENAME : " , sysTable.nameProperty() , "\n" ,
                                "OWNER     : " , sysTable.ownerProperty() , "\n" ,
                                "CREATED   : " , sysTable.createTimeProperty() , "\n" ,
                                "TYPE      : " , sysTable.tableTypeProperty() , "\n" ,
                                "LOCKMODE  : " , sysTable.lockModeProperty() , "\n" ,
                                "FRAGMENTED: " , sysTable.isfragmentProperty() , "\n" ,
                                "EXTENTS   : " , sysTable.extentsProperty() , "\n" ,
                                "NROWS     : " , sysTable.nrowsProperty() , "\n" ,
                                "PAGESIZE  : " , sysTable.pagesizeProperty() , "\n" ,
                                "TOTALPAGES: " , sysTable.nptotalProperty() , "\n" ,
                                "TOTALSIZE : " , sysTable.totalsizeProperty() , "\n" ,
                                "DATAPAGES : " , sysTable.npdataProperty() , "\n" ,
                                "DATASIZE  : " , sysTable.usedsizeProperty() , "\n")
                );
                setTooltip(tooltip);

            }
            else if(item instanceof Table){
                Table table=(Table)item;
                nodeIcon.setContent("M0 2.9531 Q0 1.7656 0.875 0.8906 Q1.7656 0 3.0469 0 L21.0469 0 Q22.2344 0 23.1094 0.8906 Q24 1.7656 24 3.0469 L24 20.9531 Q24 22.2344 23.1094 23.125 Q22.2344 24 20.9531 24 L3.0469 24 Q1.7656 24 0.875 23.125 Q0 22.2344 0 21.0469 L0 2.9531 ZM22.4844 6 L16.4844 6 L16.4844 10.4844 L22.4844 10.4844 L22.4844 6 ZM22.4844 12 L16.4844 12 L16.4844 16.4844 L22.4844 16.4844 L22.4844 12 ZM22.4844 18 L16.4844 18 L16.4844 22.4844 L21.0469 22.4844 Q21.5938 22.4844 22.0312 22.0469 Q22.4844 21.5938 22.4844 20.9531 L22.4844 18 ZM15.0469 22.4844 L15.0469 18 L9.0469 18 L9.0469 22.4844 L15.0469 22.4844 ZM7.5156 22.4844 L7.5156 18 L1.5156 18 L1.5156 20.9531 Q1.5156 21.5938 1.9531 22.0469 Q2.4062 22.4844 3.0469 22.4844 L7.5156 22.4844 ZM1.5156 16.4844 L7.5156 16.4844 L7.5156 12 L1.5156 12 L1.5156 16.4844 ZM1.5156 10.4844 L7.5156 10.4844 L7.5156 6 L1.5156 6 L1.5156 10.4844 ZM9.0469 6 L9.0469 10.4844 L15.0469 10.4844 L15.0469 6 L9.0469 6 ZM15.0469 12 L9.0469 12 L9.0469 16.4844 L15.0469 16.4844 L15.0469 12 Z");
                nodeIcon.setScaleX(0.38);
                nodeIcon.setScaleY(0.38);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                warnIcon.visibleProperty().unbind();
                warnIcon.visibleProperty().bind(Bindings.createBooleanBinding(
                        () -> "raw".equals(table.getTableType()) || "external".equals(table.getTableType()),
                        table.tableTypeProperty() // 监听tableType属性的变化
                ));

                descripLabel.textProperty().unbind();
                descripLabel.setText(table.getNrows()+"行/"+table.getTotalsize());
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                        "DATABASE  : " , table.getDbname() , "\n" ,
                                "TABLENAME : " , table.nameProperty() , "\n" ,
                                "OWNER     : " , table.ownerProperty() , "\n" ,
                                "CREATED   : " , table.createTimeProperty() , "\n" ,
                                "TYPE      : " , table.tableTypeProperty() , "\n" ,
                                "LOCKMODE  : " , table.lockModeProperty() , "\n" ,
                                "FRAGMENTED: " , table.isfragmentProperty() , "\n" ,
                                "EXTENTS   : " , table.extentsProperty() , "\n" ,
                                "NROWS     : " , table.nrowsProperty() , "\n" ,
                                "PAGESIZE  : " , table.pagesizeProperty() , "\n" ,
                                "TOTALPAGES: " , table.nptotalProperty() , "\n" ,
                                "TOTALSIZE : " , table.totalsizeProperty() , "\n" ,
                                "DATAPAGES : " , table.npdataProperty() , "\n" ,
                                "DATASIZE  : " , table.usedsizeProperty() , "\n")
                );
                setTooltip(tooltip);

            }
            else if(item instanceof View){
                View view=(View)item;
                nodeIcon.setContent("M18.9844 3 Q19.8281 3 20.4062 3.5938 Q21 4.1719 21 5.0156 L21 18.9844 Q21 19.8281 20.4062 20.4219 Q19.8281 21 18.9844 21 L5.0156 21 Q4.1719 21 3.5781 20.4219 Q3 19.8281 3 18.9844 L3 5.0156 Q3 4.1719 3.5781 3.5938 Q4.1719 3 5.0156 3 L18.9844 3 ZM18.9844 18.9844 L18.9844 6.9844 L5.0156 6.9844 L5.0156 18.9844 L18.9844 18.9844 ZM12 10.5 Q10.6406 10.5 9.4688 11.1875 Q8.2969 11.8594 7.6406 12.9844 Q8.2969 14.1562 9.4688 14.8438 Q10.6406 15.5156 12 15.5156 Q13.3594 15.5156 14.5312 14.8438 Q15.7031 14.1562 16.3594 12.9844 Q15.7031 11.8594 14.5312 11.1875 Q13.3594 10.5 12 10.5 ZM12 9 Q14.0156 9 15.6562 10.1094 Q17.2969 11.2031 18 12.9844 Q17.2969 14.7656 15.6562 15.8906 Q14.0156 17.0156 12 17.0156 Q9.9844 17.0156 8.3438 15.8906 Q6.7031 14.7656 6 12.9844 Q6.7031 11.2031 8.3438 10.1094 Q9.9844 9 12 9 ZM12 14.4844 Q11.3906 14.4844 10.9375 14.0625 Q10.5 13.6406 10.5 13.0156 Q10.5 12.375 10.9375 11.9375 Q11.3906 11.4844 12 11.4844 Q12.6094 11.4844 13.0469 11.9375 Q13.5 12.375 13.5 13.0156 Q13.5 13.6406 13.0469 14.0625 Q12.6094 14.4844 12 14.4844 Z");
                nodeIcon.setScaleX(0.55);
                nodeIcon.setScaleY(0.55);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                descripLabel.textProperty().unbind();
                descripLabel.setText("VIEW");
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                        "DATABASE: " , view.dbnameProperty() , "\n" ,
                        "VIEWNAME: " , view.nameProperty() , "\n" ,
                        "OWNER   : " , view.ownerProperty() , "\n" ,
                        "CREATED : " , view.createTimeProperty() , "\n")
                );
                setTooltip(tooltip);
            }
            else if(item instanceof Index){
                Index index=(Index)item;
                nodeIcon.setContent("M18.875 15.719v4.438h2.375v5.656h-5.656v-5.656h2.344v-3.5h-6.844v3.5h2.375v5.656h-5.688v-5.656h2.375v-3.5h-6.844v3.5h2.344v5.656h-5.656v-5.656h2.375v-4.438h7.781v-3.438h-3.063v-5.656h7.063v5.656h-3.063v3.438h7.781z");
                nodeIcon.setScaleX(0.5);
                nodeIcon.setScaleY(0.5);
                nodeIcon.setFill(Color.valueOf("#074675"));
                warnIcon.visibleProperty().unbind();
                warnIcon.visibleProperty().bind(index.isdisabledProperty());
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(
                        Bindings.createStringBinding(() ->
                                (index.tabnameProperty().get()+"("+index.nameProperty().get()+")"),index.tabnameProperty(),index.nameProperty()
                        ));
                descripLabel.textProperty().unbind();
                descripLabel.textProperty().bind(
                        Bindings.when(index.isdisabledProperty())
                                .then("DISABLED")
                                .otherwise(index.totalsizeProperty())
                );
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE  : ", index.databaseProperty(), "\n",
                                "INDEXNAME : ", index.nameProperty(), "\n",
                                "TABLENAME : ", index.tabnameProperty(), "\n",
                                "COLS      : ", index.colsProperty(), "\n",
                                "IDXTYPE   : ", index.idxtypeProperty(), "\n",
                                "LEVELS    : ", index.levelsProperty(), "\n",
                                "UNIQVALES : ", index.uniqvaluesProperty(), "\n",
                                "PAGESIZE  : ", index.pagesizeProperty(), "\n",
                                "TOTALPAGES: ", index.totalpagesProperty(), "\n",
                                "TOTALSIZE : ", index.totalsizeProperty(), "\n",
                                "DISABLED  : ", index.isdisabledProperty()
                        )
                );
                setTooltip(tooltip);
            }
            else if(item instanceof Sequence){
                Sequence sequence=(Sequence)item;
                nodeIcon.setContent("M11.9062 6.5547 L10.4375 6.5547 Q10.3594 6.5547 10.3281 6.6016 Q10.2969 6.6484 10.2969 6.6953 L10.2969 14.6641 L10.1562 14.6641 L4.625 6.6172 Q4.625 6.5703 4.5781 6.5703 Q4.5469 6.5547 4.5312 6.5547 L2.9531 6.5547 Q2.9062 6.5547 2.8594 6.6016 Q2.8125 6.6484 2.8125 6.6953 L2.8125 17.5703 Q2.8125 17.6172 2.8594 17.6641 Q2.9062 17.7109 2.9531 17.7109 L4.4375 17.7109 Q4.4844 17.7109 4.5312 17.6641 Q4.5781 17.6172 4.5781 17.5703 L4.5781 9.4766 L4.6875 9.4766 L10.2969 17.6328 Q10.2969 17.6797 10.3281 17.6953 Q10.3594 17.7109 10.4062 17.7109 L11.9062 17.7109 Q11.9531 17.7109 12 17.6641 Q12.0469 17.6172 12.0469 17.5703 L12.0469 6.6953 Q12.0469 6.6484 12 6.6016 Q11.9531 6.5547 11.9062 6.5547 L11.9062 6.5547 ZM20.7656 16.2266 L13.6406 16.2266 Q13.5781 16.2266 13.5156 16.2891 Q13.4531 16.3516 13.4531 16.4141 L13.4531 17.5391 Q13.4531 17.6172 13.5156 17.6797 Q13.5781 17.7266 13.6406 17.7266 L20.7656 17.7266 Q20.8438 17.7266 20.8906 17.6797 Q20.9531 17.6172 20.9531 17.5391 L20.9531 16.4141 Q20.9531 16.3516 20.8906 16.2891 Q20.8438 16.2266 20.7656 16.2266 ZM17.2031 14.7578 Q18.125 14.7578 18.875 14.4609 Q19.625 14.1484 20.1562 13.5391 Q20.6719 12.9766 20.9219 12.2109 Q21.1875 11.4453 21.1875 10.5078 Q21.1875 9.6016 20.9219 8.8359 Q20.6719 8.0703 20.1562 7.4922 Q19.625 6.8828 18.875 6.5859 Q18.1406 6.2734 17.2031 6.2734 Q16.2656 6.2734 15.5156 6.5859 Q14.7656 6.8828 14.25 7.5078 Q13.7344 8.1016 13.4688 8.8516 Q13.2188 9.6016 13.2188 10.5078 Q13.2188 11.4453 13.4688 12.1953 Q13.7344 12.9453 14.25 13.5391 Q14.7969 14.1484 15.5312 14.4609 Q16.2656 14.7578 17.2031 14.7578 L17.2031 14.7578 ZM15.5156 8.5703 Q15.8281 8.2109 16.2344 8.0234 Q16.6406 7.8359 17.2031 7.8359 Q17.75 7.8359 18.1719 8.0234 Q18.5938 8.1953 18.875 8.5234 Q19.1562 8.8984 19.2969 9.3984 Q19.4531 9.8828 19.4531 10.5078 Q19.4531 11.1641 19.2969 11.6484 Q19.1562 12.1328 18.875 12.4766 Q18.5625 12.8359 18.1562 13.0078 Q17.75 13.1797 17.2031 13.1797 Q16.6406 13.1797 16.2344 12.9922 Q15.8281 12.8047 15.5156 12.4609 Q15.2344 12.1016 15.0938 11.6328 Q14.9531 11.1484 14.9531 10.5391 Q14.9531 9.8984 15.0938 9.4141 Q15.2344 8.9141 15.5156 8.5703 L15.5156 8.5703 Z");
                nodeIcon.setScaleX(0.58);
                nodeIcon.setScaleY(0.6);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                descripLabel.textProperty().unbind();
                descripLabel.setText("SEQ");
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                        "DATABASE : " , sequence.databaseProperty() , "\n" ,
                                "SEQNAME  : " , sequence.nameProperty() , "\n" ,
                                "MINVALUE : " , sequence.minValueProperty() , "\n" ,
                                "MAXVALUE : " , sequence.maxValueProperty() , "\n" ,
                                "INCVALUE : " , sequence.incValueProperty() , "\n" ,
                                "CACHE    : " , sequence.cacheProperty() , "\n" ,
                                "NEXTCACHE: " , sequence.nextvalProperty() , "\n" ,
                                "CREATED  : " , sequence.createdProperty())
                );
                setTooltip(tooltip);
            }
            else if(item instanceof Synonym){
                Synonym synonym=(Synonym)item;
                nodeIcon.setContent("M0 0 L6 0 L6 3 L3 3 L3 21 L21 21 L21 18 L24 18 L24 24 L0 24 L0 0 ZM12 0 L24 0 L24 12 L19.5 7.5 L12 15 L9 12 L16.5 4.5 L12 0 Z");
                nodeIcon.setScaleX(0.35);
                nodeIcon.setScaleY(0.35);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE: " , synonym.databaseProperty() , "\n" ,
                                        "SYNNAME : " , synonym.nameProperty() , "\n" ,
                                        "SYNTYPE : " , synonym.synonymTypeProperty() , "\n" ,
                                        "CREATED : " , synonym.createdProperty()
                ));
                setTooltip(tooltip);
                descripLabel.textProperty().unbind();
                descripLabel.textProperty().bind(synonym.synonymTypeProperty());
            }
            else if(item instanceof Trigger){
                Trigger trigger=(Trigger)item;
                nodeIcon.setContent("M8.3125 0.5495 Q8.3125 0.2995 8.5156 0.1432 Q8.7188 -0.013 8.9531 -0.013 L15.0469 -0.013 Q15.3594 -0.013 15.5938 0.3151 Q15.8438 0.6276 15.6875 0.9401 L13.0469 8.9401 L18.7188 8.9401 Q19.2031 8.9401 19.3906 9.3932 Q19.5938 9.8307 19.3594 10.2214 L8.875 23.6745 Q8.6406 23.9089 8.3906 23.987 Q8.1562 24.0651 7.9219 23.9089 Q7.6875 23.7526 7.5625 23.5182 Q7.4375 23.2682 7.5156 23.0339 L10.2344 14.2214 L5.2812 14.2214 Q4.875 14.2214 4.6406 13.9557 Q4.4062 13.6745 4.5625 13.2682 L8.3125 0.5495 Z");
                nodeIcon.setScaleX(0.5);
                nodeIcon.setScaleY(0.5);
                nodeIcon.setFill(Color.valueOf("#074675"));
                warnIcon.visibleProperty().unbind();
                warnIcon.visibleProperty().bind(trigger.isdisabledProperty());
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE: " , trigger.databaseProperty() , "\n" ,
                                        "TABNAME : " , trigger.tableNameProperty() , "\n" ,
                                        "TRINAME : " , trigger.nameProperty() , "\n" ,
                                        "TRITYPE : " , trigger.triggerTypeProperty() , "\n" ,
                                        "DISABLED: " , trigger.isdisabledProperty()
                        ));
                setTooltip(tooltip);
                descripLabel.textProperty().unbind();
                descripLabel.textProperty().bind(
                        Bindings.when(trigger.isdisabledProperty())
                                .then("DISABLED")
                                .otherwise("ENABLED")
                );
            }
            else if(item instanceof Function){
                Function function=(Function)item;
                nodeIcon.setContent("M19.7188 8.6719 Q19.7656 8.625 19.7656 8.5469 Q19.7656 8.4531 19.6875 8.4062 Q19.6719 8.3906 19.6406 8.375 Q19.625 8.3594 19.5781 8.3594 L17.8906 8.3594 Q17.8438 8.3594 17.7969 8.375 Q17.7656 8.3906 17.7188 8.4062 L14.8594 11.8281 Q14.7969 11.875 14.7188 11.8906 Q14.6562 11.9062 14.5781 11.8281 Q14.5781 11.8281 14.5469 11.8281 Q14.5312 11.8125 14.5312 11.7812 L13.0625 8.4531 Q13.0312 8.4062 12.9844 8.3906 Q12.9375 8.3594 12.875 8.3594 L8.9375 8.3594 L8.9531 8.25 L9.1406 7.2656 Q9.3125 6.2812 9.8125 5.8125 Q10.3125 5.3438 11.1406 5.3438 Q11.4688 5.3438 11.75 5.375 Q12.0469 5.4062 12.2812 5.4531 L12.6094 3.8906 Q12.2188 3.8125 11.9375 3.7812 Q11.6719 3.75 11.3281 3.75 Q9.5156 3.75 8.5312 4.5781 Q7.5469 5.3906 7.2188 7.1875 L6.9844 8.3594 L4.7188 8.3594 Q4.6406 8.3594 4.5781 8.4062 Q4.5312 8.4375 4.5312 8.5 L4.2656 9.7188 Q4.25 9.7969 4.2969 9.875 Q4.3438 9.9375 4.4062 9.9375 Q4.4062 9.9531 4.4219 9.9531 Q4.4375 9.9531 4.4531 9.9531 L6.6562 9.9531 L4.5781 20.0312 Q4.5469 20.1094 4.5938 20.1719 Q4.6406 20.2188 4.7188 20.25 Q4.7344 20.25 4.7344 20.25 Q4.7344 20.25 4.7656 20.25 L6.3125 20.25 Q6.375 20.25 6.4219 20.2188 Q6.4688 20.1719 6.5 20.1094 L8.6094 9.9531 L11.7656 9.9531 L13.3906 13.2188 Q13.4062 13.2656 13.3906 13.3281 Q13.3906 13.375 13.3438 13.4219 L9.125 18.1875 Q9.0781 18.2344 9.0781 18.3125 Q9.0781 18.3906 9.125 18.4375 Q9.1719 18.4688 9.1875 18.4844 Q9.2188 18.4844 9.2656 18.4844 L10.9531 18.4844 Q11 18.4844 11.0312 18.4844 Q11.0625 18.4688 11.1094 18.4219 L14 15 Q14.0469 14.9219 14.125 14.9219 Q14.2031 14.9062 14.25 14.9531 Q14.2812 14.9688 14.2812 14.9844 Q14.2969 15 14.3281 15.0156 L15.8438 18.375 Q15.875 18.4219 15.9219 18.4531 Q15.9688 18.4844 16.0312 18.4844 L17.5312 18.4844 Q17.625 18.4844 17.6719 18.4375 Q17.7188 18.375 17.7188 18.2969 Q17.7188 18.2812 17.7188 18.2656 Q17.7188 18.2344 17.7188 18.2031 L15.4688 13.5469 Q15.4531 13.5 15.4531 13.4375 Q15.4688 13.375 15.5156 13.3594 L19.7188 8.6719 L19.7188 8.6719 Z");
                nodeIcon.setScaleX(0.7);
                nodeIcon.setScaleY(0.6);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE: ",function.databaseProperty(),"\n",
                                        "OWNER   : ",function.ownerProperty(),"\n",
                                        "FUNCNAME: ",function.nameProperty()
                        ));
                setTooltip(tooltip);
                descripLabel.textProperty().unbind();
                descripLabel.setText("FUNC");
            }
            else if(item instanceof Procedure){
                Procedure procedure=(Procedure)item;
                nodeIcon.setContent("M14.8125 16.3594 L18.1406 13.7344 Q18.2188 13.6875 18.2188 13.5938 Q18.2188 13.5 18.1406 13.4531 L14.8125 10.8281 Q14.7188 10.75 14.6094 10.7969 Q14.5156 10.8438 14.5156 10.9688 L14.5156 12.75 L6.9219 12.75 Q6.8438 12.75 6.7812 12.8125 Q6.7344 12.8594 6.7344 12.9375 L6.7344 14.25 Q6.7344 14.3125 6.7812 14.375 Q6.8438 14.4375 6.9219 14.4375 L14.5156 14.4375 L14.5156 16.2188 Q14.5156 16.3281 14.6094 16.375 Q14.7188 16.4219 14.8125 16.3594 ZM20.9375 6.8594 L17.1406 3.0625 Q17 2.9219 16.8281 2.8281 Q16.6719 2.7344 16.5 2.6875 L16.5 2.625 L3.375 2.625 Q3.0781 2.625 2.8438 2.8438 Q2.625 3.0625 2.625 3.375 L2.625 9.8906 Q2.625 9.9531 2.6875 10.0156 Q2.75 10.0781 2.8125 10.0781 L4.125 10.0781 Q4.2031 10.0781 4.25 10.0156 Q4.3125 9.9531 4.3125 9.8906 L4.3125 4.3125 L7.5 4.3125 L7.5 7.5 Q7.5 7.7969 7.7188 8.0312 Q7.9531 8.25 8.25 8.25 L15.75 8.25 Q16.0625 8.25 16.2812 8.0312 Q16.5 7.7969 16.5 7.5 L16.5 4.8281 L19.6875 8.0156 L19.6875 9.8906 Q19.6875 9.9531 19.75 10.0156 Q19.8125 10.0781 19.875 10.0781 L21.1875 10.0781 Q21.2656 10.0781 21.3125 10.0156 Q21.375 9.9531 21.375 9.8906 L21.375 7.9375 Q21.375 7.6406 21.25 7.3594 Q21.1406 7.0781 20.9375 6.8594 L20.9375 6.8594 ZM15 6.75 L9 6.75 L9 4.3125 L15 4.3125 L15 6.75 ZM21.1875 16.9688 L19.875 16.9688 Q19.8125 16.9688 19.75 17.0312 Q19.6875 17.0781 19.6875 17.1562 L19.6875 19.6875 L4.3125 19.6875 L4.3125 17.1562 Q4.3125 17.0781 4.25 17.0312 Q4.2031 16.9688 4.125 16.9688 L2.8125 16.9688 Q2.75 16.9688 2.6875 17.0312 Q2.625 17.0781 2.625 17.1562 L2.625 20.625 Q2.625 20.9219 2.8438 21.1562 Q3.0781 21.375 3.375 21.375 L20.625 21.375 Q20.9375 21.375 21.1562 21.1562 Q21.375 20.9219 21.375 20.625 L21.375 17.1562 Q21.375 17.0781 21.3125 17.0312 Q21.2656 16.9688 21.1875 16.9688 Z");
                nodeIcon.setScaleX(0.55);
                nodeIcon.setScaleY(0.55);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE: ",procedure.databaseProperty(),"\n",
                                "OWNER   : ",procedure.ownerProperty(),"\n",
                                "PROCNAME: ",procedure.nameProperty()
                        ));
                setTooltip(tooltip);
                descripLabel.textProperty().unbind();
                descripLabel.setText("PROC");
            }
            else if(item instanceof DBPackage){
                DBPackage dbPackage = (DBPackage)item;
                nodeIcon.setContent("M12.0469 -0.0703 L0 4.4766 L0 19.5703 L12 24.0703 L24 19.5703 L24 4.4766 L12.0469 -0.0703 L12.0469 -0.0703 ZM21.2812 4.9453 L17.625 6.3047 L8.3906 2.8359 L12 1.4297 L21.2812 4.9453 L21.2812 4.9453 ZM12 8.4609 L2.7188 4.9453 L6.375 3.5859 L15.6562 7.0547 L12 8.4609 L12 8.4609 ZM1.4062 5.9766 L11.2969 9.6797 L11.2969 22.2891 L1.4062 18.5859 L1.4062 5.9766 L1.4062 5.9766 ZM12.7031 22.2891 L12.7031 9.6797 L22.5938 5.9766 L22.5938 18.5859 L12.7031 22.2891 L12.7031 22.2891 Z");
                nodeIcon.setScaleX(0.45);
                nodeIcon.setScaleY(0.45);
                nodeIcon.setFill(Color.valueOf("#074675"));
                warnIcon.visibleProperty().unbind();
                warnIcon.visibleProperty().bind(dbPackage.isEmptyProperty());
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                tooltip.textProperty().unbind();
                tooltip.textProperty().bind(
                        Bindings.concat(
                                "DATABASE: ",dbPackage.databaseProperty(),"\n",
                                "OWNER   : ",dbPackage.ownerProperty(),"\n",
                                "PKGNAME : ",dbPackage.nameProperty()
                        ));
                setTooltip(tooltip);
                descripLabel.textProperty().unbind();
                descripLabel.setText("PACKAGE");
                graphicHbox.getChildren().clear();
                graphicHbox.getChildren().addAll(nodeIconStackpane,nameLabel, spacer,warnIconGroup,descripLabel);
                setGraphic(graphicHbox);
            }
            else if(item instanceof PackageFunction){
                PackageFunction packageFunction = (PackageFunction)item;
                nodeIcon.setContent("M19.7188 8.6719 Q19.7656 8.625 19.7656 8.5469 Q19.7656 8.4531 19.6875 8.4062 Q19.6719 8.3906 19.6406 8.375 Q19.625 8.3594 19.5781 8.3594 L17.8906 8.3594 Q17.8438 8.3594 17.7969 8.375 Q17.7656 8.3906 17.7188 8.4062 L14.8594 11.8281 Q14.7969 11.875 14.7188 11.8906 Q14.6562 11.9062 14.5781 11.8281 Q14.5781 11.8281 14.5469 11.8281 Q14.5312 11.8125 14.5312 11.7812 L13.0625 8.4531 Q13.0312 8.4062 12.9844 8.3906 Q12.9375 8.3594 12.875 8.3594 L8.9375 8.3594 L8.9531 8.25 L9.1406 7.2656 Q9.3125 6.2812 9.8125 5.8125 Q10.3125 5.3438 11.1406 5.3438 Q11.4688 5.3438 11.75 5.375 Q12.0469 5.4062 12.2812 5.4531 L12.6094 3.8906 Q12.2188 3.8125 11.9375 3.7812 Q11.6719 3.75 11.3281 3.75 Q9.5156 3.75 8.5312 4.5781 Q7.5469 5.3906 7.2188 7.1875 L6.9844 8.3594 L4.7188 8.3594 Q4.6406 8.3594 4.5781 8.4062 Q4.5312 8.4375 4.5312 8.5 L4.2656 9.7188 Q4.25 9.7969 4.2969 9.875 Q4.3438 9.9375 4.4062 9.9375 Q4.4062 9.9531 4.4219 9.9531 Q4.4375 9.9531 4.4531 9.9531 L6.6562 9.9531 L4.5781 20.0312 Q4.5469 20.1094 4.5938 20.1719 Q4.6406 20.2188 4.7188 20.25 Q4.7344 20.25 4.7344 20.25 Q4.7344 20.25 4.7656 20.25 L6.3125 20.25 Q6.375 20.25 6.4219 20.2188 Q6.4688 20.1719 6.5 20.1094 L8.6094 9.9531 L11.7656 9.9531 L13.3906 13.2188 Q13.4062 13.2656 13.3906 13.3281 Q13.3906 13.375 13.3438 13.4219 L9.125 18.1875 Q9.0781 18.2344 9.0781 18.3125 Q9.0781 18.3906 9.125 18.4375 Q9.1719 18.4688 9.1875 18.4844 Q9.2188 18.4844 9.2656 18.4844 L10.9531 18.4844 Q11 18.4844 11.0312 18.4844 Q11.0625 18.4688 11.1094 18.4219 L14 15 Q14.0469 14.9219 14.125 14.9219 Q14.2031 14.9062 14.25 14.9531 Q14.2812 14.9688 14.2812 14.9844 Q14.2969 15 14.3281 15.0156 L15.8438 18.375 Q15.875 18.4219 15.9219 18.4531 Q15.9688 18.4844 16.0312 18.4844 L17.5312 18.4844 Q17.625 18.4844 17.6719 18.4375 Q17.7188 18.375 17.7188 18.2969 Q17.7188 18.2812 17.7188 18.2656 Q17.7188 18.2344 17.7188 18.2031 L15.4688 13.5469 Q15.4531 13.5 15.4531 13.4375 Q15.4688 13.375 15.5156 13.3594 L19.7188 8.6719 L19.7188 8.6719 Z");
                nodeIcon.setScaleX(0.7);
                nodeIcon.setScaleY(0.6);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                descripLabel.textProperty().unbind();
                descripLabel.textProperty().bind(packageFunction.descriptionProperty());
            }
            else if(item instanceof PackageProcedure){
                PackageProcedure packageProcedure = (PackageProcedure)item;
                nodeIcon.setContent("M14.8125 16.3594 L18.1406 13.7344 Q18.2188 13.6875 18.2188 13.5938 Q18.2188 13.5 18.1406 13.4531 L14.8125 10.8281 Q14.7188 10.75 14.6094 10.7969 Q14.5156 10.8438 14.5156 10.9688 L14.5156 12.75 L6.9219 12.75 Q6.8438 12.75 6.7812 12.8125 Q6.7344 12.8594 6.7344 12.9375 L6.7344 14.25 Q6.7344 14.3125 6.7812 14.375 Q6.8438 14.4375 6.9219 14.4375 L14.5156 14.4375 L14.5156 16.2188 Q14.5156 16.3281 14.6094 16.375 Q14.7188 16.4219 14.8125 16.3594 ZM20.9375 6.8594 L17.1406 3.0625 Q17 2.9219 16.8281 2.8281 Q16.6719 2.7344 16.5 2.6875 L16.5 2.625 L3.375 2.625 Q3.0781 2.625 2.8438 2.8438 Q2.625 3.0625 2.625 3.375 L2.625 9.8906 Q2.625 9.9531 2.6875 10.0156 Q2.75 10.0781 2.8125 10.0781 L4.125 10.0781 Q4.2031 10.0781 4.25 10.0156 Q4.3125 9.9531 4.3125 9.8906 L4.3125 4.3125 L7.5 4.3125 L7.5 7.5 Q7.5 7.7969 7.7188 8.0312 Q7.9531 8.25 8.25 8.25 L15.75 8.25 Q16.0625 8.25 16.2812 8.0312 Q16.5 7.7969 16.5 7.5 L16.5 4.8281 L19.6875 8.0156 L19.6875 9.8906 Q19.6875 9.9531 19.75 10.0156 Q19.8125 10.0781 19.875 10.0781 L21.1875 10.0781 Q21.2656 10.0781 21.3125 10.0156 Q21.375 9.9531 21.375 9.8906 L21.375 7.9375 Q21.375 7.6406 21.25 7.3594 Q21.1406 7.0781 20.9375 6.8594 L20.9375 6.8594 ZM15 6.75 L9 6.75 L9 4.3125 L15 4.3125 L15 6.75 ZM21.1875 16.9688 L19.875 16.9688 Q19.8125 16.9688 19.75 17.0312 Q19.6875 17.0781 19.6875 17.1562 L19.6875 19.6875 L4.3125 19.6875 L4.3125 17.1562 Q4.3125 17.0781 4.25 17.0312 Q4.2031 16.9688 4.125 16.9688 L2.8125 16.9688 Q2.75 16.9688 2.6875 17.0312 Q2.625 17.0781 2.625 17.1562 L2.625 20.625 Q2.625 20.9219 2.8438 21.1562 Q3.0781 21.375 3.375 21.375 L20.625 21.375 Q20.9375 21.375 21.1562 21.1562 Q21.375 20.9219 21.375 20.625 L21.375 17.1562 Q21.375 17.0781 21.3125 17.0312 Q21.2656 16.9688 21.1875 16.9688 Z");
                nodeIcon.setScaleX(0.55);
                nodeIcon.setScaleY(0.55);
                nodeIcon.setFill(Color.valueOf("#074675"));
                nameLabel.textProperty().unbind();
                nameLabel.textProperty().bind(item.nameProperty());
                descripLabel.textProperty().unbind();
                descripLabel.textProperty().bind(packageProcedure.descriptionProperty());
            }
            else if(item instanceof UserFolder){
                nodeIcon.setContent("M14.8185 14.0364C14.4045 14.0621 14.3802 14.6183 14.7606 14.7837V14.7837C15.803 15.237 16.5879 15.9043 17.1508 16.756C17.6127 17.4549 18.33 18 19.1677 18H20.9483C21.6555 18 22.1715 17.2973 21.9227 16.6108C21.9084 16.5713 21.8935 16.5321 21.8781 16.4932C21.5357 15.6286 20.9488 14.9921 20.0798 14.5864C19.2639 14.2055 18.2425 14.0483 17.0392 14.0008L17.0194 14H16.9997C16.2909 14 15.5506 13.9909 14.8185 14.0364Z M4.64115 15.6993C5.87351 15.1644 7.49045 15 9.49995 15C11.5112 15 13.1293 15.1647 14.3621 15.7008C15.705 16.2847 16.5212 17.2793 16.949 18.6836C17.1495 19.3418 16.6551 20 15.9738 20H3.02801C2.34589 20 1.85045 19.3408 2.05157 18.6814C2.47994 17.2769 3.29738 16.2826 4.64115 15.6993Z M14.3675 12.0632C14.322 12.1494 14.3413 12.2569 14.4196 12.3149C15.0012 12.7454 15.7209 13 16.5 13C18.433 13 20 11.433 20 9.5C20 7.567 18.433 6 16.5 6C15.7209 6 15.0012 6.2546 14.4196 6.68513C14.3413 6.74313 14.322 6.85058 14.3675 6.93679C14.7714 7.70219 15 8.5744 15 9.5C15 10.4256 14.7714 11.2978 14.3675 12.0632Z M5 9.5C5 7.01472 7.01472 5 9.5 5C11.9853 5 14 7.01472 14 9.5C14 11.9853 11.9853 14 9.5 14C7.01472 14 5 11.9853 5 9.5Z");
                //单个用户图标
                //userIcon.setContent("M7.5 6.9922 Q7.5 8.8672 8.8281 10.1797 Q10.1562 11.4922 12 11.4922 Q13.8594 11.4922 15.1719 10.1797 Q16.5 8.8672 16.5 6.9922 Q16.5 5.1484 15.1719 3.8203 Q13.8594 2.4922 12 2.4922 Q10.1562 2.4922 8.8281 3.8203 Q7.5 5.1484 7.5 6.9922 ZM20 21.5078 L21 21.5078 L21 20.4922 Q21 19.0703 20.4375 17.7734 Q19.9062 16.5078 18.9531 15.5703 Q18 14.6172 16.7188 14.0547 Q15.4531 13.5078 14 13.5078 L10.0156 13.5078 Q8.5625 13.5078 7.2969 14.0547 Q6 14.6172 5.0469 15.5703 Q4.1094 16.5078 3.5625 17.7734 Q3 19.0703 3 20.4922 L3 21.5078 L20 21.5078 L20 21.5078 Z");
                nodeIcon.setScaleX(0.65);
                nodeIcon.setScaleY(0.65);
                nodeIcon.setFill(Color.valueOf("#074675"));
                textProperty().unbind();
                textProperty().bind(getItem().nameProperty());
                setGraphic(nodeIconStackpane);
            }
            else if(item instanceof User){
                //nodeIcon.setContent("M14.8185 14.0364C14.4045 14.0621 14.3802 14.6183 14.7606 14.7837V14.7837C15.803 15.237 16.5879 15.9043 17.1508 16.756C17.6127 17.4549 18.33 18 19.1677 18H20.9483C21.6555 18 22.1715 17.2973 21.9227 16.6108C21.9084 16.5713 21.8935 16.5321 21.8781 16.4932C21.5357 15.6286 20.9488 14.9921 20.0798 14.5864C19.2639 14.2055 18.2425 14.0483 17.0392 14.0008L17.0194 14H16.9997C16.2909 14 15.5506 13.9909 14.8185 14.0364Z M4.64115 15.6993C5.87351 15.1644 7.49045 15 9.49995 15C11.5112 15 13.1293 15.1647 14.3621 15.7008C15.705 16.2847 16.5212 17.2793 16.949 18.6836C17.1495 19.3418 16.6551 20 15.9738 20H3.02801C2.34589 20 1.85045 19.3408 2.05157 18.6814C2.47994 17.2769 3.29738 16.2826 4.64115 15.6993Z M14.3675 12.0632C14.322 12.1494 14.3413 12.2569 14.4196 12.3149C15.0012 12.7454 15.7209 13 16.5 13C18.433 13 20 11.433 20 9.5C20 7.567 18.433 6 16.5 6C15.7209 6 15.0012 6.2546 14.4196 6.68513C14.3413 6.74313 14.322 6.85058 14.3675 6.93679C14.7714 7.70219 15 8.5744 15 9.5C15 10.4256 14.7714 11.2978 14.3675 12.0632Z M5 9.5C5 7.01472 7.01472 5 9.5 5C11.9853 5 14 7.01472 14 9.5C14 11.9853 11.9853 14 9.5 14C7.01472 14 5 11.9853 5 9.5Z");
                //单个用户图标
                nodeIcon.setContent("M7.5 6.9922 Q7.5 8.8672 8.8281 10.1797 Q10.1562 11.4922 12 11.4922 Q13.8594 11.4922 15.1719 10.1797 Q16.5 8.8672 16.5 6.9922 Q16.5 5.1484 15.1719 3.8203 Q13.8594 2.4922 12 2.4922 Q10.1562 2.4922 8.8281 3.8203 Q7.5 5.1484 7.5 6.9922 ZM20 21.5078 L21 21.5078 L21 20.4922 Q21 19.0703 20.4375 17.7734 Q19.9062 16.5078 18.9531 15.5703 Q18 14.6172 16.7188 14.0547 Q15.4531 13.5078 14 13.5078 L10.0156 13.5078 Q8.5625 13.5078 7.2969 14.0547 Q6 14.6172 5.0469 15.5703 Q4.1094 16.5078 3.5625 17.7734 Q3 19.0703 3 20.4922 L3 21.5078 L20 21.5078 L20 21.5078 Z");
                nodeIcon.setScaleX(0.55);
                nodeIcon.setScaleY(0.55);
                nodeIcon.setFill(Color.valueOf("#074675"));
                textProperty().unbind();
                textProperty().bind(getItem().nameProperty());
                setGraphic(nodeIconStackpane);
            }
            else if(item instanceof CheckFolder){
                nodeIcon.setContent("M8 16L4.35009 13.3929C2.24773 11.8912 1 9.46667 1 6.88306V3L8 0L15 3V6.88306C15 9.46667 13.7523 11.8912 11.6499 13.3929L8 16ZM12.2071 5.70711L10.7929 4.29289L7 8.08579L5.20711 6.29289L3.79289 7.70711L7 10.9142L12.2071 5.70711Z");
                nodeIcon.setScaleX(0.75);
                nodeIcon.setScaleY(0.75);
                nodeIcon.setFill(Color.valueOf("#074675"));
                textProperty().unbind();
                textProperty().bind(getItem().nameProperty());
                setGraphic(nodeIconStackpane);
            }
            else if(item instanceof MonFolder){
                nodeIcon.setContent("M3.75 3 Q4.0781 3 4.2969 3.25 Q4.5156 3.4844 4.5156 3.7812 L4.5156 19.5156 L20.25 19.5156 Q20.5469 19.5156 20.7812 19.7344 Q21.0312 19.9531 21.0312 20.2656 Q21.0312 20.5781 20.7812 20.7969 Q20.5469 21 20.25 21 L3.75 21 Q3.4531 21 3.2344 20.7969 Q3.0312 20.5781 3.0312 20.2812 L3.0312 3.7812 Q3.0312 3.4844 3.2344 3.25 Q3.4531 3 3.75 3 ZM18.2812 6.1719 Q18.625 5.9375 19.0312 6.1094 Q19.4375 6.2656 19.4844 6.6562 L19.4844 18.5156 L5.5156 18.5156 L5.5156 9.4375 L8.1562 8.0938 Q8.4531 7.9531 8.7812 8.0938 L12.7188 10.4062 L18.2812 6.1719 Z");
                nodeIcon.setScaleX(0.6);
                nodeIcon.setScaleY(0.6);
                nodeIcon.setFill(Color.valueOf("#074675"));
                textProperty().unbind();
                textProperty().bind(getItem().nameProperty());
                setGraphic(nodeIconStackpane);
            }

            //子节点增加双击监听
            if(getTreeItem().isLeaf() && !(item instanceof Loading) && !(item instanceof Connecting)&& !(item instanceof User)){


                    //如果是叶子节点，需要响应双击事件
                    nodeIconStackpane.getChildren().remove(runningIcon);
                    nodeIconStackpane.getChildren().add(runningIcon);
                    runningIcon.visibleProperty().unbind();
                    runningIcon.visibleProperty().bind(item.runningProperty());
                    nodeIconGroup.visibleProperty().unbind();
                    nodeIconGroup.visibleProperty().bind(runningIcon.visibleProperty().not());
                    graphicHbox.getChildren().clear();
                    graphicHbox.getChildren().addAll(nodeIconStackpane, nameLabel, spacer, warnIconGroup, descripLabel);
                    setGraphic(graphicHbox);

                    setOnMouseClicked(null);
                    if(item instanceof Table){

                        setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            
                                    TabpaneUtil.addCustomTableInfoTab(getTreeItem());
                                    
                            
                        }
                    });
                    }else{
                    setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            DDLTask = new Task<>() {
                                @Override
                                protected Void call() throws Exception {
                                    ddl_popupstage_ddlsql = "";
                                    
                                    Object parentValue = getTreeItem().getParent() == null ? null : getTreeItem().getParent().getValue();
                                    ddl_popupstage_ddlsql = MetadataTreeviewUtil.metadataService.withMetaSession(
                                            getTreeItem(),
                                            conn -> MetadataTreeviewUtil.metadataService.getDDL(conn, getTreeItem().getValue(), parentValue)
                                    );
                                    return null;
                                }
                            };
                            DDLTask.setOnSucceeded(event1 -> {
                                if (!ddl_popupstage_ddlsql.equals("")) {
                                    PopupWindowUtil.openDDLWindow(ddl_popupstage_ddlsql);
                                }
                                item.setRunning(false);
                            });
                            GlobalErrorHandlerUtil.bindTask(DDLTask, () -> item.setRunning(false));
                            Thread thread = new Thread(DDLTask);
                            MetadataTreeviewUtil.getMetaConnect(getTreeItem()).executeSqlTask(thread);
                            item.setRunning(true);
                        }
                    });
                    }
                    

            }

        }

        setOnDragDetected(null);
        setOnDragDone(null);
        //拖动效果，如果是表或者视图，拖动到右侧显示数据
        if(item instanceof ConnectFolder||item instanceof  Connect||item instanceof Table||item instanceof View||item instanceof Database) {
            setOnDragDetected(event -> {
                if (getItem() == null) return;

                // 1. 创建当前单元格的快照（作为拖动图像）
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT); // 背景透明
                Image dragImage = this.snapshot(params, null);

                // 2. 初始化拖拽板，设置数据
                Dragboard db = startDragAndDrop(TransferMode.COPY_OR_MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("DATABASEOBJECTDRAG");
                db.setContent(content);

                // 3. 设置拖动图像，并调整偏移量（让鼠标在图像左上角，避免错位）
                db.setDragView(dragImage, event.getX(), event.getY());

                event.consume();
            });

            // 拖拽结束处理（移动操作时删除原数据）
            setOnDragDone(event -> {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    if(item instanceof ConnectFolder){
                        if(getTreeItem().getChildren().size()>0) {
                            MetadataTreeviewUtil.connectFolderInfoItem.fire();
                        }
                        else{
                            NotificationUtil.showNotification(Main.mainController.noticePane,"当前分类无连接!");
                        }
                    }else if(item instanceof Connect){
                        MetadataTreeviewUtil.connectInfoItem.fire();
                    }
                    else if(item instanceof Database){
                        MetadataTreeviewUtil.databaseOpenFileItem.fire();
                    }else {
                        String sql="";
                        if(item instanceof Table){
                            if(((Table)item).getIsfragment()==1){
                                sql+=("select * from "+item.getName());
                            }else{
                                sql+=("select rowid,* from "+item.getName());
                            }
                        }else{
                            sql+=("select * from "+item.getName());
                        }
                        sql+=";";
                        CustomSqlTab customSqlTab=null;
                        if(Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem() instanceof CustomSqlTab){
                            customSqlTab= (CustomSqlTab) Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem();
                        }
                        //如果连接和库名都相等，且没有当前执行任务，在当前窗口执行sql
                        if((Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem() instanceof CustomSqlTab)&&(customSqlTab.sqlTabController.sqlConnectChoiceBox.getValue().getName().equals(MetadataTreeviewUtil.getMetaConnect(getTreeItem()).getName()))&&(customSqlTab.sqlTabController.sqlDbChoiceBox.getValue().getName().equals(MetadataTreeviewUtil.getCurrentDatabase(getTreeItem()).getName()))&&(!customSqlTab.sqlTabController.sqlRunButton.isDisable())){

                            if(!customSqlTab.sqlTabController.sqlEditCodeArea.getText().isEmpty()){
                                sql="\n"+sql;
                            }
                            int start = customSqlTab.sqlTabController.sqlEditCodeArea.getLength();
                            customSqlTab.sqlTabController.sqlEditCodeArea.appendText(sql);
                            customSqlTab.sqlTabController.sqlEditCodeArea.moveTo(start);
                            customSqlTab.sqlTabController.sqlEditCodeArea.requestFollowCaret();
                            customSqlTab.sqlTabController.sqlEditCodeArea.selectRange(customSqlTab.sqlTabController.sqlEditCodeArea.getLength()-(sql.startsWith("\n")?(sql.length()-1):sql.length()), customSqlTab.sqlTabController.sqlEditCodeArea.getLength());
                            customSqlTab.sqlTabController.sqlRunButton.fire();
                        }
                        else {
                            MetadataTreeviewUtil.databaseOpenFileItem.fire();
                            customSqlTab= (CustomSqlTab) Main.mainController.sqlTabPane.getSelectionModel().getSelectedItem();
                            customSqlTab.sqlTabController.sqlInit=sql;
                        }



                    }
                }
            });
        }
    }
}

