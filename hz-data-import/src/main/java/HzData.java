import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HzData {
    private Map<String, Table> tables = new ConcurrentHashMap<>();

    public static class Table{
        public String name;
        public List<String> fields = new ArrayList<String>();
        public List<String> types = new ArrayList<String>();
        public List<Integer> lens = new ArrayList<Integer>();
    }

    public void initJSON() throws FileNotFoundException {
        String str = IoUtil.read(new FileInputStream("define.json"), StandardCharsets.UTF_8);
        Map<String, Table> map = JSON.parseObject(str, new TypeReference<Map<String, Table>>(){});
        tables.putAll(map);
    }

    public void init(){
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file("C:\\Users\\bin\\Desktop\\ods_run\\ods_run\\tableDefinition.xlsx"));
        List<String> sheetNames = reader.getSheetNames();
        for(int n = 0; n < reader.getSheetCount(); n++){
            Table table = new Table();
            table.name = sheetNames.get(n);
            int startRow = -1;
            reader.setSheet(n);
//            System.out.println(reader.getRowCount() + "," + reader.getColumnCount());
            for(int i = 0; i < reader.getRowCount(); i++) {
                Object value = reader.readCellValue(2, i);
//                    System.out.println(value);
                    if (value instanceof String && ((String) value).contains("字段中文名")) {
//                        System.out.println(value);
                        startRow = 1;
                        continue;
                    }
                    if(startRow > -1){
                        String field;
                        String type;
                        value = reader.readCellValue(1, i);
                        if(value instanceof String){
                            table.fields.add(((String) value).trim());
                        }
                        value = reader.readCellValue(3, i);
                        if(value instanceof String){
                            String val = ((String) value).trim();
                            int idex = val.indexOf("(");
                            int len = 0;
                            if(idex > -1){
                                table.types.add(val.substring(0, idex));
                                String[] arr = val.substring(idex + 1, val.length() - 1).split(",");
                                for (String s : arr) {
                                    len += Integer.parseInt(s);
                                }

                            } else {
                                table.types.add(val);
                                if ("INTEGER".equals(val)) {
                                } else if ("BIGINT".equals(val)) {
                                }
                            }
//                            table.types.add((String) value);
                            table.lens.add(len);
                        }
                    }
//                for(int j = 0; j < reader.getColumnCount(); j++){
//                    Cell cell = reader.getCell(i, j);
//                    if (cell == null) {
//                       continue;
//                    }
//                    if(cell.getCellType() == Cell.CELL_TYPE_STRING){
//                        String value = cell.getStringCellValue();
//                        if(value.contains("字段中文名称")){
//                            startRow = -1;
//                            break;
//                        }
//                    }
            }
            tables.put(table.name, table);
//            System.out.println(sheetNames.get(n));
//            System.out.println(reader.getRowCount());
//            System.out.println(reader.getColumnCount());
        }
        IoUtil.closeIfPosible(reader);
    }

    public void export() throws FileNotFoundException {
        IoUtil.write(new FileOutputStream("define.json"), true, JSON.toJSONBytes(tables));

    }

    public void parse(File file){
        String name = file.getName().replaceAll("_20.+?$", "").toUpperCase();
        Table table = null;
        for (Map.Entry<String, Table> entry : tables.entrySet()) {
            if(name.endsWith(entry.getKey())){
                table = entry.getValue();
                break;
            }
        }
        if (table == null) {
            return;
        }
        System.out.println("开始解析" + table.name);


        Connection conn = null;
        long stime = 0;
        try(
            BufferedReader reader = new BufferedReader(new InputStreamReader (new FileInputStream(file), "GBK"));
            ){

            conn = getConnection();
            conn.setAutoCommit(false);
            StringBuilder sb = new StringBuilder();
            sb.append("insert into temp_");
            sb.append(table.name);
            sb.append("(");
            for (String field : table.fields) {
                sb.append(field);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")values(");
            for (String field : table.fields) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            PreparedStatement statement = conn.prepareStatement(sb.toString());

             stime = System.currentTimeMillis();
            String line = null;
            int num = 0;
            while((line = reader.readLine()) != null){
                String[] arr;
                arr = line.split("\\|");
//                arr = fastSplit(line, '|');
//                arr = fastSplit(line, '|');
                append:{
                    //如果不够，继续向下读
                    while(arr.length < table.fields.size()){
                        line = reader.readLine();
                        if (line == null) {
                            break append;
                        }
                        String[] arr2 = line.split("\\s*\\|\\s*");
                        arr = append(arr,arr2);
                    }
                }
                if(arr.length > table.fields.size()){
                    //报错
                } else {
                    int i = 0;
                    for (String s : arr) {
                        statement.setString(++i, s.trim());
                    }
                    statement.addBatch();
                    int d = 1;
                    num++;
                }
            }

            System.out.printf("准备插入%d条数据,用时 %d ms \n", num, System.currentTimeMillis() - stime);
            stime = System.currentTimeMillis();
            statement.executeBatch();
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("takes %d ms", System.currentTimeMillis() - stime);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            IoUtil.closeIfPosible(conn);
        }
//        try(
//            RandomAccessFile raf = new RandomAccessFile(file, "r");
//            ) {
//            int pos = 0;
//            byte[] bytes = new byte[1024 * 1024 * 2];
//            int i = -1;
//            for (Integer len : table.lens) {
//                i++;
//                int n = raf.read(bytes, 0, len);
//                if (n <= 0) {
//                    //读取出错
//                }
//                switch (table.types.get(i)){
//                    case "VARCHAR":
//                    case "CHARACTER":
//                        String str = new String(bytes, 0, n);
//                        int c = 1;
//                        break;
//                }
//                //多读取一个|
//                raf.read();
//            }
//
////            while(pos < raf.length()){
////
////                raf.readByte()
////            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    private List<String> split(String line){
//        return (line.split("\\|"));
//    }

    public static String[] fastSplit(String line, char split){
        String[] temp = new String[line.length()/2];
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(split);  // First substring
        while( j >= 0){
            temp[wordCount++] = line.substring(i,j);
            i = j + 1;
            j = line.indexOf(split, i);   // Rest of substrings
        }
        temp[wordCount++] = line.substring(i); // Last substring
        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);
        return result;
    }//end fastSplit

    private String[] append(String[] src, String[] append){
        String[] ret = new String[src.length + append.length];
        System.arraycopy(src, 0, ret, 0, src.length);
        System.arraycopy(append, 0, ret, src.length, append.length);
        return ret;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.ibm.db2.jcc.DB2Driver");
        return DriverManager.getConnection("jdbc:db2://150.0.104.8:50000/fzsys", "db2inst1", "11111111");
    }

    public static void main(String[] args) throws FileNotFoundException {
        HzData hz = new HzData();;
        hz.init();
        hz.export();
    }
}
