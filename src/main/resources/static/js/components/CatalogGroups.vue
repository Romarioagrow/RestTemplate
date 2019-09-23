<template>
    <v-card class="mx-auto" max-width="400">
        <v-img
                class="white--text"
                height="200px"
                :src="group.groupPic"
        >
        </v-img>
        <v-card-title>
            <div>{{group.groupName}}</div>
        </v-card-title>
    </v-card>
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
    }
</script>

<style scoped>
</style>