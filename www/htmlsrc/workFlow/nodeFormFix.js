function getFormAttrFromNode(workFlowNode){
    var nodeAttr = [];
    var nodeAttrKeys = [];
    if(typeof workFlowNode.node == "string"){
        workFlowNode.node = JSON.parse(workFlowNode.node)
    }
    if(workFlowNode.type == 'input'){
        // 资料节点
        var nodeContent = workFlowNode.node.content;
        $.each(workFlowNode.node.content, function (i,v) {
            nodeAttrKeys.push(v.ename) ;
            nodeAttr.push(v);
        })
        // for(var key in nodeContent){
        //     if(nodeContent.hasOwnProperty(key)){
        //         nodeAttrKeys.push(key);
        //         var item = nodeContent[key];
        //         nodeAttr.push(item);
        //     }
        // }
    }else if(workFlowNode.type == 'check'){
        // 审核节点
        var nodeContent = workFlowNode.node;
        var items = {};
        nodeContent.states.forEach(function(elm){
            items[elm.item] = elm.item;
        })
        nodeAttr.push({
            cname: nodeContent.question,
            ename: nodeContent.key,
            items: items,
            required: true,
            label: nodeContent.question,
            type: "select"
        })
        nodeAttr.push({
            type: 'textarea',
            cname: '审核意见',
            ename: nodeContent.ps
        });
        nodeAttrKeys = ["key", "ps"];
    }
    return {
        attr: nodeAttr,
        attrKeys: nodeAttrKeys
    };
}

function fixFormAttrToEnable(formAttrs,loanAccount){
    var fixAttrs = [],
        fixRules = [],
        datetimeAttrKeys = [];
        formAttrs.forEach(function(_item) {

        if(["taskId", "files"].indexOf(_item.type) >= 0){
            // 忽略字段
            return;
        }
        // 规则
        if(_item.rules){
            _item.rules.forEach(function(r){
                fixRules[_item.ename] = {
                    required: _item.required
                }
                if(r.rule == 'Size'){
                    fixRules[_item.ename]['rangelength'] = [r.min||'', r.max||''];
                }else if(r.rule == 'Pattern'){
                    fixRules[_item.ename]['reg'] = r.value;
                }
            })
        }

        var item = {}; // 本次待生成input
        // if(_item.type == "layer"){
        //     // 弹框选择类型
        //     item = {
        //         type: "hidden",
        //         prop: _item.ename,
        //     };
        //     addFormAttr.unshift({
        //         prop: _item.ename + "-name",
        //         label: _item.cname,
        //         inputGroup: "选择" + _item.cname,
        //         required: true
        //     });
        // }

        item = {
            prop: _item.ename, // key
            label: _item.cname, // 名
            required: _item.required,
            rules: _item.rules || [],
            show: _item.show
        };
        if(_item.type == "string"){
            //    PASS;
        }else if(_item.type == "EXT_DATA"){
            // ods数据,禁止编辑
            item.disabled = true;
        }else if(_item.type == "select"){
            item.type = "select";
            if(_item.vals){
                item.vals = _item.vals;
            }else{
                item.vals = [];
                for(var attr in _item.items){
                    if(_item.items.hasOwnProperty(attr)){
                        item.vals.push({
                            name: _item.items[attr],
                            val: _item.items[attr]
                            //val: attr
                        });
                    }
                }
            }
        }else if(_item.type == "textarea"){
            item.type = "textarea";
        }else if(_item.type == 'datetime'){
            item.disabled = true;
            datetimeAttrKeys.push(_item.ename);
        }
        else if(_item.type == 'diya' && loanAccount){
            getFetch(remoteOrigin + "/api/search/accloan/04?LOAN_ACCOUNT="+loanAccount+"&order=asc&size=1000&page=1", {},function (res) {
                item.vals = []
                $.each(res.list || [] ,function (i,v) {
                    item.vals.push({
                        name: v.GAGE_NAME + "(" + v.GUARANTY_ID + ")",
                        val: v.GAGE_NAME + "(" + v.GUARANTY_ID + ")"
                    })
                })
                item.type = 'checkbox'
            }, null, {
                sync: true
            })
        }
        else if(_item.type == 'checkbox'){
            item.type = "checkbox";
            if(_item.vals){
                item.vals = _item.vals;
            }else{
                item.vals = [];
                for(var attr in _item.items){
                    if(_item.items.hasOwnProperty(attr)){
                        item.vals.push({
                            name: _item.items[attr],
                            val: _item.items[attr]
                        });
                    }
                }
            }
        }
        fixAttrs.push(item);
    });
    return {
        fixAttrs: fixAttrs,
        fixRules: fixRules,
        datetimeAttrKeys: datetimeAttrKeys
    };
}