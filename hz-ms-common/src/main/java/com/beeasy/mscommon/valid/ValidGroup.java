package com.beeasy.mscommon.valid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.osgl.util.S;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface ValidGroup {

    public interface Add{}
    public interface Edit{}
    public interface Search{}
    public interface Delete{}
    public interface Flag1{}
    public interface Flag2{}
    public interface Flag3{}
    public interface Set{}
    public interface Get{}

    default String onGetListSql(Map<String,Object> params){
        return "";
    }

    default Object onGetList(SQLManager sqlManager, Map<String,Object> params){
        return null;
    }

    default Object onAfterGetList(SQLManager sqlManager, Map<String,Object> params, Object result){
        return result;
    }

    default void onBeforeEdit(SQLManager sqlManager){}
    default Object onEdit(SQLManager sqlManager){
        sqlManager.updateById(this);
        return this;
    }
    default Object onAfterEdit(SQLManager sqlManager, Object object){
        return object;
    }
    default void onBeforeAdd(SQLManager sqlManager){
    }
    default Object onAdd(SQLManager sqlManager){
        sqlManager.insert(this, true);
        return this;
    }

    default Object onAfterAdd(SQLManager sqlManager, Object result){
        return result;
    }

    default void onBeforeDelete(SQLManager sqlManager, Long[] id){
    }

    default void onDelete(SQLManager sqlManager, Long[] id){
        for (Long aLong : id) {
           sqlManager.deleteById(getClass(), aLong);
        }
    }
    default void onAfterDelete(SQLManager sqlManager, Long[] id){
    }

    default Object onExtra(SQLManager sqlManager, String action, JSONObject object){return null; };

    /** getone **/
    default void onBeforeGetOne(SQLManager sqlManager, Object id){
    }
    default JSONObject onGetOne(SQLManager sqlManager, Object id){
        return (JSONObject) JSON.toJSON(sqlManager.single(this.getClass(),id));
    }
    default JSONObject onAfterGetOne(SQLManager sqlManager, JSONObject object){
        return object;
    }
    default String onGetOneSql(SQLManager sqlManager, Object id){ return null; }
    /** end **/

    default void Assert(Object ...objects){
        if(objects.length < 1){
            throw new RestException();
        }
        if(!Objects.equals(Boolean.class, objects[0].getClass()) && !Objects.equals(boolean.class, objects[0].getClass())){
            throw new RestException();
        }
        Object[] args;
        if(objects.length > 1){
            args = new Object[objects.length - 2];
            int idex = 0;
            for (Object object : objects) {
                if(idex > 1){
                    args[idex - 2] = object;
                }
                idex++;
            }
        }
        else{
            args = new Objects[0];
        }

        if(objects.length == 2){
            if(!((boolean)objects[0])){
                throw new RestException();
            }
        }
        else{
            if(!((boolean)objects[0])){
                throw new RestException((String) objects[1], args);
            }
        }


//        if(!b){
//            throw new RestException();
//        }
    }
//    default void Assert(boolean b, String msg){
//        if(!b){
//            throw new RestException(msg);
//        }
//    }
//    default void Assert(boolean b, String msg, Object... objs){
//        if(!b){
//            throw new RestException(msg,objs);
//        }
//    }
    default void AssertNotEmpty(Object ...objects){
        Object[] args = new Object[objects.length + 1];
        args[0] = S.notEmpty((String) objects[0]);
        for(short i = 1; i < objects.length; i++){
            args[i] = objects[i];
        }
        Assert(args);
    }
    default void AssertEq(Object ...objects){
        Object[] args = new Object[objects.length + 1];
        args[0] = Objects.equals(objects[0], objects[1]);
        for(short i = 2; i < objects.length; i++){
            args[i - 1] = objects[i];
        }
        Assert(args);
    }

    default void valid(Object object, Class validClz){
        java.util.Set<ConstraintViolation<Object>> errors = U.getValidator().validate(object, validClz);
        String str = errors.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(","));
        if(S.notEmpty(str)){
            throw new RestException(str);
        }
    }


}
