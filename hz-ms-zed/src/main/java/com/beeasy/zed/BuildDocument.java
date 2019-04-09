package com.beeasy.zed;

import java.io.IOException;

public class BuildDocument {
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("apidoc -i src/ -o src/main/resources/doc/");
    }
}
