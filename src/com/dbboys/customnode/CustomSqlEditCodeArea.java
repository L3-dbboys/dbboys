package com.dbboys.customnode;

import com.dbboys.ui.IconFactory;
import com.dbboys.ui.IconPaths;
import com.dbboys.util.KeywordsHighlightUtil;
import com.dbboys.util.MenuItemUtil;
import com.dbboys.util.SqlParserUtil;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSqlEditCodeArea extends CodeArea {
    private final int[] sqlEditCodeAreaCursorPosition = {0, 0};
    private int styleChangeFlag = 0;
    private Runnable onSaveRequest = () -> {};
    private Runnable onContentDirty = () -> {};
    private Runnable onShowFindPanel = () -> {};
    private Runnable onShowReplacePanel = () -> {};
    private Runnable onExecuteRequest = () -> {};
    private BooleanSupplier saveDisabledSupplier = () -> true;
    private BooleanSupplier executeDisabledSupplier = () -> true;

    public CustomSqlEditCodeArea() {
        super();
        CustomShortcutMenuItem codeAreaExecuteItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.execute", "Ctrl+Enter", IconFactory.group(IconPaths.SQL_RUN, 0.6));
        CustomShortcutMenuItem codeAreaFormatItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.format", "Ctrl+M", IconFactory.group(IconPaths.SQL_FORMAT, 0.6));
        CustomShortcutMenuItem codeAreaUpperItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.upper", "Ctrl+U", IconFactory.group(IconPaths.SQL_UPPER, 0.6));
        CustomShortcutMenuItem codeAreaLowerItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.lower", "Ctrl+L", IconFactory.group(IconPaths.SQL_LOWER, 0.6, 0.7));
        CustomShortcutMenuItem codeAreaCommRowItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.comment_row", "Ctrl+/", IconFactory.group(IconPaths.SQL_COMMENT_ROW, 0.6, 0.8));
        CustomShortcutMenuItem codeAreaCommRowsItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.comment_rows", "Ctrl+|", IconFactory.group(IconPaths.SQL_COMMENT_ROWS, 0.6));
        CustomShortcutMenuItem codeAreaSearchItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.search", "Ctrl+F/R", IconFactory.group(IconPaths.MAIN_SEARCH, 0.6));
        CustomShortcutMenuItem codeAreaCopyItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.copy", "Ctrl+C", IconFactory.group(IconPaths.COPY, 0.7));
        CustomShortcutMenuItem codeAreaCutItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.cut", "Ctrl+X", IconFactory.group(IconPaths.CUT, 0.6));
        CustomShortcutMenuItem codeAreaPasteItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.paste", "Ctrl+V", IconFactory.group(IconPaths.PASTE, 0.65));
        CustomShortcutMenuItem codeAreaUndoItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.undo", "Ctrl+Z", IconFactory.group(IconPaths.UNDO, 0.6));
        CustomShortcutMenuItem codeAreaRedoItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.redo", "Ctrl+Y", IconFactory.group(IconPaths.REDO, 0.6));
        CustomShortcutMenuItem codeAreaSaveItem = MenuItemUtil.createMenuItemI18n("sql.editor.menu.save", "Ctrl+S", IconFactory.group(IconPaths.GENERIC_SAVE_AS, 0.6));

        ContextMenu codeAreaMenu = new ContextMenu();

        codeAreaMenu.getItems().addAll(codeAreaExecuteItem,codeAreaFormatItem,codeAreaUpperItem,codeAreaLowerItem,codeAreaCommRowItem,codeAreaCommRowsItem,codeAreaSearchItem,codeAreaCopyItem,codeAreaCutItem,codeAreaPasteItem,codeAreaUndoItem,codeAreaRedoItem,codeAreaSaveItem);

        //仅用于对齐显示，暂不知为何不生效，treeview是有效的
        /*
        codeAreaExecuteItem.setAccelerator(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN));
        codeAreaFormatItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
        codeAreaUpperItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN));
        codeAreaLowerItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        codeAreaCommRowItem.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.CONTROL_DOWN));
        codeAreaCommRowsItem.setAccelerator(new KeyCodeCombination(KeyCode.BACK_SLASH, KeyCombination.CONTROL_DOWN));
        codeAreaSearchItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        codeAreaCopyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        codeAreaCutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        codeAreaPasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        codeAreaUndoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        codeAreaRedoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        codeAreaSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        */

        // ctrl+s保存

        setOnKeyPressed(event -> {
            if(event.isControlDown()&&event.getCode() == KeyCode.S){
                onSaveRequest.run();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.ENTER){
                fireExecute();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.M){
                codeAreaFormatItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.U){
                codeAreaUpperItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.L){
                codeAreaLowerItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.SLASH){
                codeAreaCommRowItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.BACK_SLASH){
                codeAreaCommRowsItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.F){
                onShowFindPanel.run();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.R){
                onShowReplacePanel.run();
            }
        });



        setContextMenu(codeAreaMenu);
        codeAreaFormatItem.setOnAction(event-> {
            applyTransform(SqlParserUtil::formatSql);
        });

        codeAreaUpperItem.setOnAction(event-> {
            applyTransform(SqlParserUtil::upperSql);
        });

        codeAreaLowerItem.setOnAction(event-> {
            applyTransform(SqlParserUtil::lowerSql);
        });

        codeAreaCommRowItem.setOnAction(event-> {
            int currentLine = getCurrentParagraph();
            String lineText = getParagraph(currentLine).getText();

            // 去掉开头空格判断
            String trimmed = lineText.trim();
            int lineStart = getAbsolutePosition(currentLine, 0);

            if (trimmed.startsWith("--")) {
                // 取消注释
                int commentIndex = lineText.indexOf("--");
                deleteText(lineStart + commentIndex, lineStart + commentIndex + 2);
            } else {
                // 添加注释
                int firstNonSpace = lineText.indexOf(trimmed);
                insertText(lineStart + firstNonSpace, "--");
            }
        });

        codeAreaCommRowsItem.setOnAction(event-> {
            int start = getSelection().getStart();
            int end = getSelection().getEnd();
            String selectedText = getSelectedText();

            if(selectedText==null ||selectedText.isEmpty()){

            }else{
                if (selectedText.trim().startsWith("/*") && selectedText.endsWith("*/")) {
                    // 已被块注释，取消注释
                    //String uncommented = selectedText.substring(2, selectedText.length() - 2);
                    String uncommented = selectedText.replaceAll("/\\*|\\*/","");
                    replaceText(start, end, uncommented);
                } else {
                    // 未被注释，添加块注释
                    String commented = "/*" + selectedText + "*/";
                    replaceText(start, end, commented);
                }
            }

        });

        codeAreaSearchItem.setOnAction(event->{
            onShowFindPanel.run();
        });
        // codeAreaExecuteItem 可按需要绑定外部执行按钮可见性
        codeAreaExecuteItem.setOnAction(event -> {
            fireExecute();
        });
        codeAreaCopyItem.setDisable(true);

        codeAreaCopyItem.setOnAction(event -> {
            copy();
        });
        codeAreaCutItem.setOnAction(event -> {
            cut();
        });

        codeAreaPasteItem.setOnAction(event -> {
            paste();
        });
        codeAreaUndoItem.setOnAction(event -> {
            undo();
        });
        codeAreaRedoItem.setOnAction(event -> {
            redo();
        });


        codeAreaMenu.setOnShowing(event -> {
            if(saveDisabledSupplier.getAsBoolean()){
                codeAreaSaveItem.setDisable(true);
            }else{
                codeAreaSaveItem.setDisable(false);
            }

            if(getSelectedText().isEmpty()){
                codeAreaCopyItem.setDisable(true);
                codeAreaCutItem.setDisable(true);
                codeAreaCommRowsItem.setDisable(true);
            }else{
                codeAreaCopyItem.setDisable(false);
                codeAreaCutItem.setDisable(false);
                codeAreaCommRowsItem.setDisable(false);
            }

            Clipboard clipboard = Clipboard.getSystemClipboard();
            if(clipboard.hasString()){
                codeAreaPasteItem.setDisable(false);
            }else{
                codeAreaPasteItem.setDisable(true);
            }

            if(executeDisabledSupplier.getAsBoolean()){
                codeAreaExecuteItem.setDisable(true);
            }else{
                codeAreaExecuteItem.setDisable(false);
            }
        });
        //设置行号
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        codeAreaSaveItem.setOnAction(event->{
            onSaveRequest.run();
        });


        // 监听光标位置变化，如果移动到了括号位置，高亮显示一对括号
        caretPositionProperty().addListener((obs, oldPos, newPos) -> {
            Platform.runLater(() -> {
                styleChangeFlag=1; //标记只变更了样式，未变更内容，避免高亮显示括号触发codearea变更监听
                highlightMatchingBracket(this, newPos,sqlEditCodeAreaCursorPosition);
                styleChangeFlag=0; //高亮后恢复标记
            });
        });
        //监听输入变化
        richChanges()
                //.filter(change ->  !change.isPlainTextIdentity()) // 只处理文本的变化,否则setStyleSpans也会触发事件
                .filter(change ->  styleChangeFlag==0) // 只处理文本的变化,否则setStyleSpans也会触发事件
                .subscribe(change -> {
                    /*
                    String parseText="";  //需要匹配的字符串，如果输入的开头不是空格或回车，需要往前寻找空格累加，往后同理

                    System.out.println("=============================");
                    //System.out.println(change);
                    System.out.println("changed postion: " + change.getPosition());
                    System.out.println("inserted end: " + change.getInsertionEnd());
                    System.out.println("removed end: " + change.getRemovalEnd());

                    Integer appendFirstPos=0;
                    Integer appendEndPos=0;
                    //如果是插入，按以下流程处理

                    if(change.getInsertionEnd()-change.getPosition()>0){
                        parseText=codeArea.getText( change.getPosition(),change.getInsertionEnd());
                        appendFirstPos= change.getPosition();
                        appendEndPos= change.getInsertionEnd();
                        //往前拼接到空格或回车或开头为止
                        if(appendFirstPos>0) {
                            do
                             {
                                parseText = codeArea.getText(appendFirstPos - 1, appendFirstPos) + parseText;
                                appendFirstPos--;
                            }while ((!(parseText.charAt(0) == ' ' || parseText.charAt(0) == '\n')) && appendFirstPos > 0);
                        }
                        //往后拼接到空格或回车或结尾为止
                        if(appendEndPos<codeArea.getText().length()){
                            do
                            {
                                parseText=parseText+codeArea.getText( appendEndPos,appendEndPos+1);
                                appendEndPos++;
                            }while((!(parseText.charAt(parseText.length()-1)==' '||parseText.charAt(parseText.length()-1)=='\n'))&&appendEndPos<codeArea.getText().length());
                        }



                    }
                    //如果是删除，按以下流程处理
                    else if (change.getRemovalEnd()-change.getPosition()>0) {
                        appendFirstPos= change.getPosition();
                        appendEndPos= change.getPosition();
                        parseText="";
                        //如果光标在最开始位置，不往前拼接
                        if(change.getPosition()!=0){
                            //往前拼接到空格或回车或开头为止
                            do
                            {
                                parseText=codeArea.getText( appendFirstPos-1,appendFirstPos)+parseText;
                                appendFirstPos--;
                            }while((!(parseText.charAt(0)==' '||parseText.charAt(0)=='\n'))&&appendFirstPos>0);
                        }

                        //往后拼接到空格或回车或结尾为止
                        if(appendEndPos<codeArea.getText().length()) {
                            do {
                                parseText = parseText + codeArea.getText(appendEndPos, appendEndPos + 1);
                                appendEndPos++;
                            } while ((!(parseText.charAt(parseText.length() - 1) == ' ' || parseText.charAt(parseText.length() - 1) == '\n')) && appendEndPos < codeArea.getText().length());
                        }

                    }
                    */
                    //为局部设置样式，如每次变更都全局设置样式，如脚本很长会卡
                    styleChangeFlag=1;  //设置标识，避免setStyleSpans触发richChanges
                    //如果包含特殊字符，全局更新样式
                    //System.out.println("parseText is:"+parseText);
                    //if(parseText.contains("'")||parseText.contains("\"")||parseText.contains("`")||parseText.contains("/*")||parseText.contains("*/")||parseText.contains("--")){
                    setStyleSpans(0,KeywordsHighlightUtil.applyHighlighting(getText()));
                    onContentDirty.run();
                    //如果不包含特殊字符，局部更新样式
                    //}else{
                    //    codeArea.setStyleSpans(appendFirstPos,applyHighlighting(parseText));
                    //}

                    styleChangeFlag=0;  //执行完后关闭标识

                });
    }

    public void setOnSaveRequest(Runnable onSaveRequest) {
        this.onSaveRequest = onSaveRequest == null ? () -> {} : onSaveRequest;
    }

    public void setOnContentDirty(Runnable onContentDirty) {
        this.onContentDirty = onContentDirty == null ? () -> {} : onContentDirty;
    }

    public void setOnShowFindPanel(Runnable onShowFindPanel) {
        this.onShowFindPanel = onShowFindPanel == null ? () -> {} : onShowFindPanel;
    }

    public void setOnShowReplacePanel(Runnable onShowReplacePanel) {
        this.onShowReplacePanel = onShowReplacePanel == null ? () -> {} : onShowReplacePanel;
    }

    public void setOnExecuteRequest(Runnable onExecuteRequest) {
        this.onExecuteRequest = onExecuteRequest == null ? () -> {} : onExecuteRequest;
    }

    public void setSaveDisabledSupplier(BooleanSupplier saveDisabledSupplier) {
        this.saveDisabledSupplier = saveDisabledSupplier == null ? () -> true : saveDisabledSupplier;
    }

    public void setExecuteDisabledSupplier(BooleanSupplier executeDisabledSupplier) {
        this.executeDisabledSupplier = executeDisabledSupplier == null ? () -> true : executeDisabledSupplier;
    }

    

    //如果光标位置是括号，匹配的一对括号高亮
    public void highlightMatchingBracket(CodeArea codeArea, int caretPosition,int[] last_pos) {
        String text = codeArea.getText();

        // 清除之前的样式
        //codeArea.clearStyle(0, text.length());

        // 匹配括号的逻辑
        int matchPos = -1;

        // 1. 如果光标在括号前面，尝试匹配右侧括号
        if (caretPosition < text.length()) {
            if(last_pos[0]!=0){
                try {
                    if((text.charAt(last_pos[0])=='{'&&text.charAt(last_pos[1])=='}')||(text.charAt(last_pos[0])=='}'&&text.charAt(last_pos[1])=='{')){
                        codeArea.setStyleClass(last_pos[0], last_pos[0] + 1, "comment");
                        codeArea.setStyleClass(last_pos[1], last_pos[1] + 1, "comment");
                    }else if((text.charAt(last_pos[0])=='('&&text.charAt(last_pos[1])==')')||(text.charAt(last_pos[0])==')'&&text.charAt(last_pos[1])=='(')){
                        codeArea.setStyleClass(last_pos[0], last_pos[0] + 1, "paren");
                        codeArea.setStyleClass(last_pos[1], last_pos[1] + 1, "paren");
                    }
                    last_pos[0]=0;
                    last_pos[1]=0;
                }catch (Exception e){
                    last_pos[0]=0;
                    last_pos[1]=0;
                }
            }
            char rightChar = text.charAt(caretPosition);
            if ("()[]{}".indexOf(rightChar) != -1) {
                matchPos = findMatchingBracket(text, caretPosition, rightChar);
                if (matchPos != -1) {
                    //括号显示样式
                    codeArea.setStyleClass(caretPosition, caretPosition + 1, "bracket-highlight");
                    codeArea.setStyleClass(matchPos, matchPos + 1, "bracket-highlight");
                    last_pos[0] = caretPosition;
                    last_pos[1] = matchPos;
                    return;
                }
            }
        }

        // 2. 如果光标在括号后面，尝试匹配左侧括号

        if (caretPosition > 0) {
            if(last_pos[0]!=0){
                try {
                    if((text.charAt(last_pos[0])=='{'&&text.charAt(last_pos[1])=='}')||(text.charAt(last_pos[0])=='}'&&text.charAt(last_pos[1])=='{')){
                        codeArea.setStyleClass(last_pos[0], last_pos[0] + 1, "comment");
                        codeArea.setStyleClass(last_pos[1], last_pos[1] + 1, "comment");
                    }else if((text.charAt(last_pos[0])=='('&&text.charAt(last_pos[1])==')')||(text.charAt(last_pos[0])==')'&&text.charAt(last_pos[1])=='(')){
                        codeArea.setStyleClass(last_pos[0], last_pos[0] + 1, "paren");
                        codeArea.setStyleClass(last_pos[1], last_pos[1] + 1, "paren");
                    }
                    last_pos[0]=0;
                    last_pos[1]=0;
                }catch (Exception e){
                    last_pos[0]=0;
                    last_pos[1]=0;
                }
            }
            char leftChar = text.charAt(caretPosition - 1);
            if ("()[]{}".indexOf(leftChar) != -1) {
                matchPos = findMatchingBracket(text, caretPosition - 1, leftChar);
                if (matchPos != -1) {
                    codeArea.setStyleClass(caretPosition - 1, caretPosition, "bracket-highlight");
                    codeArea.setStyleClass(matchPos, matchPos + 1, "bracket-highlight");
                    last_pos[0] = caretPosition-1;
                    last_pos[1] = matchPos;
                }
            }
        }


    }

    private void fireExecute() {
        if (!executeDisabledSupplier.getAsBoolean()) {
            onExecuteRequest.run();
        }
    }

    private void applyTransform(java.util.function.Function<String, String> transform) {
        String selectedText = getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            replaceText(transform.apply(getText()));
            return;
        }
        int start = getSelection().getStart();
        int end = getSelection().getEnd();
        replaceText(start, end, transform.apply(selectedText));
    }

    /**
     * 查找匹配的括号
     */

    public int findMatchingBracket(String text, int pos, char currentChar) {
        char matchChar;
        int direction;

        switch (currentChar) {
            case '(':
                matchChar = ')';
                direction = 1;
                break;
            case ')':
                matchChar = '(';
                direction = -1;
                break;
            case '[':
                matchChar = ']';
                direction = 1;
                break;
            case ']':
                matchChar = '[';
                direction = -1;
                break;
            case '{':
                matchChar = '}';
                direction = 1;
                break;
            case '}':
                matchChar = '{';
                direction = -1;
                break;
            default:
                return -1;
        }

        int balance = 0;
        for (int i = pos + direction; i >= 0 && i < text.length(); i += direction) {
            char c = text.charAt(i);

            if (c == currentChar) {
                balance++;
            } else if (c == matchChar) {
                if (balance == 0) {
                    return i;
                }
                balance--;
            }
        }

        return -1; // 未找到匹配的括号
    }


}
