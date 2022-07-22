<template>
  <div style="padding: 32px 64px !important;">
    <div class="title BarlowBold">
      <router-link :to="userId? { path: '/adminUserPage',query: {userId: this.userId,activeName: 'third'}} :{ path: '/administration',query: {activeName: 'second'}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
      <span v-show="!userId">Group</span>
      <span v-show="userId">User</span>
    </div>
    <!--body part-->
    <div style="margin-left: 40px">
      <div class="title-box BarlowBold">
        <span class="oneLine">{{groupInfo.name}}</span>
        <div style="display: flex;align-items: center">
          <!--<div class="editDiv">Edit</div>-->
          <div class="deleteDiv oneLine" @click="deleteGroupInfo">Delete</div>
        </div>
      </div>
      <div class="projText BarlowBold">Group info</div>
      <!--proj info part-->
      <div class="projInfo BarlowMedium">
        <div style="width: 30%">
          <div style="margin-bottom: 4px">Time created</div>
          <el-input style="margin-bottom: 16px" disabled v-model="createdTime"></el-input>
          <div style="margin-bottom: 4px">Time modified</div>
          <el-input style="margin-bottom: 16px" disabled v-model="lastModifiedTime"></el-input>
        </div>
        <div style="width: 65%">
          <div style="margin-bottom: 4px">Group name</div>
          <el-input style="margin-bottom: 16px" v-model="saveGroupInfoName"></el-input>
          <div style="margin-bottom: 4px">Group description</div>
          <el-input type="textarea" :rows="5" v-model="saveGroupInfoDescription"></el-input>
        </div>
      </div>
      <div style="display: flex;justify-content: flex-end">
        <div class="editDiv BarlowMedium" @click="doSaveGroupInfo" style="margin-top: 8px">Save</div>
      </div>
      <div class="divide_line"></div>
      <!--invite user & members-->
      <div class="BarlowMedium">
        <div>
          <div class="invite-member BarlowBold">Invite user</div>
          <el-tag size="mini" effect="plain" style="margin-bottom: 10px" v-for="tag in tags" @close="handleClose(tag)" :key="tag.id" closable type="info">{{ tag.fullName }}</el-tag>
          <div style="display: flex;align-items: center;justify-content: space-between">
            <el-select clearable @change="addUserToTags" value-key="id" v-model="selectUserObj" placeholder="Choose a user">
              <el-option class="selectStyle" v-for="item in userList" :key="item.id" :label="item.fullName" :value="item"></el-option>
            </el-select>
            <div style="display: flex;align-items: center;justify-content: space-between;margin-left: 12px;width: 400px">
              <el-select clearable v-model="userType" placeholder="Choose a role permission">
                <el-option class="selectStyle" v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
              </el-select>
              <div class="addUsers" @click="addUsersToGroup">Invite</div>
            </div>
          </div>

        </div>
        <div class="divide_line"></div>
        <div>
          <div class="invite-member BarlowBold">Members</div>
          <!--groupMember-->
          <div style="margin-top: 8px">
            <div class="bodyRow" type="flex" v-for="(item,index) in groupMember" :key="item.userId">
              <div style="display: flex;align-items: center">
                <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
                <div class="nameDesc">
                  <div>{{item.fullName}}</div>
                  <span>{{item.email}}</span>
                </div>
              </div>
              <div style="display: flex;align-items: center">
                <div>
                  <el-select  v-model="item.type" @change="changeMemberRole(item)">
                    <el-option v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
                  </el-select>
                </div>
                <el-button class="deleteMember" @click="deleteMember(item.userId)" icon="el-icon-delete-solid"></el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "AdminGroupPage",
    data() {
      return {
        groupId: this.$route.query.groupId,
        userId: this.$route.query.userId,
        projectInfo: '',
        groupInfo: '',
        createdTime: '',
        lastModifiedTime: '',
        saveGroupInfoName: '',
        saveGroupInfoDescription: '',
        saveGroupInfo: {},
        userList: [],
        tags: [],
        userType: '',
        selectUserObj: {},
        groupMember: [],
        memberTypeList: [
          {
            type: 'OWNER',
            label: 'Owner'
          }, {
            type: 'DEVELOPER',
            label: 'Developer'
          },
          {
            type: 'ASSESSOR',
            label: 'Assessor'
          },],
        addUserId: [],
      }
    },
    created() {
      this.getGroupInfo()
      this.getUserList()
      this.getGroupMember()
    },
    methods: {
      addUsersToGroup() {
        let addUserList = []
        this.addUserId.map(item => {
          let addUser = {}
          addUser.userId = item
          addUser.type = this.userType
          addUserList.push(addUser)
        })
        this.$http.put(`/api/admin/group/${this.groupId}/member`,addUserList).then(res => {
          if (res.status == 200) {
            this.$message.success('Invite successfully')
            this.selectUserObj = {}
            this.userType = ''
            this.tags = []
            this.addUserId = []
            this.getGroupMember()
          }
        })
      },
      doSaveGroupInfo() {
        this.saveGroupInfo.name = this.saveGroupInfoName
        this.saveGroupInfo.description = this.saveGroupInfoDescription
        this.$http.post(`/api/admin/group/${this.groupId}`,this.saveGroupInfo).then(res => {
          if (res.status == 200) {
            this.$message.success('Save successfully')
            this.getGroupInfo()
          }
        })
      },
      getGroupMember() {
        this.$http.get(`/api/admin/group/${this.groupId}/member`).then(res => {
          if (res.status == 200) {
            this.groupMember = res.data
          }
        })
      },
      deleteMember(userId) {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/admin/group/${this.groupId}/member/${userId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.getGroupMember()
            }
          })
        })
      },
      changeMemberRole(user) {
        let changeRole = {}
        changeRole.userId = user.userId
        changeRole.type = user.type
        this.$http.post(`/api/admin/group/${this.groupId}/member`,changeRole).then(res => {
          if (res.status== 200){
            this.$message.success('Change successfully')
          }
        })
      },
      deleteGroupInfo() {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/admin/group/${this.groupId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.$router.push({path:'/administration',query: {activeName: 'second'}})
            }
          }).catch(err => {
            if (err.response.status == 400) {
              this.$confirm('Force delete?',{type: 'error'}).then(() => {
                this.$http.delete(`/api/admin/group/${this.groupId}`,{params:{force:true}}).then(res => {
                  if (res.status == 200) {
                    this.$message.success('Delete successfully')
                    this.$router.push({path:'/administration',query: {activeName: 'second'}})
                  }
                })
              })
            }
          })
        })
      },
      addUserToTags(value) {
        if (this.tags.indexOf(value) == -1 && value != '') {
          this.tags.push(value)
          this.addUserId.push(value.id)
        }
      },
      handleClose(tag) {
        this.tags.splice(this.tags.indexOf(tag), 1);
        this.addUserId.splice(this.addUserId.indexOf(tag.id), 1);
      },
      getUserList() {
        this.$http.get('/api/admin/user',{params: {'prefix': this.prefix}}).then(res => {
          if (res.status == 200) {
            this.userList = res.data.records
          }
        })
      },
      getGroupInfo() {
        this.$http.get(`/api/admin/group/${this.groupId}`).then(res => {
          this.groupInfo = res.data
          this.saveGroupInfoName = res.data.name
          this.saveGroupInfoDescription = res.data.description
          this.createdTime = this.dateFormat(res.data.createdTime)
          this.lastModifiedTime = this.dateFormat(res.data.lastModifiedTime)
        })
      },
      dateFormat(date) {
        let time = new Date(date)
        let yyyy = time.getFullYear()
        let mm = time.getMonth()+1
        let dd = time.getDate()
        let HH = time.getHours()
        let MM = time.getMinutes()
        let SS = time.getSeconds()
        return yyyy+'-'+mm+'-'+dd+','+' '+HH+':'+MM+':'+SS
      },
    }
  }
</script>

<style lang="less" scoped>
  .title {
    display: flex;
    height: 40px;
    align-items: center;
    > a {
      > img {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
      }
    }
    > span {
      margin-left: 8px;
      font-size: 20px;
      font-weight: bold;
      line-height: 40px;
      color: #000;
    }
  }
  .title-box {
    height: 40px;
    margin-top: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    > span {
      font-size: 24px;
      font-weight: bold;
    }
  }
  .editDiv {
    cursor: pointer;
    border: 1px solid;
    border-radius: 4px;
    font-size: 16px;
    padding: 8px 12px;
  }
  .deleteDiv {
    cursor: pointer;
    margin-left: 12px;
    border: 1px solid #E02020;
    border-radius: 4px;
    font-size: 16px;
    color: #E02020;
    padding: 8px 12px;
  }
  .projText {
    font-size: 20px;
    margin-top: 18px;
  }
  .projInfo {
    display: flex;
    justify-content: space-between;
    margin-top: 16px;
  }
  .divide_line {
    margin: 24px 0px;
    height: 1px;
    background-color: #CED3D9;
  }
  .divide_line_1 {
    height: 1px;
    background-color: #CED3D9;
  }
  .invite-member {
    margin-bottom: 16px;
    font-size: 20px;
  }
  .selectStyle {
    width: 100%;
  }
  .el-tag--plain.el-tag--info {
    background-color: #fff;
    border-color: #d3d4d6;
    color: #909399;
    margin-right: 10px;
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
  .nameDesc {
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
  .deleteMember {
    position: relative;
    width: 38px;
    height: 38px;
    margin-left: 16px;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    padding: 0 !important;
    background-color: #F2F5F8;
  }
  .el-button.is-disabled, .el-button.is-disabled:focus, .el-button.is-disabled:hover {
    background-color: #f2f2f2 !important;
  }
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