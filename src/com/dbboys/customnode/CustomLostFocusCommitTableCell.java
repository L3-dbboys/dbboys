package com.dbboys.customnode;

import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CustomLostFocusCommitTableCell<S,T> extends CustomTableCell<S, T> {
    @Override
    public void commitEdit(T item) {
        // This block is necessary to support commit on losing focus, because
        // the baked-in mechanism sets our editing state to false before we can
        // intercept the loss of focus. The default commitEdit(...) method
        // simply bails if we are not editing...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(
                        table, new TablePosition<S,T>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item
                );
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);
    }


    @Override
    public void startEdit() {
        // 先调用父类的startEdit()，确保初始化编辑框（textField）
        super.startEdit();

        // 父类中已通过createTextField()初始化textField，这里直接获取并绑定焦点监听
        if (getTextField() != null) {
            // 为文本框添加焦点监听（注意：避免重复添加，利用父类textField的唯一性）
            getTextField().focusedProperty().addListener((obs, oldFocus, newFocus) -> {
                // 条件：焦点丢失 且 当前处于编辑状态
                if (!newFocus ) {
                    commitEdit((T) getTextField().getText());
                }
            });

             // 补充：按ESC键取消编辑（可选，提升交互体验）
            getTextField().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                    event.consume();
                }
            });
        }
    }

    // 为了访问父类的私有textField，需要在父类中添加getter方法（关键！）
    // 注意：这一步需要修改CustomTableCell类，添加textField的getter
    private CustomUserTextField getTextField() {
        return super.textField; // 直接访问父类的textField（需父类允许访问，见下方说明）
    }
}
