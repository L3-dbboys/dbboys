package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.vo.UpdateResult;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
//只有执行批量sql或者非查询的sql使用此类，以标记不同结果行的颜色，表的查询结果集使用CustomTableCell
public class CustomResultsetTableCell<String,Object> extends CustomTableCell<String,Object> {
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll("execute_result_error", "execute_result_ignore");
        if (!empty)

        try {
            if (getTableRow() != null && getTableRow().getItem() != null && ((UpdateResult) getTableRow().getItem()).getResult() != null) {
                if (!((UpdateResult) getTableRow().getItem()).getResult().substring(0, 4).equals("执行成功")) {
                    //setStyle("-fx-text-fill: red");
                    getStyleClass().add("execute_result_error");
                }
                //else if (((UpdateResult) getTableRow().getItem()).getResult().substring(0, 4).equals("忽略执行")) {
                    //setStyle("-fx-text-fill: #aaa");
                  //  getStyleClass().add("execute_result_ignore");
                //}

            }
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

}
