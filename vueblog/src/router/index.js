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
import SetSearchDirection from "../pages/SetSearchDirection";
import ThesisList from "../pages/ThesisList";
import test from "../pages/test";
import uploadThesis from "../pages/UploadThesis";

Vue.use(Router)

export default new Router({
  mode: "history",
  routes: [
    {
      path: '/',
      name: '登录',
      hidden: true,
      component: Login
    }, {
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
          meta: {
            keepAlive: true
          }
        }, {
          path: '/uploadThesis',
          name: '上传论文',
          component: uploadThesis,
          meta: {
            keepAlive: false
          }
        }, {
          path: '/blogDetail',
          name: '博客详情',
          component: BlogDetail,
          hidden: true,
          meta: {
            keepAlive: false
          }
        }, {
          path: '/editBlog',
          name: '编辑博客',
          component: PostArticle,
          hidden: true,
          meta: {
            keepAlive: false
          }
        }
      ]
    }, {
      path: '/home',
      component: Home,
      name: '用户管理',
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
      name: '栏目管理',
      children: [
        {
          path: '/cateMana',
          iconCls: 'fa fa-reorder',
          name: '栏目管理',
          component: test
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
      children: [
        {
          path: '/setSearchDirection',
          iconCls: 'fa fa-reorder',
          name: '研究方向管理',
          component: SetSearchDirection
        }
      ]
    }
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
