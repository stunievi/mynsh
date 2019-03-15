

//切换tab页的显示
$(document).on('click','#myTab > li',function(e){
    //清除原来显示的tab页
    var oldTab = $("#myTab li.active").removeClass("active").find("a[data-toggle='tab']");
    $(oldTab.attr("data-href")).removeClass("active");
    //设置新的显示tab页
    var newTab = $(this).addClass("active").find("a[data-toggle='tab']");
    $(newTab.attr("data-href")).addClass("active");

    refreshTabHistory(false/*isDelete*/,$(this).attr('id').substring(4));

    //触发更新事件
    var ifr = $(newTab.attr("data-href") + " iframe")[0];
    // console.log(ifr)
    ifr && ifr.contentWindow && ifr.contentWindow.onInfront && ifr.contentWindow.onInfront();

});

$("#reloadIframe").click(function(){
    var currIframe = $("#myTab li.active a").data("iframe");
    // console.log(currIframe,$("#"+currIframe).attr("src"))
    $("#"+currIframe).attr("src", $("#"+currIframe).attr("src"));
})

$("#closeAllIframe").click(function(){
    $("#myTab li").remove();
    $("#iframe-content-wrap").children().remove();
})

$("#closeOtherIframe").click(function(){
    $("#myTab li").each(function(){
        if(!$(this).hasClass("active")){
            $(this).find("i").click();
        }
    })
})

// 可拖拽
$(function() {
    $( "#myTab" ).sortable();
    $( "#myTab" ).disableSelection();
});

var currentTabId = '';//当前焦点Tab
//在非左侧菜单栏弹出的tab页也会用到该数据，如common.js中的pageForward函数
var pageCounter = 0;
/*
 id:       tab页签的html标签ID属性格式为"tab-"+id，内容容器的html标签ID格式为"tab-content-"+id
 text:     tab页签的显示文本
 url:      打开的iframe的url
 innerTab: 是否是内部弹出页（打开的tab页触发添加新的tab页），默认为undefined/false
 */

$(document).on("click", '.delete-tab', function () {
    var id = $(this).attr("data-id")
    deleteTab(id)
    return false;
});

function addTab(id,text,url,innerTab) {
    // console.log('#myTab #tab-'+id)
    //如果某个页面已经打开，则切换到该页显示即可，不会新添加tab页
    if($('#myTab #tab-'+id).length > 0){
        $('#myTab #tab-' + id + ' a').click();
    }else{
        var tab_id = "tab-" + id,
            tab_content_id = "tab-content-"+id,
            tab_html = $("<li id='" + tab_id + "'><a data-toggle='tab' href='javascript:;' data-href='#"
                + tab_content_id + "' title='"+text+"' data-iframe='iframepage-"+id+"'>" + text + "</a>"
                + ("<i class='fa fa-times delete-tab'  data-id='"+id+"'></i>") + "</li>");
        //onclick='deleteTab(\"" + id + "\")'
        if($("#myTab li.active").length > 0){
            $("#myTab li.active").after(tab_html);
        }else{
            $("#myTab").append(tab_html);
        }
        //bind event
        //添加tab页签
        $("#myTab > li").removeClass("active");
        $("#"+tab_id).addClass("active")

        //添加新的内容显示
        $(".tab-content > div").removeClass("active");
        $(".tab-content").append("<div id='"+ tab_content_id +"' class='active' style='height:100%'>"
            + "<iframe id='iframepage-" + id + "' name='iframepage-" + id
            + "' width='100%' height='100%' frameborder='0' scrolling='yes'   src='" + url + "'></iframe></div>");



        context.attach('#' + tab_html[0].id,[
            {
                text: '重新加载',
                action: function(e, selector) {
                    var currIframe = tab_html.find("a").data("iframe");
                    $("#"+currIframe).attr("src", $("#"+currIframe).attr("src"));
                }
            }
            ,{
                text: '关闭标签页'
                ,action: function(e){
                    tab_html.find("i").click();
                }
            }
            ,{
                text: "关闭其他标签页"
                ,action: function(e){
                    console.log(tab_html)
                    $("#myTab li[id*=tab-]").not(tab_html).find("i").click()
                }
            }
            ,{
                text: "关闭左侧标签页"
                ,action: function (e) {
                    tab_html.prevAll().find("i").click();
                }
            }
            ,{
                text: "关闭右侧标签页"
                ,action: function (e) {
                    tab_html.nextAll().find("i").click();
                }
            }
        ])
    }
    //刷新切换tab的历史记录
    refreshTabHistory(false/*isDelete*/,id);
    //重新设置tab页签的宽度
    refreshWidth();
}
//参数id为tab的标志，但是并不是tab页的id属性，真正的id属性值是"tab-"+id
function deleteTab(id){
    // var e = window.event
    // if(e){
    //     if(e.srcElement.tagName != 'I'){
    //         return
    //     }
    // }
    var tabJQ = $("#tab-"+id),
        tabContentJQ = $("#tab-content-" + id);
    if(!tabJQ.hasClass("active")){
        tabJQ.remove();
        tabContentJQ.remove();
        refreshTabHistory(true/*isDelete*/,id);
    }else{
        tabJQ.remove();
        tabContentJQ.remove();
        refreshTabHistory(true/*isDelete*/,id);
        console.log(currentTabId)
        $("#myTab li").each(function(index, item){
            console.log($(item),'tab-'+currentTabId , $(item).attr("id"))
            if('tab-'+currentTabId == $(item).attr("id")){
                $(item).click()
                return false;
            }
        })
    }
    refreshWidth();
}
//关闭当前tab页的快速方法
function closeCurrentTab(){
    deleteTab(currentTabId);
}

/*
 刷新页签切换历史
 isdelete: 是否是删除tab页签,true:是，false：否
 curTabId：要处理的tab页签的id,tab页签html标签元素的ID属性格式为"tab-"+curTabId
 */
function refreshTabHistory(isdelete,curTabId){
    if(!refreshTabHistory.histoty){
        //用来记录用户点击tab的历史
        refreshTabHistory.histoty = [];
    }
    var index = 0,
        leng = refreshTabHistory.histoty.length;
    //查找传入的tab页签在历史记录中的位置
    for(; index < leng; index++){
        if(refreshTabHistory.histoty[index] == curTabId){
            break;
        }
    }
    //如果是删除页签，直接在历史记录中删除即可，历史记录的其他页签的顺序不变
    if(isdelete){
        refreshTabHistory.histoty.splice(index,1);
        //如果是新增页签，先保证历史记录中没有改页签（有就删掉），然后将新增的页签放在历史记录的最后面（即该页签为最新）
    }else{
        if(index < leng) {
            refreshTabHistory.histoty.splice(index,1);
        }
        refreshTabHistory.histoty.push(curTabId);
    }
    currentTabId = refreshTabHistory.histoty[refreshTabHistory.histoty.length - 1];
}

//刷新重置tab页签的宽度
function refreshWidth(){
    var panelWidth = $('#myTab').width() - 100/*可能存在的滚动条宽度, 增加dom*/,
        tabs = $('#myTab > li'),
        tabContentAverageWidth = 0/*tab > a标签的宽度*/,
        minTabAverageWidth = 25/*margin-left:5,X按钮宽度为20*/,
        zeroContentTabWidth = 35/*当tab > a标签宽度为0时tab标签对应的宽度是30px，外加上margin-left:5*/,
        aPaddingLeft = 10/*tab > a标签的padding-left默认是10，当averageWidth< 35需要调整*/;
    averageWidth = parseInt(panelWidth/(tabs.length),10);//
    if(averageWidth >= zeroContentTabWidth){
        tabContentAverageWidth = averageWidth - zeroContentTabWidth;
        /*35 > averageWidth >= 25*/
    }else if(averageWidth >= minTabAverageWidth){
        tabContentAverageWidth = 0;
        aPaddingLeft = averageWidth - minTabAverageWidth;
        //averageWidth < 25
    }else{
        tabContentAverageWidth = 0;
        aPaddingLeft = 0;
    }
    //tab页签名称元素a标签的宽度和padding-left。这个是在box-sizing:border-box。的情况下
    tabs.find('>a').css({'width':(tabContentAverageWidth + aPaddingLeft),'padding-left':aPaddingLeft});
}
