<template>
  <div style="height: 100%">
    <el-container
        :style="isCollapse?'height: calc(100% - 3px);display: flex;flex-direction: column':'height: calc(100% - 2px);display: flex;flex-direction: column'">
      <el-header height="64px">
        <div class="title BarlowBold">
          <img class="backPic" src="../../assets/groupPic/back.png" alt="" @click="$router.back()" alt=""
               style="cursor: pointer">
          <!--<router-link :to="{path:'/projectPage',query: {id:projectId}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>-->
          <span>Project</span>
        </div>
        <div class="BarlowMedium">
          <el-radio-group v-model="principle" class="principle-group">
            <el-radio-button v-show="this.principleList.indexOf('G') != -1" label="Generic">
              Generic
              <div class="fairness-diff-count" v-if="compareFlag && diffNum.G > 0">
                <span>{{ diffNum.G }}</span>
              </div>
            </el-radio-button>
            <el-radio-button v-show="this.principleList.indexOf('F') != -1" label="Fairness">
              Fairness
              <div class="fairness-diff-count" v-if="compareFlag && diffNum.F > 0">
                <span>{{ diffNum.F }}</span>
              </div>
            </el-radio-button>
            <el-radio-button v-show="this.principleList.indexOf('EA') != -1" label="Ethics & Accountability">
              Ethics & Accountability
              <div class="fairness-diff-count" v-if="compareFlag && diffNum.EA > 0">
                <span>{{ diffNum.EA }}</span>
              </div>
            </el-radio-button>
            <el-radio-button v-show="this.principleList.indexOf('T') != -1" label="Transparency">
              Transparency
              <div class="fairness-diff-count" v-if="compareFlag && diffNum.T > 0">
                <span>{{ diffNum.T }}</span>
              </div>
            </el-radio-button>
          </el-radio-group>
        </div>
        <div id="endComSty" class="BarlowMedium" @click="compareFlag=false" v-if="compareFlag">End comparison</div>
        <div style="display: flex" class="BarlowMedium" v-if="!compareFlag">
          <div id="preview" @click="previewPdf">Preview</div>
          <div id="export" v-show="project.archived !== true" @click="openExportDialog">Export</div>
          <export-report-dialog
              ref="exportDialog"
              :projectId="projectId"
              @exported="createdReport">
          </export-report-dialog>
        </div>
      </el-header>
      <!--flex-direction: column; overflow-y: auto-->
      <el-container style="flex: 1;overflow-y: auto">
        <el-aside :width="isCollapse? '72px':'400px'">
          <QuestionnaireMenu v-show="!compareFlag"
                             @getId="getQuestionId" :permissionList="permissionList"
                             :menuData="menuData" :principle="principle" :projectId="projectId"
                             :defaultQuestionSerial="currentQuestion.serial"
                             :isCollapse="isCollapse"/>
          <QuestionnaireCompareMenu v-show="compareFlag"
                             @getId="getQuestionId" :permissionList="permissionList"
                             :menuData="menuData" :principle="principle" :projectId="projectId"
                             :defaultQuestionSerial="currentQuestion.serial"
                             :isCollapse="isCollapse"/>
        </el-aside>
        <el-main :style="openCompare?'display:flex':''">
          <QuestionnaireAnswer :archived="project.archived"
                               :permissionList="permissionList"
                               :modelArtifactVersionId="modelArtifactVersionId"
                               v-show="!compareFlag"
                               :projectId="projectId"
                               :questionId="questionId"
                               style="overflow-y: auto"></QuestionnaireAnswer>
          <QuestionnaireCompareAnswer v-show="compareFlag" :isCollapse="isCollapse"
                                      :compareVersionTime="compareVersionTime" :creator="creator"
                                      :permissionList="permissionList" :compareFlag="compareFlag"
                                      :questionnaireVid="questionnaireVid" :projectId="projectId"
                                      :questionId="questionId"></QuestionnaireCompareAnswer>
        </el-main>
      </el-container>
      <el-footer style="height: 64px;">
        <div style="display: flex;width: 100%;justify-content:space-between;align-items: center">
          <div class="notification-collapse" v-if="!isCollapse">
            <Notification ref="notification" :project-id="projectId" @show-question="showQuestionComment"/>
            <div class="collapse-box" @click="isCollapse=true">
              <img src="../../assets/projectPic/chevron-left.svg" alt="">
            </div>
          </div>
          <div class="collapse-right" @click="isCollapse=false" v-if="isCollapse">
            <img src="../../assets/projectPic/chevron-right.svg" alt="">
          </div>
          <div class="footer-right BarlowMedium draft-popover"
               :style="isCollapse? 'width: calc(100% - 72px)':'width: calc(100% - 400px)'">
            <el-popover
                class=""
                ref="compare-popover"
                placement="top-start"
                width="480"
                height="500"
                trigger="click">
              <div style="height: 100%;width: 100%">
                <el-tabs v-model="compareTab" @tab-click="handleCompareClick">
                  <el-tab-pane label="Exported Version" name="exportedOnly">
                    <el-table height="300" width="800" :data="exportedHistoryList"
                              @row-click="table_compare"
                              @cell-mouse-enter="loadExportedHistory">
                      <el-table-column width="auto" label="Name">
                        <template slot-scope="scope">
                          <div class="draft-left">
                            <!--                            <img src="../../assets/groupPic/Avatar.png" alt="">-->
                            <div>{{ scope.row.creator.fullName }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column min-width="100" label="Created">
                        <template slot-scope="scope">
                          <div class="draft-right">
                            <div>{{ scope.row.createdTime | changeTime }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column width="auto" property="message" label="Message"/>
                    </el-table>
                  </el-tab-pane>
                  <el-tab-pane label="Recent Draft" name="draftOnly">
                    <el-table height="300" width="800" :data="draftHistoryList" @row-click="table_compare"
                              @cell-mouse-enter="loadDraftHistory">
                      <el-table-column width="auto" label="Name">
                        <template slot-scope="scope">
                          <div class="draft-left">
                            <!--                            <img src="../../assets/groupPic/Avatar.png" alt="">-->
                            <div>{{ scope.row.creator.fullName }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column min-width="100" label="Created">
                        <template slot-scope="scope">
                          <div class="draft-right">
                            <div>{{ scope.row.createdTime | changeTime }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column width="auto" property="message" label="Message"/>
                    </el-table>
                  </el-tab-pane>
                </el-tabs>
              </div>
              <div class="footer-text" style="margin-left: 24px" @click="getDiffVersion(compareTab)" slot="reference">
                Compare
              </div>
            </el-popover>
            <div style="display: flex">
            </div>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import QuestionnaireMenu from "@/components/questionnaire/QuestionnaireMenu";
import QuestionnaireAnswer from "@/components/questionnaire/QuestionnaireAnswer";
import QuestionnaireCompareAnswer from "@/components/questionnaire/QuestionnaireCompareAnswer";
import ExportReportDialog from "@/components/projects/ExportReportDialog";
import projectApi from "@/api/projectApi";
import Vue from "vue";
import Notification from "@/components/comment/Notification.vue";
import QuestionnaireCompareMenu from "@/components/questionnaire/QuestionnaireCompareMenu.vue";

export default {
  name: "Questionnaire",
  components: {
    QuestionnaireCompareMenu,
    Notification,
    QuestionnaireMenu,
    QuestionnaireAnswer,
    QuestionnaireCompareAnswer,
    ExportReportDialog,
  },
  mounted() {

  },
  data() {
    return {
      principle: 'Generic',
      principleList: [],
      isCollapse: false,
      compareTab: 'exportedOnly',
      openCompare: false,
      projectId: this.$route.query.id,
      permissionList: null,
      questionId: '',
      menuData: [],
      principleMap: {
        "Generic": "G",
        "Fairness": "F",
        "Ethics & Accountability": "EA",
        "Transparency": "T"
      },
      compareFlag: false,
      creator: {},
      questionnaireVid: '',
      modelArtifactVersionId: null,
      projNotLen: '',
      suggestVersionDict: {},
      diffNum: {
        G: '',
        F: '',
        EA: '',
        T: '',
      },
      exportedHistoryList: [],
      draftHistoryList: [],
      exportedHistoryPage: {
        page: 0,
        pageSize: 20,
        pageCount: 1,
        total: 0,
      },
      draftHistoryPage: {
        page: 0,
        pageSize: 20,
        pageCount: 1,
        total: 0,
      },
      compareVersionTime: '',
      projectDetail: {},
      project: {},
      currentQuestion: {},
      toc: {},
    }
  },
  async created() {
    await this.getQuestionnaireMenu()
    // console.log('pid',this.projectId)
    sessionStorage.setItem('projectId', JSON.stringify(this.projectId.toString()))
    // if (this.$route.params.permissionList && this.$route.params.permissionList.length > 0) {
    //   console.log("pl" + this.$route.params.permissionList)
    //   this.permissionList = this.$route.params.permissionList;
    // }
    await projectApi.detail(this.projectId)
        .then(response => {
          this.projectDetail = response.data;
          this.permissionList = [];
          if (this.projectDetail.groupRole) {
            this.permissionList = this.permissionList.concat(this.projectDetail.groupRole.permissionList)
          }
          if (this.projectDetail.projectRole) {
            this.permissionList = this.permissionList.concat(this.projectDetail.projectRole.permissionList)
          }
          this.project = this.projectDetail.project;
        });
    const currentQuestionSerial = this.$route.query.q;
    let currentQuestion = null;
    if (currentQuestionSerial) {
      currentQuestion = this.tocMainQuestionList.find(q => {return q.serial === currentQuestionSerial});
    }
    if (!currentQuestion) {
      currentQuestion = this.tocMainQuestionList[0];
    }
    this.currentQuestion = currentQuestion;
    this.principle = currentQuestion.principle;
    this.questionId = currentQuestion.id;
    console.log("questionnaire created this.currentQuestion: \n" + JSON.stringify(this.currentQuestion, null, 4));
  },

  watch: {
    'principle': function () {
      if (this.compareFlag) {
        this.switchPrincipleCompare(this.questionnaireVid)
      } else {
        this.getQuestionnaireMenu()
        this.menuData = this.toc.principleAssessments[this.principleMap[this.principle]].stepList;
      }
    },
    'compareFlag': function () {
      if (this.compareFlag) {
        this.compare(this.questionnaireVid, this.creator, this.compareVersionTime)
      } else {
        this.getQuestionnaireMenu()
      }
    },
  },
  methods: {
    createdReport(reportInfo) {
      this.fetchReportHistoryList();
      console.log('reportInfo: ', JSON.stringify(reportInfo))
      this.openReportPdf(reportInfo.versionIdOfProject);
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
    openExportDialog() {
      this.$refs.exportDialog.open();
      this.suggestVersion()
    },
    suggestVersion() {
      this.$http.get(`/api/project/${this.projectId}/report/suggestion-version`).then(res => {
        this.suggestVersionDict = res.data
      })
    },
    getProjNotLen(item) {
      this.projNotLen = item
    },
    handleCompareClick() {
      this.getDiffVersion(this.compareTab)
    },
    showQuestionComment(comment) {
      console.log("comment:\n" + JSON.stringify(comment, null, 4))
      const selectedProject = comment['projectId'].toString();
      const selectedQuestionSerial = comment['mainQuestionSerial'].toString();
      const selectedQuestionId = comment['mainQuestionId'].toString();
      console.log(`selected: project ${selectedProject}, question: ${selectedQuestionSerial}`)

      const currentProject = this.projectId === selectedProject;
      const currentQuestion = this.$route.query['q'] === selectedQuestionSerial;
      if (currentProject && currentQuestion) {
        // Yes, do nothing!
        console.log("same")
      } else if(currentProject) {
        const q = this.findQuestionInToc(selectedQuestionId)
        if (q != null) {
          console.log("q:" + JSON.stringify(q, null, 4))
          this.principle = q.principle;
          this.questionId = selectedQuestionId;
          // this.$set("currentQuestion", q)
          this.currentQuestion = q
          // this.$set(this.currentQuestion,"_questionId", selectedQuestionId)
          let query = {...this.$route.query}
          query.q = selectedQuestionSerial
          this.$router.replace({ query: query })
        }
      } else {
        this.$router.push({
          path: "/questionnaire",
          query: {
            id: selectedProject,
            q: selectedQuestionSerial
          }
        })
        this.$router.go(0)
      }
    },
    findQuestionInToc(questionId) {
      const q = this.tocMainQuestionList.find(main => {
        return main.id === questionId;
      })
      if (!q) {
        console.error("Not found the question:" + questionId)
      }
      return q;
    },
    getQuestionId(data) {
      this.questionId = data
    },
    getQuestionnaireMenu() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/toc`).then(res => {
        if (res.status === 200) {
          this.questionnaireVid = res.data.questionnaireVid
          this.principleList = Object.keys(res.data.principles)
          if (res.data.modelArtifactVersionId) {
            this.modelArtifactVersionId = res.data.modelArtifactVersionId
          } else {
            this.modelArtifactVersionId = null
          }
          this.toc = res.data;
          this.menuData = res.data.principleAssessments[this.principleMap[this.principle]].stepList
        }
      })
    },
    getDiffVersion(compareType) {
      if (compareType === 'exportedOnly') {
        if (this.exportedHistoryPage.page >= this.exportedHistoryPage.pageCount) {
          return;
        }
        this.$http.get(`/api/project/${this.projectId}/questionnaire/history`,
            {
              params: {
                exportedOnly: true,
                page: this.exportedHistoryPage.page + 1,
                pageSize: this.exportedHistoryPage.pageSize,
              }
            })
            .then(res => {
              let index = this.exportedHistoryList.length;
              for (let record of res.data.records) {
                record.index = index;
                ++index;
              }
              this.exportedHistoryList = this.exportedHistoryList.concat(res.data.records);
              this.exportedHistoryPage.page = res.data.page
              this.exportedHistoryPage.pageSize = res.data.pageSize
              this.exportedHistoryPage.pageCount = res.data.pageCount
              this.exportedHistoryPage.total = res.data.total
            })
      } else if (compareType === 'draftOnly') {
        if (this.draftHistoryPage.page >= this.draftHistoryPage.pageCount) {
          return;
        }
        this.$http.get(`/api/project/${this.projectId}/questionnaire/history`,
            {
              params: {
                draftOnly: true,
                page: this.draftHistoryPage.page + 1,
                pageSize: this.draftHistoryPage.pageSize,
              }
            })
            .then(res => {
              let index = this.draftHistoryList.length;
              for (let record of res.data.records) {
                record.index = index;
                ++index;
              }
              this.draftHistoryList = this.draftHistoryList.concat(res.data.records);
              this.draftHistoryPage.page = res.data.page
              this.draftHistoryPage.pageSize = res.data.pageSize
              this.draftHistoryPage.pageCount = res.data.pageCount
              this.draftHistoryPage.total = res.data.total
            });
      }

    },
    table_compare(row, column, event) {
      this.compare(row.questionnaireVid, row.creator, row.compareVersionTime)
    },
    compare(questionnaireVid, creator, compareVersionTime) {

      console.log("this.toc.questionnaireVid: " + this.toc.questionnaireVid)
      console.log("this.questionnaireVid: " + this.questionnaireVid)
      console.log("questionnaireVid: " + questionnaireVid)
      if (this.toc.questionnaireVid === questionnaireVid) {
        this.$message.warning("Current Version");
        return
      }
      this.compareFlag = true;
      this.creator = creator
      this.compareVersionTime = compareVersionTime
      this.questionnaireVid = questionnaireVid
      this.$http.get(`/api/project/${this.projectId}/questionnaire/compare/toc`,
          {params: {'based': questionnaireVid}}).then(res => {
        if (res.status == 200) {
          this.menuData = res.data.principleAssessments[this.principleMap[this.principle]].stepList
          this.diffNum.G = res.data.principleAssessments.G.diffCount
          this.diffNum.F = res.data.principleAssessments.F.diffCount
          this.diffNum.EA = res.data.principleAssessments.EA.diffCount
          this.diffNum.T = res.data.principleAssessments.T.diffCount
        }
      })
      this.$refs["compare-popover"].doClose();
    },
    loadExportedHistory(row, column, cell, event) {
      let left = this.exportedHistoryList.length - row.index;
      if (left < 5) {
        this.getDiffVersion("draftOnly");
      }
    },
    loadDraftHistory(row, column, cell, event) {
      let left = this.draftHistoryList.length - row.index;
      if (left < 5) {
        this.getDiffVersion("draftOnly");
      }
    },
    switchPrincipleCompare(questionnaireVid) {
      this.compareFlag = true
      this.questionnaireVid = questionnaireVid.toString()
      this.$http.get(`/api/project/${this.projectId}/questionnaire/compare/toc`, {params: {'based': questionnaireVid}}).then(res => {
        if (res.status == 200) {
          if (this.principle == 'Fairness') {
            this.questionId = res.data.principleAssessments[this.principleMap[this.principle]].stepList[1].mainQuestionList[0].id.toString()
          } else {
            this.questionId = res.data.principleAssessments[this.principleMap[this.principle]].stepList[0].mainQuestionList[0].id.toString()
          }
          this.menuData = res.data.principleAssessments[this.principleMap[this.principle]].stepList
        }
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
  },
  computed: {
    projectArchived() {
      if (this.project) {
        return this.project.archived === true;
      }
      return false;
    },
    tocMainQuestionList() {
      let all = [];
      for (const principle in this.toc.principleAssessments) {
        const principleAssessment = this.toc.principleAssessments[principle]
        let stepList = principleAssessment.stepList
        if (!stepList || stepList.length === 0) {
          continue;
        }
        for (const step of stepList) {
          const mainList = step.mainQuestionList;
          if (!mainList || mainList.length === 0) {
            continue;
          }
          all = all.concat(step.mainQuestionList)
        }
      }
      console.log("call all")
      return all;
    }
  }
}
</script>

<style scoped lang="less">
.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #E6E6E6;
}

.backPic {
  width: 24px;
  height: 24px;
  margin-left: 24px;
  margin-right: 8px;
}

.title {
  height: 64px;
  display: flex;
  align-items: center;

  > div {
    font-size: 20px;
    font-weight: bold;
    color: #000;
  }
}

#preview {
  cursor: pointer;
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
  margin-right: 12px;
}

#export {
  cursor: pointer;
  padding: 8px 12px;
  background-color: #78BED3;
  border-radius: 4px;
  color: #FFFFFF;
  margin-right: 24px;
}

#endComSty {
  cursor: pointer;
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
  margin-right: 24px;
}

.notification-collapse {
  display: flex;
  justify-content: space-between;
  width: 400px;
  background-color: #F2F5F7;
  line-height: 64px;
}

.not-box {
  display: flex;
  align-items: center;
  margin-left: 24px;

  > img {
    width: 24px;
    height: 24px;
  }

  > span {
    margin-left: 8px;
    font-size: 16px;
    color: #333333;
  }

  > div {
    margin-left: 8px;
    text-align: center;
    width: 24px;
    line-height: 24px;
    background-color: #FCB215;
    border-radius: 12px;
    color: #FFFFFF;
  }
}

.collapse-box {
  position: relative;
  cursor: pointer;
  width: 72px;
  border-left: 1px solid #D7D9DB;
  text-align: center;

  > img {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 40px;
    height: 40px;
  }
}

.footer-right {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-text {
  cursor: pointer;
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
}

.arrow {
  width: 24px;
  height: 24px;
}

.footer-prev {
  cursor: pointer;
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display: flex;
  align-items: center
}

.footer-next {
  cursor: pointer;
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  margin-left: 12px;
  margin-right: 24px
}

.collapse-right {
  height: 64px;
  display: flex;
  align-items: center;
  cursor: pointer;
  width: 72px;
  background-color: #F2F5F7;
  line-height: 64px;

  > img {
    margin: auto;
    width: 40px;
    height: 40px;
  }
}

.draft-box {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;

}

.draft-left {
  display: flex;
  align-items: center;

  > img {
    width: 32px;
    height: 32px;
  }

  > div {
    font-size: 16px;
    font-family: BarlowBold;
    margin-left: 8px;
  }
}

.fairness-diff-count {
  margin-left: 8px;
  width: 16px;
  height: 16px;
  border-radius: 8px;
  background-color: #FCB215;

  > span {
    font-family: BarlowBold;
    color: #FFFFFF;
  }
}
</style>