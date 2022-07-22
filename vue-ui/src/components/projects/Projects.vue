<template>
  <div style="padding: 24px !important;">
    <div class="category">Projects</div>
    <!--header area-->
    <el-row :gutter="20">
      <el-col :span="20">
        <el-input @input="getProjectList" placeholder="Search your project here" prefix-icon="el-icon-search" v-model="keyword">
        </el-input>
      </el-col>
      <el-col :span="4">
        <div class="creProj BarlowMedium" @click="createProjectShow" style="cursor: pointer"><i class="el-icon-plus"></i><span>Create project</span></div>
      </el-col>
    </el-row>
    <!--main tabs-->
    <div style="margin-top: 10px">
      <el-tabs class="BarlowMedium" v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="All" name="first">
          <el-row :gutter="20" style="margin-top: 6px">
            <el-col v-for="(item,index) in projectList" :span="6">
              <el-card class="box-card" @click.native="projectPage(item)">
                <div slot="header" class="boxCardHeader">
                  <div class="owner oneLine" v-if="item.userOwner">{{item.userOwner.username}}</div>
                  <div class="owner oneLine" v-else-if="item.groupOwner">{{item.groupOwner.name}}</div>
                  <div class="projName oneLine BarlowBold">{{item.name}}</div>
                  <span>Edited at {{dateFormat(item.lastEditedTime)}}</span>
                </div>
                <el-tooltip class="item" effect="dark" :content="item.description" placement="top">
                  <div class="description oneLine"><span class="oneLine">{{item.description}}</span></div>
                </el-tooltip>
                <!--<div class="progressLabel">6/18</div>
                <el-progress :percentage="6/18*100" color="#78BED3" :show-text="false"></el-progress>-->
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="Created by me" name="second">
          <el-row :gutter="20" style="margin-top: 6px">
            <el-col v-for="(item,index) in myProjectList" :span="6">
              <el-card class="box-card" @click.native="projectPage(item)">
                <div slot="header" class="boxCardHeader">
                  <div class="owner oneLine" v-if="item.userOwner">{{item.userOwner.username}}</div>
                  <div class="owner oneLine" v-else-if="item.groupOwner">{{item.groupOwner.name}}</div>
                  <div class="projName oneLine BarlowBold">{{item.name}}</div>
                  <span>Edited at {{dateFormat(item.lastEditedTime)}}</span>
                </div>
                <el-tooltip class="item" effect="dark" :content="item.description" placement="top">
                  <div class="description oneLine"><span class="oneLine">{{item.description}}</span></div>
                </el-tooltip>
                <!--<div class="progressLabel">13/18</div>
                <el-progress :percentage="13/18*100" color="#78BED3" :show-text="false"></el-progress>-->
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
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
    </div>
    <!--create project-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="createProjectVisible" width="480px" append-to-body>
      <template slot="title"><span class="dialogTitle">New assessment project</span></template>
      <!--create project form-->
      <el-form :rules="projectFormRules" ref="projectFormRefs" label-position="top" label="450px" :model="projectForm">
        <el-form-item class="BarlowMedium" label="Project name" prop="name">
          <el-input placeholder="Please input a project name" v-model="projectForm.name"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Project description" prop="description">
          <el-input type="textarea" :rows="3" placeholder="Please input project description" v-model="projectForm.description"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Business scenario" prop="businessScenario">
          <el-select v-model="projectForm.businessScenario" placeholder="Please choose a business scenario">
            <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Questionnaire template" prop="questionnaireTemplateId">
          <el-select v-model="projectForm.questionnaireTemplateId" placeholder="Please choose a questionnaire template">
            <el-option v-for="item in createTemplateList" :key="item.templateId" :label="item.name" :value="item.templateId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Owner" prop="ownerType">
          <el-select v-model="projectForm.ownerType" placeholder="Please choose a owner">
            <el-option-group
                    v-for="group in ownerTypeList"
                    :key="group.label"
                    :label="group.label">
              <el-option
                      v-for="item in group.options"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createProjectVisible = false" class="BlackBorder BarlowMedium">Cancel</el-button>
        <el-button type="primary" @click="createProject" class="GreenBC BarlowMedium">Create</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "Projects",
    data() {
      return {
        activeName: 'first',
        createProjectVisible: false,
        keyword: '',
        projectForm: {
          name: '',
          description: '',
          businessScenario: '',
          ownerType: '',
          questionnaireTemplateId: '',
        },
        projectFormRules: {
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
          businessScenario: [{ required: true, trigger: 'blur' },],
          questionnaireTemplateId: [{ required: true, message: 'questionnaireTemplate is required', trigger: 'blur' },],
          ownerType: [{ required: true, message: 'owner is required', trigger: 'blur' },],
        },
        groupList: [],
        businessScenarioList: [],
        createTemplateList: [],
        ownerTypeList: [],
        projectList: [],
        myProjectList: [],
        newProjectList: [],
        newMyProjectList: [],
        page: 1,
        pageSize: 12,
        total: 0,
      }
    },
    watch: {
      activeName:function () {
        this.page = 1
        this.keyword = ''
        this.getProjectList()
      }
    },
    created() {
      this.getProjectList()
    },
    methods: {
      handleSizeChange(val) {
        this.pageSize = val
        this.getProjectList()
      },
      handleCurrentChange(val) {
        this.page = val
        this.getProjectList()
      },
      createProjectShow() {
        this.getBusinessScenarioList()
        this.getCreateTemplateList()
        this.getGroupList()
        this.getWhoAmI()
        this.createProjectVisible = true
      },
      handleClick(tab, event) {
        this.activeName = tab.name
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
      getGroupList() {
        let val = {}
        this.ownerTypeList = []
        this.$http.get('/api/group/owned-by-me').then(res => {
          if (res.status == 200) {
            this.groupList = res.data
            val.label = 'Groups'
            val.options = res.data
            this.ownerTypeList.push(val)
          }
        })
      },
      // DateFormat
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
      getWhoAmI() {
        let val = {}
        let accountList = {}
        this.$http.get('/api/account').then(res => {
          if(res.status == 200) {
            val.label = 'Users'
            accountList.id = res.data.id+'+user'
            accountList.name = res.data.username
            val.options = []
            val.options.push(accountList)
            this.ownerTypeList.push(val)
          }
        })
      },
      createProject() {
        this.$refs.projectFormRefs.validate(val => {
          if (val) {
            let ownerType = this.projectForm.ownerType + ''
            if (ownerType.indexOf('+') === -1) {
              this.projectForm.userOwnerId = ''
              this.projectForm.groupOwnerId = ownerType
              this.$http.put('/api/project/new',this.projectForm).then(res => {
                if(res.status == 201) {
                  this.$message.success('Create successfully')
                  this.$refs.projectFormRefs.resetFields()
                  this.getProjectList()
                }
              })
            } else {
              this.projectForm.groupOwnerId = ''
              this.projectForm.userOwnerId = ownerType.split('+')[0]
              this.$http.put('/api/project/new',this.projectForm).then(res => {
                if(res.status == 201) {
                  this.$message.success('Create successfully')
                  this.$refs.projectFormRefs.resetFields()
                  this.getProjectList()
                }
              })
            }
            this.createProjectVisible = false
          }
        })
      },
      getProjectList() {
        if (this.activeName == 'first') {
          this.$http.get('/api/project',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
            if (res.status == 200) {
              this.projectList = res.data.records
              this.total = res.data.total
            }
          })
        } else if(this.activeName == 'second') {
          this.$http.get('/api/project/my-projects',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
            if (res.status == 200) {
              this.myProjectList = res.data.records
              this.total = res.data.total
            }
          })
        }
      },
      projectPage(item) {
        this.$router.push({path:'/projectPage',query: {id:item.id}})
      }
    }
  }
</script>

<style lang="less" scoped>
  .category {
    font-size: 24px;
    font-family: BarlowMedium;
    font-weight: bold;
    margin-bottom: 24px;
  }
  .creProj {
    height: 40px;
    background-color: #78BED3;
    border-radius: 4px;
    text-align: center;
    > span {
      color: #fff;
      font-size: 16px;
      line-height: 40px;
      margin-left: 10px;
    }
  }
  .el-icon-plus {
    color: #fff;
  }
  .boxCardHeader {
    > span {
      margin-top: 10px;
      font-size: 12px;
      color: #bbb;
    }
  }
  .projName {
    line-height: 22px;
    font-weight: bold;
    font-size: 16px;
    margin-bottom: 8px;

  }
  .el-progress {
    margin-top: 10px;
  }
  .progressLabel {
    color: #000;
    margin-top: 18px;
    font-size: 24px;
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
  .el-select .el-input__inner {
    width: 440px !important;
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
  .box-card {
    margin-top: 16px;
    position: relative;
    cursor: pointer;
  }
  .groupCategory {
    top: 0px;
    right: 0px;
    position: absolute;
    width: 43px;
    height: 18px;
    background-color: #FCB215;
    border-radius: 4px;
    text-align: center;
    > div {
      line-height: 18px;
      font-size: 12px;
      color: #000;
    }
  }
  .userCategory {
    top: 0px;
    right: 0px;
    position: absolute;
    width: 62px;
    height: 18px;
    background-color: #78BED3;
    border-radius: 4px;
    text-align: center;
    > div {
      line-height: 18px;
      font-size: 12px;
      color: #FFF;
    }
  }
  .owner {
    font-size: 14px;
    font-family: BarlowMedium;
    font-weight: bold;
    color: #175EC2;
    margin-bottom: 4px;
  }
</style>