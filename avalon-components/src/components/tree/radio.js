import avalon, { component } from 'avalon2';
component("radio",{
    template: require("./radio.html"),
    defaults:{
        data:{
            value: ""
        }
    }
})