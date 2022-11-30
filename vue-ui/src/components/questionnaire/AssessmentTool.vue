<template>
  <div style="height: 100%">
    <el-container ref="print">
      <!--AvatarBox-->
      <el-header>
        <div class="title BarlowMedium">
          <router-link :to="{path:'/projectPage',query: {id:projectId}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
          <div>{{projectInfo.name}}</div>
        </div>
        <!--MoreActions-->
        <div class="BarlowMedium" style="display: flex;margin-right: 24px">
          <div v-show="permissionList.indexOf('project:input answer') != -1" class="save_preview BlackBorder" @click="putAnswerList"><span>Save</span></div>
          <div class="save_preview BlackBorder" @click="previewPdf"><span>Preview</span></div>
          <div class="export GreenBC" @click="exportPdfVisible = true"><span>Export report</span></div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="400px">
          <el-collapse accordion class="BarlowBold" v-model="activeName">
            <el-menu unique-opened :default-active="defaultActive" @select="choosenItem" active-text-color="#78BED3">
              <div v-for="(item1,index1) in questionnaireList" :key="index1" :style="index1 === 0? '': 'margin-top:16px'">
                <el-collapse-item :name="item1.partName" :style="index1 != questionnaireList.length -1? 'border-bottom: 1px solid #EBEEF5':''">
                  <template slot="title" class="partTitle BarlowBold">
                    <el-badge is-dot :hidden="item1.unreadCommentsCount == 0 || unreadComments[item1.partName] == 0" style="">
                      <div style="line-height: 20px">
                        {{item1.partName}}. {{item1.partTitle}}
                      </div>
                    </el-badge>
                  </template>
                  <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.questionList" :key="item2.id" :index="item2.id.toString()">
                    <template slot="title">
                      <el-badge is-dot :hidden="item1.unreadCommentsOfQuestionCountMap[item2.id] == 0 || unreadCommentsOfQuestionCount[item2.id] == 0">
                          <div><span v-show="!item2.completed && !questionIdCompleted[item2.id]" class="completed-style">* </span>{{item2.part}}{{item2.partSerial}}. {{item2.content}}</div>
                      </el-badge>
                    </template>
                  </el-menu-item>
                </el-collapse-item>
              </div>
            </el-menu>
          </el-collapse>
        </el-aside>
        <el-main>
          <div v-show="subQuestionList.length == 0">
            <div class="questionStyle BarlowBold" style="margin-bottom: 24px">{{question}}</div>
            <div style="display: flex;justify-content: space-between">
              <div></div>
              <el-popover
                placement="left"
                width="480"
                trigger="click">
                <div slot="reference" @click="getComment(serial)">
                  <el-badge :value="commentCount[serial]" class="item" :hidden="commentCount[serial] == 0">
                    <img style="width: 32px;height: 32px;" src="../../assets/adminPic/comment.png" alt="">
                  </el-badge>
                </div>
                <div>
                  <div v-for="(item,index) in commentList" style="margin-bottom: 16px" :style="!item.hasRead? 'background-color:#F8F8F8;padding:8px;border-radius:4px':''">
                    <div style="display: flex">
                      <div style="margin-right: 8px">
                        <img style="width: 26px;height: 26px;" src="../../assets/groupPic/Avatar.png" alt="">
                      </div>
                      <div>
                        <div class="comment-name BarlowBold">{{item.userFullName}}</div>
                        <div class="comment-time BarlowMedium">{{dateFormat(item.createdTime)}}</div>
                      </div>
                    </div>
                    <div class="comment-content BarlowMedium">{{item.comment}}</div>
                  </div>
                  <div class="divide_line" v-show="commentList.length"></div>
                  <el-input placeholder="Type comment here" v-model="addComment"></el-input>
                  <div style="display: flex;justify-content: space-between;margin-top: 16px">
                    <div></div>
                    <div style="display: flex">
                      <div @click="sendComment" class="GreenBC" style="font-size: 16px;padding: 8px;color: #FFF;border-radius: 4px">Send</div>
                    </div>
                  </div>
                </div>
              </el-popover>
            </div>
            <editor :key="serial.toString()" :id='serial.toString()' v-model="textarea[serial]" :init="init_summary"></editor>
          </div>
          <div v-show="subQuestionList.length != 0">
            <div class="questionStyle BarlowBold">{{question}}</div>
            <div v-for="(item,index) in subQuestionList" :key="item.id">
              <div style="display: flex;align-items: center;margin-top: 16px;margin-bottom: 8px;justify-content: space-between">
                <div class="subQuestionStyle BarlowMedium">{{item.content}}</div>
                <el-popover
                  placement="left"
                  width="480"
                  trigger="click">
                  <div slot="reference" @click="getComment(item.id)">
                    <el-badge :value="commentCount[item.id]" class="item" :hidden="commentCount[item.id] == 0">
                      <img style="width: 32px;height: 32px;" src="../../assets/adminPic/comment.png" alt="">
                    </el-badge>
                  </div>
                  <div>
                    <div v-for="(item,index) in commentList" style="margin-bottom: 16px" :style="!item.hasRead? 'background-color:#F8F8F8;padding:8px;border-radius:4px':''">
                      <div style="display: flex">
                        <div style="margin-right: 8px">
                          <img style="width: 26px;height: 26px;" src="../../assets/groupPic/Avatar.png" alt="">
                        </div>
                        <div>
                          <div class="comment-name BarlowBold">{{item.userFullName}}</div>
                          <div class="comment-time BarlowMedium">{{dateFormat(item.createdTime)}}</div>
                        </div>
                      </div>
                      <div class="comment-content BarlowMedium">{{item.comment}}</div>
                    </div>
                    <div class="divide_line" v-show="commentList.length"></div>
                    <el-input type="textarea" :rows="3" placeholder="Type comment here" v-model="addComment"></el-input>
                    <div style="display: flex;justify-content: space-between;margin-top: 16px">
                      <div></div>
                      <div style="display: flex">
                        <!--<div class="BlackBorder">Cancel</div>-->
                        <div @click="sendComment" class="GreenBC" style="font-size: 16px;padding: 8px;color: #FFF;border-radius: 4px">Send</div>
                      </div>
                    </div>
                  </div>
                </el-popover>
              </div>
              <div class="hint BarlowMedium" v-show="item.hint">
                <span>{{item.hint}}</span>
              </div>
              <editor :key="item.id.toString()" :id='item.id.toString()' v-model="textarea[item.id]" :init="init"></editor>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
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
  import axios from 'axios'
  import tinymce from 'tinymce/tinymce'
  import editor from '@tinymce/tinymce-vue'
  import 'tinymce/themes/silver/theme'
  import 'tinymce/icons/default/icons'
  import 'tinymce/plugins/image'
  import 'tinymce/plugins/link'
  import 'tinymce/plugins/code'
  import 'tinymce/plugins/table'
  import 'tinymce/plugins/lists'
  import 'tinymce/plugins/autoresize'
  import 'tinymce/plugins/contextmenu'
  import 'tinymce/plugins/wordcount'
  import 'tinymce/plugins/colorpicker'
  import 'tinymce/plugins/textcolor'
  import Vue from "vue";

  export default {
    name: "AssessmentTool",
    data() {
      return {
        activeName: 'A',
        tinymceHtml: '',
        init: {},
        init_summary: {},
        projectId: this.$route.query.id,
        questionnaireList: [],
        triQuestionnaireList: [],
        subQuestionList: [],
        question: '',
        textarea: {},
        serial: '',
        answerForm: [],
        defaultActive: '',
        commentId: '',
        commentList: [],
        addComment: '',
        projectInfo: {},
        allComment: {},
        commentCount: {},
        permissionList: [],
        questionIdCompleted: {},
        exportPdfVisible: false,
        exportPdfForm: {
          version: '',
          message: '',
        },
        suggestVersionDict: '',
        exportPdfFormRules:{
          version: [{ required: true, trigger: 'blur' },],
          message: [{ required: true, trigger: 'blur' },],
        },
        idMap: {},
        unreadComments: {},
        unreadCommentsOfQuestionCount: {}
      }
    },
    mounted () {
      let id = this.projectId
      this.init = {
        selector: 'textarea',
        skin_url: 'tinymce/skins/ui/oxide',
        autoresize_min_height : "60px",
        plugins: 'autoresize image link lists code table wordcount',
        toolbar: 'undo redo | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat',
        images_dataimg_filter: function (img) {
          return img.hasAttribute('internal-blob');
        },
        paste_data_images: true,
        id: this.$route.query.id,
        images_upload_handler: function (blobInfo, success, failure) {
          let formData = new FormData();
          formData.append("image", blobInfo.blob());
          axios.put(`/api/project/${id}/image`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(res => {
            success(res.data);
          })
        },
        branding: false,
        menubar: false,
        content_style: ' img { max-width:40%; display:block;height:auto; }'
      }
      this.init_summary = {
        selector: 'textarea',
        skin_url: 'tinymce/skins/ui/oxide',
        autoresize_min_height : "60px",
        plugins: 'autoresize image link lists code table wordcount',
        toolbar: 'undo redo | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat',
        images_dataimg_filter: function (img) {
          return img.hasAttribute('internal-blob');
        },
        paste_data_images: true,
        id: this.$route.query.id,
        images_upload_handler: function (blobInfo, success, failure) {
          let formData = new FormData();
          formData.append("image", blobInfo.blob());
          axios.put(`/api/project/${id}/image`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(res => {
            success(res.data);
          })
        },
        branding: false,
        menubar: false,
        content_style: ' img { max-width:40%; display:block;height:auto; }'
      }
      tinymce.init(this.init)
      tinymce.init(this.init_summary)
    },
    components: { editor },
    created() {
      this.getQuestionnaireInit()
      this.getProjectInfo()
      this.getFullComment()
      this.getProjectDetail()
      this.suggestVersion()
    },
    methods: {
      getProjectDetail() {
        this.$http.get(`/api/project/${this.projectId}/detail`).then(res => {
          if (res.status == 200) {
            this.permissionList = []
            if (res.data.groupRole) {
              this.permissionList = this.permissionList.concat(res.data.groupRole.permissionList)
            }
            if (res.data.projectRole) {
              this.permissionList = this.permissionList.concat(res.data.projectRole.permissionList)
            }
          }
        })
      },
      getFullComment() {
        this.$http.get(`/api/project/${this.projectId}/questionnaire/comment`).then(res => {
          this.allComment = res.data
          this.commentCount = {}
          for (let key in this.allComment) {
            let n = 0
            this.allComment[key].map(item => {
              if (!item.hasRead) {
                n++
              }
            })
            this.commentCount[key] = n
          }
        })
      },
      getProjectInfo() {
        this.$http.get(`/api/project/${this.projectId}`).then(res => {
          if (res.status == 200) {
            this.projectInfo = res.data
          }
        })
      },
      getComment(subId) {
        this.commentId = subId
        subId = parseInt(subId)
        this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${subId}/comment`,).then(res => {
          if(res.data[subId]) {
            this.commentList = res.data[subId].reverse()
          } else {
            this.commentList = []
          }
          this.commentCount[subId] = 0
        })
        this.unreadCommentsOfQuestionCount[this.serial] = this.unreadCommentsOfQuestionCount[this.serial] - this.commentCount[subId]
        for (let k in this.idMap) {
          if (this.idMap[k].indexOf(subId) != -1) {
            if (this.commentCount[subId]) {
              this.unreadComments[k] = this.unreadComments[k] - this.commentCount[subId]
            }
          }
        }
      },
      sendComment() {
        if (this.addComment) {
          this.$http.put(`/api/project/${this.projectId}/questionnaire/comment`,{questionId:this.commentId,comment:this.addComment}).then(res => {
            this.getComment(this.commentId)
            this.addComment = ''
          })
        }
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
      },
      getQuestionnaireInit() {
        this.$http.get(`/api/project/${this.projectId}/questionnaire`, {params: {onlyWithFirstAnswer:true}}).then(res => {
          if (res.status == 200) {
            this.questionnaireList = res.data.partList
            let partList = res.data.partList
            this.defaultActive = String(res.data.partList[0].questionList[0].id)
            this.choosenItem(this.defaultActive)
            this.triQuestionnaireList = []
            for (let i = 0; i<partList.length; i++) {
              let partIdList = []
              //this.idMap[partList[i].partName]=partList[i].questionList[0].id
              this.unreadComments[partList[i].partName] = partList[i].unreadCommentsCount
              Object.assign(this.unreadCommentsOfQuestionCount,partList[i].unreadCommentsOfQuestionCountMap)
              for (let j = 0; j < partList[i].questionList.length; j++) {
                this.triQuestionnaireList.push(partList[i].questionList[j])
                partIdList.push(partList[i].questionList[j].id)
                this.$set(this.textarea,partList[i].questionList[j].id,'')
                for (let k = 0; k < partList[i].questionList[j].subQuestions.length; k++) {
                  partIdList.push(partList[i].questionList[j].subQuestions[k].id)
                }
              }
              this.idMap[partList[i].partName] = partIdList
            }
            this.triQuestionnaireList.map(item => {
              for (let i = 0; i<item.subQuestions.length; i++) {
                this.$set(this.textarea,item.subQuestions[i].id,'')
              }
            })
          }
        })
      },
      getQuestionnaire() {
        this.$http.get(`/api/project/${this.projectId}/questionnaire`).then(res => {
          if(res.status == 200) {
            this.questionnaireList = res.data.partList
          }
        })
      },
      putAnswerList() {
        let answerObj = {}
        answerObj.id = this.serial
        answerObj.answer = this.textarea[this.serial]
        const triAnswer = []
        this.triQuestionnaireList.map(item => {
          if (item.id == this.serial) {
            this.subQuestionList = item.subQuestions
            for (let i = 0; i<item.subQuestions.length; i++) {
              let obj = {}
              obj.id = item.subQuestions[i].id
              obj.answer = this.textarea[item.subQuestions[i].id]
              triAnswer.push(obj)
            }
          }
        })
        answerObj.subQuestions = triAnswer
        this.$http.post(`/api/project/${this.projectId}/questionnaire/answer`,answerObj).then(res => {
          if(res.status == 200) {
            this.$set(this.questionIdCompleted,this.serial,res.data.completed)
            this.$message.success('Save successfully!')
          }
        })
      },
      choosenItem(index) {
        this.serial = index
        this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${index}`).then(res => {
          if (res.status == 200) {
            this.textarea[res.data.id] = res.data.answer
            this.question = res.data.part + res.data.partSerial + '.' +res.data.content
            this.subQuestionList = res.data.subQuestions
            for (let i = 0; i<res.data.subQuestions.length; i++) {
              this.textarea[res.data.subQuestions[i].id] = res.data.subQuestions[i].answer
            }
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .el-container {
    height: 100%;
  }
  .el-header {
    height: 56px !important;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 2px solid #E6E6E6;
  }
  .backPic {
    width: 32px;
    height: 32px;
    margin: 12px 16px;
  }
  .title {
    display: flex;
    align-items: center;
    > div {
      font-size: 18px;
      font-weight: bold;
      color: #000;
    }
  }
  .save_preview {
    cursor: pointer;
    height: 38px;
    padding: 0 16px;
    margin-right: 8px;
    > span {
      font-size: 16px;
      line-height: 40px;
      color: #666666;
    }
  }
  .divide_line {
    margin: 13px 0px;
    height: 1px;
    background-color: #CED3D9;
  }
  .export {
    cursor: pointer;
    height: 40px;
    padding: 0 16px;
    border-radius: 4px;
    background-color: #08457E;
    > span {
      font-size: 16px;
      line-height: 40px;
      color: #FFF;
    }
  }
  .partTitle {
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
    font-size: 14px;
    color: #000;
  }
  .el-aside {
    height: 100%;
    padding: 10px;
    background-color: #F2F2F2;
  }
  .el-menu {
    box-shadow:0px 0px 8px #EDF2F6;
    border-radius: 8px;
    padding: 12px;
  }
  .el-menu-item {
    height: auto;
    white-space: normal;
    word-break: break-word;
    line-height: 20px;
    padding: 8px 16px;
  }
  .menuDiv {
    width: 380px;
    background-color: #F2F2F2;
  }
  .el-textarea__inner {
    width: 992px;
    height: 160px;
  }
  .el-main {
    padding: 16px 24px;
  }
  .questionStyle {
    font-size: 16px;
    font-weight: bold;
  }
  .subQuestionStyle {
    font-size: 16px;
    font-weight: 400;
    color: #333333;
  }
  .hint {
    display: inline-block;
    padding: 0px 12px;
    margin-bottom: 8px;
    width: auto;
    text-align: center;
    line-height: 24px;
    height: 24px;
    background-color: #F2F2F2;
    border-radius: 4px;
    > i {
      color: #666666;
    }
    > span {
      font-size: 14px;
      color: #666666;
    }
  }
  .submit {
    margin-top: 16px;
    float: right;
  }
  .comment-name {
    font-size: 10px;
    line-height: 13px;
  }
  .comment-time {
    font-size: 6px;
    line-height: 13px;
    color: #888888;
  }
  .comment-content {
    font-size: 14px;
    color: #888888;
  }
  .completed-style {
    position: relative;
    color: red;
    top: 0px;
    right: 0px;
  }
</style>
