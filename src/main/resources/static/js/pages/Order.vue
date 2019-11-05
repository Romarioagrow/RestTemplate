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
                                        <span><strong>{{(product.finalPrice * amount).toLocaleString('ru-RU')}}</strong> ₽</span>
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
                    <v-card outlined v-if="productAmount !== 0">
                        <v-card-text class="pb-0">
                            <v-row>
                                <v-col>
                                    <p class="display-1">Сумма заказа <span class="text--primary">{{totalPrice.toLocaleString('ru-RU')}} ₽</span></p>
                                </v-col>
                            </v-row>
                        </v-card-text>
                        <div v-if="!auth">
                            <v-card-actions>
                                <!--В DIALOG!!!-->
                                <router-link to="/login"  >
                                    <v-btn class="cp" tile outlined color="success">
                                        <v-icon left>mdi-login-variant</v-icon>
                                        Войдите, что бы получить скидку!
                                    </v-btn>
                                </router-link>
                            </v-card-actions>
                        </div>
                        <div v-else>
                            <v-card-text>
                                <v-row>
                                    <v-col>
                                        Бонусов за заказ: <span class="cp"><strong>{{totalBonus}}</strong></span>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        Ваши бонусные рубли: <span><strong>{{bonusAvailable}}</strong></span>
                                    </v-col>
                                </v-row>
                            </v-card-text>
                        </div>
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
                        <v-card-actions v-if="productAmount !== 0">
                            <v-dialog v-model="orderDialog" persistent max-width="1000">
                                <template v-slot:activator="{ on }">
                                    <v-btn block color="#e52d00" dark v-on="on">Оформить заказ</v-btn>
                                </template>
                                <v-card outlined>
                                    <v-card-title class="headline">
                                        <span v-if="auth" class="mr-1">{{firstName}},</span><span v-else class="mr-1">Ваш </span> заказ на сумму
                                        <span style="color: #e52d00" class="ml-2" v-if="!discountApplied">{{totalPrice.toLocaleString('ru-RU')}} ₽</span>
                                        <span v-else style="color: green" class="ml-2">{{discountPrice.toLocaleString('ru-RU')}} ₽ со скидкой</span>
                                    </v-card-title>
                                    <v-divider></v-divider>
                                    <div v-if="auth">
                                        <v-card-text >
                                            <v-row>
                                                <v-col cols="3">
                                                    Ваши бонусные рубли: <span><strong>{{bonusAvailable}}</strong></span>
                                                </v-col>
                                                <v-col cols="3">
                                                    Ваша скидка: <span><strong>{{discountPercent}}%</strong></span>
                                                </v-col>
                                                <v-col cols="3" v-if="!discountApplied && bonusAvailable !== 0">
                                                    <v-btn class="cp" tile outlined color="primary" @click="applyDiscount()">
                                                        <v-icon left>mdi-sale</v-icon>
                                                        Применить скидку
                                                    </v-btn>
                                                </v-col>

                                            </v-row>
                                        </v-card-text>
                                        <v-divider></v-divider>
                                    </div>
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
                                    </v-card-text>
                                    <v-divider></v-divider>
                                    <v-card-text>
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
                                                <v-btn color="#e10c0c" block dark @click="cancelOrder()">
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
                    <v-card v-else>
                        <v-card-actions class="chartAreaWrapper">
                                <v-btn color="primary" block dark @click="toCatalog()">
                                    <v-icon dark right>mdi-backburger</v-icon>
                                    В каталог
                                </v-btn>
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
            discountPercent :0,
            discountAmount  :0,
            discountApplied :false
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
                this.discountPercent = this.calculateDiscount

                console.log(Object.keys(productList).length === 0)

                /*На всякий случай*/
                if (Object.keys(productList).length === 0) {
                    this.$store.state.currentOrder.totalBonus = 0
                    this.$store.state.currentOrder.totalPrice = 0
                    this.totalPrice = 0
                    this.totalBonus = 0
                }
            },
            deleteProduct(productID) {
                axios.post('/api/order/deleteProduct', productID).then(response => {
                    this.loadData(response)
                    this.$store.dispatch('removeOrderedProduct', productID)
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
            applyDiscount() {
                const discountAmount = Math.round(this.discountAmount)
                const price = Math.round(this.$store.state.currentOrder.totalPrice)

                this.$store.state.currentUser.bonus -= discountAmount
                this.$store.state.currentOrder.user.bonus -= discountAmount
                this.$store.state.currentOrder.discountPrice = price - discountAmount
                this.discountApplied = true
            },
            acceptOrder() {
                this.orderDialog = false

                let address = ''
                if (this.city) {
                    address = this.city + ',' + this.street  + ',' + this.house  + ',' + this.flat
                }

                let orderDetails = {
                    'orderID'   :this.order.orderID,
                    'firstName' :this.firstName,
                    'lastName'  :this.lastName,
                    'patronymic':this.patronymic,
                    'mobile'    :this.mobile,
                    'email'     :this.email,
                    'address'   :address
                }

                if (this.discountApplied) {
                    orderDetails.discountAmount = Math.round(this.discountAmount)
                    orderDetails.discountPrice = Math.round(this.$store.state.currentOrder.discountPrice)
                    orderDetails.userID = Math.round(this.$store.state.currentUser.userID)
                }

                axios.post('/api/order/acceptOrder', orderDetails).then(response => {
                    console.log(response)
                    this.$store.dispatch('acceptOrder')
                    this.$store.dispatch('clearOrderedProducts')
                    this.$router.push('/user/cabinet')
                })
            },
            cancelOrder() {
                if (this.discountApplied) {
                    this.$store.state.currentUser.bonus += Math.round(this.discountAmount)
                    this.$store.state.currentOrder.user.bonus += Math.round(this.discountAmount)
                    this.$store.state.currentOrder.discountPrice = null
                    this.discountApplied = false
                }
                this.orderDialog = false
            },
            toCatalog() {
                this.$router.push('/')
            }
        },
        computed: {
            auth() {
                return this.$store.state.currentUser
            },
            order() {
                return this.$store.state.currentOrder
            },
            calculateDiscount() {
                if (!this.auth || this.$store.state.currentUser.bonus === 0) return 0
                const bonus = this.$store.state.currentUser.bonus
                const price = this.$store.state.currentOrder.totalPrice

                let discountPercent = 100 * bonus / price
                this.discountAmount = bonus

                if (discountPercent >= 20) {
                    this.discountAmount = price / 100 * 20
                    return 20
                }
                if (discountPercent === 0) {
                    return 1
                }
                return Math.floor(discountPercent)
            },
            bonusAvailable() {
                return this.$store.state.currentUser.bonus
            },
            discountPrice() {
                return this.$store.state.currentOrder.discountPrice
            }
        },
        created() {
            axios.post('/api/order/orderedProducts').then(response => {
                this.loadData(response)
            })

            if (this.auth) {
                this.lastName   = this.$store.state.currentUser.lastName
                this.firstName  = this.$store.state.currentUser.firstName
                this.patronymic = this.$store.state.currentUser.patronymic
                this.mobile     = this.$store.state.currentUser.username
                this.email      = this.$store.state.currentUser.email
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