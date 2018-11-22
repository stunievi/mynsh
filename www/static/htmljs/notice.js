var TShowNotice = '<ul class="list-group"> <li class="list-group-item"> <div class="row"> <div class="col-xs-1"> <b>标题</b> </div> <div class="col-xs-10"> {{title}} </div> </div> </li> <li class="list-group-item"><div class="row"> <div class="col-xs-1"> <b>发送时间</b> </div> <div class="col-xs-10"> {{time}} </div> </div></li> <li class="list-group-item"> <div class="row"> <div class="col-xs-1"> <b>标题</b> </div> <div class="col-xs-10"> {{content}} </div> </div> </li> </ul>';
var showNoticeRender = template.compile(TShowNotice);

function delNoticeMsg(that){
    var value = $(that).data('value');
    console.log(value)
    delRemoteData({
      url: remoteApi.xxx+'?id='+value,
      callback: 'back'  
    })
}