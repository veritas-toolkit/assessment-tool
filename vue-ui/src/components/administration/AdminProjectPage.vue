<template>
  <div style="padding: 32px 64px !important;">
    <div class="title BarlowBold">
      <router-link :to="userId?{ path: '/adminUserPage',query: {userId: this.userId,activeName: 'second'}} :{ path: '/administration',query: {activeName: 'first'}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
      <span v-show="!userId">Project</span>
      <span v-show="userId">User</span>
    </div>
    <!--body part-->
    <div style="margin-left: 40px">
      <div class="title-box BarlowBold">
        <span class="oneLine" style="display: flex;align-items: center">
          <span v-if="projectInfo.userOwner">{{projectInfo.userOwner.username}}</span>
          <span v-else-if="projectInfo.groupOwner">{{projectInfo.groupOwner.name}}</span>
          / {{projectInfo.name}}
          <span class="archivedStyle oneLine" v-if="archived">
          <div>Archived</div>
          </span>
        </span>
        <div style="display: flex;align-items: center">
          <!--<div class="editDiv" @click="editProjectVisible = true">Edit</div>-->
          <div class="deleteDiv oneLine" @click="deleteProjectInfo">Delete</div>
        </div>
      </div>
      <div class="projText BarlowBold">Project info</div>
      <!--proj info part-->
      <div class="projInfo BarlowMedium">
        <div style="width: 30%">
          <div style="margin-bottom: 4px">Time created</div>
          <el-input style="margin-bottom: 16px" disabled v-model="createdTime"></el-input>
          <div style="margin-bottom: 4px">Time modified</div>
          <el-input style="margin-bottom: 16px" disabled v-model="lastEditedTime"></el-input>
          <div style="margin-bottom: 4px">Business scenario</div>
          <el-input disabled v-model="businessScenarioMap[projectInfo.businessScenario]"></el-input>
        </div>
        <div style="width: 65%">
          <div style="margin-bottom: 4px">Project name</div>
          <el-input style="margin-bottom: 16px" v-model="saveProjInfoName"></el-input>
          <div style="margin-bottom: 4px">Project description</div>
          <el-input type="textarea" :rows="2" v-model="saveProjInfoDescription"></el-input>
          <div style="margin-bottom: 10px;margin-top: 16px">Principles</div>
          <el-checkbox-group v-model="principleList">
            <el-checkbox label="principleGeneric" disabled>Generic</el-checkbox>
            <el-checkbox label="principleFairness">Fairness</el-checkbox>
            <el-checkbox label="principleEA">Ethics & Accountability</el-checkbox>
            <el-checkbox label="principleTransparency">Transparency</el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      <div style="display: flex;justify-content: flex-end">
        <div class="editDiv BarlowMedium" @click="doSaveProjInfo" style="margin-top: 8px">Save</div>
      </div>
      <div class="divide_line"></div>
      <!--invite user & members-->
      <div class="BarlowMedium">
        <div>
          <div class="invite-member BarlowBold">Invite user</div>
          <el-tag size="mini" effect="plain" style="margin-bottom: 10px" v-for="tag in tags" @close="handleClose(tag)" :key="tag.id" closable type="info">{{ tag.fullName }}</el-tag>
          <div style="display: flex;align-items: center;justify-content: space-between">
            <el-select filterable @change="addUserToTags" value-key="id" v-model="selectUserObj" placeholder="Choose a user">
              <el-option class="selectStyle" v-for="item in userList" :key="item.id" :label="item.fullName" :value="item"></el-option>
            </el-select>
            <div style="display: flex;justify-content: space-between;align-items: center;margin-left: 12px;width: 400px">
              <el-select v-model="userType" placeholder="Choose a role permission">
                <el-option class="selectStyle" v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
              </el-select>
              <div class="addUsers" @click="addUsersToProject">Invite</div>
            </div>
          </div>
        </div>
        <div class="divide_line"></div>
        <div>
          <div class="invite-member BarlowBold">Members</div>
          <!--<div class="divide_line_1"></div>-->
          <!--groupMember-->
          <div style="margin-top: 8px">
            <div class="bodyRow" type="flex" v-for="(item,index) in projectMember" :key="item.userId">
              <div style="display: flex;align-items: center">
                <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
                <div class="nameDesc">
                  <div>{{item.fullName}}</div>
                  <span>{{item.email}}</span>
                </div>
              </div>
              <div style="display: flex;align-items: center">
                <div>
                  <el-select @change="changeMemberRole(item)" v-model="item.type">
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
    <!--edit ProjectInfo-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="editProjectVisible" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">Edit project</span></template>
      <el-form ref="editProjectFormRefs" label-position="top" label="450px" :model="editProjectForm">
        <el-form-item class="login" label="Project name" prop="name">
          <el-input placeholder="Please input a project name" v-model="editProjectForm.name"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Project description" prop="description">
          <el-input :rows="3" type="textarea" placeholder="Please input project description here" v-model="editProjectForm.description"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Business scenario" prop="businessScenario">
          <el-select v-model="editProjectForm.businessScenario" placeholder="Please choose a business scenario">
            <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="editProjectVisible = false">Cancel</el-button>
        <el-button class="GreenBC" style="color: #FFF" @click="editProjectInfo">Edit</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "AdminProjectPage",
    data() {
      return {
        projectId: this.$route.query.projectId,
        userId: this.$route.query.userId,
        projectInfo: {},
        saveProjInfoName: '',
        saveProjInfoDescription: '',
        saveProjInfo: {},
        createdTime: '',
        lastEditedTime: '',
        userList: [],
        tags: [],
        addUserId: [],
        userType: '',
        selectUserObj: {},
        projectMember: [],
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
        businessScenarioList: [],
        businessScenarioMap: {},
        editProjectVisible: false,
        editProjectForm: {
          name: '',
          description: '',
          businessScenario: '',
        },
        principleList: ['principleGeneric'],
        archived: false,
      }
    },
    created() {
      this.getBusinessScenarioList()
      this.getProjectInfo()
      this.getUserList()
      this.getProjectMember()
    },
    methods: {
      addUsersToProject() {
        let addUserList = []
        this.addUserId.map(item => {
          let addUser = {}
          addUser.userId = item
          addUser.type = this.userType
          addUserList.push(addUser)
        })
        this.$http.put(`/api/admin/project/${this.projectId}/member`,addUserList).then(res => {
          if (res.status == 200) {
            this.$message.success('Invite successfully')
            this.selectUserObj = {}
            this.userType = ''
            this.tags = []
            this.addUserId = []
            this.getProjectMember()
          }
        })
      },
      changeMemberRole(user) {
        let changeRole = {}
        changeRole.userId = user.userId
        changeRole.type = user.type
        this.$http.post(`/api/admin/project/${this.projectId}/member`,changeRole).then(res => {
          if (res.status== 200){
            this.$message.success('Change successfully')
          }
        })
      },
      doSaveProjInfo() {
        this.saveProjInfo.name = this.saveProjInfoName
        this.saveProjInfo.description = this.saveProjInfoDescription
        if (this.principleList.indexOf('principleFairness') != -1) {
          this.saveProjInfo.principleFairness = true
        } else {this.saveProjInfo.principleFairness = false}
        if (this.principleList.indexOf('principleEA') != -1) {
          this.saveProjInfo.principleEA = true
        } else {this.saveProjInfo.principleEA = false}
        if (this.principleList.indexOf('principleTransparency') != -1) {
          this.saveProjInfo.principleTransparency = true
        } else {this.saveProjInfo.principleTransparency = false}
        this.$http.post(`/api/admin/project/${this.projectId}`,this.saveProjInfo).then(res => {
          if (res.status == 200) {
            this.$message.success('Save successfully')
            this.getProjectInfo()
          }
        })
      },
      editProjectInfo() {
        this.editProjectForm.id = this.projectId
        this.$http.post(`/api/admin/project/${this.projectId}`,this.editProjectForm).then(res => {
          if(res.status == 200) {
            this.$message.success('Edit successfully')
            this.getProjectInfo()
          }
          this.editProjectVisible = false
        })
      },
      deleteProjectInfo() {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/admin/project/${this.projectId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.$router.push({path:'/administration',query: {activeName: 'first'}})
            }
          })
        })
      },
      getBusinessScenarioList() {
        this.$http.get('/api/system/business_scenario').then(res => {
          if(res.status == 200) {
            this.businessScenarioList = res.data
            this.businessScenarioList.map(item => {
              this.businessScenarioMap[item.code] = item.name
            })
          }
        })
      },
      getProjectMember() {
        this.$http.get(`/api/admin/project/${this.projectId}/member`).then(res => {
          if (res.status == 200) {
            this.projectMember = res.data
          }
        })
      },
      deleteMember(userId) {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/admin/project/${this.projectId}/member/${userId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.getProjectMember()
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
      getProjectInfo() {
        this.$http.get(`/api/admin/project/${this.projectId}`).then(res => {
          this.archived = res.data.archived
          this.projectInfo = res.data
          this.saveProjInfo = res.data
          this.saveProjInfoName = res.data.name
          this.saveProjInfoDescription = res.data.description
          this.createdTime = this.dateFormat(res.data.createdTime)
          this.lastEditedTime = this.dateFormat(res.data.lastEditedTime)
          if (res.data.principleFairness) {this.principleList.push('principleFairness')}
          if (res.data.principleEA) {this.principleList.push('principleEA')}
          if (res.data.principleTransparency) {this.principleList.push('principleTransparency')}
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
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
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
  .archivedStyle {
    flex-shrink: 0;
    width: auto;
    display: flex !important;
    margin-left: 16px;
    height: 24px;
    background-color: #FCB215;
    padding: 0px 8px;
    text-align: center;
    border-radius: 14px;
    > div {
      color: #FFF;
      font-size: 14px;
      line-height: 24px;
    }
  }

</style>