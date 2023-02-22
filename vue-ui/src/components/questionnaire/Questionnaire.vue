<template>
  <div style="height: 100%">
    <el-container :style="isCollapse?'height: calc(100% - 3px);display: flex;flex-direction: column':'height: calc(100% - 2px);display: flex;flex-direction: column'">
      <el-header height="64px">
        <div class="title BarlowBold">
          <img class="backPic" src="../../assets/groupPic/back.png" alt="" @click="$router.back()" alt="" style="cursor: pointer">
          <!--<router-link :to="{path:'/projectPage',query: {id:projectId}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>-->
          <span>Project</span>
        </div>
        <div class="BarlowMedium">
          <el-radio-group v-model="principle" class="principle-group">
            <el-radio-button v-show="this.principleList.indexOf('G') != -1" label="Generic"></el-radio-button>
            <el-radio-button v-show="this.principleList.indexOf('F') != -1" label="Fairness"></el-radio-button>
            <el-radio-button v-show="this.principleList.indexOf('EA') != -1" label="Ethics & Accountability"></el-radio-button>
            <el-radio-button v-show="this.principleList.indexOf('T') != -1" label="Transparency"></el-radio-button>
          </el-radio-group>
        </div>
        <div id="endComSty" class="BarlowMedium" @click="compareFlag=false" v-if="compareFlag">End comparison</div>
        <div style="display: flex" class="BarlowMedium" v-if="!compareFlag">
          <div id="preview">Preview</div>
          <div id="export">Export</div>
        </div>
      </el-header>
      <!--flex-direction: column; overflow-y: auto-->
      <el-container style="flex: 1;overflow-y: auto">
          <el-aside :width="isCollapse? '72px':'400px'">
            <QuestionnaireMenu @getId="getQuestionId" :defaultId="defaultId" :menuData="menuData" :principle="principle" :projectId="projectId" :isCollapse="isCollapse"></QuestionnaireMenu>
          </el-aside>
          <el-main :style="openCompare?'display:flex':''">
<!--            <QuestionnaireAnswer v-if="openCompare" style="border-right: 1px solid #D5D8DD;overflow-y: auto"></QuestionnaireAnswer>-->
            <QuestionnaireAnswer v-show="!compareFlag" :projectId="projectId" :questionId="questionId" style="overflow-y: auto"></QuestionnaireAnswer>
            <QuestionnaireCompareAnswer v-show="compareFlag" :compareFlag="compareFlag" :questionnaireVid="questionnaireVid" :projectId="projectId" :questionId="questionId"></QuestionnaireCompareAnswer>
          </el-main>
      </el-container>
      <el-footer style="height: 64px;">
        <div style="display: flex;width: 100%;justify-content:space-between;align-items: center">
          <div class="notification-collapse"  v-if="!isCollapse">
            <el-popover
                placement="top-start"
                width="400"
                trigger="click">
              <Notifications></Notifications>
              <div class="not-box" slot="reference">
                <img src="../../assets/projectPic/notification.png" alt="">
                <span class="BarlowBold">Notifications</span>
                <div>6</div>
              </div>
            </el-popover>
            <div class="collapse-box" @click="isCollapse=true">
              <img src="../../assets/projectPic/chevron-left.svg" alt="">
            </div>
          </div>
          <div class="collapse-right" @click="isCollapse=false" v-if="isCollapse">
            <img src="../../assets/projectPic/chevron-right.svg" alt="">
          </div>
          <div class="footer-right BarlowMedium draft-popover"  :style="isCollapse? 'width: calc(100% - 72px)':'width: calc(100% - 400px)'">
            <el-popover
                class=""
                placement="top-start"
                width="480"
                trigger="click">
              <div style="height: 100%;width: 100%">
                <el-tabs v-model="compareTab" @tab-click="handleCompareClick">
                  <el-tab-pane label="Exported Version" name="exportedOnly">
                    <div v-for="(item,index) in draftList" @click="compare(item.questionnaireVid)" class="draft-box" :style="index==0?'':'border-top:1px solid #D5D8DD'">
                      <div class="draft-left">
                        <img src="../../assets/groupPic/Avatar.png" alt="">
                        <div>{{item.creator.username}}</div>
                      </div>
                      <div class="draft-right">{{item.createdTime|changeTime}}</div>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="Recent Draft" name="draftOnly">
                    最近草稿
                  </el-tab-pane>
                </el-tabs>
              </div>
              <div class="footer-text" style="margin-left: 24px" @click="getDiffVersion(compareTab)" slot="reference">Compare</div>
            </el-popover>
<!--            <div @click="openCompare=true" style="border: 1px solid red">open compare</div>-->
            <div style="display: flex">
              <div class="footer-prev">
                <img class="arrow" src="../../assets/projectPic/arrow-up.svg" alt="">
                Prev
              </div>
              <div class="footer-next">
                <img class="arrow" src="../../assets/projectPic/arrow-down.svg" alt="">
                Next
              </div>
            </div>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import QuestionnaireMenu from "@/components/questionnaire/QuestionnaireMenu";
import QuestionnaireAnswer from "@/components/questionnaire/QuestionnaireAnswer";
import Notifications from "@/components/comment/Notifications";
import QuestionnaireCompareAnswer from "@/components/questionnaire/QuestionnaireCompareAnswer";

export default {
  name: "Questionnaire",
  components: {
    QuestionnaireMenu,
    QuestionnaireAnswer,
    QuestionnaireCompareAnswer,
    Notifications,
  },
  mounted() {

  },
  data() {
    return {
      principle: 'Generic',
      principleList: [],
      isCollapse: false,
      compareTab: 'exportedOnly',
      openCompare: false,
      projectId: this.$route.query.id,
      questionId: '',
      defaultId: '',
      menuData: [],
      principleMap: {
        "Generic" : "G",
        "Fairness" : "F",
        "Ethics & Accountability" : "EA",
        "Transparency" : "T"
      },
      draftList: [],
      compareFlag: false,
      questionnaireVid: ''
    }
  },
  created() {
    this.getQuestionnaireMenu()
  },

  watch: {
    'principle': function () {
      if (this.compareFlag) {
        this.compare(this.questionnaireVid)
      } else {
        this.getQuestionnaireMenu()
      }
    },
    'compareFlag': function () {
      if(this.compareFlag) {
        this.compare(this.questionnaireVid)
      } else {
        this.getQuestionnaireMenu()
      }
    },
  },
  methods: {
    handleCompareClick() {
      this.getDiffVersion(this.compareTab)
    },
    getQuestionId(data) {
      this.questionId = data
    },
    getQuestionnaireMenu() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/toc`).then(res => {
        if (res.status == 200) {
          this.principleList = Object.keys(res.data.principles)
          this.menuData = res.data.principleAssessments[this.principleMap[this.principle]].stepList
          this.questionId = res.data.principleAssessments[this.principleMap[this.principle]].stepList[0].mainQuestionList[0].id.toString()
          this.defaultId = this.questionId.toString()
        }
      })
    },
    getDiffVersion(compareType) {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/history`).then(res => {
        if (res.status == 200) {
          this.draftList = res.data.records.reverse()
          // console.log(this.draftList)
        }
        // console.log(compareType)
      })
    },
    compare(questionnaireVid) {
      this.compareFlag = true
      this.questionnaireVid = questionnaireVid.toString()
      this.$http.get(`/api/project/${this.projectId}/questionnaire/compare/toc`,{params:{'based':questionnaireVid}}).then(res => {
        if (res.status == 200) {
          this.questionId = res.data.principleAssessments[this.principleMap[this.principle]].stepList[0].mainQuestionList[0].id.toString()
          this.menuData = res.data.principleAssessments[this.principleMap[this.principle]].stepList
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #E6E6E6;
}
.backPic {
  width: 24px;
  height: 24px;
  margin-left: 24px;
  margin-right: 8px;
}
.title {
  height: 64px;
  display: flex;
  align-items: center;
  > div {
    font-size: 20px;
    font-weight: bold;
    color: #000;
  }
}
#preview {
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
  margin-right: 12px;
}
#export {
  padding: 8px 12px;
  background-color: #78BED3;
  border-radius: 4px;
  color: #FFFFFF;
  margin-right: 24px;
}
#endComSty {
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
  margin-right: 24px;
}
.notification-collapse {
  display: flex;
  justify-content: space-between;
  width: 400px;
  background-color: #F2F5F7;
  line-height: 64px;
}
.not-box {
  display: flex;
  align-items: center;
  margin-left: 24px;
  >img {
    width: 24px;
    height: 24px;
  }
  >span {
    margin-left: 8px;
    font-size: 16px;
    color: #333333;
  }
  >div {
    margin-left: 8px;
    text-align: center;
    width: 24px;
    line-height: 24px;
    background-color: #FCB215;
    border-radius: 12px;
    color: #FFFFFF;
  }
}
.collapse-box {
  position: relative;
  cursor: pointer;
  width: 72px;
  border-left: 1px solid #D7D9DB;
  text-align: center;
  >img {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    width: 40px;
    height: 40px;
  }
}
.footer-right {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.footer-text {
  cursor: pointer;
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
}
.arrow {
  width: 24px;
  height: 24px;
}
.footer-prev {
  cursor: pointer;
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display: flex;
  align-items: center
}
.footer-next {
  cursor: pointer;
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display:flex;
  align-items:center;
  margin-left: 12px;
  margin-right: 24px
}
.collapse-right {
  height: 64px;
  display: flex;
  align-items: center;
  cursor: pointer;
  width: 72px;
  background-color: #F2F5F7;
  line-height: 64px;
  >img {
    margin: auto;
    width: 40px;
    height: 40px;
  }
}
.draft-box {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;

}
.draft-left {
  display: flex;
  align-items: center;
  > img {
    width: 32px;
    height: 32px;
  }
  > div {
    font-size: 16px;
    font-family: BarlowBold;
    margin-left: 8px;
  }
}

</style>