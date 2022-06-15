<template>
    <div v-loading="loading">
      <div style="text-align: left">
        <el-form :model="updateInfoForm" label-position="top" ref="updateInfoForm"
                 style="color:#20a0ff;font-size: 14px;margin-top: 30px;">
          <el-form-item prop="nickname">
            <el-button>修改昵称</el-button>
            <el-input type="nickname" v-model.nickname="updateInfoForm.nickname" auto-complete="off" style="width: 300px"
                      placeholder="请输入新昵称。不超过14个字符！"></el-input>
            <el-button type="success" @click="submitForm1('updateInfoForm')">确定</el-button>
          </el-form-item>


        </el-form>
        <el-form :model="changepwdForm" label-position="top" ref="changepwdForm"
                 style="color:#20a0ff;font-size: 14px;margin-top: 50px;">
          <el-form-item prop="password">
            <el-button>修改密码</el-button>
            <el-input type="password" v-model.password="changepwdForm.password" auto-complete="off" style="width: 300px"
                      placeholder="请输入新密码..."></el-input>
          </el-form-item>
          <el-form-item>
            <el-button>确认密码</el-button>
            <el-input type="password" v-model="password2" style="width: 300px"
                      placeholder="请再次输入新密码..."></el-input>
            <el-button type="success" @click="submitForm2('changepwdForm')">确定</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
</template>
<script>
import {getRequest} from '../utils/api'
import {putRequest} from '../utils/api'
export default{
  data(){
    return {
      password2: '',
      changepwdForm: {
        password: ''
      },
      updateInfoForm: {
        nickname: '',
        userface: ''
      },
      loading: false
    }
  },
  methods: {
    submitForm1(formName) {
      var _this = this;
        this.$refs[formName].validate((valid) => {
          if (valid) {
            _this.loading = true;
            putRequest("/updateUserNickname", {nickname: _this.updateInfoForm.nickname}).then(resp=> {
              _this.loading = false;
              if (resp.status == 200) {
                _this.$message({type: resp.data.status, message: resp.data.msg});
              } else {
                _this.$message({type: 'error', message: '修改失败!'});
              }
            }, resp=> {
              _this.loading = false;
              _this.$message({type: 'error', message: '修改失败!'});
            });
          } else {
            _this.$message({type: 'error', message: '!!'})
            return false;
          }
        });
    },
    submitForm2(formName) {
      var _this = this;
      if (this.password2 !== this.changepwdForm.password) {
        this.$message.error('密码不一致')}else{
      this.$refs[formName].validate((valid) => {
        if (valid) {
          _this.loading = true;
          putRequest("/updateUserPwd", {password: _this.changepwdForm.password}).then(resp=> {
            _this.loading = false;
            if (resp.status == 200) {
              _this.$message({type: resp.data.status, message: resp.data.msg});
            } else {
              _this.$message({type: 'error', message: '修改失败!'});
            }
          }, resp=> {
            _this.loading = false;
            _this.$message({type: 'error', message: '修改失败!'});
          });
        } else {
          _this.$message({type: 'error', message: '!!'})
          return false;
        }
      });}
    }
  }
}
</script>



