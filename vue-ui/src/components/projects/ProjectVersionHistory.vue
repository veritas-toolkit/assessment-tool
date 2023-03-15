<template>
  <div>
    <div class="module_title" v-show="reportHistoryList.length > 0">Version history</div>

    <div v-for="(item,index) in reportHistoryList" v-show="reportHistoryList.length > 0">
      <div class="version-box" :style="index != 0? 'border-top: none':''">
        <div style="display: flex;width: 100%">
          <div style="margin-left: 16px;width: 80px">v {{ item.version }}</div>
          <div style="width: 30%">{{ item.message }}</div>
          <div style="display: flex;width: 250px">
            <img style="width: 20px;height: 20px;margin-right: 8px" src="../../assets/groupPic/Avatar.png" alt="">
            &nbsp;{{ item.creator.username }}
          </div>
          <div>{{ item.createdTime|changeTime }}</div>
        </div>
        <div style="display: flex;flex-shrink: 0;margin-right: 16px">
          <div class="model-artifacts" @click="downloadHistoryJsonFile(item)">
            <img src="../../assets/projectPic/modelArtifacts.png" alt=""><span>Model artifacts</span>
          </div>
          <div class="fairness-assessment" @click="questionnaireHistory(item)">
            <img src="../../assets/projectPic/questionnaire.png" alt=""><span>Questionnaire</span>
          </div>
          <div class="pdf-report" @click="previewHistoryPdf(item)">
            <img src="../../assets/projectPic/report.png" alt=""><span>Report</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import projectApi from "@/api/projectApi";
import dateFormat from "@/util/dateFormat";
import Router from "@/router";

export default {
  name: "ProjectVersionHistory",
  props: {
    reportHistoryList: {type: Array, required: true},
  },
  data() {
    return {
    };
  },
  methods: {

    dateFormat(date) {
      return dateFormat.dateFormat(date);
    },
    downloadHistoryJsonFile(reportHistory) {
      if (reportHistory['modelArtifactVid'] == null) {
        this.$alert("This report does not have an associated model artifact.",
            "Model Artifact",
            {type: "warning"})
        return
      }
      projectApi.fetchHistoryJsonInfo(reportHistory.projectId, reportHistory.versionId)
          .then(res => {
            return res.data;
          })
          .then(modelArtifact => {
            projectApi.downloadHistoryJsonFile(reportHistory.projectId, reportHistory.versionId)
                .then(res => {
                  let blob = new Blob([res.data], {type: "application/octet-stream;charset=utf-8"});
                  if (window.navigator.msSaveBlob) {
                    try {
                      window.navigator.msSaveBlob(blob, modelArtifact.filename)
                    } catch (e) {
                    }
                  } else {
                    let Temp = document.createElement('a')
                    Temp.href = window.URL.createObjectURL(blob)
                    if (modelArtifact.filename) {
                      Temp.download = modelArtifact.filename
                    } else {
                      Temp.download = 'data.json'
                    }
                    document.body.appendChild(Temp)
                    Temp.click()
                    document.body.removeChild(Temp)
                    window.URL.revokeObjectURL(Temp.href)
                  }
                })
          });

    },
    questionnaireHistory: async function (reportHistory) {
      let reportInfo = {
        projectId: reportHistory.projectId,
        versionId: reportHistory.versionIdOfProject
      };
      await Router.push({path: '/questionnaireHistory', query: reportInfo})
    },

    previewHistoryPdf(reportHistory) {
      let projectId = reportHistory.projectId;
      let versionId = reportHistory['versionIdOfProject'];
      projectApi.fetchReportPdf(projectId,versionId).then(res => {
        const binaryData = []
        binaryData.push(res.data)
        let blob = new Blob(binaryData,{type: res.data.type})
        let pdfUrl = window.URL.createObjectURL(blob)
        window.open(pdfUrl)
      })
    }
  },

}
</script>

<style lang="less" scoped>
.version-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  border: 1px solid #E0E0E0;
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
  background-color: #EDF2F6;
  padding: 0px 8px;
  > img {
    width: 16px;
    height: 16px;
    margin-right: 8px;
  }
  > span {
    font-size: 14px;
  }
}
.fairness-assessment {
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 24px;
  border-radius: 4px;
  background-color: #EDF2F6;
  margin-left: 16px;
  padding: 0px 8px;
  > img {
    width: 16px;
    height: 16px;
    margin-right: 8px;
  }
  > span {
    font-size: 14px;
  }
}
.pdf-report {
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 24px;
  border-radius: 4px;
  background-color: #EDF2F6;
  margin-left: 16px;
  padding: 0px 8px;
  > img {
    width: 16px;
    height: 16px;
    margin-right: 8px;
  }
  > span {
    font-size: 14px;
  }
}
.dividingLine2 {
  width: 100%;
  height: 1px;
  background-color: #E6E6E6;
}
.module_title {
  font-size: 18px;
  font-weight: bold;
  margin: 16px 0 16px 0;
}
</style>