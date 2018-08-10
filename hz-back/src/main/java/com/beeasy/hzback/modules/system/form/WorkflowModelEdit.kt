package com.beeasy.hzback.modules.system.form

import com.beeasy.hzback.core.exception.RestException
import com.beeasy.hzback.core.helper.NoArg
import lombok.NoArgsConstructor
import java.lang.Exception
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.math.min


class WorkflowModelEdit{
    @NotNull(message = "模型ID不能为空")
    var id: Long? = null

    @NotNull(message = "工作流名字不能为空")
    @Size(min = 2, max = 20, message = "工作流名需在2-20位之间")
    var name = ""
    var info  = ""
    var open  = false
}