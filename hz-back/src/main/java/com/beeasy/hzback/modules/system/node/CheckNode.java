package com.beeasy.hzback.modules.system.node;

import com.alibaba.druid.util.StringUtils;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeAttributeDao;
import com.beeasy.common.entity.User;
import com.beeasy.common.entity.WorkflowNodeAttribute;
import com.beeasy.common.entity.WorkflowNodeInstance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.beetl.core.misc.NumberUtil;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class CheckNode extends BaseNode{

    @ApiModelProperty(required = true)
    @NotEmpty(message = "问题不能为空")
    String question;

    @ApiModelProperty(hidden = true)
    String key = "fieldName";
    @ApiModelProperty(hidden = true)
    String ps = "ps";

    @ApiModelProperty(required = true)
    @Min(value = 1,message = "审批人数格式错误")
    @NotNull(message = "审批人数不能为空")
    Integer count = 1;

    @ApiModelProperty(required = true)
    @NotNull(message = "状态不能为空")
    @Valid
    protected ArrayList<CheckNodeState> states;


    public CheckNode(Map<String,Object> v) {
        states = new ArrayList<>();

        if (v.containsKey("count")) {
            setCount(NumberUtils.toInt(String.valueOf(v.get("count"))));
        }
        if (v.containsKey("ps")) {
            setPs(String.valueOf(v.get("ps")));
        }
        if (v.containsKey("fieldName")) {
            setKey(String.valueOf(v.get("fieldName")));
        }
        if (v.containsKey("question")) {
            setQuestion(String.valueOf(v.get("question")));
        }

        //状态机
        if (v.containsKey("states")) {
            ((List<Map>) (v.get("states"))).forEach((vv) -> {
                CheckNodeState state = new CheckNodeState(
                        String.valueOf(vv.get("item")),
                        (Integer) vv.get("condition"),
                        String.valueOf(vv.get("behavior"))
                );
                states.add(state);
            });
        }
    }



    @Override
    public void submit(User user, WorkflowNodeInstance wNInstance, Map data, IWorkflowNodeAttributeDao attributeDao) {
        String item = String.valueOf(data.get(getKey()));
        String ps = String.valueOf(data.get(getPs()));
        //如果可选项不在选项里面, 那么无视
        boolean hasOption = getStates()
                .stream()
                .anyMatch(state -> state.getItem().equals(item));
        if (!hasOption) {
            return;
        }
        //每个审批节点只允许审批一次
        WorkflowNodeAttribute attribute = addAttribute(user,wNInstance,getKey(),item,question);
        attributeDao.save(attribute);

        //如果填写了审核说明
        //valueof会格式化null
        if(ps.equals("null")){
           ps = "";
        }
        if (!StringUtils.isEmpty(ps)) {
            attribute = addAttribute(user,wNInstance,getPs(),ps,"备注");
            attributeDao.save(attribute);
        }
    }




    //    @Getter
//    @Setter
//    public static class Content extends AbstractBaseEntity{
////        Set<String> items;
////        String passItem;
//
////        int pass = 1;
////        int fail = 1;
//    }


}
