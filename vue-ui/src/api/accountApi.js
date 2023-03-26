import request from "@/api/request";



const accountApi = {
    whoami() {
        return request({
            url: `api/account`,
            method: 'get'
        });
    },
}

export default accountApi;