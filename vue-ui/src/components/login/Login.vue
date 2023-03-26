<template>
  <el-container>
    <!--head-->
    <el-header>
      <!--avatar-->
      <div class="avatar_box">
        <img src="../../assets/homePic/logo.png" alt="">
        <span class="BarlowBold">Veritas Assessment Tool</span>
      </div>
    </el-header>
    <el-main>
      <!--login-->
      <el-card style="padding: 20px" v-show="logIn" shadow="always">
        <div class="cardTitle BarlowBold">Sign in to Veritas Assessment Tool</div>
        <!--login form-->
        <el-form :rules="loginFormRules" ref="loginFormRefs" label-position="top" label="450px" :model="loginForm">
          <el-form-item class="login BarlowMedium" label="Username or email" prop="username">
            <el-input v-model="loginForm.username" prefix-icon="el-icon-s-custom"
                      @keyup.enter.native="signIn">
            </el-input>
          </el-form-item>
          <el-form-item class="login BarlowMedium" label="Password" prop="password">
            <el-input v-model="loginForm.password" type="password" prefix-icon="el-icon-s-cooperation"
                      @keyup.enter.native="signIn">
            </el-input>
          </el-form-item>
        </el-form>
        <!--checkbox&forget password-->
        <div class="checkbox_password BarlowMedium">
          <!--<el-checkbox v-model="checked">Remember me</el-checkbox>
          <el-link type="primary">Forgot your password?</el-link>-->
        </div>
        <div class="signIn BarlowMedium" style="cursor: pointer" @click="signIn"><span>Sign in</span></div>
        <!--or-->
        <div class="orStyle BarlowMedium">
          <div></div>
          <span>or</span>
          <div></div>
        </div>
        <div v-show="registerAble" class="noneAccount BarlowMedium">Don't have account yet?</div>
        <div v-show="!registerAble" class="noneAccount BarlowMedium">Please contact the administrator to create</div>
        <div v-show="registerAble" class="signUp BarlowMedium" style="cursor: pointer" @click="signCreateTrans"><span>Create your account</span></div>
      </el-card>
      <!--create account-->
      <el-card style="padding: 20px" v-show="createAccount" shadow="always">
        <div class="cardTitle BarlowBold">Create your account</div>
        <!--login form-->
        <el-form :rules="createFormRules" ref="createFormRefs" label-position="top" label="450px" :model="createForm">
          <el-form-item class="login BarlowMedium" label="Email Address" prop="email">
            <el-input v-model="createForm.email"></el-input>
          </el-form-item>
          <el-form-item class="login BarlowMedium" label="Username" prop="username">
            <el-input v-model="createForm.username"></el-input>
          </el-form-item>
          <el-form-item class="login BarlowMedium" label="Fullname" prop="fullName">
            <el-input v-model="createForm.fullName"></el-input>
          </el-form-item>
          <el-form-item class="login BarlowMedium" label="Password" prop="password">
            <el-input v-model="createForm.password" type="password"></el-input>
          </el-form-item>
          <el-form-item class="login BarlowMedium" label="Password confirmation" prop="passwordConfirm">
            <el-input v-model="createForm.passwordConfirm" type="password"></el-input>
          </el-form-item>
        </el-form>
        <div class="signIn BarlowMedium" style="cursor: pointer;margin-top: 20px" @click="signUp"><span>Create account</span></div>
        <!--or-->
        <div class="orStyle BarlowMedium">
          <div></div>
          <span>or</span>
          <div></div>
        </div>
        <div class="noneAccount BarlowMedium">Already had an account?</div>
        <div class="signUp BarlowMedium" style="cursor: pointer" @click="signCreateTrans"><span>Sign in to existing account</span></div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
  export default {
    name: "Login",
    data() {
      return {
        loginForm: {
          username: '',
          password: '',
        },
        createForm: {
          email: '',
          username: '',
          fullName: '',
          password: '',
          passwordConfirm: '',
        },
        loginFormRules: {
          username: [
            { required: true, trigger: 'blur' },
          ],
          password: [
            { required: true, trigger: 'blur' },
          ]
        },
        createFormRules: {
          email: [
            { required: true, trigger: 'blur' },
          ],
          username: [
            { required: true, trigger: 'blur' },
          ],
          fullName: [
            { required: true, trigger: 'blur' },
          ],
          password: [
            { required: true, trigger: 'blur' },
          ],
          passwordConfirm: [
            { required: true, trigger: 'blur' },
          ]
        },
        checked: false,
        logIn: true,
        createAccount: false,
        registerAble: ''
      }
    },
    created() {
      this.registerSupport()
    },
    methods: {
      registerSupport() {
        this.$http.get('/api/system/config/register-supported').then(res => {
          this.registerAble = res.data
        })
      },
      signIn() {
        this.$refs.loginFormRefs.validate(val => {
          if (val) {
            this.$http.post('/api/login',this.loginForm).then(res => {
              if (res.status === 200) {
                if (this.$route.query.redirect != null) {
                  this.$router.replace(this.$route.query.redirect)
                } else {
                  this.$router.push('/home')
                }
              }
            }).catch(err => {
              let message = err.response.data.message;
              if (!message) {
                  message = 'Login failed.'
              }
              this.$message.error(message)
            })
          }
        })
      },
      signUp() {
        this.$refs.createFormRefs.validate(val => {
          if (val) {
            /*judge whether the two password are consistent?*/
            if (this.createForm.password === this.createForm.passwordConfirm) {
              this.$http.put('/api/register',this.createForm).then(res => {
                if (res.status === 201) {
                  this.$message.success('Register successfully')
                }
              })
            } else {
              this.$message.error('The password you entered twice was inconsistent')
            }
          }
        })
      },
      signCreateTrans() {
        this.logIn = !this.logIn
        this.createAccount = !this.createAccount
      }
    }
  }
</script>

<style lang="less" scoped>
  .el-container{
    height: 100%;
  }
  .el-header {
    height: 64px !important;
    background-color: #78BED3;
    align-items: center;
    display: flex;
  }
  .el-main {
    display: flex;
    text-align: center;
    background-color: #F7F7F7;
  }
  .avatar_box {
    height: 60px;
    display: flex;
    align-items: center;
    > img {
      height: 60px;
      margin-left: 16px;
    }
    > span {
      margin-left: 16px;
      color: #FFF;
      font-size: 20px;
      font-weight: 500;
    }
  }
  .el-card {
    width: 480px;
    padding: 4px;
    margin: auto;
  }
  .cardTitle {
    margin-bottom: 16px;
    font-size: 20px;
    font-weight: bold;
  }
  .checkbox_password {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
  }
  .signIn {
    height: 40px;
    background-color: #78BED3;
    border-radius: 4px;
    margin-top: 10px;
    line-height: 40px;
    text-align: center;
    > span {
      color: #FFF;
      font-size: 18px;
    }
  }
  .orStyle {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 0px;
    > div {
      margin-top: 3px;
      background-color: #E5E7E8;
      height: 2px;
      width: 47%;
    }
    > span {
      font-size: 14px;
      color: #333333;
    }
  }
  .noneAccount {
    font-size: 14px;
    color: #333333;
  }
  .signUp {
    height: 40px;
    border: 1px solid rgba(0,0,0,0.85);
    border-radius: 4px;
    margin-top: 10px;
    line-height: 40px;
    text-align: center;
    > span {
      color: #42526E;
      font-size: 18px;
    }
  }
</style>