<template>
  <el-container class="home_container">
    <el-header class="el-header">
      <div class="home_title">实验室论文管理系统</div>
      <div class="home_userinfoContainer">
        <el-dropdown @command="handleCommand">
      <span class="el-dropdown-link home_userinfo">
        {{currentUserName}}<i class="el-icon-arrow-down el-icon--right home_userinfo"></i>
      </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-if="currentUserAuth!=0">
              <router-link to = '/user'>用户管理</router-link>
            </el-dropdown-item>
            <el-dropdown-item v-if="currentUserAuth!=0">
              <router-link to = '/setSearchDirection'>研究方向管理</router-link>
            </el-dropdown-item>
            <el-dropdown-item v-if="currentUserAuth!=0">
              <router-link to = '/charts'>数据管理</router-link>
            </el-dropdown-item>
            <el-dropdown-item>
              <router-link to = '/selfUser'>个人主页</router-link>
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="250px" class="el-aside">
        <el-menu
          default-active="0"
          class="el-menu-vertical-demo" style="background-color: #abbfbd;" router>
          <template v-for="(item,index) in this.$router.options.routes" v-if="!item.hidden">
            <el-submenu :index="index+''" v-if="item.children.length>1" :key="index">
              <template slot="title">
                <i :class="item.iconCls"></i>
                <span>{{item.name}}</span>
              </template>
              <el-menu-item v-for="child in item.children" v-if="!child.hidden" :index="child.path" :key="child.path">
                {{child.name}}
              </el-menu-item>
            </el-submenu>
            <template v-else>
              <el-menu-item :index="item.children[0].path">
                <i :class="item.children[0].iconCls"></i>
                <span slot="title">{{item.children[0].name}}</span>
              </el-menu-item>
            </template>
          </template>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-text="this.$router.currentRoute.name"></el-breadcrumb-item>
          </el-breadcrumb>
          <keep-alive>
            <router-view v-if="this.$route.meta.keepAlive"></router-view>
          </keep-alive>
          <router-view v-if="!this.$route.meta.keepAlive"></router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>
<script>
  import {getRequest} from '../utils/api'
  export default{
    methods: {
      handleCommand(command){
        var _this = this;
        if (command == 'logout') {
          this.$confirm('注销登录吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(function () {
            getRequest("/logout")
            _this.currentUserName = '游客';
            _this.$router.replace({path: '/'});
          }, function () {
            //取消
          })
        }
      }
    },
    mounted: function () {
      //TODO: Remove tourists
      //TODO: Get Auth
      var _this = this;
      getRequest("/currentUserName").then(function (msg) {
        _this.currentUserName = msg.data;
      }, function (msg) {
        _this.currentUserName = '游客';
      });
      getRequest("/currentUserId").then(function (msg) {
        _this.currentUserId = msg.data;
        if (msg.data) {
          getRequest("/admin/user/" + _this.currentUserId).then(resp=> {
            if (resp.status == 200) {
              _this.currentUserAuth = resp.data.auth;
            }
          }, resp=> {
            _this.$message({type: 'error', message: '用户身份错误！请联系管理员。'});
          });
        }
      }, function (msg) {
        this.$message.error("用户身份错误！请联系管理员。");
      });

    },
    data(){
      return {
        currentUserName: '',
        currentUserId: '',
        currentUserAuth: ''
      }
    }
  }
</script>
<style>
  .home_container {
    height: 100%;
    position: absolute;
    top: 0px;
    left: 0px;
    width: 100%;
  }

  .el-header {
    background-color: #17393f;
    color: #333;
    text-align: center;
    display: flex;
    align-items: center;
    height: 100px;
    justify-content: space-between;
  }

  .el-aside {
    background-color: #abbfbd;
  }

  .el-main {
    background-color: #fff;
    color: #000;
    text-align: center;
  }

  .home_title {
    color: #fff;
    font-size: 30px;
    display: inline;
  }

  .home_userinfo {
    color: #fff;
    cursor: pointer;
  }

  .home_userinfoContainer {
    display: inline;
    margin-right: 20px;
  }
</style>
