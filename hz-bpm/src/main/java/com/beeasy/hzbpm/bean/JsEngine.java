package com.beeasy.hzbpm.bean;

import cn.hutool.core.util.StrUtil;
import com.github.llyb120.nami.json.Obj;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JsEngine {
    private static final ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("javascript");

    public static boolean runExpression(Obj attrs, String expression){
        if(StrUtil.isBlank(expression)){
            return true;
        }
        expression = expression.replaceAll("“|”", "\"")
                .replaceAll("‘|’","'");
        StringBuilder sb = new StringBuilder();
        sb.append("(function(){\n");
        sb.append("var data = ");
        sb.append(attrs.toString());
        sb.append(";\n");
        sb.append("var getValue = function(key){return data[key] || ''};\n");
        sb.append("return ");
        sb.append(expression);
        sb.append("})()");
        try{
            return (boolean) jsEngine.eval(sb.toString());
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
