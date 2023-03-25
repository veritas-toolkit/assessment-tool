<template>
  <div style="height: 100%;background-color: #F2F2F2;transition: all .5s">
    <el-collapse ref="collapse" :style="isCollapse?'width:fit-content':''" class="BarlowBold" v-model="activeStep" accordion>
      <el-menu class="myMenu" unique-opened active-text-color="#78BED3"
               :default-active="activeQuestion" >
        <div class="main-question" v-for="(step, stepIndex) in menuData" :key="stepIndex">
          <el-collapse-item :disabled="step.mainQuestionList.length === 0"
                            :class="isCollapse ? 'myCollapseContent' : ''" :name="step.step"
                            :style="stepIndex === 0? 'margin-top: 6px;':''">
            <!-- Step -->
            <template slot="title" class="BarlowBold">
              <div class="step-box" :class="{'grey': step.mainQuestionList.length === 0}">
                <img class="step-pic" :style="isCollapse?'':'margin-right: 12px;'" :src="stepPic[step.serialNo]" alt="">
                <span class="collapse-step" v-show="!isCollapse">{{ step.step }}</span>
              </div>
            </template>
            <el-menu-item class="BarlowMedium" v-for="(mainQuestion, mainQuestionIndex) in step.mainQuestionList"
                          :key="mainQuestion.serial"
                          :name="mainQuestion.serial"
                          @click="clickMainQuestion(mainQuestion)"
                          :index="mainQuestion.serial">
              <!-- Main Question -->
              <template slot="title">
                <div class="ques-list">
                  <div>
                    <div class="ques-serial">
                      {{ mainQuestion.serial }}
                    </div>
                    <div class="vertical-line-box">
                      <div class="vertical-line"></div>
                    </div>
                  </div>
                  <div class="ques-content" v-show="!isCollapse">{{ mainQuestion.question }}
                    <span :class="mainQuestion.editType" v-show="!isUnmodified(mainQuestion)">
                      {{ mainQuestion['editType'] }}
                    </span>
                  </div>
                </div>
              </template>
            </el-menu-item>
          </el-collapse-item>
        </div>
      </el-menu>
    </el-collapse>
  </div>
</template>

<script>

export default {
  name: "QuestionnaireMenu",
  props: {
    isCollapse: {
      type: Boolean,
      required: true
    },
    menuData: {
      type: Array,
      required: true
    },
    defaultQuestion: {
      type: Object,
      required: false,
    }
  },
  data() {
    return {
      activeStep: 'Principles to Practice',
      activeQuestion: '',
      stepPic: {
        '0': require('../../assets/questionnairePic/portfolio.svg'),
        '1': require('../../assets/questionnairePic/department.svg'),
        '2': require('../../assets/questionnairePic/page.svg'),
        '3': require('../../assets/questionnairePic/issues.svg'),
        '4': require('../../assets/questionnairePic/screen.svg')
      },
      currentMainQuestion: {}
    }
  },
  watch: {
    "currentMainQuestion": function () {

      // console.log("activeQuestion: " + this.activeQuestion)
      // console.log("activeStep: " + this.activeStep)
    },
    "defaultQuestion": function () {
      if (this.defaultQuestion) {
        this.currentMainQuestion = this.defaultQuestion;
        this.activeQuestion = this.defaultQuestion.serial;
        this.activeStep = this.defaultQuestion.step;
      }
    }

  },
  created() {
    if (this.defaultQuestion) {
      this.currentMainQuestion = this.defaultQuestion;
    }
    // console.log("created");
    // console.log("activeQuestion: " + this.activeQuestion)
    // console.log("activeStep: " + this.activeStep)
  },
  methods: {
    clickMainQuestion(mainQuestion) {
      // console.log("mainQuestion:\n" + JSON.stringify(mainQuestion, null, 4))
      // console.log("this.currentMainQuestion:\n" + JSON.stringify(this.currentMainQuestion, null, 4))
      if (this.currentMainQuestion === mainQuestion || this.currentMainQuestion.serial === mainQuestion.serial) {
        return
      }
      this.currentMainQuestion = mainQuestion;
      let query = {...this.$route.query}
      query.q = mainQuestion.serial
      try {
        this.$router.replace({ query: query })
      } catch (e) {
        console.error(e)
      }
      this.$emit("getId", mainQuestion.id)
      this.$emit("selectQuestion", mainQuestion);
    },
    isUnmodified(mainQuestion) {
      return mainQuestion && mainQuestion['editType'] === "Unmodified";
    },


  },
  computed: {

  }
}
</script>

<style scoped lang="less">
.main-question {
  border-radius: 8px;
}

.el-menu {
  box-shadow: 0px 0px 8px #EDF2F6;
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

  > img {
    width: 24px;
    height: 24px;
  }
}
.grey {
  color: #A0A0A0;
}

.ques-list {
  display: flex;
  margin: 12px 0px;
}

.ques-serial {
  font-size: 16px;
  font-family: BarlowBold;
  width: 48px;
  text-align: center;
}

.ques-content {
  display: flex;
  align-items: center;
  font-size: 16px;
  padding-right: 12px;
  font-family: BarlowMedium;
  width: calc(100% - 50px);
}

.vertical-line-box {
  height: calc(100% - 24px);
  position: relative;
}

.vertical-line {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 2px;
  height: 100%;
  background-color: #E0E0E0;
}

.Edited {
  white-space: nowrap;
  background: #DBE8FF;
  border-radius: 12px;
  border: 1px solid #4D89FF;
  padding: 0px 8px;
  margin-left: 8px;
  color: #246DFF;
}

.New {
  white-space: nowrap;
  background: #D3F2E3;
  border-radius: 12px;
  border: 1px solid #44BC85;
  padding: 0px 8px;
  margin-left: 8px;
  color: #058F5A;
}

.Deleted {
  white-space: nowrap;
  background: #FBE5E2;
  border-radius: 12px;
  border: 1px solid #ED5234;
  padding: 0px 8px;
  margin-left: 8px;
  color: #ED5234;
}

.Unmodified {
  border: 1px solid #00ff00;
}
</style>