~(function () {
    var ret = [];
    var  ename = "q_company_base";
    var  cname = "企业基础信息表";

    var trs = $('#my-table tr');
    //读取主体
    var firstLine = true;
    var obj = generateEmptyTable();
    for(var i = 0; i < trs.length; i++){
        var tds = trs.eq(i).find("td");
        if(tds.length == 4){
            break
        }
        if(firstLine){
            firstLine = false;
            continue
        }
        obj.field.push(readTableField(tds,0))
    }
    if(obj.field.length){
        obj.en = ename;
        obj.cn = cname;
        obj.qcc = "";
        ret.push(obj)
    }


    //读取附加字段
    for(var i = 0; i < trs.length; i++){
        var tds = trs.eq(i).find("td");
        //向下读取字段
        if(tds.length == 4){
            var obj = generateEmptyTable();
            obj.field.push(readTableField(tds, 1))
            //拆分名字
            var txt = tds.eq(0)[0].childNodes;
            obj.qcc = txt[0].nodeValue.trim();
            obj.en = "q_" + toLine(obj.qcc);
            obj.cn = txt[2].nodeValue.trim();
            while(++i < trs.length){
                tds = trs.eq(i).find("td");
                if(tds.length == 4){
                    i--;
                    break
                }
                obj.field.push(readTableField(tds, 0));
                console.log(readTableField(tds, 0))
            }
            obj.field.push({
                en: "main_id",
                type: "bigint",
                cn: "关联主表",
                qcc: ""
            })
            ret.push(obj);
        }
    }

    //结构信息
    console.log(JSON.stringify(ret))

    //根据返回详情补充字段



    function toLine(name) {
        name = name[0].toLowerCase() + name.substr(1);
        return name.replace(/([A-Z])/g,"_$1").toLowerCase();
    }

    function formatType(type) {
        switch (type) {
            case "String":
                return "varchar";

            case "DateTime":
                return "datetime";

            case "Int32":
                return "integer";
        }
        return "error";
    }

    function generateEmptyTable() {
        var obj = {
            field: [
                {
                    en: "row_id",
                    type: "bigint",
                    cn: "主键",
                    qcc: ""
                }
            ],
            en: "",
            cn: "",
            qcc: ""
        }
        return obj;
    }

    function readTableField(tds, idex) {
        var qcc = tds.eq(idex++).text().trim();
        var en = toLine(qcc);
        var type = formatType(tds.eq(idex++).text().trim());
        var cn = tds.eq(idex++).text().trim();
        return ({
            en,
            type,
            cn,
            qcc
        });
    }
})();