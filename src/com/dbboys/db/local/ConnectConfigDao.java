package com.dbboys.db.local;

import com.dbboys.vo.Connect;
import com.dbboys.vo.ConnectFolder;
import com.dbboys.vo.TreeData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectConfigDao {
    private static final Logger log = LogManager.getLogger(ConnectConfigDao.class);

    public List<TreeData> getAll() {
        List<TreeData> list = new ArrayList<>();
        try (Statement stmt = LocalDbConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select c_id,c_parentid,c_name,c_ip,c_port,c_database,c_username,c_dbtype,c_password," +
                     "c_driver,c_props,c_info,c_drivermd5,c_dbversion,c_readonly from t_connect order by c_name")) {
            while (rs.next()) {
                list.add(mapConnect(rs));
            }
        } catch (Exception e) {
            log.error("Failed to load connects", e);
        }
        return list;
    }

    public List<TreeData> getByFolder(ConnectFolder folder) {
        List<TreeData> list = new ArrayList<>();
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "select c_id,c_parentid,c_name,c_dbtype,c_ip,c_port,c_database,c_readonly,c_username,c_password," +
                "c_driver,c_props,c_info,c_drivermd5,c_dbversion from t_connect where c_parentid=? order by c_name")) {
            ps.setInt(1, folder.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Connect c = new Connect();
                    c.setId(rs.getInt(1));
                    c.setParentId(rs.getInt(2));
                    c.setName(rs.getString(3));
                    c.setDbtype(rs.getString(4));
                    c.setIp(rs.getString(5));
                    c.setPort(rs.getString(6));
                    c.setDatabase(rs.getString(7));
                    c.setReadonly("1".equals(rs.getString(8)));
                    c.setUsername(rs.getString(9));
                    c.setPassword(rs.getString(10));
                    c.setDriver(rs.getString(11));
                    c.setProps(rs.getString(12));
                    c.setInfo(rs.getString(13));
                    c.setDrivermd5(rs.getString(14));
                    c.setDbversion(rs.getString(15));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load folder connects", e);
        }
        return list;
    }

    public String create(Connect connect) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "insert into t_connect(c_parentid,c_name,c_dbtype,c_driver,c_ip,c_port,c_database,c_readonly," +
                "c_username,c_password,c_props,c_info,c_drivermd5,c_dbversion) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            ps.setObject(1, connect.getParentId());
            ps.setObject(2, connect.getName());
            ps.setObject(3, connect.getDbtype());
            ps.setObject(4, connect.getDriver());
            ps.setObject(5, connect.getIp());
            ps.setObject(6, connect.getPort());
            ps.setObject(7, connect.getDatabase());
            ps.setObject(8, connect.getReadonly() ? "1" : "0");
            ps.setObject(9, connect.getUsername());
            ps.setObject(10, connect.getPassword());
            ps.setObject(11, connect.getProps());
            ps.setObject(12, connect.getInfo());
            ps.setObject(13, connect.getDrivermd5());
            ps.setObject(14, connect.getDbversion());
            ps.executeUpdate();

            try (PreparedStatement ps2 = LocalDbConnection.get().prepareStatement("select max(c_id) from t_connect");
                 ResultSet rs = ps2.executeQuery()) {
                if (rs.next()) {
                    connect.setId(rs.getInt(1));
                }
            }
            return "";
        } catch (Exception e) {
            log.error("Failed to create connect", e);
            return e.getMessage();
        }
    }

    public boolean update(Connect connect) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "update t_connect set c_parentid=?,c_name=?,c_database=?,c_props=? where c_id=?")) {
            ps.setInt(1, connect.getParentId());
            ps.setString(2, connect.getName());
            ps.setString(3, connect.getDatabase());
            ps.setString(4, connect.getProps());
            ps.setInt(5, connect.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Failed to update connect", e);
            return false;
        }
    }

    public boolean delete(Connect connect) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement("delete from t_connect where c_id=?")) {
            ps.setInt(1, connect.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete connect", e);
            return false;
        }
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement("delete from t_sqlhistory where c_connectid=?")) {
            ps.setInt(1, connect.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete connect history", e);
        }
        return true;
    }

    public String getCopyName(Connect connect) {
        List<String> names = new ArrayList<>();
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement("select c_name from t_connect");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException e) {
            log.error("Failed to get copy name", e);
        }
        int i = 1;
        String result;
        while (true) {
            result = connect.getName() + "_" + i;
            if (!names.contains(result)) break;
            i++;
        }
        return result;
    }

    public boolean checkExists(Connect connect) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "select c_name from t_connect where c_name=? and c_id!=?")) {
            ps.setString(1, connect.getName());
            ps.setInt(2, connect.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Failed to check connect exists", e);
            return false;
        }
    }

    public boolean checkDriverInUse(String jarName) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "select 1 from t_connect where c_driver=?")) {
            ps.setString(1, jarName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Failed to check driver in use", e);
            return false;
        }
    }

    public String getFolderName(Connect connect) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "select c_name from t_connect_folder where c_id=?")) {
            ps.setInt(1, connect.getParentId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get folder name", e);
        }
        return null;
    }

    private Connect mapConnect(ResultSet rs) throws SQLException {
        Connect c = new Connect();
        c.setId(rs.getInt(1));
        c.setParentId(rs.getInt(2));
        c.setName(rs.getString(3));
        c.setIp(rs.getString(4));
        c.setPort(rs.getString(5));
        c.setDatabase(rs.getString(6));
        c.setUsername(rs.getString(7));
        c.setDbtype(rs.getString(8));
        c.setPassword(rs.getString(9));
        c.setDriver(rs.getString(10));
        c.setProps(rs.getString(11));
        c.setInfo(rs.getString(12));
        c.setDrivermd5(rs.getString(13));
        c.setDbversion(rs.getString(14));
        c.setReadonly("1".equals(rs.getString(15)));
        return c;
    }
}
