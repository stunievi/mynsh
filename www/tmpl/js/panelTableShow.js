var TPanelTableShow = '<div class="panel panel-default"> <div class="panel-heading">{{panelTitle}}</div> <table class="table"> <tbody> {{each attrs value}} {{if $index%2 == 0}} <tr> <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info">{{data[value.prop]}}</td> {{else}} <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info">{{data[value.prop]}}</td> </tr> {{/if}} {{/each}} </tbody> </table> </div><style>table:{word-break: break-all !important; word-wrap: break-word !important;}</style>';

<<<<<<< HEAD
<<<<<<< HEAD
var TPanelTableShow2 = '<table class="table break-table"> <tbody> {{each attrs value}} {{if $index%2 == 0}} <tr> <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}}  <% if (typeof data[value.prop] !="string") { %>  {{each data[value.prop] vvv iii}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}{{iii}}" data-val="{{vvv["val"]}}">{{vvv["name"]}}</a>{{/each}}  <% } else{ %> <a href="javascript:;" class="label label-default" id="handle{{value.prop}}" data-val="{{value.val}}">{{data[value.prop]}}</a> <% } %> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> {{else}} <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <% if (typeof data[value.prop] !="string") { %>  {{each data[value.prop] vvv iii}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}{{iii}}" data-val="{{vvv["val"]}}">{{vvv["name"]}}</a>{{/each}}  <% } else{ %> <a href="javascript:;" class="label label-default" id="handle{{value.prop}}" data-val="{{value.val}}">{{data[value.prop]}}</a> <% } %>{{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> </tr> {{/if}} {{/each}} </tbody> </table> <style> table { word-break: break-all !important; word-wrap: break-word !important; } .table > tbody > tr > td { border-top: 0; border-bottom: 1px solid #ddd; }</style>';
=======
var TPanelTableShow2 = '<table class="table break-table"> <tbody> {{each attrs value}} {{if $index%2 == 0}} <tr> <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}">{{data[value.prop]}}</a> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> {{else}} <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}">{{data[value.prop]}}</a> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> </tr> {{/if}} {{/each}} </tbody> </table> <style> table { word-break: break-all !important; word-wrap: break-word !important; } .table > tbody > tr > td { border-top: 0; border-bottom: 1px solid #ddd; }</style>';;
>>>>>>> 1571af57a5ce375c6796e98c13aeab62032c9c4c
=======
var TPanelTableShow2 = '<table class="table break-table"> <tbody> {{each attrs value}} {{if $index%2 == 0}} <tr> <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}">{{data[value.prop]}}</a> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> {{else}} <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}">{{data[value.prop]}}</a> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> </tr> {{/if}} {{/each}} </tbody> </table> <style> table { word-break: break-all !important; word-wrap: break-word !important; } .table > tbody > tr > td { border-top: 0; border-bottom: 1px solid #ddd; }</style>';;
>>>>>>> 1571af57a5ce375c6796e98c13aeab62032c9c4c

var panelTableShowRender  = template.compile(TPanelTableShow);
var panelTableShowRender2 = (function (){
    var tmpl = template.compile(TPanelTableShow2);
    return function (options) {
        if(options.attrs){
            var keys = $.map(options.attrs, function (v) {
                return v.prop;
            });

            var arr1 = [], arr2 = [];
            $.each(options.attrs, function (i,v) {
                if(options.data['$' + v.prop]){
                    arr1.push(v);
                }
                else{
                    arr2.push(v);
                }
            })
            arr1 = arr1.sort(function (a,b) {
                return keys.indexOf(a.prop) - keys.indexOf(b.prop);
            })
            arr2 = arr2.sort(function (a,b) {
                return keys.indexOf(a.prop) - keys.indexOf(b.prop);
            });
            options.attrs = arr1.concat(arr2);
        }
        $.each(options.attrs || [], function (i,v) {
            if(v.prop && options.data && options.data["$" + v.prop]){
                options.data[v.prop] = options.data['$' + v.prop];
            }
        });
        return tmpl(options)
    }
})();
// (function () {
//     var p3 = null;
//     panelTableShowRender2 = function(data, callback, undefined){
//         var async = callback !== undefined;
//         data.swidth = $("html").width();
//         if(!p3){
//             var c = $.ajax({
//                 type: "get",
//                 url: "/tmpl/panel_table.html",
//                 cache:false,
//                 async: async,
//                 dataType: "text"
//                 , success: function(xml){
//                     if(async){
//                         p3 = template.compile(xml);
//                         callback(p3(data))
//                     }
//                 }
//             });
//             if(!async){
//                 var txt = (c.responseText);
//                 p3 = template.compile(txt);
//                 return p3(data);
//             }
//         }
//         else{
//             var html = p3(data);
//             if(async){
//                 callback(html);
//             }
//             else{
//                 return html;
//             }
//         }
//     }
// })();

