<template>
  <div style="padding: 24px !important;">
    <div class="category">Administration</div>
    <!--header area-->
    <el-row :gutter="20" v-show="activeName != 'third' && activeName != 'fourth'">
      <el-col :span="24">
        <el-input :disabled="activeName == 'fifth'" @input="searchByPrefix" placeholder="Search project/group/user/questionnaire here" prefix-icon="el-icon-search"
                  v-model="keyword">
        </el-input>
      </el-col>
    </el-row>
    <el-row :gutter="20" v-show="activeName == 'third'">
      <el-col :span="20">
        <el-input :disabled="activeName == 'fifth'" @input="searchByPrefix" placeholder="Search project/group/user/questionnaire here" prefix-icon="el-icon-search"
                  v-model="keyword">
        </el-input>
      </el-col>
      <el-col :span="4">
        <div class="creProj BarlowMedium" style="cursor: pointer" @click="createUser"><i class="el-icon-plus"></i><span>Create user</span></div>
      </el-col>
    </el-row>
    <el-row :gutter="20" v-show="activeName == 'fourth'">
      <el-col :span="20">
        <el-input :disabled="activeName == 'fifth'" @input="searchByPrefix" placeholder="Search project/group/user/questionnaire here" prefix-icon="el-icon-search"
                  v-model="keyword">
        </el-input>
      </el-col>
      <el-col :span="4">
        <div class="creProj BarlowMedium" style="cursor: pointer" @click="templateVisibleDo"><i class="el-icon-plus"></i><span>Create template</span></div>
      </el-col>
    </el-row>
    <!--main tabs-->
    <div style="margin-top: 10px">
      <el-tabs class="BarlowMedium" v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="Project" name="first">
          <div class="bodyRow" v-for="(item,index) in projectList" :key="item.id" @click="adminProjectPage(item)">
            <div style="display: flex;align-items: center">
              <div class="firstText"><span>{{ item.name.substring(0, 1) }}</span></div>
              <div class="nameDesc oneLine">
                <div class="oneLine">
                  <span v-if="item.userOwner">{{item.userOwner.username}}</span>
                  <span v-else-if="item.groupOwner">{{item.groupOwner.name}}</span>
                  / {{ item.name }}</div>
                <span>{{ item.description }}</span>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Group" name="second">
          <div class="bodyRow" v-for="(item,index) in groupList" :key="item.id" @click="adminGroupPage(item)">
            <div style="display: flex;align-items: center">
              <div class="firstText"><span>{{item.name.substring(0,1)}}</span></div>
              <div>
                <div class="nameDesc oneLine">
                  <div class="oneLine">{{item.name}}</div>
                  <span>{{item.description}}</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="User" name="third">
          <div class="bodyRow" v-for="(item,index) in userList" :key="item.userId" @click="adminUserPage(item)">
            <div style="display: flex;align-items: center">
              <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
              <div class="nameDesc oneLine">
                <div class="oneLine" style="display: flex;align-items: center">
                  <div>{{item.fullName}}</div>
                  <div v-show="item.locked" class="locked-style">locked</div>
                </div>
                <span>{{item.email}}</span>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Questionnaire" name="fourth">
          <div class="bodyRow" v-for="(item,index) in templateList" :key="item.templateId">
            <div style="display: flex;align-items: center;width: calc(100% - 32px)" @click="adminTemplatePage(item)">
              <img class="avatar" src="../../assets/adminPic/template.png" alt="">
              <div class="nameDesc oneLine">
                <div class="oneLine">{{item.name}}</div>
                <span>{{item.description}}</span>
              </div>
            </div>
            <el-button style="width: 32px" v-show="item.type != 'SYSTEM'" type="danger" icon="el-icon-delete" circle size="small" @click="deleteTemplate(item.templateId)"></el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="System" name="fifth">
          <el-card class="system-style">
            <div>
              <div>
                <div style="display: flex;align-items: center;justify-content: space-between">
                  <span class="BarlowBold system-config">Register</span>
                  <el-switch v-model="register_supported" active-color="#67C23A" inactive-color="#C0C4CC" @change="registerSystemConfig"></el-switch>
                </div>
                <div class="system-control-text">Control whether registration is allowed on the login page</div>
              </div>
              <el-divider></el-divider>
              <div>
                <div style="display: flex;align-items: center;justify-content: space-between">
                  <span class="BarlowBold system-config">Modify account</span>
                  <el-switch v-model="modify_account_supported" active-color="#67C23A" inactive-color="#C0C4CC" @change="modifyAccountSystemConfig"></el-switch>
                </div>
                <div  class="system-control-text">Decide whether the username email or full name can be modified on the my account page</div>
              </div>
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
      <div class="block" v-show="activeName != 'fifth'">
        <el-pagination
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
    <!--create group-->
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" @close="closeCreateTemplate" :visible.sync="templateVisible" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">New questionnaire template</span></template>
      <el-form ref="templateFormRefs" label-position="top" label="450px" :model="templateForm">
        <el-form-item class="BarlowMedium" label="Create from" prop="basicTemplateId">
          <el-select v-model="templateForm.basicTemplateId" placeholder="Please choose a existing template to create from">
            <el-option v-for="item in createTemplateList" :key="item.templateId" :label="item.name" :value="item.templateId">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.description }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Template name" prop="name">
          <el-input placeholder="Please input a template name" v-model="templateForm.name"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Template description" prop="description">
          <el-input type="textarea" :rows="3" placeholder="Please input template description here" v-model="templateForm.description"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="templateVisible = false" class="BlackBorder BarlowMedium">Cancel</el-button>
        <el-button type="primary" @click="createTemplate" class="GreenBC BarlowMedium">Create</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Administration",
  data() {
    return {
      activeName: 'first',
      keyword: '',
      projectList: [],
      groupList: [],
      userList: [],
      templateVisible: false,
      templateForm: {
        basicTemplateId: '',
        name: '',
        description: '',
      },
      templateList: [],
      createTemplateList: [],
      page: 1,
      pageSize: 8,
      total: 0,
      register_supported: '',
      modify_account_supported: '',
    }
  },
  mounted() {
    if (this.$route.query.activeName) {
      this.activeName = this.$route.query.activeName
    } else {this.activeName = 'first'}
  },
  created() {
    this.getProjectList()
    this.getSystemConfig()
  },
  watch: {
    activeName: function () {
      this.page = 1
      this.keyword = ''
      if (this.activeName == 'first') {
        this.getProjectList()
      } else if (this.activeName == 'second') {
        this.getGroupList()
      } else if (this.activeName == 'third') {
        this.getUserList()
      }
      else if (this.activeName == 'fourth') {
        this.getTemplateList()
      }
    }
  },
  methods: {
    registerSystemConfig() {
      this.$http.post('/api/admin/system/config',{'register_supported':this.register_supported}).then(res => {
        if (res.status == 200) {
          if (this.register_supported == true) {
            this.$message.success('Set support register successfully')
          } else if (this.register_supported == false) {
            this.$message.success('Set not support register successfully')
          }
        }
      })
    },
    modifyAccountSystemConfig() {
      this.$http.post('/api/admin/system/config',{'modify_account_supported':this.modify_account_supported}).then(res => {
        if (res.status == 200) {
          if (this.modify_account_supported == true) {
            this.$message.success('Set support modify account successfully')
          } else if (this.modify_account_supported == false) {
            this.$message.success('Set not support modify account successfully')
          }
        }
      })
    },
    getSystemConfig() {
      this.$http.get('/api/admin/system/config').then(res => {
        if (res.data.register_supported == null || res.data.register_supported == 'false') {
          this.register_supported = false
        } else if (res.data.register_supported == 'true') {
          this.register_supported = true
        }
        if (res.data.modify_account_supported == null || res.data.modify_account_supported == 'false') {
          this.modify_account_supported = false
        } else if (res.data.modify_account_supported == 'true') {
          this.modify_account_supported = true
        }
      })
    },
    templateVisibleDo() {
      this.getCreateTemplateList()
      this.templateVisible = true
    },
    deleteTemplate(id) {
      this.$confirm('Confirm delete?',{type: 'warning'}).then(() => {
        this.$http.delete(`/api/admin/questionnaire/${id}`).then(res => {
          if (res.status == 200) {
            this.$message.success('Delete successfully')
            this.getTemplateList()
          }
        })
      })
    },
    getCreateTemplateList() {
      this.$http.get('/api/admin/questionnaire').then(res => {
        if(res.status == 200) {
          this.createTemplateList = res.data.records
        }
      })
    },
    closeCreateTemplate() {
      this.$refs.templateFormRefs.resetFields()
    },
    createTemplate() {
      this.$http.put('/api/admin/questionnaire',this.templateForm).then(res => {
        if (res.status == 200) {
          this.$message.success('Create successfully')
          this.getTemplateList()
          this.getCreateTemplateList()
          this.templateVisible = false
        }
      })
    },
    handleClick(tab, event) {
      this.activeName = tab.name
    },
    searchByPrefix() {
      if (this.activeName == 'first') {
        this.getProjectList()
      } else if (this.activeName == 'second') {
        this.getGroupList()
      } else if (this.activeName == 'third') {
        this.getUserList()
      }
      else if (this.activeName == 'fourth') {
        this.getTemplateList()
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      if (this.activeName == 'first') {
        this.getProjectList()
      } else if (this.activeName == 'second') {
        this.getGroupList()
      } else if (this.activeName == 'third') {
        this.getUserList()
      }
      else if (this.activeName == 'fourth') {
        this.getTemplateList()
      }
    },
    handleCurrentChange(val) {
      this.page = val
      if (this.activeName == 'first') {
        this.getProjectList()
      } else if (this.activeName == 'second') {
        this.getGroupList()
      } else if (this.activeName == 'third') {
        this.getUserList()
      }
      else if (this.activeName == 'fourth') {
        this.getTemplateList()
      }
    },
    getProjectList() {
      this.$http.get('/api/admin/project',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
        if (res.status == 200) {
          this.projectList = res.data.records
          if (this.activeName == 'first') {
            this.total = res.data.total
          }
        }
      })
    },
    getGroupList() {
      this.$http.get('/api/admin/group',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
        if (res.status == 200) {
          this.groupList = res.data.records
          if (this.activeName == 'second') {
            this.total = res.data.total
          }
        }
      })
    },
    getUserList() {
      this.$http.get('/api/admin/user',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
        if (res.status == 200) {
          this.userList = res.data.records
          if (this.activeName == 'third') {
            this.total = res.data.total
          }
        }
      })
    },
    getTemplateList() {
      this.$http.get('/api/admin/questionnaire',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
        if (res.status == 200) {
          this.templateList = res.data.records
          if (this.activeName == 'fourth') {
            this.total = res.data.total
          }
        }
      })
    },
    adminProjectPage(item) {
      this.$router.push({path:'/adminProjectPage',query: {projectId:item.id}})
    },
    adminGroupPage(item) {
      this.$router.push({path:'/adminGroupPage',query: {groupId:item.id}})
    },
    adminUserPage(item) {
      this.$router.push({path:'/adminUserPage',query: {userId:item.id}})
    },
    adminTemplatePage(item) {
      this.$router.push({path:'/adminTemplatePage',query: {templateId:item.templateId}})
    },
    createUser() {
      this.$router.push({path:'/createUser'})
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
  .bodyRow {
    cursor: pointer;
    height: 78px;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .firstText {
    flex-shrink: 0;
    text-align: center;
    min-width: 48px;
    height: 48px;
    background-color: #D5EBF1;
    border-radius: 4px;
    > span {
      font-size: 24px;
      font-weight: 600;
      color: #35839A;
      line-height: 48px;
    }
  }
  .nameDesc {
    flex-shrink: 0;
    margin-left: 16px;
    height: 48px;
    > div {
      font-size: 16px;
      font-weight: 600;
      line-height: 24px;
      padding-bottom: 2px;
      > span {
        color: #175EC2;
      }
    }
    > span {
      font-size: 14px;
      font-weight: 400;
      color: #B8B8B8;
    }
  }
  .avatar {
    width: 40px;
    height: 40px;
  }
  .deleteMember {
    position: relative;
    width: 38px;
    height: 38px;
    margin-left: 16px;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    background-color: #F2F5F8;
    > img {
      width: 24px;
      height: 24px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%,-50%);
    }
  }
  .creProj {
    height: 40px;
    background-color: #78BED3;
    border-radius: 4px;
    text-align: center;
    color: #FFF;
    > span {
      color: #FFF;
      font-size: 16px;
      line-height: 40px;
      margin-left: 10px;
    }
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
  .edit-box {
    border: 1px solid;
    border-radius: 4px;
    font-size: 24px;
    padding: 4px 7px;
  }
  .delete-box {
    border: 1px solid;
    border-radius: 4px;
    font-size: 24px;
    padding: 4px 7px;
  }
  .locked-style {
    font-size: 14px;
    margin-left: 12px;
    padding: 0px 4px;
    font-weight: 400;
    color: #FFF;
    background-color: #F48200;
    border-radius: 12px;
  }
  .system-style {
    margin: 20px 10px 16px 10px;
    border-shaow: none !important;
  }
  .system-config {
    background-color: #78BED3;
    border-radius: 4px;
    padding: 2px 4px;
    color: #FFF;
  }
  .system-control-text {
    margin-top: 8px;
    font-size: 14px;
    color: #909399;
  }
</style>