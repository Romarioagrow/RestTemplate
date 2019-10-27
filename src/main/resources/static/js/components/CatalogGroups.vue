<template>
    <router-link :to="path">
        <v-card class="mx-auto mt-3" width="300" height="300">
            <v-img class="white--text w-50" :src="group[1]" contain width="200" height="200"></v-img>
            <v-card-title class="font-weight-light" >
                {{group[0]}}
            </v-card-title>
        </v-card>
    </router-link>
</template>

<script>
    export default {
        props: {
            group: []
        },
        data() {
            return {
                path: '/products/' + this.group[0].toLowerCase().replace(' ', '_'),
            }
        }
    }
</script>

<style scoped>
</style>