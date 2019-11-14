<template>
    <div>
        <v-progress-linear indeterminate color="#e52d00" v-if="loading"></v-progress-linear>

        <v-navigation-drawer app width="350" v-if="showFilters" :clipped="$vuetify.breakpoint.lgAndUp">

            <template v-slot:prepend>
                <v-container>
                    <v-row>
                        <v-col>
                            <span><strong>{{ (group.charAt(0).toUpperCase() + group.substr(1)).replace('_',' ') }}</strong></span>
                        </v-col>
                        <v-col cols="3">
                            <v-btn icon @click="hideFilters()">
                                <v-icon>mdi-chevron-left</v-icon>
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-container>
            </template>

            <v-divider></v-divider>

            <v-card-actions>
                <v-expansion-panels multiple>

                    <v-expansion-panel>
                        <v-expansion-panel-header >Цены</v-expansion-panel-header>
                        <v-expansion-panel-content>
                            <v-card-text>
                                <v-row>
                                    <v-col>
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

                    <v-expansion-panel>
                        <v-expansion-panel-header>Бренды</v-expansion-panel-header>
                        <v-expansion-panel-content>
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

                    <v-expansion-panel v-for="[key, val] of filtersDiapasons" :key="key" >
                        <v-expansion-panel-header v-if="val[1] !== undefined" :class="{'red': val[1] === undefined}">{{ key.charAt(0).toUpperCase() + key.substr(1) }}</v-expansion-panel-header>
                        <v-expansion-panel-content>
                            <v-row>
                                <v-col>
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

                    <v-expansion-panel v-for="[key, val] of filtersParams" :key="key">
                        <v-expansion-panel-header>{{ key.charAt(0).toUpperCase() + key.substr(1) }}</v-expansion-panel-header>
                        <v-expansion-panel-content>
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
            </v-card-actions>
        </v-navigation-drawer>




        <b-container fluid fill-height>

            <v-row class="p-3">
                <router-link to="/">
                    <v-btn depressed text small>Каталог</v-btn>
                </router-link>
                <router-link to="/">
                    <v-btn depressed text small>{{linkCategory}}</v-btn>
                </router-link>
                <v-btn depressed disabled text small>{{linkProductGroup}}</v-btn>
            </v-row>

            <v-row class="ml-3">
                <p v-model="totalProductsFound">Всего товаров: {{totalProductsFound}}</p>
            </v-row>

            <!--<v-row v-if="totalPages !== 1">
                <v-col cols="3">
                    <v-pagination color="#e52d00" v-model="page" :length="totalPages" :total-visible="6" @input="loadPage(page)"></v-pagination>
                </v-col>
            </v-row>-->


            <!--<v-slide-group multiple show-arrows>
                <v-slide-item v-for="feature in filtersFeats" :key="feature" v-slot:default="{ active, toggle }">
                    <v-btn class="mx-2" :input-value="active" active-class="orange white text" depressed rounded @click="filterProducts(feature)">
                        {{ feature }}
                    </v-btn>
                </v-slide-item>
            </v-slide-group>-->


            <v-slide-group multiple show-arrows>
                <v-slide-item v-for="feature in filtersFeats" :key="feature" v-slot:default="{ active, toggle }">
                    <v-btn class="mx-2" :input-value="active" active-class="orange text" depressed rounded @click="toggle" @mouseup="filterProducts(feature)">
                        {{ feature }}
                    </v-btn>
                </v-slide-item>
            </v-slide-group>


            <v-row>
                <product-card v-for="product in products" :key="product.productID" :product="product" :products="products"></product-card>
            </v-row>


            <!--<v-row v-if="totalPages !== 1">
                <v-col cols="3">
                    <v-pagination color="#e52d00" v-model="page" :length="totalPages" :total-visible="6" @input="loadPage(page)"></v-pagination>
                </v-col>
            </v-row>-->


            <!--<b-container>
                <v-row>
                    <v-col>
                        <router-link to="/">
                            <v-btn depressed text small>Каталог</v-btn>
                        </router-link>
                    </v-col>
                    <v-col>
                        <router-link to="/">
                            <v-btn depressed text small>{{linkCategory}}</v-btn>
                        </router-link>
                    </v-col>
                    <v-col>
                        <v-btn depressed disabled text small>{{linkProductGroup}}</v-btn>
                    </v-col>
                </v-row>

                <strong class="ml-3" v-model="totalProductsFound">Всего товаров: {{totalProductsFound}}</strong>

                <v-row class="mt-1 ml-1" v-if="totalPages !== 1">
                    <div class="text-center">
                        <v-pagination color="#e52d00" v-model="page" :length="totalPages" :total-visible="7" @input="loadPage(page)"></v-pagination>
                    </div>
                </v-row>

                <v-sheet class="mx-auto mt-2">
                    <v-slide-group multiple show-arrows>
                        <v-slide-item v-for="feature in filtersFeats" :key="feature" v-slot:default="{ active, toggle }">
                            <v-btn class="mx-2"  :input-value="active" active-class="purple white text" depressed rounded @click="toggle" @mousedown="filterProducts(feature)">
                                {{ feature }}
                            </v-btn>
                        </v-slide-item>
                    </v-slide-group>
                </v-sheet>

                <v-row>
                    <product-card v-for="product in products" :key="product.productID" :product="product" :products="products"></product-card>
                </v-row>

                <v-row>
                    <div class="text-center" v-if="totalPages !== 1">
                        <v-pagination color="#e52d00" v-model="page" :length="totalPages" :total-visible="7" @input="loadPage(page)"></v-pagination>
                    </div>
                </v-row>
            </b-container>-->
        </b-container>
    </div>
</template>

<script>
    import axios from 'axios'
    import ProductCard from "components/ProductCard.vue";
    export default {
        components: {ProductCard},
        data() {
            return {
                showFilters: true,
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
                linkCategory: '',
                linkProductGroup: '',
                requestGroup:'',
                productsRequest: '',
                filtersRequest: '',
                pageRequest: '',
                totalProductsFound: 0,

                scrollPage: 1,
                filtersScrollPage: 0,
                filters: {}
            }
        },
        created() {
            this.requestGroup    = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/'))).replace('_', ' ')
            this.pageRequest     = '/api/products/group' + this.requestGroup
            this.productsRequest = '/api/products/group' + this.requestGroup + '/0'
            this.filtersRequest  = '/api/products/build_filters' + this.requestGroup

            let linkGroup = this.requestGroup.replace('/', '')
            linkGroup = linkGroup.charAt(0).toUpperCase() + linkGroup.substr(1)
            this.linkProductGroup = linkGroup

            /*Load Filters*/
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

            /*Load Products*/
            axios.get(this.productsRequest).then(response => {
                this.products = response.data.content
                this.totalPages = response.data.totalPages
                this.totalProductsFound = response.data.totalElements
                this.linkCategory = this.products[0].productCategory
                this.loading = false
            })
        },
        mounted() {

            /*ДЛЯ БЕЗФИЛЬТРОВ И С ФИЛЬТРАМИ*/
            /*ПАГИНАЦИЯ ПОДГРУЗКОЙ*/

            window.onscroll = () =>{
                const el = document.documentElement
                const isBottomOfScreen = el.scrollTop + window.innerHeight === el.offsetHeight

                if(isBottomOfScreen) {
                    console.log('bottom')
                    console.log('/api/products/group'+this.requestGroup + '/' + this.scrollPage)

                    console.log(this.filters)

                    if (Object.keys(this.filters).length === 0) {
                        console.log('ПУСТО')
                        axios.get('/api/products/group'+this.requestGroup + '/' + this.scrollPage).then(response => {
                            console.log(response.data.content)
                            this.products = this.products.concat(response.data.content)
                            this.scrollPage+=1
                            console.log(this.products)
                        })
                    }
                    else {
                        /*если есть заполненнные фильтры, то отфильтровать и отправить на страницу 15 штук первых товаров*/
                        /*затем при прокрутке до дна прислать еще 15 товаров с этими же фильтрами */
                        console.log('ЕСТЬ ФИЛЬТРЫ')

                        this.filtersScrollPage+=1

                        const filterURL = '/api/products/filter' + this.requestGroup + '/' + this.filtersScrollPage
                        axios.post(filterURL, this.filters).then(response => {

                            this.products = this.products.concat(response.data.content)

                            //this.products = response.data.content
                            //this.totalPages = response.data.totalPages
                            //this.totalProductsFound = response.data.totalElements
                        })

                    }


                }
            }
        },
        beforeDestroy() {
            window.onscroll = null
            this.scrollPage = 1
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
                axios.get(pageRequest).then(response =>
                    this.products = response.data.content)
            },
            filterProducts(param) {
                //let filters = {}
                this.filtersScrollPage = 0

                if (param !== undefined)
                {
                    if (param.includes(':')) {
                        let key = param.substr(0, param.indexOf(':'));
                        let val = (param.substr(param.indexOf(':') + 1))
                        const valArray = val.split(',').map(Number);
                        this.selectedDiapasons[key] = val       /// to filter API
                        this.diapasonValues.set(key, valArray)  /// to input slider
                    }
                    else {
                        if (!this.selectedFeatures.includes(param)) this.selectedFeatures.push(param)
                        else this.selectedFeatures.splice(this.selectedFeatures.indexOf(param), 1);
                    }
                }

                this.filters['prices']   = this.priceRange
                this.filters['brands']   = this.selectedBrands
                this.filters['params']   = this.selectedParams
                this.filters['features'] = this.selectedFeatures

                let selectedDiapasons = []
                for (const [key, value] of Object.entries(this.selectedDiapasons)) selectedDiapasons.push(key+':'+value)
                this.filters['selectedDiapasons'] = selectedDiapasons

                console.log('/api/products/filter' + this.requestGroup)

                const filterURL = '/api/products/filter' + this.requestGroup + '/' + this.filtersScrollPage
                axios.post(filterURL, this.filters).then(response => {
                    this.products = response.data.content
                    this.totalPages = response.data.totalPages
                    this.totalProductsFound = response.data.totalElements
                })
            },
            hideFilters() {
                this.showFilters = false
                this.$store.dispatch('showFilters')
            }
        }
    }
</script>

<style scoped>
    .red {
        background-color: darkred;
    }
</style>
