package bin.leblanc.workflow;

import bin.leblanc.workflow.exception.StartTaskException;
import bin.leblanc.workflow.node.EndNode;
import bin.leblanc.workflow.node.InformationNode;
import bin.leblanc.workflow.node.LogicNode;
import bin.leblanc.workflow.node.ReviewNode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WorkflowModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    private List<WorkflowNode> nodeList = new ArrayList<>();

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Double version;


    public InformationNode createInformationNode(){
        InformationNode node = new InformationNode();
        nodeList.add(node);
        return node;
    }

    public ReviewNode createReviewNode(){
        ReviewNode node = new ReviewNode();
        nodeList.add(node);
        return node;
    }

    public LogicNode createLogicNode(){
        LogicNode node = new LogicNode();
        nodeList.add(node);
        return node;
    }

    public EndNode createEndNode(){
        EndNode node = new EndNode();
        nodeList.add(node);
        return node;
    }


    /**
     * 检查一个角色是否可以发布这个任务
     */
    public boolean canStart(Object role){
        if(this.nodeList.size() == 0){
            return false;
        }
        return this.nodeList.get(0).getDealers().contains(role);
    }

    /**
     * 检查一个角色是否可以在处理流程序列中
     */
    public boolean canDeal(Object role){
        if(nodeList.size() == 0){
            return  false;
        }
        return nodeList.stream().anyMatch(node -> {
            return (node instanceof ReviewNode) && node.getDealers().contains(role);
        });

    }

    /**
     * 启用新的任务处理
     */
    public WorkflowTask startNewTask(Object role) throws StartTaskException {
        if(!canStart(role)){
            throw new StartTaskException();
        }
        WorkflowTask task = new WorkflowTask(this);
        task.setStarter(role);
        return task;
    }
}
