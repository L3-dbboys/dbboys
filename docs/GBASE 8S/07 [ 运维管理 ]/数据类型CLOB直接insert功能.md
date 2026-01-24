GBase 8s的数据库CLOB是一种文本智能大对象类型，一般情况下可以使用fileclob函数进行插入数据，或者在程序中使用绑定变量的方式插入。  
在GBase 8s的SQLMODE=Oracle模式中，实现了直接insert操作，但SQLMODE=GBase模式中没有实现  
以下介绍自己实现在SQLMODE=GBase模式下实现直接insert clob的操作的方法  
**注意：数据库版本的不同，可能实现的方法略有不同**  
从扩展中创建dbms_lob_* 函数，使用到的：  
```sql
-- 创建dbms_lob_new_clob函数，该函数在自带的extend/excompat中
drop function if exists dbms_lob_new_clob (lvarchar);
create function 'gbasedbt'.dbms_lob_new_clob (lvarchar)
returns clob with (PARALLELIZABLE,NOT VARIANT,HANDLESNULLS)
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_new_clob)' language c;

-- 获取clob的行长函数
drop function if exists dbms_lob_getlength(clob);
create function 'gbasedbt'.dbms_lob_getlength (clob)
returns integer WITH (PARALLELIZABLE,NOT VARIANT,HANDLESNULLS)
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_getlength)' language c;

-- dbms_lob_substr
drop function if exists dbms_lob_substr(clob,int,int);
create function 'gbasedbt'.dbms_lob_substr (clob,integer default 32767,integer default 1)
returns lvarchar WITH (PARALLELIZABLE,NOT VARIANT,HANDLESNULLS)
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_substr)' language c;
```
创建隐式转换，需要注意的是，可能已经存在lvarchar到clob的转换（CREATE IMPLICIT CAST(lvarchar AS clob WITH clobinput);）  
```sql
-- 创建隐式转换
DROP CAST if EXISTS (lvarchar AS clob);
-- syscasts中存在 lvarchar和clob 的转换，使用的函数是 clobinput，跟现有的冲突
-- CREATE IMPLICIT CAST(lvarchar AS clob WITH clobinput);
CREATE IMPLICIT CAST(lvarchar AS clob WITH dbms_lob_new_clob);
```
创建测试表，并插入数据，此时已经可以直接insert clob  
```sql
DROP TABLE IF EXISTS tabclob;
CREATE TABLE tabclob(col1 int, col2 clob);

INSERT INTO tabclob VALUES (1, 'abcdefgh');
INSERT INTO tabclob VALUES (2,lpad('b',32767,'b'));

SELECT col1,substr(col2,2,3) FROM tabclob;
```
如果 语句 SELECT col1,substr(col2,2,3) FROM tabclob; 能正常执行。
表示此为SQLMODE=Oracle模式下已经实现了insert clob的版本，以下还可定义concat函数  
## SQLMODE=Oracle模式已经实现insert clob功能的版本  
自定义concat函数  
```sql
-- concat函数
DROP FUNCTION IF EXISTS concat(clob,lvarchar);
CREATE FUNCTION concat(c clob, s lvarchar)
RETURNS clob with (NOT VARIANT)
  RETURN substr(c,1,dbms_lob_getlength(c)) || s;
END FUNCTION;

DROP FUNCTION IF EXISTS concat(lvarchar,clob);
CREATE FUNCTION concat(s lvarchar,c clob)
RETURNS clob with (NOT VARIANT)
  RETURN s || substr(c,1,dbms_lob_getlength(c));
END FUNCTION;

DROP FUNCTION IF EXISTS concat(clob,clob);
CREATE FUNCTION concat(c1 clob, c2 clob)
RETURNS clob with (NOT VARIANT)
  RETURN substr(c1,1,dbms_lob_getlength(c1)) || substr(c2,1,dbms_lob_getlength(c2));
END FUNCTION;
```
测试  
```sql
SELECT dbms_lob_getlength('dddd' || col2 || 'tttt') FROM tabclob;
```

如果 语句 SELECT col1,substr(col2,2,3) FROM tabclob; 不能正常执行（报1260错误）。
表示此为没有实现insert clob的版本  
## 没有实现insert clob功能的版本    
clob字段截断，就需要使用dbms_lob_substr函数，注意写法不同  
```text
dbms_lob_substr(clob字段, 截取结束位置(而不是长度), 截取开始位置)
而
substr(字段,截取开始位置,截取长度)
```
使用dbms_lob_substr包装成clob_substr函数  
```sql
DROP FUNCTION IF EXISTS clob_substr(clob,int,int);
-- 只能返回clob类型，返回lvarchar将在2048位置截断
CREATE FUNCTION clob_substr(c clob, s1 int DEFAULT 1, s2 int DEFAULT 32767)
RETURNS clob
  RETURN dbms_lob_substr(c,LEAST(s2,dbms_lob_getlength(c)-s1+1),s1);
END FUNCTION;
```

自定义concat函数  
```sql
DROP FUNCTION IF EXISTS concat(clob,lvarchar);
CREATE FUNCTION concat(c clob, s lvarchar)
RETURNS clob with (NOT VARIANT)
  RETURN dbms_lob_substr(c,dbms_lob_getlength(c),1) || s;
END FUNCTION;

DROP FUNCTION IF EXISTS concat(lvarchar,clob);
CREATE FUNCTION concat(s lvarchar,c clob)
RETURNS clob with (NOT VARIANT)
  RETURN s || dbms_lob_substr(c,dbms_lob_getlength(c),1);
END FUNCTION;

DROP FUNCTION IF EXISTS concat(clob,clob);
CREATE FUNCTION concat(c1 clob, c2 clob)
RETURNS clob with (NOT VARIANT)
  RETURN dbms_lob_substr(c1,dbms_lob_getlength(c1),1) || dbms_lob_substr(c2,dbms_lob_getlength(c2),1);
END FUNCTION;
```

自定义等于(equal)操作  
```sql
DROP FUNCTION IF EXISTS equal(clob,lvarchar);
CREATE FUNCTION equal(c clob, s lvarchar)
RETURNS boolean WITH (NOT VARIANT);
  IF dbms_lob_substr(c,dbms_lob_getlength(c),1) = s THEN
    RETURN 't';
  ELSE
    RETURN 'f';
  END IF;
END FUNCTION;
```

测试  
```sql
SELECT dbms_lob_getlength('dddd' || col2 || 'tttt') FROM tabclob;
```

## 清理  
删除concat函数   
```sql
-- 删除concat函数   
DROP FUNCTION IF EXISTS concat(clob,lvarchar);
DROP FUNCTION IF EXISTS concat(lvarchar,clob);
DROP FUNCTION IF EXISTS concat(clob,clob);
```
删除转换  
```sql
-- 删除转换
DROP CAST if EXISTS (lvarchar AS clob);

-- 创建回原来的转换 
create IMPLICIT CAST(lvarchar AS clob WITH clobinput);
```