<template>
    <v-app>
        <navbar></navbar>
        <v-content>
            <router-view></router-view>
        </v-content>
    </v-app>
</template>

<script>
    import Navbar from "components/Navbar.vue";
    export default {
        components: {Navbar}
    }
</script>
<style scoped></style>

