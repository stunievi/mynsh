var TPanelTableShow = '<div class="panel panel-default"> <div class="panel-heading">{{panelTitle}}</div> <table class="table"> <tbody> {{each attrs value}} {{if $index%2 == 0}} <tr> <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info">{{data[value.prop]}}</td> {{else}} <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info">{{data[value.prop]}}</td> </tr> {{/if}} {{/each}} </tbody> </table> </div><style>table:{word-break: break-all !important; word-wrap: break-word !important;}</style>';

var TPanelTableShow2 = '<table class="table"> <tbody> {{each attrs value}} {{if $index%2 == 0}} <tr> <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}">{{data[value.prop]}}</a> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> {{else}} <td class="col-xs-2 text-right"><b>{{value.label}}</b></td> <td class="col-xs-4 type-info"> {{if value.handle}} <a href="javascript:;" class="label label-default" id="handle{{value.prop}}">{{data[value.prop]}}</a> {{ else if value.tmpl }} <span id="tmpl{{value.prop}}"></span> {{else}} {{data[value.prop]}} {{/if}} </td> </tr> {{/if}} {{/each}} </tbody> </table> <style> table { word-break: break-all !important; word-wrap: break-word !important; } .table > tbody > tr > td { border-top: 0; border-bottom: 1px solid #ddd; }</style>';;

var panelTableShowRender  = template.compile(TPanelTableShow);
var panelTableShowRender2 = template.compile(TPanelTableShow2);
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

