package bin.leblanc.workflow;

import bin.leblanc.workflow.node.InformationNode;
import bin.leblanc.workflow.node.LogicNode;
import bin.leblanc.workflow.node.ReviewNode;

public class WorkflowModel {

//    public WorkflowNode createNode(int nodeType) throws ErrorNodeTypeException {
//        WorkflowNode node = null;
//        switch (nodeType){
//            case WorkflowNode.INFOMATION_NODE:
//                node = new InformationNode();
//                break;
//
//            case WorkflowNode.REVIEW_NODE:
//                node = new ReviewNode();
//                break;
//
//            case WorkflowNode.LOGIC_NODE:
//                node = new LogicNode();
//                break;
//
//            default:
//                throw new ErrorNodeTypeException();
//        }
//        return node;
//    }

    public InformationNode createInformationNode(){
        return new InformationNode();
    }

    public ReviewNode createReviewNode(){
        return new ReviewNode();
    }

    public LogicNode createLogicNode(){
        return new LogicNode();
    }

}
