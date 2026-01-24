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
      	<th align="left">SQL</th>  <!-- 右对齐-->
        <th align="left">表行数</th>  <!-- 右对齐-->
        <th align="left">分组字段</th>  <!-- 右对齐-->
        <th align="left">索引字段</th>  <!-- 右对齐-->
        <th align="left">执行耗时</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>select count(*) from(select max(c_name) from t_user group by c_birthday);</td>
        <td>100000</td>
        <td>c_birthday</td>
      	<td>无</td>
      	<td>0.347 sec</td>
    </tr>
    <tr>
        <td>2</td>
        <td>select count(*) from(select max(c_name) from t_user group by c_birthday);</td>
        <td>100000</td>
        <td>c_birthday</td>
      	<td>c_birthday</td>
      	<td>0.328 sec</td>
    </tr>
    <tr>
        <td>3</td>
        <td>set pdqpriority 100;<br />
          select count(*) from(select max(c_name) from t_user group by c_birthday);</td>
        <td>100000</td>
        <td>c_birthday</td>
      	<td>无</td>
      	<td>0.098 sec</td>
    </tr>
    <tr>
        <td>4</td>
        <td>select count(*) from(select distinct c_birthday from t_user);</td>
        <td>100000</td>
        <td>c_birthday</td>
      	<td>无</td>
      	<td>0.162 sec</td>
    </tr>
    <tr>
        <td>5</td>
        <td>select count(*) from(select distinct c_birthday from t_user);</td>
        <td>100000</td>
        <td>c_birthday</td>
      	<td>c_birthday</td>
      	<td>0.134 sec</td>
    </tr>
    <tr>
        <td>6</td>
        <td>set pdqpriority 100;<br />select count(*) from(select distinct c_birthday from t_user);</td>
        <td>100000</td>
        <td>c_birthday</td>
      	<td>无</td>
      	<td>0.144 sec</td>
    </tr>
    <tr>
        <td>7</td>
        <td>select count(*) from(select distinct c_name,c_birthday,c_cardno from t_user);</td>
        <td>100000</td>
        <td>c_name,c_birthday,c_cardno</td>
      	<td>无</td>
      	<td>0.445 sec</td>
    </tr>
    <tr>
        <td>8</td>
        <td>set pdqpriority 100;<br />select count(*) from(select distinct c_name,c_birthday,c_cardno from t_user);</td>
        <td>100000</td>
        <td>c_name,c_birthday,c_cardno</td>
      	<td>无</td>
      	<td>0.252 sec</td>
    </tr>
</table>

1、设置并行可不同程度提升group by性能  
2、distinct字段越多，并行提升性能越明显
