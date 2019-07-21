package com.beeasy.hzbpm.ctrl;

import com.github.llyb120.nami.core.Async;

import java.io.IOException;

public class webhook {

    public void hook() throws IOException {
        Async.execute(() -> {
            Runtime.getRuntime().exec("bash /hz/build.sh");
        });
    }
}
