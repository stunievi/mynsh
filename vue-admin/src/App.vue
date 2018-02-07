<template>
    <div id="app">
        <Layout class="layout">
            <Sider>
                <LeftMenu></LeftMenu>
            </Sider>
            <Content>
                <router-view/>
            </Content>
        </Layout>
        <!--<img src="./assets/logo.png">-->
    </div>
</template>

<script>

    import LeftMenu from "@/components/Menu"

    export default {
        name: 'App',
        components: {
            LeftMenu
        }
    }
</script>

<style>
    html,body{
        width: 100%;
        height: 100%;
        background: #f0f0f0;
        overflow: hidden;
    }
    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        /*margin-top: 60px;*/

        height: 100%;
        width: 100%;
    }
    .layout{
        width: 100%;
        height: 100%;
    }

    .menu {
        float: left;
    }
</style>
