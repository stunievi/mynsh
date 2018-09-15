package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.system.dao.IInfoCollectLinkDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeAttributeDao;
import com.beeasy.common.entity.InfoCollectLink;
import com.beeasy.common.entity.WorkflowInstance;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
            String LOAN_ACCOUNT,
            Long START_DATE,
            Long END_DATE,
            Pager pager,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Specification query = ((root, criteriaQuery, cb) -> {
//            criteriaQuery.groupBy(root.get("id"));
            criteriaQuery.distinct(true);
//            criteriaQuery.subquery(WorkflowInstance.class).
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
            if (null != START_DATE) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("add_time"), new Date(START_DATE))
                );
            }
            if (null != END_DATE) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("add_time"), new Date(END_DATE))
                );
            }
            //使用bill_no的情况下, 查询所属的关联
            if (!StringUtils.isEmpty(LOAN_ACCOUNT)) {
                Join join = root.join("collectLinks");
                predicates.add(
                        cb.equal(
                                join.get("loanAccount"),
                                LOAN_ACCOUNT
                        )
                );
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
            map.put("CERT_TYPE", objs.size() > 0 ? objs.get(0) : "");

            objs = attributeDao.getValueByWorkflowInstance(instance.getId(), "CERT_CODE");
            map.put("CERT_CODE", objs.size() > 0 ? objs.get(0) : "");

            map.put("addTime", instance.getAddTime());
            map.put("state", instance.getState());
            map.put("title", instance.getTitle());

            //查询和台账的关联
            map.put("loanAccount", linkDao.findTopByInstanceId(instance.getId()).map(item -> item.getLoanAccount()).orElse(""));
            return map;
        });
        return Result.ok(ret);
    }


    @Transactional
    @ApiOperation(value = "绑定")
    @RequestMapping(value = "/createLink", method = RequestMethod.GET)
    public Result createLink(
            @RequestParam String LOAN_ACCOUNT,
            @RequestParam String id
    ) {
        List<Long> success = new ArrayList<>();
        for (Long aLong : Utils.convertIds(id)) {
            WorkflowInstance instance = workflowService.findInstance(aLong);
            if (null == instance) {
                continue;
            }
            if (linkDao.countByLoanAccountAndInstanceId(LOAN_ACCOUNT, aLong) > 0) {
                success.add(aLong);
                continue;
            }
            InfoCollectLink infoCollectLink = new InfoCollectLink();
            infoCollectLink.setLoanAccount(LOAN_ACCOUNT);
            infoCollectLink.setInstanceId(aLong);
            linkDao.save(infoCollectLink);

            ApplyTaskRequest request = new ApplyTaskRequest();
            request.setDataSource(ApplyTaskRequest.DataSource.ACC_LOAN);
            request.setDataId(LOAN_ACCOUNT);
            //TODO: rpc 资料收集
//            workflowService.addExtData(instance, instance.getWorkflowModel(), request, false);
            success.add(aLong);
        }
        return Result.ok(success);
    }

    @Transactional
    @ApiOperation(value = "解绑")
    @RequestMapping(value = "/deleteLink", method = RequestMethod.GET)
    public Result deleteLink(
            @RequestParam String LOAN_ACCOUNT,
            @RequestParam String id
    ) {
        return Result.finish(linkDao.deleteAllByLoanAccountAndInstanceIdIn(LOAN_ACCOUNT, Utils.convertIdsToList(id)) > 0);
    }

}
