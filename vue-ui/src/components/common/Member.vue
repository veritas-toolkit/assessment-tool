<template>
  <div class="bodyRow" type="flex">
    <div style="display: flex;align-items: center">
      <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
      <div class="memberInfo">
        <div>{{ member.fullName }}</div>
        <span>{{ member.email }}</span>
      </div>
    </div>
    <div class="action_box">
      <div>
        <el-select :disabled="!canChangeRole" v-model="currentRole"
                   @change="changeRole">
          <el-option v-for="role in roleList" :key="role.type" :label="role.label" :value="role.type"></el-option>
        </el-select>
      </div>
      <el-button :disabled="!canDelete" class="deleteMember"
                 @click="deleteMember()"
                 icon="el-icon-delete-solid">
      </el-button>
    </div>
  </div>
</template>

<script>
import constants from "@/util/constants";
export default {
  name: "Member",
  props: {
    "member": {type: Object, required: true},
    "canChangeRole": {type: Boolean, required: false, default: false},
    "canDelete": {type: Boolean, required: false, default: false}
  },
  data() {
    return {
      currentRole: null,
      roleList: constants.MEMBER_TYPE_LIST,
    };
  },
  created() {
    this.currentRole = this.roleList.find(e => e.type === this.member.type)?.label
  },
  methods: {
    changeRole() {
      console.log('change role. new role: ', this.currentRole)
      this.$emit('changeRole', this.member.userId, this.currentRole)
    },
    deleteMember() {
      console.log('delete member. member user id: ', this.member.userId);
      this.$emit('deleteMember', this.member.userId)
    }
  }
}
</script>

<style lang="less" scoped>
.memberInfo {
  margin-left: 16px;
  height: 48px;

  > div {
    font-size: 16px;
    font-weight: 600;
    line-height: 24px;
    padding-bottom: 2px;
  }

  > span {
    font-size: 14px;
    font-weight: 400;
    color: #B8B8B8;
  }
}

.bodyRow {
  height: 80px;
  border-bottom: 1px solid #E5E7EB;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar {
  width: 40px;
  height: 40px;
}

.action_box {
  display: flex;
  align-items: center
}

</style>