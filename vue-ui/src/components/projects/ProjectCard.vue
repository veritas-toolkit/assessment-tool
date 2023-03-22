<template>
  <el-card class="box-card" @click.native="gotoProjectPage">
    <div slot="header" class="boxCardHeader">
      <div style="display: flex;justify-content: space-between;margin-top: 8px">
        <div></div>
        <div class="businessScenarioStyle BarlowMedium">{{ businessScenarioName }}</div>
      </div>

      <div class="owner oneLine">
        {{ ownerName }}
      </div>
      <div class="projName oneLine BarlowBold">
        <span> {{ project['name'] }} </span>
      </div>
      <span>Edited on {{ project['lastEditedTime']|changeTime }}</span>
    </div>
    <div style="padding: 20px">
      <div class="progress-text">
        {{ project['assessmentProgress'].completed }}/{{ project['assessmentProgress'].count }}
      </div>
      <el-progress :percentage="completePercentage"
                   color="#78BED3" :show-text="false">
      </el-progress>
    </div>
  </el-card>
</template>

<script>

export default {
  name: "ProjectCard",
  props: {
    "project": {
      type: Object,
      required: true
    },
    "businessScenarioList": {
      type: Array,
      required: true
    }

  },
  methods: {
    gotoProjectPage() {
      this.$router.push({
        path:'/projectPage',
        query: {
          id: this.project.id
        }
      })
    }
  },
  computed: {
    ownerName() {
      if (this.project['userOwner']) {
        return this.project['userOwner'].username;
      } else if (this.project['groupOwner']) {
        return this.project['groupOwner'].name;
      } else {
        console.error()
        return null;
      }
    },
    businessScenarioName() {
      const bizId = this.project['businessScenario']
      for (const b of this.businessScenarioList) {
        if (b['code'] === bizId) {
          return b['name'];
        }
      }
      return null;
    },
    completePercentage() {
      const progress = this.project['assessmentProgress'];
      if (!progress) {
        return 0;
      }
      if (progress.count === 0) {
        return 0;
      }
      return this.project['assessmentProgress'].completed / this.project['assessmentProgress'].count * 100;
    }

  }

}
</script>

<style lang="less" scoped>
.box-card {
  //width: 75%;
  //margin-left: 10px;
  margin-top: 10px;
  margin-bottom: 12px;
  padding: 0;
}
.boxCardHeader {
  > span {
    margin-top: 10px;
    font-size: 12px;
    color: #bbb;
  }
}
.owner {
  font-size: 14px;
  font-family: BarlowMedium;
  font-weight: bold;
  color: #175EC2;
  margin-bottom: 4px;
}
.projName {
  line-height: 22px;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;

}
.businessScenarioStyle {
  display: flex;
  align-items: center;
  margin-right: -12px;
  background-color: #78BED3;
  padding: 0px 8px 2px 8px;
  border-radius: 14px;
  color: #FFF;
  font-size: 14px;
}
.progress-text {
  margin-top: -10px;
  font-size: 24px;
  font-weight: bold;
  font-family: BarlowBold;
}
</style>

<style lang="less">
.box-card .el-card__header {
  padding-top: 0px;
}
</style>