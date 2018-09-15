package com.beeasy.hzdata.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzdata.entity.Department;
import com.beeasy.hzdata.entity.User;
import com.beeasy.hzdata.utils.Utils;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.entity.WorkflowModel;
import com.beeasy.rpc.BackRpc;
import com.beeasy.rpc.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping
@Slf4j
public class SearchController {

    @Autowired
//    @Reference(url = "dubbo://127.0.0.1:28001", check = false, timeout = 10000)
    BackRpc backRpc;

    //字段施加限制
    public static Map<String, Object> fieldsMap = C.newMap(
            "201", C.newMap(
                    "no", 201,
                    "type", SearchTargetType.CUS_COM,
                    "fields", S.fastSplit("CUS_ID,CUS_NAME,CUS_TYPE,CERT_TYPE,CERT_CODE,INVEST_TYPE,COM_SUB_TYP,COM_SCALE,COM_HOLD_TYPE,COM_INS_CODE,COM_CLL_TYPE,COM_CLL_NAME,COM_EMPLOYEE,COM_CRD_TYP,COM_CRD_GRADE,COM_OPT_ST,CUST_MGR,MAIN_BR_ID", ",")
            ),
            "202", C.newMap(
                    "no", 202,
                    "type", SearchTargetType.CUS_INDIV,
                    "fields", S.fastSplit("CUS_ID,MNG_BR_ID,CUS_TYPE,CUS_NAME,INDIV_SEX,CERT_TYPE,CERT_CODE,INDIV_NTN,INDIV_BRT_PLACE,INDIV_POL_ST,INDIV_EDT,INDIV_MAR_ST,CUST_MGR,MAIN_BR_ID,CUS_STATUS", ",")
            ),
            "203", C.newMap(
                    "no", 203,
                    "type", SearchTargetType.ACC_LOAN,
                    "fields", S.fastSplit("BILL_NO,CONT_NO,PRD_NAME,PRD_TYPE,CUS_ID,CUS_NAME,LOAN_ACCOUNT,LOAN_FORM,LOAN_NATURE,ASSURE_MEANS_MAIN,CUR_TYPE,LOAN_START_DATE,LOAN_END_DATE,TERM_TYPE,ORIG_EXPI_DATE,REPAYMENT_MODE,CLA,CLA_DATE,CUS_MANAGER,INPUT_BR_ID,FINA_BR_ID,MAIN_BR_ID,ACCOUNT_STATUS,RETURN_DATE", ",")
            )
    );

    @Autowired
    SQLManager sqlManager;

    @Lazy
    @Resource(name = "remoteService")
    DataService dataService;

    @GetMapping("/search/aaa")
    public Result test(){
        return Result.ok(dataService.foo());
    }

    @RequestMapping(value = "/search/accloan/{no}", method = RequestMethod.GET)
    public Result search(
            @PathVariable String no,
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String, Object> params,
            HttpSession session
    ) {

        final long uid = Long.parseLong(String.valueOf(session.getAttribute("uid")));
        User user = sqlManager.unique(User.class,uid);
        //permission
        Map<String, List> limitMap = getPermissionLimit(user);
        if (null == limitMap) {
            return Result.error("找不到数据");
        } else if (C.notEmpty(limitMap)) {
            params.put("deplimit", limitMap.get("dep"));
            params.put("userlimit", limitMap.get("user"));
        }
        PageQuery pageQuery = new PageQuery(pageable.getPageNumber() + 1, pageable.getPageSize());
        try {
            pageQuery.setParas(params);
            sqlManager.pageQuery("accloan." + no, Map.class, pageQuery);
            //命名方式错误, 转换
            List retList = Utils.underLineList(pageQuery.getList());
            //字段过滤
            if(!user.getSu() && fieldsMap.containsKey(no)){
                Map item = (Map) fieldsMap.get(no);
                retList = getPermissionResultLimit(user, (SearchTargetType)item.get("type"),retList, (List)item.get("fields"));
            }
            Page ret = new PageImpl(retList, pageable, pageQuery.getTotalRow());
            //

            List<WorkflowModel> models = new ArrayList<>();
            final Map<Long,Boolean> pubMap = C.newMap();
            if(params.containsKey("modelName")){
                models = sqlManager.lambdaQuery(WorkflowModel.class)
                        .andLike(WorkflowModel::getModelName,"%" + params.getOrDefault("modelName","") + "%")
                        .andEq(WorkflowModel::getOpen,true)
                        .select();
                //检查是否有发任务的权限
                initPubMap(pubMap, user.getId(), models);
            }

            //做出模型名缓存
            final Map<String,String> loanModelNames = C.newMap();
            for (Object o : ret.getContent()) {
                //补充默认字段
                Map<String,String> map = (Map<String, String>) o;
                map.put("pubModelId","0");
                map.put("pubModelName","");
                loanModelNames.put(map.getOrDefault("LOAN_ACCOUNT",""),"");
            }

            if(no.equals("219")){
                //处理参数
                List<WorkflowModel> finalModels = models;
                final String modelName = (String) params.getOrDefault("modelName","");
                if (modelName.contains("贷后跟踪")) {
                    initLoanMap(loanModelNames);
                }
                if(C.empty(finalModels)){
                    return Result.ok(ret);
                }
                ret = ((PageImpl) ret).map(o -> {
                    Map<String,String> map = (Map<String, String>) o;
                    String loanAccount = (String) map.getOrDefault("LOAN_ACCOUNT","");
                    if(modelName.contains("贷后跟踪")){
                        String name = loanModelNames.getOrDefault(loanAccount,"");
                        for (WorkflowModel m : finalModels) {
                            if(S.neq(m.getModelName(), name)) {
                                continue;
                            }
                            if(!pubMap.get(m.getId())){
                                continue;
                            }
                            map.put("pubModelId", finalModels.get(0).getId() + "");
                            map.put("pubModelName", finalModels.get(0).getModelName());
                            break;
                        }
                    }
                    else if(C.notEmpty(finalModels) && pubMap.get(finalModels.get(0).getId())){
                        map.put("pubModelId", finalModels.get(0).getId() + "");
                        map.put("pubModelName", finalModels.get(0).getModelName());
                    }

                    //当有任务正在进行时, 禁止发起相同的登记和不良资产管理流程
                    if(modelName.equals("不良资产登记")){
                        Map r = sqlManager.selectSingle("accloan.countSameBL", C.newMap("MODEL_NAME", modelName, "LOAN_ACCOUNT", loanAccount) , Map.class);
                        int count = (int) r.getOrDefault("1",0);
                        if(count > 0){
                            map.put("pubModelId","0");
                            map.put("pubModelName","");
                        }
                    }
                    return map;

                });
            }
            //对公客户
            //对私客户
            //全部客户
            else if(no.equals("214") || no.equals("217") || no.equals("229")){
                models = sqlManager.lambdaQuery(WorkflowModel.class)
                        .andLike(WorkflowModel::getModelName, "%资料收集%")
                        .andEq(WorkflowModel::getOpen,true)
                        .select();
                List<WorkflowModel> finalModels1 = models;
                initPubMap(pubMap,uid,models);
                ret = ((PageImpl) ret).map(o -> {
                    Map<String,String> map = (Map<String, String>) o;
                    map.put("pubModelId","0");
                    map.put("pubModelName","");
                    if(C.notEmpty(finalModels1) && pubMap.get(finalModels1.get(0).getId())){
                        map.put("pubModelId", finalModels1.get(0).getId() + "");
                        map.put("pubModelName", finalModels1.get(0).getModelName());
                    }
                    return map;
                });
            }
            return Result.ok(ret);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("参数错误");
        }
    }

    private void initPubMap(final Map pubMap, final long uid, final List<WorkflowModel> models){
        Long[] ls = models.stream()
                .map(WorkflowModel::getId)
                .peek(l -> pubMap.put(l,false))
                .collect(Collectors.toList())
                .toArray(new Long[models.size()]);
        pubMap.putAll(backRpc.canPub(uid,ls));

//        String s=HttpRequest.sendGet("http://47.94.97.138/rpc/back/canPub", String.format("uid=%s&modelIds=%s", uid + "", Strings.join(Arrays.asList(ls), ',')));
//        System.out.println(s);
//        Object o = backRpc.canPub(uid,ls);
//        log.error(JSON.toJSONString(o));
        if(true)return;
//        pubMap.putAll(backRpc.canPub(uid,ls));
//        if(true) return;
        Map<Long,Boolean> map = (Map<Long, Boolean>) sqlManager.select("workflow.canPubOrPoint", Map.class, C.newMap(
                "uid", uid,
                "mid", ls
                )).stream().collect(Collectors.toMap(
                        item -> (Long)item.get("objectId"),
                        item -> (int)item.getOrDefault("pub",0) > 0 || (int)item.getOrDefault("point",0) > 0
                ));
        pubMap.putAll(map);
    }
    private void initLoanMap(final Map<String,String> loanMap){
        Map<String,String> map = sqlManager.select("task.selectModelName",Map.class, C.newMap(
                "v0", loanMap.keySet()
        )).stream().collect(Collectors.toMap(
               item -> (String)item.getOrDefault("LOAN_ACCOUNT",""),
                item -> (String)item.getOrDefault("MODEL_NAME","")
        ));
        loanMap.putAll(map);
    }

//    private User findUserById(final long uid) {
//        User template = new User();
//        template.id = uid;
//        User user = sqlManager.templateOne(template);
//        return user;
//    }

    /**
     * 你问住我了, 这太久远了我也不知道是啥意思了
     *
     * @param uid
     * @return
     */
    private Map getPermissionLimit(final User user) {
        if (null == user) {
            return null;
        }
        if (user.getSu()) {
            return C.newMap();
        }
//        List<Quarters> qs = (List<Quarters>) user.get("qs");
        List<Map> ps = sqlManager.select("user.selectPsByUser", Map.class, new HashMap() {{
            put("uid", user.getId());
            put("type", "DATA_SEARCH_CONDITION");
            put("userType", "ROLE");
        }});
        List<String> managerCode = new ArrayList<>();
        List<String> userCode = new ArrayList<>();
        List<String> finalManagerCode = managerCode;
        List<String> finalUserCode = userCode;

        //default permission by manager
        sqlManager.select("user.selectManagedDepartment", Department.class, C.newMap("uid", user.getId()))
                .stream()
                .map(Department::getAccCode)
                .forEach(managerCode::add);
//        qs.stream().filter(q -> q.manager)
//                .map(q -> (Department) q.get("dep"))
//                .filter($::isNotNull)
//                .map(dep -> (dep.accCode))
//                .forEach(managerCode::add);
        //default permission by user
        userCode.add(user.getAccCode());

        //external permission
        ps.stream().filter(p -> null != p.get("description"))
                .map(p -> (JSONArray) (JSON.parse(p.get("description").toString())))
                .map(arr -> arr.toJavaList(SearchConditionRule.class))
                .flatMap(List::stream)
                //暂时不要这个, 虽然将来还可能加回来
//                .filter(rule -> rule.targetType.equals(searchTargetType))
                .forEach(rule -> {
                    switch (rule.searchType) {
                        case FOR_DEPARTMENT:
                            finalManagerCode.add(rule.value);
                            break;

                        case FOR_USER:
                            finalUserCode.add(rule.value);
                            break;
                    }
                });
        String uuid = "!@#$";
        //去除无用数据
        managerCode.add(uuid);
        userCode.add(uuid);
        System.out.println("rilegou");
        return C.newMap(
                "dep", managerCode.stream()
                        .filter(S::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList())
                , "user", userCode.stream()
                        .filter(S::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList()));
    }

    /**
     * 别问我我也不知道为什么这么写啊
     *
     *
     * @param user
     * @param searchTargetType
     * @param ret
     * @param defaultFields
     * @return
     */
    private List getPermissionResultLimit(User user, SearchTargetType searchTargetType, List<Map<String,Object>> ret, List<String> defaultFields) {
        if (user.getSu()) {
            return ret;
        }
        List<Map> ps = sqlManager.select("user.selectPsByUser", Map.class, C.newMap(
                "uid", user.getId(),
                "type", "DATA_SEARCH_RESULT",
                "userType", "ROLE"
        ));
        Set<String> fields = ps.stream()
                .filter(p -> $.isNotNull(p.get("description")))
                .map(p -> (JSONArray) JSON.parse(p.get("description").toString()))
                .map(arr -> arr.toJavaList(SearchResultRule.class))
                .flatMap(List::stream)
                .filter(item -> item.searchTargetType.equals(searchTargetType))
                .flatMap(item -> S.fastSplit(item.value, "\n").stream())
                .filter(S::isNotBlank)
                .map(item -> item.trim())
                .collect(Collectors.toSet());
        fields.addAll(defaultFields);

        for (Map<String, Object> map : ret) {
            Iterator it = map.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next();
                if(!fields.contains(entry.getKey())){
                    it.remove();
                }
            }
        }
        return ret;
    }

//    private boolean canPub(final long mid, final long uid){
//        Map<String,Integer> map = sqlManager.selectSingle("workflow.canPub", C.newMap("modelId", mid, "uid",uid) , Map.class);
//        return map.getOrDefault("num", 0) > 0;
//    }
//
//    private String getAutoTaskModelName(final String loanAccount){
//        List<Map> list = sqlManager.select("task.selectModelName", Map.class, C.newMap("v0", loanAccount));
//        if(C.empty(list)){
//            return "";
//        }
//        return (String) list.get(0).getOrDefault("MODEL_NAME","");
//    }


    public static class SearchConditionRule {
        //查询规则
        public SearchType searchType;
        //授权对象
        public SearchTargetType targetType;
        //约束类型
        public SearchValueType valueType;
        //约束值
        public String value;
    }

    public static class SearchResultRule {
        //授权对象
        public SearchTargetType searchTargetType;
        //限制字段
        public String value;
    }

    public enum SearchType {
        FOR_DEPARTMENT,
        FOR_USER,
        //不做限制
//        UNLIMITED
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


class HttpRequest {
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}