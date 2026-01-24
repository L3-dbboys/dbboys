创建测试表
```
create table t_user(
c_id serial primary key,  --ID
c_name  varchar(20),  --姓名
c_age int,c_sex char(3),  --性别
c_cardno char(20),  --身份证
c_birthday char(10),  --出生日期
c_phone char(11),  --手机号
c_address1 varchar(255),  --联系地址1
c_address2 lvarchar(255)  --联系地址2
);

```
生成测试数据
```
sh gendata.sh 100000>t_user.unl
```
```
#!/bin/sh
#gendata.sh
for i in `seq $1`
do
xing="赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐"
num=$((RANDOM % ${#xing})) 
ming="靖铭琛川承司斯宗骁聪在钩锦铎楚铮钦则女宝宝宜用字真心新悦西兮楚初千锐素锦静镜斯舒瑜童楠景茗聿启尧言嘉桉桐筒竹林乔栋家翊松楠景茗聿启尧言嘉桉桐筒竹林乔栋家翊松清澈泫浚润泽向凡文浦洲珩玄洋淮雨子云妍澜淇沐潆盈雨文冰雯溪子云汐潞淇妙涵卓昱南晨知宁年易晗炎焕哲煦旭明阳朗典灿夏珞煊晴彤诺宁恬钧灵昭琉晨曦南毓冉辰宸野安为亦围岚也以延允容恩衡宇硕已意也坤辰伊米安恩以容宛岚又衣亚悠允画"
num1=$((RANDOM % ${#ming}))
num2=$((RANDOM % ${#ming}))
age=$(( $RANDOM % (99)))
xingbies="男女"
xingbie=$((RANDOM % ${#xingbies}))
random_day=$((RANDOM % (36500)))
target_timestamp=$((random_day * 86400))
random_date=$(date -d @$target_timestamp "+%Y-%m-%d")
addr=`openssl rand -base64 100`
echo "0|"${xing:$num:1}${ming:$num1:1}${ming:$num2:1}"|"$age"|"${xingbies:$xingbie:1}"|"$(openssl rand -base64 10 |cksum |cut -c1-8)$(openssl rand -base64 10 |cksum |cut -c1-9)"|"$random_date"|139"$(openssl rand -base64 10 |cksum |cut -c1-8)"|"$addr"|"$addr"|"
done
```
导入数据
```
echo "load from t_user.unl insert into t_user;" |dbaccess testdb
```
测试SQL及结果
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
      	<th align="left">场景</th>  <!-- 右对齐-->
        <th align="left">原始SQL</th>  <!-- 右对齐-->
        <th align="left">优化SQL</th>  <!-- 右对齐-->
        <th align="left">原始SQL耗时</th>  <!-- 右对齐-->
        <th align="left">优化SQL耗时</th>  <!-- 右对齐-->
        <th align="left">说明</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>exists简化</td>
        <td>select count(*) <br />
from t_user  t1<br />
where   (select count(*) <br />
from t_user t2 where<br />
t1.c_cardno=t2.c_cardno)>0;</td>
        <td>select count(*) <br />
from t_user  t1<br />
where  exists (select 1<br />
from t_user t2 where<br />
t1.c_cardno=t2.c_cardno);</td>
      	<td>0.593 sec</td>
      	<td>0.253 sec</td>
      	<td>部分场景改写为exists可提升性能</td>
    </tr>
    <tr>
        <td>2</td>
        <td>exists改join</td>
        <td>select count(\*)<br />from t_user  t1<br />where  exists (select 1<br />from t_user t2 where<br />t1.c_cardno=t2.c_cardno);</td>
        <td>select count(*)<br />from t_user  t1 join t_user t2 <br />on t1.c_cardno=t2.c_cardno</td>
      	<td>0.253 sec</td>
      	<td>0.257 sec</td>
      	<td>exists可改写为join，性能差异视场景而定</td>
    </tr>
    <tr>
        <td>3</td>
        <td>exists改in</td>
        <td>select count(\*) <br />
from t_user  t1<br />
where  exists (select 1<br />
from t_user t2 where<br />
t1.c_cardno=t2.c_cardno);</td>
        <td>select count(*) <br />
from t_user t1 where t1.c_cardno in <br />
(select  c_cardno from t_user t2)</td>
      	<td>0.253 sec</td>
      	<td>0.526 sec</td>
      	<td>exists可改写为in，性能差异视场景而定</td>
    </tr>
    <tr>
        <td>4</td>
        <td>exists中使用(+)</td>
        <td>select count(\*) from t_user t1 where exists (select 1 from t_user t2 where t1.c_id=t2.c_id(+))</td>
        <td>select count(\*) from t_user t1 where exists (select 1 from t_user t2 where t1.c_id=t2.c_id)</td>
      	<td>语法错误</td>
      	<td>正常</td>
      	<td>等价</td>
    </tr>
    <tr>
        <td>5</td>
        <td>not exists改not in</td>
        <td>select count(\*) from t_user t1 where not exists (select 1 from t_user t2 where t1.c_id=t2.c_id)</td>
        <td>select count(\*) from t_user t1 where t1.c_id not in (select c_id from t_user t2 where t2.c_id is not null)</td>
      	<td>0.299 sec</td>
      	<td>0.372 sec</td>
      	<td>等价，not exists改not in需要在子查询加上is not null条件</td>
    </tr>
</table>

