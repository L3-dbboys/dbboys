package com.dbboys.customnode;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CustomTableCell<S,T> extends TableCell<S,T> {
    CustomUserTextField textField;
    //结果集拖动鼠标框选
    private static Integer result_select_row1=0;
    private static Integer result_select_row2=0;
    private static Integer result_select_col1=0;
    private static Integer result_select_col2=0;
    private static Integer result_select_startRow=0;
    private static Integer result_select_endRow=0;
    private static Integer result_select_startCol=0;
    private static Integer result_select_endCol=0;
    private static long lastClickTime = 0;



    @Override
    public void startEdit() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 300) {
            // 如果两次点击间隔小于300ms，认为是双击
            super.startEdit();
            if (textField == null) {
                createTextField();
            }
            Platform.runLater(() -> {
                textField.positionCaret(textField.getText().length());
            });
            setText(null);
            setGraphic(textField);
            textField.setText(getItem()==null?"[NULL]":getItem().toString().replaceAll("\n","\u21B5"));
            textField.requestFocus();
        }
        lastClickTime = currentTime;

    }


    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(getItem()==null?"[NULL]":getItem().toString().replaceAll("\n","\u21B5"));
        setGraphic(null);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText("");
        setStyle("");
        setTooltip(null);
        if (empty) {
        } else if(item==null){
            setText("[NULL]");
            setStyle("-fx-text-fill: #ddd");
        }else if (isEditing()) {
            if (textField != null) {
                textField.setText(item.toString());
            }
            setText(null);
            setGraphic(textField);
        }
        else{
            //text.setText(item.toString()); // 设置带换行符的内容
            //setGraphic(text);
            setText(item.toString().replaceAll("\n","\u21B5"));
            //setTooltip(new Tooltip(item.toString().replaceAll("\u21B5","\n")));
            setGraphic(null);
        }

    }




    private void createTextField() {
        textField = new CustomUserTextField();
        textField.setText(getItem()==null?"[NULL]":getItem().toString());
        textField.setStyle("-fx-background-color: #2871a8;-fx-border-width: 0;-fx-padding: 0;-fx-text-fill: white");
        textField.setOnAction(event -> {
            commitEdit((T) textField.getText());
        });

        /*

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEdit((Object) textField.getText().toString());
            }
        });

         */




    }

    public CustomTableCell() {
        setOnDragDetected(event -> {
            startFullDrag(); // 不执行任何拖动任务
            getScene().setCursor(Cursor.CROSSHAIR);
            result_select_row1=getIndex();
            result_select_col1=getTableView().getColumns().indexOf(getTableColumn());
            event.consume();
        });

        setOnMouseDragEntered(event -> {
            result_select_row2=getIndex();
            result_select_col2=getTableView().getColumns().indexOf(getTableColumn());
            result_select_startRow=Math.min(result_select_row1,result_select_row2);
            result_select_startCol=Math.min(result_select_col1,result_select_col2);
            result_select_endRow=Math.max(result_select_row1,result_select_row2);
            result_select_endCol=Math.max(result_select_col1,result_select_col2);
            getTableView().getSelectionModel().clearSelection();
            for(int i=result_select_startRow;i<=result_select_endRow;i++)
            {
                for(int j=result_select_startCol;j<=result_select_endCol;j++){
                    getTableView().getSelectionModel().select(i,getTableView().getColumns().get(j));
                }
            }
        });

        setOnMouseReleased(event -> {
            getScene().setCursor(Cursor.DEFAULT);
            event.consume(); // 消费事件
        });
        // 单击更新点击事件，用于下次单击时判断两次单击事件间隔，来确定是否进入编辑模式，在startedit里判断
        setOnMouseClicked(event -> {
            lastClickTime = System.currentTimeMillis();
        });


        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                // 可选择是否消费（阻止父节点响应）
                event.consume();
            }
        });

    }

}
