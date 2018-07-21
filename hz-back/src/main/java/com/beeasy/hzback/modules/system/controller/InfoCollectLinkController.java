package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.system.dao.IInfoCollectLinkDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeAttributeDao;
import com.beeasy.hzback.modules.system.entity.InfoCollectLink;
import com.beeasy.hzback.modules.system.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "贷款台账绑定API")
@RestController
@RequestMapping("/api/infolink")
public class InfoCollectLinkController {

    @Autowired
    IWorkflowInstanceDao instanceDao;
    @Autowired
    SqlUtils sqlUtils;
    @Autowired
    IWorkflowNodeAttributeDao attributeDao;
    @Autowired
    IInfoCollectLinkDao linkDao;
    @Autowired
    UserService userService;
    @Autowired
    WorkflowService workflowService;

    @ApiOperation(value = "查询我自己的资料收集")
    @RequestMapping(value = "/getMyInfoCollect", method = RequestMethod.GET)
    public Result getMyInfoCollect(
            String CUS_NAME,
            String PHONE,
            String CERT_CODE,
            String BILL_NO,
            Long START_DATE,
            Long END_DATE,
            Pager pager,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Specification query = ((root, criteriaQuery, cb) -> {
            criteriaQuery.groupBy(root.get("id"));
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("modelName"), "资料收集"));
            predicates.add(cb.equal(root.get("dealUserId"), Utils.getCurrentUserId()));
            Join nl = root.join("nodeList", JoinType.LEFT);
            Join at = nl.join("attributeList", JoinType.LEFT);
            if (!StringUtils.isEmpty(CUS_NAME)) {
                predicates.add(
                        cb.and(
                                cb.equal(at.get("attrKey"), "CUS_NAME"),
                                cb.like(at.get("attrValue"), "%" + CUS_NAME + "%")
                        )
                );
            }
            if (!StringUtils.isEmpty(PHONE)) {
                predicates.add(
                        cb.and(
                                cb.equal(at.get("attrKey"), "PHONE"),
                                cb.like(at.get("attrValue"), "%" + PHONE + "%")
                        )
                );

            }
            if (!StringUtils.isEmpty(CERT_CODE)) {
                predicates.add(
                        cb.and(
                                cb.equal(at.get("attrKey"), "CERT_CODE"),
                                cb.like(at.get("attrValue"), "%" + CERT_CODE + "%")
                        )
                );
            }
            if(null != START_DATE){
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("add_time"), new Date(START_DATE))
                );
            }
            if(null != END_DATE){
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("add_time"), new Date(END_DATE))
                );
            }
            //使用bill_no的情况下, 查询所属的关联
            if (!StringUtils.isEmpty(BILL_NO)) {
                Join join = root.join("collectLinks");
                predicates.add(
                        cb.equal(
                                join.get("billNo"),
                                BILL_NO
                        )
                )
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        Page page = (instanceDao.findAll(query, pageable));
        Object ret = page.map(object -> {
            WorkflowInstance instance = (WorkflowInstance) object;
            //得到倒数第一个节点
            Map<String, Object> map = new HashMap<>();
            map.put("id", instance.getId());
            List objs = attributeDao.getValueByWorkflowInstance(instance.getId(), "CUS_NAME");
            map.put("CUS_NAME", objs.size() > 0 ? (String) objs.get(0) : "");
            objs = attributeDao.getValueByWorkflowInstance(instance.getId(), "PHONE");
            map.put("PHONE", objs.size() > 0 ? (String) objs.get(0) : "");
            objs = attributeDao.getValueByWorkflowInstance(instance.getId(), "CERT_TYPE");
            map.put("CERT_TYPE", objs.size());
            map.put("addTime", instance.getAddTime());
            map.put("state", instance.getState());
            map.put("title",instance.getTitle());

            //查询和台账的关联
            map.put("billNo", linkDao.findTopByInstanceId(instance.getId()).map(item -> item.getBillNo()).orElse(""));
            return map;
        });
        return Result.ok(ret);
    }


//    @ApiOperation(value = "查询贷款台账")
//    @RequestMapping(value = "/getAccLoan", method = RequestMethod.GET)
//    public String getAccLoan(
//            Pager pager,
//            @PageableDefault(direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        String sql = "select * from ACC_LOAN order by BILL_NO desc limit " + pageable.getOffset() + "," + pageable.getPageSize();
//        List list = sqlUtils.query(sql).stream().map(item -> {
//            Map<String,Object> map = new HashMap<>();
//            map.put("BILL_NO", item.get("BILL_NO"));
//            map.put("CUS_ID",item.get("CUS_ID"));
//            map.put("CUS_NAME", item.get("CUS_NAME"));
//            map.put("LOAN_ACCOUNT",item.get("LOAN_ACCOUNT"));
//            map.put("links",linkDao.getInstancesByBillNo((String) map.get("BILL_NO")));
//            return map;
//        }).collect(Collectors.toList());
//        String countSql = "select count(*) as num from ACC_LOAN";
//        List<Map<String,String>> countList = sqlUtils.query(countSql);
//        long count = Long.valueOf(countList.get(0).get("num"));
//        PageImpl page = new PageImpl(list,pageable,count);
//        return Result.ok(page).toJson(
//                new Result.DisallowEntry(WorkflowInstance.class, "nodeList")
//        );
//    }


    @Transactional
    @ApiOperation(value = "绑定")
    @RequestMapping(value = "/createLink", method = RequestMethod.GET)
    public Result createLink(
            @RequestParam String BILL_NO,
            @RequestParam String id
    ) {
        List<Long> success = new ArrayList<>();
        for (Long aLong : Utils.convertIds(id)) {
            WorkflowInstance instance = workflowService.findInstance(aLong).orElse(null);
            if (null == instance) {
                continue;
            }
            if (linkDao.countByBillNoAndInstanceId(BILL_NO, aLong) > 0) {
                success.add(aLong);
                continue;
            }
            InfoCollectLink infoCollectLink = new InfoCollectLink();
            infoCollectLink.setBillNo(BILL_NO);
            infoCollectLink.setInstanceId(aLong);
            linkDao.save(infoCollectLink);

            ApplyTaskRequest request = new ApplyTaskRequest();
            request.setDataSource(ApplyTaskRequest.DataSource.ACC_LOAN);
            request.setDataId(BILL_NO);
            workflowService.addExtData(instance, instance.getWorkflowModel(), request, false);
            success.add(aLong);
        }
        return Result.ok(success);
    }

    @Transactional
    @ApiOperation(value = "解绑")
    @RequestMapping(value = "/deleteLink", method = RequestMethod.GET)
    public Result deleteLink(
            @RequestParam String BILL_NO,
            @RequestParam String id
    ) {
        return Result.finish(linkDao.deleteAllByBillNoAndInstanceIdIn(BILL_NO, Utils.convertIdsToList(id)) > 0);
    }

}
