package bin.leblanc.dataset;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class DataModel {
    //允许使用的字段
    private Set<String> fields;
    //允许查询的字段
    private Set<String> searchFields;
    //类模型
    private Class clz;
    //是否返回多个
//    private boolean multipul = false;
    //查找路径
    private List<String> path = new ArrayList<>();

    private String alias = "";

    //关联字段名
    private String linkField;

    public DataModel(Class clz){
        this.clz = clz;
    }

    public DataModel setLinkField(String linkField){
        this.linkField = linkField;
        return this;
    }

//    public DataModel setMultipul(boolean multipul){
//        this.multipul = multipul;
//        return this;
//    }

    public DataModel setPath(String ...path){
        this.path = Arrays.asList(path);
        return this;
    }

    public DataModel setAlias(String alias){
        this.alias = alias;
        return this;
    }








}
