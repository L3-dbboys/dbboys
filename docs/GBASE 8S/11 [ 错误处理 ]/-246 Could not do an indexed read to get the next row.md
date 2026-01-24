---
hide_title: false
sidebar_position: 246
---
错误信息
```
-246 Could not do an indexed read to get the next row.
-246    不能按带索引的读得到下一行。
```

错误原因
```
通过索引扫描到的行被锁。
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
update t set col2='aaa' where col1=1;</td>
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
246: Could not do an indexed read to get the next row.<br />    
107: ISAM error:  record is locked.</td>
  </tr>
</table>
