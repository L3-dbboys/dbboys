### DELIMIDENT环境变量说明
DELIMIDENT 环境变量用于指定双引号包围符 ( " ) 之间的内容是数据库标识(表名、字段名等)，还是是字符串。默认值为n，即表示字符串，非数据库标识。

n，默认值，应用程序可以使用双引号 ( " ) 或单引号 ( ' ) 来对字符串定界，双引号( " ) 包围的内容表示为字符串，因此不可对数据库标识 (表名、字段名等) 使用双引号( " )定界、会报语法错误。SQL中的数据库标识 (表名、字段名等) 不区分大小写。

y，应用程序必须使用单引号 ( ' ) 来对字符串定界，不可使用双引号 ( " ) 来对字符串定界，会报语法错误。双引号 ( " ) 包围的内容表示为数据库标志 (表名、字段名等)，SQL语句中双引号包围的数据库标识区分大小写，SQL语句中没有双引号包围的数据库标识不区分大小写。

参考示例：

<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
      	<th align="left">SQL语句</th>   <!-- 左对齐 -->
        <th align="left">DELIMIDENT=n（默认）</th> <!-- 居中对其（默认）-->
        <th align="left">DELIMIDENT=y</th>  <!-- 右对齐-->
      	<th align="left">说明</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>create table Table1(Col1 varchar(100)); </td>
        <td>正常</td>
      	<td>正常</td>
      	<td>无双引号包围，SQL中标识不区分大小写</td>
  	</tr>
    <tr>
        <td>2</td>
        <td>insert into table1 values("text"); </td>
        <td>正常</td>
      	<td>报语法错误，默认双引号包围内容为数据库标识名</td>
      	<td></td>
  	</tr>  
    <tr>
        <td>3</td>
        <td>insert into table1 values('text'); </td>
        <td>正常</td>
      	<td>正常</td>
      	<td></td>
  	</tr> 
    <tr>
        <td>4</td>
        <td>select COL1 from TABLE1; </td>
        <td>正常</td>
      	<td>正常</td>
      	<td>不使用双引号包围情况下，数据库标识名均不区分大小写</td>
  	</tr>  
    <tr>
        <td>5</td>
        <td>select * from "table1"; </td>
        <td>语法错误，不支持引号包围标识名</td>
      	<td>正常</td>
      	<td>建表时未使用双引号包围，默认存储小写表名</td>
  	</tr>
    <tr>
        <td>6</td>
        <td>select * from "Table1"; </td>
        <td>语法错误，不支持引号包围标识名</td>
      	<td>找不到表</td>
      	<td>建表时未使用双引号包围，默认存储小写表名，使用双引号包围的表Table1找不到</td>
  	</tr> 
    <tr>
        <td>7</td>
        <td>create table "Table2" ("Col1" int); </td>
        <td>语法错误，不支持引号包围标识名</td>
      	<td>正常</td>
      	<td></td>
  	</tr> 
    <tr>
        <td>8</td>
        <td>select * from Table2 </td>
        <td>找不到表</td>
      	<td>找不到表</td>
      	<td>表创建时使用了双引号包围，表实际在系统表中存储为Table2，在SQL语句中不使用双引号包围，默认查找小写表名</td>
  	</tr> 
    <tr>
        <td>9</td>
        <td>select * from "Table2" </td>
        <td>语法错误，不支持双引号包围标识名</td>
      	<td>正常</td>
      	<td></td>
  	</tr> 
    <tr>
        <td>10</td>
        <td>select * from "TABLE2" </td>
        <td>语法错误，不支持双引号包围标识名</td>
      	<td>找不到表</td>
      	<td></td>
  	</tr> 
</table>

### 如何设置DELIMIDENT
1、在数据库实例启动时设置启动用户环境变量后启动数据库
如：
```
export DELIMIDENT=y
oninit -v
```
以上示例，数据库整个实例默认DELIMIDENT=y，全局有效。

可通过【onstat -g env |grep DELIMIDENT】检查当前实例的DELIMIDENT值，如无输出，表示默认值n

**注意：实例设置全局DELIMIDENT后，如想变更，需要重新初始化实例。实例设置DELIMIDENT=y后，客户端设置DELIMIDENT=n无效。**

2、在JDBC URL中设置DELIMIDENT=y （推荐）
如：
```
private static String URL_STRING = "jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;DELIMIDENT=y";
```
以上示例，通过此URL连接的会话DELIMIDENT=y，不影响数据库上的其他连接。