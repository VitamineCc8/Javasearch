package com.javasearch.www.util;

import java.text.SimpleDateFormat;
import java.util.Date;

//日志系统
public class Logger {
    public static void Info(String msg) {
        long id = Thread.currentThread().getId();
        SimpleDateFormat sdf = new SimpleDateFormat();
        Date date = new Date();
        String format = sdf.format(date);
        System.out.println("[INFO] " + "thread-" + id + " " + format + " " + msg);
    }

    public static void error(String msg) {
        long id = Thread.currentThread().getId();
        SimpleDateFormat sdf = new SimpleDateFormat();
        Date date = new Date();
        String format = sdf.format(date);
        System.out.println("[ERROR] " + "thread-" + id + " " + format + " " + msg);
    }
}
