<template>
  <div style="padding: 24px !important;">
    <div class="category">Groups</div>
    <!--header-->
    <el-row :gutter="20">
      <el-col :span="20">
        <el-input @input="getGroupList" placeholder="Search your group here" prefix-icon="el-icon-search" v-model="keyword">
        </el-input>
      </el-col>
      <el-col :span="4">
        <div class="creProj BarlowMedium" style="cursor: pointer" @click="dialogVisible = true"><i class="el-icon-plus"></i><span>Create group</span></div>
      </el-col>
    </el-row>
    <!--main tabs-->
    <div style="margin-top: 10px">
      <el-tabs v-model="activeName" class="BarlowMedium" >
        <el-tab-pane label="All" name="first">
          <div class="bodyRow" v-for="(item,index) in groupList" :key="item.id" @click="groupPage(item)">
            <!--first character-->
            <div class="firstText"><span>{{item.name.substring(0,1)}}</span></div>
            <!--name and description-->
            <div class="nameDesc oneLine">
              <div class="oneLine">{{item.name}}</div>
              <span>{{item.description}}</span>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Created by me" name="second">
          <div class="bodyRow" v-for="(item,index) in myGroupList" :key="item.id" @click="groupPage(item)">
            <div class="firstText"><span>{{item.name.substring(0,1)}}</span></div>
            <div class="nameDesc oneLine">
              <div class="oneLine">{{item.name}}</div>
              <span>{{item.description}}</span>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div class="block">
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
    <el-dialog :close-on-click-modal="false" class="BarlowMedium" :visible.sync="dialogVisible" width="548px" append-to-body>
      <template slot="title"><span class="dialogTitle">New group</span></template>
      <el-form :rules="groupFormRules" ref="groupFormRefs" label-position="top" label="450px" :model="groupForm">
        <el-form-item class="BarlowMedium" label="Group name" prop="name">
          <el-input placeholder="Please input a group name" v-model="groupForm.name"></el-input>
        </el-form-item>
        <el-form-item class="BarlowMedium" label="Group description" prop="description">
          <el-input type="textarea" :rows="3" placeholder="Please input group description here" v-model="groupForm.description"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="BlackBorder BarlowMedium">Cancel</el-button>
        <el-button type="primary" @click="createGroup" class="GreenBC BarlowMedium">Create</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "group",
    created() {
      this.getGroupList()
    },
    data() {
      return {
        activeName: 'first',
        keyword: '',
        dialogVisible: false,
        groupForm: {
          name: '',
          description: '',
        },
        groupFormRules: {
          name: [{ required: true, trigger: 'blur' },],
          description: [{ required: true, trigger: 'blur' },],
        },
        groupList: [],
        myGroupList: [],
        page: 1,
        pageSize: 8,
        total: 0,
      }
    },
    watch: {
      activeName:function () {
        this.page = 1
        this.keyword = ''
        this.getGroupList()
      }
    },
    methods: {
      createGroup() {
        this.$refs.groupFormRefs.validate(val => {
          if (val) {
            this.$http.put('/api/group/new',this.groupForm).then(res => {
              if (res.status == 201) {
                this.$message.success('Create successfully')
                this.$refs.groupFormRefs.resetFields()
                this.getGroupList()
              }
            })
            this.dialogVisible = false
          }
        })
      },
      handleSizeChange(val) {
        this.pageSize = val
        this.getGroupList()
      },
      handleCurrentChange(val) {
        this.page = val
        this.getGroupList()
      },
      getGroupList() {
        if (this.activeName == 'first') {
          this.$http.get('/api/group',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
            if (res.status == 200) {
              this.groupList = res.data.records
              this.total = res.data.total
            }
          })
        } else if (this.activeName == 'second') {
          this.$http.get('/api/group/my-groups',{params: {'keyword': this.keyword,page:this.page,pageSize:this.pageSize}}).then(res => {
            if (res.status == 200) {
              this.myGroupList = res.data.records
              this.total = res.data.total
            }
          })
        }
      },
      groupPage(item) {
        this.$router.push({path:'/groupPage',query: {groupId:item.id}})
      }
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
    color: #FFF;
  }
  .boxCardHeader {
    > div {
      font-weight: bold;
      font-size: 16px;
    }
    > span {
      margin-top: 10px;
      font-size: 12px;
      color: #bbb;
    }
  }
  .el-progress {
    margin-top: 10px;
  }
  .progressLabel {
    color: darkblue;
    margin-top: 18px;
    font-size: 16px;
  }
  .dialogTitle {
    font-size: 20px;
    font-weight: bold;
  }
  .bodyRow {
    cursor: pointer;
    height: 80px;
    border-bottom: 1px solid #E5E7EB;
    display: flex;
    align-items: center;
  }
  .firstText {
    flex-shrink: 0;
    text-align: center;
    min-width: 48px;
    height: 48px;
    background-color: #D5EBF1;
    border-radius: 4px;
    margin-right: 16px;
    > span {
      font-size: 24px;
      font-weight: 600;
      color: #35839A;
      line-height: 48px;
    }
  }
  .nameDesc {
    flex-shrink: 0;
    min-width: calc(100% - 64px);
    height: 48px;
    > div {
      font-size: 16px;
      font-weight: 600;
      line-height: 24px;
      padding-bottom: 2px;
    }
    > span {
      font-size: 14px;
      font-weight: 400;
      color: #B8B8B8;
    }
  }
  .el-textarea__inner {
    font-size: 50px;
  }
</style>