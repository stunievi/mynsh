package com.beeasy.hzback.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.$;
import org.osgl.util.S;

import javax.validation.constraints.*;
import java.io.File;
import java.util.Date;

@Table(name = "T_LOAN_MANAGER")
@Getter
@Setter
public class LoanManager extends ValidGroup {
    @Null(groups = ValidGroup.Add.class)
    @AssignID("simple")
    Long id;

    @AssignID
    @NotBlank(groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String loanAccount;

    @NotBlank(message = "姓名不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String name;

    @NotBlank(message = "类型不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String type;

    @NotBlank(message = "联系方式不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String phone;

    @NotBlank(message = "证件号码不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String code;

    Date lastModify;

    Date mmhtjyrqDate;
    String fcz;
    Date fczDate;
    String wczResult;
    String reason;
    String explain;
    String developerFullName;
    String lpFullName;
    String dshjyzxqk;
    Long modifyUid;


    @AssertTrue(message = "", groups = {Add.class,Edit.class})
    public boolean getZValid() {
        lastModify = new Date();
        //编辑限制, 只有自己管理的任务才可以编辑
         
        return true;
    }


    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action){
            case "set":
                //处理时间
                if(object.containsKey("mmhtjyrqDate")){
                    if(S.isNotBlank(object.getString("mmhtjyrqDate"))){
                        object.put("mmhtjyrqDate", DateUtil.parse(object.getString("mmhtjyrqDate"), "yyyy-MM-dd hh:mm:ss"));
                    }
                }
                if(object.containsKey("fczDate")) {
                    if(S.isNotBlank(object.getString("fczDate"))){
                        object.put("fczDate", DateUtil.parse(object.getString("fczDate"), "yyyy-MM-dd hh:mm:ss"));
                    }
                }
                LoanManager loanManager = $.map(object).to(getClass());
                valid(loanManager, Add.class);
                //删除
                sqlManager.lambdaQuery(LoanManager.class)
                    .andEq(LoanManager::getLoanAccount, loanManager.getLoanAccount())
                    .delete();
                sqlManager.insert(loanManager, true);
                return loanManager;

            case "get":
                break;
        }
        return null;
    }

//    public boolean importExcel(SQLManager sqlManager, File file){
//        return false;
//    }


}
