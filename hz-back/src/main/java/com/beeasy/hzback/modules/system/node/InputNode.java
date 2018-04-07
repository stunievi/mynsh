package com.beeasy.hzback.modules.system.node;

import com.alibaba.druid.util.StringUtils;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeAttribute;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class InputNode extends BaseNode{
    boolean start;
    Map<String,Content> content = new HashMap<>();

    public InputNode(String name, Map v) {
        super(name, "input", v);

        //起始和结束禁止编辑
        //start和end不能同时存在
        if (v.containsKey("start")) {
            setStart(true);
        } else if (v.containsKey("end")) {
            setEnd(true);
        }

        //内容
        Map content = (Map) v.get("content");
        if (content != null) {
            content.forEach((ck, cv) -> {
                InputNode.Content cnt = new InputNode.Content();
                cnt.setCname(String.valueOf(ck));

                if (cv instanceof Map) {
                    cnt.setType((String) ((Map) cv).get("map"));
                    cnt.setEname((String) ((Map) cv).get("name"));
                    cnt.setRequired(((Map) cv).get("required").equals("y"));
                } else if (cv instanceof String) {
                    List<String> args = Utils.splitByComma(String.valueOf(cv));
                    if (args.size() != 3) {
                        return;
                    }
                    cnt.setEname(args.get(0));
                    cnt.setType(args.get(1));
                    cnt.setRequired(args.get(2).equals("y"));
                } else {
                    return;
                }
                getContent().put(cnt.getEname(), cnt);
            });
        }

    }

    @Getter
    @Setter
    public static class Content extends AbstractBaseEntity{
        String type;
        String cname;
        String ename;
        boolean required = false;
    }

    @Override
    public void submit(User user, WorkflowNodeInstance wNInstance, Map<String, Object> data) {
        Map<String, InputNode.Content> model = getContent();
        for (Map.Entry<String, InputNode.Content> entry : model.entrySet()) {
            String k = entry.getKey();
            InputNode.Content v = entry.getValue();

            //如果是必填字段, 却没有传递
            String attrKey = v.getEname();
            if(!data.containsKey(attrKey)){
                continue;
            }
            //不论是否必填, 空属性就略过
            if (StringUtils.isEmpty(String.valueOf(data.get(attrKey)))) {
                continue;
            }
            //验证属性格式
            //TODO: 这里需要验证属性的格式


            //覆盖旧节点的信息
            Optional<WorkflowNodeAttribute> target = wNInstance.getAttributeList()
                    .stream()
                    .filter(attr -> attr.getAttrKey().equals(attrKey))
                    .findFirst();
            WorkflowNodeAttribute attribute = target.orElse(new WorkflowNodeAttribute());
            attribute.setAttrKey(attrKey);
            attribute.setAttrValue(String.valueOf(data.get(attrKey)));
            attribute.setDealUser(user);
            attribute.setNodeInstance(wNInstance);

            wNInstance.getAttributeList().add(attribute);
        }
    }
}

