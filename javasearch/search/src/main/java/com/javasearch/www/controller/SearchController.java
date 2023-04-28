package com.javasearch.www.controller;


import com.javasearch.www.Main;
import com.javasearch.www.pojo.ResultVO;
import com.javasearch.www.service.ArticleService;
import com.javasearch.www.service.IndexService;
import com.javasearch.www.util.JsonUtils;
import com.javasearch.www.util.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SearchController extends BaseServlet {

    private final IndexService indexService = IndexService.getInstance();
    private final ArticleService articleService = ArticleService.getInstance();

    public ResultVO add(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setHeader(res);
        String article = req.getParameter("article");
        if (article == null) {
            PrintWriter writer = res.getWriter();
            writer.println(JsonUtils.toString(new ResultVO<>(4002, "param error", null)));
            writer.close();
        }

//        CompletableFuture<ResultVO<Long>> resultVOCompletableFuture = CompletableFuture.supplyAsync(
//                () -> {
//                    ResultVO<Long> resultVO = articleService.addData(article);
//                    if (resultVO == null) {
//                        return new ResultVO<>(4002, "sql insert error", null);
//                    }
//                    return resultVO;
//                }
//                , Main.threadPool);
//
//        resultVOCompletableFuture.thenRun(() -> {
//            try {
//                indexService.addForwardIndex(article, resultVOCompletableFuture.get().getData());
//            } catch (InterruptedException | ExecutionException e) {
//                Logger.error("addForwardIndex error");
//            }
//        });
//
//        resultVOCompletableFuture.thenRun(() -> {
//
//            try {
//                indexService.addInvertedIndex(article, resultVOCompletableFuture.get().getData());
//            } catch (InterruptedException | ExecutionException e) {
//                Logger.error("addInvertedIndex error");
//            }
//        });

        ResultVO<Long> resultVO = articleService.addData(article);
        if (resultVO == null) {
            return new ResultVO<>(4002, "sql insert error", null);
        }

        indexService.addForwardIndex(article, resultVO.getData());
        indexService.addInvertedIndex(article, resultVO.getData());


        return new ResultVO<>(4000, "OK", null);
    }

    public ResultVO search(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setHeader(res);
        String search = req.getParameter("search");
        if (search == null) {
            PrintWriter writer = res.getWriter();
            writer.println(JsonUtils.toString(new ResultVO<>(4002, "param error", null)));
            writer.close();
        }
        return indexService.searchToTree(search);
    }

    public void delete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        setHeader(res);
        String articleId = req.getParameter("articleId");
        if (articleId == null) {
            PrintWriter writer = res.getWriter();
            writer.println(JsonUtils.toString(new ResultVO<>(4002, "param error", null)));
            writer.close();
        }

        CompletableFuture.runAsync(() -> articleService.delete(Long.valueOf(articleId)), Main.threadPool);

        CompletableFuture.runAsync(() -> indexService.delete(Long.valueOf(articleId)), Main.threadPool);

    }

    /**
     * 根据删除时间来回复记录，我们在字段表里添加了一个删除时间列头
     * 文章数据库先进行查询后，将伪删除字段设为“0”，之后获得Id以及文本内容，再重新插入倒排索引和正排索引
     * 再去调用索引恢复
     */
    public void recover() {

    }


    private void setHeader(HttpServletResponse res) {
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.setCharacterEncoding("utf-8");
    }
}
