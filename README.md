# 惠州农商行风险管理系统

需要环境

```
1. JDK1.8以上（不要使用1.9）
2. mysql
3. redis
```

部署方法

```
1. 使用maven导入该项目
2. 更改参数，首次运行项目，会自动生成对应的数据库
3. 手动插入一条用户信息，以及一条最顶级的部门记录
```


已知问题：
在频繁插入删除节点的时候,avalon会停止响应


工作流设计流程
1. 针对一个数据集合（可能是某些实体，需要预先抽象出这些实体模型，一旦确定不可更改）发起
例如，发起人本身就是一个实体，可通过字典设置该实体的额外属性，这些可以更改
例如某一个用户的状态（在职、离职、请假、等等）
不良资产也算在该内
2. 中间节点可自由设计，节点暂时分为两种（资料节点和审核节点）
资料节点可以自由调整表单字段
审核节点分为普通审核和会签
普通审核就是一个人就可以审核通过
会签是多人审核，并且满足一定条件后才走下一环节
3. 审核节点可以驳回或者返回上一步
4. 最终处理节点，是对最初发起的资料集合的回调处理，例如请假审核通过后，可将改用户的状态字段设置为请假，持续多长时间

存疑问题：多次发起是否会造成状态冲突？


# 包说明

# bin.leblanc.zed

自动化API 数据库操作模型 

可以由客户端直接发起（需要验证权限以及提交数据的合法性），也可以由服务端使用超级管理员权限进行操作（服务端进行的操作默认会拥有所有权限，也可以根据不同权限进行调用）

即

# 使用者只需要配置“指定权限”的某个用户可以在限定条件内由“指定字段”进行查询并操作“指定数据集”的“指定字段”进行新增、删除、修改操作，即可免除80%需要手工编写的API，余下20%则是包含了复杂逻辑或者复杂校验的手工编写API，例如登录、下单、付款等。


调用方法：所有操作使用POST进行提交，Body即整个报文，报文格式采用标准json，以下为格式说明

```
{
    "method":"get/post/put/delete",
    ...
}
```
## GET
用于对数据模型的查询

所有查询需验证查询权限，即是否拥有某个字段的使用权，以及这个字段具体的使用范围，超过范围的条件会被无效化

查询ID为10的用户
```
{
    "method" : "get",
    "User" : {
        "$where": {
            "id" : 10
        }
    }
}
```

查询ID为10的用户以及用户所有的角色
```
{
    "method" : "get",
    "User" : {
        "roles": {}
        "$where": {
            "id" : 10
        }
    }
}
```


查询ID大于10且小于100的所有用户
```
{
    "method" : "get",
    "User[]" : {
        "$where": {
            "id" : {
                "lt" : 100,
                "gt" : 10
            }
        }
    }
}

```

查询ID小于10或者大于100的第一页所有用户
```
{
    "method" : "get",
    "User[]" : {
        "$where": {
            "id" : {
                "lt" : 10
            },
            "$or":{
                "id": {
                    "gt" : 100
                }
            }
        },
        "$page":1
    }
}

```

## POST 请求
用于对数据集的新增

新增一个用户
```
{
    "method": "post",
    "User"{
        ...
        //这里需填写用户的信息，校验需通过Entity模型声明的所有校验，否则会插入失败
    }
}
```


## PUT 请求
用于对数据集的修改操作

修改一个用户的姓名
新增一个用户
```
{
    "method": "put",
    "User"{
        "id" : 10, //PUT操作必须传递需要操作实体的主键，如果不传递视为无效操作
        //ID可以传递数组，进行批量修改
        ...
        //这里需填写用户的信息，校验需通过Entity模型声明的所有校验，否则会修改失败
        //特别提示：所有字段的需要符合对应的权限，如果有超出权限的修改（例如用户名在表中唯一不允许修改），则会视为无效操作
    }
}
```


## DELETE 请求
用于对数据集的删除操作

删除一个用户
```
{
    "method":"delete",
    "User":{
        "id" : 10 //同PUT请求一样，主键为必填字段，并且会校验当前用户是否拥有这个数据集的删除权限，如果没有，则会视为无效操作
        //ID可以传递数组，进行批量删除
    }
}
```

# bin.leblanc.workflow

被剥离出来的工作流，具体使用请参见TestWorkflow.java文件



