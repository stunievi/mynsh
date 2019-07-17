
if(typeof WebUploader === "undefined"){
    document.write("<script type=\"text/javascript\" src=\"/static/vendor/webUploader/webuploader.min.js\"></script>");
}

function uploadFactory(conf){
    conf["defaultFun"] = conf["defaultFun"]===false ? false : true;
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
    // init
    var uploader = WebUploader.create(config);
    //
    if(typeof conf["beforeFileQueued"] === "function"){
        uploader.on("beforeFileQueued", function(file){
            conf["beforeFileQueued"](file)
        });
    }else if(conf["defaultFun"]!==false){
        uploader.on("beforeFileQueued", function (file) {
            if(file["size"] < 1){
                layer.msg("空文件！");
                return false
            }
        });
    }

    if(typeof conf["uploadBeforeSend"] === "function"){
        uploader.on("uploadBeforeSend", function(obj, data, headers){
            conf["uploadBeforeSend"](obj, data, headers)
        });
    }else if(conf["defaultFun"]!==false){
        uploader.on("uploadBeforeSend", function(obj, data, headers){
            layer.msg("上传中...");
        });
    }
    if(typeof conf["uploadError"] === "function"){
        uploader.on('uploadError', function (file, res) {
            conf["uploadError"](file, res);
        });
    }else if(conf["defaultFun"]!==false){
        uploader.on('uploadError', function (file, res) {
            layer.msg("上传失败！");
            uploader.removeFile(file)
        });
    }

    if(typeof conf["uploadSuccess"] === "function"){
        uploader.on('uploadSuccess', function (file, res) {
            conf["uploadSuccess"](file, res);
        });
    }else if(conf["defaultFun"]!==false){
        uploader.on('uploadSuccess', function (file, res) {
            if(res.success){
                layer.msg("正在处理，系统稍后会将导入结果发送至收件箱，请注意查收！");
            }else{
                layer.msg("失败！请查看文件格式是否正确！");
            }
            uploader.removeFile(file)
        });
    }

    return uploader;
}