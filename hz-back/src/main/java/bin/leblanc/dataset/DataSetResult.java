package bin.leblanc.dataset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.beetl.ext.fn.Json;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class DataSetResult {

    @Data
    @AllArgsConstructor
    static class DeleteItem {
        Object object;
        String key;
    }

    private DataSet model;

    private Map<String, JSONObject> condition = new LinkedHashMap<>();

    public DataSetResult(DataSet model) {
        this.model = model;
    }

    public DataSetResult addCondition(String alias, String key, Object value) {
        if (!condition.containsKey(alias)) {
            condition.put(alias, new JSONObject());
        }
        condition.get(alias).put(key, value);
        return this;
    }

    public DataSetResult addCondition(String key, Object value) {
        return addCondition("$$main", key, value);
    }

    public DataSetResult setPage(String alias, String key, int value) {
        return this;
    }

    public DataSetResult clearCondition() {
        condition.clear();
        return this;
    }

    public JSONObject search() {
        //拼装zed请求
        JSONObject request = new JSONObject();
        request.put("method", "get");
        //拼装主请求
        Map<String, DataModel> models = model.getModels();
        if (models.size() == 0) {
            return null;
        }

        String mainPath = "";
        //拼装所有路径请求
        for (Map.Entry<String, DataModel> stringDataModelEntry : models.entrySet()) {
            DataModel dataModel = stringDataModelEntry.getValue();
            if (stringDataModelEntry.getKey().equals("$$main")) {
                String clzName = mainPath = dataModel.getClz().getSimpleName();
                //数据集合的主体一定是一个独一无二的
                JSONObject item = new JSONObject();
                if (condition.containsKey("$$main")) {
                    item.put("$where", condition.get("$$main"));
                }
                request.put(clzName, item);
            } else {
                JSONObject object = request.getJSONObject(mainPath);
                LinkedList<String> path = dataModel.getPath();
                for (String p : path) {
                    if (object.containsKey(p)) {
                        object = object.getJSONObject(p);
                    } else {
                        JSONObject parent = object;
                        object = new JSONObject();
                        parent.put(p, object);
                    }
                }
                if (condition.containsKey(stringDataModelEntry.getKey())) {
                    object.put("$where", condition.get(stringDataModelEntry.getKey()));
                }
//                String linkField = path.getLast();
//                object.put(linkField, item);
            }
        }

        try {
            log.info("zed request:" + JSON.toJSONString(request));

            Map<?, ?> result = model.getZed().parse(request);

            log.info("zed result:" + JSON.toJSONString(result));
            //整理数据
            JSONObject ret = (JSONObject) result.get(mainPath);

            Set<DeleteItem> deleteItems = new HashSet<>();
            //放入主体
            for (Map.Entry<String, DataModel> stringDataModelEntry : models.entrySet()) {
                if (stringDataModelEntry.getKey().equals("$$main")) {
                    continue;
                }
                DataModel dataModel = stringDataModelEntry.getValue();
                List<String> path = dataModel.getPath();
                //如果别名为空，那么直接使用连接字段的名字
                //最后再删除，因为还可能需要进行别的整理
                if (path.size() > 0) {
                    Set<Object> set = new LinkedHashSet<>();
                    set.add(ret);
                    int count = 0;
                    for (String p : path) {
                        if(count++ == path.size() - 1){
                            continue;
                        }

                        Set<Object> newSet = new LinkedHashSet<>();
                        for (Object o : set) {
                            JSONObject obj = (JSONObject) o;
                            if (!obj.containsKey(p)) {
                                continue;
                            }
                            Object child = obj.get(p);
                            if (child instanceof JSONArray) {
                                newSet.addAll((JSONArray)child);
                            } else if (child instanceof JSONObject) {
                                newSet.add(child);
                            }
                        }
                        set = newSet;
                    }
                    JSONArray exts = new JSONArray();
                    for (Object o : set) {
                        if (!(o instanceof JSONObject)) {
                            continue;
                        }
                        if (!((JSONObject) o).containsKey(dataModel.getLinkField())) {
                            continue;
                        }
                        deleteItems.add(new DeleteItem(o, dataModel.getLinkField()));

                        Object item = ((JSONObject) o).get(dataModel.getLinkField());
                        if (item instanceof JSONArray) {
                            if (((JSONArray) item).size() == 0) {
                                continue;
                            }
                            exts.addAll((JSONArray)item);
                        } else if (item instanceof JSONObject) {
                            if (((JSONObject) item).keySet().size() == 0) {
                                continue;
                            }
                            exts.add(item);
                        }
//                        exts.add(((JSONObject) o).get(dataModel.getLinkField()));
                    }
                    ret.put(dataModel.getLinkField(), exts);
                    log.info("fuck");
                }
            }
            //删除无用的
            for (Map.Entry<String, DataModel> stringDataModelEntry : models.entrySet()) {
                if (stringDataModelEntry.getKey().equals("$$main")) {
                    continue;
                }
                String alias = stringDataModelEntry.getKey();
                DataModel dataModel = stringDataModelEntry.getValue();
                if (!alias.equals(dataModel.getLinkField())) {
                    ret.put(alias, ret.get(dataModel.getLinkField()));
                    ret.remove(dataModel.getLinkField());
                }

            }
            for (DeleteItem deleteItem : deleteItems) {
                ((JSONObject) deleteItem.getObject()).remove(deleteItem.getKey());
            }

            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
