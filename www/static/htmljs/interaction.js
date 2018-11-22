

// 点击查看客户详情
$(document).on("click", ".handleShowClientInfo", function(){
    var that = $(this),
        type = that.data("type");
    var clientType;
    if(typeof type === "number"){
        // 根据客户类型码判断客户类型
        clientType = (type+'').charAt(0) == enumClientType.private ? "indiv" : "com";
    }else if(type == "indiv" || type == "com") {
        // 已知客户类型，直接赋值
        clientType = type;
    }else {
        console.error("找不到对应的客户类型！")
        return false;
    }
    var clientId = that.data("id"),
        clientName = that.data("name");
    showClientInfo(clientId, clientName, clientType);
    return true;
})


// 查看工作流
eventBind(".handleNodeShow", function(that){
    var nodeid = that.data("nodeid"); // 节点id
    var href = '/htmlsrc/workFlow/nodeStates.html?id='+nodeid;
    top.addTab("show-task-" + nodeid, "查看任务 - " + nodeid , href);

    // handleeeNodeShowIndex = layerOpenIframe({
    //     title: "任务查看 - " + nodeid,
    //     url: '/htmlsrc/workFlow/nodeStates.html?id='+nodeid
    // })
})

// 查看任务时,删除任务回调
function cancelNodeCallback() {
    layuiTableReload();
    layer.close(handleeeNodeShowIndex)
    layer.msg("删除成功!")
    return true;
}

// 处理工作流
eventBind(".handleNodeDeal", function(that){
    var nodeid = that.data("nodeid"); // 节点id
    handleeeNodeDealIndex = layerOpenIframe({
        title: "任务处理",
        url: '/htmlsrc/workFlow/nodeDeal.html?id='+nodeid
    })
})

// 返回上一页
$("#handleGoHis").click(function(){
    var rel = decodeURI(getParam("rel"));
    if(rel){
        location.href = rel;
    }else{
        history.go(-1);
    }
})

// panelTableShow查看详情
eventBind(".handleeeShowDetail", function(that){
    layerOpenIframe({
        title: "查看详情",
        url: "/htmlsrc/htmllayer/detailInfo/detailInfo.html?name="+that.data('name')+"&id="+that.data('id')
    })
})

$().ready(function(){
    // 修复more按钮在layTableList中显示问题
    $(document).on("click", ".layui-table-body .dropdown-toggle", function(){
        if($(".layui-table-body").height()>210){
            if($(".layui-table-body tbody tr").length>=6){
                $(".layui-table-body tbody tr:gt(-4)").find('.btn-group').addClass("dropup");
            }
        }else{
            if($(".layui-table-body tbody tr").length==4){
                $(".layui-table-body tbody tr:gt(-2)").find('.btn-group').addClass("dropup");
            }
            if($(".layui-table-body tbody tr").length==5){
                $(".layui-table-body tbody tr:gt(-3)").find('.btn-group').addClass("dropup");
            }
        }
        $(this).parents('.layui-table-fixed .layui-table-body').css({overflow: "visible"});
        $(this).parents('.layui-table-cell').css({overflow: "visible"});
    });
})