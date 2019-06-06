package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.*;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Null;
import java.util.Map;

@Table(name = "T_INFO_COLLECT_LINK")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoLink extends ValidGroup {
    @Null(groups = Add.class)
    @AssignID("simple")
    Long id;
    Long insId;
    String loanAccount;


    /****************/
    @AssertTrue(message = "该台账已经和这个资料收集绑定, 请不要重复操作",groups = Add.class)
    protected boolean getZAdd(){
        return U.getSQLManager().lambdaQuery(InfoLink.class)
            .andEq(InfoLink::getInsId, insId)
            .andEq(InfoLink::getLoanAccount, loanAccount)
            .count() == 0;
    }

}
