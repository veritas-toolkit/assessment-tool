import axios from 'axios';
import qs from 'qs';

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
service.interceptors.response.use(response => {
    // let res = respone.data; // 如果返回的结果是data.data的，嫌麻烦可以用这个，return res
    return response;
}, error => {
    return Promise.reject(error);
})

export default service;