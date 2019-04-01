
var qccTableHandle = null;
var qccTableRender = function(data, callback, undefined){
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
