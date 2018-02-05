import avalon, { component } from 'avalon2';
component("checkbox",{
    template: require("./checkbox.html"),
    defaults:{
        data:{
            items: [],
            atLeast:1
        },
        onAtLeastChange:function () {
            this.data.atLeast = parseInt(this.data.atLeast);
            if(this.data.atLeast !== this.data.atLeast || this.data.atLeast < 1){
                this.data.atLeast = 1;
            }
        },
        add:function () {
            this.data.items.push("")
        },
        remove:function (index) {
            this.data.splice(index,1);
        }
    }
})