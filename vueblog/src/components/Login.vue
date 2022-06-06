<template>
  <div  class="background_style">
    <el-form class="login-container" label-position="left"
             label-width="0px" v-loading="loading">
      <h3 class="login_title" >系统登录</h3>
      <el-form-item prop="account">
        <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
      </el-form-item>
      <el-form-item prop="checkPass">
        <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
      </el-form-item>
      <el-checkbox class="login_remember" v-model="checked" label-position="left">记住密码</el-checkbox>
      <el-button style="margin-left: 90px;"  type="text"> 没有账号？</el-button>
      <el-button @click.native.prevent="login=false; toRegister();">注册</el-button>
      <el-form-item>
        <el-button
          type="primary"
          style="width: 100%;"
          @click.native.prevent="login=true; submitClick();" >
          登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import {postRequest} from '../utils/api'
import {putRequest} from '../utils/api'
export default{
  data(){
    return {
      login: true,
      checked: false,
      loginForm: {
        username: 'Good',
        password: '123'
      },
      loading: false
    }
  },
  methods: {
    toRegister() {
      this.$router.replace({path: '/toRegister'});
    },
    clearForm: function () {
      this.loginForm.username = '';
      this.loginForm.password = '';
    },
    submitClick: function (type) {
      if (this.loginForm.username == '') {
        this.$message.error('请输入账号!');
        return;
      }
      if (this.loginForm.password == '') {
        this.$message.error('请输入密码!');
        return;
      }
      var _this = this;
      this.loading = true;
      postRequest( '/login', {
        username: this.loginForm.username,
        password: this.loginForm.password
      }).then(resp=> {
        _this.loading = false;
        if (resp.status == 200) {
          //成功
          var json = resp.data;
          if (json.status == 'success') {
            _this.$router.replace({path: '/home'});
          } else {
            _this.$message.error(resp.data.msg);
          }
        } else {
          //失败
          _this.$alert('登录失败!!', '失败!!');
        }
      }, resp=> {
        _this.loading = false;
        _this.$alert('找不到服务器⊙﹏⊙∥!', '失败!');
      });
    }
  }
}
</script>
<style>
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 240px auto;
  width: 400px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}

.login_remember {
  margin: 0px 0px 35px 0px;
  text-align: left;
}
.background_style {
  width: 100%;
  height: 100%;
  position: fixed;
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-image: url("../assets/background.jpg");
}
</style>
