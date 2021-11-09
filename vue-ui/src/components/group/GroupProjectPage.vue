<template>
  <div style="padding: 32px 64px !important;">
    <!--groupPage title-->
    <div class="title BarlowBold">
      <router-link :to="{ path: '/groupPage' ,query: {groupId:groupId}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
      <span>Group</span>
    </div>
    <!--groupPage body-->
    <div class="BarlowMedium" style="margin-left: 48px">
      <div class="BarlowMedium titleBox">
        <div style="display:flex;align-items: center;" class="oneLine">
          <div class="projName">{{projectInfo.name}}</div>
          <div class="businessScenarioStyle oneLine">
            <div>{{businessScenario}}</div>
          </div>
        </div>
        <!--more actions-->
        <el-popover placement="left" width="154px" trigger="click" v-show="permissionList.indexOf('project:edit') != -1 || permissionList.indexOf('project:edit questionnaire') != -1 || permissionList.indexOf('project:delete') != -1">
          <div>
            <div v-show="permissionList.indexOf('project:edit') != -1" class="editProject BarlowMedium" @click="editProjectVisible = true" style="cursor: pointer"><img src="../../assets/groupPic/edit.png" alt=""><span>Edit project</span></div>
            <div v-show="permissionList.indexOf('project:edit') != -1 && permissionList.indexOf('project:edit questionnaire') != -1" class="divide_line"></div>
            <div v-show="permissionList.indexOf('project:edit questionnaire') != -1" class="editProject BarlowMedium" @click="editTemplate" style="cursor: pointer"><img src="../../assets/projectPic/editQuestionnaire.png" alt=""><span>Edit questionnaire</span></div>
            <div v-show="permissionList.indexOf('project:edit questionnaire') != -1 && permissionList.indexOf('project:delete') != -1" class="divide_line"></div>
            <div v-show="permissionList.indexOf('project:delete') != -1" class="deleteProject BarlowMedium" @click="deleteProjectInfo" style="cursor: pointer"><img src="../../assets/groupPic/delete.png" alt=""><span>Delete project</span></div>
          </div>
          <div slot="reference" class="moreAct" style="cursor: pointer">
            <img src="../../assets/groupPic/more.png" alt="">
          </div>
        </el-popover>
      </div>
      <div class="timeDesc">
        <div class="dateStyle">Created  at  {{dateFormat(projectInfo.createdTime)}}</div>
        <div class="dateStyle">Last changed  at  {{dateFormat(projectInfo.lastEditedTime)}}</div>
      </div>
      <!--group desc-->
      <div class="groupDesc">
        {{projectInfo.description}}
      </div>
      <el-tabs style="margin-top: 16px" v-model="activeName" class="BarlowMedium" >
        <el-tab-pane label="Assessment" name="first">
          <div style="display: flex;justify-content: space-between;align-items:center;margin-top: 16px">
            <div style="width: 40%" v-show="permissionList.indexOf('project:upload json') != -1">
              <div class="artifacts">Model artifacts</div>
              <div class="file-accepted">only .JSON file accepted</div>
              <!--upload JSON File-->
              <el-upload
                style="width: 60%"
                class="upload-demo"
                ref="upload"
                :action="actionUrl"
                multiple
                accept=".json"
                :limit="1"
                :file-list="fileList"
                :on-success="handleSuccess"
                :on-error="handleFailed"
                :auto-upload="false">
                <el-button slot="trigger" class="transparent BarlowMedium" size="small"><i class="el-icon-upload"></i>&nbsp;&nbsp;&nbsp;Add a JSON file</el-button>
                <el-button class="BarlowMedium" style="margin-left: 8px" size="small" @click="submitUpload">Upload</el-button>
              </el-upload>
            </div>
            <div style="width: 60%" v-if="jsonInfoVisible">
              <div class="artifacts">File name</div>
              <div style="display: flex;align-items: center">
                <div class="file-accepted">{{jsonInfo.filename}}</div>
                <div>
                  <el-button size="mini" icon="el-icon-download" circle plain style="margin-left: 10px" @click="downloadJsonFile"></el-button>
                </div>
              </div>
              <div class="artifacts">Upload time</div>
              <div class="file-accepted">{{dateFormat(jsonInfo.uploadTime)}}</div>
            </div>
          </div>
          <div v-if="permissionList.indexOf('project:upload json') != -1 || jsonInfoVisible" class="dividingLine"></div>
          <div>
            <div class="artifacts">Fairness assessment</div>
            <div style="display: flex;align-items: center;margin-top: 16px;justify-content: space-between">
              <div>
                <div class="progressLabel">
                  {{progressCompleted}}/{{progressCount}}
                </div>
                <el-progress :percentage="progressCompleted/progressCount*100" color="#78BED3" :show-text="false"></el-progress>
              </div>
              <div style="display: flex;align-items: center;">
                <div class="fairnessButton" @click="questionnaire">
                  <img src="../../assets/projectPic/edit.png" alt="">
                  <span>Edit answer</span>
                </div>
                <div class="fairnessButton" @click="previewPdf">
                  <img src="../../assets/projectPic/preview.png" alt="">
                  <span>Preview</span>
                </div>
                <div class="fairnessButton" @click="exportPdfVisible = true"  v-show="fairnessAssessmentVisible">
                  <img src="../../assets/projectPic/exportReport.png" alt="">
                  <span>Export report</span>
                </div>
              </div>
            </div>
          </div>
          <div class="dividingLine"></div>
          <div class="version" v-show="reportHistoryList.length != 0">Version history</div>
          <div class="dividingLine1" v-show="reportHistoryList.length != 0"></div>
          <!--version history-->
          <div  v-for="(item,index) in reportHistoryList" v-show="reportHistoryList.length != 0">
            <div class="version-box">
              <div  class="oneLine">
                <div style="display:flex;font-size: 14px;margin-right: 12px;padding: 4px 20px 2px 0px;font-weight: bold">
                  <div class="version-style"><i class="el-icon-price-tag"></i> v {{item.version}}</div>
                  <span>&nbsp;&nbsp;&nbsp;</span>
                  <div class="oneLine"><i class="el-icon-document"></i> {{item.message}}</div>
                </div>
                <div style="padding: 0px 0px 4px 0px">
                  <span class="date"><i class="el-icon-time"></i> on {{dateFormat(item.createdTime)}}</span>
                </div>
              </div>
              <div style="display: flex;flex-shrink: 0;">
                <div class="model-artifacts" @click="downloadHistoryJsonFile(item.projectId,item.versionIdOfProject)"><i class="el-icon-help"></i><span>Model artifacts</span></div>
                <div class="fairness-assessment" @click="questionnaireHistory(item.projectId,item.versionIdOfProject)"><i class="el-icon-notebook-1"></i><span>Fairness assessment</span></div>
                <div class="pdf-report" @click="previewHistoryPdf(item.projectId,item.versionIdOfProject)"><i class="el-icon-document-remove"></i><span>Report</span></div>
              </div>
            </div>
            <div class="dividingLine2"></div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Member" name="second">
          <div v-show="permissionList.indexOf('project:manage members') != -1">
            <div style="margin: 16px 0px;">
              <div class="artifacts">Invite members</div>
            </div>
            <el-tag size="mini" effect="plain" style="margin-bottom: 10px" v-for="tag in tags" @close="handleClose(tag)" :key="tag.id" closable type="info">{{ tag.fullName }}</el-tag>
            <div style="display: flex;align-items: center;justify-content: space-between">
              <el-select filterable @change="addUserToTags" value-key="id" v-model="selectUserObj" placeholder="Choose a user">
                <el-option class="selectStyle" v-for="item in userList" :key="item.id" :label="item.fullName" :value="item"></el-option>
              </el-select>
              <div style="display: flex;justify-content: space-between;align-items: center;margin-left: 12px;width: 400px">
                <el-select v-model="userType" placeholder="Choose a role permission">
                  <el-option class="selectStyle" v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
                </el-select>
                <div class="addUsers" @click="addUsersToGroup">Invite</div>
              </div>
            </div>
            <div class="dividingLine"></div>
          </div>
          <div class="version" style="margin-top: 24px">Members</div>
          <!--groupMember-->
          <div style="margin-top: 8px">
            <div class="bodyRow" type="flex" v-for="(item,index) in projectMember" :key="item.userId">
              <div style="display: flex;align-items: center">
                <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
                <div class="nameDesc">
                  <div>{{item.fullName}}</div>
                  <span>{{item.email}}</span>
                </div>
              </div>
              <div style="display: flex;align-items: center">
                <div>
                  <el-select :disabled="permissionList.indexOf('project:manage members') == -1" v-model="item.type" @change="changeMemberRole(item)" >
                    <el-option v-for="item in memberTypeList" :key="item.type" :label="item.label" :value="item.type"></el-option>
                  </el-select>
                </div>
                <el-button :disabled="permissionList.indexOf('project:manage members') == -1 || groupOwnerMemberList.indexOf(item.userId) != -1" class="deleteMember" @click="deleteMember(item.userId)" icon="el-icon-delete-solid"></el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <!--edit Project Info-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="editProjectVisible" @close="editProjectClosed" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">Edit project</span></template>
      <!--创建project表单区域-->
      <el-form :rules="editProjectFormRules" ref="editProjectFormRefs" label-position="top" label="450px" :model="editProjectForm">
        <el-form-item class="login" label="Project name" prop="name">
          <el-input placeholder="Please input a project name" v-model="editProjectForm.name"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Project description" prop="description">
          <el-input :rows="3" type="textarea" placeholder="Please input project description here" v-model="editProjectForm.description"></el-input>
        </el-form-item>
        <el-form-item class="login" label="Business scenario" prop="businessScenario">
          <el-select v-model="editProjectForm.businessScenario" placeholder="Please choose a business scenario">
            <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="editProjectVisible = false">Cancel</el-button>
        <el-button class="GreenBC" @click="editProjectInfo">Edit</el-button>
      </span>
    </el-dialog>
    <!--exportPDF-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="exportPdfVisible" @close="exportPdfClosed" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">Export report</span></template>
      <div v-if="suggestVersionDict.latestVersion" style="margin-top: 4px">Lasted Version: <span>{{suggestVersionDict.latestVersion}}</span></div>
      <el-form :rules="exportPdfFormRules" ref="exportPdfFormRefs" label-position="top" label="450px" :model="exportPdfForm">
        <el-form-item class="login" label="Report version" prop="version">
          <el-select v-model="exportPdfForm.version" filterable allow-create default-first-option placeholder="Please choose or input a report version">
            <el-option
              v-for="item in suggestVersionDict.suggestionVersionList"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="login" label="Report message" prop="message">
          <el-input placeholder="Please input a report message" v-model="exportPdfForm.message"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="exportPdfVisible = false">Cancel</el-button>
        <el-button class="GreenBC" @click="exportPdf">Export</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import Vue from "vue";

  export default {
    name: "GroupProjectPage",
    data() {
      return {
        projectId: this.$route.query.id,
        groupId: this.$route.query.groupId,
        businessScenarioList: [],
        projectInfo: {},
        projectDetail: {},
        businessScenario: '',
        editProjectVisible: false,
        editQuestionnaireVisible: false,
        fileList: [],
        editProjectForm: {
          id: '',
          name: '',
          description: '',
          businessScenario: '',
        },
        editProjectFormRules: {
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
          businessScenario: [{ required: true, trigger: 'blur' },],
        },
        actionUrl: '',
        jsonInfo: {},
        jsonInfoVisible: false,
        activeName: 'first',
        tags: [],
        addUserId: [],
        selectUserObj: {},
        userList: [],
        memberTypeList: [
          {
            type: 'OWNER',
            label: 'Owner'
          }, {
            type: 'DEVELOPER',
            label: 'Developer'
          },
          {
            type: 'ASSESSOR',
            label: 'Assessor'
          },],
        userType: '',
        projectMember: [],
        progressCompleted: 0,
        progressCount: 18,
        fairnessAssessmentVisible: false,
        reportHistoryList: [],
        jsonData: {},
        permissionList: [],
        groupOwnerId: '',
        groupOwnerMemberList: [],
        exportPdfVisible: false,
        exportPdfForm: {
          version: '',
          message: '',
        },
        suggestVersionDict: '',
        exportPdfFormRules:{
          version: [{ required: true, trigger: 'blur' },],
          message: [{ required: true, trigger: 'blur' },],
        }
      }
    },
    created() {
      this.getProjectInfo()
      this.actionUrl = `/api/project/${this.projectId}/modelArtifact`
      this.getUserList()
      this.getProjectMember()
      this.getProjectDetail()
      this.suggestVersion()
    },
    methods: {
      getProjectDetail() {
        this.progressCompleted = 0
        this.progressCount = 18
        this.$http.get(`/api/project/${this.projectId}/detail`).then(res => {
          this.jsonInfo = res.data.modelArtifact
          if (this.jsonInfo) {
            this.jsonInfoVisible = true
          }
          this.permissionList = []
          if (res.data.groupRole) {
            this.permissionList = this.permissionList.concat(res.data.groupRole.permissionList)
          }
          if (res.data.projectRole) {
            this.permissionList = this.permissionList.concat(res.data.projectRole.permissionList)
          }
          this.fairnessAssessmentVisible = true
          this.projectDetail = res.data
          this.progressCompleted = this.projectDetail.questionnaireProgress.completed
          this.progressCount = this.projectDetail.questionnaireProgress.count
          this.reportHistoryList = this.projectDetail.reportHistoryList
        }).catch(err => {
          this.fairnessAssessmentVisible = false
          this.projectDetail = {}
        })
      },
      editTemplate() {
        this.$router.push({path:'/editTemplate',query: {id:this.projectId}})
      },
      getProjectMember() {
        this.$http.get(`/api/project/${this.projectId}/member`).then(res => {
          this.projectMember = res.data
        })
      },
      addUserToTags(value) {
        if (this.tags.indexOf(value) == -1 && value != '') {
          this.tags.push(value)
          this.addUserId.push(value.id)
        }
      },
      handleClose(tag) {
        this.tags.splice(this.tags.indexOf(tag), 1);
        this.addUserId.splice(this.addUserId.indexOf(tag.id), 1);
      },
      addUsersToGroup() {
        let addUserList = []
        this.addUserId.map(item => {
          let addUser = {}
          addUser.userId = item
          addUser.type = this.userType
          addUserList.push(addUser)
        })
        this.$http.put(`/api/project/${this.projectId}/member`,addUserList).then(res => {
          if (res.status == 200) {
            this.$message.success('Invite successfully')
            this.selectUserObj = {}
            this.userType = ''
            this.tags = []
            this.addUserId = []
            this.getProjectMember()
          }
        })
      },
      deleteMember(userId) {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/project/${this.projectId}/member/${userId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.getProjectMember()
            }
          })
        })
      },
      changeMemberRole(user) {
        let changeRole = {}
        changeRole.userId = user.userId
        changeRole.type = user.type
        this.$http.post(`/api/project/${this.projectId}/member`,changeRole).then(res => {
          if (res.status == 200) {
            this.$message.success('Change successfully')
          }
        })
      },
      getUserList() {
        this.$http.get('/api/user',{params: {'prefix': this.prefix}}).then(res => {
          if (res.status == 200) {
            this.userList = res.data
          }
        })
      },
      editProjectInfo() {
        this.$refs.editProjectFormRefs.validate(val => {
          if (val) {
            this.editProjectForm.id = this.projectId
            this.$http.post(`/api/project/${this.projectId}`,this.editProjectForm).then(res => {
              if(res.status == 200) {
                this.$message.success('Edit successfully')
                this.getProjectInfo()
              }
              this.editProjectVisible = false
            })
          }
        })
      },
      editProjectClosed() {
        this.$refs.editProjectFormRefs.resetFields()
      },
      deleteProjectInfo() {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/project/${this.projectId}`).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.$router.push('/projects')
            }
          })
        })
      },
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
      getProjectInfo() {
        this.$http.get(`/api/project/${this.projectId}`).then(res => {
          if (res.status == 200) {
            this.projectInfo = res.data
            this.editProjectForm.name = res.data.name
            this.editProjectForm.description = res.data.description
            if (res.data.groupOwnerId) {
              this.groupOwnerId = res.data.groupOwnerId
              this.$http.get(`/api/project/${this.projectId}/member`).then(res => {
                res.data.map(item => {
                  this.groupOwnerMemberList.push(item.userId)
                })
              })
            }
            this.$http.get('/api/system/business_scenario').then(res => {
              if(res.status == 200) {
                this.businessScenarioList = res.data
                this.businessScenarioList.map(item => {
                  if (item.code == this.projectInfo.businessScenario) {
                    this.businessScenario = item.description
                    this.editProjectForm.businessScenario = item.code
                  }
                })
              }
            })
          }
        })
      },
      questionnaire() {
        this.$router.push({path:'/assessmentTool',query: {id:this.projectId}})
      },
      questionnaireHistory(projectId,versionId) {
        this.$router.push({path:'/assessmentToolHistory',query: {projectId:projectId,versionId:versionId}})
      },
      // upload json file
      submitUpload() {
        this.$refs.upload.submit()
      },
      handleSuccess() {
        this.$message.success('Upload successfully')
        this.getProjectDetail()
      },
      handleFailed() {
        this.$message.error('Upload failed')
      },
      downloadJsonFile() {
        this.$http({
          url:`/api/project/${this.projectId}/modelArtifact/download`,
          method: 'get',
          responseType: "blob",
          headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }).then(res => {
          if (res.status == 200) {
            let blob = new Blob([res.data],{type: "application/octet-stream;charset=utf-8"});
            if (window.navigator.msSaveBlob) {
              try {
                window.navigator.msSaveBlob(blob, this.jsonInfo.filename)
              } catch (e) {
              }
            } else {
              let Temp = document.createElement('a')
              Temp.href = window.URL.createObjectURL(blob)
              if (this.jsonInfo.filename) {
                Temp.download = this.jsonInfo.filename
              } else {
                Temp.download = 'data.json'
              }
              document.body.appendChild(Temp)
              Temp.click()
              document.body.removeChild(Temp)
              window.URL.revokeObjectURL(Temp.href)
            }
          }
        }).catch(err => {return err})
      },
      downloadHistoryJsonFile(projectId,versionId) {
        this.$http({
          url:`/api/project/${projectId}/history/${versionId}/modelArtifact/download`,
          method: 'get',
          responseType: "blob",
          headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }).then(res => {
          if (res.status == 200) {
            let blob = new Blob([res.data],{type: "application/octet-stream;charset=utf-8"});
            if (window.navigator.msSaveBlob) {
              try {
                window.navigator.msSaveBlob(blob, this.jsonInfo.filename)
              } catch (e) {
              }
            } else {
              let Temp = document.createElement('a')
              Temp.href = window.URL.createObjectURL(blob)
              if (this.jsonInfo.filename) {
                Temp.download = this.jsonInfo.filename
              } else {
                Temp.download = 'data.json'
              }
              document.body.appendChild(Temp)
              Temp.click()
              document.body.removeChild(Temp)
              window.URL.revokeObjectURL(Temp.href)
            }
          }
        }).catch(err => {
          if (err.response.config.responseType == "blob") {
            let data = err.response.data
            let fileReader = new FileReader()
            fileReader.onload = function() {
              let jsonData = JSON.parse(this.result)
              Vue.prototype.$message.error(jsonData.message)
            };
            fileReader.readAsText(data)
          }
        })
      },
      previewPdf() {
        this.$http({
          url:`/api/project/${this.projectId}/report/preview_pdf`,
          method: 'get',
          responseType: "blob",
          headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }).then(res => {
          const binaryData = []
          binaryData.push(res.data)
          let blob = new Blob(binaryData,{type: res.data.type})
          let pdfUrl = window.URL.createObjectURL(blob)
          window.open(pdfUrl)
        })
      },
      previewHistoryPdf(projectId,versionId) {
        this.$http({
          url:`/api/project/${projectId}/history/${versionId}/report`,
          method: 'get',
          responseType: "blob",
          headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }).then(res => {
          const binaryData = []
          binaryData.push(res.data)
          let blob = new Blob(binaryData,{type: res.data.type})
          let pdfUrl = window.URL.createObjectURL(blob)
          window.open(pdfUrl)
        }).catch(err => {
          if (err.response.config.responseType == "blob") {
            let data = err.response.data
            let fileReader = new FileReader()
            fileReader.onload = function() {
              let jsonData = JSON.parse(this.result)
              Vue.prototype.$message.error(jsonData.message)
            };
            fileReader.readAsText(data)
          }
        })
      },
      exportPdfClosed() {
        this.$refs.exportPdfFormRefs.resetFields()
      },
      suggestVersion() {
        this.$http.get(`/api/project/${this.projectId}/report/suggestion-version`).then(res => {
          this.suggestVersionDict = res.data
        })
      },
      exportPdf() {
        this.$refs.exportPdfFormRefs.validate(val => {
          if(val) {
            this.$http({
              url:`/api/project/${this.projectId}/report/export`,
              method: 'post',
              responseType: "blob",
              headers: {'Content-Type': 'application/json; charset=UTF-8'},
              data: this.exportPdfForm
            }).then(res => {
              if (res.status == 200) {
                let blob = new Blob([res.data],{type: res.data.type});
                if (window.navigator.msSaveBlob) {
                  try {
                    window.navigator.msSaveBlob(blob, 'application')
                  } catch (e) {
                  }
                } else {
                  let Temp = document.createElement('a')
                  Temp.href = window.URL.createObjectURL(blob)
                  Temp.download = 'application'
                  document.body.appendChild(Temp)
                  Temp.click()
                  document.body.removeChild(Temp)
                  window.URL.revokeObjectURL(Temp.href)
                }
              }
              this.exportPdfVisible = false
              this.suggestVersion()
            }).catch(err => {
              if (err.response.config.responseType == "blob") {
                let data = err.response.data
                let fileReader = new FileReader()
                fileReader.onload = function() {
                  let jsonData = JSON.parse(this.result)
                  Vue.prototype.$message.error(jsonData.message)
                };
                fileReader.readAsText(data)
              }
              this.exportPdfVisible = false
              this.suggestVersion()
            })
          }
        })
        this.getProjectDetail()
      },
    }
  }
</script>

<style lang="less" scoped>
  .title {
    display: flex;
    height: 40px;
    align-items: center;
    > a {
      height: 40px;
      display: flex;
      align-items: center;
    }
    > span {
      margin-left: 16px;
      font-size: 16px;
      font-weight: bold;
      line-height: 40px;
      color: #000;
    }
  }
  .titleBox {
    margin-top: 8px;
    height: 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .projName {
    font-size: 24px;
    font-weight: bold;
  }
  .backPic {
    line-height: 40px;
    width: 32px;
    height: 32px;
  }
  .moreAct {
    width: 40px;
    height: 40px;
    position: relative;
    background-color: #FFF;
    border: 1px solid rgba(0,0,0,0.85);
    border-radius: 4px;
    > img {
      width: 24px;
      height: 24px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%,-50%);
    }
  }
  .timeDesc {
    margin-top: 12px;
  }
  .dateStyle {
    font-size: 14px;
    line-height: 20px;
    color: #929292;
    font-weight: 400;
  }
  .groupDesc {
    margin-top: 16px;
    font-size: 16px;
    line-height: 24px;
    color: #333333;
    font-weight: 400;
  }
  .dividingLine {
    width: 100%;
    height: 1px;
    background-color: #E6E6E6;
    margin: 24px 0px;
  }
  .dividingLine1 {
    width: 100%;
    height: 1px;
    background-color: #E6E6E6;
    margin-top: 24px;
  }
  .dividingLine2 {
    width: 100%;
    height: 1px;
    background-color: #E6E6E6;
  }
  .editProject {
    display: flex;
    align-items: center;
    > img {
      width: 24px;
      height: 24px;
    }
    > span {
      margin-left: 12px;
      font-size: 14px;
      color: #333333;
    }
  }
  .deleteProject {
    display: flex;
    align-items: center;
    > img {
      width: 24px;
      height: 24px;
    }
    > span {
      margin-left: 12px;
      font-size: 14px;
      color: #E02020;
    }
  }
  .divide_line {
    margin: 13px 0px;
    height: 1px;
    background-color: #CED3D9;
  }
  .businessScenarioStyle {
    flex-shrink: 0;
    width: auto;
    display:flex !important;
    margin-left: 16px;
    height: 24px;
    background-color: #78BED3;
    padding: 0px 8px;
    text-align: center;
    border-radius: 14px;
    > div {
      color: #FFF;
      font-size: 14px;
      line-height: 24px;
    }
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
  .artifacts {
    font-weight: bold;
    font-size: 18px;
  }
  .file-accepted {
    font-size: 14px;
    font-width: 400;
    color: #808080;
    margin: 16px 0px 8px 0px;
  }
  .el-progress {
    margin: 8px 0px;
    width: 360px;
  }
  .progressLabel {
    color: #000;
    font-size: 20px;
  }
  .version {
    font-size: 18px;
    font-weight: bold;
  }
  .version-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 48px;
  }
  .model-artifacts {
    cursor: pointer;
    display: flex;
    align-items: center;
    height: 24px;
    border-radius: 4px;
    background-color: #0091FF;
    padding: 0px 8px;
    > i {
      color: #FFF;
      margin-right: 8px;
    }
    > span {
      color: #FFF;
      font-size: 12px;
    }
  }
  .fairness-assessment {
    cursor: pointer;
    display: flex;
    align-items: center;
    height: 24px;
    border-radius: 4px;
    background-color: #1962E4;
    margin-left: 16px;
    padding: 0px 8px;
    > i {
      color: #FFF;
      margin-right: 8px;
    }
    > span {
      color: #FFF;
      font-size: 12px;
    }
  }
  .pdf-report {
    cursor: pointer;
    display: flex;
    align-items: center;
    height: 24px;
    border-radius: 4px;
    background-color: #FA6400;
    margin-left: 16px;
    padding: 0px 8px;
    > i {
      color: #FFF;
      margin-right: 8px;
    }
    > span {
      color: #FFF;
      font-size: 12px;
    }
  }
  .upload-demo {
    width: 100%;
  }
  .date {
    color: #333333;
    font-size: 12px;
  }
  .el-button--small {
    border: 1px solid;
  }
  .fairnessButton {
    cursor: pointer;
    display: flex;
    align-items: center;
    border: 1px solid;
    padding: 8px 12px;
    border-radius: 4px;
    margin-left: 12px;
    >img {
      width: 24px;
      height: 24px;
    }
    >span {
      margin-left: 8px;
      font-size: 16px;
    }
  }
  .el-tag--plain.el-tag--info {
    margin-right: 10px;
  }
  .addUsers {
    cursor: pointer;
    margin-left: 12px;
    text-align: center;
    line-height: 38px;
    width: 60px;
    height: 38px;
    border: 1px solid;
    border-radius: 4px;
  }
  .bodyRow {
    height: 80px;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .avatar {
    width: 40px;
    height: 40px;
  }
  .nameDesc {
    margin-left: 16px;
    height: 48px;
    > div {
      font-size: 16px;
      font-weight: 600;
      line-height: 24px;
      padding-bottom: 2px;
    }
    > span {
      font-size: 14px;
      font-weight: 400;
      color: #B8B8B8;
    }
  }
  .deleteMember {
    position: relative;
    width: 38px;
    height: 38px;
    margin-left: 16px;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    padding: 0 !important;
    background-color: #F2F5F8;
  }
  .el-button.is-disabled, .el-button.is-disabled:focus, .el-button.is-disabled:hover {
    background-color: #f2f2f2 !important;
  }
  .version-style {
    background-color: #67C23A;
    padding: 0px 4px;
    border-radius: 4px;
    color: #FFF;
  }
</style>
