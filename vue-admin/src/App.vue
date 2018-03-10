<template>
    <!--<div id="app">-->
        <!--<Layout class="layout">-->
            <!--<Sider>-->
                <!--<LeftMenu></LeftMenu>-->
            <!--</Sider>-->
            <!--<Content>-->
                <!--<router-view/>-->
            <!--</Content>-->
        <!--</Layout>-->
        <!--&lt;!&ndash;<img src="./assets/logo.png">&ndash;&gt;-->
    <!--</div>-->

    <el-container class="app">
        <el-header>
            <NavMenu/>
        </el-header>
        <el-container>
            <el-aside >
                <LeftMenu/>
            </el-aside>
            <el-container>
                <el-main>Main</el-main>
                <el-footer>Footer</el-footer>
            </el-container>
        </el-container>
    </el-container>
</template>

<script>



    import LeftMenu from "@/components/Menu"
    import NavMenu from "@/components/NavMenu";

    export default {
        name: 'App',
        components: {
            NavMenu,
            LeftMenu
        }
    }
</script>

<style>
    *{
        margin: 0;
        padding: 0;
    }
    html,body{
        width: 100%;
        height: 100%;
        background: #f0f0f0;
        overflow: hidden;
    }
    .app{
        width: 100%;
        height: 100%;
        overflow: hidden;
    }
    .aside{
    }
    /*#app {*/
        /*font-family: 'Avenir', Helvetica, Arial, sans-serif;*/
        /*-webkit-font-smoothing: antialiased;*/
        /*-moz-osx-font-smoothing: grayscale;*/
        /*text-align: center;*/
        /*color: #2c3e50;*/
        /*!*margin-top: 60px;*!*/

        /*height: 100%;*/
        /*width: 100%;*/
    /*}*/
    /*.layout{*/
        /*width: 100%;*/
        /*height: 100%;*/
    /*}*/

    .menu {
        float: left;
    }
</style>
