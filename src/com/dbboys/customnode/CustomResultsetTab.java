package com.dbboys.customnode;

import com.dbboys.ctrl.ResultSetTabController;
import com.dbboys.vo.Connect;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class CustomResultsetTab extends Tab {
    public ResultSetTabController resultSetTabController;
    public VBox resultset_vbox;

    public CustomResultsetTab(Connect SQLConnect, StackPane sql_execute_process_stackpane) throws IOException {
        //设置标题保证标题溢出下拉正常显示标题

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dbboys/fxml/ResultSetTab.fxml"));

        loader.setControllerFactory(clazz -> {
            if (clazz == ResultSetTabController.class) {
                return new ResultSetTabController(SQLConnect,sql_execute_process_stackpane);
            } else {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        resultset_vbox=loader.load();
        setContent(resultset_vbox);
        resultSetTabController=loader.getController();
        resultSetTabController.lastsql_refresh_button.setVisible(false);
        setStyle("-fx-pref-height: 18;");
        setClosable(true);
        //关闭窗口事件响应
        setOnCloseRequest(event -> {

            //似乎只有隐式的结果集需要关闭后才能断开连接，显示声明的结果集可不关闭，连接关闭时自动关闭
            if(resultSetTabController.sql_resultset!=null) {
                try {
                    resultSetTabController.sql_resultset.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


}
