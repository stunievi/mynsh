package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.lib.zed.exception.ErrorWhereFieldsException;
import com.beeasy.hzback.lib.zed.exception.NoEntityException;
import com.beeasy.hzback.lib.zed.exception.NoPermissionException;
import com.beeasy.hzback.lib.zed.metadata.RoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.xml.stream.events.Attribute;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SQLUtil {

    @Autowired
    EntityManager entityManager;

    @Autowired
    EntityManagerFactory entityManagerFactory;

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


    public Object postSingleItem(Class clz, Root root, JSONObject obj) throws Exception {
        Set<String> allFields = jpaUtil.getAllFields(root);
        Object instance = clz.newInstance();
        Set<String> keys = obj.keySet();
        String idName = jpaUtil.getIdName(root);
        for (String key : keys) {
            //消去不存在的字段
            if (!allFields.contains(key)) {
                continue;
            }
            //禁止设置主键
            if (idName.equals(key)) {
                continue;
            }
            Field field = clz.getDeclaredField(key);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(instance, obj.get(key));
        }
        Object result = entityManager.merge(instance);
        if (result == null) {
            return -1;
        }
        Field idField = result.getClass().getDeclaredField(idName);
        if (!idField.isAccessible()) {
            idField.setAccessible(true);
        }
        return idField.get(result);
    }

    @Transactional
    public Map<String, Object> post(JSONObject postObject,Set<RoleEntity> roleEntities) throws NoPermissionException {
        Map<String, Class<?>> entityMap = Zed.getEntityMap();
        Set<String> entityKeys = postObject.keySet();
        Map<String, Object> map = new HashMap<>();

        boolean isSU = isSuperAdmin(roleEntities);

        for (String entityKey : entityKeys) {
            if (!entityMap.containsKey(entityKey)) {
                map.put(entityKey, Long.valueOf(-1));
                continue;
            }
            Class clz = entityMap.get(entityKey);

            Set<RoleEntity> newRoleEntities = roleEntities;
            if(!isSU){
                newRoleEntities = roleEntities
                        .stream()
                        .filter(roleEntity -> {
                            Optional<EntityPermission> entityPermission = roleEntity
                                    .getRolePermission()
                                    .getEntityPermission(clz);
                            return entityPermission.isPresent() && entityPermission.get().isPost();
                        })
                        .collect(Collectors.toSet());
                if(newRoleEntities.size() == 0){
                    throw new NoPermissionException();
                }
            }

            Root root = jpaUtil.getRoot(clz);
            Object entityValue = postObject.get(entityKey);
            //批量增加
            if (entityValue instanceof JSONArray) {
                List ids = ((JSONArray) entityValue).stream().map(item -> {
                    try {
                        return postSingleItem(clz, root, (JSONObject) item);
                    } catch (Exception e) {
                        return -1;
                    }
                }).collect(Collectors.toList());
                map.put(entityKey, ids);
            } else if (entityValue instanceof JSONObject) {
                Object ret;
                try {
                    ret = postSingleItem(clz, root, (JSONObject) entityValue);
                } catch (Exception e) {
                    ret = -1;
                }
                map.put(entityKey, ret);
            }
        }

        return map;
    }

    protected boolean isSuperAdmin(Set<RoleEntity> roleEntities){
        return (roleEntities.stream().anyMatch(roleEntity -> roleEntity.getRolePermission().getAllowMap().containsKey(Zed.GET)));
    }

    protected boolean putSingleItem(Class clz, String idName, Object idValue, JSONObject update) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate query = cb.createCriteriaUpdate(clz);
        Root root = query.from(clz);
        Set<String> keySet = update.keySet();
        keySet.forEach(key -> {
            query.set(root.get(key), update.get(key));
        });

        //拼装条件
        if (idValue instanceof JSONArray) {
            Set<Object> ids = ((JSONArray) idValue)
                    .stream()
                    .collect(Collectors.toSet());
            query.where(root.get(idName).in(ids));
        } else {
            query.where(cb.equal(root.get(idName), idValue));
        }

        Query q = entityManager.createQuery(query);
        return q.executeUpdate() > 0;
    }

    @Transactional
    public Map<String, Boolean> put(JSONObject putObject,Set<RoleEntity> roleEntities) throws NoPermissionException {
        Map<String, Class<?>> entityMap = Zed.getEntityMap();
        Set<String> entityKeys = putObject.keySet();
        Map<String, Boolean> map = new HashMap<>();

        boolean isSU = isSuperAdmin(roleEntities);

        for (String entityKey : entityKeys) {
            if (!entityMap.containsKey(entityKey)) {
                map.put(entityKey, false);
                continue;
            }
            //每个被修改的实体必须要求有主键，其他所有条件都被无视
            Class clz = entityMap.get(entityKey);
            Set<RoleEntity> newRoleEntities = roleEntities;
            if(!isSU){
                newRoleEntities = roleEntities
                        .stream()
                        .filter(roleEntity -> {
                            Optional<EntityPermission> entityPermission = roleEntity
                                    .getRolePermission()
                                    .getEntityPermission(clz);
                            return entityPermission.isPresent() && entityPermission.get().isPut();
                        })
                        .collect(Collectors.toSet());
                if(newRoleEntities.size() == 0){
                    throw new NoPermissionException();
                }
            }

            String idName = jpaUtil.getIdName(clz);
            JSONObject singlePutItem = putObject.getJSONObject(entityKey);
            if (!singlePutItem.containsKey(idName)) {
                map.put(entityKey, false);
                continue;
            }
            Object idValue = singlePutItem.get(idName);
            //删除主键字段
            singlePutItem.remove(idName);
            boolean success = putSingleItem(clz, idName, idValue, singlePutItem);
            map.put(entityKey, success);
        }
        return map;
    }

    protected boolean deleteSingleItem(Class clz, String idName, Object idValue) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete query = cb.createCriteriaDelete(clz);
        Root root = query.from(clz);
        //批量删除
        if (idValue instanceof JSONArray) {
            Set<Object> ids = ((JSONArray) idValue)
                    .stream()
                    .collect(Collectors.toSet());
            query.where(root.get(idName).in(ids));
        } else {
            query.where(cb.equal(root.get(idName), idValue));
        }
        Query q = entityManager.createQuery(query);
        return q.executeUpdate() > 0;
    }

    @Transactional
    public Map<String, Boolean> delete(JSONObject deleteObject,Set<RoleEntity> roleEntities) throws NoPermissionException {
        Map<String, Class<?>> entityMap = Zed.getEntityMap();
        Set<String> entityKeys = deleteObject.keySet();
        Map<String, Boolean> map = new HashMap<>();

        boolean isSuperAdmin = isSuperAdmin(roleEntities);

        for (String entityKey : entityKeys) {
            if (!entityMap.containsKey(entityKey)) {
                map.put(entityKey, false);
                continue;
            }
            //每个被删除的实体必须要求有主键，其他所有条件都被无视
            Class clz = entityMap.get(entityKey);
            Set<RoleEntity> newRoleEntities = roleEntities;
            if(!isSuperAdmin) {
                 newRoleEntities = roleEntities
                        .stream()
                        .filter(roleEntity -> {
                            Optional<EntityPermission> entityPermission = roleEntity
                                    .getRolePermission()
                                    .getEntityPermission(clz);
                            return entityPermission.isPresent() && entityPermission.get().isDelete();
                        })
                        .collect(Collectors.toSet());
                if(newRoleEntities.size() == 0){
                    throw new NoPermissionException();
                }
            }

            String idName = jpaUtil.getIdName(clz);
            JSONObject singleDeleteItem = deleteObject.getJSONObject(entityKey);
            if (!singleDeleteItem.containsKey(idName)) {
                map.put(entityKey, false);
                continue;
            }
            Object idValue = singleDeleteItem.get(idName);
            boolean success = deleteSingleItem(clz, idName, idValue);
            map.put(entityKey, success);
        }
        return map;
    }

    public Map<String, Object> select(JSONObject object, Set<RoleEntity> roleEntities) throws Exception {
        Map<String, Class<?>> entityMap = Zed.getEntityMap();

        //如果有任何一个权限允许查询所有表（拥有SU权限）那么不再验证
        boolean isSuperAdmin = isSuperAdmin(roleEntities);


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
                throw new NoEntityException();
            }

            //得到需要查询的表
            Class<?> clz = entityMap.get(entityKey);

            Set<RoleEntity> newRoleEntities = roleEntities;
            //验证是否拥有查询权限
            if(!isSuperAdmin){
                newRoleEntities = newRoleEntities.stream()
                        .filter(roleEntity -> {
                            Optional<EntityPermission> entityPermission = roleEntity
                                    .getRolePermission()
                                    .getEntityPermission(clz);
                            return entityPermission.isPresent() && entityPermission.get().isGet();
                        })
                        .collect(Collectors.toSet());
                if(newRoleEntities.size() == 0){
                    throw new NoPermissionException();
                }
            }
            map.put(sourceKey, selectSingle(newRoleEntities,clz, entityValue, null, multipul));
        }

        return map;
    }

    private Object selectSingle(Set<RoleEntity> roleEntities,Class clz, JSONObject entityValue, String joinName, boolean multipul) throws Exception {
        return selectSingle(roleEntities,clz, entityValue, joinName, multipul, null, null);

    }

    private Object selectSingle(Set<RoleEntity> roleEntities,Class clz, JSONObject entityValue, String joinName, boolean multipul, String joinIdName, Object joinIdValue) throws Exception {
        JSONObject condition;
        //得到检索条件，如果没有，则为空
        if (entityValue.containsKey(WHERE)) {
            condition = entityValue.getJSONObject(WHERE);
        } else {
            condition = new JSONObject();
        }

        /**
         * 目前只拿出权限组的其中一个
         */
        RoleEntity roleEntity = roleEntities.stream().findFirst().get();
        Optional<EntityPermission> opRolePermission = roleEntity.getRolePermission().getEntityPermission(clz);

        //检查字段
        if(opRolePermission.isPresent()){
            EntityPermission rolePermission = opRolePermission.get();

            //如果设定了只能提交的字段
            //禁止提交别的字段
            Set<String> uniqueWhereFields = rolePermission.getUniqueWhereFields();
            //删除无用的字段
            Set<String> removeKeys = condition
                    .entrySet()
                    .stream()
                    .filter(entity -> !uniqueWhereFields.contains(entity.getKey()))
                    .map(entity -> entity.getKey())
                    .collect(Collectors.toSet());
            removeKeys.forEach(key -> {
                condition.remove(key);
            });

            Set<String> requiredWhereFields = rolePermission.getRequiredWhereFields();
            //只能提交的字段同时也是必须提交的
            requiredWhereFields.addAll(uniqueWhereFields);

            //如果设定了必须提交的字段
            if(requiredWhereFields.size() > 0){
                //检查，如果不存在该有的字段，那么直接报错
                for (String key : requiredWhereFields) {
                    if(!condition.containsKey(key)){
                        throw new ErrorWhereFieldsException();
                    }
                }
            }

        }

        //得到需要查询的字段
        CriteriaQuery<?> query = buildWhere(clz, condition, entityValue.containsKey(ORDER) ? entityValue.getJSONObject(ORDER) : null, joinName, joinIdName, joinIdValue);

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
            JSONArray ret = fetchExtendFields(roleEntities,clz, result, entityValue);
            opRolePermission.ifPresent(rolePermission -> {
                if(rolePermission.getGetReturnFields().size() > 0){
                    ret.forEach(obj -> {
                        Set<String> removeKeys = ((JSONObject)obj)
                                .entrySet()
                                .stream()
                                .filter(e -> !rolePermission.getGetReturnFields().contains(e.getKey()))
                                .map(item -> item.getKey())
                                .collect(Collectors.toSet());
                        removeKeys.forEach(k -> {
                            ((JSONObject) obj).remove(k);
                        });
                    });
                }
            });
            return ret;
        } else {
            result = result.subList(0, 1);
            JSONArray ret = fetchExtendFields(roleEntities,clz, result, entityValue);
            opRolePermission.ifPresent(rolePermission -> {
                if(rolePermission.getGetReturnFields().size() > 0){
                    JSONObject obj = (JSONObject) ret.get(0);
                    Set<String> removeKeys = obj
                            .entrySet()
                            .stream()
                            .filter(e -> !rolePermission.getGetReturnFields().contains(e.getKey()))
                            .map(item -> item.getKey())
                            .collect(Collectors.toSet());
                    removeKeys.forEach(k -> {
                        obj.remove(k);
                    });
                }
            });
            return ret.get(0);
        }
    }


    /**
     * 查找一个模型的附加字段
     */
    private JSONArray fetchExtendFields(Set<RoleEntity> roleEntities,Class clz, List result, JSONObject entityValue) throws Exception {
        //检查是否有附加字段
        Set<String> externFields = jpaUtil.getAvaExternFields(clz, entityValue.keySet());


        //需要保留的字段

        //过滤掉所有附加字段
        //暂时使用JSONFIELD过滤，因为有些时候并不需要过滤所有的字段
        JSONArray jsonArray = (JSONArray) JSON.toJSON(result);
        if (externFields.size() == 0) {
            return jsonArray;
        }


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
                    JSONObject singleJSONObject = (JSONObject) singleResult;

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

                    Object ret = selectSingle(roleEntities,clz, externEntity, externField, multipul, idName, idValue);
                    singleJSONObject.put(externField, ret);

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
    public CriteriaQuery<?> buildWhere(Class<?> clz, JSONObject object, JSONObject orderObject, String joinName, String joinIdName, Object joinIdValue) throws Exception {
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
        if (orderObject != null) {
            List<Order> orders = new ArrayList<>();
            Set<String> orderKeys = orderObject.keySet();
            for (String orderKey : orderKeys) {
                String orderType = orderObject.getString(orderKey).toLowerCase();
                if (orderType.equals("asc")) {
                    orders.add(cb.asc(join == null ? root.get(orderKey) : join.get(orderKey)));
                } else {
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
