import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import Home from '@/components/Home'
import ArticleList from '@/components/ArticleList'
import CateMana from '@/components/CateMana'
import DataCharts from '@/components/DataCharts'
import PostArticle from '@/components/PostArticle'
import UserMana from '@/components/UserMana'
import BlogDetail from '@/components/BlogDetail'
import Register from "../pages/Register"
import SelfUser from "../pages/SelfUser";
import ThesisDetail from "../pages/ThesisDetail";
import SetSearchDirection from "../pages/SetSearchDirection";
import ThesisList from "../pages/ThesisList";
import uploadThesis from "../pages/UploadThesis";
import ThesisEdit from "../pages/ThesisEdit";

Vue.use(Router)

export default new Router({
  //mode: "history",
  routes: [
    {
      path: '/',
      name: '登录',
      hidden: true,
      component: Login
    }, {
      path: '/toRegister',
      name: '注册',
      hidden: true,
      component: Register
    },{
      path: '/home',
      name: '',
      component: Home,
      hidden: true
    }, {
      path: '/home',
      component: Home,
      name: '论文管理',
      iconCls: 'fa fa-file-text-o',
      children: [
        {
          path: '/thesisList',
          name: '论文列表',
          component: ThesisList,
          iconCls: 'fa fa-file-text-o',
          meta: {
            keepAlive: false
          }
        },{
          path: '/uploadThesis',
          name: '上传论文',
          component: uploadThesis,
          iconCls: 'fa fa-file-text-o',
          meta: {
            keepAlive: false
          }
        },
        {
          path: '/thesisDetail',
          name: '论文详情',
          component: ThesisDetail,
          hidden: true,
          meta: {
            keepAlive: false
          }
        }, {
          path: '/editThesis',
          name: '编辑论文信息',
          component: ThesisEdit,
          hidden: true,
          meta: {
            keepAlive: false
          }
        }
      ]
    },
    {
      path: '/home',
      component: Home,
      name: '用户管理',
      hidden: true,
      children: [
        {
          path: '/user',
          iconCls: 'fa fa-user-o',
          name: '用户管理',
          component: UserMana
        }
      ]
    }, {
      path: '/home',
      component: Home,
      name: '数据统计',
      iconCls: 'fa fa-bar-chart',
      children: [
        {
          path: '/charts',
          iconCls: 'fa fa-bar-chart',
          name: '数据统计',
          component: DataCharts
        }
      ]
    }, {
      path: '/home',
      component: Home,
      name: '研究方向管理',
      hidden: true,
      children: [
        {
          path: '/setSearchDirection',
          iconCls: 'fa fa-reorder',
          name: '研究方向管理',
          component: SetSearchDirection
        }
      ]
    },/* {
      path: '/home',
      component: Home,
      name: '用户注册',
      children: [
        {
          path: '/register',
          iconCls: 'fa fa-reorder',
          name: '用户注册',
          component: Register,
        }
      ]
    },*/{
      path: '/home',
      component: Home,
      name: '个人主页',
      children: [
        {
          path: '/selfUser',
          iconCls: 'fa fa-reorder',
          name: '个人主页',
          component: SelfUser,
        }
      ]
    },
  ]
})

const reSetPermissionList = to => {
  return new Promise((resolve, reject) => {
    if (to.path !== '/login' && store.state.permission.permissionList.length === 0) {
      store
        .dispatch('permission/getPermissionList')
        .then(() => {
          resolve('permCode')
        })
        .catch(error => {
          resolve('permCode')
        })
    } else {
      resolve()
    }
  })
}
