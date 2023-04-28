package com.javasearch.www.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionUtil {

    private static final int MAX = 1000;
    private static final int THRESHOLD = 500;
    private static final int MIN = 10;
    private static int HAS_CONNECTIONS = MIN;
    private static final int ERROR_MAX = 5;
    public static final ThreadLocal<ConnectionProxy> CONNECTION_TL = new ThreadLocal<>();
    private static final List<java.sql.Connection> CONNECTIONS = new ArrayList<>(MAX);
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition COND = LOCK.newCondition();

    private static String URL;

    static {
        try {
            Properties properties = new Properties();
            InputStream in = ConnectionUtil.class.getClassLoader().getResourceAsStream("database.properties");
            properties.load(in);
            URL = properties.getProperty("url");
            for (int i = 0; i < MIN; i++) {
                Connection connection = DriverManager.getConnection(URL);
                CONNECTIONS.add(connection);
            }
        } catch (SQLException | IOException e) {
            Logger.error(e.toString());
        }
    }

    public static void infoConnectionsCount() {
        Logger.Info("当前已打开连接数: " + HAS_CONNECTIONS);
    }

    public static ConnectionProxy getConnection() {
        try {
            LOCK.lock();
            int errorCount = 0;
            while (CONNECTIONS.size() == 0) {
                if (HAS_CONNECTIONS < MAX) {
                    HAS_CONNECTIONS++;
                    Connection connection = DriverManager.getConnection(URL);
                    return new ConnectionProxy(connection);
                }
                if (errorCount == ERROR_MAX) {
                    return null;
                }
                errorCount++;
                COND.await(10, TimeUnit.MILLISECONDS);
            }
            Connection connection = CONNECTIONS.remove(CONNECTIONS.size() - 1);
            return new ConnectionProxy(connection);
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
        return null;
    }

    public static void flyBackConnection(Connection connection) {
        try {
            LOCK.lock();
            if (CONNECTIONS.size() >= THRESHOLD) {
                connection.close();
            } else {
                CONNECTIONS.add(connection);
            }
        } catch (SQLException throwables) {
            Logger.error("数据连接归还更新错误");
        } finally {
            LOCK.unlock();
        }
    }

    public static String getInsertSentence(String tableName, Object obj) {
        if (CommonUtil.isNull(obj)) {
            return null;
        }
        StringBuilder sentence = new StringBuilder();
        sentence.append("INSERT INTO ").append(tableName).append(" (");
        Class<?> aClass = obj.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            field.setAccessible(true);
            String name = field.getName();
            sentence.append(name);
            if (i < declaredFields.length - 1) {
                sentence.append(",");
            } else {
                sentence.append(") ");
            }
        }
        //"INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
        //                   "VALUES (1, 'Paul', 32, 'California', 20000.00 );"
        sentence.append("values (");
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            try {
                String simpleName = field.getType().getSimpleName();
                Object o = field.get(obj);
                switch (simpleName) {
                    case "String" -> {
                        String value = (String) o;
                        sentence.append("\'").append(value).append("\'");
                    }
                    case "Integer" -> {
                        Integer value2 = (Integer) o;
                        sentence.append(value2);
                    }
                    case "Long" -> {
                        Long value3 = (Long) o;
                        sentence.append(value3);
                    }
                }
                if (i < declaredFields.length - 1) {
                    sentence.append(",");
                } else {
                    sentence.append(")");
                }
            } catch (IllegalAccessException e) {
                Logger.error("string error");
            }
        }
        sentence.append(";");
        return sentence.toString();
    }


    public static String getSelectSentence(String tableName, Object obj) {
        if (CommonUtil.isNull(obj)) {
            return null;
        }
        //  select ... from tableName where ...
        StringBuilder str = new StringBuilder("select * from ");
        str.append(tableName);
        Class<?> aClass = obj.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        int hasWhere = 0;
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                Object o = declaredField.get(obj);
                if (!CommonUtil.isNull(o)) {
                    hasWhere++;
                }
            } catch (IllegalAccessException e) {
                Logger.error(e.toString());
            }
        }

        if (hasWhere > 0) {
            str.append(" where ");
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                try {
                    Object o = declaredField.get(obj);
                    if (o == null) {
                        continue;
                    }
                    String name = declaredField.getName();
                    String simpleName = declaredField.getType().getSimpleName();
                    str.append(name).append("=");
                    switch (simpleName) {
                        case "String" -> {
                            String value = (String) o;
                            str.append("'").append(value).append("'");
                        }
                        case "Integer" -> {
                            Integer value2 = (Integer) o;
                            str.append(value2);
                        }
                        case "Long" -> {
                            Long value3 = (Long) o;
                            str.append(value3);
                        }
                    }
                    hasWhere--;
                    if (hasWhere > 0) {
                        str.append(" and ");
                    }
                } catch (IllegalAccessException e) {
                    Logger.error(e.toString());
                }
            }
            str.append(";");
        }
        return str.toString();
    }


    public static String getUpdateSentence(String tableName, Integer id, Long articleId) {
        //  //UPDATE document SET isDelete = '1' WHERE id = '1'......
        return "UPDATE " + tableName + " SET isDeleted = " + id + "  , deletedTime = " + System.currentTimeMillis() + " WHERE id = " + articleId + ";";
    }

    public static String getDeletedSQL(String document) {
        return "DELETE FROM " + document + " WHERE isDeleted = 1 AND (deletedTime + 15*24*24*1000) > " + System.currentTimeMillis() + ";";
    }
}
