### 创建存储过程
```sql
create	procedure cursorTest(id integer) returning integer,varchar(4096);
define v_sqlstr varchar(250);
define v_tabid integer;
define v_tabname varchar(4096);

let v_sqlstr = "select tabid,tabname from systables where tabid < ?";
prepare stmt from v_sqlstr;

declare v_cursor cursor for stmt;
open v_cursor using id;
while(1 = 1) 
fetch v_cursor into	v_tabid,v_tabname;
if(	SQLCODE != 100) then return v_tabid,v_tabname with resume;
else exit;
end if
end while close v_cursor;

free v_cursor;
free stmt;
end procedure;
```
### 执行
```
EXECUTE PROCEDURE cursorTest(10);
```
参考输出
```
(expression)  1
(expression)  systables

(expression)  2
(expression)  syscolumns

(expression)  3
(expression)  sysindices

(expression)  4
(expression)  systabauth

(expression)  5
(expression)  syscolauth

(expression)  6
(expression)  sysviews

(expression)  7
(expression)  sysusers

(expression)  8
(expression)  sysdepend

(expression)  9
(expression)  syssynonyms
```