<template>
    <div>
        <v-progress-linear indeterminate color="#e52d00" v-if="loading"></v-progress-linear>
        <v-row v-if="!loading">
            <v-navigation-drawer class="mt-5" v-show="drawer" v-model="drawer" :mini-variant.sync="mini" width="500">
                <v-list-item>
                    <v-btn icon @click.stop="mini = !mini">
                        <v-icon>mdi-shopping</v-icon>
                    </v-btn>
                    <v-container>
                        <v-row>
                            <v-col cols="10">
                                <span><strong>{{ (group.charAt(0).toUpperCase() + group.substr(1)).replace('_',' ') }}</strong></span>
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
                                                <v-range-slider class="align-center" v-model="priceRange" :min="min" :max="max" hide-details @end="filterProducts()">
                                                    <template v-slot:prepend>
                                                        <v-text-field class="" @input="filterProducts()" v-model="priceRange[0]" hide-details single-line type="number" ></v-text-field>
                                                    </template>
                                                    <template v-slot:append>
                                                        <v-text-field class="" @input="filterProducts()" v-model="priceRange[1]" hide-details single-line type="number" ></v-text-field>
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
                                                <v-checkbox @change="filterProducts()" v-model="selectedBrands" :label="brand.firstBrand" :value="brand.firstBrand" height="2"></v-checkbox>
                                            </v-col>
                                            <v-col class="p-0 m-0" v-if="brand.secondBrand !== undefined">
                                                <v-checkbox @change="filterProducts()" v-model="selectedBrands" :label="brand.secondBrand" :value="brand.secondBrand" height="2"></v-checkbox>
                                            </v-col>
                                        </v-row>
                                    </div>
                                </v-expansion-panel-content>
                            </v-expansion-panel>

                            <!--Автовывод фильтров-диапазонов-->
                            <v-expansion-panel class="mt-2" v-for="[key, val] of filtersDiapasons" :key="key" >
                                <v-expansion-panel-header :class="{'red': val[1] === undefined}">{{ key.charAt(0).toUpperCase() + key.substr(1) }}</v-expansion-panel-header>
                                <v-expansion-panel-content class="ml-3">
                                    <v-row>
                                        <v-col class="px-4">
                                            <v-range-slider v-model="val" :min="val[0]" :max="val[1]" hide-details class="align-center" @end="filterProducts(key +':'+ val)">
                                                <template v-slot:prepend>
                                                    <v-text-field @input="filterProducts(key +':'+ diapasonValues.get(key))" v-model="diapasonValues.get(key)[0]" class="mt-0 pt-0" hide-details single-line type="float" style="width: 60px"></v-text-field>
                                                </template>
                                                <template v-slot:append>
                                                    <v-text-field @input="filterProducts(key +':'+ diapasonValues.get(key))" v-model="diapasonValues.get(key)[1]" class="mt-0 pt-0" hide-details single-line type="float" style="width: 60px"></v-text-field>
                                                </template>
                                            </v-range-slider>
                                        </v-col>
                                    </v-row>
                                </v-expansion-panel-content>
                            </v-expansion-panel>

                            <!--Автовывод фильтров-параметров-->
                            <v-expansion-panel class="mt-2" v-for="[key, val] of filtersParams" :key="key">
                                <v-expansion-panel-header>{{ key.charAt(0).toUpperCase() + key.substr(1) }}</v-expansion-panel-header>
                                <v-expansion-panel-content class="ml-3">
                                    <div v-for="(param, i) in val" :key="i" :brand="param">
                                        <v-row>
                                            <v-col class="p-0 m-0">
                                                <v-checkbox @change="filterProducts()" v-model="selectedParams" :label="param" :value="key +': '+param" height="2"></v-checkbox>
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
                    <v-breadcrumbs :items="items" large></v-breadcrumbs>
                    <!---->
                    <strong class="ml-3" v-model="totalProductsFound">Всего товаров: {{totalProductsFound}}</strong>
                    <v-row class="mt-1 ml-1" v-if="totalPages !== 1">
                        <div class="text-center">
                            <v-pagination color="#e52d00" v-model="page" :length="totalPages" :total-visible="7" @input="loadPage(page)"></v-pagination>
                        </div>
                    </v-row>
                    <!---->
                    <v-sheet class="mx-auto mt-2">
                        <v-slide-group multiple show-arrows>
                            <v-slide-item v-for="feature in filtersFeats" :key="feature" v-slot:default="{ active, toggle }">
                                <v-btn class="mx-2"  :input-value="active" active-class="purple white text" depressed rounded @click="toggle" @mousedown="filterProducts(feature)">
                                    {{ feature }}
                                </v-btn>
                            </v-slide-item>
                        </v-slide-group>
                    </v-sheet>

                    <!--<v-img src="D:\Projects\Rest\src\main\resources\static\pics\logo.png"></v-img>-->
                    <v-row>
                        <product-card v-for="product in products" :key="product.productID" :product="product" :products="products"></product-card>
                    </v-row>
                    <v-row>
                        <div class="text-center" v-if="totalPages !== 1">
                            <v-pagination color="#e52d00" v-model="page" :length="totalPages" :total-visible="7" @input="loadPage(page)"></v-pagination>
                        </div>
                    </v-row>
                </v-container>
            </v-item-group>
        </v-row>
    </div>
</template>

<script>
    import axios from 'axios'
    import ProductCard from "components/ProductCard.vue";
    export default {
        components: {ProductCard},
        data() {
            return {
                loading: true,
                products: [],

                selectedBrands: [],
                selectedParams: [],
                selectedDiapasons: {},
                selectedFeatures: [],

                filtersPrice: [],
                filtersBrands: [],
                filtersFeats: [],

                filtersDiapasons: new Map(),
                diapasonValues: new Map(),
                filtersParams: new Map(),

                drawer: true,
                mini: false,
                group: decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/') + 1),
                min: '',
                max: '',
                priceRange: [],
                selected: [],
                page: 1,
                totalPages: 0,
                items: [
                    {
                        text: 'Catalog',
                        disabled: false,
                        href: '/',
                    },
                    {
                        text: 'Category',
                        disabled: false,
                        href: '#breadcrumbs_link_1',
                    },
                    {
                        text: 'Group',
                        disabled: true,
                        href: '#breadcrumbs_link_2',
                    },
                ],
                requestGroup:'',
                productsRequest: '',
                filtersRequest: '',
                pageRequest: '',
                totalProductsFound: 0
            }
        },
        created() {
            this.requestGroup    = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/'))).replace('_', ' ')
            this.pageRequest     = '/api/products/group' + this.requestGroup
            this.productsRequest = '/api/products/group' + this.requestGroup + '/0'
            this.filtersRequest  = '/api/products/build_filters' + this.requestGroup

            //console.log(this.filtersRequest)
            //console.log(this.productsRequest)
            //console.log(this.pageRequest)

            /*loadFilters*/
            axios.get(this.filtersRequest).then(response => {
                let prices = response.data.prices
                this.min = prices[0]
                this.max = prices[1]
                this.priceRange[0] = prices[0]
                this.priceRange[1] = prices[1]

                this.filtersBrands = response.data.brands
                this.filtersFeats  = response.data.features

                let diapasons = response.data.diapasonsFilters
                for (let [key, value] of Object.entries(diapasons)) {
                    this.filtersDiapasons.set(key, value.slice(","))
                    this.diapasonValues.set(key, value.slice(","))
                }

                let params = response.data.paramFilters
                for (const [key, value] of Object.entries(params)) this.filtersParams.set(key, value)
            });

            /*loadProducts*/
            axios.get(this.productsRequest).then(response => {
                this.products = response.data.content
                this.totalPages = response.data.totalPages
                this.totalProductsFound = response.data.totalElements
                this.loading = false
            })
        },
        computed: {
            twoColsBrands() {
                const twoColsBrands = []
                for (let i = 0; i < this.filtersBrands.length; i+=2) {
                    let first = this.filtersBrands[i]
                    let second = this.filtersBrands[i+1]
                    twoColsBrands.push({
                        firstBrand: first, secondBrand: second
                    })
                }
                return twoColsBrands
            },
        },
        methods: {
            loadPage(page) {
                let pageRequest = this.pageRequest + '/' + page
                //console.log(pageRequest)
                axios.get(pageRequest).then(response => this.products = response.data.content)
            },
            filterProducts(param) {
                let filters = {}

                if (param !== undefined)
                {
                    if (param.includes(':')) {
                        let key = param.substr(0, param.indexOf(':'));

                        let val = (param.substr(param.indexOf(':') + 1))
                        const valArray = val.split(',').map(Number);

                        /*to filter API*/
                        this.selectedDiapasons[key] = val
                        /*to input slider*/
                        this.diapasonValues.set(key, valArray)
                    }
                    else {
                        if (!this.selectedFeatures.includes(param)) this.selectedFeatures.push(param)
                        else this.selectedFeatures.splice(this.selectedFeatures.indexOf(param), 1);
                    }
                }

                filters['prices']   = this.priceRange
                filters['brands']   = this.selectedBrands
                filters['params']   = this.selectedParams
                filters['features'] = this.selectedFeatures

                let selectedDiapasons = []
                for (const [key, value] of Object.entries(this.selectedDiapasons)) selectedDiapasons.push(key+':'+value)
                filters['selectedDiapasons'] = selectedDiapasons

                axios.post('/api/products/filter' + this.requestGroup, filters).then(response => {
                    this.products = response.data.content
                    this.totalPages = response.data.totalPages
                    this.totalProductsFound = response.data.totalElements
                })
            }
        }
    }
</script>

<style scoped>
    .red {
        background-color: darkred;
    }
</style>
