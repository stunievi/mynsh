package bin.leblanc.workflow;

import bin.leblanc.workflow.node.InformationNode;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class WorkflowNode {
    @Getter
    protected Set<Object> dealers = new LinkedHashSet<>();


    public WorkflowNode addDealers(Object[] objects) {
        dealers.addAll(Arrays.asList(objects));
        return this;
    }

    public WorkflowNode addDealers(List<Object> dealers) {
        this.dealers.addAll(dealers);
        return this;
    }

    public WorkflowNode addDealers(Set<Object> dealers) {
        this.dealers.addAll(dealers);
        return this;
    }


    /**
     * 是否已经填写值
     *
     * @return
     */
    public boolean hasValue() {
        return false;
    }


}
