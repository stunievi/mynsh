import avalon, { component } from 'avalon2';
component("radio",{
    template: require("./radio.html"),
    defaults:{
        data:{
            items: []
        },
        addItem:function () {
            console.log(this)
            this.data.items.push("");
        }
    }
})