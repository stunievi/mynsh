package com.beeasy.hzback.modules.system.controller

import com.beeasy.hzback.core.helper.Result
import com.beeasy.hzback.modules.system.form.WorkflowModelEdit
import com.beeasy.hzback.modules.system.service.WorkflowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMethod.*


@RequestMapping(value = "/api/fuck")
@RestController
open class TestKotlinController{

    @Autowired
    lateinit var workflowService: WorkflowService

    @RequestMapping(value = "/ri", method = arrayOf(GET))
    fun c() : Any {
        return Result.ok()
    }

    @RequestMapping(value = "/f", method = arrayOf(POST))
    fun d(){

    }
}