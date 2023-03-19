import request from "@/api/request";


const systemApi = {


    getBusinessScenarioList() {
        return request({
            url: `/api/system/business_scenario`,
            method: 'get',
        });
    },

};

export default systemApi;