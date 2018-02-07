<template>
    <Tree :data="list" v-on:on-select-change="href" ></Tree>
    <!--<div class="menu">-->
        <!--<dl  v-for="item in list">-->
           <!--<dt v-text="item.title"></dt>-->
            <!--<dd>-->
                <!--<ul>-->
                    <!--<li v-for="child in item.children">-->
                        <!--<a v-text="child.title" @click="go(child)"></a>-->
                    <!--</li>-->
                <!--</ul>-->
            <!--</dd>-->
        <!--</dl>-->
    <!--</div>-->
</template>

<script>
    import config from "../common/config";
    // import {Slider} from "iview";
    export default{
        data(){
            return {
                list: [],
                value:[20,50]
            }
        },


        created(){
            this.$http
                .get(config.server + "/api/admin/menu")
                .then(res => res.json())
                .then(data => {this.initData(data.message)} )
        },

        methods:{
            go(item){
                this.$router.push(item.href)
            },
            /**
             * 整理数据
             */
            initData(data){
                data.map(item => item.expand = true)
                console.log(data)
                this.list = data;
            },

            href(el){

                if(!el[0].href){
                    return;
                }
                this.go(el[0])
            }


        },

        // components:{Slider}
    }
</script>

<style >
    .ivu-tree-title{
        color: white;
    }
</style>
