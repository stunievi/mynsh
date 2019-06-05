package com.beeasy.hzback.entity;

import com.beeasy.hzback.core.util.Log;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.Map;

@Table(name = "T_DICT")
@Getter
@Setter
public class Dict extends ValidGroup {
    @AssignID("simple")
    @Null(groups = {Add.class})
    @NotNull(groups = {Edit.class})
    Long   id;
    @NotBlank(message = "字典分组不能为空", groups = {Add.class,Edit.class})
    String name;
    @NotBlank(message = "枚举名不能为空", groups = {Add.class,Edit.class})
    String vKey;
    @NotBlank(message = "枚举值不能为空", groups = {Add.class,Edit.class})
    String vValue;
    Date   lastModify;

    @Override
    public String onGetListSql(Map<String,Object> params) {
        //字典默认开放授权, 很多地方都要用到
//        User.AssertMethod("系统管理.字典管理");
//        Log.log("读取字典定义");
        return "system.searchDicts";
    }

    @Override
    public void onBeforeAdd(SQLManager sqlManager) {
        User.AssertMethod("系统管理.字典管理");
        long count = sqlManager.lambdaQuery(Dict.class)
            .andEq(Dict::getName, name)
            .andEq(Dict::getVKey, vKey)
            .andEq(Dict::getVValue, vValue)
            .count();
        Assert(count == 0, "已经有相同的字典值");
        lastModify = new Date();

        Log.log("新增字典: %s, %s, %s", name, vKey, vValue);
    }

    @Override
    public void onBeforeEdit(SQLManager sqlManager) {
        User.AssertMethod("系统管理.字典管理");
        long count = sqlManager.lambdaQuery(Dict.class)
            .andEq(Dict::getName, name)
            .andEq(Dict::getVKey, vKey)
            .andEq(Dict::getVValue, vValue)
            .andNotEq(Dict::getId, id)
            .count();
        Assert(count == 0, "已经有相同的字典值");
        lastModify = new Date();

        Log.log("编辑字典: %s, %s, %s", name, vKey, vValue);
    }

    @Override
    public void onBeforeDelete(SQLManager sqlManager, Long[] id) {
        User.AssertMethod("系统管理.字典管理");
    }

}
