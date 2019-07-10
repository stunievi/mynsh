package com.beeasy.hzlink;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;

import java.time.LocalDateTime;
import java.util.Date;

import static com.github.llyb120.nami.core.Json.o;

public class connTest implements Runnable{

    @Override
    public void run() {
        boolean flag = true;
        while (flag) {
            String resultString ="";
            long start = System.currentTimeMillis();
            try{
                resultString =  HttpUtil.get("http://loan.hznsyh.com/api/auto/qcclog/getList", o(
                        "page","1","size","10"
                ));

            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("时间："+LocalDateTime.now()+">>内容："+resultString);
            if(System.currentTimeMillis()-start>5000){
                System.out.println("<<<<<<当前时间>>>>>>："+ LocalDateTime.now());
                JOptionPane.showMessageDialog(null,"内网服务超时！！","异常",JOptionPane.ERROR_MESSAGE);
                flag=false;
                System.exit(0);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
