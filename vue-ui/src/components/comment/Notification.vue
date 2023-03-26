<template>
  <el-popover
      ref="notification_popover"
      placement="bottom"
      height="880"
      width="700"
      trigger="click">
    <el-tabs v-model="selectedTab" @tab-click="handleTabClick">
      <el-tab-pane label="All" name="all">
        <el-table :data="allUnreadCommentList"
                  height="300" width="800"
                  @row-click="handleCommentClick"
                  @cell-mouse-enter="">
          <el-table-column width="100" prop="userFullName" label="Name"/>
          <el-table-column width="100" label="Project">
            <template slot-scope="scope">
              <div>
                {{ scope.row['projectOwner'] }}/{{ scope.row['projectName'] }}
              </div>
            </template>
          </el-table-column>
          <el-table-column width="120" label="Created">
            <template slot-scope="scope">
              <div>{{ scope.row.createdTime | changeTime }}</div>
            </template>
          </el-table-column>
          <el-table-column min-width="200" prop="comment" label="Comment"/>
        </el-table>
      </el-tab-pane>

      <el-tab-pane v-if="projectId" label="Current Project" name="project">
        <el-table :data="currentProjectUnreadCommentList"
                  height="300" width="800"
                  @row-click="handleCommentClick"
                  @cell-mouse-enter="">
          <!--                  :show-header="false"-->
          <el-table-column width="100" prop="userFullName" label="Name"/>
          <el-table-column width="100" max-width="200" label="Question">
            <template slot-scope="scope">
              <div>
                {{ scope.row['mainQuestionSerial'] }}
              </div>
            </template>
          </el-table-column>
          <el-table-column width="120" label="Created">
            <template slot-scope="scope">
              <div>{{ scope.row.createdTime | changeTime }}</div>
            </template>
          </el-table-column>
          <el-table-column min-width="200" prop="comment" label="Comment"/>
        </el-table>

      </el-tab-pane>


    </el-tabs>
    <div class="not-box" slot="reference">
      <img src="../../assets/projectPic/notification.png" alt="">
      <span class="BarlowBold">Notifications</span>
      <div>{{ unreadCount }}</div>
    </div>
  </el-popover>
</template>

<script>

export default {
  name: "Notification",
  props: {
    projectId: {
      type: String,
      required: false
    },
    forceShowQuestion: {
      type: Boolean,
      default: false
    }

  },

  data() {
    return {
      selectedTab: null,
      allUnreadCommentList: [],
      currentProjectUnreadCommentList: [],
      // unused
      allPage: {
        page: 0,
        pageSize: 20,
        pageCount: 1,
        total: 0,
      },
      // unused
      currentProjectPage: {
        page: 0,
        pageSize: 20,
        pageCount: 1,
        total: 0,
      },

    };
  },

  created() {
    if (!this.projectId) {
      this.selectedTab = 'all';
    } else {
      this.selectedTab = 'project'
    }
    this.getUnreadCommentByProject();
    this.getUnreadComment();
    console.log("notification created.")
  },

  methods: {
    doToggle() {
      this.$refs.notification_popover.doToggle();
    },
    handleTabClick() {

    },
    handleCommentClick(row, event) {
      this.$emit("show-question", row);
      if (this.forceShowQuestion) {
        const isQuestionnairePage = this.$route.path === '/questionnaire';
        const isSameProject = this.$route.query.id === row.projectId;
        if (isQuestionnairePage && isSameProject) {
          this.$router.replace({
            path: "/questionnaire",
            query: {
              id : row['projectId'],
              q : row['mainQuestionSerial']
            }
          })
        } else {
          this.$router.push({
            path: "/questionnaire",
            query: {
              id : row['projectId'],
              q : row['mainQuestionSerial']
            }
          })
        }

      }
    },

    getUnreadCommentByProject() {
      if (this.projectId) {
        this.$http
            .get(`/api/project/${this.projectId}/questionnaire/all_unread_comment_list`)
            .then(res => {
              this.currentProjectUnreadCommentList = res.data
            });
      }
    },
    getUnreadComment() {
      this.$http
          .get('/api/all_unread_comment_list')
          .then(res => {
            this.allUnreadCommentList = res.data
            this.$emit('unreadCount', this.allUnreadCommentList.length)
          })
    },

  },
  computed: {
    unreadCount() {
      // if (this.selectTab !== 'all') {
      if (this.projectId) {
        return this.currentProjectUnreadCommentList.length;
      } else {
        return this.allUnreadCommentList.length;
      }

    }
  }

}
</script>

<style lang="less" scoped>

.not-box {
  display: flex;
  align-items: center;
  margin-left: 24px;

  > img {
    width: 24px;
    height: 24px;
  }

  > span {
    margin-left: 8px;
    font-size: 16px;
    color: #333333;
  }

  > div {
    margin-left: 8px;
    text-align: center;
    width: 24px;
    line-height: 24px;
    background-color: #FCB215;
    border-radius: 12px;
    color: #FFFFFF;
  }
}

</style>