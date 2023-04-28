package com.javasearch.www.service;

import com.javasearch.www.dao.ArticleDao;
import com.javasearch.www.pojo.DocumentDao;
import com.javasearch.www.pojo.Search.IdContextVO;
import com.javasearch.www.pojo.Search.QueryDao;
import com.javasearch.www.pojo.ResultVO;
import com.javasearch.www.util.CommonUtil;
import com.javasearch.www.util.ConnectionProxy;
import com.javasearch.www.util.ConnectionUtil;
import com.javasearch.www.util.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author VitamineG
 * @DateTime 2023/3/8 13:26
 */
public class ArticleService {

    private ArticleService() {
    }

    private static final ArticleService articleService = new ArticleService();

    public static ArticleService getInstance() {
        return articleService;
    }

    private final ArticleDao articleDao = ArticleDao.getInstance();

    public ResultVO<Long> addData(String content) {
        ConnectionProxy connection = ConnectionUtil.getConnection();
        if (CommonUtil.isNull(connection)) {
            return new ResultVO<>(4002, "connection empty", null);
        }
        long snowFlakeId = 0;
        Integer insert = null;
        try {
            ConnectionUtil.CONNECTION_TL.set(connection);
            snowFlakeId = CommonUtil.SnowFlake.getSnowFlakeId(1L);
            DocumentDao document = new DocumentDao(snowFlakeId, content, 0, System.currentTimeMillis(), " ");
            insert = articleDao.insert("document", document);
        } catch (SQLException e) {
            Logger.error(e.toString());
        } finally {
            ConnectionUtil.CONNECTION_TL.remove();
            connection.close();
        }

        if (insert != 0) {
            return new ResultVO<>(4000, "OK", snowFlakeId);
        } else {
            return new ResultVO<>(4002, "sql insert error", null);
        }
    }

    public ResultVO queryData(Long id) {
        IdContextVO content = null;
        ConnectionProxy connection = ConnectionUtil.getConnection();
        if (CommonUtil.isNull(connection)) {
            return new ResultVO<>(4002, "connection empty", null);
        }
        try {
            ConnectionUtil.CONNECTION_TL.set(connection);
            QueryDao queryDao = new QueryDao(id, 0);
            ResultSet resultSet = articleDao.select("document", queryDao);
            while (resultSet.next()) {
                content = new IdContextVO(id, resultSet.getString("content"));
            }
        } catch (SQLException e) {
            Logger.error(e.toString());
        } finally {
            ConnectionUtil.CONNECTION_TL.remove();
            connection.close();
        }

        if (content != null) {
            return new ResultVO<>(4000, "OK", content);
        } else {
            return null;
        }
    }

    public ResultVO ScheduledDeletion() {
        ConnectionProxy connection = ConnectionUtil.getConnection();
        if (CommonUtil.isNull(connection)) {
            return new ResultVO<>(4002, "connection empty", null);
        }
        Integer count = null;
        try {
            ConnectionUtil.CONNECTION_TL.set(connection);
            count = articleDao.ScheduledDeleted("document");
        } catch (SQLException e) {
            Logger.error(e.toString());
        } finally {
            ConnectionUtil.CONNECTION_TL.remove();
            connection.close();
        }

        return new ResultVO<>(4000, "OK", count);
    }

    public ResultVO delete(Long articleId) {
        ConnectionProxy connection = ConnectionUtil.getConnection();
        if (CommonUtil.isNull(connection)) {
            return new ResultVO<>(4002, "connection empty", null);
        }
        try {
            ConnectionUtil.CONNECTION_TL.set(connection);
            articleDao.delete("document", 1, articleId);
        } catch (SQLException e) {
            Logger.error(e.toString());
        } finally {
            ConnectionUtil.CONNECTION_TL.remove();
            connection.close();
        }

        return new ResultVO<>(4000, "OK", null);
    }
}
