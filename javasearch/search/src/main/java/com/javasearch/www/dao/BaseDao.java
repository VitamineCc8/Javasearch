package com.javasearch.www.dao;

import com.javasearch.www.util.ConnectionProxy;
import com.javasearch.www.util.ConnectionUtil;


/**
 * @author VitamineG
 * @DateTime 2023/3/8 22:00
 */
public class BaseDao {
    private BaseDao() {
    }

    private static final BaseDao baseDao = new BaseDao();

    public static BaseDao getInstance() {
        return baseDao;
    }

    public ConnectionProxy setConnection(String sql) {
        ConnectionProxy connection = ConnectionUtil.CONNECTION_TL.get();
        if (connection != null) {
            connection.getPreparedStatement();
            connection.setSql(sql);
            return connection;
        } else {
            return null;
        }
    }
}
