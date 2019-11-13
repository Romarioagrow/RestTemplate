<template>
    <div>
        <v-app-bar color="#f48337" dark app height="70" src="http://www.picshare.ru/uploads/191113/6b5152mGVs.jpg">

            <!--<v-btn outlined class="mr-3" v-if="filtersClosed">Фильтры</v-btn>-->


            <router-link to="/">
                <v-app-bar-nav-icon></v-app-bar-nav-icon>
            </router-link>

            <v-toolbar-title>
                <router-link to="/">
                    <v-img src="http://www.picshare.ru/uploads/191113/E699PhEOi3.png" contain height="60"></v-img>
                </router-link>
            </v-toolbar-title>

            <v-spacer></v-spacer>

            <v-autocomplete
                    v-model="select"
                    :loading="loading"
                    :items="items"
                    :search-input.sync="search"
                    cache-items
                    class="mx-4"
                    flat
                    hide-no-data
                    hide-details
                    label="Найдите что вам нужно"
                    solo-inverted
                    @keyup="search()"
            ></v-autocomplete>

            <v-spacer></v-spacer>

            <router-link to="/order" class="ml-5">
                <v-btn tile outlined>
                    <span>Корзина</span>
                    <v-icon>mdi-cart</v-icon>
                </v-btn>
            </router-link>

            <div v-if="!admin">
                <router-link to="/user/cabinet" class="ml-5" v-if="auth">
                    <v-btn tile outlined>
                        <span>Личный кабинет</span>
                        <v-icon>mdi-account</v-icon>
                    </v-btn>
                </router-link>
                <router-link to="/login" class="ml-5" v-else>
                    <v-btn tile outlined>
                        <span>Вход</span>
                        <v-icon>mdi-login-variant</v-icon>
                    </v-btn>
                </router-link>
            </div>

            <router-link to="/admin" class="ml-5" v-if="admin">
                <v-btn>
                    <span>Admin</span>
                    <v-icon>mdi-account-badge-horizontal</v-icon>
                </v-btn>
            </router-link>
        </v-app-bar>
    </div>
</template>

<script>
    export default {
        name: "Navbar",
        data: () => ({
            collapseOnScroll: true,
            loading: false,
            items: [],
            search: null,
            select: null,
            states: [
                'Alabama',
                'Alaska',
            ],
        }),
        watch: {
            search (val) {
                val && val !== this.select && this.querySelections(val)
            },
        },
        methods: {
            /*search() {

            },*/
            querySelections(v) {
                this.loading = true
                // Simulated ajax query
                setTimeout(() => {
                    this.items = this.states.filter(e => {
                        return (e || '').toLowerCase().indexOf((v || '').toLowerCase()) > -1
                    })
                    this.loading = false
                }, 500)
            }
        },
        computed: {
            auth () {
                return this.$store.state.currentUser && !this.$store.state.currentUser.roles.includes('ADMIN')
            },
            admin() {
                return this.$store.state.currentUser && this.$store.state.currentUser.roles.includes('ADMIN')
            },
            filtersClosed() {
                return this.$store.state.filtersClosedButton
            }
        }
    }
</script>

<style scoped>
</style>
