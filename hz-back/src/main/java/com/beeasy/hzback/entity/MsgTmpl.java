package com.beeasy.hzback.entity;

import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.query.LambdaQuery;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Table(name = "T_MESSAGE_TEMPLATE")
@Getter
@Setter
public class MsgTmpl extends TailBean implements ValidGroup {
    @NotNull(groups = {ValidGroup.Edit.class})
    @AssignID("simple")
    private Long id;

    @NotBlank(message = "模板名不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    private String name;

    @NotBlank(message = "模板内容不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    private String template;

//    @NotBlank(message = "占位符语句不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    private String placeholder;

    @AssertTrue(message = "已经有同名模板", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    protected boolean isValidName(){
        SQLManager sqlManager = U.getSQLManager();
        LambdaQuery<MsgTmpl> query = sqlManager.lambdaQuery(MsgTmpl.class)
            .andEq(MsgTmpl::getName, name);
        //edit
        if(null != id){
            return query.andNotEq(MsgTmpl::getId, id).count() == 0;
        }
        //add
        else{
            return query.count() == 0;
        }
    }



    /*****/
    @Override
    public String onGetListSql(Map<String,Object> params) {
        return "system.查询消息模板列表";
    }


}
