<template>
    <div>
        <v-container>
            <v-row>
                <v-col cols="6">

                    <div class="mb-3">
                        <router-link :to="'/catalog/'+product.productCategory">
                            <v-btn depressed small>{{product.productCategory}}</v-btn>
                        </router-link>
                        <router-link :to="linkBack">
                            <v-btn depressed small>{{product.productGroup}}</v-btn>
                        </router-link>
                    </div>

                    <v-card max-width="600">

                        <v-list-item>
                            <v-list-item-content>
                                <v-list-item-title class="headline">{{ product.originalName }}</v-list-item-title>
                                <v-list-item-subtitle>{{ product.productGroup }}</v-list-item-subtitle>
                                <v-list-item-subtitle>{{ product.productType }}</v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>

                        <a @mouseover="this.style.cursor='pointer'">
                            <v-img class="white--text" contain max-height="300" :src="product.pic" alt="Bad Link" @click.stop="picDialog = true"></v-img>
                        </a>

                        <v-dialog v-model="picDialog" max-width="80%">
                            <v-card>
                                <v-img class="white--text" height="1000" contain :src="product.pic" alt="Bad Link" @click.stop="picDialog = true"></v-img>
                            </v-card>
                        </v-dialog>

                        <b-card>
                            <b-card-body>
                                <v-row v-for="(value, param) of formattedAnno" :key="param" style="margin-bottom: -1rem;">
                                    <v-col>
                                        {{param}}
                                        <hr style="width: 220%;">
                                    </v-col>
                                    <v-col align="right">
                                        {{value}}
                                    </v-col>
                                </v-row>
                            </b-card-body>
                        </b-card>
                    </v-card>
                </v-col>
                <v-col class="mt-10">
                    <v-card >
                        <v-row>
                            <v-col cols="4">
                                <v-card-title>{{ product.finalPrice.toLocaleString('ru-RU') }} ₽</v-card-title>
                            </v-col>
                            <v-col class="mr-5">
                                <v-card-text>За покупку будет зачисленно <strong>{{ product.bonus }} </strong> баллов!</v-card-text>
                            </v-col>
                        </v-row>

                        <v-card-actions v-if="!productInOrder(product.productID)">
                            <v-btn text outlined block color="#e52d00" @click="addToOrder(product.productID)">
                                В корзину
                            </v-btn>
                        </v-card-actions>
                        <v-card-actions v-else>
                            <v-btn class="goToOrderButton" block @click="toOrder()" style="background-color: #e52d00; color: #ffffff">
                                Перейти в корзину
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </div>
</template>

<script>
    import axios from 'axios'
    export default {
        data() {
            return {
                product: '',
                linkBack: '',
                anno: [],
                picDialog: false,
                formattedAnno:{}
            }
        },
        beforeCreate() {
            const productID = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/')+1));
            let url = '/api/products/show/' + productID;
            axios.get(url).then(response => {
                this.product = response.data
                this.anno =  (this.product.shortAnnotation.split(';').map(String)).filter(Boolean)
                this.linkBack = '/products/'+ (this.product.productGroup).toLowerCase();

                this.anno.forEach(value => {
                    if (value.includes(':')) {
                        let key = value.substr(0, value.indexOf(':'))
                        let val = value.substr(value.indexOf(':')+1).trim()
                        this.formattedAnno[key] = val
                    }
                    else {
                        this.formattedAnno[value] = ''
                    }
                })
            });
        },
        methods: {
            addToOrder(productID) {
                const url = '/api/order/addProduct'
                axios.post(url, productID).then(response => {
                    this.$store.dispatch('addOrderedProduct', productID)
                    this.$store.dispatch('updateOrder', response.data)
                })
            },
            productInOrder(productID) {
                return this.$store.state.orderedProducts.includes(productID)
            },
            toOrder() {
                this.$router.push('/order')
            }
        }
    }
</script>

<style scoped></style>