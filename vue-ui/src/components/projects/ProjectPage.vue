<template>
  <div v-if="projectInfo !== null" style="padding: 32px 64px !important;">
    <!--groupPage title-->
    <div class="title BarlowBold">
      <img @click="$router.back()" class="backPic" src="../../assets/groupPic/back.png" alt="">
      <span>Project</span>
    </div>
    <!--groupPage body-->
    <div class="BarlowMedium" style="margin-left: 48px">
      <div class="BarlowMedium titleBox">
        <div style="display:flex;align-items: center;" class="oneLine">
          <div class="projName">{{ projectInfo.name }}</div>
          <div class="businessScenarioStyle oneLine">
            <div>{{ businessScenario }}</div>
          </div>
          <div class="archivedStyle oneLine" v-if="archived">
            <div>Archived</div>
          </div>
        </div>
        <!--more actions-->
        <el-popover placement="left" width="154px" trigger="click"
                    v-show="has_permission(PermissionType.PROJECT_EDIT) || has_permission(PermissionType.PROJECT_DELETE) || has_permission(PermissionType.PROJECT_EDIT_QUESTIONNAIRE)">
          <div>
            <div v-show="showEditProjectButton"
                 class="editProject BarlowMedium"
                 @click="editProjectVisible = true" style="cursor: pointer">
              <img src="../../assets/groupPic/edit.png" alt=""/>
              <span>Edit project</span>
            </div>
            <div v-show="showEditProjectButton && showEditQuestionnaireButton"
                 class="divide_line">
            </div>
            <div v-show="showEditQuestionnaireButton"
                 class="editProject BarlowMedium" :style="archived?'pointer-events: none;':''"
                 @click="editTemplate" style="cursor: pointer">
              <img src="../../assets/projectPic/editQuestionnaire.png" alt=""/>
              <span>Edit questionnaire</span>
            </div>
            <div v-show="showEditQuestionnaireButton && showDeleteProjectButton"
                 class="divide_line"></div>
            <div v-show="showDeleteProjectButton"
                 class="deleteProject BarlowMedium"
                 @click="deleteProjectInfo" style="cursor: pointer">
              <img src="../../assets/groupPic/delete.png" alt=""/>
              <span>Delete project</span>
            </div>
          </div>
          <div slot="reference" class="moreAct" style="cursor: pointer">
            <img src="../../assets/groupPic/more.png" alt="">
          </div>
        </el-popover>
      </div>
      <div class="timeDesc">
        <div class="dateStyle">Created at {{ dateFormat(projectInfo.createdTime) }}</div>
        <div class="dateStyle">Last changed at {{ dateFormat(projectInfo.lastEditedTime) }}</div>
      </div>
      <!--group desc-->
      <div class="groupDesc">
        {{ projectInfo.description }}
      </div>
      <el-tabs style="margin-top: 16px" v-model="activeName" class="BarlowMedium">
        <el-tab-pane label="Assessment" name="first">
          <div style="margin-top: 16px">
            <div v-show="has_permission(PermissionType.PROJECT_UPLOAD_JSON)">
              <div class="artifacts" style="margin-bottom: 12px">Model artifacts</div>
              <!--              <div class="file-accepted">only .JSON file accepted</div>-->
              <!--upload JSON File-->
              <div style="display: flex;align-items:center;justify-content: space-between">
                <div style="width: 420px">
                  <el-upload
                      :disabled="archived"
                      class="upload-demo"
                      ref="upload"
                      drag
                      :action="actionUrl"
                      multiple
                      accept=".json"
                      :on-change="fileChange"
                      :show-file-list="true"
                      :file-list="fileList"
                      :on-success="handleSuccess"
                      :on-error="handleFailed"
                      :before-upload="beforeUpload"
                      :auto-upload="true">
                    <div class="upload-div">
                      <div>
                        <img class="upload-icon" src="../../assets/projectPic/upload.svg" alt="">
                      </div>
                      <div>
                        <div id="click-text">Click or drag and drop the file here to upload.</div>
                        <div id="json-text">only .JSON file accepted</div>
                      </div>
                    </div>
                  </el-upload>
                </div>
                <div class="file-info" v-if="jsonInfoVisible">
                  <div style="display: flex">
                    <img style="width: 40px;height: 40px;margin: 0px 16px" src="../../assets/projectPic/medium.svg"
                         alt="">
                    <div>
                      <div id="click-text">{{ jsonInfo.filename }}</div>
                      <div id="json-text">{{ dateFormat(jsonInfo.uploadTime) }}</div>
                    </div>
                  </div>
                  <div style="margin-right: 16px;cursor: pointer">
                    <img src="../../assets/projectPic/download.svg" alt="" @click="downloadJsonFile">
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="has_permission(PermissionType.PROJECT_UPLOAD_JSON) || jsonInfoVisible" class="dividingLine"></div>
          <div>
            <div class="artifacts">Assessment questionnaire</div>
            <div style="display: flex;align-items: center;margin-top: 12px;">
              <el-card v-for="(item,index) in progressList" class="box-card" :style="index == 0? '':'margin-left:12px'">
                <div class="artifacts" style="margin-bottom: 12px">{{ principleMap[item.principle] }}</div>
                <div class="progressLabel">
                  {{ item.completed }}/{{ item.count }}
                </div>
                <el-progress :percentage="item.completed/item.count*100"
                             color="#78BED3"
                             :show-text="false"/>
              </el-card>
            </div>
            <div style="display: flex;align-items: center;">
              <div class="fairnessButton" v-if="archived" @click="questionnaire">
                <!-- view questionnaire if the project has been archived. -->
                <img src="../../assets/projectPic/new_edit.png" alt="">
                <span>View</span>
              </div>
              <div class="fairnessButton" v-if="!archived" @click="questionnaire">
                <img src="../../assets/projectPic/new_edit.png" alt="">
                <span>Edit</span>
              </div>
              <div class="fairnessButton" @click="previewPdf">
                <img src="../../assets/projectPic/new_preview.png" alt="">
                <span>Preview</span>
              </div>

              <div class="fairnessButton" v-if="!archived" @click="openExportDialog">
                <img src="../../assets/projectPic/new_export.png" alt="">
                <span>Export</span>
              </div>
              <export-report-dialog
                  ref="exportDialog"
                  :projectId="projectId"
                  @exported="createdReport">
              </export-report-dialog>
            </div>
          </div>
          <div class="dividingLine"></div>
          <project-version-history :reportHistoryList="reportHistoryList"></project-version-history>
        </el-tab-pane>
        <el-tab-pane label="Member" name="second">
          <ProjectMember v-if="projectInfo !== null" :project="projectInfo" :archived="archived"
                         :has_manage_members_permission="has_permission(PermissionType.PROJECT_MANAGE_MEMBERS)"></ProjectMember>
        </el-tab-pane>
      </el-tabs>
    </div>
    <!--edit Project Info-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="editProjectVisible"
               @close="editProjectClosed" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">Edit project</span></template>
      <el-form :rules="editProjectFormRules" ref="editProjectFormRefs" label-position="top" label="450px"
               class="createProject"
               :model="editProjectForm">
        <el-form-item class="login" label="Project name" prop="name">
          <el-input placeholder="Please input a project name" v-model="editProjectForm.name" :disabled="archived"/>
        </el-form-item>
        <el-form-item class="login" label="Project description" prop="description">
          <el-input :rows="3" type="textarea" placeholder="Please input project description here"
                    v-model="editProjectForm.description" :disabled="archived"/>
        </el-form-item>
        <el-form-item class="login" disabled label="Business scenario" prop="businessScenario">
          <el-select disabled v-model="editProjectForm.businessScenario"
                     placeholder="Please choose a business scenario">
            <el-option v-for="item in businessScenarioList" :key="item.code" :label="item.name"
                       :value="item.code"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="login" label="Assess Principle" prop="principleGeneric">
          <el-checkbox disabled v-model="editProjectForm.principleGeneric">Generic</el-checkbox>
          <el-checkbox :disabled="archived" style="margin-left: 8px" v-model="editProjectForm.principleFairness">
            Fairness
          </el-checkbox>
          <el-checkbox :disabled="archived" style="margin-left: 8px" v-model="editProjectForm.principleEA">Ethics &
            Accountability
          </el-checkbox>
          <el-checkbox :disabled="archived" style="margin-left: 8px" v-model="editProjectForm.principleTransparency">
            Transparency
          </el-checkbox>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer" style="display: flex;justify-content: space-between">
        <el-button class="GreenBC" @click="archiveProject" v-if="!archived">Archive</el-button>
        <el-button class="GreenBC" @click="unarchiveProject" v-if="archived">Unarchive</el-button>
        <div>
          <el-button class="BlackBorder" @click="editProjectVisible = false">Cancel</el-button>
          <el-button class="GreenBC" @click="editProjectInfo">Edit</el-button>
        </div>
      </span>
    </el-dialog>
    <!--exportPDF-->

  </div>
</template>

<script>
import Vue from "vue";
import {permissionCheck, PermissionType} from "@/util/permission";
import ProjectMember from "@/components/projects/ProjectMember";
import ProjectVersionHistory from "@/components/projects/ProjectVersionHistory";
import ExportReportDialog from "@/components/projects/ExportReportDialog";
import projectApi from "@/api/projectApi";

export default {
  name: "ProjectPage",
  components: {
    ExportReportDialog,
    ProjectMember,
    ProjectVersionHistory
  },
  data() {
    return {
      principleMap: {
        "G": "Generic",
        "F": "Fairness",
        "EA": "Ethics & Accountability",
        "T": "Transparency"
      },
      projectId: this.$route.query.id,
      businessScenarioList: [],
      projectInfo: null,
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
        principleGeneric: true,
        principleFairness: false,
        principleEA: false,
        principleTransparency: false
      },
      editProjectFormRules: {
        name: [{required: true, trigger: 'blur'},],
        description: [{required: true, trigger: 'blur'},],
        businessScenario: [{required: true, trigger: 'blur'},],
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
      progressList: [],
      jsonData: {},
      permissionList: [],
      userOwnerId: '',
      groupOwnerId: '',
      groupOwnerMemberList: [],
      exportPdfVisible: false,
      exportPdfForm: {
        version: '',
        message: '',
      },
      exportPdfFormRules: {
        version: [{required: true, trigger: 'blur'},],
        message: [{required: true, trigger: 'blur'},],
      },
      PermissionType: PermissionType,
      archived: false,
      projectPrinciple: {
        principleGeneric: false,
        principleFairness: false,
        principleEA: false,
        principleTransparency: false,
      }


    }
  },
  created() {
    // window.sessionStorage.setItem('projectId', JSON.stringify(this.projectId))
    sessionStorage.setItem('projectId', JSON.stringify(this.projectId.toString()))
    this.resetSetItem('projectId', JSON.stringify(this.projectId));
    this.getProjectInfo()
    this.actionUrl = `/api/project/${this.projectId}/modelArtifact`
    this.getUserList()
    this.getProjectMember()
    this.getProjectDetail()
    this.fetchReportHistoryList()
  },
  computed: {
    showEditProjectButton() {
      return this.has_permission(PermissionType.PROJECT_EDIT)
    },
    showEditQuestionnaireButton() {
      return this.has_permission(PermissionType.PROJECT_EDIT_QUESTIONNAIRE) && !this.archived
    },
    showDeleteProjectButton() {
      return this.has_permission(PermissionType.PROJECT_DELETE) && !this.archived
    }
  },
  methods: {
    fileChange(file, fileList) {
      if (fileList.length > 0) {
        this.fileList = [fileList[fileList.length - 1]]; // 获取最后一次选择的文件
      }
    },
    has_permission(target_permission) {
      return permissionCheck(this.permissionList, target_permission);
    },
    openExportDialog() {
      this.$refs.exportDialog.open();
    },

    getProjectDetail() {
      this.progressCompleted = 0
      this.progressCount = 18
      this.$http.get(`/api/project/${this.projectId}/detail`).then(res => {
        this.jsonInfo = res.data.modelArtifact
        this.progressList = res.data.progressList
        if (res.data.project.groupOwnerId) {
          this.groupOwnerId = res.data.project.groupOwnerId
          if (res.data.groupRole) {
            this.$http.get(`/api/group/${this.groupOwnerId}/member`).then(res => {
              res.data.map(item => {
                this.groupOwnerMemberList.push(item.userId)
              })
            })
          }
        }
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
      }).catch(err => {
        this.fairnessAssessmentVisible = false
        this.projectDetail = {}
      })
    },
    editTemplate() {
      this.$router.push({path: '/template', query: {id: this.projectId}})
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
    inviteMembers() {
      let addUserList = []
      this.addUserId.map(item => {
        let addUser = {}
        addUser.userId = item
        addUser.type = this.userType
        addUserList.push(addUser)
      })
      this.$http.put(`/api/project/${this.projectId}/member`, addUserList).then(res => {
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
      this.$confirm('Confirm delete?', {type: 'warning'}).then(() => {
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
      this.$http.post(`/api/project/${this.projectId}/member`, changeRole).then(res => {
        if (res.status == 200) {
          this.$message.success('Change successfully')
        }
      })
    },
    getUserList() {
      this.$http.get('/api/user', {params: {'prefix': this.prefix}}).then(res => {
        if (res.status == 200) {
          if (this.userOwnerId) {
            res.data.map(item => {
              if (item.id != this.userOwnerId) {
                this.userList.push(item)
              }
            })
          } else {
            this.userList = res.data
          }
        }
      })
    },
    editProjectInfo() {
      this.$refs.editProjectFormRefs.validate(val => {
        if (val) {
          this.editProjectForm.id = this.projectId
          this.$http.post(`/api/project/${this.projectId}`, this.editProjectForm).then(res => {
            if (res.status === 200) {
              this.$message.success('Edit successfully')
              this.getProjectDetail()
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
      this.$confirm('Confirm delete?', {type: 'warning'}).then(() => {
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
      let mm = time.getMonth() + 1
      let dd = time.getDate()
      let HH = time.getHours()
      let MM = time.getMinutes()
      let SS = time.getSeconds()
      return yyyy + '-' + mm + '-' + dd + ',' + ' ' + HH + ':' + MM + ':' + SS
    },
    getProjectInfo() {
      this.$http.get(`/api/project/${this.projectId}`).then(res => {
        if (res.status == 200) {
          this.archived = res.data.archived
          this.projectInfo = res.data
          this.editProjectForm.name = res.data.name
          this.editProjectForm.description = res.data.description
          this.editProjectForm.principleGeneric = res.data.principleGeneric
          this.editProjectForm.principleFairness = res.data.principleFairness
          this.editProjectForm.principleEA = res.data.principleEA
          this.editProjectForm.principleTransparency = res.data.principleTransparency
          this.projectPrinciple.principleFairness = res.data.principleFairness
          this.projectPrinciple.principleEA = res.data.principleEA
          this.projectPrinciple.principleTransparency = res.data.principleTransparency
          this.projectPrinciple.principleGeneric = res.data.principleGeneric
          if (res.data.userOwnerId) {
            this.userOwnerId = res.data.userOwnerId
          }
          if (res.data.groupOwnerId) {
            this.groupOwnerId = res.data.groupOwnerId
          }
          this.$http.get('/api/system/business_scenario').then(res => {
            if (res.status == 200) {
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
      this.$router.push({
        name: 'questionnaire',
        query: {
          id: this.projectId,
        },
        params: {
          permissionList: this.permissionList
        }
      });
    },
    questionnaireHistory(projectId, versionId) {
      this.$router.push({path: '/assessmentToolHistory', query: {projectId: projectId, versionId: versionId}})
    },

    handleSuccess() {
      this.$message.success('Upload successfully')
      this.getProjectDetail()
    },
    async handleFailed(error) {
      console.log("error")
      console.log(error)
      if (error.status === 401) {
        this.$message.error("Please sign in to the system.")
        return this.$router.replace({
          path: "/login",
          query: {redirect: this.$route.fullPath}
        })
      }
      let data = error.message;
      let json = null;
      if (data) {
        try {
          json = JSON.parse(data);
        } catch (e) {
          console.log("Not a json file.")
          json = null;
        }
      }
      if (data && json && json.message) {
        await this.$confirm(
            json.message,
            "Upload failed.",
            {
              type: 'error',
              cancelButtonText: 'cancel',
              confirmButtonText: 'OK',
              showCancelButton: false,
            });
      } else {
        this.$message.error('Upload failed')
      }
    },
    beforeUpload(file) {
      return new Promise(async (resolve, reject) => {
        const reader = new FileReader();
        if (!file.name.endsWith(".json")) {
          await this.$alert("This is not a json file.");
          return reject("You should upload json file.");
        }
        let g = this.projectPrinciple.principleGeneric;
        let f = this.projectPrinciple.principleFairness;
        let t = this.projectPrinciple.principleTransparency;
        reader.onload = async (res) => {
          const jsonContent = res.target.result;
          let jsonModel = null;
          try {
            jsonModel = JSON.parse(jsonContent);
          } catch (e) {
            await this.$alert("This is not a json file.");
            return reject("Parse file failed.")
          }
          let fairness = jsonModel['fairness'];
          let transparency = jsonModel['transparency'];
          let onlyTransparency = !fairness && transparency;
          let onlyFairness = fairness && !transparency;
          let bothFT = fairness && transparency;
          let neitherFT = !fairness && !transparency;

          if ((g && f && t) && bothFT) {
            return resolve(true);
          } else if ((g && f && !t) && onlyFairness) {
            return resolve(true);
          }

          if (neitherFT) {
            return this.$confirm(
                "The model artifact data does not cover the principle you selected, please check further.",
                "Upload failed.",
                {
                  type: 'error',
                  cancelButtonText: 'Cancel',
                  confirmButtonText: 'Continue',
                  showConfirmButton: false,
                })
                .then(() => {
                  return reject(false)
                })
                .catch(() => {
                  return reject(false)
                })
          } else if (onlyTransparency) {
            if (!t) {
              return this.$confirm(
                  "The model artifact data does not cover the principle you selected, please check further.",
                  "Upload",
                  {
                    type: 'error',
                    cancelButtonText: 'cancel',
                    confirmButtonText: 'Continue',
                    showConfirmButton: false,
                  })
                  .then(() => {
                    return reject(false)
                  })
                  .catch(() => {
                    return reject(false)
                  })
            } else if (g || f) {
              return this.$confirm(
                  "Related data in some of the selected principles is not available. Run evaluate( ) function in Diagnosis Tool for autofill.",
                  "Upload",
                  {
                    type: 'warning',
                    cancelButtonText: 'Cancel',
                    confirmButtonText: 'Continue',
                  })
                  .then(() => {
                    return resolve(true)
                  })
                  .catch(() => {
                    return reject(false)
                  });
            }
          } else if (onlyFairness) {
            return this.$confirm(
                "The principles covered by model artifact data differ from the selected principles. Please check further.",
                "Upload",
                {
                  type: 'warning',
                  cancelButtonText: 'Cancel',
                  confirmButtonText: 'Continue',
                })
                .then(() => {
                  return resolve(true)
                })
                .catch(() => {
                  return reject(false)
                });
          } else {
            return this.$confirm(
                "The principles covered by model artifact data differ from the selected principles. Please check further.",
                "Upload",
                {
                  type: 'warning',
                  cancelButtonText: 'Cancel',
                  confirmButtonText: 'Continue',
                })
                .then(() => {
                  return resolve(true)
                })
                .catch(() => {
                  return reject(false)
                });
          }
        }


        reader.onerror = (err) => {
          return reject(false);
        }
        reader.readAsText(file);
      })
    },
    downloadJsonFile() {
      let url = `/api/project/${this.projectId}/modelArtifact/download`;
      let filename = this.jsonInfo.filename;
      if (!filename) {
        filename = 'data.json'
      }
      window.open(url, filename)
    },
    downloadJsonFile2() {
      this.$http({
        url: `/api/project/${this.projectId}/modelArtifact/download`,
        method: 'get',
        responseType: "blob",
        timeout: 30 * 60 * 1000,
        headers: {'Content-Type': 'application/json; charset=UTF-8'}
      }).then(res => {
        if (res.status === 200) {
          let blob = new Blob([res.data], {type: "application/octet-stream;charset=utf-8"});
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
        console.error("download failed.")
        console.error(err.code)
        console.error(err.message)
        console.error(err.stack)
        return err
      })
    },
    previewPdf() {
      this.$http({
        url: `/api/project/${this.projectId}/report/preview_pdf`,
        method: 'get',
        responseType: "blob",
        headers: {'Content-Type': 'application/json; charset=UTF-8'}
      }).then(res => {
        const binaryData = []
        binaryData.push(res.data)
        let blob = new Blob(binaryData, {type: res.data.type})
        let pdfUrl = window.URL.createObjectURL(blob)
        window.open(pdfUrl)
      })
    },
    previewHistoryPdf(projectId, versionId) {
      this.$http({
        url: `/api/project/${projectId}/history/${versionId}/report`,
        method: 'get',
        responseType: "blob",
        headers: {'Content-Type': 'application/json; charset=UTF-8'}
      }).then(res => {
        const binaryData = []
        binaryData.push(res.data)
        let blob = new Blob(binaryData, {type: res.data.type})
        let pdfUrl = window.URL.createObjectURL(blob)
        window.open(pdfUrl)
      }).catch(err => {
        if (err.response.config.responseType == "blob") {
          let data = err.response.data
          let fileReader = new FileReader()
          fileReader.onload = function () {
            let jsonData = JSON.parse(this.result)
            Vue.prototype.$message.error(jsonData.message)
          };
          fileReader.readAsText(data)
        }
      })
    },
    createdReport(reportInfo) {
      this.fetchReportHistoryList();
      if (reportInfo['versionIdOfProject']) {
        this.openReportPdf(reportInfo['versionIdOfProject']);
      }
    },

    fetchReportHistoryList() {
      projectApi.fetchReportHistoryList(this.projectId)
          .then(res => {
            this.reportHistoryList = res.data;
          })
    },

    openReportPdf(versionId) {
      projectApi.fetchReportPdf(this.projectId, versionId)
          .then(res => {
            const binaryData = []
            binaryData.push(res.data)
            let blob = new Blob(binaryData, {type: res.data.type})
            let pdfUrl = window.URL.createObjectURL(blob)
            window.open(pdfUrl)
          }).catch(err => {
        if (err.response.config.responseType === "blob") {
          let data = err.response.data
          let fileReader = new FileReader()
          fileReader.onload = function () {
            let jsonData = JSON.parse(this.result)
            Vue.prototype.$message.error(jsonData.message)
          };
          fileReader.readAsText(data)
        }
      })
    },
    archiveProject() {
      this.$http.post(`/api/project/${this.projectId}/archive`).then(res => {
        if (res.status == 200) {
          this.$message.success('Archive successfully')
          this.editProjectVisible = false
          this.getProjectInfo()
        }
      })
    },
    unarchiveProject() {
      this.$http.post(`/api/project/${this.projectId}/unarchive`).then(res => {
        if (res.status == 200) {
          this.$message.success('Unarchive successfully')
          this.editProjectVisible = false
          this.getProjectInfo()
        }
      })
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
  cursor: pointer;
  line-height: 40px;
  width: 32px;
  height: 32px;
}

.moreAct {
  width: 40px;
  height: 40px;
  position: relative;
  background: #EDF2F6;
  border-radius: 4px;

  > img {
    width: 24px;
    height: 24px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
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
  display: flex !important;
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

.archivedStyle {
  flex-shrink: 0;
  width: auto;
  display: flex !important;
  margin-left: 16px;
  height: 24px;
  background-color: #FCB215;
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
  font-size: 16px;
}

.file-accepted {
  font-size: 14px;
  font-width: 400;
  color: #808080;
  margin: 16px 0px 8px 0px;
}

.el-progress {
  margin: 8px 0px;

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
  background: #EDF2F6;
  padding: 8px 12px;
  border-radius: 4px;
  margin-right: 12px;

  > img {
    width: 24px;
    height: 24px;
  }

  > span {
    margin-left: 8px;
    font-size: 16px;
  }
}

.archivedButton {
  cursor: not-allowed;
  pointer-events: none;
  display: flex;
  align-items: center;
  background: #EDF2F6;
  padding: 8px 12px;
  border-radius: 4px;
  margin-right: 12px;

  > img {
    width: 24px;
    height: 24px;
  }

  > span {
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

.upload-div {
  padding: 16px;
  border-radius: 4px;
  display: flex;
  align-items: center;
}

.upload-icon {
  margin-right: 16px;
  width: 40px;
  height: 40px;
}

#click-text {
  font-size: 16px;
  font-family: BarlowBold;
}

#json-text {
  font-size: 14px;
  font-family: BarlowMedium;
}

.file-info {
  width: 600px;
  display: flex;
  justify-content: space-between;

  align-items: center;
  height: 74px;
  background: #F5F7F9;
  border-radius: 4px;
  border: 1px solid #D5D8DD;
}

.box-card {
  width: 25%;
  margin-bottom: 12px;
  padding: 12px;
}
</style>
