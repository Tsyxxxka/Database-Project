<template>
  <div>
    <el-form style="margin-bottom: 50px;margin-left: 10px;">
      <el-form-item>
        <el-button>
          论文标题
        </el-button>
        <el-input
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
      <el-form-item>
        <el-button >
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

        <!--<el-autocomplete v-if="barType==1"
            style="width: 400px"
            prefix-icon="el-icon-search"
            v-model="searchForm.direction"
            :fetch-suggestions="querySearchDirection"
            @select="handleSelectDirection"
        ></el-autocomplete>-->


      </el-form-item>
      <!--      TODO-->
      <!--      <el-form-item v-if="barType!=1" style="margin-left: 100px;">
              没有所需研究方向？联系管理员（普通用户）或<el-button type="text">添加研究方向</el-button>
            </el-form-item>-->
      <el-form-item v-if="barType!=1">
        <el-button >
          论文链接
        </el-button>
        <el-input
            prefix-icon="el-icon-search"
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
      <el-button type="primary" style="margin-left: 400px" icon="el-icon-search" @click="searchClick" v-if="barType==1">搜索
      </el-button>
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
      directionList: [
        /*{
          id: 1,
          parentId: 0,
          name: "全部分类",
        },
        { id: 1, parentId: 0, directionName: "一级菜单A"},
        { id: 2, parentId: 0, name: "一级菜单B"},
        { id: 3, parentId: 0, name: "一级菜单C"},
        { id: 4, parentId: 1, name: "二级菜单A-A"},
        { id: 5, parentId: 1, name: "二级菜单A-B"},
        { id: 6, parentId: 2, name: "二级菜单B-A"},
        { id: 7, parentId: 4, name: "三级菜单A-A-A"},
        { id: 8, parentId: 7, name: "四级菜单A-A-A-A"},
        { id: 9, parentId: 0, name: "一级菜单C"},
        { id: 10, parentId: 0, name: "一级菜单end"}*/
      ],

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
    this.onLoadDirections();
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
  }
}
</script>

<style scoped>

</style>
