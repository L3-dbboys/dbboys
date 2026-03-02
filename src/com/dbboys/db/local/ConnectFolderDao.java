package com.dbboys.db.local;

import com.dbboys.vo.ConnectFolder;
import com.dbboys.vo.TreeData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectFolderDao {
    private static final Logger log = LogManager.getLogger(ConnectFolderDao.class);

    public List<TreeData> getAll() {
        List<TreeData> folders = new ArrayList<>();
        try (Statement stmt = LocalDbConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery("select c_id,c_name,c_expand from t_connect_folder order by c_name")) {
            while (rs.next()) {
                ConnectFolder folder = new ConnectFolder();
                folder.setId(rs.getInt(1));
                folder.setName(rs.getString(2));
                folder.setExpand(rs.getInt(3));
                folders.add(folder);
            }
        } catch (Exception e) {
            log.error("Failed to load connect folders", e);
        }
        return folders;
    }

    public boolean create(ConnectFolder folder) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "insert into t_connect_folder(c_name,c_expand) values(?,?)")) {
            ps.setObject(1, folder.getName());
            ps.setObject(2, folder.getExpand());
            ps.executeUpdate();

            try (PreparedStatement ps2 = LocalDbConnection.get().prepareStatement(
                    "select max(c_id) from t_connect_folder");
                 ResultSet rs = ps2.executeQuery()) {
                if (rs.next()) {
                    folder.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to create connect folder", e);
            return false;
        }
    }

    public boolean update(ConnectFolder folder) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "update t_connect_folder set c_name=?,c_expand=? where c_id=?")) {
            ps.setObject(1, folder.getName());
            ps.setObject(2, folder.getExpand());
            ps.setObject(3, folder.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Failed to update connect folder", e);
            return false;
        }
    }

    public boolean delete(ConnectFolder folder) {
        try (PreparedStatement ps = LocalDbConnection.get().prepareStatement(
                "delete from t_connect_folder where c_id=?")) {
            ps.setObject(1, folder.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Failed to delete connect folder", e);
            return false;
        }
    }
}
