var readys = [];
var promises = [];
var names = [];
var sources = {};

window.ArtComponent = {
    define: function (name, path, code) {
        names.push(name);
        promises.push($.ajax(
            {
                type: "get",
                url: path
            }
        ));
    },

    ready: function (callback) {
        readys.push(callback);
        $.when.apply($, promises).then(function () {
            for (var i = 0; i < arguments.length; i++) {
                sources[names[i]] = arguments[i];
            }
            readys.forEach(c = > {
                console.log(c)
            c();
        })
            readys = [];
        })
    }
}


//劫持系统参数
template.defaults.loader = function (componentName) {
    return sources[componentName];
}

