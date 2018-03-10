<template>
    <div class="box">
        <div class="left"></div>
        <div class="right">
            <b>{{title}}</b>

            <el-menu
                default-active="2"
                class="el-menu-vertical-demo"
                >
                <el-menu-item v-for="item in menu">
                    {{item.title}}
                </el-menu-item>
            </el-menu>

        </div>
    </div>
</template>

<script>
    import config from "../common/config";
    // import {Slider} from "iview";
    export default{
        data(){
            return {
                title: "",
                menu:[
                    {
                        title: "我的工作台"
                    },
                    {
                        title: "待办事项"
                    }
                ]
            }
        },


        created(){
            eventBus.$on("top_menu_click",(e) => {
                this.title = e.title;
                if(e.children.length){
                    this.menu = e.children;
                }

            });

        },

        methods:{

            test(){
              alert(13)
            },
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

<style scoped>
    .box{
        height: 100%;
        overflow: hidden;
    }
    .left{
        background: #3a3a3a;
        width: 60px;
        height: 100%;
        float: left;
    }
    .right{
        display: inline-block;
        width: 180px;
        height: 100%;
        background: #ddd;
        float: left;
    }
    .ivu-tree-title{
        color: white;
    }
</style>
