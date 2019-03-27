
var hiscominfoHandle = null;
var hiscominfoRender = function(data, callback, undefined){
    var async = callback !== undefined;
    data.swidth = $("html").width();
    if(!hiscominfoHandle){
        var c = $.ajax({
            type: "get",
            url: "/htmlsrc/qichacha/tmpl/eci/hiscominfo.html",
            cache:false,
            async: async,
            dataType: "text"
            , success: function(xml){
                if(async){
                    hiscominfoHandle = template.compile(xml);
                    callback(hiscominfoHandle(data))
                }
            }
        });
        if(!async){
            var txt = (c.responseText);
            hiscominfoHandle = template.compile(txt);
            return hiscominfoHandle(data);
        }
    }
    else{
        var html = hiscominfoHandle(data);
        if(async){
            callback(html);
        }
        else{
            return html;
        }
    }
};
