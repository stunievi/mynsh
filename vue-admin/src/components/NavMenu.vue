<template>
    <el-container>
        <img src="" alt="">
        <span>
            贷后管理系统
        </span>
        <el-menu mode="horizontal" @select="selectTopMenu">
            <el-menu-item v-for="(item,index) in menu" :index="index">
                {{item.title}}
            </el-menu-item>
        </el-menu>
    </el-container>

</template>

<script>
    export default{
        data(){
            return {
                menu : [
                    {
                        title:"工作台",
                        children:[
                            {
                                title: "我的工作台"
                            },
                            {
                                title: "待办事项"
                            }
                        ]
                    },
                    {
                        title: "信贷系统管理",
                        children:[
                            {
                                title: "我的工作台222"
                            },
                            {
                                title: "待办事项222"
                            }
                        ]
                    },
                    {
                        title: "客户管理",
                        children:[
                            {
                                title: "我的工作台333"
                            },
                            {
                                title: "待办事项333"
                            }
                        ]
                    },
                    {
                        title: "贷前管理",
                        children: [
                            {
                                title: "我的工作台444"
                            },
                            {
                                title: "待办事项444"
                            }
                        ]
                    }
                ]
            }
        },

        methods:{
            selectTopMenu(key,keyPath){
                let target = this.menu[key];
                eventBus.$emit("top_menu_click",target)
            }
        }
    }
</script>
