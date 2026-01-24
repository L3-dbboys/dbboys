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
生成测试数据，或下载【[t_user.unl](https://dl.gbase8s.com:9088/TestData/t_user.unl)】。
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
常用Hint关键字
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
      	<th align="left">关键字</th>  <!-- 右对齐-->
        <th align="left">作用</th>  <!-- 右对齐-->
        <th align="left">示例</th>  <!-- 右对齐-->
        <th align="left">说明</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>AVOID_FULL</td>
        <td>指定表不全表扫描</td>
        <td>SELECT /\*+ AVOID_FULL(t_user) \*/<br />
count(\*) FROM t_user WHERE c_sex='男';</td>
      	<td>部分场景优化器走全表扫描可能不是最优路径，需要避免全表扫描</td>
    </tr>
    <tr>
        <td>2</td>
        <td>AVOID_INDEX</td>
        <td>指定不走指定索引</td>
        <td>SELECT /\*+ AVOID_INDEX(t_user idx_c_sex) \*/<br />
count(\*) FROM t_user WHERE c_sex='男';</td>
      	<td>存在多个索引的情况下，优化器可能选择的索引不是最佳索引，可将指定索引排除</td>
    </tr>
    <tr>
        <td>3</td>
        <td>FULL</td>
        <td>指定表全表扫描</td>
        <td>SELECT /\*+ FULL(t_user) \*/<br />
count(\*) FROM t_user WHERE c_sex='男';</td>
      	<td>部分场景使用索引扫描可能比全表扫描更慢，可指定全表扫描</td>
    </tr>
    <tr>
        <td>4</td>
        <td>INDEX</td>
        <td>指定表走指定索引</td>
        <td>SELECT /\*+ INDEX(t_user idx_c_sex) \*/<br />
count(\*) FROM t_user WHERE c_sex='男';</td>
      	<td>优化器可能选择的索引路径不是最优路径，指定走某个索引</td>
    </tr>
    <tr>
        <td>5</td>
        <td>ORDERED</td>
        <td>按书写顺序join</td>
        <td>SELECT /\*+ ORDERED \*/<br />
count(\*) FROM t_user t1 join t_user t2<br />
          on t1.c_cardno=t2.c_cardno<br />
          join t_user t3<br />
          on t2.c_cardno=t3.c_cardno<br />
          WHERE t1.c_cardno='430524199008129900';</td>
      	<td>按join的书写顺序执行join顺序，可按用户的意愿执行</td>
    </tr>
    <tr>
        <td>6</td>
        <td>USE_NL</td>
        <td>使用嵌套循环连接</td>
        <td>SELECT /\*+ USE_NL(t2,t3) \*/<br />
count(\*) FROM t_user t1 join t_user t2<br />
          on t1.c_cardno=t2.c_cardno<br />
          join t_user t3<br />
          on t2.c_cardno=t3.c_cardno<br />
          WHERE t1.c_cardno='430524199008129900';</td>
      	<td>指定使用嵌套循环连接，可能比使用hash join更快</td>
    </tr>
    <tr>
        <td>7</td>
        <td>USE_HASH</td>
        <td>使用哈希连接</td>
        <td>SELECT /\*+ USE_HASH(t2,t3) \*/<br />
count(\*) FROM t_user t1 join t_user t2<br />
          on t1.c_cardno=t2.c_cardno<br />
          join t_user t3<br />
          on t2.c_cardno=t3.c_cardno<br />
          WHERE t1.c_cardno='430524199008129900';</td>
      	<td>指定使用hash连接，可能比使用嵌套循环连接更快</td>
    </tr>
</table>

