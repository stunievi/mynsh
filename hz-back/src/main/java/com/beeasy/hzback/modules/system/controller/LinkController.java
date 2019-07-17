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

    @RequestMapping(value = "relatedSearch/compare")
    public Result compare(
            @RequestParam Map<String, Object> params
    ){
        JSONObject object = (JSONObject) JSON.toJSON(params);
        object.put("uid", AuthFilter.getUid());
        String cusName = object.getString("CUS_NAME");
        String certCode = object.getString("CERT_CODE");
        PageQuery<JSONObject> dataList = U.beetlPageQuery("link.loan_related_search", JSONObject.class, object);
        if(S.isBlank(certCode)){
            return Result.ok(dataList);
        }
        boolean find = sqlManager.lambdaQuery(RelatedPartyList.class).andEq(RelatedPartyList::getCertCode,certCode).count() > 0;
        if(find){
                        /*贷前关联方查询功能。该功能是为了给客户经理提供贷款是否属于关联方贷款信息，若查询后得知是关联方贷款，则提示客户经理：“该笔贷款属于关联方贷款，不得发放信用贷款，贷款金额占资本净额的比例为XX%”,同时系统发布此条信息至具有“总行关联方风险角色”的用户邮箱，总行具有该角色用户可以通过点击该信息，获知其受理贷款的具体情况，同时可以查看关联方贷款列表，获取更为详细的信息。若查询得知不属于关联方贷款，则提示客户经理：“该笔贷款不是关联方贷款，贷款金额占资本净额的比例为XX%”。
                        是否关联方的判定依据：基础数据中的关联方清单。
*/
            List<JSONObject> list = sqlManager.select("user.查询总行关联方风险角色", JSONObject.class, object);
            for(JSONObject item : list){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, item.getLong("uid"), String.format("客户：%s，证件号：%s，属于关联方贷款，不得发放信用贷款，贷款金额占资本净额的比例为%.2f", cusName, certCode,  11.1), null);
            }
            noticeService2.addNotice(SysNotice.Type.SYSTEM, AuthFilter.getUid(), String.format("客户：%s，证件号：%s，属于关联方贷款，不得发放信用贷款，贷款金额占资本净额的比例为%.2f", cusName, certCode, 11.1), null);
        }else{
            noticeService2.addNotice(SysNotice.Type.SYSTEM, AuthFilter.getUid(), String.format("客户：%s，证件号：%s，不是关联方贷款，贷款金额占资本净额的比例为：%.2f", cusName, certCode, 11.1), null);
        }
        return Result.ok(dataList);
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
