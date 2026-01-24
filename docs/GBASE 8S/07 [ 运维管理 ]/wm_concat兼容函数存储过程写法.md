# wm_concat兼容函数存储过程写法  
wm_conat函数的GBase 8s兼容写法  
```sql
DROP FUNCTION IF EXISTS wm_concat_ws_init;
-- p_delimiter : delimiter, default ','
CREATE dba FUNCTION  wm_concat_ws_init (p_dummy varchar(255), p_delimiter char(1) default ',')
  RETURNING varchar(255) with (not variant);
  RETURN p_delimiter;
END FUNCTION;

DROP FUNCTION IF EXISTS wm_concat_ws_iter;
-- p_iter_result : iter result
-- p_next_value  : iter next value
CREATE dba FUNCTION  wm_concat_ws_iter (p_iter_result varchar(255), p_next_value varchar(255))
  RETURNING varchar(255) with (not variant);
  DEFINE v_del char(1);
  LET v_del = left(p_iter_result,1);
  RETURN p_iter_result || p_next_value || v_del;
END FUNCTION;

DROP FUNCTION IF EXISTS wm_concat_ws_combine;
CREATE dba FUNCTION  wm_concat_ws_combine(p_partial1 varchar(255), p_partial2 varchar(255))
  RETURNING varchar(255) with (not variant);
  RETURN p_partial1 || p_partial2;
END FUNCTION;

DROP FUNCTION IF EXISTS wm_concat_ws_final;
-- p_final : finish iter result
CREATE dba FUNCTION  wm_concat_ws_final(p_final varchar(255))
  RETURNING varchar(255) with (not variant);
  DEFINE v_len int;
  ON EXCEPTION
    RETURN null;
  END EXCEPTION;
  IF p_final IS NULL OR p_final = '' THEN
    RETURN null;
  ELSE
    LET v_len = CHAR_LENGTH(p_final) - 1;
	-- remove head and tail p_delimiter
    RETURN substr(p_final,2,v_len);
  END IF;
END FUNCTION;

DROP aggregate if exists wm_concat_ws;
-- param1 : column for aggregate
-- param2 : delimiter to init (p_delimiter)
create aggregate wm_concat_ws with
(
  INIT = wm_concat_ws_init,
  ITER = wm_concat_ws_iter,
  COMBINE = wm_concat_ws_combine,
  FINAL = wm_concat_ws_final
);
```
增加允许指定分隔符的写法：
```sql
DROP FUNCTION IF EXISTS wm_concat_ws_init;
-- p_delimiter : delimiter, default ','
CREATE dba FUNCTION  wm_concat_ws_init (p_dummy varchar(255), p_delimiter char(1) default ',')
  RETURNING varchar(255) with (not variant);
  RETURN p_delimiter;
END FUNCTION;

DROP FUNCTION IF EXISTS wm_concat_ws_iter;
-- p_iter_result : iter result
-- p_next_value  : iter next value
CREATE dba FUNCTION  wm_concat_ws_iter (p_iter_result varchar(255), p_next_value varchar(255))
  RETURNING varchar(255) with (not variant);
  DEFINE v_del char(1);
  LET v_del = left(p_iter_result,1);
  RETURN p_iter_result || p_next_value || v_del;
END FUNCTION;

DROP FUNCTION IF EXISTS wm_concat_ws_combine;
CREATE dba FUNCTION  wm_concat_ws_combine(p_partial1 varchar(255), p_partial2 varchar(255))
  RETURNING varchar(255) with (not variant);
  RETURN p_partial1 || p_partial2;
END FUNCTION;

DROP FUNCTION IF EXISTS wm_concat_ws_final;
-- p_final : finish iter result
CREATE dba FUNCTION  wm_concat_ws_final(p_final varchar(255))
  RETURNING varchar(255) with (not variant);
  DEFINE v_len int;
  ON EXCEPTION
    RETURN null;
  END EXCEPTION;
  IF p_final IS NULL OR p_final = '' THEN
    RETURN null;
  ELSE
    LET v_len = CHAR_LENGTH(p_final) - 1;
	-- remove head and tail p_delimiter
    RETURN substr(p_final,2,v_len);
  END IF;
END FUNCTION;

DROP aggregate if exists wm_concat_ws;
-- param1 : column for aggregate
-- param2 : delimiter to init (p_delimiter)
create aggregate wm_concat_ws with
(
  INIT = wm_concat_ws_init,
  ITER = wm_concat_ws_iter,
  COMBINE = wm_concat_ws_combine,
  FINAL = wm_concat_ws_final
);
```