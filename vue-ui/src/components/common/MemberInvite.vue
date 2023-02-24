<template>
  <div>
    <div style="display: flex;align-items: center;justify-content: space-between">
      <el-select value-key="id"
                 :disabled="archived"
                 multiple
                 filterable
                 v-model="selectedUserList"
                 placeholder="Choose a user">
        <el-option v-for="user in userList" :key="user.id" :label="user.fullName" :value="user"></el-option>
      </el-select>
      <div style="display: flex;justify-content: space-between;align-items: center;margin-left: 12px;width: 400px">
        <el-select :disabled="archived" v-model="userType" placeholder="Choose a role permission">
          <el-option v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
        </el-select>
        <div class="addUsers" :style="archived?'pointer-events: none;':''" @click="invite">Invite</div>
      </div>
    </div>
  </div>

</template>

<script>
import constants from "@/util/constants";
import userApi from "@/api/userApi";
// import account from "@/store/account";

export default {
  name: "MemberInvite",
  props: {
    account: {
      type: Object,
      required: true
    },
    'archived': {type: Boolean, required: false, default: false},
  },
  data() {
    return {
      userList: [],
      userType: '',
      selectedUserList: [],
      selectedUser: {},
      memberTypeList: constants.MEMBER_TYPE_LIST,
    }
  },
  created() {
    this.fetch_all_users();
  },
  methods: {
    invite() {
      if (this.selectedUserList.length <= 0) {
      //  todo throw exception or
      }
      if (this.userType === '') {
        //  todo throw exception
      }
      let memberList = [];
      this.selectedUserList.forEach(user => {
        memberList.push({
          userId: user.id,
          type: this.userType,
        });
      });
      this.$emit('invite', memberList);
      this.selectedUserList = [];
      this.userType = '';
    },

    fetch_all_users() {
      userApi.getUserByPrefix('').then(res => {
        // let list = res.data;
        // list.splice(list.findIndex(user => user.id === account.id), 1);
        // this.userList = list;
        console.log("account: ", this.account);
        this.userList = res.data.filter(user => {
          console.log("user id: ", user.id, ", account.id:", this.account.id);
          return user.id !== this.account.id
        });
      });
    }
  }
}
</script>

<style lang="less" scoped>

.addUsers {
  cursor: pointer;
  margin-left: 12px;
  text-align: center;
  line-height: 38px;
  width: 60px;
  height: 38px;
  border: 1px solid;
  border-radius: 4px;
}

</style>