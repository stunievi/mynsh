package bin.leblanc.workflow;

import bin.leblanc.workflow.exception.NoRequiredFieldException;
import bin.leblanc.workflow.field.InformationField;
import bin.leblanc.workflow.node.InformationNode;
import bin.leblanc.workflow.node.ReviewNode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkflowTask implements Serializable{

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Object starter;

    private WorkflowModel workflowModel;

    private int currentIndex = -1;

    @Getter
    private Map<Integer,Object> data = new LinkedHashMap<>();

    public WorkflowTask(WorkflowModel model){
        workflowModel = model;
        currentIndex = 0;
    }

    public void submitData(Object role, Object data) throws NoRequiredFieldException {
        WorkflowNode currentNode = workflowModel.nodeList.get(currentIndex);
        //资料节点的处理
        if(currentNode instanceof InformationNode){
            Map<String,Object> mapData = (Map<String, Object>) data;
            Set<InformationField> fields = ((InformationNode) currentNode).getFields();
            for (InformationField field : fields) {
                //如果这个字段是必填且没有传值，那么抛出异常
                if(field.isRequired() && !mapData.containsKey(field.getName())){
                    throw new NoRequiredFieldException();
                }
                //校验类型，暂且略过
                //TODO: 需要根据field类型校验每个提交的值
            }
            //删除不需要的提交资料
            Set<String> removeKeys = mapData.keySet().stream().filter(key -> {
                return fields.stream().anyMatch(field -> field.getName().equals(key));
            }).collect(Collectors.toSet());
            removeKeys.forEach(key -> {
                mapData.remove(key);
            });

            //放入资料池
            this.data.put(currentIndex,data);
            this.goNextNode();
        }
        //审核节点的处理
        else if(currentNode instanceof ReviewNode){

        }
    }


    public void goNextNode(){

    }


}
