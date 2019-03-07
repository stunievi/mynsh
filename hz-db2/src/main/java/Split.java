import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.osgl.util.IO;

import java.io.File;
import java.io.FileOutputStream;

public class Split {

    public static void main(String[] args){
        File file = new File("D:\\work\\hznsh\\hz-back\\doc\\qcc\\company.json");
        String str = IO.readContentAsString(file);
        JSONArray arr = (JSONArray) JSON.parse(str);
        for (Object o : arr) {
            JSONObject object = (JSONObject) o;
            String en = object.getString("en");
            File target = new File(file.getParentFile(), "out");
            IO.write(object.toJSONString(), new File(target, en + ".json"));
        }
    }
}
