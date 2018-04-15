//package com.beeasy.hzback.lib.fuck;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.AsyncResult;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.Future;
//
//@Component
//@Slf4j
//public class AsyncTask {
////    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Async
//    public String doTask1() throws InterruptedException{
////        logger.info("Task1 started.");
//        long start = System.currentTimeMillis();
//        Thread.sleep(5000);
//        long end = System.currentTimeMillis();
//
////        logger.info("Task1 finished, interval elapsed: {} ms.", end-start);
//
//        return ("Task1 accomplished!");
//    }
//
//    @Async
//    public Future<String> doTask2() throws InterruptedException{
////        logger.info("Task2 started.");
//        long start = System.currentTimeMillis();
//        Thread.sleep(3000);
//        long end = System.currentTimeMillis();
//
////        logger.info("Task2 finished, interval elapsed: {} ms.", end-start);
//
//        return new AsyncResult<>("Task2 accomplished!");
//    }
//}