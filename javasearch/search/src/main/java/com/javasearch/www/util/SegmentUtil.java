package com.javasearch.www.util;

import java.util.*;

//分词以及强匹配
public class SegmentUtil {

    //接收搜索结果，返回单个字符以及该词的频率（剔除空格以及标点符号）
    //第三版不需要分词，直接单个字符(保留所有符号，去除所有空格)
    public static Map<String, Integer> articleParticiple(String searchSentence) {
        Map<String, Integer> resultMap = new HashMap<>();
        if (searchSentence != null) {
            String search = searchSentence.replaceAll(" ", "");
            String[] splitString = search.split("");
            for (String s : splitString) {
                resultMap.put(s, resultMap.get(s) == null ? 1 : resultMap.get(s) + 1);
            }
        }
        return resultMap;
    }

    public static String[] searchParticiple(String searchSentence) {
        if (searchSentence != null){
            String search = searchSentence.replaceAll(" ", "");
            return search.split("");
        }else {
            return null;
        }
    }


    //第一版，是最通用的分词结果
//        public static HashMap<String, Integer> SearchParticiple01(String searchSentence) {
//        HashMap<String, Integer> resultMap = new HashMap<>();
//        Map<String, Integer> map = SegmentHelper.segment(searchSentence, SegmentResultHandlers.wordCount());
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            String key = entry.getKey();
//            if (!(key.matches("\\pP|\\pS") || key.matches("\\s"))) {
//                resultMap.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return resultMap;
//    }
//
    //第二版，是全字匹配的分词结果，相当于微信搜索的第一版本
//    public static HashMap<String, Integer> SearchParticiple02(String searchSentence) {
//        HashMap<String, Integer> resultMap = new HashMap<>();
//        Map<String, Integer> map = SegmentBs.newInstance()
//                .segmentMode(SegmentModes.index())
//                .segment(searchSentence, SegmentResultHandlers.wordCount());
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            String key = entry.getKey();
//            if (!(key.matches("\\pP|\\pS") || key.matches("\\s"))) {
//                resultMap.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return resultMap;
//    }

    //接收文章，直接返回分词（剔除空格以及标点符号）
//    public static List<String> ArticleParticiple(String article) {
//        List<String> resultList = new LinkedList<>();
//        List<String> list = SegmentBs.newInstance()
//                .segmentMode(SegmentModes.index())
//                .segment(article, SegmentResultHandlers.word());
//        Iterator<String> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            if (!(key.matches("\\pP|\\pS") || key.matches("\\s"))) {
//                resultList.add(key);
//            }
//        }
//        return resultList;
//    }

}





