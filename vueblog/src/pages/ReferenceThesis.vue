<template>
  <div class="transfer">
    <el-transfer
      filterable
      :filter-method="filterMethod"
      filter-placeholder="搜索文献"
      v-model="selectedArticle"
      :data="articles"
      :titles="titles">
    </el-transfer>
  </div>
</template>
<!--
<script>
export default {
  data() {
    const generateData2 = _ => {
      const data = [];
      const cities = ['上海', '北京', '广州', '深圳', '南京', '西安', '成都'];
      const pinyin = ['shanghai', 'beijing', 'guangzhou', 'shenzhen', 'nanjing', 'xian', 'chengdu'];
      cities.forEach((city, index) => {
        data.push({
          label: city,
          key: index,
          pinyin: pinyin[index]
        });
      });
      return data;
    };
    return {
      data2: generateData2(),
      value2: [],
      filterMethod(query, item) {
        return item.pinyin.indexOf(query) > -1;
      }
    };
  }
};
</script>
-->

<script>
import {getRequest} from "../utils/api";

export default {
  name: "ReferenceThesis",
  data() {
    return {
      selectedArticle: [],
      articles: [],
      titles: ["全部文献","添加引用文献"]
    }
  },
  mounted() {
    var url = '/article//getAllArticles';
    var _this = this;
    getRequest(url).then( resp => {
      _this.loading = false;
      if (resp.status == 200) {
        for (let d in resp.data) {
          _this.articles.push({
            key: resp.data[d].id,
            label: resp.data[d].title
          });
        }
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
  methods: {
    filterMethod(query, item) {
      return item.label.indexOf(query) > -1;
    },
    getReferenceForm() {
      this.$emit('getReferenceArticles',this.selectedArticle);
    }
  }
}
</script>

<style scoped>
.transfer >>> .el-transfer-panel {
  text-align: left;
  width:400px;
  height: 550px;
}
.transfer >>> .el-transfer-panel__list.is-filterable{
  text-align: left;
  width: 400px;
  height: 550px;
}
</style>
