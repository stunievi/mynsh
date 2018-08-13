package com.beeasy.hzback.modules.system.controller

import com.beeasy.hzback.core.helper.Result
import com.beeasy.hzback.modules.system.form.WorkflowModelEdit
import com.beeasy.hzback.modules.system.service.WorkflowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMethod.*


@RequestMapping(value = "/api/fuck")
@RestController
@RefreshScope
open class TestKotlinController{

    @Value("\${permission.type}")
    lateinit var type: String

    @Autowired
    lateinit var workflowService: WorkflowService

    @RequestMapping(value = "/ri", method = arrayOf(GET))
    fun c() : Any {
        return Result.ok(type)
    }

    @RequestMapping(value = "/f", method = arrayOf(POST))
    fun d(){

    }
}