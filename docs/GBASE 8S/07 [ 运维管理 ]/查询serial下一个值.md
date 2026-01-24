```
SELECT t.tabname, GREATEST(p.serialv,p.cur_serial8,cur_bigserial) AS cur_serial 
FROM systables t, sysmaster:sysptnhdr p
WHERE t.partnum = p.partnum
  AND t.tabname = 'systables'; 
```