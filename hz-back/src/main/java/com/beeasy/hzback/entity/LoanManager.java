package com.beeasy.hzback.entity;

import cn.hutool.core.date.DateUtil;
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

import javax.validation.constraints.*;
import java.util.Date;

@Table(name = "T_LOAN_MANAGER")
@Getter
@Setter
public class LoanManager extends TailBean implements ValidGroup {
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
    Integer fcz;
    Date fczDate;


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
                    object.put("mmhtjyrqDate", DateUtil.parse(object.getString("mmhtjyrqDate"), "yyyy-MM-dd hh:mm:ss"));
                }
                if(object.containsKey("fczDate")) {
                    object.put("fczDate", DateUtil.parse(object.getString("fczDate"), "yyyy-MM-dd hh:mm:ss"));
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

    public Object importExcel(SQLManager sqlManager, JSONObject params){
        return null;
    }


}
