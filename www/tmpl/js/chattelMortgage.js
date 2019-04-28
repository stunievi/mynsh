
var p4 = null;
var chattelMortgageRender = function(data, callback, undefined){
    var async = callback !== undefined;
    data.swidth = $("html").width();
    if(!p4){
        var c = $.ajax({
            type: "get",
            url: "/tmpl/html/chattelMortgage.html",
            cache:false,
            async: async,
            dataType: "text"
            , success: function(xml){
                if(async){
                    p4 = template.compile(xml);
                    callback(p4(data))
                }
            }
        });
        if(!async){
            var txt = (c.responseText);
            p4 = template.compile(txt);
            return p4(data);
        }
    }
    else{
        var html = p4(data);
        if(async){
            callback(html);
        }
        else{
            return html;
        }
    }
};
