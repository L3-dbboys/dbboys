-- @sqlexe.sys_dual
select  * from sysmaster:sysdual

-- @sqlexe.sqlmode_gbase
set environment sqlmode 'gbase'

-- @sqlexe.sqlmode_mysql
set environment sqlmode 'mysql'

-- @sqlexe.sqlmode_oracle
set environment sqlmode 'oracle'

-- @sqlexe.tabnames_group
select tabid,tabname from systables group by 1
