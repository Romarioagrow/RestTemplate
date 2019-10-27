<template>
    <v-app>
        <navbar></navbar>
        <v-content>
            <router-view></router-view>
        </v-content>
        <v-footer class="mt-5">
            <v-spacer></v-spacer>
            <div> {{ new Date().getFullYear() }}</div>
        </v-footer>
    </v-app>
</template>

<script>
    import Navbar from "components/Navbar.vue";

    export default {
        components: {Navbar}
    }
</script>
<style scoped></style>

