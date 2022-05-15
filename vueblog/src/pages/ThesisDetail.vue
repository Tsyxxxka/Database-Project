<template>
  <div v-loading="loading">
    <div style="text-align: left; margin-top: 10px;margin-bottom: 20px;">
      <el-button type="text" icon="el-icon-back" @click="goBack" style="padding-bottom: 0px;">返回</el-button>
    </div>
    <div class="content-title">
      <h1> {{article.title}} </h1>
    </div>
    <div style="width: 100%;margin-top: 5px;display: flex;justify-content: flex-end;align-items: center">
      <div style="display: inline; color: #335A66;margin-left: 50px;margin-right:20px;font-size: 15px;">
        上传用户: {{article.nickname}}
      </div>
      <span style="color: #335A66;margin-right:20px;font-size: 15px;">浏览 {{article.pageView==null?0:article.pageView}}</span>
      <span style="color: #335A66;margin-right:20px;font-size: 15px;">最近编辑于 {{article.editTime | formatDateTime}}</span>
    </div>
    <div class="content">
      <h3> 基本信息 </h3>
      <el-descriptions direction="vertical" :column="4" border style="width: 1000px; margin-left:15px; font-size: 20px;">
        <el-descriptions-item label="论文类型">{{typeName}}</el-descriptions-item>
        <el-descriptions-item label="论文作者">{{article.author}}</el-descriptions-item>
        <el-descriptions-item label="发表会议">{{article.conference}}</el-descriptions-item>
        <el-descriptions-item label="研究方向">{{article.directionName}}</el-descriptions-item>
        <el-descriptions-item label="论文摘要">{{article.summary}}</el-descriptions-item>
      </el-descriptions>
    </div>
    <div class="content">
      <h3> 论文链接 </h3>
      <el-descriptions direction="vertical" :column="4" border style="width: 1000px; margin-left:15px; font-size: 20px;">
        <el-descriptions-item label="点击跳转">{{article.link}}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>
<script>
  import {getRequest} from '../utils/api'
  export default{
    methods: {
      goBack(){
        this.$router.go(-1);
      }
    },
    mounted: function () {
      var aid = this.$route.query.aid; //get id of the thesis
      //this.activeName = this.$route.query.an
      var _this = this;
      this.loading = true;
      getRequest("/article/" + aid).then(resp=> {
        if (resp.status == 200) {
          _this.article = resp.data;
          var type = _this.article.type;
          if (type == 0) {
            this.typeName = "理论证明型";
          } else if (type == 1) {
            this.typeName = "综述型";
          } else if (type == 2) {
            this.typeName = "实验型";
          } else if (type == 3) {
            this.typeName = "工具型";
          } else if (type == 4){
            this.typeName = "数据集型";
          } else {
            this.typeName = "None";
          }
        }
        _this.loading = false;
      }, resp=> {
        _this.loading = false;
        _this.$message({type: 'error', message: '页面加载失败!'});
      });
    },
    data(){
      return {
        article: {},
        loading: false,
        activeName: '',
        typeName: ''
      }
    }
  }
</script>
<style scoped>
h2 {
  font-size: 30px;
}

h3 {
  margin-left: 15px;
}

.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}


p {
  line-height: 130%;
  margin: 10px;
}

p {
  display: block;
  margin-block-start: 1em;
  margin-block-end: 1em;
  margin-inline-start: 0px;
  margin-inline-end: 0px;
}

.content-title {
  color: #335A66;
  font-size: 35px;
  border: none;
  text-align: center;
}

.content {
  width: 1050px;
  padding: 25px 25px;
  margin: 25px auto;
  background-color: #fff;
  border-radius: 20px;
  border: #e3e1e4 1px solid;
  font-size: 20px;
}

.bannerImg{
  width: 100%;
  height: inherit;
}

</style>
