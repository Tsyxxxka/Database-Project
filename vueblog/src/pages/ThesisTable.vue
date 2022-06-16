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
    <el-table
      ref="multipleTable"
      :key="isAdmin"
      :data="articles"
      tooltip-effect="dark"
      style="width: 100%;overflow-x: hidden; overflow-y: hidden;"
      @selection-change="handleSelectionChange"
      v-loading="loading"
      :max-height="800">
      <el-table-column
        type="selection"
        width="35" align="left"
        v-if="isAdmin || showDelete">
      </el-table-column>
      <el-table-column
        prop="title"
        label="标题"
        width="300" align="left">
        <template slot-scope="scope">
          <span style="color: #409eff;cursor: pointer" @click="itemClick(scope.row)">{{ scope.row.title}}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="nickname"
        label="上传用户"
        width="150" align="left">
      </el-table-column>
      <el-table-column
        prop="author"
        label="论文作者"
        width="150" align="left">
      </el-table-column>
      <el-table-column
        prop="conference"
        label="发表会议"
        width="150" align="left">
      </el-table-column>
      <el-table-column
        prop="publishDate"
        label="发表日期" width="130" align="left">
        <template slot-scope="scope">{{ scope.row.publishDate.toString().slice(0,10) }}</template>
      </el-table-column>
      <el-table-column
        prop="directionName"
        label="所属研究方向"
        width="150" align="left">
      </el-table-column>
      <el-table-column
        prop="type"
        label="论文类型"
        width="120" align="left"
        :formatter="typeFormatter">
      </el-table-column>
      <el-table-column
        prop="editTime"
        label="最近编辑时间" width="140" align="left">
        <template slot-scope="scope">{{ scope.row.editTime | formatDateTime}}</template>
      </el-table-column>
      <el-table-column label="操作" align="left" v-if="isAdmin || showEdit || showDelete">
        <template slot-scope="scope">
          <el-button
            @click="handleEdit(scope.$index, scope.row)" v-if="showEdit">编辑
          </el-button>
          <el-button
            @click="handleRestore(scope.$index, scope.row)" v-if="showRestore">还原
          </el-button>
          <el-button
            type="danger"
            @click="handleDelete(scope.$index, scope.row)" v-if="isAdmin || showDelete">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="blog_table_footer">
      <el-button type="danger"  style="margin: 0px;" v-show="this.articles.length>0 && (isAdmin || showDelete)"
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
    props: {
      state: {
        type: Number,
        default: 1
      },
      showEdit: {
        type: Boolean,
        default: false,
      },
      showDelete: {
        type: Boolean,
        default: false,
      },
      showRestore: {
        type: Boolean,
        default: false,
      },
      isAdmin: {
        type: Boolean,
        default: false,
      }
    },
    mounted: function () {
      //init the page
      var _this = this;
      this.loading = true;
      this.loadBlogs(1, this.pageSize);
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
        this.$router.push({path: '/thesisDetail', query: {aid: row.id}})
      },
      deleteMany(){
        var selItems = this.selItems;
        for (var i = 0; i < selItems.length; i++) {
          this.dustbinData.push(selItems[i].id)
        }
        this.deleteToDustBin(this.state)
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
        getRequest(url).then( resp => {
          _this.loading = false;
          if (resp.status == 200) {
            _this.articles = resp.data.articles;
            console.info(_this.articles);
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
          //服务器 fail
          _this.loading = false;
          _this.$message({type: 'error', message: '数据加载失败!'});
        })
      },
      handleSelectionChange(val) {
        this.selItems = val;
      },
      handleEdit(index, row) {
        this.$router.push({path: '/editThesis', query: {aid:row.id}});
      },
      handleDelete(index, row) {
        this.dustbinData.push(row.id);
        this.deleteToDustBin(this.state);
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
                window.bus.$emit('blogTableReload')//
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
        var _this = this; // 0:myAll  2:dustbin
        this.$confirm(state != 2 ? '将该文件放入回收站，是否继续?' : '永久删除该文件, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.loading = true;
          putRequest('/article/dustbin', {aids: _this.dustbinData, state: state}).then(resp=> {
            if (resp.status == 200) {
              var data = resp.data;
              _this.$message({type: data.status, message: data.msg});
              if (data.status == 'success') {
                window.bus.$emit('blogTableReload');
                _this.$message({type: 'success', message: '删除成功!'});
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
