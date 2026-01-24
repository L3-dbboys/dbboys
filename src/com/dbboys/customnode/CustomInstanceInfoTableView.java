package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.util.MetadataTreeviewUtil;
import com.dbboys.util.TabpaneUtil;
import com.dbboys.vo.Connect;
import com.dbboys.vo.TreeData;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class CustomInstanceInfoTableView extends CustomResultsetTableView {
    public CustomInstanceInfoTableView(){
        super();
        TableColumn<Connect, String> name = new TableColumn<>("连接名称");
        // 自定义 TableView 的排序策略

        TableColumn<Connect, String> dbtype = new TableColumn<>("数据库类型");
        TableColumn<Connect, String> dbversion = new TableColumn<>("数据库版本");
        TableColumn<Connect, String> ip = new TableColumn<>("地址");
        TableColumn<Connect, String> database = new TableColumn<>("库名");
        TableColumn<Connect, String> port = new TableColumn<>("端口");
        TableColumn<Connect, String> username = new TableColumn<>("用户名");
        //TableColumn<Connect, String> driver = new TableColumn<>("驱动");
        TableColumn<Connect, String> status = new TableColumn<>("只读");
        status.setSortable(false);
        //这里前提是把Connect所在的包在moodule-info.java export出来，否可会报以下错误
        //Can not retrieve property 'xxxx' in PropertyValueFactory
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        //name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        ((TableColumn<?, ?>) getColumns().get(0)).setMaxWidth(30);
        ((TableColumn<?, ?>) getColumns().get(0)).setMinWidth(30);

        /*

        SVGPath indexIcon = new SVGPath();
        indexIcon.setScaleX(0.65);
        indexIcon.setScaleY(0.65);
        indexIcon.setContent("M11.9062 6.5547 L10.4375 6.5547 Q10.3594 6.5547 10.3281 6.6016 Q10.2969 6.6484 10.2969 6.6953 L10.2969 14.6641 L10.1562 14.6641 L4.625 6.6172 Q4.625 6.5703 4.5781 6.5703 Q4.5469 6.5547 4.5312 6.5547 L2.9531 6.5547 Q2.9062 6.5547 2.8594 6.6016 Q2.8125 6.6484 2.8125 6.6953 L2.8125 17.5703 Q2.8125 17.6172 2.8594 17.6641 Q2.9062 17.7109 2.9531 17.7109 L4.4375 17.7109 Q4.4844 17.7109 4.5312 17.6641 Q4.5781 17.6172 4.5781 17.5703 L4.5781 9.4766 L4.6875 9.4766 L10.2969 17.6328 Q10.2969 17.6797 10.3281 17.6953 Q10.3594 17.7109 10.4062 17.7109 L11.9062 17.7109 Q11.9531 17.7109 12 17.6641 Q12.0469 17.6172 12.0469 17.5703 L12.0469 6.6953 Q12.0469 6.6484 12 6.6016 Q11.9531 6.5547 11.9062 6.5547 L11.9062 6.5547 ZM20.7656 16.2266 L13.6406 16.2266 Q13.5781 16.2266 13.5156 16.2891 Q13.4531 16.3516 13.4531 16.4141 L13.4531 17.5391 Q13.4531 17.6172 13.5156 17.6797 Q13.5781 17.7266 13.6406 17.7266 L20.7656 17.7266 Q20.8438 17.7266 20.8906 17.6797 Q20.9531 17.6172 20.9531 17.5391 L20.9531 16.4141 Q20.9531 16.3516 20.8906 16.2891 Q20.8438 16.2266 20.7656 16.2266 ZM17.2031 14.7578 Q18.125 14.7578 18.875 14.4609 Q19.625 14.1484 20.1562 13.5391 Q20.6719 12.9766 20.9219 12.2109 Q21.1875 11.4453 21.1875 10.5078 Q21.1875 9.6016 20.9219 8.8359 Q20.6719 8.0703 20.1562 7.4922 Q19.625 6.8828 18.875 6.5859 Q18.1406 6.2734 17.2031 6.2734 Q16.2656 6.2734 15.5156 6.5859 Q14.7656 6.8828 14.25 7.5078 Q13.7344 8.1016 13.4688 8.8516 Q13.2188 9.6016 13.2188 10.5078 Q13.2188 11.4453 13.4688 12.1953 Q13.7344 12.9453 14.25 13.5391 Q14.7969 14.1484 15.5312 14.4609 Q16.2656 14.7578 17.2031 14.7578 L17.2031 14.7578 ZM15.5156 8.5703 Q15.8281 8.2109 16.2344 8.0234 Q16.6406 7.8359 17.2031 7.8359 Q17.75 7.8359 18.1719 8.0234 Q18.5938 8.1953 18.875 8.5234 Q19.1562 8.8984 19.2969 9.3984 Q19.4531 9.8828 19.4531 10.5078 Q19.4531 11.1641 19.2969 11.6484 Q19.1562 12.1328 18.875 12.4766 Q18.5625 12.8359 18.1562 13.0078 Q17.75 13.1797 17.2031 13.1797 Q16.6406 13.1797 16.2344 12.9922 Q15.8281 12.8047 15.5156 12.4609 Q15.2344 12.1016 15.0938 11.6328 Q14.9531 11.1484 14.9531 10.5391 Q14.9531 9.8984 15.0938 9.4141 Q15.2344 8.9141 15.5156 8.5703 L15.5156 8.5703 Z");
        indexIcon.setFill(Color.valueOf("#074675"));
        indexColumn.setGraphic(indexIcon);

         */

        SVGPath nameIcon = new SVGPath();
        nameIcon.setScaleX(0.5);
        nameIcon.setScaleY(0.5);
        nameIcon.setContent("M21.2812 3.3281 Q22.2656 4.3125 22.6094 5.6562 Q22.9688 6.9844 22.6094 8.3281 Q22.2656 9.6562 21.2812 10.6406 L19.4531 12.4688 Q18.4688 13.4531 17.125 13.7812 Q15.7969 14.1094 14.4844 13.7812 L19.4531 8.8125 Q20.2031 8.0625 20.2031 6.9844 Q20.2031 5.9062 19.4531 5.1562 Q18.7031 4.4062 17.625 4.4062 Q16.5469 4.4062 15.7969 5.1562 L10.8281 10.125 Q10.5 8.8125 10.8281 7.4844 Q11.1562 6.1406 12.1406 5.1562 L13.9688 3.3281 Q14.9531 2.3438 16.2812 2 Q17.625 1.6406 18.9531 2 Q20.2969 2.3438 21.2812 3.3281 ZM10.3125 16.125 L16.7344 9.7031 Q17.1094 9.3281 17.1094 8.7969 Q17.1094 8.25 16.7031 7.875 Q16.3125 7.5 15.7656 7.5312 Q15.2344 7.5469 14.8594 7.875 L8.4844 14.2969 Q8.1094 14.6719 8.1094 15.2188 Q8.1094 15.75 8.4844 16.125 Q8.8594 16.5 9.3906 16.4844 Q9.9375 16.4531 10.3125 16.125 ZM9.375 18.8438 L14.3438 13.875 Q14.7188 15.1875 14.3906 16.5312 Q14.0625 17.8594 13.0781 18.8438 L11.25 20.6719 Q10.2188 21.7031 8.9062 22.0312 Q7.5938 22.3594 6.25 22.0312 Q4.9219 21.7031 3.9062 20.7031 Q2.9062 19.6875 2.5469 18.3594 Q2.2031 17.0156 2.5469 15.6875 Q2.9062 14.3438 3.8906 13.3594 L5.7656 11.5312 Q6.75 10.5469 8.0625 10.2188 Q9.375 9.8906 10.7344 10.2188 L5.7656 15.1875 Q4.9688 15.9375 4.9688 17.0156 Q4.9688 18.0938 5.7344 18.875 Q6.5156 19.6406 7.5625 19.6406 Q8.625 19.6406 9.375 18.8438 Z");
        nameIcon.setFill(Color.valueOf("#074675"));
        name.setGraphic(nameIcon);

        dbtype.setCellValueFactory(new PropertyValueFactory<>("dbtype"));
        SVGPath dbtypeIcon = new SVGPath();
        dbtypeIcon.setScaleX(0.4);
        dbtypeIcon.setScaleY(0.4);
        dbtypeIcon.setContent("M21 3.4219 L21 5.5781 Q21 6.9844 17.9219 8 Q14.8594 9 10.5 9 Q6.1406 9 3.0625 8 Q0 6.9844 0 5.5781 L0 3.4219 Q0 2.0156 3.0625 1.0156 Q6.1406 0 10.5 0 Q14.8594 0 17.9219 1.0156 Q21 2.0156 21 3.4219 ZM21 8.25 L21 13.0781 Q21 14.4844 17.9219 15.5 Q14.8594 16.5 10.5 16.5 Q6.1406 16.5 3.0625 15.5 Q0 14.4844 0 13.0781 L0 8.25 Q3.3281 10.5469 10.5 10.5469 Q17.6719 10.5469 21 8.25 ZM21 15.75 L21 20.5781 Q21 21.9844 17.9219 22.9844 Q14.8594 24 10.5 24 Q6.1406 24 3.0625 22.9844 Q0 21.9844 0 20.5781 L0 15.75 Q3.3281 18.0469 10.5 18.0469 Q17.6719 18.0469 21 15.75 Z");
        dbtypeIcon.setFill(Color.valueOf("#074675"));
        dbtype.setGraphic(dbtypeIcon);

        dbversion.setCellValueFactory(new PropertyValueFactory<>("dbversion"));
        SVGPath dbversionIcon = new SVGPath();
        dbversionIcon.setScaleX(0.5);
        dbversionIcon.setScaleY(0.5);
        dbversionIcon.setContent("M11.625 21 Q10.5469 21 9.7656 20.2344 Q9 19.4531 9 18.375 L9 18.375 L9 5.625 Q9 4.5469 9.7656 3.7812 Q10.5469 3 11.625 3 L21.375 3 Q22.4531 3 23.2188 3.7812 Q24 4.5469 24 5.625 L24 18.375 Q24 19.4531 23.2188 20.2344 Q22.4531 21 21.375 21 L21.375 21 L11.625 21 ZM11.25 18.375 Q11.25 18.5312 11.3594 18.6406 Q11.4688 18.75 11.625 18.75 L21.375 18.75 Q21.5469 18.75 21.6406 18.6406 Q21.75 18.5312 21.75 18.375 L21.75 18.375 L21.75 5.625 Q21.75 5.4531 21.6406 5.3594 Q21.5469 5.25 21.375 5.25 L21.375 5.25 L11.625 5.25 Q11.4688 5.25 11.3594 5.3594 Q11.25 5.4531 11.25 5.625 L11.25 5.625 L11.25 18.375 ZM7.3594 5.2656 Q7.4375 5.3906 7.4688 5.5312 Q7.5 5.6719 7.5 5.8281 Q7.5 6.1406 7.3438 6.4062 Q7.2031 6.6562 6.9375 6.7969 L6.9375 6.7969 Q6.8438 6.8438 6.7969 6.9375 Q6.75 7.0312 6.75 7.125 L6.75 7.125 L6.75 16.875 Q6.75 16.9688 6.7969 17.0625 Q6.8438 17.1562 6.9375 17.2031 L6.9375 17.2031 Q7.2031 17.3438 7.3438 17.6094 Q7.5 17.8594 7.5 18.1562 Q7.5 18.625 7.1719 18.9531 Q6.8438 19.2812 6.375 19.2812 Q6.2188 19.2812 6.0781 19.25 Q5.9375 19.2188 5.8125 19.1406 L5.8125 19.1406 Q5.2344 18.7969 4.8594 18.2031 Q4.5 17.5938 4.5 16.875 Q4.5 16.875 4.5 16.875 Q4.5 16.875 4.5 16.875 L4.5 16.875 L4.5 7.125 Q4.5 6.3906 4.8594 5.7812 Q5.2344 5.1719 5.8125 4.8438 Q5.9375 4.7812 6.0781 4.75 Q6.2188 4.7031 6.375 4.7031 Q6.6875 4.7031 6.9375 4.8594 Q7.2031 5.0156 7.3438 5.25 L7.3594 5.2656 L7.3594 5.2656 ZM2.4375 8.2969 Q2.7031 8.1562 2.8438 7.9062 Q3 7.6406 3 7.3281 Q3 6.8594 2.6719 6.5312 Q2.3438 6.2031 1.875 6.2031 Q1.7188 6.2031 1.5781 6.25 Q1.4375 6.2812 1.3125 6.3438 L1.3125 6.3438 Q0.7344 6.7031 0.3594 7.2969 Q0 7.8906 0 8.625 Q0 8.625 0 8.625 Q0 8.625 0 8.625 L0 8.625 L0 15.375 Q0 16.0938 0.3594 16.7031 Q0.7344 17.3125 1.3125 17.6406 Q1.4375 17.7188 1.5781 17.75 Q1.7188 17.7812 1.875 17.7812 Q2.3438 17.7812 2.6719 17.4531 Q3 17.125 3 16.6562 Q3 16.3594 2.8438 16.1094 Q2.7031 15.8438 2.4375 15.7031 L2.4375 15.7031 Q2.3438 15.6562 2.2969 15.5625 Q2.25 15.4688 2.25 15.375 L2.25 15.375 L2.25 8.625 Q2.25 8.5312 2.2969 8.4375 Q2.3438 8.3438 2.4375 8.2969 L2.4375 8.2969 L2.4375 8.2969 Z");
        dbversionIcon.setFill(Color.valueOf("#074675"));
        dbversion.setGraphic(dbversionIcon);

        ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
        SVGPath ipIcon = new SVGPath();
        ipIcon.setScaleX(0.6);
        ipIcon.setScaleY(0.6);
        ipIcon.setContent("M6.776,4.72h1.549v6.827H6.776V4.72z M11.751,4.669c-0.942,0-1.61,0.061-2.087,0.143v6.735h1.53 V9.106c0.143,0.02,0.324,0.031,0.527,0.031c0.911,0,1.691-0.224,2.218-0.721c0.405-0.386,0.628-0.952,0.628-1.621 c0-0.668-0.295-1.234-0.729-1.579C13.382,4.851,12.702,4.669,11.751,4.669z M11.709,7.95c-0.222,0-0.385-0.01-0.516-0.041V5.895 c0.111-0.03,0.324-0.061,0.639-0.061c0.769,0,1.205,0.375,1.205,1.002C13.037,7.535,12.53,7.95,11.709,7.95z M10.117,0 C5.523,0,1.8,3.723,1.8,8.316s8.317,11.918,8.317,11.918s8.317-7.324,8.317-11.917S14.711,0,10.117,0z M10.138,13.373 c-3.05,0-5.522-2.473-5.522-5.524c0-3.05,2.473-5.522,5.522-5.522c3.051,0,5.522,2.473,5.522,5.522 C15.66,10.899,13.188,13.373,10.138,13.373z");
        ipIcon.setFill(Color.valueOf("#074675"));
        ip.setGraphic(ipIcon);

        database.setCellValueFactory(new PropertyValueFactory<>("database"));
        SVGPath databaseIcon = new SVGPath();
        databaseIcon.setScaleX(0.5);
        databaseIcon.setScaleY(0.5);
        databaseIcon.setContent("M19.5 1.5 L6 1.5 Q5.375 1.5 4.9375 1.9375 Q4.5 2.3594 4.5 3 L4.5 3 L4.5 6 L3 6 L3 7.5 L4.5 7.5 L4.5 11.25 L3 11.25 L3 12.75 L4.5 12.75 L4.5 16.5 L3 16.5 L3 18 L4.5 18 L4.5 21 Q4.5 21.625 4.9375 22.0625 Q5.375 22.5 6 22.5 L6 22.5 L19.5 22.5 Q20.1406 22.5 20.5625 22.0625 Q21 21.625 21 21 L21 21 L21 3 Q21 2.3594 20.5625 1.9375 Q20.1406 1.5 19.5 1.5 L19.5 1.5 ZM19.5 21 L6 21 L6 18 L7.5 18 L7.5 16.5 L6 16.5 L6 12.75 L7.5 12.75 L7.5 11.25 L6 11.25 L6 7.5 L7.5 7.5 L7.5 6 L6 6 L6 3 L19.5 3 L19.5 21 ZM10.5 6 L16.5 6 L16.5 7.5 L10.5 7.5 L10.5 6 ZM10.5 11.25 L16.5 11.25 L16.5 12.75 L10.5 12.75 L10.5 11.25 ZM10.5 16.5 L16.5 16.5 L16.5 18 L10.5 18 L10.5 16.5 Z");
        databaseIcon.setFill(Color.valueOf("#074675"));
        database.setGraphic(databaseIcon);

        port.setCellValueFactory(new PropertyValueFactory<>("port"));
        SVGPath portIcon = new SVGPath();
        portIcon.setScaleX(0.7);
        portIcon.setScaleY(0.7);
        portIcon.setContent("M13.25 15C13.25 14.5858 12.9142 14.25 12.5 14.25C12.0858 14.25 11.75 14.5858 11.75 15H13.25ZM11.75 19C11.75 19.4142 12.0858 19.75 12.5 19.75C12.9142 19.75 13.25 19.4142 13.25 19H11.75ZM13.75 7C13.75 7.41421 14.0858 7.75 14.5 7.75C14.9142 7.75 15.25 7.41421 15.25 7H13.75ZM14.5 5H15.25C15.25 4.58579 14.9142 4.25 14.5 4.25V5ZM10.5 5V4.25C10.0858 4.25 9.75 4.58579 9.75 5H10.5ZM9.75 7C9.75 7.41421 10.0858 7.75 10.5 7.75C10.9142 7.75 11.25 7.41421 11.25 7H9.75ZM14.25 13C14.25 12.5858 13.9142 12.25 13.5 12.25C13.0858 12.25 12.75 12.5858 12.75 13H14.25ZM13.5 15V15.75C13.9142 15.75 14.25 15.4142 14.25 15H13.5ZM11.5 15H10.75C10.75 15.4142 11.0858 15.75 11.5 15.75V15ZM12.25 13C12.25 12.5858 11.9142 12.25 11.5 12.25C11.0858 12.25 10.75 12.5858 10.75 13H12.25ZM11.75 15V19H13.25V15H11.75ZM15.25 7V5H13.75V7H15.25ZM14.5 4.25H10.5V5.75H14.5V4.25ZM9.75 5V7H11.25V5H9.75ZM12.75 13V15H14.25V13H12.75ZM13.5 14.25H11.5V15.75H13.5V14.25ZM12.25 15V13H10.75V15H12.25Z M16.5 7H8.5V13H16.5V7Z");
        portIcon.setFill(Color.valueOf("#074675"));
        port.setGraphic(portIcon);

        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        SVGPath usernameIcon = new SVGPath();
        usernameIcon.setScaleX(0.5);
        usernameIcon.setScaleY(0.5);
        usernameIcon.setContent("M7.5 6.9922 Q7.5 8.8672 8.8281 10.1797 Q10.1562 11.4922 12 11.4922 Q13.8594 11.4922 15.1719 10.1797 Q16.5 8.8672 16.5 6.9922 Q16.5 5.1484 15.1719 3.8203 Q13.8594 2.4922 12 2.4922 Q10.1562 2.4922 8.8281 3.8203 Q7.5 5.1484 7.5 6.9922 ZM20 21.5078 L21 21.5078 L21 20.4922 Q21 19.0703 20.4375 17.7734 Q19.9062 16.5078 18.9531 15.5703 Q18 14.6172 16.7188 14.0547 Q15.4531 13.5078 14 13.5078 L10.0156 13.5078 Q8.5625 13.5078 7.2969 14.0547 Q6 14.6172 5.0469 15.5703 Q4.1094 16.5078 3.5625 17.7734 Q3 19.0703 3 20.4922 L3 21.5078 L20 21.5078 L20 21.5078 Z");
        usernameIcon.setFill(Color.valueOf("#074675"));
        username.setGraphic(usernameIcon);

        status.setCellValueFactory(new PropertyValueFactory<>("readonly"));
        SVGPath statusIcon = new SVGPath();
        statusIcon.setScaleX(0.6);
        statusIcon.setScaleY(0.6);
        statusIcon.setContent("M7.0513157,1.68377223 C7.34140089,0.813516663 8.52958649,0.773959592 8.90131717,1.56510102 L8.9486823,1.68377223 L11.999,10.838 L13.0513157,7.68377223 C13.1750557,7.31255211 13.5021221,7.05117665 13.8839548,7.00672177 L13.999999,7 L14.999999,7 C15.5522837,7 15.999999,7.44771525 15.999999,8 C15.999999,8.51283584 15.6139588,8.93550716 15.1166201,8.99327227 L14.999999,9 L14.72,9 L12.9486823,14.3162278 C12.6585971,15.1864833 11.4704115,15.2260404 11.0986808,14.434899 L11.0513157,14.3162278 L7.999999,5.161 L5.9486823,11.3162278 C5.66748671,12.1598145 4.52796777,12.2312701 4.12404882,11.4837549 L4.07152231,11.3713907 L2.961,8.596 L2.89017501,8.6833128 C2.73101502,8.85332296 2.51533249,8.96455528 2.27945404,8.99286635 L2.16,9 L0.999999,9 C0.44771425,9 -1e-06,8.55228475 -1e-06,8 C-1e-06,7.48716416 0.38603919,7.06449284 0.883377875,7.00672773 L0.999999,7 L1.495,7 L2.07801673,5.61276791 C2.40786899,4.82740446 3.48655852,4.79910618 3.87548912,5.51555754 L3.92847569,5.62860932 L4.914,8.094 L7.0513157,1.68377223 Z");
        statusIcon.setFill(Color.valueOf("#074675"));
        status.setGraphic(statusIcon);

        // 设置图标单元格工厂
        /*
        status.setCellFactory(col -> new TableCell<ConnectLeaf, String>() {
            @Override
            protected void updateItem(String readonly, boolean empty) {
                super.updateItem(readonly, empty);
                if (empty || readonly == null) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER);
                    SVGPath statusIcon = new SVGPath();
                    statusIcon.setScaleX(0.5);
                    statusIcon.setScaleY(0.5);
                    statusIcon.setContent("M22.4062 21.0156 L12.6562 2.2656 Q12.5625 2.0781 12.3906 1.9844 Q12.2188 1.875 12 1.875 Q11.7969 1.875 11.6094 1.9844 Q11.4375 2.0781 11.3438 2.2656 L11.3438 2.2656 L1.5938 21.0156 Q1.5469 21.0938 1.5156 21.1875 Q1.5 21.2812 1.5 21.375 Q1.5 21.6719 1.7188 21.9062 Q1.9531 22.125 2.25 22.125 Q2.25 22.125 2.25 22.125 Q2.25 22.125 2.25 22.125 L21.75 22.125 Q21.75 22.125 21.75 22.125 Q21.75 22.125 21.75 22.125 Q22.0625 22.125 22.2812 21.9062 Q22.5 21.6719 22.5 21.375 Q22.5 21.2812 22.4688 21.1875 Q22.4531 21.0938 22.4062 21.0156 L22.4062 21.0156 ZM11.1562 7.875 L12.8438 7.875 L12.8438 15.375 L11.1562 15.375 L11.1562 7.875 ZM12 19.875 Q11.5312 19.875 11.2031 19.5469 Q10.875 19.2188 10.875 18.75 Q10.875 18.2812 11.2031 17.9531 Q11.5312 17.625 12 17.625 Q12.4688 17.625 12.7969 17.9531 Q13.125 18.2812 13.125 18.75 L13.125 18.75 Q13.125 19.2188 12.7969 19.5469 Q12.4688 19.875 12 19.875 L12 19.875 Z");
                    statusIcon.setFill(Color.valueOf("#9f453c"));
                    hbox.getChildren().addAll(new Group(statusIcon));

                    SVGPath checkedIcon = new SVGPath();
                    checkedIcon.setScaleX(0.4);
                    checkedIcon.setScaleY(0.4);
                    checkedIcon.setContent("M12 0 Q10.7188 0 7.5938 0.7969 Q5.6875 1.3594 3.2812 2.1562 Q2.6406 2.3125 2.2031 2.8438 Q1.7656 3.3594 1.6875 4 Q1.125 8.7188 2.3125 12.7969 Q3.2812 16.2344 5.4375 19.0469 Q7.0469 21.125 9.2031 22.7188 Q10 23.2812 10.75 23.6406 Q11.5156 24 12 24 Q12.4844 24 13.2812 23.6094 Q14.0781 23.2031 14.7969 22.7188 Q16.9531 21.125 18.5625 19.0469 Q20.7188 16.2344 21.6875 12.7969 Q22.875 8.7188 22.3125 4 Q22.2344 3.3594 21.7969 2.8438 Q21.3594 2.3125 20.7188 2.1562 Q18.5625 1.4375 16.4062 0.7969 Q13.2812 0 12 0 ZM15.2031 7.6875 Q15.4375 7.5156 15.75 7.5156 Q16.0781 7.5156 16.2812 7.7188 Q16.4844 7.9219 16.4844 8.25 Q16.4844 8.5625 16.3125 8.7969 L11.7656 13.2812 Q11.5938 13.5156 11.2656 13.5156 Q10.9531 13.5156 10.7188 13.2812 L8.4844 11.0469 Q8.2344 10.7969 8.2344 10.4844 Q8.2344 10.1562 8.4688 9.9688 Q8.7188 9.7656 9 9.7656 Q9.2812 9.7656 9.5156 10 L11.2812 11.6875 L15.2031 7.6875 Z");
                    checkedIcon.setFill(Color.valueOf("#074675"));
                    hbox.getChildren().addAll(new Label(" "),new Group(checkedIcon));
                    setGraphic(hbox);
                }
            }
        });

         */


        name.setCellFactory(col -> new CustomTableCell<>());
        dbtype.setCellFactory(col -> new CustomTableCell<>());
        dbversion.setCellFactory(col -> new CustomTableCell<>());
        ip.setCellFactory(col -> new CustomTableCell<>());
        port.setCellFactory(col -> new CustomTableCell<>());
        database.setCellFactory(col -> new CustomTableCell<>());
        username.setCellFactory(col -> new CustomTableCell<>());
        status.setCellFactory(col -> new CustomTableCell<>());


        getColumns().addAll(name, dbtype,dbversion, ip, port,database, username, status);
        //禁止拖动列
        name.setReorderable(false);
        dbtype.setReorderable(false);
        dbversion.setReorderable(false);
        ip.setReorderable(false);
        database.setReorderable(false);
        port.setReorderable(false);
        username.setReorderable(false);
        status.setReorderable(false);

        //设置列宽占满整个容器
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        /*
                        instance_info_tableview.setOnMouseClicked(event -> {
                            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                                instance_info_tableview.edit(instance_info_tableview.getSelectionModel().getSelectedIndex(), name);
                            }
                        });

                         */

        //监听行变化
        getSelectionModel().selectedItemProperty().addListener((obs1, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 当前选中的行数据
                //Connect connect = newSelection;
            }
        });

//连接列表行上右键
        //ContextMenu contextMenu = new ContextMenu();

        MenuItem dataManager = new MenuItem("新建SQL(New SQL Command)");
        SVGPath dataManagerIcon = new SVGPath();
        dataManagerIcon.setContent("M12 9.7656 Q12.3125 9.7656 12.5156 9.9688 Q12.7188 10.1562 12.7188 10.4844 L12.7188 12.7188 L15.0469 12.7188 Q15.2812 12.7188 15.5156 12.9688 Q15.7656 13.2031 15.7656 13.5312 Q15.7656 13.8438 15.5156 14.0469 Q15.2812 14.2344 15.0469 14.2344 L12.7188 14.2344 L12.7188 16.4844 Q12.7188 16.7969 12.5156 17.0469 Q12.3125 17.2812 12 17.2812 Q11.6875 17.2812 11.4844 17.0469 Q11.2812 16.7969 11.2812 16.4844 L11.2812 14.2344 L9.0469 14.2344 Q8.7188 14.2344 8.4688 14.0469 Q8.2344 13.8438 8.2344 13.5312 Q8.2344 13.2031 8.4688 12.9688 Q8.7188 12.7188 9.0469 12.7188 L11.2812 12.7188 L11.2812 10.4844 Q11.2812 10.1562 11.4844 9.9688 Q11.6875 9.7656 12 9.7656 ZM21.0469 6.7188 L21.0469 20.9531 Q21.0469 22.2344 20.1562 23.125 Q19.2812 24 18 24 L6 24 Q4.7188 24 3.8281 23.125 Q2.9531 22.2344 2.9531 21.0469 L2.9531 2.9531 Q3.0469 1.7656 3.875 0.8906 Q4.7188 0 6 0 L14.2344 0 L21.0469 6.7188 ZM16.4844 6.7188 Q15.5938 6.7188 14.9062 6.0781 Q14.2344 5.4375 14.2344 4.4844 L14.2344 1.5156 L6 1.5156 Q5.3594 1.5156 4.9219 1.9688 Q4.4844 2.4062 4.4844 2.9531 L4.4844 20.9531 Q4.4844 21.5938 4.9219 22.0469 Q5.3594 22.4844 6 22.4844 L18 22.4844 Q18.6406 22.4844 19.0781 22.0469 Q19.5156 21.5938 19.5156 20.9531 L19.5156 6.7188 L16.4844 6.7188 Z");
        dataManagerIcon.setScaleX(0.6);
        dataManagerIcon.setScaleY(0.55);
        dataManagerIcon.setFill(Color.valueOf("#074675"));
        dataManager.setGraphic(new Group(dataManagerIcon));


        SVGPath connectInfoItemIcon = new SVGPath();
        connectInfoItemIcon.setContent("M10.5 7.125 Q10.5 6.6562 10.8281 6.3281 Q11.1562 6 11.625 6 L12.375 6 Q12.8438 6 13.1719 6.3281 Q13.5 6.6562 13.5 7.125 L13.5 7.875 Q13.5 8.3438 13.1719 8.6719 Q12.8438 9 12.375 9 L11.625 9 Q11.1562 9 10.8281 8.6719 Q10.5 8.3438 10.5 7.875 L10.5 7.125 ZM15 18 L9 18 L9 16.5 L10.5 16.5 L10.5 12 L9 12 L9 10.5 L13.5 10.5 L13.5 16.5 L15 16.5 L15 18 ZM12 0 Q9.5156 0 7.3281 0.9375 Q5.1406 1.875 3.5 3.5 Q1.875 5.125 0.9375 7.3281 Q0 9.5156 0 12 Q0 14.4844 0.9375 16.6719 Q1.875 18.8594 3.5 20.5 Q5.1406 22.125 7.3281 23.0625 Q9.5156 24 12 24 Q14.4844 24 16.6719 23.0625 Q18.875 22.125 20.5 20.5 Q22.125 18.8594 23.0625 16.6719 Q24 14.4844 24 12 Q24 9.5156 23.0625 7.3281 Q22.125 5.125 20.5 3.5 Q18.875 1.875 16.6719 0.9375 Q14.4844 0 12 0 ZM12 21.75 Q9.9844 21.75 8.2031 20.9844 Q6.4219 20.2188 5.0938 18.9062 Q3.7812 17.5781 3.0156 15.7969 Q2.25 14.0156 2.25 12 Q2.25 9.9844 3.0156 8.2031 Q3.7812 6.4219 5.0938 5.0938 Q6.4219 3.7656 8.2031 3.0156 Q9.9844 2.25 12 2.25 Q14.0156 2.25 15.7969 3.0156 Q17.5781 3.7656 18.9062 5.0938 Q20.2344 6.4219 20.9844 8.2031 Q21.75 9.9844 21.75 12 Q21.75 14.0156 20.9844 15.7969 Q20.2344 17.5781 18.9062 18.9062 Q17.5781 20.2188 15.7969 20.9844 Q14.0156 21.75 12 21.75 Z");
        connectInfoItemIcon.setScaleX(0.55);
        connectInfoItemIcon.setScaleY(0.55);
        connectInfoItemIcon.setFill(Color.valueOf("#074675"));
        SVGPath healthCheckItemIcon = new SVGPath();
        healthCheckItemIcon.setScaleX(0.65);
        healthCheckItemIcon.setScaleY(0.65);
        healthCheckItemIcon.setContent("M17.7656 5.8594 Q18 6.0938 17.7656 6.375 L7.9688 16.1719 Q7.8281 16.3125 7.6875 16.3125 L7.6875 16.3125 Q7.5469 16.3125 7.4062 16.1719 L3.6094 12.4219 Q3.4688 12.2812 3.4688 12.1406 Q3.4688 12 3.6094 11.8594 L5.6719 9.7969 Q5.9531 9.5156 6.2344 9.7969 L7.6875 11.25 L15.1406 3.75 Q15.2344 3.6562 15.4219 3.6562 Q15.6094 3.6562 15.7031 3.75 L17.7656 5.8594 ZM12.9844 12.8906 L15.375 10.4531 L15.375 19.125 Q15.375 19.5938 15 19.9688 Q14.625 20.3438 14.1562 20.3438 L14.1094 20.3438 L14.1094 20.3438 L1.2188 20.3438 L1.2188 20.3438 L1.2188 20.3438 Q0.75 20.3438 0.375 19.9688 Q0 19.5938 0 19.125 L0 6.1875 Q0 5.7188 0.375 5.3438 Q0.75 4.9688 1.2188 4.9688 L1.2656 4.9688 L12.2344 4.9688 L9.8438 7.3594 L2.3906 7.3594 L2.3906 17.9531 L12.9844 17.9531 L12.9844 12.8906 Z");
        healthCheckItemIcon.setFill(Color.valueOf("#074675"));
        SVGPath onlinelogItemIcon = new SVGPath();
        onlinelogItemIcon.setScaleX(0.5);
        onlinelogItemIcon.setScaleY(0.5);
        onlinelogItemIcon.setContent("M22.4844 14.9688 L3.75 15 Q3.4531 15 3.2188 15.2188 Q3 15.4375 3 15.7656 Q3 16.0781 3.2188 16.2969 Q3.4531 16.5 3.75 16.5 L13.5 16.5 L13.5 18 L3.75 18 Q3.4531 18 3.2188 18.2188 Q3 18.4375 3 18.75 Q3 19.0469 3.2188 19.2812 Q3.4531 19.5 3.75 19.5 L13.5 19.5 L13.5 23.9688 L3 23.9688 Q2.375 23.9688 1.9375 23.5469 Q1.5 23.1094 1.5 22.4688 L1.5 1.5 Q1.5 0.8594 1.9375 0.4375 Q2.375 0 3 0 L20.9844 0 Q21.6094 0 22.0469 0.4375 Q22.4844 0.8594 22.4844 1.5 L22.4844 14.9531 L22.4844 14.9688 ZM20.2344 3.0156 L3.75 3.0156 Q3.4531 3.0156 3.2188 3.2344 Q3 3.4375 3 3.7656 Q3 4.0781 3.2188 4.2969 Q3.4531 4.5156 3.75 4.5156 L20.2344 4.5156 Q20.5625 4.5156 20.7656 4.2969 Q20.9844 4.0781 20.9844 3.7656 Q20.9844 3.4375 20.7656 3.2344 Q20.5625 3.0156 20.2344 3.0156 ZM20.2344 6.0156 L3.75 6.0156 Q3.4531 6.0156 3.2188 6.2344 Q3 6.4375 3 6.7656 Q3 7.0781 3.2188 7.2969 Q3.4531 7.5156 3.75 7.5156 L20.2344 7.5156 Q20.5625 7.5156 20.7656 7.2969 Q20.9844 7.0781 20.9844 6.7656 Q20.9844 6.4375 20.7656 6.2344 Q20.5625 6.0156 20.2344 6.0156 ZM20.2344 9 L3.75 9 Q3.4531 9 3.2188 9.2188 Q3 9.4375 3 9.7656 Q3 10.0781 3.2188 10.2969 Q3.4531 10.5 3.75 10.5 L20.2344 10.5 Q20.5625 10.5 20.7656 10.2969 Q20.9844 10.0781 20.9844 9.7656 Q20.9844 9.4375 20.7656 9.2188 Q20.5625 9 20.2344 9 ZM20.2344 12 L3.75 12 Q3.4531 12 3.2188 12.2188 Q3 12.4375 3 12.7656 Q3 13.0781 3.2188 13.2969 Q3.4531 13.5 3.75 13.5 L20.2344 13.5 Q20.5625 13.5 20.7656 13.2969 Q20.9844 13.0781 20.9844 12.7656 Q20.9844 12.4375 20.7656 12.2188 Q20.5625 12 20.2344 12 ZM22.0312 17.5469 L16.0312 23.5781 Q15.6094 24 15 24 L15 16.4688 L22.4844 16.4688 Q22.4844 17.1094 22.0312 17.5469 Z");
        onlinelogItemIcon.setFill(Color.valueOf("#074675"));
        SVGPath spaceManagerItemIcon = new SVGPath();
        spaceManagerItemIcon.setScaleX(0.55);
        spaceManagerItemIcon.setScaleY(0.55);
        spaceManagerItemIcon.setContent("M2.0156 20.0156 L2.0156 15.9844 L21.9844 15.9844 L21.9844 20.0156 L2.0156 20.0156 ZM3.9844 17.0156 L3.9844 18.9844 L6 18.9844 L6 17.0156 L3.9844 17.0156 ZM2.0156 3.9844 L21.9844 3.9844 L21.9844 8.0156 L2.0156 8.0156 L2.0156 3.9844 ZM6 6.9844 L6 5.0156 L3.9844 5.0156 L3.9844 6.9844 L6 6.9844 ZM2.0156 14.0156 L2.0156 9.9844 L21.9844 9.9844 L21.9844 14.0156 L2.0156 14.0156 ZM3.9844 11.0156 L3.9844 12.9844 L6 12.9844 L6 11.0156 L3.9844 11.0156 Z");
        spaceManagerItemIcon.setFill(Color.valueOf("#074675"));
        SVGPath onconfigItemIcon = new SVGPath();
        onconfigItemIcon.setScaleX(0.55);
        onconfigItemIcon.setScaleY(0.55);
        onconfigItemIcon.setContent("M12.7031 14.4297 Q13.7188 13.4141 13.7188 12.0078 Q13.7188 10.5859 12.7031 9.5859 Q11.7031 8.5703 10.2812 8.5703 Q8.8594 8.5703 7.8594 9.5859 Q6.8594 10.5859 6.8594 12.0078 Q6.8594 13.4141 7.8594 14.4297 Q8.8594 15.4297 10.2812 15.4297 Q11.7031 15.4297 12.7031 14.4297 ZM20.5781 10.5391 L20.5781 13.5078 Q20.5781 13.6797 20.4688 13.8203 Q20.3594 13.9609 20.2031 13.9922 L17.7188 14.3672 Q17.4688 15.0859 17.2031 15.5859 Q17.6719 16.2578 18.625 17.4297 Q18.7656 17.6016 18.7656 17.7734 Q18.7656 17.9453 18.6406 18.0859 Q18.2812 18.5703 17.3125 19.5234 Q16.3594 20.4766 16.0625 20.4766 Q15.8906 20.4766 15.7031 20.3516 L13.8594 18.9141 Q13.2656 19.2109 12.6406 19.4141 Q12.4219 21.2422 12.25 21.9141 Q12.1562 22.2891 11.7656 22.2891 L8.7969 22.2891 Q8.6094 22.2891 8.4688 22.1641 Q8.3281 22.0547 8.3125 21.8828 L7.9375 19.4141 Q7.2812 19.2109 6.7344 18.9297 L4.8438 20.3516 Q4.7188 20.4766 4.5156 20.4766 Q4.3281 20.4766 4.1719 20.3359 Q2.4844 18.8047 1.9688 18.0859 Q1.875 17.9453 1.875 17.7734 Q1.875 17.6172 1.9844 17.4609 Q2.1875 17.1797 2.6562 16.5703 Q3.1406 15.9609 3.3906 15.6328 Q3.0312 14.9609 2.8438 14.3047 L0.3906 13.9453 Q0.2188 13.9141 0.1094 13.7734 Q0 13.6328 0 13.4609 L0 10.4922 Q0 10.3203 0.1094 10.1797 Q0.2188 10.0234 0.3594 10.0078 L2.8594 9.6328 Q3.0469 9.0078 3.375 8.3984 Q2.8438 7.6328 1.9375 6.5547 Q1.8125 6.3828 1.8125 6.2266 Q1.8125 6.0859 1.9219 5.9141 Q2.2812 5.4297 3.25 4.4766 Q4.2188 3.5234 4.5156 3.5234 Q4.6875 3.5234 4.8594 3.6484 L6.7031 5.0859 Q7.2969 4.7734 7.9219 4.5859 Q8.1406 2.7578 8.3125 2.0859 Q8.4062 1.7109 8.7969 1.7109 L11.7656 1.7109 Q11.9531 1.7109 12.0938 1.8359 Q12.2344 1.9453 12.25 2.1172 L12.625 4.5859 Q13.2812 4.7891 13.8281 5.0703 L15.7344 3.6484 Q15.8594 3.5234 16.0625 3.5234 Q16.2344 3.5234 16.3906 3.6484 Q18.125 5.2422 18.6094 5.9297 Q18.7031 6.0391 18.7031 6.2266 Q18.7031 6.3828 18.5938 6.5391 Q18.3906 6.8203 17.9062 7.4297 Q17.4219 8.0391 17.1875 8.3672 Q17.5312 9.0391 17.7344 9.6797 L20.1875 10.0547 Q20.3594 10.0859 20.4688 10.2266 Q20.5781 10.3672 20.5781 10.5391 Z");
        onconfigItemIcon.setFill(Color.valueOf("#074675"));
        SVGPath instanceStopItemIcon = new SVGPath();
        instanceStopItemIcon.setScaleX(0.65);
        instanceStopItemIcon.setScaleY(0.65);
        instanceStopItemIcon.setContent("M19.2031 6.0078 L19.2031 17.7734 Q19.2031 18.3516 18.7812 18.7734 Q18.3594 19.1953 17.7656 19.1953 L6 19.1953 Q5.5156 19.1953 5.1562 18.8516 Q4.8125 18.4922 4.8125 18.0078 L4.8125 6.2422 Q4.8125 5.6484 5.2344 5.2266 Q5.6562 4.8047 6.2344 4.8047 L18 4.8047 Q18.5 4.8047 18.8438 5.1641 Q19.2031 5.5078 19.2031 6.0078 L19.2031 6.0078 Z");
        instanceStopItemIcon.setFill(Color.valueOf("#9f453c"));

        MenuItem connectInfoItem = new MenuItem("实例信息 ( Instance Info )",new Group(connectInfoItemIcon));
        MenuItem healthCheckItem = new MenuItem("一键巡检 ( Health Check )",new Group(healthCheckItemIcon));
        MenuItem onlinelogItem = new MenuItem("运行日志 ( online.log )",new Group(onlinelogItemIcon));
        MenuItem spaceManagerItem = new MenuItem("容量管理 ( DBSpace Manager )",new Group(spaceManagerItemIcon));
        MenuItem onconfigItem = new MenuItem("参数管理 ( ONCONFIG )",new Group(onconfigItemIcon));
        MenuItem instanceStopItem = new MenuItem("实例启停 ( Start & Stop )",new Group(instanceStopItemIcon));

        ContextMenu contextMenu =getContextMenu();
        MenuItem copyItem= contextMenu.getItems().get(0);
        contextMenu.getItems().remove(copyItem);
        contextMenu.getItems().add(dataManager);
        contextMenu.getItems().add(connectInfoItem);

        contextMenu.getItems().add(healthCheckItem);
        contextMenu.getItems().add(onlinelogItem);
        contextMenu.getItems().add(spaceManagerItem);
        contextMenu.getItems().add(onconfigItem);
        contextMenu.getItems().add(instanceStopItem);
        contextMenu.getItems().add(copyItem);
        contextMenu.setOnShowing(e->{
            Connect connect = (Connect)getSelectionModel().getSelectedItem();
            if(connect!=null&&!connect.getUsername().equals("gbasedbt"))
            {
                healthCheckItem.setDisable(true);
                onlinelogItem.setDisable(true);
                spaceManagerItem.setDisable(true);
                onconfigItem.setDisable(true);
                instanceStopItem.setDisable(true);
            }
            else{
                healthCheckItem.setDisable(false);
                onlinelogItem.setDisable(false);
                spaceManagerItem.setDisable(false);
                onconfigItem.setDisable(false);
                instanceStopItem.setDisable(false);
            }
        });

        connectInfoItem.setOnAction(event->{
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomInstanceTab(rowData,0);
        });


        healthCheckItem.setOnAction(event->{
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomInstanceTab(rowData,1);
        });

        onlinelogItem.setOnAction(event->{
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomInstanceTab(rowData,2);
        });

        spaceManagerItem.setOnAction(event->{
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomInstanceTab(rowData,3);
        });
        onconfigItem.setOnAction(event->{
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomInstanceTab(rowData,4);
        });

        instanceStopItem.setOnAction(event->{
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomInstanceTab(rowData,5);
        });

        // 设置表格的上下文菜单策略
        setContextMenu(contextMenu);
        /*
        instInfo.setOnAction(event -> {
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
            //MetadataTreeviewUtil.showInstanceInfo(rowData);
        });

         */

        dataManager.setOnAction(event -> {
            Connect rowData = (Connect)getSelectionModel().getSelectedItem();
            if(rowData!=null)
                TabpaneUtil.addCustomSqlTab(rowData);
        });

        setRowFactory(tv -> {
            TableRow<Connect> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    Connect rowData = row.getItem();
                    TabpaneUtil.addCustomInstanceTab(rowData,0);
                }
            });
            return row;
        });
    }
}
