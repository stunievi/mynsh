package com.beeasy.hzback.test;

import com.beeasy.hzback.lib.fuck.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAsync {

    @Autowired
    AsyncTask asyncTask;

    @Test
    public void t1() throws InterruptedException, TimeoutException, ExecutionException {
        String a = asyncTask.doTask1();
        Future<String> b = asyncTask.doTask2();
        String d = b.get(6, TimeUnit.SECONDS);
        int c = 1;
    }
}
