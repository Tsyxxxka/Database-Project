<template>
  <div
    style="display: flex;height: 500px;width: 100%;align-items: center;justify-content: center;">
    <chart :options="polar1" style="margin-top: 20px"></chart>
    <chart ref="dschart" :options="polar2" style="margin-top: 20px"></chart>
  </div>
</template>

<style>
</style>
<script>
  import ECharts from 'vue-echarts/components/ECharts.vue'
  import 'echarts/lib/chart/line'
  import 'echarts/lib/component/tooltip'

  import 'echarts/lib/component/polar'

  import 'echarts/lib/component/legend'
  import 'echarts/lib/component/title'
  import 'echarts/theme/dark'
  import 'echarts/lib/chart/bar'

  import {getRequest} from '../utils/api'
  export default{
    components: {
      'chart': ECharts
    },
    mounted: function () {
      var _this = this;
      getRequest("/article/dataStatistics").then(resp=> {
        if (resp.status == 200) {
          for (let i = 0; i < resp.data.totalCount; i++) {
            var obj = {};
            obj["name"] = resp.data.directionName[i];
            obj["value"] = resp.data.directionCount[i];
            console.info(typeof [{name: '测试', value: 1}]);
            _this.pieData.push(obj);
            console.info(_this.pieData);
          }
          _this.$refs.dschart.options.xAxis.data = resp.data.directionName;
          _this.$refs.dschart.options.series[0].data = resp.data.directionCount;
        } else {
          _this.$message({type: 'error', message: '数据加载失败!'});
        }
      }, resp=> {
        _this.$message({type: 'error', message: '数据加载失败!'});
      });
    },
    methods: {},
    computed: {
      polar1() {
        var polar = {
          title: {
            text: '我上传的论文',
            x:'center'
          },
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c} ({d}%)",
          },
          legend: {
            orient: 'vertical',
            bottom: 'bottom',
            data: this.pieData
          },
          series: [
            {
              name: "选项内容",
              type: "pie",
              radius: "55%",
              center: ["50%", "50%"],//位置
              data: this.pieData,
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: "rgba(0, 0, 0, 0.5)",
                },
              },
            },
          ],
        }
        return polar;
      },
    },
    data: function () {
      return {
        pieData: [],
        polar2:{
          title: {
            text: '个人数据统计图'
          },
          toolbox: {
            show: true,
            feature: {
              dataZoom: {
                yAxisIndex: 'none'
              },
              dataView: {
                readOnly: false
              },
              magicType: {
                type: ['line', 'bar']
              },
              restore: {},
              saveAsImage: {}
            }
          },
          tooltip: {},
          legend: {
            data: ['上传论文数量']
          },
          xAxis: {},
          yAxis: {},
          series: [{
            name: '上传论文数量',
            type: 'bar',
            data: []
          }],
          animationDuration: 3000
        }
      }
    }
  }
</script>
