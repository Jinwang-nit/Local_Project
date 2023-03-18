import { createRouter, createWebHistory } from 'vue-router'
import NotFound from '../views/error/NotFound.vue';
import PkIndex from '../views/pk/PkIndex.vue';
import RanklistIndex from '../views/ranklist/RanklistIndex.vue';
import RecordIndex from '../views/record/RecordIndex.vue';
import UserbotIndex from '../views/user/bot/UserbotIndex.vue';
import UserAccountLogin from '../views/user/account/UserAccountLogin.vue'
import UserAccountRegister from '../views/user/account/UserAccountRegister.vue'
import RecordContent from '../views/record/RecordContent.vue'
import store from '../store/index';
const routes = [

  {
    path:"/record/:recordId/",
    name:"RecordContent",
    component:RecordContent,
    meta:{
      requestAuth:true,
    }
  },

  {
    path:"/",
    name:"home",
    redirect:"/pk/",
    meta:{
      requestAuth:true,
    }
  },
  {
    path:"/pk/",
    component:PkIndex,
    name:"pk_index",
    meta:{
      requestAuth:true,
    }
  },

  {
    path:"/record/",
    component:RecordIndex,
    name:"record_index",
    meta:{
      requestAuth:true,
    }
  },
  {
    path:"/ranklist/",
    component:RanklistIndex,
    name:"ranklist_index",
    meta:{
      requestAuth:true,
    }
  },
  {
    path:"/user/bot/",
    component:UserbotIndex,
    name:"user_bot_index",
    meta:{
      requestAuth:true,
    }
  },
  {
    path:"/user/account/login/",
    component:UserAccountLogin,
    name:"user_account_login"
  },
  {
    path:"/user/account/register/",
    component:UserAccountRegister,
    name:"user_account_register"
  },
  {
    path:"/404/",
    component:NotFound,
    name:"404",
  },
  {
    path:"/:catchAll(.*)",
    redirect:"404"
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 判断跳转页面前先判断目标页面是否要授权
router.beforeEach((to,from,next)=>{
  if (to.meta.requestAuth && !store.state.user.is_login){
    next({name:"user_account_login"});
  }else{
    next();
  }
})

export default router;
