<template>
    <div v-loading="loading">
      <div style="text-align: left">
        <el-form :model="updateInfoForm" label-position="top" ref="updateInfoForm"
                 style="color:#20a0ff;font-size: 14px;">
          <el-form-item prop="nickname" label="修改昵称">
            <el-input type="nickname" v-model.nickname="updateInfoForm.nickname" auto-complete="off" style="width: 300px"
                      placeholder="请输入新昵称。不超过14个字符！" size="mini"></el-input>
          </el-form-item>
          <el-form-item prop="userface" label="修改头像">
            <el-input type="userface" v-model.nickname="updateInfoForm.userface" auto-complete="off" style="width: 300px"
                      placeholder="请上传新头像..." size="mini"></el-input>
          </el-form-item>
          <el-button type="primary" @click="submitForm1('updateInfoForm')" size="mini">确定</el-button>

        </el-form>
        <el-form :model="changepwdForm" label-position="top" ref="changepwdForm"
                 style="color:#20a0ff;font-size: 14px;">
          <el-form-item prop="password" label="修改密码">
            <el-input type="password" v-model.password="changepwdForm.password" auto-complete="off" style="width: 300px"
                      placeholder="请输入新密码..." size="mini"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" class="cd" style="width: 300px">
            <el-input type="password" v-model="password2" placeholder="请再次输入新密码..." size="mini"></el-input>
          </el-form-item>
          <el-button type="primary" @click="submitForm2('changepwdForm')" size="mini">确定</el-button>
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



