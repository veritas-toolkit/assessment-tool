import Vue from 'vue'
import VueCookies from 'vue-cookies'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import './plugins/element.js'
import '../src/assets/css/global.css'
import Print from './plugins/print.js'
import { Message } from 'element-ui';
import { format } from 'timeago.js';

Vue.use(Print) // 注册
Vue.use(VueCookies)
Vue.config.productionTip = false
Vue.prototype.$message = Message;

Vue.prototype.resetSetItem = function (key, newVal) {
  if (key === 'projectId') {
    // 创建一个StorageEvent事件
    let newStorageEvent = document.createEvent('StorageEvent');
    const storage = {
      setItem: function (k, val) {
        sessionStorage.setItem(k, val);
        // 初始化创建的事件
        newStorageEvent.initStorageEvent('setItem', false, false, k, null, val, null, null);
        // 派发对象
        window.dispatchEvent(newStorageEvent)
      }
    }
    return storage.setItem(key, newVal);
  }
}


axios.interceptors.response.use(
  function(response) {
    return response
  },
  function(err) {
    if (err.response.status == '403') {

      window.location.href = '/'
    } else {
      if (err.response.config.responseType != "blob") {
        Vue.prototype.$message.error(err.response.data.message)
      }
      return Promise.reject(err)
    }
  }
)

/*全局挂载*/
Vue.prototype.$http = axios
//定义全局的过滤器 "changeTime":改变时间为几周前
Vue.filter('changeTime', function(dateStr) {
  return format(dateStr);
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
