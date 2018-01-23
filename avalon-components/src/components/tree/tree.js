import avalon, { component } from 'avalon2';
window.lastSelected = null;
component('tree', {
    template: require("./tree.html"),
    defaults: {
        tree: [],
        select : function (el) {
            if(lastSelected && lastSelected != el){
                lastSelected.selected = 0;
            }
            el.selected = !el.selected;
            //0 - 1
            if(el.selected == 1){
                lastSelected = el;
            }
            //1 - 0
            else{
                lastSelected = null;
            }

        }
    }
})