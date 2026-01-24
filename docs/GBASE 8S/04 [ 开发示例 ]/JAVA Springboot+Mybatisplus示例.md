IDEA 
```
new project
Spring Initializr
Name:springbootdemo
Location:E:\Java\ideaproject\
Type:Maven
JDK:Oracle OpenJDK version 1.8.0_191
Java:8
```
NEXT
```
Sprint Boot :2.7.15
```
Create

创建配置文件
resource 文件夹右键->new file->application.yml
```yml
server:
  port: 8088
spring:
  application:
    name: mp
  datasource:
    driver-class-name: com.gbasedbt.jdbc.Driver
    url: jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10;
    username: gbasedbt
    password: GBase123
    type: com.alibaba.druid.pool.DruidDataSource
```   
    
工程名右键
New Package：com.example.springbootdemo.mapper
New Class：UserMapper
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.springbootdemo.mapper.UserMapper">

    <!-- 通用查询映射结果 -->
    <resultMap id="userMap" type="com.example.springbootdemo.pojo.User">
        <result column="user_name" property="userName"/>
        <result column="pass_word" property="passWord"/>
    </resultMap>
    <select id="queryAllUser" resultMap="userMap">
        SELECT user_name,pass_word from user;
    </select>

    <!-- 循环插入，每行一提交 -->
    <insert id="foreachAddUser">
        <foreach collection="list" item="user">
            insert into user(user_name,pass_word) values
            (#{user.userName},#{user.passWord});
        </foreach>
    </insert>

    <!-- 批量提交 -->
    <insert id="batchAdduser">
        insert into user(user_name,pass_word)
        select * from (
        <foreach collection="list" item="user" separator=" union all ">
            SELECT
            '${user.userName}','${user.passWord}'
            FROM dual
        </foreach>
        )
    </insert>

    <insert id="addUser" parameterType="com.example.springbootdemo.pojo.User">
        insert into user(user_name,pass_word) values
            (#{userName},#{passWord})
    </insert>
</mapper>
```

SpringbootdemoApplication添加mapper路径
```java
@MapperScan(basePackages = "com.example.springbootdemo.mapper")
```

New Package：com.example.springbootdemo.pojo
New Class：User
```java
package com.example.springbootdemo.pojo;

import lombok.Data;

@Data  //data注解后不需要手动编写getter和setter方法
public class User {
    private String userName;
    private String passWord;
}
```

resources 目录下 New Package：mapper
mapper New Configfile：UserMapper.xml
```java
package com.example.springbootdemo.mapper;

import com.example.springbootdemo.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<User> queryAllUser();
    void foreachAddUser(List <User> users);
    void batchAdduser(List <User> users);
    void addUser(User user);

}
```
maven依赖pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.15-SNAPSHOT</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>springbootdemo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springbootdemo</name>
    <description>springbootdemo</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- 配置druid连接池，建议1.2.23，低版本可能存在找不到数据库类型的问题-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.2.23</version>
        </dependency>
        <!-- 配置jdbc驱动,不同版本对应不同驱动 -->
        <!-- https://mvnrepository.com/artifact/com.gbasedbt/jdbc -->
        <dependency>
            <groupId>com.gbasedbt</groupId>
            <artifactId>jdbc</artifactId>
            <version>3.6.3.32</version>
        </dependency>
        <!-- 配置mybatisplus，建议3.5.2以上版本，低版本可能存在Ipage分页报-1213错误问题 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.2</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <releases>
                <enabled>false</enabled>
            </releases>
        </repository>

    </repositories>

    <pluginRepositories>
        <pluginRepository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </pluginRepository>
        <pluginRepository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <releases>
                <enabled>false</enabled>
            </releases>
        </pluginRepository>
    </pluginRepositories>

</project>
```

测试方法SpringbootdemoApplicationTests
```java
package com.example.springbootdemo;

import com.example.springbootdemo.pojo.User;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.springbootdemo.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringbootdemoApplicationTests {
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private SqlSessionFactory sqlSessionFactory;
    @Test
    void testQueryAllUser() { //查询所有用户信息
        User user;
        List<User> list = userMapper.queryAllUser();
        for(int i=0;i<list.size();i++){
            user=list.get(i);
        }

    }

    @Test
    void testForeachAddUser() { //循环插入，每行一提交
        User user= new User();
        user.setUserName("testuser1");
        user.setPassWord("testpwd1");
        List<User> list = new ArrayList<User>();
        for(int i=1;i<=1000;i++){
            list.add(user);
        }
        Long start=System.currentTimeMillis();
        userMapper.foreachAddUser(list);
        Long end=System.currentTimeMillis();

    }

    @Test
    void testBatchAddUserUnionAll() { //批量插入，使用union all
        User user= new User();
        user.setUserName("testuser1");
        user.setPassWord("testpwd1");
        List<User> list = new ArrayList<User>();
        for(int i=1;i<=1000;i++){
            list.add(user);
        }
        Long start=System.currentTimeMillis();
        userMapper.batchAdduser(list);
        Long end=System.currentTimeMillis();

    }

    @Test
    void testAddUser() { //单行插入，批量提交
        User user= new User();
        user.setUserName("testuser1");
        user.setPassWord("testpwd1");
        List<User> list = new ArrayList<User>();
        for(int i=1;i<=100000;i++){
            list.add(user);
        }

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        Long start=System.currentTimeMillis();
        int BATCH = 100000;
        for (int i = 0; i < list.size(); i++) {
            userMapper.addUser(list.get(i));
            if ( i % BATCH == 0) {
                sqlSession.rollback();
                Long end=System.currentTimeMillis();
            }
        }
        sqlSession.commit();
        Long end=System.currentTimeMillis();

    }
}
```

数据库建表语句：
```sql
create table user(user_name varchar(100),pass_word varchar(100));
``` 