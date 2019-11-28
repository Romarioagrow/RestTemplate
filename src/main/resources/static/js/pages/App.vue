<template>
    <v-app >
        <navbar></navbar>

        <v-content class="bg">
            <div @click="hideSearch()">
               <!-- <v-progress-linear indeterminate color="#e52d00"></v-progress-linear>-->
                <router-view ></router-view>
            </div>
        </v-content>


        <!--<v-footer>
            <v-spacer></v-spacer>
            <div> {{ new Date().getFullYear() }}</div>
        </v-footer>-->

    </v-app>
</template>

<script>
    import axios from 'axios'
    import Navbar from "components/Navbar.vue";
    import Footer from "components/Footer.vue";
    export default {
        components: {Navbar, Footer},
        beforeCreate() {
            axios.get('/api/order/checkSessionOrder').then(response => {
                if (response.data === false) {
                    this.$store.dispatch('removeOrder')
                    this.$store.dispatch('clearOrderedProducts')
                }
            })
        },
        methods: {
            hideSearch() {
                this.$store.dispatch('hideSearchedArea')
            }
        }
    }
</script>

<style scoped>
    .bg{
        background-color: #f2f2f2;
    }
</style>

