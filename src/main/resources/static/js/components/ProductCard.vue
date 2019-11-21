<template>
    <v-item v-slot:default="{ active, toggle }">
        <v-card outlined class="mx-auto mt-3" max-width="300">



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

            <!--<v-list>
                <v-list-item-group v-model="item" color="primary">
                    <v-list-item v-for="anno in annotations" v-if="anno" :key="anno">

                        <v-list-item-content>
                            <v-list-item-subtitle v-text="anno"></v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>

                &lt;!&ndash;<v-list-item link>
                    <v-list-item-content>
                        <v-list-item-title class="title">John Leider</v-list-item-title>
                        <v-list-item-subtitle>john@vuetifyjs.com</v-list-item-subtitle>
                    </v-list-item-content>
                </v-list-item>&ndash;&gt;
            </v-list>-->

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
                <v-btn class="goToOrderButton" @click="toOrder()">
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