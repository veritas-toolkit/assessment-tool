<template>
  <div>
    <div v-show="has_manage_members_permission">
      <div style="margin: 16px 0;">
        <div class="scope_title">Invite members</div>
      </div>
      <MemberInvite v-if="account!==null" @invite="invite" :archived="archived" :account="account"> </MemberInvite>
      <div class="dividingLine"></div>
    </div>
    <div class="scope_title">Members</div>
    <div v-if="projectMemberList !== null" style="margin-top: 8px">
      <Member v-for="member in projectMemberList"
              :archived="archived"
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
    'archived': {type: Boolean, required: false, default: false},
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
      if (member['organizationType'] === "group") {
        return false;
      }
      if (!this.has_manage_members_permission) {
        return false;
      }
      if (member.userId === this.project['userOwnerId']) {
        return false;
      }
      if (this.lastOwner(member)) {
        return false;
      }
      return true;
    },
    canDeleteMember(member) {
      if (member['organizationType'] === "group") {
        return false;
      }
      if (!this.has_manage_members_permission) {
        return false;
      }
      if (member.userId === this.project['userOwnerId']) {
        return false;
      }
      if (this.lastOwner(member)) {
        return false;
      }
      return true;
    },
    lastOwner(member) {
      if (member.type === "OWNER") {
        let ownerCount = this.projectMemberList.filter(m => {return m.type === "OWNER"}).length;
        if (ownerCount <= 1) {
          return true;
        }
      }
      return false;
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