package com.beeasy.hzback.modules.system.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.beeasy.hzback.entity.LoanManager;
import com.beeasy.mscommon.Result;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/api/excel")
@RestController
@Transactional
public class BackExcelController {

    @Autowired
    SQLManager sqlManager;

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
}
