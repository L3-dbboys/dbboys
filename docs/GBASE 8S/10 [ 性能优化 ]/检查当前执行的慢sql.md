### 一、使用SQL查询
```
dbaccess sysmaster -<<!
select first 10 sqx_estcost,
sqx_estrows, sqx_sessionid,
sqx_sqlstatement
from sysmaster:syssqexplain
where 1=1
order by sqx_estcost desc;
!
```
输出：
```
sqx_estcost       2147483647
sqx_estrows       49
sqx_sessionid     51
sqx_sqlstatement  select max(t3.tabid) as id from systables t1,systables t2,sys
                  tables t3, systables t4,systables t5,systables t6 
group by t
                  1.tabname,t2.tabname
```

### 二、使用命令检查
1、检查一直在运行的线程rstcb，检查第三列不变持续输出的线程
```
onstat -g act -r 1 | egrep "sqlexec|threads"
```
输出：
```
Running threads:
 215      4a645178         470f33e8         1    running                 8cpu         sqlexec
Running threads:
 215      4a645178         470f33e8         1    running                 8cpu         sqlexec
``` 
2、查看线程会话，根据上一步输出，检查线程信息
```
onstat -u |grep 470f33e8
```
输出：
```
470f33e8         ---P--- 51       gbasedbt -        0                0    1     5        0
```
3、检查会话信息，跟据上一步输出，检查会话信息及执行的sql
```
onstat -g ses 51
```
输出：
```
On-Line -- Up 14 days 19:53:19 -- 674664 Kbytes

session           effective                            #RSAM    total      used       dynamic 
id       user     user      tty      pid      hostname threads  memory     memory     explain 
51       gbasedbt -         -        1486     dbhost1  1        221184     218648     off 

Program :
/opt/gbase/bin/dbaccess

tid      name     rstcb            flags    curstk   status
215      sqlexec  470f33e8         ---P---  10528    running-

Memory pools    count 2
name         class addr              totalsize  freesize   #allocfrag #freefrag 
51           V     4a745040         217088     1728       453        6         
51*O0        V     4a788040         4096       808        1          1         

name           free       used           name           free       used      
overhead       0          6576           scb            0          144       
opentable      0          9192           filetable      0          904       
log            0          16536          temprec        0          22688     
keys           0          176            ralloc         0          80024     
gentcb         0          1616           ostcb          0          2968      
sqscb          0          21064          sql            0          18952     
hashfiletab    0          552            osenv          0          2768      
sqtcb          0          9688           fragman        0          1240      
shmblklist     0          22568          rsam_seqscan   0          992       

sqscb info
scb              sqscb            optofc   pdqpriority optcompind  directives
47b61290         4a735028         0        0           2           1         

Sess       SQL            Current            Iso Lock       SQL  ISAM F.E. 
Id         Stmt type      Database           Lvl Mode       ERR  ERR  Vers  Explain    
51         SELECT         testdb             LC  Not Wait   0    0    9.24  Off        

Current statement name : unlcur

Current SQL statement (2) :
  select max(t3.tabid) as id from systables t1,systables t2,systables t3,
    systables t4,systables t5,systables t6  group by t1.tabname,t2.tabname

Last parsed SQL statement :
  select max(t3.tabid) as id from systables t1,systables t2,systables t3,
    systables t4,systables t5,systables t6  group by t1.tabname,t2.tabname
```