<template>
  <div style="padding: 4px;border-radius: 8px;overflow-y: auto">
    <div class="not-head">
      <div class="not-head-left">Notification</div>
      <div class="not-head-right" @click="markAllRead">Mark all as read</div>
    </div>
    <div v-for="(item,index) in unReadCommentList">
      <div class="body-list-box" @click="markCommentRead(item.projectId,item.questionId,item.id)">
        <img class="avatar" src="../../assets/groupPic/Avatar.png" alt="">
        <div>
          <div style="font-size: 14px;line-height: 14px">
            <span style="font-family: BarlowBold">{{item.username}}</span>
            <span style="font-family: BarlowMedium"> in </span>
            <span style="font-family: BarlowBold">{{item.projectName}}</span>
          </div>
          <div style="font-size: 12px;line-height: 12px;margin-top: 4px">
            {{item.createdTime|changeTime}}
          </div>
        </div>
      </div>
      <div class="body-comment" @click="markCommentRead(item.projectId,item.questionId,item.id)">{{item.comment}}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Notifications",
  created() {
    this.getUnreadComment()
  },
  data() {
    return {
      unReadCommentList: [],
    }
  },
  methods: {
    markCommentRead(pid,qid,cid) {
      this.$http.post(`/api/project/${pid}/questionnaire/question/${qid}/comment/${cid}/read`).then(res => {
        if (res.status == 200) {
          this.getUnreadComment()
        }
      })
    },
    getUnreadComment() {
      this.$http.get('/api/all_unread_comment_list').then(res => {
        if (res.status == 200) {
          this.unReadCommentList = res.data
          this.$emit('getNotLen',this.unReadCommentList.length)
        }
      })
    },
    markAllRead() {
      this.unReadCommentList.map(item => {
        this.$http.post(`/api/project/${item.projectId}/questionnaire/question/${item.questionId}/comment/${item.id}/read`).then(res => {
          if (res.status == 200) {
          }
        })
        this.getUnreadComment()
      })
    }
  }
}
</script>

<style scoped lang="less">
.not-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.not-head-left {
  font-size: 16px;
  font-family: BarlowBold;
  font-weight: 500;
}
.not-head-right {
  cursor: pointer;
  font-size: 14px;
  font-family: BarlowBold;
  font-weight: 500;
  color: #175EC2;
}
.avatar {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}
.body-list-box {

  padding: 12px 0px;
  border-top: 1px solid #E0E0E0;
  display: flex;
}
.body-comment {
  margin-bottom: 8px;
  font-size: 14px;
  font-family: BarlowMedium;
}
</style>