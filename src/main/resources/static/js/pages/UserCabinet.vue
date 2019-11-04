<template>
    <div>
        <v-navigation-drawer absolute permanent>
            <template v-slot:prepend>
                <v-list-item two-line>
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

                        </v-list-item>
                    </v-list>
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