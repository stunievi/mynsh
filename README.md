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