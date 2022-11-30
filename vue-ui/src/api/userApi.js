import request from "@/api/request";


const userApi = {
    getUserByPrefix(prefix) {
        return request({
            url: `api/user`,
            method: 'get',
            params: {
                prefix,
            }
        });
    }

};

export default userApi;