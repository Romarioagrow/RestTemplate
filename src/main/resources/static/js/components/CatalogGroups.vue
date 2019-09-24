<template>
    <router-link :to="path">
        <v-card class="mx-auto" width="300" height="300">
            <v-img class="white--text w-50" :src="group.groupPic"></v-img>
            <v-card-title>
                <div>{{group.groupName}}</div>
            </v-card-title>
        </v-card>
    </router-link>

</template>

<script>
    export default {
        props: {
            group: {
                type: Object,
                required: true
            },
            index: Number
        },
        data() {
            return {
                path: '/products/' + this.group.groupName.toLowerCase().replace(' ', '_'),
            }
        }
    }
</script>

<style scoped>
</style>