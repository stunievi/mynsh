package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.RelatedPartyList;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.entity.SysVar;
import com.beeasy.hzback.modules.system.service.LinkSeachService;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/")
public class LinkController {

    @Autowired
    LinkSeachService linkSeachService;

    @Autowired
    SQLManager sqlManager;

    @Autowired
    NoticeService2 noticeService2;
    /**
     * 股东清单
     */
    @RequestMapping(value = "search/link/{no}", method = RequestMethod.GET)
    public Result linkSeach(@PathVariable String no,
                            @RequestParam Map<String, Object> params){
        return Result.ok(linkSeachService.gdSeach(no,params));
    }

    // 集团客户，关联方，股东及股东关联台账
    @RequestMapping(value = "linkLoan/{linkModel}")
    public Result linkLoan(
            @PathVariable String linkModel,
            @RequestParam Map<String, Object> params
    ){
        //TODO:: 总行关联方风险角色用户可以在此查询及导出贷款明细。
        // groupCus ， linkList ， stockHolder
        params.put("uid", AuthFilter.getUid());
        params.put("LINK_MODEL", linkModel);
        PageQuery<JSONObject> dataList = U.beetlPageQuery("link.loan_link_list", JSONObject.class, params);

        SysVar sysNetCapital = sqlManager.lambdaQuery(SysVar.class).andEq(SysVar::getVarName, "NET_CAPITAL").single();
        double tetCapital = Double.parseDouble(sysNetCapital.getVarValue()) * 10000;
        dataList.getList().forEach(item->{
            double acceptAmt = item.getLong("LOAN_AMOUNT");
            BigDecimal b = new BigDecimal(acceptAmt/ tetCapital).multiply(new BigDecimal(100));
            double ratioVal = b.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
            item.put("RATIO", ratioVal + "%");
        });
        return Result.ok(dataList);
    }
}
