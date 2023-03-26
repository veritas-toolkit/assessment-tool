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
<!--
        <el-popover
            placement="top-start"
            width="400"
            trigger="click">
          <Notifications v-if="projectId==null" @getNotLen="getNotLen"></Notifications>
          <ProjectNotifications v-if="projectId > 0" @getProjNotLen="getProjNotLen" :projectId="projectId"></ProjectNotifications>
          <div class="not-box" slot="reference">
            <img src="../assets/projectPic/notification.png" alt="">
            <span class="BarlowBold">Notifications</span>
            <div v-if="projectId==null">{{notLen}}</div>
            <div v-if="projectId!=null">{{projNotLen}}</div>
          </div>
        </el-popover>
-->
        <Notification ref="notification" :project-id="projectId" :force-show-question="true"/>
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
        <el-button type="info" class="GreenBC" @click="changePassword" @keyup.enter.native="changePassword">Save</el-button>
      </span>
    </el-dialog>
    <!--user wizardPic-->
    <el-dialog
        id="wizard"
      class="BarlowMedium"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      :visible.sync="userWizardShow"
      width="560px"
      append-to-body>
      <!--<template slot="title"><span class="dialogTitle BarlowBold">User Guide</span></template>-->
      <!--tour wizard-->
      <div v-show="activeStep==0">
        <img class="tour" src="../assets/wizardPic/tour1.png" alt="">
        <div class="tour-title">Create your project</div>
        <div class="tour-text">Create a project to assess fairness, ethics, accountability and transparency principles.</div>
      </div>
      <div v-show="activeStep==1">
        <img class="tour" src="../assets/wizardPic/tour2.png" alt="">
        <div class="tour-title">Add model artifact</div>
        <div class="tour-text">Upload json file as model artifact.</div>
      </div>
      <div v-show="activeStep==2">
        <img class="tour" src="../assets/wizardPic/tour3.png" alt="">
        <div class="tour-title">Answer assessment questions</div>
        <div class="tour-text">Upload charts, create table and answer questions set by the template.</div>
      </div>
      <div v-show="activeStep==3">
        <img class="tour" src="../assets/wizardPic/tour4.png" alt="">
        <div class="tour-title">Compare with history versions</div>
        <div class="tour-text">Compare the difference with any history version or recent drafts.</div>
      </div>
      <div v-show="activeStep==4">
        <img class="tour" src="../assets/wizardPic/tour5.png" alt="">
        <div class="tour-title">Export as PDF file</div>
        <div class="tour-text">Preview and export the questionnaire as a PDF assessment report.</div>
      </div>
      <el-steps :active=activeStep simple>
        <el-step></el-step>
        <el-step></el-step>
        <el-step></el-step>
        <el-step></el-step>
        <el-step></el-step>
      </el-steps>
      <div style="display: flex;justify-content: space-between">
        <span slot="footer" class="dialog-footer">
        <el-button type="info" class="GreyBC" style="color: #000000" @click="userWizardShow=false">Skip</el-button>
      </span>
        <span slot="footer" class="dialog-footer">
        <el-button type="info" class="GreenBC" @click="nextStep">Next</el-button>
      </span>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Notification from "@/components/comment/Notification.vue";

export default {
  name: 'Home',
  components: {
    Notification,
  },
  data() {
    return {
      activeStep: 0,
      activeName: '',
      accountInfo: {},
      admin: false,
      shouldChangePassword: false,
      finishedUserGuide: true,
      userWizardShow: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        passwordConfirm: '',
      },
      notLen: '',
      projectId: '',
      projNotLen: '',
    }
  },
  watch: {

  },
  created() {
    // this.projectId = JSON.parse(window.sessionStorage.getItem("projectId"));
    window.addEventListener('setItem', ()=> {
      this.projectId= JSON.parse(sessionStorage.getItem('projectId'));
    })
    this.getWhoAmI()
    this.activeName = window.location.href.split('/')[window.location.href.split('/').length - 1]
  },
  methods: {
    getNotLen(item) {
      this.notLen = item
    },
    getProjNotLen(item) {
      this.projNotLen = item
    },
    nextStep() {
      this.activeStep++
      if (this.activeStep == 5) this.userWizardShow = false;
    },
    getWhoAmI() {
      this.$http.get('/api/account').then(res => {
        if(res.status == 200) {
          this.accountInfo = res.data
          this.admin = res.data.admin
          this.shouldChangePassword = res.data.shouldChangePassword
          this.finishedUserGuide = res.data.finishedUserGuide
        }
      })
    },
    changePasswordClosed() {
      this.$refs.passwordFormRefs.resetFields()
    },
    userWizardFunction() {
      this.userWizardShow = !this.finishedUserGuide
      this.$http.post('/api/account/finish_user_guide').then(res => {
        if(res.status == 200) {
          console.log(res)
        }
      })
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
                setTimeout(this.userWizardFunction, 1000);
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
    border: 1px solid red;
    display: flex;
    align-items: center;
    margin-left: 20px;
    margin-bottom: 10px;
    > div {
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
  .tour {
    width: 560px;
    height: 290px;
    margin: -30px 0 0 -20px;
    border-radius: 3px;
  }
  .tour-title {
    font-size: 20px;
    font-weight: bold;
    font-family: BarlowBold;
    text-align: center;
    margin-top: 14px;
  }
  .tour-text {
    height: 48px;
    margin: auto;
    font-size: 16px;
    font-family: BarlowMedium;
    margin-top: 10px;
    width: 344px;
    text-align: center;
    word-break: break-word;
  }
  .el-steps {
    margin-top: 20px;
  }
  .popover-title {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
  }
  .not-style {
    font-size: 16px;
    font-weight: bold;
    font-family: BarlowBold;
  }
  .mark-style {
    font-size: 14px;
    font-family: BarlowMedium;
    color: #175EC2;
  }
  .divide-line {
    height: 1px;
    width: calc(100% + 24px);
    background-color: #CED3D9;
    margin-left: -12px;
  }
  .not-box {
    display: flex;
    align-items: center;
    line-height: 64px;
    margin-left: 16px;
    >img {
      width: 24px;
      height: 24px;
    }
    >span {
      margin-left: 8px;
      font-size: 16px;
      color: #333333;
    }
    >div {
      margin-left: 8px;
      text-align: center;
      width: 24px;
      line-height: 24px;
      background-color: #FCB215;
      border-radius: 12px;
      color: #FFFFFF;
    }
  }
</style>
