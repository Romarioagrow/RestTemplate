<template>
    <v-item v-slot:default="{ active, toggle }">
        <v-card outlined class="mx-2 mt-3" max-width="300">

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


            <v-card-text>
                <router-link :to=showProductInfo>
                    <div class="align-center fill-height headline">{{product.fullName}}</div>
                </router-link>
            </v-card-text>

            <v-card-text>
                <ul>
                    <li v-for="anno in annotations" v-if="anno">{{anno}}</li>
                </ul>

                <h3>{{product.finalPrice.toLocaleString('ru-RU')}} ₽</h3>
                <!--<span>{{product.supplier}}</span>-->
            </v-card-text>

            <v-card-actions v-if="!productInOrder(product.productID)">
                <v-btn text outlined color="#e52d00" @click="addToOrder(product.productID)">
                    В корзину
                </v-btn>
            </v-card-actions>
            <v-card-actions v-else>
                <v-btn style="background-color: #e52d00; color: #ffffff" @click="toOrder()">
                    Перейти в корзину
                </v-btn>
            </v-card-actions>
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