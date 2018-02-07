import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'

import UserPage from "@/pages/user";
import IndexPage from "@/pages/index";

/*引入资源请求插件*/
import VueResource from 'vue-resource'

/*使用VueResource插件*/
Vue.use(VueResource)
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
      {
          path: '/admin',
          name: 'admin_index',
          component: IndexPage
      },
      {
          path: "/admin/setting/user/list",
          name:"setting_user",
          component: UserPage
      }
  ]
})
