package com.beeasy.hzback.modules.system.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.entity.*;
import com.beeasy.hzback.modules.system.service.LinkSeachService;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.KeyHolder;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@RequestMapping("/api/excel")
@RestController
@Transactional
public class BackExcelController {

    @Autowired
    SQLManager sqlManager;

    @Autowired
    DataSource dataSource;

    @Autowired
    NoticeService2 noticeService2;

    @Autowired
    LinkSeachService linkSeachService;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Value("${filepath.path}")
    String path;

    @RequestMapping("/auto/import")
    public Result importExcelData(
            MultipartFile file,
            String actType
    ){
        if(null==actType || actType.isEmpty()){
            return Result.error("未知导入类型");
        }
        long uid = AuthFilter.getUid();
        try {
            if("groupCusList".equals(actType)){
                // 集团客户
                File temp = File.createTempFile("group_cus_list", "");
                file.transferTo(temp);
                ThreadUtil.execAsync(() -> import_groupCusList(uid, temp));
            }else if("holderLink".equals(actType)){
                // 股东关联
                File temp = File.createTempFile("holder_link", "");
                file.transferTo(temp);
                ThreadUtil.execAsync(() -> import_holderLink(uid, temp));
            }else if("holderList".equals(actType)){
                // 股东
                File temp = File.createTempFile("holder_list", "");
                file.transferTo(temp);
                ThreadUtil.execAsync(() -> import_hl(uid, temp));
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(e.toString());
        }

    }

    // 导入股东关联
    private  void  import_holderLink(long uid, File temp){
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getId,uid).single();
        try {
            sqlManager.update("link.delete_02_holder_link");
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            HashMap data = new HashMap();
            int rowCount = reader.getRowCount();
            Date nowDate = new Date();
            // 用户导入
            data.put("data_flag", "02");
            data.put("add_time", nowDate);
            int total = 0;
            JSONArray dataList = new JSONArray();
            JSONObject extParam = linkSeachService.creatLinkParam("14", uid);
            for (int i = 1; i < rowCount; i++) {
                List<Object> row = reader.readRow(i);
                if(row.size() > 10){
                    linkSeachService.writeLinkData(extParam, row);
                    ++total;
                    long id = IdUtil.createSnowflake(0,0).nextId();
                    data.put("id", id);
                    // 0 序号
                    data.put("related_name", row.get(1));
                    data.put("cert_code", "");
                    // 3 一级支行
                    // 4 股东
                    // 5 股东身份证号
                    // 6 股东持股
                    data.put("link_rule", "");// 7 关联类型
                    data.put("remark_1", "");
                    data.put("remark_2", "");
                    data.put("remark_3", "");
                    sqlManager.insert("link.insert_holder_link", data, null, "id");
                    // TODO::
                    RelatedPartyList d = sqlManager.single(RelatedPartyList.class, id);
                    JSONObject item = (JSONObject) JSON.toJSON(d);
                    JSONObject insertResData = new JSONObject();
                    item.forEach((k,v)->{
                        insertResData.put(U.camelToUnderline(k).toUpperCase(), v);
                    });
                    dataList.add(insertResData);
                }
            }
            linkSeachService.writeLinkData(extParam, false);
            // 发送MQ
//            linkImportMQSend("14", dataList, uid);
            linkImportMQSend(extParam);
        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "导入股东关联信息失败", null);
            if(1!= uid){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, "执行人："+user.getTrueName()+"。导入股东关联信息失败", null);
            }
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }

    // 导入集团客户
    private  void  import_groupCusList(long uid, File temp){
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getId,uid).single();
        try {
            sqlManager.update("link.delete_02_group_cus");
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            HashMap data = new HashMap();
            Date nowDate = new Date();
            // 用户导入
            data.put("data_flag", "02");
            data.put("add_time", nowDate);
            int total = 0;
            JSONArray dataList = new JSONArray();
            List<String> keys = Arrays.asList(
                "cus_name",
                "cert_code",
                "link_name",
                "link_cert_code",
                "link_rule",
                "remark_1",
                "remark_2",
                "remark_3");
            for (int i = 1; i < reader.getRowCount(); i++) {
                List<Object> row = reader.readRow(i);
                if(row.size() > 8){
                    ++total;
                    long id = IdUtil.createSnowflake(0,0).nextId();
                    data.put("id", id);
                    IntStream.range(0, keys.size()).forEach(index -> data.put(keys.get(index), String.valueOf(row.get(index+1)).trim()) );
                    sqlManager.insert("link.insert_group_cus", data, null, "id");
                    List<JSONObject> list = sqlManager.select("link.search_group_cus_list", JSONObject.class, C.newMap("ID", id));
                    // TODO::
                    JSONObject findData = new JSONObject();
                    list.get(0).forEach((k,v)->{
                        findData.put(U.camelToUnderline(k).toUpperCase(), v);
                    });
                    dataList.add(findData);
                }
            }
            // 发送MQ
            linkImportMQSend("13", dataList, uid);

            Log.log("批量导入集团客户信息 %d 条", total);
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, String.format("批量导入集团客户信息：总%d条", total), null);
            if(1!= uid){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, String.format("执行人："+user.getTrueName()+"。批量导入集团客户信息：总%d条", total), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "导入集团客户信息失败", null);
            if(1!= uid){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, "执行人："+user.getTrueName()+"。导入集团客户信息失败", null);
            }
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }

    private void linkImportMQSend(JSONObject jsonObject){
        ActiveMQQueue mqQueue = new ActiveMQQueue("link-infos-request");
        jmsMessagingTemplate.convertAndSend(mqQueue , new LinkSeachService.LinkRequest(
                jsonObject.getString("OrderId"),
                jsonObject.getString("Sign"),
                (File) jsonObject.get("zipPath")
        ));
        this.logLink(jsonObject.getString("Sign"), jsonObject.getLong("uid"));
    }

    private void logLink(
            String sign,
            long uid
    ){
        if("14".equals(sign)){
            Log.logByUid("系统将【股东关联清单】发送至MQ", uid);
        }else if ("13".equals(sign)){
            Log.logByUid("系统将【集团客户清单】发送至MQ", uid);
        }else if ("12".equals(sign)){
            Log.logByUid("系统将【股东清单】发送至MQ", uid);
        }else if ("11".equals(sign)){
            Log.logByUid("系统将【关联方清单】发送至MQ", uid);
        }
    }

    private void linkImportMQSend(String sign, JSONArray jsonArray, long uid){
        // 11：关联方清单；12：股东清单；13：集团客户清单；14：股东关联清单；
        ActiveMQQueue mqQueue = new ActiveMQQueue("link-infos-request");
        // 按格式封装指令格式放入MQ
        JSONObject result = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
        Date nowDate = new Date();
        result.put("OrderId", "fzsys_" + df.format(nowDate));
        result.put("Sign", sign);
        result.put("Content", jsonArray);
        jmsMessagingTemplate.convertAndSend(mqQueue, result.toString());
        this.logLink(sign, uid);
    }

    @JmsListener(destination = "link-infos-request")
    public void linkInfosRequest(Object MQReq) throws IOException, JMSException {
        if (MQReq instanceof TextMessage) {
            System.out.println(((TextMessage) MQReq).getText());
        }else if (MQReq instanceof BlobMessage) {
            String file = IO.readContentAsString(((BlobMessage) MQReq).getInputStream());
            System.out.println(file);
        }
    }


    @RequestMapping("/loanmanager/import")
    public Result importLM(
            MultipartFile file
    ) {
        long uid = AuthFilter.getUid();
        try {
            File temp = File.createTempFile("temp_lm_", "");
            file.transferTo(temp);
            ThreadUtil.execAsync(() -> import_lm(uid, temp));
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.ok();
    }

    private void import_lm(long uid, File temp) {
//        File temp = null;
        Date nowDate = new Date();
        LoanManager lm = new LoanManager();
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getId,uid).single();
        try {
//            temp = File.createTempFile("temp_lm_", "");
//            file.transferTo(temp);
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            //skip first row
            int total = 0;
            int failed = 0;
            for (int i = 1; i < reader.getRowCount(); i++) {
                total++;
                String loanAccount;
                try {
                    loanAccount = String.valueOf(reader.readCellValue(1, i)).trim();
                } catch (Exception e) {
                    failed++;
                    continue;
                }
                Object o2 = reader.readCellValue(2, i);
                Object o3 = reader.readCellValue(3, i);
                Object o4 = reader.readCellValue(4, i);
                Object o5 = reader.readCellValue(5, i);
                Object o6 = reader.readCellValue(6, i);
                Object o7 = reader.readCellValue(7, i);
                Object o8 = reader.readCellValue(8, i);
                if (S.notBlank(loanAccount)) {
                    try {
                        DateTime d2 = null;
                        try {
                            d2 = (DateTime) o2;
                        } catch (Exception e) {
//                            continue;
                        }
                        String i3 = String.valueOf(o3).trim();
                        DateTime d4 = null;
                        //即使为空也可以导入
                        try {
                            d4 = (DateTime) o4;
                        } catch (Exception e) {
                        }
                        String i5 = String.valueOf(o5).trim();
                        String i6 = String.valueOf(o6);
                        String i7 = String.valueOf(o7);
                        String i8 = String.valueOf(o8);

                        lm.setLoanAccount(loanAccount);
                        if (d2 != null) {
                            lm.setMmhtjyrqDate(d2.toJdkDate());
                        }
                        lm.setFcz(S.notEmpty(i3) && S.neq(i3, "0") ? "1" : "0");
                        if (d4 != null) {
                            lm.setFczDate(d4.toJdkDate());
                        }
                        lm.setId(null);
                        lm.setReason(i5);
                        lm.setExplain(i6);
                        lm.setDeveloperFullName(i7);
                        lm.setLpFullName(i8);
                        lm.setLastModify(nowDate);
                        lm.setModifyUid(uid);

                    } catch (Exception e) {
                        failed++;
                        continue;
                    }

                    long count = sqlManager.lambdaQuery(LoanManager.class)
                        .andEq(LoanManager::getLoanAccount, loanAccount)
                        .count();
                    /*if (count > 0) {
                        sqlManager.lambdaQuery(LoanManager.class)
                            .andEq(LoanManager::getLoanAccount, loanAccount)
                            .updateSelective(lm);
                    } else {
                        sqlManager.insert(lm);
                    }*/
                    if (count > 0) {
                        sqlManager.lambdaQuery(LoanManager.class)
                                .andEq(LoanManager::getLoanAccount, loanAccount)
                                .delete();
                    }
                    sqlManager.insert(lm);
                }
            }
            Log.log("批量导入按揭贷款信息 %d 条, 成功 %d 条, 失败 %d 条", total, total - failed, failed);
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, String.format("批量导入按揭贷款信息结果：总%d条，成功%d条，失败%d条", total, total - failed, failed), null);
            if(1!= uid){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, String.format("执行人："+user.getTrueName()+"。批量导入按揭贷款信息结果：总%d条，成功%d条，失败%d条", total, total - failed, failed), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "批量导入按揭贷款信息失败", null);
            if(1!= uid){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, "执行人："+user.getTrueName()+"。批量导入按揭贷款信息失败", null);
            }
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }

    @RequestMapping("/defination/import")
    public Result importDefination(
            MultipartFile file
    ) {
        File temp = null;
        File dir = null;
        try {

            dir = new File(path);
            dir.getParentFile().mkdirs();
            file.transferTo(dir);

            ExcelReader reader = ExcelUtil.getReader(dir);
            reader.setSheet(0);
            JSONObject object = new JSONObject();

            for (int i = 1; i < reader.getRowCount(); i++) {
                String en = (String) reader.readCellValue(1, i);
                String cn = (String) reader.readCellValue(0, i);
                object.put(cn, en);
            }
            sqlManager.lambdaQuery(Definition.class)
                    .andEq(Definition::getName, "accloan")
                    .delete();
            Definition definition = new Definition();
            definition = new Definition();
            definition.setName("accloan");
            definition.setConfig(object.toJSONString());
            sqlManager.insert(definition, true);
            Log.log("导入信贷中间表转换定义文件");

//            String filePath="F:\\";
//            String filePath = "/home/fzsysafter/app/";
//            String fileName = "dingyi.xlsx";


            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            if(null != dir){
                dir.delete();
            }
            return Result.error("导入定义失败");
        } finally {
//            if (temp != null) {
//                temp.delete();
//            }
        }

    }


    @RequestMapping("/xindai/import")
    public Result importXinDai(
            MultipartFile file
    ) {
        long uid = AuthFilter.getUid();
        new Thread(() -> {
            import_xin_dai(uid, file);
        }).start();
        return Result.ok();
    }

    private void import_xin_dai(long uid, MultipartFile file) {
        //解压文件
        try (
                InputStream is = file.getInputStream();
        ) {
            //读取定义
            Definition definition = sqlManager.lambdaQuery(Definition.class)
                    .andEq(Definition::getName, "accloan")
                    .single();
            if (definition == null) {
                throw new Exception();
            }
            JSONObject map = JSON.parseObject(definition.getConfig());
            byte[] bs;
            if (file.getOriginalFilename().endsWith(".del")) {
                bs = IoUtil.readBytes(is);
            } else if(file.getOriginalFilename().endsWith(".gz")){
                bs = ZipUtil.unGzip(is);
            } else {
                throw new IOException();
            }
            int pos = 0;
            scan:
            {
                while (pos < bs.length) {
                    switch (bs[pos]) {
                        case '\n':
                            break scan;
                    }
                    pos++;
                }
            }
            //读取第一行
            String[] dfs = new String(bs, 0, pos, CharsetUtil.GBK).split(",");
            pos++;
            //读取第二行
            StringBuilder sb = new StringBuilder();
            sb.append("insert into RPT_M_RPT_SLS_ACCT (");
            List<Integer> poss = new ArrayList<>();
            for (int i = 0; i < dfs.length; i++) {
                String key = map.getString(dfs[i]);
                if (S.empty(key)) {
                    continue;
                }
                sb.append(key);
                sb.append(",");
                poss.add(i);
            }
            sb.append("SOC_NO,DATA_CENTER_NO,CREUNIT_NO,SRC_SYS_CD");
            sb.append(")values(");
            String prefix = sb.toString();
//            for (Integer integer : poss) {
//                sb.append("?,");
//            }
//            sb.deleteCharAt(sb.length() - 1);
//            sb.append(")");

            int lastPos = pos;
            int spaceStart = -1;
            boolean openTag = false;
            List<String> sqls = new ArrayList<>();
            List<String> values = new ArrayList<>();
            while (pos < bs.length) {
                switch (bs[pos]) {
                    case ',':
                        if (openTag) {
                            break;
                        }
                        values.add(formatValue(new String(bs, lastPos, pos - lastPos, CharsetUtil.GBK)));
                        lastPos = pos + 1;
                        break;

                    case '\n':
                        values.add(formatValue(new String(bs, lastPos, pos - lastPos, CharsetUtil.GBK)));
//                        sb.append(",");
//                        sb.append(")");

                        for (Integer integer : poss) {
                            sb.append(values.get(integer));
                            sb.append(",");
                        }
                        values.clear();
                        //补充多余的字段
                        sb.append("'003','021','0801','PRT')");
                        sqls.add(sb.toString());
                        lastPos = pos + 1;

                        sb.setLength(0);
                        sb.append(prefix);
                        break;

                    case '"':
                        openTag = !openTag;
                        break;

                    case '\\':
                        pos++;
                        break;

                }
                pos++;
            }

            Connection fake = null;
            Statement fakeStmt = null;
            try {
                Connection connection = fake = dataSource.getConnection();
                Statement stmt = fakeStmt = connection.createStatement();
                connection.setAutoCommit(false);
                stmt.executeUpdate("delete from RPT_M_RPT_SLS_ACCT");
                int limit = 0;
                int block = 1;
                for (String sql : sqls) {
//                    try{
//                        stmt.executeUpdate(sql);
//                    }
//                    catch (Exception e){
//                        System.out.println(sql);
//                        throw e;
//                    }
                    if (++limit > 1000) {
                        stmt.executeBatch();
                        stmt.clearBatch();
                        System.out.printf("dealed %d", (limit - 1) * (block));
                        block++;
                        limit = 0;
                    }
                    stmt.addBatch(sql);
                }

                stmt.executeBatch();
                connection.commit();

                Log.log("导入信贷中间表数据文件");
            } catch (Exception e) {
                if (fake != null) {
                    try {
                        fake.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                throw e;
            } finally {
                if (fakeStmt != null) {
                    try {
                        fakeStmt.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (fake != null) {
                    try {
                        fake.setAutoCommit(true);
                        fake.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "导入信贷中间表成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "导入信贷中间表失败", null);
        }
    }


    private String formatValue(String value) {
        if (S.empty(value)) {
            return null;
        } else if (value.startsWith("+")) {
            return new BigDecimal(value).toString();
        } else {
            return value.replaceAll("\\s*\"", "'");
        }
    }


//    public Result importL123213List(
//            MultipartFile file
//    ){
//        long uid = AuthFilter.getUid();
//        try {
//            File temp = File.createTempFile("temp_lm_", "");
//            file.transferTo(temp);
//            ThreadUtil.execAsync(() -> import_lm1(uid, temp));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Result.ok();
//    }
    // 任务产生条件导入
    @RequestMapping("/sysVar/import")
    public Result import_lm1(MultipartFile file) {
        File temp = null;
        try {
            temp = File.createTempFile("temp_lm_", "");
            file.transferTo(temp);
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            //skip first row
            int total = 0;
            int failed = 0;
            if(!"*产品编号".equals(String.valueOf(reader.readCellValue(0, 0)).trim())){
                return Result.error();
            }

            List<SysVar> sysVars = new ArrayList<>();
            for (int i = 1; i < reader.getRowCount(); i++) {
                total++;
                String proNumber;
                try {
                    proNumber = String.valueOf(reader.readCellValue(0, i)).trim();
                } catch (Exception e) {
                    failed++;
                    continue;
                }

                Object o2 = reader.readCellValue(1, i);
                String i2 = String.valueOf(o2).trim();
                Object o3 = reader.readCellValue(2, i);
                Object o4 = reader.readCellValue(3, i);
                Object o5 = reader.readCellValue(4, i);
                if (S.notBlank(proNumber) && null != o2) {
                    try {

                        String i3 = String.valueOf(o3).trim();
                        String i4 = String.valueOf(o4).trim();

                        String i5 = String.valueOf(o5).trim();
                        String BIZ_TYPE = "ACC_"+(total-1)+"_BIZ_TYPE";
                        String LOAN_CHECK = "ACC_"+(total-1)+"_LOAN_CHECK";
                        saveSysVar(BIZ_TYPE, proNumber, sysVars);
                        saveSysVar(LOAN_CHECK, i2, sysVars);

                        if(null != o3){
                            String LOAN_AMOUNT_MIN = "ACC_"+(total-1)+"_LOAN_AMOUNT_MIN";
                            saveSysVar(LOAN_AMOUNT_MIN, i3, sysVars);
                        }
                        if(null != o4){
                            String LOAN_AMOUNT_MAX = "ACC_"+(total-1)+"_LOAN_AMOUNT_MAX";
                            saveSysVar(LOAN_AMOUNT_MAX, i4, sysVars);
                        }
                        if(null != o5){
                            String EXPECT_DAY = "ACC_"+(total-1)+"_EXPECT_DAY";
                            saveSysVar(EXPECT_DAY, i5, sysVars);
                        }

                    } catch (Exception e) {
                        failed++;
                        continue;
                    }
                }
            }
            sqlManager.lambdaQuery(SysVar.class)
                    .andLike(SysVar::getVarName, "ACC_%")
                    .delete();
            sqlManager.insertBatch(SysVar.class, sysVars);
            Log.log("导入贷后检查任务产生规则 %d 条, 成功 %d 条, 失败 %d 条", total, total - failed, failed);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (temp != null) {
                temp.delete();
            }
        }
        return Result.ok();
    }

    private void saveSysVar(String name, String value, List<SysVar> sysVars){

        SysVar sysVar = new SysVar();
        sysVar.setId(null);
//        sysVar.setId(U.getSnowflakeIDWorker().nextId());
        sysVar.setVarValue(value);
        sysVar.setVarName(name);
        sysVars.add(sysVar);
    }

    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public Result querySys(){
        List<SysVar> sysVarList= sqlManager.lambdaQuery(SysVar.class).andLike(SysVar::getVarName,"ACC_%").select();

        JSONArray resp = new JSONArray();
//        int i = 0;
        for (int i=0;i<sysVarList.size();i++){
            JSONObject jsonObject = new JSONObject();
            for (SysVar list : sysVarList) {
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(list.getVarName());
//                System.out.println( m.replaceAll("").trim());
                if(i == Integer.parseInt(m.replaceAll("").trim())){
                    jsonObject.put("SERIAL_NUMBER",i+1);
                    if("ACC__BIZ_TYPE".equals(list.getVarName().replaceAll("\\d+", ""))){
                        List<JSONObject> lists = sqlManager.select("accloan.查询产品名称", JSONObject.class, C.newMap("prdCode",list.getVarValue()));
                        if(null != lists && !lists.isEmpty()){
                            jsonObject.put("PRO_NAME", lists.get(0).getString("PRD_NAME"));
                        }
                    }
                    jsonObject.put(list.getVarName().replaceAll("\\d+", ""), list.getVarValue());
                }
            }
            if(!jsonObject.isEmpty()){
                resp.add(jsonObject);
            }
        }
        return Result.ok(resp);
    }

    /**
     * 股东清单数据导入
     */
    private void import_hl(long uid, File temp) {
        Date nowDate = new Date();
        RelatedPartyList entity = new RelatedPartyList();
        ShareHolderList gdEntity = new ShareHolderList();
        try {
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            //skip first row
            int total = 0;
            int failed = 0;
            if(reader.getRowCount()>0){
//                sqlManager.lambdaQuery(RelatedPartyList.class)
//                        .delete();

                sqlManager.lambdaQuery(ShareHolderList.class)
                        .delete();
            }
            for (int i = 1; i < reader.getRowCount(); i++) {
                total++;
                String i1;
                try {
                    i1 = String.valueOf(reader.readCellValue(1, i)).trim();
                } catch (Exception e) {
                    failed++;
                    continue;
                }
                String i5 = "";
                String i16 = "";
                if (S.notBlank(i1)) {
                    try {
                        String i2 = String.valueOf(reader.readCellValue(2, i)).trim();
                        String i3 = String.valueOf(reader.readCellValue(3, i)).trim();
                        String i4 = String.valueOf(reader.readCellValue(4, i));

                        i5 = String.valueOf(reader.readCellValue(5, i)).trim();
                        String i6 = String.valueOf(reader.readCellValue(6, i));
                        String i7 = String.valueOf(reader.readCellValue(7, i));
                        String i8 = String.valueOf(reader.readCellValue(8, i));
                        String i9 = String.valueOf(reader.readCellValue(9, i)).trim();
                        String i10 = String.valueOf(reader.readCellValue(10, i)).trim();
                        String i11 = String.valueOf(reader.readCellValue(11, i)).trim();
                        String i12 = String.valueOf(reader.readCellValue(12, i)).trim();
                        String i13 = String.valueOf(reader.readCellValue(13, i)).trim();
                        String i14 = String.valueOf(reader.readCellValue(14, i)).trim();
                        String i15 = String.valueOf(reader.readCellValue(15, i)).trim();
                        i16 = String.valueOf(reader.readCellValue(16, i)).trim();
                        String i17 = String.valueOf(reader.readCellValue(17, i)).trim();
                        String i18 = String.valueOf(reader.readCellValue(18, i)).trim();
                        String i19 = String.valueOf(reader.readCellValue(19, i)).trim();
                        String i20 = String.valueOf(reader.readCellValue(20, i)).trim();
                        String i21 = String.valueOf(reader.readCellValue(21, i)).trim();
                        String i22 = String.valueOf(reader.readCellValue(22, i)).trim();
                        String i23 = String.valueOf(reader.readCellValue(23, i)).trim();
                        String i24 = String.valueOf(reader.readCellValue(24, i)).trim();
                        String i25 = String.valueOf(reader.readCellValue(25, i)).trim();

                        entity.setId(null);
                        entity.setAddTime(nowDate);
                        entity.setRelatedName(i5);
                        entity.setLinkRule("12.1");
                        entity.setLinkInfo(i6);
                        entity.setCertCode(i16);
                        entity.setRemark_1("");
                        entity.setRemark_2(i18);
                        entity.setRemark_3("");
                        entity.setDataFlag("01");

                        gdEntity.setId(null);
                        gdEntity.setAddTime(nowDate);
                        gdEntity.setOpenBrId(i1);
                        gdEntity.setOpenBrName(i2);
                        gdEntity.setOpenDate(i3);
                        gdEntity.setGdType(i4);
                        gdEntity.setCusName(i5);
                        gdEntity.setGjpzhm(i6);
                        gdEntity.setGpzh(i7);
                        gdEntity.setZgg(i8);
                        gdEntity.setTzg(i9);
                        gdEntity.setGp(i10);
                        gdEntity.setGfhj(i11);
                        gdEntity.setJszh(i12);
                        gdEntity.setFhzhkhjg(i13);
                        gdEntity.setFhzhzt(i14);
                        gdEntity.setCertType(i15);
                        gdEntity.setCertCode(i16);
                        gdEntity.setIndivHouhRegAdd(i17);
                        gdEntity.setPostAddr(i18);
                        gdEntity.setPhone(i19);
                        gdEntity.setMobile(i20);
                        gdEntity.setIndivComPhn(i21);
                        gdEntity.setFphone(i22);
                        gdEntity.setFaxCode(i23);
                        gdEntity.setIsTurn(i24);
                        gdEntity.setGqdjtgqqFlg(i25);

                    } catch (Exception e) {
                        failed++;
                        continue;
                    }

                    long count = sqlManager.lambdaQuery(RelatedPartyList.class)
                            .andEq(RelatedPartyList::getRelatedName, i5)
                            .andEq(RelatedPartyList::getCertCode, i16)
                            .count();
                    if(count>0){
                        sqlManager.lambdaQuery(RelatedPartyList.class)
                                .andEq(RelatedPartyList::getRelatedName, i5)
                                .andEq(RelatedPartyList::getCertCode, i16)
                                .delete();
                    }
                    sqlManager.insert(entity);

                    sqlManager.insert(gdEntity);

                }
            }
//            Log.log("批量导入股东清单 %d 条, 成功 %d 条, 失败 %d 条", total, total - failed, failed);
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, String.format("批量导入股东清单结果：总%d条，成功%d条，失败%d条", total, total - failed, failed), null);


        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "批量导入股东清单失败", null);

        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }
    /**
     * 关联方清单数据导入
     */
    @RequestMapping("/partyList/import")
    public Result importRP(
            MultipartFile file
    ) {
        long uid = AuthFilter.getUid();
        try {
            File temp = File.createTempFile("temp_lm_", "");
            file.transferTo(temp);
            ThreadUtil.execAsync(() -> import_party(uid, temp));
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.ok();
    }

    private void import_party(long uid, File temp) {
        Date nowDate = new Date();
        RelatedPartyList entity = new RelatedPartyList();
        try {
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            //skip first row
            int total = 0;
            int failed = 0;
            if(reader.getRowCount()>0){
                sqlManager.lambdaQuery(RelatedPartyList.class)
                        .andNotEq(RelatedPartyList::getLinkRule,"12.1")
                        .delete();
            }
            for (int i = 3; i < reader.getRowCount(); i++) {
                String i1;
                try {
                    i1 = String.valueOf(reader.readCellValue(1, i)).trim();
                } catch (Exception e) {
                    failed++;
                    continue;
                }
                if (S.notBlank(i1)) {
                total++;
                    try {
                        String i2 = String.valueOf(reader.readCellValue(2, i)).trim();
                        String i3 = String.valueOf(reader.readCellValue(3, i)).trim();
                        String i4 = String.valueOf(reader.readCellValue(4, i));
                        String i5 = String.valueOf(reader.readCellValue(5, i)).trim();
                        String i6 = String.valueOf(reader.readCellValue(6, i));
                        String i7 = String.valueOf(reader.readCellValue(7, i));

                        entity.setId(null);
                        entity.setAddTime(nowDate);
                        entity.setRelatedName(i1);
                        String[] rule = i2.split("\\-");
                        if(rule.length>=2){
                            entity.setLinkRule(rule[0]);
                        }else{
                            entity.setLinkRule("");
                        }
                        entity.setLinkInfo(i3);
                        entity.setCertCode(i4);
                        entity.setRemark_1(i5);
                        entity.setRemark_2(i6);
                        entity.setRemark_3(i7);
                        entity.setDataFlag("01");
                    } catch (Exception e) {
                        failed++;
                        continue;
                    }

                    sqlManager.insert(entity);
                }
            }
//            Log.log("批量导入关联清单 %d 条, 成功 %d 条, 失败 %d 条", total, total - failed, failed);
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, String.format("批量导入关联清单结果：总%d条，成功%d条，失败%d条", total, total - failed, failed), null);
        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "批量导入关联清单失败", null);

        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }

}
