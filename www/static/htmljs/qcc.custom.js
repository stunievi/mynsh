// 获取url中的参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURIComponent(r[2]); return null;
}


function setFullScreenListener(fullScreenChange){
    document.addEventListener('fullscreenchange', function(){ fullScreenChange() });
    document.addEventListener('webkitfullscreenchange', function(){ fullScreenChange()});
    document.addEventListener('mozfullscreenchange', function(){ fullScreenChange()});
    document.addEventListener('MSFullscreenChange', function(){ fullScreenChange()});
}
function isFullScreen(){
    if(document.fullscreen){
        return true;
    }else if(document.mozFullScreen){
        return true;
    }else if(document.webkitIsFullScreen){
        return true;
    }else if(document.msFullscreenElement){
        return true;
    }else{
        return false;
    }
}
function launchFullScreen(element) {
    if(element.requestFullscreen) {
        element.requestFullscreen();
    }else if(element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    }else if(element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    } else if(element.msRequestFullscreen) {
        element.msRequestFullscreen();
    }
}
function exitFullScreen(){
    if(document.exitFullscreen){
        document.exitFullscreen();
    }
    else if(document.mozCancelFullScreen){
        document.mozCancelFullScreen();
    }
    else if(document.msExitFullscreen){
        document.msExitFullscreen();
    }
    else if(document.webkitCancelFullScreen){
        document.webkitCancelFullScreen();
    }
}


function animatieChart(myChart,targetX,targetY){
    targetX = targetX||0;
    targetY = targetY||0;
    var centerX = myChart.getZrender().getWidth()/2;
    var centerY = myChart.getZrender().getHeight()/2;
    var layer = myChart.getZrender().painter._layers[1];
    var animation = myChart.getZrender().animation;
    layer.scale = [0.2,0.2,centerX,centerY];
    layer.rotation = [0,centerX,centerY];
    layer.position = [targetX,targetY];
    myChart.getZrender().render();
    animation.animate(layer).when(400, {
        scale: [1,1,centerX,centerY]
    }).start('spline').done(function(){
        layer.scale[2] = 0;
        layer.scale[3] = 0;
    }).during(function(){
        myChart.getZrender().render();
    });
}

function download(canvas,type) {
    if(!!window.ActiveXObject || "ActiveXObject" in window){
        var blob = canvas.msToBlob();
        window.navigator.msSaveBlob(blob, new Date().toLocaleDateString() + '.png');
        return;
    }
    type = 'png';
    //设置保存图片的类型
    var imgdata = canvas.toDataURL(type);
    //将mime-type改为image/octet-stream,强制让浏览器下载
    var fixtype = function (type) {
        type = type.toLocaleLowerCase().replace(/jpg/i, 'jpeg');
        var r = type.match(/png|jpeg|bmp|gif/)[0];
        return 'image/' + r;
    }
    imgdata = imgdata.replace(fixtype(type), 'image/octet-stream')
    //将图片保存到本地
    var saveFile = function (data, filename) {
        var link = document.createElement('a');
        link.href = data;
        link.download = filename;
        var event = document.createEvent('MouseEvents');
        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        link.dispatchEvent(event);
    }
    var filename = new Date().toLocaleDateString() + '.' + type;
    saveFile(imgdata, filename);
}