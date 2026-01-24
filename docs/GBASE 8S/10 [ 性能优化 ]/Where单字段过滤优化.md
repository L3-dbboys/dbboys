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
        <th align="left">过滤字段类型</th>  <!-- 右对齐-->
        <th align="left">索引字段</th>  <!-- 右对齐-->
        <th align="left">执行耗时</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>select \* from t_user where c_age='100';</td>
        <td>100000</td>
        <td>int</td>
      	<td>无</td>
      	<td>0.021 sec</td>
    </tr>
    <tr>
        <td>2</td>
        <td>select \* from t_user where c_cardno='430524199008129900';</td>
        <td>100000</td>
        <td>char(20)</td>
      	<td>无</td>
      	<td>0.021 sec</td>
    </tr>
    <tr>
        <td>3</td>
        <td>select \* from t_user where c_name='天津南';</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>无</td>
      	<td>0.022 sec</td>
    </tr>
    <tr>
        <td>4</td>
        <td>select \* from t_user where c_address1='天津南大通用数据技术股份有限公司';</td>
        <td>100000</td>
        <td>varchar(255)</td>
      	<td>无</td>
      	<td>0.021 sec</td>
    </tr>
    <tr>
        <td>5</td>
        <td>select * from t_user where c_address2='天津南大通用数据技术股份有限公司';</td>
        <td>100000</td>
        <td>lvarchar(255)</td>
      	<td>无</td>
      <td>0.336 sec</td>
    </tr>
    <tr>
        <td>6</td>
        <td>select \* from t_user where c_age='100';</td>
        <td>100000</td>
        <td>int</td>
      	<td>c_age</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>7</td>
        <td>select \* from t_user where c_cardno='430524199008129900';</td>
        <td>100000</td>
        <td>char(20)</td>
      	<td>c_cardno</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>8</td>
        <td>select \* from t_user where c_name='天津南';</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_name</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>9</td>
        <td>select \* from t_user where c_address1='天津南大通用数据技术股份有限公司';</td>
        <td>100000</td>
        <td>varchar(255)</td>
      	<td>c_address1</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>10</td>
        <td>select \* from t_user where c_address2='天津南大通用数据技术股份有限公司';</td>
        <td>100000</td>
        <td>lvarchar(255)</td>
      	<td>c_address2</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>11</td>
        <td>select count(\*) from t_user where c_address1 &lg; 'skdflsdjlf' and c_address1 &lt;'sd;fjllksd';</td>
        <td>100000</td>
        <td>varchar(255)</td>
      	<td>c_address1</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>12</td>
        <td>select count(\*) from t_user where c_address2 &lg; 'skdflsdjlf' and c_address1 &lt;'sd;fjllksd';</td>
        <td>100000</td>
        <td>lvarchar(255)</td>
      	<td>c_address2</td>
      	<td>0.350 sec</td>
    </tr>
    <tr>
        <td>13</td>
        <td>select \* from t_user where like '天津南';</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_name</td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>14</td>
        <td>select \* from t_user where c_name like '天津南%';</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_name</td>
      	<td>0.103 sec</td>
    </tr>
    <tr>
        <td>15</td>
        <td>select \* from t_user where c_name like '%天津南%';</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_name</td>
      	<td>0.022 sec</td>
    </tr>
    <tr>
        <td>16</td>
        <td>select \* from t_user where instr(c_name,'天津南')>0;</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_name</td>
      	<td>0.075 sec</td>
    </tr>
    <tr>
        <td>17</td>
        <td>select \* from t_user where c_birthday='2020-01-01';</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_birthday </td>
      	<td>0.002 sec</td>
    </tr>
    <tr>
        <td>18</td>
        <td>select \* from t_user where to_date(c_birthday,'yyyy-mm-dd')=current;</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_birthday </td>
      	<td>1.018 sec</td>
    </tr>
    <tr>
        <td>19</td>
        <td>select \* from t_user where c_birthday=to_char(current);</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_birthday </td>
      	<td>1.033 sec</td>
    </tr>
    <tr>
        <td>20</td>
        <td>select \* from t_user where c_birthday=to_char(current)::varchar(20);</td>
        <td>100000</td>
        <td>varchar(20)</td>
      	<td>c_birthday </td>
      	<td>0.003 sec</td>
    </tr>
</table>

1、对于选择度较高（如唯一值个数>10）的字段作为where条件的过滤字段，应当创建索引提高效率
2、lvarchar在顺序扫描、优化器执行计划制定等方面，与varchar/char存在明显的性能差异，应尽量避免使用，新版本varchar已支持最长32765字节，可平替lvarchar
3、单表通过一个where条件过滤的简单sql（结果集小），正常运行时间应该在10ms以内
4、尽量避免在过滤字段上使用函数，函数写在常量上
5、to_char返回lvarchar，需转换为varchar才能正常使用索引



