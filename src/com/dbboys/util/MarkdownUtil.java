package com.dbboys.util;

import com.dbboys.customnode.*;
import com.dbboys.vo.Markdown;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MarkdownUtil {

    private static Pattern LINK_PATTERN = Pattern.compile("\\[([^]]+)\\]\\(([^)]+)\\)");
    private Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private Pattern INLINE_CODE_PATTERN = Pattern.compile("`([^`]+)`");
    private static Pattern CODE_BLOCK_PATTERN = Pattern.compile("```(?:\\w+)?\\s*\\n?(.*?)```", Pattern.DOTALL);
    private static Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s*(.+)$", Pattern.MULTILINE);

    public static String markdownText="";
    public static TreeView<Markdown> treeView=new TreeView<>() ;
    private static List<TreeItem<Markdown>> clipboardFiles; // 替换原有的 clipboardFile
    private static boolean cutOperation;
    public static TreeItem<Markdown> selectedTreeItem;
    //public static TreeItem<Markdown> sourceTreeItem;
    public static ContextMenu contextMenu=new ContextMenu();


    public static List<TreeItem<Markdown>> sourceTreeItems;

    static {
        File rootDir = null;
        try {
            rootDir = new File("docs").getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        treeView.setRoot(createNode(rootDir));
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new CustomMarkdownTreeCell());
        treeView.setStyle(" -fx-pref-height: 2000 ;-fx-border-width: 0.5 0 0 0;-fx-border-color: #aaa;");
        if(!treeView.getRoot().getChildren().isEmpty())
        treeView.getSelectionModel().select(treeView.getRoot().getChildren().get(0));
        //treeView.getRoot().getChildren().forEach(child -> child.setExpanded(true));
        createContextMenu();
    }


    //创建一个TreeItem,重构ifLeaf显示箭头
    public static TreeItem<Markdown> createNode(final File file) {
        Markdown markdown = new Markdown(file);
        TreeItem<Markdown> treeItem = new TreeItem<>(markdown) {
            @Override
            public boolean isLeaf() {
                // ⚠ 不要用外部的 file 变量
                Markdown current = getValue();
                if (current == null) return true;
                File f = current.getFile();
                return f == null || !f.isDirectory();
            }
        };
        if(file.isDirectory()) {
            treeItem.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                if (isNowExpanded&& treeItem.getChildren().isEmpty()){
                    buildChildren(treeItem);
                };
            });
        }
        return treeItem;
    }



    private static void buildChildren(TreeItem<Markdown> treeItem) {
        ObservableList<TreeItem<Markdown>> children = FXCollections.observableArrayList();
        File f = treeItem.getValue().getFile();
        if (f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                Arrays.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) return -1;
                    if (!f1.isDirectory() && f2.isDirectory()) return 1;
                    return f1.getName().compareToIgnoreCase(f2.getName());
                });

                for (File child : files) {
                    try {
                        children.add(createNode(child.getCanonicalFile()));
                    } catch (IOException e) {
                        children.add(createNode(child.getAbsoluteFile()));
                    }
                }
                treeItem.getChildren().setAll(children);
            }
        }
    }


    private static void createContextMenu() {
        SVGPath copyItemIcon = new SVGPath();
        copyItemIcon.setScaleX(0.65);
        copyItemIcon.setScaleY(0.65);
        copyItemIcon.setContent("M5.5156 4.6094 L5.5156 6.7656 L5.5156 17.2344 Q5.5156 18.625 6.4531 19.5625 Q7.3906 20.5 8.7344 20.5 L17.375 20.5 Q17.1406 21.1719 16.5312 21.5781 Q15.9375 21.9844 15.2656 21.9844 L8.7344 21.9844 Q7.8281 21.9844 6.9375 21.625 Q6.0469 21.2656 5.375 20.625 Q4.7031 19.9688 4.3438 19.0781 Q3.9844 18.1875 3.9844 17.2344 L3.9844 6.7656 Q3.9844 6 4.4062 5.4219 Q4.8438 4.8438 5.5156 4.6094 ZM17.7656 2.0156 Q18.6719 2.0156 19.3438 2.6719 Q20.0156 3.3125 20.0156 4.2656 L20.0156 17.2344 Q20.0156 18.1875 19.3438 18.8438 Q18.6719 19.4844 17.7656 19.4844 L8.7344 19.4844 Q7.8281 19.4844 7.1562 18.8438 Q6.4844 18.1875 6.4844 17.2344 L6.4844 4.2656 Q6.4844 3.3125 7.1562 2.6719 Q7.8281 2.0156 8.7344 2.0156 L17.7656 2.0156 ZM17.7656 3.5 L8.7344 3.5 Q8.4531 3.5 8.2344 3.7188 Q8.0156 3.9375 8.0156 4.2656 L8.0156 17.2344 Q8.0156 17.5625 8.2344 17.7812 Q8.4531 18 8.7344 18 L17.7656 18 Q18.0469 18 18.2656 17.7812 Q18.4844 17.5625 18.4844 17.2344 L18.4844 4.2656 Q18.4844 3.9375 18.2656 3.7188 Q18.0469 3.5 17.7656 3.5 Z");
        copyItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath refreshItemIcon = new SVGPath();
        refreshItemIcon.setContent("M17.6719 6.3281 L20.0156 3.9844 L20.0156 11.0156 L12.9844 11.0156 L16.2188 7.7812 Q15.375 6.9375 14.2969 6.4688 Q13.2188 6 12 6 Q10.3594 6 8.9688 6.7969 Q7.5938 7.5938 6.7969 8.9844 Q6 10.3594 6 12 Q6 13.6406 6.7969 15.0312 Q7.5938 16.4062 8.9688 17.2031 Q10.3594 18 12 18 L12 18 Q13.7344 18 15.3906 16.8281 Q17.0625 15.6562 17.6719 14.0156 L19.7344 14.0156 Q19.0312 16.6406 16.8906 18.3281 Q14.7656 20.0156 12 20.0156 Q9.8438 20.0156 7.9844 18.9375 Q6.1406 17.8594 5.0781 16.0156 Q4.0312 14.1562 4.0312 12 Q4.0312 9.8438 5.0781 8 Q6.1406 6.1406 7.9844 5.0625 Q9.8438 3.9844 12 3.9844 L12 3.9844 Q13.3594 3.9844 15.0156 4.6875 Q16.6875 5.3906 17.6719 6.3281 Z");
        refreshItemIcon.setScaleX(0.7);
        refreshItemIcon.setScaleY(0.7);
        refreshItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath deleteItemIcon = new SVGPath();
        deleteItemIcon.setContent("M16.5156 11.6406 Q17.9531 11.6406 19.25 12.3906 Q20.5469 13.125 21.2656 14.4062 Q21.9844 15.6719 21.9844 17.1406 Q21.9844 18.5938 21.2656 19.875 Q20.5469 21.1406 19.25 21.8906 Q17.9531 22.625 16.4844 22.625 Q15.0312 22.625 13.75 21.8906 Q12.4844 21.1406 11.7344 19.9062 Q10.9844 18.6562 10.9844 17.1406 Q10.9844 15.625 11.7344 14.375 Q12.4844 13.125 13.75 12.3906 Q15.0312 11.6406 16.5156 11.6406 ZM12 1.375 Q13.2969 1.375 14.2344 2.2656 Q15.1719 3.1406 15.2656 4.4375 L15.2656 4.625 L20.5 4.625 Q20.7812 4.625 21 4.8438 Q21.2188 5.0625 21.2344 5.3594 Q21.2656 5.6406 21.0625 5.8594 Q20.875 6.0781 20.5938 6.125 L19.7344 6.125 L19.2031 11.2031 Q18.4844 10.9219 17.7656 10.7812 L18.1875 6.125 L5.8125 6.125 L7.0625 19.0312 Q7.1094 19.4688 7.4219 19.7812 Q7.7344 20.0938 8.2031 20.1406 L10.75 20.1406 Q11.1875 20.9531 11.8125 21.625 L8.2969 21.625 Q7.2969 21.625 6.5312 20.9844 Q5.7656 20.3281 5.6094 19.3281 L4.2656 6.125 L3.5 6.125 Q3.2188 6.125 3 5.9531 Q2.7812 5.7812 2.7344 5.5 L2.7344 5.4062 Q2.7344 5.1094 2.9219 4.9062 Q3.125 4.6875 3.4062 4.625 L8.7344 4.625 Q8.7344 3.2812 9.6875 2.3281 Q10.6562 1.375 12 1.375 ZM13.7344 14.2344 L13.625 14.2812 L13.5781 14.375 Q13.3906 14.6094 13.5781 14.9062 L15.7969 17.1562 L13.5781 19.375 Q13.3906 19.6562 13.5781 19.9375 L13.7344 20.0469 Q14.0156 20.2344 14.2969 20.0469 L16.5156 17.8281 L18.7188 20.0469 Q19.0156 20.2344 19.2969 20.0469 L19.3906 19.9375 Q19.5781 19.6562 19.3906 19.375 L17.1875 17.1562 L19.3906 14.9062 Q19.5781 14.6094 19.3906 14.375 L19.2969 14.2344 Q19.0156 14.0469 18.7188 14.2344 L16.5156 16.4375 L14.2969 14.2344 Q14.0625 14.0938 13.7812 14.1875 L13.7344 14.2344 ZM12 2.9062 Q11.3281 2.9062 10.8125 3.3594 Q10.3125 3.8125 10.2656 4.4844 L10.2656 4.625 L13.7344 4.625 Q13.7344 3.9062 13.2188 3.4062 Q12.7188 2.9062 12 2.9062 Z");
        deleteItemIcon.setScaleX(0.7);
        deleteItemIcon.setScaleY(0.7);
        deleteItemIcon.setFill(Color.valueOf("#9f453c"));

        SVGPath renameItemIcon = new SVGPath();
        renameItemIcon.setContent("M11.0156 20.0156 L15 15.9844 L21 15.9844 L21 20.0156 L11.0156 20.0156 ZM6.1875 18 L14.8594 9.3281 L13.6406 8.1094 L5.0156 16.7812 L5.0156 18 L6.1875 18 ZM18.4219 5.8125 Q19.0312 6.4219 19.0312 7.2188 Q19.0312 8.0156 18.4219 8.625 L7.0312 20.0156 L3 20.0156 L3 15.9375 Q14.25 4.7344 14.3906 4.5938 Q15 3.9844 15.7969 3.9844 Q16.5938 3.9844 17.2031 4.5938 L18.4219 5.8125 Z");
        renameItemIcon.setScaleX(0.7);
        renameItemIcon.setScaleY(0.7);
        renameItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath cutItemIcon = new SVGPath();
        cutItemIcon.setContent("M10.0156 6.4844 Q10.0156 5.0625 8.9844 4.0312 Q7.9531 3 6.5 3 Q5.0625 3 4.0312 4.0312 Q3 5.0625 3 6.4844 Q3 7.9375 4.0312 8.9688 Q5.0625 10 6.5 10 Q6.9375 10 7.3438 9.8906 Q7.7656 9.7656 8.1094 9.5781 L10.4844 12.1094 L8.1562 14.4375 Q7.7812 14.2188 7.375 14.1094 Q6.9688 13.9844 6.5 13.9844 Q5.0625 13.9844 4.0312 15.0156 Q3 16.0469 3 17.5 Q3 18.9375 4.0312 19.9688 Q5.0625 21 6.5 21 Q7.9531 21 8.9844 19.9688 Q10.0156 18.9375 10.0156 17.5 Q10.0156 17.0312 9.8906 16.625 Q9.7812 16.2188 9.5625 15.8438 L11.8438 13.5625 L17 19.125 Q17.4219 19.5469 17.9688 19.7656 Q18.5156 19.9844 19.125 19.9844 L22.0156 19.9844 L9.3594 8.5 Q9.6562 8.0781 9.8281 7.5625 Q10.0156 7.0469 10.0156 6.4844 L10.0156 6.4844 ZM6.5 7.9844 Q5.8906 7.9844 5.4375 7.5625 Q5 7.125 5 6.4844 Q5 5.875 5.4375 5.4375 Q5.8906 4.9844 6.5 4.9844 Q7.125 4.9844 7.5625 5.4375 Q8 5.875 8 6.4844 Q8 7.125 7.5625 7.5625 Q7.125 7.9844 6.5 7.9844 ZM6.5 19 Q5.8906 19 5.4375 18.5625 Q5 18.1094 5 17.5 Q5 16.875 5.4375 16.4375 Q5.8906 16 6.5 16 Q7.125 16 7.5625 16.4375 Q8 16.875 8 17.5 Q8 18.1094 7.5625 18.5625 Q7.125 19 6.5 19 ZM17 4.875 L13.2969 9.2969 L14.7031 10.7031 L22.0156 4 L19.125 4 Q18.5156 4 17.9688 4.2344 Q17.4219 4.4531 17 4.875 L17 4.875 Z");
        cutItemIcon.setScaleX(0.7);
        cutItemIcon.setScaleY(0.7);
        cutItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath pasteItemIcon = new SVGPath();
        pasteItemIcon.setContent("M18.9844 21.0234 L18.9844 4.9922 L17.0156 4.9922 L17.0156 7.9922 L6.9844 7.9922 L6.9844 4.9922 L5.0156 4.9922 L5.0156 21.0234 L18.9844 21.0234 ZM12.7031 3.3047 Q12.4219 3.0234 12 3.0234 Q11.5781 3.0234 11.2969 3.3047 Q11.0156 3.5859 11.0156 4.0078 Q11.0156 4.4297 11.2969 4.7109 Q11.5781 4.9922 12 4.9922 Q12.4219 4.9922 12.7031 4.7109 Q12.9844 4.4297 12.9844 4.0078 Q12.9844 3.5859 12.7031 3.3047 ZM18.9844 3.0234 Q19.7812 3.0234 20.3906 3.6172 Q21 4.1953 21 4.9922 L21 21.0234 Q21 21.8203 20.3906 22.4141 Q19.7812 22.9922 18.9844 22.9922 L5.0156 22.9922 Q4.2188 22.9922 3.6094 22.4141 Q3 21.8203 3 21.0234 L3 4.9922 Q3 4.1953 3.6094 3.6172 Q4.2188 3.0234 5.0156 3.0234 L9.1875 3.0234 Q9.5156 2.1328 10.2656 1.5703 Q11.0156 1.0078 12 1.0078 Q12.9844 1.0078 13.7344 1.5703 Q14.4844 2.1328 14.8125 3.0234 L18.9844 3.0234 Z");
        pasteItemIcon.setScaleX(0.65);
        pasteItemIcon.setScaleY(0.65);
        pasteItemIcon.setFill(Color.valueOf("#074675"));


        SVGPath newFileItemIcon = new SVGPath();
        newFileItemIcon.setContent("M12 9.7656 Q12.3125 9.7656 12.5156 9.9688 Q12.7188 10.1562 12.7188 10.4844 L12.7188 12.7188 L15.0469 12.7188 Q15.2812 12.7188 15.5156 12.9688 Q15.7656 13.2031 15.7656 13.5312 Q15.7656 13.8438 15.5156 14.0469 Q15.2812 14.2344 15.0469 14.2344 L12.7188 14.2344 L12.7188 16.4844 Q12.7188 16.7969 12.5156 17.0469 Q12.3125 17.2812 12 17.2812 Q11.6875 17.2812 11.4844 17.0469 Q11.2812 16.7969 11.2812 16.4844 L11.2812 14.2344 L9.0469 14.2344 Q8.7188 14.2344 8.4688 14.0469 Q8.2344 13.8438 8.2344 13.5312 Q8.2344 13.2031 8.4688 12.9688 Q8.7188 12.7188 9.0469 12.7188 L11.2812 12.7188 L11.2812 10.4844 Q11.2812 10.1562 11.4844 9.9688 Q11.6875 9.7656 12 9.7656 ZM21.0469 6.7188 L21.0469 20.9531 Q21.0469 22.2344 20.1562 23.125 Q19.2812 24 18 24 L6 24 Q4.7188 24 3.8281 23.125 Q2.9531 22.2344 2.9531 21.0469 L2.9531 2.9531 Q3.0469 1.7656 3.875 0.8906 Q4.7188 0 6 0 L14.2344 0 L21.0469 6.7188 ZM16.4844 6.7188 Q15.5938 6.7188 14.9062 6.0781 Q14.2344 5.4375 14.2344 4.4844 L14.2344 1.5156 L6 1.5156 Q5.3594 1.5156 4.9219 1.9688 Q4.4844 2.4062 4.4844 2.9531 L4.4844 20.9531 Q4.4844 21.5938 4.9219 22.0469 Q5.3594 22.4844 6 22.4844 L18 22.4844 Q18.6406 22.4844 19.0781 22.0469 Q19.5156 21.5938 19.5156 20.9531 L19.5156 6.7188 L16.4844 6.7188 Z");
        newFileItemIcon.setScaleX(0.65);
        newFileItemIcon.setScaleY(0.6);
        newFileItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath newFolderItemIcon = new SVGPath();
        newFolderItemIcon.setContent("M18.4375 12.0234 Q19.6875 12.0234 20.7812 12.6484 Q21.8906 13.2734 22.5156 14.3828 Q23.1406 15.4766 23.1406 16.7266 Q23.1406 17.9766 22.5156 19.0859 Q21.8906 20.1953 20.7812 20.8203 Q19.6875 21.4297 18.4375 21.4297 Q16.4688 21.4297 15.0938 20.0703 Q13.7344 18.6953 13.7344 16.7266 Q13.7344 14.7578 15.0938 13.3984 Q16.4688 12.0234 18.4375 12.0234 ZM18.4375 13.7578 L18.3438 13.7578 Q18.0469 13.8047 18 14.0859 L18 16.3047 L15.7969 16.3047 Q15.5 16.3516 15.4531 16.6328 L15.4531 16.8203 Q15.5 17.1172 15.7969 17.1641 L18 17.1641 L18 19.3672 Q18.0469 19.6641 18.3438 19.7109 L18.5312 19.7109 Q18.8125 19.6641 18.8594 19.3672 L18.8594 17.1641 L21.0781 17.1641 Q21.3594 17.1172 21.4062 16.8203 L21.4062 16.6328 Q21.3594 16.3516 21.0781 16.3047 L18.8594 16.3047 L18.8594 14.0859 Q18.8125 13.8047 18.5312 13.7578 L18.4375 13.7578 ZM8.7344 2.5703 Q9.3594 2.5703 9.8438 2.9609 L12 4.7266 L20.3594 4.7266 Q21.0781 4.7266 21.625 5.2109 Q22.1719 5.6953 22.2656 6.4141 L22.2656 12.6953 Q21.7031 12.1797 21.0312 11.7891 L21.0312 6.6484 Q20.9688 6.4141 20.8281 6.2422 Q20.6875 6.0703 20.4531 6.0234 L12 6.0234 L9.9844 7.7109 Q9.5 8.0859 8.9219 8.1328 L3.0312 8.1328 L3.0312 17.7891 Q3.0312 18.0234 3.1719 18.2266 Q3.3125 18.4141 3.5469 18.4609 L13.1094 18.4609 Q13.3438 19.1328 13.7344 19.7578 L3.6406 19.7578 Q2.875 19.7578 2.3281 19.2266 Q1.7812 18.6953 1.7344 17.9297 L1.7344 4.5391 Q1.7344 3.7734 2.2344 3.2266 Q2.7344 2.6641 3.5 2.6172 L8.7344 2.5703 ZM8.7344 3.8672 L3.6406 3.8672 Q3.4062 3.8672 3.2188 4.0391 Q3.0312 4.2109 3.0312 4.4453 L3.0312 6.8984 L8.7344 6.8984 Q8.9219 6.8984 9.0781 6.7891 L10.7969 5.3516 L9.1719 4.0078 Q9.0312 3.9141 8.8281 3.8672 L8.7344 3.8672 Z");
        newFolderItemIcon.setScaleX(0.7);
        newFolderItemIcon.setScaleY(0.7);
        newFolderItemIcon.setFill(Color.valueOf("#074675"));

        SVGPath newRootFolderItemIcon = new SVGPath();
        newRootFolderItemIcon.setContent("M18.4375 12.0234 Q19.6875 12.0234 20.7812 12.6484 Q21.8906 13.2734 22.5156 14.3828 Q23.1406 15.4766 23.1406 16.7266 Q23.1406 17.9766 22.5156 19.0859 Q21.8906 20.1953 20.7812 20.8203 Q19.6875 21.4297 18.4375 21.4297 Q16.4688 21.4297 15.0938 20.0703 Q13.7344 18.6953 13.7344 16.7266 Q13.7344 14.7578 15.0938 13.3984 Q16.4688 12.0234 18.4375 12.0234 ZM18.4375 13.7578 L18.3438 13.7578 Q18.0469 13.8047 18 14.0859 L18 16.3047 L15.7969 16.3047 Q15.5 16.3516 15.4531 16.6328 L15.4531 16.8203 Q15.5 17.1172 15.7969 17.1641 L18 17.1641 L18 19.3672 Q18.0469 19.6641 18.3438 19.7109 L18.5312 19.7109 Q18.8125 19.6641 18.8594 19.3672 L18.8594 17.1641 L21.0781 17.1641 Q21.3594 17.1172 21.4062 16.8203 L21.4062 16.6328 Q21.3594 16.3516 21.0781 16.3047 L18.8594 16.3047 L18.8594 14.0859 Q18.8125 13.8047 18.5312 13.7578 L18.4375 13.7578 ZM8.7344 2.5703 Q9.3594 2.5703 9.8438 2.9609 L12 4.7266 L20.3594 4.7266 Q21.0781 4.7266 21.625 5.2109 Q22.1719 5.6953 22.2656 6.4141 L22.2656 12.6953 Q21.7031 12.1797 21.0312 11.7891 L21.0312 6.6484 Q20.9688 6.4141 20.8281 6.2422 Q20.6875 6.0703 20.4531 6.0234 L12 6.0234 L9.9844 7.7109 Q9.5 8.0859 8.9219 8.1328 L3.0312 8.1328 L3.0312 17.7891 Q3.0312 18.0234 3.1719 18.2266 Q3.3125 18.4141 3.5469 18.4609 L13.1094 18.4609 Q13.3438 19.1328 13.7344 19.7578 L3.6406 19.7578 Q2.875 19.7578 2.3281 19.2266 Q1.7812 18.6953 1.7344 17.9297 L1.7344 4.5391 Q1.7344 3.7734 2.2344 3.2266 Q2.7344 2.6641 3.5 2.6172 L8.7344 2.5703 ZM8.7344 3.8672 L3.6406 3.8672 Q3.4062 3.8672 3.2188 4.0391 Q3.0312 4.2109 3.0312 4.4453 L3.0312 6.8984 L8.7344 6.8984 Q8.9219 6.8984 9.0781 6.7891 L10.7969 5.3516 L9.1719 4.0078 Q9.0312 3.9141 8.8281 3.8672 L8.7344 3.8672 Z");
        newRootFolderItemIcon.setScaleX(0.7);
        newRootFolderItemIcon.setScaleY(0.7);
        newRootFolderItemIcon.setFill(Color.valueOf("#074675"));

        MenuItem copyItem = new MenuItem("复制 ( Copy )                               Ctrl+C",new Group(copyItemIcon));
        MenuItem cutItem = new MenuItem("剪切 ( Cut )                                  Ctrl+X",new Group(cutItemIcon));
        MenuItem pasteItem = new MenuItem("粘贴 ( Paste )                               Ctrl+V",new Group(pasteItemIcon));
        MenuItem renameItem = new MenuItem("重命名 ( Rename )",new Group(renameItemIcon));
        MenuItem newFileItem = new MenuItem("新建文件 ( New File )                   Ctrl+N",new Group(newFileItemIcon));
        MenuItem newFolderItem = new MenuItem("新建文件夹 ( New Folder )",new Group(newFolderItemIcon));
        MenuItem newRootFolderItem = new MenuItem("新建主目录 ( New Root Folder )",new Group(newRootFolderItemIcon));
        MenuItem deleteItem = new MenuItem("删除 ( Delete )                             Delete",new Group(deleteItemIcon));
        MenuItem refreshItem = new MenuItem("刷新 ( Refresh )",new Group(refreshItemIcon));

        //绑定操作在第一次执行时无效，鼠标点击一次后有效，如果与按键一起设置，可能重复执行两次
        //copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        //cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        //pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        //newFileItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        //deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        //绑定操作在第一次执行时无效，鼠标点击一次后有效
        treeView.setOnKeyPressed(event -> {
            if(event.isControlDown()&&event.getCode() == KeyCode.C){
                copyItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.X){
                cutItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.V){
                pasteItem.fire();
            }
            if(event.isControlDown()&&event.getCode() == KeyCode.N){
                newFileItem.fire();
            }
            if(event.getCode() == KeyCode.DELETE){
                deleteItem.fire();
            }

        });




        // 复制到程序内部 + 系统剪贴板
        copyItem.setOnAction(e -> {
            ObservableList<TreeItem<Markdown>> selectedItems = treeView.getSelectionModel().getSelectedItems();
            copyFiles(selectedItems);
        });

        // 剪切，仅程序内部
        cutItem.setOnAction(e -> {
            ObservableList<TreeItem<Markdown>> selectedItems = treeView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) return;

            // 筛选出顶层节点（排除子节点）
            List<TreeItem<Markdown>> topLevelItems = filterTopLevelItems(selectedItems);
            if (topLevelItems.isEmpty()) return;

            for (TreeItem<Markdown> item : topLevelItems) {
                File file = item.getValue().getFile();
                if (TabpaneUtil.findCustomMarkdownTab(file.toPath()) != null) {
                    if(file.isDirectory())
                    AlterUtil.CustomAlert("错误", "文件夹【" + file.getName() + "】中有文件正在被打开，请关闭文件后重试!");
                    else
                        AlterUtil.CustomAlert("错误", "文件【" + file.getName() + "】正在被打开，请关闭文件后重试!");
                    return;
                }
            }

            clipboardFiles = new ArrayList<>(topLevelItems);
            cutOperation = true;
        });

        // 粘贴
        pasteItem.setOnAction(e -> {
            selectedTreeItem=treeView.getSelectionModel().getSelectedItem();
            pasteFiles(selectedTreeItem);
        });



        renameItem.setOnAction(e -> {
            selectedTreeItem=treeView.getSelectionModel().getSelectedItem();
            File file = selectedTreeItem.getValue().getFile();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("重命名");
            alert.setHeaderText("");
            alert.setGraphic(null); //避免显示问号
            //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
            Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
            alterstage.getIcons().add(new Image("file:images/logo.png"));
            HBox hbox = new HBox();
            hbox.getChildren().add(new Label("请输入重命名名称  "));
            hbox.setAlignment(Pos.CENTER_LEFT);
            CustomUserTextField textField = new CustomUserTextField();
            textField.setPrefWidth(200);
            textField.setText(selectedTreeItem.getValue().getName());
            textField.positionCaret(textField.getText().length());
            hbox.getChildren().add(textField);
            alert.getDialogPane().setContent(hbox);

            // 自定义按钮
            ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
            Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
            button.requestFocus();
            textField.requestFocus();
            ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
            String newName=textField.getText();
            if (result == buttonTypeOk) {
                if (newName.equals(file.getName()) || newName.isBlank()) return;
                try {
                    Path newPath = file.toPath().resolveSibling(newName);
                    if(file.isDirectory()){
                        if(TabpaneUtil.findCustomMarkdownTab(file.toPath())!=null){
                            AlterUtil.CustomAlert("错误","文件夹【"+file.getName()+"】中有文件正在被打开，请关闭文件后重试!");
                            return;
                        }
                    }
                    if (Files.exists(newPath)) {
                        AlterUtil.CustomAlert("错误","已存在同名文件/文件夹!");
                        return;
                    }
                    Files.move(file.toPath(), newPath);
                    selectedTreeItem.getValue().setFile(newPath.toFile());
                    selectedTreeItem.getValue().setName(newPath.toFile().getName());
                    if(newPath.toFile().isDirectory())
                    refreshNode(selectedTreeItem);
                    TabpaneUtil.renameCustomMarkdownTab(file.toPath(),newPath);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    AlterUtil.CustomAlert("错误",ex.getMessage());
                }
            }
        });

        newFileItem.setOnAction(e ->{
            selectedTreeItem=treeView.getSelectionModel().getSelectedItem();
            createNewFile(selectedTreeItem, false);
        } );
        newFolderItem.setOnAction(e -> {
            selectedTreeItem=treeView.getSelectionModel().getSelectedItem();
            createNewFile(selectedTreeItem, true);
        });
        newRootFolderItem.setOnAction(e -> {
            selectedTreeItem=treeView.getSelectionModel().getSelectedItem();
            createNewFile(selectedTreeItem, true);
        });
        deleteItem.setOnAction(e -> {
            ObservableList<TreeItem<Markdown>> selectedItems = treeView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) return;

            // 筛选出顶层节点（排除子节点）
            List<TreeItem<Markdown>> topLevelItems = filterTopLevelItems(selectedItems);
            if (topLevelItems.isEmpty()) return;

            // 确认删除对话框
            String msg = topLevelItems.size() > 1
                    ? "确定要删除选中的 " + topLevelItems.size() + " 个项目（含其子内容）吗？"
                    : "确定要删除【" + topLevelItems.get(0).getValue().getName() + "】（含其子内容）吗？";

            if (AlterUtil.CustomAlertConfirm("删除文件", msg)) {
                try {
                    Set<TreeItem<Markdown>> parentNodes = new HashSet<>();

                    for (TreeItem<Markdown> item : topLevelItems) {
                        File file = item.getValue().getFile();
                        if(file.isDirectory()&&TabpaneUtil.findCustomMarkdownTab(file.toPath())!=null){
                            AlterUtil.CustomAlert("错误","文件夹【"+file.getName()+"】中有文件正在被打开，请关闭文件后重试!");
                            return;
                        }
                        deleteRecursively(file.toPath()); // 物理删除（包含子内容）
                        parentNodes.add(item.getParent());
                        item.getParent().getChildren().remove(item); // 从树中移除
                    }

                    // 刷新父节点
                    parentNodes.forEach(MarkdownUtil::refreshNode);

                    // 选中第一个父节点保持焦点
                    if (!parentNodes.isEmpty()) {
                        TreeItem<Markdown> parent = parentNodes.iterator().next();
                        treeView.getSelectionModel().clearSelection();
                        /*
                        if(parent.equals(treeView.getRoot())){
                            treeView.getSelectionModel().select(parent.getChildren().get(0));
                        }else{
                            treeView.getSelectionModel().select(parent);
                        }

                         */
                        treeView.getSelectionModel().select(parent);
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                    AlterUtil.CustomAlert("错误", e1.getMessage());
                }
            }
        });
        refreshItem.setOnAction(e -> {
            selectedTreeItem=treeView.getSelectionModel().getSelectedItem();
            //File file = selectedTreeItem.getValue().getFile();
            refreshNode(selectedTreeItem);
        });
        contextMenu.getItems().addAll(copyItem, cutItem,renameItem);

        treeView.setContextMenu(contextMenu);
        contextMenu.setOnShowing(event -> {
            // 获取所有选中的节点
            ObservableList<TreeItem<Markdown>> selectedItems = treeView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                contextMenu.getItems().clear();
                contextMenu.getItems().addAll(newFileItem,newFolderItem,pasteItem,refreshItem);
                return;
            }

            // 判断是否为多选（选中数量 > 1）
            boolean isMultiSelect = selectedItems.size() > 1;

            // 清空菜单
            contextMenu.getItems().clear();

            if (isMultiSelect) {
                // 多选场景：只保留复制、剪切、删除
                contextMenu.getItems().addAll(copyItem, cutItem, deleteItem);
            } else {
                // 单选场景：根据节点类型显示完整菜单
                TreeItem<Markdown> singleItem = selectedItems.get(0);
                boolean isDirectory = singleItem.getValue().getFile().isDirectory();

                // 基础选项：复制、剪切、重命名、删除
                List<MenuItem> baseItems = new ArrayList<>(Arrays.asList(copyItem, cutItem, renameItem, deleteItem));

                if (isDirectory) {
                    // 单选文件夹：添加粘贴和新建类选项
                    baseItems.add(0,newFileItem);
                    baseItems.add(1,newFolderItem);
                    baseItems.add(4, pasteItem); // 在删除前插入粘贴'
                    baseItems.add(5,refreshItem);
                    //baseItems.addAll(Arrays.asList(newFileItem, newFolderItem, refreshItem));
                }

                contextMenu.getItems().addAll(baseItems);
            }
        });


        /*
        if (file.isDirectory()) {
            return new ContextMenu(copyItem, cutItem, pasteItem, renameItem, newFileItem, newFolderItem, deleteItem, refreshItem);
        } else {
            return new ContextMenu(copyItem, cutItem, renameItem, deleteItem);
        }

         */
    }
    /**
     * 从选中的节点列表中筛选出顶层节点（排除被其他选中节点包含的子节点）
     */
    public static List<TreeItem<Markdown>> filterTopLevelItems(List<TreeItem<Markdown>> selectedItems) {
        // 先按路径长度排序（短路径可能是父节点）
        List<TreeItem<Markdown>> sorted = new ArrayList<>(selectedItems);
        sorted.sort((a, b) -> {
            int lenA = a.getValue().getFile().toPath().getNameCount();
            int lenB = b.getValue().getFile().toPath().getNameCount();
            return Integer.compare(lenA, lenB); // 短路径在前
        });

        Set<TreeItem<Markdown>> topLevel = new HashSet<>();
        for (TreeItem<Markdown> item : sorted) {
            Path itemPath = item.getValue().getFile().toPath();
            boolean isChild = false;
            // 只需要判断已加入的顶层节点是否为当前节点的父节点
            for (TreeItem<Markdown> existing : topLevel) {
                Path existingPath = existing.getValue().getFile().toPath();
                if (isPathContained(existingPath, itemPath)) {
                    isChild = true;
                    break;
                }
            }
            if (!isChild) {
                topLevel.add(item);
            }
        }
        return new ArrayList<>(topLevel);
    }


    public static void copyFiles(ObservableList<TreeItem<Markdown>> selectedItems){
        //ObservableList<TreeItem<Markdown>> selectedItems = treeView.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) return;

        // 筛选出顶层节点（排除子节点）
        List<TreeItem<Markdown>> topLevelItems = filterTopLevelItems(selectedItems);
        if (topLevelItems.isEmpty()) return;

        // 保存所有选中的节点到程序内部剪贴板
        clipboardFiles = new ArrayList<>(topLevelItems); // 新增一个List<TreeItem<Markdown>>变量存储多选节点
        cutOperation = false;

        // 同时复制到系统剪贴板（支持文件粘贴到系统其他地方）
        List<File> files = topLevelItems.stream()
                .map(item -> item.getValue().getFile())
                .collect(Collectors.toList());
        //ClipboardContent content = new ClipboardContent();
        //content.putFiles(files);
        //Clipboard.getSystemClipboard().setContent(content);
        copyFilesToClipboard(files);
    }

    public static void copyFilesToClipboard(List<File> files) {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{ DataFlavor.javaFileListFlavor };
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.javaFileListFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) {
                return files;
            }
        };

        clipboard.setContents(transferable, null);
    }

    public static void pasteFiles(TreeItem<Markdown> targetItem){
        //ObservableList<TreeItem<Markdown>> targetItems = treeView.getSelectionModel().getSelectedItems();
        //if (targetItem==null) return; // 目标目录不能为空
        if(targetItem==null) targetItem=treeView.getRoot();

        // 目标只能是一个文件夹（取第一个选中的文件夹）
        File targetDirFile = targetItem.getValue().getFile();
        if (!targetDirFile.isDirectory()) {
            //AlterUtil.CustomAlert("错误", "目标必须是文件夹");
            return;
        }
        Path targetDirPath = targetDirFile.toPath();

        // 1. 收集待粘贴的文件列表（优先内部剪贴板，其次系统剪贴板）
        List<File> filesToPaste = new ArrayList<>();

        // 检查程序内部剪贴板（剪切/复制的文件）
        if (clipboardFiles != null && !clipboardFiles.isEmpty()) {
            filesToPaste.addAll(clipboardFiles.stream()
                    .map(item -> item.getValue().getFile())
                    .collect(Collectors.toList()));
        }
        // 检查系统剪贴板（外部文件）
        else {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasFiles()) {
                filesToPaste.addAll(clipboard.getFiles());
            } else {
                AlterUtil.CustomAlert("提示", "剪贴板中没有可粘贴的文件！");
                return;
            }
        }

        try {
            for (File srcFile : filesToPaste) {
                Path srcPath = srcFile.toPath();
                Path targetPath = targetDirPath.resolve(srcFile.getName());

                // 检测1：目标是否已存在同名文件/文件夹
                if (Files.exists(targetPath)) {
                    AlterUtil.CustomAlert("错误", "目标已存在同名文件/文件夹【 " + srcFile.getName()+"】");

                    return;
                }

                // 检测2：若源是文件夹，判断目标是否是源的子目录（避免循环）
                if (srcFile.isDirectory() && isPathContained(srcPath, targetPath)) {
                    AlterUtil.CustomAlert("错误", "不能将文件夹【"+srcFile.getName()+"】粘贴到其自身的子目录中！");
                        return;
                }

                // 执行操作（剪切/复制）
                if (clipboardFiles != null && cutOperation) { // 内部剪切操作
                    Files.move(srcPath, targetPath);
                    // 从原位置移除节点（仅内部文件需要）
                    clipboardFiles.stream()
                            .filter(item -> item.getValue().getFile().equals(srcFile))
                            .findFirst()
                            .ifPresent(item -> item.getParent().getChildren().remove(item));
                } else { // 复制操作（内部复制或外部文件粘贴）
                    if (srcFile.isDirectory()) {
                        copyDirectory(srcPath, targetPath);
                    } else {
                        Files.copy(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            // 刷新目标文件夹节点
            refreshNode(targetItem);
            // 清空内部剪贴板状态
            if (clipboardFiles != null) {
                clipboardFiles = null;
                cutOperation = false;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            AlterUtil.CustomAlert("粘贴错误", ex.getMessage());
        }
    }

    public static void createNewFile(TreeItem<Markdown> treeItem, boolean isFolder) {
        if(treeItem==null) treeItem=treeView.getRoot();
        File parent=treeItem.getValue().getFile();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(isFolder?"新建文件夹":"新建文件");
        alert.setHeaderText("");
        alert.setGraphic(null); //避免显示问号
        //alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getScene().getStylesheets().add(MetadataTreeviewUtil.class.getResource("/com/dbboys/css/app.css").toExternalForm());
        Stage alterstage = (Stage) alert.getDialogPane().getScene().getWindow();
        alterstage.getIcons().add(new Image("file:images/logo.png"));
        HBox hbox = new HBox();
        hbox.getChildren().add(new Label("请输入"+(isFolder?"文件夹":"文件")+"名称  "));
        hbox.setAlignment(Pos.CENTER_LEFT);
        CustomUserTextField textField = new CustomUserTextField();
        textField.setPrefWidth(200);
        textField.setText(isFolder?"新建文件夹":"新建markdown文档.md");
        textField.positionCaret(textField.getText().length());
        hbox.getChildren().add(textField);
        alert.getDialogPane().setContent(hbox);

        // 自定义按钮
        ButtonType buttonTypeOk = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Button button = (Button) alert.getDialogPane().lookupButton(buttonTypeOk);
        button.requestFocus();
        textField.requestFocus();
        ButtonType result = alert.showAndWait().orElse(buttonTypeCancel);
        if (result == buttonTypeOk) {
            try {
                Path newPath = parent.toPath().resolve(textField.getText());
                if (Files.exists(newPath)) {
                    AlterUtil.CustomAlert("错误","文件/文件夹已存在！");
                    return;
                }
                if (isFolder) Files.createDirectory(newPath);
                else Files.createFile(newPath);

                refreshNode(treeItem);
                for(TreeItem<Markdown> item : treeItem.getChildren()){
                    if(item.getValue().getFile().getAbsolutePath().equals(newPath.toString())) {
                        treeView.getSelectionModel().clearSelection();
                        treeView.getSelectionModel().select(item);
                        break;
                    }
                }
                if(!isFolder){
                    TabpaneUtil.addCustomMarkdownTab(treeView.getSelectionModel().getSelectedItem().getValue().getFile(),true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                AlterUtil.CustomAlert("错误",e.getMessage());
            }
        }
    }

    /**
     * 判断目标路径是否是源路径的子目录（避免循环操作）
     */
    public static boolean isPathContained(Path source, Path target) {
        try {
            // 标准化源路径：存在则用 toRealPath()，不存在则用绝对路径标准化
            Path normalizedSource = normalizePath(source);
            // 标准化目标路径：同上
            Path normalizedTarget = normalizePath(target);

            // 检查根目录是否一致（跨文件系统直接返回 false）
            if (!Objects.equals(normalizedSource.getRoot(), normalizedTarget.getRoot())) {
                return false;
            }

            // 检查目标路径是否以源路径为前缀（包含关系）
            return normalizedTarget.startsWith(normalizedSource);

        } catch (IOException e) {
            // 其他 IO 异常（如权限不足），默认返回 false
            return false;
        }
    }

    /**
     * 标准化路径：存在则用 toRealPath()，不存在则用 absolute().normalize()
     */
    private static Path normalizePath(Path path) throws IOException {
        if (Files.exists(path)) {
            // 路径存在：解析为真实路径（处理符号链接、相对路径等）
            return path.toRealPath();
        } else {
            // 路径不存在：转换为绝对路径并标准化（处理 ./ 和 ../）
            return path.toAbsolutePath().normalize();
        }
    }
    private static void deleteFile(TreeItem<Markdown> treeItem) {
        if(treeItem.getParent().equals(treeView.getRoot())&&treeView.getRoot().getChildren().size()==1){
            AlterUtil.CustomAlert("错误","当前只有一个文件夹，不允许删除！");
            return;
        }
        File file=treeItem.getValue().getFile();
        if (AlterUtil.CustomAlertConfirm("删除文件","确定要删除"+(file.isFile()?("文件【"+file.getName()):("文件夹【"+file.getName()))+"】吗？")) {
            try {
                deleteRecursively(file.toPath());
                if(treeItem.getParent().equals(treeView.getRoot())){
                    treeView.getSelectionModel().select(treeView.getRoot().getChildren().get(0));
                }else{
                    treeView.getSelectionModel().select(treeItem.getParent());
                }
                treeItem.getParent().getChildren().remove(treeItem);
            } catch (IOException e) {
                e.printStackTrace();
                AlterUtil.CustomAlert("错误",e.getMessage());
            }
        }
    }

    public static void refreshNode(TreeItem<Markdown> node) {
        if(node == null) node=treeView.getRoot();
        node.getChildren().clear();
        buildChildren(node);

    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Files.deleteIfExists(file);
                    TabpaneUtil.removeCustomMarkdownTab(path);
                } catch (AccessDeniedException e) {
                    // 尝试解除只读或延迟重试
                    File f = file.toFile();
                    if (!f.canWrite()) f.setWritable(true);
                    Files.deleteIfExists(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.deleteIfExists(dir);
                } catch (AccessDeniedException e) {
                    // 防御性重试
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    Files.deleteIfExists(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }


    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(src -> {
            try {
                Path dest = target.resolve(source.relativize(src));
                if (Files.isDirectory(src)) Files.createDirectories(dest);
                else Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                AlterUtil.CustomAlert("错误",e.getMessage());
            }
        });
    }


}
