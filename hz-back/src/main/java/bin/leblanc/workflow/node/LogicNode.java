package bin.leblanc.workflow.node;

import bin.leblanc.workflow.WorkflowNode;
import bin.leblanc.workflow.exception.LogicNodeException;
import bin.leblanc.workflow.metadata.ILogicExpression;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogicNode extends WorkflowNode {
    private ILogicExpression lastExpression = null;

    @Getter
    private Map<ILogicExpression,Integer> logicMap = new LinkedHashMap<>();

    @Getter
    private int elseOffset = -1;


    public LogicNode addIfExpression(ILogicExpression exp){
        lastExpression = exp;
        return this;
    }

    public void addElse(int nodeIndex){
        elseOffset = nodeIndex;
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
