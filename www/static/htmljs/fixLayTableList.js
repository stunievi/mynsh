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