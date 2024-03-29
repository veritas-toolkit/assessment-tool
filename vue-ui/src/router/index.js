import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../components/login/Login.vue'
import Home from '../components/Home'
import Group from '../components/group/Group'
import GroupPage from '../components/group/GroupPage'
import Projects from '../components/projects/Projects'
import ProjectPage from '../components/projects/ProjectPage'
import MyAccount from "../components/myAccount/MyAccount";
import Administration from "../components/administration/Administration";
import AdminProjectPage from "../components/administration/AdminProjectPage";
import AdminGroupPage from "../components/administration/AdminGroupPage";
import AdminUserPage from "../components/administration/AdminUserPage";
import CreateUser from "../components/administration/CreateUser";
import AdminTemplatePage from "../components/administration/AdminTemplatePage";
import Questionnaire from "@/components/questionnaire/Questionnaire";
import QuestionnaireHistory from "@/components/questionnaireHistory/QuestionnaireHistory";

import Template from "@/components/template/Template";
import AdminTemplate from "@/components/adminTemplate/AdminTemplate";

Vue.use(VueRouter)

const router = new VueRouter({
  routes: [
    {path: '/', redirect: '/login'},
    {path: '/login', component: Login},
    {
      path: '/home',
      component: Home,
      redirect: '/projects',
      children: [
        {path: '/projects', component: Projects},
        {path: '/projectPage', component: ProjectPage},
        {path: '/group', component: Group},
        {path: '/groupPage', component: GroupPage},
        {path: '/myAccount', component: MyAccount},
        {path: '/administration', component: Administration},
        {path: '/adminProjectPage', component: AdminProjectPage},
        {path: '/adminGroupPage', component: AdminGroupPage},
        {path: '/adminUserPage', component: AdminUserPage},
        {path: '/createUser', component: CreateUser},
      ]
    },
    {path: '/adminTemplatePage', component: AdminTemplatePage},
    {path: '/questionnaire', name:'questionnaire', component: Questionnaire},
    {path: '/questionnaireHistory', component: QuestionnaireHistory},
    {path: '/template', component: Template},
    {path: '/adminTemplate',component:AdminTemplate}
  ]
})

export default router
