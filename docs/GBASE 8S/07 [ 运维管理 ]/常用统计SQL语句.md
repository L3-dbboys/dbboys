# 获取IPA（in-place_alter）表的语句  
IPA表是快速更新表，有多个版本页。在版本升降级中，IPA表需先处理掉。  
```sql
SELECT t.dbsname , trim(t.owner) ||'.'|| t.tabname  AS fullname, h.pta_totpgs
FROM sysmaster:sysptnhdr h,sysmaster:systabnames t
WHERE h.partnum = t.partnum
and dbsname not in ('sysmaster')
and pta_totpgs > 0;
```

# 获取表结构属性  
获取表结构属性，包含 字段序号，字段号，数据类型，字符集，非空，默认值，是否主键，自增长，字段权限，注释 等字段属性。  
```sql
SELECT c.colno,c.colname,c.coltypename2,
        (SELECT sysd.dbs_collate FROM sysmaster:sysdbslocale sysd WHERE sysd.dbs_dbsname = dbinfo('dbname')) AS COLLATION,
        CASE WHEN mod(c.coltype,256) = c.coltype - 256 THEN 'YES' ELSE 'NO' END AS nullable,
        CASE d.type
             WHEN 'L' THEN get_default_value(c.coltype, c.extended_id, c.collength, d.default::lvarchar(256))::VARCHAR(254)
             WHEN 'C' THEN 'current year to second'::VARCHAR(254)
             WHEN 'S' THEN 'dbservername'::VARCHAR(254)
             WHEN 'U' THEN 'user'::VARCHAR(254)
             WHEN 'T' THEN 'today'::VARCHAR(254)
             WHEN 'E' THEN de.default::VARCHAR(254)
             ELSE          NULL::VARCHAR(254)
        END AS defvalue,
        CASE WHEN c.colno IN (SELECT i.part1 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part2 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part3 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part4 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part5 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part6 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part7 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part8 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part9 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part10 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part11 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part12 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part13 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part14 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part15 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part16 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               ) THEN 'PRI' ELSE '' END AS iskey,
         CASE WHEN mod(c.coltype,256) IN (6,18,53) THEN 'auto increment' ELSE '' END AS Extra,
         CASE lower(colauth)
           WHEN 's--' THEN 'select'
           WHEN 'su-' THEN 'select,update'
           WHEN 'sur' THEN 'select,update,references'
           WHEN '-u-' THEN 'update'
           WHEN '-ur' THEN 'update,references'
           WHEN '--r' THEN 'references'
           ELSE 'select,update,reference'
         END AS privilege,
         colcomm.comments
FROM syscolumnsext c
  LEFT JOIN systables t ON c.tabid = t.tabid
  LEFT JOIN sysdefaults d ON (c.tabid = d.tabid AND c.colno = d.colno)
  LEFT JOIN sysdefaultsexpr de ON (c.tabid = de.tabid AND c.colno = de.colno and de.type='T')
  LEFT JOIN syscolauth colauth ON (c.tabid = colauth.tabid AND c.colno = colauth.colauth)
  LEFT JOIN syscolcomments colcomm ON (t.tabname = colcomm.tabname AND c.colname = colcomm.colname)
WHERE t.tabname = '表名'
```
也可改写存储过程或者函数  
```sql
DROP PROCEDURE IF EXISTS infocolumns(varchar);
CREATE PROCEDURE infocolumns(p_tabname varchar(128))
RETURNS int,varchar(128),varchar(128),varchar(128),varchar(20),varchar(128),varchar(20),varchar(128),varchar(128),nvarchar(255);
  define v_colno int;
  define v_colname varchar(128);
  define v_typename varchar(128);
  define v_collate varchar(128);
  define v_nullable varchar(20);
  define v_defvalue varchar(128);
  define v_iskey    varchar(20);
  define v_extra    varchar(128);
  define v_privilege varchar(128);
  define v_comments  nvarchar(255);

FOREACH 
  SELECT c.colno,c.colname,c.coltypename2,
        (SELECT sysd.dbs_collate FROM sysmaster:sysdbslocale sysd WHERE sysd.dbs_dbsname = dbinfo('dbname')) AS COLLATION,
        CASE WHEN mod(c.coltype,256) = c.coltype - 256 THEN 'YES' ELSE 'NO' END AS nullable,
        CASE d.type
             WHEN 'L' THEN get_default_value(c.coltype, c.extended_id, c.collength, d.default::lvarchar(256))::VARCHAR(254)
             WHEN 'C' THEN 'current year to second'::VARCHAR(254)
             WHEN 'S' THEN 'dbservername'::VARCHAR(254)
             WHEN 'U' THEN 'user'::VARCHAR(254)
             WHEN 'T' THEN 'today'::VARCHAR(254)
             WHEN 'E' THEN de.default::VARCHAR(254)
             ELSE          NULL::VARCHAR(254)
        END AS defvalue,
        CASE WHEN c.colno IN (SELECT i.part1 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part2 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part3 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part4 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part5 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part6 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part7 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part8 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part9 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part10 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part11 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part12 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part13 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part14 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part15 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               UNION ALL SELECT i.part16 FROM sysindexes i,sysconstraints con WHERE i.idxname = con.idxname AND con.constrtype = 'P' AND con.tabid = c.tabid
                               ) THEN 'PRI' ELSE '' END AS iskey,
         CASE WHEN mod(c.coltype,256) IN (6,18,53) THEN 'auto increment' ELSE '' END AS Extra,
         CASE lower(colauth)
           WHEN 's--' THEN 'select'
           WHEN 'su-' THEN 'select,update'
           WHEN 'sur' THEN 'select,update,references'
           WHEN '-u-' THEN 'update'
           WHEN '-ur' THEN 'update,references'
           WHEN '--r' THEN 'references'
           ELSE 'select,update,reference'
         END AS privilege,
         colcomm.comments
INTO v_colno,v_colname,v_typename,v_collate,v_nullable,v_defvalue,v_iskey,v_extra,v_privilege,v_comments
FROM syscolumnsext c
  LEFT JOIN systables t ON c.tabid = t.tabid
  LEFT JOIN sysdefaults d ON (c.tabid = d.tabid AND c.colno = d.colno)
  LEFT JOIN sysdefaultsexpr de ON (c.tabid = de.tabid AND c.colno = de.colno and de.type='T')
  LEFT JOIN syscolauth colauth ON (c.tabid = colauth.tabid AND c.colno = colauth.colauth)
  LEFT JOIN syscolcomments colcomm ON (t.tabname = colcomm.tabname AND c.colname = colcomm.colname)
WHERE t.tabname = p_tabname
  RETURN v_colno,v_colname,v_typename,v_collate,v_nullable,v_defvalue,v_iskey,v_extra,v_privilege,v_comments WITH resume;
END foreach;
END PROCEDURE;
```

# 按库查询表的数据占用  
仅表行数，占用空间大小（MB）  
```sql
SELECT t.dbsname,t.tabname,p.nrows,sum(p.nptotal * p.pagesize)/1048576 total_mb
FROM sysmaster:systabnames t,sysmaster:sysptnhdr p
WHERE t.partnum = p.partnum
  AND t.dbsname IN (SELECT name FROM sysmaster:sysdatabases)
GROUP BY t.dbsname,t.tabname,p.nrows
ORDER BY t.dbsname,total_mb DESC;
```

# 查询库里表的数据量，单位是MB  
表类型，及数据空间占用（MB）  
```sql
-- 查询库里表的数据量，单位是MB
SELECT t.tabname,'Standard' AS tabtype,p.nrows, (p.nptotal * p.pagesize)/1048576 total_mb
FROM systables t, sysmaster:sysptnhdr p
WHERE t.tabid > 99
  AND t.tabtype = 'T'
  AND t.partnum = p.partnum
UNION ALL
SELECT t.tabname,'Fragment' AS tabtype,sum(p.nrows), sum(p.nptotal * p.pagesize)/1048576 total_mb
FROM systables t, sysfragments f, sysmaster:sysptnhdr p
WHERE t.tabid > 99
  AND t.tabtype = 'T'
  AND t.tabid = f.tabid
  AND f.fragtype = 'T'
  AND f.partn = p.partnum
GROUP BY 1,2
ORDER BY total_mb DESC;
```
