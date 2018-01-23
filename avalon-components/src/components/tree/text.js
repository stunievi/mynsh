import avalon, { component } from 'avalon2';
component("text",{
    template: require("./text.html"),
    defaults:{
        data:{
            value: ""
        }
    }
})