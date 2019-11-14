<template>
    <div>
        <v-app-bar color="#f48337" dark :clipped-left="$vuetify.breakpoint.lgAndUp" app height="70" src="http://www.picshare.ru/uploads/191113/6b5152mGVs.jpg">

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
                    @keyup="searchProducts()"
            ></v-autocomplete>

            <!--<v-container v-if="searchArea" class="search">
                <v-card outlined>
                    <v-list subheader>
                        <v-subheader>Найдено</v-subheader>

                        <v-list-item v-for="product in searchedProducts" :key="product.productID" @click="">


                            <v-list-item-avatar>
                                <v-img :src="product.pic"></v-img>
                            </v-list-item-avatar>


                            <v-list-item-content class="ml-12">
                                <router-link :to="'/products/product/' + product.productID">
                                    <v-list-item-title v-text="product.fullName" style="color: black"></v-list-item-title>
                                </router-link>
                            </v-list-item-content>


                        </v-list-item>
                    </v-list>
                </v-card>
            </v-container>-->

            <v-card class="search" width="30%" style=" background-color: #fafafa; color: black" v-if="searchArea">
                <v-card-text style="color: black">
                    <p v-for="product in searchedProducts" :key="product.productID">
                        {{product.fullName}}
                    </p>
                </v-card-text>
            </v-card>

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
    import searchJSON from 'assets/json/search.json'
    import axios from "axios";
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

            searchedProducts: []
        }),
        created() {
            this.states = searchJSON
        },
        /*watch: {
            search (val) {
                val && val !== this.select && this.querySelections(val)
            },
        },*/
        methods: {
            searchProducts() {
                this.loading = true

                console.log(this.search)



                axios.post('api/products/searchProducts', this.search).then(response => {
                    //console.log(response.data)

                    this.searchedProducts = response.data
                    this.$store.dispatch('showSearchedArea')
                })



                this.loading = false


               /* setTimeout(() => {
                    /!*console.log(this.search)
                    console.log(this.states)*!/

                    axios.post('api/products/searchProducts')

                    this.loading = false
                }, 500)*/


            },
            /*querySelections(v) {
                this.loading = true
                setTimeout(() => {
                    //this.items = ['suka', 'lol','loloi']



                    this.items = this.states.filter(e => {
                        return (e || '').toLowerCase().indexOf((v || '').toLowerCase()) > -1
                    })

                    console.log(this.items)

                    this.loading = false
                }, 500)
            }*/
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
            },
            searchArea() {
                return this.$store.state.showSearchedArea
            }

        }
    }
</script>

<style scoped>
    .search {
        position: absolute;
        left: 39%;
        top: 100%;
        background-color: #fafafa;
    }
</style>
