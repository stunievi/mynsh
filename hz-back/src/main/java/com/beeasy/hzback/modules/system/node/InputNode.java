package com.beeasy.hzback.modules.system.node;

import com.alibaba.druid.util.StringUtils;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeAttributeDao;
import com.beeasy.hzback.modules.system.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class InputNode extends BaseNode{
    boolean start;
    Map<String,Content> content = new HashMap<>();

    public InputNode(WorkflowModel workflowModel, Map v) {

        //起始和结束禁止编辑
        //start和end不能同时存在
//        if (v.containsKey("start")) {
//            setStart(true);
//        } else if (v.containsKey("end")) {
//            setEnd(true);
//        }

        //内容
        Map content = (Map) v.get("content");
        if (content != null) {
            content.forEach((ck, cv) -> {
                InputNode.Content cnt = new InputNode.Content();
                cnt.setCname(String.valueOf(ck));

                if (cv instanceof Map) {
                    Map cvmap = (Map) cv;
                    cnt.setType((String) ((Map) cv).get("type"));
                    cnt.setEname((String) ((Map) cv).get("name"));

                    String required = (String) cvmap.getOrDefault("required","n");
                    cnt.setRequired(required.equals("y"));
                    //特殊类型
                    List items = (List) ((Map) cv).getOrDefault("items",new ArrayList<>());
                    cnt.getItems().addAll((Collection<? extends String>) items.stream().map(item -> String.valueOf(item)).collect(Collectors.toList()));

                    //固有字段
                    boolean innate = (boolean) cvmap.getOrDefault("innate",false);
                    if(innate){
                        WorkflowModelInnate workflowModelInnate = new WorkflowModelInnate();
                        workflowModelInnate.setContent(cnt);
                        workflowModelInnate.setFieldName(cnt.getEname());
                        workflowModelInnate.setModel(workflowModel);
                        workflowModel.getInnates().add(workflowModelInnate);
                        return;
                    }

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
        List<String> items = new ArrayList<>();
        boolean required = false;
//        boolean innate = false;
    }

    @Override
    public void submit(User user, WorkflowNodeInstance wNInstance, Map<String, Object> data, IWorkflowNodeAttributeDao attributeDao) {
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
            attribute.setAttrCname(v.getCname());
            attribute.setDealUser(user);
            attribute.setNodeInstance(wNInstance);

            attributeDao.save(attribute);
//            wNInstance.getAttributeList().add(attribute);
        }
    }
}

