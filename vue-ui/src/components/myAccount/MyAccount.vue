<template>
  <div style="padding: 24px !important;">
    <div class="category">My Account</div>
    <div class="divide-line"></div>
    <!--info&password-->
    <div class="info-password BarlowMedium">
      <span>Personal Info</span>
      <el-form label-position="top" label="432px" :model="personalInfoForm">
        <el-form-item class="login BarlowMedium" label="Username">
          <el-input :disabled="!modifyAccount" v-model="personalInfoForm.username"></el-input>
        </el-form-item>
        <el-form-item class="login BarlowMedium" label="Email">
          <el-input :disabled="!modifyAccount" v-model="personalInfoForm.email"></el-input>
        </el-form-item>
        <el-form-item class="login BarlowMedium" label="Full name">
          <el-input :disabled="!modifyAccount" v-model="personalInfoForm.fullName"></el-input>
        </el-form-item>
      </el-form>
      <div>
        <el-button v-show="modifyAccount" type="info" class="username BarlowMedium" @click="changeInfo">Save</el-button>
      </div>
    </div>
    <div class="divide-line"></div>
    <div class="info-password">
      <div style="display: flex;">
        <div>
          <el-button class="BlackBorder BarlowMedium" @click="changePasswordVisible = true">Change password</el-button>
        </div>
        <div style="margin-left: 12px">
          <el-button class="BlackBorder BarlowMedium" @click="logOut">Log out</el-button>
        </div>
      </div>
    </div>
    <!--change password-->
    <el-dialog :close-on-click-modal="false" :visible.sync="changePasswordVisible" width="480px" @close="changePasswordClosed" append-to-body>
      <template slot="title"><span class="dialogTitle">Change password</span></template>
      <el-form ref="passwordFormRefs" class="BarlowMedium" label-position="top" label="450px" :model="passwordForm">
        <el-form-item class="login" label="Old password" prop="oldPassword">
          <el-input type="password" placeholder="Please input old password" v-model="passwordForm.oldPassword"></el-input>
        </el-form-item>
        <el-form-item class="login" label="New password" prop="newPassword">
          <el-input type="password" placeholder="Please input new password" v-model="passwordForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Password confirmation" prop="passwordConfirm">
          <el-input type="password" placeholder="Please input new password again" v-model="passwordForm.passwordConfirm"
                    @keyup.enter.native="changePassword"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="changePasswordVisible = false">Cancel</el-button>
        <el-button type="info" class="GreenBC" @click="changePassword">Save</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "MyAccount",
    data () {
      return {
        personalInfoForm: {
          id: '',
          email: '',
          username: '',
          fullName: '',
        },
        accountInfo: {},
        changePasswordVisible: false,
        passwordForm: {
          oldPassword: '',
          newPassword: '',
          passwordConfirm: '',
        },
        modifyAccount: '',
      }
    },
    created() {
      this.getWhoAmI()
      this.modifyAccountSupport()
    },
    methods: {
      modifyAccountSupport() {
        this.$http.get('/api/system/config/modify-account-supported').then(res => {
          this.modifyAccount = res.data
        })
      },
      getWhoAmI() {
        this.$http.get('/api/account').then(res => {
          if(res.status == 200) {
            this.personalInfoForm.email = res.data.email
            this.personalInfoForm.username = res.data.username
            this.personalInfoForm.fullName = res.data.fullName
            this.personalInfoForm.id = res.data.id
          }
        })
      },
      logOut() {
        this.$http.post('/api/logout',{username:this.personalInfoForm.username}).then(res => {
          this.$router.push('/login')
        })
      },
      changeInfo() {
        this.$http.post('/api/account',this.personalInfoForm).then(res => {
          if (res.status == 200) {
            this.$message.success('Save successfully')
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
                id:this.personalInfoForm.id,
                oldPassword:this.passwordForm.oldPassword,
                newPassword:this.passwordForm.newPassword}).then(res => {
                if(res.status == 200) {
                  this.$message.success('Change successfully')
                  this.changePasswordVisible = false
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
  .category {
    display: flex;
    justify-content: center;
    font-size: 24px;
    font-family: BarlowMedium;
    font-weight: bold;
    margin-bottom: 24px;
  }
  .divide-line {
    height: 1px;
    background-color: #CED3D9;
    margin: 24px 0px;
  }
  .info-password {
    margin: auto;
    width: 432px;
    > span {
      margin: 10px 0px;
      font-size: 18px;
      color: #172B4D;
      font-width: bold;
    }
  }
  .el-button {
   border: 0px;
  }
  .el-button--info.username {
    margin-top: 12px;
    background-color: #78BED3;
  }
  .el-button--info.password {
    background-color: #78BED3;
  }
  .el-form {
    margin: 10px 0px;
  }
  .password {
    margin-top: 16px;
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
</style>