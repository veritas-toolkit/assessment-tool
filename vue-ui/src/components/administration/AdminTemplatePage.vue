<template>
  <div style="height: 100%">
    <el-container ref="print">
      <!--AvatarBox-->
      <el-header>
        <div class="title BarlowMedium">
          <router-link :to="{path:'/administration',query: {activeName: 'fourth'}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>
          <div>Questionnaire Template</div>
        </div>
        <!--MoreActions-->
        <div v-show="part == 'A' || part == 'E'" class="BarlowMedium" style="display: flex;margin-right: 24px">
          <div v-show="type != 'SYSTEM'" class="export BlackBorder" @click="modifyQuesVisibleFuc"><span>Modify question</span></div>
        </div>
      </el-header>
      <el-container class="container">
        <el-aside width="400px">
          <el-collapse accordion class="BarlowBold" v-model="activeCollapse">
            <el-menu :default-active="defaultActive" @select="choosenItem" active-text-color="#78BED3">
              <div v-for="(item1,index1) in questionnaireList" :key="index1" :style="index1 === 0? '': 'margin-top:16px'">
                <!--<div class="menuDiv" :style="index1 === 0? 'height:0': 'height:16px'"></div>-->
                <el-collapse-item :name="item1.partName" :style="index1 != questionnaireList.length -1? 'border-bottom: 1px solid #EBEEF5':''">
                  <template slot="title" class="partTitle BarlowBold">{{item1.partName}}. {{item1.partTitle}}</template>
                  <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.questionList" :key="item2.id" :index="item2.id.toString()">
                    <template slot="title">
                      <div class="hover-visible">
                        <div class="BarlowBold secondTitle">
                          <div>{{item2.part}}{{item2.partSerial}}</div>
                          <div class="target" v-show="item2.editable">
                            <img v-show="type != 'SYSTEM'" class="secondTitlePic" src="../../assets/adminPic/deleteProblem.png" alt="" @click="deleteQuestion(item2.id)">
                          </div>
                        </div>
                        <div>{{item2.content}}</div>
                      </div>
                    </template>
                  </el-menu-item>
                  <el-input style="width: calc(100% - 32px);margin-left: 16px;margin-top: 12px" type="textarea" :rows="3" v-model="newQues[index1]" v-if="clickItem[index1]">
                  </el-input>
                  <div style="display: flex;justify-content: space-between" v-if="clickItem[index1]">
                    <div></div>
                    <div style="margin-right: 16px">
                      <i slot="suffix" class="el-input__icon el-icon-check" @click="addQues(newQues[index1],item1.partName,index1)"></i>
                      <i slot="suffix" class="el-input__icon el-icon-close" @click="clickItem[index1] = false"></i>
                    </div>
                  </div>
                  <div v-show="type != 'SYSTEM'" class="insert-problem BarlowMedium" @click="addNewQusetion(index1)">+</div>
                </el-collapse-item>
              </div>
            </el-menu>
          </el-collapse>
        </el-aside>
        <el-main style="height: calc(100%)">
          <div style="padding: 16px 24px;height: calc(100% - 73px)">
            <div v-show="subQuestionList.length == 0">
              <div class="questionStyle BarlowBold" style="margin-bottom: 24px">{{question}}</div>
            </div>
            <div v-show="subQuestionList.length != 0">
              <div class="questionStyle BarlowBold">{{question}}</div>
              <div v-for="(item,index) in subQuestionList" :key="item.id">
                <div class="hover-visible" style="display: flex;align-items: center">
                  <div class="subQuestionStyle BarlowMedium">{{item.content}}</div>
                  <div class="target" v-show="item.editable">
                    <img v-show="type != 'SYSTEM'" class="secondTitlePic" src="../../assets/adminPic/upPage.png" alt="" @click="upPage(subQuestionList,index)">
                    <img v-show="type != 'SYSTEM'" class="secondTitlePic" src="../../assets/adminPic/downPage.png" alt="" @click="downPage(subQuestionList,index)">
                    <img v-show="type != 'SYSTEM'" class="secondTitlePic" src="../../assets/adminPic/deleteProblem.png" alt="" @click="deleteSubQuestion(subQuestionList,item.id,index)">
                  </div>
                </div>
                <div class="hint BarlowMedium" v-show="item.hint">
                  <div>
                    <span>{{item.hint}}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-for="(item,index) in newSubQuestionList" :key="index">
              <el-input style="margin-top: 16px" v-model="item.content" placeholder="Please input a new subquestion">
                <i slot="suffix" class="el-input__icon el-icon-check" @click="addSubQuestion(item.content,index)"></i>
                <i slot="suffix" class="el-input__icon el-icon-close" @click="deleteNewSub(index)"></i>
              </el-input>
            </div>
            <div style="display: flex">
              <div v-show="(part == 'A' || part == 'E') && type != 'SYSTEM'" class="addSubquestion BarlowBold" style="cursor: pointer" @click="addNewSub"><span>+ Add subquestion</span></div>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="modifyQuesVisible" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">Modify question</span></template>
      <el-form ref="modifyQuestionFormRefs" label-position="top" label="450px" :model="modifyQuestionForm">
        <el-form-item class="login" :label="partName + '.' + ' '+'question'">
          <el-input type="textarea" autosize placeholder="Please modify the question" v-model="modifyQuestionForm[serial]"></el-input>
        </el-form-item>
        <el-form-item v-for="(item,index) in subQuestionList" class="login" :label="partName+'.'+(index+1)+'.'+' '+'subquestion'">
          <el-input type="textarea" autosize placeholder="Please modify the question" v-model="modifyQuestionForm[item.id]"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button class="BlackBorder" @click="modifyQuesVisible = false">Cancel</el-button>
        <el-button class="GreenBC" style="color: #FFF" @click="modifyQues">Modify</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "AdminTemplatePage",
    data() {
      return {
        activeCollapse: 'A',
        templateId: this.$route.query.templateId,
        questionnaireList: [],
        subQuestionList: [],
        newSubQuestionList: [],
        question: '',
        serial: 1,
        answerForm: [],
        contentEditable: {
        },
        secondQuestion: {},
        clickItem: {
          '0':false,
          '1':false,
          '2':false,
          '3':false,
          '4':false
        },
        newQues: {},
        defaultActive: '',
        modifyQuesVisible: false,
        modifyQuestionForm: {
        },
        partName: '',
        part: '',
        type: '',
      }
    },
    created() {
      this.getQuestionnaireInit()
    },
    methods: {
      modifyQuesVisibleFuc() {
        this.choosenItem(this.serial)
        this.modifyQuesVisible = true
      },
      modifyQues() {
        let pushModifyForm = {}
        let subList = []
        for (let key in this.modifyQuestionForm) {
          let subForm = {}
          subForm.id = key
          subForm.content = this.modifyQuestionForm[key]
          subList.push(subForm)
        }
        pushModifyForm.id = subList[0].id
        pushModifyForm.content = subList[0].content
        subList.shift()
        pushModifyForm.subQuestions = subList
        this.$http.post(`/api/admin/questionnaire/${this.templateId}/question`,pushModifyForm).then(res => {
          if (res.status == 200) {
            this.$message.success('Modify successfully')
            this.reload(this.serial)
            this.getQuestionnaire()
            this.modifyQuesVisible = false
          }
        })
      },
      addQues(content,partName,index) {
        console.log(content,partName)
        this.$http.put(`/api/admin/questionnaire/${this.templateId}/question`,{part:partName, content:content}).then(res => {
          if (res.status == 200) {
            this.$message.success('Add successfully')
            this.getQuestionnaire()
            this.clickItem[index] = false
          }
        })
      },
      deleteQuestion(id) {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          this.$http.delete(`/api/admin/questionnaire/${this.templateId}/question`,{params:{'questionId' : id}}).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.getQuestionnaire()
            }
          })
        })
      },
      addNewQusetion(index) {
        this.clickItem = {'0':false, '1':false, '2':false, '3':false, '4':false},
          this.clickItem[index] = true
      },
      addNewSub() {
        this.newSubQuestionList.push({content:''})
      },
      deleteNewSub(index) {
        this.newSubQuestionList.splice(index,1)
      },
      addSubQuestion(content,index) {
        this.reload(this.serial)
        this.secondQuestion.subQuestions.push({content: content})
        this.$http.post(`/api/admin/questionnaire/${this.templateId}/question`,this.secondQuestion).then(res => {
          if (res.status == 200) {
            this.$message.success('Add successfully')
            this.reload(this.serial)
            this.newSubQuestionList.splice(index,1)
          }
        })
      },
      reload(index) {
        this.$http.get(`/api/admin/questionnaire/${this.templateId}`).then(res => {
          if(res.status == 200) {
            let partList = res.data.partList
            let triQuestionnaireList = []
            for (let i = 0; i<partList.length; i++) {
              for (let j = 0; j < partList[i].questionList.length; j++) {
                triQuestionnaireList.push(partList[i].questionList[j])
              }
            }
            triQuestionnaireList.map(item => {
              if (item.id == index) {
                this.secondQuestion = item
                this.question = item.part + item.partSerial + '.' +item.content
                this.subQuestionList = item.subQuestions
              }
            })
          }
        })
      },
      upPage(subQuestionList,index) {
        if(index > 0) {
          let temp = subQuestionList[index - 1]
          subQuestionList[index - 1] = subQuestionList[index]
          subQuestionList[index] = temp
        } else if (index == 0) {
          subQuestionList.push(subQuestionList.shift())
        }
        this.secondQuestion.subQuestions = subQuestionList
        this.$http.post(`/api/admin/questionnaire/${this.templateId}/question`,this.secondQuestion).then(res => {
          if (res.status == 200) {
            this.$message.success('Exchange successfully')
            this.getQuestionnaire()
          }
        })
      },
      downPage(subQuestionList,index) {
        if(index < subQuestionList.length-1) {
          let temp = subQuestionList[index + 1]
          subQuestionList[index + 1] = subQuestionList[index]
          subQuestionList[index] = temp
        } else if (index == subQuestionList.length-1) {
          subQuestionList.unshift(subQuestionList.pop())
        }
        this.secondQuestion.subQuestions = subQuestionList
        this.$http.post(`/api/admin/questionnaire/${this.templateId}/question`,this.secondQuestion).then(res => {
          if (res.status == 200) {
            this.$message.success('Exchange successfully')
            this.getQuestionnaire()
          }
        })
      },
      deleteSubQuestion(subQuestionList,id,index) {
        this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
          subQuestionList.splice(index,1)
          this.$http.delete(`/api/admin/questionnaire/${this.templateId}/question`,{params:{'subQuestionId' : id}}).then(res => {
            if (res.status == 200) {
              this.$message.success('Delete successfully')
              this.getQuestionnaire()
            }
          })
        })
      },
      getQuestionnaireInit() {
        this.$http.get(`/api/admin/questionnaire/${this.templateId}`).then(res => {
          if(res.status == 200) {
            this.type = res.data.type
            this.questionnaireList = res.data.partList
            let partList = res.data.partList
            this.defaultActive = String(res.data.partList[0].questionList[0].id)
            this.choosenItem(this.defaultActive)
          }
        })
      },
      getQuestionnaire() {
        this.$http.get(`/api/admin/questionnaire/${this.templateId}`).then(res => {
          if(res.status == 200) {
            this.questionnaireList = res.data.partList
          }
        })
      },
      choosenItem(index) {
        this.serial = index
        this.newSubQuestionList = []
        this.$http.get(`/api/admin/questionnaire/${this.templateId}`).then(res => {
          if(res.status == 200) {
            let partList = res.data.partList
            let triQuestionnaireList = []
            for (let i = 0; i<partList.length; i++) {
              for (let j = 0; j < partList[i].questionList.length; j++) {
                triQuestionnaireList.push(partList[i].questionList[j])
              }
            }
            this.modifyQuestionForm = {}
            triQuestionnaireList.map(item => {
              if (item.id == index) {
                this.secondQuestion = item
                this.partName = item.part + item.partSerial
                this.part = item.part
                this.$set(this.modifyQuestionForm,item.id,'')
                this.modifyQuestionForm[item.id] = item.content
                this.question = item.part + item.partSerial + '.' +item.content
                this.subQuestionList = item.subQuestions
                this.subQuestionList.map(item => {
                  this.$set(this.modifyQuestionForm,item.id,'')
                  this.modifyQuestionForm[item.id] = item.content
                })
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
  .container {
    height: calc(100% - 600px);
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
    height: 38px;
    padding: 0 16px;
    border-radius: 4px;
    > span {
      font-size: 16px;
      line-height: 40px;
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
  .questionStyle {
    font-size: 16px;
    font-weight: bold;
  }
  .subQuestionStyle {
    height: 24px;
    line-height: 24px;
    margin: 24px 0px;
    font-size: 16px;
    font-weight: 400;
    color: #333333;
  }
  .hint {
    display: inline-block;
    width: auto;
    text-align: center;
    line-height: 24px;
    height: 24px;
    > div {
      display: flex;
      > i {
        color: #666666;
      }
      > span {
        background-color: #F2F2F2;
        padding: 0px 12px;
        border-radius: 4px;
        font-size: 14px;
        color: #666666;
        margin-right: 8px;
      }
    }
  }
  .add-hint {
    font-size: 18px;
    width: 32px;
    height: 22px;
    line-height: 22px;
    text-align: center;
    border: 1px solid #D7D9DB;
    border-radius: 4px;
  }
  .submit {
    margin-top: 16px;
    float: right;
  }
  .secondTitle {
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .secondTitlePic {
    cursor: pointer;
    margin-left: 12px;
    width: 24px;
    height: 24px;
  }
  .target {
    display: none;
  }
  .hover-visible:hover .target{
    display: block;
  }
  .el-menu-item:hover {
    background-color: #F0F0F0;
  }
  .el-menu-item.is-active {
    background-color: #EDF2F6;
    color: #000000 !important;
  }
  .insert-problem {
    cursor: pointer;
    margin: 0px 16px 0px 16px;
    border: 1px solid;
    border-radius: 4px;
    line-height: 30px;
    text-align: center;
    height: 30px;
    font-size: 26px;
  }
  .modifyQuestion {
    border: 1px solid;
    border-radius: 4px;
    padding: 8px;
  }
  .addSubquestion {
    margin-top: 16px;
    border: 1px solid;
    border-radius: 4px;
    padding: 8px;
  }
  .add-subquestion {
    border-top: 1px solid #CED3D9;
    height: 72px;
    > div {
      margin: 16px;
      line-height: 38px;
      text-align: center;
      border: 1px solid #000000;
      border-radius: 4px;
      height: 38px;
    }
  }
  .el-input__icon {
    font-weight: bold;
    cursor: pointer;
    color: #888888;
    margin-left: 8px;
    font-size: 18px;
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
</style>