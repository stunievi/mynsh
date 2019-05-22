package com.beeasy.hzback.modules.system.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.Definition;
import com.beeasy.hzback.entity.LoanManager;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.AssignID;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping("/loanmanager/import")
    public Result importLM(
        MultipartFile file
    ){
        File temp = null;
        LoanManager lm = new LoanManager();
        try {
            temp = File.createTempFile("temp_lm_", "");
            file.transferTo(temp);
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            //skip first row
            int total = 0;
            int failed = 0;
            for(int i = 1; i < reader.getRowCount(); i++){
                total++;
                String loanAccount;
                try{
                    loanAccount = String.valueOf(reader.readCellValue(0, i)).trim();
                }
                catch (Exception e){
                    failed++;
                    continue;
                }
                Object o2 = reader.readCellValue(1, i);
                Object o3 = reader.readCellValue(2, i);
                Object o4 = reader.readCellValue(3, i);
                if (S.notBlank(loanAccount)) {
                    try{
                        DateTime d2 = (DateTime) o2;
                        String i3 = String.valueOf(o3).trim();
                        DateTime d4 = (DateTime) o4;
                        lm.setLoanAccount(loanAccount);
                        lm.setMmhtjyrqDate(d2.toJdkDate());
                        lm.setFcz(S.notEmpty(i3) && S.neq(i3, "0") ? "1" : "0");
                        lm.setFczDate(d4.toJdkDate());
                        lm.setId(null);
                    } catch (Exception e){
                        failed++;
                        continue;
                    }

                    long count = sqlManager.lambdaQuery(LoanManager.class)
                        .andEq(LoanManager::getLoanAccount, loanAccount)
                        .count();
                    if (count > 0) {
                        sqlManager.lambdaQuery(LoanManager.class)
                            .andEq(LoanManager::getLoanAccount, loanAccount)
                            .updateSelective(lm);
                    } else {
                        sqlManager.insert(lm);
                    }
                }
            }
            return Result.ok(
                C.newMap(
                    "total", total,
                    "success", total - failed,
                    "failed", failed
                )
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败");
        }
        finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }

    @RequestMapping("/defination/import")
    public Result importDefination(
        MultipartFile file
    ){
        File temp = null;
        try {
            temp = File.createTempFile("temp_de_", "");
            file.transferTo(temp);
            ExcelReader reader = ExcelUtil.getReader(temp);
            reader.setSheet(0);
            JSONObject object = new JSONObject();

            for(int i = 1; i < reader.getRowCount(); i++) {
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
            return Result.ok();
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("导入定义失败");
        }
        finally {
            if (temp != null) {
                temp.delete();
            }
        }

    }


    @RequestMapping("/xindai/import")
    public Result importXinDai(
        MultipartFile file
    ){
        long uid = AuthFilter.getUid();
        new Thread(() -> {
            import_xin_dai(uid, file);
        }).start();
        return Result.ok();
    }

    private void import_xin_dai(long uid, MultipartFile file){
        //解压文件
        try(
            InputStream is = file.getInputStream();
        ){
            //读取定义
            Definition definition = sqlManager.lambdaQuery(Definition.class)
                .andEq(Definition::getName, "accloan")
                .single();
            if (definition == null) {
                throw new Exception();
            }
            JSONObject map = JSON.parseObject(definition.getConfig());

            byte[] bs = ZipUtil.unGzip(is);
            int pos = 0;
            scan:{
                while(pos < bs.length){
                    switch (bs[pos]){
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
            sb.append("insert into TEMP_RPT_M_RPT_SLS_ACCT (");
            List<Integer> poss = new ArrayList<>();
            for(int i = 0; i < dfs.length; i++){
                String key = map.getString(dfs[i]);
                if (S.empty(key)) {
                    continue;
                }
                sb.append(key);
                sb.append(",");
                poss.add(i);
            }
            sb.deleteCharAt(sb.length() - 1);
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
            while(pos < bs.length){
                switch (bs[pos]){
                    case ',':
                        if(openTag){
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
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append(")");
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
            try(
                Connection connection = fake = dataSource.getConnection();
                Statement stmt = connection.createStatement();
            ){
                connection.setAutoCommit(false);
                stmt.executeUpdate("delete from TEMP_RPT_M_RPT_SLS_ACCT");
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
                    if(++limit > 1000){
                        stmt.executeBatch();
                        stmt.clearBatch();
                        System.out.printf("dealed %d", (limit - 1) * (block));
                        block++;
                        limit = 0;
                    }
                    stmt.addBatch(sql);
                }
//                while((line = reader.readLine()) != null ){
//                    values.clear();
//                    int ptr = 0;
//                    int lastPos = 0;
//                    boolean openTag = false;
//                    while(ptr < line.length()){
//                        char c = line.charAt(ptr);
//                        switch (c){
//                            case ',':
//                                //如果仍在字符串内
//                                if(openTag){
//                                    break;
//                                }
//                                //
//                                String value = line.substring(lastPos, ptr);
//                                values.add(formatValue(value)) ;
//                                lastPos = ptr + 1;
//                                break;
//
//                            case '"':
//                                openTag = !openTag;
//                                break;
//
//                            case '\\':
//                                //无条件前进一步
//                                ptr++;
//                                break;
//                        }
//
//                        ptr++;
//                    }
//                    //补上最后一个
//                    String value = line.substring(lastPos);
//                    values.add(formatValue(value));
//                    sb.setLength(0);
//                    sb.append(prefix);
//                    for (Integer integer : poss) {
//                        sb.append(values.get(integer));
//                        sb.append(",");
////                        stmt.setObject(++i, values.get(integer));
//                    }
//                    sb.deleteCharAt(sb.length() - 1);
//                    sb.append(")");
//                    try{
//                        stmt.executeUpdate(sb.toString());
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }

//                    if(limit++ > 500){
//                        stmt.executeBatch();
//                        stmt.close();
//                        stmt = connection.createStatement();
//                        limit = 0;
//                        System.out.println("dealed 500");
//                    }
//                    stmt.addBatch(sb.toString());
//                }

                stmt.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
            catch (Exception e){
                if (fake != null) {
                    try {
                        fake.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                throw e;
            }

            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "导入信贷中间表成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "导入信贷中间表失败", null);
        }
    }


    private String formatValue(String value){
        if(S.empty(value)){
            return null;
        } else if(value.startsWith("+")) {
            return new BigDecimal(value).toString();
        }else{
            return value.replaceAll("\\s*\"", "'");
        }
    }

}
