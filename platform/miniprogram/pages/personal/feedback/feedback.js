Page({
  data: { content: '' },
  onInput(event) { this.setData({ content: event.detail.value }); },
  submit() {
    wx.showToast({ title: '反馈功能待接入后端', icon: 'none' });
  }
});
