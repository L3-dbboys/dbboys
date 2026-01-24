错误信息
```
-19841 Error In Specifying Automatically (Server) Generated Keys.
```

错误原因
```
场景1、mybatis设置的主键字段与数据库自增字段不一致
场景2、conn.prepareStatement("insert into t(col2) values(?)", new String[]{"col1"});--col1不是主键会报此错误
```

解决方案
```
数据库避免设置DELIMIDENT=y区分大小写导致mybatis不能正确找到自增字段
```

如mybatis xml映射文件以下配置
```
<insert id="addUser" parameterType="com.example.springbootdemo.pojo.User" useGeneratedKeys="true" keyColumn="ID" keyProperty="id">
insert into user(user_name,pass_word) values (#{userName},#{passWord})
</insert>
```
keyColumn="ID",如果SERVER设置了DELIMIDENT=y启动，或URL中设置了DELIMIDENT=y，表字段为小写的话就会报此错误。
注意：如果SERVER设置了DELIMIDENT=y初始化数据库，要去掉该环境变量必须重新初始化数据库，否则部分系统视图无法查询报语法错误，如sysmaster:sysdatabases;

