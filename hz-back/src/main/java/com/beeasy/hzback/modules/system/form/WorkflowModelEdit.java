//package com.beeasy.hzback.modules.system.form;
//
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.validator.constraints.Range;
//
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import java.util.ArrayList;
//import java.util.List;
//
//@ApiModel
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class WorkflowModelEdit {
//
//    @NotNull(message = "模型ID不能为空")
//    @ApiModelProperty(name = "id", value = "工作流模型ID")
//    Long id;
//
//    String name;
//
//    @ApiModelProperty(name = "info", value = "工作流描述")
//    String info;
//
//    @NotNull(message = "工作流开关不能为空")
//    @ApiModelProperty(name = "open",value = "是否打开工作流")
//    boolean open;
//
//    @Range(min = 1, max = 99999, message = "处理周期在1-99999天之间")
//    Integer processCycle;
////    List<Long> departmentIds = new ArrayList<>();
//}
