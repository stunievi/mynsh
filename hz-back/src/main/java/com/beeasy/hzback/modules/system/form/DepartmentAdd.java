//package com.beeasy.hzback.modules.system.form;
//
//import com.beeasy.hzback.core.helper.SpringContextUtils;
//import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import org.hibernate.validator.constraints.NotEmpty;
//import org.hibernate.validator.constraints.Range;
//
//import javax.validation.constraints.*;
//
//@ApiModel
//@Data
//public class DepartmentAdd {
//
//    @ApiModelProperty(value = "部门名称",required = true)
//    @NotEmpty(message = "部门名称不能为空")
//    private String name;
//
//    @ApiModelProperty(value = "部门父级ID,顶级部门请写0",required = true)
//    @Min(value = 0,message = "父级ID不能小于0")
//    private long parentId;
//
//    @ApiModelProperty(value = "部门描述")
//    private String info;
//
//    @Range(min = 0, max = 255)
//    int sort = 0;
//
//    String accCode = "";
//
//    @AssertTrue(message = "已经有同名部门")
//    public boolean isValidDepartment(){
//        if(0 == parentId){
//            return SpringContextUtils.getBean(IDepartmentDao.class).countByParentAndName(null,name) == 0;
//        }
//        else{
//            return SpringContextUtils.getBean(IDepartmentDao.class).countByParentIdAndName(parentId,name) == 0;
//        }
//    }
//}
