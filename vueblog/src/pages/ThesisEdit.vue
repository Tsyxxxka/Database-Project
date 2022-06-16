<template>
  <div style="margin-top: 30px;" v-loading="loading">
    <div style="text-align: left; margin-top: 10px;margin-bottom: 20px;">
      <el-button type="text" icon="el-icon-back" @click="goBack" style="padding-bottom: 0px;">返回</el-button>
    </div>
    <div style="height: 700px">
      <el-form style="margin-bottom: 50px;margin-left: 10px;">
        <el-form-item>
          <el-button>
            论文标题
          </el-button>
          <el-input
            prefix-icon="el-icon-search"
            v-model="updateForm.keywords" style="width: 400px">
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
            v-model="updateForm.author" style="width: 400px" >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button >
            发表会议
          </el-button>
          <el-input
            prefix-icon="el-icon-search"
            v-model="updateForm.conference" style="width: 400px" >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button>
            发表日期
          </el-button>
          <el-date-picker
            v-model="updateForm.publishDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="                             选择论文发表日期"
            style="width: 400px">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button>
            研究方向
          </el-button>
          <el-select v-model="updateForm.multiDirection" multiple filterable placeholder="请选择" :filter-method="queryDirection"
                     style="width: 310px">
            <el-option
              v-for="item in directionAllForSelect"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
          <el-button type="text" @click="goToDirection">添加研究方向</el-button>
        </el-form-item>
        <el-form-item>
          <el-button >
            论文链接
          </el-button>
          <el-input
            prefix-icon="el-icon-search"
            v-model="updateForm.link" style="width: 400px" >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="margin-bottom: 100px;">
            论文摘要
          </el-button>
          <el-input
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 10}"
            prefix-icon="el-icon-search"
            v-model="updateForm.summary" style="width: 400px" >
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <el-button-group>
      <el-button type="success" @click="editThesis">更新</el-button>
    </el-button-group>
  </div>
</template>

<script>
import ThesisSearchBar from "./ThesisSearchBar";
import {getRequest, postRequest} from "../utils/api";

export default {
  name: "ThesisEdit",
  components: {
    ThesisSearchBar,
  },
  data() {
    return {
      article: {},
      updateForm: {
        keywords: '', //title
        type: '',
        author: '',
        conference: '',
        directionName: '',
        multiDirection: [],
        summary: '',
        publishDate: '',
        link: ''
      },
      searchType: '',
      loading: '',
      userAll: [],
      directionAll: [],
      directionAllForSelect: [],
      typeAll: [
        {"value": "理论证明型", "type": 0},
        {"value": "综述型", "type": 1},
        {"value": "实验型", "type": 2},
        {"value": "工具型", "type": 3},
        {"value": "数据集型", "type": 4}
      ],
    }
  },
  mounted() {
    this.loadUsers();
    this.loadDirections();
    var aid = this.$route.query.aid;
    this.loading = true;
    getRequest("/article/" + aid).then(resp=> {
      if (resp.status == 200) {
        this.updateForm.author = resp.data.author;
        this.updateForm.conference = resp.data.conference;
        this.updateForm.keywords = resp.data.title;
        this.updateForm.publishDate = resp.data.publishDate;
        this.updateForm.multiDirection = resp.data.multiDirection;
        //this.updateForm.directionName = resp.data.directionName;
        this.updateForm.summary = resp.data.summary;
        this.updateForm.link = resp.data.link;

        this.updateForm.type = resp.data.type;
        var type = this.updateForm.type;
        if (type == 0) {
          this.searchType = "理论证明型";
        } else if (type == 1) {
          this.searchType = "综述型";
        } else if (type == 2) {
          this.searchType = "实验型";
        } else if (type == 3) {
          this.searchType = "工具型";
        } else if (type == 4){
          this.searchType = "数据集型";
        } else {
          this.searchType = "None";
        }
      }
      this.loading = false;
    }, resp=> {
      this.loading = false;
      this.$message({type: 'error', message: '页面加载失败!'});
    });
  },
  methods: {
    goBack(){
      this.$router.go(-1);
    },
    goToDirection() {
      if(this.currentUserAuth != 0)
        this.$router.push('/setSearchDirection');
      else{
        this.$message.error("请联系管理员添加研究方向");
      }
    },
    queryDirection(queryString) {
      var directionAll = this.directionAll;
      if (queryString == '') {
        this.directionAllForSelect = directionAll;
        return
      }
      this.directionAllForSelect = queryString ? directionAll.filter(this.createFilter(queryString)) : directionAll;
    },
    createFilter(queryString) {
      return (all) => {
        return (all.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
      };
    },
    editThesis() {
      console.info(this.updateForm);
      postRequest('/article/update', {
        id: this.$route.query.aid,
        title: this.updateForm.keywords,
        type: this.updateForm.type,
        author: this.updateForm.author,
        conference: this.updateForm.conference,
        summary: this.updateForm.summary,
        link: this.updateForm.link,
        publishDate: this.updateForm.publishDate,
        multiDirection: this.updateForm.multiDirection,
        //directionName: this.updateForm.directionName
      }).then(resp => {
        if (resp.status == 200) {
          //成功
          //add some actions
          this.$message.success('更新成功!');
          //refresh
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
              this.directionAll.push({"id": r.id,"value": r.directionName});
            }
          })
          this.directionAllForSelect = this.directionAll;
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
    handleSelectUser(item) {
      this.updateForm.user = item.value;
    },
    handleSelectType(item) {
      this.searchType = item.value;
      this.updateForm.type = item.type;
    },
    handleSelectDirection(item) {
      this.updateForm.directionName = item.value;
    },
  }
}
</script>

<style scoped>

</style>
