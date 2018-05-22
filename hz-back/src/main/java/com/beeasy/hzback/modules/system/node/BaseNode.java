package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.modules.system.dao.IWorkflowNodeAttributeDao;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeAttribute;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
abstract public class BaseNode implements Serializable {
    protected static final long serialVersionUID = 1L;


//    @ApiModelProperty(required = true)
//    protected String name;

//    protected String type;

//    @ApiModelProperty(hidden = true)
//    protected boolean start = false;
//    @ApiModelProperty(hidden = true)
//    protected boolean end = false;


    //下一个节点, 通畅只有资料节点有, 其他则用behavior执行
//    @ApiModelProperty(required = true)
//    protected LinkedHashSet<String> next = new LinkedHashSet<>();



    public static BaseNode create(Map v){
        switch (String.valueOf(v.get("type"))){
            case "check":
                return new CheckNode(v);

            case "checkprocess":
                return new CheckProcessNode(v);

            case "input":
                return new InputNode(v);

            case "logic":
                return new LogicNode(v);

            case "universal":
                return new UniversalNode(v);

            case "end":
                NormalNode node = new NormalNode(v);
//                node.setEnd(true);
                return node;
        }

        throw new RuntimeException();
    }

    @Deprecated
    public void submit(User user, WorkflowNodeInstance wNInstance, Map<String, Object> data){}
    public void submit(User user, WorkflowNodeInstance wNInstance, Map<String,Object> data, IWorkflowNodeAttributeDao attributeDao){}

    protected void addNode(User user, WorkflowNodeInstance wNInstance, String key, String value){
        WorkflowNodeAttribute attribute = wNInstance.getAttributeList()
                .stream()
                .filter(a -> a.getDealUser().getId().equals(user.getId()) && a.getAttrKey().equals(key))
                .findAny()
                .orElse(new WorkflowNodeAttribute());
        attribute.setDealUser(user);
        attribute.setAttrKey(key);
        attribute.setAttrValue(value);
        attribute.setNodeInstance(wNInstance);
        //因为改成了list 这里需要去重
        wNInstance.getAttributeList().add(attribute);
    }
}
