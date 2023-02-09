<template>
  <div style="position: relative;height: 100%">
    <div class="vertical-line"></div>
    <div class="main-ques-box">
      <div class="main-ques">F1. Question content</div>
      <div class="main-ques">F1. Question content</div>
    </div>
    <div class="compare-box">
      <div class="compare-box-left">
        <div class="sub-ques">sub-ques</div>
        <div class="sub-ans">sub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans

        </div>
      </div>
      <div class="compare-box-right">
        <div class="sub-ques">sub-ques</div>
        <div class="sub-ans">sub-ans</div>
      </div>
    </div>
    <div class="compare-box">
      <div class="compare-box-left">
        <div class="sub-ques">sub-ques</div>
        <div class="sub-ans">sub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans
          sub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-anssub-ans

        </div>
      </div>
      <div class="compare-box-right">
        <div class="sub-ques">sub-ques</div>
        <div class="sub-ans">sub-ans</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "QuestionnaireCompareAnswer"
}
</script>

<style scoped lang="less">
.vertical-line {
  width: 1px;
  height: 100%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%,-50%);
  background-color: #D5D8DD;
}
.main-ques-box {
  display: flex;
}
.main-ques {
  font-size: 16px;
  font-family: BarlowBold;
  width: calc(50% - 1px);
  margin: 16px 24px;
}
.compare-box {
  display: flex;
  justify-content: space-between;
}
.compare-box-left {
  padding: 0px 24px 16px 24px;
  width: calc(50% - 49px);
}
.compare-box-right {
  padding: 0px 24px 16px 24px;
  width: calc(50% - 49px);
}
.sub-ques {
  font-size: 16px;
  font-family: BarlowMedium;
  padding: 8px 16px;
  background: #F5F7F9;
  border-radius: 4px 4px 0px 0px;
  border: 1px solid #D5D8DD;
}
.sub-ans {
  padding: 8px 16px;
  font-size: 16px;
  font-family: BarlowMedium;
  border-radius: 0px 0px 4px 4px;
  border-right: 1px solid #D5D8DD;
  border-left: 1px solid #D5D8DD;
  border-bottom: 1px solid #D5D8DD;
}
</style>