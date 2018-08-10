//package com.beeasy.hzback.modules.system.form;
//
//import com.beeasy.hzback.core.helper.SpringContextUtils;
//import com.beeasy.hzback.modules.system.dao.IRoleDao;
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.validator.constraints.NotEmpty;
//import org.hibernate.validator.constraints.Range;
//
//import javax.validation.constraints.AssertFalse;
//import javax.validation.constraints.AssertTrue;
//import javax.validation.constraints.NotNull;
//
//@Getter
//@Setter
//public class RoleRequest {
//    public interface add{}
//    public interface edit{}
//
//    @Range(min = 1, groups = edit.class)
//    long id = 0;
//
//    @NotEmpty(groups = {edit.class,add.class})
//    String name;
//
//    String info;
//
////    @NotNull(groups = {add.class,edit.class}, message = "是否删除必填")
//    boolean canDelete = true;
//
//    @AssertTrue(message = "已经有同名的角色", groups = {add.class,edit.class})
//    public boolean getCheckName(){
//        IRoleDao roleDao = SpringContextUtils.getBean(IRoleDao.class);
//        return roleDao.countByNameAndIdNot(name,id) == 0;
//    }
//
//    @Range(min = 0, max = 255, message = "排序在0-255之间", groups = {add.class,edit.class})
//    int sort;
//
//}
