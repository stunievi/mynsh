package com.beeasy.hzbpm;

import com.github.llyb120.nami.core.Nami;

import java.util.concurrent.CountDownLatch;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.beeasy.hzbpm.service.MongoService.db;

public class App {

    public static void main(String[] args) throws InterruptedException {
        String config = null;
        if(args.length > 0){
            for (int i = 0; i < args.length; i++) {
                if(args[i].equals("-c")){
                    config = args[++i];
                }
            }
        }

        if (config == null) {
            config = "./nami.conf";
        }
        Nami.start(config, true);
        sqlManager.getBeetl();
        db.getName();

        CountDownLatch cd = new CountDownLatch(1);
        cd.await();
    }
}

