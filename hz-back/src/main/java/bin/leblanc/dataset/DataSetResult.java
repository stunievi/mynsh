package bin.leblanc.dataset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class DataSetResult {

    private DataSet model;

    public DataSetResult(DataSet model){
        this.model = model;
    }

    public DataSetResult addCondition(String key, Object value){
        return this;
    }

    public void search(){
        //拼装zed请求
        JSONObject request = new JSONObject();
        request.put("method","get");
        //拼装主请求
        List<DataModel> models = model.getModels();
        if(models.size() == 0){
            return;
        }
        DataModel mainModel = models.get(0);

        boolean isMain = true;
        String mainPath = "";
        //拼装所有路径请求
        for (DataModel dataModel : models) {
            if(isMain){
                String clzName = dataModel.getClz().getSimpleName();
                //数据集合的主体一定是一个独一无二的
//                if(dataModel.isMultipul()){
//                    clzName += "[]";
//                }
                request.put(clzName,new JSONObject());
                mainPath = clzName;
                isMain = false;
            }
            else{
                List<String> path = dataModel.getPath();
                if(path.size() == 0){
                    JSONObject object = request.getJSONObject(mainPath);
                    object.put(dataModel.getLinkField(),new JSONObject());
                }
                else{
                    JSONObject object = request.getJSONObject(mainPath);
                    for (String p : path) {
                        object = object.getJSONObject(p);
                    }
                    object.put(dataModel.getLinkField(),new JSONObject());
                }
            }
        }

        try{
            Map<?,?> result = model.getZed().parse(request);
            log.info(JSON.toJSONString(request));
        }
        catch (Exception e){
e.printStackTrace();
        }
        log.info(JSON.toJSONString(request));
    }
}
