---
hide_title: false
sidebar_position: 3
---
page1
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:1                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |1           |1     |0x46  |88    |0x1800|1680  |12    |0           |0           |

     PAGE DATA: (2008 BYTES USED,12 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 17 )
     [ offset   24 len   17 ]ROOTNAME rootdbs

     Slot2: Slot Begin Postion ( 41 ) ,Slot Length ( 35 )
     [ offset   41 len   35 ]ROOTPATH /data/gbase/rootdbschk001

     Slot3: Slot Begin Postion ( 76 ) ,Slot Length ( 13 )
     [ offset   76 len   13 ]ROOTOFFSET 0

     Slot4: Slot Begin Postion ( 89 ) ,Slot Length ( 16 )
     [ offset   89 len   16 ]ROOTSIZE 200000

     Slot5: Slot Begin Postion ( 105 ) ,Slot Length ( 9 )
     [ offset  105 len    9 ]MIRROR 0

     Slot6: Slot Begin Postion ( 114 ) ,Slot Length ( 46 )
     [ offset  114 len   46 ]MIRRORPATH /opt/gbase/tmp/demo_on.root_mirror

     Slot7: Slot Begin Postion ( 160 ) ,Slot Length ( 15 )
     [ offset  160 len   15 ]MIRROROFFSET 0

     Slot8: Slot Begin Postion ( 175 ) ,Slot Length ( 21 )
     [ offset  175 len   21 ]DBSERVERNAME gbase01

     Slot9: Slot Begin Postion ( 196 ) ,Slot Length ( 12 )
     [ offset  196 len   12 ]SERVERNUM 0

     Slot10: Slot Begin Postion ( 208 ) ,Slot Length ( 34 )
     [ offset  208 len   34 ]MSGPATH /opt/gbase/tmp/online.log

     Slot11: Slot Begin Postion ( 242 ) ,Slot Length ( 21 )
     [ offset  242 len   21 ]TAPEDEV /data/backup

     Slot12: Slot Begin Postion ( 263 ) ,Slot Length ( 11 )
     [ offset  263 len   11 ]TAPESIZE 0

     Slot13: Slot Begin Postion ( 274 ) ,Slot Length ( 13 )
     [ offset  274 len   13 ]TAPEBLK 2048

     Slot14: Slot Begin Postion ( 287 ) ,Slot Length ( 20 )
     [ offset  287 len   20 ]LTAPEDEV /dev/ltape

     Slot15: Slot Begin Postion ( 307 ) ,Slot Length ( 12 )
     [ offset  307 len   12 ]LTAPESIZE 0

     Slot16: Slot Begin Postion ( 319 ) ,Slot Length ( 14 )
     [ offset  319 len   14 ]LTAPEBLK 2048

     Slot17: Slot Begin Postion ( 333 ) ,Slot Length ( 15 )
     [ offset  333 len   15 ]PHYSFILE 50000

     Slot18: Slot Begin Postion ( 348 ) ,Slot Length ( 13 )
     [ offset  348 len   13 ]PHYSBUFF 128

     Slot19: Slot Begin Postion ( 361 ) ,Slot Length ( 11 )
     [ offset  361 len   11 ]LOGFILES 7

     Slot20: Slot Begin Postion ( 372 ) ,Slot Length ( 14 )
     [ offset  372 len   14 ]LOGSIZE 10000

     Slot21: Slot Begin Postion ( 386 ) ,Slot Length ( 11 )
     [ offset  386 len   11 ]LOGBUFF 64

     Slot22: Slot Begin Postion ( 397 ) ,Slot Length ( 15 )
     [ offset  397 len   15 ]DYNAMIC_LOGS 2

     Slot23: Slot Begin Postion ( 412 ) ,Slot Length ( 10 )
     [ offset  412 len   10 ]LTXHWM 70

     Slot24: Slot Begin Postion ( 422 ) ,Slot Length ( 11 )
     [ offset  422 len   11 ]LTXEHWM 80

     Slot25: Slot Begin Postion ( 433 ) ,Slot Length ( 11 )
     [ offset  433 len   11 ]RESIDENT 0

     Slot26: Slot Begin Postion ( 444 ) ,Slot Length ( 12 )
     [ offset  444 len   12 ]CLEANERS 32

     Slot27: Slot Begin Postion ( 456 ) ,Slot Length ( 19 )
     [ offset  456 len   19 ]SHMBASE 0x44000000

     Slot28: Slot Begin Postion ( 475 ) ,Slot Length ( 14 )
     [ offset  475 len   14 ]CKPTINTVL 300

     Slot29: Slot Begin Postion ( 489 ) ,Slot Length ( 22 )
     [ offset  489 len   22 ]DBSPACETEMP tempdbs01

     Slot30: Slot Begin Postion ( 511 ) ,Slot Length ( 34 )
     [ offset  511 len   34 ]PLOG_OVERFLOW_PATH /opt/gbase/tmp

     Slot31: Slot Begin Postion ( 545 ) ,Slot Length ( 68 )
     [ offset  545 len   68 ]BTSCANNER num=1,threshold=5000,rangesize=-1,alice=6,compression=med

     Slot32: Slot Begin Postion ( 613 ) ,Slot Length ( 21 )
     [ offset  613 len   21 ]RTO_SERVER_RESTART 0

     Slot33: Slot Begin Postion ( 634 ) ,Slot Length ( 21 )
     [ offset  634 len   21 ]RAS_PLOG_SPEED 25000

     Slot34: Slot Begin Postion ( 655 ) ,Slot Length ( 20 )
     [ offset  655 len   20 ]RAS_LLOG_SPEED 4167

     Slot35: Slot Begin Postion ( 675 ) ,Slot Length ( 26 )
     [ offset  675 len   26 ]AUTO_TUNE_SERVER_SIZE OFF

     Slot36: Slot Begin Postion ( 701 ) ,Slot Length ( 12 )
     [ offset  701 len   12 ]AUTO_LLOG 0

     Slot37: Slot Begin Postion ( 713 ) ,Slot Length ( 18 )
     [ offset  713 len   18 ]AUTO_LRU_TUNING 1

     Slot38: Slot Begin Postion ( 731 ) ,Slot Length ( 13 )
     [ offset  731 len   13 ]AUTO_CKPTS 1

     Slot39: Slot Begin Postion ( 744 ) ,Slot Length ( 14 )
     [ offset  744 len   14 ]AUTO_AIOVPS 1

     Slot40: Slot Begin Postion ( 758 ) ,Slot Length ( 15 )
     [ offset  758 len   15 ]BACKUP_FILTER 

     Slot41: Slot Begin Postion ( 773 ) ,Slot Length ( 16 )
     [ offset  773 len   16 ]RESTORE_FILTER 

     Slot42: Slot Begin Postion ( 789 ) ,Slot Length ( 25 )
     [ offset  789 len   25 ]STORAGE_FULL_ALARM 600,3

     Slot43: Slot Begin Postion ( 814 ) ,Slot Length ( 19 )
     [ offset  814 len   19 ]CONVERSION_GUARD 2

     Slot44: Slot Begin Postion ( 833 ) ,Slot Length ( 33 )
     [ offset  833 len   33 ]RESTORE_POINT_DIR /opt/gbase/tmp

     Slot45: Slot Begin Postion ( 866 ) ,Slot Length ( 16 )
     [ offset  866 len   16 ]SP_AUTOEXPAND 1

     Slot46: Slot Begin Postion ( 882 ) ,Slot Length ( 15 )
     [ offset  882 len   15 ]SP_THRESHOLD 0

     Slot47: Slot Begin Postion ( 897 ) ,Slot Length ( 18 )
     [ offset  897 len   18 ]FULL_DISK_INIT -1

     Slot48: Slot Begin Postion ( 915 ) ,Slot Length ( 21 )
     [ offset  915 len   21 ]AUTO_READAHEAD 1,128

     Slot49: Slot Begin Postion ( 936 ) ,Slot Length ( 18 )
     [ offset  936 len   18 ]UNSECURE_ONSTAT 0

     Slot50: Slot Begin Postion ( 954 ) ,Slot Length ( 19 )
     [ offset  954 len   19 ]SHMVIRTSIZE 409600

     Slot51: Slot Begin Postion ( 973 ) ,Slot Length ( 15 )
     [ offset  973 len   15 ]STACKSIZE 2048

     Slot52: Slot Begin Postion ( 988 ) ,Slot Length ( 14 )
     [ offset  988 len   14 ]SHMADD 204800

     Slot53: Slot Begin Postion ( 1002 ) ,Slot Length ( 11 )
     [ offset 1002 len   11 ]SHMTOTAL 0

     Slot54: Slot Begin Postion ( 1013 ) ,Slot Length ( 40 )
     [ offset 1013 len   40 ]ALARMPROGRAM /opt/gbase/etc/log_full.sh

     Slot55: Slot Begin Postion ( 1053 ) ,Slot Length ( 21 )
     [ offset 1053 len   21 ]VP_MEMORY_CACHE_KB 0

     Slot56: Slot Begin Postion ( 1074 ) ,Slot Length ( 21 )
     [ offset 1074 len   21 ]LOW_MEMORY_RESERVE 0

     Slot57: Slot Begin Postion ( 1095 ) ,Slot Length ( 17 )
     [ offset 1095 len   17 ]LOW_MEMORY_MGR 0

     Slot58: Slot Begin Postion ( 1112 ) ,Slot Length ( 22 )
     [ offset 1112 len   22 ]SBSPACENAME sbspace01

     Slot59: Slot Begin Postion ( 1134 ) ,Slot Length ( 16 )
     [ offset 1134 len   16 ]SYSSBSPACENAME 

     Slot60: Slot Begin Postion ( 1150 ) ,Slot Length ( 13 )
     [ offset 1150 len   13 ]SBSPACETEMP 

     Slot61: Slot Begin Postion ( 1163 ) ,Slot Length ( 20 )
     [ offset 1163 len   20 ]DS_MAX_QUERIES 2048

     Slot62: Slot Begin Postion ( 1183 ) ,Slot Length ( 24 )
     [ offset 1183 len   24 ]DS_TOTAL_MEMORY 4096000

     Slot63: Slot Begin Postion ( 1207 ) ,Slot Length ( 21 )
     [ offset 1207 len   21 ]DS_MAX_SCANS 1048576

     Slot64: Slot Begin Postion ( 1228 ) ,Slot Length ( 13 )
     [ offset 1228 len   13 ]OPTCOMPIND 2

     Slot65: Slot Begin Postion ( 1241 ) ,Slot Length ( 20 )
     [ offset 1241 len   20 ]MAX_PDQPRIORITY 100

     Slot66: Slot Begin Postion ( 1261 ) ,Slot Length ( 13 )
     [ offset 1261 len   13 ]DIRECTIVES 1

     Slot67: Slot Begin Postion ( 1274 ) ,Slot Length ( 12 )
     [ offset 1274 len   12 ]OPT_GOAL -1

     Slot68: Slot Begin Postion ( 1286 ) ,Slot Length ( 17 )
     [ offset 1286 len   17 ]AUTO_REPREPARE 1

     Slot69: Slot Begin Postion ( 1303 ) ,Slot Length ( 34 )
     [ offset 1303 len   34 ]JVPLOGFILE /opt/gbase/tmp/jvp.log

     Slot70: Slot Begin Postion ( 1337 ) ,Slot Length ( 17 )
     [ offset 1337 len   17 ]ENCRYPT_CIPHERS 

     Slot71: Slot Begin Postion ( 1354 ) ,Slot Length ( 19 )
     [ offset 1354 len   19 ]ENCRYPT_MAC medium

     Slot72: Slot Begin Postion ( 1373 ) ,Slot Length ( 14 )
     [ offset 1373 len   14 ]ENCRYPT_CDR 0

     Slot73: Slot Begin Postion ( 1387 ) ,Slot Length ( 14 )
     [ offset 1387 len   14 ]ENCRYPT_HDR 0

     Slot74: Slot Begin Postion ( 1401 ) ,Slot Length ( 16 )
     [ offset 1401 len   16 ]ENCRYPT_SWITCH 

     Slot75: Slot Begin Postion ( 1417 ) ,Slot Length ( 14 )
     [ offset 1417 len   14 ]ENCRYPT_SMX 0

     Slot76: Slot Begin Postion ( 1431 ) ,Slot Length ( 14 )
     [ offset 1431 len   14 ]SYSSYNCCACH 0

     Slot77: Slot Begin Postion ( 1445 ) ,Slot Length ( 34 )
     [ offset 1445 len   34 ]CONSOLE /opt/gbase/tmp/online.con

     Slot78: Slot Begin Postion ( 1479 ) ,Slot Length ( 20 )
     [ offset 1479 len   20 ]DEADLOCK_TIMEOUT 60

     Slot79: Slot Begin Postion ( 1499 ) ,Slot Length ( 13 )
     [ offset 1499 len   13 ]LOCKS 100000

     Slot80: Slot Begin Postion ( 1512 ) ,Slot Length ( 16 )
     [ offset 1512 len   16 ]ONDBSPACEDOWN 2

     Slot81: Slot Begin Postion ( 1528 ) ,Slot Length ( 14 )
     [ offset 1528 len   14 ]TXTIMEOUT 300

     Slot82: Slot Begin Postion ( 1542 ) ,Slot Length ( 10 )
     [ offset 1542 len   10 ]RA_PAGES 

     Slot83: Slot Begin Postion ( 1552 ) ,Slot Length ( 14 )
     [ offset 1552 len   14 ]FILLFACTOR 90

     Slot84: Slot Begin Postion ( 1566 ) ,Slot Length ( 22 )
     [ offset 1566 len   22 ]OFF_RECVRY_THREADS 10

     Slot85: Slot Begin Postion ( 1588 ) ,Slot Length ( 20 )
     [ offset 1588 len   20 ]ON_RECVRY_THREADS 1

     Slot86: Slot Begin Postion ( 1608 ) ,Slot Length ( 13 )
     [ offset 1608 len   13 ]DATASKIP off

     Slot87: Slot Begin Postion ( 1621 ) ,Slot Length ( 16 )
     [ offset 1621 len   16 ]HETERO_COMMIT 0

     Slot88: Slot Begin Postion ( 1637 ) ,Slot Length ( 43 )
     [ offset 1637 len   43 ]SYSALARMPROGRAM /opt/gbase/etc/evidence.sh

     [ offset 1692 len    4 ]Slot 88: [2B]Slot->sl_ptr=1637,[2B]Slot->sl_len=  43
     [ offset 1696 len    4 ]Slot 87: [2B]Slot->sl_ptr=1621,[2B]Slot->sl_len=  16
     [ offset 1700 len    4 ]Slot 86: [2B]Slot->sl_ptr=1608,[2B]Slot->sl_len=  13
     [ offset 1704 len    4 ]Slot 85: [2B]Slot->sl_ptr=1588,[2B]Slot->sl_len=  20
     [ offset 1708 len    4 ]Slot 84: [2B]Slot->sl_ptr=1566,[2B]Slot->sl_len=  22
     [ offset 1712 len    4 ]Slot 83: [2B]Slot->sl_ptr=1552,[2B]Slot->sl_len=  14
     [ offset 1716 len    4 ]Slot 82: [2B]Slot->sl_ptr=1542,[2B]Slot->sl_len=  10
     [ offset 1720 len    4 ]Slot 81: [2B]Slot->sl_ptr=1528,[2B]Slot->sl_len=  14
     [ offset 1724 len    4 ]Slot 80: [2B]Slot->sl_ptr=1512,[2B]Slot->sl_len=  16
     [ offset 1728 len    4 ]Slot 79: [2B]Slot->sl_ptr=1499,[2B]Slot->sl_len=  13
     [ offset 1732 len    4 ]Slot 78: [2B]Slot->sl_ptr=1479,[2B]Slot->sl_len=  20
     [ offset 1736 len    4 ]Slot 77: [2B]Slot->sl_ptr=1445,[2B]Slot->sl_len=  34
     [ offset 1740 len    4 ]Slot 76: [2B]Slot->sl_ptr=1431,[2B]Slot->sl_len=  14
     [ offset 1744 len    4 ]Slot 75: [2B]Slot->sl_ptr=1417,[2B]Slot->sl_len=  14
     [ offset 1748 len    4 ]Slot 74: [2B]Slot->sl_ptr=1401,[2B]Slot->sl_len=  16
     [ offset 1752 len    4 ]Slot 73: [2B]Slot->sl_ptr=1387,[2B]Slot->sl_len=  14
     [ offset 1756 len    4 ]Slot 72: [2B]Slot->sl_ptr=1373,[2B]Slot->sl_len=  14
     [ offset 1760 len    4 ]Slot 71: [2B]Slot->sl_ptr=1354,[2B]Slot->sl_len=  19
     [ offset 1764 len    4 ]Slot 70: [2B]Slot->sl_ptr=1337,[2B]Slot->sl_len=  17
     [ offset 1768 len    4 ]Slot 69: [2B]Slot->sl_ptr=1303,[2B]Slot->sl_len=  34
     [ offset 1772 len    4 ]Slot 68: [2B]Slot->sl_ptr=1286,[2B]Slot->sl_len=  17
     [ offset 1776 len    4 ]Slot 67: [2B]Slot->sl_ptr=1274,[2B]Slot->sl_len=  12
     [ offset 1780 len    4 ]Slot 66: [2B]Slot->sl_ptr=1261,[2B]Slot->sl_len=  13
     [ offset 1784 len    4 ]Slot 65: [2B]Slot->sl_ptr=1241,[2B]Slot->sl_len=  20
     [ offset 1788 len    4 ]Slot 64: [2B]Slot->sl_ptr=1228,[2B]Slot->sl_len=  13
     [ offset 1792 len    4 ]Slot 63: [2B]Slot->sl_ptr=1207,[2B]Slot->sl_len=  21
     [ offset 1796 len    4 ]Slot 62: [2B]Slot->sl_ptr=1183,[2B]Slot->sl_len=  24
     [ offset 1800 len    4 ]Slot 61: [2B]Slot->sl_ptr=1163,[2B]Slot->sl_len=  20
     [ offset 1804 len    4 ]Slot 60: [2B]Slot->sl_ptr=1150,[2B]Slot->sl_len=  13
     [ offset 1808 len    4 ]Slot 59: [2B]Slot->sl_ptr=1134,[2B]Slot->sl_len=  16
     [ offset 1812 len    4 ]Slot 58: [2B]Slot->sl_ptr=1112,[2B]Slot->sl_len=  22
     [ offset 1816 len    4 ]Slot 57: [2B]Slot->sl_ptr=1095,[2B]Slot->sl_len=  17
     [ offset 1820 len    4 ]Slot 56: [2B]Slot->sl_ptr=1074,[2B]Slot->sl_len=  21
     [ offset 1824 len    4 ]Slot 55: [2B]Slot->sl_ptr=1053,[2B]Slot->sl_len=  21
     [ offset 1828 len    4 ]Slot 54: [2B]Slot->sl_ptr=1013,[2B]Slot->sl_len=  40
     [ offset 1832 len    4 ]Slot 53: [2B]Slot->sl_ptr=1002,[2B]Slot->sl_len=  11
     [ offset 1836 len    4 ]Slot 52: [2B]Slot->sl_ptr= 988,[2B]Slot->sl_len=  14
     [ offset 1840 len    4 ]Slot 51: [2B]Slot->sl_ptr= 973,[2B]Slot->sl_len=  15
     [ offset 1844 len    4 ]Slot 50: [2B]Slot->sl_ptr= 954,[2B]Slot->sl_len=  19
     [ offset 1848 len    4 ]Slot 49: [2B]Slot->sl_ptr= 936,[2B]Slot->sl_len=  18
     [ offset 1852 len    4 ]Slot 48: [2B]Slot->sl_ptr= 915,[2B]Slot->sl_len=  21
     [ offset 1856 len    4 ]Slot 47: [2B]Slot->sl_ptr= 897,[2B]Slot->sl_len=  18
     [ offset 1860 len    4 ]Slot 46: [2B]Slot->sl_ptr= 882,[2B]Slot->sl_len=  15
     [ offset 1864 len    4 ]Slot 45: [2B]Slot->sl_ptr= 866,[2B]Slot->sl_len=  16
     [ offset 1868 len    4 ]Slot 44: [2B]Slot->sl_ptr= 833,[2B]Slot->sl_len=  33
     [ offset 1872 len    4 ]Slot 43: [2B]Slot->sl_ptr= 814,[2B]Slot->sl_len=  19
     [ offset 1876 len    4 ]Slot 42: [2B]Slot->sl_ptr= 789,[2B]Slot->sl_len=  25
     [ offset 1880 len    4 ]Slot 41: [2B]Slot->sl_ptr= 773,[2B]Slot->sl_len=  16
     [ offset 1884 len    4 ]Slot 40: [2B]Slot->sl_ptr= 758,[2B]Slot->sl_len=  15
     [ offset 1888 len    4 ]Slot 39: [2B]Slot->sl_ptr= 744,[2B]Slot->sl_len=  14
     [ offset 1892 len    4 ]Slot 38: [2B]Slot->sl_ptr= 731,[2B]Slot->sl_len=  13
     [ offset 1896 len    4 ]Slot 37: [2B]Slot->sl_ptr= 713,[2B]Slot->sl_len=  18
     [ offset 1900 len    4 ]Slot 36: [2B]Slot->sl_ptr= 701,[2B]Slot->sl_len=  12
     [ offset 1904 len    4 ]Slot 35: [2B]Slot->sl_ptr= 675,[2B]Slot->sl_len=  26
     [ offset 1908 len    4 ]Slot 34: [2B]Slot->sl_ptr= 655,[2B]Slot->sl_len=  20
     [ offset 1912 len    4 ]Slot 33: [2B]Slot->sl_ptr= 634,[2B]Slot->sl_len=  21
     [ offset 1916 len    4 ]Slot 32: [2B]Slot->sl_ptr= 613,[2B]Slot->sl_len=  21
     [ offset 1920 len    4 ]Slot 31: [2B]Slot->sl_ptr= 545,[2B]Slot->sl_len=  68
     [ offset 1924 len    4 ]Slot 30: [2B]Slot->sl_ptr= 511,[2B]Slot->sl_len=  34
     [ offset 1928 len    4 ]Slot 29: [2B]Slot->sl_ptr= 489,[2B]Slot->sl_len=  22
     [ offset 1932 len    4 ]Slot 28: [2B]Slot->sl_ptr= 475,[2B]Slot->sl_len=  14
     [ offset 1936 len    4 ]Slot 27: [2B]Slot->sl_ptr= 456,[2B]Slot->sl_len=  19
     [ offset 1940 len    4 ]Slot 26: [2B]Slot->sl_ptr= 444,[2B]Slot->sl_len=  12
     [ offset 1944 len    4 ]Slot 25: [2B]Slot->sl_ptr= 433,[2B]Slot->sl_len=  11
     [ offset 1948 len    4 ]Slot 24: [2B]Slot->sl_ptr= 422,[2B]Slot->sl_len=  11
     [ offset 1952 len    4 ]Slot 23: [2B]Slot->sl_ptr= 412,[2B]Slot->sl_len=  10
     [ offset 1956 len    4 ]Slot 22: [2B]Slot->sl_ptr= 397,[2B]Slot->sl_len=  15
     [ offset 1960 len    4 ]Slot 21: [2B]Slot->sl_ptr= 386,[2B]Slot->sl_len=  11
     [ offset 1964 len    4 ]Slot 20: [2B]Slot->sl_ptr= 372,[2B]Slot->sl_len=  14
     [ offset 1968 len    4 ]Slot 19: [2B]Slot->sl_ptr= 361,[2B]Slot->sl_len=  11
     [ offset 1972 len    4 ]Slot 18: [2B]Slot->sl_ptr= 348,[2B]Slot->sl_len=  13
     [ offset 1976 len    4 ]Slot 17: [2B]Slot->sl_ptr= 333,[2B]Slot->sl_len=  15
     [ offset 1980 len    4 ]Slot 16: [2B]Slot->sl_ptr= 319,[2B]Slot->sl_len=  14
     [ offset 1984 len    4 ]Slot 15: [2B]Slot->sl_ptr= 307,[2B]Slot->sl_len=  12
     [ offset 1988 len    4 ]Slot 14: [2B]Slot->sl_ptr= 287,[2B]Slot->sl_len=  20
     [ offset 1992 len    4 ]Slot 13: [2B]Slot->sl_ptr= 274,[2B]Slot->sl_len=  13
     [ offset 1996 len    4 ]Slot 12: [2B]Slot->sl_ptr= 263,[2B]Slot->sl_len=  11
     [ offset 2000 len    4 ]Slot 11: [2B]Slot->sl_ptr= 242,[2B]Slot->sl_len=  21
     [ offset 2004 len    4 ]Slot 10: [2B]Slot->sl_ptr= 208,[2B]Slot->sl_len=  34
     [ offset 2008 len    4 ]Slot  9: [2B]Slot->sl_ptr= 196,[2B]Slot->sl_len=  12
     [ offset 2012 len    4 ]Slot  8: [2B]Slot->sl_ptr= 175,[2B]Slot->sl_len=  21
     [ offset 2016 len    4 ]Slot  7: [2B]Slot->sl_ptr= 160,[2B]Slot->sl_len=  15
     [ offset 2020 len    4 ]Slot  6: [2B]Slot->sl_ptr= 114,[2B]Slot->sl_len=  46
     [ offset 2024 len    4 ]Slot  5: [2B]Slot->sl_ptr= 105,[2B]Slot->sl_len=   9
     [ offset 2028 len    4 ]Slot  4: [2B]Slot->sl_ptr=  89,[2B]Slot->sl_len=  16
     [ offset 2032 len    4 ]Slot  3: [2B]Slot->sl_ptr=  76,[2B]Slot->sl_len=  13
     [ offset 2036 len    4 ]Slot  2: [2B]Slot->sl_ptr=  41,[2B]Slot->sl_len=  35
     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=  17

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |       0x46|
     ##################################################################################

```