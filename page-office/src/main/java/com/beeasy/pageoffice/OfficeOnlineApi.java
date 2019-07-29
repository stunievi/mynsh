package com.beeasy.pageoffice;

import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class OfficeOnlineApi {

    @GetMapping("/word")
    public String showWord(HttpServletRequest request, Map<String, Object> map) {
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        //设置授权程序servlet
        poCtrl.setServerPage("/poserver.zz");
        //添加自定义按钮
        poCtrl.addCustomToolButton("保存", "Save", 1);
        //保存按钮接口地址
        poCtrl.setSaveFilePage("/save");
        //打开文件(打开的文件类型由OpenModeType决定，docAdmin是一个word，并且是管理员权限，如果是xls文件，则使用openModeType.xls开头的,其他的office格式同等)，最后一个参数是作者
        // TODO 这里有个坑，这里打开的文件是本地的，地址如果写成/结构的路径，页面就会找不到文件，会从http://xxxxx/G/id...去找，写成\\就是从本地找
        poCtrl.webOpen("/Users/bin/Downloads/2015-03-17-人生密碼商城.docx", OpenModeType.docAdmin, "光哥");
        //pageoffice 是文件的变量，前端页面通过这个变量加载出文件
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        //跳转到word.html
        return "word";
    }
}
