<template>
  <div>
    <div v-show="has_manage_members_permission">
      <div style="margin: 16px 0;">
        <div class="scope_title">Invite members</div>
      </div>
      <MemberInvite v-if="account!==null" @invite="invite" :account="account"> </MemberInvite>
      <div class="dividingLine"></div>
    </div>
    <div class="scope_title">Members</div>
    <div v-if="projectMemberList !== null" style="margin-top: 8px">
      <Member v-for="member in projectMemberList"
              :member="member" :can-change-role="canChangeRole(member)" :can-delete="canDeleteMember(member)"
              @changeRole="changeRole" @deleteMember="deleteMember">
      </Member>
    </div>
  </div>
</template>

<script>
import projectApi from "@/api/projectApi";
import Member from "@/components/common/Member";
import MemberInvite from "@/components/common/MemberInvite";
import accountApi from "@/api/accountApi";

export default {
  name: "ProjectMember",
  components: {
    MemberInvite,
    Member
  },
  props: {
    'project': {type: Object, required: true},
    'has_manage_members_permission': {type: Boolean, required: false, default: false},
  },
  data() {
    return {
      projectMemberList: null,
      selectedList: [],
      account: {},
    }
  },
  computed: {
    // has manage members permission
    projectId() {
      return this.project.id;
    }

  },

  created() {
    accountApi.whoami().then(res => this.account = res.data)
  },

  mounted() {
    this.fetchMemberList();
  },
  methods: {

    changeRole(userId, roleType) {
      projectApi.changeMemberRole(this.projectId, userId, roleType)
    },

    deleteMember(userId) {
      projectApi.deleteMember(this.projectId, userId);
      this.projectMemberList.splice(this.projectMemberList.findIndex(item => item.userId === userId), 1)
    },

    fetchMemberList() {
      projectApi.getMemberList(this.projectId).then(res => {
        this.projectMemberList = res.data
      });
    },

    invite(memberList) {
      projectApi.addMemberList(this.projectId, memberList).then(res => {
        let invited = res.data;
        invited.forEach(e => {
          let index = this.projectMemberList.findIndex(m => m.userId === e.userId);
          if (index < 0) {
            this.projectMemberList.push(e)
          } else {
            this.$set(this.projectMemberList, index, e);
          }
        });
      });
    },

    canChangeRole(member) {
      if (!this.has_manage_members_permission) {
        return false;
      }
      // 不能修改自己的角色
      return true;
    },
    canDeleteMember(member) {
      if (!this.has_manage_members_permission) {
        return false;
      }
      // 除了creator ，都可以修改删除自己
      return true;
    }


  },
  watch: {
    'project': function (newProject, oldVal) {
      this.fetchMemberList();
    }
  }

}
</script>

<style lang="less" scoped>

.scope_title {
  font-size: 18px;
  font-weight: bold;
  margin-top: 24px;
}


</style>