// 获取两者之间发送的消息
function getCallBetweenInfos(){
    if(!toUser){
        return;
    }
    getRemoteData({
        loading: true,
        url: remoteApi.apiUserRecentMsg,
        data: {
            userId: toUser.id
        },
        callback: function(origin){
            if(origin.length > 0){
                // 数组反转
                origin.reverse();
                lastMsgId = origin[origin.length-1].id;
                origin.forEach(function(elm){
                    elm.sendTime = dateFormate1(elm.sendTime);
                });
            }
            $("#main-content").html('');
            $("#main-content").append(template('noticeList', {
                remoteOrigin: remoteApi.apiOpenDownload,
                toUser: toUser,
                currUser: currUser,
                msgList: origin || []
            }));

            // 获取历史消息按钮样式
            if(origin.length < 20){
                $("#getHisNotice").data("enable", false);
                $("#getHisNotice").text("无历史消息");
            }
            else{
                $("#getHisNotice").data("enable", true);
                $("#getHisNotice").text("点击加载更多消息");
            }
            // 上传初始化
            $(document).ready(function(){
                var uploader = WebUploader.create({
                    swf: '/static/vendor/webUploader/Uploader.swf',
                    //server: remoteApi.apiMsgSendFile,
                    server: remoteApi.apiUploadFile,
                    pick:'#picker',
                    headers: {
                        authorization: getUserToken()
                    },
                    auto: true,
                    resize: false
                });
                // 设置data
                uploader.on("uploadBeforeSend", function(obj, data, headers){
                    data.token = $.cookie('authorization');
                    data.type = "TEMP";

                });
                // 单个文件上传成功
                uploader.on('uploadSuccess', function(file, res) {
                    var fileData = res.data;
                    postRemoteData({
                        url: remoteApi.apiMsgSendFile,
                        data: {
                            fileId: fileData.id,
                            toUid: toUserId
                        },
                        callback: function(origin){
                            origin.sendTime = dateFormate1(origin.sendTime); // 格式化发送时间
                            $("#noticeDataList").append(template('mySendMsg', {
                                remoteOrigin: remoteApi.apiOpenDownload,
                                toUser: toUser,
                                currUser: currUser,
                                msg: origin
                            }));
                        }
                    })
                });
            });
        }
    });
}

function changeToUser(touid){
    if(touid == $.cookie("userId")){
        layer.msg("不能向自己发送消息！");
    }else{
        if(actionType =='sendMsg'){
            rememberParam("toUserId", touid);
            getUserById(touid,function(user){
                toUser = user;
                getCallBetweenInfos();
            })
        }else{
            location.href = "?toUserId="+touid+"&act=sendMsg"
        }
    }
}

$(document).on("click", ".handleSwitchUser", function(){
    changeToUser($(this).data("id"));
})

// 切换通信用户
$(document).on("click","#home2 .layui-table-cell",function(){
    toUserId = $(this).parent("td").data("content");
    changeToUser(toUserId);
})
// 筛选用户列表
$("#handleSearch").click(function(){
    layuiTableReload({
        id: "userList",
        where: {
            name: $("#searchName").val()
        }
    })
})
// 更多用户筛选
eventBind("#searchUser", function(){
    choiceUserIndex = layerOpenIframe({
        title: "查询用户",
        url: "../../htmllayer/choiceUser.html"
    })
})

// 选用通信用户回调
function choiceUserCallback(user){
    toUserId = user.id;
    if(toUserId == $.cookie("userId")){
        layer.msg("不能向自己发送消息！");
        return false;
    }
    if(actionType =='sendMsg'){
        rememberParam("toUserId", toUserId);
        getUserById(toUserId,function(user){
            toUser = user;
            getCallBetweenInfos();
        })
        layer.close(choiceUserIndex);
        return true;
    }
    location.href = "/htmlsrc/bench/notice/imNotice.list.html?toUserId="+toUserId+"&act=sendMsg";

}