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
          <el-row :gutter="20" style="margin-top: 6px;margin-bottom: 6px">
            <el-col v-for="(item,index) in projectList" :span="6">
              <el-card class="box-card" @click.native="projectPage(item)">
                <div slot="header" class="boxCardHeader">
                  <div class="owner oneLine" v-if="item.userOwner">{{item.userOwner.username}}</div>
                  <div class="owner oneLine" v-else-if="item.groupOwner">{{item.groupOwner.name}}</div>
                  <div class="projName oneLine BarlowBold">{{item.name}}</div>
                  <span>Edited on {{dateFormat(item.lastEditedTime)}}</span>
                </div>
                <div style="padding: 20px">
                  <div class="progress-text">6/50</div>
                  <el-progress :percentage="6/50*100" color="#78BED3" :show-text="false"></el-progress>
                  <!--                <el-tooltip class="item" effect="dark" :content="item.description" placement="top">-->
                  <!--                  <div class="description oneLine"><span class="oneLine">{{item.description}}</span></div>-->
                  <!--                </el-tooltip>-->
                  <!--<div class="progressLabel">6/18</div>
                  <el-progress :percentage="6/18*100" color="#78BED3" :show-text="false"></el-progress>-->
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="Created by me" name="second">
          <el-row :gutter="20" style="margin-top: 6px;margin-bottom: 6px">
            <el-col v-for="(item,index) in myProjectList" :span="6">
              <el-card class="box-card" @click.native="projectPage(item)">
                <div slot="header" class="boxCardHeader">
                  <div class="owner oneLine" v-if="item.userOwner">{{item.userOwner.username}}</div>
                  <div class="owner oneLine" v-else-if="item.groupOwner">{{item.groupOwner.name}}</div>
                  <div class="projName oneLine BarlowBold">{{item.name}}</div>
                  <span>Edited on {{dateFormat(item.lastEditedTime)}}</span>
                </div>
                <div style="padding: 20px">
                  <div class="progress-text">6/50</div>
                  <el-progress :percentage="6/50*100" color="#78BED3" :show-text="false"></el-progress>
                  <!--                <el-tooltip class="item" effect="dark" :content="item.description" placement="top">-->
                  <!--                  <div class="description oneLine"><span class="oneLine">{{item.description}}</span></div>-->
                  <!--                </el-tooltip>-->
                  <!--<div class="progressLabel">13/18</div>
                  <el-progress :percentage="13/18*100" color="#78BED3" :show-text="false"></el-progress>-->
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
      <div class="block">
        <el-pagination
          hide-on-single-page
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
      <template slot="title"><span class="dialogTitle">Create project</span></template>
      <!--tabs-->
      <el-tabs v-model="activeNewProjectName" @tab-click="handleClickProject">
        <el-tab-pane label="New project" name="create">
          <!--create project form-->
          <el-form class="createProject" :rules="projectFormRules" ref="projectFormRefs" label-position="top" label="450px" :model="projectForm">
            <el-form-item class="BarlowMedium" label="Project name" prop="name">
              <el-input placeholder="Please input a project name" v-model="projectForm.name"></el-input>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Project description" prop="description">
              <el-input type="textarea" :rows="3" placeholder="Please input project description" v-model="projectForm.description"></el-input>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Business scenario" prop="businessScenario">
              <el-select clearable v-model="projectForm.businessScenario" placeholder="Please choose a business scenario">
                <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name" :value="item.code"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Questionnaire template" prop="questionnaireTemplateId" v-show="projectForm.businessScenario || projectForm.businessScenario===0">
              <el-select clearable v-model="projectForm.questionnaireTemplateId" placeholder="Please choose a questionnaire template">
                <el-option v-for="item in createTemplateList" :key="item.templateId" :label="item.name" :value="item.templateId"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Assess Principle" prop="principleGeneric">
              <el-checkbox disabled v-model="projectForm.principleGeneric">Generic</el-checkbox>
              <el-checkbox style="margin-left: 8px" v-model="projectForm.principleFairness">Fairness</el-checkbox>
              <el-checkbox style="margin-left: 8px" v-model="projectForm.principleEA">Ethics & Accountability</el-checkbox>
              <el-checkbox style="margin-left: 8px" v-model="projectForm.principleTransparency">Transparency</el-checkbox>
            </el-form-item>
            <el-form-item style="margin-top: -6px" class="BarlowMedium" label="Owner" prop="ownerType">
              <el-select clearable v-model="projectForm.ownerType" placeholder="Please choose a owner">
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
        </el-tab-pane>
        <el-tab-pane label="Create from existing project" name="copy">
          <!--Create from existing project-->
          <div class="form-label">Existing project</div>
          <el-autocomplete
              class="inline-input"
              clearable
              v-model="selectExistingProject"
              :fetch-suggestions="querySearch"
              placeholder="Please input a existing project name"
              @select="handleSelect">
              <i class="el-icon-search el-input__icon" slot="suffix"></i>
          </el-autocomplete>
          <el-form v-show="createExistFlag" class="createProject" :rules="existingProjectFormRules" ref="existingProjectFormRefs" label-position="top" label="450px" :model="existingProjectForm">
            <el-form-item class="BarlowMedium" label="Business scenario" prop="businessScenario">
              <el-select disabled v-model="existingProjectForm.businessScenario" placeholder="Please choose a business scenario">
                <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name" :value="item.code"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Questionnaire template" prop="questionnaireTemplateId" v-show="existingProjectForm.businessScenario || existingProjectForm.businessScenario===0">
              <el-select clearable v-model="existingProjectForm.questionnaireTemplateId" placeholder="Please choose a questionnaire template">
                <el-option v-for="item in createTemplateList" :key="item.templateId" :label="item.name" :value="item.templateId"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Assess Principle" prop="principleGeneric">
              <el-checkbox disabled v-model="existingProjectForm.principleGeneric">Generic</el-checkbox>
              <el-checkbox style="margin-left: 8px" v-model="existingProjectForm.principleFairness">Fairness</el-checkbox>
              <el-checkbox style="margin-left: 8px" v-model="existingProjectForm.principleEA">Ethics & Accountability</el-checkbox>
              <el-checkbox style="margin-left: 8px" v-model="existingProjectForm.principleTransparency">Transparency</el-checkbox>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Project name" prop="name">
              <el-input placeholder="Please input a project name" v-model="existingProjectForm.name"></el-input>
            </el-form-item>
            <el-form-item class="BarlowMedium" label="Project description" prop="description">
              <el-input type="textarea" :rows="3" placeholder="Please input project description" v-model="existingProjectForm.description"></el-input>
            </el-form-item>

            <el-form-item class="BarlowMedium" label="Owner" prop="ownerType">
              <el-select v-model="existingProjectForm.ownerType" placeholder="Please choose a owner">
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
        </el-tab-pane>
      </el-tabs>
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
        selectExistingProject: '',
        activeName: 'first',
        activeNewProjectName: 'create',
        createProjectVisible: false,
        keyword: '',
        projectForm: {
          name: '',
          description: '',
          businessScenario: '',
          principleGeneric: true,
          principleFairness: false,
          principleEA: false,
          principleTransparency: false,
          ownerType: '',
          questionnaireTemplateId: '',
        },
        existingProjectForm: {
          businessScenario: '',
          principleGeneric: true,
          principleFairness: false,
          principleEA: false,
          principleTransparency: false,
          questionnaireTemplateId: '',
          name: '',
          description: '',
          ownerType: '',
        },
        projectFormRules: {
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
          businessScenario: [{ required: true, trigger: 'blur' },],
          questionnaireTemplateId: [{ required: true, message: 'questionnaireTemplate is required', trigger: 'blur' },],
          principleGeneric: [{ required: true, message: 'principle is required', trigger: 'blur' },],
          ownerType: [{ required: true, message: 'owner is required', trigger: 'blur' },],
        },
        existingProjectFormRules: {
          businessScenario: [{ required: true, trigger: 'blur' },],
          principleGeneric: [{ required: true, trigger: 'blur' },],
          questionnaireTemplateId: [{ required: true, message: 'questionnaireTemplate is required', trigger: 'blur' },],
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
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
        createExistFlag: false,
      }
    },
    watch: {
      activeName:function () {
        this.page = 1
        this.keyword = ''
        this.getProjectList()
      },
      'projectForm.businessScenario':function () {
        this.getCreateTemplateList()
      },
      'selectExistingProject': function() {
        if (!this.selectExistingProject) {
          this.createExistFlag = false
        }
      },
    },
    created() {
      this.getProjectList()
      sessionStorage.setItem('projectId', null)
      this.resetSetItem('projectId', null);
    },
    methods: {
      querySearch(queryString, cb) {
        let projects = []
        this.projectList.map(item => {
          item.value = item.name
          projects.push(item)
        })
        cb(projects);
      },
      handleSelect(item) {
        if (item) {
          this.createExistFlag = true
        }
        this.$http.get(`/api/project/${item.id}`).then(res => {
          if (res.status == 200) {
            let projectInfo = res.data
            console.log(projectInfo.businessScenario)
            this.existingProjectForm.businessScenario = projectInfo.businessScenario
            this.existingProjectForm.principleGeneric = projectInfo.principleGeneric
            this.existingProjectForm.principleFairness = projectInfo.principleFairness
            this.existingProjectForm.principleEA = projectInfo.principleEA
            this.existingProjectForm.principleTransparency = projectInfo.principleTransparency
            this.existingProjectForm.name = projectInfo.name
            this.existingProjectForm.description = projectInfo.description
            this.$http.get('/api/system/questionnaire_template',{params:{'businessScenario':projectInfo.businessScenario}}).then(res => {
              if(res.status == 200) {
                this.createTemplateList = res.data
              }
            })
          }
        })
        // this.selectExistingProject = item.id
        console.log(item);
      },
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
      handleClickProject(tab, event) {
        this.activeNewProjectName = tab.name
      },
      getBusinessScenarioList() {
        this.$http.get('/api/system/business_scenario').then(res => {
          if(res.status == 200) {
            this.businessScenarioList = res.data
          }
        })
      },
      getCreateTemplateList() {
        this.$http.get('/api/system/questionnaire_template',{params:{'businessScenario':this.projectForm.businessScenario}}).then(res => {
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
        if (this.activeNewProjectName == 'create') {
          this.$refs.projectFormRefs.validate(val => {
            if (val) {
              let ownerType = this.projectForm.ownerType + ''
              if (ownerType.indexOf('+') === -1) {
                this.projectForm.userOwnerId = ''
                this.projectForm.groupOwnerId = ownerType
                this.$http.post('/api/project/new',this.projectForm).then(res => {
                  if(res.status == 201) {
                    this.$message.success('Create successfully')
                    this.$refs.projectFormRefs.resetFields()
                    this.getProjectList()
                  }
                })
              } else {
                this.projectForm.groupOwnerId = ''
                this.projectForm.userOwnerId = ownerType.split('+')[0]
                this.$http.post('/api/project/new',this.projectForm).then(res => {
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
        } else if (this.activeNewProjectName == 'copy') {
          this.$refs.existingProjectFormRefs.validate(val => {
            if (val) {
              let ownerType = this.existingProjectForm.ownerType + ''
              if (ownerType.indexOf('+') === -1) {
                this.existingProjectForm.userOwnerId = ''
                this.existingProjectForm.groupOwnerId = ownerType
                this.$http.post('/api/project/new',this.existingProjectForm).then(res => {
                  if(res.status == 201) {
                    this.$message.success('Create successfully')
                    this.$refs.existingProjectFormRefs.resetFields()
                    this.getProjectList()
                  }
                })
              } else {
                this.existingProjectForm.groupOwnerId = ''
                this.existingProjectForm.userOwnerId = ownerType.split('+')[0]
                this.$http.post('/api/project/new',this.existingProjectForm).then(res => {
                  if(res.status == 201) {
                    this.$message.success('Create successfully')
                    this.$refs.existingProjectFormRefs.resetFields()
                    this.getProjectList()
                  }
                })
              }
              this.createProjectVisible = false
            }
          })
        }

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
  .progress-text {
    margin-top: -10px;
    font-size: 24px;
    font-weight: bold;
    font-family: BarlowBold;
  }
  .form-label {
    line-height: 40px;
    float: none;
    font-size: 14px;
    color: #aaa;
    display: flex !important;
    text-align: left;
    height: 32px;
    margin-top: 4px !important;
    padding: 0 !important;
  }
  .generic {
    background: #78BED3;
    font-size: 16px;
    line-height: 16px !important;
    border-radius: 20px;
    opacity: 0.65;
    padding: 0px 12px;
  }
</style>