<style type="text/css">
  .blog_table_footer {
    display: flex;
    box-sizing: content-box;
    padding-top: 10px;
    padding-bottom: 0px;
    margin-bottom: 0px;
    justify-content: space-between;
  }
</style>

<template>
  <div>
    <div style="display: flex;justify-content: flex-start">
      <thesis-search-bar @getSearchForm="getSearchForm" :state="state"></thesis-search-bar>
    </div>
    <!--<div style="width: 100%;height: 1px;background-color: #20a0ff;margin-top: 8px;margin-bottom: 0px"></div>-->
    <el-table
      ref="multipleTable"
      :data="articles"
      tooltip-effect="dark"
      style="width: 100%;overflow-x: hidden; overflow-y: hidden;"
      @selection-change="handleSelectionChange"
      v-loading="loading"
      :max-height="800">
      <el-table-column
        type="selection"
        width="35" align="left" v-if="showEdit || showDelete">
      </el-table-column>
      <el-table-column
        label="标题"
        width="300" align="left">
        <template slot-scope="scope"><span style="color: #409eff;cursor: pointer" @click="itemClick(scope.row)">{{ scope.row.title}}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="nickname"
        label="上传用户"
        width="120" align="left">
      </el-table-column>
      <el-table-column
        prop="author"
        label="论文作者"
        width="120" align="left">
      </el-table-column>
      <el-table-column
        prop="conference"
        label="发表会议"
        width="120" align="left">
      </el-table-column>
      <el-table-column
        label="发表日期" width="130" align="left">
        <template slot-scope="scope">{{ scope.row.publishDate | formatDateTime}}</template>
      </el-table-column>
      <el-table-column
        prop="directionName"
        label="所属研究方向"
        width="120" align="left">
      </el-table-column>
      <el-table-column
        prop="type"
        label="论文类型"
        width="120" align="left"
        :formatter="typeFormatter">
      </el-table-column>
      <el-table-column
        prop="link"
        label="论文链接"
        width="120" align="left">
      </el-table-column>
      <el-table-column
        label="最近编辑时间" width="140" align="left">
        <template slot-scope="scope">{{ scope.row.editTime | formatDateTime}}</template>
      </el-table-column>
      <el-table-column label="操作" align="left" v-if="showEdit || showDelete">
        <template slot-scope="scope">
          <el-button

            @click="handleEdit(scope.$index, scope.row)" v-if="showEdit">编辑
          </el-button>
<!--          <el-button

            @click="handleRestore(scope.$index, scope.row)" v-if="showRestore">还原
          </el-button>-->
          <el-button

            type="danger"
            @click="handleDelete(scope.$index, scope.row)" v-if="showDelete">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="blog_table_footer">
      <el-button type="danger"  style="margin: 0px;" v-show="this.articles.length>0 && showDelete"
                 :disabled="this.selItems.length==0" @click="deleteMany">批量删除
      </el-button>
      <span></span>
      <el-pagination
        background
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalCount" @current-change="currentChange" v-show="this.articles.length>0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
  import {putRequest} from '../utils/api'
  import {getRequest} from '../utils/api'
  import thesisSearchBar from "./ThesisSearchBar";
//  import Vue from 'vue'
//  var bus = new Vue()

  export default{
    components: {
      thesisSearchBar
    },
    data() {
      return {
        articles: [],
        selItems: [],
        loading: false,
        currentPage: 1,
        totalCount: -1,
        pageSize: 8,
        searchForm: {
          keywords: '',
          user: '',
          type: '',
          author: '',
          conference: '',
          direction: ''
        },
        dustbinData: [],
      }
    },
    props: ['state', 'showEdit', 'showDelete', 'activeName', 'showRestore'],
    mounted: function () {
      //init the page
      var _this = this;
      this.loading = true;
      this.loadBlogs(1, this.pageSize);
      // ???
      var _this = this;
      window.bus.$on('blogTableReload', function () {
        _this.loading = true;
        _this.loadBlogs(_this.currentPage, _this.pageSize);
      })
    },
    methods: {
      getSearchForm(data) {
        this.searchForm = data;
        this.loadBlogs(1, this.pageSize);
      },
      itemClick(row){
        this.$router.push({path: '/blogDetail', query: {aid: row.id}})
      },
      deleteMany(){
        var selItems = this.selItems;
        for (var i = 0; i < selItems.length; i++) {
          this.dustbinData.push(selItems[i].id)
        }
        this.deleteToDustBin(selItems[0].state)
      },
      //翻页
      currentChange(currentPage){
        this.currentPage = currentPage;
        this.loading = true;
        this.loadBlogs(currentPage, this.pageSize);
      },
      loadBlogs(page, count){ //keywords search
        var _this = this;
        var url = '';
        url = "/article/all?state=" + this.state +
              "&page=" + page + "&count=" + count +
              "&keywords=" + this.searchForm.keywords +
              "&nickname=" + this.searchForm.user +
              "&type=" + this.searchForm.type +
              "&author=" + this.searchForm.author +
              "&conference=" + this.searchForm.conference +
              "&direction=" + this.searchForm.direction;
        console.info()
        /*if (this.state == -2) {
          url = "/admin/article/all" + "?page=" + page + "&count=" + count + "&keywords=" + this.keywords;
        } else {
          url = "/article/all?state=" + this.state + "&page=" + page + "&count=" + count + "&keywords=" + this.keywords;
        }*/
        getRequest(url).then( resp => {
          _this.loading = false;
          if (resp.status == 200) {
            _this.articles = resp.data.articles;
            _this.totalCount = resp.data.totalCount;
          } else {
            _this.$message({type: 'error', message: '数据加载失败!'});
          }
        }, resp=> {
          _this.loading = false;
          if (resp.response.status == 403) {
            _this.$message({type: 'error', message: resp.response.data});
          } else {
            _this.$message({type: 'error', message: '数据加载失败!'});
          }
        }).catch(resp=> {
          //压根没见到服务器
          _this.loading = false;
          _this.$message({type: 'error', message: '数据加载失败!'});
        })
      },
      handleSelectionChange(val) {
        this.selItems = val;
      },
      handleEdit(index, row) {
        this.$router.push({path: '/editBlog', query: {from: this.activeName,id:row.id}});
      },
      handleDelete(index, row) {
        this.dustbinData.push(row.id);
        this.deleteToDustBin(row.state);
      },
      handleRestore(index, row) {
        let _this = this;
        this.$confirm('将该文件还原到原处，是否继续？','提示',{
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        } ).then(() => {
          _this.loading = true;
          putRequest('/article/restore', {articleId: row.id}).then(resp=> {
            if (resp.status == 200) {
              var data = resp.data;
              _this.$message({type: data.status, message: data.msg});
              if (data.status == 'success') {
                window.bus.$emit('blogTableReload')//通过选项卡都重新加载数据
              }
            } else {
              _this.$message({type: 'error', message: '还原失败!'});
            }
            _this.loading = false;
          });
        }).catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消还原'
          });
        });
      },
      deleteToDustBin(state){
        var _this = this;
        this.$confirm(state != 2 ? '将该文件放入回收站，是否继续?' : '永久删除该文件, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.loading = true;
          var url = '';
          if (_this.state == -2) {
            url = "/admin/article/dustbin";
          } else {
            url = "/article/dustbin";
          }
          putRequest(url, {aids: _this.dustbinData, state: state}).then(resp=> {
            if (resp.status == 200) {
              var data = resp.data;
              _this.$message({type: data.status, message: data.msg});
              if (data.status == 'success') {
                window.bus.$emit('blogTableReload')//通过选项卡都重新加载数据
              }
            } else {
              _this.$message({type: 'error', message: '删除失败!'});
            }
            _this.loading = false;
            _this.dustbinData = []
          }, resp=> {
            _this.loading = false;
            _this.$message({type: 'error', message: '删除失败!'});
            _this.dustbinData = []
          });
        }).catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          });
          _this.dustbinData = []
        });
      },
      typeFormatter(row, col) {
        if (row.type == 0) {
          return "理论证明型";
        } else if (row.type == 1) {
          return "综述型";
        } else if (row.type == 2) {
          return "实验型";
        } else if (row.type == 3) {
          return "工具型";
        } else if (row.type == 4){
          return "数据集型";
        } else {
          return "None";
        }
      }
    },
  }
</script>
