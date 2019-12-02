<template>
    <router-link :to="path" class="mt-4 mr-2">
        <v-card width="225" height="225" outlined>
            <v-card-title class="mb-3">
                <v-img class="white--text" :src="group[1]" contain height="125" ></v-img>
            </v-card-title>
            <v-card-text>
                <p class="subtitle-1 font-weight-bolder text-center">{{group[0]}}</p>
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

<style scoped></style>