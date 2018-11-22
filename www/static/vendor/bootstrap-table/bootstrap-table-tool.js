var sprintf = $.fn.bootstrapTable.utils.sprintf;

function btCreateModal(id, title, icon, btnText, columns) {
    var rid = "#avdSearchModal_" + id;
    if (!$(rid).hasClass("modal")) {
        var vModal = sprintf("<div id=\"%s\"  class=\"modal fade\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"mySmallModalLabel\" aria-hidden=\"true\">", rid.substr(1));
        vModal += "<div class=\"modal-dialog modal-xs\">";
        vModal += " <div class=\"modal-content\">";
        vModal += "  <div class=\"modal-header\">";
        vModal += "   <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\" >&times;</button>";
        vModal += sprintf("   <h4 class=\"modal-title\">%s</h4>", title);
        vModal += "  </div>";
        vModal += "  <div class=\"modal-body modal-body-custom\">";
        vModal += sprintf("   <div class=\"container-fluid\" style=\"padding-right: 0px;padding-left: 0px;\" >");

        vModal += btCreateForm(id, icon, btnText,columns).join("")

        vModal += "   </div>";
        vModal += "  </div>";
        vModal += "  </div>";
        vModal += " </div>";
        vModal += "</div>";

        $("body").append($(vModal));

        // var vFormAvd = btCreateForm(btnText, columns),
        //     timeoutId = 0;;

        // $('#avdSearchModalContent' + "_" + that.options.idTable).append(vFormAvd.join(''));

        // $('#' + that.options.idForm).off('keyup blur', 'input').on('keyup blur', 'input', function (event) {
        //     clearTimeout(timeoutId);
        //     timeoutId = setTimeout(function () {
        //         that.onColumnAdvancedSearch(event);
        //     }, that.options.searchTimeOut);
        // });

        var callbacks = $.grep(columns, function (v,i) {
            return typeof v == 'function' && v.name == 'onClick'
        });
        var initCallback = $.grep(columns, function (v) {
            return typeof v =='function' && v.name == 'onInit'
        });
        $.each(initCallback,function (i,v) {
            v.call(null, $(rid))
        });

        $(rid)
            .modal()
            .find("[class*=btnCloseAvd]")
            .off("click")
            .on("click",function (event) {
                var _this = this;
                $.each(callbacks,function (i,callback) {
                    callback.call(_this, $(rid))
                });
                // $(rid).modal("hide")
            });

        // $("#btnCloseAvd" + "_" + that.options.idTable).click(function() {
        //     $(rid).modal('hide');
        // });

        // $(rid).modal();
    } else {
        var initCallback = $.grep(columns, function (v) {
            return typeof v =='function' && v.name == 'onInit'
        });
        $.each(initCallback,function (i,v) {
            v.call(null, $(rid))
        });
        $(rid).modal();
    }
};

function btCreateForm(id, icon, btnText, columns) {
    var htmlForm = [];
    htmlForm.push(sprintf('<form class="form-horizontal" id="form-auto-add-%s" action="%s" >', id, ""));

    $.each(columns, function (i,vObjCol) {
        if(typeof vObjCol == 'function'){
            return
        }
        htmlForm.push('<div class="form-group">');
        htmlForm.push(sprintf('<label class="col-sm-4 control-label">%s</label>', vObjCol.title));
        htmlForm.push('<div class="col-sm-6">');
        htmlForm.push(sprintf('<input type="text" class="form-control input-md" name="%s" autocomplete="off" placeholder="%s" id="%s">', vObjCol.field, vObjCol.title, vObjCol.field));
        htmlForm.push('</div>');
        htmlForm.push('</div>');
    });
    // for (var i in pColumns) {
    //     var vObjCol = pColumns[i];
    //     if (!vObjCol.checkbox && vObjCol.visible && vObjCol.searchable) {
    //         htmlForm.push('<div class="form-group">');
    //         htmlForm.push(sprintf('<label class="col-sm-4 control-label">%s</label>', vObjCol.title));
    //         htmlForm.push('<div class="col-sm-6">');
    //         htmlForm.push(sprintf('<input type="text" class="form-control input-md" name="%s" placeholder="%s" id="%s">', vObjCol.field, vObjCol.title, vObjCol.field));
    //         htmlForm.push('</div>');
    //         htmlForm.push('</div>');
    //     }
    // }

    htmlForm.push('<div class="form-group" style="text-align: center">');
    htmlForm.push(sprintf('<button type="button" class="btn btn-primary btnCloseAvd_%s"><i class="glyphicon glyphicon-%s"></i> %s</button>', id, icon, btnText));
    htmlForm.push('</div>');
    htmlForm.push('</form>');

    return htmlForm;
};

var $ptr = 0;

function renderTable(selector, options) {
    var $table = $(selector);
    $.each(options.cols, function (i, col) {
        if (!col.align) {
            col.align = 'center';
        }
        if (col.templet || col.template) {
            col.formatter = function (value, row, index) {
                var _selector = (col.templet || col.template)
                var html = $(_selector).html();
            }
        }
    })
    var ops = {
        columns: options.cols
        // ,search: true
        // , advancedSearch: true
        // , idTable: "rile"
        , dataType: "json"
        , url: options.url
        , queryParams: function (params) {
            var ret = {
                size: params.limit,                         //页面大小
                page: (params.offset / params.limit) + 1,
            }

            if(params.filter){
                var obj = JSON.parse(params.filter)
                for(var i in obj){
                    ret[i] = obj[i]
                }
            }
            if(window.searchParams){
                var groups = window.searchParams.split("&");
                $.each(groups,function (i,v) {
                    var arr = v.split("=")
                    ret[arr[0]] = arr[1] || ''
                })
            }
            console.log(params)
            return ret;
        }
        , pagination: true
        , pageNumber: 1
        , pageSize: options.rows || 10
        , pageList: [10, 25, 50, 100]
        , sidePagination: "server"
        , responseHandler: function (res) {
            var ret = {
                rows: []
                , total: 0
            }
            if (!res.success) {
                return ret
            }
            $.each(res.data.content || res.data.list, function (i, item) {
                ret.rows.push(item)
            })
            ret.total = res.data.totalRow || res.data.totalElements || 0;
            return ret
        }
    };
    var toolbar = null;
    if (options.toolbar) {
        toolbar = $("<div>").attr("id", "toolbar");
        for(var i in options.toolbar){
            var item = options.toolbar[i]
            switch (i) {
                case "search":
                    break

                case 'remove':
                    toolbar.append(
                        $("<button>")
                            .append(
                                $("<i>").attr("class", "glyphicon glyphicon-remove")
                            )
                            .append(" 删除")
                            .attr({
                                id: "remove",
                                "class": "btn btn-danger",
                                "disabled": "disabled"
                            })
                            .css({
                                marginRight: 10
                            })
                            .on("click", function () {
                                var index = layer.confirm("一经删除不可撤销, 确定删除?", {icon:3, title: "提示"} ,function () {
                                    layer.close(index)
                                    var ids = $table.bootstrapTable("getSelections")
                                    options.toolbar.remove.call(this, ids)
                                })
                            })
                    );
                    break

                case "add":
                    toolbar.append(
                        $("<button>")
                            .append(
                                $("<i>").attr("class", "glyphicon glyphicon-plus")
                            )
                            .append(" 添加")
                            .attr({
                                id: "add",
                                "class": "btn btn-primary"
                            })
                            .css({
                                marginRight: 10
                            })
                            .on("click", function () {
                                btCreateModal("add", "添加字典", 'save', "确定", options.toolbar.add || [], function () {
                                })
                            })
                    )
                    break
            }
        }
        toolbar.appendTo($table.parent()).after($table)
        ops.toolbar = '#' + toolbar.attr("id")
        //补充columns
        ops.columns.unshift({
            field: '$state',
            checkbox: true,
            align: 'center',
            valign: 'middle'
        })

    }
    if (options.toolbar && options.toolbar.search) {
        ops.search = true
        ops.advancedSearch = true
        ops.idTable = 'idTable_' + ($ptr++)
        ops.searchForm = options.toolbar.search
    }

    var ret = $table.bootstrapTable(ops)
    $table.on('check.bs.table uncheck.bs.table ' +
        'check-all.bs.table uncheck-all.bs.table', function () {
        toolbar && toolbar.find("button:not([id=add])").prop('disabled', !$table.bootstrapTable('getSelections').length)
    });
    $table.on("search.bs.table", function (e) {
        console.log(e)
    })
    return ret;
}
