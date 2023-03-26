<template>
  <div class="menu-bg" :style="isCollapse ? 'width: fit-content;' : ''">
    <el-collapse accordion class="collapseMenu" v-model="activeMenu">
      <template v-for="item in questionnaireList">
        <el-collapse-item class="menu-item" :name="item.title" :class="isCollapse ? 'myCollapse' : ''">
          <template slot="title">
            <img src="../assets/groupPic/time.png" class="menuIcon">
            <span class="title-txt" v-show="!isCollapse">{{item.title}}</span>
          </template>
          <template v-for="child in item.children">
            <MenuItemQuestionaire :isActive="activeName === child.title" :title="child.title" :content="child.content" @click.native="choosenItem(child.title)" />
          </template>
        </el-collapse-item>
      </template>
    </el-collapse>

    <img src="../assets/groupPic/admin.png" class="menuIcon btn" @click="(isCollapse = !isCollapse)" />

    <!-- <el-menu class="collapseMenu" :collapse="isCollapse">
      <el-submenu index="1" class="menu-item">
        <template slot="title">
          <i class="el-icon-location"></i>
          <span slot="title">导航一</span>
        </template>
        <el-submenu index="3" class="menu-item">
          <template slot="title">
          <img src="../assets/adminPic/administration.png" class="menuIcon">
          <span slot="title">Have you documented key errors, biases or properties present in the data used by the system that may impact the system’s fairness?</span>
          </template>
        </el-submenu>
        <el-submenu index="2" class="menu-item">
          <i class="el-icon-location"></i>
          <span slot="title" class="menu-title">Principles to Practice</span>
        </el-submenu>
      </el-submenu>
      <el-menu-item index="4" class="menu-item">
        <i class="el-icon-location"></i>
        <span slot="title" class="menu-title">Principles to Practice</span>
      </el-menu-item> -->
      
      <!-- <el-menu-item index="2">
        <i class="el-icon-menu"></i>
        <span slot="title">导航二</span>
      </el-menu-item> -->
    <!-- </el-menu> -->
  </div>
</template>

<script>
import PortfolioIcon from '@atlaskit/icon/glyph/portfolio'
import MenuItemQuestionaire from './MenuItemQuestionaire.vue'

export default {
    name: "MenuQuestionaire",
    components: { PortfolioIcon, MenuItemQuestionaire },
    data() {
      return {
        isCollapse: false,
        activeMenu: 'Principles to Practice',
        activeName: 'F04',
        questionnaireList: [
          {
            title: 'Principles to Practice',
            children: [
              {
                title: 'F04',
                content: 'Have you documented key errors, biases or properties present in the data used by the system that may impact the system’s fairness?'
              },
              {
                title: 'F05',
                content: 'Have you documented how are these impacts being mitigated?'
              },
              {
                title: 'F22',
                content: 'Have you assessed and documented the quantitative estimates of the system’s performance against its fairness objectives and the uncertainties in those estimates, assessed over the individuals and groups in F1 and the potential harms and benefits in F2?'
              },
              {
                title: 'F07',
                content: 'Have you assessed and documented the achievable trade-offs between the system’s fairness objectives and its other objectives?'
              }
            ]
          },
          {
            title: 'Define System Context & Design'
          },
          {
            title: 'Prepare Input data'
          },
          {
            title: 'Build & Validate'
          },
          {
            title: 'Deploy & Monitor'
          }
        ]
      }
    },
    methods: {
      choosenItem(val) {
        this.activeName = val
        console.log(val);
      }
    },
  }
</script>

<style lang="less" scoped>
.menu-bg {
  will-change: width;
  background: #F2F5F7;
  padding: 12px;
  width: 400px;
  // width: fit-content;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
  position: fixed;
  transition: all .5s;
}
.collapseMenu {
  // width: 400px;
}
.menu-item {
  border-radius: 8px;
  background-color: #fff;
}
.menu-item+.menu-item {
  margin-top: 12px;
}
.title-txt {
  margin-left: 8px;
}
.menu-title {
  font-size: 16px;
  font-family: Barlow-Medium, Barlow;
  font-weight: 500;
  color: rgba(0,0,0,0.85);
  margin-left: 7px;
}
.menuIcon {
    width: 24px;
    height: 24px;
    // margin-right: 8px;
  }
  .btn {
    position: absolute;
    left: 30px;
    top: 95%;
    cursor: pointer;
  }
</style>
