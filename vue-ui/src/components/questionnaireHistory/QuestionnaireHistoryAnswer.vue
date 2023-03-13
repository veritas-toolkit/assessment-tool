<template>
  <div style="padding: 16px 24px">
    <div class="main-ques BarlowBold">{{answerDict.serial}}. {{answerDict.question}}</div>
    <!--main-question-->
    <div class="sub-ques" v-show="!answerDict.subQuestionList">
      <div class="sub-ques-title">
        <div style="padding: 8px 16px;font-size: 16px" class="BarlowMedium">
          {{ answerDict.question }}
        </div>
        <div style="display: flex" v-show="false">
          <img @click="mainQuesEcho(answerDictId)" class="subQues-edit" src="../../assets/questionnairePic/edit.svg" alt="">
          <el-popover
              placement="left-start"
              width="400"
              trigger="click">
            <div>
              <div v-for="(item,index) in commentList" style="margin-bottom: 16px" :style="!item.hasRead? 'background-color:#F8F8F8;padding:8px;border-radius:4px':''">
                <div style="display: flex">
                  <div style="margin-right: 8px">
                    <img style="width: 26px;height: 26px;" src="../../assets/groupPic/Avatar.png" alt="">
                  </div>
                  <div>
                    <div class="comment-name BarlowBold">{{item.userFullName}}</div>
                    <div class="comment-time BarlowMedium">{{item.createdTime|changeTime}}</div>
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
            <div @click="getComment(answerDict.id)" slot="reference">
              <el-badge :value="commentCount[answerDict.id]" class="item" :hidden="commentCount[answerDict.id] == 0">
                <img class="subQues-com" src="../../assets/questionnairePic/comment.svg" alt="">
              </el-badge>

            </div>
          </el-popover>
        </div>
      </div>
      <!--      <div v-show="answerDict.answer && !editorShow[answerDictId]" v-html="answerDict.answer" class="sub-ques-ans BarlowMedium"></div>-->
      <div v-show="answerDict.answer && !editorShow[answerDictId]" v-html="transformAnswer(answerDict.answer,answerDict.vid)" class="sub-ques-ans BarlowMedium"></div>
      <editor v-if="editorShow[answerDictId]" :key="answerDictId.toString()" :id='answerDictId.toString()' v-model="textarea[answerDictId]" :init="init"></editor>
      <div v-if="editorShow[answerDictId]" style="display: flex;justify-content: right;margin-top: 16px">
        <div class="editor-cancel" @click="editorShow[answerDictId]=false">Cancel</div>
        <div class="editor-update" @click="uploadAnswer(answerDictId,answerDict.vid)">Update</div>
      </div>
    </div>
    <!--sub-question-->
    <div v-for="(item,index) in answerDict.subQuestionList">
      <div class="sub-ques">
        <div class="sub-ques-title">
          <div style="padding: 8px 16px;font-size: 16px" class="BarlowMedium">
            {{ item.question }}
          </div>
          <div style="display: flex" v-show="false">
            <img @click="subQuesEcho(item.id)" class="subQues-edit" src="../../assets/questionnairePic/edit.svg" alt="">
            <el-popover
                placement="left-start"
                width="400"
                trigger="click">
              <div>
                <div v-for="(item,index) in commentList" style="margin-bottom: 16px" :style="!item.hasRead? 'background-color:#F8F8F8;padding:8px;border-radius:4px':''">
                  <div style="display: flex">
                    <div style="margin-right: 8px">
                      <img style="width: 26px;height: 26px;" src="../../assets/groupPic/Avatar.png" alt="">
                    </div>
                    <div>
                      <div class="comment-name BarlowBold">{{item.userFullName}}</div>
                      <div class="comment-time BarlowMedium">{{item.createdTime|changeTime}}</div>
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
              <div @click="getComment(item.id)" slot="reference">
                <el-badge :value="commentCount[item.id]" class="item" :hidden="commentCount[item.id] == 0">
                  <img class="subQues-com" src="../../assets/questionnairePic/comment.svg" alt="">
                </el-badge>
              </div>
            </el-popover>
          </div>
        </div>
        <!--        <div v-show="item.answer && !editorShow[item.id]" v-html="item.answer" class="sub-ques-ans BarlowMedium"></div>-->
        <div v-show="item.answer && !editorShow[item.id]" v-html="transformAnswer(item.answer)" class="sub-ques-ans BarlowMedium"></div>

        <editor v-if="editorShow[item.id]" :key="item.id.toString()" :id='item.id.toString()' v-model="textarea[item.id]" :init="init"></editor>
        <div v-if="editorShow[item.id]" style="display: flex;justify-content: right;margin-top: 16px">
          <div class="editor-cancel" @click="editorShow[item.id]=false">Cancel</div>
          <div class="editor-update" @click="uploadAnswer(item.id,item.vid)">Update</div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import tinymce from "tinymce";
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
import axios from "axios";
import * as echarts from 'echarts';
import {
  curveOptionData,
  twoLineOptionData,
  pieOptionData,
  confusionMatrixOptionData,
  correlationMatrixOptionData,
  permutationImportanceOptionData,
  waterfallOptionData
} from "@/util/echartsOption";
import {Message} from "element-ui";
import option from "element-ui/packages/option";

export default {
  name: "QuestionnaireHistoryAnswer",
  components: { editor },
  props: {
    projectId: {
      type: String,
      required: true
    },
    questionId: {
      type: String,
      required: true
    },
    modelArtifactVersionId: {
      type: Number,
    }
  },
  created() {
    this.getFullComment()
    let id = this.projectId
    this.init = {
      skin_url: 'tinymce/skins/ui/oxide',
      content_css: 'tinymce/skins/content/answer_editor.css',
      autoresize_min_height : "60px",
      plugins: 'autoresize image link lists code table wordcount',
      toolbar: 'undo redo | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat',
      paste_data_images: true,
      id: this.projectId,
      images_dataimg_filter: function (img) {
        return img.hasAttribute('internal-blob');
      },
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
  },
  mounted() {
  },
  watch: {
    'questionId': function () {
      this.getQuestionnaireAnswer()
    }
  },
  data() {
    return {
      answerDict: {},
      init: {},
      rowAnswer: {},
      textarea: {},
      answerDictId: '',
      editorShow: {},
      commentList: [],
      addComment: '',
      commentId: '',
      commentCount: {},
    }
  },
  methods: {
    sendComment() {
      if (this.addComment) {
        this.$http.put(`/api/project/${this.projectId}/questionnaire/comment`,{questionId:this.commentId,comment:this.addComment}).then(res => {
          this.getComment(this.commentId)
          this.addComment = ''
        })
      }
    },
    getComment(id) {
      this.commentId = id
      // id = parseInt(id)
      this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${id}/comment`).then(res => {
        if(res.data[id]) {
          this.commentList = res.data[id].reverse()
        } else {
          this.commentList = []
        }
        this.commentCount[id] = 0
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
    transformAnswer(answer) {
      const el = document.createElement('div');
      el.innerHTML = answer
      const allImages =  el.getElementsByTagName('img')
      for (let i = 0; i < allImages.length; i++) {
        let plotImg = allImages[i];
        if (!plotImg.id) {
          plotImg.id = Math.ceil(Math.random()*999999999).toString()+'_plot';
        }
        // get echarts option data
        this.getPlotData(plotImg)
        // plotImg.remove();
      }
      return el.innerHTML
    },
    getPlotData(plotImg) {
      let plotFetch = {}
      plotFetch.modelArtifactVid = this.modelArtifactVersionId
      plotFetch.imgId = plotImg.id
      plotFetch.imgSrc = plotImg.src

      this.$nextTick(() => {
        let optionData = {}
        this.$http.post(`/api/project/${this.projectId}/plot`,plotFetch).then(res => {
          if (res.status == 200) {
            if (res.data.type == 'none') {
              optionData = null
            } else if (res.data.type == 'curve') {
              optionData = curveOptionData(res.data.data)
            } else if (res.data.type == 'two_line') {
              optionData = twoLineOptionData(res.data.data)
            } else if (res.data.type == 'pie') {
              optionData = pieOptionData(res.data.data)
            } else if (res.data.type == 'confusion_matrix') {
              optionData = confusionMatrixOptionData(res.data.data)
            } else if (res.data.type == 'correlation_matrix') {
              optionData = correlationMatrixOptionData(res.data.data)
            } else if (res.data.type == 'permutation_importance') {
              optionData = permutationImportanceOptionData(res.data.data)
            } else if (res.data.type == 'waterfall') {
              optionData = waterfallOptionData(res.data.data)
            } else {
              optionData = null;
            }
          } else  {
            let mess = Message({option})
            mess.closeAll()
          }
          if (optionData == null) {
            // echartsDiv.remove();
            return false;
          }
          let img = document.getElementById(plotImg.id)
          if (img == null) {
            return false;
          }
          let divChart = document.createElement("div");
          divChart.style.width = '80%'
          divChart.style.margin = 'auto'
          divChart.style.height = '500px'
          divChart.id = plotImg.id + '_plot'
          if (img.nextSibling) {
            img.parentNode.insertBefore(divChart, img.nextSibling);
          } else {
            img.parentNode.appendChild(divChart);
          }
          let echartsDiv = document.getElementById(plotImg.id+'_plot')

          echarts.dispose(echartsDiv)
          //let myChart = echarts.init(document.getElementById(plotImg.id+'_plot'));
          let myChart = echarts.init(echartsDiv);
          myChart.setOption(optionData);
          img.remove();
          return true;
        })
      })


    },
    getQuestionnaireAnswer() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${this.questionId}`).then(res => {
        if (res.status == 200) {
          this.answerDict = res.data
          this.answerDictId = res.data.id
          this.rowAnswer[res.data.id] = res.data.answer
          this.$set(this.editorShow,this.answerDictId,false)
          if (res.data.subQuestionList) {
            for (let i=0;i<res.data.subQuestionList.length;i++) {
              this.$set(this.editorShow,res.data.subQuestionList[i].id,false)
              this.rowAnswer[res.data.subQuestionList[i].id] = res.data.subQuestionList[i].answer
            }
          }
        }
      })
    },
    uploadAnswer(id,vid) {
      let updateAnswer = {}
      updateAnswer.projectId = this.projectId
      updateAnswer.questionId = id
      updateAnswer.basedQuestionVid = vid
      updateAnswer.answer = this.textarea[id]
      this.$http.post(`/api/project/${this.projectId}/questionnaire/answer`,updateAnswer).then( res=> {
        if (res.status == 200) {
          this.$message.success('Update successfully!')
          this.editorShow[id] = false
          this.getQuestionnaireAnswer()
        }
      })
    },
    mainQuesEcho(id) {
      this.textarea[id] = this.rowAnswer[id]
      this.editorShow[id] = true
    },
    subQuesEcho(id) {
      this.textarea[id] = this.rowAnswer[id]
      this.editorShow[id] = true
    }
  }
}
</script>

<style scoped lang="less">
.main-ques {
  font-size: 16px;
}
.sub-ques-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-right: 16px;
  background: #F5F7F9;
  border-radius: 4px 4px 0px 0px;
  border: 1px solid #D5D8DD;
}
.subQues-edit {
  width: 24px;
  height: 24px;
  margin-right: 8px;
  cursor: pointer;
}
.subQues-com {
  width: 24px;
  height: 24px;
  cursor: pointer;
}
.sub-ques-ans {
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 0px 0px 4px 4px;
  border-right: 1px solid #D5D8DD;
  border-left: 1px solid #D5D8DD;
  border-bottom: 1px solid #D5D8DD;
}
.editor-cancel {
  padding: 4px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
  margin-right: 8px;
  cursor: pointer;
}
.editor-update {
  padding: 4px 12px;
  background-color: #78BED3;
  border-radius: 4px;
  color: #FFFFFF;
  cursor: pointer;
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
  border-bottom: 1px solid #E0E0E0;
}
.divide_line {
  margin: 13px 0px;
  height: 1px;
  background-color: #CED3D9;
}
</style>