<template>
    <router-link :to="path" class="mt-4 mr-2">
        <v-card width="225" height="225">
            <v-card-title>
                <v-img class="white--text" :src="group[1]" contain height="125" ></v-img>
            </v-card-title>

            <v-card-text>
                <p class="font-weight-bold text-center">{{group[0]}}</p>
            </v-card-text>
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