<template>
  <div style="height: calc(100% - 48px);padding: 16px 24px">
    <div class="main-ques">
      {{ questionData.serial }}. {{ questionData.question }}
    </div>
    <div v-for="(item,index) in subQuestionList" class="sub-ques "> <!--hover-visible-->
      <span v-show="!editSubQuesFlag[item.id]">{{ item.question }}</span>
      <el-input v-show="editSubQuesFlag[item.id]" v-model="editSubQues[item.id]"
                placeholder="Please input a new subquestion">
        <i slot="suffix" class="el-input__icon el-icon-check" @click="writeSubQues(item.id,item.vid)"></i>
        <i slot="suffix" class="el-input__icon el-icon-close" @click="editSubQuesFlag[item.id] = false"></i>
      </el-input>
      <div>
        <div class="ques-img " v-show="!editSubQuesFlag[item.id]"> <!--target-->
          <img src="../../assets/adminPic/upPage.png" @click="upRecordSubQues(index)" alt="">
          <img src="../../assets/adminPic/downPage.png" @click="downRecordSubQues(index)" alt="">
          <img src="../../assets/adminPic/writeProblem.png" @click="editSubQuesFlag[item.id] = true" alt="">
          <img src="../../assets/adminPic/deleteProblem.png" @click="deleteSubQues(item.id)" alt="">
        </div>
      </div>
    </div>
    <el-input v-show="addSubQuesFlag" style="margin-top: 24px" type="textarea" :rows="3" v-model="addSubQues"
              placeholder="Please input a new subquestion">
    </el-input>
    <div v-show="addSubQuesFlag" style="display: flex;justify-content: right">
      <i class="el-icon-check" style="color: #78BED3;font-weight: bold;font-size: 20px" @click="addSubQuestion"></i>
      <i class="el-icon-close" style="color: darkred;font-weight: bold;font-size: 20px;margin-left: 8px"
         @click="handleAddSubQues"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: "TemplateSubQuestion",
  props: {
    projectId: {
      type: String,
      required: true
    },
    questionId: {
      type: String,
      required: true
    },
    addSubQuesFlag: {
      type: Boolean
    },
    editFlag: {
      type: Boolean
    }
  },
  data() {
    return {
      questionData: {},
      addSubQues: '',
      subQuestionList: [],
      editSubQuesFlag: {},
      editSubQues: {},
    }
  },
  watch: {
    'questionId': function () {
      if (!this.editFlag) {
        this.getSubQuestion()
        this.$emit('updateFlag', false)
      }
    },
    'editFlag': function () {
      if (this.editFlag) {
        this.getSubQuestion()
        this.$emit('getEditFlag', false)
      }
    }
  },
  created() {
  },
  methods: {
    getSubQuestion() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${this.questionId}`).then(res => {
        if (res.status == 200) {
          this.questionData = res.data
          this.subQuestionList = res.data.subQuestionList
          if (res.data.subQuestionList && res.data.subQuestionList.length) {
            for (let i = 0; i < res.data.subQuestionList.length; i++) {
              this.$set(this.editSubQuesFlag, res.data.subQuestionList[i].id, false)
              this.$set(this.editSubQues, res.data.subQuestionList[i].id, res.data.subQuestionList[i].question)
            }
          }
        }
      })
    },
    handleAddSubQues() {
      this.$emit('updateFlag', false)
    },
    addSubQuestion() {
      let newSubQues = {}
      newSubQues.beforeQuestionId = null;
      newSubQues.question = this.addSubQues
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/question/${this.questionId}/sub/new`, newSubQues).then(res => {
        if (res.status == 200) {
          this.subQuestionList = res.data.subQuestionList
          this.addSubQues = ''
          this.$emit('updateFlag', false)
        }
      })
    },
    deleteSubQues(id) {
      this.$confirm('Confirm delete?', {type: 'warning'}).then(() => {
        this.$http.delete(`/api/project/${this.projectId}/questionnaire/edit/question/${this.questionId}/sub/${id}`).then(res => {
          if (res.status == 200) {
            this.subQuestionList = res.data.subQuestionList
          }
        })
      })
    },
    writeSubQues(id, vid) {
      let editSubQues = {}
      editSubQues.questionId = id
      editSubQues.basedQuestionVid = vid
      editSubQues.question = this.editSubQues[id]
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/question/${this.questionId}/sub/${id}`, editSubQues).then(res => {
        if (res.status == 200) {
          this.subQuestionList = res.data.subQuestionList
          this.editSubQuesFlag[id] = false
        }
      })
    },
    upRecordSubQues(index) {
      let subQuestionReorder = {}
      subQuestionReorder.basedQuestionnaireVid = this.questionData.vid
      subQuestionReorder.mainQuestionId = this.questionData.id
      let subQuesList = this.subQuestionList.map(item => {
        return item.id
      })
      if (index > 0) {
        let temp = subQuesList[index - 1]
        subQuesList[index - 1] = subQuesList[index]
        subQuesList[index] = temp
      } else {
        subQuesList.push(subQuesList.shift())
      }
      subQuestionReorder.orderedSubQuestionIdList = subQuesList
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/question/${this.questionId}/sub`, subQuestionReorder).then(res => {
        if (res.status == 200) {
          this.subQuestionList = res.data.subQuestionList
        }
      })
    },
    downRecordSubQues(index) {
      let subQuestionReorder = {}
      subQuestionReorder.basedQuestionnaireVid = this.questionData.vid
      subQuestionReorder.mainQuestionId = this.questionData.id
      let subQuesList = this.subQuestionList.map(item => {
        return item.id
      })
      if (index < subQuesList.length - 1) {
        let temp = subQuesList[index + 1]
        subQuesList[index + 1] = subQuesList[index]
        subQuesList[index] = temp
      } else if (index == subQuesList.length - 1) {
        subQuesList.unshift(subQuesList.pop())
      }
      subQuestionReorder.orderedSubQuestionIdList = subQuesList
      this.$http.post(`/api/project/${this.projectId}/questionnaire/edit/question/${this.questionId}/sub`, subQuestionReorder).then(res => {
        if (res.status == 200) {
          this.subQuestionList = res.data.subQuestionList
        }
      })
    }
  },
}
</script>

<style scoped lang="less">
.main-ques {
  font-size: 16px;
  font-family: BarlowBold;
}

.sub-ques {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  > span {
    font-size: 16px;
    font-family: BarlowMedium;
  }
}

.ques-img {
  display: flex;

  > img {
    width: 24px;
    height: 24px;
    margin-left: 8px;
  }
}

.target {
  display: none;
}

.hover-visible:hover .target {
  display: block;
}
</style>