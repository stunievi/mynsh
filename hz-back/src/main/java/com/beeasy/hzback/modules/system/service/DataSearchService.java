package com.beeasy.hzback.modules.system.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.system.dao.IGlobalPermissionDao;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jdk.nashorn.internal.objects.Global;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSearchService {

    @Autowired
    SqlUtils sqlUtils;
    @Autowired
    UserService userService;
    @Autowired
    IGlobalPermissionDao globalPermissionDao;

    /**
     * 对公客户列表
     *
     * @param request
     * @param pageable
     * @return
     */
    public Page searchPublicClient(long uid, PublicClientRequest request, Pageable pageable) {
        String sql = "select a.CUS_ID,a.CUS_NAME,a.CERT_TYPE,a.CERT_CODE,b.CUST_MGR,b.MAIN_BR_ID from CUS_BASE as a " +
                "left join CUS_COM as b on a.CUS_ID=b.CUS_ID " +
                "where (a.CUS_TYPE<>'110' and a.CUS_TYPE<>'120' and a.CUS_TYPE<>'130')";
        //拼接参数
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            sql += (String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            sql += ((String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if (!StringUtils.isEmpty(request.getCERT_CODE())) {
            sql += String.format(" and a.CERT_CODE like '%%%s%%'", request.getCERT_CODE());
        }
        //授权
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CUS_COM);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( b.MAIN_BR_ID in (%s) or b.CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
//        limitCondition.add(String.format(" b.MAIN_BR_ID in (%s)", joinIn(managerCode)));
//        limitCondition.add(String.format(" b.CUST_MGR in (%s)", joinIn(userCode)));
//        if(limitCondition.size() > 0){
//            sql += " and (" + StringUtils.join(limitCondition.toArray(), " or ") + ")";
//        }
        log.error(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 对私客户列表
     *
     * @Param uid
     * @param request
     * @param pageable * @return
     */
    public Page searchPrivateClient(long uid, PrivateClientRequest request, Pageable pageable) {
        String sql = "select a.CUS_ID,a.CUS_NAME,a.CERT_TYPE,a.CERT_CODE,b.CUST_MGR,b.MAIN_BR_ID from CUS_BASE as a left join CUS_INDIV as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE = '110' or a.CUS_TYPE = '120' or a.CUS_TYPE = '130')";
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            sql += (String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            sql += ((String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if (!StringUtils.isEmpty(request.getCERT_CODE())) {
            sql += (String.format(" and a.CERT_CODE like '%%%s%%'", request.getCERT_CODE()));
        }
        //授权
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CUS_INDIV);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( b.MAIN_BR_ID in (%s) or b.CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        log.info(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Map searchClientBase(String CUS_ID) {
        String sql = "select CUS_ID,CUS_NAME,PHONE,CERT_TYPE,CERT_CODE from CUS_BASE where CUS_ID = ? ";
        List<Map<String, String>> rs = sqlUtils.query(sql, Collections.singleton(CUS_ID));
        if (rs.size() > 0) {
            return rs.get(0);
        }
        return new HashMap();
    }

    /**
     * 贷款台账查询
     *
     * @param uid
     * @param request
     * @param pageable
     * @return
     */
    public Page searchAccLoan(final long uid, AccloanRequest request, Pageable pageable) {
        String sql = String.format("select a.BILL_NO,a.CUS_ID,a.CUS_NAME,a.LOAN_ACCOUNT,a.ASSURE_MEANS_MAIN,a.LOAN_AMOUNT,a.LOAN_BALANCE,a.CUR_TYPE,a.LOAN_START_DATE,a.LOAN_END_DATE,a.ACCOUNT_STATUS,a.CLA,a.CUS_MANAGER,a.MAIN_BR_ID,a.CONT_NO,b.USE_DEC,b.LOAN_TERM from ACC_LOAN as a left join CTR_LOAN_CONT as b on a.CONT_NO=b.CONT_NO %s where 1 = 1 ", null != request.getRegister() ? ", t_workflow_instance ins" : "");
        if (null != request.register) {
            sql += " and ins.model_name = '不良资产登记' ";
            sql += " and (select count(*) from t_workflow_instance_attribute attr where attr.instance_id = ins.id and attr.attr_key = 'BILL_NO' and attr.attr_value <> '')";
            if (request.register) {
                sql += " > 0";
            } else {
                sql += " = 0";
            }
        }
        if (!StringUtils.isEmpty(request.getBILL_NO())) {
            sql += String.format(" and a.BILL_NO like '%%%s%%'", request.getBILL_NO());
        }
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            sql += String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID());
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            sql += String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME());
        }
        if (!StringUtils.isEmpty(request.getLOAN_ACCOUNT())) {
            sql += String.format(" and a.LOAN_ACCOUNT like '%%%s%%'", request.getLOAN_ACCOUNT());
        }
        if(!StringUtils.isEmpty(request.getCLA())){
            sql += String.format(" and a.CLA = '%s'", request.getCLA());
        }
        if(!StringUtils.isEmpty(request.getACCOUNT_STATUS())){
            sql += String.format(" and a.ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        if(request.isTimeout()){
            sql += (" and ((a.CAP_OVERDUE_DATE<>'' and a.CAP_OVERDUE_DATE is not NULL) or (a.INT_OVERDUE_DATE<>'' and a.INT_OVERDUE_DATE is not NULL))\n");
        }
        //授权
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.ACC_LOAN);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( a.MAIN_BR_ID in (%s) or a.CUS_MANAGER in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        log.error(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

//    public Page searchAccLoanData(AccloanRequest request, Pageable pageable) {
//        String sql = "select a.BILL_NO,a.CONT_NO,a.LOAN_ACCOUNT,a.CUS_ID,a.CUS_NAME,a.ASSURE_MEANS_MAIN,a.LOAN_AMOUNT,a.LOAN_BALANCE,a.REPAYMENT_MODE,a.CLA,a.CUS_MANAGER,a.MAIN_BR_ID,a.CUS_TYPE from ACC_LOAN a where 1 = 1";
//        List<String> strings = new ArrayList<>();
//        if (!StringUtils.isEmpty(request.getBILL_NO())) {
//            strings.add(String.format(" and a.BILL_NO like '%%%s%%'", request.getBILL_NO()));
//        }
//        if (!StringUtils.isEmpty(request.getCONT_NO())) {
//            strings.add(String.format(" and a.CONT_NO like '%%%s%%'", request.getCONT_NO()));
//        }
//        if (!StringUtils.isEmpty(request.getLOAN_ACCOUNT())) {
//            strings.add(String.format(" and a.LOAN_ACCOUNT like '%%%s%%'", request.getLOAN_ACCOUNT()));
//        }
//        if (!StringUtils.isEmpty(request.getCUS_ID())) {
//            strings.add(String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
//        }
//        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
//            strings.add(String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME()));
//        }
//        sql += StringUtils.join(strings.toArray(), " ");
//        return sqlUtils.pageQuery(sql, pageable);
//    }


    /**
     * 高管信息查询
     *
     * @param uid
     * @param cusId
     * @param pageable
     * @return
     */
    public Page searchCusComManager(final long uid, String cusId, Pageable pageable) {
        String sql = String.format("select a.CUS_ID,a.COM_MRG_NAME,a.COM_MRG_CERT_TYP,a.COM_MRG_CERT_CODE,a.COM_MRG_DUTY,a.COM_MRG_EDT,a.COM_MRG_PHN,a.COM_MRG_ADRR from CUS_COM_MANAGER as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where a.CUS_ID= '%s' ", cusId);
        //授权
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CUS_COM);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( b.MAIN_BR_ID in (%s) or b.CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        log.error(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 联系信息
     *
     * @param uid
     * @param cusId
     * @param pageable
     * @return
     */
    public Page searchComAddr(final long uid, String cusId, Pageable pageable) {
        String sql = String.format("select a.SEQ,a.COM_ADDR_TYP,a.COM_ADDR,a.COM_PHN_CODE,a.COM_FAX_CODE from CUS_COM_CONT as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where a.CUS_ID='%s'", cusId);
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CUS_COM);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( b.MAIN_BR_ID in (%s) or b.CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 个人收入情况
     *
     * @param cusId
     * @param pageable
     * @return
     */
    public Page searchCusInDiv(long uid, String cusId, Pageable pageable) {
        String sql = String.format("select a.INDIV_DEPOSITS,a.INDIV_SUR_YEAR,a.INDIV_ANN_INCM,a.REMARK from CUS_INDIV_INCOME as a left join CUS_INDIV as b on a.CUS_ID=b.CUS_ID where a.CUS_ID='%s'", cusId);
        //授权
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CUS_INDIV);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( b.MAIN_BR_ID in (%s) or b.CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 贷款合同
     * @param uid
     * @param contNo
     * @param pageable
     * @return
     */
    public Page searchCrtLoan(long uid, String contNo, Pageable pageable) {
        String sql = String.format("select CONT_NO,CUS_ID,CUS_NAME,PRD_NAME,CONT_AMT,CONT_STATE,LOAN_START_DATE,LOAN_END_DATE,CUST_MGR,MAIN_BR_ID from CTR_LOAN_CONT where CONT_NO='%s'\n", contNo);
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CTR_LOAN_CONT);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( MAIN_BR_ID in (%s) or CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 担保合同
     * @param uid
     * @param contNo
     * @param pageable
     * @return
     */
    public Page searchGrtGuar(long uid, String contNo, Pageable pageable) {
        String sql = String.format("select a.CONT_NO,b.GUAR_CONT_NO,b.GUAR_CONT_TYPE,b.GUAR_WAY,b.GUAR_NAME,b.GUAR_AMT,b.USED_AMT,b.GUAR_START_DATE,b.GUAR_END_DATE,b.GUAR_CONT_STATE from GRT_LOANGUAR_INFO as a left join GRT_GUAR_CONT as b on a.GUAR_CONT_NO=b.GUAR_CONT_NO where a.CONT_NO='%s'\n", contNo);
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.GRT_GUAR_CONT);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( MAIN_BR_ID in (%s) or CUSTOMER_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 抵押物明细
     * @param uid
     * @param contNo
     * @param pageable
     * @return
     */
    public Page searchGRTGBasicInfo(final long uid, String contNo, Pageable pageable) {
        String sql = String.format("select a.GUARANTY_ID,a.CUS_ID,a.CUS_NAME,a.GAGE_TYPE,a.CER_NO,a.GAGE_NAME,a.RIGHT_CERT_TYPE_CODE,a.RIGHT_CERT_NO,a.CURRENCY,a.CORE_VALUE,a.DEPOT_STATUS from GRT_LOANGUAR_INFO as b left join GRT_GUARANTY_RE as c on b.SERNO=c.SER_NO left join GRT_G_BASIC_INFO as a on c.GUARANTY_ID=a.GUARANTY_ID left join ACC_LOAN as d on b.CONT_NO=d.CONT_NO where b.CONT_NO='%s'\n", contNo);
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.ACC_LOAN);
        if (null == limitMap) {
            return new PageImpl(new ArrayList(), pageable, 0);
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( d.MAIN_BR_ID in (%s) or d.CUS_MANAGER in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        return sqlUtils.pageQuery(sql, pageable);
    }


    /**
     * 对公客户-基本信息
     * @param uid
     * @param cusId
     * @return
     */
    public List searchCUS_COM(final long uid, String cusId) {
        String sql = String.format("select CUS_ID,CUS_NAME,CUS_TYPE,CERT_TYPE,CERT_CODE,CUS_BANK_REL,COM_HOLD_STK_AMT,INVEST_TYPE,COM_SUB_TYP,COM_SCALE,COM_HOLD_TYPE,COM_INS_CODE,COM_CLL_TYPE,COM_CLL_NAME,COM_EMPLOYEE,LEGAL_NAME,LEGAL_CERT_TYPE,LEGAL_CERT_CODE,LEGAL_PHONE,NAT_TAX_REG_CODE,NAT_TAX_REG_ORG,LOC_TAX_REG_CODE,LOC_TAX_REG_ORG,FNA_MGR,COM_OPERATOR,POST_ADDR,PHONE,FAX_CODE,BAS_ACC_BANK,BAS_ACC_NO,COM_CRD_TYP,COM_CRD_GRADE,COM_OPT_ST,COM_REL_DGR,COM_CITY_FLG,CUST_MGR,MAIN_BR_ID,TOTAL_ASSETS,TOTAL_SALES from CUS_COM where CUS_ID = '%s' ", cusId);
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.CUS_COM);
        if (null == limitMap) {
            return new ArrayList();
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( MAIN_BR_ID in (%s) or CUST_MGR in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        List ret = sqlUtils.query(sql);
        return getPermissionResultLimit(uid,SearchTargetType.CUS_COM, ret);
    }

    /**
     * 对私客户-基本信息
     * @param uid
     * @param cusId
     * @return
     */
    public List searchCUS_INDIV(final long uid, String cusId) {
        String sql = "select INNER_CUS_ID,CUS_ID,MNG_BR_ID,CUS_TYPE,CUS_NAME,INDIV_SEX,CERT_TYPE,CERT_CODE,AGRI_FLG,CUS_BANK_REL,COM_HOLD_STK_AMT,BANK_DUTY,INDIV_NTN,INDIV_BRT_PLACE,INDIV_HOUH_REG_ADD,INDIV_DT_OF_BIRTH,INDIV_POL_ST,INDIV_EDT,INDIV_MAR_ST,POST_ADDR,PHONE,FPHONE,FAX_CODE,EMAIL,INDIV_RSD_ADDR,INDIV_RSD_ST,INDIV_SOC_SCR,INDIV_COM_NAME,INDIV_COM_TYP,INDIV_COM_FLD,INDIV_COM_PHN,INDIV_COM_FAX,INDIV_COM_ADDR,INDIV_COM_CNT_NAME,INDIV_COM_JOB_TTL,INDIV_CRTFCTN,INDIV_SAL_ACC_BANK,INDIV_SAL_ACC_NO,INDIV_SPS_NAME,INDIV_SPS_ID_TYP,INDIV_SPS_ID_CODE,INDIV_SCOM_NAME,INDIV_SPS_OCC,INDIV_SPS_DUTY,INDIV_SPS_PHN,INDIV_SPS_MPHN,INDIV_SPS_JOB_DT,COM_REL_DGR,CRD_GRADE,CRD_DATE,REMARK,CUST_MGR,MAIN_BR_ID,CUS_STATUS,INDIV_COM_FLD_NAME from CUS_INDIV where CUS_ID = ?";
        List ret =  sqlUtils.query(sql, Collections.singleton(cusId));
        return getPermissionResultLimit(uid, SearchTargetType.CUS_INDIV, ret);
    }

    /**
     * 贷款台账-基本信息
     * @param uid
     * @param billNo
     * @return
     */
    public List searchACC_LOAN(final long uid, String billNo) {
        String sql = "select BILL_NO,CONT_NO,PRD_PK,BIZ_TYPE,PRD_NAME,PRD_TYPE,CUS_ID,CUS_NAME,BIZ_TYPE_SUB,ACCOUNT_CLASS,LOAN_ACCOUNT,LOAN_FORM,LOAN_NATURE,LOAN_TYPE_EXT,ASSURE_MEANS_MAIN,CUR_TYPE,LOAN_AMOUNT,LOAN_BALANCE,LOAN_START_DATE,LOAN_END_DATE,TERM_TYPE,ORIG_EXPI_DATE,RECE_INT_CUMU,ACTUAL_INT_CUMU,DELAY_INT_CUMU,REPAYMENT_MODE,LOAN_DIRECTION,EXTENSION_TIMES,CAP_OVERDUE_DATE,INT_OVERDUE_DATE,OVER_TIMES_CURRENT,OVER_TIMES_TOTAL,MAX_TIMES_TOTAL,CLA,CLA_DATE,CLA_PRE,CLA_DATE_PRE,LATEST_REPAY_DATE,CUS_MANAGER,INPUT_BR_ID,FINA_BR_ID,MAIN_BR_ID,SETTL_DATE,ACCOUNT_STATUS,GL_CLASS,ISCIRCLE,RETURN_DATE,REMARK from ACC_LOAN where BILL_NO = ?";
        List ret = sqlUtils.query(sql, Collections.singleton(billNo));
        return getPermissionResultLimit(uid, SearchTargetType.ACC_LOAN, ret);
    }


    public Result searchInnateData(final long uid, final String billNo){
        String sql = String.format("select PRD_TYPE from ACC_LOAN where BILL_NO = '%s'", billNo);
        List<Map<String,String>> res = sqlUtils.query(sql);
        if(res.size() == 0){
            return Result.error("找不到这条台账信息");
        }
            String type = res.get(0).get("PRD_TYPE");
            if(type.equals("01")){
                sql = String.format("select a.BILL_NO,a.CONT_NO,a.CUS_ID,a.CUS_NAME,a.ASSURE_MEANS_MAIN,a.LOAN_AMOUNT             ,a.LOAN_BALANCE            ,a.LOAN_START_DATE         ,a.LOAN_END_DATE           ,a.CLA,a.SEVEN_RESULT,a.CAP_OVERDUE_DATE,a.INT_OVERDUE_DATE,a.UNPD_PRIN_BAL,a.DELAY_INT_CUMU,a.CUS_MANAGER,a.INPUT_BR_ID,a.FINA_BR_ID,a.MAIN_BR_ID,b.PHONE,b.CONTACT_NAME,c.POST_ADDR,c.COM_CRD_GRADE,e.LOAN_TERM from ACC_LOAN as a left join CUS_BASE as b on a.CUS_ID=b.CUS_ID left join  CUS_COM as c on a.CUS_ID=c.CUS_ID left join CTR_LOAN_CONT as e on a.CONT_NO=e.CONT_NO where a.BILL_NO='%s'", billNo);
            }
            else if(type.equals("02")){
                sql = String.format("select a.BILL_NO,a.CONT_NO,a.CUS_ID,a.CUS_NAME,a.ASSURE_MEANS_MAIN,a.LOAN_AMOUNT             ,a.LOAN_BALANCE            ,a.LOAN_START_DATE         ,a.LOAN_END_DATE           ,a.CLA,a.SEVEN_RESULT,a.CAP_OVERDUE_DATE,a.INT_OVERDUE_DATE,a.UNPD_PRIN_BAL,a.DELAY_INT_CUMU,a.CUS_MANAGER,a.INPUT_BR_ID,a.FINA_BR_ID,a.MAIN_BR_ID,b.PHONE,b.CONTACT_NAME,d.POST_ADDR,d.CRD_GRADE,e.LOAN_TERM from ACC_LOAN as a left join CUS_BASE as b on a.CUS_ID=b.CUS_ID left join  CUS_INDIV as d on a.CUS_ID=d.CUS_ID left join CTR_LOAN_CONT as e on a.CONT_NO=e.CONT_NO where a.BILL_NO='%s'", billNo);
            }
            else{
                return Result.error("目前只可以针对对公或者对私发起任务");
            }


        //授权
        Map<String, List<String>> limitMap = getPermissionLimit(uid, SearchTargetType.ACC_LOAN);
        if (null == limitMap) {
            return Result.error("找不到这条台账信息");
        } else if (limitMap.size() > 0) {
            sql += String.format(" and ( a.MAIN_BR_ID in (%s) or a.CUS_MANAGER in (%s) )", joinIn(limitMap.get("dep")), joinIn(limitMap.get("user")));
        }
        List ret = sqlUtils.query(sql);
        if(ret.size() == 0){
            return Result.error("找不到这条台账信息");
        }
        return Result.ok(ret.get(0));
    }


    /********* 报表 **********/
    public Page searchYQYSBJDQ(YQYSBJDQRequest request, Pageable pageable) {
        String sql = "SELECT BILL_NO, CONT_NO, CUS_ID, CUS_NAME, LOAN_BALANCE\n" +
                "FROM ACC_LOAN\n" +
                "WHERE 1 = 1 ";
        if (!StringUtils.isEmpty(request.getSTART_DATE())) {
            sql += (String.format(" and LOAN_END_DATE >= '%s'", request.getSTART_DATE()));
        }
        if (!StringUtils.isEmpty(request.getEND_DATE())) {
            sql += (String.format(" and LOAN_END_DATE <= '%s'", request.getEND_DATE()));
        }
        if (!StringUtils.isEmpty(request.getPRD_TYPE())) {
            sql += (String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE()));
        }
        if (!StringUtils.isEmpty(request.getLOAN_NATURE())) {
            sql += (String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN()));
        }
        if (!StringUtils.isEmpty(request.getACCOUNT_STATUS())) {
            sql += (String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS()));
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchYQYSLX(YQYSLXRequest request, Pageable pageable) {
        String sql = "SELECT a1.BILL_NO,a1.CONT_NO,a1.CUS_ID,a1.CUS_NAME,a1.LOAN_BALANCE * a1.REALITY_IR_Y / 12 * (\n" +
                " TO_DAYS(Least(a1.LOAN_END_DATE, '%s')) - TO_DAYS(GREATEST(a1.LOAN_START_DATE, '%s'))\n" +
                " ) / 30 as INT_CUMU\n" +
                "FROM ACC_LOAN a1\n" +
                "WHERE 1 = 1 ";
        sql = String.format(sql, request.getEND_DATE(), request.getSTART_DATE());
        if (!StringUtils.isEmpty(request.getPRD_TYPE())) {
            sql += String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE());
        }
        if (!StringUtils.isEmpty(request.getLOAN_NATURE())) {
            sql += String.format(" and LOAN_NATURE = '%s'", request.getLOAN_NATURE());
        }
        if (!StringUtils.isEmpty(request.getASSURE_MEANS_MAIN())) {
            sql += String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN());
        }
        if (!StringUtils.isEmpty(request.getACCOUNT_STATUS())) {
            sql += String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchYuQYSBJ(YuQYSBJRequest request, Pageable pageable) {
        String sql = "SELECT BILL_NO, CONT_NO, CUS_ID, CUS_NAME, UNPD_PRIN_BAL\n" +
                "FROM ACC_LOAN\n" +
                "WHERE 1 = 1";
        if (!StringUtils.isEmpty(request.getPRD_TYPE())) {
            sql += String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE());
        }
        if (!StringUtils.isEmpty(request.getLOAN_NATURE())) {
            sql += String.format(" and LOAN_NATURE = '%s'", request.getLOAN_NATURE());
        }
        if (!StringUtils.isEmpty(request.getASSURE_MEANS_MAIN())) {
            sql += String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN());
        }
        if (!StringUtils.isEmpty(request.getACCOUNT_STATUS())) {
            sql += String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchYuQYSLX(YuQYSLXRequest request, Pageable pageable) {
        String sql = "SELECT BILL_NO, CONT_NO, CUS_ID, CUS_NAME, OVERDUE_RECE_INT,DELAY_INT_CUMU,UNPD_ARR_PRN_BAL,ACT_ARR_PRN_BAL\n" +
                "FROM ACC_LOAN\n" +
                "WHERE 1 = 1 ";
        if (!StringUtils.isEmpty(request.getPRD_TYPE())) {
            sql += String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE());
        }
        if (!StringUtils.isEmpty(request.getLOAN_NATURE())) {
            sql += String.format(" and LOAN_NATURE = '%s'", request.getLOAN_NATURE());
        }
        if (!StringUtils.isEmpty(request.getASSURE_MEANS_MAIN())) {
            sql += String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN());
        }
        if (!StringUtils.isEmpty(request.getACCOUNT_STATUS())) {
            sql += String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        return sqlUtils.pageQuery(sql, pageable);
    }


    /**
     * 设置查找条件授权
     *
     * @param request
     * @return
     */
    public boolean setPermissions(
            GlobalPermissionEditRequest request
    ) {
        //清空授权
        userService.deleteGlobalPermissionByTypeAndObjectId(request.getType(), 0);
        List rules;
        switch (request.getType()) {
            case DATA_SEARCH_CONDITION:
                rules = request.getArray().toJavaList(SearchConditionRule.class);
                userService.addGlobalPermission(request.getType(), 0, request.getUserType(), request.getLinkIds(), rules);
                break;

            case DATA_SEARCH_RESULT:
                rules = request.getArray().toJavaList(SearchResultRule.class);
                userService.addGlobalPermission(request.getType(), 0, request.getUserType(), request.getLinkIds(), rules);
                break;
        }
        return true;
    }

    /**
     * 授权查询
     *
     * @return
     */
    public Optional<GlobalPermission> getPermission(GlobalPermission.Type type, GlobalPermission.UserType userType, Long linkId) {
        List limitList = ImmutableList.of(
                GlobalPermission.Type.DATA_SEARCH_CONDITION,
                GlobalPermission.Type.DATA_SEARCH_RESULT
        );
        if (!limitList.contains(type)) {
            return Optional.empty();
        }
        return userService.getGlobalPermission(type, 0, userType, linkId);
    }


    public List getPermissionResultLimit(final long uid, SearchTargetType searchTargetType,List ret){
        //管理员默认开放所有权限
        if (userService.isSu(uid)) {
            return ret;
        }
        //查询用户所持有的所有授权
        List<GlobalPermission> ps = globalPermissionDao.getPermissionsByUser(Collections.singleton(GlobalPermission.Type.DATA_SEARCH_RESULT), Collections.singleton(GlobalPermission.UserType.ROLE), uid);
        List<String> fields = ps.stream()
                .filter(p -> null != p.getDescription())
                .map(p -> ((JSONArray) p.getDescription()).toJavaList(SearchResultRule.class))
                .flatMap(List::stream)
                .filter(item -> item.getSearchTargetType().equals(searchTargetType))
                .flatMap(item -> Arrays.stream(item.getValue().split("\n")))
                .filter(item -> null != item)
                .map(item -> item.trim())
                .collect(Collectors.toList());
        return (List) ret.stream()
                .map(item -> {
                    Map<String,Object> map = (Map<String, Object>) item;
                    return map.entrySet().stream()
                            .filter(entry -> fields.contains(entry.getKey()))
                            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

                })
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> getPermissionLimit(final long uid, SearchTargetType searchTargetType) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return null;
        }
        //管理员默认开放所有权限
        if (user.isSu()) {
            return new HashMap<>();
        }
        //查询用户所持有的所有授权
        List<GlobalPermission> ps = globalPermissionDao.getPermissionsByUser(Collections.singleton(GlobalPermission.Type.DATA_SEARCH_CONDITION), Collections.singleton(GlobalPermission.UserType.ROLE), uid);
        //部门主管
        List<Quarters> qs = user.getQuarters()
                .stream()
//                .filter(q -> q.isManager())
                .collect(Collectors.toList());
        List<String> managerCode = new ArrayList<>();
        List<String> userCode = new ArrayList<>();
        List<String> finalManagerCode = managerCode;
        List<String> finalUserCode = userCode;
        ps.stream().filter(p -> null != p.getDescription())
                .map(p -> ((JSONArray) p.getDescription()).toJavaList(SearchConditionRule.class))
                .flatMap(List::stream)
                .filter(rule -> rule.getTargetType().equals(searchTargetType))
                .forEach(rule -> {
                    switch (rule.getSearchType()) {
                        case FOR_DEPARTMENT:
                            switch (rule.getValueType()) {
                                case BIND_VALUE:
                                    finalManagerCode.addAll(qs.stream().map(q -> initString(q.getDepartment().getAccCode())).collect(Collectors.toList()));
                                    break;

                                case CUSTOM:
                                    finalManagerCode.add(initString(rule.getValue()));
                                    break;
                            }
                            break;

                        case FOR_USER:
                            switch (rule.getValueType()) {
                                case BIND_VALUE:
                                    finalUserCode.add(initString(user.getAccCode()));
                                    break;

                                case CUSTOM:
                                    finalUserCode.add(initString(rule.getValue()));
                                    break;
                            }
                            break;
                    }
                });

        String uuid = UUID.randomUUID().toString();
        //去除无用的数据
        userCode = userCode.stream()
                .filter(code -> !StringUtils.isEmpty(code))
                .distinct()
                .collect(Collectors.toList());
        managerCode = managerCode.stream()
                .filter(code -> !StringUtils.isEmpty(code))
                .distinct()
                .collect(Collectors.toList());
        if (managerCode.size() == 0) {
            managerCode.add(uuid);
        }
        if (userCode.size() == 0) {
            userCode.add(uuid);
        }
        log.error(JSON.toJSONString(ps));
        log.error(JSON.toJSONString(managerCode));
        log.error(JSON.toJSONString(userCode));
        return ImmutableMap.of(
                "dep", managerCode,
                "user", userCode
        );
    }

    private String initString(String str) {
        return null == str ? "" : str;
    }

    private String joinIn(List<String> list) {
        list = list.stream().map(str -> "'" + str + "'").collect(Collectors.toList());
        return StringUtils.join(list.toArray(), ",");
    }


    @Data
    public static class PublicClientRequest {
        String CUS_ID;
        String CUS_NAME;
        String CERT_CODE;
    }

    @Data
    public static class PrivateClientRequest {
        String CUS_ID;
        String CUS_NAME;
        String CERT_CODE;
        String PHONE;
        String CUST_MGR;
    }

    @Data
    public static class AccloanRequest {
        String BILL_NO;
//        String CONT_NO;
        String LOAN_ACCOUNT;
        String CUS_ID;
        String CUS_NAME;
        String ACCOUNT_STATUS;
        String CLA;

        Boolean register;
        boolean timeout;
    }

    @Data
    public static class YQYSBJDQRequest {
        String START_DATE;
        String END_DATE;

        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }

    @Data
    public static class YQYSLXRequest {
        @NotEmpty
        String START_DATE;
        @NotEmpty
        String END_DATE;
        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }

    @Data
    public static class YuQYSBJRequest {
        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }

    @Data
    public static class YuQYSLXRequest {
        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }

    @Data
    public static class SearchConditionRule {
        //查询规则
        SearchType searchType;
        //授权对象
        SearchTargetType targetType;
        //约束类型
        SearchValueType valueType;
        //约束值
        String value;
    }

    @Data
    public static class SearchResultRule {
        //授权对象
        SearchTargetType searchTargetType;
        //限制字段
        String value;
    }


    public enum SearchType {
        FOR_DEPARTMENT,
        FOR_USER
    }

    public enum SearchTargetType {
        //对公客户
        CUS_COM,
        //对私客户
        CUS_INDIV,
        //贷款合同
        CTR_LOAN_CONT,
        //担保合同
        GRT_GUAR_CONT,
        //贷款台账
        ACC_LOAN
        //高管
//        CUS_COM_MANAGER,
        //联系信息
//        CUS_COM_CONT
//        PRIVATE_CLIENT,
//        PUBLIC_CLIENT,
//        ACC_LOAN_DATA
    }

    public enum SearchValueType {
        BIND_VALUE,
        CUSTOM
    }

}
