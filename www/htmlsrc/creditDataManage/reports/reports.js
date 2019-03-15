var reportsList = [
    { id: "demo", reportsName: "报表示例" },
    { id: "count", reportsName: "统计报表示例" },
    { id: "r1", reportsName: "信贷资产质量情况统计表（月报表）", dept: "授信管理部", menu: true},
    { id: "r2", reportsName: "简报表数据", dept: "授信管理部" },
    { id: "r3", reportsName: "信贷营销情况（一般贷款）", dept: "授信管理部" },
    { id: "r4", reportsName: "草稿草稿", dept: "草稿" },
    { id: "r5", reportsName: "预期应收本金（到期）", dept: "预期/逾期" },
    { id: "r6", reportsName: "预期应收利息", dept: "预期/逾期" },
    { id: "r7", reportsName: "逾期应收本金", dept: "预期/逾期" },
    { id: "r8", reportsName: "逾期应收利息", dept: "预期/逾期" },
    { id: "r9", reportsName: "表外不良贷款清收旬报表", dept: "风险资产管理部", menu: true },
    { id: "r10", reportsName: "广东省农村合作金融机构以物抵债情况季报表", dept: "风险资产管理部", menu: true },
    { id: "r11", reportsName: "不良贷款重点系列（大户）情况表", dept: "风险资产管理部", menu: true },
    { id: "r12", reportsName: "发放及到期收回情况", dept: "授信管理部"},
    { id: "r13", reportsName: "信贷资产质量情况", dept: "授信管理部"},
    { id: "r14", reportsName: "隐性不良贷款情况", dept: "授信管理部"},
    { id: "r15", reportsName: "按担保类型划分", dept: "授信管理部"},
    { id: "r16", reportsName: "前十大户和最大单户情况", dept: "授信管理部"},
    { id: "r17", reportsName: "房地产贷款情况 - 房地产开发贷款情况", dept: "授信管理部"},
    { id: "r18", reportsName: "个人按揭业务情况", dept: "授信管理部"},
    { id: "r19", reportsName: "卡贷宝贷款业务情况", dept: "授信管理部"},
    { id: "r20", reportsName: "广东省农村合作金融机构贷款五级分类迁徙表", dept: "授信管理部" },
    { id: "r21", reportsName: "广东省农村合作金融机构不良贷款压降情况季报表", dept: "授信管理部", menu: true },
    { id: "r22", reportsName: "表内正常贷款欠息情况统计报表", dept: "授信管理部", menu: true },
    { id: "r23", reportsName: "隐性不良贷款明细表", dept: "授信管理部", menu: true },
    { id: "r24", reportsName: "五级分类不良贷款明细表", dept: "授信管理部", menu: true },
    { id: "r25", reportsName: "五级分类不良贷款现金收回明细表", dept: "授信管理部", menu: true },
    { id: "r26", reportsName: "五级分类不良贷款上调明细表", dept: "授信管理部", menu: true },
    { id: "r27", reportsName: "不良贷款清收压降情况表", dept: "授信管理部", menu: true },
    { id: "r28", reportsName: "信贷营销情况（按揭贷款）", dept: "授信管理部", menu: true },
    { id: "r29", reportsName: "利息收回情况", dept: "授信管理部", menu: true },
    { id: "r30", reportsName: "正常类贷款欠息情况", dept: "授信管理部", menu: true },
    { id: "r31", reportsName: "不良贷款利息减免情况汇总季报表", dept: "风险资产管理部", menu: true },
    { id: "r32", reportsName: "不良贷款减免情况明细表(季报)", dept: "风险资产管理部", menu: true }
    
];
var id = getParam('id');
var reportsInfo = {
    reportsName: '报表'
};
if(id){
    for(var i=0; i<reportsList.length;i++){
        if(reportsList[i].id == id){
            reportsInfo = reportsList[i];
            break;
        }
    }
    $(".reports-name").text(reportsInfo.reportsName);

}
