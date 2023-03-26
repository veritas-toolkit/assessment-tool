<template>
  <el-dialog :close-on-click-modal="false"
             class="BarlowMedium"
             :visible.sync="dialogVisible"
             @close="close()"
             id="export-dialog"
             width="548px"
             append-to-body>
    <div id="export-div">
      <div v-if="latestVersion" style="margin-top: 4px">Latest Version:
        <span>{{ latestVersion }}</span>
      </div>
      <el-form :rules="exportPdfFormRules" ref="exportPdfFormRefs" label-position="top" label="450px"
               id="export-form"

               :model="exportPdfForm">
        <el-form-item class="login" label="Report version" prop="version">
          <el-select v-model="exportPdfForm.version" filterable allow-create default-first-option
                     placeholder="Please choose or input a report version">
            <el-option
                v-for="item in suggestVersionList"
                :key="item"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="login" label="Report message" prop="message">
          <el-input placeholder="Please input a report message"
                    v-model="exportPdfForm.message"
                    @keyup.enter.native="exportPdf"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button class="BlackBorder" @click="close()">Cancel</el-button>
          <el-button id="export-button" class="GreenBC" @click="exportPdf">Export</el-button>
      </span>
    </div>
  </el-dialog>
</template>

<script>
import projectApi from "@/api/projectApi";
import {ca} from "timeago.js/lib/lang";

export default {
  name: "ExportReportDialog",
  props: {
    projectId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      dialogVisible: false,
      latestVersion: '',
      suggestVersionList: [],
      exportPdfFormRules: {
        version: [{required: true, trigger: 'blur'},],
        message: [{required: true, trigger: 'blur'},],
      },
      exportPdfForm: {
        version: null,
        message: '',
      },
    }
  },

  methods: {
    close() {
      this.dialogVisible = false;
    },
    open() {
      projectApi.detail(this.projectId).then(response => {
        const detail = response.data;
        const needJson = true;
        const hasJson = !!detail['modelArtifact'];
        let open = true
        if (needJson && !hasJson) {
          this.$confirm(
              "No model artifact related answer will be exported.",
              "Export",
              {
                type: "warning",
                cancelButtonText: 'Cancel',
                confirmButtonText: 'Continue',
                showCancelButton: true,
              })
              .then(() => {
                this.dialogVisible = true;
                this.fetchSuggestionVersionList();
              })
              .catch(() => {})
        } else {
          this.dialogVisible = true;
          this.fetchSuggestionVersionList();
        }
      })
    },
    fetchSuggestionVersionList() {
      projectApi.fetchSuggestionVersion(this.projectId)
          .then(res => {
            const {latestVersion, suggestionVersionList} = res.data;
            this.latestVersion = latestVersion;
            this.suggestVersionList = suggestionVersionList;
          });
    },
    async exportPdf() {

      try {
        let validate = await this.$refs.exportPdfFormRefs.validate();
        if (!validate) {
          return false;
        }
      } catch (e) {
        console.error(e)
        return false;
      }
      let loading = this.$loading(
          {
            target: "#export-div",
            spinner: 'el-icon-loading',
          });
      projectApi.exportReport(this.projectId, this.exportPdfForm)
          .then(res => {
            let createdReport = res.data;
            this.$emit('exported', createdReport);
          })
          .then(() => {
            this.exportPdfForm.version = null;
            this.exportPdfForm.message = '';
            this.close();
          })
          .finally(() => {
            loading.close();
          })
    },
    exportPdf2() {
      this.$refs.exportPdfFormRefs.validate(val => {
        console.log("validate")
        console.log(val)
        if (!val) {
          return;
        }
        let loading = this.$loading(
            {
              target: "#export-div",
              spinner: 'el-icon-loading',
            });
        projectApi.exportReport(this.projectId, this.exportPdfForm)
            .then(res => {
              let createdReport = res.data;
              this.$emit('exported', createdReport);
            })
            .then(() => {
              this.exportPdfForm.version = null;
              this.exportPdfForm.message = '';
              this.close();
            })
            .finally(() => {
              loading.close();
            })
      })
    }
  }
}
</script>

<style scoped lang="less">
.dialog-footer {
  margin-top: 16px;
  margin-bottom: 8px;
  display: flex;
  justify-content: right;
}

</style>