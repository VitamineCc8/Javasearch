package com.javasearch.www.util;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

public class MapDBUtil {

    private static DB db;

    private static ConcurrentMap<String, Long> map;

    private static String PATH;

    static {
        try {
            Properties properties = new Properties();
            InputStream in = ConnectionUtil.class.getClassLoader().getResourceAsStream("database.properties");
            properties.load(in);
            PATH = properties.getProperty("mapdburl");
        } catch (IOException e) {
            Logger.error(e.toString());
        }
    }

    public static void IndexInsertDb(String key, Long value) {
        db = DBMaker.fileDB(PATH)
                .fileMmapEnable()
                .fileMmapPreclearDisable()
                .cleanerHackEnable()
                .closeOnJvmShutdown()
                .transactionEnable()
                .make();

        map = db.hashMap("words")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.LONG)
                .createOrOpen();
        map.put(key, value);
        db.commit();
        db.close();
    }

    public static Map<String, Long> getIndexDB(String path) {
        DB db = DBMaker.fileDB(path)
                .fileMmapEnable()
                .fileMmapPreclearDisable()
                .cleanerHackEnable()
                .closeOnJvmShutdown()
                .transactionEnable()
                .make();

        ConcurrentMap<String, Long> map = db.hashMap("words")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.LONG)
                .createOrOpen();
        return map;
    }

    public static void closeDB() {
        db.close();
    }

    public static Long getValue(String key) {
        db = DBMaker.fileDB(PATH)
                .fileMmapEnable()
                .fileMmapPreclearDisable()
                .cleanerHackEnable()
                .closeOnJvmShutdown()
                .transactionEnable()
                .make();

        map = db.hashMap("words")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.LONG)
                .createOrOpen();
        return map.get(key);
    }


    public static void remove(String key) {
        db = DBMaker.fileDB(PATH)
                .fileMmapEnable()
                .fileMmapPreclearDisable()
                .cleanerHackEnable()
                .closeOnJvmShutdown()
                .transactionEnable()
                .make();

        map = db.hashMap("words")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.LONG)
                .createOrOpen();
        map.remove(key);
    }
}
