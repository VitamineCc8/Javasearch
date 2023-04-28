package com.javasearch.www;


import com.javasearch.www.controller.SearchController;
import com.javasearch.www.pojo.*;
import com.javasearch.www.service.ArticleService;
import com.javasearch.www.service.IndexService;

import com.javasearch.www.util.JsonUtils;
import com.javasearch.www.util.Logger;
import com.javasearch.www.util.MapDBUtil;
import com.javasearch.www.util.SearchBstTree;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;


public class Main {

    public static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    public static ArrayDeque<Task> deleteTaskDeque = new ArrayDeque<>();
    public static final ExecutorService threadPool = new ThreadPoolExecutor(
            4,
            8,
            2L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(3),
            Executors.defaultThreadFactory(),
            //new ThreadPoolExecutor.AbortPolicy()
            //new ThreadPoolExecutor.CallerRunsPolicy()
            //new ThreadPoolExecutor.DiscardOldestPolicy()
            new ThreadPoolExecutor.DiscardPolicy()
    );
    public static SearchBstTree<Long, ArrayList<String>> FSBT;
    public static SearchBstTree<String, ArrayList<Document>> ISBT;


    public static void main(String[] args) throws LifecycleException, IOException {
        init();
    }

    private static void init() throws IOException, LifecycleException {
        ArticleService articleService = ArticleService.getInstance();
        IndexService indexService = IndexService.getInstance();
        ISBT = new SearchBstTree<>((a, b) -> {
            if (a.compareTo(b) > 0) {
                return 1;
            } else if (a.compareTo(b) < 0) {
                return -1;
            } else {
                return 0;
            }
        });

        FSBT = new SearchBstTree<>((a, b) -> {
            if (a > b) {
                return 1;
            } else if (a < b) {
                return -1;
            } else {
                return 0;
            }
        });

        //查看日志是否需要回滚恢复数据
//        log.rollback();

        //加载倒排索引缓存
        Runnable InvertedRunnable = () -> {
            String path = "E:/A-Programming file/javasearch/search/src/main/resources/Inverted/mapdb.db" ;
            Map<String, Long> indexDB = MapDBUtil.getIndexDB(path);
            System.out.println("1111");
            if (indexDB != null) {
                for (Map.Entry<String, Long> entry : indexDB.entrySet()) {
                    Long key = entry.getValue();
                    if ((key >> 12 & 31) == 3L) {
                        String value = entry.getKey();
                        InvertedIndex invertedIndex = JsonUtils.toObj(value, InvertedIndex.class);
                        if (invertedIndex != null) {
                            Document document = invertedIndex.getDocument();
                            if (document.getIsDelete().equals(0)) {
                                String word = invertedIndex.getWord();
                                indexService.InvertedIndexReally(word, document);
                            }
                        }
                    }
                }
            } else {
                Logger.error("InvertedIndex  error");
            }
            MapDBUtil.closeDB();
        };

        threadPool.submit(InvertedRunnable);


        //加载正排索引缓存
        Runnable ForwardRunnable = () -> {
            String path = "E:/A-Programming file/javasearch/search/src/main/resources/Forward/mapdb.db" ;
            Map<String, Long> indexDB = MapDBUtil.getIndexDB(path);
            System.out.println("22222");
            if (indexDB != null) {
                for (Map.Entry<String, Long> entry : indexDB.entrySet()) {
                    Long key = entry.getValue();
                    if ((key >> 12 & 31) == 2L) {
                        String value = entry.getKey();
                        ForwardIndex forwardIndex = JsonUtils.toObj(value, ForwardIndex.class);
                        if (forwardIndex != null && forwardIndex.getIsDelete().equals(0)) {
                            Long word = forwardIndex.getWord();
                            ArrayList<String> idList = forwardIndex.getIdList();
                            indexService.ForwardIndexReally(word, idList);
                        }
                    }
                }
            } else {
                Logger.error("InvertedIndex  error");
            }
            MapDBUtil.closeDB();
        };

        threadPool.submit(ForwardRunnable);


        //磁盘的定期删除
        scheduledExecutorService.scheduleWithFixedDelay(new TimerTask() {
            @Override
            public void run() {
                for (Task task : deleteTaskDeque) {
                    String forwardNodeContext = task.getForwardNodeContext();
                    String forwardString = JsonUtils.toString(forwardNodeContext);
                    MapDBUtil.remove(forwardString);
                    String invertedNodeContext = task.getInvertedNodeContext();
                    String invertedString = JsonUtils.toString(invertedNodeContext);
                    MapDBUtil.remove(invertedString);
                }
                articleService.ScheduledDeletion();
            }
        }, 2, 3, TimeUnit.HOURS);


        String contextPath = "";
        Path tempBaseDir = Files.createTempDirectory("tomcat-temp-base-dir");//创建临时目录作为tomcat的基础目录
        Path tempDocDir = Files.createTempDirectory("tomcat-temp-doc-dir");//创建临时目录作为应用文档资源的目录
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(9000);//设置绑定端口
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);
        tomcat.getHost().setAutoDeploy(false);
        tomcat.setBaseDir(tempBaseDir.toFile().getAbsolutePath());

        StandardContext content = (StandardContext) tomcat.addWebapp(contextPath, tempDocDir.toFile().getAbsolutePath());//创建应用上下文
        content.setParentClassLoader(Main.class.getClassLoader());
        content.setUseRelativeRedirects(false);
        Wrapper servlet = tomcat.addServlet(contextPath, "test", new SearchController());
        servlet.addMapping("/search/*");
        //tomcat 启动jar扫描设置为跳过所有，避免与框架结合出现 jar file not found exception
        System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "\\,*");
        tomcat.start();
        tomcat.getServer().await();
    }
}
