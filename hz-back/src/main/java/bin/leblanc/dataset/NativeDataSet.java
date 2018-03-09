package bin.leblanc.dataset;

import bin.leblanc.dataset.metadata.ICheckParamValue;
import bin.leblanc.dataset.metadata.INativeDataSetFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

public class NativeDataSet {
    @Getter
    private EntityManager entityManager;

    @AllArgsConstructor
    @Getter
    @Setter
    static class Holder{
        String type;
        ICheckParamValue holder;
    }

    @Getter
    private String baseSql;
    @Getter
    private Map<String,Holder> params = new HashMap<>();

    @Getter
    private INativeDataSetFilter resultFilter;


    public NativeDataSet(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public NativeDataSetResult newSearch(){
        return new NativeDataSetResult(this);
    }

    public NativeDataSet setParamType(String key, String type,ICheckParamValue handler){
        if(params.containsKey(key)){
            return this;
        }
        Holder holder = new Holder(type,handler);
        params.put(key,holder);
        return this;
    }

    public NativeDataSet setResultFileter(INativeDataSetFilter filter){
        this.resultFilter = filter;
        return this;
    }

    public NativeDataSet setBaseSql(String sql){
        baseSql = sql;
        return this;
    }


}
