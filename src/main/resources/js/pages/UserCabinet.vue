<template>
    <v-container fluid>
        <v-row>
            <v-col cols="10">
                Здравствуйте, {{username}}
            </v-col>
            <v-col v-if="this.$store.state.currentUser.admin">
                suka ya admin
            </v-col>
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
                username: this.$store.state.currentUser.firstName
            }
        },
        methods: {
            logout() {
                axios.post('http://localhost:9000/user/logout').then((response) => {
                    this.$store.dispatch('logout')
                    this.$router.push('/')
                })
            }
        },
        beforeCreate() {
            axios.post('/auth/noUser').then(noUser => {
                if (noUser.data === true) {
                    this.$router.push('/login')
                }
            })
        },
        computed: {
            /*auth () {
                return this.$store.state.currentUser
            }*/
        }
    }
</script>

<style scoped>
</style>