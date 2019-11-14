<template>
    <div v-if="created">
        <v-navigation-drawer absolute permanent>
            <template v-slot:prepend>
                <v-list-item two-line>
                    <v-list-item-content>
                        <v-list-item-title>Admin</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </template>
            <v-list dense>
                <!--База данных-->
                <v-list-item @click="openDB()">
                    <v-list-item-icon>
                        <v-icon>mdi-file-excel</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                        <v-list-item-title>База данных</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <!--Текущие заказы-->
                <v-list-item @click="openOrders()">
                    <v-list-item-icon>
                        <v-icon>mdi-format-list-bulleted-square</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content >
                        <v-list-item-title>Текущие аказы</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <!--Завершенные заказы-->
                <v-list-item @click="openCompletedOrders()">
                    <v-list-item-icon>
                        <v-icon>mdi-check</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content >
                        <v-list-item-title>Завершенные заказы</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
            <v-divider></v-divider>
            <v-card-actions>
                <v-btn block tile color="red" @click="logout">
                    <span>Выход</span>
                    <v-icon>mdi-logout</v-icon>
                </v-btn>
            </v-card-actions>
        </v-navigation-drawer>
        <v-content>
            <v-container fluid fill-height>
                <v-container v-if="activeContainerDB">
                    <v-row>
                        <v-col>
                            <v-card>
                                <v-card-title>Обновить БД</v-card-title>
                                <v-card-actions class="ml-5">
                                    <form enctype="multipart/form-data">
                                        <v-row class="mb-3"><input type="file" name="file" v-on:change="fileChange($event.target.files)" /></v-row>
                                        <v-row v-if="!updatingDB"><v-btn color="success" v-on:click="uploadExcelFile()">Загрузить</v-btn></v-row>
                                        <v-row v-else>
                                            <v-progress-circular indeterminate color="primary"></v-progress-circular>
                                        </v-row>
                                    </form>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                        <v-col>
                            <v-card>
                                <v-card-title>Бренды Эксперт</v-card-title>
                                <v-card-actions class="ml-5">
                                    <form enctype="multipart/form-data">
                                        <v-row class="mb-3"><input type="file" name="fileBrands" v-on:change="brandsFileChange($event.target.files)" /></v-row>
                                        <v-row v-if="!updatingBrands"><v-btn color="success" v-on:click="uploadBrandsPrice()">Загрузить бренд-прайс</v-btn></v-row>
                                        <v-row v-else>
                                            <v-progress-circular indeterminate color="primary"></v-progress-circular>
                                        </v-row>
                                    </form>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>Обновить каталог</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="blue" v-on:click="updateCatalog()">Обновить</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>Обновить поиск</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="blue" v-on:click="updateSearch()">Поиск</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>Спарсить ссылки фото RUSBT</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="yellow" v-on:click="parsRUSBTPics()">Запустить парсер</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>TEST</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="blue" v-on:click="test()">Test</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                </v-container>
                <v-container v-if="activeContainerOrders">
                    <v-layout justify-center align-center>
                        <v-list subheader width="100%">
                            <v-subheader><h3>Принятые заказы</h3></v-subheader>
                            <v-list-item v-for="order of acceptedOrders" :key="order.orderID" style="background-color: #fafafa;">
                                <v-card outlined width="100%" class="mb-3">
                                    <v-card-title>
                                        <v-row>
                                            <v-col cols="2">
                                                Заказ №{{order.orderID}}
                                            </v-col>
                                            <v-col>
                                                от {{order.openDate.replace('T',' ')}}
                                            </v-col>
                                            <v-col cols="6">
                                                <h3 v-if="order.confirmed" style="color: #5fb053">Подтвержден, ожидает оплаты</h3>
                                                <h3 v-else style="color: red;">Не подтвержден</h3>
                                            </v-col>
                                        </v-row>
                                    </v-card-title>
                                    <v-card-text>
                                        <v-row>
                                            <v-col class="subtitle-1 black--text">
                                                ФИО: {{order.user.lastName}} {{order.user.firstName}} {{order.user.patronymic}}
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col class="subtitle-1 black--text">
                                                Телефон: +{{order.user.username}}
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col class="subtitle-1 black--text">
                                                Сумма заказа: {{order.totalPrice.toLocaleString('ru-RU')}} ₽
                                            </v-col>
                                        </v-row>
                                    </v-card-text>
                                    <v-divider></v-divider>
                                    <v-list subheader>
                                        <v-subheader>Заказанные товары</v-subheader>
                                        <v-list-item v-for="product in order.orderedList" :key="product.productID">
                                            <v-list-item-avatar>
                                                <v-img :src="product.pic"></v-img>
                                            </v-list-item-avatar>
                                            <v-list-item-content>
                                                <v-list-item-title v-text="product.productName"></v-list-item-title>
                                            </v-list-item-content>
                                            <v-list-item-content>
                                                <v-list-item-title>
                                                    <span><strong>{{(product.productPrice * product.productAmount).toLocaleString('ru-RU')}}</strong> ₽</span>
                                                </v-list-item-title>
                                            </v-list-item-content>
                                            <v-list-item-content>
                                                <v-list-item-title>
                                                    <span>за {{product.productAmount}} шт.</span>
                                                </v-list-item-title>
                                            </v-list-item-content>
                                        </v-list-item>
                                    </v-list>
                                    <v-divider></v-divider>
                                    <v-card-actions>
                                        <v-row>
                                            <v-col>
                                                <div v-if="!order.confirmed">
                                                    <v-btn block outlined color="primary" dark @click="confirmOrder(order.orderID)">
                                                        Подтвердить
                                                        <v-icon dark right>mdi-check</v-icon>
                                                    </v-btn>
                                                </div>
                                                <div v-else>
                                                    <v-btn block color="primary" dark @click="cancelConfirmOrder(order.orderID)">
                                                        Отменить подтверждение
                                                        <v-icon dark right>mdi-cancel</v-icon>
                                                    </v-btn>
                                                </div>
                                            </v-col>
                                            <v-col v-if="order.confirmed">
                                                <v-btn block outlined color="green" dark @click="completeOrder(order.orderID)">
                                                    Завершить
                                                    <v-icon dark right>mdi-checkbox-marked-circle</v-icon>
                                                </v-btn>
                                            </v-col>
                                            <v-col>
                                                <v-btn block color="red" dark @click="deleteOrder(order.orderID)">
                                                    Удалить
                                                    <v-icon dark right>mdi-close</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-card-actions>
                                </v-card>
                            </v-list-item>
                        </v-list>
                    </v-layout>
                </v-container>
                <v-container v-if="activeContainerCompletedOrders">
                    <v-layout justify-center align-center>
                        <v-list subheader width="100%">
                            <v-subheader><h3>Завершенные заказы</h3></v-subheader>
                            <v-list-item v-for="order of completedOrders" :key="order.orderID" style="background-color: #fafafa;">
                                <v-card outlined width="100%" class="mb-3">
                                    <v-card-title>
                                        <v-row>
                                            <v-col cols="2">
                                                Заказ №{{order.orderID}}
                                            </v-col>
                                            <v-col>
                                                от {{order.openDate.replace('T',' ')}}
                                            </v-col>
                                        </v-row>
                                    </v-card-title>
                                    <v-card-text>
                                        <v-row>
                                            <v-col class="subtitle-1 black--text">
                                                ФИО: {{order.user.lastName}} {{order.user.firstName}} {{order.user.patronymic}}
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col class="subtitle-1 black--text">
                                                Телефон: +{{order.user.username}}
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col class="subtitle-1 black--text">
                                                Сумма заказа: {{order.totalPrice.toLocaleString('ru-RU')}} ₽
                                            </v-col>
                                        </v-row>
                                    </v-card-text>
                                    <v-divider></v-divider>
                                    <v-list subheader>
                                        <v-subheader>Заказанные товары</v-subheader>
                                        <v-list-item v-for="product in order.orderedList" :key="product.productID">
                                            <v-list-item-avatar>
                                                <v-img :src="product.pic"></v-img>
                                            </v-list-item-avatar>
                                            <v-list-item-content>
                                                <v-list-item-title v-text="product.productName"></v-list-item-title>
                                            </v-list-item-content>
                                            <v-list-item-content>
                                                <v-list-item-title>
                                                    <span><strong>{{(product.productPrice * product.productAmount).toLocaleString('ru-RU')}}</strong> ₽</span>
                                                </v-list-item-title>
                                            </v-list-item-content>
                                            <v-list-item-content>
                                                <v-list-item-title>
                                                    <span>за {{product.productAmount}} шт.</span>
                                                </v-list-item-title>
                                            </v-list-item-content>
                                        </v-list-item>
                                    </v-list>
                                    <v-divider></v-divider>
                                    <v-card-actions>
                                        <v-row>
                                            <v-col>
                                                <v-btn outlined block color="black" dark @click="cancelCompleteOrder(order.orderID)">
                                                    Отменить завершение
                                                    <v-icon dark right>mdi-cancel</v-icon>
                                                </v-btn>
                                            </v-col>
                                            <v-col>
                                                <v-btn color="red" block dark @click="deleteOrder(order.orderID)">
                                                    Удалить
                                                    <v-icon dark right>mdi-close</v-icon>
                                                </v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-card-actions>
                                </v-card>
                            </v-list-item>
                        </v-list>
                    </v-layout>
                </v-container>
            </v-container>
        </v-content>
    </div>
</template>

<script>
    import axios from "axios";
    export default {
        data() {
            return {
                file: new FormData(),
                fileBrands: new FormData(),
                created: false,
                activeContainerDB: true,
                activeContainerOrders: false,
                activeContainerCompletedOrders: false,
                acceptedOrders: [],
                completedOrders: [],
                dialog: false,
                updatingDB: false,
                updatingBrands: false
            }
        },
        beforeCreate() {
            axios.get('/admin').then(() => {
                this.created = true
            }).catch(error =>
            {
                if (error.response) {
                    console.log(error.response.status);
                    if (error.response.status === 403) {
                        this.$router.push('/')
                    }
                }
            })
        },
        created() {
            axios.get('/admin/acceptedOrders').then(response => {
                this.acceptedOrders = response.data
            })
            axios.get('/admin/completedOrders').then(response => {
                this.completedOrders = response.data
            })
        },
        methods: {
            fileChange(fileList) {
                this.file.append("file", fileList[0], fileList[0].name);
            },
            brandsFileChange(fileList) {
                this.fileBrands.append("fileBrands", fileList[0], fileList[0].name);
            },
            uploadExcelFile() {
                this.updatingDB = true
                axios.post('admin/uploadFileDB', this.file).then(() => {
                    this.updatingDB = false
                    this.file = new FormData()
                });

            },
            uploadBrandsPrice() {
                this.updatingBrands = true
                axios.post('admin/uploadFileBrands', this.fileBrands).then(() => {
                    this.updatingBrands = false
                    this.fileBrands = new FormData()
                });
            },
            updateCatalog() {
                axios.post('admin/updateCatalog').then(console.log('catalog updated'));
            },
            parsRUSBTPics() {
                axios.post('admin/parsePicsRUSBT').then(console.log('pics parsed'));
            },
            logout() {
                axios.post('user/logout').then((response) => {
                    this.$store.dispatch('logout')
                    this.$router.push('/')
                })
            },
            openDB() {
                this.activeContainerDB = true
                this.activeContainerOrders = false
                this.activeContainerCompletedOrders = false
            },
            openOrders() {
                this.activeContainerDB = false
                this.activeContainerOrders = true
                this.activeContainerCompletedOrders = false
            },
            openCompletedOrders() {
                this.activeContainerCompletedOrders = true
                this.activeContainerDB = false
                this.activeContainerOrders = false
            },
            confirmOrder(orderID) {
                axios.post('admin/confirmOrder', orderID).then(() => {
                    axios.get('/admin/acceptedOrders').then(response => {
                        this.acceptedOrders = response.data
                    })
                })
            },
            cancelConfirmOrder(orderID) {
                axios.post('admin/cancelConfirmOrder', orderID).then(() => {
                    axios.get('/admin/acceptedOrders').then(response => {
                        this.acceptedOrders = response.data
                    })
                })
            },
            completeOrder(orderID) {
                axios.post('admin/completeOrder', orderID).then(() => {
                    axios.get('/admin/acceptedOrders').then(response => {
                        this.acceptedOrders = response.data
                    })
                })
            },
            deleteOrder(orderID) {
                axios.post('admin/deleteOrder', orderID).then((response) => {
                    this.acceptedOrders = response.data
                })
            },
            test() {
                axios.post('admin/test').then((response) => {
                    console.log(response)
                })
            },
            updateSearch() {
                axios.post('admin/updateSearch').then((response) => {
                    console.log(response)
                })
            }
        }
    }
</script>

<style scoped></style>