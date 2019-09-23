<template>
    <v-app>
        <navbar></navbar>
        <router-view></router-view>
    </v-app>
</template>

<script>
    import Navbar from "components/Navbar.vue";
    export default {
        components: {Navbar}
    }
</script>
<style scoped></style>