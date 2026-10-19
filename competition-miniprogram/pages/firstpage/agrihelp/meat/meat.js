
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
        name: '贻贝鲜活 海虹青口贝日照特产海鲜淡菜野生净重5斤装',
        url: '../meat/0',
        imageurl: '/icon/26.jpg',
        price: "59.00-69.00",
        discount: "8.8",
        height: 0, 
      },
      {
        goodId: 1,
        name: '2.	羊蝎子500gX4袋塞罕坝坝上草原生态养殖河北帮扶',
        url: '../meat/1',
        imageurl: '/icon/27.jpg',
        price: "178.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 2,
        name: '塞罕坝生态羔羊羊小腿500g 河北帮扶',
        url: '../meat/2',
        imageurl: '/icon/28.jpg',
        price: "45",
        discount: "8.8",
        height: 0, 
      },
      {
        goodId: 3,
        name: '薇娅扶贫直播间正宗罗老板灵芝营养鸡蛋新鲜富硒农家土蛋30枚包邮',
        url: '../meat/3',
        imageurl: '/icon/29.jpg',
        price: "89.00",
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

