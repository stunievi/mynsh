import avalon, { component } from 'avalon2';
component("ziliaoNode",{
    template: require('./ziliaoNode.html'),
    defaults:{
        data : {
            fields:[]
        },
        addField:function () {
            this.data.fields.push({
                name:"",
                type:"text",
                required:false,
                data: {
                    value: ""
                }
            });
        },
        getType: function () {
            return "资料"
        },
        removeField:function (index) {
            this.data.fields.splice(index,1);
        }
    }
});