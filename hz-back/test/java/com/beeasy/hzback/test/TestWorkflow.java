package com.beeasy.hzback.test;

import bin.leblanc.workflow.WorkflowEngine;
import bin.leblanc.workflow.WorkflowModel;
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
        model.createInformationNode()
                .addTextField(field -> {
                    field.setName("请假标题");
                    field.setPlaceHolder("填写你的请假标题");
                })
                .addDateField(field -> {
                    field.setName("请假时间");
                    field.setPlaceholder("填写请假时间");
                })
                .addRadioField(field -> {
                    field.setName("请假类型");
                    field.setItems(new String[]{"事假","病假"});
                })
                .addTextareaField(field -> {
                    field.setName("请假内容");
                    field.setPlaceholder("填写你的请假理由");
                });

        model.createReviewNode()
                .setPassNum(1)
                .setMaxNum(1);

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
    }
}
