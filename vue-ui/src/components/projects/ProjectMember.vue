<template>
  <div>
    <div v-show="has_manage_members_permission">
      <div style="margin: 16px 0;">
        <div class="scope_title">Invite members</div>
      </div>
      <MemberInvite @invite="invite"> </MemberInvite>
      <div class="dividingLine"></div>
    </div>
    <div class="scope_title">Members</div>
    <div style="margin-top: 8px">
      <Member v-for="member in projectMemberList"
              :member="member" :can-change-role="true" :can-delete="true"
              @changeRole="changeRole" @deleteMember="deleteMember">
      </Member>
    </div>
  </div>
</template>

<script>
import projectApi from "@/api/projectApi";
import Member from "@/components/common/Member";
import MemberInvite from "@/components/common/MemberInvite";

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
      projectMemberList: [],
      selectedList: [],
    }
  },
  computed: {
    // has manage members permission
    projectId() {
      return this.project.id;
    }

  },

  created() {
    // this.fetchMemberList();
  },
  methods: {

    addUserToSelectedList(userItem) {

    },

    addMemberList(){

    },
    changeRole(userId, roleType) {
      projectApi.changeMemberRole(this.project.id, userId, roleType)
    },

    deleteMember(userId) {
      projectApi.deleteMember(this.project.id, userId);
      this.projectMemberList.splice(this.projectMemberList.findIndex(item => item.userId === userId), 1)
    },

    fetchMemberList() {
      projectApi.getMemberList(this.project.id).then(res => {
        this.projectMemberList = res.data
      });
    },

    invite(memberList) {
      console.log('to be invited member list', memberList.toString());
      projectApi.addMemberList(this.projectId, memberList);
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