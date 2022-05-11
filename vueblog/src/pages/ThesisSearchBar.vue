<template>
  <el-form style="margin-bottom: 50px">
    <el-form-item>
      <el-button>
        论文标题
      </el-button>
      <el-input
        placeholder="请输入内容"
        prefix-icon="el-icon-search"
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
        prefix-icon="el-icon-search"
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
        prefix-icon="el-icon-search"
        v-model="searchForm.conference" style="width: 400px" >
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-button >
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
    <el-button type="primary" style="margin-left: 400px" icon="el-icon-search" @click="searchClick">搜索
    </el-button>
  </el-form>
</template>

<script>
import {getRequest} from "../utils/api";

export default {
  name: "ThesisSearchBar",
  props: ["state"],
  data() {
    return {
      searchForm: {
        keywords: '',
        user: '',
        type: '1',
        author: '',
        conference: '',
        direction: ''
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
    }
  }
}
</script>

<style scoped>

</style>
