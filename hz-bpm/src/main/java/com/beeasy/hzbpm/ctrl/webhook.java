package com.beeasy.hzbpm.ctrl;

import com.github.llyb120.nami.core.Async;

import java.io.IOException;

public class webhook {

    public void hook() throws IOException {
        Async.execute(() -> {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("ps -ef |grep build.sh |awk '{print $2}'|xargs kill -9").waitFor();
            runtime.exec("bash /hz/build.sh").waitFor();
        });
    }
}
