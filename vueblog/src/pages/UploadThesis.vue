<template>
  <div style="margin-top: 30px;">
    <el-steps :active="active" finish-status="success" simple style="margin-top: 20px; margin-bottom: 40px; height: 50px;">
      <el-step title="基本信息"></el-step>
      <el-step title="引用文献"></el-step>
      <el-step title="论文笔记"></el-step>
      <el-step title="附加文件"></el-step>
    </el-steps>
    <div style="height: 600px">
      <thesis-search-bar ref="searchBar" @getSearchForm="getUploadForm" :state="0" :bar-type="0" v-if="active==0"></thesis-search-bar>
      <reference-thesis ref="referenceForm" @getReferenceArticles="getReferenceArticles" v-if="active==1"></reference-thesis>
      <mavon-editor
        style="height: 90%;width: 100%;"
        v-model="note"
        @change="change"
        v-if="active==2"></mavon-editor>
    </div>
    <el-button-group>
<!--      <el-button type="primary" icon="el-icon-arrow-left" @click="getFront" :disabled="active==0">上一步</el-button>-->
      <el-button type="primary" @click="getNext" :disabled="active==3">确定<i class="el-icon-arrow-right el-icon--right"></i></el-button>
      <el-button type="success" @click="uploadThesis" v-if="active==3">上传</el-button>
    </el-button-group>
  </div>
</template>

<script>
import ThesisSearchBar from "./ThesisSearchBar";
import {postRequest} from "../utils/api";
import {mavonEditor} from "mavon-editor";
import ReferenceThesis from "./ReferenceThesis";
export default {
  name: "UploadThesis",
  components: {
    ThesisSearchBar,
    mavonEditor,
    ReferenceThesis
  },
  data() {
    return {
      active: 0,
      uploadForm: {
        keywords: '', //title
        type: '',
        author: '',
        conference: '',
        direction: '',
        summary: '',
        publishDate: ''
      },
      referenceArticles: [],
      note: '', //markdown
    }
  },
  methods: {
    getUploadForm(data) {
      this.uploadForm = data;
    },
    getReferenceArticles(data) {
      this.referenceArticles = data;
    },
    getNext() {
      if (this.active==0) {
        this.$refs.searchBar.searchClick();
        for (let k in this.uploadForm) {
          if (this.uploadForm[k].length == 0 && k != 'user') {
            this.$message.error('请填上必要信息！');
            return;
          }
        }
      }
      if (this.active == 1) {
       this.$refs.referenceForm.getReferenceForm();
      }
      this.active = this.active + 1;
    },/*
    getFront() {
      this.active = this.active - 1;
      if (this.active==0) {
        //TODO: remain info
      }
    },*/
    uploadThesis() {
      var referenceList = [];
      this.referenceArticles.forEach(a => {
        referenceList.push(a);
      })
      postRequest('/article/addNew', {
        title: this.uploadForm.keywords,
        type: this.uploadForm.type,
        author: this.uploadForm.author,
        conference: this.uploadForm.conference,
        summary: this.uploadForm.summary,
        link: this.uploadForm.link,
        publishDate: this.uploadForm.publishDate,
        directionName: this.uploadForm.direction,
        referenceList: referenceList,
        note: this.note
      }).then(resp => {
        if (resp.status == 200) {
          //成功
          this.$message.success('上传成功!');
          this.$router.push('/thesisList');
          /*var json = resp.data;
          if (json.status == 'success') {
            _this.$router.replace({path: '/home'});
          } else {
            _this.$alert('登录失败!', '失败!');
          }*/
        } else {
          //失败
          this.$alert('失败!', '失败!');
        }
      })
    },
    change (value, render) {
      // render 为 markdown 解析后的结果(转化成了HTML格式）
      this.note = render;
    }
  }
}
</script>

<style scoped>

</style>
