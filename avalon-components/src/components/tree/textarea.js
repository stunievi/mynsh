import avalon, { component } from 'avalon2';
component("textarea",{
    template: require("./textarea.html"),
    defaults:{
        data:{
            value: ""
        }
    }
})