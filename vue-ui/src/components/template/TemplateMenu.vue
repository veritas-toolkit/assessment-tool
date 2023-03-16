<template>
  <div style="height: 100%;background-color: #F2F2F2">
    <el-collapse class="BarlowBold" v-model="activeName" accordion>
      <el-menu :default-active="defaultId" class="myMenu" unique-opened active-text-color="#78BED3" @select="handleSelect">
        <div class="main-question" v-for="(item1,index1) in menuList" :key="index1">
          <el-collapse-item :name="item1.step" :style="index1 == 0? 'margin-top: 6px;':''">
            <!--main question-->
            <template slot="title" class="BarlowBold">
              <div class="step-box">
                <img class="step-pic" style="margin-right: 12px" :src="stepPic[item1.serialNo]" alt="">
                <span class="collapse-step">{{item1.step}}</span>
              </div>
            </template>
            <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.mainQuestionList" :key="item2.id" :index="item2.id.toString()">
              <!--sub question-->
              <template slot="title">
                <div class="ques-list">
                  <div class="ques-serial">
                    <div class="serial-font">
                      {{item2.serial}}
                    </div>
                    <div class="ques-img">
                      <img src="../../assets/adminPic/upPage.png" @click.stop="upRecordMainQues(index2,item1.serialNo)" alt="">
                      <img src="../../assets/adminPic/downPage.png" @click.stop="downRecordMainQues(index2,item1.serialNo)" alt="">
                      <img src="../../assets/adminPic/writeProblem.png" @click="editMainQuesFlag[item2.id] = true" alt="">
                      <img src="../../assets/adminPic/deleteProblem.png" @click="deleteMainQues(item2.id)" alt="">
                    </div>

                  </div>
                  <div v-show="!editMainQuesFlag[item2.id]" class="ques-content">{{item2.question}}</div>
                  <el-input v-show="editMainQuesFlag[item2.id]" type="textarea" :rows="3" v-model="editMainQues[item2.id]" placeholder="Please input a new subquestion">
                  </el-input>
                  <div style="display: flex;justify-content: right">
                    <i class="el-icon-check" v-show="editMainQuesFlag[item2.id]" style="color: #78BED3;font-weight: bold;font-size: 20px" @click="editMainQuestion(item2.id,item2.vid)"></i>
                    <i class="el-icon-close" v-show="editMainQuesFlag[item2.id]" style="color: darkred;font-weight: bold;font-size: 20px;margin-left: 8px" @click="editMainQuesFlag[item2.id] = false"></i>
                  </div>
                </div>
              </template>
            </el-menu-item>
            <el-input v-show="addMainQuesFlag[item1.serialNo]" type="textarea" :rows="3" v-model="addMainQues[item1.serialNo]" placeholder="Please input a new main question">
            </el-input>
            <div style="display: flex;justify-content: right">
              <i class="el-icon-check" v-show="addMainQuesFlag[item1.serialNo]" style="color: #78BED3;font-weight: bold;font-size: 20px" @click="addMainQuestion(item1.serialNo)"></i>
              <i class="el-icon-close" v-show="addMainQuesFlag[item1.serialNo]" style="color: darkred;font-weight: bold;font-size: 20px;margin-left: 8px" @click="addMainQuesFlag[item1.serialNo] = false"></i>
            </div>
            <div class="add-main" @click="addMainQuesFlag[item1.serialNo] = true" v-show="!addMainQuesFlag[item1.serialNo]">
              <img class="add-main-pic" src="../../assets/questionnairePic/add.svg" alt="">
            </div>
          </el-collapse-item>
        </div>
      </el-menu>
    </el-collapse>
  </div>
</template>

<script>
export default {
  name: "TemplateMenu",
  props: {
    principle: {
      type: String,
      required: true
    },
    projectId: {
      type: String,
      required: true
    },
    menuData: {
      type: Array,
      required: true
    },
    defaultId: {
      type: String,
      required: true
    }
  },
  watch: {
    'menuData': function () {
      this.menuList = this.menuData
      if (this.menuData.length > 0) {
        for (let i = 0 ; i < this.menuData.length; i++) {
          if (this.menuData[i].mainQuestionList && this.menuData[i].mainQuestionList.length > 0) {
            for (let j = 0 ; j < this.menuData[i].mainQuestionList.length; j++) {
              this.$set(this.editMainQuesFlag,this.menuData[i].mainQuestionList[j].id,false)
              this.$set(this.editMainQues,this.menuData[i].mainQuestionList[j].id,this.menuData[i].mainQuestionList[j].question)
            }
          }
        }
      }
      // console.log('this.menuList',this.menuList)
    }
  },
  data() {
    return {
      activeName: 'Principles to Practice',
      stepPic: {
        '0': require('../../assets/questionnairePic/portfolio.svg'),
        '1': require('../../assets/questionnairePic/department.svg'),
        '2': require('../../assets/questionnairePic/page.svg'),
        '3': require('../../assets/questionnairePic/issues.svg'),
        '4': require('../../assets/questionnairePic/screen.svg')
      },
      principleMap: {
        "Generic" : "G",
        "Fairness" : "F",
        "Ethics & Accountability" : "EA",
        "Transparency" : "T"
      },
      addMainQuesFlag: {
        0: false,
        1: false,
        2: false,
        3: false,
        4: false,
      },
      addMainQues: {
        0: '',
        1: '',
        2: '',
        3: '',
        4: '',
      },
      menuList: this.menuData,
      editMainQuesFlag: {},
      editMainQues: {},
      deleteFlag: false
    }
  },
  created() {

  },
  methods: {
    handleSelect(questionId) {
      if (!this.deleteFlag) {
        this.$emit("getId",questionId)
      }
      this.deleteFlag = false
    },
    addMainQuestion(stepNo) {
      let newMainQues = {}
      newMainQues.projectId = this.projectId
      newMainQues.principle = this.principleMap[this.principle]
      newMainQues.step = stepNo
      newMainQues.question = this.addMainQues[stepNo]
      newMainQues.subQuestionList = []
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/question/new`,newMainQues).then(res => {
        if (res.status == 200) {
          this.$emit("getId",res.data.question.id.toString())
          this.menuList = res.data.toc.principleAssessments[this.principleMap[this.principle]].stepList
          this.addMainQues[stepNo] = ''
          this.$set(this.editMainQuesFlag,res.data.question.id.toString(),false)
          this.$set(this.editMainQues,res.data.question.id.toString(),res.data.question.question)
          this.addMainQuesFlag[stepNo] = false
        }
      })
    },
    deleteMainQues(id) {
      this.deleteFlag = true
      this.$http.delete(`/api/project/${this.projectId}/questionnaire/edit/question/${id}`).then(res => {
        if (res.status == 200) {
          this.menuList = res.data.principleAssessments[this.principleMap[this.principle]].stepList

        }
      })
    },
    editMainQuestion(id,vid) {
      let editQues = {}
      editQues.questionId = id
      editQues.basedQuestionVid = vid
      editQues.question = this.editMainQues[id]
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/question/${id}`,editQues).then(res => {
        if (res.status == 200) {
          this.$emit("getId",res.data.question.id.toString())
          this.$emit("getEditFlag",true)
          this.menuList = res.data.toc.principleAssessments[this.principleMap[this.principle]].stepList
          this.editMainQuesFlag[id] = false
        }
      })
    },
    upRecordMainQues(index,step) {
      let mainQuesRecorder = {}
      mainQuesRecorder.projectId = this.projectId
      mainQuesRecorder.principle = this.principleMap[this.principle]
      mainQuesRecorder.step = step
      let mainQuesList = this.menuList[step].mainQuestionList.map(item => {
        return item.id
      })
      if(index > 0) {
        let temp = mainQuesList[index - 1]
        mainQuesList[index - 1] = mainQuesList[index]
        mainQuesList[index] = temp
      } else {
        mainQuesList.push(mainQuesList.shift())
      }
      mainQuesRecorder.questionIdList = mainQuesList
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/reorder`,mainQuesRecorder).then(res => {
        if (res.status == 200) {
          if(index > 0) {
            this.$emit("getId",mainQuesList[index-1])
            this.$emit("getEditFlag",true)
          } else {
            this.$emit("getId",mainQuesList[mainQuesList.length-1])
            this.$emit("getEditFlag",true)
          }
          this.menuList = res.data.principleAssessments[this.principleMap[this.principle]].stepList
        }
      })
    },
    downRecordMainQues(index,step) {
      let mainQuesRecorder = {}
      mainQuesRecorder.projectId = this.projectId
      mainQuesRecorder.principle = this.principleMap[this.principle]
      mainQuesRecorder.step = step
      let mainQuesList = this.menuList[step].mainQuestionList.map(item => {
        return item.id
      })
      if(index < mainQuesList.length-1) {
        let temp = mainQuesList[index + 1]
        mainQuesList[index + 1] = mainQuesList[index]
        mainQuesList[index] = temp
      } else {
        mainQuesList.unshift(mainQuesList.pop())
      }
      mainQuesRecorder.questionIdList = mainQuesList
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/reorder`,mainQuesRecorder).then(res => {
        if (res.status == 200) {
          if(index < mainQuesList.length-1) {
            this.$emit("getId",mainQuesList[index+1])
            this.$emit("getEditFlag",true)
          } else {
            this.$emit("getId",mainQuesList[0])
            this.$emit("getEditFlag",true)
          }

          this.menuList = res.data.principleAssessments[this.principleMap[this.principle]].stepList
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.main-question {
  border-radius: 8px;
}
.el-menu {
  box-shadow:0px 0px 8px #EDF2F6;
  background-color: #F2F2F2;
}
.el-menu-item {
  height: auto;
  white-space: normal;
  word-break: break-word;
  line-height: 20px;
}
.el-collapse-item {
  padding: 6px 12px;
  border-right: none;
}
.step-box {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-family: BarlowBold;
  >img {
    width: 24px;
    height: 24px;
  }
}
.ques-list {

}
.ques-serial {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.serial-font {
  font-size: 16px;
  font-family: BarlowBold;
  width: 48px;
  text-align: center;
}
.ques-img {
  display: flex;
  margin-right: 12px;
  >img {
    width: 24px;
    height: 24px;
    margin-right: 8px;
  }
}
.ques-content {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-family: BarlowMedium;
  padding: 8px 16px;
}
.vertical-line-box {
  height: calc(100% - 24px);
  position: relative;
}
.vertical-line {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  width: 2px;
  height: 100%;
  background-color: #E0E0E0;
}
.add-main {
  display: flex;
  align-items: center;
  background: #EDF2F6;
  border-radius: 4px;
  margin: 8px 16px;
  cursor: pointer;
}
.add-main-pic {
  margin: auto;
  width: 24px;
  height: 24px;
}
</style>