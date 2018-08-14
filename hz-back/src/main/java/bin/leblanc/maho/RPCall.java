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

    private static Map<String, Object> map = Collections.synchronizedMap(new HashMap());
    private static Map<String, Method> methodMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<Method, MethodStruct> cache = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, RPCOption> optionMap = Collections.synchronizedMap(new HashMap<>());

    private static Register register = new Register();
    private static RPCOption EMPTY_OPTION = new RPCOption();

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


    private static void log(Object o) {
        System.out.println(o);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class MethodStruct {
        List<Type> argumentTypes;
        List<Class> sourceTypes;
        Type returnType;
//        Object instance;
//        String methodName;
    }

    enum Type {
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
    private static MethodStruct analyze(Method method) {
        if (cache.containsKey(method)) {
            return cache.get(method);
        }
        Parameter[] parameters = method.getParameters();
//        log(method.getName());
//        log(method.getReturnType().getName());
        List<Type> args = new ArrayList<>();
        List<Class> sourceTypes = new ArrayList<>();
        for (Parameter parameter : parameters) {
            args.add(getParameterType(parameter));
            sourceTypes.add(parameter.getType());
        }
        MethodStruct methodStruct = new MethodStruct(args, sourceTypes, getCommonType(method.getReturnType()));
        cache.put(method, methodStruct);
        return methodStruct;
    }


    private static Type getParameterType(Parameter parameter) {
        Type type = getCommonType(parameter.getType());
        return type;
    }

    private static Type getCommonType(Class clz) {
        //枚举
        if (clz.isEnum()) {
            return Type.ENUM;
        }
        switch (clz.getName()) {
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

    private static Object convertArgument(Type type, Class sourceType, Object value) {
        try {
            switch (type) {
                case INT:
                    if (null == value) {
                        return 0;
                    }
                    return Integer.valueOf(String.valueOf(value));

                case STRING:
                    if (null == value) {
                        return "";
                    }
                    return String.valueOf(value);

                case BOOL:
                    if (null == value) {
                        return false;
                    }
                    return String.valueOf(value).equals("true");

                case LONG:
                    if (null == value) {
                        return 0;
                    }
                    return Long.valueOf(String.valueOf(value));

                case DOUBLE:
                    if (null == value) {
                        return 0.00;
                    }
                    return Double.valueOf(String.valueOf(value));

                case ENUM:
                    if (null == value) {
                        return null;
                    }
                    for (Object o : sourceType.getEnumConstants()) {
                        if (((Enum) o).name().equals(value)) {
                            return o;
                        }
                    }
                    return null;

                case JSON:
                    //TODO: 转换失败的时候尽量给一个默认值
                    if (null == value) {
                        return null;
                    }
                    return JSON.parseObject(String.valueOf(value), sourceType);

            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static Object dealResult(RPCOption option, Object result) {
        if (null == option.getAReturn()) {
            return null;
        }
        //结果处理
        result = option.getAReturn().call(result);
        return result;
    }

    public static Object call(String module, String action, String[] args, String requestBody) {
        AtomicInteger count = new AtomicInteger(0);
        return call(module, action, Stream.of(args).collect(Collectors.toMap(item -> count.getAndIncrement(), item -> item)), requestBody);
    }

    public static Object call(String module, String action, Map<Integer, String> args, String requestBody) {
        RPCOption option = optionMap.getOrDefault(module, EMPTY_OPTION);
        //得到模型类
        Object object = map.get(module);
        if (null == object) {
            return dealResult(option, "没有找到这个函数");
        }
        //得到函数
        try {
            Method method;
            if (option.dev) {
                method = Arrays.asList(object.getClass().getMethods()).stream().filter(item -> item.getName().equals(action)).findFirst().orElse(null);
            } else {
                method = methodMap.get(module + action);
            }
            if (null == method) {
                return dealResult(option, "没有找到这个函数");
//                method = Arrays.asList(object.getClass().getMethods()).stream().filter(item -> item.getName().equals(action)).findFirst().orElse(null);
//                methodMap.put(module + action, method);
            }
//            try {
//                Method tst = object.getClass().getMethod("hasQuarters", Object.class, Object.class);
//                log(tst);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }

//            log(JSON.toJSONString(methodMap.keySet()));
            if (null == method) {
                return dealResult(option, "没有找到这个函数");
            }
//            RPCMapping rpcMapping = method.getAnnotation(RPCMapping.class);
//            if(null == rpcMapping){
//                return dealResult(option, "没有找到这个函数");
//            }
//            if(Arrays.asList(method.getAnnotations()).stream().allMatch(a -> !a.equals(RPCMapping.class))){
//                return dealResult(option,null);
//            }
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            MethodStruct methodStruct = analyze(method);

            //准备调用
            //调整参数
            LinkedList<Object> realArgs = new LinkedList<>();
            for (int i = 0; i < methodStruct.getArgumentTypes().size(); i++) {
                realArgs.add(convertArgument(methodStruct.getArgumentTypes().get(i), methodStruct.getSourceTypes().get(i), args.get(i)));
            }
            //如果最后一个参数为空,且存在请求内容
            if (methodStruct.getArgumentTypes().size() > 0 && null != requestBody && null == realArgs.get(methodStruct.getArgumentTypes().size() - 1)) {
                int lastIndex = methodStruct.getArgumentTypes().size() - 1;
                try {
                    realArgs.add(lastIndex, JSON.parseObject(requestBody, methodStruct.getSourceTypes().get(lastIndex)));
                } catch (Exception e) {
                }
            }

            //前置参数处理
            if (null != option.getPrefixArgs()) {
                List<Object> prefixArgs = option.getPrefixArgs().call();
                Collections.reverse(prefixArgs);
                for (Object prefixArg : prefixArgs) {
                    realArgs.addFirst(prefixArg);
                }
            }
            //后置参数处理
            if (null != option.getAfterArgs()) {
                List<Object> afterArgs = option.getAfterArgs().call();
                //后置不用翻转
                for (Object afterArg : afterArgs) {
                    realArgs.addLast(afterArg);
                }
            }

            Object result = method.invoke(object, realArgs.toArray());

            return dealResult(option, result);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return dealResult(option, null);
    }


    public static Register register(String key, Object value) {
        return register.register(key, value);
    }

    public static Register register(String key, Object value, RPCOption option) {
        return register.register(key, value, option);
    }


    public static class Register {

        @Synchronized
        public Register register(String key, Object value, RPCOption option) {
            log(key + " registered");
            map.put(key, value);
            optionMap.put(key, option);
            //简单记录函数名
            for (Method method : value.getClass().getDeclaredMethods()) {
                methodMap.put(key + method.getName(), method);
            }
            return this;
        }

        @Synchronized
        public Register register(String key, Object value) {
            return register(key, value, null);
        }
    }

}
