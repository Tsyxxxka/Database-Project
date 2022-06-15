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
        <SelectTree
            prefix-icon="el-icon-search"
            style="width: 310px"
            :props="props"
            :options="optionData"
            :value="valueId"
            :clearable="isClearable"
            :accordion="isAccordion"
            @getValue="getValue($event)"
        />
        <el-select v-model="searchForm.multiDirection" multiple filterable placeholder="请选择" :filter-method="queryDirection"
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
<!--      <el-form-item v-if="barType!=1">-->
<!--        <el-button>-->
<!--          研究方向-->
<!--        </el-button>-->
<!--        <el-autocomplete-->
<!--          style="width: 310px"-->
<!--          prefix-icon="el-icon-search"-->
<!--          v-model="searchForm.direction"-->
<!--          :fetch-suggestions="querySearchDirection"-->
<!--          @select="handleSelectDirection"-->
<!--        ></el-autocomplete>-->
<!--        <el-button type="text" @click="goToDirection">添加研究方向</el-button>-->
<!--      </el-form-item>-->
      <el-form-item v-else>
        <el-button>
          研究方向
        </el-button>
        <SelectTree
            prefix-icon="el-icon-search"
            style="width: 400px"
            :props="props"
            :options="optionData"
            :value="valueId"
            :clearable="isClearable"
            :accordion="isAccordion"
            @getValue="getValue($event)"
        />
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
import {mavonEditor} from "mavon-editor";
import ReferenceThesis from "./ReferenceThesis";
import SelectTree from "../components/treeSelect.vue";

export default {
  name: "ThesisSearchBar",
  components:{
    SelectTree
  },
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
      isClearable: true, // 可清空（可选）
      isAccordion: true, // 可收起（可选）
      valueId: 0, // 初始ID（可选）
      props: {
        // 配置项（必选）
        value: "id",
        label: 'directionName',
        children: "children"
        // disabled:true
      },
      // 选项列表（必选）
      directionList: [],
      directionAllForSelect: [],
      //value1: [],
      searchForm: {
        keywords: '',
        user: '',
        type: '',
        author: '',
        conference: '',
        isMul: false,
        direction: '',
        multiDirection: [],
        //bar-type==0
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
      currentUserAuth: ''
    }
  },
  mounted: function () {
    this.loadUsers();
    this.loadDirections();
    this.onLoadDirections();

    var _this = this;
    getRequest("/currentUserId").then(function (msg) {
      _this.currentUserId = msg.data;
      if (msg.data) {
        getRequest("/admin/user/" + _this.currentUserId).then(resp=> {
          if (resp.status == 200) {
            _this.currentUserAuth = resp.data.auth;
          }
        }, resp=> {
          _this.$message({type: 'error', message: '用户身份错误！请联系管理员。'});
        });
      }
    }, function (msg) {
      this.$message.error("用户身份错误！请联系管理员。");
    });
  },
  computed: {
    /* 转树形数据 */
    optionData() {
      let cloneData = JSON.parse(JSON.stringify(this.directionList)); // 对源数据深度克隆
      return cloneData.filter(father => {
        // 循环所有项，并添加children属性
        let branchArr = cloneData.filter(child => father.id == child.parentId); // 返回每一项的子级数组
        branchArr.length > 0 ? (father.children = branchArr) : ""; //给父级添加一个children属性，并赋值
        return father.parentId == 0; //返回第一层
      });
    }
  },
  methods: {
    // 取值
    getValue(value) {
      this.valueId = value;
      getRequest('/direction/' + this.valueId).then(resp =>{
        if (resp.status == 200) {
          this.searchForm.direction = resp.data.directionName;
        }
      })
      //console.log(this.valueId);
      //console.log(this.searchForm.direction);
    },
    onLoadDirections(){
      let that = this;
      getRequest('/direction/all').then(resp =>{
        if (resp.status == 200) {
          resp.data.forEach(r => {
            if (r.parentId == null) {
              r.parentId = 0;
            }
          })
          that.directionList = resp.data;
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
            this.directionAllForSelect = this.directionAll;
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
    queryDirection(queryString) {
      var directionAll = this.directionAll;
      if (queryString == '') {
        this.directionAllForSelect = directionAll;
        return
      }
      this.directionAllForSelect = queryString ? directionAll.filter(this.createFilter(queryString)) : directionAll;
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
      if (this.barType == 0) {
        this.searchForm.isMul = true;
      };
      if (this.searchType == '') {
        this.searchForm.type = '';
      };
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
      if(this.currentUserAuth != 0)
        this.$router.push('/setSearchDirection');
      else{
        this.$message.error("请联系管理员添加研究方向");
      }
    }
  }
}
</script>

<style scoped>

</style>
