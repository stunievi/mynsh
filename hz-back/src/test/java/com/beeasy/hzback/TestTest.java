package com.beeasy.hzback;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestTest {

    @Test
    public void test() throws IOException {


    }

    public static void main(String[] args) throws IOException {
        String path = args[0];
        String destfile = args[1];
        if (null == destfile) {
            destfile = "sql.txt";
        }
        File dir = new File(path);
//        File dir = new File("/Users/bin/work/ods_table_for_db2");
        StringBuilder sb = new StringBuilder();
        List<String> strs = Arrays.stream(dir.listFiles())
                .filter(File::isDirectory)
                .sorted(new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return Integer.valueOf(o1.getName().substring(0, 2)) - Integer.valueOf(o2.getName().substring(0, 2));
                    }
                })
                .flatMap(file -> Arrays.stream(file.listFiles()))
                .flatMap(file -> {
                    try {
                        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "GBK");
                        return IOUtils.readLines(reader).stream();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new ArrayList<String>().stream();
                })
                .collect(Collectors.toList());
//        for (File childDir : dir.listFiles()) {
//            if(!childDir.isDirectory()){
//                continue;
//            }
//            for (File file : childDir.listFiles()) {
//
//                for (String line : (IOUtils.readLines(new FileReader(file)))) {
//                    sb.append(line + "\n");
//                }
//            }
//        }

        File dest = new File(destfile);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(dest), "GBK");
        IOUtils.write(String.join("\n", strs), writer);

        System.out.println("generate success");
    }
//    public static void main(){
//
//    }
}
