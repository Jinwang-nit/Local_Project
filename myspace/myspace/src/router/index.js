import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LogIn from '../views/LogIn.vue'
import NotFound from '../views/NotFound.vue'
import RegiSter from '../views/RegiSter.vue'
import UserList from '../views/UserList.vue'
import UserProfile from '../views/UserProfile.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
    {
    path: '/userlist/',
    name: 'userlist',
    component: UserList
  },
    {
    path: '/userprofile/:userId/',
    name: 'userprofile',
    component: UserProfile
  },
    {
    path: '/login/',
    name: 'login',
    component: LogIn
  },
    {
    path: '/register/',
    name: 'register',
    component: RegiSter
  },
    {
    path: '/404/',
    name: '404',
    component: NotFound
  },
  {
    path:'/:catchAll(.*)',
    redirect:"/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
