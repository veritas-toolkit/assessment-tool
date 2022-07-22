<template>
  <div style="height: 100%">
    <el-container class="container">
      <!--aside area-->
      <el-aside style="width:280px">
        <div style="height: calc(100% - 64px)">
          <!--veritas title-->
          <img class="asideTitle" src="../assets/homePic/logo.png" alt="">
          <!--<div class="asideTitle RobotoMediumItalic"><span>Veritas</span></div>-->
          <!--menu area-->
          <el-menu :default-active="activeName" background-color="#EDF2F6" router active-text-color="#333333">
            <el-menu-item index="projects">
              <template slot="title">
                <img src="../assets/groupPic/time.png" class="menuIcon">
                <span class="BarlowBold">Projects</span>
              </template>
            </el-menu-item>
            <el-menu-item index="group">
              <template slot="title">
                <img src="../assets/groupPic/admin.png" class="menuIcon">
                <span class="BarlowBold">Groups</span>
              </template>
            </el-menu-item>
            <el-menu-item index="myAccount">
              <template slot="title">
                <img src="../assets/groupPic/user.png" class="menuIcon">
                <span class="BarlowBold">My Account</span>
              </template>
            </el-menu-item>
            <el-menu-item index="Administration" v-if="admin">
              <template slot="title">
                <img src="../assets/adminPic/administration.png" class="menuIcon">
                <span class="BarlowBold">Administration</span>
              </template>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="leftBottom BarlowBold">
          <img src="../assets/groupPic/info.png" class="menuIcon">
          <span>{{accountInfo.fullName}}</span>
        </div>
      </el-aside>
      <!--main area-->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
    <!--change password-->
    <el-dialog class="BarlowMedium" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" :visible.sync="shouldChangePassword" width="480px" @close="changePasswordClosed" append-to-body>
      <template slot="title"><span class="dialogTitle BarlowBold">Change password</span></template>
      <!--create group form-->
      <el-form ref="passwordFormRefs" class="BarlowMedium" label-position="top" label="450px" :model="passwordForm">
        <el-form-item class="login" label="Old password" prop="oldPassword">
          <el-input type="password" placeholder="Please input old password" v-model="passwordForm.oldPassword"></el-input>
        </el-form-item>
        <el-form-item class="login" label="New password" prop="newPassword">
          <el-input type="password" placeholder="Please input new password" v-model="passwordForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Password confirmation" prop="passwordConfirm">
          <el-input type="password" placeholder="Please input new password again" v-model="passwordForm.passwordConfirm"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="info" class="GreenBC" @click="changePassword">Save</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      activeName: '',
      accountInfo: {},
      admin: false,
      shouldChangePassword: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        passwordConfirm: '',
      }
    }
  },
  created() {
    this.getWhoAmI()
    this.activeName = window.location.href.split('/')[window.location.href.split('/').length - 1]
  },
  methods: {
    getWhoAmI() {
      this.$http.get('/api/account').then(res => {
        if(res.status == 200) {
          this.accountInfo = res.data
          this.admin = res.data.admin
          this.shouldChangePassword = res.data.shouldChangePassword
        }
      })
    },
    changePasswordClosed() {
      this.$refs.passwordFormRefs.resetFields()
    },
    changePassword() {
      if (this.passwordForm.newPassword && this.passwordForm.passwordConfirm && this.passwordForm.oldPassword) {
        if (this.passwordForm.newPassword === this.passwordForm.passwordConfirm) {
          if (this.passwordForm.oldPassword != this.passwordForm.newPassword) {
            this.$http.post('/api/account/change_password',{
              id:this.accountInfo.id,
              oldPassword:this.passwordForm.oldPassword,
              newPassword:this.passwordForm.newPassword}).then(res => {
              if(res.status == 200) {
                this.$message.success('Change successfully')
                this.shouldChangePassword = false
              }
            })
          } else {
            this.$message.warning('Old password and new password cannot be the same')
          }
        } else {
          this.$message.warning('The password you entered twice was inconsistent')
        }
      } else {
        this.$message.warning('Old password and new password cannot be empty')
      }
    }
  }
}
</script>

<style lang="less" scoped>
  .el-menu {
    border-right: none;
  }
  .container {
    height: 100%;
  }
  .el-menu-item:hover{
    outline: 0 !important;
    color: #333333 !important;
    background: #DCE6EC !important;
  }
  .el-menu-item.is-active {
    color: #333333 !important;
    background: #DCE6EC !important;
  }
  .el-aside {
    padding: 0px 8px;
    background-color: #EDF2F6;
    height: 100%;
    > .el-menu {
      border-right: none;
      > .el-menu-item {
        padding-left: 12px !important;
        font-size: 16px;
        font-weight: 600;
      }
    }
  }
  .asideTitle {
    margin: 8px 0 0 8px;
    height: 60px;
  }
  .leftBottom {
    display: flex;
    align-items: center;
    margin-left: 20px;
    margin-bottom: 24px;
    > span {
      font-size: 16px;
      font-weight: 600;
      color: #333333;
    }
  }
  .menuIcon {
    width: 24px;
    height: 24px;
    margin-right: 8px;
  }
</style>
