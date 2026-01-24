有时我们需要将表结构转换成数据库设计文档（WORD或者其他格式），这时需要使用脚本将表结构导出，转换成可用格式。
该脚本适用于GBase 8s小版本号在3.0之后的版本（含有syscolumnsext、syscomments以及syscolcomments表）
脚本gettabschema.sh
```shell
#!/bin/bash
# 文件名称: gettabschema.sh
# 功能说明: 导出表结构生成html格式文件，可使用word打开，并转换成doc格式
# 使用方法：gettabschema.sh 库名 > 库名.html
# 注    意：本文件为UTF-8格式
# 更新日期：2024-01-26 liaosnet@gbasedbt.com

DBNAME=${1:-testdb}
TMPTABFILE=get_tabname.unl

# create procedure;
dbaccess ${DBNAME} - <<!SQL >/dev/null 2>/dev/null
drop procedure if exists get_tabddl(varchar);
/*****************************************************
 * PROCEDURE: get_tabddl(OBJECT_NAME)
 * DESCRIPTION: get schema for OBJECT_NAME
 * USAGE EG : get_tabddl('Tab1')
 * WRITE BY : liaosnet@gbasedbt.com 2023-11-02
 *****************************************************/
create procedure get_tabddl(p_name varchar(128))
-- return v_colno, v_colname, v_coltype_name, v_nullable, v_defvalue, v_tabcomm
returning int, varchar(128), varchar(128), varchar(128), varchar(254), nvarchar(255);
  define v_numtab int;
  define v_tabname varchar(128);
  define v_colname varchar(128);
  define v_coltype_name varchar(128);
  define v_nullable varchar(128);
  define v_defvalue varchar(254);
  define v_deftype  varchar(10);
  define v_collength int;
  define v_decimal_p int;
  define v_decimal_s int;
  define v_colno     int;
  define v_tabcomm  nvarchar(255);

  on exception
    return null,null,null,null,null,null;
  end exception;

  -- exists table, delimident=y only for tablename
  let v_tabname = '';
  let v_numtab = 0;
  -- table, view, indexes
  select count(1) into v_numtab
  from systables where (tabname = p_name  or tabname = lower(p_name));

  if v_numtab = 0 then
    return null,null,null,null,null,null;
  elif v_numtab > 1 then
    let v_tabname = chr(34) || p_name || chr(34);
  else
    let v_tabname = lower(p_name);
  end if;

  -- table
  -- colno, colname, coltype, nullable, default_value, default_type_flag
  FOREACH SELECT tmp.colno,tmp.colname,tmp.coltype_name,
      CASE d.type
        WHEN 'L' THEN gbasedbt.get_default_value(tmp.coltype, tmp.extended_id, tmp.collength, d.default::lvarchar(256))::VARCHAR(254)
        WHEN 'C' THEN 'current year to second'::VARCHAR(254)
        WHEN 'S' THEN 'dbservername'::VARCHAR(254)
        WHEN 'U' THEN 'user'::VARCHAR(254)
        WHEN 'T' THEN 'today'::VARCHAR(254)
        WHEN 'E' THEN de.default::VARCHAR(254)
        ELSE          NULL::VARCHAR(254)
      END AS def_value,
      tmp.nullable,
      d.type,
      tmp.collength,
      cc.comments      
    INTO v_colno, v_colname, v_coltype_name, v_defvalue, v_nullable, v_deftype, v_collength, v_tabcomm          
    FROM (
      SELECT ce.tabid, t.tabname, ce.colno, ce.colname,ce.coltype,ce.extended_id,ce.collength,ce.coltypename2::varchar(128) AS coltype_name,
      CASE WHEN ce.coltype > 255 THEN 'NO' ELSE 'YES' END AS nullable
      FROM syscolumnsext ce, systables t
      WHERE ce.tabid = t.tabid
      AND t.tabname = v_tabname
      ORDER BY ce.colno
    ) tmp LEFT JOIN sysdefaults d ON (tmp.tabid = d.tabid AND tmp.colno = d.colno)
          LEFT JOIN sysdefaultsexpr de ON (tmp.tabid = de.tabid AND tmp.colno = de.colno and de.type='T')
          LEFT JOIN syscolcomments cc ON (tmp.tabname = cc.tabname AND tmp.colname = cc.colname)
	  	  
    -- no column found, return null
    if v_colname is null or v_colname = '' then
      return null,null,null,null,null,null;
    end if;
    
    IF v_coltype_name in ('DECIMAL','MONEY') THEN
      let v_decimal_p = v_collength / 256;
      let v_decimal_s = mod(v_collength,256);
      if v_decimal_s = 255 then
        let v_coltype_name = v_coltype_name || '(' || v_decimal_p || ')';
      else
        let v_coltype_name = v_coltype_name || '(' || v_decimal_p || ',' || v_decimal_s || ')';
      end if;
    END IF;
    
    -- default values with function not do.
    if v_deftype = 'E' then
      let v_defvalue = '(' || v_defvalue || ')';
    elif v_deftype = 'L' and v_coltype_name[1,4] in ('CHAR','VARC','LVAR','NCHA','NVAR') then
      let v_defvalue = '''' || v_defvalue || '''';
    end if;
    
    return v_colno, v_colname, v_coltype_name, v_nullable, v_defvalue, v_tabcomm with resume;
  end foreach;
end procedure;
!SQL

if [ ! $? -eq 0 ]; then
  exit 1
fi

# tab
dbaccess ${DBNAME} - <<!SQL 2>/dev/null
unload to ${TMPTABFILE}
select t.tabname,c.comments
from systables t left join syscomments c on t.tabname = c.tabname
where t.tabid > 99
  and t.tabtype = 'T'
!SQL

if [ ! $? -eq 0 ]; then
  exit 2
fi

if [ ! -f ${TMPTABFILE} ]; then
  exit 3
fi

# 输出html头部信息
cat << EOF
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>数据库【${DBNAME}】表结构</title>
<style>
table, td, th {
  border: 1px solid black;
}

table {
  width: 100%;
  border-collapse: collapse;
}
</style>
</head>
<body>
EOF
IFS='|'
while read TABNAME TABCOMM
do
  cat <<EOF
  <p>表 ${TABNAME} ${TABCOMM} </p>
  <table>
    <thead>
	  <tr><th>序号</th><th>字段名称</th><th>字符类型</th><th>允许空</th><th>缺省值</th><th>字段描述</th></tr>
	</thead>
	<tbody>
EOF
  # col
  dbaccess ${DBNAME} - <<!SQL 2>/dev/null | grep -v '^$' | awk 'function ltrim(str){sub("^[ ]*","",str);return str};{$1="";str="<tr><td>"ltrim($0);for(i=2;i<6;i++){getline;$1="";str=str"</td><td>"ltrim($0)};getline;$1="";str=str"</td><td>"ltrim($0)"</td></tr>";print str"  "}'
call get_tabddl('$TABNAME');
!SQL

  cat <<EOF
    </tbody>
   </table>
EOF
done < ${TMPTABFILE}

# 输出html尾部信息
cat << EOF
</body>
</html>
EOF

if [ -f ${TMPTABFILE} ]; then
  rm -f ${TMPTABFILE}
fi

exit 0
```
## 使用方法：
1，将gettabschema.sh.gz上传到数据库服务器上，并解压  
```shell
gunzip gettabschema.sh.gz
```
2，执行表结构导出
```shell
bash gettabschema.sh 库名 > 库名.html
```
3，使用Word直接打开 库名.html，可另存为word使用的格式  
