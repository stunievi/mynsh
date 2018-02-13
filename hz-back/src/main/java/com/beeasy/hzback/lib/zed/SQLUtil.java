package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SQLUtil {

    @Autowired
    EntityManager entityManager;

    @Autowired
    JPAUtil jpaUtil;

    final static String GT = "gt";
    final static String GTE = "gte";
    final static String LT = "lt";
    final static String LTE = "lte";
    final static String IN = "in";
    final static String BETWEEN = "between";
    final static String AND = "and";
    final static String OR = "or";
    final static String EQ = "eq";
    final static String NEQ = "neq";
    final static String LIKE = "like";
    final static String NLIKE = "nlike";

    final static String WHERE = "$where";
    final static String PAGE = "$page";
    final static String ROWS = "$rows";
    final static String ORDER = "$order";

//    final static String WHERE = "$where";

//    final static         String LT = "lt";


//    static String OP_IN = "in";
//    static String OP_GET = "get";
//    static String OP_LET = "let";
//    static String OP_IN = "in";


//    public Map<String,Object> select(JSONObject object) throws Exception {
//        return select(object,null);
//    }

//    public Map<String,Object> select(Class clz,JSONObject object,String joinName) throws Exception {
//        return select(object,null);
//    }

    public Map<String, Object> select(JSONObject object) throws Exception {
        Map<String, Class<?>> entityMap = Zed.getEntityMap();

        Set<String> entityKeys = object.keySet();
        Map<String, Object> map = new HashMap<>();
        for (String entityKey : entityKeys) {
            String sourceKey = entityKey;
            JSONObject entityValue = object.getJSONObject(entityKey);

            boolean multipul = false;
            //单独查询
            if (entityKey.contains("[]")) {
                multipul = true;
                entityKey = entityKey.replace("[]", "");
            }
            //多个查询
            log.info(entityKey);
            //不存在该表的情况直接略过
            if (!entityMap.containsKey(entityKey)) {
                log.info("no entity " + entityKey);
                continue;
            }

            //得到需要查询的表
            Class<?> clz = entityMap.get(entityKey);

            map.put(sourceKey, selectSingle(clz, entityValue, null, multipul));
        }

        return map;
    }

    private Object selectSingle(Class clz, JSONObject entityValue, String joinName, boolean multipul) throws Exception {
        return selectSingle(clz, entityValue, joinName, multipul, null, null);

    }

    private Object selectSingle(Class clz, JSONObject entityValue, String joinName, boolean multipul, String joinIdName, Object joinIdValue) throws Exception {
        JSONObject condition;
        //得到检索条件，如果没有，则为空
        if (entityValue.containsKey(WHERE)) {
            condition = entityValue.getJSONObject(WHERE);
        } else {
            condition = new JSONObject();
        }
        //得到需要查询的字段
        CriteriaQuery<?> query = buildWhere(clz, condition,entityValue.containsKey(ORDER) ? entityValue.getJSONObject(ORDER) : null , joinName, joinIdName, joinIdValue);

        //TODO: 分组还没有做
        //TODO: 将来可能附加的功能：字段运算、分组


        TypedQuery<?> q = entityManager.createQuery(query);

        //分页
        Integer rows = entityValue.getInteger(ROWS);
        Integer page = entityValue.getInteger(PAGE);
        if (rows == null) rows = 20;
        if (page == null) page = 1;
        q.setFirstResult((page - 1) * rows);
        q.setMaxResults(rows);

        List<?> result = q.getResultList();
        if (result.size() == 0) {
            return result;
        }
        if (multipul) {
            //如果声明了附加字段
            JSONArray ret = fetchExtendFields(clz, result, entityValue);
            return ret;
        } else {
            result = result.subList(0, 1);
            JSONArray ret = fetchExtendFields(clz, result, entityValue);
            return ret.get(0);
        }
    }


    /**
     * 查找一个模型的附加字段
     */
    private JSONArray fetchExtendFields(Class clz, List result, JSONObject entityValue) throws Exception {
        //检查是否有附加字段
        Set<String> externFields = jpaUtil.getAvaExternFields(clz, entityValue.keySet());

        //得到这个模型的所有附加字段
//        Root root = jpaUtil.getRoot(clz);
//        Set<Attribute> normalFields = jpaUtil.getNormalFields(root);
//        Set<String> fieldNames = normalFields.stream().map(field -> field.getName()).collect(Collectors.toSet());

//        Set<String> allFields = normalFields.stream().map(item -> item.getName()).collect(Collectors.toSet());
//        linkFields.forEach(field -> {
//            allFields.add(field.getName());
//        });

        //需要保留的字段

        //过滤掉所有附加字段
        //暂时使用JSONFIELD过滤，因为有些时候并不需要过滤所有的字段
        JSONArray jsonArray = (JSONArray) JSON.toJSON(result);
        if (externFields.size() == 0) {
            return jsonArray;
        }
//        String jsonString = JSON.toJSONString(result,new PropertyFilter(){
//
//            @Override
//            public boolean apply(Object o, String name, Object value) {
//                return fieldNames.contains(name);
//            }
//
//        });
//        JSONArray jsonResult = JSON.parseArray(jsonString);
////        result = result
////                .stream()
////                .map(singleItem -> {
////                    return singleItem
////                })
////                .collect(Collectors.toList());
//        JSONArray jsonArray = (JSONArray) JSON.toJSON(result);

        String idName = jpaUtil.getIdName(clz);
        Field idField = clz.getDeclaredField(idName);
        idField.setAccessible(true);

        for (String externField : externFields) {
            fieldIterator:
            //如果有任何报错，过直接滤掉这个字段
            try {
                Field field = clz.getDeclaredField(externField);
                field.setAccessible(true);
                Class fieldType = field.getType();

                for (Object singleResult : jsonArray) {
                    JSONObject singleJSONObject = (JSONObject)singleResult;

                    log.info(field.getType().getSimpleName());

                    //得到附加字段的检索条件
                    JSONObject externEntity = entityValue.getJSONObject(externField);
                    boolean multipul = false;
                    if (fieldType.equals(Set.class) || fieldType.equals(List.class)) {
                        multipul = true;
                    }
                    //强制设定条件
                    Object idValue = singleJSONObject.get(idName);
                    if (idValue == null) {
                        continue;
                    }

                    Object ret = selectSingle(clz, externEntity, externField, multipul, idName, idValue);
                    singleJSONObject.put(externField,ret);

//                    if (multipul) {
//                        //是SET的情况
//                        if (fieldType.equals(Set.class)) {
//                            if (ret instanceof List) {
//                                field.set(singleResult, new LinkedHashSet<>((Collection) ret));
//                            } else {
//                                field.set(singleResult, ret);
//                            }
//                        }
//                        //如果应该是LIST
//                        else if (fieldType.equals(List.class)) {
//                            if (ret instanceof Set) {
//                                field.set(singleResult, new ArrayList((Set) ret));
//                            } else {
//                                field.set(singleResult, ret);
//                            }
//                        }
//                    } else {
//                        field.set(singleResult, ret);
//                    }

                }
            } catch (Exception e) {
                break fieldIterator;
            }
        }

        return jsonArray;
    }


    /**
     * 拼装where条件
     *
     * @param clz
     * @param object
     * @return
     */
    public CriteriaQuery<?> buildWhere(Class<?> clz, JSONObject object,JSONObject orderObject, String joinName, String joinIdName, Object joinIdValue) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery(clz);

        Root<?> root = query.from(clz);
        Join join = null;
        if (joinName != null) {
            join = root.join(joinName);
            query.select(join);
        }


        Predicate condition = buildCondition(clz, join == null ? root : join, cb, query, object);
        if (condition == null) {
            condition = cb.equal(cb.literal(1), cb.literal(1));
        }

        if (joinIdName != null) {
            condition = cb.and(cb.equal(root.get(joinIdName), joinIdValue), condition);
        }

        //排序
        if(orderObject != null){
            List<Order> orders = new ArrayList<>();
            Set<String> orderKeys = orderObject.keySet();
            for(String orderKey : orderKeys){
                String orderType = orderObject.getString(orderKey).toLowerCase();
                if(orderType.equals("asc")){
                    orders.add(cb.asc(join == null ? root.get(orderKey) : join.get(orderKey)));
                }
                else{
                    orders.add(cb.desc(join == null ? root.get(orderKey) : join.get(orderKey)));
                }
            }
            query.orderBy(orders);
        }

        query.where(condition);

        return query;
    }


    private Predicate buildCondition(Class clz, Path root, CriteriaBuilder cb, CriteriaQuery query, Object object) throws Exception {
        if (!(object instanceof JSONObject)) {
            throw new Exception();
        }
        Predicate result = null;
        Set<String> keys = ((JSONObject) object).keySet();
//        List<Predicate> predicateList = new LinkedList<>();
//        List<Predicate> list;

        for (String fieldName : keys) {
            if (fieldName.equals(AND) || fieldName.equals(OR)) {
                continue;
            }

            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = ((JSONObject) object).get(fieldName);
            Path<?> path = root.get(fieldName);
            //对象
            Predicate condition = buildObjectCondition(cb, query, path, fieldValue);
            if (result == null) result = condition;
            else result = cb.and(condition, result);
        }

        if (((JSONObject) object).containsKey(AND)) {
            Predicate andCondition = buildCondition(clz, root, cb, query, ((JSONObject) object).get(AND));
            if (result == null) result = andCondition;
            else result = cb.and(andCondition, result);
        }
        //最后处理or
        if (((JSONObject) object).containsKey(OR)) {
            Predicate orCondition = buildCondition(clz, root, cb, query, ((JSONObject) object).get(OR));
            if (result == null) result = orCondition;
            else result = cb.or(orCondition, result);
        }

        return result;
    }


    private Predicate buildObjectCondition(CriteriaBuilder cb, CriteriaQuery<?> query, Path<?> path, Object object) throws Exception {
        List<Predicate> predicateList = new LinkedList<>();
        if (object instanceof JSONObject) {
            JSONObject obj = (JSONObject) object;
            Set<String> keys = obj.keySet();
            for (String operator : keys) {
                log.info("has this key", operator);
                switch (operator) {

                    case IN:
                        buildInCondition(cb, predicateList, path, obj.getJSONArray(operator));
                        break;

                    case LT:
                        buildLtCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case LTE:
                        buildLetCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case GT:
                        buildGtCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case GTE:
                        buildGteCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case EQ:
                        buildEqCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case NEQ:
                        buildNeqCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case LIKE:
                        buildLikeCondition(cb, predicateList, path, obj.get(operator));
                        break;

                    case NLIKE:
                        buildNLikeCondition(cb, predicateList, path, obj.get(operator));
                        break;

                }
            }
        } else if (object instanceof JSONArray) {

        } else {
            buildEqCondition(cb, predicateList, path, object);
        }
        Predicate[] predicates = new Predicate[predicateList.size()];
        return cb.and(predicateList.toArray(predicates));
    }

    private void buildNLikeCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.notLike(path, String.valueOf(object))
        );
    }

    private void buildLikeCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.like(path, String.valueOf(object))
        );
    }

    private void buildNeqCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.equal(path, object)
        );
    }

    private void buildEqCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.equal(path, object)
        );
    }

    private void buildGtCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        if (!(object instanceof Integer) && !(object instanceof BigDecimal)) {
            throw new Exception();
        }
        log.info(object.toString());
        predicateList.add(
                cb.greaterThan(path, (Comparable) object)
        );
    }

    private void buildGteCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        if (!(object instanceof Integer) && !(object instanceof BigDecimal)) {
            throw new Exception();
        }
        log.info(object.toString());
        predicateList.add(
                cb.greaterThanOrEqualTo(path, (Comparable) object)
        );
    }


    private void buildLtCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        if (!(object instanceof Integer) && !(object instanceof BigDecimal)) {
            throw new Exception();
        }
        log.info(object.toString());
        predicateList.add(
                cb.lessThan(path, (Comparable) object)
        );
    }


    private void buildLetCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        if (!(object instanceof Integer) && !(object instanceof BigDecimal)) {
            throw new Exception();
        }
        log.info(object.toString());
        predicateList.add(
                cb.lessThanOrEqualTo(path, (Comparable) object)
        );
    }


    private void buildInCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, JSONArray arr) throws Exception {
        if (arr == null) {
            throw new Exception();
        }
        List<Object> list = arr.stream()
                .map(item -> {
                    //in只可能为Integer和String和
                    if (item instanceof Integer) {
                        return item;
                    } else if (item instanceof String) {
                        return item;
                    } else if (item instanceof BigDecimal) {
                        return item;
                    }
                    return null;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
        //检查限定条件
        if (list.size() == 0) {
            throw new Exception();
        }
        predicateList.add(
                path.in(list)
        );
    }
}
