import axios from 'axios';
import Vue from "vue";
import router from "@/router";


const service = axios.create({
    // baseURL: 'http://127.0.0.1/api/', // api的base_url
    // withCredentials: true, // 跨域请求时是否发送cookies
    timeout: 30 * 1000
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
    function (err) {
        console.error("error")
        console.error(err)
        console.error(err.code)
        console.error(err.message)
        console.error(err.stack)
        let httpStatus = null;
        if (err.response) {
            httpStatus = err.response.status;
        }
        if (httpStatus === 401) {
            const notLoginPage = router.currentRoute.path !== "/login";
            if (notLoginPage) {
                return router.replace({
                    path: "/login",
                    query: {redirect: router.currentRoute.fullPath}
                })
            }
        } else if (httpStatus === 403) {
            // todo show message, only once
            return router.replace({
                path: "/home",
            })
        } else if (500 <= httpStatus && httpStatus <= 599) {
            let message = err.response.data.message;
            if (!message) {
                message = "Server error.";
            }
            Vue.prototype.$message.error(message);
        } else if (httpStatus){
            if (err.request.config.responseType !== "blob") {
                Vue.prototype.$message.error(err.response.data.message)
            }
        }
        return Promise.reject(err)
    }
)

export default service;