<template>
    <div>
        <Table :columns="columns1" :data="data1"></Table>
        <Page v-on:on-change="test" :total="total" :page-size="size" :current="current" show-elevator></Page>
    </div>
</template>

<script>
    import config from "../common/config";
    export default {
        data(){
            this.init();
            return {
                columns1:[
                    {
                        title: '编号',
                        key: 'id'
                    },
                    {
                        title: '用户名',
                        key: 'username'
                    },
                    {
                        title: '添加时间',
                        key: 'addTime'
                    },
                    {
                        title: '操作',
                        key: 'action',
                        render: (h, params) => {
                            return h('div', [
                                h('Button', {
                                    props: {
                                        type: 'primary',
                                        size: 'small'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.show(params.index)
                                        }
                                    }
                                }, '编辑'),
                                h('Button', {
                                    props: {
                                        type: 'error',
                                        size: 'small'
                                    },
                                    on: {
                                        click: () => {
                                            this.remove(params.index)
                                        }
                                    }
                                }, '删除')
                            ]);
                        }
                    }

                ],
                data1:[

                ],
                total:0,
                size:0,
                current:0
            }
        },
        methods:{
            init(page = 0){
                // console.log(123)
                this.$http
                    .get(config.server + "/api/admin/users?page=" + page)
                    .then(res => res.json())
                    .then(data => {
                        this.data1 = data.message.content;
                        this.total = data.message.totalElements;
                        this.size = data.message.size;
                        this.current = (data.message.number + 1);
                    })
            },
            test(el){
                this.init(el - 1)
            }
        }
    }
    // import Menu from ""
    // export default{
    //
    // }
</script>
