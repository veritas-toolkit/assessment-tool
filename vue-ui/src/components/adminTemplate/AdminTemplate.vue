<template>
  <div>
    {{templateId}}
  </div>
</template>

<script>
export default {
  name: "AdminTemplate",
  data() {
    return {
      templateId: this.$route.query.templateId,
    }
  }
}
</script>

<style scoped lang="less">

</style>