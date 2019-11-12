<template>
    <v-container>
        <v-row>
            <v-col cols="6">
                <div class="mb-3">
                    <router-link to="/">
                        <v-btn depressed small>{{product.productCategory}}</v-btn>
                    </router-link>
                    <router-link :to="linkBack">
                        <v-btn depressed small>{{product.productGroup}}</v-btn>
                    </router-link>
                </div>
                <v-card max-width="600" max-height="500">
                    <v-list-item>
                        <v-list-item-content>
                            <v-list-item-title class="headline">{{ product.originalName }}</v-list-item-title>
                            <v-list-item-subtitle>{{ product.productGroup }}</v-list-item-subtitle>
                            <v-list-item-subtitle>{{ product.productType }}</v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                    <v-img :src="product.pic" contain max-height="300"></v-img>
                    <b-card>
                        <b-card-body>
                            <!--{{ product.annotation }}-->
                            <ul>
                                <li v-for="param in anno" :key="param">
                                    <div v-if="param !== ''">{{param}}</div>
                                </li>
                            </ul>
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
                        <v-btn class="goToOrderButton" block @click="toOrder()">
                            Перейти в корзину
                        </v-btn>
                    </v-card-actions>

                    <!--<v-card-actions>
                        <v-btn text color="deep-purple accent-4">В корзину</v-btn>
                        <v-btn text color="deep-purple accent-4">Купить сейчас</v-btn>
                        <div class="flex-grow-1"></div>
                    </v-card-actions>-->
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import axios from 'axios'
    export default {
        data() {
            return {
                product: '',
                linkBack: '',
                anno: []
            }
        },
        beforeCreate() {
            const productID = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/')+1));
            let url = '/api/products/show/' + productID;
            axios.get(url).then(response => {
                this.product = response.data
                this.anno =  (this.product.formattedAnnotation.split('<br>').map(String)).filter(Boolean)
                this.linkBack = '/products/'+ (this.product.productGroup).toLowerCase();

                console.log(this.product)
                console.log(this.anno)
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

<style scoped>

</style>