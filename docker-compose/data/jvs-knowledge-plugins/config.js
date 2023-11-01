let pluginConfig = {
  onlyOfficeUrl:'http://documentIpAddress:32467/web-apps/apps/api/documents/api.js',
  officeCallBackUrl:'http://documentIpAddress:10000', //'http://10.0.0.38:10000'
  uploadFileLimit:5, //文件上传数量限制
  // 协同连接地址
  otClientUrl:'ws://documentIpAddress:28080',
  // 是否开启协同
  openOTClient:true,
  // 自动保存的接口请求地址
  autoSaveUrl:'http://documentIpAddress:10000'
}