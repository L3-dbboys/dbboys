package com.dbboys.customnode;

import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CustomSpaceChart extends BarChart<Number, String> {
    private static final Logger log = LogManager.getLogger(CustomSpaceChart.class);

    /* ===================== 数据模型 ===================== */
    public static class SpaceUsage {
        private  int number;
        private  String label;
        private  String name;
        private  int isExtendable;
        private  double used;
        private  double total;
        private int extents;
        private  int usedPages;
        private  int totalPages;
        private  double metaUsed;
        private  double metaTotal;
        private double limitSize;

        public SpaceUsage(int number,String label,String name, int isExtendable, double total, double used, int extents, int totalPages, int usedPages, double metaTotal, double metaUsed) {
            this.isExtendable = isExtendable;
            this.number=number;
            this.name = name;
            this.label=label;
            this.used = used;
            this.total = total;
            this.extents = extents;
            this.usedPages = usedPages;
            this.totalPages = totalPages;
            this.metaTotal=metaTotal;
            this.metaUsed=metaUsed;
        }

        public String getName() { return name; }
        public double getUsed() { return used; }
        public double getTotal() { return total; }
        public double getUnused() { return total - used; }
        public double getUsagePercent() {
            return total == 0 ? 0 : used / total * 100;
        }
        public int getUsedPages() { return usedPages; }
        public int getTotalPages() { return totalPages; }
        public int getIsExtendable() { return isExtendable; }
        public int getExtents() { return extents; }
        public String getLabel() { return label; }
        public int getNumber() {return number; }
        public double getMetaTotal() {return metaTotal;}
        public double getMetaUsed() {return metaUsed;}
        public void setLimitSize(double limitSize){
            this.limitSize=limitSize;
        }
        public double getlimitSize() {return limitSize;}

    }


    /* ===================== 颜色常量 ===================== */
    private static final String COLOR_NORMAL = "#42b983";
    private static final String COLOR_WARNING = "#fcc30b";
    private static final String COLOR_DANGER = "#e84a43";
    private static final String COLOR_UNUSED = "#e0e0e0";
    private static final String COLOR_EXTENDABLE = "#3f80b0";

    /* ===================== 缩放相关配置 ===================== */
    private static final double HOVER_SCALE = 1.05; // 悬停时缩放比例（1.05=放大5%）
    private static final double NORMAL_SCALE = 1.0; // 正常状态缩放比例
    private static final double SCALE_DURATION = 150; // 缩放动画时长（毫秒）

    private final XYChart.Series<Number, String> series = new XYChart.Series<>();
    private NumberAxis xAxis;
    private ColorMode colorMode = ColorMode.DBSPACE;
    private boolean menuItemsDisabled = false;  //用于外部控制菜单是否需要禁用，如connect是只读，需要禁用但显示

    // 右键菜单事件回调接口（用于外部处理菜单点击逻辑）
    public interface ContextMenuListener {
        void onCreateDbspace(SpaceUsage spaceUsage,boolean isAddFile); // 查看详情
        void onDropDbspace(SpaceUsage spaceUsage); // 刷新数据
        void onDropDatafile(SpaceUsage spaceUsage);
        void onExpandDatafile(SpaceUsage spaceUsage);
        void onUnExpandDatafile(SpaceUsage spaceUsage);
        void onUnlimitedSpaceSize(SpaceUsage spaceUsage);
    }

    private ContextMenuListener contextMenuListener; // 外部设置的回调监听器

    // 提供setter方法，让外部设置菜单点击回调
    public void setContextMenuListener(ContextMenuListener listener) {
        this.contextMenuListener = listener;
    }

    public enum ColorMode {
        DBSPACE,   // 按使用率 %
        CHUNK,      // 按已使用值
        DATABASE,
        TABLE      // 按总容量
    }

    /* ===================== 构造器 ===================== */
    public CustomSpaceChart(List<SpaceUsage> data, ColorMode colorMode) {
        super(new NumberAxis(), createYAxis(data));

        this.xAxis = (NumberAxis) getXAxis();
        this.colorMode = colorMode;
        setAnimated(false);
        setBarGap(10);
        setCategoryGap(10);
        setLegendVisible(false);

        getData().add(series);

        updateXAxis(data);
        render(data);
    }

    private void updateXAxis(List<SpaceUsage> data) {
        double max = data.stream()
                .mapToDouble(SpaceUsage::getTotal)
                .max()
                .orElse(1);

        double niceMax = niceMax(max);
        double tick = niceTick(niceMax);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(niceMax);
        xAxis.setTickUnit(tick);

        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number n) {
                return String.format("%.1f", n.doubleValue());
            }
        });
    }

    private String resolveUsedColor(SpaceUsage u) {
        return switch (colorMode) {
            case DBSPACE -> colorForSpaces(u);
            case CHUNK -> colorForChunks(u);
            case DATABASE->colorForDatabases(u);
            case TABLE -> colorForTable(u);
        };
    }

    private String colorForSpaces(SpaceUsage u) {
        if (u.isExtendable > 0) {
            return COLOR_EXTENDABLE;
        }
        if (u.extents == 0 &&!u.getLabel().contains("[B]")) {  //blobspace的extents是0，需要排除
            return COLOR_NORMAL;
        }
        if (u.getUsagePercent() >= 90) return COLOR_DANGER;
        if (u.getUsagePercent() >= 80) return COLOR_WARNING;
        return COLOR_NORMAL;
    }
    private String colorForDatabases(SpaceUsage u) {
        return COLOR_NORMAL;
    }

    private String colorForTable(SpaceUsage u) {
        if (u.getUsedPages() >= 12000000) return COLOR_DANGER;
        if (u.getUsedPages() >= 10000000) return COLOR_WARNING;
        return COLOR_NORMAL;
    }

    private String colorForChunks(SpaceUsage u) {
        if(u.isExtendable>0)return COLOR_EXTENDABLE;
        return COLOR_NORMAL;                  // 小盘
    }

    private void updateYAxis(List<SpaceUsage> data) {
        List<String> names = new ArrayList<>(
                data.stream().map(SpaceUsage::getLabel).toList()
        );
        Collections.reverse(names);

        CategoryAxis yAxis = (CategoryAxis) getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.getCategories().clear(); // 清空旧分类，避免空白行

        yAxis.setCategories(FXCollections.observableArrayList(names));
    }

    /* ===================== 渲染主入口 ===================== */
    public void render(List<SpaceUsage> data) {
        updateXAxis(data);
        updateYAxis(data);
        series.getData().clear();
        int barHeight = 22;
        int chartHeight = data.size() * (barHeight) + 52;
        setPrefHeight(chartHeight);
        setMinHeight(chartHeight);

        for (SpaceUsage usage : data) {
            XYChart.Data<Number, String> bar =
                    new XYChart.Data<>(usage.getTotal(), usage.getLabel());

            series.getData().add(bar);

            Tooltip tooltip = createTooltip(usage);

            bar.nodeProperty().addListener((obs, o, node) -> {
                if (node instanceof Region r) {
                    applyBarStyle(r, usage);
                    Tooltip.install(r, tooltip);
                    // 给柱子添加鼠标悬停缩放+右键菜单功能
                    addHoverScaleAndContextMenuEffect(r, usage);
                }
            });

        }

        applyCss();
        //设置柱子区域背景色
        Node plot = lookup(".chart-plot-background");
        plot.setStyle("-fx-background-color: white;");
        layout();
        refreshAllBars(data);
    }

    /* ===================== 缩放+右键菜单组合功能（核心修改） ===================== */
    private void addHoverScaleAndContextMenuEffect(Region bar, SpaceUsage spaceUsage) {
        // 1. 缩放效果（保留原有逻辑）
        Scale scale = new Scale(NORMAL_SCALE, NORMAL_SCALE);
        bar.getTransforms().add(scale);

        // 新增：标记是否正在显示右键菜单（控制缩放是否恢复）
        boolean[] isMenuShowing = {false};

        // 鼠标进入：放大+阴影
        bar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            if (!isMenuShowing[0]) { // 菜单未显示时才放大
                scale.setPivotX(0); // X轴锚点（柱子左侧）
                scale.setPivotY(bar.getHeight() / 2); // Y轴锚点（垂直居中）
                // 平滑缩放
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(SCALE_DURATION),
                                new javafx.animation.KeyValue(scale.xProperty(), HOVER_SCALE),
                                new javafx.animation.KeyValue(scale.yProperty(), HOVER_SCALE)
                        )
                );
                timeline.play();
                // 添加阴影
               // bar.setStyle(bar.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0.1, 0, 0);");
            }
        });

        // 鼠标离开：仅当菜单未显示时才恢复缩放（核心修改）
        bar.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            if (!isMenuShowing[0]) { // 菜单关闭时才恢复
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(SCALE_DURATION),
                                new javafx.animation.KeyValue(scale.xProperty(), NORMAL_SCALE),
                                new javafx.animation.KeyValue(scale.yProperty(), NORMAL_SCALE)
                        )
                );
                timeline.play();
                // 移除阴影
                //bar.setStyle(bar.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0.1, 0, 0);", ""));
            }
        });

        // 2. 右键菜单功能（核心修改：控制菜单显示/隐藏时的缩放状态）
        ContextMenu contextMenu = new ContextMenu();


        // 菜单选项1：查看详情
        MenuItem createDbspace = new MenuItem("创建数据库空间 ( Create Dbspace )");
        SVGPath createDbspaceIcon = new SVGPath();
        createDbspaceIcon.setContent("M12 1.5 Q9.8281 1.5 7.9219 2.3125 Q6 3.1406 4.5625 4.5781 Q3.1406 6 2.3281 7.9219 Q1.5 9.8125 1.5 12 Q1.5 14.1719 2.3281 16.0781 Q3.1406 18 4.5625 19.4375 Q6 20.8594 7.9219 21.6719 Q9.8281 22.5 12 22.5 Q14.1875 22.5 16.0781 21.6719 Q18 20.8594 19.4219 19.4375 Q20.8594 18 21.6875 16.0781 Q22.5 14.1719 22.5 12 Q22.5 9.8125 21.6875 7.9219 Q20.8594 6 19.4219 4.5781 Q18 3.1406 16.0781 2.3125 Q14.1875 1.5 12 1.5 ZM16.5 12.5625 Q16.5 12.625 16.4375 12.6875 Q16.3906 12.75 16.3125 12.75 L12.75 12.75 L12.75 16.3125 Q12.75 16.375 12.6875 16.4375 Q12.6406 16.5 12.5625 16.5 L11.4375 16.5 Q11.375 16.5 11.3125 16.4375 Q11.25 16.375 11.25 16.3125 L11.25 12.75 L7.6875 12.75 Q7.625 12.75 7.5625 12.6875 Q7.5 12.625 7.5 12.5625 L7.5 11.4375 Q7.5 11.3594 7.5625 11.3125 Q7.625 11.25 7.6875 11.25 L11.25 11.25 L11.25 7.6875 Q11.25 7.6094 11.3125 7.5625 Q11.375 7.5 11.4375 7.5 L12.5625 7.5 Q12.6406 7.5 12.6875 7.5625 Q12.75 7.6094 12.75 7.6875 L12.75 11.25 L16.3125 11.25 Q16.3906 11.25 16.4375 11.3125 Q16.5 11.3594 16.5 11.4375 L16.5 12.5625 Z");
        createDbspaceIcon.setScaleX(0.6);
        createDbspaceIcon.setScaleY(0.6);
        createDbspaceIcon.setFill(Color.valueOf("#074675"));
        createDbspace.setGraphic(new Group(createDbspaceIcon));
        createDbspace.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onCreateDbspace(spaceUsage,false);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        // 菜单选项2：刷新数据
        MenuItem deleteDbspace = new MenuItem("删除数据库空间 ( Drop Dbspace )");
        SVGPath deleteItemIcon = new SVGPath();
        deleteItemIcon.setContent("M16.5156 11.6406 Q17.9531 11.6406 19.25 12.3906 Q20.5469 13.125 21.2656 14.4062 Q21.9844 15.6719 21.9844 17.1406 Q21.9844 18.5938 21.2656 19.875 Q20.5469 21.1406 19.25 21.8906 Q17.9531 22.625 16.4844 22.625 Q15.0312 22.625 13.75 21.8906 Q12.4844 21.1406 11.7344 19.9062 Q10.9844 18.6562 10.9844 17.1406 Q10.9844 15.625 11.7344 14.375 Q12.4844 13.125 13.75 12.3906 Q15.0312 11.6406 16.5156 11.6406 ZM12 1.375 Q13.2969 1.375 14.2344 2.2656 Q15.1719 3.1406 15.2656 4.4375 L15.2656 4.625 L20.5 4.625 Q20.7812 4.625 21 4.8438 Q21.2188 5.0625 21.2344 5.3594 Q21.2656 5.6406 21.0625 5.8594 Q20.875 6.0781 20.5938 6.125 L19.7344 6.125 L19.2031 11.2031 Q18.4844 10.9219 17.7656 10.7812 L18.1875 6.125 L5.8125 6.125 L7.0625 19.0312 Q7.1094 19.4688 7.4219 19.7812 Q7.7344 20.0938 8.2031 20.1406 L10.75 20.1406 Q11.1875 20.9531 11.8125 21.625 L8.2969 21.625 Q7.2969 21.625 6.5312 20.9844 Q5.7656 20.3281 5.6094 19.3281 L4.2656 6.125 L3.5 6.125 Q3.2188 6.125 3 5.9531 Q2.7812 5.7812 2.7344 5.5 L2.7344 5.4062 Q2.7344 5.1094 2.9219 4.9062 Q3.125 4.6875 3.4062 4.625 L8.7344 4.625 Q8.7344 3.2812 9.6875 2.3281 Q10.6562 1.375 12 1.375 ZM13.7344 14.2344 L13.625 14.2812 L13.5781 14.375 Q13.3906 14.6094 13.5781 14.9062 L15.7969 17.1562 L13.5781 19.375 Q13.3906 19.6562 13.5781 19.9375 L13.7344 20.0469 Q14.0156 20.2344 14.2969 20.0469 L16.5156 17.8281 L18.7188 20.0469 Q19.0156 20.2344 19.2969 20.0469 L19.3906 19.9375 Q19.5781 19.6562 19.3906 19.375 L17.1875 17.1562 L19.3906 14.9062 Q19.5781 14.6094 19.3906 14.375 L19.2969 14.2344 Q19.0156 14.0469 18.7188 14.2344 L16.5156 16.4375 L14.2969 14.2344 Q14.0625 14.0938 13.7812 14.1875 L13.7344 14.2344 ZM12 2.9062 Q11.3281 2.9062 10.8125 3.3594 Q10.3125 3.8125 10.2656 4.4844 L10.2656 4.625 L13.7344 4.625 Q13.7344 3.9062 13.2188 3.4062 Q12.7188 2.9062 12 2.9062 Z");
        deleteItemIcon.setScaleX(0.6);
        deleteItemIcon.setScaleY(0.6);
        deleteItemIcon.setFill(Color.valueOf("#9f453c"));
        deleteDbspace.setGraphic(new Group(deleteItemIcon));
        deleteDbspace.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onDropDbspace(spaceUsage);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        // 菜单选项3：删除大小限制
        MenuItem unlimitSizeItem = new MenuItem("解除大小限制 ( Set Size Unlimited )");
        SVGPath unlimitSizeItemIcon = new SVGPath();
        unlimitSizeItemIcon.setContent("M7.6875 14.9219 Q8.0625 14.5 8.6406 14.6406 Q9.2188 14.7812 9.3594 15.3594 Q9.5 15.9375 9.0781 16.3125 L6.4375 19.0156 L8.0156 19.0156 Q8.4062 19.0156 8.6562 19.25 Q8.9219 19.4844 8.9688 19.875 L9.0312 20.0156 Q9.0312 20.4062 8.7188 20.7188 Q8.4062 21.0312 8.0156 21.0312 L3.9844 21.0312 Q3.5938 21.0312 3.2812 20.7188 Q2.9688 20.4062 3.0312 20.0156 L3.0312 15.9844 Q3.0312 15.5938 3.3125 15.3125 Q3.5938 15.0312 4 15.0312 Q4.4219 15.0312 4.7031 15.3125 Q4.9844 15.5938 4.9844 15.9844 L4.9844 17.5625 L7.6875 14.9219 ZM15.9844 21.0312 Q15.5938 21.0312 15.3125 20.7188 Q15.0312 20.4062 15.0312 20 Q15.0312 19.5781 15.3125 19.2969 Q15.5938 19.0156 15.9844 19.0156 L17.5625 19.0156 L14.9219 16.3125 Q14.6406 16.0781 14.6094 15.7031 Q14.5938 15.3125 14.8281 15.0312 L14.9219 14.9219 Q15.2188 14.6406 15.625 14.6406 Q16.0312 14.6406 16.3125 14.9219 L19.0156 17.6094 L19.0156 15.9844 Q19.0156 15.5938 19.25 15.3438 Q19.4844 15.0781 19.875 15.0312 L20.0156 15.0312 Q20.4062 15.0312 20.7188 15.3125 Q21.0312 15.5938 21.0312 15.9844 L21.0312 20.0156 Q21.0312 20.4062 20.7188 20.7188 Q20.4062 21.0312 20.0156 21.0312 L15.9844 21.0312 ZM8.0156 2.9688 Q8.5938 2.9688 8.875 3.4844 Q9.1719 3.9844 8.875 4.4844 Q8.5938 4.9844 8.0156 4.9844 L6.4375 4.9844 L9.0781 7.6875 Q9.3594 7.9219 9.375 8.3125 Q9.4062 8.6875 9.1719 8.9688 L9.0781 9.0781 Q8.7812 9.3594 8.375 9.3594 Q7.9688 9.3594 7.6875 9.0781 L4.9844 6.4375 L4.9844 8.0156 Q4.9844 8.4062 4.75 8.6719 Q4.5156 8.9219 4.125 8.9688 L3.9844 8.9688 Q3.5938 8.9688 3.2812 8.6875 Q2.9688 8.4062 3.0312 8.0156 L3.0312 3.9844 Q3.0312 3.5938 3.3125 3.3125 Q3.5938 3.0312 3.9844 3.0312 L8.0156 3.0312 L8.0156 2.9688 ZM20.0156 2.9688 Q20.4062 2.9688 20.7188 3.2812 Q21.0312 3.5938 21.0312 3.9844 L21.0312 8.0156 Q21.0312 8.4062 20.7188 8.6875 Q20.4062 8.9688 19.9844 8.9688 Q19.5781 8.9688 19.2969 8.6875 Q19.0156 8.4062 19.0156 8.0156 L19.0156 6.4375 L16.3125 9.0781 Q16.0781 9.3594 15.6875 9.3906 Q15.3125 9.4062 15.0312 9.1719 L14.9219 9.0781 Q14.6406 8.7812 14.6406 8.375 Q14.6406 7.9688 14.9219 7.6875 L17.5625 4.9844 L15.9844 4.9844 Q15.6406 4.9844 15.3594 4.75 Q15.0781 4.5156 15.0312 4.125 L15.0312 3.9844 Q15.0312 3.5938 15.3125 3.2812 Q15.5938 2.9688 15.9844 3.0312 L20.0156 3.0312 L20.0156 2.9688 Z");
        unlimitSizeItemIcon.setScaleX(0.6);
        unlimitSizeItemIcon.setScaleY(0.6);
        unlimitSizeItemIcon.setFill(Color.valueOf("#074675"));
        unlimitSizeItem.setGraphic(new Group(unlimitSizeItemIcon));
        unlimitSizeItem.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onUnlimitedSpaceSize(spaceUsage);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        // 菜单选项3：自定义其他操作
        MenuItem addDatafile = new MenuItem("扩容数据库空间 ( Add Data File )");
        SVGPath addDatafileIcon = new SVGPath();
        addDatafileIcon.setContent("M12 9.7656 Q12.3125 9.7656 12.5156 9.9688 Q12.7188 10.1562 12.7188 10.4844 L12.7188 12.7188 L15.0469 12.7188 Q15.2812 12.7188 15.5156 12.9688 Q15.7656 13.2031 15.7656 13.5312 Q15.7656 13.8438 15.5156 14.0469 Q15.2812 14.2344 15.0469 14.2344 L12.7188 14.2344 L12.7188 16.4844 Q12.7188 16.7969 12.5156 17.0469 Q12.3125 17.2812 12 17.2812 Q11.6875 17.2812 11.4844 17.0469 Q11.2812 16.7969 11.2812 16.4844 L11.2812 14.2344 L9.0469 14.2344 Q8.7188 14.2344 8.4688 14.0469 Q8.2344 13.8438 8.2344 13.5312 Q8.2344 13.2031 8.4688 12.9688 Q8.7188 12.7188 9.0469 12.7188 L11.2812 12.7188 L11.2812 10.4844 Q11.2812 10.1562 11.4844 9.9688 Q11.6875 9.7656 12 9.7656 ZM21.0469 6.7188 L21.0469 20.9531 Q21.0469 22.2344 20.1562 23.125 Q19.2812 24 18 24 L6 24 Q4.7188 24 3.8281 23.125 Q2.9531 22.2344 2.9531 21.0469 L2.9531 2.9531 Q3.0469 1.7656 3.875 0.8906 Q4.7188 0 6 0 L14.2344 0 L21.0469 6.7188 ZM16.4844 6.7188 Q15.5938 6.7188 14.9062 6.0781 Q14.2344 5.4375 14.2344 4.4844 L14.2344 1.5156 L6 1.5156 Q5.3594 1.5156 4.9219 1.9688 Q4.4844 2.4062 4.4844 2.9531 L4.4844 20.9531 Q4.4844 21.5938 4.9219 22.0469 Q5.3594 22.4844 6 22.4844 L18 22.4844 Q18.6406 22.4844 19.0781 22.0469 Q19.5156 21.5938 19.5156 20.9531 L19.5156 6.7188 L16.4844 6.7188 Z");
        addDatafileIcon.setScaleX(0.6);
        addDatafileIcon.setScaleY(0.55);
        addDatafileIcon.setFill(Color.valueOf("#074675"));
        addDatafile.setGraphic(new Group(addDatafileIcon));
        addDatafile.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onCreateDbspace(spaceUsage,true);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        MenuItem expandDatafile = new MenuItem("自动扩展 ( Set Extendable)");
        SVGPath expandDatafileIcon = new SVGPath();
        expandDatafileIcon.setContent("M11 12.0078 L3 12.0078 Q2.5781 12.0078 2.2812 12.3047 Q2 12.5859 2 13.0078 L2 13.0078 L2 21.0078 Q2 21.4297 2.2812 21.7266 Q2.5781 22.0078 3 22.0078 L3 22.0078 L11 22.0078 Q11.4219 22.0078 11.7031 21.7266 Q12 21.4297 12 21.0078 L12 21.0078 L12 13.0078 Q12 12.5859 11.7031 12.3047 Q11.4219 12.0078 11 12.0078 L11 12.0078 L11 12.0078 ZM10.0156 19.9922 L4.0156 19.9922 L4.0156 13.9922 L10.0156 13.9922 L10.0156 19.9922 ZM21.9219 2.6328 Q21.8438 2.4453 21.7031 2.3047 Q21.5625 2.1641 21.375 2.0859 L21.375 2.0859 Q21.3125 2.0391 21.2031 2.0234 Q21.0938 1.9922 21 1.9922 L15 1.9922 Q14.5781 1.9922 14.2812 2.2891 Q14 2.5859 14 3.0078 Q14 3.4297 14.2812 3.7266 Q14.5781 4.0078 15 4.0078 L15 4.0078 L18.5938 4.0078 L13.2969 9.2891 Q13.1562 9.4297 13.0625 9.6172 Q12.9844 9.8047 12.9844 10.0078 Q12.9844 10.2266 13.0625 10.3984 Q13.1562 10.5703 13.2969 10.7109 L13.2969 10.7109 Q13.4375 10.8516 13.6094 10.9453 Q13.7812 11.0234 14 11.0234 Q14.2031 11.0234 14.3906 10.9453 Q14.5781 10.8516 14.7188 10.7109 L14.7188 10.7109 L20 5.4141 L20 9.0078 Q20 9.4297 20.2812 9.7266 Q20.5781 10.0078 21 10.0078 Q21.4219 10.0078 21.7188 9.7266 Q22.0156 9.4297 22.0156 9.0078 L22.0156 9.0078 L22.0156 3.0078 Q22.0156 2.9141 21.9844 2.8047 Q21.9688 2.6953 21.9219 2.6328 L21.9219 2.6328 L21.9219 2.6328 Z");
        expandDatafileIcon.setScaleX(0.6);
        expandDatafileIcon.setScaleY(0.6);
        expandDatafileIcon.setFill(Color.valueOf("#074675"));
        expandDatafile.setGraphic(new Group(expandDatafileIcon));
        expandDatafile.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onExpandDatafile(spaceUsage);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        MenuItem unExpandDatafile = new MenuItem("禁用自动扩展 ( Set UnExtendable)");
        SVGPath unExpandDatafileIcon = new SVGPath();
        unExpandDatafileIcon.setContent("M3 3 L3 9.75 L4.5 9.75 L4.5 4.5 L9.75 4.5 L9.75 3 L3 3 ZM14.25 3 L14.25 4.5 L19.5 4.5 L19.5 9.75 L21 9.75 L21 3 L14.25 3 ZM3 14.25 L3 21 L9.75 21 L9.75 19.5 L4.5 19.5 L4.5 14.25 L3 14.25 ZM19.5 14.25 L19.5 19.5 L14.25 19.5 L14.25 21 L21 21 L21 14.25 L19.5 14.25 Z");
        unExpandDatafileIcon.setScaleX(0.6);
        unExpandDatafileIcon.setScaleY(0.6);
        unExpandDatafileIcon.setFill(Color.valueOf("#074675"));
        unExpandDatafile.setGraphic(new Group(unExpandDatafileIcon));
        unExpandDatafile.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onUnExpandDatafile(spaceUsage);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        MenuItem deleteDatafile = new MenuItem("删除数据文件 ( Drop Data File)");
        SVGPath deleteDatafileIcon = new SVGPath();
        deleteDatafileIcon.setContent("M16.5156 11.6406 Q17.9531 11.6406 19.25 12.3906 Q20.5469 13.125 21.2656 14.4062 Q21.9844 15.6719 21.9844 17.1406 Q21.9844 18.5938 21.2656 19.875 Q20.5469 21.1406 19.25 21.8906 Q17.9531 22.625 16.4844 22.625 Q15.0312 22.625 13.75 21.8906 Q12.4844 21.1406 11.7344 19.9062 Q10.9844 18.6562 10.9844 17.1406 Q10.9844 15.625 11.7344 14.375 Q12.4844 13.125 13.75 12.3906 Q15.0312 11.6406 16.5156 11.6406 ZM12 1.375 Q13.2969 1.375 14.2344 2.2656 Q15.1719 3.1406 15.2656 4.4375 L15.2656 4.625 L20.5 4.625 Q20.7812 4.625 21 4.8438 Q21.2188 5.0625 21.2344 5.3594 Q21.2656 5.6406 21.0625 5.8594 Q20.875 6.0781 20.5938 6.125 L19.7344 6.125 L19.2031 11.2031 Q18.4844 10.9219 17.7656 10.7812 L18.1875 6.125 L5.8125 6.125 L7.0625 19.0312 Q7.1094 19.4688 7.4219 19.7812 Q7.7344 20.0938 8.2031 20.1406 L10.75 20.1406 Q11.1875 20.9531 11.8125 21.625 L8.2969 21.625 Q7.2969 21.625 6.5312 20.9844 Q5.7656 20.3281 5.6094 19.3281 L4.2656 6.125 L3.5 6.125 Q3.2188 6.125 3 5.9531 Q2.7812 5.7812 2.7344 5.5 L2.7344 5.4062 Q2.7344 5.1094 2.9219 4.9062 Q3.125 4.6875 3.4062 4.625 L8.7344 4.625 Q8.7344 3.2812 9.6875 2.3281 Q10.6562 1.375 12 1.375 ZM13.7344 14.2344 L13.625 14.2812 L13.5781 14.375 Q13.3906 14.6094 13.5781 14.9062 L15.7969 17.1562 L13.5781 19.375 Q13.3906 19.6562 13.5781 19.9375 L13.7344 20.0469 Q14.0156 20.2344 14.2969 20.0469 L16.5156 17.8281 L18.7188 20.0469 Q19.0156 20.2344 19.2969 20.0469 L19.3906 19.9375 Q19.5781 19.6562 19.3906 19.375 L17.1875 17.1562 L19.3906 14.9062 Q19.5781 14.6094 19.3906 14.375 L19.2969 14.2344 Q19.0156 14.0469 18.7188 14.2344 L16.5156 16.4375 L14.2969 14.2344 Q14.0625 14.0938 13.7812 14.1875 L13.7344 14.2344 ZM12 2.9062 Q11.3281 2.9062 10.8125 3.3594 Q10.3125 3.8125 10.2656 4.4844 L10.2656 4.625 L13.7344 4.625 Q13.7344 3.9062 13.2188 3.4062 Q12.7188 2.9062 12 2.9062 Z");
        deleteDatafileIcon.setScaleX(0.6);
        deleteDatafileIcon.setScaleY(0.6);
        deleteDatafileIcon.setFill(Color.valueOf("#9f453c"));
        deleteDatafile.setGraphic(new Group(deleteDatafileIcon));


        deleteDatafile.setOnAction(e -> {
            if (contextMenuListener != null) {
                contextMenuListener.onDropDatafile(spaceUsage);
            }
            isMenuShowing[0] = false; // 菜单关闭，标记恢复
        });

        if(spaceUsage.isExtendable>0){
            expandDatafile.setDisable(true);
        }else{
            unExpandDatafile.setDisable(true);
        }
        // 添加菜单选项
        if(colorMode.equals(ColorMode.DBSPACE)) {
            if(spaceUsage.getlimitSize()>0){
                contextMenu.getItems().addAll(createDbspace, addDatafile, unlimitSizeItem,deleteDbspace);
            }else{
                contextMenu.getItems().addAll(createDbspace, addDatafile, deleteDbspace);
            }
        }else if(colorMode.equals(ColorMode.CHUNK)){
            contextMenu.getItems().addAll(expandDatafile,unExpandDatafile,deleteDatafile);
        }

        // 监听菜单显示/隐藏事件（更新标记）
        contextMenu.showingProperty().addListener((obs, oldVal, newVal) -> {
            isMenuShowing[0] = newVal; // 菜单显示时标记为true，隐藏时标记为false
            if (!newVal && !bar.isHover()) { // 菜单隐藏且鼠标不在柱子上，才恢复缩放
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(SCALE_DURATION),
                                new javafx.animation.KeyValue(scale.xProperty(), NORMAL_SCALE),
                                new javafx.animation.KeyValue(scale.yProperty(), NORMAL_SCALE)
                        )
                );
                timeline.play();
                //bar.setStyle(bar.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0.1, 0.1, 0);", ""));
            }
        });

        // 右键点击显示菜单（阻止事件冒泡，避免触发MOUSE_EXITED）
        bar.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume(); // 消费事件，阻止冒泡到父组件
                // 在鼠标位置显示菜单
                for (MenuItem item : contextMenu.getItems()) {
                    if(menuItemsDisabled)
                    item.setDisable(menuItemsDisabled);
                }
                contextMenu.show(bar, e.getScreenX(), e.getScreenY());
            }
        });

        // 解决Tooltip和菜单冲突
        bar.setMouseTransparent(false);
    }

    /* ===================== 样式与 Tooltip ===================== */
    private void applyBarStyle(Region bar, SpaceUsage u) {
        double ratio = u.getTotal() == 0 ? 0 : u.getUsed() / u.getTotal();

        String usedColor = resolveUsedColor(u);


        bar.setStyle(String.format(
                "-fx-background-color: linear-gradient(to right, %s 0%%, %s %.1f%%, %s %.1f%%, %s 100%%);" +
                        "-fx-background-insets: 0; -fx-border-width: 0;",
                usedColor, usedColor, ratio * 100,
                COLOR_UNUSED, ratio * 100, COLOR_UNUSED
        ));

        if(u.getMetaTotal()>0){ //如果是大对象空间，单独着色,getTotalPages()==0表示为数据库空间
            String usedColorData=COLOR_NORMAL;
            if(u.getUsed()/(u.getTotal()-u.getMetaTotal())>=0.8){
                usedColorData=COLOR_WARNING;
            }
            if(u.getUsed()/(u.getTotal()-u.getMetaTotal())>=0.9){
                usedColorData=COLOR_DANGER;
            }

            String usedColorMeta=COLOR_NORMAL;
            if(u.getMetaUsed()/u.getMetaTotal()>=0.8){
                usedColorMeta=COLOR_WARNING;
            }
            if(u.getMetaUsed()/u.getMetaTotal()>=0.9){
                usedColorMeta=COLOR_DANGER;
            }

            if(u.getTotalPages()!=0){ //如果是chunk,不需要分色显示告警
                usedColorData=COLOR_NORMAL;
                usedColorMeta=COLOR_NORMAL;
            }

            double ratio1=u.getUsed()/u.getTotal();
            double ratio2=(u.getTotal()-u.getMetaTotal())/u.getTotal();
            double ratio3=(u.getTotal()-u.getMetaTotal()+u.getMetaUsed())/u.getTotal();


            bar.setStyle(String.format(
                    "-fx-background-color: linear-gradient(to right, %s 0%%, %s %.1f%%, " +
                            "%s %.1f%%,%s %.1f%%," +
                            "%s %.1f%%,%s %.1f%%," +
                            "%s %.1f%%, %s 100%%);" +
                            "-fx-background-insets: 0; -fx-border-width: 0;",
                    usedColorData, usedColorData, ratio1 * 100,
                    COLOR_UNUSED, ratio1 * 100, COLOR_UNUSED,ratio2 * 100,
                    usedColorMeta, ratio2 * 100,usedColorMeta,ratio3 * 100,
                    COLOR_UNUSED,ratio3 * 100,COLOR_UNUSED
            ));
        }
        bar.setShape(new Rectangle(6, 6));
        bar.setScaleShape(true);
    }

    private Tooltip createTooltip(SpaceUsage u) {
        Tooltip t = new Tooltip();
        if (colorMode.equals(ColorMode.DBSPACE)) {
            if(u.getMetaTotal()>0){
                t = new Tooltip(String.format(
                        "空间名：%s\n总容量：%.2f GB\n已使用：%.2f GB（%.1f%%）\n未使用：%.2f GB\n可扩展：%s\n--------------------\n元数据总大小：%.2f GB\n元数据已使用：%.2f GB（%.1f%%）",
                        u.getName(), u.getTotal()-u.getMetaTotal(), u.getUsed(), u.getUsed()/(u.getTotal()-u.getMetaTotal()), u.getTotal()-u.getMetaTotal()-u.getUsed(), u.isExtendable > 0 ? "是" : "否",u.getMetaTotal(),u.getMetaUsed(),u.getMetaUsed()*100/u.getMetaTotal()
                ));
            }else{
                t = new Tooltip(String.format(
                        "空间名：%s\n总容量：%.2f GB\n已使用：%.2f GB（%.1f%%）\n未使用：%.2f GB\n可扩展：%s",
                        u.getName(), u.getTotal(), u.getUsed(), u.getUsagePercent(), u.getUnused(), u.isExtendable > 0 ? "是" : "否"
                ));
            }

            if(u.getlimitSize()>0){
                t.setText(t.getText()+String.format("\n--------------------\n限制大小：%.2f GB",u.getlimitSize()));
            }

        } else if (colorMode.equals(ColorMode.CHUNK)) {
            if(u.getMetaTotal()>0){
                t = new Tooltip(String.format(
                        "空间名：%s\n总容量：%.2f GB\n已使用：%.2f GB（%.1f%%）\n未使用：%.2f GB\n可扩展：%s\n--------------------\n元数据总大小：%.2f GB\n元数据已使用：%.2f GB（%.1f%%）",
                        u.getName(), u.getTotal()-u.getMetaTotal(), u.getUsed(), u.getUsed()/(u.getTotal()-u.getMetaTotal()), u.getTotal()-u.getMetaTotal()-u.getUsed(), u.isExtendable > 0 ? "是" : "否",u.getMetaTotal(),u.getMetaUsed(),u.getMetaUsed()*100/u.getMetaTotal()
                ));
            }else {
                t = new Tooltip(String.format(
                        "文件名：%s\n总容量：%.2f GB\n已使用：%.2f GB（%.1f%%）\n未使用：%.2f GB\n可扩展：%s",
                        u.getName(), u.getTotal(), u.getUsed(), u.getUsagePercent(), u.getUnused(), u.isExtendable > 0 ? "是" : "否"
                ));
            }
        }else if (colorMode.equals(ColorMode.DATABASE)) {
            t = new Tooltip(String.format(
                    "库名：%s\n总容量：%.2f GB\n已使用：%.2f GB（%.1f%%）\n未使用：%.2f GB",
                    u.getName(), u.getTotal(), u.getUsed(), u.getUsagePercent(), u.getUnused()
            ));
        }
        else if (colorMode.equals(ColorMode.TABLE)) {
            t = new Tooltip(String.format(
                    "对象名：%s\n总容量：%.2f GB\n已使用：%.2f GB（%.1f%%）\n未使用：%.2f GB\n分配页：%d\n数据页：%d",
                    u.getName(), u.getTotal(), u.getUsed(), u.getUsagePercent(), u.getUnused(), u.getTotalPages(), u.getUsedPages()
            ));
        }

        t.setShowDelay(javafx.util.Duration.millis(100));
        t.setHideDelay(javafx.util.Duration.millis(500));
        return t;
    }

    private void refreshAllBars(List<SpaceUsage> data) {
        Map<String, SpaceUsage> map = new HashMap<>();
        data.forEach(d -> map.put(d.getLabel(), d));

        for (XYChart.Data<Number, String> d : series.getData()) {
            SpaceUsage u = map.get(d.getYValue());
            if (u != null && d.getNode() instanceof Region r) {
                applyBarStyle(r, u);
                Tooltip.install(r, createTooltip(u));
                // 刷新时重新绑定缩放+右键菜单
                addHoverScaleAndContextMenuEffect(r, u);
            }
        }
    }

    /* ===================== 轴创建 ===================== */
    private static NumberAxis createXAxis(List<SpaceUsage> data) {
        double max = data.stream().mapToDouble(SpaceUsage::getTotal).max().orElse(1);
        NumberAxis x = new NumberAxis(0, niceMax(max), niceMax(max) / 10);
        x.setTickLabelFormatter(new NumberAxis.DefaultFormatter(x) {
            @Override
            public String toString(Number n) {
                return String.format("%.1f", n);
            }
        });
        return x;
    }

    private static CategoryAxis createYAxis(List<SpaceUsage> data) {
        List<String> names = new ArrayList<>(
                data.stream().map(SpaceUsage::getName).toList()
        );

        Collections.reverse(names); // ⭐ 关键

        CategoryAxis y = new CategoryAxis();
        y.setAutoRanging(false);    // 非常重要
        y.setCategories(FXCollections.observableArrayList(names));
        return y;
    }

    private static double niceMax(double max) {
        if (max <= 0) return 1;

        double exp = Math.floor(Math.log10(max));
        double base = max / Math.pow(10, exp);

        double niceBase;
        if (base <= 1) niceBase = 1;
        else if (base <= 2) niceBase = 2;
        else if (base <= 2.5) niceBase = 2.5;
        else if (base <= 5) niceBase = 5;
        else niceBase = 10;

        return niceBase * Math.pow(10, exp);
    }

    private static double niceTick(double niceMax) {
        return niceMax / 10.0;
    }
    public Node createLegend() {
        HBox legend = new HBox(6);
        legend.setStyle("""
        -fx-alignment: CENTER;
        -fx-padding: 6;
        -fx-background-color: none;
        -fx-border-color: none;
        -fx-border-radius: 3;
        -fx-background-radius: 3;
        -fx-font-size: 9;
        """);

        switch (colorMode) {

            case DBSPACE -> {
                legend.getChildren().addAll(
                        createLegendItem(COLOR_NORMAL, "正常 (< 80%或不增长)"),
                        createLegendItem(COLOR_WARNING, "警告 (80% ~ 90%)"),
                        createLegendItem(COLOR_DANGER, "危险 (≥ 90%)"),
                        createLegendItem(COLOR_EXTENDABLE, "可自动扩展"),
                        createLegendItem(COLOR_UNUSED, "已分配未使用")
                );
            }

            case CHUNK -> {
                legend.getChildren().addAll(
                        createLegendItem(COLOR_NORMAL, "不自动扩展"),
                        createLegendItem(COLOR_EXTENDABLE, "可自动扩展"),
                        createLegendItem(COLOR_UNUSED, "已分配未使用")
                );
            }

            case DATABASE -> {
                legend.getChildren().addAll(
                        createLegendItem(COLOR_NORMAL, "已使用"),
                        createLegendItem(COLOR_UNUSED, "已分配未使用")
                );
            }

            case TABLE -> {
                legend.getChildren().addAll(
                        createLegendItem(COLOR_NORMAL, "使用页 < 10,000,000"),
                        createLegendItem(COLOR_WARNING, "使用页 ≥ 10,000,000"),
                        createLegendItem(COLOR_DANGER, "使用页 ≥ 12,000,000"),
                        createLegendItem(COLOR_UNUSED, "已分配未使用")
                );
            }
        }

        return legend;
    }
    private Node createLegendItem(String color, String text) {
        Rectangle rect = new Rectangle(9, 9);
        rect.setArcWidth(4);
        rect.setArcHeight(4);
        rect.setFill(Color.web(color));

        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle("-fx-font-size: 9px;");

        javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, rect, label);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return box;
    }

    public void setMenuItemsDisabled(boolean enabled) {
        this.menuItemsDisabled = enabled;
    }
}