


,
resHandler: function(res){
    res.data.content.forEach(function(element) {
        transformOdsEnumData(element,['ASSURE_MEANS_MAIN', 'REPAYMENT_MODE', 'CLA']);
    });
    return res;
}

SuperPost({
    dt: "json2",
    type: "post",
    url: remoteApi,
    data: " ",
    success: function(res){

    }
})


ssss: [
    {name:'--请选择--', val:''},
    {name:'fff', val: 'ff1'},
    {name:'fff', val: 'ff2'},
    {name:'fff', val: 'ff3'},
    {name:'fff', val: 'ff4'},
    {name:'fff', val: 'ff5'},
    {name:'fff', val: 'ff6'},
    {name:'fff', val: 'ff7'},
    {name:'fff', val: 'ff8'},
    {name:'fff', val: 'ff9'},
], // 城乡类型

ssss: [
    {name:'--请选择--', val:''},
    {name:'是', val: '1'},
    {name:'否', val: '0'}
], // 城乡类型