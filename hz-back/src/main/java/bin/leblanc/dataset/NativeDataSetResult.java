package bin.leblanc.dataset;

import bin.leblanc.dataset.exception.NullParamValueException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NativeDataSetResult {

    private NativeDataSet context;
    private Map<String,Object> params = new HashMap<>();

    private static Pattern pattern = Pattern.compile("\\$(\\w+)");

    public NativeDataSetResult(NativeDataSet context){
        this.context = context;
    }

    public NativeDataSetResult setParam(String key, Object value){
        params.put(key,value);
        return this;
    }

    public List search() throws NullParamValueException {
        String sql = this.buildSql();
        EntityManager entityManager = context.getEntityManager();
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List list = query.getResultList();
        if(context.getResultFilter() != null){
            List newList = new ArrayList();
            list.forEach(item -> {
                newList.add(context.getResultFilter().filter((Map<String, ?>) item));
            });
            return newList;
        }
        return list;
    }

    private Object getParamValue(String key){
        NativeDataSet.Holder holder = context.getParams().get(key);
        if(holder == null) return null;
        Object value = params.get(key);
        if(value == null) return null;
        switch (holder.getType()){
            case "int":
                value = Integer.valueOf(String.valueOf(value));
                break;

            case "string":
                value = "'" + String.valueOf(value) + "'";
                break;
        }
        if(value != null){
            if(holder.getHolder() != null){
                //如果没有命中范围，那么返回null值（跑出异常）
                if(!holder.getHolder().call(value)){
                    return null;
                }
            }
        }
        return value;

    }


    private String buildSql() throws NullParamValueException {
        log.info(context.getBaseSql());
        Matcher matcher = pattern.matcher(context.getBaseSql());
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            String key = matcher.group(1);
            Object value = getParamValue(key);
            if(value == null){
                throw new NullParamValueException();
            }
            log.info(matcher.group());
            matcher.appendReplacement(sb,String.valueOf(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
