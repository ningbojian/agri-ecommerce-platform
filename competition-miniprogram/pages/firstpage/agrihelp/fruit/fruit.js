
Page({

  data: {
    scrollH: 0,
    imgWidth: 0,
    loadingCount: 0,
    images: [],
    col1: [],
    col2: [],
    
  },

  onLoad: function () {
    wx.getSystemInfo({
      success: (res) => {
        let ww = res.windowWidth;
        let wh = res.windowHeight;
        let imgWidth = ww * 0.48;
        let scrollH = wh;

        this.setData({
          scrollH: scrollH,
          imgWidth: imgWidth
        });

        //加载首组图片
        this.loadImages();
      }
    })
  },

  onImageLoad: function (e) {
    let imageId = e.currentTarget.id;
    let oImgW = e.detail.width;         //图片原始宽度
    let oImgH = e.detail.height;        //图片原始高度
    let imgWidth = this.data.imgWidth;  //图片设置的宽度
    let scale = imgWidth / oImgW;        //比例计算
    let imgHeight = oImgH * scale;      //自适应高度

    let images = this.data.images;
    let imageObj = null;

    for (let i = 0; i < images.length; i++) {
      let img = images[i];
      if (img.id === imageId) {
        imageObj = img;
        break;
      }
    }

    imageObj.height = imgHeight;

    let loadingCount = this.data.loadingCount - 1;
    let col1 = this.data.col1;
    let col2 = this.data.col2;

    //判断当前图片添加到左列还是右列
    if (col1.length <= col2.length) {
      col1.push(imageObj);
    } else {
      col2.push(imageObj);
    }

    let data = {
      loadingCount: loadingCount,
      col1: col1,
      col2: col2
    };

    //当前这组图片已加载完毕，则清空图片临时加载区域的内容
    if (!loadingCount) {
      data.images = [];
    }

    this.setData(data);
  },

  loadImages: function () {
    let images = [
      {
        goodId: 0,
        name: '【爱心助农】正宗广西德庆皇帝柑新鲜特级大果10/5斤沃柑贡柑水果',
        url: '../fruit/0',
        imageurl: '/icon/6.jpg',
        price: "39.80-89.80",
        discount: "8.8",
        height: 0, 
      },
      {
        goodId: 1,
        name: '【爱心助农】广东湛江徐闻县香水小菠萝10斤新鲜当季水果凤梨包邮',
        url: '../fruit/1',
        imageurl: '/icon/7.jpg',
        price: "29.80-99.80",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 2,
        name: '【爱心助农】云南天山雪莲果新鲜10斤当季红心水果红泥沙特级大果',
        url: '../fruit/2',
        imageurl: '/icon/8.jpg',
        price: "39.00",
        discount: "8.8",
        height: 0, 
      },
      {
        goodId: 3,
        name: '【爱心助农】正宗陕西周至县富饶翠香猕猴桃新鲜应季奇异果水果',
        url: '../fruit/3',
        imageurl: '/icon/9.jpg',
        price: " 68.80-98.80",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 4,
        name: '【爱心助农 红心猕猴桃】善品公社 四川雅安新鲜水果 新鲜上市 "',
        url: '../fruit/4',
        imageurl: '/icon/10.jpg',
        price: "40.40-61.80",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 5,
        name: '【赣南脐橙直播~爱心助农】高档送礼盒装鲜甜应季水果10斤20包邮',
        url: '../fruit/5',
        imageurl: '/icon/11.jpg',
        price: "49.90 - 108.80",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 6,
        name: '【爱心助农】新疆阿克苏冰糖心苹果10斤野生丑苹果冰糖心水果整箱',
        url: '../fruit/6',
        imageurl: '/icon/12.jpg',
        price: "68.00-118.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 7,
        name: '	山东爱心助农莱阳丰水梨新鲜水果冰糖甜梨子净重5斤不甜包退',
        url: '../fruit/7',
        imageurl: '/icon/13.jpg',
        price: "39.90",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 8,
        name: '优农生活爱心助农丹东九九99牛奶草莓特产辽宁包邮新鲜采摘水果大',
        url: '../fruit/8',
        imageurl: '/icon/14.jpg',
        price: "59.00 - 158.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 9,
        name: '甘肃灵台苹果脆甜红富士水果果园直供新鲜采摘甘肃帮扶',
        url: '../fruit/9',
        imageurl: '/icon/15.jpg',
        price: "52.90-89.90",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 10,
        name: '【10根】内蒙现摘新鲜黄白黑糯粘玉米棒真空包装零食东北速食黏玉米',
        url: '../fruit/10',
        imageurl: '/icon/16.jpg',
        price: " 88.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 11,
        name: '【央广助农】云南瑞丽黄河小蜜瓜 新鲜当季水果金整箱新疆哈密瓜',
        url: '../fruit/11',
        imageurl: '/icon/17.jpg',
        price: " 39.90-59.90",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 12,
        name: '【央广助农】广西百香果 5斤大果包邮热带水果应季现摘新鲜果特',
        url: '../fruit/12',
        imageurl: '/icon/18.jpg',
        price: " 39.90-99.90",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 13,
        name: '重庆巫溪马尔科小土豆洋芋迷你黄心新鲜农家自种蔬菜高山非转基因',
        url: '../fruit/13',
        imageurl: '/icon/19.jpg',
        price: "18.88",
        discount: "8.8",
        height: 0,
      },
      
    ];

    let baseId = "img-" + (+new Date());

    for (let i = 0; i < images.length; i++) {
      images[i].id = baseId + "-" + i;
    }

    this.setData({  
      loadingCount: images.length,
      images: images
    });
  },
  click:function(e){
    var $id = e.currentTarget.dataset.goodId;
    console.log($id)
    if ("0"== $id){
      wx.navigateTo({
        url: 'https://detail.tmall.com/item.htm?spm=a1z0d.6639537.1997196601.45.61bd7484MIgvOR&id=632819521536',
      })
    }
    if ("1" == $id) {
      wx.navigateTo({
        url: 'https://detail.tmall.com/item.htm?spm=a1z0d.6639537.1997196601.102.2e577484QXlomb&id=574205693564&skuId=4767146570898',
      })
    }
    if ("2" == $id) {
      wx.navigateTo({
        url: 'https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.1dab3dbc55aSOe&id=634154030793&areaId=310100&user_id=2207972801931&cat_id=2&is_b=1&rn=340c46416d0431e4a2f5aa0741436471',
      })
    }
    if ("3" == $id) {
      wx.navigateTo({
        url: 'https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.3d7e6d2azV1g9S&id=634477695882&areaId=310100&user_id=2207972801931&cat_id=2&is_b=1&rn=a7dccb71338a479d478927c8143d7ef3',
      })
    }
    if ("4" == $id) {
      wx.navigateTo({
        url: 'https://detail.tmall.com/item.htm?spm=a1z0d.6639537.1997196601.63.6bed7484BTzgry&id=631706878550y',
      })
    }
   
    
  },


})

