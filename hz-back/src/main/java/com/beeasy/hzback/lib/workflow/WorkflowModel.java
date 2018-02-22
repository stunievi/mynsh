package com.beeasy.hzback.lib.workflow;

import com.beeasy.hzback.lib.workflow.exception.ErrorNodeTypeException;
import com.beeasy.hzback.lib.workflow.node.InformationNode;
import com.beeasy.hzback.lib.workflow.node.LogicNode;
import com.beeasy.hzback.lib.workflow.node.ReviewNode;

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
