package com.dbboys.customnode;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;

public class CustomResultsetTableView extends TableView{
    public CustomResultsetTableView() {
        super();
        TableColumn<ObservableList<String>, Object> rowNumberCol = new TableColumn<>("");;
        rowNumberCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ObservableList<String>, Object> call(TableColumn<ObservableList<String>, Object> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null); // 空行不显示行号
                        } else {
                            setText(String.valueOf(getIndex() + 1)); // 行号从 1 开始
                            setStyle("-fx-background-color: #f2f2f2;-fx-text-fill: black");
                            setOnMouseClicked(event -> {
                                int rowIndex = getIndex();
                                getSelectionModel().clearAndSelect(rowIndex);
                            });
                        }
                    }
                };
            }
        });

        SVGPath rowNumberColIcon = new SVGPath();
        rowNumberColIcon.setScaleX(0.65);
        rowNumberColIcon.setScaleY(0.65);
        rowNumberColIcon.setContent("M11.9062 6.5547 L10.4375 6.5547 Q10.3594 6.5547 10.3281 6.6016 Q10.2969 6.6484 10.2969 6.6953 L10.2969 14.6641 L10.1562 14.6641 L4.625 6.6172 Q4.625 6.5703 4.5781 6.5703 Q4.5469 6.5547 4.5312 6.5547 L2.9531 6.5547 Q2.9062 6.5547 2.8594 6.6016 Q2.8125 6.6484 2.8125 6.6953 L2.8125 17.5703 Q2.8125 17.6172 2.8594 17.6641 Q2.9062 17.7109 2.9531 17.7109 L4.4375 17.7109 Q4.4844 17.7109 4.5312 17.6641 Q4.5781 17.6172 4.5781 17.5703 L4.5781 9.4766 L4.6875 9.4766 L10.2969 17.6328 Q10.2969 17.6797 10.3281 17.6953 Q10.3594 17.7109 10.4062 17.7109 L11.9062 17.7109 Q11.9531 17.7109 12 17.6641 Q12.0469 17.6172 12.0469 17.5703 L12.0469 6.6953 Q12.0469 6.6484 12 6.6016 Q11.9531 6.5547 11.9062 6.5547 L11.9062 6.5547 ZM20.7656 16.2266 L13.6406 16.2266 Q13.5781 16.2266 13.5156 16.2891 Q13.4531 16.3516 13.4531 16.4141 L13.4531 17.5391 Q13.4531 17.6172 13.5156 17.6797 Q13.5781 17.7266 13.6406 17.7266 L20.7656 17.7266 Q20.8438 17.7266 20.8906 17.6797 Q20.9531 17.6172 20.9531 17.5391 L20.9531 16.4141 Q20.9531 16.3516 20.8906 16.2891 Q20.8438 16.2266 20.7656 16.2266 ZM17.2031 14.7578 Q18.125 14.7578 18.875 14.4609 Q19.625 14.1484 20.1562 13.5391 Q20.6719 12.9766 20.9219 12.2109 Q21.1875 11.4453 21.1875 10.5078 Q21.1875 9.6016 20.9219 8.8359 Q20.6719 8.0703 20.1562 7.4922 Q19.625 6.8828 18.875 6.5859 Q18.1406 6.2734 17.2031 6.2734 Q16.2656 6.2734 15.5156 6.5859 Q14.7656 6.8828 14.25 7.5078 Q13.7344 8.1016 13.4688 8.8516 Q13.2188 9.6016 13.2188 10.5078 Q13.2188 11.4453 13.4688 12.1953 Q13.7344 12.9453 14.25 13.5391 Q14.7969 14.1484 15.5312 14.4609 Q16.2656 14.7578 17.2031 14.7578 L17.2031 14.7578 ZM15.5156 8.5703 Q15.8281 8.2109 16.2344 8.0234 Q16.6406 7.8359 17.2031 7.8359 Q17.75 7.8359 18.1719 8.0234 Q18.5938 8.1953 18.875 8.5234 Q19.1562 8.8984 19.2969 9.3984 Q19.4531 9.8828 19.4531 10.5078 Q19.4531 11.1641 19.2969 11.6484 Q19.1562 12.1328 18.875 12.4766 Q18.5625 12.8359 18.1562 13.0078 Q17.75 13.1797 17.2031 13.1797 Q16.6406 13.1797 16.2344 12.9922 Q15.8281 12.8047 15.5156 12.4609 Q15.2344 12.1016 15.0938 11.6328 Q14.9531 11.1484 14.9531 10.5391 Q14.9531 9.8984 15.0938 9.4141 Q15.2344 8.9141 15.5156 8.5703 L15.5156 8.5703 Z");
        rowNumberColIcon.setFill(Color.valueOf("#074675"));
        rowNumberCol.setGraphic(rowNumberColIcon);


        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE);
        getSelectionModel().setCellSelectionEnabled(true);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        rowNumberCol.setSortable(false);
        rowNumberCol.setPrefWidth(30);
        getColumns().add(rowNumberCol);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("复制 ( Copy )                                Ctrl+C");
        copyItem.setOnAction(e -> copySelectionToClipboard(this));
        contextMenu.getItems().add(copyItem);
        setContextMenu(contextMenu);

        SVGPath menu_copy_icon = new SVGPath();
        menu_copy_icon.setScaleX(0.7);
        menu_copy_icon.setScaleY(0.7);
        menu_copy_icon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        menu_copy_icon.setFill(Color.valueOf("#074675"));
        copyItem.setGraphic(new Group(menu_copy_icon));

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                copySelectionToClipboard(this);
                event.consume();
            }
        });

        contextMenu.setOnShowing(e -> {
            boolean hasSelection = !getSelectionModel().getSelectedCells().isEmpty();
            copyItem.setDisable(!hasSelection); // 没有选中则禁用
        });
    }

    private <T> void copySelectionToClipboard(TableView<T> table) {
        ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();
        if (posList.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        int prevRow = -1;

        for (int i = 0; i < posList.size(); i++) {
            TablePosition pos = posList.get(i);
            int row = pos.getRow();
            int col = pos.getColumn();

            Object cell = table.getColumns().get(col).getCellData(row);
            if (cell == null) cell = "";

            if (prevRow == row) {
                sb.append('\t');
            } else if (prevRow != -1) {
                sb.append('\n');
            }

            sb.append(cell.toString());
            prevRow = row;
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
