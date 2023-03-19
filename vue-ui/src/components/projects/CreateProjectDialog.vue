<template>
  <!--create project-->
  <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="dialogVisible" width="480px"
             append-to-body>
    <template slot="title"><span class="dialogTitle">Create project</span></template>
    <!--tabs-->
    <el-tabs v-model="activeNewProjectName" @tab-click="handleClickProject">
      <el-tab-pane label="New project" name="create">
        <!--create project form-->
        <el-form class="createProject" :rules="projectFormRules" ref="projectFormRefs" label-position="top"
                 label="450px" :model="projectForm">
          <el-form-item class="BarlowMedium" label="Project name" prop="name">
            <el-input placeholder="Please input a project name" v-model="projectForm.name"></el-input>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Project description" prop="description">
            <el-input type="textarea" :rows="3" placeholder="Please input project description"
                      v-model="projectForm.description"></el-input>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Business scenario" prop="businessScenario">
            <el-select clearable v-model="projectForm.businessScenario" placeholder="Please choose a business scenario">
              <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name"
                         :value="item.code"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Questionnaire template" prop="questionnaireTemplateId"
                        disabled="projectForm.businessScenario">
            <el-select clearable v-model="projectForm.questionnaireTemplateId"
                       placeholder="Please choose a questionnaire template"
                       no-data-text="Please choose a business scenario first."
                       :disabled="createTemplateList === null || createTemplateList.length === 0">
              <el-option v-for="item in createTemplateList" :key="item.templateId" :label="item.name"
                         :value="item.templateId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Assess Principle" prop="principleGeneric">
            <el-checkbox disabled v-model="projectForm.principleGeneric">Generic</el-checkbox>
            <el-checkbox style="margin-left: 8px" v-model="projectForm.principleFairness">Fairness</el-checkbox>
            <el-checkbox style="margin-left: 8px" v-model="projectForm.principleEA">Ethics & Accountability
            </el-checkbox>
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
            :debounce=1
            placeholder="Please input a existing project name"
            @select="handleSelect">
          <i class="el-icon-search el-input__icon" slot="suffix"></i>
        </el-autocomplete>
        <el-form v-show="createExistFlag" class="createProject" :rules="existingProjectFormRules"
                 ref="existingProjectFormRefs" label-position="top" label="450px" :model="existingProjectForm">
          <el-form-item class="BarlowMedium" label="Business scenario" prop="businessScenario">
            <el-select disabled v-model="existingProjectForm.businessScenario"
                       placeholder="Please choose a business scenario">
              <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name"
                         :value="item.code"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Assess Principle" prop="principleGeneric">
            <el-checkbox disabled v-model="existingProjectForm.principleGeneric">Generic</el-checkbox>
            <el-checkbox style="margin-left: 8px" v-model="existingProjectForm.principleFairness">Fairness</el-checkbox>
            <el-checkbox style="margin-left: 8px" v-model="existingProjectForm.principleEA">Ethics & Accountability
            </el-checkbox>
            <el-checkbox style="margin-left: 8px" v-model="existingProjectForm.principleTransparency">Transparency
            </el-checkbox>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Project name" prop="name">
            <el-input placeholder="Please input a project name" v-model="existingProjectForm.name"></el-input>
          </el-form-item>
          <el-form-item class="BarlowMedium" label="Project description" prop="description">
            <el-input type="textarea" :rows="3" placeholder="Please input project description"
                      v-model="existingProjectForm.description"></el-input>
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
        <el-button @click="close" class="BlackBorder BarlowMedium">Cancel</el-button>
        <el-button type="primary" @click="createProject" class="GreenBC BarlowMedium">Create</el-button>
      </span>
  </el-dialog>
</template>
<script>

import projectApi from "@/api/projectApi";

export default {
  name: "CreateProjectDialog",
  props: {
    testId: {
      type: String,
      required: false
    },
    defaultUserOwnerId: {
      type: String,
      required: false
    },
    defaultGroupOwnerId: {
      type: String,
      required: false
    },
  },
  data() {
    return {
      dialogVisible: false,
      activeNewProjectName: 'create',
      businessScenarioList: [],
      createTemplateList: [],
      projectCreateDto: {
        name: '',
        description: '',
        userOwnerId: null,
        groupOwnerId: null,
        businessScenario: null,
        questionnaireTemplateId: null,
        copyFromProjectId: null,
        principleGeneric: true,
        principleFairness: false,
        principleEA: false,
        principleTransparency: false,
      },
      projectForm: {
        name: '',
        description: '',
        businessScenario: '',
        principleGeneric: true,
        principleFairness: false,
        principleEA: false,
        principleTransparency: false,
        ownerType: '',
        questionnaireTemplateId: null,
      },
      existingProjectForm: {
        copyFromProjectId: null,
        businessScenario: null,
        principleGeneric: true,
        principleFairness: false,
        principleEA: false,
        principleTransparency: false,
        questionnaireTemplateId: null,
        name: '',
        description: '',
        ownerType: '',
      },
      createExistFlag: false,
      selectExistingProject: '',
      existingProjectFormRules: {
        businessScenario: [{required: true, trigger: 'blur'},],
        principleGeneric: [{required: true, trigger: 'blur'},],
        questionnaireTemplateId: [{required: true, message: 'questionnaireTemplate is required', trigger: 'blur'},],
        name: [{required: true, trigger: 'blur'},],
        description: [{required: true, trigger: 'blur'},],
        ownerType: [{required: true, message: 'owner is required', trigger: 'blur'},],
      },
      projectFormRules: {
        name: [{required: true, trigger: 'blur'},],
        description: [{required: true, trigger: 'blur'},],
        businessScenario: [{required: true, trigger: 'blur'},],
        questionnaireTemplateId: [{required: true, message: 'questionnaireTemplate is required', trigger: 'blur'},],
        principleGeneric: [{required: true, message: 'principle is required', trigger: 'blur'},],
        ownerType: [{required: true, message: 'owner is required', trigger: 'blur'},],
      },
      ownerTypeList: [],
    }
  },
  watch: {
    'selectExistingProject': function () {
      if (!this.selectExistingProject) {
        this.createExistFlag = false
      }
    },
    'projectForm.businessScenario': function () {
      if (this.projectForm.businessScenario) {
        this.getCreateTemplateList()
      } else {
        this.createTemplateList = [];
      }
      this.projectForm.questionnaireTemplateId = null;
    },
  },
  methods: {
    open() {
      this.getWhoAmI();
      this.getBusinessScenarioList();
      this.getCreateTemplateList();
      this.getGroupList();
      this.dialogVisible = true;
    },
    async close() {
      // confirm/
      // clean
      this.dialogVisible = false;
    },
    handleClickProject(tab, event) {
      this.activeNewProjectName = tab.name
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
    getCreateTemplateList() {
      if (!this.projectForm.businessScenario) {
        return;
      }
      this.$http.get('/api/system/questionnaire_template', {params: {'businessScenario': this.projectForm.businessScenario}}).then(res => {
        if (res.status == 200) {
          this.createTemplateList = res.data
        }
      });
    },
    getBusinessScenarioList() {
      this.$http.get('/api/system/business_scenario').then(res => {
        if (res.status == 200) {
          this.businessScenarioList = res.data
        }
      })
    },
    getWhoAmI() {
      let val = {}
      let accountList = {}
      this.$http.get('/api/account').then(res => {
        if (res.status == 200) {
          val.label = 'Users'
          accountList.id = res.data.id + '+user'
          accountList.name = res.data.username
          val.options = []
          val.options.push(accountList)
          this.ownerTypeList.push(val)
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
          this.existingProjectForm.copyFromProjectId = projectInfo.id
          this.existingProjectForm.businessScenario = projectInfo.businessScenario
          this.existingProjectForm.principleGeneric = projectInfo.principleGeneric
          this.existingProjectForm.principleFairness = projectInfo.principleFairness
          this.existingProjectForm.principleEA = projectInfo.principleEA
          this.existingProjectForm.principleTransparency = projectInfo.principleTransparency
          this.existingProjectForm.name = projectInfo.name
          this.existingProjectForm.description = projectInfo.description
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
              this.$http.post('/api/project/new', this.projectForm).then(res => {
                if (res.status == 201) {
                  this.$message.success('Create successfully')
                  this.$refs.projectFormRefs.resetFields()
                  // this.getProjectList()
                  this.$emit("created", res.data)
                }
              })
            } else {
              this.projectForm.groupOwnerId = ''
              this.projectForm.userOwnerId = ownerType.split('+')[0]
              this.$http.post('/api/project/new', this.projectForm).then(res => {
                if (res.status == 201) {
                  this.$message.success('Create successfully')
                  this.$refs.projectFormRefs.resetFields()
                  // this.getProjectList()
                  this.$emit("created", res.data)
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
              this.$http.post('/api/project/new', this.existingProjectForm).then(res => {
                if (res.status == 201) {
                  this.$message.success('Create successfully')
                  this.$refs.existingProjectFormRefs.resetFields()
                  this.getProjectList()
                  this.$emit("created", res.data)
                }
              })
            } else {
              this.existingProjectForm.groupOwnerId = ''
              this.existingProjectForm.userOwnerId = ownerType.split('+')[0]
              this.$http.post('/api/project/new', this.existingProjectForm).then(res => {
                if (res.status == 201) {
                  this.$message.success('Create successfully')
                  this.$refs.existingProjectFormRefs.resetFields()
                  // this.getProjectList()
                  this.$emit("created", res.data)
                }
              })
            }
            this.createProjectVisible = false
          }
        })
      }

    },

  },
  computed: {}
}

</script>
<style>
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

</style>