import Vue from 'vue'
import VueResource from 'vue-resource'
import router from "./router";
import App from "pages/App.vue"
import Vuetify from "vuetify";
import 'vuetify/dist/vuetify.min.css'
import 'vue-material-design-icons/styles.css';
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.use(VueResource);
Vue.use(Vuetify/*,{ iconfont: 'mdiSvg' }*/);
Vue.use(BootstrapVue);


new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    router,
    render: a => a(App)
});

