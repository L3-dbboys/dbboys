package com.dbboys.model;

/**
 * 存储空间使用情况数据模型。
 * 从 {@code CustomSpaceChart} 中提取，供 API 层、方言实现和 UI 组件共享。
 */
public class SpaceUsage {
    private int number;
    private String label;
    private String name;
    private int isExtendable;
    private double used;
    private double total;
    public int extents;
    private int usedPages;
    private int totalPages;
    private double metaUsed;
    private double metaTotal;
    private double limitSize;

    public SpaceUsage(int number, String label, String name, int isExtendable,
                      double total, double used, int extents,
                      int totalPages, int usedPages,
                      double metaTotal, double metaUsed) {
        this.isExtendable = isExtendable;
        this.number = number;
        this.name = name;
        this.label = label;
        this.used = used;
        this.total = total;
        this.extents = extents;
        this.usedPages = usedPages;
        this.totalPages = totalPages;
        this.metaTotal = metaTotal;
        this.metaUsed = metaUsed;
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
    public int getNumber() { return number; }
    public double getMetaTotal() { return metaTotal; }
    public double getMetaUsed() { return metaUsed; }
    public void setLimitSize(double limitSize) {
        this.limitSize = limitSize;
    }
    public double getLimitSize() { return limitSize; }
    public double getlimitSize() { return getLimitSize(); }
}
