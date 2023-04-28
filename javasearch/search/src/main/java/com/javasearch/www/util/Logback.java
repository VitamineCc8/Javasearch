//package com.javasearch.www.util;
//
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
///**
// * @author VitamineG
// * @DateTime 2023/3/24 18:48
// */
//public class Logback {
//    //记录日志
//    public class MyClass {
//        private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
//
//        public void doSomething() {
//            LOGGER.debug("This is a debug message");
//            LOGGER.info("This is an info message");
//            LOGGER.warn("This is a warning message");
//            LOGGER.error("This is an error message", new RuntimeException("Something went wrong"));
//        }
//    }
//
//    //读取日志
//    public void readLogFile(String path) throws IOException {
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                // process each line of the log file
//                System.out.println(line);
//            }
//        }
//    }
//
//
//
//}
