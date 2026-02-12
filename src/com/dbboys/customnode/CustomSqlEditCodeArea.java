package com.dbboys.customnode;

import com.dbboys.util.KeywordsHighlightUtil;
import com.dbboys.util.SqlParserUtil;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSqlEditCodeArea extends CodeArea {
    private int[] sqledit_codearea_cursor_positon={0,0};  //括号最后配对高亮显示的位置,需要两个位置标记
    private int styleFlag = 0;
    public Button sql_save_button;  //保存文件路径
    public Button sqlRunButton;
    public CustomSearchReplaceVbox searchReplaceBox;

    public CustomSqlEditCodeArea() {
        super();
        SVGPath codeAreaExecuteItemIcon = new SVGPath();
        codeAreaExecuteItemIcon.setScaleX(0.6);
        codeAreaExecuteItemIcon.setScaleY(0.6);
        codeAreaExecuteItemIcon.setContent("M20.625 11.2812 L17 9.1719 L14 12.0781 L16.7344 14.7031 L20.625 12.4531 Q20.7969 12.3906 20.8906 12.2344 Q20.9844 12.0625 20.9844 11.875 Q20.9844 11.875 20.9844 11.875 Q20.9844 11.875 20.9844 11.875 L20.9844 11.875 Q20.9844 11.875 20.9844 11.875 Q20.9844 11.875 20.9844 11.875 Q20.9844 11.6875 20.8906 11.5312 Q20.7969 11.375 20.625 11.2812 L20.625 11.2812 L20.625 11.2812 ZM16.3906 8.8281 L12.375 6.5 L3.6094 2 L13.5 11.5938 L16.3906 8.8281 ZM3.75 22 L12.4062 17.2344 L16.125 15.0781 L13.5 12.5469 L3.75 22 ZM3.0312 2.4062 L3.0312 21.7656 L13.0156 12.0781 L3.0312 2.4062 Z");
        codeAreaExecuteItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaExecuteItem = new  MenuItem("执行 ( Execute Sql )                 Ctrl+Enter");
        codeAreaExecuteItem.setGraphic(new Group(codeAreaExecuteItemIcon));

        SVGPath codeAreaFormatItemIcon = new SVGPath();
        codeAreaFormatItemIcon.setScaleX(0.6);
        codeAreaFormatItemIcon.setScaleY(0.6);
        codeAreaFormatItemIcon.setContent("M11.0156 12.9844 L11.0156 11.0156 L21 11.0156 L21 12.9844 L11.0156 12.9844 ZM11.0156 9 L11.0156 6.9844 L21 6.9844 L21 9 L11.0156 9 ZM3 3 L21 3 L21 5.0156 L3 5.0156 L3 3 ZM3 21 L3 18.9844 L21 18.9844 L21 21 L3 21 ZM3 12 L6.9844 8.0156 L6.9844 15.9844 L3 12 ZM11.0156 17.0156 L11.0156 15 L21 15 L21 17.0156 L11.0156 17.0156 Z");
        codeAreaFormatItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaFormatItem = new  MenuItem("格式化 ( Format )                         Ctrl+M");
        codeAreaFormatItem.setGraphic(new Group(codeAreaFormatItemIcon));

        SVGPath codeAreaUpperItemIcon = new SVGPath();
        codeAreaUpperItemIcon.setScaleX(0.6);
        codeAreaUpperItemIcon.setScaleY(0.6);
        codeAreaUpperItemIcon.setContent("M9.8906 15.5531 Q10.125 16.0375 9.8438 16.5219 Q9.5781 16.9906 9 16.9906 Q8.375 16.9906 8.1094 16.4438 L7.3906 15.0063 L2.6094 15.0063 L1.8906 16.4438 Q1.7344 16.8188 1.3281 16.9594 Q0.9375 17.0844 0.5469 16.8969 Q0.1719 16.725 0.0312 16.3344 Q-0.0938 15.9438 0.0938 15.5531 L4.1094 7.5375 Q4.375 7.0375 5.0156 7.0375 Q5.6562 7.0375 5.9062 7.5375 L9.8906 15.5531 ZM3.625 12.9906 L6.3906 12.9906 L4.9844 10.225 L3.625 12.9906 ZM21 16.9906 L15 16.9906 Q14.4219 16.9906 14.125 16.475 Q13.8281 15.9438 14.2031 15.3813 L19.0156 9.0063 L15 9.0063 Q14.5938 9.0063 14.2812 8.7094 Q13.9844 8.3969 13.9844 7.9906 Q13.9844 7.5844 14.2812 7.2875 Q14.5938 6.9906 15 6.9906 L21 6.9906 Q21.5781 6.9906 21.875 7.5375 Q22.1719 8.0688 21.7969 8.6 L16.9844 15.0063 L21 15.0063 Q21.4062 15.0063 21.7031 15.3031 Q22.0156 15.5844 22.0156 15.9906 Q22.0156 16.3969 21.7031 16.6938 Q21.4062 16.9906 21 16.9906 ZM13.0156 12.9906 L10.9844 12.9906 Q10.5781 12.9906 10.2969 12.7094 Q10.0156 12.4125 10.0156 12.0063 Q10.0156 11.6 10.2969 11.3031 Q10.5781 10.9906 10.9844 10.9906 L13.0156 10.9906 Q13.4219 10.9906 13.7031 11.3031 Q13.9844 11.6 13.9844 12.0063 Q13.9844 12.4125 13.7031 12.7094 Q13.4219 12.9906 13.0156 12.9906 Z");
        codeAreaUpperItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaUpperItem = new  MenuItem("转换为大写 ( Upper )                    Ctrl+U");
        codeAreaUpperItem.setGraphic(new Group(codeAreaUpperItemIcon));


        SVGPath codeAreaLowerItemIcon = new SVGPath();
        codeAreaLowerItemIcon.setScaleX(0.6);
        codeAreaLowerItemIcon.setScaleY(0.7);
        codeAreaLowerItemIcon.setContent("M6 11.0156 Q6.8438 11.0156 7.4062 11.5781 Q7.9688 12.1406 8.0156 12.9844 L8.0156 17.0156 L3.9844 17.0156 Q3.1406 16.9688 2.5781 16.4062 Q2.0156 15.8438 2.0156 15 L2.0156 12.9844 Q2.0156 12.1406 2.5781 11.5781 Q3.1406 11.0156 3.9844 11.0156 L6 11.0156 ZM3.9844 12.9844 L3.9844 15 L6 15 L6 12.9844 L3.9844 12.9844 ZM20.0156 12.9844 L20.0156 15 L21.9844 15 L21.9844 17.0156 L20.0156 17.0156 Q19.1719 16.9688 18.5781 16.4062 Q18 15.8438 18 15 L18 12.9844 Q18 12.1406 18.5781 11.5781 Q19.1719 11.0156 20.0156 11.0156 L21.9844 11.0156 L21.9844 12.9844 L20.0156 12.9844 ZM12 6.9844 L12 11.0156 L14.0156 11.0156 Q14.8594 11.0156 15.4219 11.5781 Q15.9844 12.1406 15.9844 12.9844 L15.9844 15 Q15.9844 15.8438 15.4219 16.4062 Q14.8594 16.9688 14.0156 17.0156 L12 17.0156 Q11.1562 16.9688 10.5938 16.4062 Q10.0312 15.8438 9.9844 15 L9.9844 6.9844 L12 6.9844 ZM12 15 L14.0156 15 L14.0156 12.9844 L12 12.9844 L12 15 Z");
        codeAreaLowerItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaLowerItem = new  MenuItem("转换为小写 ( Lower )                     Ctrl+L");
        codeAreaLowerItem.setGraphic(new Group(codeAreaLowerItemIcon));

        SVGPath codeAreaCommRowItemIcon = new SVGPath();
        codeAreaCommRowItemIcon.setScaleX(0.6);
        codeAreaCommRowItemIcon.setScaleY(0.8);
        codeAreaCommRowItemIcon.setContent("M17.0469 6.9531 Q17.2812 7.2031 17.2812 7.5312 Q17.2812 7.8438 17.0469 8 L8 17.0469 Q7.8438 17.2812 7.5156 17.2812 Q7.2031 17.2812 6.9531 17.0469 Q6.7188 16.7969 6.7188 16.4844 Q6.7188 16.1562 6.9531 16 L16 6.9531 Q16.1562 6.7188 16.4688 6.7188 Q16.7969 6.7188 17.0469 6.9531 Z");
        codeAreaCommRowItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaCommRowItem = new  MenuItem("注释行 ( Comment Row )             Ctrl+/");
        codeAreaCommRowItem.setGraphic(new Group(codeAreaCommRowItemIcon));

        SVGPath codeAreaCommRowsItemIcon = new SVGPath();
        codeAreaCommRowsItemIcon.setScaleX(0.6);
        codeAreaCommRowsItemIcon.setScaleY(0.6);
        codeAreaCommRowsItemIcon.setContent("M7.0156 7.0078 L17 7.0078 L17 9.0078 L7.0156 9.0078 L7.0156 7.0078 ZM7.0156 10.9922 L14 10.9922 L14 13.0078 L7.0156 13.0078 L7.0156 10.9922 ZM20 1.9922 L4.0156 1.9922 Q3.1719 1.9922 2.5781 2.5859 Q2 3.1641 2 4.0078 L2 22.0078 L7.3438 18.0078 L20 18.0078 Q20.8438 18.0078 21.4219 17.4297 Q22.0156 16.8359 22.0156 16.0078 L22.0156 4.0078 Q22.0156 3.1641 21.4219 2.5859 Q20.8438 1.9922 20 1.9922 L20 1.9922 ZM20 16.0078 L6.6562 16.0078 L4.0156 18.0078 L4.0156 4.0078 L20 4.0078 L20 16.0078 Z");
        codeAreaCommRowsItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaCommRowsItem = new  MenuItem("注释段 ( Comment Rows )            Ctrl+|");
        codeAreaCommRowsItem.setGraphic(new Group(codeAreaCommRowsItemIcon));

        SVGPath codeAreaSearchItemIcon = new SVGPath();
        codeAreaSearchItemIcon.setScaleX(0.6);
        codeAreaSearchItemIcon.setScaleY(0.6);
        codeAreaSearchItemIcon.setContent("M9.8438 1.7184 Q12.0469 1.7184 13.9219 2.7965 Q15.7969 3.8746 16.8906 5.7496 Q18 7.609 18 9.8278 Q18 12.4684 16.4688 14.6246 L21.8906 20.0934 Q22.2656 20.4371 22.2812 20.9684 Q22.3125 21.484 21.9531 21.8746 Q21.5938 22.2496 21.0938 22.2809 Q20.5938 22.2965 20.2031 21.9684 L14.6406 16.4528 Q12.4844 17.984 9.8438 17.984 Q7.625 17.984 5.75 16.8903 Q3.8906 15.7809 2.8125 13.9059 Q1.7344 12.0309 1.7344 9.8278 Q1.7344 7.609 2.8125 5.7496 Q3.8906 3.8746 5.75 2.7965 Q7.625 1.7184 9.8438 1.7184 ZM9.8438 4.2496 Q8.3594 4.2496 7.0625 4.9996 Q5.7656 5.7496 5.0156 7.0465 Q4.2656 8.3434 4.2656 9.8278 Q4.2656 11.3121 5.0156 12.609 Q5.7656 13.9059 7.0625 14.6559 Q8.3594 15.3903 9.8594 15.3903 Q11.375 15.3903 12.6406 14.6559 Q13.9219 13.9059 14.6562 12.6403 Q15.4062 11.359 15.4062 9.859 Q15.4062 8.3434 14.6562 7.0778 Q13.9219 5.7965 12.6406 5.0309 Q11.375 4.2496 9.8438 4.2496 Z");
        codeAreaSearchItemIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaSearchItem = new  MenuItem("查找/替换 ( Search )                  Ctrl+F/R");
        codeAreaSearchItem.setGraphic(new Group(codeAreaSearchItemIcon));

        SVGPath codeAreaCopyIcon = new SVGPath();
        codeAreaCopyIcon.setScaleX(0.7);
        codeAreaCopyIcon.setScaleY(0.7);
        codeAreaCopyIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        codeAreaCopyIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaCopyItem = new  MenuItem("复制 ( Copy )                                Ctrl+C");
        codeAreaCopyItem.setGraphic(new Group(codeAreaCopyIcon));

        SVGPath codeAreaCutIcon = new SVGPath();
        codeAreaCutIcon.setScaleX(0.60);
        codeAreaCutIcon.setScaleY(0.60);
        codeAreaCutIcon.setContent("M9.6562 7.6406 L21.9844 20.0156 L21.9844 21 L18.9844 21 L12 14.0156 L9.6562 16.3594 Q9.9844 17.1562 9.9844 18 Q9.9844 19.6406 8.8125 20.8125 Q7.6406 21.9844 6 21.9844 Q4.3594 21.9844 3.1875 20.8125 Q2.0156 19.6406 2.0156 18 Q2.0156 16.3594 3.1875 15.1875 Q4.3594 14.0156 6 14.0156 Q6.8438 14.0156 7.6406 14.3438 L9.9844 12 L7.6406 9.6562 Q6.8438 9.9844 6 9.9844 Q4.3594 9.9844 3.1875 8.8125 Q2.0156 7.6406 2.0156 6 Q2.0156 4.3594 3.1875 3.1875 Q4.3594 2.0156 6 2.0156 Q7.6406 2.0156 8.8125 3.1875 Q9.9844 4.3594 9.9844 6 Q9.9844 6.8438 9.6562 7.6406 ZM6 8.0156 Q6.8438 8.0156 7.4219 7.4375 Q8.0156 6.8438 8.0156 6 Q8.0156 5.1562 7.4219 4.5781 Q6.8438 3.9844 6 3.9844 Q5.1562 3.9844 4.5625 4.5781 Q3.9844 5.1562 3.9844 6 Q3.9844 6.8438 4.5625 7.4375 Q5.1562 8.0156 6 8.0156 ZM6 20.0156 Q6.8438 20.0156 7.4219 19.4375 Q8.0156 18.8438 8.0156 18 Q8.0156 17.1562 7.4219 16.5781 Q6.8438 15.9844 6 15.9844 Q5.1562 15.9844 4.5625 16.5781 Q3.9844 17.1562 3.9844 18 Q3.9844 18.8438 4.5625 19.4375 Q5.1562 20.0156 6 20.0156 ZM12 12.5156 Q12.1875 12.5156 12.3438 12.3594 Q12.5156 12.1875 12.5156 12 Q12.5156 11.8125 12.3438 11.6562 Q12.1875 11.4844 12 11.4844 Q11.8125 11.4844 11.6406 11.6562 Q11.4844 11.8125 11.4844 12 Q11.4844 12.1875 11.6406 12.3594 Q11.8125 12.5156 12 12.5156 ZM18.9844 3 L21.9844 3 L21.9844 3.9844 L15 11.0156 L12.9844 9 L18.9844 3 Z");
        codeAreaCutIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaCutItem = new  MenuItem("剪切 ( Cut )                                   Ctrl+X");
        codeAreaCutItem.setGraphic(new Group(codeAreaCutIcon));

        SVGPath codeAreaCopyAllIcon = new SVGPath();
        codeAreaCopyAllIcon.setScaleX(0.7);
        codeAreaCopyAllIcon.setScaleY(0.7);
        codeAreaCopyAllIcon.setContent("M7 6 L7 3 Q7 2.5781 7.2812 2.2969 Q7.5781 2 8 2 L20 2 Q20.4219 2 20.7031 2.2969 Q21 2.5781 21 3 L21 17 Q21 17.4219 20.7031 17.7188 Q20.4219 18 20 18 L17 18 L17 21 Q17 21.4219 16.7031 21.7188 Q16.4219 22 16 22 L4 22 Q3.5938 22 3.2969 21.7188 Q3 21.4219 3 21 L3 7 Q3 6.5781 3.2969 6.2969 Q3.5938 6 4 6 L7 6 ZM5 8 L5 20 L15 20 L15 8 L5 8 ZM9 6 L17 6 L17 16 L19 16 L19 4 L9 4 L9 6 ZM7 11 L13 11 L13 13 L7 13 L7 11 ZM7 15 L13 15 L13 17 L7 17 L7 15 Z");
        codeAreaCopyAllIcon.setFill(Color.valueOf("#074675"));

        SVGPath codeAreaPasteIcon = new SVGPath();
        codeAreaPasteIcon.setScaleX(0.65);
        codeAreaPasteIcon.setScaleY(0.65);
        codeAreaPasteIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        codeAreaPasteIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaPasteItem = new MenuItem("粘贴 ( Paste )                                Ctrl+V");
        codeAreaPasteItem.setGraphic(new Group(codeAreaPasteIcon));

        SVGPath codeAreaUndoIcon = new SVGPath();
        codeAreaUndoIcon.setScaleX(0.60);
        codeAreaUndoIcon.setScaleY(0.60);
        codeAreaUndoIcon.setContent("M14.3906 6.6094 L8.3906 6.6094 L8.3906 3 L1.2188 7.8281 L8.3906 12.6094 L8.3906 9 L14.3906 9 Q16.3594 9 17.7812 10.4062 Q19.2188 11.8125 19.2188 13.8125 Q19.2188 15.7969 17.7812 17.2031 Q16.3594 18.6094 14.3906 18.6094 L8.3906 18.6094 L8.3906 21 L14.3906 21 Q16.3594 21 18 20.0469 Q19.6406 19.0781 20.625 17.4219 Q21.6094 15.75 21.6094 13.8125 Q21.6094 11.8594 20.625 10.2031 Q19.6406 8.5312 18 7.5781 Q16.3594 6.6094 14.3906 6.6094 Z");
        codeAreaUndoIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaUndoItem = new MenuItem("撤销 ( Undo )                                Ctrl+Z");
        codeAreaUndoItem.setGraphic(new Group(codeAreaUndoIcon));

        SVGPath codeAreaRedoIcon = new SVGPath();
        codeAreaRedoIcon.setScaleX(0.60);
        codeAreaRedoIcon.setScaleY(0.60);
        codeAreaRedoIcon.setContent("M9.6094 6.6094 L15.6094 6.6094 L15.6094 3 L22.7812 7.8281 L15.6094 12.6094 L15.6094 9 L9.6094 9 Q7.6406 9 6.2031 10.4062 Q4.7812 11.8125 4.7812 13.8125 Q4.7812 15.7969 6.2031 17.2031 Q7.6406 18.6094 9.6094 18.6094 L15.6094 18.6094 L15.6094 21 L9.6094 21 Q7.6406 21 6 20.0469 Q4.3594 19.0781 3.375 17.4219 Q2.3906 15.75 2.3906 13.8125 Q2.3906 11.8594 3.375 10.2031 Q4.3594 8.5312 6 7.5781 Q7.6406 6.6094 9.6094 6.6094 Z");
        codeAreaRedoIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaRedoItem = new MenuItem("重做 ( Redo )                                Ctrl+Y");
        codeAreaRedoItem.setGraphic(new Group(codeAreaRedoIcon));

        SVGPath codeAreaSaveIcon = new SVGPath();
        codeAreaSaveIcon.setScaleX(0.60);
        codeAreaSaveIcon.setScaleY(0.60);
        codeAreaSaveIcon.setContent("M20.3438 6.0938 Q21 6.75 21 7.6875 L21 20.25 Q21 21.1875 20.3438 21.8438 Q19.6875 22.5 18.75 22.5 L2.25 22.5 Q1.3125 22.5 0.6562 21.8438 Q0 21.1875 0 20.25 L0 3.75 Q0 2.8125 0.6562 2.1562 Q1.3125 1.5 2.25 1.5 L14.8125 1.5 Q15.75 1.5 16.4062 2.1562 L20.3438 6.0938 ZM8.3594 18.6406 Q9.2344 19.5 10.5 19.5 Q11.7656 19.5 12.625 18.6406 Q13.5 17.7656 13.5 16.5 Q13.5 15.2344 12.625 14.375 Q11.7656 13.5 10.5 13.5 Q9.2344 13.5 8.3594 14.375 Q7.5 15.2344 7.5 16.5 Q7.5 17.7656 8.3594 18.6406 ZM15 5.2031 Q15 5.0156 14.8125 4.8281 L14.6719 4.6875 Q14.4844 4.5 14.2969 4.5 L3.5625 4.5 Q3 4.5 3 5.0625 L3 9.9375 Q3 10.5 3.5625 10.5 L14.4375 10.5 Q15 10.5 15 9.9375 L15 5.2031 Z");
        codeAreaSaveIcon.setFill(Color.valueOf("#074675"));
        MenuItem codeAreaSaveItem = new MenuItem("保存 ( Save )                                 Ctrl+S");
        codeAreaSaveItem.setGraphic(new Group(codeAreaSaveIcon));

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
                sql_save_button.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.ENTER){
                sqlRunButton.fire();
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
                codeAreaSearchItem.fire();
                searchReplaceBox.findField.requestFocus();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.R){
                codeAreaSearchItem.fire();
                if(searchReplaceBox.tobottomBtn.isVisible()){
                    searchReplaceBox.tobottomBtn.fire();
                    searchReplaceBox.findField.requestFocus();
                }
            }
        });



        setContextMenu(codeAreaMenu);
        codeAreaFormatItem.setOnAction(event-> {
            String selectedText = getSelectedText();
            if (selectedText == null || selectedText.isEmpty()) {
                selectedText=getText();
                replaceText(SqlParserUtil.formatSql(selectedText));
            }else{
                int start = getSelection().getStart();
                int end = getSelection().getEnd();
                replaceText(start, end, SqlParserUtil.formatSql(selectedText));
            }
        });

        codeAreaUpperItem.setOnAction(event-> {
            String selectedText = getSelectedText();
            if (selectedText == null || selectedText.isEmpty()) {
                selectedText=getText();
                replaceText(SqlParserUtil.upperSql(selectedText));
            }else{
                int start = getSelection().getStart();
                int end = getSelection().getEnd();
                replaceText(start, end, SqlParserUtil.upperSql(selectedText));
            }
        });

        codeAreaLowerItem.setOnAction(event-> {
            String selectedText = getSelectedText();
            if (selectedText == null || selectedText.isEmpty()) {
                selectedText=getText();
                replaceText(SqlParserUtil.lowerSql(selectedText));
            }else{
                int start = getSelection().getStart();
                int end = getSelection().getEnd();
                replaceText(start, end, SqlParserUtil.lowerSql(selectedText));
            }
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
            searchReplaceBox.setVisible(true);
        });
        //codeAreaExecuteItem.visibleProperty().bind(sqlRunButton.visibleProperty());
        codeAreaExecuteItem.setOnAction(event -> {
            sqlRunButton.fire();
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


        setOnContextMenuRequested((ContextMenuEvent event) -> {
            if(sql_save_button.isDisable()){
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

            if(sqlRunButton.isDisable()){
                codeAreaExecuteItem.setDisable(true);
            }else{
                codeAreaExecuteItem.setDisable(false);
            }
        });
        //设置行号
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        codeAreaSaveItem.setOnAction(event->{
            sql_save_button.fire();
        });


        // 监听光标位置变化，如果移动到了括号位置，高亮显示一对括号
        caretPositionProperty().addListener((obs, oldPos, newPos) -> {
            Platform.runLater(() -> {
                styleFlag=1; //标记只变更了样式，未变更内容，避免高亮显示括号触发codearea变更监听
                highlightMatchingBracket(this, newPos,sqledit_codearea_cursor_positon);
                styleFlag=0; //高亮后恢复标记
            });
        });
        //监听输入变化
        richChanges()
                //.filter(change ->  !change.isPlainTextIdentity()) // 只处理文本的变化,否则setStyleSpans也会触发事件
                .filter(change ->  styleFlag==0) // 只处理文本的变化,否则setStyleSpans也会触发事件
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
                    styleFlag=1;  //设置标识，避免setStyleSpans触发richChanges
                    //如果包含特殊字符，全局更新样式
                    //System.out.println("parseText is:"+parseText);
                    //if(parseText.contains("'")||parseText.contains("\"")||parseText.contains("`")||parseText.contains("/*")||parseText.contains("*/")||parseText.contains("--")){
                    setStyleSpans(0,KeywordsHighlightUtil.applyHighlighting(getText()));
                    sql_save_button.setDisable(false);
                    //如果不包含特殊字符，局部更新样式
                    //}else{
                    //    codeArea.setStyleSpans(appendFirstPos,applyHighlighting(parseText));
                    //}

                    styleFlag=0;  //执行完后关闭标识

                });
    }

    public static String format(String sql) {

        String STRING_PATTERN = "'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"+"|" + "'[\\s\\S]*";
        String DOUBLE_STRING_PATTERN = "\"[^\"]*\""+"|" + "\"[\\s\\S]*";
        String FANYINHAO_STRING_PATTERN = "`[^`]*`"+"|" + "`[\\s\\S]*";
        //String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"+ "|" + "/\\*(.|\\n)*" ; //可能堆栈溢出
        String COMMENT_PATTERN = "--[^\n]*" + "|"+"/\\*[\\s\\S]*?\\*/"+"|"+"/\\*[\\s\\S]*" +"|"+"\\{[\\s\\S]*?\\}";//正常
        final String[] KEYWORDS = {
                "SELECT", "FROM", "WHERE", "GROUP BY", "HAVING", "ORDER BY",
                "LIMIT", "OFFSET", "JOIN", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN",
                "ON", "AND", "OR", "UNION", "EXCEPT", "INTERSECT"
        };
        String KEYWORD_PATTERN = "(?i)\\b(" + String.join("|", KEYWORDS) + ")\\b";
        if (sql == null || sql.isBlank()) return "";
        sql=sql
                .replaceAll("[\\r\\n]+", " ")     // 替换所有换行符为一个空格
                .replaceAll("\\s{2,}", " ")       // 连续两个以上空白压缩为一个空格
                .trim();
        // 将关键字前统一加换行
        Pattern pattern=Pattern.compile(
                STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN +"|(?<KEYWORD>"+KEYWORD_PATTERN+")"
        );
        Matcher matcher = pattern.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            if (matcher.group("KEYWORD") != null) {
                String keyword = matcher.group("KEYWORD");
                matcher.appendReplacement(sb, "\n" + keyword);
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
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
                    last_pos[0]=0;
                }catch (Exception e){
                    last_pos[0]=0;
                    last_pos[0]=0;
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
                    last_pos[0]=0;
                }catch (Exception e){
                    last_pos[0]=0;
                    last_pos[0]=0;
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
