<template>
  <!--version history-->
  <div>
<div>Version history new</div>

    <div v-for="(item,index) in reportHistoryList" v-show="reportHistoryList.length != 0">
      <div class="version-box">
        <div class="oneLine">
          <div style="display:flex;font-size: 14px;margin-right: 12px;padding: 4px 20px 2px 0px;font-weight: bold">
            <div class="version-style"><i class="el-icon-price-tag"></i> v {{ item.version }}</div>
            <span>&nbsp;&nbsp;&nbsp;</span>
            <div class="oneLine"><i class="el-icon-document"></i> {{ item.message }}</div>
          </div>
          <div style="padding: 0px 0px 4px 0px">
            <span class="date"><i class="el-icon-time"></i> on {{ dateFormat(item.createdTime) }}</span>
          </div>
        </div>
        <div style="display: flex;flex-shrink: 0;">
          <div class="model-artifacts" @click="downloadHistoryJsonFile(item.projectId,item.versionIdOfProject)"><i
              class="el-icon-help"></i><span>Model artifacts</span></div>
          <div class="fairness-assessment" @click="questionnaireHistory(item.projectId,item.versionIdOfProject)"><i
              class="el-icon-notebook-1"></i><span>Fairness assessment</span></div>
          <div class="pdf-report" @click="previewHistoryPdf(item.projectId,item.versionIdOfProject)"><i
              class="el-icon-document-remove"></i><span>Report</span></div>
        </div>
      </div>
      <div class="dividingLine2"></div>
    </div>
  </div>
</template>

<script>
import projectApi from "@/api/projectApi";
import dateFormat from "@/util/dateFormat";

export default {
  name: "ProjectVersionHistory",
  props: {
    reportHistoryList: {type: Array, required: true},
  },
  data() {
    return {

    };
    //reportHistoryList: []
  },
  computed: {},
  created() {
    //console.log('watch reportHistoryList: ', this.reportHistoryList)
  },
  methods: {
    dateFormat(date) {
      return dateFormat.dateFormat(date);
    },
    downloadHistoryJsonFile(projectId, versionId) {
      projectApi.downloadHistoryJsonFile(projectId, versionId)
    },
    questionnaireHistory(projectId,versionId) {
      this.$router.push({path:'/assessmentToolHistory',query: {projectId:projectId,versionId:versionId}})
    },
    previewHistoryPdf(projectId,versionId) {
      projectApi.previewHistoryPdf(projectId,versionId).then(res => {
        const binaryData = []
        binaryData.push(res.data)
        let blob = new Blob(binaryData,{type: res.data.type})
        let pdfUrl = window.URL.createObjectURL(blob)
        window.open(pdfUrl)
      }).catch(err => {
        if (err.response.config.responseType == "blob") {
          let data = err.response.data
          let fileReader = new FileReader()
          fileReader.onload = function() {
            let jsonData = JSON.parse(this.result)
            Vue.prototype.$message.error(jsonData.message)
          };
          fileReader.readAsText(data)
        }
      })
    }
  },
  /*
  watch: {
    'reportHistoryList': function (val, oldval) {
      console.log('watch reportHistoryList: ', val.toString())
    }
  }
  */

}
</script>

<style lang="less" scoped>
.version-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
}
.version-style {
  background-color: #67C23A;
  padding: 0px 4px;
  border-radius: 4px;
  color: #FFF;
}
.date {
  color: #333333;
  font-size: 12px;
}
.model-artifacts {
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 24px;
  border-radius: 4px;
  background-color: #0091FF;
  padding: 0px 8px;
  > i {
    color: #FFF;
    margin-right: 8px;
  }
  > span {
    color: #FFF;
    font-size: 12px;
  }
}
.fairness-assessment {
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 24px;
  border-radius: 4px;
  background-color: #1962E4;
  margin-left: 16px;
  padding: 0px 8px;
  > i {
    color: #FFF;
    margin-right: 8px;
  }
  > span {
    color: #FFF;
    font-size: 12px;
  }
}
.pdf-report {
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 24px;
  border-radius: 4px;
  background-color: #FA6400;
  margin-left: 16px;
  padding: 0px 8px;
  > i {
    color: #FFF;
    margin-right: 8px;
  }
  > span {
    color: #FFF;
    font-size: 12px;
  }
}
.dividingLine2 {
  width: 100%;
  height: 1px;
  background-color: #E6E6E6;
}
</style>