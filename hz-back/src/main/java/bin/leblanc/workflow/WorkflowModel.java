package bin.leblanc.workflow;

import bin.leblanc.workflow.node.EndNode;
import bin.leblanc.workflow.node.InformationNode;
import bin.leblanc.workflow.node.LogicNode;
import bin.leblanc.workflow.node.ReviewNode;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

public class WorkflowModel {

    @Getter
    Set<WorkflowNode> nodeList = new LinkedHashSet<>();


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

}
