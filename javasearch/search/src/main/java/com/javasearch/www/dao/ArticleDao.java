package com.javasearch.www.dao;

import com.javasearch.www.util.ConnectionProxy;
import com.javasearch.www.util.ConnectionUtil;
import com.javasearch.www.util.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author VitamineG
 * @DateTime 2023/3/7 20:53
 */
public class ArticleDao {

    private ArticleDao() {
    }

    private static final ArticleDao articleDao = new ArticleDao();

    private final BaseDao baseDao = BaseDao.getInstance();


    public static ArticleDao getInstance() {
        return articleDao;
    }

    public Integer insert(String tableName, Object data) throws SQLException {
        String sql = ConnectionUtil.getInsertSentence(tableName, data);
        ConnectionProxy connection = baseDao.setConnection(sql);
        if (connection == null){
            Logger.error("get sql connection error");
            return 0;
        }else {
            return connection.executeUpdate();
        }
    }

    public ResultSet select(String tableName, Object data) throws SQLException {
        String sql = ConnectionUtil.getSelectSentence(tableName, data);
        ConnectionProxy connection = baseDao.setConnection(sql);
        if (connection == null){
            Logger.error("get sql connection error");
            return null;
        }else {
            return connection.executeQuery();
        }
    }


    public Integer delete(String document, Integer isDelete, Long articleId) throws SQLException {
        String sql = ConnectionUtil.getUpdateSentence(document, isDelete, articleId);
        ConnectionProxy connection = baseDao.setConnection(sql);
        if (connection == null){
            Logger.error("get sql connection error");
            return null;
        }else {
            return connection.executeUpdate();
        }
    }

    public Integer ScheduledDeleted(String document) throws SQLException {
        String sql = ConnectionUtil.getDeletedSQL(document);
        ConnectionProxy connection = baseDao.setConnection(sql);
        if (connection == null){
            Logger.error("get sql connection error");
            return null;
        }else {
            return connection.executeUpdate();
        }
    }


}
