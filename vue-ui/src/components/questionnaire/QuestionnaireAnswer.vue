<template>
  <div style="padding: 16px 24px">
    <div class="main-ques BarlowBold">{{answerDict.serial}}. {{answerDict.question}}</div>
    <div v-for="(item,index) in answerDict.subQuestionList">
      <div class="sub-ques">
        <div class="sub-ques-title">
          <div style="padding: 8px 16px;font-size: 16px" class="BarlowMedium">
            {{ item.question }}
          </div>
          <div style="display: flex">
            <img class="subQues-edit" src="../../assets/questionnairePic/edit.svg" alt="">
            <img class="subQues-com" src="../../assets/questionnairePic/comment.svg" alt="">
          </div>
        </div>
        <div class="sub-ques-ans BarlowMedium">
          {{ item.answer }}
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "QuestionnaireAnswer",
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

  },
  watch: {
    'questionId': function () {
      this.getQuestionnaireAnswer()
    }
  },
  data() {
    return {
      answerDict: {}
    }
  },
  methods: {
    getQuestionnaireAnswer() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${this.questionId}`).then(res => {
        if (res.status == 200) {
          this.answerDict = res.data
          console.log(this.answerDict)
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
}
.subQues-com {
  width: 24px;
  height: 24px;
  margin-right: 16px;
}
.sub-ques-ans {
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 0px 0px 4px 4px;
  border-right: 1px solid #D5D8DD;
  border-left: 1px solid #D5D8DD;
  border-bottom: 1px solid #D5D8DD;
}
</style>