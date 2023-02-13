<template>
  <div style="height: 100%;background-color: #F2F2F2">
    <el-collapse class="BarlowBold" v-model="activeName" accordion>
      <el-menu :default-active="defaultId" class="myMenu" unique-opened active-text-color="#78BED3" @select="handleSelect">
        <div class="main-question" v-for="(item1,index1) in menuData" :key="index1">
          <el-collapse-item :name="item1.step" :style="index1 == 0? 'margin-top: 6px;':''">
            <!--main question-->
            <template slot="title" class="BarlowBold">
              <div class="step-box">
                <img class="step-pic" style="margin-right: 12px" :src="stepPic[item1.serialNo]" alt="">
                <span class="collapse-step">{{item1.step}}</span>
              </div>
            </template>
            <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.mainQuestionList" :key="item2.id" :index="item2.id.toString()">
              <!--sub question-->
              <template slot="title">
                <div class="ques-list">
                  <div class="ques-serial">
                    <div class="serial-font">
                      {{item2.serial}}
                    </div>
                    <div class="ques-img">
                      <img src="../../assets/adminPic/upPage.png" alt="">
                      <img src="../../assets/adminPic/downPage.png" alt="">
                      <img src="../../assets/adminPic/writeProblem.png" alt="">
                      <img src="../../assets/adminPic/deleteProblem.png" alt="">
                    </div>

                  </div>
                  <div class="ques-content">{{item2.question}}
                    <span>{{ item2.editType }} </span>
                  </div>
                </div>
              </template>
            </el-menu-item>
            <div class="add-main">
              <img class="add-main-pic" src="../../assets/questionnairePic/add.svg" alt="">
            </div>
          </el-collapse-item>
        </div>
      </el-menu>
    </el-collapse>
  </div>
</template>

<script>
export default {
  name: "TemplateMenu",
  props: {
    principle: {
      type: String,
      required: true
    },
    projectId: {
      type: String,
      required: true
    },
    menuData: {
      type: Array,
      required: true
    },
    defaultId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      activeName: 'Principles to Practice',
      stepPic: {
        '0': require('../../assets/questionnairePic/portfolio.svg'),
        '1': require('../../assets/questionnairePic/department.svg'),
        '2': require('../../assets/questionnairePic/page.svg'),
        '3': require('../../assets/questionnairePic/issues.svg'),
        '4': require('../../assets/questionnairePic/screen.svg')
      },
    }
  },
  created() {

  },
  methods: {
    handleSelect(questionId) {
      this.$emit("getId",questionId)
    }
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

}
.ques-serial {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.serial-font {
  font-size: 16px;
  font-family: BarlowBold;
  width: 48px;
  text-align: center;
}
.ques-img {
  display: flex;
  margin-right: 12px;
  >img {
    width: 24px;
    height: 24px;
    margin-right: 8px;
  }
}
.ques-content {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-family: BarlowMedium;
  padding: 8px 16px;
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
.add-main {
  display: flex;
  align-items: center;
  background: #EDF2F6;
  border-radius: 4px;
  margin: 8px 16px;
}
.add-main-pic {
  margin: auto;
  width: 24px;
  height: 24px;
}
</style>