# 使用到的工具库
* * *
1. [JQuery](https://jquery.com/)
2. [jquery-cookie](https://github.com/carhartl/jquery-cookie#readme)
3. [art-template](http://aui.github.io/art-template/)
4. [bootcss](https://v3.bootcss.com/)
5. [echarts](http://echarts.baidu.com/)
6. [jqueryui](http://jqueryui.com/)
    1. [datepicker](http://jqueryui.com/datepicker/)
7. [layer](http://layer.layui.com/)
<!-- 8. [Paging](http://www.lovewebgames.com/jsmodule/paging.html) -->
9. [jquery-manifest](https://github.com/jstayton/jquery-manifest)
10. [jquery-marcopolo](https://github.com/jstayton/jquery-marcopolo)
* * *
__注意!!!!__
1.__jquery-marcopolo，Paging有细微改动，和官网不一致; JQuery中放入jquery-cookie，Paging(弃用中)，unserialize等插件，shime兼容低版本浏览器__
2.__工具类js修改处有"!!!!!!!!"标识__
3.__global.config.js一定要放置utils.js之前加载__
4.__layui__
4.1__laytable默认都是使用dataList标识id,插入位置标识请使用'dataList'作为id__

# 文件结构
-htmlsrc #静态页面，页面独有js会存放同一级目录。
-static #静态资源
    --css #样式文件
    --htmlImage #静态图片
    --htmljs #js文件
    --vendor #js库
-tmpl # art-template渲染模板
    --html #模板html
    --js #模板整合为字符串后，art-template再编译为变量
    
# 服务器响应
服务端返回数据格式为
```js
/* 
* success: 访问接口成功，success即为true
* errMessage: 接口访问成功，但是执行出错返回信息。若为空，请确定数据格式是否正确
* data: 访问&&执行成功后返回数据
*/
res = {
    success: Boolean,
    errMessage: String
    data: Object
}
/* 
* content: 服务器返回数据库数据，不分页数据为data
* first: 是否首页
* last: 是否最后一页
* size: 分页大小
* totalPages: 总页数
* totalElements: 记录总条数
*/
res.data = {
    content: Object
    size: Number,
    totalElements: Number,
    totalPages: Number
}
```
向服务器提交数据框架函数 postFrameFun(),其中【因考虑兼容ie8跨域请求，自行封装[SuperPost](/static/htmljs/utils.js#SuperPost)异步获取数据】 报错信息，请求失败等提示皆封装此函数中。
只有当请求成功且服务器执行成功才会将res.data返回至callback(origin); 
callback字符标识函数
'back' == 返回上一步
'render' == 重新渲染tableList,[舍弃中]
'layrender' == 重新渲染layTableList

# 页面渲染
1.ajax获取服务器数据
2.处理响应数据
>[getRemoteData](/static/htmljs/utils.js)获取服务器数据。callback回调拿到数据渲染
3.art-template渲染
>相关渲染函数，模板见tmpl文件下内容
<!-- 舍弃中 -->
~~tableList渲染封装：
变量tableListRenderObj为初始化约定变量名
初始化函数initTableList()~~
<!-- 舍弃中 -->
[laytableRender](/hznsh/static/htmljs/utils.js)渲染[详情](http://www.layui.com/doc/modules/table.html)

4.jq插入页面

5.页面筛选
默认拦截函数[checkSubmitForm()](/hznsh/static/htmljs/utils.js)
```js
    function checkSubmitForm(){
        // 表单名默认 form-search
        var form = $("form[name='form-search']");
        layuiTableReload({
            where: form.serialize()
        })
        return false;
    }
```
如需改变或有其他处理需求请重写该函数

# 变量名含义
"T"开头变量为模板变量： 如，TPanelList，变量对应为[文件](/tmpl/js/panleList.js)，[示例文件](/tmpl/html/panelList.html)。
***Render函数为渲染函数，例，/tmpl/js/panleList.js中panelFormRender。

~~"enum***"开头变量为枚举变量~~

~~"selectData.***"变量为select下来框选项 selectData~~

# JS获取数据
<!-- 启用中-begin -->
tableListRender渲染tableList选中数组
```js
    var ids = $("#sellect-all").data("val");
```
<!-- 启用中-end -->
该语句获取tableList中所有勾选的列主键值，相关语句语句位于[/tmpl/js/tableList.js]
laytable选中数据
```js
    var ids = getLayuiTabelCheckIds();
```


# iframe页面

切换iframe自动执行函数: onInfront

关闭当前iframe: closeCurrentIframe

# addNavTab中id规范
工作流: 'workflow' + id,
用户详情： 'user' + id
对公客户： 'com-user' + id,
对私客户： 'indi-user' + id,
贷款台账详情: 'loan' + id , '贷款台账详情 - {{loanid}} - {{name}}', "/htmlsrc/creditDataManage/ledger/loan.show.html?loanid={{loanid}}"
贷款资料详情: 'loandata' + id
任务详情: 'worktask' + id
企查查公司详情: "ECIdetail" + 公司名
企查查资质公司详情: "ECIdetail-Qual" + 公司名