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
    },
    whoami() {
        return request(
            {
                url: `api/account`,
                method: `get`,
            }
        )
    }

};

export default userApi;