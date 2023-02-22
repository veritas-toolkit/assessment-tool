<template>
  <div style="position: relative;height: 100%">
    <div class="vertical-line"></div>
<!--main-ques & ans-->
    <div class="main-ques-box">
      <div class="main-ques-box-left">
        <div class="main-ques">
          <span :class="diffItem[0]== -1?'compare-left':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.serial).text, html2string(mainNewQuestion.serial).text)" v-show="diffItem[0] == 0 || diffItem[0]== -1">
            {{diffItem[1]}}
          </span>
          <span>. </span>
          <span :class="diffItem[0]== -1?'compare-left':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.question).text, html2string(mainNewQuestion.question).text)" v-show="diffItem[0] == 0 || diffItem[0]== -1">
            {{diffItem[1]}}
          </span>
<!--          {{ mainBasedQuestion.serial }}. {{mainBasedQuestion.question}}-->
        </div>
        <div class="sub-ques" style="margin-top: 16px" v-show="!diffSummary.subList">
          <span :class="diffItem[0]== -1?'compare-left':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.question).text, html2string(mainNewQuestion.question).text)" v-show="diffItem[0] == 0 || diffItem[0]== -1">
            {{diffItem[1]}}
          </span>
<!--          {{ mainBasedQuestion.question }}-->
        </div>
        <div class="sub-ans" v-show="!diffSummary.subList">
          <span :class="diffItem[0]== -1?'compare-left':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.answer).text, html2string(mainNewQuestion.answer).text)" v-show="diffItem[0] == 0 || diffItem[0]== -1">
            {{diffItem[1]}}
          </span>
<!--          {{mainBasedQuestion.answer}}-->
        </div>
      </div>
      <div class="main-ques-box-right">
        <div class="main-ques">
          <span :class="diffItem[0]== 1?'compare-right':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.serial).text, html2string(mainNewQuestion.serial).text)" v-show="diffItem[0] == 0 || diffItem[0]== 1">
            {{diffItem[1]}}
          </span>
          <span>. </span>
          <span :class="diffItem[0]== 1?'compare-right':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.question).text, html2string(mainNewQuestion.question).text)" v-show="diffItem[0] == 0 || diffItem[0]== 1">
            {{diffItem[1]}}
          </span>
<!--          {{ mainNewQuestion.serial }}. {{mainNewQuestion.question}}-->
        </div>
        <div class="sub-ques" style="margin-top: 16px" v-show="!diffSummary.subList">
          <span :class="diffItem[0]== 1?'compare-right':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.question).text, html2string(mainNewQuestion.question).text)" v-show="diffItem[0] == 0 || diffItem[0]== 1">
            {{diffItem[1]}}
          </span>
<!--          {{ mainNewQuestion.question }}-->
        </div>
        <div class="sub-ans" v-show="!diffSummary.subList">
          <span :class="diffItem[0]== 1?'compare-right':''" v-for="(diffItem,index) in compareDiff(html2string(mainBasedQuestion.answer).text, html2string(mainNewQuestion.answer).text)" v-show="diffItem[0] == 0 || diffItem[0]== 1">
            {{diffItem[1]}}
          </span>
<!--          {{ mainNewQuestion.answer }}-->
        </div>
      </div>
    </div>
<!--    sub-ques & ans-->
    <div class="compare-box" v-for="(item,index) in diffSummary.subList" v-show="diffSummary.subList">
      <div class="compare-box-left">
<!--        html2string {{ item.basedQuestion.question }}-->
        <div class="sub-ques">
          <span :class="diffItem[0]== -1?'compare-left':''" v-for="(diffItem,index) in compareDiff(html2string(item.basedQuestion.question).text, html2string(item.newQuestion.question).text)" v-show="diffItem[0] == 0 || diffItem[0]== -1">
            {{diffItem[1]}}
          </span>
        </div>
        <div class="sub-ans">
          <span :class="diffItem[0]== -1?'compare-left':''" v-for="(diffItem,index) in compareDiff(html2string(item.basedQuestion.answer).text, html2string(item.newQuestion.answer).text)" v-show="diffItem[0] == 0 || diffItem[0]== -1">
            {{diffItem[1]}}
          </span>
        </div>
      </div>
      <div class="compare-box-right">
        <div class="sub-ques">
          <span :class="diffItem[0]== 1?'compare-right':''" v-for="(diffItem,index) in compareDiff(html2string(item.basedQuestion.question).text, html2string(item.newQuestion.question).text)" v-show="diffItem[0] == 0 || diffItem[0]== 1">
            {{diffItem[1]}}
          </span>
        </div>
        <div class="sub-ans">
          <span :class="diffItem[0]== 1?'compare-right':''" v-for="(diffItem,index) in compareDiff(html2string(item.basedQuestion.answer).text, html2string(item.newQuestion.answer).text)" v-show="diffItem[0] == 0 || diffItem[0]== 1">
            {{diffItem[1]}}
          </span>
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
      html2string: html2string,
      compareDiff: compareDiff,
      diffSummary: {},
      mainBasedQuestion: {},
      mainNewQuestion: {},
      diff: []
    }
  },
  created() {
    this.showCompareDiff()
    // console.log(this.compareDiff('','1212'))
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
    },
    showCompareDiff() {
      let diff = compareDiff('it is a dog 1','it is a cat 1')
      this.diff = diff


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
</style>