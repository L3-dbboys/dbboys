package com.dbboys.db.local;

import com.dbboys.vo.UpdateResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlHistoryDao {
    private static final Logger log = LogManager.getLogger(SqlHistoryDao.class);

    public String save(UpdateResult result) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "insert into t_sqlhistory values(?,?,?,?,?,?,?,?)")) {
            ps.setObject(1, result.getConnectId());
            ps.setObject(2, result.getDatabase());
            ps.setObject(3, result.getUpdateSql());
            ps.setObject(4, result.getStartTime());
            ps.setObject(5, result.getEndTime());
            ps.setObject(6, result.getElapsedTime());
            ps.setObject(7, result.getAffectedRows());
            ps.setObject(8, result.getMark());
            ps.executeUpdate();
            return "";
        } catch (Exception e) {
            log.error("Failed to save SQL history", e);
            return e.getMessage();
        }
    }

    public List<UpdateResult> getList(Integer connectId) {
        List<UpdateResult> list = new ArrayList<>();
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "select * from t_sqlhistory where c_connectid=?")) {
            ps.setObject(1, connectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UpdateResult ur = new UpdateResult();
                    ur.setDatabase(rs.getString(2));
                    ur.setUpdateSql(rs.getString(3));
                    ur.setStartTime(rs.getString(4));
                    ur.setEndTime(rs.getString(5));
                    ur.setElapsedTime(rs.getString(6));
                    ur.setAffectedRows(rs.getInt(7));
                    ur.setMark(rs.getString(8));
                    list.add(ur);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load SQL history", e);
        }
        return list;
    }

    public boolean deleteAll() {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement("""
                DELETE FROM t_sqlhistory
                WHERE (c_connectid, c_endtime) NOT IN (
                  SELECT c_connectid, c_endtime
                  FROM (
                    SELECT c_connectid, c_endtime,
                      ROW_NUMBER() OVER (PARTITION BY c_connectid ORDER BY c_endtime DESC) AS rn
                    FROM t_sqlhistory
                  ) AS ranked
                  WHERE rn <= 1000)
                """)) {
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Failed to delete SQL history", e);
            return false;
        }
    }
}
