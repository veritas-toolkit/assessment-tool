<template>
  <div style="border: 1px solid;height: 100%">
    <el-collapse class="BarlowBold" v-model="activeName" accordion>
      <el-menu
          default-active="2"
          class="el-menu-vertical-demo">
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-location"></i>
            <span>导航一</span>
          </template>
        </el-submenu>
        <el-menu-item index="2">
          <i class="el-icon-menu"></i>
          <span slot="title">导航二</span>
        </el-menu-item>
      </el-menu>
    </el-collapse>
  </div>
</template>

<script>
export default {
  name: "QuestionnaireMenu",
  data() {
    return {
      activeName: '',
    }
  }
}
</script>

<style scoped>

</style>