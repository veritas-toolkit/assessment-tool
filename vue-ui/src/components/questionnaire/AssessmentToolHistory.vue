<template>
  <div style="height: 100%">
    <el-container ref="print">
      <!--AvatarBox-->
      <el-header>
        <div class="title BarlowMedium">
          <router-link :to="{path:'/projectPage',query: {id:projectId}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
          <div>Version history</div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="400px">
          <el-collapse accordion class="BarlowBold" v-model="activeName">
            <el-menu unique-opened :default-active="defaultActive" @select="choosenItem" active-text-color="#78BED3">
              <div v-for="(item1,index1) in questionnaireList" :key="index1" :style="index1 === 0? '': 'margin-top:16px'">
                <el-collapse-item :name="item1.partName" :style="index1 != questionnaireList.length -1? 'border-bottom: 1px solid #EBEEF5':''">
                  <template slot="title" class="partTitle BarlowBold">{{item1.partName}}. {{item1.partTitle}}</template>
                  <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.questionList" :key="item2.id" :index="item2.id.toString()">
                    <template slot="title">
                      <span>{{item2.part}}{{item2.partSerial}}. {{item2.content}}</span>
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
            <editor :key="serial.toString()" :id='serial.toString()' v-model="textarea[serial]" :init="init_summary"></editor>
          </div>
          <div v-show="subQuestionList.length != 0">
            <div class="questionStyle BarlowBold">{{question}}</div>
            <div v-for="(item,index) in subQuestionList" :key="item.id">
              <div class="subQuestionStyle BarlowMedium">{{item.content}}</div>
              <div class="hint BarlowMedium" v-show="item.hint">
                <span>{{item.hint}}</span>
              </div>
              <editor :key="item.id.toString()" :id='item.id.toString()' v-model="textarea[item.id]" :init="init"></editor>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
  import tinymce from 'tinymce/tinymce'
  import editor from '@tinymce/tinymce-vue'
  import 'tinymce/themes/silver/theme'
  import 'tinymce/icons/default/icons'
  import 'tinymce/plugins/image'
  import 'tinymce/plugins/link'
  import 'tinymce/plugins/code'
  import 'tinymce/plugins/table'
  import 'tinymce/plugins/autoresize'
  import 'tinymce/plugins/lists'
  import 'tinymce/plugins/contextmenu'
  import 'tinymce/plugins/wordcount'
  import 'tinymce/plugins/colorpicker'
  import 'tinymce/plugins/textcolor'

  export default {
    name: "AssessmentToolHistory",
    data() {
      return {
        activeName: 'A',
        tinymceHtml: '',
        init: {
          selector: 'textarea',
          skin_url: 'tinymce/skins/ui/oxide',
          autoresize_min_height : "60px",
          plugins: 'autoresize link lists code table wordcount',
          toolbar: 'undo redo | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat',
          branding: false,
          menubar: false,
          content_style: ' img { max-width:40%; display:block;height:auto; }'
        },
        init_summary: {
          selector: 'textarea',
          skin_url: 'tinymce/skins/ui/oxide',
          autoresize_min_height : "60px",
          plugins: 'autoresize link lists code table wordcount',
          toolbar: 'undo redo | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat',
          branding: false,
          menubar: false,
          content_style: ' img { max-width:40%; display:block;height:auto; }'
        },
        projectId: this.$route.query.projectId,
        versionId: this.$route.query.versionId,
        questionnaireList: [],
        triQuestionnaireList: [],
        subQuestionList: [],
        question: '',
        textarea: {},
        serial: '',
        answerForm: [],
        defaultActive: '',
      }
    },
    mounted () {
      console.log(this.projectId,this.versionId)
      tinymce.init({})
    },
    components: { editor },
    created() {
      this.getQuestionnaireInit()
    },
    methods: {
      getQuestionnaireInit() {
        this.$http.get(`/api/project/${this.projectId}/history/${this.versionId}/questionnaire`).then(res => {
          if(res.status == 200) {
            this.questionnaireList = res.data.partList
            let partList = res.data.partList
            this.defaultActive = String(res.data.partList[0].questionList[0].id)
            this.choosenItem(this.defaultActive)
            let triQuestionnaireList = []
            for (let i = 0; i<partList.length; i++) {
              for (let j = 0; j < partList[i].questionList.length; j++) {
                triQuestionnaireList.push(partList[i].questionList[j])
                this.$set(this.textarea,partList[i].questionList[j].id,'')
              }
            }
            triQuestionnaireList.map(item => {
              for (let i = 0; i<item.subQuestions.length; i++) {
                this.$set(this.textarea,item.subQuestions[i].id,'')
              }
            })
          }
        })
      },
      getQuestionnaire() {
        this.$http.get(`/api/project/${this.projectId}/history/${this.versionId}/questionnaire`).then(res => {
          if(res.status == 200) {
            this.questionnaireList = res.data.partList
          }
        })
      },
      choosenItem(index) {
        this.serial = index
        this.triQuestionnaireList = []
        this.$http.get(`/api/project/${this.projectId}/history/${this.versionId}/questionnaire`).then(res => {
          if(res.status == 200) {
            let partList = res.data.partList
            for (let i = 0; i<partList.length; i++) {
              for (let j = 0; j < partList[i].questionList.length; j++) {
                this.triQuestionnaireList.push(partList[i].questionList[j])
                this.textarea[partList[i].questionList[j].id] = partList[i].questionList[j].answer
              }
            }
            this.triQuestionnaireList.map(item => {
              if (item.id == index) {
                this.question = item.part + item.partSerial + '.' +item.content
                this.subQuestionList = item.subQuestions
                for (let i = 0; i<item.subQuestions.length; i++) {
                  this.textarea[item.subQuestions[i].id] = item.subQuestions[i].answer
                }
              }
            })
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
    margin-top: 24px;
    margin-bottom: 8px;
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
</style>