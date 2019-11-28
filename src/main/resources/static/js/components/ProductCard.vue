<template>
    <v-item v-slot:default="{ active, toggle }">
        <v-card outlined  class="mx-2 mt-3" width="300">

            <div class="p-3">
                <a @mouseover="this.style.cursor='pointer'">
                    <v-img class="white--text" contain height="150px" :src="product.pic" alt="Bad Link" @click.stop="picDialog = true"></v-img>
                </a>
                <v-dialog v-model="picDialog" max-width="80%">
                    <v-card>
                        <v-img class="white--text" height="1000" contain :src="product.pic" alt="Bad Link" @click.stop="picDialog = true"></v-img>
                    </v-card>
                </v-dialog>
            </div>

            <b-card-sub-title class="ml-5 mt-1">
                {{product.singleTypeName}}
            </b-card-sub-title>

            <v-card-text >
                <router-link :to=showProductInfo>
                    <div class="align-top fill-height headline">{{product.brand}} {{product.modelName}}</div>
                </router-link>
            </v-card-text>

            <div>
                <v-card-text >
                    <ul class="pl-1">
                        <li v-for="anno in annotations" v-if="anno" style="list-style-type: none;" >
                            <span class="font-weight-light">{{anno}}</span>
                        </li>
                    </ul>
                </v-card-text>
            </div>

            <div class="d-flex align-baseline">
                <v-card-text style="padding-top: 25px;">
                    <h5>{{product.finalPrice.toLocaleString('ru-RU')}}₽</h5>
                </v-card-text>

                <v-card-actions style="align-items: end; padding-right: 15px;">
                    <v-btn text outlined color="#e52d00" v-if="!productInOrder(product.productID)" @click="addToOrder(product.productID)">
                        В корзину
                    </v-btn>
                    <v-btn style="background-color: #e52d00; color: #ffffff" v-else @click="toOrder()">
                        Перейти в корзину
                    </v-btn>
                </v-card-actions>
            </div>
        </v-card>
    </v-item>
</template>

<script>
    import axios from 'axios'
    export default {
        props: ['product'],
        data() {
            return {
                showProductInfo: '/products/product/' + this.product.productID,
                dialog: false,
                annotations: (this.product.shortAnnotation.split(';').map(String)).slice(0,8),
                picDialog: false
            }
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

<style scoped>
    .goToOrderButton {
        background-color: #e52d00;
        color: #fafafa;
    }
</style>

<!--
<v-card outlined  class="mx-2 mt-3" width="300">

            <div class="p-3">
                <a @mouseover="this.style.cursor='pointer'">
                    <v-img class="white--text" contain height="150px" :src="product.pic" alt="Bad Link" @click.stop="picDialog = true"></v-img>
                </a>
                <v-dialog v-model="picDialog" max-width="80%">
                    <v-card>
                        <v-img class="white--text" height="1000" contain :src="product.pic" alt="Bad Link" @click.stop="picDialog = true"></v-img>
                    </v-card>
                </v-dialog>
            </div>

            <b-card-sub-title class="ml-5 mt-1">
                {{product.singleTypeName}}
            </b-card-sub-title>

            <v-card-text >
                <router-link :to=showProductInfo>
                    <div class="align-top fill-height headline">{{product.brand}} {{product.modelName}}</div>
                </router-link>
            </v-card-text>

            <div>
                <v-card-text >
                    <ul class="pl-1">
                        <li v-for="anno in annotations" v-if="anno" style="list-style-type: none;" >
                            <span class="font-weight-light">{{anno}}</span>
                        </li>
                    </ul>
                </v-card-text>
            </div>

            <div class="d-flex align-baseline">
                <v-card-text style="padding-top: 25px;">
                    <h5>{{product.finalPrice.toLocaleString('ru-RU')}} ₽</h5>
                </v-card-text>
                <v-card-actions style="align-items: end; padding-right: 15px;">
                    <v-btn text outlined color="#e52d00" v-if="!productInOrder(product.productID)" @click="addToOrder(product.productID)">
                        В корзину
                    </v-btn>
                    <v-btn style="background-color: #e52d00; color: #ffffff" v-else @click="toOrder()">
                        Перейти в корзину
                    </v-btn>
                </v-card-actions>
            </div>
        </v-card>
        -->