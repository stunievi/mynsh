<template>
    <div class="menu">
        <dl  v-for="item in list">
           <dt v-text="item.title"></dt>
            <dd>
                <ul>
                    <li v-for="child in item.children">
                        <a v-text="child.title" @click="go(child)"></a>
                    </li>
                </ul>
            </dd>
        </dl>
    </div>
</template>

<script>
    import config from "../common/config";

    export default{
        data(){
            return {
                list: []
            }
        },

        created(){
            this.$http
                .get(config.server + "/api/admin/menu")
                .then(res => res.json())
                .then(data => this.list = data.message)
        },

        methods:{
            go(item){
                this.$router.push(item.href)
            }
        }
    }
</script>
