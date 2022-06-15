<template>
  <el-container>
    <el-header class="cate_mana_header">
      <el-input
        placeholder="请输入研究方向名称"
        v-model="directionName" style="width: 200px;">
      </el-input>
      <el-input
        placeholder="请输入归属研究方向ID"
        v-model="parentId" style="width: 200px;">
      </el-input>
      <el-button type="primary" size="medium" style="margin-left: 10px" @click="addNewCate">新增研究方向</el-button>
    </el-header>
    <el-main class="cate_mana_main">
      <el-table
        ref="multipleTable"
        :data="categories"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange" v-loading="loading">
        <el-table-column
          type="selection"
          width="55" align="left">
        </el-table-column>
        <el-table-column
          label="ID"
          prop="id"
          width="120" align="left">
        </el-table-column>
        <el-table-column
          label="研究方向名称"
          prop="directionName"
          width="120" align="left">
        </el-table-column>
        <el-table-column
          prop="parentId"
          label="归属研究方向ID" align="left">
        </el-table-column>
        <el-table-column label="操作" align="left">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleEdit(scope.$index, scope.row)">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="danger" :disabled="this.selItems.length==0" style="margin-top: 10px;width: 100px;"
                 @click="deleteAll" v-if="this.categories.length>0">批量删除
      </el-button>
    </el-main>
  </el-container>
</template>
<script>
import {postRequest} from '../utils/api'
import {putRequest} from '../utils/api'
import {deleteRequest} from '../utils/api'
import {getRequest} from '../utils/api'
import {treeSelect} from "../components/treeSelect";
export default{
  components: {
    treeSelect,
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
    addNewCate(){
      this.loading = true;
      var _this = this;
      postRequest('/direction/',
                  {directionName: this.directionName,
                          parentId: this.parentId}).then(resp=> {
        if (resp.status == 200) {
          var json = resp.data;
          _this.$message({type: json.status, message: json.msg});
          _this.directionName = '';
          _this.parentId = '';
          _this.refresh();
        }
        _this.loading = false;
      }, resp=> {
        if (resp.response.status == 403) {
          _this.$message({
            type: 'error',
            message: resp.response.data
          });
        } else if (resp.response.status == 500) {
          _this.$message({
            type: 'error',
            message: '添加失败，请检查归属方向ID是否正确！'
          });
        }
        _this.loading = false;
      });
    },
    deleteAll(){
      var _this = this;
      this.$confirm('确认删除这 ' + this.selItems.length + ' 条数据?', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(()=> {
        var selItems = _this.selItems;
        var ids = '';
        for (var i = 0; i < selItems.length; i++) {
          ids += selItems[i].id + ",";
        }
        _this.deleteCate(ids.substring(0, ids.length - 1));
      }).catch(() => {
        //取消
        _this.loading = false;
      });
    },
    handleSelectionChange(val) {
      this.selItems = val;
      //console.log(this.selItems);
    },
    handleEdit(index, row){
      var _this = this;
      this.$prompt('请输入新名称', '编辑', {
        confirmButtonText: '更新',
        inputValue: '',
        cancelButtonText: '取消'
      }).then(({value}) => {
        //value就是输入值
        if (value == null || value.length == 0) {
          _this.$message({
            type: 'info',
            message: '数据不能为空!'
          });
        } else {
          _this.loading = true;
          putRequest("/direction/", {id: row.id, directionName: value}).then(resp=> {
            var json = resp.data;
            _this.$message({
              type: json.status,
              message: json.msg
            });
            _this.refresh();
          }, resp=> {
            if (resp.response.status == 403) {
              _this.$message({
                type: 'error',
                message: resp.response.data
              });
            }
            _this.loading = false;
          });
        }
      });
    },
    handleDelete(index, row){
      let _this = this;
      this.$confirm('确认删除 ' + row.directionName + ' ?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.deleteCate(row.id);
      }).catch(() => {
        //取消
        _this.loading = false;
      });
    },
    deleteCate(ids){
      var _this = this;
      this.loading = true;
      //删除
      deleteRequest("/direction/" + ids).then(resp=> {
        var json = resp.data;
        _this.$message({
          type: json.status,
          message: json.msg
        });
        _this.refresh();
      }, resp=> {
        _this.loading = false;
        if (resp.response.status == 403) {
          _this.$message({
            type: 'error',
            message: resp.response.data
          });
        } else if (resp.response.status == 500) {
          _this.$message({
            type: 'error',
            message: '该方向下尚有文章/该方向下尚有方向,删除失败!'
          });
        }
      })
    },
    refresh(){ //list directions
      let _this = this;
      getRequest("/direction/all").then(resp=> {
        _this.categories = resp.data;
        _this.loading = false;
      }, resp=> {
        if (resp.response.status == 403) {
          _this.$message({
            type: 'error',
            message: resp.response.data
          });
        }
        _this.loading = false;
      });
    }
  },
  mounted: function () {
    this.loading = true;
    this.refresh();
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
  data(){
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
      directionName: '',
      parentId: '',
      selItems: [],
      categories: [],
      loading: false
    }
  }
}
</script>
<style>
.cate_mana_header {
  background-color: #FFFFFF;
  margin-top: 20px;
  padding-left: 5px;
  display: flex;
  justify-content: flex-start;
}

.cate_mana_main {
  /*justify-content: flex-start;*/
  display: flex;
  flex-direction: column;
  padding-left: 5px;
  background-color: #ffffff;
  margin-top: 20px;
  padding-top: 10px;
}
</style>
