---
hide_title: false
sidebar_position: 242
---
错误信息
```
-242 Could not open database table.
-113 ISAM error: the file is locked.
-106 ISAM error: non-exclusive access.
-104    ISAM error: too many files open.
-242 不能打开数据库表 (表名)。
-113 ISAM 错误：该文件已被锁定。
-106: ISAM 错误：非独占访问。
```

错误原因
```
DDL操作无法给表加独占锁，或打开文件太多超过内核参数限制
-113 表上或行上有锁
-106 表无锁但有查询打开了此表，无法锁定
```

解决方案
```
1、ISAM=-113 设置锁等待时间，或加大锁等时间
2、ISAM=-106 如果是创建索引，可在sql语句最后加上关键字online，其他操作稍等后重试。
3、ISAM=-104 调整内核参数open files
```


重现示例：
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
        <th align="left">session1</th> <!-- 居中对其（默认）-->
        <th align="left">session2</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>dbaccess testdb -  <br />
CREATE TABLE exp_tab (a int) FRAGMENT BY EXPRESSION partition p1 (a `<=` 10) IN datadbs1, partition p2 (a `<=` 20) IN datadbs1, partition p3 (a `<=` 30) IN datadbs1;<br /></td>
        <td></td>
  </tr>
      <tr>
        <td>2</td>
        <td>begin;<br />
insert into exp_tab values(10);</td>
        <td></td>
  </tr>
        <tr>
        <td>3</td>
        <td></td>
        <td>dbaccess testdb -  <br />                  
ALTER FRAGMENT ON TABLE exp_tab ADD PARTITION p4 (a`<=`40) IN datadbs1;</td>
  </tr>
          <tr>
        <td>4</td>
        <td></td>
        <td>报错：  <br />                  
242: 不能打开数据库表 (gbasedbt.exp_tab)。<br />     
113: ISAM 错误：文件已锁定。</td>
  </tr>
        <tr>
        <td>5</td>
        <td>String SelectTab = "select * from exp_tab";<br />
        pstmt = conn.prepareStatement(SelectTab);<br />
        ResultSet rs=pstmt.executeQuery();<br />
        while (rs.next())`{`<br />
            Thread.sleep(1000000); //模拟查询未完成，游标未释放<br />
            System.out.println(rs.getString(1));<br />
        `}`<br /></td>
        <td></td>
  </tr>
          <tr>
        <td>6</td>
        <td></td>
        <td>dbaccess testdb -  <br />                  
ALTER TABLE exp_tab ADD b int;</td>
  </tr>
          <tr>
        <td>7</td>
        <td></td>
        <td>报错：  <br />                  
242: 不能打开数据库表 (gbasedbt.exp_tab)。<br />     
-106: ISAM 错误：非独占访问。</td>
  </tr>
</table>
