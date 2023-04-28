package com.javasearch.www.dao;

import com.javasearch.www.util.ConnectionProxy;
import com.javasearch.www.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author VitamineG
 * @DateTime 2023/3/7 20:54
 */
public class IndexDao {

    private IndexDao() {
    }

    private static final IndexDao indexDao = new IndexDao();

    private final BaseDao baseDao = BaseDao.getInstance();

    public static IndexDao getInstance() {
        return indexDao;
    }

}

