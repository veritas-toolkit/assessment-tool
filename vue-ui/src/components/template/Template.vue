<template>
  <div style="height: 100%">
    <el-container style="height: 100%;display: flex;flex-direction: column">
      <el-header height="64px">
        <div class="title BarlowBold">
          <img class="backPic" src="../../assets/groupPic/back.png" alt="">
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
        <div class="BarlowMedium">
          <div id="save">Save</div>
        </div>
      </el-header>
      <!--flex-direction: column; overflow-y: auto-->
      <el-container style="flex: 1;overflow-y: auto">
        <el-aside width="400px">

        </el-aside>
        <el-main>

        </el-main>
      </el-container>
      <el-footer style="height: 64px;">
        <div style="display: flex;width: 100%;justify-content:space-between;align-items: center">
          <div class="notification">
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
          </div>
          <div class="footer-right BarlowMedium draft-popover"  style="width: calc(100% - 400px)">
            <div style="display: flex;justify-content: right">
              <div class="add-subques">
                <img class="watch" src="../../assets/questionnairePic/watch.svg" alt="">
                Add subquestion
              </div>
            </div>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import Notifications from "@/components/comment/Notifications";

export default {
  name: "Template",
  components: {
    Notifications
  },

  data() {
    return {
      projectId: this.$route.query.id,
      principle: 'Generic',
      principleMap: {
        "Generic" : "G",
        "Fairness" : "F",
        "Ethics & Accountability" : "EA",
        "Transparency" : "T"
      },
      principleList: [],
    }
  },
  created() {
    this.getQuestionnaireMenu()
  },
  methods: {
    getQuestionnaireMenu() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/toc`).then(res => {
        if (res.status == 200) {
          this.principleList = Object.keys(res.data.principles)
        }
      })
    },
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
.backPic {
  width: 24px;
  height: 24px;
  margin-left: 24px;
  margin-right: 8px;
}
#save {
  padding: 8px 12px;
  background-color: #78BED3;
  border-radius: 4px;
  color: #FFFFFF;
  margin-right: 24px;
}
.notification {
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
.watch {
  width: 24px;
  height: 24px;
  margin-right: 8px;
}
.add-subques {
  cursor: pointer;
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display:flex;
  align-items:center;
  margin-left: 12px;
  margin-right: 24px
}
</style>