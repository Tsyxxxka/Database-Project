<template>
  <div>
    <el-form
      style="margin-bottom: 50px;margin-left: 10px;">
      <el-form-item>
        <el-button>
          论文标题
        </el-button>
        <el-input
          v-model="searchForm.keywords" style="width: 400px">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button >
          论文类型
        </el-button>
        <el-autocomplete
          style="width: 400px"
          prefix-icon="el-icon-search"
          v-model="searchType"
          :fetch-suggestions="querySearchType"
          @select="handleSelectType"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item>
        <el-button>
          论文作者
        </el-button>
        <el-input
          v-model="searchForm.author" style="width: 400px" >
        </el-input>
      </el-form-item>
      <el-form-item v-if="state!=0">
        <el-button>
          上传用户
        </el-button>
        <el-autocomplete
          style="width: 400px"
          prefix-icon="el-icon-search"
          v-model="searchForm.user"
          :fetch-suggestions="querySearchUser"
          @select="handleSelectUser"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item>
        <el-button >
          发表会议
        </el-button>
        <el-input
          v-model="searchForm.conference" style="width: 400px" >
        </el-input>
      </el-form-item>
      <el-form-item v-if="barType!=1">
        <el-button>
          发表日期
        </el-button>
        <el-date-picker
          v-model="searchForm.publishDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="                             选择论文发表日期"
          style="width: 400px">
        </el-date-picker>
      </el-form-item>
      <el-form-item v-if="barType!=1">
        <el-button>
          研究方向
        </el-button>
        <el-autocomplete
          style="width: 310px"
          prefix-icon="el-icon-search"
          v-model="searchForm.direction"
          :fetch-suggestions="querySearchDirection"
          @select="handleSelectDirection"
        ></el-autocomplete>
        <el-button type="text" @click="goToDirection">添加研究方向</el-button>
      </el-form-item>
      <el-form-item v-else>
        <el-button>
          研究方向
        </el-button>
        <el-autocomplete
          style="width: 400px"
          prefix-icon="el-icon-search"
          v-model="searchForm.direction"
          :fetch-suggestions="querySearchDirection"
          @select="handleSelectDirection"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item v-if="barType!=1">
        <el-button >
          论文链接
        </el-button>
        <el-input
          v-model="searchForm.link" style="width: 400px" >
        </el-input>
      </el-form-item>
      <el-form-item v-if="barType!=1">
        <el-button style="margin-bottom: 100px;">
          论文摘要
        </el-button>
        <el-input
          type="textarea"
          :autosize="{ minRows: 6, maxRows: 10}"
          prefix-icon="el-icon-search"
          v-model="searchForm.summary" style="width: 400px" >
        </el-input>
      </el-form-item>
      <el-form-item v-if="barType==1">
        <el-button type="success" style="margin-left: 300px" @click="resetSearch">重置
        </el-button>
        <el-button type="primary" style="margin-left: 10px" icon="el-icon-search" @click="searchClick">搜索
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {getRequest} from "../utils/api";

export default {
  name: "ThesisSearchBar",
  props: {
    state: {
      type: Number,
      default: 1,
    },
    barType: {
      type: Number,
      default: 1, //1:search 0:upload
    },
  },
  data() {
    return {
      searchForm: {
        keywords: '',
        user: '',
        type: '',
        author: '',
        conference: '',
        direction: '',
        //bartype==0
        link: '',
        summary: '',
        publishDate: '',
      },
      userAll: [],
      directionAll: [],
      searchType: '', //value
      typeAll: [
        {"value": "理论证明型", "type": 0},
        {"value": "综述型", "type": 1},
        {"value": "实验型", "type": 2},
        {"value": "工具型", "type": 3},
        {"value": "数据集型", "type": 4}
      ],
    }
  },
  mounted: function () {
    this.loadUsers();
    this.loadDirections();
  },
  methods: {
    loadUsers() {
      getRequest('/getAllNickname').then(resp => {
        if (resp.status == 200) {
          resp.data.forEach(r => {
            if (r.nickname != null) {
              this.userAll.push({"value": r.nickname});
            }
          })
        }
      })
    },
    loadDirections() {
      getRequest('/direction/all').then(resp => {
        if (resp.status == 200) {
          resp.data.forEach(r => {
            if (r.directionName != null) {
              this.directionAll.push({"value": r.directionName});
            }
          })
        }
      })
    },
    querySearchUser(queryString, cb) {
      var userAll = this.userAll;
      var results = queryString ? userAll.filter(this.createFilter(queryString)) : userAll;
      cb(results);
    },
    querySearchType(queryString, cb) {
      cb(this.typeAll);
    },
    querySearchDirection(queryString, cb) {
      var directionAll = this.directionAll;
      var results = queryString ? directionAll.filter(this.createFilter(queryString)) : directionAll;
      cb(results);
    },
    createFilter(queryString) {
      return (all) => {
        return (all.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
      };
    },
    handleSelectUser(item) {
      this.searchForm.user = item.value;
    },
    handleSelectType(item) {
      this.searchType = item.value;
      this.searchForm.type = item.type;
    },
    handleSelectDirection(item) {
      this.searchForm.direction = item.value;
    },
    searchClick() {
      if (this.searchType == '') {
        this.searchForm.type = '';
      }
      this.$emit('getSearchForm',this.searchForm);
    },
    resetSearch() {
      for(let i in this.searchForm){
        this.searchForm[i]='';
      }
      this.searchType='';
      this.searchClick();
    },
    goToDirection() {
      this.$router.push('/setSearchDirection');
    }
  }
}
</script>

<style scoped>

</style>
