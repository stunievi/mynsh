package com.beeasy.test;

import org.junit.Test;

import java.io.*;

public class TestReadAndWriteFile {

    @Test
    public void testZip(){

        File file = new File("D:\\java projects\\hznsh\\load-qcc\\src\\main\\resources\\zip\\qcc\\1.text");
        OutputStream outputStream = null;
        if (!file.exists()) {
            try {
                // 如果文件找不到，就new一个
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // 定义输出流，写入文件的流
            outputStream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 定义将要写入文件的数据
        String string = "Hell Java, Hello World, 你好，世界！\n";
        // 把string转换成byte型的，并存放在数组中
        byte[] bs = string.getBytes();
        try {
            // 写入bs中的数据到file中
            outputStream.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // =================到此，文件的写入已经完成了！

        // 如果想在文件后面追加内容的话，用下面的方法
        OutputStream outToFileEnd = null;
        try {
            outToFileEnd = new FileOutputStream(file,true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            // 这里利用了finally总会被执行的特性，索性把后面的代码都写在finally中
            String string2 = "Here I come!!";
            byte[] bs2 = string2.getBytes();
            try {
                outToFileEnd.write(bs2);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    outToFileEnd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
