### 数据库服务器客户端配置
下载exporter安装包：【[GBase8s_exporter.jar](https://dl.gbase8s.com:9088/Prometheus/GBase8s_exporter.jar)】

【root】用户创建目录：
```
cd /opt
mkdir GBase8s_exporter
cd GBase8s_exporter
```
【root】用户将以下文件传入【/opt/GBase8s_exporter】
```
GBase8s_exporter.jar
```
【root】用户在【/opt/GBase8s_exporter】目录创建配置文件【[GBase8s_exporter_application.yml](https://dl.gbase8s.com:9088/Prometheus/GBase8s_exporter_application.yml)】，修改连接串url和密码，修改instancename：

```
cd /opt/GBase8s_exporter
cat <<EOF >GBase8s_exporter_application.yml
spring:
  application:
    name: gbase8s_exporter
  datasource:
    druid:
      url: jdbc:gbasedbt-sqli://192.168.17.103:9088/sysmaster:GBASEDBTSERVER=gbase03;IFX_LOCK_MODE_WAIT=10；
      username: gbasedbt
      password: GBase123
      driver-class-name: com.gbasedbt.jdbc.Driver
      max-active: 10
      initial-size: 1
      max-wait: 2000
      min-idle: 1
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 30000
      validation-query: 'select 1 from dual'
      validation-query-timeout: 10
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: false
      break-after-acquire-failure: false
      connection-error-retry-attempts: 10
com:
  gbase:
    instancename: gbase01
    cmname: 

management:
  endpoints:
    web:
      exposure:
        include: "*"
  metrics:
    enable:
      tomcat: false
      jvm: false
      process: false
      hikaricp: false
      system: false
      jdbc: false
      logback: false
      http: false
server:
  servlet:
    context-path: /gbase8s
  port: 8001
logging:
  level:
    root: info
EOF
```
【root】用户在【/opt/GBase8s_exporter】目录创建运行脚本【[exporter.sh](https://dl.gbase8s.com:9088/Prometheus/exporter.sh)】,JAVA_HOME为java解压目录：
``` 
#!/bin/sh

programName=GBase8s_exporter.jar 

JAVA_HOME="/opt/jdk1.8.0_201/bin"

pid=$(ps -ef |grep ${programName} | grep -v grep | grep -v sh | awk '{print $2}')

usage(){
	echo "sh exporter.sh [start|stop|status] "
	exit 1
}


stop(){
	if [[ ! -z ${pid} ]]; then
		kill -9 ${pid}
	fi
}

start(){
	if [[  -z ${pid} ]]; then
		nohup ${JAVA_HOME}/java -jar ${programName} --spring.config.location=GBase8s_exporter_application.yml -Xmx=1024m -Xms=1024m >/dev/null 2>&1 &
	        echo 'Please use <sh exporter.sh status>  to check it running or not !'
	fi
}

status(){
	if [[ -z ${pid} ]]; then
		echo " ${programName}  is not running "
	else
		echo " ${programName}  is running. Pid is ${pid} "
	fi
}


case "$1" in
	"start")
		start
		;;
	"stop")
		stop
		;;
	 "status")
                 status
                  ;;

	*)	
	usage
	;;
esac

```
【root】用户运行：
```
sh exporter.sh start
```
浏览器输入以下地址，检查数据是否正常：【IP端口修改为实际IP和端口】
```
http://192.168.17.103:8001/gbase8s/actuator/prometheus
```
采集指标说明：
 ```
# HELP gbase8s_onstat_p  
# TYPE gbase8s_onstat_p gauge
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_dskreads",type="GBase8s",} 7767.0  #磁盘读
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_bufreads",type="GBase8s",} 393894.0 #缓存读
gbase8s_onstat_p{ip="192.168.17.103",profile="sc_db_rollbk_per",type="GBase8s",} 0.0 #事务回滚百分比
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_dskwrits",type="GBase8s",} 6808.0 #磁盘写入
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_db_rollbks",type="GBase8s",} 0.0 #事务回滚数
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_db_commits",type="GBase8s",} 6826.0 #事务提交数
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_bufwrits",type="GBase8s",} 59632.0 #缓存写
gbase8s_onstat_p{ip="192.168.17.103",profile="sc_cache_read_hitrate",type="GBase8s",} 98.02814970525066 #缓存读命中率
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_seqscans",type="GBase8s",} 1766.0 #顺序扫描数
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_deadlks",type="GBase8s",} 0.0 #死锁数
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_ovlock",type="GBase8s",} 0.0 #锁溢出
gbase8s_onstat_p{ip="192.168.17.103",profile="sc_cache_write_hitrate",type="GBase8s",} 88.58331097397371 #写缓存命中率
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_ovbuff",type="GBase8s",} 0.0 #缓存溢出
gbase8s_onstat_p{ip="192.168.17.103",profile="ts_isamtot",type="GBase8s",} 465609.0 #调用总数
# HELP gbase8s_db_mode  
# TYPE gbase8s_db_mode gauge
gbase8s_db_mode{db_mode="stand-alone",ip="192.168.17.103",type="GBase8s",} 0.0
gbase8s_db_mode{db_mode="stand-alone",ip="192.168.17.101",type="GBase8s",} 0.0
# HELP gbase8s_onstat_g_sql_count  
# TYPE gbase8s_onstat_g_sql_count gauge
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="insert",type="GBase8s",} 0.0 #插入sql总数
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="select",type="GBase8s",} 0.0 #查询sql总数
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="update",type="GBase8s",} 0.0 #更新sql总数
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="delete",type="GBase8s",} 0.0 #删除sql总数
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="activeSqlCount",type="GBase8s",} 0.0 #活动sql总数
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="sessionCount",type="GBase8s",} 35.0 #连接会话总数
gbase8s_onstat_g_sql_count{ip="192.168.17.103",stat_type="allSqlCount",type="GBase8s",} 0.0 #运行sql总数
# HELP gbase8s_dbspace_usage  
# TYPE gbase8s_dbspace_usage gauge
gbase8s_dbspace_usage{alldbs="avg_usage",ip="192.168.17.103",type="GBase8s",} 37.67 #所有空间使用率
# HELP gbase8s_ckp_dirty_buffers  
# TYPE gbase8s_ckp_dirty_buffers gauge
gbase8s_ckp_dirty_buffers{ckp="0",ip="192.168.17.103",type="GBase8s",} 7.0 #检查点每秒写入脏数据页
# HELP gbase8s_buff_seg_count  
# TYPE gbase8s_buff_seg_count gauge
gbase8s_buff_seg_count{classType="b",ip="192.168.17.103",type="GBase8s",} 2.0 #缓存区内存段个数
gbase8s_buff_seg_count{classType="r",ip="192.168.17.103",type="GBase8s",} 1.0 #保留区内存段个数
gbase8s_buff_seg_count{classType="v",ip="192.168.17.103",type="GBase8s",} 1.0 #虚拟区内存段个数
# HELP gbase8s_buff_seg_blkused_sum  
# TYPE gbase8s_buff_seg_blkused_sum gauge
gbase8s_buff_seg_blkused_sum{classType="b",ip="192.168.17.103",type="GBase8s",} 68273.0 #缓存区使用块数
gbase8s_buff_seg_blkused_sum{classType="r",ip="192.168.17.103",type="GBase8s",} 4014.0 #保留区使用块数
gbase8s_buff_seg_blkused_sum{classType="v",ip="192.168.17.103",type="GBase8s",} 29538.0 #虚拟区使用块数
# HELP gbase8s_ckp_dskful_buffers  
# TYPE gbase8s_ckp_dskful_buffers gauge
gbase8s_ckp_dskful_buffers{ckp="0",ip="192.168.17.103",type="GBase8s",} 7.0 #检查点每秒写入缓存数
# HELP gbase8s_dbspace_utiliaztion  
# TYPE gbase8s_dbspace_utiliaztion gauge
gbase8s_dbspace_utiliaztion{dbspacename="rootdbs",dbstype="PhysicalLogspace",ip="192.168.17.103",type="GBase8s",} 74.89 #rootdbs空间使用率
gbase8s_dbspace_utiliaztion{dbspacename="datadbs02",dbstype="Dbspace",ip="192.168.17.103",type="GBase8s",} 0.45 #datadbs02空间使用率
# HELP gbase8s_ckp_block_time  
# TYPE gbase8s_ckp_block_time gauge
gbase8s_ckp_block_time{ckp="0",ip="192.168.17.103",type="GBase8s",} 0.0 #检查点阻塞时间
# HELP gbase8s_ckp_total_time  
# TYPE gbase8s_ckp_total_time gauge
gbase8s_ckp_total_time{ckp="0",ip="192.168.17.103",type="GBase8s",} 1.1 #检查点执行时间
# HELP gbase8s_onstat_g_rea_count  
# TYPE gbase8s_onstat_g_rea_count gauge
gbase8s_onstat_g_rea_count{cpClass="cpu",ip="192.168.17.103",status="ready",type="GBase8s",} 0.0 #就绪队列数
# HELP gbase8s_db_status  
# TYPE gbase8s_db_status gauge
gbase8s_db_status{ip="192.168.17.103",ts_db_status="On-Line ",type="GBase8s",} 1.0 #数据库实例状态
# HELP gbase8s_onstat_g_act_count  
# TYPE gbase8s_onstat_g_act_count gauge
gbase8s_onstat_g_act_count{cpClass="cpu",ip="192.168.17.103",status="running",type="GBase8s",} 0.0 #活动事务数
# HELP gbase8s_ckp_flush_time  
# TYPE gbase8s_ckp_flush_time gauge
gbase8s_ckp_flush_time{ckp="0",ip="192.168.17.103",type="GBase8s",} 0.0 #检查点写入脏数据时间
# HELP gbase8s_db_runtime  
# TYPE gbase8s_db_runtime gauge
gbase8s_db_runtime{ip="192.168.17.103",ts_db_runtime=" Up 00:02:10 ",type="GBase8s",} 1.0 #数据库运行时间
# HELP gbase8s_onstat_g_glo  
# TYPE gbase8s_onstat_g_glo gauge
gbase8s_onstat_g_glo{ip="192.168.17.103",type="GBase8s",} 36.0 #数据库VP总数量
# HELP gbase8s_buff_seg_blkfree_sum  
# TYPE gbase8s_buff_seg_blkfree_sum gauge
gbase8s_buff_seg_blkfree_sum{classType="b",ip="192.168.17.103",type="GBase8s",} 0.0 #缓存区空闲块数
gbase8s_buff_seg_blkfree_sum{classType="r",ip="192.168.17.103",type="GBase8s",} 0.0 #保留区
空闲块数闲
gbase8s_buff_seg_blkfree_sum{classType="v",ip="192.168.17.103",type="GBase8s",} 72862.0 #虚拟区使用块数
```
### 普罗米修斯服务端配置
在普罗米修斯服务端配置文件最后加上下面三行：
【192.168.17.103:8001】为数据库IP和采集端口。
```
cp /opt/prometheus/prometheus.yml /opt/prometheus/prometheus.yml.bak
cat <<! >>/opt/prometheus/prometheus.yml
  - job_name: 'gbase01'
    metrics_path: /gbase8s/actuator/prometheus
    static_configs:
    - targets: ['192.168.17.103:8001']
!
```
重启普罗米修斯服务：
```
pkill prometheus
/opt/prometheus/prometheus --config.file="/opt/prometheus/prometheus.yml" &
 ```
要图形化展示采集数据，参考图形模板：【[GBase8s.json](https://dl.gbase8s.com:9088/Prometheus/GBase8s.json)】
