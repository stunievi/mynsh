package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeAttribute;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
abstract public class BaseNode implements Serializable {
    protected static final long serialVersionUID = 1L;

    public BaseNode(String name, String type, Map map) {
        this.name = name;
        this.type = type;

        if(map.containsKey("next")){
            Object next = map.get("next");
            if(next instanceof Collections){
                setNext(new HashSet((Collection) next));
            }
            else{
                setNext(new HashSet(Collections.singleton(next)));
            }
        }

    }

    protected String name;
    protected String type;
    protected boolean start = false;
    protected boolean end = false;
    protected int order = 0;


    //下一个节点, 通畅只有资料节点有, 其他则用behavior执行
    protected Set<String> next = new LinkedHashSet<>();



    public static BaseNode create(String k, Map v){
        switch (String.valueOf(v.get("type"))){
            case "check":
                return new CheckNode(k,v);

            case "checkprocess":
                return new CheckProcessNode(k,v);

            case "input":
                return new InputNode(k,v);

            case "logic":
                return new LogicNode(k,v);

            case "universal":
                return new UniversalNode(k,v);

            case "end":
                NormalNode node = new NormalNode(k,v);
                node.setEnd(true);
                return node;
        }

        throw new RuntimeException();
    }

    public  void submit(User user, WorkflowNodeInstance wNInstance, Map<String, Object> data){}

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
        wNInstance.getAttributeList().add(attribute);
    }
}
