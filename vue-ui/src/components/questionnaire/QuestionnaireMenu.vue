<template>
  <div style="height: 100%;background-color: #F2F2F2;">
    <el-collapse :style="isCollapse?'width:fit-content':''" class="BarlowBold" v-model="activeName" accordion>
      <el-menu unique-opened active-text-color="#78BED3">
        <div class="main-question" v-for="(item1,index1) in menu" :key="index1">
          <el-collapse-item :class="isCollapse ? 'myCollapseContent' : ''" :name="item1.serial" :style="index1 == 0? 'margin-top: 6px;':''">
            <!--main question-->
            <template slot="title" class="BarlowBold">
              <div class="step-box">
                <img class="step-pic" :style="isCollapse?'':'margin-right: 12px;'" :src="stepPic[item1.serial]" alt="">
                <span class="collapse-step" v-show="!isCollapse">{{item1.step}}</span>
              </div>
            </template>
            <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.mainQuestionList" :key="item2.id" :index="item2.id.toString()">
              <!--sub question-->
              <template slot="title">
                <div class="ques-list">
                  <div>
                    <div class="ques-serial">
                      {{item2.serial}}
                    </div>
                    <div class="vertical-line-box">
                      <div class="vertical-line"></div>
                    </div>
                  </div>
                  <div class="ques-content" v-show="!isCollapse">{{item2.question}}</div>
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
    principle: {
      type: String,
      required: true
    },
    isCollapse: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      activeName: '',
      menu: '',
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
    }
  },
  created() {
    this.questionnaireMenu()
  },
  watch: {
    'principle': function () {
      this.questionnaireMenu();
    }
  },
  methods: {
    questionnaireMenu() {
      // this.$http.get(`/api/project/1/questionnaire`, {params: {onlyWithFirstAnswer:true}}).then(res => {
      //   if(res.status == 200) {
      //     this.menu = res.data.partList
      //   }
      // })
      this.$http.get('questionnaire-toc.json').then(res => {
        if (res.status == 200) {
          this.menu = res.data.principleAssessments[this.principleMap[this.principle]].stepList
          console.log(this.menu)
        }
      })
    },
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
  transform: translate(-50%,-50%);
  width: 2px;
  height: 100%;
  background-color: #E0E0E0;
}
</style>