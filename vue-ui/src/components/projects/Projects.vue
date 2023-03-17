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
        <div class="creProj BarlowMedium"
             style="cursor: pointer"
             @click="$refs.createProjectDialog.open()">
          <i class="el-icon-plus"></i>
          <span>Create project</span>
        </div>
      </el-col>
    </el-row>
    <!--main tabs-->
    <div style="margin-top: 10px">
      <div>
        <el-select @change="getProjectsByBusSce" clearable v-model="searchByBusinessScenario" placeholder="Business scenario" class="search-business">
          <el-option
              v-for="item in businessScenarioList"
              :key="item.code"
              :label="item.name"
              :value="item.code">
          </el-option>
        </el-select>
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
                    <div class="progress-text">{{ item.assessmentProgres.completed }}/{{ item.assessmentProgres.count }}</div>
                    <el-progress :percentage="item.assessmentProgres.completed / item.assessmentProgres.count*100" color="#78BED3" :show-text="false"></el-progress>
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
                    <div class="progress-text">{{ item.assessmentProgres.completed }}/{{ item.assessmentProgres.count }}</div>
                    <el-progress :percentage="item.assessmentProgres.completed / item.assessmentProgres.count*100" color="#78BED3" :show-text="false"></el-progress>
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
          <el-tab-pane label="Archived" name="third">
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
                    <div class="progress-text">{{ item.assessmentProgres.completed }}/{{ item.assessmentProgres.count }}</div>
                    <el-progress :percentage="item.assessmentProgres.completed / item.assessmentProgres.count*100" color="#78BED3" :show-text="false"></el-progress>
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
      </div>
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
    <create-project-dialog ref="createProjectDialog"
                           @created="onProjectCreated">
    </create-project-dialog>

  </div>
</template>

<script>
  import projectApi from "@/api/projectApi";
  import CreateProjectDialog from "@/components/projects/CreateProjectDialog";


  export default {
    name: "Projects",
    components: {
      CreateProjectDialog,
    },
    data() {
      return {
        selectExistingProject: '',
        activeName: 'first',
        activeNewProjectName: 'create',
        createProjectVisible: false,
        keyword: '',
        searchByBusinessScenario: '',
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
      this.getBusinessScenarioList()
    },
    methods: {
      onProjectCreated(newProject) {
        this.getProjectList();
        this.$router.push({
          path:'/projectPage',
          query: {
            id: newProject.id
          }
        })
      },
      querySearch(queryString, cb) {
        projectApi.fetchAllByKeyword(queryString).then(projects => {
          projects.map(project => {
            let owner_name;
            if (project['userOwner']) {
              owner_name = project['userOwner'].username;
            } else {
              owner_name = project['groupOwner'].name;
            }
            project.value = owner_name + " / " + project.name;
          })
          cb(projects)
        })
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
          this.$http.get('/api/project',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize,businessScenario:this.searchByBusinessScenario}}).then(res => {
            if (res.status == 200) {
              this.projectList = res.data.records
              this.total = res.data.total
            }
          })
        } else if(this.activeName == 'second') {
          this.$http.get('/api/project',{params: {'keyword': this.keyword,createdByMe:true,page:this.page,pageSize:this.pageSize,businessScenario:this.searchByBusinessScenario}}).then(res => {
            if (res.status == 200) {
              this.myProjectList = res.data.records
              this.total = res.data.total
            }
          })
        } else if(this.activeName == 'third') {
          this.$http.get('/api/project',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize,businessScenario:this.searchByBusinessScenario,archived:true}}).then(res => {
            if (res.status == 200) {
              this.projectList = res.data.records
              this.total = res.data.total
            }
          })
        }
      },
      getProjectsByBusSce() {
        if (this.activeName == 'first') {
          this.$http.get('/api/project',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize,businessScenario:this.searchByBusinessScenario}}).then(res => {
            if (res.status == 200) {
              this.projectList = res.data.records
              this.total = res.data.total
            }
          })
        } else if(this.activeName == 'second') {
          this.$http.get('/api/project',{params: {'keyword': this.keyword,createdByMe:true,page:this.page,pageSize:this.pageSize,businessScenario:this.searchByBusinessScenario}}).then(res => {
            if (res.status == 200) {
              this.myProjectList = res.data.records
              this.total = res.data.total
            }
          })
        } else if(this.activeName == 'third') {
          this.$http.get('/api/project',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize,businessScenario:this.searchByBusinessScenario,archived:true}}).then(res => {
            if (res.status == 200) {
              this.projectList = res.data.records
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
  .el-tabs {
    position: relative;
  }
</style>