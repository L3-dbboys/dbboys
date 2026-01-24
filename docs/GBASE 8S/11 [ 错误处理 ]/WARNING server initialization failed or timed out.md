数据库启动报错
```
oninit -v
Reading configuration file '/opt/gbase/etc/onconfig.gbase01'...succeeded
Creating /INFORMIXTMP/.infxdirs...succeeded
Allocating and attaching to shared memory...succeeded
Creating resident pool 1086746 kbytes...succeeded
Creating infos file "/opt/gbase/etc/.infos.gbsae01"...succeeded
Linking conf file "/opt/gbase/etc/.conf.inst2_jq01"...succeeded
Initializing rhead structure...rhlock_t 524288 (16384K)... rlock_t (1062500K)... Writing to infos file...succeeded
Initialization of Encryption...succeeded
Initializing ASF...succeeded
Initializing Dictionary Cache and SPL Routine Cache...succeeded
Bringing up ADM VP...succeeded
Creating VP classes...succeeded
Forking main_loop thread...succeeded
Initializing DR structures...succeeded
Forking 1 'soctcp' listener threads...succeeded
Forking 1 'soctcp' listener threads...succeeded
Starting tracing...succeeded
Initializing 128 flushers...succeeded
Initializing SDS Server network connections...succeeded
Initializing log/checkpoint information...succeeded
Initializing dbspaces...succeeded
Opening primary chunks...succeeded
Validating chunks...succeeded
Initialize Async Log Flusher...succeeded
Starting B-tree Scanner...succeeded
Init ReadAhead Daemon...succeeded
Init DB Util Daemon...succeeded
Initializing DBSPACETEMP list...succeeded
Init Auto Tuning Daemon...succeeded

WARNING: server initialization failed or timed out.
Check the message log, online.log, for errors.
```
fast recovery时间较长，忽略即可。