<template>
  <div style="height: 100%">
    <el-container style="height: 100%">
      <el-header>
        <div class="title BarlowBold">
          <img class="backPic" src="../../assets/groupPic/back.png" alt="">
<!--          <router-link :to="{path:'/projectPage',query: {id:projectId}}"><img class="backPic" src="../../assets/groupPic/back.png" alt=""></router-link>-->
          <span>Project</span>
        </div>
        <div class="BarlowMedium">
          <el-radio-group v-model="principle" class="principle-group">
            <el-radio-button label="Generic"></el-radio-button>
            <el-radio-button label="Fairness"></el-radio-button>
            <el-radio-button label="Ethics & Accountability"></el-radio-button>
            <el-radio-button label="Transparency"></el-radio-button>
          </el-radio-group>
        </div>
        <div style="display: flex" class="BarlowMedium">
          <div id="preview">Preview</div>
          <div id="export">Export</div>
        </div>
      </el-header>
      <el-main>
        <QuestionnaireMenu style="width: 400px"></QuestionnaireMenu>
      </el-main>
      <el-footer style="height: 64px">
        <div style="display: flex;width: 100%">
          <div class="notification-collapse">
            <div class="not-box">
              <img src="../../assets/projectPic/notification.png" alt="">
              <span class="BarlowBold">Notifications</span>
              <div>6</div>
            </div>
            <div class="collapse-box">
              <img src="../../assets/projectPic/chevron-left.svg" alt="">
            </div>
          </div>
          <div class="footer-right BarlowMedium">
            <div class="footer-text" style="margin-left: 24px">Compare</div>
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
export default {
  name: "Questionnaire",
  components: {
    QuestionnaireMenu
  },
  data() {
    return {
      principle: '',
    }
  }
}
</script>

<style scoped lang="less">
.el-header {
  height: 64px;
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
  width: 64px;
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
  width: calc(100% - 400px);
}
.footer-text {
  padding: 8px 12px;
  background-color: #EDF2F6;
  border-radius: 4px;
}
.arrow {
  width: 24px;
  height: 24px;
}
.footer-prev {
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display: flex;
  align-items: center
}
.footer-next {
  padding: 5px 12px 5px 8px;
  background-color: #EDF2F6;
  border-radius: 4px;
  display:flex;
  align-items:center;
  margin-left: 12px;
  margin-right: 24px
}
</style>