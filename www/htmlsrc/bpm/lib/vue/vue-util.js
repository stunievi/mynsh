function loadComponent(name, src){
    if(typeof Vue === 'undefined'){
        return
    }
    Vue.component(name, function (resolve,reject) {
        $.get(src, function (html) {
            html = $(html);
            var script = html.filter("script");
            var template = html.filter("template");
            var style = html.filter("style");
            script = "(function(){ " + script.html() + "; return component; })();";
            var data = eval(script);
            data.template = template.html();
            resolve(data)
            if(style.length){
                $("head").append(style)
            }
        });
    });
}


function getParam(name, url) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    if(url){
        var r = url.substr(1).match(reg);
    }else{
        var r = window.location.search.substr(1).match(reg);
    }
    if (r != null) return r[2];
    return '';
}
