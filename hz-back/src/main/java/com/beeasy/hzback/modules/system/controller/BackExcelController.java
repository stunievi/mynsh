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
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.entity.Definition;
import com.beeasy.hzback.entity.LoanManager;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.entity.User;
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
import java.util.Date;
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
    ) {
        long uid = AuthFilter.getUid();
        ThreadUtil.execAsync(() -> import_lm(uid, file));
        return Result.ok();
    }

    private void import_lm(long uid, MultipartFile file) {
        File temp = null;
        Date nowDate = new Date();
        LoanManager lm = new LoanManager();
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getId,uid).single();
        try {
            temp = File.createTempFile("temp_lm_", "");
            file.transferTo(temp);
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
                    if (count > 0) {
                        sqlManager.lambdaQuery(LoanManager.class)
                            .andEq(LoanManager::getLoanAccount, loanAccount)
                            .updateSelective(lm);
                    } else {
                        sqlManager.insert(lm);
                    }
                }
            }
            Log.log("批量导入按揭贷款信息 %d 条, 成功 %d 条, 失败 %d 条", total, total - failed, failed);
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, String.format("批量导入按揭贷款信息结果：总%d条，成功%d条，失败%d条", total, total - failed, failed), null);
            if(!"1".equals(uid)){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, String.format("操作人："+user.getTrueName()+"。批量导入按揭贷款信息结果：总%d条，成功%d条，失败%d条", total, total - failed, failed), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, "批量导入按揭贷款信息失败", null);
            if(!"1".equals(uid)){
                noticeService2.addNotice(SysNotice.Type.SYSTEM, 1, "操作人："+user.getTrueName()+"。批量导入按揭贷款信息失败", null);
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
        try {
            temp = File.createTempFile("temp_de_", "");
            file.transferTo(temp);
            ExcelReader reader = ExcelUtil.getReader(temp);
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
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入定义失败");
        } finally {
            if (temp != null) {
                temp.delete();
            }
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

}
