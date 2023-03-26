<template>
  <div class="item" :class="isActive ? 'is-active' : ''">
    <div class="title">
      {{title}}
      <!-- <div class="title-txt">{{title}}</div> -->
      <div class="line"></div>
    </div>
    <div class="content">{{content}}</div>
  </div>
</template>

<script>
  export default {
    name: 'MenuItemQuestionaire',

    componentName: 'MenuItemQuestionaire',
    data() {
      return {
        // isActive: true
      }
    },
    props: {
      title: String,
      content: String,
      isActive: {
        type: Boolean,
        default() {
          return false
        }
      }
    },
  }
</script>

<style lang="less" scoped>
.item {
  padding: 8px;
  background-color: #fff;
  display: flex;
  cursor: pointer;
}
.is-active {
  background: #F2F5F7;
  border-radius: 4px; 
}
.title {
  padding: 0 1px;
  font-size: 14px;
  font-family: Barlow-Bold, Barlow;
  font-weight: bold;
}
.title-txt {
  width: 30px;
  text-align: center;
}

.line {
  // display: inline-block;
  // border-left: 1px solid #B7D0E4;
}
.content {
  padding: 0 2px;
  margin-left: 8px;
}
</style>
