package com.beeasy.hzback.modules.system.node;

import com.alibaba.druid.util.StringUtils;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
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
import java.util.Map;

//import org.beetl.core.misc.NumberUtil;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class CheckNode extends BaseNode{

    @ApiModelProperty
    @NotEmpty(message = "问题不能为空")
    String question;

    @ApiModelProperty(hidden = true)
    String key = "key";
    @ApiModelProperty(hidden = true)
    String ps = "ps";

    @ApiModelProperty
    @Min(value = 1,message = "审批人数格式错误")
    int count = 1;

    @ApiModelProperty(hidden = true)
    String type = "check";

    @ApiModelProperty
    @NotNull(message = "状态不能为空")
    @Valid
    private ArrayList<CheckNodeState> states;


    public CheckNode(String name, Map<String,Object> v) {
        super(name, "check", v);

        states = new ArrayList<>();

        if (v.containsKey("count")) {
            setCount(NumberUtils.toInt(String.valueOf(v.get("count"))));
        }
        if (v.containsKey("ps")) {
            setPs(String.valueOf(v.get("ps")));
        }
        if (v.containsKey("key")) {
            setKey(String.valueOf(v.get("key")));
        }
        if (v.containsKey("question")) {
            setQuestion(String.valueOf(v.get("question")));
        }

        //状态机
        if (v.containsKey("states")) {
            ((Map<String, Map>) (v.get("states"))).forEach((kk, vv) -> {
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
    public void submit(User user, WorkflowNodeInstance wNInstance, Map data) {
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
        addNode(user,wNInstance,getKey(),item);

        //如果填写了审核说明
        if (!StringUtils.isEmpty(ps)) {
            addNode(user,wNInstance,getPs(),ps);
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
