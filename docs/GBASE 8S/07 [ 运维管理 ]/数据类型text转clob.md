```
dbaccess myblog <<!
    drop table blogtext;
    create table blogtext(id serial not null primary key,name varchar(1024),blog text, time  datetime year to second default current year to second);
    load from blog.unload insert into blogtext;

    drop table blogclob;
    create table blogclob(id serial not null primary key,name varchar(1024),blog clob, time  datetime year to second default current year to second);
    insert into blogclob(id,name,blog,time) select id,name,LOCOPY(blog),time from blogtext ;
    create index btsidx01 on blogclob(blog bts_clob_ops) using bts(analyzer="CJK");
!
```