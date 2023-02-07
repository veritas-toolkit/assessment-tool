<template>
  <div style="padding: 16px 24px">
    <div class="main-ques BarlowBold">{{answerDict.serial}}. {{answerDict.question}}</div>
<!--main-question-->
    <div class="sub-ques" v-show="!answerDict.subQuestionList">
      <div class="sub-ques-title">
        <div style="padding: 8px 16px;font-size: 16px" class="BarlowMedium">
          {{ answerDict.question }}
        </div>
        <div style="display: flex">
          <img @click="editorShow[answerDictId] = true" class="subQues-edit" src="../../assets/questionnairePic/edit.svg" alt="">
          <img class="subQues-com" src="../../assets/questionnairePic/comment.svg" alt="">
        </div>
      </div>
      <div v-show="answerDict.answer" class="sub-ques-ans BarlowMedium">
        {{ answerDict.answer }}
      </div>
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
          <div style="display: flex">
            <img @click="editorShow[item.id] = true" class="subQues-edit" src="../../assets/questionnairePic/edit.svg" alt="">
            <img class="subQues-com" src="../../assets/questionnairePic/comment.svg" alt="">
          </div>
        </div>
        <div v-show="item.answer" v-html="item.answer" class="sub-ques-ans BarlowMedium"></div>
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

export default {
  name: "QuestionnaireAnswer",
  components: { editor },
  props: {
    projectId: {
      type: String,
      required: true
    },
    questionId: {
      type: String,
      required: true
    }
  },
  created() {
    this.init = {
      selector: 'textarea',
      skin_url: 'tinymce/skins/ui/oxide',
      autoresize_min_height : "60px",
      plugins: 'autoresize image link lists code table wordcount',
      toolbar: 'undo redo | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat',
      paste_data_images: true,
      id: this.$route.query.id,
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
      textarea: {},
      answerDictId: '',
      editorShow: {},
    }
  },
  methods: {
    getQuestionnaireAnswer() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${this.questionId}`).then(res => {
        if (res.status == 200) {
          this.answerDict = res.data
          this.answerDictId = res.data.id
          this.$set(this.editorShow,this.answerDictId,false)
          if (res.data.subQuestionList) {
            for (let i=0;i<res.data.subQuestionList.length;i++) {
              this.$set(this.editorShow,res.data.subQuestionList[i].id,false)
            }
          }
          console.log(this.answerDict)
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
  margin-right: 16px;
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
</style>