<template>
  <div id="app">
      <Layout>
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
    components:{
        LeftMenu
    }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
    .menu{
        float: left;
    }
</style>
