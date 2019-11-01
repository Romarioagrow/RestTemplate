<template>
    <div>
        <v-progress-linear indeterminate color="#e52d00" v-if="loading"></v-progress-linear>
        <b-container fluid style="width: 90%;" v-else>
            <v-row>
                <v-col cols="8">
                    <v-card outlined>
                        <v-list subheader>
                            <v-subheader>Товары в корзине</v-subheader>
                            <v-list-item v-for="[product, amount] of orderedProducts" :key="product.productID" @click="" style="min-height: 5rem;">
                                <v-list-item-avatar>
                                    <v-img :src="product.pic"></v-img>
                                </v-list-item-avatar>
                                <v-list-item-content class="ml-12">
                                    <v-list-item-title v-text="product.fullName"></v-list-item-title>
                                </v-list-item-content>
                                <v-list-item-content class="ml-12">
                                    <v-list-item-title>
                                        <span><strong>{{product.finalPrice}}</strong> ₽</span>
                                    </v-list-item-title>
                                </v-list-item-content>
                                <v-list-item-action class="ml-12">
                                    <div class="my-2">
                                        <v-btn text small icon @click="decreaseAmount(product.productID)" :disabled="amount === 1">
                                            <v-icon>mdi-minus</v-icon>
                                        </v-btn>
                                        <span>{{amount}}</span>
                                        <v-btn text small icon @click="increaseAmount(product.productID)">
                                            <v-icon>mdi-plus</v-icon>
                                        </v-btn>
                                    </div>
                                </v-list-item-action>
                                <v-list-item-icon>
                                    <v-btn icon @click="deleteProduct(product.productID)">
                                        <v-icon>mdi-close-circle</v-icon>
                                    </v-btn>
                                </v-list-item-icon>
                            </v-list-item>
                        </v-list>
                    </v-card>
                </v-col>
                <v-col>
                    <v-card outlined>
                        <v-card-text class="pb-0">
                            <p class="display-1">Сумма заказа <span class="text--primary">{{totalPrice.toLocaleString('ru-RU')}} ₽</span></p>
                        </v-card-text>
                        <v-card-text class="pt-0">
                            Без скидки
                        </v-card-text>
                        <v-card-text>
                            <div class="cp text--primary">
                                Бонусов за заказ: <span class="cp"><strong>{{totalBonus}}</strong></span>
                            </div>
                        </v-card-text>
                        <v-card-actions>

                            <!--В DIALOG!!!-->
                            <router-link to="/login"  v-if="!auth">
                                <v-btn class="cp" tile outlined color="success">
                                    <v-icon left>mdi-login-variant</v-icon>
                                    Войдите, что бы получить скидку!
                                </v-btn>
                            </router-link>

                            <v-btn class="cp" tile outlined color="primary" v-else>
                                <v-icon left>mdi-sale</v-icon>
                                Применить скидку
                            </v-btn>
                        </v-card-actions>
                        <v-divider></v-divider>
                        <div>
                            <v-row>
                                <v-col cols="5">
                                    <v-card-text>
                                        Всего наименований
                                    </v-card-text>
                                </v-col>
                                <v-col>
                                    <v-badge left>
                                        <template v-slot:badge>
                                            <span>{{productAmount}}</span>
                                        </template>
                                    </v-badge>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="5">
                                    <v-card-text>
                                        Количество едениц
                                    </v-card-text>
                                </v-col>
                                <v-col>
                                    <v-badge left>
                                        <template v-slot:badge>
                                            <span>{{itemAmount}}</span>
                                        </template>
                                    </v-badge>
                                </v-col>
                            </v-row>
                        </div>
                        <v-divider></v-divider>
                        <v-card-actions>
                            <v-dialog v-model="orderDialog" persistent max-width="1000">
                                <template v-slot:activator="{ on }">
                                    <v-btn block color="#e52d00" dark v-on="on">Оформить заказ</v-btn>
                                </template>
                                <v-card>
                                    <v-card-title class="headline"><span v-if="auth">{{firstName}},</span><span v-else class="mr-1">Ваш </span> заказ на сумму <span style="color: #e52d00" class="ml-2">{{totalPrice.toLocaleString('ru-RU')}} ₽ </span></v-card-title>
                                    <v-card-text>
                                        <div class="mt-3" >
                                            <h5>Ваши контактные данные</h5>
                                            <v-row>
                                                <v-col>
                                                    <v-text-field prepend-icon="mdi-account" type="text" v-model="lastName" label="Фамилия"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field type="text" v-model="firstName" label="Имя"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field type="text" v-model="patronymic" label="Отчество"></v-text-field>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col>
                                                    <v-text-field type="text" prepend-icon="mdi-phone" v-mask="'7-###-###-##-##'" v-model="mobile" label="Номер телефона"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field type="email" prepend-icon="mdi-email" v-model="email" label="E-mail"></v-text-field>
                                                </v-col>
                                            </v-row>
                                        </div>
                                        <div class="mt-3">
                                            <h5>Способ получения товара</h5>
                                            <v-tabs>
                                                <v-tab @click="noDelivery = true">Самовывоз</v-tab>
                                                <v-tab @click="noDelivery = false">Доставка</v-tab>
                                            </v-tabs>
                                            <div class="mt-3" v-if="noDelivery">
                                                <span>Заберите ваш заказ с магазина по адресу: <strong>город Чебаркуль, Карпенко 7</strong></span>
                                            </div>
                                            <div class="mt-3" v-else>
                                                <v-row>
                                                    <v-col>
                                                        <v-text-field type="text" v-model="city" label="Населенный пункт"></v-text-field>
                                                    </v-col>
                                                    <v-col>
                                                        <v-text-field type="text" v-model="street" label="Улица"></v-text-field>
                                                    </v-col>
                                                    <v-col>
                                                        <v-text-field type="text" v-model="house" label="Дом"></v-text-field>
                                                    </v-col>
                                                    <v-col>
                                                        <v-text-field type="text" v-model="flat" label="Квартира"></v-text-field>
                                                    </v-col>
                                                </v-row>
                                            </div>
                                        </div>
                                    </v-card-text>
                                    <v-card-actions class="chartAreaWrapper">
                                        <v-row>
                                            <v-col>
                                                <v-btn color="green" block dark @click="acceptOrder()">
                                                    Оформить
                                                    <v-icon dark right>mdi-checkbox-marked-circle</v-icon>
                                                </v-btn>
                                            </v-col>
                                            <v-col>
                                                <v-btn color="#e10c0c" block dark @click="orderDialog = false">
                                                    Отмена
                                                    <v-icon dark right>mdi-cancel</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </b-container>
    </div>
</template>

<script>
    import axios from 'axios'
    export default {
        data: () => ({
            orderedProducts : {},
            totalPrice      : 0,
            totalBonus      : 0,
            productAmount   : 0,
            itemAmount      : 0,
            loading         : true,
            orderDialog     : false,
            noDelivery      : true,
            lastName        : '',
            firstName       : '',
            patronymic      : '',
            mobile          : '',
            email           : '',
            city            :'',
            street          :'',
            house           :'',
            flat            :'',
        }),
        methods: {
            loadData(response) {
                const order = response.data[0]
                const productList = response.data[1]
                this.$store.dispatch('updateOrder', order)

                let orderedProducts = new Map()
                let itemAmount = 0
                for (const [product, amount] of Object.entries(productList)) {
                    const obj = JSON.parse(product);
                    orderedProducts.set(obj, amount)
                    itemAmount += amount
                }
                this.itemAmount = itemAmount
                this.productAmount = orderedProducts.size
                this.orderedProducts = orderedProducts
                this.totalPrice = order.totalPrice
                this.totalBonus = order.totalBonus
                this.loading = false
            },
            deleteProduct(productID) {
                axios.post('/api/order/deleteProduct', productID).then(response => {
                    this.loadData(response)
                })
            },
            increaseAmount(productID) {
                axios.post('/api/order/increaseAmount', productID).then(response => {
                    this.loadData(response)
                })
            },
            decreaseAmount(productID) {
                axios.post('/api/order/decreaseAmount', productID).then(response => {
                    this.loadData(response)
                })
            },
            acceptOrder() {
                this.orderDialog = false

                let address = ''
                if (this.city) {
                    address = this.city + ',' + this.street  + ',' + this.house  + ',' + this.flat
                }

                let orderConfirmDetails = {
                    'orderID'   :this.order.orderID,
                    'firstName' :this.firstName,
                    'lastName'  :this.lastName,
                    'patronymic':this.patronymic,
                    'mobile'    :this.mobile,
                    'email'     :this.email,
                    'address'   :address
                }
                console.log(orderConfirmDetails)

                axios.post('/api/order/acceptOrder', orderConfirmDetails).then(response => {
                    console.log(response)
                })
            }
        },
        created() {
            axios.post('/api/order/orderedProducts').then(response => {
                this.loadData(response)
            })

            if (this.auth) {
                this.lastName = this.$store.state.currentUser.lastName
                this.firstName = this.$store.state.currentUser.firstName
                this.patronymic = this.$store.state.currentUser.patronymic
                this.mobile = this.$store.state.currentUser.username
                this.email = this.$store.state.currentUser.email
            }
        },
        computed: {
            auth() {
                return this.$store.state.currentUser
            },
            order() {
                return this.$store.state.currentOrder
            }
        }
    }
</script>

<style scoped>
    .cp {
        padding-top: 0 !important;
        margin-top: -1rem;
    }
    .chartAreaWrapper {
        overflow-x: hidden;
    }

</style>