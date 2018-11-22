var TPanelForm = '<div class="panel panel-default"> {{if panelTitle }} <div class="panel-heading">{{panelTitle}}</div> {{/if}} <div class="panel-body"> <form name="{{formName ? formName : \'form-search\'}}"  class="form-horizontal" onsubmit="return checkSubmitForm(this)"> <div class="row panel-form-input-group"> {{each list value}} {{if value.type == \'hidden\'}} <input type="hidden"  name="{{value.prop}}" value="{{data[value.prop]}}"> {{else if value.manifest}} <div class="col-xs-12 form-group "> <label for="user" class="col-xs-2 text-right" style="width:16.22%">{{if value.required}} <span style="color:red">*</span> {{/if}} {{value.label}}</label> <div class="col-xs-6"> <input type="text" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> </div> <div class="col-xs-2"> <button class="btn btn-default btn-sm groupBtn" type="button" id="groupBtn-{{value.prop}}">{{value.manifest}}</button> </div> </div> {{else}} <div class="col-xs-{{value.width ? value.width : 6}} form-group "> <label for="{{value.prop}}" class="col-xs-{{value.labelWidth ? value.labelWidth : 4}} text-right"> {{if value.required}} <span style="color:red">*</span> {{/if}} {{value.label}} </label> {{if !value.type }} {{if value.inputGroup }} <div class="col-xs-8 input-group" style="padding-right: 15px; padding-left: 15px;"> <input type="text" class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> <span class="input-group-btn"> <button class="btn btn-default btn-sm groupBtn" type="button" id="groupBtn-{{value.prop}}">{{value.inputGroup}}</button> </span> {{else}} <div class="col-xs-8"> <input type="text" class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> {{/if}} {{else if value.type == \'checkbox\'}} <div class="col-xs-8" style="height:30px;"> <input type="checkbox" class="input-sm" id="{{value.prop}}" name="{{value.prop}}" {{data[value.prop] == true ? \'checked\' : \'\'}} value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> {{else if value.type == \'select\'}} <div class="col-xs-8"> <select class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}"> {{each value.vals val}} <option value="{{val.val}}" {{data[value.prop] == val.val ? \'selected\' : \'\'}} {{value.disabled ? \'readonly\' : \'\'}} >{{val.name}}</option> {{/each}} </select> {{else if value.type == \'textarea\'}} <div class="col-xs-8"> <textarea class="form-control input-sm" rows="3" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}>{{data[value.prop]}}</textarea> {{else if value.type == \'file\'}} <div class="col-xs-8"> <input type="file" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" style="height:30px;"> {{/if}} </div> </div> {{/if}} {{/each}} </div> <div class="row text-center"> {{ if button }} {{ each button btn }} {{ if btn.type == \'reset\'}} <button type="reset" class="btn btn-default">重 置</button> {{ else if btn.type == \'submit\'}} <button type="submit" class="btn btn-primary">{{btn.name ? btn.name : \'确 定\'}}</button> {{ else }} <a href="javascript:;" class="btn btn-default {{btn.class_name}}">{{btn.name}}</a> {{ /if }} {{ /each }} {{ else }} <button type="reset" class="btn btn-default">重 置</button> <button type="submit" class="btn btn-primary">确 定</button> {{ /if }} </div> </form> </div> </div>';

var TPanelForm2 = '<div class="col-xs-12" style="padding:15px;"> <form name="{{formName ? formName : \'form-search\'}}"  class="form-horizontal" onsubmit="return checkSubmitForm(this)"> <div class="row panel-form-input-group"> {{each list value}} {{if value.type == \'hidden\'}} <input type="hidden"  name="{{value.prop}}" value="{{data[value.prop]}}"> {{else if value.manifest}} <div class="col-xs-12 form-group "> <label for="user" class="col-xs-2 text-right" style="width:16.22%">{{if value.required}} <span style="color:red">*</span> {{/if}} {{value.label}}</label> <div class="col-xs-6"> <input type="text" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> </div> <div class="col-xs-2"> <button class="btn btn-default btn-sm groupBtn" type="button" id="groupBtn-{{value.prop}}">{{value.manifest}}</button> </div> </div> {{else}} <div class="col-xs-{{value.width ? value.width : 6}} form-group "> <label for="{{value.prop}}" class="col-xs-{{value.labelWidth ? value.labelWidth : 4}} text-right"> {{if value.required}} <span style="color:red">*</span> {{/if}} {{value.label}} </label> {{if !value.type }} {{if value.inputGroup }} <div class="col-xs-8 input-group" style="padding-right: 15px; padding-left: 15px;"> <input type="text" class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> <span class="input-group-btn"> <button class="btn btn-default btn-sm groupBtn" type="button" id="groupBtn-{{value.prop}}">{{value.inputGroup}}</button> </span> {{else}} <div class="col-xs-8"> <input type="text" class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> {{/if}} {{else if value.type == \'checkbox\'}} <div class="col-xs-8" style="height:30px;"> <input type="checkbox" class="input-sm" id="{{value.prop}}" name="{{value.prop}}" {{data[value.prop] == true ? \'checked\' : \'\'}} value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> {{else if value.type == \'select\'}} <div class="col-xs-8"> <select class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}"> {{each value.vals val}} <option value="{{val.val}}" {{data[value.prop] == val.val ? \'selected\' : \'\'}} {{value.disabled ? \'readonly\' : \'\'}} >{{val.name}}</option> {{/each}} </select> {{else if value.type == \'textarea\'}} <div class="col-xs-8"> <textarea class="form-control input-sm" rows="3" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}>{{data[value.prop]}}</textarea> {{else if value.type == \'file\'}} <div class="col-xs-8"> <input type="file" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" style="height:30px;"> {{/if}} </div> </div> {{/if}} {{/each}} </div> <div class="row text-center"> {{ if button }} {{ each button btn }} {{ if btn.type == \'reset\'}} <button type="reset" class="btn btn-default">重 置</button> {{ else if btn.type == \'submit\'}} <button type="submit" class="btn btn-primary">{{btn.name ? btn.name : \'确 定\'}}</button> {{ else }} <a href="javascript:;" class="btn btn-default {{btn.class_name}}">{{btn.name}}</a> {{ /if }} {{ /each }} {{ else }} <button type="reset" class="btn btn-default">重 置</button> <button type="submit" class="btn btn-primary">确 定</button> {{ /if }} </div> </form> </div>';

var TPanelForm3 = '<div class="panel panel-default"> {{if panelTitle }} <div class="panel-heading">{{panelTitle}}</div> {{/if}} <div class="panel-body"> <form name="{{formName ? formName : \'form-search\'}}"  class="form-horizontal" onsubmit="return checkSubmitForm(this)"> <div class="row panel-form-input-group"> {{each list value}} {{if value.type == \'hidden\'}} <input type="hidden"  name="{{value.prop}}" value="{{data[value.prop]}}"> {{else if value.manifest}} <div class="col-xs-12 form-group "> <label for="user" class="col-xs-2 text-right" style="width:16.22%">{{if value.required}} <span style="color:red">*</span> {{/if}} {{value.label}}</label> <div class="col-xs-6"> <input type="text" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> </div> <div class="col-xs-2"> <button class="btn btn-default btn-sm groupBtn" type="button" id="groupBtn-{{value.prop}}">{{value.manifest}}</button> </div> </div> {{else}} {{ if col == 3}} <div class="col-xs-{{value.width ? value.width : 4}} col-xs-6 col-sm-4 col-md-3 col-lg-2 form-group "> {{ else }} <div class="col-xs-{{value.width ? value.width : 6}} form-group "> {{ /if }} <label for="{{value.prop}}" class="text-right" style="width:{{value.labelWidth ? value.labelWidth : 110}}px;float:left;"> {{if value.required}} <span style="color:red">*</span> {{/if}} {{value.label}} </label> <div class="col-xs-8" style="padding-right: 15px; padding-left: 15px;height:30px;"> {{if !value.type }} {{if value.inputGroup }} <input type="text" class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> <span class="input-group-btn"> <button class="btn btn-default btn-sm groupBtn" type="button" id="groupBtn-{{value.prop}}">{{value.inputGroup}}</button> </span> {{else}} <input type="text" class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> {{/if}} {{else if value.type == \'checkbox\'}} <input type="checkbox" class="input-sm" id="{{value.prop}}" name="{{value.prop}}" {{data[value.prop] == true ? \'checked\' : \'\'}} value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}> {{else if value.type == \'select\'}} <select class="form-control input-sm" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}"> {{each value.vals val}} <option value="{{val.val}}" {{data[value.prop] == val.val ? \'selected\' : \'\'}} {{value.disabled ? \'readonly\' : \'\'}} >{{val.name}}</option> {{/each}} </select> {{else if value.type == \'textarea\'}} <textarea class="form-control input-sm" rows="3" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" {{value.disabled ? \'readonly\' : \'\'}}>{{data[value.prop]}}</textarea> {{else if value.type == \'file\'}} <input type="file" id="{{value.prop}}" name="{{value.prop}}" value="{{data[value.prop]}}" style="height:30px;"> {{/if}} </div> </div> {{/if}} {{/each}} </div> <div class="row text-center"> {{ if button }} {{ each button btn }} {{ if btn.type == \'reset\'}} <button type="reset" class="btn btn-default">重 置</button> {{ else if btn.type == \'submit\'}} <button type="submit" class="btn btn-primary">{{btn.name ? btn.name : \'确 定\'}}</button> {{ else }} <a href="javascript:;" class="btn btn-default {{btn.class_name}}">{{btn.name}}</a> {{ /if }} {{ /each }} {{ else }} <button type="reset" class="btn btn-default">重 置</button> <button type="submit" class="btn btn-primary">确 定</button> {{ /if }} </div> </form> </div> </div>';

var panelFormRender = template.compile(TPanelForm);
var panelFormRender2 = template.compile(TPanelForm2);
// var panelFormRender3 = template.compile(TPanelForm3);

var p3 = null;
var panelFormRender3 = function(data, callback, undefined){
    var async = callback !== undefined;
    data.swidth = $("html").width();
    if(!p3){
        var c = $.ajax({
            type: "get",
            url: "/tmpl/panel3.html",
            cache:false,
            async: async,
            dataType: "text"
            , success: function(xml){
                if(async){
                    p3 = template.compile(xml);
                    callback(p3(data))
                }
            }
        });
        if(!async){
            var txt = (c.responseText);
            p3 = template.compile(txt);
            return p3(data);
        }
    }
    else{
        var html = p3(data);
        if(async){
            callback(html);
        }
        else{
            return html;
        }
    }
};


$(document).on('click', '#panel-form .panel-heading', function(){
    $(this).siblings('.panel-body').toggle();
});

