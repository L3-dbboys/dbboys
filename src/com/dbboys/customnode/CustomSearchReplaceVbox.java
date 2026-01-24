package com.dbboys.customnode;

import com.dbboys.app.Main;
import com.dbboys.util.NotificationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.model.TwoDimensional;

public class CustomSearchReplaceVbox extends VBox {
    public GenericStyledArea codeArea;
    public final CustomUserTextField findField;
    public final CustomUserTextField replaceField;
    private final ToggleButton  caseToggle;
    public final Button tobottomBtn = new Button();
    public final Button totopBtn = new Button();
    private final Label statusLabel;
    private int lastFindPosition = -1;



    // 构造方法：接收要绑定的CodeArea
    public CustomSearchReplaceVbox(StyledTextArea codeArea) {
        this.codeArea = codeArea;
        this.findField = new CustomUserTextField();
        this.replaceField = new CustomUserTextField();
        this.statusLabel = new Label("");
        this.caseToggle = new ToggleButton();


        initUI();
        initEvents();
        setVisible(false);

        findField.setOnKeyPressed(event -> {
            if(event.isControlDown()&&event.getCode() == KeyCode.ENTER){
                findNext();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.R){
                if(tobottomBtn.isVisible()){
                    tobottomBtn.fire();
                    replaceField.requestFocus();
                }else{
                    totopBtn.fire();
                    findField.requestFocus();
                }
            }
        });

        replaceField.setOnKeyPressed(event -> {
            if(event.isControlDown()&&event.getCode() == KeyCode.ENTER){
                //findNext();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.R){
                    totopBtn.fire();
                    findField.requestFocus();

            }
        });
    }



    // 初始化UI布局
    private void initUI() {
        // 基本样式设置
        //setSpacing(10);
        setPadding(new Insets(2));
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd;-fx-border-width: 0.5");
        setPrefWidth(300);

        // 查找区域
        HBox findBox = new HBox(5);
        findBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(findField, Priority.ALWAYS);

        StackPane buttonPane = new StackPane();
        tobottomBtn.setFocusTraversable(false);
        SVGPath tobottomBtnIcon = new SVGPath();
        tobottomBtnIcon.setScaleX(0.6);
        tobottomBtnIcon.setScaleY(0.6);
        tobottomBtnIcon.setContent("M6 18.0078 L18 18.0078 L18 19.9922 L6 19.9922 L6 18.0078 ZM11 4.0078 L11 12.5859 L6.7031 8.3047 L5.2969 9.7109 L12 16.4141 L18.7031 9.7109 L17.2969 8.3047 L13.0156 12.5859 L13.0156 4.0078 L11 4.0078 Z");
        tobottomBtnIcon.setFill(Color.valueOf("#074675"));
        tobottomBtn.setGraphic(new Group(tobottomBtnIcon));
        tobottomBtn.getStyleClass().add("little-custom-button");
        buttonPane.getChildren().add(tobottomBtn);
        findField.setStyle("-fx-padding: 1 1 1 5");
        findBox.getChildren().addAll(buttonPane, findField);


        totopBtn.setFocusTraversable(false);
        SVGPath totopBtnIcon = new SVGPath();
        totopBtnIcon.setScaleX(0.6);
        totopBtnIcon.setScaleY(0.6);
        totopBtnIcon.setContent("M6 18.2188 L18 18.2188 L18 20.2031 L6 20.2031 L6 18.2188 ZM12 3.7969 L5.2969 10.5 L6.7031 11.9062 L11 7.625 L11 16.2188 L13.0156 16.2188 L13.0156 7.625 L17.2969 11.9062 L18.7031 10.5 L12 3.7969 Z");
        totopBtnIcon.setFill(Color.valueOf("#074675"));
        totopBtn.setGraphic(new Group(totopBtnIcon));
        totopBtn.getStyleClass().add("little-custom-button");
        totopBtn.visibleProperty().bind(tobottomBtn.visibleProperty().not());
        buttonPane.getChildren().add(totopBtn);

        caseToggle.setFocusTraversable(false);
        findBox.getChildren().add(caseToggle);
        caseToggle.setFocusTraversable(false);
        SVGPath caseToggleIcon = new SVGPath();
        caseToggleIcon.setScaleX(1);
        caseToggleIcon.setScaleY(0.7);
        caseToggleIcon.setContent("M17.25 9.75 L13.5 9.75 L13.5 11.25 L17.25 11.25 L17.25 12.75 L14.25 12.75 Q13.625 12.75 13.1875 13.1875 Q12.75 13.6094 12.75 14.25 L12.75 14.25 L12.75 15.75 Q12.75 16.375 13.1875 16.8125 Q13.625 17.25 14.25 17.25 L14.25 17.25 L18.75 17.25 L18.75 11.25 Q18.75 10.6094 18.3125 10.1875 Q17.8906 9.75 17.25 9.75 L17.25 9.75 ZM17.25 15.75 L14.25 15.75 L14.25 14.25 L17.25 14.25 L17.25 15.75 ZM9.75 6.75 L6.75 6.75 Q6.125 6.75 5.6875 7.1875 Q5.25 7.6094 5.25 8.25 L5.25 8.25 L5.25 17.25 L6.75 17.25 L6.75 13.5 L9.75 13.5 L9.75 17.25 L11.25 17.25 L11.25 8.25 Q11.25 7.6094 10.8125 7.1875 Q10.3906 6.75 9.75 6.75 L9.75 6.75 ZM6.75 12 L6.75 8.25 L9.75 8.25 L9.75 12 L6.75 12 Z");
        caseToggleIcon.setFill(Color.valueOf("#074675"));
        caseToggle.setGraphic(new Group(caseToggleIcon));

        Button findPrevBtn = new Button("");
        findPrevBtn.setFocusTraversable(false);
        SVGPath findPrevBtnIcon = new SVGPath();
        findPrevBtnIcon.setScaleX(0.4);
        findPrevBtnIcon.setScaleY(0.4);
        findPrevBtnIcon.setContent("M12 3.3047 L19.8125 11.1016 L15.6094 11.1016 L15.6094 20.6953 L8.3906 20.6953 L8.3906 11.1016 L4.2031 11.1016 L12 3.3047 L12 3.3047 Z");
        findPrevBtnIcon.setFill(Color.valueOf("#074675"));
        findPrevBtn.setGraphic(new Group(findPrevBtnIcon));
        findPrevBtn.getStyleClass().add("little-custom-button");
        findBox.getChildren().add(findPrevBtn);

        Button findNextBtn = new Button("");
        findNextBtn.setFocusTraversable(false);
        SVGPath findNextBtnIcon = new SVGPath();
        findNextBtnIcon.setScaleX(0.4);
        findNextBtnIcon.setScaleY(0.4);
        findNextBtnIcon.setContent("M12 20.6953 L4.2031 12.8828 L8.3906 12.8828 L8.3906 3.3047 L15.6094 3.3047 L15.6094 12.8828 L19.8125 12.8828 L12 20.6953 L12 20.6953 Z");
        findNextBtnIcon.setFill(Color.valueOf("#074675"));
        findNextBtn.setGraphic(new Group(findNextBtnIcon));
        findNextBtn.getStyleClass().add("little-custom-button");
        findBox.getChildren().add(findNextBtn);

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("searchCloseButton");
        findBox.getChildren().add(closeBtn);

        // 替换区域
        HBox replaceBox = new HBox(5);
        replaceBox.setAlignment(Pos.CENTER_LEFT);
        Label repalceLabel = new Label();
        repalceLabel.setMinWidth(20);
        replaceField.setStyle("-fx-padding: 1 1 1 5");
        replaceBox.getChildren().addAll(repalceLabel, replaceField);
        HBox.setHgrow(replaceField, Priority.ALWAYS);



        // 按钮区域
        HBox buttonBox = new HBox(5);
        Button replaceBtn = new Button();
        replaceBtn.setFocusTraversable(false);
        SVGPath replaceBtnIcon = new SVGPath();
        replaceBtnIcon.setScaleX(0.6);
        replaceBtnIcon.setScaleY(0.6);
        replaceBtnIcon.setContent("M4.8281 5.625 L8.2344 9.0156 L11.5469 5.6719 L10.5156 4.6406 L8.9688 6.1562 L8.9688 3.7656 Q8.9688 3.4531 9.1875 3.2188 Q9.4062 2.9688 9.7188 2.9688 L12 2.9688 L12 1.5 L9.7188 1.5 Q8.8125 1.5312 8.1406 2.1875 Q7.4844 2.8281 7.5156 3.7656 L7.5156 6.1562 L5.875 4.5781 L4.8281 5.625 ZM14.8281 8.3125 L14.8594 8.3125 Q15.2656 9.0156 16.0625 9.0156 Q16.9375 9.0156 17.4688 8.2656 Q18 7.5 18 6.25 Q18 5.1094 17.5469 4.4531 Q17.0938 3.7812 16.25 3.7812 Q15.3438 3.7812 14.8594 4.6406 L14.8281 4.6406 L14.8281 1.5 L13.5156 1.5 L13.5156 8.8906 L14.8281 8.8906 L14.8281 8.3125 ZM14.8125 6.7188 L14.8125 6.2031 Q14.8125 5.6562 15.0625 5.3125 Q15.3125 4.9531 15.75 4.9531 Q15.9375 4.9531 16.1094 5.0625 Q16.2969 5.1562 16.3906 5.3125 Q16.625 5.6562 16.625 6.2812 Q16.625 7.0312 16.375 7.4375 Q16.2656 7.625 16.0781 7.7344 Q15.8906 7.8438 15.6719 7.8438 Q15.2812 7.8438 15.0469 7.5156 Q14.8125 7.1719 14.8125 6.7188 ZM13.5156 19.1719 Q12.9375 19.5 11.8281 19.5 Q10.5625 19.5312 9.7812 18.7344 Q9 17.9219 9 16.6406 Q9 15.2031 9.8438 14.3594 Q10.6875 13.5 12.0938 13.5 Q13.0625 13.5312 13.5156 13.7969 L13.5156 15.2812 Q12.9844 14.875 12.3125 14.875 Q11.5938 14.875 11.1562 15.3281 Q10.7344 15.7656 10.7344 16.5312 Q10.7344 17.2969 11.1406 17.7344 Q11.5469 18.1562 12.2656 18.1562 Q12.8906 18.1562 13.4844 17.75 L13.4844 19.1719 L13.5156 19.1719 ZM6 10.5 L4.5156 12.0156 L4.5156 21.0156 L6 22.5 L16.5156 22.5 L18 21.0156 L18 12.0156 L16.5156 10.5 L6 10.5 ZM6 12.0156 L16.5156 12.0156 L16.5156 21.0156 L6 21.0156 L6 12.0156 Z");
        replaceBtnIcon.setFill(Color.valueOf("#074675"));
        replaceBtn.setGraphic(new Group(replaceBtnIcon));
        replaceBtn.getStyleClass().add("little-custom-button");

        Button replaceAllBtn = new Button();
        replaceAllBtn.setFocusTraversable(false);
        SVGPath replaceAllBtnIcon = new SVGPath();
        replaceAllBtnIcon.setScaleX(0.6);
        replaceAllBtnIcon.setScaleY(0.6);
        replaceAllBtnIcon.setContent("M17.4062 4.0312 Q17.7344 3.3281 18.3125 3.3281 Q18.8906 3.3281 19.2031 3.8594 Q19.5156 4.3906 19.5156 5.3125 Q19.5156 6.2969 19.1562 6.9062 Q18.7969 7.5 18.2188 7.5 Q17.6562 7.5 17.4062 6.9531 L17.4062 6.9531 L17.4062 7.4062 L16.5156 7.4062 L16.5156 1.5 L17.4062 1.5 L17.4062 4.0312 L17.4062 4.0312 ZM17.375 5.6719 Q17.3594 6.0156 17.5156 6.2969 Q17.6875 6.5625 17.9688 6.5625 Q18.2656 6.5625 18.4219 6.25 Q18.5938 5.9219 18.5938 5.3125 Q18.5938 4.8438 18.4375 4.5625 Q18.2812 4.2656 18 4.2656 Q17.7344 4.2656 17.5469 4.5469 Q17.3594 4.8906 17.375 5.2656 L17.375 5.6719 ZM6.1875 11.5625 L3 8.375 L3.9844 7.3906 L5.5 8.875 L5.5 6.6094 Q5.5 5.75 6.1094 5.1406 Q6.7188 4.5312 7.5781 4.5 L11.1094 4.5 L11.1094 5.875 L7.5781 5.875 Q7.2969 5.8906 7.0938 6.1094 Q6.8906 6.3281 6.8906 6.6094 L6.8906 8.875 L8.2969 7.4531 L9.2812 8.4375 L6.1719 11.5625 L6.1875 11.5625 ZM14.0469 7.4062 L15 7.4062 L15 4.8438 Q15 3.0156 13.5781 3.0156 Q13.2656 3.0156 12.9062 3.1406 Q12.5938 3.2031 12.3438 3.375 L12.3438 4.3906 Q12.8594 3.9219 13.4531 3.9219 Q14.0469 3.9219 14.0469 4.625 L13.1562 4.7656 Q12 4.9531 12 6.2031 Q12 6.8125 12.2656 7.1719 Q12.5469 7.5312 13.0312 7.5 Q13.6875 7.5 14.0156 6.7812 L14.0469 6.7812 L14.0469 7.4062 ZM14.0469 5.375 L14.0469 5.6562 Q14.0625 6.0156 13.875 6.2969 Q13.7031 6.5625 13.3906 6.5625 Q13.2031 6.5938 13.0625 6.4375 Q12.9375 6.2812 12.9375 6.0625 Q12.9375 5.5625 13.4375 5.4844 L14.0469 5.375 ZM10.5156 19.4062 L9.5312 19.4062 L9.5312 18.7812 L9.5312 18.7812 Q9.1875 19.5 8.5469 19.5 Q8.0625 19.5312 7.7812 19.1719 Q7.5156 18.8125 7.5156 18.2031 Q7.5156 16.9531 8.6406 16.7656 L9.5469 16.625 Q9.5469 15.9062 8.9531 15.9062 Q8.3594 15.9062 7.8281 16.3906 L7.8281 15.375 Q8.0469 15.2344 8.4062 15.125 Q8.7812 15.0156 9.0938 15.0156 Q10.5156 15.0156 10.5156 16.8438 L10.5156 19.4062 ZM9.5469 17.6562 L9.5469 17.375 L8.9531 17.4844 Q8.4531 17.5625 8.4531 18.0625 Q8.4531 18.2812 8.5625 18.4375 Q8.6875 18.5938 8.875 18.5625 Q9.1875 18.5625 9.3594 18.3125 Q9.5312 18.0625 9.5312 17.6562 L9.5469 17.6562 ZM13.8906 19.5 Q14.6094 19.5 15 19.2656 L15 18.1875 Q14.6406 18.5 14.1875 18.5156 Q13.7344 18.5156 13.4375 18.1875 Q13.1562 17.8438 13.1562 17.2812 Q13.1562 16.7031 13.4375 16.3594 Q13.7344 16 14.1875 16.0156 Q14.6406 16.0312 15 16.3281 L15 15.2031 Q14.7188 15.0156 14.0625 15.0156 Q13.125 15.0156 12.5625 15.6562 Q12 16.2812 12 17.375 Q12 18.3281 12.5312 18.9219 Q13.0625 19.5 13.8906 19.5 ZM3 13.5 L4.5156 12.0156 L18 12.0156 L19.5156 13.5 L19.5156 21.0156 L18 22.5 L4.5156 22.5 L3 21.0156 L3 13.5 ZM4.5156 13.5 L4.5156 21.0156 L18 21.0156 L18 13.5 L4.5156 13.5 ZM9 10.5 L10.5156 9.0156 L21 9.0156 L22.5156 10.5 L22.5156 18.0156 L21 19.5 L21 10.5 L9 10.5 Z");
        replaceAllBtnIcon.setFill(Color.valueOf("#074675"));
        replaceAllBtn.setGraphic(new Group(replaceAllBtnIcon));
        replaceAllBtn.getStyleClass().add("little-custom-button");

        replaceBox.getChildren().addAll(replaceBtn,replaceAllBtn);
        // 组装面板
        getChildren().addAll(
                findBox
        );

        // 绑定按钮事件
        findNextBtn.setOnAction(e -> findNext());
        findPrevBtn.setOnAction(e -> findPrevious());
        replaceBtn.setOnAction(e -> replaceCurrent());
        replaceAllBtn.setOnAction(e -> replaceAll());
        closeBtn.setOnAction(e -> {
            if (totopBtn.isVisible()) {
                totopBtn.fire();
            }
            setVisible(false);
        });

        tobottomBtn.setOnAction(event->{
            getChildren().add(replaceBox);
            tobottomBtn.setVisible(false);
        });
        totopBtn.setOnAction(event->{
            getChildren().remove(replaceBox);
            tobottomBtn.setVisible(true);
        });

    }

    // 初始化事件
    private void initEvents() {
        // 输入内容变化时重置查找位置
        findField.textProperty().addListener((obs, oldVal, newVal) -> {
            lastFindPosition = -1;
            findNext();
        });

        // 区分大小写选项变化时重置查找位置
        caseToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            lastFindPosition = -1;
        });

        // 按Enter键触发"下一个"
        findField.setOnAction(e -> findNext());
        replaceField.setOnAction(e -> replaceCurrent());
    }

    // 查找下一个匹配项
    private void findNext() {
        String findText = findField.getText();
        if (findText.isEmpty()||codeArea.getText().isEmpty()) {
            //System.out.println("请输入查找内容");
            return;
        }
        codeArea.selectRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());

        String text = codeArea.getText();
        int startPos = (lastFindPosition == -1) ? 0 : lastFindPosition + 1;

        if (startPos >= text.length()) {
            startPos = 0; // 到达末尾，从头开始
        }

        int foundPos = findInText(text, findText, startPos, true);
        if (foundPos != -1) {
            highlightAndMove(foundPos, findText.length());
            lastFindPosition = foundPos;
        } else {
           // System.out.println("已到达末尾，未找到: " + findText);
            NotificationUtil.showNotification(Main.mainController.notice_pane,"已到达结尾，下一个从开头开始查找！");
            lastFindPosition = -1;
        }
    }

    // 查找上一个匹配项
    private void findPrevious() {
        String findText = findField.getText();
        if (findText.isEmpty()||codeArea.getText().isEmpty()) {
            return;
        }
        codeArea.selectRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());

        String text = codeArea.getText();
        int startPos = (lastFindPosition == -1) ? codeArea.getText().length() - 1 : lastFindPosition - 1;


        if (startPos < 0) {
            startPos = text.length() - 1; // 到达开头，从末尾开始
        }

        int foundPos = findInText(text, findText, startPos, false);
        if (foundPos != -1) {
            highlightAndMove(foundPos, findText.length());
            lastFindPosition = foundPos;
        } else {
            //System.out.println("已到达开头，未找到: " + findText);
            NotificationUtil.showNotification(Main.mainController.notice_pane,"已到达开头，下一个从结尾开始搜索!");
            lastFindPosition = -1;
        }
    }

    // 替换当前匹配项
    private void replaceCurrent() {
        String findText = findField.getText();
        String replaceText = replaceField.getText();

        if (findText.isEmpty()||codeArea.getText().isEmpty()) {
            return;
        }

        // 检查是否有选中的匹配项
        int caretPos = codeArea.getCaretPosition();
        if (lastFindPosition != -1 &&
                caretPos >= lastFindPosition &&
                caretPos <= lastFindPosition + findText.length()) {

            // 执行替换
            codeArea.replaceText(lastFindPosition, lastFindPosition + findText.length(), replaceText);
            //NotificationUtil.showNotification(Main.mainController.notice_pane,"已替换一处!");

            // 继续查找下一个
            lastFindPosition = lastFindPosition + replaceText.length();
            findNext();
        } else {
            // 没有选中项，先查找再替换
            findNext();
        }
    }

    // 替换所有匹配项
    private void replaceAll() {
        String findText = findField.getText();
        String replaceText = replaceField.getText();
        if (findText.isEmpty()||codeArea.getText().isEmpty()) {
            return;
        }

        String text = codeArea.getText();
        int count = 0;
        int pos = 0;

        // 构建新文本
        StringBuilder newText = new StringBuilder();

        while (pos <= text.length() - findText.length()) {
            int foundPos = findInText(text, findText, pos, true);
            if (foundPos == -1) break;

            // 追加找到的位置之前的文本
            newText.append(text.substring(pos, foundPos));
            // 追加替换文本
            newText.append(replaceText);

            pos = foundPos + findText.length();
            count++;
        }

        // 追加剩余文本
        newText.append(text.substring(pos));

        // 更新文本
        if (count > 0) {
            codeArea.replaceText(0, text.length(), newText.toString());
            NotificationUtil.showNotification(Main.mainController.notice_pane,"已替换全部 " + count + " 处！");
            lastFindPosition = -1;
        } else {
        }
    }

    // 在文本中查找（核心查找逻辑）
    private int findInText(String text, String target, int startPos, boolean forward) {
        if (target.isEmpty() || startPos < 0 || startPos >= text.length()) {
            return -1;
        }

        // 根据是否区分大小写处理
        String textToCheck = text;
        String targetToCheck = target;

        if (!caseToggle.isSelected()) {
            textToCheck = text.toLowerCase();
            targetToCheck = target.toLowerCase();
        }

        if (forward) {
            // 正向查找
            return textToCheck.indexOf(targetToCheck, startPos);
        } else {
            // 反向查找
            for (int i = startPos; i >= 0; i--) {
                if (i + targetToCheck.length() > textToCheck.length()) {
                    continue;
                }
                String substring = textToCheck.substring(i, i + targetToCheck.length());
                if (substring.equals(targetToCheck)) {
                    return i;
                }
            }
            return -1;
        }
    }

    // 高亮并移动光标到找到的位置
    private void highlightAndMove(int start, int length) {
        // 清除之前的高亮
        //codeArea.getStyleSpans(0, codeArea.getText().length()).clearStyle("search-highlight");
        // 添加新的高亮
        //codeArea.setStyleClass(start, start + length, "search-highlight");
        // 移动光标并确保可见

        codeArea.requestFollowCaret();

        /*
        codeArea.moveTo(start + length);

        // 确保该段落出现在视图中
        int paragraph = codeArea.offsetToPosition(start, TwoDimensional.Bias.Forward).getMajor();
        codeArea.showParagraphInViewport(paragraph);

         */
        //codeArea.requestFocus();
        //codeArea.showParagraphInViewport(codeArea.getCurrentParagraph());
        codeArea.selectRange(start, start + length);
    }

    // 显示面板并聚焦到查找框
    public void showPanel() {
        setVisible(true);
        findField.requestFocus();
        // 记录当前光标位置作为起始点
        lastFindPosition = -1;
    }



}
