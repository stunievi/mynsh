package com.beeasy.hzback.entity;

import com.beeasy.hzback.core.util.Log;
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
import java.util.Arrays;
import java.util.Map;

@Table(name = "T_MESSAGE_TEMPLATE")
@Getter
@Setter
public class MsgTmpl extends ValidGroup {
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
        if(id == 0){
            id = null;
        }
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

    @Override
    public Object onAfterAdd(SQLManager sqlManager, Object result) {
        Log.log("新增消息模板 %s", name);
        return super.onAfterAdd(sqlManager, result);
    }

    @Override
    public Object onAfterEdit(SQLManager sqlManager, Object object) {
        Log.log("编辑消息模板 %s", name);
        return super.onAfterEdit(sqlManager, object);
    }

    @Override
    public void onDelete(SQLManager sqlManager, Long[] id) {
        sqlManager.lambdaQuery(MsgTmpl.class)
            .andIn(MsgTmpl::getId, Arrays.asList(id))
            .select(MsgTmpl::getName)
            .forEach(e -> {
                Log.log("删除消息模板 %s", e.getName());
            });
        super.onBeforeDelete(sqlManager, id);
    }


    /*****/
    @Override
    public String onGetListSql(Map<String,Object> params) {
        return "system.查询消息模板列表";
    }


}
