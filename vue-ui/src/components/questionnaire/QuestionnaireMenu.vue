<template>
  <div style="height: 100%;background-color: #F2F2F2;">
    <el-collapse class="BarlowBold" v-model="activeName" accordion>
      <el-menu unique-opened active-text-color="#78BED3">
        <div class="main-question" v-for="(item1,index1) in menu" :key="index1">
          <el-collapse-item :name="item1.partName" :style="index1 == 0? 'margin-top: 6px;':''">
            <!--main question-->
            <template slot="title" class="partTitle BarlowBold">
              <div style="line-height: 20px">
                {{item1.partName}}. {{item1.partTitle}}
              </div>
            </template>
            <el-menu-item class="BarlowMedium" v-for="(item2,index2) in item1.questionList" :key="item2.id" :index="item2.id.toString()">
              <!--sub question-->
              <template slot="title">
                <div>{{item2.part}}{{item2.partSerial}}. {{item2.content}}</div>
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
  data() {
    return {
      activeName: '',
      menu: '',
    }
  },
  created() {
    this.questionnaireMenu()
  },
  methods: {
    questionnaireMenu() {
      this.$http.get(`/api/project/1/questionnaire`, {params: {onlyWithFirstAnswer:true}}).then(res => {
        if(res.status == 200) {
          this.menu = res.data.partList
        }
      })
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
  padding: 8px 16px;
}
.el-collapse-item {
  padding: 6px 12px;
  border-right: none;
}
</style>