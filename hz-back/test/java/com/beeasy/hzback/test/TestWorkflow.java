package com.beeasy.hzback.test;

import bin.leblanc.workflow.WorkflowEngine;
import bin.leblanc.workflow.WorkflowModel;
import bin.leblanc.workflow.WorkflowTask;
import bin.leblanc.workflow.exception.StartTaskException;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
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
                    field.setRequired(true);
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
                .addElseExpression(() -> {
                    return false;
                })
                .then(1);

        model.createEndNode();

        /**
         * 开启新任务
         */
        try {
            WorkflowTask task = model.startNewTask(1);
        } catch (StartTaskException e) {
            e.printStackTrace();
        }
    }
}
