<template>
  <div style="position: relative;height: 100%">
<!--    :style="isCollapse?'width:calc(100% - 72px)':'width:calc(100% - 400px)'"-->
    <div class="compare-version-tag" style="width: 100%">
      <div style="width: 50%;display: flex;justify-content: right;margin-right: 24px">
        <span class="comp-version">{{creator.username}} {{compareVersionTime|changeTime}} Version</span>
      </div>
      <div style="width: 50%;display: flex;justify-content: left;margin-left: 24px">
        <span class="ur-version">Your Version</span>
      </div>
    </div>
    <div class="vertical-line"></div>
<!--main-ques & ans-->
    <div class="main-ques-box">
      <div class="main-ques-box-left">
        <div class="main-ques" v-html="mainBasedQuestion['questionHtml']">
        </div>
        <div class="sub-ques" style="margin-top: 16px" v-show="!diffSummary.subList" v-html="mainBasedQuestion['questionHtml']">
        </div>
        <div class="sub-ans" v-show="!diffSummary.subList" v-html="mainBasedQuestion['answerHtml']">
        </div>
      </div>

      <div class="main-ques-box-right">
        <div class="main-ques" v-html="mainNewQuestion['questionHtml']">
        </div>
        <div class="sub-ques" style="margin-top: 16px" v-show="!diffSummary.subList" v-html="mainNewQuestion['questionHtml']">
        </div>
        <div class="sub-ans" v-show="!diffSummary.subList" v-html="mainNewQuestion['answerHtml']">
        </div>

      </div>
    </div>
<!--    sub-ques & ans-->
    <div class="compare-box" v-for="(item,index) in diffSummary.subList" v-show="diffSummary.subList">
      <div class="compare-box-left">
<!--        html2string {{ item.basedQuestion.question }}-->
        <div class="sub-ques">
          <span v-html="item.basedQuestion['questionHtml']" />
        </div>

        <div class="sub-ans">
          <span v-html="item.basedQuestion['answerHtml']" />
        </div>
      </div>
      <div class="compare-box-right">
        <div class="sub-ques">
          <span v-html="item.newQuestion['questionHtml']" />
        </div>
        <div class="sub-ans">
          <span v-html="item.newQuestion['answerHtml']" />
        </div>
      </div>
    </div>
    <div id="result"></div>
  </div>
</template>

<script>
import {html2string} from "@/util/html2string";
import {compareDiff} from "@/util/compareDiff";

export default {
  name: "QuestionnaireCompareAnswer",
  props: {
    projectId: {
      type: String,
      required: true
    },
    creator: {
      type: Object,
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
    },
    compareVersionTime: {
      type: String,
    },
    isCollapse: {
      type: Boolean,
    }
  },
  data() {
    return {
      html2string: html2string,
      compareDiff: compareDiff,
      diffSummary: {},
      mainBasedQuestion: {},
      mainNewQuestion: {},
    }
  },
  created() {
    this.showCompareDiff()
  },
  watch: {
    'questionnaireVid': function() {
      if (this.compareFlag) {
        this.getQuesDiffData()
      }
    },
    'compareFlag': function() {
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
      this.$http.get(`/api/project/${this.projectId}/questionnaire/compare/question/${this.questionId}`,
          {
            params: {
              'basedQuestionnaireVid': this.questionnaireVid
            }
          })
          .then(res => {
            if (res.status == 200) {
              this.diffSummary = res.data
              if (res.data.subList) {
                for(let i=0;i<res.data.subList.length;i++) {
                  if (res.data.subList[i].basedQuestion == null) {
                    this.diffSummary.subList[i].basedQuestion = {
                      question: '',
                      answer: ''
                    }
                  }
                  if (res.data.subList[i].newQuestion == null) {
                    this.diffSummary.subList[i].newQuestion = {
                      question: '',
                      answer: ''
                    }
                  }
                }
              }
              if (res.data.basedQuestion) {
                this.mainBasedQuestion = res.data.basedQuestion
              } else {
                this.mainBasedQuestion.serial = ''
                this.mainBasedQuestion.question = ''
                this.mainBasedQuestion.answer = ''
              }
              if (res.data.newQuestion) {
                this.mainNewQuestion = res.data.newQuestion
              } else {
                this.mainNewQuestion.serial = ''
                this.mainNewQuestion.question = ''
                this.mainNewQuestion.answer = ''
              }

            }
          })
    },
    showCompareDiff() {
      let diff = compareDiff('it is a dog 1','it is a cat 1')
      console.log("====================")
      console.log(diff)
      // document.getElementById('result').innerHTML = diffHtml;
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
.compare-left {
  background: #FBE5E2;
  color: #E60101;
}
.compare-right {
  background: #D3F2E3;
  color: #00945A;
}
.compare-version-tag {
  display: flex;
  //position: fixed;
  font-family: BarlowMedium;
  font-size: 14px;
}
.comp-version {
  background-color: #FCB215;
  color: #FFFFFF;
  padding: 0px 8px;
  border-radius: 14px;
}
.ur-version {
  background-color: #78BED3;
  color: #FFFFFF;
  padding: 0px 8px;
  border-radius: 14px;
}
</style>

<style lang="less">
  .diff_del {
    background: #FBE5E2 !important;
    color: #E60101 !important;
  }
  .diff_add {
    background: #D3F2E3 !important;
    color: #00945A !important;
  }

</style>
