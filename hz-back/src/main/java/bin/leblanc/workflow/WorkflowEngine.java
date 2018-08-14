package bin.leblanc.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkflowEngine {


    /**
     * 创建一个工作流模型
     */
    public WorkflowModel createModel() {
        WorkflowModel model = new WorkflowModel();
        return model;
    }
}
