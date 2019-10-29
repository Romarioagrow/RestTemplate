<template>
    <v-container fluid>
        <v-row>
            <v-col>
                <v-btn @click="logout">
                    <span>Выход</span>
                    <v-icon>mdi-logout</v-icon>
                </v-btn>
            </v-col>
        </v-row>
    </v-container>

</template>

<script>
    import axios from 'axios'
    export default {
        data() {
            return {

            }
        },
        methods: {
            logout() {
                axios.post('http://localhost:9000/user/logout').then((response) => {
                    this.$store.dispatch('logout')
                    this.$router.push('/')
                })
            }
        }
    }
</script>

<style scoped>
</style>