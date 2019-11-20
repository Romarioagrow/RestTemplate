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

            <!--:loading="loading"
            v-model="select"-->
            <v-autocomplete
                    id="searchInput"

                    cache-items
                    class="mx-4"
                    flat
                    hide-no-data
                    hide-details
                    label="Найдите что вам нужно"
                    solo-inverted
                    @keyup="searchProducts()"
            ></v-autocomplete>

            <!--<v-text-field
                    id="searchInput"
                    class="mt-5"
                    label="Найдите что вам нужно"
                    single-line
                    outlined
                    @keyup="searchProducts()"
            ></v-text-field>-->


            <!--<v-container v-if="searchArea" class="display-result">
                <p v-for="product in searchedProducts" :key="product.productID">
                    <router-link :to="'/products/product/'+product.productID">
                        {{product.fullName}}
                    </router-link>
                </p>
            </v-container>-->

            <!--ВОТ ЭТА//////////-->
            <!--<v-container v-if="searchArea" class="display-result">
                <div v-for="product in searchedProducts" :key="product.productID">
                    <p @click="goToProductPage(product.productID)" >
                        {{product.fullName}}
                    </p>
                </div>
            </v-container>-->

            <v-container v-if="searchArea" class="display-result">
                <v-list shaped color="white">
                    <v-list-item-group >
                        <v-list-item v-for="product in searchedProducts" :key="product.productID" @click="goToProductPage(product.productID)" color="black" class="searchedItem">

                            <v-list-item-avatar>
                                <v-img :src="product.pic"></v-img>
                            </v-list-item-avatar>

                            <v-list-item-content>
                                <v-list-item-title v-text="product.fullName" style="color: black"></v-list-item-title>
                            </v-list-item-content>

                            <!--<p @click="goToProductPage(product.productID)" >
                                {{product.fullName}}
                            </p>-->
                        </v-list-item>
                    </v-list-item-group>
                </v-list>
            </v-container>

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
    import axios from "axios";
    export default {
        name: "Navbar",
        data: () => ({
            collapseOnScroll: true,
            loading: false,
        }),
        methods: {
            searchProducts() {
                const searchQuery = document.getElementById('searchInput').value
                this.$store.dispatch('searchProducts', searchQuery)

                //this.loading = true
                //this.loading = false

                /* setTimeout(() => {
                     /!*console.log(this.search)
                     console.log(this.states)*!/

                     axios.post('api/products/searchProducts')

                     this.loading = false
                 }, 500)*/
            },
            goToProductPage(productID) {
                //console.log('productID')

                this.$store.commit('hideSearchedAreaTrue')
                //this.$router.go('/products/product/'+productID)

                if (this.$route.fullPath.includes('product/')) {
                    this.$router.push('/products/product/'+productID)
                    location.reload()
                }
                else this.$router.push('/products/product/'+productID)
            }
        },
        computed: {
            searchedProducts() {
                return this.$store.state.searchedProducts
            },
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
    /*.searchedItem {
         background-color: #fafafa
     }*/
    .searchedItem:hover {
        background-color: #f2f2f2
    }
    .search {
        position: absolute;
        left: 39%;
        top: 100%;
        background-color: #fafafa;
    }
    .display-result {
        position: absolute;
        color: black;
        width: 50%;
        left: 30%;
        top: 100%;
        max-height: 400px;
        overflow: auto;
        box-shadow: 0 5px 13px 0 rgba(0,0,0,.2);
        background-color: white !important;
        border: 1px solid #ececec;
        padding: 15px;
        z-index: 50;
    }
</style>
