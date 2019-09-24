import Vue from 'vue';
import VueRouter from "vue-router";
import TestPage from "pages/TestPage.vue";
import Home from "pages/Home.vue";
import Products from "pages/Products.vue";

Vue.use(VueRouter);

const routes = [
    {path: '/', component: Home},
    {path: '/products/:group', component: Products},
    {path: '/test', component: TestPage}
];

export default new VueRouter({
    mode: 'history',
    routes
})