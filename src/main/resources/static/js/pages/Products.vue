<template>
    <v-content>


        <v-row>
            <v-col cols="3" class="m-0 p-0">
                <v-container>
                    <v-navigation-drawer v-model="drawer" :mini-variant.sync="mini" permanent width="500"
                    >
                        <v-list-item>
                            <v-list-item-avatar>
                                <v-img src="https://randomuser.me/api/portraits/men/85.jpg"></v-img>
                            </v-list-item-avatar>

                            <v-list-item-title>Фильтры</v-list-item-title>

                            <v-btn icon @click.stop="mini = !mini">
                                <v-icon>mdi-chevron-left</v-icon>
                            </v-btn>
                        </v-list-item>

                        <v-divider></v-divider>

                        <v-list dense >
                            <v-list-item
                                    v-for="item in items"
                                    :key="item.title"
                                    link
                            >
                                <v-list-item-icon>
                                    <v-icon>{{ item.icon }}</v-icon>
                                </v-list-item-icon>

                                <v-list-item-content>
                                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </v-list>
                    </v-navigation-drawer>
                </v-container>
            </v-col>

            <v-col>
                <v-container fluid>
                    <v-sheet class="mx-auto mt-3">
                        <v-slide-group multiple show-arrows>
                            <v-slide-item v-for="n in 25" :key="n" v-slot:default="{ active, toggle }">
                                <v-btn class="mx-2" :input-value="active" active-class="purple white--text" depressed rounded @click="toggle">
                                    Options {{ n }}
                                </v-btn>
                            </v-slide-item>
                        </v-slide-group>
                    </v-sheet>
                    <v-row align="start" justify="space-around">
                        <product-card v-for="product in products"
                                      :key="product.productID"
                                      :product="product"
                                      :products="products">
                        </product-card>
                    </v-row>
                </v-container>
            </v-col>
        </v-row>
    </v-content>
</template>

<script>
    import axios from 'axios'
    import ProductCard from "components/ProductCard.vue";
    export default {
        components: {ProductCard},
        data() {
            return {
                products: [],
                drawer: false,
                items: [
                    { title: 'Home', icon: 'mdi-home-city' },
                    { title: 'My Account', icon: 'mdi-account' },
                    { title: 'Users', icon: 'mdi-account-group-outline' },
                ],
                mini: false,
            }
        },
        beforeCreate() {
            var url = '/api/products' + decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/'));
            axios.get(url).then(response => this.products = response.data)
        }
    }
</script>

<style scoped>

</style>