package bin.leblanc.zed.proxy;

import bin.leblanc.zed.Zed;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.SpringContextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZedProxyHandler implements InvocationHandler {

    private Map<String,Object> template;
    private Map<String,String> cache = new HashMap<>();
    private static Pattern pattern = Pattern.compile("\\$(\\w+)");

    public ZedProxyHandler(Map template) {
        this.template = template;
        this.template.forEach((k,v) -> {
            cache.put(k, JSON.toJSONString(v));
        });
    }

    @Override
    public synchronized Optional<Object> invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String zedTemplate = cache.get(method.getName());
        if(zedTemplate == null){
            return Optional.empty();
        }
        if(!(zedTemplate instanceof String)){
            return Optional.empty();
        }

        //得到参数名
        Map<String,Object> argsMap = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        int count = 0;
        for (Parameter parameter : parameters) {
            if (parameter.isNamePresent()) {
                argsMap.put(parameter.getName(),args[count++]);
            }
        }
        Matcher matcher = pattern.matcher(zedTemplate);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            String key = matcher.group(1);
            if(argsMap.containsKey(key)){
                matcher.appendReplacement(sb,String.valueOf(argsMap.get(key)));
            }
        }
        matcher.appendTail(sb);
        String template = sb.toString();
        try{
            Zed zed = SpringContextUtils.getBean(Zed.class);
            Object ret = zed.parse(template);
            return Optional.of(ret);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
