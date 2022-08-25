<template>
  <div>
    <el-tag size="mini" effect="plain" style="margin-bottom: 10px"
            v-for="selectedUser in selectedUserList"
            @close="removeFromSelected(selectedUser)"
            :key="selectedUser.id"
            closable
            type="info">
      {{ selectedUser.fullName }}
    </el-tag>
    <div style="display: flex;align-items: center;justify-content: space-between">
      <el-select filterable
                 @change="addToSelected"
                 value-key="id"
                 v-model="selectedUser"
                 placeholder="Choose a user">
        <el-option v-for="user in userList" :key="user.id" :label="user.fullName" :value="user"></el-option>
      </el-select>
      <div style="display: flex;justify-content: space-between;align-items: center;margin-left: 12px;width: 400px">
        <el-select v-model="userType" placeholder="Choose a role permission">
          <el-option v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
        </el-select>
        <div class="addUsers" @click="invite">Invite</div>
      </div>
    </div>
  </div>

</template>

<script>
import constants from "@/util/constants";
import userApi from "@/api/userApi";

export default {
  name: "MemberInvite",
  data() {
    return {
      userList: [],
      userType: '',
      selectedUserList: [],
      selectedUser: {},
      memberTypeList: constants.memberTypeList,
    }
  },
  created() {
    userApi.getUserByPrefix('').then(res => {
      this.userList = res.data
    });
  },
  methods: {
    removeFromSelected(user) {
      this.selectedUserList.splice(this.selectedUserList.indexOf(user), 1);
    },

    addToSelected(user) {
      if (this.selectedUserList.indexOf(user) === -1 && user !== '') {
        this.selectedUserList.push(user);
      }
    },

    invite() {
      if (this.selectedUserList.length <= 0) {
      //  todo throw exception or
      }
      let memberList = [];
      this.selectedUserList.forEach(user => {
        memberList.push({
          userId: user.id,
          type: this.userType,
        });
      });
      this.$emit('invite', memberList);
    },
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