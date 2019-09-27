<template>
    <v-row>
        <!--<v-navigation-drawer v-model="drawer" :mini-variant.sync="mini" permanent width="500"
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
        </v-navigation-drawer>-->


        <v-navigation-drawer class="mt-5" v-show="drawer" v-model="drawer" :mini-variant.sync="mini" width="500">
            <v-list-item>
                <v-btn icon @click.stop="mini = !mini">
                    <v-icon>mdi-shopping</v-icon>
                </v-btn>
                <v-container>
                    <v-row>
                        <v-col cols="10">
                            <div>Фильтры для {{group}}</div>
                        </v-col>
                        <v-col>
                            <v-icon>mdi-chevron-left</v-icon>
                        </v-col>
                    </v-row>
                    <v-expansion-panels multiple>
                        <v-expansion-panel class="mt-2">
                            <v-expansion-panel-header >Цены</v-expansion-panel-header>
                            <v-expansion-panel-content>
                                <v-card-text>
                                    <v-row>
                                        <v-col class="px-4">
                                            <v-range-slider class="align-center"
                                                    v-model="range"
                                                    :max="max"
                                                    :min="min"
                                                    hide-details
                                            >
                                                <template v-slot:prepend>
                                                    <v-text-field
                                                            v-model="range[0]"
                                                            class="mt-0 pt-0"
                                                            hide-details
                                                            single-line
                                                            type="number"
                                                            style="width: 60px"
                                                    ></v-text-field>
                                                </template>
                                                <template v-slot:append>
                                                    <v-text-field
                                                            v-model="range[1]"
                                                            class="mt-0 pt-0"
                                                            hide-details
                                                            single-line
                                                            type="number"
                                                            style="width: 60px"
                                                    ></v-text-field>
                                                </template>
                                            </v-range-slider>
                                        </v-col>
                                    </v-row>
                                </v-card-text>
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <v-expansion-panel class="mt-2" >
                            <v-expansion-panel-header>Бренды</v-expansion-panel-header>
                            <v-expansion-panel-content>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <!--<v-expansion-panel class="mt-2" v-for="(item,i) in 5" :key="i" >
                            <v-expansion-panel-header>Item</v-expansion-panel-header>
                            <v-expansion-panel-content>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                            </v-expansion-panel-content>
                        </v-expansion-panel>-->
                    </v-expansion-panels>
                </v-container>
            </v-list-item>
        </v-navigation-drawer>
        <v-container fluid>
            <v-sheet class="mx-auto mt-2">
                <v-slide-group multiple show-arrows>
                    <v-slide-item v-for="n in 25" :key="n" v-slot:default="{ active, toggle }">
                        <v-btn class="mx-2" :input-value="active" active-class="purple white text" depressed rounded @click="toggle">
                            Options {{ n }}
                        </v-btn>
                    </v-slide-item>
                </v-slide-group>
            </v-sheet>
            <v-row>
                <product-card v-for="product in products"
                              :key="product.productID"
                              :product="product"
                              :products="products">
                </product-card>
            </v-row>
        </v-container>
    </v-row>
</template>

<script>
    import axios from 'axios'
    import ProductCard from "components/ProductCard.vue";
    export default {
        components: {ProductCard},
        methods: {
        },
        data() {
            return {
                products: [],
                filtersPrice: [],
                filtersBrands: [],
                filtersFeat: [],
                filtersCompute: [],
                filtersParams: [],
                drawer: true,
                /*items: [
                    { title: 'Home', icon: 'mdi-home-city' },
                    { title: 'My Account', icon: 'mdi-account' },
                    { title: 'Users', icon: 'mdi-account-group-outline' },
                ],*/
                mini: false,
                group: decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/')+1),
                min: '',
                max: '',
                slider: 100,
                range: [],
                //panel: [0]
            }
        },
        beforeCreate() {
            var requestGroup = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/'))).replace('_', ' ');
            var url = '/api/products' + requestGroup;
            let filtersRequest = '/api/filters/construct' + requestGroup;

            console.log(url)
            console.log(filtersRequest)

            axios.get(url).then(response => this.products = response.data);
            axios.get(filtersRequest).then(response => {
                console.log(response.data.prices);
                let prices = response.data.prices;
                this.min = prices[0];
                this.max = prices[1];
                this.range[0] = prices[0];
                this.range[1] = prices[1];
            });
        }
    }
</script>

<style scoped></style>

<!--
ФУНКЦИОНАЛ СТРАНИЦЫ PRODUCTS
1. Скрыть\развернуть сайд бар с фильтрами по нажатию кнопки
2. В скрытом состоянии сайдбара отображать мини.бар
3. При скрытии сайдбара разширить контейнер с products на всю ширину, при развертке сайдбара - сузить
4. Алгоритм наполнения фильтров:
    4.1: Фильтры мин/макс цены
    4.2: Фильтры с множественными парамтерами по признаку/типу
    4.3: Фильтры с вычисляемыми числовыми значениями
    4.4: Фильтры особенности
    4.5: Фильтры брендов

    АЛГОРИТМ
    1) наполнения списка products по группе

-->