<template>
  <div style="padding: 32px 64px !important;">
    <div class="title BarlowBold">
      <router-link :to="{ path: '/administration',query: {activeName: 'third'}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
      <span>User</span>
    </div>
    <!--body part-->
    <div style="margin-left: 40px">
      <div class="title-box BarlowBold">
        <span>{{userInfo.fullName}}&nbsp;&nbsp;
          <span v-show="userInfo.admin" class="admin-style">Admin</span>
          <span v-show="userInfo.locked" class="locked-style">Locked</span>
        </span>
        <div style="display: flex;align-items: center">
          <div v-show="!userInfo.admin" class="editDiv" @click="setAdmin" style="margin-right: 12px">Set admin</div>
          <div v-show="userInfo.admin" class="editDiv" @click="unSetAdmin" style="margin-right: 12px">Unset admin</div>
          <div v-show="!userInfo.locked" class="editDiv" style="margin-right: 12px" @click="lockUser">Lock</div>
          <div v-show="userInfo.locked" class="editDiv" @click="unlockUser">Unlock</div>
          <div class="deleteDiv" @click="deleteUser">Delete</div>
        </div>
      </div>
      <el-tabs style="margin-top: 24px" class="BarlowMedium" v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="Profile" name="first">
          <div class="userProfile">
            <div style="width: 54%">
              <div style="margin-bottom: 4px">Username</div>
              <el-input style="margin-bottom: 16px" v-model="userInfo.username"></el-input>
              <div style="margin-bottom: 4px">Full name</div>
              <el-input style="margin-bottom: 16px" v-model="fullName"></el-input>
              <div style="margin-bottom: 4px">Email</div>
              <el-input style="margin-bottom: 16px" v-model="userInfo.email"></el-input>
              <div style="margin-bottom: 4px">Project amount limit</div>
              <el-input style="margin-bottom: 16px" v-model="userInfo.projectLimited"></el-input>
              <div style="margin-bottom: 4px">Group amount limit</div>
              <el-input style="margin-bottom: 24px" v-model="userInfo.groupLimited"></el-input>
              <div style="display: flex">
                <div class="editDiv" @click="doSaveUserInfo">Save</div>
              </div>
            </div>
            <div style="width: 38%">
              <div style="margin-bottom: 4px">New password</div>
              <el-input :type="type" style="margin-bottom: 16px" v-model="password"></el-input>
              <div style="margin-bottom: 4px">Password confirmation</div>
              <el-input :type="type" style="margin-bottom: 8px" v-model="passwordConfirm"></el-input>
              <el-checkbox style="margin-bottom: 24px" v-model="checked">Show password</el-checkbox>
              <div style="display: flex">
                <div class="editDiv" @click="setNewPassword">Set new password</div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Project" name="second">
          <el-row class="bodyRow" v-for="(item,index) in projectList" :key="item.id" @click.native="userToProject(item.id)">
            <div class="firstText"><span>{{ item.name.substring(0, 1) }}</span></div>
            <div class="nameDesc">
              <div>
                <span v-if="item.userOwner">{{item.userOwner.username}}</span>
                <span v-else-if="item.groupOwner">{{item.groupOwner.name}}</span>
                / {{ item.name }}</div>
              <span>{{ item.description }}</span>
            </div>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="Group" name="third">
          <el-row class="bodyRow" v-for="(item,index) in groupList" :key="item.id"  @click.native="userToGroup(item.id)">
            <div class="firstText"><span>{{item.name.substring(0,1)}}</span></div>
            <div>
              <div class="nameDesc">
                <div>{{item.name}}</div>
                <span>{{item.description}}</span>
              </div>
            </div>
          </el-row>
        </el-tab-pane>
      </el-tabs>
      <div class="block" v-show="activeName != 'first'">
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
    </div>
  </div>
</template>

<script>
  export default {
    name: "AdminUserPage",
    data() {
      return {
        type: 'password',
        userId: this.$route.query.userId,
        userInfo: '',
        fullName: '',
        activeName: 'first',
        password: '',
        passwordConfirm: '',
        projectList: [],
        groupList: [],
        checked: false,
        page: 1,
        pageSize: 8,
        total: 0,
      }
    },
    mounted() {
      if (this.$route.query.activeName) {
        this.activeName = this.$route.query.activeName
      } else {this.activeName = 'first'}
    },
    created() {
      this.getUserInfo()
      this.getProjectList()
      this.getGroupList()
    },
    watch: {
      checked:function () {
        if (this.checked == true) {
          this.type = 'text'
        } else {
          this.type = 'password'
        }
      },
      activeName:function () {
        this.page = 1
        if (this.activeName == 'second') {
          this.getProjectList()
        } else if (this.activeName == 'third') {
          this.getGroupList()
        }
      }
    },
    methods: {
      userToProject(id) {
        this.$router.push({path:'/adminProjectPage',query: {projectId:id,userId:this.userId}})
      },
      userToGroup(id) {
        this.$router.push({path:'/adminGroupPage',query: {groupId:id,userId:this.userId}})
      },
      setAdmin() {
        this.$http.put(`/api/admin/user/${this.userId}/set-admin`).then(res => {
          if (res.status == 200) {
            this.$message.success('Set successfully')
            this.getUserInfo()
          }
        })
      },
      unSetAdmin() {
        this.$http.put(`/api/admin/user/${this.userId}/unset-admin`).then(res => {
          if (res.status == 200) {
            this.$message.success('Unset successfully')
            this.getUserInfo()
          }
        })
      },
      unlockUser() {
        this.$http.post(`/api/admin/user/${this.userId}/unlock`).then(res => {
          if (res.status == 200) {
            this.$message.success('Unlock successfully')
            this.getUserInfo()
          }
        })
      },
      lockUser() {
        this.$http.post(`/api/admin/user/${this.userId}/lock`).then(res => {
          if (res.status == 200) {
            this.$message.success('Lock successfully')
            this.getUserInfo()
          }
        })
      },
      handleClick(tab, event) {
        this.activeName = tab.name
      },
      getUserInfo() {
        this.$http.get(`/api/admin/user/${this.userId}`).then(res => {
          if (res.status == 200) {
            this.userInfo = res.data
            this.fullName = res.data.fullName
          }
        })
      },
      doSaveUserInfo() {
        this.userInfo.fullName = this.fullName
        this.$http.post(`/api/admin/user/${this.userId}`,this.userInfo).then(res => {
          if (res.status == 200) {
            this.$message.success('Save successfully')
          }
        })
      },
      deleteUser() {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/admin/user/${this.userId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.$router.push({path:'/administration',query: {activeName: 'third'}})
            }
          }).catch(err => {
            if (err.response.status == 400) {
              this.$confirm('Lock user?',{type: 'error'}).then(() => {
                this.$http.post(`/api/admin/user/${this.userId}/lock`).then(res => {
                  if (res.status == 200) {
                    this.$message.success('Lock successfully')
                    this.getUserInfo()
                  }
                })
              })
            }
          })
        })
      },
      handleSizeChange(val) {
        this.pageSize = val
        if (this.activeName == 'second') {
          this.getProjectList()
        } else if (this.activeName == 'third') {
          this.getGroupList()
        }
      },
      handleCurrentChange(val) {
        this.page = val
        if (this.activeName == 'second') {
          this.getProjectList()
        } else if (this.activeName == 'third') {
          this.getGroupList()
        }
      },
      getProjectList() {
        this.$http.get(`/api/admin/user/${this.userId}/project`,{params:{page:this.page,pageSize:this.pageSize}}).then(res => {
          if (res.status == 200) {
            this.projectList = res.data.records
            this.total = res.data.total
          }
        })
      },
      getGroupList() {
        this.$http.get(`/api/admin/user/${this.userId}/group`,{params:{page:this.page,pageSize:this.pageSize}}).then(res => {
          if (res.status == 200) {
            this.groupList = res.data.records
            this.total = res.data.total
          }
        })
      },
      setNewPassword() {
        if (this.password == '' && this.passwordConfirm == '') {
          this.$message.warning('New password cannot be empty')
          return
        } else {
          if (this.password === this.passwordConfirm) {
            let obj = {}
            obj.newPassword = this.password
            this.$http.post(`/api/admin/user/${this.userId}/change_password`,obj).then(res => {
              if (res.status == 200) {
                this.$message.success('Set successfully')
              }
            })
          }else {
            this.$message.error('The password you entered twice was inconsistent')
          }
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .bodyRow {
    cursor: pointer;
    height: 80px;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    align-items: center;
  }
  .firstText {
    flex-shrink: 0;
    text-align: center;
    min-width: 48px;
    height: 48px;
    background-color: #D5EBF1;
    border-radius: 4px;
    > span {
      font-size: 24px;
      font-weight: 600;
      color: #35839A;
      line-height: 48px;
    }
  }
  .nameDesc {
    flex-shrink: 0;
    margin-left: 16px;
    height: 48px;
    > div {
      font-size: 16px;
      font-weight: 600;
      line-height: 24px;
      padding-bottom: 2px;
      > span {
        color: #175EC2;
      }
    }
    > span {
      font-size: 14px;
      font-weight: 400;
      color: #B8B8B8;
    }
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

  .userProfile {
    display: flex;
    justify-content: space-between;
    margin-top: 32px;
  }
  .locked-style {
    font-size: 14px;
    padding: 2px 4px;
    font-weight: 400;
    color: #FFF;
    background-color: #F48200;
    border-radius: 12px;
  }
  .unlocked-style {
    font-size: 14px;
    padding: 2px 4px;
    font-weight: 400;
    color: #FFF;
    background-color: #78BED3;
    border-radius: 12px;
  }
  .admin-style {
    margin-right: 12px;
    font-size: 14px;
    padding: 2px 4px;
    font-weight: 400;
    color: #FFF;
    background-color: #78BED3;
    border-radius: 12px;
  }
  .user-style {
    margin-right: 12px;
    font-size: 14px;
    padding: 2px 4px;
    font-weight: 400;
    color: #FFF;
    background-color: #67C23A;
    border-radius: 12px;
  }
</style>