<template>
  <div style="padding: 24px !important;">
    <div class="category">Projects</div>
    <!--header area-->
    <el-row :gutter="20">
      <el-col :span="20">
        <el-input @input="getProjectList" placeholder="Search your project here" prefix-icon="el-icon-search"
                  v-model="keyword">
        </el-input>
      </el-col>
      <el-col :span="4">
        <div class="creProj BarlowMedium"
             style="cursor: pointer"
             @click="$refs.createProjectDialog.open()">
          <i class="el-icon-plus"></i>
          <span>Create project</span>
        </div>
      </el-col>
    </el-row>
    <!--main tabs-->
    <div style="margin-top: 10px">
      <div>
        <div style="display: flex;justify-content: space-between">
          <div></div>
          <el-select @change="getProjectsByBusSce" clearable v-model="searchByBusinessScenario"
                     placeholder="Business scenario" class="search-business">
            <el-option
                v-for="item in businessScenarioList"
                :key="item.code"
                :label="item.name"
                :value="item.code">
            </el-option>
          </el-select>
        </div>
        <el-tabs style="margin-top: -40px" class="BarlowMedium" v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="All" name="first">
            <el-row :gutter="20" style="margin-top: 6px;margin-bottom: 6px">
              <el-col v-for="(item,index) in projectList" :span="6">
                <project-card :project="item" :business-scenario-list="businessScenarioList"/>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="Created by me" name="second">
            <el-row :gutter="20" style="margin-top: 6px;margin-bottom: 6px">
              <el-col v-for="(item,index) in myProjectList" :span="6">
                <project-card :project="item" :business-scenario-list="businessScenarioList"/>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="Archived" name="third">
            <el-row :gutter="20" style="margin-top: 6px;margin-bottom: 6px">
              <el-col v-for="(item,index) in projectList" :span="6">
                <project-card :project="item" :business-scenario-list="businessScenarioList"/>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="block">
        <el-pagination
            hide-on-single-page
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page=page
            :page-sizes="[4, 8, 12, 16]"
            :page-size=pageSize
            layout="sizes, prev, pager, next, jumper, total"
            :total=total>
        </el-pagination>
      </div>
    </div>
    <!--create project-->
    <create-project-dialog ref="createProjectDialog" @created="onProjectCreated"/>
  </div>
</template>

<script>
import projectApi from "@/api/projectApi";
import CreateProjectDialog from "@/components/projects/CreateProjectDialog";
import ProjectCard from "@/components/projects/ProjectCard.vue";
import systemApi from "@/api/systemApi";


export default {
  name: "Projects",
  components: {
    CreateProjectDialog,
    ProjectCard,
  },
  data() {
    return {
      activeName: 'first',
      keyword: null,
      searchByBusinessScenario: null,
      businessScenarioList: [],
      projectList: [],
      myProjectList: [],
      page: 1,
      pageSize: 12,
      total: 0,
    }
  },
  watch: {
    activeName: function () {
      this.page = 1
      this.keyword = ''
      this.getProjectList()
    },
  },
  created() {
    this.getBusinessScenarioList();
    this.getProjectList()
    sessionStorage.setItem('projectId', null)
    this.resetSetItem('projectId', null);
  },
  methods: {
    onProjectCreated(newProject) {
      this.getProjectList();
      this.$router.push({
        path: '/projectPage',
        query: {
          id: newProject.id
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getProjectList()
    },
    handleCurrentChange(val) {
      this.page = val
      this.getProjectList()
    },
    handleClick(tab, event) {
      this.activeName = tab.name
    },
    getBusinessScenarioList() {
      systemApi.getBusinessScenarioList().then(response => {
        this.businessScenarioList = response.data;
      })
    },

    getProjectList() {
      if (this.activeName === 'first') {
        this.$http.get('/api/project',
            {
              params: {
                'keyword': this.keyword,
                page: this.page,
                pageSize: this.pageSize,
                businessScenario: this.searchByBusinessScenario
              }
            }).then(res => {
          this.projectList = res.data.records
          this.total = res.data.total
        })
      } else if (this.activeName === 'second') {
        this.$http.get('/api/project',
            {
              params: {
                'keyword': this.keyword,
                createdByMe: true,
                page: this.page,
                pageSize: this.pageSize,
                businessScenario: this.searchByBusinessScenario
              }
            }).then(res => {
          this.myProjectList = res.data.records
          this.total = res.data.total
        })
      } else if (this.activeName === 'third') {
        this.$http.get('/api/project', {
          params: {
            'keyword': this.keyword,
            page: this.page,
            pageSize: this.pageSize,
            businessScenario: this.searchByBusinessScenario,
            archived: true
          }
        }).then(res => {
          this.projectList = res.data.records
          this.total = res.data.total
        })
      }
    },
    getProjectsByBusSce() {
      if (this.activeName === 'first') {
        this.$http.get('/api/project', {
          params: {
            'keyword': this.keyword,
            page: this.page,
            pageSize: this.pageSize,
            businessScenario: this.searchByBusinessScenario
          }
        }).then(res => {
          this.projectList = res.data.records
          this.total = res.data.total
        })
      } else if (this.activeName === 'second') {
        this.$http.get('/api/project', {
          params: {
            'keyword': this.keyword,
            createdByMe: true,
            page: this.page,
            pageSize: this.pageSize,
            businessScenario: this.searchByBusinessScenario
          }
        }).then(res => {
          this.myProjectList = res.data.records
          this.total = res.data.total
        })
      } else if (this.activeName === 'third') {
        this.$http.get('/api/project', {
          params: {
            'keyword': this.keyword,
            page: this.page,
            pageSize: this.pageSize,
            businessScenario: this.searchByBusinessScenario,
            archived: true
          }
        }).then(res => {
          this.projectList = res.data.records
          this.total = res.data.total
        })
      }
    },
  }
}
</script>

<style lang="less" scoped>
.category {
  font-size: 24px;
  font-family: BarlowMedium;
  font-weight: bold;
  margin-bottom: 24px;
}

.creProj {
  height: 40px;
  background-color: #78BED3;
  border-radius: 4px;
  text-align: center;

  > span {
    color: #fff;
    font-size: 16px;
    line-height: 40px;
    margin-left: 10px;
  }
}

.el-icon-plus {
  color: #fff;
}

.boxCardHeader {
  > span {
    margin-top: 10px;
    font-size: 12px;
    color: #bbb;
  }
}

.projName {
  line-height: 22px;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;

}

.el-progress {
  margin-top: 10px;
}

.progressLabel {
  color: #000;
  margin-top: 18px;
  font-size: 24px;
}

.dialogTitle {
  font-size: 20px;
  font-weight: bold;
}

.el-select .el-input__inner {
  width: 440px !important;
}

.description {
  width: 100%;
  height: 32px;
  background-color: #F2F2F2;
  display: flex;
  align-items: center;

  > span {
    margin-left: 8px;
    font-size: 14px;
  }
}

.box-card {
  margin-top: 16px;
  position: relative;
  cursor: pointer;
}

.groupCategory {
  top: 0px;
  right: 0px;
  position: absolute;
  width: 43px;
  height: 18px;
  background-color: #FCB215;
  border-radius: 4px;
  text-align: center;

  > div {
    line-height: 18px;
    font-size: 12px;
    color: #000;
  }
}

.userCategory {
  top: 0px;
  right: 0px;
  position: absolute;
  width: 62px;
  height: 18px;
  background-color: #78BED3;
  border-radius: 4px;
  text-align: center;

  > div {
    line-height: 18px;
    font-size: 12px;
    color: #FFF;
  }
}

.owner {
  font-size: 14px;
  font-family: BarlowMedium;
  font-weight: bold;
  color: #175EC2;
  margin-bottom: 4px;
}

.progress-text {
  margin-top: -10px;
  font-size: 24px;
  font-weight: bold;
  font-family: BarlowBold;
}

.form-label {
  line-height: 40px;
  float: none;
  font-size: 14px;
  color: #aaa;
  display: flex !important;
  text-align: left;
  height: 32px;
  margin-top: 4px !important;
  padding: 0 !important;
}

.generic {
  background: #78BED3;
  font-size: 16px;
  line-height: 16px !important;
  border-radius: 20px;
  opacity: 0.65;
  padding: 0px 12px;
}

.el-tabs {
  position: relative;
}

.businessScenarioStyle {
  flex-shrink: 0;
  width: auto;
  display: flex !important;
  //margin-left: 16px;
  //height: 24px;
  background-color: #78BED3;
  padding: 0px 8px;
  text-align: center !important;
  border-radius: 14px;
  color: #FFF;
  font-size: 14px;
  //line-height: 24px;
}
.project-card {
  padding: 0;
}
</style>