<template>
  <el-container class="article_list">
    <el-main class="main">
      <el-tabs v-model="activeName" @tab-click="handleClick" type="card">
        <el-tab-pane label="全部论文" name="all">
          <thesis-table :state=1 :showEdit="false" :showDelete="false" :showRestore="false"></thesis-table>
        </el-tab-pane>
        <el-tab-pane label="我上传的论文" name="myAll">
          <thesis-table :state=0 :showEdit="true" :showDelete="true" :showRestore="false"></thesis-table>
        </el-tab-pane>
        <el-tab-pane label="回收站" name="dustbin">
          <thesis-table :state=2 :showEdit="false" :showDelete="true" :showRestore="true"></thesis-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>
<script>
  import ThesisTable from "./ThesisTable";
  import {postRequest} from '../utils/api'
  import {putRequest} from '../utils/api'
  import {deleteRequest} from '../utils/api'
  import {getRequest} from '../utils/api'
  export default {
    mounted: function () {
      var _this = this;
      getRequest("/isAdmin").then(resp=> {
        if (resp.status == 200) {
          _this.isAdmin = resp.data;
        }
      })
    },
    data() {
      return {
        activeName: 'all',
        isAdmin: false
      };
    },
    methods: {
      handleClick(tab, event) {
//        console.log(tab, event);
      }
    },
    components: {
      ThesisTable,
    }
  };
</script>
<style>
  .article_list > .header {
    background-color: #ececec;
    margin-top: 10px;
    padding-left: 5px;
    display: flex;
    justify-content: flex-start;
  }

  .article_list > .main {
    /*justify-content: flex-start;*/
    display: flex;
    flex-direction: column;
    padding-left: 0px;
    background-color: #fff;
    padding-top: 0px;
    margin-top: 8px;
  }
</style>
