package bin.leblanc.workflow.node;

import bin.leblanc.workflow.WorkflowNode;
import bin.leblanc.workflow.exception.LogicNodeException;
import bin.leblanc.workflow.metadata.ILogicExpression;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogicNode extends WorkflowNode {
    private ILogicExpression lastExpression = null;
    private Map<ILogicExpression,Integer> logicMap = new LinkedHashMap<>();

    public LogicNode addIfExpression(ILogicExpression exp){
        lastExpression = exp;
        return this;
    }

    public LogicNode addElseExpression(ILogicExpression exp){
        lastExpression = exp;
        return this;
    }

    public LogicNode then(int nodeIndex){
        if(lastExpression == null){
            return this;
        }
        logicMap.put(lastExpression,nodeIndex);
        lastExpression = null;
        return this;
    }
}
