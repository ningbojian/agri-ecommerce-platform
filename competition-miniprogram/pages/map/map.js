import * as echarts from '../../pages/packageA/ec-canvas/echarts';
import geoJson from '../../pages/packageA/ec-canvas/mapData.js';

const app = getApp();
let myChart1 = null;


Page({

  /**
   * 页面的初始数据
   */
  data: {
    ec1: {
      lazyLoad: true
    },
    
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

    wx.setNavigationBarTitle({
      title: '中国地图概览'
    })
    this.requestData()
  },

  requestData() {
    var mapDataArr = [{
        name: "北京",
        value: 0,
        // selected: true
       
      },
      {
        name: "天津",
        value: 0,
      },
      {
        name: "上海",
        value: 0,
      },
      {
        name: "重庆",
        value: 14,
      },
      {
        name: "河北",
        value: 45,
      },
      {
        name: "河南",
        value: 38,
      },
      {
        name: "云南",
        value: 88,
        
      },
      {
        name: "辽宁",
        value: 0,
      },
      {
        name: "黑龙江",
        value: 20,
       
      },
      {
        name: "湖南",
        value: 40,
      },
      {
        name: "安徽",
        value: 20,
       
      },
      {
        name: "山东",
        value: 0,
      },
      {
        name: "新疆",
        value:32,
      },
      {
        name: "江苏",
        value: 0,
      },
      {
        name: "浙江",
        value: 0,
      },
      {
        name: "江西",
        value: 0,
      },
      {
        name: "湖北",
        value: 28,
      },
      {
        name: "广西",
        value: 33,
      },
      {
        name: "甘肃",
        value: 58,
      },
      {
        name: "山西",
        value: 36,
      },
      {
        name: "内蒙古",
        value: 31,
      },
      {
        name: "陕西",
        value: 56,
      },
      {
        name: "吉林",
        value: 8,
      },
      {
        name: "福建",
        value: 0,
      },
      {
        name: "贵州",
        value: 66,
      },
      {
        name: "广东",
        value: 0,
      },
      {
        name: "青海",
        value: 42,
      },
      {
        name: "西藏",
        value: 74,
      },
      {
        name: "四川",
        value: 66,
      },
      {
        name: "宁夏",
        value:8,
      },
      {
        name: "海南",
        value: 5,
      },
      {
        name: "台湾",
        value: 0,
      },
      {
        name: "香港",
        value: 0,
      },
      {
        name: "澳门",
        value: 0,
      },
      {
        name: '南海诸岛',
        value: 0,
      }
    ];

    this.init_echarts1(mapDataArr)
    // this.init_echarts2(chartData)
    
   

  },
 
  //中国地图
  init_echarts1: function (chartData) {
    this.chart1Componnet = this.selectComponent('#mychart1');
    this.chart1Componnet.init((canvas, width, height, dpr) => {
      myChart1 = echarts.init(canvas, null, {
        width: width,
        height: height,
        devicePixelRatio: dpr
      });
      echarts.registerMap('china', geoJson); // 绘制中国地图
      myChart1.setOption(this.getChart1Option(chartData));
      return myChart1;
    });
  },
  //横柱状图

  getChart1Option: function (chartData) {
    
    var option = {
      tooltip: {
        trigger: 'item',
        enterable:true,  //鼠标可以进入
        triggerOn: 'click',   //可可点击
        formatter: function (e) {
          // console.log(e)
          var name = e.name ? e.name : '获取中';
          var value = e.value ? e.value : '暂无数据';
          
          
        return `${name}:\n脱贫县数量(个):${value} `
     
          
        }
      },
      // 地理坐标系组件
      geo: [{
        type: "map", //图表类型
        mapType: 'china',
        roam: true, // 可以缩放和平移
        aspectScale: 10, // 比例
        layoutCenter: ["50%", "50%"], // position位置
        layoutSize: 1000, // 地图大小，保证了不超过 370x370 的区域
        label: {
          // 图形上的文本标签
          normal: {
            show: true,
            textStyle: {
              color: "rgba(0, 0, 0, 0.9)",
              fontSize: '15'
            }
          },
          emphasis: { // 高亮时样式
            color: "#333"
          }
        },
        itemStyle: {
          // 图形上的地图区域
          normal: {
            borderColor: "rgba(0,0,0,0.2)",
            areaColor: "#005dff"
          },
          emphasis: {
            areaColor: "#38BC9D", //鼠标选择区域颜色
            areaColor: 'red',
            shadowOffsetX: 0,
            shadowOffsetY: 0,
            shadowBlur: 20,
            borderWidth: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        }
      }],
      //图例
      visualMap: {
        type: "piecewise", //类型为分段型。
        splitNumber: 5, //对于连续型数据，自动平均切分成几段。默认为5段。 连续数据的范围需要 max 和 min 来指定。
        pieces: [
          {
            min: 80,
            max: 99,
            label: "80-99"
          },
          {
            min: 60,
            max: 79,
            label: "60-79"
          },
          {
            min: 40,
            max: 59,
            label: "40-59"
          },
          {
            min: 20,
            max: 39,
            label: "20-39"
          },
          {
            min: 0,
            max: 19,
            label: "0-19" 
          } 
        ],
         // color: "#FF0000",//里面可以加颜色
         // 不指定 max，表示 max 为无限大（Infinity）。 // 表示 value 等于 123 的情况。
          // {
          //   value: 0,
          //   label: "0"
          // } // 不指定 min，表示 min 为无限大（-Infinity）。
       
        textStyle: {
          fontSize: 12
        },
        realtime: false,
        calculable: false,
        inRange: {
          color: ['pink', 'yellow', 'orangered','red'],
        },
        orient: "horizontal",
        bottom: 10,
        left:40,
        right: 10,
        itemHeight: 10,
        itemWidth: 8,
      },

      //图例
      // visualMap: {
      //   min: 0,
      //   max: 10000,
      //   left: 'left',
      //   top: 'bottom',
      //   itemWidth: 19,
      //   itemHeight: 60,
      //   text: ['高', '低'], // 文本，默认为数值文本
      //   calculable: true,
      //   // inRange: {
      //   //   color: ['#0055A4', '#007ADE', '#0085EF', '#0D91F9', '#62ADFB']
      //   // },
      // },

      //工具条
      toolbox: {
        show: false,
        orient: 'vertical',
        left: 'right',
        top: 'center',
        feature: {
          dataView: {
            readOnly: false
          },
          restore: {},
          saveAsImage: {}
        }
      },
      series: [{
        name: '报名人数',
        type: 'map', //图表类型
        mapType: 'china',
        // selectedMode: 'multiple',
        label: {
          normal: {
            show: true,
            fontSize: 8,
          },
          emphasis: {
            show: true
          }
        },
        itemStyle: {
          normal: {
            borderColor: '#38BC9D',
            areaColor: '#fff',
          },
          emphasis: {
            areaColor: '#38BC9D',
            borderWidth: 0
          }
        },
        data: chartData
      }]
    };
    return option
  },

  

  
})