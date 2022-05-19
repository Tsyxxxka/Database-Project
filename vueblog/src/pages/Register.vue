<template>
  <el-form
    class="login-container"
    label-position="left"
    label-width="70px"
    v-loading="loading">
    <h3 class="login_title" >用户注册</h3>
    <el-form-item label="账户名">
      <el-input type="username" v-model="mailForm.username"></el-input>
    </el-form-item>
    <el-form-item label="密码">
      <el-input type="password" v-model="mailForm.password"></el-input>
    </el-form-item>
    <el-form-item label="确认密码">
      <el-input type="password" v-model="password2"></el-input>
    </el-form-item>
    <el-form-item label="邮箱">
      <el-input style="width: 270px; padding-right:12px;" v-model="mailForm.email"/>
      <el-button type="primary" @click="getCode">
        <span v-show="show">发送验证码</span>
        <span v-show="!show">{{ count }} s后重发</span>
      </el-button>
    </el-form-item>
    <el-form-item label="验证码">
      <el-input v-model="mailForm.code"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" style="width: 40%;" @click="getCurrentTime(); submit();">确认注册</el-button>
      <el-button type="text" style="width: 40%;" @click="toLogin">返回登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {postRequest} from '../utils/api'
import {putRequest} from '../utils/api'
import resp from "autoprefixer";

const TIME_COUNT = 60 // 设置一个全局的倒计时的时间
export default {
  name: "Register",
  data() {
    return {
      show: true,
      count: '',
      password2: '',
      mailForm: {
        email: '',
        password: '',
        code: '',
        username: '',
        regTime:''
      },
      loading: false
    };
  },
  methods: {
    clearForm: function () {
      this.mailForm.username = '';
      this.mailForm.password = '';
      this.mailForm.email = '';
      this.mailForm.code = '';
      this.mailForm.regTime = ''
    },
    toLogin() {
      this.$router.replace({path: '/'});
    },
    getCurrentTime() {
      var _this = this;
      let yy = new Date().getFullYear();
      let mm = new Date().getMonth()+1;
      let dd = new Date().getDate();
      let hh = new Date().getHours();
      let mf = new Date().getMinutes()<10 ? '0'+new Date().getMinutes() : new Date().getMinutes();
      let ss = new Date().getSeconds()<10 ? '0'+new Date().getSeconds() : new Date().getSeconds();
      _this.gettime = yy+'/'+mm+'/'+dd+' '+hh+':'+mf+':'+ss;
      this.mailForm.regTime = _this.gettime;
    },
    getCode() {
      const regexMail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
      if (!regexMail.test(this.mailForm.email)) {
        this.$message.warning('请输入正确的邮箱')
      } else {
        postRequest('/sendMail',{email: this.mailForm.email}).then(resp => {
          if (resp.status === 200) {
            this.$message.success(resp.data.msg)
          }else{
            this.$message.error('发送失败!')
          }
        }).catch(() => {
          this.$message.error('发送失败!!')
        });
        // 验证码倒计时
        if (!this.timer) {
          this.count = TIME_COUNT
          this.show = false
          this.timer = setInterval(() => {
            if (this.count > 0 && this.count <= TIME_COUNT) {
              this.count--
            } else {
              this.show = true
              clearInterval(this.timer)
              this.timer = null
            }
          }, 1000)
        }
      }
    },
    submit() {
      // 验证码校验
      const regexCode = /^[0-9]{6}$/
      if (!regexCode.test(this.mailForm.code)) {
        this.$message.warning('请输入正确的验证码')
      } else if (this.password2 !== this.mailForm.password) {
        this.$message.error('密码不一致');
      } else {
        postRequest('/insertUser', {
          username: this.mailForm.username,
          password: this.mailForm.password,
          email: this.mailForm.email,
          code: this.mailForm.code,
          regTime: this.mailForm.regTime
        }).then(res => {
          if ((res.status === 200)) {
            if (res.data.status == 'success') {
              this.$router.replace({path: '/'});
              this.$message.success(res.data.msg)
            } else {
              this.$message.error(res.data.msg);
            }
          } else {
            this.$message.error(res.data.msg);
          }
        }).catch(() => {
          this.$message.error('请重新获取验证码');
        });
      }
    },
  }
}
</script>

<style scoped>
.el-form-item__label {
  text-align: justify;
}
.el-input {
  width: 400px;
}
.register {
  width: 435px;
  margin: 180px auto;
  border: 1px solid gray;
  padding: 20px;
  border-radius: 10px;
}
.register el-input{
  width: 260px
}
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 180px auto;
  width: 500px;
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

</style>
