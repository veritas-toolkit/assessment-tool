<template>
  <div style="height: 100%">
    <div style="height: calc(100% - 73px)">
      <div style="padding: 32px 64px !important">
        <div class="title BarlowBold">
          <router-link :to="{ path: '/administration',query: {activeName: 'third'} }"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
          <span>User</span>
        </div>
        <div style="margin-left: 48px">
          <div class="title-box BarlowBold">
            <span>Create user</span>
          </div>
          <div class="add-text BarlowMedium" v-show="addUserList.length">
            <div style="width: 15%">Username</div>
            <div style="width: 22%">Full name</div>
            <div style="width: 22%">Email</div>
            <div style="width: 15%">Password</div>
            <div style="width: 8%">Project limit</div>
            <div style="width: 8%">Group limit</div>
            <div style="width: 16px"></div>
          </div>
          <div class="add-user" v-for="(item,index) in addUserList">
            <el-input style="width: 15%;" v-model="item.username"></el-input>
            <el-input style="width: 22%;" v-model="item.fullName"></el-input>
            <el-input style="width: 22%;" v-model="item.email"></el-input>
            <el-input type="password" style="width: 15%;" v-model="item.password"></el-input>
            <el-input style="width: 8%;" v-model="item.projectLimited"></el-input>
            <el-input style="width: 8%;" v-model="item.groupLimited"></el-input>
            <div :key="index" style="cursor: pointer" @click="deleteAddUser(index)"><i class="el-icon-delete"></i></div>
          </div>
          <div class="editDiv" @click="addUser">Add user</div>
        </div>
      </div>
    </div>
    <div style="height: 72px">
      <div class="footer BarlowMedium">
        <div class="defaultValue" @click="setDefaultValueVisible = true">
          <img src="../../assets/groupPic/setting.png" class="menuIcon">
          <div>Configure default value</div>
        </div>
        <div class="GreenBC createUsers" @click="createMultipleUsers">Create multiple users</div>
      </div>
    </div>
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="setDefaultValueVisible" width="548px" @close="" append-to-body>
      <template slot="title"><span class="dialogTitle">Set default user info</span></template>
      <el-form :rules="defaultUserInfoFormRules" ref="defaultUserInfoRefs" label-position="top" label="450px" :model="defaultUserInfoForm">
        <el-form-item class="BarlowMedium" label="Email extension" prop="email">
          <el-input placeholder="Please input a email" v-model="defaultUserInfoForm.email"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Project account limit" prop="projectLimit">
          <el-input placeholder="Please input group description here" v-model="defaultUserInfoForm.projectLimit"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Group account limit" prop="accountLimit">
          <el-input placeholder="Please input group description here" v-model="defaultUserInfoForm.accountLimit"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Password" prop="password">
          <el-input type="password" placeholder="Please input group description here" v-model="defaultUserInfoForm.password"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="setDefaultValueVisible = false" class="BlackBorder BarlowMedium">Cancel</el-button>
        <el-button type="primary" @click="setDefaultValue" class="GreenBC BarlowMedium">Save</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "CreateUser",
    data() {
      return {
        addUserList: [],
        setDefaultValueVisible: false,
        defaultUserInfoForm: {
          email: '',
          projectLimit: '',
          accountLimit: '',
          password: '',
        },
        defaultUserInfoFormRules: {
          email: [{ required: true, trigger: 'blur' },],
          projectLimit: [{ required: true, trigger: 'blur' },],
          accountLimit: [{ required: true, trigger: 'blur' },],
          password: [{ required: true, trigger: 'blur' },],
        },
      }
    },
    created() {
      this.getDefaultValue()
    },
    methods: {
      addUser() {
        this.addUserList.push({
          username:'',
          fullName:'',
          email:this.defaultUserInfoForm.email,
          password:this.defaultUserInfoForm.password,
          projectLimited:this.defaultUserInfoForm.projectLimit,
          groupLimited:this.defaultUserInfoForm.accountLimit})
      },
      deleteAddUser(index) {
        this.addUserList.splice(index,1)
      },
      createMultipleUsers() {
        for (let i = 0;i< this.addUserList.length;i++) {
          if (this.addUserList[i].username == '' || this.addUserList[i].fullName == '' || this.addUserList[i].email.split('@')[0] == '') {
            this.$message.warning("Username,Full name and Email can't be empty")
            return
          }
        }
        this.$http.put(`/api/admin/user/new`,this.addUserList).then(res => {
          if (res.status == 201) {
            this.addUserList = []
            this.$message.success('Create successfully')
            this.$router.push({path:'/administration',query: {activeName: 'third'}})
          }
        })
      },
      getDefaultValue() {
        this.$http.get('api/admin/system/user-default-property').then(res => {
          console.log(res)
          this.defaultUserInfoForm.email = res.data.default_email_suffix
          this.defaultUserInfoForm.projectLimit = res.data.default_limit_project
          this.defaultUserInfoForm.accountLimit = res.data.default_limit_group
          this.defaultUserInfoForm.password = res.data.default_user_password
        })
      },
      setDefaultValue() {
        this.$refs.defaultUserInfoRefs.validate(val => {
          if (val) {
            let default_email_suffix = this.defaultUserInfoForm.email
            let default_limit_project = this.defaultUserInfoForm.projectLimit
            let default_limit_group = this.defaultUserInfoForm.accountLimit
            let default_user_password = this.defaultUserInfoForm.password
            this.$http.post('api/admin/system/config',{
              default_email_suffix:default_email_suffix,
              default_limit_project:default_limit_project,
              default_limit_group:default_limit_group,
              default_user_password:default_user_password,
            }).then(res => {
              this.$message.success('Set successfully')
              this.getDefaultValue()
              this.setDefaultValueVisible = false
            })
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
      > img {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
      }
    }
    > span {
      margin-left: 16px;
      font-size: 16px;
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
  .add-text {
    display: flex;
    margin-top: 24px;
    margin-bottom: 10px;
    justify-content: space-between;
    font-size: 14px;
  }
  .add-user {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
  }
  .editDiv {
    cursor: pointer;
    margin-top: 16px;
    border: 1px solid;
    border-radius: 4px;
    font-size: 16px;
    padding: 8px 0;
    text-align: center;
  }
  .footer {
    border-top: 1px solid #CED3D9;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%
  }
  .defaultValue {
    cursor: pointer;
    display: flex;
    margin-left: 26px;
    align-items: center;
    border: 1px solid;
    border-radius: 4px;
    padding: 8px 12px;
    >img {
      width: 24px;
      height: 24px;
    }
    > div {
      font-size: 16px;
      margin-left: 8px;
    }
  }
  .createUsers {
    cursor: pointer;
    margin-right: 26px;
    border-radius: 4px;
    padding: 8px 12px;
    color: #FFF;
    background-color: #78BED3;
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
</style>