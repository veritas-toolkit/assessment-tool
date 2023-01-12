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

Vue.use(Print) // 注册
Vue.use(VueCookies)
Vue.config.productionTip = false
Vue.prototype.$message = Message;

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

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
