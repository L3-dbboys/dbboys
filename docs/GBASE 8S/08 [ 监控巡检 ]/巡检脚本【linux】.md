下载脚本【[GBase8schk.sh](https://dl.gbase8s.com:9088/Scripts/GBase8schk.sh)】

下载报告【[T_XXX业务系统_GBase 8s数据库巡检报告_20250707_LFL.docx](https://dl.gbase8s.com:9088/Scripts/T_XXX%E4%B8%9A%E5%8A%A1%E7%B3%BB%E7%BB%9F_GBase%208s%E6%95%B0%E6%8D%AE%E5%BA%93%E5%B7%A1%E6%A3%80%E6%8A%A5%E5%91%8A_20250707_LFL.docx)】


```shell
#!/bin/bash
###################################################################################
# filename: GBase8schk.sh
# Last modified by: L3 2025-07-07
# support OS: Linux
# support database version: GBase 8s V8.x
# useage: sh GBase8schk.sh
###################################################################################

if [[ -n "${GBASEDBTSERVER}" ]]; then
    INSTANCE=${GBASEDBTSERVER}
elif [[ -n "${INFORMIXSERVER}" ]]; then
    INSTANCE=${INFORMIXSERVER}
else
    echo "ERROR:can't found instance name!"
    exit 1
fi

echo ""
echo "Begin to collect data for INSTANCE:"${INSTANCE}
echo ""
mytime=`date '+%Y%m%d%H%M%S'`
outpath="GBase8schk_${INSTANCE}_${mytime}"

if [ ! -d ${outpath} ]; then
mkdir ${outpath}
fi

###################################################################################
## Machine
###################################################################################
echo "collect machine info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/machine.unl delimiter '|'
select
os_name,os_release,os_nodename,os_version,os_machine,os_num_procs,os_num_olprocs,
os_pagesize,os_mem_total,os_mem_free,os_open_file_lim,os_shmmax
from  sysmachineinfo;
EOF

###################################################################################
## Instance
###################################################################################
echo "collect instance info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/instance.unl delimiter '|'
select
dbinfo('UTC_TO_DATETIME',sh_boottime)||' T' start_time,
(current year to second - dbinfo('UTC_TO_DATETIME',sh_boottime))||' T'  run_time,
sh_maxchunks as maxchunks,
sh_maxdbspaces maxdbspaces,
sh_maxuserthreads maxuserthreads,
sh_maxtrans maxtrans,
sh_maxlocks locks,
sh_longtx longtxs,
dbinfo('UTC_TO_DATETIME',sh_pfclrtime)||' T'  onstat_z_running_time
from sysshmvals;
EOF

###################################################################################
## CPUVP
###################################################################################
echo "collect cpuvp info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/cpuvp.unl delimiter '|'
select vpid,classname class,pid,round(usecs_user,2) user_cpu,round(usecs_sys,2) sys_cpu,num_ready,
total_semops,total_busy_wts,total_yields,total_spins,vp_cache_size,vp_cache_allocs
from sysvplst ;
EOF

###################################################################################
## Memory
###################################################################################
echo "collect instance memory info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/memory.unl delimiter '|'
select
indx,bufsize pagesize,
nbuffs buffers,
round(nbuffs*bufsize/1024/1024/1024,2)||'GB' buffsize,
nlrus,mindirty,maxdirty,
(bufwaits / (bufwrites + pagreads)) * 100.00 buff_wait_rate,
100 * (bufreads-dskreads)/ bufreads buff_read_rate,
100 * (bufwrites-dskwrites)/ bufwrites buff_write_rate,
fgwrites,lruwrites ,chunkwrites
from sysbufpool;
EOF

###################################################################################
## Network
###################################################################################
echo "collect sqlhosts info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./${outpath}/sqlhosts.unl delimiter '|'
select dbsvrnm,nettype,hostname,svcname,options,
svrsecurity,netbuf_size,svrgroup
from  syssqlhosts;
EOF

###################################################################################
## Session time
###################################################################################
echo "collect session runtime info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./${outpath}/sessiontime.unl delimiter '|'
SELECT first 500 s.sid, s.username, s.hostname, q.odb_dbname database,
dbinfo('UTC_TO_DATETIME',s.connected) conection_time,
dbinfo('UTC_TO_DATETIME',t.last_run_time) last_run_time,
current - dbinfo('UTC_TO_DATETIME',s.connected) connected_since,
current - dbinfo('UTC_TO_DATETIME',t.last_run_time) idle_time
FROM syssessions s, systcblst t, sysrstcb r, sysopendb q
WHERE t.tid = r.tid AND s.sid = r.sid AND s.sid = q.odb_sessionid
ORDER BY 8 DESC;
EOF

###################################################################################
## Session wait
###################################################################################
echo "collect session waits info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./${outpath}/sessionwait.unl delimiter '|'
select first 20 sid,pid, username, hostname,
is_wlatch, -- blocked waiting on a latch
is_wlock, -- blocked waiting on a locked record or table
is_wbuff, -- blocked waiting on a buffer
is_wckpt, -- blocked waiting on a checkpoint
is_incrit -- session is in a critical section of transaction-- (e.g writting to disk)
from syssessions
order by  is_wlatch+is_wlock+is_wbuff+is_wckpt+is_incrit desc;
EOF

###################################################################################
## Session IO
###################################################################################
echo "collect session IO info using sql ......"
dbaccess sysmaster -  << EOF
unload to ./${outpath}/sessionio.unl delimiter '|'
select first 100 syssesprof.sid,isreads,iswrites,isrewrites,
isdeletes,bufreads,bufwrites,seqscans ,
pagreads ,pagwrites,total_sorts ,dsksorts  ,
max_sortdiskspace,logspused
from syssesprof, syssessions
where syssesprof.sid = syssessions.sid
order by bufreads+bufwrites desc
;
EOF

###################################################################################
## Checkpoint
###################################################################################
echo "collect checkpoint info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/checkpoint.unl delimiter '|'
select
intvl,type,caller,dbinfo('UTC_TO_DATETIME',clock_time)||' T' clock_time,
round(crit_time,4),round(flush_time,4),round(cp_time,4),n_dirty_buffs,
plogs_per_sec,llogs_per_sec,dskflush_per_sec,ckpt_logid,ckpt_logpos,physused,logused,
n_crit_waits,tot_crit_wait,longest_crit_wait,block_time
from syscheckpoint order by intvl;
EOF

###################################################################################
## Database
###################################################################################
echo "collect database info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/database.unl delimiter '|'
SELECT trim(name) dbname,trim(owner) owner, created||' T'  created_time,
TRIM(DBINFO('dbspace',partnum)) AS dbspace,
CASE WHEN is_logging+is_buff_log=1 THEN "Unbuffered logging"
     WHEN is_logging+is_buff_log=2 THEN "Buffered logging"
     WHEN is_logging+is_buff_log=0 THEN "No logging"
ELSE "" END Logging_mode
FROM sysdatabases
where trim(name) not like 'sys%';
EOF

###################################################################################
## DBspace
###################################################################################
echo "collect dbspaces info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/dbspace.unl delimiter '|'
SELECT A.dbsnum as No, trim(B.name) as name,
CASE  WHEN (bitval(B.flags,'0x10')>0 AND bitval(B.flags,'0x2')>0)
  THEN 'MirroredBlobspace'
  WHEN bitval(B.flags,'0x10')>0  THEN 'Blobspace'
  WHEN bitval(B.flags,'0x2000')>0 AND bitval(B.flags,'0x8000')>0
  THEN 'TempSbspace'
  WHEN bitval(B.flags,'0x2000')>0 THEN 'TempDbspace'
  WHEN (bitval(B.flags,'0x8000')>0 AND bitval(B.flags,'0x2')>0)
  THEN 'MirroredSbspace'
  WHEN bitval(B.flags,'0x8000')>0  THEN 'SmartBlobspace'
  WHEN bitval(B.flags,'0x2')>0    THEN 'MirroredDbspace'
        ELSE   'Dbspace'
END  as dbstype,
 round(sum(chksize)*2/1024/1024,2)||'GB'  as DBS_SIZE ,
 round(sum(decode(mdsize,-1,nfree,udfree))*2/1024/1024,2)||'GB' as free_size,
 case when sum(decode(mdsize,-1,nfree,udfree))*100/sum(decode(mdsize,-1,chksize,udsize))
   >sum(decode(mdsize,-1,nfree,nfree))*100/sum(decode(mdsize,-1,chksize,mdsize))
then TRUNC(100-sum(decode(mdsize,-1,nfree,nfree))*100/sum(decode(mdsize,-1,chksize,mdsize)),2)||"%"
else TRUNC(100-sum(decode(mdsize,-1,nfree,udfree))*100/sum(decode(mdsize,-1,chksize,udsize)),2)||"%"
    end  as used,
  TRUNC(MAX(A.pagesize/1024))||"KB" as pgsize,
  MAX(B.nchunks) as nchunks
FROM syschktab A, sysdbstab B
WHERE A.dbsnum = B.dbsnum
 GROUP BY A.dbsnum,name, 3
ORDER BY A.dbsnum;
EOF

###################################################################################
## Chunks
###################################################################################
echo "collect chunk info using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/chunks.unl delimiter '|'
SELECT  A.chknum as num, B.name as spacename,
 TRUNC((A.pagesize/1024)) as pgsize,
 A.offset offset,
 round( A.chksize*2/1024/1024,2)||'GB'  as size,
 round(decode(A.mdsize,-1,A.nfree,A.udfree)*2/1024/1024,2)||'GB' as free,
 TRUNC(100 - decode(A.mdsize,-1,A.nfree,A.udfree)*100/A.chksize,2 )  as used,
 A.fname
FROM syschktab A, sysdbstab B
WHERE A.dbsnum = B.dbsnum
order by B.dbsnum;
EOF

###################################################################################
## Chunk IO
###################################################################################
echo "collect chunk IO using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/chunk_io.unl delimiter '|'
select d.name dbspace, fname[1,125] chunk_name,reads read_count,writes write_count,
reads+writes total_count,pagesread,pageswritten,
pagesread+pageswritten total_pg
from sysmaster:syschkio c, sysmaster:syschunks k, sysmaster:sysdbspaces d
where d.dbsnum = k.dbsnum and k.chknum  = c.chunknum
order by 8 desc;
EOF

###################################################################################
## Logical Log
###################################################################################
echo "collect logical log using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/logicallog.unl delimiter '|'
SELECT  A.number as num,  A.uniqid as uid,  round(A.size*2/1024,2)||'MB' as size,
 TRIM( TRUNC(A.used*100/A.size,0)||'%') as used,
d.name as spacename,
 TRIM( A.chunk||'_'||A.offset ) as location,
 decode(A.filltime,0,'NotFull',
 dbinfo('UTC_TO_DATETIME', A.filltime)::varchar(50))||' T' as filltime,
 CASE  WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x4')>0
   THEN 'UsedBackedUp'
   WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x2')>0
   THEN 'UsedCurrent'
   WHEN bitval(A.flags,'0x1') > 0   THEN 'Used'
   ELSE   hex(A.flags)::varchar(50)
 END as flags,
 CASE  WHEN A.filltime-B.filltime > 0 THEN
  round(CAST(TRUNC(A.size/(A.filltime-B.filltime),4)
      as varchar(20))*2/1024,2)||'MB/S'
   ELSE    ' N/A '   END as pps
FROM syslogfil A, syslogfil B,syschktab c, sysdbstab d
WHERE  A.uniqid-1 = B.uniqid
and c.dbsnum = d.dbsnum
and a.chunk=c.chknum
UNION
SELECT  A.number as num,  A.uniqid as uid, round(A.size*2/1024,2)||'MB' as size,
 TRIM( TRUNC(A.used*100/A.size,0)||'%') as used,
 d.name as spacename,
 TRIM( A.chunk||'_'||A.offset ) as location,
 decode(A.filltime,0,'NotFull',
 dbinfo('UTC_TO_DATETIME', A.filltime)::varchar(50))||' T'  as filltime,
 CASE   WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x4')>0
   THEN 'UsedBackedUp'
   WHEN bitval(A.flags,'0x1') > 0 AND bitval(A.flags,'0x2')>0
   THEN 'UsedCurrent'
   WHEN bitval(A.flags,'0x1') > 0  THEN 'Used'
   WHEN bitval(A.flags,'0x8') > 0  THEN 'NewAdd'
   ELSE hex(A.flags)::varchar(50)  END as flags,
   'N/A' as pps
FROM syslogfil A ,syschktab c, sysdbstab d
WHERE ( A.uniqid = (SELECT min(uniqid) FROM syslogfil WHERE uniqid > 0)
   OR A.uniqid = 0  )
and c.dbsnum = d.dbsnum
and a.chunk=c.chknum
ORDER BY A.uniqid ;
EOF

###################################################################################
## Locks on Table
###################################################################################
echo "collect table locks using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/tab_actlock.unl delimiter '|'
select dbsname,tabname,
sum(pf_rqlock) as locks,
sum(pf_wtlock) as lockwaits,
sum(pf_deadlk) as deadlocks
from sysactptnhdr,systabnames
where systabnames.partnum = sysactptnhdr.partnum
group by dbsname,tabname
order by lockwaits,locks desc;
EOF

dbaccess sysmaster -  << EOF
unload to  ./${outpath}/tab_lock.unl delimiter '|'
select dbsname,tabname,
sum(lockreqs) as lockreqs,
sum(lockwts) as lockwaits,
sum(deadlks) as deadlocks
from sysptprof
group by dbsname,tabname
order by deadlocks desc,lockwaits desc,lockreqs desc;
EOF

###################################################################################
## Databaes Used Space
###################################################################################
echo "collect database used space using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/database_space.unl delimiter '|'
select t1.dbsname,
round(sum(ti_nptotal)*max(ti_pagesize)/1024/1024/1024,2)||'GB' allocated_size,
round(sum(ti_npused)*max(ti_pagesize)/1024/1024/1024,2)||'GB'  used_size
from systabnames t1, systabinfo t2,sysdatabases t3
where t1.partnum = t2.ti_partnum
and trim(t3.name)=trim(t1.dbsname)
group by dbsname
order by sum(ti_nptotal) desc;
EOF

###################################################################################
## Tables Space
###################################################################################
echo "collect table and index used space using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/tab_space.unl delimiter '|'
SELECT  st.dbsname databasename,  st.tabname,
    MAX(dbinfo('UTC_TO_DATETIME',sin.ti_created)) createdtime,
    SUM( sin.ti_nextns ) extents,
    SUM( sin.ti_nrows ) nrows,
    MAX( sin.ti_nkeys ) nkeys,
    MAX( sin.ti_pagesize ) pagesize,
    SUM( sin.ti_nptotal ) nptotal,
    round(SUM( sin.ti_nptotal*sd.pagesize )/1024/1024,2)||'MB' total_size,
    SUM( sin.ti_npused ) npused,
    round(SUM( sin.ti_npused*sd.pagesize )/1024/1024,2)||'MB' used_size,
    SUM( sin.ti_npdata ) npdata,
    round(SUM( sin.ti_npdata*sd.pagesize )/1024/1024,2)||'MB' data_size
FROM
    sysmaster:systabnames st,
    sysmaster:sysdbspaces sd,
    sysmaster:systabinfo sin
WHERE
    sd.dbsnum = trunc(st.partnum / 1048576)
    AND st.partnum = sin.ti_partnum
    AND st.dbsname NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1')
    AND st.tabname[1,3] NOT IN ('sys','TBL')
GROUP BY  1,  2
ORDER BY  8 DESC;
EOF

###################################################################################
## Tables Space By Partition
###################################################################################
echo "collect table and index partition used space using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/tab_space_frag.unl delimiter '|'
SELECT  st.dbsname databasename,  st.tabname,st.partnum partnum,
    dbinfo('UTC_TO_DATETIME',sin.ti_created) createdtime,
    sin.ti_nextns  extents,
    sin.ti_nrows nrows,
    sin.ti_nkeys  nkeys,
    sin.ti_pagesize  pagesize,
    sin.ti_nptotal  nptotal,
    round(( sin.ti_nptotal*sd.pagesize )/1024/1024,2)||'MB' total_size,
    ( sin.ti_npused ) npused,
    round(( sin.ti_npused*sd.pagesize )/1024/1024,2)||'MB' used_size,
    ( sin.ti_npdata ) npdata,
    round(( sin.ti_npdata*sd.pagesize )/1024/1024,2)||'MB' data_size
FROM
    sysmaster:systabnames st,
    sysmaster:sysdbspaces sd,
    sysmaster:systabinfo sin
WHERE
    sd.dbsnum = trunc(st.partnum / 1048576)
    AND st.partnum = sin.ti_partnum
    AND st.dbsname NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1')
    AND st.tabname[1,3] NOT IN ('sys','TBL')
ORDER BY  9 DESC;
EOF

###################################################################################
## Tables and index IO and seqscans
###################################################################################
echo "collect table and index io and seqscans using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/tab_io.unl delimiter '|'
SELECT
    st.dbsname,p.tabname,SUM( sin.ti_nrows ) nrows,
    round(SUM( sin.ti_nptotal*sd.pagesize )/1024/1024,2)||'MB' total_size,
    round(SUM( sin.ti_npused*sd.pagesize )/1024/1024,2)||'MB' used_size,
    SUM( seqscans ) AS seqscans,
    SUM( pagreads ) diskreads,
    SUM( bufreads ) bufreads,
    SUM( bufwrites ) bufwrites,
    SUM( pagwrites ) diskwrites,
    SUM( pagreads )+ SUM( pagwrites ) disk_rsws,
    trunc(decode(SUM( bufreads ),0,0,(100 -((SUM( pagreads )* 100)/ SUM( bufreads + pagreads )))),2) AS rbufhits,
    trunc(decode(SUM( bufwrites ),0,0,(100 -((SUM( pagwrites )* 100)/ SUM( bufwrites + pagwrites )))),2) AS wbufhits
FROM
    sysmaster:sysptprof p,
    sysmaster:systabinfo sin,
    sysmaster:sysdbspaces sd,
    sysmaster:systabnames st
WHERE
    sd.dbsnum = trunc(st.partnum / 1048576)
    AND p.partnum = st.partnum
    AND st.partnum = sin.ti_partnum
    AND st.dbsname NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1')
    AND st.tabname[1,3] NOT IN ('sys','TBL')
GROUP BY 1,  2
ORDER BY 11 DESC;
EOF

###################################################################################
## Current slowest sql
###################################################################################
echo "collect current slowest sql using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/slowsql.unl delimiter '|'
Select first 100 sqx_estcost,sqx_estrows,sqx_sqlstatement
FROM sysmaster:syssqexplain
order by sqx_estcost desc;
EOF

###################################################################################
## Table statistics,lockmode,index keys
###################################################################################
echo "collect tables statistics,lockmode,index keys using sql ......"
dbaccess sysmaster -  << EOF
unload to  ./${outpath}/tabstat.sql delimiter ";"
select
"unload to ./${outpath}/"||trim(name)||"_stat.unl Select t.tabname,t.created as tabcreated,t.nrows,(select sum( ti_nrows ) from sysmaster:systabnames tn join sysmaster:systabinfo ti on ti.ti_partnum = tn.partnum  where t.tabname=tn.tabname   and dbsname = '"||trim(name)||"' )  as realrows,t.locklevel,t.ustlowts,i.idxname,"||
"trim(case when i.part1>0 then (select colname from "||trim(name)||":syscolumns where colno=i.part1 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part2>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part2 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part3>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part3 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part4>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part4 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part5>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part5 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part6>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part6 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part7>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part7 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part8>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part8 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part9>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part9 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part10>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part10 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part11>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part11 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part12>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part12 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part13>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part13 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part14>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part14 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part15>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part15 and tabid=i.tabid) else '' end)||"||
"trim(case when i.part16>0 then (select ','||colname from "||trim(name)||":syscolumns where colno=i.part16 and tabid=i.tabid) else '' end ) index_cols"||
",i.nunique "||
"from "||trim(name)||":systables t left join "||trim(name)||":sysindexes i on t.tabid=i.tabid "||
"where t.tabid>99 "||
"and t.tabtype='T' "||
"order by 4 desc,1"
from sysdatabases
where name NOT IN ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1','sys')
and is_logging=1;
EOF
dbaccess sysmaster ${outpath}/tabstat.sql

###################################################################################
## onstat cmd
###################################################################################
echo "collect instance running status using onstat commands ......"
onstat -b > ./${outpath}/onstat_b.unl
onstat -C all > ./${outpath}/onstat_C_all.unl
onstat -C > ./${outpath}/onstat_bigc.unl
onstat -c > ./${outpath}/onstat_c.unl
onstat -D > ./${outpath}/onstat_bigd.unl
onstat -d > ./${outpath}/onstat_d.unl
onstat -F  > ./${outpath}/onstat_F.unl
onstat -g act > ./${outpath}/onstat_g_act.unl
onstat -g arc > ./${outpath}/onstat_g_arc.unl
onstat -g ath > ./${outpath}/onstat_g_ath.unl
onstat -g buf > ./${outpath}/onstat_g_buf.unl
onstat -g cluster > ./${outpath}/onstat_g_cluster.unl
onstat -g cmsm > ./${outpath}/onstat_g_cmsm.unl
onstat -g cfg > ./${outpath}/onstat_g_cfg.unl
onstat -g cfg diff > ./${outpath}/onstat_g_cfg_diff.unl
onstat -g ckp > ./${outpath}/onstat_g_ckp.unl
onstat -g con > ./${outpath}/onstat_g_con.unl
onstat -g cpu > ./${outpath}/onstat_g_cpu.unl
onstat -g dic > ./${outpath}/onstat_g_dic.unl
onstat -g dis > ./${outpath}/onstat_g_dis.unl
onstat -g dsc > ./${outpath}/onstat_g_dsc.unl
onstat -g env > ./${outpath}/onstat_g_env.unl
onstat -g glo > ./${outpath}/onstat_g_glo.unl
onstat -g iof > ./${outpath}/onstat_g_iof.unl
onstat -g iog > ./${outpath}/onstat_g_iog.unl
onstat -g ioq > ./${outpath}/onstat_g_ioq.unl
onstat -g iov > ./${outpath}/onstat_g_iov.unl
onstat -g lmx > ./${outpath}/onstat_g_lmx.unl
#onstat -g mem > ./${outpath}/onstat_g_mem.unl
onstat -g mgm > ./${outpath}/onstat_g_mgm.unl
onstat -g ntd  > ./${outpath}/onstat_g_ntd.unl
onstat -g ntt  > ./${outpath}/onstat_g_ntt.unl
onstat -g ntu  > ./${outpath}/onstat_g_ntu.unl
onstat -g osi > ./${outpath}/onstat_g_osi.unl
onstat -g rea > ./${outpath}/onstat_g_rea.unl
onstat -g seg > ./${outpath}/onstat_g_seg.unl
onstat -g ses 0 > ./${outpath}/onstat_g_ses_0.unl
onstat -g ses > ./${outpath}/onstat_g_ses.unl
onstat -g smb s > ./${outpath}/onstat_g_smb_s.unl
#onstat -g spi | sort -n -k 2 | tail -200 > ./${outpath}/onstat_g_spi.unl
onstat -g sql > ./${outpath}/onstat_g_sql.unl
onstat -g sql 0 > ./${outpath}/onstat_g_sql_0.unl
#onstat -g ssc > ./${outpath}/onstat_g_ssc.unl
#onstat -g stk >onstat_g_stk.unl
#onstat -g sts >onstat_g_sts.unl
onstat -g wai > ./${outpath}/onstat_g_wai.unl
onstat -L > ./${outpath}/onstat_bigl.unl
onstat -l > ./${outpath}/onstat_l.unl
onstat -p > ./${outpath}/onstat_p.unl
onstat -R > ./${outpath}/onstat_R.unl
onstat -u > ./${outpath}/onstat_u.unl
onstat -V > ./${outpath}/onstat_V.unl
onstat -x > ./${outpath}/onstat_x.unl
onstat -X > ./${outpath}/onstat_bigx.unl

###################################################################################
## system cmd
###################################################################################
echo ""
echo "collect instance running status using system command ......"
echo ""
echo "collect cm memory ......"
ps -aux |grep cmsm > ./${outpath}/cm_mem.unl

echo ""
echo "collect online.log last 50000 rows......"
onlinefile=`onstat -m |grep 'Message Log File' | awk '{print $4}'`
tail -50000 ${onlinefile} > ./${outpath}/online.log

echo ""
echo "collect current user env ......"
env > ./${outpath}/env.unl

echo ""
echo "collect system cpu and memory using vmstat ......"
vmstat 1 5 > ./${outpath}/vmstat.unl

echo ""
echo "##################################################################"
echo "GBase 8s Database Health Check Finshed"
echo "tar all of the output files in path: ${outpath}"
echo "tar -cvf ${outpath}.tar ${outpath} "
echo "##################################################################"

###################################################################################
## end of all
###################################################################################
```
