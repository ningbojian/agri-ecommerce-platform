
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
        name: '云南 | 滇藏之路，雪山深处，遇见香格里拉',
        url: '/pages/firstpage/tourism/xgll/xgll',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcZT2EnMrf3nTjOxLMvgFbPM19raV9QgvqnACp*91joTQaY74UjsEUuOxSLLpJeT6hesZ7MyHo.6byv7PhN1yQTQ!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4',
        price: "69.00-158.00",
        discount: "8.8",
        height: 0, 
      },
      {
        goodId: 1,
        name: '广西巴马 | 世界长寿之乡·中国人瑞圣地',
        url: '/pages/firstpage/tourism/bama/bama',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcZT2EnMrf3nTjOxLMvgFbPNVx173WRzv.r63I6wL7D47L83Alx8mDbvKvOXnJsAFV9.LyYxNOMoZObrKUgugPqA!/b&bo=QAYjBAAAAAABF1E!&rf=viewer_4',
        price: "39.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 2,
        name: '湖南 | 重回沈从文笔下的边城，湘西凤凰古城',
        url: '/pages/firstpage/tourism/fenghuang/fenghuang',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcWYeCVpxRbgtR5VoxBoI7gBnbgKAUfpIQ9vzvG.5I5R0kxU5y6DNoc6JnpzKE.v9eJpQwFl1PLQzMRKqZxIH9W0!/b&bo=QAYqBAAAAAABN3g!&rf=viewer_4',
        price: "16.50",
        discount: "8.8",
        height: 0, 
      },
      {
        goodId: 3,
        name: '安徽金寨县 | 薪火相传，红色金寨，挺进大别山',
        url: '/pages/firstpage/tourism/jinzhai/jinzhai',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcWYeCVpxRbgtR5VoxBoI7gCrW.DN8*QF0JAuyxVULDIzb6n2PzSsRIuL9kZp87S1i7*kiCEiGJNfo*OVXCR3tC0!/b&bo=AASqAgAAAAABJ6w!&rf=viewer_4',
        price: "19.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 4,
        name: '安徽寿县 | 中华楚文化的发源，带你神游千年',
        url: '/pages/firstpage/tourism/shouxian/shouxian',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcWYeCVpxRbgtR5VoxBoI7gD2H7aT6N37PJWVaK88*ZrcAqHxlu9*aRLNQuRood8jOvkbFlWd00g0SxJWb9ZqFr8!/b&bo=OAQqAwAAAAABJxU!&rf=viewer_4',
        price: "76.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 5,
        name: '甘肃临夏 | 塞上江南，彩陶之乡，花儿临夏',
        url: '/pages/firstpage/tourism/linxia/linxia',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcWYeCVpxRbgtR5VoxBoI7gADj6dPiKKt3ZyqJB3yqs06a1dqdYX89QwY6bjmk3RQo7CwaFl7egl2O2oZ2TU8GHw!/b&bo=VAY4BAAAAAABN34!&rf=viewer_4',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 6,
        name: '贵州平塘 | “中国天眼”基地，自由瞭望天空',
        url: '/pages/firstpage/tourism/pingtang/pingtang',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcRL9Qqv44VOEzHbXuU.OO82U8WN3TjClNxYsYML9K*BGYWpF0Qr0cxL4mU2ssUFH32Xwfpl6lWqq0YiSsXDO64U!/b&bo=oQU4BAAAAAABN4g!&rf=viewer_4',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 7,
        name: '贵州荔波 | 民族风情，黔南荔波',
        url: '/pages/firstpage/tourism/libo/libo',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcRL9Qqv44VOEzHbXuU.OO80ZjtxNbmNeurP51z6yj4kKLWiM6IGf1t5GX.EgV3cPlq1epKdEcTTt*y4g.EQvie8!/b&bo=OATQAgAAAAABJ.4!&rf=viewer_4',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 8,
        name: '海南小众旅游，美丽保亭',
        url: '/pages/firstpage/tourism/baoting/baoting',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcRL9Qqv44VOEzHbXuU.OO80T81Bcv2B.38C27Rin33sX2brFek433x*haQ6N3v1IrcC1S2L.4lwX5fLz9GY4gnM!/b&bo=QAYsBAAAAAABN34!&rf=viewer_4',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 9,
        name: '河北承德 | 水草丰茂，木兰围场，避暑胜地',
        url: '/pages/firstpage/tourism/chengde/chengde',
        imageurl: 'http://m.qpic.cn/psc?/V54DWESa0sRAoU0aTodf0ZH2Ht0OfYHO/45NBuzDIW489QBoVep5mcRL9Qqv44VOEzHbXuU.OO81iTTzqs*xYdZSTm27SIA8M6NnteZY2fzqD8m7q4g73RH.Q4uB7WrmCgS5wsFPmsfM!/b&bo=QAYsBAAAAAABJ24!&rf=viewer_4',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 10,
        name: '河北尚义 | 尚义大青山，草原天路的最西端',
        url: '/pages/firstpage/tourism/shangyi/shangyi',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcQkxR19jIxcM86CP6fjtr6tizkYHU1S6YDWAud566lg8zYt.5PTJog8gxY*LSVNN5ztHS32YIf5znZnU8J3ewDw!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 11,
        name: '畅游延边安图 —— 长白山下第一县',
        url: '/pages/firstpage/tourism/antu/antu',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcXFshS3a1rpWHvkCV3s4Rkt2UUA2HT7NIdeMd9aeTBXGU0q7ZlyVtYXrkaFVfHg9n8*6povyqZ6dT.ykwI*GU8w!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 12,
        name: '神农架林区 | 悠悠山水间，寻迹神农，问道武当',
        url: '/pages/firstpage/tourism/shennong/shennong',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcU73ZuJkKbsz6boQuQ7iZAN21Ugxn5*IJUjoAmguQkoLhewirNIQoilsBBQr*bpu4*TePG*UR096RFZMV67frvw!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 13,
        name: '湖北秭归：从屈原故里到三峡大坝',
        url: '/pages/firstpage/tourism/zigui/zigui',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcU73ZuJkKbsz6boQuQ7iZAPpye5OC6rQSD2ChmTWg2nQjLqmtRVo0598w.jrPuV2WRvOhwipjAVl85*tTbrCAKg!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 14,
        name: '江西上饶 | 婺源篁岭赏水墨画卷，品徽州古韵',
        url: '/pages/firstpage/tourism/shangrao/shangrao',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcU73ZuJkKbsz6boQuQ7iZAPrmlwLzq9kBw4xiyTHTrrAeoc*TTHBth*h8vlZBGN62pzZqlkabn8V3qGVnLyGmyY!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 15,
        name: '内蒙 | 亲历浩渺大漠，阿鲁科尔沁旗',
        url: '/pages/firstpage/tourism/alukeerqin/alukeerqin',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcU73ZuJkKbsz6boQuQ7iZAMOENb1PfElZSppG*s6O9xhZbCpfAEt.JBRlYkrL5oQIiRIqC97ruBc0YmvlyR1ziQ!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 16,
        name: '四子王旗 | 来杜尔伯特草原，风吹草低见牛羊',
        url: '/pages/firstpage/tourism/siziwang/siziwang',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcQRI4FtT5p53Bkd2QaCwVvMuggmPolF*Q3OV.oaASHwkXs1*uHQlLZmjleNwg5mxy0PAk*amTD8OwzQ391nqPzQ!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 17,
        name: '宁夏泾源 | 千古萧关两孤楼，六盘崆峒两山游',
        url: '/pages/firstpage/tourism/jingyuan/jingyuan',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcQRI4FtT5p53Bkd2QaCwVvOAo5s3BFwTGf7fbocAmH4xk6pX*8ilCWXGtz2.oy3UTEYlMfqXy0FDUo0qbyDfRWQ!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 18,
        name: '青海湟源 | “海藏咽喉”，游丹噶尔古城',
        url: '/pages/firstpage/tourism/huangyuan/huangyuan',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcQRI4FtT5p53Bkd2QaCwVvM1eqMNRP5T10zrXMk4Qkru0dq5r3cpLLHHM*Kn5RN3SsOa8y7AWIIUt65df9*FAc4!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      },
      {
        goodId: 19,
        name: '玉树 | 寻觅康巴秘境之地，可可西里，三江源头',
        url: '/pages/firstpage/tourism/yushu/yushu',
        imageurl: 'http://r.photo.store.qq.com/psc?/V52VDqLL0Wdqvt35jdqH3mXU1U2hur8z/45NBuzDIW489QBoVep5mcQRI4FtT5p53Bkd2QaCwVvMhqN1WPF5xYi7Yo2HAKt7HL9BrZ2LeQ*yyCTO9PGb87NW8Wwl5NkFZZmN71b1ULpI!/r',
        price: " 68.00",
        discount: "8.8",
        height: 0,
      }
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






