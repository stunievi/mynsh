package com.beeasy.hzbpm.bean;

import com.github.llyb120.nami.ext.file.SimpleStorage;
import com.github.llyb120.nami.ext.file.Storage;

public class FileStorage {

    public static SimpleStorage storage = (SimpleStorage) Storage.getStorage("file");
}
