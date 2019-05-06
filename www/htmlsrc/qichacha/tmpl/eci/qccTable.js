
var qccTableHandle = null;
var qccTableRender = function(data, callback, undefined){
    var cols = [];
    var cols2 = [];
    var keyIndex = 1;
    var keyItem;
    if(Object.prototype.toString.call(data["cols"]) === "[object Object]"){
        for(var key in data["cols"]){
            if(data["cols"].hasOwnProperty(key)){
                if(key === "data"){
                    cols = cols.concat(data["cols"]["data"]);
                }else{
                    data["cols"][key].forEach(function (item) {
                        data.data[key+"_"+item.field] = (data.data[key] || {})[item.field];
                        item.field = key+"_"+item.field;
                        cols.push(item);
                    })
                }
            }
        }
        data.cols = cols;
    }
    // 分组
    if(data["cols"] instanceof Array){
        (data["cols"] || []).forEach(function (item, index) {
            if(item.spec){
                // 特殊项独立一行
                keyIndex = 1;
                cols2.push([item]);
            }else{
                if(keyIndex === 1){
                    keyIndex = 2;
                    keyItem = [];
                    keyItem.push(item);
                    // 若是最后一项，则直接放入
                    if(index === data["cols"].length - 1 || data["cols"][index+1].spec){
                        cols2.push(keyItem);
                    }
                }else{
                    keyItem.push(item);
                    cols2.push(keyItem);
                    keyIndex = 1;
                    keyItem = [];
                }
            }
        });
    }
    data.cols = cols2;
    var async = callback !== undefined;
    data.swidth = $("html").width();
    if(!qccTableHandle){
        var c = $.ajax({
            type: "get",
            url: "/htmlsrc/qichacha/tmpl/eci/qccTable.html",
            cache:false,
            async: async,
            dataType: "text"
            , success: function(xml){
                if(async){
                    qccTableHandle = template.compile(xml);
                    callback(qccTableHandle(data))
                }
            }
        });
        if(!async){
            var txt = (c.responseText);
            qccTableHandle = template.compile(txt);
            return qccTableHandle(data);
        }
    }
    else{
        var html = qccTableHandle(data);
        if(async){
            callback(html);
        }
        else{
            return html;
        }
    }
};
