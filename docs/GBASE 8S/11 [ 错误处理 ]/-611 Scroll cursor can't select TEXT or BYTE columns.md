---
hide_title: false
sidebar_position: 611
---
错误信息
```
-611    Scroll cursor can't select TEXT or BYTE columns.
-611    滚动游标不能选定blob字段。
```

错误原因
```
text/byte不支持滚动游标
```

解决方案
```
不使用滚动游标或更改数据类型为其他数据类型。
```

重现示例
```
//create table t(col1 text);
String SelectTab="select * from t";
pstmt=conn.prepareStatement(SelectTab,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
ResultSet rs=pstmt.executeQuery();
rs.first(); //此处报错
```
调整方式：
```
//create table t(col1 text);
String SelectTab="select * from t";
pstmt=conn.prepareStatement(SelectTab);
ResultSet rs=pstmt.executeQuery();
rs.next();
```

