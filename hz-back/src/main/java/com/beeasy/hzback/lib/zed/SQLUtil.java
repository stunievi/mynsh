package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
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
//    final static String WHERE = "$where";

//    final static         String LT = "lt";


//    static String OP_IN = "in";
//    static String OP_GET = "get";
//    static String OP_LET = "let";
//    static String OP_IN = "in";



    public void select(JSONObject object) throws Exception {
        Map<String, Class<?>> entityMap = Zed.getEntityMap();

        Set<String> entityKeys = object.keySet();
        Map<String,Object> map = new HashMap<>();
        for (String entityKey : entityKeys) {
            JSONObject entityValue = object.getJSONObject(entityKey);

            boolean multipul = false;
            //单独查询
            if(entityKey.contains("[]")){
                multipul = true;
                entityKey = entityKey.replace("[]","");
            }
            //多个查询
            log.info(entityKey);
            //不存在该表的情况直接略过
            if (!entityMap.containsKey(entityKey)) {
                log.info("no entity " + entityKey);
                continue;
            }
            JSONObject condition;
            //得到检索条件，如果没有，则为空
            if(entityValue.containsKey(WHERE)){
                condition = entityValue.getJSONObject(WHERE);
            }
            else{
                condition = new JSONObject();
            }
            //得到需要查询的表
            Class<?> clz = entityMap.get(entityKey);
            //得到需要查询的字段
            Set<String> keys = condition.keySet();
            CriteriaQuery<?> query = buildWhere(clz,condition);

            //得到附加字段
            Set<PluralAttribute> linkFields = jpaUtil.getLinkFields(clz);

            //略过需要查询的附加字段
//            Root root = query.from(clz);

            TypedQuery<?> q = entityManager.createQuery(query);
            Integer rows = entityValue.getInteger(ROWS);
            Integer page = entityValue.getInteger(PAGE);
            if(rows == null) rows = 2;
            if(page == null) page = 1;
            //检查是否有附加字段
            Set<String> externFields = entityValue
                    .keySet()
                    .stream()
                    .filter(item -> !(item.equals(WHERE) || item.equals(PAGE) || item.equals(ROWS)) )
                    .collect(Collectors.toSet());
            q.setFirstResult((page - 1) * rows);
            q.setMaxResults(rows);


            List<?> result = q.getResultList();

//            Root root = query.from(clz);
//            Set<PluralAttribute> linkFields = root.getModel().getDeclaredPluralAttributes();

            if(result.size() == 0){

            }


            if(multipul){
                //如果声明了附加字段
                fetchExtendFields(clz,result,externFields);

            }
            else{
                result = result.subList(0,1);
                fetchExtendFields(clz,result,externFields);
//                result = result.stream().filter((item,index) -> index > 1);
//                dealSingleSearchResult(linkFields,externFields);
            }

//                    cb.createQuery(clz);



//            Root<?> root = query.from(clz);
//            List<Predicate> predicateList = new ArrayList<>();
            //检查是否存在这个字段




//            query.select(root.get("id"));
//            query.where(predicates);

//
//            q.setFirstResult(100);
//            q.setMaxResults(10);
//
//
//            if(multipul){
//
//            }
//            else{
//
//            }
//
//
//            map.put(entityKey,result);
//            log.info(result.size() + "");
        }
    }


    /**
     * 查找一个模型的附加字段
     */
    private List fetchExtendFields(Class clz, List result,Set<String> externFields){
        //得到这个模型的所有附加字段
        Root root = jpaUtil.getRoot(clz);
        Set<Attribute> normalFields = jpaUtil.getNormalFields(root);
        Set<PluralAttribute> linkFields = jpaUtil.getLinkFields(root);
        Set<String> fieldNames = normalFields.stream().map(field -> field.getName()).collect(Collectors.toSet());

        Set<String> allFields = normalFields.stream().map(item -> item.getName()).collect(Collectors.toSet());
        linkFields.forEach(field -> {
            allFields.add(field.getName());
        });

        //需要保留的字段

        //过滤掉所有附加字段
        //暂时使用JSONFIELD过滤，因为有些时候并不需要过滤所有的字段
        String jsonString = JSON.toJSONString(result,new PropertyFilter(){

            @Override
            public boolean apply(Object o, String name, Object value) {
                return fieldNames.contains(name);
            }

        });
        JSONArray jsonResult = JSON.parseArray(jsonString);
//        result = result
//                .stream()
//                .map(singleItem -> {
//                    return singleItem
//                })
//                .collect(Collectors.toList());
        JSONArray jsonArray = (JSONArray) JSON.toJSON(result);

        for(Object r : result){

        }
        return new ArrayList();
    }

    private void dealSingleSearchResult(Set<PluralAttribute> linkFields,Set<String> externFields){
        if(externFields.size() > 0){
            for(String externField : externFields ){
                linkFields
                        .stream()
                        .filter(item -> item.getName().equals(externField))
                        .findAny()
                        .ifPresent(item -> {
                            log.info(item.getDeclaringType().getSingularAttribute("id").getName());
                            log.info(item.getPersistentAttributeType().getClass().getName());
//                            item.getDeclaringType().
                            log.info("fuck");
                        });
            }
        }

    }

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


//        CriteriaQuery<?> q = cb.createQuery(User.class);
//        Root p = q.from(User.class);
////        Path pid0 = p.get("parent");
//        Set<Integer> list = new HashSet<>();
//        list.add(3);
////        p.fetch("roles",JoinType.INNER);
////        q.select(p);
////        p.get("roles")
////        Join join = p.join("roles");
////        q.where(join.get("id").in(list));
//
//
//        TypedQuery<?> ret = entityManager.createQuery(q);
//        List<User> a = (List<User>) ret.getResultList();
//        if(a.get(0).getRoles() instanceof HibernateProxy){
//            log.info("fuck");
//        }
//
//        if(!!true){
//            return null;
//        }
        Root<?> root = query.from(clz);
        Predicate condition = buildCondition(clz,root,cb,query,object);
        if(condition == null){
            condition = cb.equal(cb.literal(1),cb.literal(1));
        }

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
        EntityType<?> model = root.getModel();
        Set set = model.getDeclaredPluralAttributes();

//Join join = root.join("roles");
//query.select(root.get("id"));
//query.select(root.get("password"));
//        Path pid = p.get("parent_id");
//        root.
//        query.multiselect(root.get("id"),root.get("password"));
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
