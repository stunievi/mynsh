//package com.beeasy.test;
//
//import org.junit.Test;
//import org.osgl.util.IO;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.*;
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.channels.FileChannel;
//import java.nio.charset.Charset;
//import java.nio.charset.CharsetDecoder;
//import java.nio.charset.CharsetEncoder;
//import java.util.Arrays;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//public class MergeFile {
//    public static final int BUFSIZE = 1024 * 8;
//
//    @Test
//    public static void main(String[] args){
//        String outfile="D:/java projects/hznsh/load-qcc/src/main/resources/qcc/txt/2019-04-15/merged.txt";
//        String[] files=new String[] {
//                "D:/java projects/hznsh/load-qcc/src/main/resources/qcc/txt/2019-04-15/fzsys_20190420211039427.txt",
//                "D:/java projects/hznsh/load-qcc/src/main/resources/qcc/txt/2019-04-15/fzsys_20190420211150097.txt"
//        };
//        MergeFile.mergeFiles(outfile,files);
//
//
//
//        File __file = new File(outfile);
//        File __zip = new File("D:/java projects/hznsh/load-qcc/src/main/resources/qcc/txt/2019-04-15/merged.zip");
//        try(
//                FileInputStream fis = new FileInputStream(__file);
//                FileOutputStream fos = new FileOutputStream(__zip);
//                ZipOutputStream zip = new ZipOutputStream(fos);
//        ) {
//            ZipEntry entry = new ZipEntry("zip");
//            entry.setSize(__file.length());
//            zip.putNextEntry(entry);
//            byte[] bs = new byte[1024];
//            int len = -1;
//            while((len = fis.read(bs)) > 0){
//                zip.write(bs, 0, len);
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    public static void mergeFiles(String outFile, String[] files) {
//        FileOutputStream fos = null;
//        try{
//            fos = new FileOutputStream(outFile);
//            for (String file : files) {
//                FileInputStream fis = null;
//                try{
//                    fis =                 new FileInputStream(file);
//                    IO.copy(fis,fos,false);
//                }
//                finally {
//                    if (fis != null) {
//                        try{
//                            fis.close();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            fos.flush();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
////
////        FileChannel outChannel = null;
////
////        System.out.println("Merge " + Arrays.toString(files) + " into " + outFile);
////
////        try {
////
////            outChannel = new FileOutputStream(outFile).getChannel();
////
////            for(String f : files){
////
////                Charset charset= Charset.forName("utf-8");
////
////                CharsetDecoder chdecoder=charset.newDecoder();
////
////                CharsetEncoder chencoder=charset.newEncoder();
////
////                FileChannel fc = new FileInputStream(f).getChannel();
////
////                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
////
////                CharBuffer charBuffer=chdecoder.decode(bb);
////
////                ByteBuffer nbuBuffer=chencoder.encode(charBuffer);
////
////                while(fc.read(nbuBuffer) != -1){
////                    bb.flip();
////                    nbuBuffer.flip();
////                    outChannel.write(nbuBuffer);
////                    bb.clear();
////                    nbuBuffer.clear();
////                }
////                fc.close();
////            }
////
////            System.out.println("Merged!! ");
////
////        } catch (IOException ioe) {
////
////            ioe.printStackTrace();
////
////        } finally {
////
////            try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}
////
////        }
//
//    }
//}
