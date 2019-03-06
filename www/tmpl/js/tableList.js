var TTableList = '<div class="x-tablelist-wrap" style="overflow-x:auto"> <table class="table table-bordered x-table"> <thead> <tr> {{if select }} <th> <input type="checkbox" data-val="[]" id="sellect-all" name="sellect-all" class="x-checkall"> </th> {{/if}} {{each headCols val}} <th style="min-width:{{val.width ? val.width : 75}}px">{{val.label}}</th> {{/each}} </tr> </thead> <tbody> {{each bodyCols value index}} <tr class="cols-item" data-primary="{{value[primary ? primary : \'id\']}}"> {{if select }} <td> <input type="checkbox" name="ids[]" class="x-check" value="{{value[primary ? primary : \'id\']}}"> </td> {{/if}} {{each headCols val}} {{if val.prop }} <td>{{value[val.prop]}}</td> {{else}} <td class="handle-cols"> {{each handles v}} {{if v.cond }} {{if value[v.cond.key] == v.cond.val}} <a href="{{v.href ? v.href+value[primary ? primary : \'id\'] : \'javascript:;\' }}" class="btn btn-xs {{v.class_name}}" data-value="{{value[primary ? primary : \'id\']}}" data-index="{{index}}">{{v.name}}</a> {{/if}} {{else}} <a href="{{v.href ? v.href+value[primary ? primary : \'id\'] : \'javascript:;\' }}" class="btn btn-xs {{v.class_name}}" data-value="{{value[primary ? primary : \'id\']}}" data-index="{{index}}">{{v.name}}</a> {{/if}} {{/each}} </td> {{/if}} {{/each}} </tr> {{/each}} </tbody> </table> </div> <style> .ui-paging-container { color: #666; font-size: 12px } .ui-paging-container ul { overflow: hidden; text-align: center } .ui-paging-container li,.ui-paging-container ul { list-style: none } .ui-paging-container li { line-height: 26px; height: 26px; display: inline-block; padding: 0 12px; margin-left: 5px; color: #666 } .ui-paging-container li.ui-pager { cursor: pointer; border-radius: 2px } .ui-paging-container li.focus, .ui-paging-container li.ui-pager:hover { background-color: #1E9FFF; color: #FFF } .ui-paging-container li.js-page-action:hover{ background-color: #fff; color: #000; } .ui-paging-container li.ui-paging-ellipse { border: none } .ui-paging-container li.ui-paging-toolbar { padding: 0 } .ui-paging-container li.ui-paging-toolbar select { height: 26px; border: 1px solid #ddd; border-radius:2px; color: #666 } .ui-paging-container li.ui-paging-toolbar input {outline:none; line-height: 26px; height: 26px; padding: 0; border: 1px solid #ddd; border-radius:2px; text-align: center; width: 40px; margin: 0 6px 0 6px; vertical-align: top } .ui-paging-container li.ui-paging-toolbar a { vertical-align: middle; text-decoration: none; display: inline-block; height: 26px; border: 1px solid #ddd; vertical-align: top; border-radius: 2px; line-height: 26px; padding: 0 10px; cursor: pointer; margin-left: 5px; color: #666 } .ui-paging-container li.ui-pager-disabled,.ui-paging-container li.ui-pager-disabled:hover { background-color: #f6f6f6; cursor: default; border: none; color: #ddd } </style> <div class="x-pagination"></div>';

var tableListRender  = template.compile(TTableList);

$(document).on("click", ".x-table input[type='checkbox']", function(){
    var that = this;
    var check_arr = [];
    if(that.name == "sellect-all"){
        $(".x-check").prop("checked", that.checked);
    }
    else{
        $(".x-checkall").prop("checked", $(".x-check:checked").length == $(".x-check").length);
    }
    $(".x-check:checked").each(function(index, elm){
        check_arr.push($(elm).val());
    });
    $(".x-checkall").data('val', check_arr);
});