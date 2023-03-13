<template>
  <div style="padding: 32px 64px !important;">
    <!--groupPage title-->
    <div style="display: flex;justify-content: space-between">
      <div class="title BarlowBold">
        <router-link :to="{ path: '/group' }"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
        <span>Group</span>
      </div>

    </div>
    <!--groupPage body-->
    <div style="margin-left: 48px" class="BarlowMedium">
      <div class="BarlowMedium titleBox">
        <div class="oneLine"><span class="projName">{{groupInfo.name}}</span></div>
        <!--more actions-->
        <el-popover placement="left" width="154px" trigger="click" v-show="permissionList.indexOf('group:edit') != -1 || permissionList.indexOf('group:delete') != -1">
          <div>
            <div v-show="permissionList.indexOf('group:edit') != -1" class="editGroup BarlowMedium" @click="editGroupVisible = true" style="cursor: pointer"><img src="../../assets/groupPic/edit.png" alt=""><span>Edit group</span></div>
            <div v-show="permissionList.indexOf('group:edit') != -1 && permissionList.indexOf('group:delete') != -1" class="divide_line"></div>
            <div v-show="permissionList.indexOf('group:delete') != -1" class="deleteGroup BarlowMedium" @click="deleteGroupInfo" style="cursor: pointer"><img src="../../assets/groupPic/delete.png" alt=""><span>Delete group</span></div>
          </div>
          <div slot="reference" class="moreAct" style="cursor: pointer">
            <img src="../../assets/groupPic/more.png" alt="">
          </div>
        </el-popover>
      </div>
      <div class="timeDesc">
        <div class="dateStyle">Created  at  {{dateFormat(groupInfo.createdTime)}}</div>
        <div class="dateStyle">Last changed  at  {{dateFormat(groupInfo.lastModifiedTime)}}</div>
      </div>
      <!--group desc-->
      <div class="groupDesc">
        {{groupInfo.description}}
      </div>
      <el-tabs style="margin-top: 16px" v-model="activeName" class="BarlowMedium" >
        <el-tab-pane label="Project" name="first">
          <el-row :gutter="20" style="margin-top: 24px;display: flex">
            <el-col>
              <el-input @input="getProjectList" placeholder="Search your project here" prefix-icon="el-icon-search" v-model="projectKeyword"></el-input>
            </el-col>
            <el-col v-show="permissionList.indexOf('project:create') != -1" style="width: 260px">
              <div class="creProj BarlowMedium" @click="createProjectVisible = true" style="cursor: pointer"><i class="el-icon-plus"></i><span>Create project</span></div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 6px">
            <el-col v-for="(item,index) in projectList" :span="6">
              <el-card class="box-card" @click.native="projectPage(item)">
                <div slot="header" class="boxCardHeader">
                  <div class="owner oneLine" v-if="item.userOwner">{{item.userOwner.username}}</div>
                  <div class="owner oneLine" v-else-if="item.groupOwner">{{item.groupOwner.name}}</div>
                  <div class="projName1 oneLine BarlowBold">{{item.name}}</div>
                  <span>Edited at {{dateFormat(item.lastEditedTime)}}</span>
                </div>
                <el-tooltip class="item" effect="dark" :content="item.description" placement="top">
                  <div class="description oneLine"><span class="oneLine">{{item.description}}</span></div>
                </el-tooltip>
              </el-card>
            </el-col>
          </el-row>
          <div class="block">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page=page
              :page-sizes="[4, 8, 12, 16]"
              :page-size=pageSize
              layout="sizes, prev, pager, next, jumper, total"
              :total=total>
            </el-pagination>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Member" name="second">
          <div v-show="permissionList.indexOf('group:manage members') != -1">
            <div style="margin: 16px 0px;">
              <div class="artifacts">Invite members</div>
            </div>
            <el-tag size="mini" effect="plain" style="margin-bottom: 10px" v-for="tag in tags" @close="handleClose(tag)" :key="tag.id" closable type="info">{{ tag.fullName }}</el-tag>
            <div style="display: flex;align-items: center;justify-content: space-between">
              <el-select filterable @change="addUserToTags" value-key="id" v-model="selectUserObj" placeholder="Choose a user">
                <el-option class="selectStyle" v-for="item in userList" :key="item.id" :label="item.fullName" :value="item"></el-option>
              </el-select>
              <div style="display: flex;justify-content: space-between;align-items: center;margin-left: 12px;width: 400px">
                <el-select v-model="userTypeGroup" placeholder="Choose a role permission">
                  <el-option class="selectStyle" v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
                </el-select>
                <div class="addUsers" @click="addUsersToGroup">Invite</div>
              </div>
            </div>
            <div class="dividingLine"></div>
          </div>
          <div class="artifacts" style="margin-top: 24px">Members</div>
          <!--groupMember-->
          <div style="margin-top: 8px">
            <div class="bodyRow" type="flex" v-for="(item,index) in groupMember" :key="item.userId">
              <div style="display: flex;align-items: center">
                <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
                <div class="nameDesc">
                  <div>{{item.fullName}}</div>
                  <span>{{dateFormat(item.joinTime)}}</span>
                </div>
              </div>
              <div style="display: flex;align-items: center">
                <div>
                  <el-select :disabled="permissionList.indexOf('group:manage members') == -1 || item.userId == creatorUserId" v-model="item.type" @change="changeMemberRole(item)">
                    <el-option v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
                  </el-select>
                </div>
                <el-button :disabled="permissionList.indexOf('group:manage members') == -1 || item.userId == creatorUserId" class="deleteMember" @click="deleteMember(item.userId)" icon="el-icon-delete-solid"></el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="createProjectVisible" width="548px">
      <template slot="title"><span class="dialogTitle">New project</span></template>
      <el-form :rules="projectFormRules" ref="projectFormRefs" label-position="top" label="450px" :model="projectForm">
        <el-form-item class="BarlowMedium" label="Project name" prop="name">
          <el-input placeholder="Please input a project name" v-model="projectForm.name"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Project description" prop="description">
          <el-input type="textarea" :rows="3" placeholder="Please input project description here" v-model="projectForm.description"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Business scenario" prop="businessScenario">
          <el-select v-model="projectForm.businessScenario" placeholder="Please choose a business scenario">
            <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Questionnaire template" prop="questionnaireTemplateId">
          <el-select v-model="projectForm.questionnaireTemplateId" placeholder="Please choose a questionnaire template">
            <el-option v-for="item in createTemplateList" :key="item.templateId" :label="item.name" :value="item.templateId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createProjectVisible = false" class="BlackBorder BarlowMedium">Cancel</el-button>
        <el-button type="primary" @click="createProject" class="GreenBC BarlowMedium">Create</el-button>
      </span>
    </el-dialog>
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="addMemberVisible" width="548px">
      <template slot="title"><span class="dialogTitle">Invite member</span></template>
      <el-input @input="getUserList" clearable style="margin: 10px 0px 16px 0px" placeholder="Search by keyword" prefix-icon="el-icon-search" v-model="keyword"></el-input>
      <el-select v-model="userType" placeholder="Choose a role permission">
        <el-option class="selectStyle" v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
      </el-select>
      <el-date-picker style="margin-top: 16px" v-model="expirationDate" type="date" placeholder="Expiration date"></el-date-picker>
      <el-checkbox-group v-model="checkList">
      <div :class="['userList',index===0? 'border_top': '']" v-for="(item,index) in userList" :key="item.id" v-show="userListShow">
        <div class="userListLeft">
          <el-checkbox :label="item.id"><br></el-checkbox>
          <img src="../../assets/groupPic/Avatar.png" alt="">
          <div class="nameDesc">
            <div>{{item.fullName}}</div>
            <span>{{item.email}}</span>
          </div>
        </div>
        <div class="memberStyle"><span>member</span></div>
      </div>
      </el-checkbox-group>
      <div v-show="notFound">No matches found</div>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="addMemberVisible = false">Cancel</el-button>
        <el-button class="GreenBC" type="primary" @click="addMember">Invite</el-button>
      </span>
    </el-dialog>
    <!--edit GroupInfo-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="editGroupVisible" width="548px" @close="editGroupClosed" append-to-body>
      <template slot="title"><span class="dialogTitle">Edit group</span></template>
      <el-form :rules="editGroupFormRules" ref="editGroupFormRefs" label-position="top" label="450px" :model="editGroupForm">
        <el-form-item class="login" label="Group name" prop="name">
          <el-input placeholder="Please input a group name" v-model="editGroupForm.name"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Group description" prop="description">
          <el-input :rows="3" type="textarea" placeholder="Please input group description here" v-model="editGroupForm.description"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="editGroupVisible = false">Cancel</el-button>
        <el-button class="GreenBC" @click="editGroupInfo">Edit</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "GroupPage",
    data() {
      return {
        checkList: [],
        userTypeGroup: '',
        userType: '',
        expirationDate: '',
        addMemberList: [],
        groupId: this.$route.query.groupId,
        groupInfo: {},
        groupMember: [],
        userList: [],
        addMemberVisible: false,
        editGroupVisible: false,
        keyword: '',
        projectKeyword: '',
        userListShow: false,
        notFound: false,
        editGroupForm: {
          name: '',
          description: '',
        },
        editGroupFormRules: {
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
        },
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
        activeName: 'first',
        projectList: [],
        tags: [],
        selectUserObj: {},
        createProjectVisible: false,
        businessScenarioList: [],
        createTemplateList: [],
        projectForm: {
          name: '',
          description: '',
          businessScenario: '',
          groupOwnerId: '',
          questionnaireTemplateId: '',
        },
        projectFormRules: {
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
          businessScenario: [{ required: true, trigger: 'blur' },],
          questionnaireTemplateId: [{ required: true, message: 'questionnaireTemplate is required', trigger: 'blur' },],
        },
        addUserId: [],
        permissionList: [],
        creatorUserId: '',
        page: 1,
        pageSize: 8,
        total: 0,
      }
    },
    created() {
      this.getGroup()
      this.getGroupDetail()
      this.getGroupMember()
      this.getUserList()
      this.getProjectList()
      this.getBusinessScenarioList()
      this.getCreateTemplateList()
    },
    methods: {
      getGroupDetail() {
        this.$http.get(`/api/group/${this.groupId}/detail`).then(res => {
          this.permissionList = res.data.groupRole.permissionList
        })
      },
      projectPage(item) {
        this.$router.push({path:'/projectPage',query: {id:item.id,groupId:this.groupId}})
      },
      addUsersToGroup() {
        let addUserList = []
        this.addUserId.map(item => {
          let addUser = {}
          addUser.userId = item
          addUser.type = this.userTypeGroup
          addUserList.push(addUser)
        })
        this.$http.put(`/api/group/${this.groupId}/member`,addUserList).then(res => {
          if (res.status == 200) {
            this.$message.success('Invite successfully')
            this.selectUserObj = {}
            this.userTypeGroup = ''
            this.tags = []
            this.addUserId = []
            this.getGroupMember()
          }
        })
      },
      getBusinessScenarioList() {
        this.$http.get('/api/system/business_scenario').then(res => {
          if(res.status == 200) {
            this.businessScenarioList = res.data
          }
        })
      },
      getCreateTemplateList() {
        this.$http.get('/api/system/questionnaire_template').then(res => {
          if(res.status == 200) {
            this.createTemplateList = res.data
          }
        })
      },
      createProject() {
        this.$refs.projectFormRefs.validate(val => {
          if (val) {
            this.projectForm.groupOwnerId = this.groupId
            this.$http.put('/api/project/new',this.projectForm).then(res => {
              if (res.status == 201) {
                this.$message.success('Create successfully')
                this.$refs.projectFormRefs.resetFields()
                this.getProjectList()
              }
            })
            this.createProjectVisible = false
          }
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
      getGroup() {
        this.$http.get(`/api/group/${this.groupId}`).then(res => {
          if (res.status == 200) {
            this.groupInfo = res.data
            this.creatorUserId = res.data.creatorUserId
            this.editGroupForm.name = res.data.name
            this.editGroupForm.description = res.data.description
          }
        })
      },
      getGroupMember() {
        this.$http.get(`/api/group/${this.groupId}/member`).then(res => {
          if (res.status == 200) {
            this.groupMember = res.data
          }
        })
      },
      getUserList() {
        this.$http.get('/api/user',{params: {'keyword': this.keyword}}).then(res => {
          if (res.status == 200) {
            this.notFound = false
            this.userListShow = true
            res.data.map(item => {
              if (item.id != this.creatorUserId) {
                this.userList.push(item)
              }
            })
          }
        }).catch(err => {
          if (err.response.status == 404) {
            this.userListShow = false
            this.notFound = true
          }
        })
      },
      addMember() {
        this.addMemberList = []
        this.checkList.map(item => {
          let val = {}
          val.userId = item
          val.groupId = this.groupId
          val.type = this.userType
          val.expirationDate = this.expirationDate
          this.addMemberList.push(val)
        })
        this.$http.put(`/api/group/${this.groupId}/member`,this.addMemberList).then(res => {
          if (res.status == 200) {
            this.$message.success('Invite successfully')
            this.getGroupMember()
          }
        })
        this.addMemberVisible = false
      },
      deleteMember(userId) {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/group/${this.groupId}/member/${userId}`).then(res => {
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
        this.$http.post(`/api/group/${this.groupId}/member`,changeRole).then(res => {
          if (res.status == 200) {
            this.$message.success('Change successfully')
          }
        })
      },
      deleteGroupInfo() {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/group/${this.groupId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.$router.push('/group')
            }
          }).catch(err => {
            if (err.response.status == 400) {
              this.$confirm('Force delete?',{type: 'error'}).then(() => {
                this.$http.delete(`/api/group/${this.groupId}`,{params:{force:true}}).then(res => {
                  if (res.status == 200) {
                    this.$message.success('Delete successfully')
                    this.$router.push('/group')
                  }
                })
              })
            }
          })
        })
      },
      editGroupInfo() {
        this.$refs.editGroupFormRefs.validate(val => {
          if (val) {
            this.$http.post(`/api/group/${this.groupId}`,this.editGroupForm).then(res => {
              if (res.status == 200) {
                this.$message.success('Edit successfully')
                this.getGroup()
              }
              this.editGroupVisible = false
            })
          }
        })
      },
      editGroupClosed() {
        this.$refs.editGroupFormRefs.resetFields()
      },
      handleSizeChange(val) {
        this.pageSize = val
        this.getProjectList()
      },
      handleCurrentChange(val) {
        this.page = val
        this.getProjectList()
      },
      getProjectList() {
        this.$http.get(`/api/group/${this.groupId}/project`,{params: {'keyword': this.projectKeyword,page:this.page,pageSize:this.pageSize}}).then(res => {
          if (res.status == 200) {
            this.projectList = res.data.records
            this.total = res.data.total
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .title {
    display: flex;
    height: 40px;
    align-items: center;
    > a {
      height: 40px;
      display: flex;
      align-items: center;
    }
    > span {
      margin-left: 16px;
      font-size: 16px;
      font-weight: bold;
      line-height: 40px;
      color: #000;
    }
  }
  .titleBox {
    margin-top: 8px;
    height: 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .projName {
    font-size: 24px;
    font-weight: bold;
  }
  .projName1 {
    line-height: 22px;
    font-size: 16px;
    font-weight: bold;
  }
  .creProj {
    height: 40px;
    background-color: #78BED3;
    border-radius: 4px;
    text-align: center;
    color: #fff;
    > span {
      color: #fff;
      font-size: 16px;
      line-height: 40px;
      margin-left: 10px;
    }
  }
  .backPic {
    line-height: 40px;
    width: 32px;
    height: 32px;
  }
  .moreAct {
    width: 40px;
    height: 40px;
    position: relative;
    background-color: #FFF;
    border: 1px solid rgba(0,0,0,0.85);
    border-radius: 4px;
    > img {
      width: 24px;
      height: 24px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%,-50%);
    }
  }
  .timeDesc {
    margin-top: 12px;
  }
  .dateStyle {
    font-size: 14px;
    line-height: 20px;
    color: #929292;
    font-weight: 400;
  }
  .groupDesc {
    margin-top: 16px;
    font-size: 16px;
    line-height: 24px;
    color: #333333;
    font-weight: 400;
  }
  .dividingLine {
    width: 100%;
    height: 1px;
    background-color: #E6E6E6;
    margin: 24px 0px;
  }
  .memberDesc {
    display: flex;
    align-items: center;
    justify-content: space-between;
    > span {
      font-size: 16px;
      font-weight: bold;
      line-height: 40px;
    }
    > div {
      width: 111px;
      height: 40px;
      text-align: center;
      > span {
        font-size: 16px;
        color: #666666;
        font-weight: 500;
        line-height: 40px;
      }
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
  .border_top {
    margin-top: 16px;
    border-top: 1px solid #E6E6E6;
  }
  .userList {
    height: 64px;
    border-bottom: 1px solid #E6E6E6;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .userListLeft {
    display: flex;
    align-items: center;
    > img {
      margin-left: 4px;
      width: 40px;
      height: 40px;
    }
  }
  .memberStyle {
    width: 71px;
    height: 24px;
    border-radius: 12px;
    border: 1px solid #E0E0E0;
    text-align: center;
    > span {
      font-size: 14px;
      line-height: 24px;
    }
  }
  .selectStyle {
    width: 100%;
  }
  .editGroup {
    display: flex;
    align-items: center;
    > img {
      width: 24px;
      height: 24px;
    }
    > span {
      margin-left: 12px;
      font-size: 16px;
      color: #333333;
    }
  }
  .deleteGroup {
    display: flex;
    align-items: center;
    > img {
      width: 24px;
      height: 24px;
    }
    > span {
      margin-left: 12px;
      font-size: 16px;
      color: #E02020;
    }
  }
  .divide_line {
    margin: 13px 0px;
    height: 1px;
    background-color: #CED3D9;
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
  .artifacts {
    font-weight: bold;
    font-size: 18px;
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
  .el-tag--plain.el-tag--info {
    margin-right: 10px;
  }
  .box-card {
    cursor: pointer;
    margin-top: 16px;
    position: relative;
  }
  .owner {
    font-size: 14px;
    font-family: BarlowMedium;
    font-weight: bold;
    color: #175EC2;
    margin-bottom: 4px;
  }
  .boxCardHeader {
    > span {
      margin-top: 10px;
      font-size: 12px;
      color: #bbb;
    }
  }
  .progressLabel {
    color: #000;
    margin-top: 16px;
    margin-bottom: 2px;
    font-size: 24px;
  }
  .oneLine{
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .description {
    width: 100%;
    height: 32px;
    background-color: #F2F2F2;
    display: flex;
    align-items: center;
    > span {
      margin-left: 8px;
      font-size: 14px;
    }
  }
</style>
