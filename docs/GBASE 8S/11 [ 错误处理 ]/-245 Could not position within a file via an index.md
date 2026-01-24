---
hide_title: false
sidebar_position: 245
---
错误信息
```
-245 Could not position within a file via an index.
-245    不能通过索引在文件内定位。
```

错误原因
```
扫描的索引key被锁，不能通过索引定位行。
```


解决方案：（选择一种）
```
1、设置锁等待时间，等待被锁定的数据行锁释放（sql前执行：set lock mode to wait 10;）
如是java程序，在JDBC URL总增加IFX_LOCK_MODE_WAIT=10;
如使用的是数据库管理工具datastudio，连接上右键-编辑连接-驱动属性-右键-添加新属性IFX_LOCK_MODE_WAIT 10
2、修改隔离级别为LC，（sql前执行：set isolation to committed read last committed;）
如是java程序，在JDBC URL总增加IFX_ISOLATION_LEVEL=5;
如使用的是数据库管理工具datastudio，连接上右键-编辑连接-驱动属性-右键-添加新属性IFX_ISOLATION_LEVEL 5
```

重现示例
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
        <th align="left">session1</th> <!-- 居中对其（默认）-->
        <th align="left">session2</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>dbaccess testdb -  <br />                 
drop table if exists t;<br />
create table t(col1 int,col2 char(100));<br />
create index idx_t on t(col1);<br />
insert into t select tabid,tabname from systables;  <br /></td>
        <td></td>
  </tr>
      <tr>
        <td>2</td>
        <td>begin;<br />
delete from t where col1=1;</td>
        <td></td>
  </tr>
        <tr>
        <td>3</td>
        <td></td>
        <td>dbaccess testdb -  <br />                 
select * from t where col1=1;</td>
  </tr>
          <tr>
        <td>4</td>
        <td></td>
        <td>报错：  <br />                 
245: Could not position within a file via an index.<br />    
144: ISAM error: key value locked.</td>
  </tr>
</table>
