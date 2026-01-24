### 示例1-避免全表扫描(AVOID_FULL)
```
SELECT /*+ AVOID_FULL(t_user) */
count(*) FROM t_user WHERE c_sex='男';
```
### 示例2-不走指定索引(AVOID_INDEX)
```
SELECT /*+ AVOID_INDEX(t_user idx_c_sex) */
count(*) FROM t_user WHERE c_sex='男';
```
### 示例3-指定表全表扫描(FULL)
```
SELECT /*+ FULL(t_user) */
count(*) FROM t_user WHERE c_sex='男';
```
### 示例4-指定表走指定索引(INDEX)
```
SELECT /*+ INDEX(t_user idx_c_sex) */
count(*) FROM t_user WHERE c_sex='男';
```
### 示例5-按书写顺序join(ORDERED)
```
SELECT /*+ ORDERED */
count(*) FROM t_user t1 join t_user t2
on t1.c_cardno=t2.c_cardno
join t_user t3
on t2.c_cardno=t3.c_cardno
WHERE t1.c_cardno='430524199008129900';
```
### 示例6-使用嵌套循环连接(USE_NL)
```
SELECT /*+ USE_NL(t2,t3) */
count(*) FROM t_user t1 join t_user t2
on t1.c_cardno=t2.c_cardno
join t_user t3
on t2.c_cardno=t3.c_cardno
WHERE t1.c_cardno='430524199008129900';
```
### 示例7-使用哈希连接(USE_HASH)
```
SELECT /*+ USE_HASH(t2,t3) */
count(*) FROM t_user t1 join t_user t2
on t1.c_cardno=t2.c_cardno
join t_user t3
on t2.c_cardno=t3.c_cardno
WHERE t1.c_cardno='430524199008129900';
```
参考文档【[GBase 8s SQL 指南：语法.pdf](https://www.dbboys.com/dl/gbase8s/docs/Guide_to_SQL_Syntax.pdf)】


