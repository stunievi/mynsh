import avalon, { component } from 'avalon2';
component("shenheNode",{
    template: require('./shenheNode.html'),
    defaults:{
        data : {
            num:1,
            passNum:0
        },

        getType: function () {
            return "审核"
        },

        onNumChange(){
            if(this.data.num < 1){
                this.data.num = 1;
            }
        },

        onPassNumChange(){
            if(this.data.passNum < 1){
                this.data.passNum = 1;
            }
            if(this.data.passNum > this.data.num){
                this.data.passNum = this.data.num;
            }
        }

    }
});