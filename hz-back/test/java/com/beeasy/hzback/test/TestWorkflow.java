package com.beeasy.hzback.test;

import com.beeasy.hzback.lib.workflow.WorkflowEngine;
import com.beeasy.hzback.lib.workflow.WorkflowModel;
import com.beeasy.hzback.lib.workflow.WorkflowNode;
import com.beeasy.hzback.lib.workflow.exception.ErrorNodeTypeException;
import com.beeasy.hzback.lib.workflow.node.InformationField;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
            .addTextareaField(field -> {
                field.setName("请假内容");
                field.setPlaceholder("填写你的请假理由");
            });


    }
}
