package com.beeasy.tool;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.osgl.util.C;
import org.osgl.util.S;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileUtil {
    //允许通过的文件
    private final static List<String> FileTypes = C.newList(
        "txt","pdf","doc","docx","xls","xlsx","ppt","pptx"
    );

    /**
     * 读取文件
     * @param ext 文件拓展名
     * @param is 文件输入流
     * @return
     */
    public static String readFile(String ext, InputStream is){
        if(!FileTypes.contains(ext)){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("错误的文件类型");
        }
        String cnt = "";
        try {
            switch (ext){
                case "doc":
                case "docx":
                    cnt = readDoc(is);
                    break;
                case "xls":
                    cnt = readXls(is);
                    break;

                case "xlsx":
                    cnt = readXlsx(is);
                    break;

                case "ppt":
                case "pptx":
                    cnt = readPpt(is);
                    break;

                case "txt":
                    cnt = readTxt(is);
                    break;

                case "pdf":
                    cnt = readPdf((FileInputStream)is);
                    break;
            }
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cnt;
    }

    /**
     * 读取文件
     * @param file 文件对象
     * @return 文件内容
     */
    public static String readFile(File file){
        Assert(file.exists() && file.isFile(), RuntimeException.class, "文件不存在");
        String ext = S.fileExtension(file.getName()).toLowerCase();
        String cnt = "";
        try(
            InputStream is = new FileInputStream(file);
        ){
            cnt = readFile(ext,is);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            if(e instanceof RuntimeException){
                throw new RuntimeException(((RuntimeException) e).getMessage());
            }
        }
        return cnt;
    }

    /**
     * 读取文件
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFile(String filePath){
        File file = new File(filePath);
        return readFile(file);
    }


    /**
     * 读取txt格式的文件, 只支持UTF-8
     * @param is
     * @return
     */
    public static String readTxt(InputStream is){
        StringBuilder result = new StringBuilder();
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));// 构造一个BufferedReader类来读取文件
        ){
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result.append("\n").append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 读取doc和docx文件
     * @param is
     * @return
     * @throws IOException
     */
    public static String readDoc (InputStream is) throws IOException {
        String text= "";
        is = FileMagic.prepareToCheckMagic(is);
        if (FileMagic.valueOf(is) == FileMagic.OLE2) {
            HWPFDocument doc = new HWPFDocument(is);
            Range range = doc.getRange();
            text = range.text();
        } else if(FileMagic.valueOf(is) == FileMagic.OOXML) {
            XWPFDocument doc = new XWPFDocument(is);
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            text = extractor.getText();
            extractor.close();
        }
        return text;
    }

    /**
     * 读取xls文件
     * @param is
     * @return
     * @throws IOException
     * @throws BiffException
     */
    public static String readXls(InputStream is) throws IOException, BiffException {
        String result = "";
        StringBuilder sb = new StringBuilder();
        Workbook rwb = Workbook.getWorkbook(is);
        Sheet[] sheet = rwb.getSheets();
        for (int i = 0; i < sheet.length; i++) {
            Sheet rs = rwb.getSheet(i);
            for (int j = 0; j < rs.getRows(); j++) {
                Cell[] cells = rs.getRow(j);
                for (int k = 0; k < cells.length; k++)
                    sb.append(cells[k].getContents());
            }
        }
        result += sb.toString();
        return result;
    }

    /**
     * 读取xlsx文件
     * @param is
     * @return
     * @throws IOException
     */
    public static String readXlsx(InputStream is) throws IOException {
        String result = "";
        StringBuilder sb = new StringBuilder();
        XSSFWorkbook xwb = new XSSFWorkbook(is);
        XSSFSheet sheet = xwb.getSheetAt(0);
        for (int i=sheet.getFirstRowNum()+1;i<sheet.getPhysicalNumberOfRows();i++) {
            XSSFRow row= sheet.getRow(i);
            for (int j =row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {
                sb.append(row.getCell(j).toString());
            }
        }
        result += sb.toString();
        return result;
    }


    /**
     * 读取ppt 暂时只支持fileinputstream
     * @param is
     * @return
     */
    public static String readPpt(InputStream is) throws IOException {
        if(is instanceof FileInputStream){
            PowerPointExtractor ppExtractor=new PowerPointExtractor((FileInputStream) is);
            return ppExtractor.getText();
        }
        return "";
    }

    /**
     * 读取pdf
     * @param is
     * @return
     * @throws IOException
     */
    public static String readPdf(FileInputStream is) throws IOException {
        PDDocument document=PDDocument.load(is);
        PDFTextStripper stripper=new PDFTextStripper();
        stripper.setSortByPosition(false);
        String result=stripper.getText(document);
        document.close();
        return result;
    }

    /**********/

    private static <T> void Assert(boolean b, Class<T> clz, String msg){
        if(!b){
            throw new RuntimeException(msg);
        }
    }


    /************/
}
