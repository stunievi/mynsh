
if(typeof WebUploader === "undefined"){
    document.write("<script type=\"text/javascript\" src=\"/static/vendor/webUploader/webuploader.min.js\"></script>");
}

function uploadFactory(conf){
    var config = {
        swf: '/static/vendor/webUploader/Uploader.swf',
        server: '', //上传接口
        pick: '#picker',
        auto: true,
        fileVal: 'file',
        formData: {},
        resize: false,
        dupliacate: false
    };
    for(var key in config){
        config[key] = conf[key] ? conf[key] : config[key];
    }
    var uploader = WebUploader.create(config);
    return uploader;
}