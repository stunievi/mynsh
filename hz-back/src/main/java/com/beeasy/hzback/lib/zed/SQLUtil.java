package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SQLUtil {

    @Autowired
    EntityManager entityManager;

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

//    final static         String LT = "lt";


//    static String OP_IN = "in";
//    static String OP_GET = "get";
//    static String OP_LET = "let";
//    static String OP_IN = "in";

    /**
     * 拼装where条件
     *
     * @param clz
     * @param object
     * @return
     */
    public CriteriaQuery<?> buildWhere(Class<?> clz, JSONObject object) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery(clz);
        Root<?> root = query.from(clz);

        Predicate condition = buildCondition(clz,root,cb,query,object);

//                new LinkedList<>();
//        for (String fieldName : keys) {
//            Field field = clz.getDeclaredField(fieldName);
//            field.setAccessible(true);
//            Object fieldValue = object.get(fieldName);
//            Path<?> path = root.get(fieldName);
//            //对象
//            List<Predicate> list = buildObjectCondition(cb, query, path, fieldValue);
//            predicateList.addAll(list);
//
//
//            log.info("has this field");
//        }

//        predicateList.clear();
//        predicateList.add(
//);


//        if (predicateList.size() == 0) {
//            throw new Exception();
//        }

//        Predicate p = cb.and(cb.equal(cb.literal(1),cb.literal(1)));
//        p = cb.and(p,cb.equal(cb.literal(2),cb.literal(2)));
//        Predicate[] predicates = new Predicate[]{p};
//        predicates = predicateList.toArray(predicates);
//        query.where(predicates);
        query.where(condition);

        return query;
    }


    private Predicate buildCondition(Class clz,Path root,CriteriaBuilder cb, CriteriaQuery query, Object object) throws Exception {
        if(!(object instanceof JSONObject)){
            throw  new Exception();
        }
        Predicate result = null;
        Set<String> keys = ((JSONObject) object).keySet();
//        List<Predicate> predicateList = new LinkedList<>();
//        List<Predicate> list;
        for (String fieldName : keys) {
            if(fieldName.equals(AND) || fieldName.equals(OR)){
                continue;
            }

            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = ((JSONObject) object).get(fieldName);
            Path<?> path = root.get(fieldName);
            //对象
            Predicate condition = buildObjectCondition(cb, query, path, fieldValue);
            if(result == null) result = condition;
            else result = cb.and(condition,result);
        }

        if(((JSONObject) object).containsKey(AND)){
            Predicate andCondition = buildCondition(clz,root,cb,query,((JSONObject) object).get(AND));
            if(result == null) result = andCondition;
            else result = cb.and(andCondition,result);
        }
        //最后处理or
        if(((JSONObject) object).containsKey(OR)){
            Predicate orCondition = buildCondition(clz,root,cb,query,((JSONObject) object).get(OR));
            if(result == null) result = orCondition;
            else result = cb.or(orCondition,result);
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
                        buildInCondition(cb,  predicateList, path, obj.getJSONArray(operator));
                        break;

                    case LT:
                        buildLtCondition(cb,  predicateList, path, obj.get(operator));
                        break;

                    case LTE:
                        buildLetCondition(cb,  predicateList, path, obj.get(operator));
                        break;

                    case GT:
                        buildGtCondition(cb,  predicateList, path, obj.get(operator));
                        break;

                    case GTE:
                        buildGteCondition(cb,  predicateList, path, obj.get(operator));
                        break;

                    case EQ:
                        buildEqCondition(cb,predicateList,path,obj.get(operator));
                        break;

                    case NEQ:
                        buildNeqCondition(cb,predicateList,path,obj.get(operator));
                        break;

                    case LIKE:
                        buildLikeCondition(cb,predicateList,path,obj.get(operator));
                        break;

                    case NLIKE:
                        buildNLikeCondition(cb,predicateList,path,obj.get(operator));
                        break;

                }
            }
        } else if (object instanceof JSONArray) {

        } else {
            buildEqCondition(cb,predicateList,path,object);
        }
        Predicate[] predicates = new Predicate[predicateList.size()];
        return cb.and(predicateList.toArray(predicates));
    }

    private void buildNLikeCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.notLike(path,String.valueOf(object))
        );
    }

    private void buildLikeCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.like(path,String.valueOf(object))
        );
    }

    private void buildNeqCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.equal(path,object)
        );
    }

    private void buildEqCondition(CriteriaBuilder cb, List<Predicate> predicateList, Path path, Object object) throws Exception {
        log.info(object.toString());
        predicateList.add(
                cb.equal(path,object)
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
