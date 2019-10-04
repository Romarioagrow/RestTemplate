<template>
    <v-row>
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

                        <!--Автовывод фильтров-цен-->
                        <v-expansion-panel class="mt-2">
                            <v-expansion-panel-header >Цены</v-expansion-panel-header>
                            <v-expansion-panel-content>
                                <v-card-text>
                                    <v-row>
                                        <v-col class="px-4">
                                            <v-range-slider class="align-center" v-model="range" :min="min" :max="max"  hide-details>
                                                <template v-slot:prepend>
                                                    <v-text-field class="mt-0 pt-0" v-model="range[0]" hide-details single-line type="number" style="width: 60px"></v-text-field>
                                                </template>
                                                <template v-slot:append>
                                                    <v-text-field class="mt-0 pt-0" v-model="range[1]" hide-details single-line type="number" style="width: 60px"></v-text-field>
                                                </template>
                                            </v-range-slider>
                                        </v-col>
                                    </v-row>
                                </v-card-text>
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <!--Автовывод фильтров-брендов-->
                        <v-expansion-panel class="mt-2" >
                            <v-expansion-panel-header>Бренды</v-expansion-panel-header>
                            <v-expansion-panel-content class="ml-3">
                                <div v-for="brand in twoColsBrands" >
                                    <v-row>
                                        <v-col class="p-0 m-0">
                                            <v-checkbox v-model="selectedBrands" :label="brand.firstBrand" :value="brand.firstBrand" height="2"></v-checkbox>
                                        </v-col>
                                        <v-col class="p-0 m-0" v-if="brand.secondBrand !== undefined">
                                            <v-checkbox v-model="selectedBrands" :label="brand.secondBrand" :value="brand.secondBrand" height="2"></v-checkbox>
                                        </v-col>
                                    </v-row>
                                </div>
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <!--Автовывод фильтров-диапазонов-->
                        <v-expansion-panel class="mt-2" v-for="[key, val] of filtersDiapasons" :key="key" >
                            <v-expansion-panel-header>{{ key }}</v-expansion-panel-header>
                            <v-expansion-panel-content class="ml-3">
                                <v-row>
                                    <v-col class="px-4">
                                        <v-range-slider v-model="val" :min="val[0]" :max="val[1]"  hide-details class="align-center">
                                            <template v-slot:prepend>
                                                <v-text-field v-model="val[0]" class="mt-0 pt-0" hide-details single-line type="float" style="width: 60px"></v-text-field>
                                            </template>
                                            <template v-slot:append>
                                                <v-text-field v-model="val[1]" class="mt-0 pt-0" hide-details single-line type="float" style="width: 60px"></v-text-field>
                                            </template>
                                        </v-range-slider>
                                    </v-col>
                                </v-row>
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <!--Автовывод фильтров-параметров-->
                        <v-expansion-panel class="mt-2" v-for="[key, val] of filtersParams" :key="key" >
                            <v-expansion-panel-header>{{ key }}</v-expansion-panel-header>
                            <v-expansion-panel-content class="ml-3">
                                <div v-for="(param, i) in val" :key="i" :brand="param">
                                    <v-row>
                                        <v-col class="p-0 m-0">
                                            <v-checkbox v-model="selectedParams" :label="param" :value="param" height="2"></v-checkbox>
                                        </v-col>
                                    </v-row>
                                </div>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </v-container>
            </v-list-item>
        </v-navigation-drawer>
        <!---->
        <v-item-group multiple>
            <v-container fluid>
                <v-sheet class="mx-auto mt-2">
                    <v-slide-group multiple show-arrows>

                        <v-slide-item v-for="feature in filtersFeats" :key="feature" v-slot:default="{ active, toggle }">
                            <v-btn class="mx-2" :input-value="active" active-class="purple white text" depressed rounded @click="toggle">
                                {{ feature }}
                            </v-btn>
                        </v-slide-item>

                    </v-slide-group>
                </v-sheet>
                <!---->
                <v-row>
                    <product-card v-for="product in products" :key="product.productID" :product="product" :products="products"></product-card>
                </v-row>
            </v-container>
        </v-item-group>
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
                selectedBrands: [],
                selectedParams: [],
                products: [],
                filtersPrice: [],
                filtersBrands: [],
                filtersFeats: [],
                filtersDiapasons: new Map(),
                filtersParams: new Map(),
                drawer: true,
                mini: false,
                group: decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/')+1),
                min: '',
                max: '',
                range: [],
                selected: []
            }
        },
        beforeCreate() {
            const requestGroup = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/'))).replace('_', ' ');
            let url = '/api/products' + requestGroup;
            let filtersRequest = '/api/filters/construct' + requestGroup;

            axios.get(url).then(response => this.products = response.data);

            axios.get(filtersRequest).then(response =>
            {
                console.log(response.data); ///

                /// priceFilters()
                let prices = response.data.prices;
                this.min = prices[0];
                this.max = prices[1];
                this.range[0] = prices[0];
                this.range[1] = prices[1];

                /// brandsFilters()
                this.filtersBrands = response.data.brands;

                /// featuresFilters()
                this.filtersFeats = response.data.features;

                ///
                let diapasons = response.data.diapasonsFilters;
                for (const [key, value] of Object.entries(diapasons)) this.filtersDiapasons.set(key, value)

                ///
                let params = response.data.paramFilters;
                for (const [key, value] of Object.entries(params)) this.filtersParams.set(key, value)
            });
        },
        computed: {
            twoColsBrands() {
                const twoColsBrands = []
                for (let i = 0; i < this.filtersBrands.length; i+=2) {
                    let first = this.filtersBrands[i];
                    let second = this.filtersBrands[i+1];
                    twoColsBrands.push({
                        firstBrand: first, secondBrand: second
                    })
                }
                return twoColsBrands
            }
        }
    }
</script>

<style scoped></style>
