package com.javasearch.www.service;


import com.javasearch.www.Main;

import com.javasearch.www.pojo.*;

import com.javasearch.www.pojo.Search.IdContextVO;
import com.javasearch.www.pojo.Search.IdContextWordCountVO;
import com.javasearch.www.pojo.Search.SearchVO;
import com.javasearch.www.util.*;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class IndexService {

    private static final Integer DISPLAY_NUMBER = 10;

    private IndexService() {
    }

    private static final IndexService indexService = new IndexService();

    private final ArticleService articleService = ArticleService.getInstance();

    public static IndexService getInstance() {
        return indexService;
    }

    private final ReentrantLock Lock = new ReentrantLock();

    public void addForwardIndex(String article, Long articleId) {
        String[] strings = SegmentUtil.searchParticiple(article);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(strings));
        long snowFlakeId = CommonUtil.SnowFlake.getSnowFlakeId(2L);
        ForwardIndex forwardIndex = new ForwardIndex(articleId, 0, list);
        String nodeContext = JsonUtils.toString(forwardIndex);
        MapDBUtil.IndexInsertDb(nodeContext, snowFlakeId);
        indexService.ForwardIndexReally(articleId, list);
    }

    public void addInvertedIndex(String article, Long articleId) {
        Map<String, Integer> participle = SegmentUtil.articleParticiple(article);
        for (Map.Entry<String, Integer> entry : participle.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Document document = new Document(articleId, value, 0);
            long snowFlakeId = CommonUtil.SnowFlake.getSnowFlakeId(3L);
            InvertedIndex invertedIndex = new InvertedIndex(key, document);
            String nodeContext = JsonUtils.toString(invertedIndex);
            MapDBUtil.IndexInsertDb(nodeContext, snowFlakeId);
            indexService.InvertedIndexReally(key, document);
        }
    }

    public ResultVO searchToTree(String search) {
        ArrayList<IdContextWordCountVO> list = new ArrayList<>();
        ResultVO resultVO;
        HashMap<Long, SearchVO> map = new HashMap<>();
        String[] participle = SegmentUtil.searchParticiple(search);
        if (participle == null) {
            return null;
        }
        for (String word : participle) {
            ArrayList<Document> documents = Main.ISBT.get(word);
            if (documents != null) {
                for (Document document : documents) {
                    Long articleId = document.getDocId();
                    if (map.get(articleId) == null) {
                        map.put(articleId, new SearchVO());
                    }
                    SearchVO searchVO = map.get(articleId);
                    if (searchVO.getCount() == null) {
                        searchVO.setCount(1);
                    } else {
                        searchVO.setCount(searchVO.getCount() + 1);
                    }
                    if (searchVO.getWordCount() == null) {
                        searchVO.setWordCount(document.getWordCount());
                    } else {
                        searchVO.setWordCount(searchVO.getWordCount() + document.getWordCount());
                    }
                }
            }
        }

        if (map.size() == 0) {
            return new ResultVO(4000, "OK", "not data");
        }

        for (Map.Entry<Long, SearchVO> entry : map.entrySet()) {
            if (entry.getValue().getCount() == participle.length) {
                resultVO = articleService.queryData(entry.getKey());
                if (resultVO != null && resultVO.getCode() == 4000) {
                    IdContextVO data = (IdContextVO) resultVO.getData();
                    if (CommonUtil.kmp(data.getContent(), search) != -1) {
                        IdContextWordCountVO idContextWordCountVO = new IdContextWordCountVO(data.getId(), data.getContent(), entry.getValue().getWordCount());
                        list.add(idContextWordCountVO);
                    }
                }
            }
        }

        if (list == null) {
            return new ResultVO(4000, "OK", "not data");
        }

        Collections.sort(list, (o1, o2) -> {
            if (o1.getWordCount() > o2.getWordCount()) {
                return -1;
            } else if (o1.getWordCount() < o2.getWordCount()) {
                return 1;
            } else {
                return 0;
            }
        });

        return new ResultVO(4000, "OK", list);
    }


    public void InvertedIndexReally(String word, Document document) {
        try {
            Lock.lock();
            ArrayList<Document> documents = Main.ISBT.get(word);
            if (documents == null) {
                ArrayList<Document> ds = new ArrayList<>();
                ds.add(document);
                Main.ISBT.insert(word, ds);
            } else {
                documents.add(document);
            }
        } finally {
            Lock.unlock();
        }
    }

    public void ForwardIndexReally(Long word, ArrayList<String> idList) {
        try {
            Lock.lock();
            Main.FSBT.insert(word, idList);
        } finally {
            Lock.unlock();
        }
    }

    public void delete(Long articleId) {
        ArrayList<String> forwardList = Main.FSBT.get(articleId);
        for (String word : forwardList) {
            ArrayList<Document> InvertedList = Main.ISBT.get(word);
            for (Document document : InvertedList) {
                if (document.getDocId().equals(articleId)) {
                    //写一个删除日志在这里

                    //这些放入定时任务中的队列里面，定时IO操作一起删除
                    InvertedIndex invertedIndex = new InvertedIndex(word, document);
                    String invertedNodeContext = JsonUtils.toString(invertedIndex);

                    ForwardIndex forwardIndex = new ForwardIndex(articleId, 0, forwardList);
                    String forwardNodeContext = JsonUtils.toString(forwardIndex);

                    Main.deleteTaskDeque.add(new Task(invertedNodeContext, forwardNodeContext));

                    //在内存的索引树中删除
                    InvertedList.remove(document);
                    break;
                }
            }
        }
    }


    //原本用来定时删除的
//    public void ScheduledDeletion() {
//        Map<String, Long> indexDB = MapDBUtil.getIndexDB();
//        if (indexDB != null) {
//            for (Map.Entry<String, Long> entry : indexDB.entrySet()) {
//                Long key = entry.getValue();
//                if ((key >> 12 & 31) == 3L) {
//                    String value = entry.getKey();
//                    InvertedIndex invertedIndex = JsonUtils.toObj(value, InvertedIndex.class);
//                    if (invertedIndex != null) {
//                        Document document = invertedIndex.getDocument();
//                        if (document.getIsDelete().equals(1)) {
//                            MapDBUtil.remove(value);
//                        }
//                    }
//                } else {
//                    String value = entry.getKey();
//                    ForwardIndex forwardIndex = JsonUtils.toObj(value, ForwardIndex.class);
//                    if (forwardIndex != null) {
//                        if (forwardIndex.getIsDelete().equals(1))
//                            MapDBUtil.remove(value);
//                    }
//                }
//            }
//        }
//        MapDBUtil.closeDB();
//    }
}
