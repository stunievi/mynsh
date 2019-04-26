package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.WfInsAttr;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzback.modules.system.service.BackExcelService;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping(value = "/api/workflow/export")
public class ExportController {

    @Autowired
    SQLManager sqlManager;
    @Autowired
    BackExcelService excelService;

    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public ResponseEntity<byte[]> export(
        @RequestParam long id
    ){
        WfIns ins = sqlManager.selectSingle("workflow.查询贷后检查表", C.newMap("id",id), WfIns.class);
        if(!WfIns.canManage(AuthFilter.getUid(), ins.getId())){
            throw new RestException();
        }
        try{
            if(!ins.getState().equals(WfIns.State.FINISHED)){
                throw new Exception();
            }
            //只有贷后跟踪可以导出
            if(!ins.getModelName().contains("贷后跟踪")){
                throw new Exception();
            }
            List<WfInsAttr> attrList =  sqlManager.lambdaQuery(WfInsAttr.class)
                .andEq(WfInsAttr::getInsId, id)
                .select(WfInsAttr::getAttrCname, WfInsAttr::getAttrValue);
            JSONObject attrs = new JSONObject();
            for (WfInsAttr wfInsAttr : attrList) {
                attrs.put(wfInsAttr.getAttrCname(), wfInsAttr.getAttrValue()); 
            }
            attrs.put("检查次数", ins.get("nowNum"));
            attrs.put("总检查次数", ins.get("totalNum"));
            attrs.put("当前年", ins.get("currYear"));
            //处理个别属性
            switch (ins.getModelName()){
                case "贷后跟踪-零售银行部个人按揭":
                    //是否每一期能准时还本付息
//                    String
                    //房产证
                    String fc = U.stGet(attrs,"房产证");
                    String fcdate = U.stGet(attrs,"出证时间");
                    if(S.eq(fc,"未出证")){
                        attrs.put("房产证", S.fmt("未出证(预计出证时间:  %s )", fcdate));
                    }
                    //拖欠
                    String tq = U.stGet(attrs,"有无拖欠本金、利息");
                    String tqj = U.stGet(attrs,"拖欠本金、利息金额（元）");
                    if(S.eq(tq,"是")){
                        attrs.put("拖欠本金、利息金额（元）", S.fmt("是 %s 万元", tqj));
                    }

                    break;
            }

            byte[] bytes = excelService.exportExcelByTemplate(S.fmt("excel/%s.xlsx", ins.getModelName()), attrs);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = ins.getTitle() + ".xlsx";
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
            return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(new byte[0], HttpStatus.OK);
        }
    }
}
