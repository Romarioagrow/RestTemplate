<template>
    <v-item v-slot:default="{ active, toggle }">
    <v-card class="mx-auto mt-3" max-width="350">
        <v-img class="white--text" contain height="200px" :src="product.pic" alt="Bad Link"></v-img>
        <v-card-text>
            <router-link :to=showProductInfo>
                <v-card-title class="align-end fill-height">{{product.fullName}}</v-card-title>
            </router-link>
            <ul>
                <li v-for="anno in annotations" v-if="anno">{{anno}}</li>
            </ul>
            <h3>{{product.finalPrice.toLocaleString('ru-RU')}} ₽</h3>
            <span>{{product.supplier}}</span>
        </v-card-text>
        <v-card-actions v-if="!productInOrder(product.productID)">
            <v-btn text outlined color="#e52d00" @click="addToOrder(product.productID)">
                В корзину
            </v-btn>
                <!--<v-dialog v-model="dialog" max-width="500">
                    <template v-slot:activator="{ on }">
                        <v-btn text color="orange" v-on="on">Купить в 1 клик</v-btn>
                    </template>
                    <v-card>
                        <v-card-title class="headline">Оформить заказ</v-card-title>
                        <v-card-text>Цена: {{product.finalPrice}} ₽</v-card-text>
                        <v-card-actions>
                            <div class="flex-grow-1"></div>
                            <v-btn color="green darken-1" text @click="dialog = false">Оформить</v-btn>
                            <v-btn color="green darken-1" text @click="dialog = false">Отмена</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>-->
        </v-card-actions>
        <v-card-actions v-else>
            <v-btn text color="orange" @click="toOrder()">
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
                annotations: this.product.shortAnnotation.split(';').map(String),
            }
        },
        methods: {
            addToOrder(productID) {
                console.log(productID)
                const url = '/api/order/addProduct'

                axios.post(url, productID).then(response => {
                    console.log(response.data)
                    this.$store.dispatch('addOrderedProduct', productID)
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

</style>