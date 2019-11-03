<template>
    <v-app>
        <navbar></navbar>
        <v-content>
            <router-view></router-view>
        </v-content>
        <v-footer>
            <v-spacer></v-spacer>
            <div> {{ new Date().getFullYear() }}</div>
        </v-footer>
    </v-app>
</template>

<script>
    import Navbar from "components/Navbar.vue";
    import Footer from "components/Footer.vue";

    export default {
        components: {Navbar, Footer}
    }
</script>
<style scoped></style>

