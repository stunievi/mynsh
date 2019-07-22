package com.beeasy.hzbpm;

import com.github.llyb120.nami.core.Nami;

public class App {

    public static void main(String[] args) {
        String config = null;
        if(args.length > 0){
            for (int i = 0; i < args.length; i++) {
                if(args[i].equals("-c")){
                    config = args[++i];
                }
            }
        }

        if (config == null) {
            Nami.start();
        } else {
            Nami.start(config, false);
        }
    }
}

