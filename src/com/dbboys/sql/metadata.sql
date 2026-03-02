-- @metadata.databases_gbase
select t1.*,t2.allocedsize from(
SELECT trim(name) dbname,
trim(owner) owner,
to_char(created,'YYYY-MM-DD')  created_time,
TRIM(DBINFO('dbspace',partnum)) AS dbspace,
CASE WHEN is_logging+is_buff_log=1 THEN 'unbuffered'
     WHEN is_logging+is_buff_log=2 THEN 'buffered'
     WHEN is_logging+is_buff_log=0 THEN 'nolog'
ELSE '' END Logging_mode,
is_nls,
trim(replace(replace(dbs_collate,'57372','UTF8'),'5488','GB18030-2000'))
FROM sysmaster:sysdatabases d,sysmaster:sysdbslocale s where d.name=s.dbs_dbsname
order by
case when name in ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1','gbasedbt','sys')
then 0 else 1 end,name
) t1
left join
(
SELECT
trim(st.dbsname) dbname,
replace(format_units(sum(sin.ti_nptotal*sd.pagesize),'b'),' ','') allocedsize
from
sysmaster:systabnames st JOIN sysmaster:systabinfo sin ON  st.partnum=sin.ti_partnum
JOIN sysmaster:sysdbspaces sd ON sd.dbsnum = trunc(st.partnum/1048576)
GROUP BY 1
)t2
on t1.dbname=t2.dbname

-- @metadata.databases_oracle
select t1.*,t2.allocedsize from(
SELECT trim(name) dbname,
trim(owner) owner,
to_char(created,'YYYY-MM-DD')  created_time,
TRIM(DBINFO('dbspace',partnum)) AS dbspace,
CASE WHEN is_logging+is_buff_log=1 THEN 'unbuffered'
     WHEN is_logging+is_buff_log=2 THEN 'buffered'
     WHEN is_logging+is_buff_log=0 THEN 'nolog'
ELSE '' END Logging_mode,
is_nls,
trim(replace(replace(dbs_collate,'57372','UTF8'),'5488','GB18030-2000'))
FROM sysmaster.sysdatabases d,sysmaster.sysdbslocale s where d.name=s.dbs_dbsname
order by
case when name in ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1','gbasedbt','sys')
then 0 else 1 end,name
) t1
left join
(
SELECT
trim(st.dbsname) dbname,
replace(format_units(sum(sin.ti_nptotal*sd.pagesize),'b'),' ','') allocedsize
from
sysmaster.systabnames st JOIN sysmaster.systabinfo sin ON  st.partnum=sin.ti_partnum
JOIN sysmaster.sysdbspaces sd ON sd.dbsnum = trunc(st.partnum/1048576)
GROUP BY 1
)t2
on t1.dbname=t2.dbname

-- @metadata.database_info
select t1.*,t2.allocedsize from(
SELECT trim(name) dbname,
trim(owner) owner,
to_char(created,'YYYY-MM-DD')  created_time,
TRIM(DBINFO('dbspace',partnum)) AS dbspace,
CASE WHEN is_logging+is_buff_log=1 THEN 'unbuffered'
     WHEN is_logging+is_buff_log=2 THEN 'buffered'
     WHEN is_logging+is_buff_log=0 THEN 'nolog'
ELSE '' END Logging_mode,
is_nls,
trim(replace(replace(dbs_collate,'57372','UTF8'),'5488','GB18030-2000'))
FROM sysmaster:sysdatabases d,sysmaster:sysdbslocale s where d.name=s.dbs_dbsname
and trim(name)  =?
) t1
left join
(
SELECT
trim(st.dbsname) dbname,
replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','') allocedsize
from
sysmaster:systabnames st JOIN sysmaster:systabinfo sin ON  st.partnum=sin.ti_partnum
where st.dbsname=?
GROUP BY 1
)t2
on t1.dbname=t2.dbname

-- @metadata.users
select username from sysuser:sysusermap where username!='public';

-- @metadata.dbspace_for_create_database
SELECT name,pgsize,
CASE when extendablechunks >0 THEN 'autoextendable' ELSE free_size||'GB Free' END AS freesize

from(
SELECT trim(B.name) as name,
CASE  WHEN (sysmaster:bitval(B.flags,'0x10')>0 AND sysmaster:bitval(B.flags,'0x2')>0)
  THEN 'MirroredBlobspace'
  WHEN sysmaster:bitval(B.flags,'0x10')>0  THEN 'Blobspace'
  WHEN sysmaster:bitval(B.flags,'0x2000')>0 AND sysmaster:bitval(B.flags,'0x8000')>0
  THEN 'TempSbspace'
  WHEN sysmaster:bitval(B.flags,'0x2000')>0 THEN 'TempDbspace'
  WHEN (sysmaster:bitval(B.flags,'0x8000')>0 AND sysmaster:bitval(B.flags,'0x2')>0)
  THEN 'MirroredSbspace'
  WHEN sysmaster:bitval(B.flags,'0x8000')>0  THEN 'SmartBlobspace'
  WHEN sysmaster:bitval(B.flags,'0x2')>0    THEN 'MirroredDbspace'
        ELSE   'Dbspace'
END  as dbstype,
 round(sum(decode(mdsize,-1,nfree,udfree))*2/1024/1024,2) as free_size,
  TRUNC(MAX(A.pagesize/1024))||"K Page," as pgsize,
  sum(is_extendable) extendablechunks
FROM sysmaster:syschunks A, sysmaster:sysdbstab B
WHERE A.dbsnum = B.dbsnum
 GROUP BY name, 2
ORDER BY extendablechunks DESC,free_size DESC)
WHERE dbstype='Dbspace'

-- @metadata.user_tables_count
select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and tabtype='T'

-- @metadata.user_tables_size
select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')
from systables s left join sysmaster:systabnames n on s.tabname=trim(n.tabname)
left join sysmaster:systabinfo i on i.ti_partnum=n.partnum
where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and n.dbsname=?

-- @metadata.system_tables_count
select count(*) from systables where tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION')

-- @metadata.system_tables_size
select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')
from systables s left join sysmaster:systabnames n on s.tabname=trim(n.tabname)
left join sysmaster:systabinfo i on i.ti_partnum=n.partnum
where tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION') and n.dbsname=?

-- @metadata.system_tables
select ?,dt.tabname,max(dt.owner),max(to_char(dt.created,'YYYY-MM-DD')),max(case when dt.tabtype=='V' then 'view' else 'table' end ),max(dt.locklevel) lock_level,
max(case when dt.partnum==0 then 1 else 0 end) isfragment,sum(ti_nextns) extents,
sum(sin.ti_nrows) nrows,max(sin.ti_pagesize) pagesize, sum(sin.ti_nptotal) nptotal, nvl(replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','') ,'0.000B')  total_size,
sum(sin.ti_npdata) npused,nvl(replace(format_units(sum(sin.ti_npdata*sin.ti_pagesize),'b'),' ',''),'0.000B') used_size
from systables dt left join sysmaster:systabnames st
on trim(dt.tabname)=trim(st.tabname) and st.dbsname=?
left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum
where  dt.tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION')
group by 1,2
order by  2

-- @metadata.user_tables
select ?,tabname,max(owner),max(createtime),max(tabtype),max(locklevel),max(isfragment),
sum(ti_nextns) extents,
sum(ti_nrows) nrows,max(ti_pagesize) pagesize, sum(ti_nptotal) nptotal,  replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')  total_size,
sum(ti_npdata) npused,replace(format_units(sum(ti_npdata*ti_pagesize),'b'),' ','')
 from
(select tabname,owner,to_char(created,'YYYY-MM-DD') createtime,
case when t.flags==16 then 'raw' when t.flags==32 then 'external' else 'standard' end tabtype,
locklevel,case when t.partnum==0 then 1 else 0 end isfragment,
case when t.partnum=0 then f.partn else t.partnum end  as partnum
from
systables t left join sysfragments f on t.tabid=f.tabid
where t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in ('T','E')
) t
left join sysmaster:systabinfo i on i.ti_partnum=partnum
group by 1,2
order by  2

-- @metadata.table_detail
select ?,max(tabname),max(owner),max(createtime),max(tabtype),max(locklevel),max(isfragment),
sum(ti_nextns) extents,
sum(ti_nrows) nrows,max(ti_pagesize) pagesize, sum(ti_nptotal) nptotal,  replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')  total_size,
sum(ti_npdata) npused,replace(format_units(sum(ti_npdata*ti_pagesize),'b'),' ','')
 from
(select tabname,owner,to_char(created,'YYYY-MM-DD') createtime,
case when t.flags==16 then 'raw' when t.flags==32 then 'external' else 'standard' end tabtype,
locklevel,case when t.partnum==0 then 1 else 0 end isfragment,
case when t.partnum=0 then f.partn else t.partnum end  as partnum
from
systables t left join sysfragments f on t.tabid=f.tabid
where tabname=?) t
join sysmaster:systabinfo i on i.ti_partnum=partnum

-- @metadata.table_comment
select max(c.comments)
from systables t
left join syscomments c on t.tabname = c.tabname
where t.tabtype in ('T','E') and t.tabname=?

-- @metadata.indexes
select ?, i.idxname, t.tabname,
trim( case when i.part1 > 0 then( select colname from syscolumns where colno = i.part1 and tabid = i.tabid ) else '' end )
|| trim( case when i.part2 > 0 then( select ',' || colname from syscolumns where colno = i.part2 and tabid = i.tabid ) else '' end )
|| trim( case when i.part3 > 0 then( select ',' || colname from syscolumns where colno = i.part3 and tabid = i.tabid ) else '' end )
|| trim( case when i.part4 > 0 then( select ',' || colname from syscolumns where colno = i.part4 and tabid = i.tabid ) else '' end )
|| trim( case when i.part5 > 0 then( select ',' || colname from syscolumns where colno = i.part5 and tabid = i.tabid ) else '' end )
|| trim( case when i.part6 > 0 then( select ',' || colname from syscolumns where colno = i.part6 and tabid = i.tabid ) else '' end )
|| trim( case when i.part7 > 0 then( select ',' || colname from syscolumns where colno = i.part7 and tabid = i.tabid ) else '' end )
|| trim( case when i.part8 > 0 then( select ',' || colname from syscolumns where colno = i.part8 and tabid = i.tabid ) else '' end )
|| trim( case when i.part9 > 0 then( select ',' || colname from syscolumns where colno = i.part9 and tabid = i.tabid ) else '' end )
|| trim( case when i.part10 > 0 then( select ',' || colname from syscolumns where colno = i.part10 and tabid = i.tabid ) else '' end )
|| trim( case when i.part11 > 0 then( select ',' || colname from syscolumns where colno = i.part11 and tabid = i.tabid ) else '' end )
|| trim( case when i.part12 > 0 then( select ',' || colname from syscolumns where colno = i.part12 and tabid = i.tabid ) else '' end )
|| trim( case when i.part13 > 0 then( select ',' || colname from syscolumns where colno = i.part13 and tabid = i.tabid ) else '' end )
|| trim( case when i.part14 > 0 then( select ',' || colname from syscolumns where colno = i.part14 and tabid = i.tabid ) else '' end )
|| trim( case when i.part15 > 0 then( select ',' || colname from syscolumns where colno = i.part15 and tabid = i.tabid ) else '' end )
|| trim( case when i.part16 > 0 then( select ',' || colname from syscolumns where colno = i.part16 and tabid = i.tabid ) else '' end ) as cols,
i.idxtype,
i.levels,
i.nunique,
sin.ti_pagesize pagesize,
sum(sin.ti_nptotal) nptotal,
replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','')  total_size,
max(o.state)
from
systables t join sysindexes i
on t.tabid = i.tabid and t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')
join sysobjstate o on  o.tabid=t.tabid and o.name=i.idxname
left join sysmaster:systabnames st
on trim(i.idxname)=trim(st.tabname) and st.dbsname=?
left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum
group by 1,2,3,4,5,6,7,8
order by 3,4

-- @metadata.index_count
select count(*) from sysindexes i,systables t where i.tabid=t.tabid and t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')

-- @metadata.index_size
select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')
from sysindexes s left join sysmaster:systabnames n on trim(s.idxname)=trim(n.tabname)
left join sysmaster:systabinfo i on i.ti_partnum=n.partnum
where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')

-- @metadata.index
select ?, i.idxname, t.tabname,
trim( case when i.part1 > 0 then( select colname from syscolumns where colno = i.part1 and tabid = i.tabid ) else '' end )
|| trim( case when i.part2 > 0 then( select ',' || colname from syscolumns where colno = i.part2 and tabid = i.tabid ) else '' end )
|| trim( case when i.part3 > 0 then( select ',' || colname from syscolumns where colno = i.part3 and tabid = i.tabid ) else '' end )
|| trim( case when i.part4 > 0 then( select ',' || colname from syscolumns where colno = i.part4 and tabid = i.tabid ) else '' end )
|| trim( case when i.part5 > 0 then( select ',' || colname from syscolumns where colno = i.part5 and tabid = i.tabid ) else '' end )
|| trim( case when i.part6 > 0 then( select ',' || colname from syscolumns where colno = i.part6 and tabid = i.tabid ) else '' end )
|| trim( case when i.part7 > 0 then( select ',' || colname from syscolumns where colno = i.part7 and tabid = i.tabid ) else '' end )
|| trim( case when i.part8 > 0 then( select ',' || colname from syscolumns where colno = i.part8 and tabid = i.tabid ) else '' end )
|| trim( case when i.part9 > 0 then( select ',' || colname from syscolumns where colno = i.part9 and tabid = i.tabid ) else '' end )
|| trim( case when i.part10 > 0 then( select ',' || colname from syscolumns where colno = i.part10 and tabid = i.tabid ) else '' end )
|| trim( case when i.part11 > 0 then( select ',' || colname from syscolumns where colno = i.part11 and tabid = i.tabid ) else '' end )
|| trim( case when i.part12 > 0 then( select ',' || colname from syscolumns where colno = i.part12 and tabid = i.tabid ) else '' end )
|| trim( case when i.part13 > 0 then( select ',' || colname from syscolumns where colno = i.part13 and tabid = i.tabid ) else '' end )
|| trim( case when i.part14 > 0 then( select ',' || colname from syscolumns where colno = i.part14 and tabid = i.tabid ) else '' end )
|| trim( case when i.part15 > 0 then( select ',' || colname from syscolumns where colno = i.part15 and tabid = i.tabid ) else '' end )
|| trim( case when i.part16 > 0 then( select ',' || colname from syscolumns where colno = i.part16 and tabid = i.tabid ) else '' end ) as cols,
i.idxtype,
i.levels,
i.nunique,
sin.ti_pagesize pagesize,
sum(sin.ti_nptotal) nptotal,
replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','')  total_size,
max(o.state)
from
systables t join sysindexes i
on t.tabid = i.tabid and i.idxname==?
join sysobjstate o on  o.tabid=t.tabid and o.name=i.idxname
left join sysmaster:systabnames st
on trim(i.idxname)=trim(st.tabname)
left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum
group by 1,2,3,4,5,6,7,8
order by 3,4

-- @metadata.sequence_count
select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in('Q')

-- @metadata.sequences
select ?,tabname as seqname,min_val,max_val,inc_val,cache,cur_serial8,t.created
from systables t,syssequences q,sysmaster:sysptnhdr p
where t.tabtype='Q' and t.tabid=q.tabid and t.partnum=p.partnum

-- @metadata.synonym_count
select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in('P','S')

-- @metadata.synonyms
select ?,tabname,case tabtype when 'S' then 'PUBLIC' else 'PRIVATE' end,created
from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in ('P','S')

-- @metadata.trigger_count
select count(*) from systriggers

-- @metadata.triggers
select ?,tabname,trigname,
case event when 'S' then 'select' when 'D' then 'delete' when 'U' then 'update' when 'I' then 'insert' end,
s.state
from systriggers t,sysobjstate s,systables st
where t.tabid=st.tabid and s.objtype='T' and s.name=t.trigname

-- @metadata.trigger
select ?,tabname,trigname,
case event when 'S' then 'select' when 'D' then 'delete' when 'U' then 'update' when 'I' then 'insert' end,
s.state
from systriggers t,sysobjstate s,systables st
where t.tabid=st.tabid and s.objtype='T' and s.name=t.trigname and t.trigname=?

-- @metadata.view_count
select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and tabtype='V'

-- @metadata.views
select ?,tabname,owner,to_char(created,'YYYY-MM-DD')
from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and tabtype='V'

-- @metadata.sys_dual_tabid
select tabid from systables where tabname='dual'

-- @metadata.sys_proc_has_type
select count(*) from systables t,syscolumns c where t.tabid=c.tabid and t.tabname='sysprocedures' and c.colname='type'

-- @metadata.function_count
SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 'f' and mode='O'%s

-- @metadata.functions
select distinct ?,procname,owner FROM sysprocedures WHERE isproc = 'f' and mode='O'%s

-- @metadata.procedure_count
SELECT COUNT(distinct procname ) FROM sysprocedures WHERE isproc = 't' and mode='O'%s

-- @metadata.procedures
select distinct ?,procname,owner FROM sysprocedures WHERE isproc = 't' and mode='O'%s

-- @metadata.package_count
SELECT COUNT(distinct procname) FROM sysprocedures WHERE mode='O' and retsize=0

-- @metadata.packages
select ?,procname,owner,count(*) FROM sysprocedures WHERE mode='O' and retsize=0 group by 1,2,3 order by 1,2
