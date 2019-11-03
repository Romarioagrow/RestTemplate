<template>
    <div>
        <v-navigation-drawer absolute permanent>
            <template v-slot:prepend>
                <v-list-item two-line>
                    <!--<v-list-item-avatar>
                        <img src="https://randomuser.me/api/portraits/women/81.jpg">
                    </v-list-item-avatar>-->
                    <v-list-item-content>
                        <v-list-item-title>{{lastName}} {{username}} {{patronymic}}</v-list-item-title>
                        <v-list-item-subtitle>Бонусов: <strong>{{userBonus}}</strong></v-list-item-subtitle>
                    </v-list-item-content>
                </v-list-item>
            </template>

            <v-divider></v-divider>

            <v-list dense>
                <v-list-item v-for="item in menu" :key="item.title" @click="">
                    <v-list-item-icon>
                        <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-icon>

                    <v-list-item-content>
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>

            <v-divider></v-divider>

            <v-card-actions>
                <v-btn block tile @click="logout">
                    <span>Выход</span>
                    <v-icon>mdi-logout</v-icon>
                </v-btn>
            </v-card-actions>
        </v-navigation-drawer>

        <v-content>
            <v-container fluid fill-height>
                <v-layout justify-center align-center>


                    <!--<v-row v-for="order of acceptedOrders" :key="order.orderID">
                        <v-row>


                            <v-card dense width="100%">
                                <v-card-title>
                                    Заказ№ {{order.orderID}}
                                </v-card-title>
                            </v-card>
                        </v-row>
                    </v-row>-->


                    <v-list subheader width="100%">
                        <v-subheader>Принятые заказы</v-subheader>
                        <v-list-item v-for="order of acceptedOrders" :key="order.orderID">
                            <v-card width="100%" class="mb-3">
                                <v-card-title>
                                    Заказ №{{order.orderID}}
                                </v-card-title>
                                <v-card-text>
                                    <div class="my-4 subtitle-1 black--text">
                                        Сумма заказа: {{order.totalPrice}} ₽
                                    </div>
                                </v-card-text>
                                <v-divider></v-divider>
                                <v-card-actions>
                                    <v-list v-for="[product, amount] in order.orderedProducts" :key="product">
                                        <v-list-item-content>
                                            <v-list-item-title v-text="product"></v-list-item-title>
                                        </v-list-item-content>
                                    </v-list>
                                </v-card-actions>
                            </v-card>
                            <!--<v-list-item-content>
                                Заказ№ {{order.orderID}}
                            </v-list-item-content>
                            <v-list-item-content class="ml-12">
                                <v-list-item-title>
                                    {{order.totalPrice}} ₽
                                </v-list-item-title>
                            </v-list-item-content>-->
                        </v-list-item>
                    </v-list>

                    <!--<v-list-item-content class="ml-12">
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
                    </v-list-item-icon>-->


                    <!--<v-card>
                        <v-card-title>
                            Lol
                        </v-card-title>
                    </v-card>-->

                    <!--<v-card>
                        <v-card-title>
                            Lol
                        </v-card-title>
                    </v-card>
                    &lt;!&ndash;<v-flex shrink>
                        <v-card>
                            <v-card-title>
                                Lol
                            </v-card-title>
                        </v-card>
                        &lt;!&ndash;<v-tooltip right>
                            <template v-slot:activator="{ on }">
                                <v-btn :href="source" icon large target="_blank" v-on="on">
                                    <v-icon large>mdi-code-tags</v-icon>
                                </v-btn>
                            </template>
                            <span>Source</span>
                        </v-tooltip>
                        <v-tooltip right>
                            <template v-slot:activator="{ on }">
                                <v-btn
                                        icon
                                        large
                                        href="https://codepen.io/johnjleider/pen/aezMOO"
                                        target="_blank"
                                        v-on="on"
                                >
                                    <v-icon large>mdi-codepen</v-icon>
                                </v-btn>
                            </template>
                            <span>Codepen</span>
                        </v-tooltip>
                    </v-flex>-->
                </v-layout>
            </v-container>
        </v-content>
    </div>

</template>

<script>
    import axios from 'axios'
    export default {
        data() {
            return {
                username:   this.$store.state.currentUser.firstName,
                lastName:   this.$store.state.currentUser.lastName,
                patronymic: this.$store.state.currentUser.patronymic,
                userBonus:  this.$store.state.currentUser.bonus,
                acceptedOrders: [],
                menu: [
                    { title: 'Принятые заказы',     icon: 'mdi-clock-outline' },
                    { title: 'Завершенные заказы',  icon: 'mdi-check-outline' },
                    { title: 'Все заказы',          icon: 'mdi-script-text-outline' },
                ]
            }
        },
        methods: {
            logout() {
                axios.post('http://localhost:9000/user/logout').then((response) => {
                    this.$store.dispatch('logout')
                    this.$router.push('/')
                })
            },
            showAcceptedOrders() {
                axios.get('/api/order/getAcceptedOrders').then((response) => {
                    console.log(response.data)
                    this.acceptedOrders = response.data
                })
            }
        },
        beforeCreate() {
            axios.post('/auth/noUser').then(noUser => {
                if (noUser.data === true) {
                    this.$router.push('/login')
                }
            })
        },
        created() {
            this.showAcceptedOrders()
        }
    }
</script>

<style scoped>
</style>