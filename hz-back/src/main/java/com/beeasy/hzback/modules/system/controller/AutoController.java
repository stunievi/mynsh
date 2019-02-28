package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.ClassUtils;
import com.beeasy.hzback.entity.User;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.ann.AssertMethod;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.json.FJHttpMessageConverter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.Unique;
import com.beeasy.mscommon.valid.ValidGroup;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.query.Query;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Transactional
@RestController
@RequestMapping("/api/auto")
public class AutoController {

    @Value("${dev.debug}")
    boolean debug;

    private static final Map<String,Class<ValidGroup>> ClassCache = C.newMap();
    private static final Map<String,Object> ExtraCache = C.newMap();
    private static final Map<String, List<Map>> ValidCache = C.newMap();

//    @Autowired
//    Validator validator;

    @Autowired
    SQLManager sqlManager;
    @Autowired
    FJHttpMessageConverter httpMessageConverter;

    @RequestMapping(value = "/{$model}/delete", method = RequestMethod.GET)
    public Result doDelete(
        @PathVariable String $model
        , @RequestParam Long[] id
    ) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class clz = getClassByName($model);
        ValidGroup instance = (ValidGroup) clz.newInstance();
        ((TailBean)instance).set("$id", id);
        instance.valid(instance, ValidGroup.Delete.class);
        instance.onDelete(sqlManager,id);
        instance.onAfterDelete(sqlManager, id);
        return Result.ok();
    }

    @RequestMapping(value = "/{$model}/getList", method = RequestMethod.GET)
    public Result doGetList(
        @PathVariable String $model
        , @RequestParam Map<String,Object> params
    ) throws IllegalAccessException, InstantiationException {
        Class clz = getClassByName($model);
        ValidGroup instance = (ValidGroup) clz.newInstance();
        String sql = instance.onGetListSql(params);
        Object result;
        if(S.notEmpty(sql)){
            result = U.beetlPageQuery(sql, JSONObject.class, params);
        }
        else{
            result = instance.onGetList(sqlManager, params);
        }
        result = instance.onAfterGetList(sqlManager, params, result);
        return Result.ok(result);
    }

    @RequestMapping(value = "/{$model}/getOne", method = RequestMethod.GET)
    public Result doGetOne(
        @PathVariable String $model
        , @RequestParam Object id
    ) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Method method;
        JSONObject result;
        Class clz = getClassByName($model);
        ValidGroup ins = (ValidGroup)clz.newInstance();
        String sql = ins.onGetOneSql(sqlManager,id);
        ins.onBeforeGetOne(sqlManager,id);
        if(S.notEmpty(sql)){
            result = sqlManager.selectSingle(sql, C.newMap("id", id), JSONObject.class);
        }
        else{
            result = ins.onGetOne(sqlManager,id);
        }
        result = ins.onAfterGetOne(sqlManager, result);
        return Result.ok(result);
    }

    @RequestMapping(value = "/{$model}/add", method = RequestMethod.POST)
    public Result doAdd(
        @PathVariable String $model
        , @RequestBody JSONObject json
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method;
        Object result;
        Class<ValidGroup> clz = getClassByName($model);
        ValidGroup instance = $.map(json).to(clz);
        instance.valid(instance, ValidGroup.Add.class);
        customValid(clz,instance, ValidGroup.Add.class);
        instance.onBeforeAdd(sqlManager);
        result = instance.onAdd(sqlManager);
        result = instance.onAfterAdd(sqlManager,result);
        return Result.ok(result);
    }


    @RequestMapping(value = "/{$model}/edit", method = RequestMethod.POST)
    public Result doEdit(
        @PathVariable String $model
        , @RequestBody JSONObject json
    ) throws IllegalAccessException, NoSuchFieldException {
        Object result;
        Class<ValidGroup> clz = getClassByName($model);
        ValidGroup instance = $.map(json).to(clz);
        instance.valid(instance, ValidGroup.Edit.class);
        customValid(clz, instance, ValidGroup.Edit.class);
        instance.onBeforeEdit(sqlManager);
        result = instance.onEdit(sqlManager);
        result = instance.onAfterEdit(sqlManager, result);
        return Result.ok(result);
    }

    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
    public Result doGet(
        @PathVariable String $model
        , @PathVariable String $action
        , @RequestParam Map<String,Object> params
    ) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        JSONObject json = (JSONObject) JSON.toJSON(params);
        return doPost($model,$action,json);
    }
//
//
    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.POST)
    public Result doPost(
        @PathVariable String $model
        , @PathVariable String $action
        , @RequestBody JSONObject params
    ) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        AuthFilter.setUid(null);
        AuthFilter.setUid(AuthFilter.getUid());
        Object ins;
        Object result = null;
        //优先反射方法
        Class clz = getClassByName($model);
        try{
            Method method = clz.getDeclaredMethod($action, SQLManager.class, JSONObject.class);
            method.setAccessible(true);
            AssertMethod allowMethods = method.getAnnotation(AssertMethod.class);
            //如果声明了该参数
            if(null != allowMethods){
                User.AssertMethod(allowMethods.value());
            }
            result = method.invoke(getInstance($model), sqlManager, params);
        }
        catch (NoSuchMethodException e){
            result = getInstance($model).onExtra(sqlManager, $action, params);
        }
        return Result.ok(result);
    }


    private Class<ValidGroup> getClassByName(String clzName){
            if(false){
               List<Class<?>> clzs = ClassUtils.getAllClassByInterface("com.beeasy", ValidGroup.class);
               return clzs.stream()
                   .map(clz -> (Class<ValidGroup>)clz)
                   .filter(clz -> clz.getSimpleName().equalsIgnoreCase(clzName))
                   .findFirst()
                   .orElse(null);
            }
            else{
                return ClassCache.get(clzName);
            }
    }

    private ValidGroup getInstance(String model) throws IllegalAccessException, InstantiationException {
        Class clz = getClassByName(model);
        ValidGroup instance = (ValidGroup) ExtraCache.get(clz.getSimpleName());
        if (instance == null) {
            instance = (ValidGroup) clz.newInstance();
            ExtraCache.put(clz.getSimpleName(), instance);
        }
        return instance;
    }

    /**
     * 校验规则补强
     * @param clz
     * @param instance
     * @param filter
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private void customValid(Class clz, ValidGroup instance, Class filter) throws IllegalAccessException, NoSuchFieldException {
        //自定义的检查
        for (Map map : ValidCache.get(clz.getSimpleName())) {
            Field field = (Field) map.get("field");
            Annotation annotation = (Annotation) map.get("method");
            Map params = (Map) map.get("params");
            if(annotation instanceof Unique){
                //必定为string
                String key = U.camelToUnderline(field.getName());
                String value = ((String) field.get(instance));
                //null pass
                if(S.empty(value)){
                    continue;
                }
                value = value.trim();
                field.set(instance, value);
                String name = (String) params.get("value");
                String id = (String) params.get("id");
                Field idField = (Field) params.get("idField");
                Query<ValidGroup> query = sqlManager.query(clz).andEq(key, value);
                long count = filter == ValidGroup.Add.class ? query.count()  : query.andNotEq(id, idField.get(instance)).count();
                instance.Assert(count == 0, S.fmt("已经存在相同的%s", name));
            }
        }
    }


    @PostConstruct
    public void init() throws NoSuchFieldException {
//        OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);

        if(true){
            //生产模式下, 直接扫描所有entity做出缓存
            List<Class<?>> clzs = ClassUtils.getAllClassByInterface("com.beeasy", ValidGroup.class);
            for (Class<?> clz : clzs) {
                ClassCache.put(clz.getSimpleName(), (Class<ValidGroup>) clz);
                ClassCache.put(clz.getSimpleName().toLowerCase(), (Class<ValidGroup>) clz);
                //解析字段
                Field[] fields = clz.getDeclaredFields();
                List list = C.newList();
                Map<Field,Annotation> map = C.newMap();
                ValidCache.put(clz.getSimpleName(), list);
                ValidCache.put(clz.getSimpleName().toLowerCase(), list);
                for (Field field : fields) {
                    field.setAccessible(true);
                    Annotation[] ans = field.getAnnotations();
                    for (Annotation an : ans) {
                        if(an instanceof Unique){
                            Field idField = clz.getDeclaredField(((Unique) an).id());
                            idField.setAccessible(true);
                            list.add(C.newMap(
                                "field", field
                                , "key", U.camelToUnderline(field.getName())
                                , "method", an
                                , "params", C.newMap(
                                    "id", ((Unique) an).id()
                                    , "value", ((Unique) an).value()
                                    , "idField", idField
                                )
                            ));
                        }
                    }
                }
            }
        }
    }


}
