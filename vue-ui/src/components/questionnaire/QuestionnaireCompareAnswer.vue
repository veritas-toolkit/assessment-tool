<template>
  <div style="position: relative;height: 100%">
    <div class="vertical-line"></div>
<!--main-ques & ans-->
    <div class="main-ques-box">
      <div class="main-ques-box-left">
        <div class="main-ques">{{ mainBasedQuestion.serial }}. {{mainBasedQuestion.question}}</div>
        <div class="sub-ques" style="margin-top: 16px" v-show="!diffSummary.subList">{{ mainBasedQuestion.question }}</div>
        <div class="sub-ans" v-show="!diffSummary.subList"> {{mainBasedQuestion.answer}}
        </div>
      </div>
      <div class="main-ques-box-right">
        <div class="main-ques">{{ mainNewQuestion.serial }}. {{mainNewQuestion.question}}</div>
        <div class="sub-ques" style="margin-top: 16px" v-show="!diffSummary.subList">{{ mainNewQuestion.question }}</div>
        <div class="sub-ans" v-show="!diffSummary.subList">{{ mainNewQuestion.answer }}</div>
      </div>
    </div>
<!--    sub-ques & ans-->
    <div class="compare-box" v-for="(item,index) in diffSummary.subList" v-show="diffSummary.subList">
      <div class="compare-box-left">
        <div class="sub-ques">{{ item.basedQuestion.question }}</div>
        <div class="sub-ans">{{ item.basedQuestion.answer}}</div>
      </div>
      <div class="compare-box-right">
        <div class="sub-ques">{{ item.newQuestion.question }}</div>
        <div class="sub-ans">{{ item.newQuestion.answer }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "QuestionnaireCompareAnswer",
  props: {
    projectId: {
      type: String,
      required: true
    },
    questionId: {
      type: String,
      required: true
    },
    questionnaireVid: {
      type: String,
      required: true
    },
    compareFlag: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      diffSummary: {},
      mainBasedQuestion: {},
      mainNewQuestion: {},
    }
  },
  created() {
    console.log(this.questionnaireVid)
    console.log(this.compareFlag)

  },
  watch: {
    'questionnaireVid': function() {
      if (this.compareFlag) {
        this.getQuesDiffData()
      }
    },
    'questionId': function () {
      if (this.compareFlag) {
        this.getQuesDiffData()
      }
    }
  },
  methods: {
    getQuesDiffData() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/compare/question/${this.questionId}`,{params:{'basedQuestionnaireVid':this.questionnaireVid}}).then(res => {
        if (res.status == 200) {
          this.diffSummary = res.data
          this.mainBasedQuestion = res.data.basedQuestion
          this.mainNewQuestion = res.data.newQuestion

        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.vertical-line {
  width: 1px;
  height: 100%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  background-color: #D5D8DD;
}
.main-ques-box {
  display: flex;
}
.main-ques-box-left {
  padding: 0px 24px 16px 24px;
  width: calc(50% - 49px);
}
.main-ques-box-right {
  padding: 0px 24px 16px 24px;
  width: calc(50% - 49px);
}
.main-ques {
  font-size: 16px;
  font-family: BarlowBold;
  margin-top: 16px;
}
.compare-box {
  display: flex;
  justify-content: space-between;
}
.compare-box-left {
  padding: 0px 24px 16px 24px;
  width: calc(50% - 49px);
}
.compare-box-right {
  padding: 0px 24px 16px 24px;
  width: calc(50% - 49px);
}
.sub-ques {
  font-size: 16px;
  font-family: BarlowMedium;
  padding: 8px 16px;
  background: #F5F7F9;
  border-radius: 4px 4px 0px 0px;
  border: 1px solid #D5D8DD;
}
.sub-ans {
  padding: 8px 16px;
  font-size: 16px;
  font-family: BarlowMedium;
  border-radius: 0px 0px 4px 4px;
  border-right: 1px solid #D5D8DD;
  border-left: 1px solid #D5D8DD;
  border-bottom: 1px solid #D5D8DD;
}
</style>