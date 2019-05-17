import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class App {



    public static void main(String[] args) throws FileNotFoundException {
        HzData hz = new HzData();
        hz.initJSON();
        hz.parse(new File("S_021_cmis_grt_guar_cont_20190316.all"));
//        ExcelReader reader = ExcelUtil.getReader(FileUtil.file("C:\\Users\\bin\\Desktop\\ods_run\\ods_run\\tableDefinition.xlsx"));
//        System.out.println(reader.getColumnCount());
//        System.out.println(reader.getRowCount());

    }
}
