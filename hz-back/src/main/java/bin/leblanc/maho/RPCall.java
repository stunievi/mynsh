package bin.leblanc.maho;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RPCall {

    private static Map<String,Object> map = Collections.synchronizedMap(new HashMap());
    private static Map<String,Method> methodMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<Method,MethodStruct> cache = Collections.synchronizedMap(new HashMap<>());

    private static Register register = new Register();

//    @PostConstruct
//    protected static void start() {
//        register("test",new test());
//    }
//
//    static class test{
//        public int a(int aa, double bbb, Integer a, Double b, Object c){
//            return 2;
//        }
//
//
//        public void ttt(){
//            test(new ArrayList<>(),null,null,1);
//        }
//        public String guichu(String a, int d){return a + d + "ffdd";}
//
//        public String test(List<Long> list, String a, @NonNull Type type, int d){return type.name() + list.size() + a + d + "ffdd";}
//
//
//        public String cubi(JSONObject obj){
//            return JSON.toJSONString(obj);
//        }
//
//    }


    private static void log(Object o){
        System.out.println(o);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class MethodStruct{
        List<Type> argumentTypes;
        List<Class> sourceTypes;
        Type returnType;
//        Object instance;
//        String methodName;
    }

    enum Type{
        INT,
        LONG,
        DOUBLE,
        BOOL,
        STRING,
        JSON,
        ENUM
    }

    /**
     * 分析函数
     */
    private static MethodStruct analyze(Method method){
        if(cache.containsKey(method)){
            return cache.get(method);
        }
        Parameter[] parameters = method.getParameters();
        log(method.getName());
        log(method.getReturnType().getName());
        List<Type> args = new ArrayList<>();
        List<Class> sourceTypes = new ArrayList<>();
        for (Parameter parameter : parameters) {
            args.add(getParameterType(parameter));
            sourceTypes.add(parameter.getType());
        }
        MethodStruct methodStruct =  new MethodStruct(args,sourceTypes, getCommonType(method.getReturnType()));
        cache.put(method,methodStruct);
        return methodStruct;
    }


    private static Type getParameterType(Parameter parameter){
        Type type = getCommonType(parameter.getType());
        return type;
    }
    private static Type getCommonType(Class clz){
        //枚举
        if(clz.isEnum()){
            return Type.ENUM;
        }
        switch (clz.getName()){
            //基本类型
            case "int":
            case "java.lang.Integer":
                return Type.INT;

            case "long":
            case "java.lang.Long":
                return Type.LONG;

            case "double":
            case "java.lang.Double":
                return Type.DOUBLE;

            case "boolean":
            case "java.lang.Boolean":
                return Type.BOOL;

            case "java.lang.String":
                return Type.STRING;

            //默认类型
            default:
                return Type.JSON;
        }
    }

    private static Object convertArgument(Type type, Class sourceType, Object value){
        try{
            switch (type){
                case INT:
                    if(null == value){
                        return 0;
                    }
                    return Integer.valueOf(String.valueOf(value));

                case STRING:
                    if(null == value){
                        return "";
                    }
                    return String.valueOf(value);

                case BOOL:
                    if(null == value){
                        return false;
                    }
                    return String.valueOf(value).equals("true");

                case LONG:
                    if(null == value){
                        return 0;
                    }
                    return Long.valueOf(String.valueOf(value));

                case DOUBLE:
                    if(null == value){
                        return 0.00;
                    }
                    return Double.valueOf(String.valueOf(value));

                case ENUM:
                    if(null == value){
                        return null;
                    }
                    for (Object o : sourceType.getEnumConstants()) {
                        if(((Enum)o).name().equals(value)){
                            return o;
                        }
                    }
                    return null;

                case JSON:
                    //TODO: 转换失败的时候尽量给一个默认值
                    if(null == value){
                        return null;
                    }
                    return JSON.parseObject(String.valueOf(value), sourceType);

            }
            return null;
        }
        catch (Exception e){
            return null;
        }
    }

    public static Object call(String module, String action, String[] args, String requestBody){
        AtomicInteger count = new AtomicInteger(0);
        return call(module,action, Stream.of(args).collect(Collectors.toMap(item -> count.getAndDecrement(), item -> item)), requestBody);
    }

    public static Object call(String module, String action, Map<Integer,String> args, String requestBody){
        //得到模型类
        Object object = map.get(module);
        if(null == object){
            return null;
        }
        //得到函数
        try {
            Method method = methodMap.get(module + action);
            if(null == method){
                return null;
            }
            if(!method.isAccessible()){
                method.setAccessible(true);
            }
            MethodStruct methodStruct = analyze(method);

            //准备调用
            //调整参数
            Object[] realArgs = new Object[methodStruct.getArgumentTypes().size()];
            for(int i = 0; i < realArgs.length; i++){
                realArgs[i] = convertArgument(methodStruct.getArgumentTypes().get(i), methodStruct.getSourceTypes().get(i), args.get(i));
            }
            //如果最后一个参数为空,且存在请求内容
            if(methodStruct.getArgumentTypes().size() > 0 && null != requestBody && null == realArgs[methodStruct.getArgumentTypes().size() - 1]){
                int lastIndex = methodStruct.getArgumentTypes().size() - 1;
                try{
                    realArgs[lastIndex] = JSON.parseObject(requestBody,methodStruct.getSourceTypes().get(lastIndex));
                }
                catch (Exception e){
                }
            }
            Object result = method.invoke(object,realArgs);
            return result;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static Register register(String key, Object value){
        return register.register(key,value);
    }


    public static class Register{

        @Synchronized
        public Register register(String key, Object value){
            log(key + " registered");
            map.put(key,value);
            //简单记录函数名
            for (Method method : value.getClass().getDeclaredMethods()) {
                methodMap.put(key + method.getName(), method);
            }
            return this;
        }
    }

}
