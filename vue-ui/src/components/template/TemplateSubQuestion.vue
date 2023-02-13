<template>
  <div style="height: calc(100% - 48px);padding: 16px 24px">
    <div class="main-ques">
      {{questionData.serial}}. {{questionData.question}}
    </div>
    <div v-for="item in [1,2,3]" class="sub-ques">
      <span>zheshiyige sub -question</span>
      <div>
        <div class="ques-img">
          <img src="../../assets/adminPic/upPage.png" alt="">
          <img src="../../assets/adminPic/downPage.png" alt="">
          <img src="../../assets/adminPic/writeProblem.png" alt="">
          <img src="../../assets/adminPic/deleteProblem.png" alt="">
        </div>
      </div>
    </div>
    <el-input v-show="addSubQuesFlag" style="margin-top: 24px" v-model="addSubQues" placeholder="Please input a new subquestion">
      <i slot="suffix" class="el-input__icon el-icon-check"></i>
      <i slot="suffix" class="el-input__icon el-icon-close" @click="handleAddSubQues"></i>
    </el-input>
  </div>
</template>

<script>
export default {
  name: "TemplateSubQuestion",
  props: {
    projectId: {
      type: String,
      required: true
    },
    questionId: {
      type: String,
      required: true
    },
    addSubQuesFlag: {
      type: Boolean
    }
  },
  data() {
    return {
      questionData: {},
      addSubQues: ''
    }
  },
  watch: {
    'questionId': function () {
      this.getSubQuestion()
      console.log(this.projectId,this.questionId)
    },
  },
  created() {

  },
  methods: {
    getSubQuestion() {
      this.$http.get(`/api/project/${this.projectId}/questionnaire/question/${this.questionId}`).then(res => {
        if (res.status == 200) {
          this.questionData = res.data

        }
      })
    },
    handleAddSubQues() {
      this.$emit('updateFlag', false)
    }
  },
}
</script>

<style scoped lang="less">
.main-ques {
  font-size: 16px;
  font-family: BarlowBold;
}
.sub-ques {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  >span {
    font-size: 16px;
    font-family: BarlowMedium;
  }
}
.ques-img {
  display: flex;
  >img {
    width: 24px;
    height: 24px;
    margin-left: 8px;
  }
}
</style>