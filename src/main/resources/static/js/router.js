import Vue from 'vue';
import VueRouter from "vue-router";
import TestPage from "pages/TestPage.vue";
import Home from "pages/Home.vue";
import Products from "pages/Products.vue";
import ProductInfo from "pages/ProductInfo.vue"
import Admin from "pages/Admin.vue"

Vue.use(VueRouter);

const routes = [
    {path: '/', component: Home},
    {path: '/products/:group', component: Products},
    {path: '/test', component: TestPage},
    {path: '/products/product/:productID', component: ProductInfo},
    {path: '/admin', component: Admin},
];

export default new VueRouter({
    mode: 'history',
    routes
})