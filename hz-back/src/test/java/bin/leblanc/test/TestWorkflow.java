package bin.leblanc.test;

import bin.leblanc.workflow.WorkflowEngine;
import bin.leblanc.workflow.WorkflowModel;
import bin.leblanc.workflow.WorkflowTask;
import bin.leblanc.workflow.enums.DealType;
import bin.leblanc.workflow.exception.*;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestWorkflow {

    @Autowired
    WorkflowEngine workflowEngine;

    @Test
    public void test() {
        WorkflowModel model = workflowEngine.createModel();
        model.setName("请假测试");
        model.setVersion(123.00);
        model.createInformationNode()
                .addTextField(field -> {
                    field.setName("请假标题");
                    field.setPlaceHolder("填写你的请假标题");
                    field.setRequired(true);
                })
                .addDateField(field -> {
                    field.setName("请假时间");
                    field.setPlaceholder("填写请假时间");
                    field.setRequired(false);
                })
                .addRadioField(field -> {
                    field.setName("请假类型");
                    field.setItems(new String[]{"事假","病假"});
                    field.setRequired(true);
                })
                .addTextareaField(field -> {
                    field.setName("请假内容");
                    field.setPlaceholder("填写你的请假理由");
                    field.setRequired(true);
                })
                .addDealers(new Integer[]{1,2,3});

        model.createReviewNode()
                .setPassNum(1)
                .setMaxNum(1)
                .addDealers(new Integer[]{4,5,6});

        model.createLogicNode()
                .addIfExpression(() -> {
                    return true;
                })
                .then(1)
                .addElse(1);

        model.createReviewNode()
                .setPassNum(1)
                .setMaxNum(1)
                .addDealers(new Integer[]{4,5,6});

        model.createEndNode();

        /**
         * 开启新任务
         */
        try {

            //提交第一个资料
            Map<String,String> data = new HashMap<>();
            data.put("请假标题","测试请假标题");
            data.put("请假内容","测试请假内容");
            data.put("请假类型","事假");

            /**
             * 测试成功的提交
             */
            log.info("测试工作流程1");
            WorkflowTask task = model.startNewTask(1);
            //提交信息
            task.submitData(1,data);
            //审批
            task.submitData(4, DealType.SUCCESS);
            task.submitData(4,DealType.SUCCESS);
            Assert.assertEquals(task.isFinished(),true);

            /**
             * 测试失败的提交（退回上一级）
             */
            log.info("测试工作流程2");
            WorkflowTask task2 = model.startNewTask(1);
            task2.submitData(1,data);
            task2.submitData(4,DealType.SUCCESS);
            task2.submitData(4,DealType.BACK_TO_LAST);

            Assert.assertEquals(task2.getCurrentIndex(),1);
            log.info(JSON.toJSONString(task2.getMoveList()));


            /**
             * 测试失败提交（废弃）
             */
            log.info("测试工作流程3");
            WorkflowTask task3 = model.startNewTask(1);
            task3.submitData(1,data);
            task3.submitData(4,DealType.SUCCESS);
            task3.submitData(4,DealType.STOP);

            Assert.assertEquals(task3.isStoped(),true);

        } catch (StartTaskException e) {
            e.printStackTrace();
        } catch (ReviewFailedException e) {
            e.printStackTrace();
        } catch (NoPermissionException e) {
            e.printStackTrace();
        } catch (NoRequiredFieldException e) {
            e.printStackTrace();
        } catch (NoElseExpressionException e) {
            e.printStackTrace();
        } catch (NoWorkflowNodeException e) {
            e.printStackTrace();
        }
    }
}
