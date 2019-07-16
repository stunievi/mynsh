

// 查看客户详情
function showClientInfo(clientId, clientName, clientType){
    var navTabId = "com-user-" + clientId;
    var url = hrefUrl.comClientInfo;
    if(clientType === "indiv"){
        navTabId = "indiv-user-" + clientId;
        url = hrefUrl.indivClientInfo;
    }
    addNavTab(navTabId, "客户详情 - " + clientName , url + clientId);
    return true;
}

// 跳转显示菜单
function redirectShowMenu(topMenuId, sideMenuId){
    if(document.getElementById(topMenuId)==null){
        layer.msg("您没有此菜单[#"+topMenuId+"]权限！");
        return false;
    }
    $("#"+topMenuId).click();
    var zTree = $.fn.zTree.getZTreeObj("x-wrap-menu");
    var dom = zTree.getNodeByParam("id", sideMenuId);
    if(dom==null){
        layer.msg("您没有此菜单["+sideMenuId+"]权限！");
        return false;
    }else{
        $("#" + dom.tId + "_a").click();
    }
}

function addNavTab(id, tabName, url,args,callback){
    if(arguments.length === 2){
        var md5Id = md5(tabName);
        top.addTab(md5Id, id, tabName);
    }else{
        /*
        id:       tab页签的html标签ID属性格式为"tab-"+id，内容容器的html标签ID格式为"tab-content-"+id
        tabName:     tab页签的显示文本
        url:      打开的iframe的url
        */
        var md5Id = typeof md5 === "undefined" ? id: md5(id);
        top.addTab(md5Id, tabName, url,args,callback);
    }
}

// 获取用户某个key的val
function getUserValById(uid, key){
  key = key || "name";
  var user = top.topCacheUserObj[uid] || {};
  return user[key] || "未知用户";
}

// 获取用户token
function getUserToken(){
    return $.cookie('authorization');
}

// 获取用户简单信息
function getUserById(uid, callback){
    var user;
    if(uid){
        user = top.topCacheUserObj[uid] || {};
    }else{
      user = top.topCacheUserInfo || {};
      user.name = user.trueName;
    }
    if(callback){
        if(JSON.stringify(user) === "{}"){
            getRemoteData({
                url: remoteApi.apiUserIds + "?ids=" + uid,
                callback: function (origin) {
                    var u = origin[0] || {};
                    u.name = u.trueName;
                    top.topCacheUserObj[uid] = u;
                    callback(u);
                }
            })
        }else{
            callback(user);
        }
    }else{
        return user;
    }
}
// 设置用户岗位
function setUserQuarters(uid, quarters){
  top.topCacheUserList.forEach(function(user){
      if(user.id == uid){
          user.quarters = quarters
      }
  });
  top.topCacheUserObj[uid].quarters = quarters;
  return true;
}
// 得到部门信息
function transformDeptId(deptId, key){
  key = key || "name";
  var dept = top.topCacheDepartmentObj[deptId];
  return dept[key];
}

// layer打开新的页面
function layerOpenIframe(options){
  var title = options.title || '';
  var url = options.url || '';
  var btn = options.btn === true ? ["确定"] : undefined;
  var width = options.w || options.wp || '840';
  var height = options.h || '450';
  if(width.indexOf("%") == -1 && width.indexOf("px") == -1){
      width = width + 'px';
  }
  var shade = options.shade === false ? false : [0.2];
  return layer.open({
      type: 2,
      title: title,
      area: [ width, height+'px'],
      shadeClose: true,
      shade: shade,
      fixed: false, //不固定
      maxmin: true,
      content: url,
      btn: btn,
      yes:function(id){
          var win = $("[times=" + id + "] iframe")[0].contentWindow;
        if(win.onLayerConfirmlCallback){
          win.onLayerConfirmlCallback();
        }
        return true;
      },
      cancel: function(id){
          var win = $("[times=" + id + "] iframe")[0].contentWindow;
          if(win.onLayerCancelCallback){
              win.onLayerCancelCallback();
          }
      },
      zIndex: layer.zIndex, //重点1
      success: function(layero){
        layer.setTop(layero); //重点2
      }
  });
}

// 替换消息内容占位符
function replaceNoticeContent(obj, key){
  var bindData = obj.bindData;
  key = key || 'content';
  return obj[key].replace(/\$\{(.+?)\}/g,function(a,b){
    if(b == "taskId"){
      return "<a href='/htmlsrc/workFlow/nodeDeal.html?id="+bindData.taskId+"'><b>["+bindData.taskName+"]</b></a>";
    } else if(b == "nodeId") {
      return bindData.nodeName;
    }
  })
}

function getEnumVal(originData, data, key, style){
  var k = (data && typeof data == "object") ? data[key] : data;
  var obj = originData[key] || {};
  var val = obj[k];
    if(style){
        return val ? k + "(" + val + ")" : null;
    }else{
        val = val ? val : "无字典";
        return val ? k + "(" + val + ")" : null;
    }
}
function getGlobalEnumVal(data, key, style){
  return getEnumVal(globalEnumData, data, key, style);
}
// 得到ods枚举变量val
function getOdsEnumVal(data, key, style){
  return getEnumVal(odsEnmuData, data, key, style);
}
// 格式化ods中的枚举数据
function transformOdsEnumData(data, keys){
  if(data instanceof Array){
    data.forEach(function(item){
        keys.forEach(function(key){
          if(odsEnmuData[key] && item[key]){
            item[key] = odsEnmuData[key][item[key]];
          }
        })
    })
    return true;
  }else{
    keys.forEach(function(key){
      var obj = odsEnmuData[key] || {};
      data[key] = obj[data[key]] || "";
    })
    return true;
  }
}

function translateOdsInData(data){
    if(data instanceof Array && data.length > 0){
        data.forEach(function(item){
            for (var key in item){
                if(item.hasOwnProperty(key)){
                    var v = getOdsEnumVal(item[key], key, true);
                    v = v ? v : item[key];
                    item[key] = v;
                }
            }
        })
    }else if(typeof data == "object"){
        for (var key in data){
            if(data.hasOwnProperty(key)){
                var v = getOdsEnumVal(data[key], key, true);
                v = v ? v : data[key];
                data[key] = v;
            }
        }
    }
}

// 将枚举数据转化为select
function getEnmuSelect(data, keyName, showNone){
  showNone = showNone || true;
  showNone = showNone === false ? false : true;
  var data = data[keyName];
  var vals = [];
  if(showNone === true){
    vals.push({name:'--请选择--', val:''});
  }
  if(data instanceof Array){
    data.forEach(function(v){
      vals.push({ name: v, val: v })
    })
  }else {
    for(var v in data){
      vals.push({ name: data[v], val: v })
    }
  }
  return vals;
}
// 将ods枚举变量转化为select
function getOdsEnumSelect(keyName, showNone){
  return getEnmuSelect(odsEnmuData, keyName, showNone);
}
// 将全局枚举变量转化为select
function getGlobalEnumSelect(keyName, showNone){
  return getEnmuSelect(globalEnumData, keyName, showNone);
}
// 得到一个随机数
function getRandomNum(d){
  var d = d || 100;
  return Math.floor(Math.random()*d);
}

// 设置选中的岗位
function setCheckedQuarter(dataList, checkedArr){
  var l = dataList.length;
  while(l-- > 0){
      if(checkedArr.indexOf(dataList[l].partid) >= 0){
          dataList[l].checked = true;
      }
      if(typeof dataList[l].children != 'undefined'){
          if(dataList[l].children.length > 0){
              setCheckedQuarter(dataList[l].children, checkedArr)
          }
      }
  }
}
// 获取岗位和部门合并后的列表
function getMergeDeptAndQuarter(){
  if(top.topCacheDepartmentAndQuarter.length > 0){
    return JSON.parse(JSON.stringify(top.topCacheDepartmentAndQuarter));
  }else{
    var quarterList = JSON.parse(JSON.stringify(top.topCacheDepartment));
    // 将岗位，部门整合为同一个树
    var parseTree = function(treeNodes){
        if (!treeNodes || !treeNodes.length) return;
        treeNodes.forEach(function(elm, index){
            var childs = elm.children;
            var quarters = elm.quarters;
            elm.chkDisabled = elm.partid == undefined ? true: false; // 部门不能被选取
            if(childs){
                if(quarters.length>0){
                  quarters.forEach(function(elm2){
                    treeNodes[index].children.push({
                        name: elm2.name,
                        partid: elm2.id
                    });
                  })
                }
                parseTree(childs);
            }
        })
    };
    parseTree(quarterList);
    top.topCacheDepartmentAndQuarter = quarterList;
    return JSON.parse(JSON.stringify(quarterList));
  }
}

// 遍历树
function getDataFromTree(data, key, val) {
  var dataList = [];
  dataList = JSON.parse(JSON.stringify(data))
  while(dataList.length > 0){
    node = dataList.shift();
    if(node[key] == val){
        return node;
        break;
    }
    if(node.children && node.children.length > 0){
        node.children.forEach(function(element) {
            dataList.push(element);
        });
    }
  }
  return {};
}
/*
  添加多个值
*/
function choiceManifest(options){
  var domName = options.domName;
  var nameKey  = options.name || 'name';
  var idKey  = options.idKey || 'id';
  var getData = options.getData || 'user';
  var marcoPolo = null;
  if(getData === 'user'){
      // 手动劫持数据请求，自行ajax请求生成数据
      marcoPolo = {
          formatStatic:true,
          formatItem: function(data){
            // 配置列表显示数据key
            return data[nameKey]
          },
          formatData: function () {
              return top.topCacheUserList.filter(function(elm){
                // 输入关键词，过滤符合条件的数据
                if(elm[nameKey]){
                  return elm[nameKey].indexOf($(domName).val()) >= 0
                }
              });
          },
          minChars: 1 // 最少输入一位字符
      }
  }else if(typeof getData == 'function'){
      marcoPolo = {
          formatStatic:true,
          formatData: function () {
              return getData();
          },
          minChars: 1
      }
  }
  else{
    // 为false时，手动输入时
    marcoPolo = false;
  }
  // 详见：[jquery-manifest](https://github.com/jstayton/jquery-manifest)
  $(domName).manifest({
      formatDisplay: function (data, $item, $mpItem) {
        // 配置选中后显示数据key
        return data[nameKey];
      },
      formatValue: function (data, $value, $item, $mpItem) {
        // 配置显示数据的唯一id
          return data[idKey];
      },
      onAdd:function(data, $item, initial){
          var values = $(domName).manifest('values');
          for(var i=0;i<values.length;i++){
              if(values[i] == data.id){
                // 添加时删除重复数据
                $(domName).manifest('remove', ':eq('+i+')');
                break;
              }
          }
          return data;
      },
      onRemove:function(data, $item){
          var remove = options.remove;
          if(remove){
              remove(data, $item);
          }
      },
      marcoPolo: marcoPolo
  });
}

function loginOut(){
    top.layer.load();
  var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
  if(keys) {
      for(var i = keys.length; i--;)
          document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString()
  };
  if(localStorage){
      localStorage.clear()
  }
  getFetch("/api/logout");

  setTimeout(function () {
      top.location.href = "/login.html";
  },1000)
}

// 向服务器提交数据框架
function postFrameFun(options){
  var type = options.type; // 方法
  var url = options.url; // 地址
  var data = options.data; // 数据
  var dt = options.dt || 'traditional'; // content-type类型
  var callback = options.callback; // 请求成功回调
  var error = options.error; // 发生错误回调
  var loading = options.loading; // 加载中..
  if(loading){
    var indexLoading = layerLoad();
  }
  if(callback == 'back'){
    callback = function(){ history.go(-1); }
  }else if(callback == 'render'){
    callback = function(){
      tableListRenderObj.param='';
      if(type == 'post') layer.msg("添加成功");
      if(type == 'put') layer.msg("更新成功");
      if(type == 'delete') layer.msg("删除成功");
      initTableList(tableListRenderObj);
    }
  }else if(callback == 'layrender'){
    callback = function(){
      layuiTableReload();
    }
  }else if(callback == 'none'){
    callback = function(){ };
  }else if(callback == 'reload'){
    callback = function(){ location.reload();}
  }
  var request = {
    urlType: 'self',
    type: type,
    data: data,
    dt: dt,
    url: url,
    success:function(res){
        if(res.success){
          var origin = res.data;
          callback(origin);
        } else{
          if(error){
              error(res)
          }else{
            if(res.errMessage == "权限验证失败"){
              layer.msg(res.errMessage || "访问链接或传递参数错误！", {icon: 5}, function(){
                //loginOut();
              });
            }else if(res.errMessage){
              res.errMessage = res.errMessage || "访问链接或传递参数错误！";
              layErrorMsg(res.errMessage);
            }
          };
        }
        // 关闭加载页
        if(loading && indexLoading){
          layer.close(indexLoading);
        }
    },
    error:function(err){
      if(loading){
        layer.close(indexLoading);
      }
      if(err && error){
        error(err);
      }else if(err){
        if(err.status == 502 || err.status == 0){
          layErrorMsg("与服务器断开连接！");
            //loginOut();
        }else{
          layErrorMsg();
          console.error(err)
          console.error(url)
        }
      }

    }
  }
  return SuperPost(request);
}
// 向服务器提交【添加】请求
function postRemoteData(options){
  options.confirm === true ? true : false;
  options.type = 'post';
  // 判断是否操作提醒
  if(options.confirm){
    actConfirm(function(){
      postFrameFun(options);
    },options.confirmInfo)
  }else{
    postFrameFun(options);
  }
}
// 向服务器提交【更新】请求
function putRemoteData(options){
  options.type = 'put';
  // 判断是否操作提醒
  if(options.confirm){
    actConfirm(function(){
      postFrameFun(options);
    },options.confirmInfo)
  }else{
    postFrameFun(options);
  }
}
// 向服务器提交【删除】请求
function delRemoteData(options){
  options.type = 'delete';
  actConfirm(function(){
    postFrameFun(options);
  })
}
// 向服务器提交【获取】请求
function getRemoteData(options){
  options.loading = options.loading || false;
  options.type = 'get';
  options.callback = options.callback || function () {};
  return postFrameFun(options)
}

// 封装jq绑定dom事件
function eventBind(target, callback, type){
  if(type == undefined){
      type = 'click';
  }
  $(document).on(type, target, function(event){
      callback($(this), event);
  });
}

function bindInputEvent(selector, callback) {
    var inputTimer = null;
    $(document).on("input",selector, function () {
        if(inputTimer != null){
            clearTimeout(inputTimer)
            inputTimer = null
        }
        var _this = this;
        inputTimer =  setTimeout(function () {
            callback.call(_this)
            inputTimer = null
        },50)
    });

    //for ie
    if(document.all){
        $(selector).each(function() {
            var that=this;
            if(this.attachEvent) {
                this.attachEvent('onpropertychange',function(e) {
                    if(e.propertyName!='value') return;
                    $(that).trigger('input');
                });
            }
        })
    }

    bindLongDelete(selector)
}

function bindLongDelete(selector) {
    var clearTimer = null;
    $(document).on("keydown",selector,function (e) {
        var _this = $(this)
        if(e.keyCode == 8){
            clearTimer = setTimeout(function () {
                _this.val("").trigger("input")
            },1000);
        }
        console.log(e.keyCode)
    })
    $(document).on("keyup", selector, function (e) {
        clearInterval(clearTimer)
    })
}

/*
  需要固定的列必须放在数组首列，
  固定于左侧的列将筛选后放置于cols[0]头部
  固定于右侧的列将筛选后放置于cols[0]尾部
  详见【http://www.layui.com/doc/modules/table.html】
*/

function laytableRender(options, undefined){
    var id = options.id || "dataList";
    var elem = options.elem ? $(options.elem) : $("#" + id);
    var url = options.url;
    var rightFixedNum = 0;

    //处理url
    if(options.urlType == 'disk'){
        url = remoteClound + url;
    }

    //处理elem
    if(elem[0].tagName != 'TABLE'){
        elem.append("<table id='table-"+id+"'></table>")
        elem = elem.find("table");
    }

    if(options.showIndex){
        options.cols[0].unshift({
            title: '序号',
            align: 'center',
            setWidth: 40,
            formatter: function(value, row, index) {
                return index + 1;
            }
        });
    }

    //处理cols
    $.each(options.cols, function (i,v) {
        $.each(v, function (ii,vv) {
            if(vv && vv.templet){
                if(typeof vv.templet == "function"){
                    vv.formatter = function (value, row, index) {
                        return vv.templet(row, index);
                    }
                }else{
                    var templet = $(vv.templet).html();
                    vv.formatter = function (index,row) {
                        var html = layui.laytpl(templet).render(row)
                        return html;
                    }
                }

            }
            if(!vv.valign){
                vv.valign = 'middle'
            }
            //money
            if(vv.money && !vv.templet){
                vv.formatter = function (index,row) {
                    var val = (Number(row[vv.field] || 0)).toString();
                    var pointIdex = val.indexOf(".");
                    var tail = "";
                    var idex = val.length;
                    if(pointIdex > -1){
                        tail = val.substr(pointIdex);
                        idex = pointIdex;
                    }
                    var buf = [];
                    var ptr = 0;
                    while(idex-- > 0){
                        buf.push(val[idex]);
                        if(++ptr % 3 == 0 && idex != 0){
                            buf.push(",");
                        }
                    }
                    return buf.reverse().join("") + tail;
                }
            }
            //fix width
            if(vv.width){
                var w = vv.width;
                // vv.formatter = function (value,row,index) {
                //     var div = "<div style='width:"+w+";'>"+value+"</div>";//调列宽，在td中嵌套一个div，调整div大小
                //     return div;
                // }
                vv.cellStyle = {
                    css:{
                        "min-width": w +"px"
                    }
                }
                delete vv.width;
                // "min-width:" + vv.width + "px";
                // delete vv.width;
            }
            if(vv["setWidth"]){
                var w = vv.setWidth;
                vv.cellStyle = {
                    css:{
                        "width": w +"px"
                    }
                }
                delete vv["setWidth"];
            }
            if(vv.fix == 'right'){
                rightFixedNum++;
            }
        })
    });

    //修复少侠的bug
    var selector = options.form || "[name=form-search]";

    var usepage = options.page === undefined;

    var ops = {
        url: url
        , dataType: options.dataType == 'json' ? "json" : "jsonp"
        , columns:options.cols
        ,queryParams: function (data) {
            // console.log(arguments)
            var where;
            if($(selector).length){
                where = ($('[name=form-search]').ghostsf_serialize())
                if(options.report || (options.url && options.url.indexOf("/api/report") > -1) ){
                    where = where.replace(/(\d+)-/g,"$1");
                }
                where = $.unserialize(where);
            }
            else{
                where = {};
            }
            if(data.limit !== undefined && data.offset !== undefined){
                //私有云处理
                if(options.urlType == 'disk'){
                    data.pageSize = data.limit;
                    data.page = (data.offset / data.limit) + 1;
                }
                else{
                    data.size = data.limit;
                    data.page = (data.offset / data.limit) + 1;
                }
                delete data.limit;
                delete data.offset;
            }
            //附加默认参数
            for(var i in where){
                data[i] = where[i];
            }
            return data;
        }
        , ajax: function (request) {
            var data = request.data;
            if(options.where){
                for(var i in options.where){
                    data[i] = options.where[i];
                }
            }
            // data["__v"] = Math.random();
            // if(url.indexOf("?")>-1){
            //     url += "&__v="+Math.random();
            // }else{
            //     url +="?__v="+Math.random();
            // }
            var ajaxOps = ({
                url: request.url
                , dataType:"json"
                // , dataType: request.dataType
                // , jsonp: "callback"
                , headers: {
                    Token: top.store.get("token")
                }
                , data: data
                , success: function (res) {
                    var rows = ((res.code || res.success) && res.data) ? (res.data.content || res.data.list || res.data) : (res.rows || []);
                    var total = ((res.code || res.success) && res.data) ? (res.data.totalElements || res.data.totalRow || res.data.length) : (res.total || 0);
                    var ret = usepage ? {
                        rows: rows
                        , total: total
                    } : rows;

                    if(options.onData){
                        var temp_ret = options.onData(ret);
                        if(temp_ret){
                            ret = temp_ret;
                        }
                    }
                    elem.bootstrapTable("load", ret);

                    if(options.success){
                        options.success(ret);
                    }
                }
                , error: function (err) {
                    // var ret = JSON.parse(err.responseText)
// alert(console.log(err,ret))
                }
                , complete: function () {
                    elem.bootstrapTable("hideLoading")
                }
            });
            if(options.urlType == 'disk'){
                ajaxOps.dataType = 'json';
            }
            //容错, 报表第一次不加载
            if((ajaxOps.url && ajaxOps.url.indexOf("/api/report") > -1) || options.needCond){
                window.$tableRender = window.$tableRender || {};
                window.$tableRender[id] = window.$tableRender[id] || 0;
                if(window.$tableRender[id]++ == 0){
                    $('.fixed-table-loading').text("请设置搜索条件进行统计")
                    return;
                }
            }
            $.ajax(ajaxOps);
        }
        , onPostBody: function() {
            options.onRender && options.onRender();
        }
        // , responseHandler: function (res) {
        //     alert(123)
        // }
    };

    if(options.showColumns){
        ops.showColumns = true;
    }


    //处理静态数据
    if(options.data){
        delete ops.url;
        delete ops.ajax;
        ops.data = options.data;
        ops.onPreBody = function () {
            if(options.onData instanceof Function){
                options.onData();
            }
        };
    }

    //处理分页
    if(usepage){
        ops.pagination = true;
        ops.showJumpto = true;
        if(options.data){
            ops.sidePagination = "client";
        }else{
            ops.sidePagination = "server";
        }

        ops.pageNumber = 1;
        ops.pageSize = 10;
        ops.pageList = [10,20,30,40,50];
    }

    //树
    if(options.tree){
        var arr = options.tree.split(",")
        ops.treeShowField = arr[0];
        // ops.parentIdField = arr[1];
        // ops.onLoadSuccess = function () {
        //     elem.treegrid({
        //         // initialState: 'collapsed',
        //         treeColumn: arr[2],
        //         // expanderExpandedClass: 'glyphicon glyphicon-minus',
        //         // expanderCollapsedClass: 'glyphicon glyphicon-plus',
        //         onChange: function() {
        //             elem.bootstrapTable('resetWidth');
        //         }
        //
        // });

            // $(".treegrid-expander-expanded").click()
        // }
        ops.dataType = "json";
        delete ops.ajax;
    }

    //冻结列
    // if(rightFixedNum > 0){
    //     ops.fixedColumns = true;
    //     ops.fixedFrom = 'right';
    //     ops.fixedNumber = rightFixedNum;
    // }
    // console.warn(ops)

    // TODO::
    elem.bootstrapTable(ops);

    // '#dataList';
    return;

}

function checkSubmitForm(){
  var form = $("form[name='form-search']")
  layuiTableReload({
      where: form.serialize()
  })
  return false;
}

function layTableRenderStaticData(options){
  if(typeof layui == "undefined"){
    console.error("请引入layui.js");
  }else{
    var layout = options.layout || ['prev','page','limit','skip']
    var laypage = options.page === undefined ? {
        theme:'#1E9FFF',
        first: '首页',
        last: '末页',
        prev: '上一页',
        next: '下一页',
        layout: layout
    } :  false;
    layui.use('table', function(){
      layui.table.render({
        limit: options.limit,
        cellMinWidth: options.cellMinWidth,
        id: options.id || "dataList",
        elem: options.elem || "#dataList",
        cols: options.cols || [],
        data: JSON.parse(JSON.stringify(options.data)) || [],
        page: laypage
      })
    })
  }
}

// laytable重载
function layuiTableReload(options){
    options = options || {};
    var where = options.where || undefined;
    if(typeof layCurrPage == "undefined"){
      layCurrPage = 1;
    }
    var page = options.page == undefined ? { curr: layCurrPage } : options.page;
    var resHandler = options.resHandler;
    var success = options.success;
    var urlType = options.urlType || "self";
    var id = options.id || 'dataList';
    if(typeof where === 'string'){
      where = $.unserialize(where)
        var obj = {};
        for(var i in where){
            var n = i
            var v = where[i]
            if(typeof n == "string" && n.indexOf("flip-") > -1){
                n = n.replace("flip-","")
                if(v == 0) v = 1
                else if(v == 1) v = 0
                else if(v == '') v = 1
                else v = !v
            }
            obj[n] = v
        }
        where = obj
    }
    var elem = $("#" + id);
    elem = elem.is("table") ? elem : elem.find("#table-" + id);
    var ops = {
        query: where
        ,silent: true
    };
    if(options.data){
        elem.bootstrapTable("load", options.data);
        elem.bootstrapTable("hideLoading");
        return;
    }
    elem.bootstrapTable("refresh",ops);
    return;
}
// 获取选中ids
function getLayuiTabelCheckIds(keyName, elemIdName){
  // layuiTable的ID
  elemIdName = elemIdName == undefined ? 'dataList' : elemIdName;
  keyName = keyName === undefined ? 'id' : keyName;
  var elem = $("#"+elemIdName);
  elem = elem.is("table") ? elem : elem.find("#table-" + elemIdName);
  var selections = elem.bootstrapTable("getAllSelections");
  return $.map(selections, function (v) {
      return v[keyName];
  });
}

//layer加载中
function layerLoad(){
  return layer.load(1, {
    shade: [0.1,'#fff'] //0.1透明度的白色背景
  });
}

// layer提示错误信息
function layErrorMsg(msg, err){
    layer.closeAll();
  layer.msg(msg, {icon: 5});
  if(err){
    console.error(err);
  }
}

// 操作提示
function actConfirm(confirmFun, confirmInfo){
  if(!confirmInfo){
    confirmInfo = '确定执行该操作?';
  }
  var index = layer.confirm(confirmInfo, {icon: 3, title:'操作提示'}, function () {
    layer.close(index);
    confirmFun();
  });
}

// 拼接参数
function getJoinParams(params){
  if(!params){ return ''; }
  var str = '?';
  var arr = [];
  for(var attr in params ){
    arr.push(attr + '=' + params[attr]);
  }
  str = '?' + arr.join("&");
  return str;
}
// 返回拼接参数后的完整路径
function getJoinParamsHref(params){
  // console.log(params);
  var originPath = window.location.protocol + '//' + window.location.host + window.location.pathname;
  if(!params){
    return originPath;
  }
  str = getJoinParams(params);
  // console.log(originPath + str);
  return originPath + str;
}

function rememberParam(key, val){
  var params = putUrlParam(key, val);
  if(typeof history.pushState == 'function'){
    history.pushState('','',location.pathname+params);
  }
}

function putUrlParam(ref, value, url) {
  if(!url){
    url = window.location.search;
  }
  // 如果没有参数
  if (url.indexOf('?') == -1)
      return url + "?" + ref + "=" + value;
  // 如果不包括此参数
  if (url.indexOf(ref+'=') == -1)
      return url + "&" + ref + "=" + value;
  var arr_url = url.split('?');
  var base = arr_url[0];
  var arr_param = arr_url[1].split('&');
  for (i = 0; i < arr_param.length; i++) {
      var paired = arr_param[i].split('=');
      if (paired[0] == ref) {
          paired[1] = value;
          arr_param[i] = paired.join('=');
          break;
      }
  }
  return base + "?" + arr_param.join('&');
}

// 获取链接参数
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

// SuperPost
if (typeof $ !== "undefined") {
  jQuery.support.cors = true;
  var flag = navigator.appName == "Microsoft Internet Explorer" && parseInt(navigator.appVersion.split(";")[1].replace(/[ ]/g, "").replace("MSIE", "")) < 10;
  // var pathName = window.location.pathname;
  // var whitelist = ['/htmlsrc/document/private/cloud.fileList.html'];
  // if(whitelist.indexOf(pathName) > -1 && !flag){
  //   flag = true;
  // }
	if (false) {
		var url = remoteOrigin + "/open/cross.html";
		var loaded = false;
		var stack = [];
		var ifr;
		var post = function(method, url, headers, data, success, failed, dt) {
				if (!loaded) {
					stack.push([method, url, headers, data, success, failed, dt]);
					return
        }
        if(typeof data == 'string'){
          data += "&_method="+method;
        }else{
          data._method = method;
        }
				var data = {
          dt: dt,
          // xhrFields: {
          //   withCredentials: true
          // },
          // crossDomain: true,
					headers:headers,
					url: url,
					data: data,
					success: null,
					failed: null
        };
				if (success != null) {
					window[data.success = "ifcallback" + (new Date().getTime()) + Math.random()] = success
				}
				if (failed != null) {
					window[data.failed = "ifcallback" + (new Date().getTime()) + Math.random()] = failed
				}
				ifr[0].contentWindow.postMessage(JSON.stringify(data), "*")
			};
		$(function() {
			ifr = $("<iframe>");
			ifr.css({
				display: "none"
			});
			ifr.attr("src", url);
			ifr[0].onload = function() {
				loaded = true;
				if (stack.length) {
					for (var i = 0; i < stack.length; i++) {
						post.apply(null, stack[i])
					}
					stack = []
				}
			};
			$("body").append(ifr);
			var handler = function(e) {
					var data = JSON.parse(e.data);
					if (data.type == "success") {
						if (data.success) {
							window[data.success].call(null, data.data)
						}
					} else if (data.type == "failed") {
						if (data.failed) {
							window[data.failed].call()
						}
					}
					if (data.success) {
						window[data.success] = undefined
					}
					if (data.failed) {
						window[data.failed] = undefined
					}
				};
			if (typeof window.addEventListener != 'undefined') {
				window.addEventListener('message', handler, false)
			} else if (typeof window.attachEvent != 'undefined') {
				window.attachEvent('onmessage', handler)
			}
		})
	} else {
		var post = function(method, url, headers, data, success, failed ,dt) {
				var request = {
          // xhrFields: {
          //   withCredentials: true
          // },
          // crossDomain: true,
					method: method,
					headers: headers,
					url: url,
					data: data,
					// traditional: true,
					success: success,
					error: failed
				};
				if(dt == "traditional"){
          request.dataType = "json";
					request.traditional = true;
				} else if(dt == "json"){
          request.dataType = "json";
					request.data = JSON.stringify(request.data);
					request.contentType = "application/json";
        }else if(dt == "json2"){
          request.contentType = "application/json";
        }else{
          request.dataType = "json";
        }
				return $.ajax(request)
			}
	}
	window.SuperPost = function(options) {
    options.urlType = options.urlType || 'self';
    if(options.urlType == 'self'){
      var failed = options.error || function(){};
      var success = options.success || function(){};
      options.dt = options.dt || "normal";
      if(options.hasOwnProperty('headers')){
        options.headers['Authorization'] = $.cookie('authorization');
      }else{
        options.headers = {
          'Authorization' : $.cookie('authorization')
        }
      }

      var idex = options.url.indexOf("http://");
      if(idex == -1 || (idex > -1 && idex != 0)){
          options.url = remoteOrigin + options.url;
      }
    }else if(options.urlType == 'disk'){
      var opt = JSON.parse(JSON.stringify(options));
      if(opt.data){
        var p = getJoinParams(opt.data);
        opt.url = opt.url + p;
      }
      options.url = remoteClound + opt.url;
      // .replace(".action", ".action"+$.cookie("remoteCloundToken"));
      options.data = undefined;

      var succ = options.success;
      options.success = function (res) {
          if(res.status == 'ERROR'){
              layErrorMsg(res.msg)
              return;
          }
        succ(res)
      }
    }
    // console.log("dat")
    // console.error(options)
	return post(options.type, options.url, options.headers, options.data || {}, options.success, failed, options.dt);
	}
}

// 得到当前时间的时间戳
function getTimeStamp(){
  return Math.round(new Date().getTime()/1000)
}

/**
 * 日期 转换为 Unix时间戳
 * @param <string> 2014-01-01 20:20:20  日期格式
 * @return <int>        unix时间戳(秒)
 */
function dateToTimestamp(string){
  if(string == '' || string == undefined){
    return '';
  }
  var f = string.split(' ', 2);
  var d = (f[0] ? f[0] : '').split('-', 3);
  var t = (f[1] ? f[1] : '').split(':', 3);
  return (new Date(
          parseInt(d[0], 10) || null,
          (parseInt(d[1], 10) || 1) - 1,
          parseInt(d[2], 10) || null,
          parseInt(t[0], 10) || null,
          parseInt(t[1], 10) || null,
          parseInt(t[2], 10) || null
          )).getTime();
}

function dateFormate1(value, iSmillis){
  if(iSmillis){
    return dateFormate('Y-m-d H:i:s', value);
  }else{
    return dateFormate('Y-m-d H:i:s', value/1000);
  }
}
// php式格式化时间戳
function dateFormate(i,g){var h,e;var c=["Sun","Mon","Tues","Wednes","Thurs","Fri","Satur","January","February","March","April","May","June","July","August","September","October","November","December"];var b=/\\?(.?)/gi;var a=function(f,k){return e[f]?e[f]():k};var d=function(k,f){k=String(k);while(k.length<f){k="0"+k}return k};e={d:function(){return d(e.j(),2)},D:function(){return e.l().slice(0,3)},j:function(){return h.getDate()},l:function(){return c[e.w()]+"day"},N:function(){return e.w()||7},S:function(){var f=e.j();var k=f%10;if(k<=3&&parseInt((f%100)/10,10)===1){k=0}return["st","nd","rd"][k-1]||"th"},w:function(){return h.getDay()},z:function(){var k=new Date(e.Y(),e.n()-1,e.j());var f=new Date(e.Y(),0,1);return Math.round((k-f)/86400000)},W:function(){var k=new Date(e.Y(),e.n()-1,e.j()-e.N()+3);var f=new Date(k.getFullYear(),0,4);return d(1+Math.round((k-f)/86400000/7),2)},F:function(){return c[6+e.n()]},m:function(){return d(e.n(),2)},M:function(){return e.F().slice(0,3)},n:function(){return h.getMonth()+1},t:function(){return(new Date(e.Y(),e.n(),0)).getDate()},L:function(){var f=e.Y();return f%4===0&f%100!==0|f%400===0},o:function(){var l=e.n();var f=e.W();var k=e.Y();return k+(l===12&&f<9?1:l===1&&f>9?-1:0)},Y:function(){return h.getFullYear()},y:function(){return e.Y().toString().slice(-2)},a:function(){return h.getHours()>11?"pm":"am"},A:function(){return e.a().toUpperCase()},B:function(){var k=h.getUTCHours()*3600;var f=h.getUTCMinutes()*60;var l=h.getUTCSeconds();return d(Math.floor((k+f+l+3600)/86.4)%1000,3)},g:function(){return e.G()%12||12},G:function(){return h.getHours()},h:function(){return d(e.g(),2)},H:function(){return d(e.G(),2)},i:function(){return d(h.getMinutes(),2)},s:function(){return d(h.getSeconds(),2)},u:function(){return d(h.getMilliseconds()*1000,6)},e:function(){var f="Not supported (see source code of date() for timezone on how to add support)";throw new Error(f)},I:function(){var k=new Date(e.Y(),0);var m=Date.UTC(e.Y(),0);var f=new Date(e.Y(),6);var l=Date.UTC(e.Y(),6);return((k-m)!==(f-l))?1:0},O:function(){var k=h.getTimezoneOffset();var f=Math.abs(k);return(k>0?"-":"+")+d(Math.floor(f/60)*100+f%60,4)},P:function(){var f=e.O();return(f.substr(0,3)+":"+f.substr(3,2))},T:function(){return"UTC"},Z:function(){return -h.getTimezoneOffset()*60},c:function(){return"Y-m-d\\TH:i:sP".replace(b,a)},r:function(){return"D, d M Y H:i:s O".replace(b,a)},U:function(){return h/1000|0}};var j=function(k,f){h=(f===undefined?new Date():(f instanceof Date)?new Date(f):new Date(f*1000));return k.replace(b,a)};return j(i,g)};

var subDateStr = function(dateStr){
    if(dateStr && dateStr.length>=10){
        return dateStr.substring(0, 10);
    }else if(dateStr){
        console.error("请查看字段格式",dateStr);
    }
    return " - ";
};
// 格式化括号
function formatBrackets(str) {
    if(str){
       return str.trim().replace(")", "）").replace("(", "（");
    }
    return "";
}
function showECI_Info(name, keyNo) {
    name = formatBrackets(name);
    var showA = function () {
        if($("#showECIcheck").prop("checked")){
            $.cookie("showECI", 1,{path: "/"});
        }
        addNavTab("ECIdetail"+name, "企查查 - " + name, hrefUrl.ECIdetail + name);
        layer.close(index);
    };
    var showB = function () {
        if($("#showECIcheck").prop("checked")){
            $.cookie("showECI", 2,{path: "/"});
        }
        $.ajax({
            url: remoteApi.apiOdsSearchAllClient,
            data: {
                CUS_NAME: name
            },
            success:function (res) {
                var list = res.data.list;
                if(list.length === 1){
                    var comInfo = list[0];
                    var cusId = comInfo["CUS_ID"];
                    addNavTab("com-user-"+cusId, "客户详情 - " + name, hrefUrl.comClientInfo+cusId);
                    return;
                }else if(list.length>1){
                    addNavTab("客户资料 - 所有客户 - " + name, "/htmlsrc/dataQuery/clientList/clientList.html?name="+name);
                    return;
                }else{
                    layer.msg("没有查询到该公司！");
                }
            },
            error: function () {
                layer.msg("没有查询到该公司！");
            }
        });
    };
    if($.cookie("showECI")){
        if($.cookie("showECI") == 1){
            showA();
        }else{
            showB();
        }
    }else{
        var index = layer.confirm("",{
            title: false,
            shadeClose: true,
            content : '<div id="output_detail" class="pace-done" style="padding: 10px;" ><p >'+ '【企查查】直接查看详情； 【行内】则在我负责的客户中搜索公司名；' + '</p>本次登录记住选择，不再提醒！<input type=checkbox name=details id=showECIcheck></div>',
            btn: ['企查查', '行内', '外网查询'],
            btn1: function () {
                showA();
            },
            btn2: function () {
                showB();
            },
            btn3: function (){
                if(keyNo && keyNo!=="undefined"){
                    top.window.open("https://www.qichacha.com/firm_"+keyNo+".html", "_blank");
                }else{
                    top.window.open("https://www.qichacha.com/search?key="+name);
                }
            }
        });
    }
}
$(document).on("click", "[client-name]",function () {
    showECI_Info($(this).attr("client-name"), $(this).attr("key-no"));
});
$(document).on("click", ".show-client-target",function () {
    showECI_Info($(this).html().trim(), $(this).attr("key-no"));
});
//
var formRequest = function (options) {
    var config = $.extend(true, { method: 'post' }, options);
    var $iframe = $('<iframe id="down-file-iframe" />');
    var $form = $('<form target="down-file-iframe" content-type="application/json" method="' + config.method + '" />');
    $form.attr('action', config.url);
    for (var key in config.data) {
        $form.append("<input type=\"hidden\" name='" + key + "' value=\'" + JSON.stringify(config.data[key]) + "' />");
    }
    $iframe.append($form);
    $(document.body).append($iframe);
    $form[0].submit();
    $iframe.remove();
};

if(typeof $ !== "undefined"){
    $.fn.ghostsf_serialize = function () {
        var a = this.serializeArray();
        var $radio = $('input[type=radio],input[type=checkbox]', this);
        var temp = {};
        $.each($radio, function () {
            if (!temp.hasOwnProperty(this.name)) {
                if ($("input[name='" + this.name + "']:checked").length == 0) {
                    temp[this.name] = "";
                    a.push({name: this.name, value: ""});
                }
            }
        });
        //console.log(a);
        return jQuery.param(a);
    };

    //fix right click menu
    $(document).on('click', 'html', function () {
        window.top.$('.dropdown-context').fadeOut(100, function () {
            window.top.$('.dropdown-context').css({display: ''}).find('.drop-left').removeClass('drop-left');
        });
    });
}


function getRemoteEnum(names, completePromise) {
    var complete;
    if(completePromise ===undefined){
        complete = $.Deferred()
    }
    var getArr = [];
    if(names instanceof Array){
        names.forEach(function (value) {
            getArr.push(getFetch(remoteOrigin + "/api/auto/dict/getList", {name: value, size: 100}));
        });
        $.when.apply(this, getArr).done(function () {
            var resArr = [];
            if(names.length === 1){
                resArr.push(arguments);
            }else{
                resArr = Array.prototype.slice.call(arguments)
            }
            var retObj = {};
            resArr.forEach(function (value) {
                var dataList = value[0].data.list || [];
                dataList.forEach(function (value1) {
                    var keyName = value1["name"];
                    if(names.indexOf(keyName) > -1){
                        if(!retObj[keyName]){
                            retObj[keyName] = [{name:"--请选择--", val:""}];
                        }
                        retObj[keyName].push({val: value1.vKey, name: value1.vValue});
                    }
                });
            });
            if(completePromise === undefined){
                complete.resolve(retObj);
            }else{
                completePromise.resolve(retObj);
            }
        });
    }
    return completePromise ? completePromise : complete;
}

