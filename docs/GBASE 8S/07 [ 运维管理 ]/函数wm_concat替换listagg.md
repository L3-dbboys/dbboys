```
create table t1(col1 varchar(10),col2 varchar(10),col3 varchar(10),col4 varchar(10),col5 varchar(10));
insert into t1 values('a','a22','ma3','fa4','da5');
insert into t1 values('b','4yy','adf','sdf','3ff');
insert into t1 values('a','a22','ndn','sfv','1tr');
insert into t1 values('b','4yy','43t','sdv','32v');
insert into t1 values('c','rgg','43t','5uj','oyt');
insert into t1 values('a','kkj','3tg','mdd','ljj');
```
```
select col1,col2,listagg(col3,',')  within group (order by col4,col5) as listaggcol from t1 group by col1,col2;
```
等价wm_concat:
```
with tmp1 as (select * from t1 order by col4,col5) select col1,col2,wm_concat(col3) as listaggcol from tmp1 group by col1,col2;
```
```
select col1,col2,listagg(col3,',')  within group (order by col5,col4) as listaggcol from t1 group by col1,col2;
```
等价wm_concat:
```
with tmp1 as (select * from t1 order by col5,col4) select col1,col2,wm_concat(col3) as listaggcol from tmp1 group by col1,col2;
```

