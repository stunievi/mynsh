import avalon, { component } from 'avalon2';
component("ziliaoNode",{
    template: require('./ziliaoNode.html'),
    defaults:{
        data : {
            fields:[]
        },
        edit: true,

        addField:function (type) {
            var data = {
                name:"",
                type:type,
                required:false,
            };
            for(var i in   avalon.components[type].defaults.data){
                data[i] = avalon.components[type].defaults.data[i];
            }
            this.data.fields.push(data);
        },
        getType: function () {
            return "资料"
        },
        removeField:function (index) {
            this.data.fields.splice(index,1);
        }
    }
});