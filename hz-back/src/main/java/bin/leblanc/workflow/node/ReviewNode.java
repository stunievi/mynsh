package bin.leblanc.workflow.node;

import bin.leblanc.workflow.WorkflowNode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReviewNode extends WorkflowNode {
    int maxNum;
    int passNum;

    public ReviewNode setMaxNum(int maxNum) {
        this.maxNum = maxNum;
        return this;
    }

    public ReviewNode setPassNum(int passNum) {
        this.passNum = passNum;
        return this;
    }
}
