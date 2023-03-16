import axios from 'axios';
import Vue from "vue";
import router from "@/router";


const service = axios.create({
    // baseURL: 'http://127.0.0.1/api/', // api的base_url
    // withCredentials: true, // 跨域请求时是否发送cookies
    timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(config => {
    if (!config.data) {
        config.data = {};
    }
    return config;
}, error => {
    // 处理请求错误
    return Promise.reject(error);
})

// 响应拦截器
service.interceptors.response.use(
    function(response) {
        return response
    },
    function(err) {
        console.log(err.response)
        if (err.response.status === 401) {
            if (router.currentRoute.path !== "/login") {
                router.replace({
                    path:"/login",
                    query: {redirect: router.currentRoute.fullPath}
                })
            } else {
                Vue.prototype.$message.error(err.response.data.message)
            }
        } else {
            if (err.response.config.responseType !== "blob") {
                Vue.prototype.$message.error(err.response.data.message)
            }
        }
        return Promise.reject(err)
    }
)

export default service;