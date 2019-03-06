// import 'babel-polyfill'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-table'
import 'bootstrap-table/dist/bootstrap-table.css'
import layer from 'layer'
import "layer.css"

global.$ = global.jQuery = $;
global.layer = layer;

// $(function () {
//
//     $('#table').bootstrapTable({
//         query:{
//             a:1,
//             b:2
//         },
//         columns: [{
//             field: 'id',
//             title: 'Item ID'
//         }, {
//             field: 'name',
//             title: 'Item Name'
//         }, {
//             field: 'price',
//             title: 'Item Price'
//         }],
//         data: [{
//             id: 1,
//             name: 'Item 1',
//             price: '$1'
//         }, {
//             id: 2,
//             name: 'Item 2',
//             price: '$2'
//         }]
//     });
//
// });


export var a = 1;
