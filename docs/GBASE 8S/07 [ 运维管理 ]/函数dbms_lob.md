dbms_lob扩展函数位于$GBASEDBTDIR/extend/excompat.1.0/excompat.bld 中，主要的函数有  
## 获取lob类型的长度  
```sql
-- dbms_lob_getlength
drop function if exists dbms_lob_getlength(blob);
create function 'gbasedbt'.dbms_lob_getlength (blob)
returns integer
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_getlength)' language c;

drop function if exists dbms_lob_getlength(clob);
create function 'gbasedbt'.dbms_lob_getlength (clob)
returns integer
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_getlength)' language c;
```

## lob数据比较  
```sql
-- dbms_lob_compare
drop function if exists dbms_lob_compare(blob,blob,int,int,int);
create function 'gbasedbt'.dbms_lob_compare (blob,blob,integer default 2147483647,integer default 1,integer default 1)
returns integer
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_compare)' language c;

drop function if exists dbms_lob_compare(clob,clob,int,int,int);
create function 'gbasedbt'.dbms_lob_compare (clob,clob,integer default 2147483647,integer default 1,integer default 1)
returns integer 
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_compare)' language c;
```

## 字段串转clob  
将字符串类型转换为clob类型  
```sql
-- dbms_lob_new_clob
drop function if exists dbms_lob_new_clob (lvarchar);
create function 'gbasedbt'.dbms_lob_new_clob (lvarchar)
returns clob with (not variant)
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_new_clob)' language c;
```

## clob类型截取为字符串  
当clob长度较小时，能整个输出，达到clob to lvarchar的效果  
```sql
-- dbms_lob_substr
drop function if exists dbms_lob_substr(clob,int,int);
create function 'gbasedbt'.dbms_lob_substr (clob,integer default 32767,integer default 1)
returns lvarchar
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_substr)' language c;
```

## 字符串在clob函数  
```sql
create function dbms_lob_instr (clob,lvarchar,integer default 1,integer default 1)
returns integer
external name '$GBASEDBTDIR/extend/excompat.1.0/excompat.bld(dbms_lob_instr)' language c;
```

