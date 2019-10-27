<template>
    <b-container fluid style="width: 90%;">
        <v-row>
            <v-col cols="8">
                <v-card outlined><!--max-width="500" class="mx-auto"-->
                    <!--<v-toolbar>
                        <v-toolbar-title>Товары в корзине</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>-->

                    <v-list subheader>
                        <v-subheader>Товары в корзине</v-subheader>
                        <v-list-item v-for="(item, index) in orderedProducts" :key="item.productID" @click="" style="min-height: 5rem;">
                            <v-list-item-avatar>
                                №{{index+1}}
                            </v-list-item-avatar>

                            <v-list-item-avatar>
                                <v-img :src="item.pic"></v-img>
                            </v-list-item-avatar>

                            <v-list-item-content class="ml-12">
                                <v-list-item-title v-text="item.fullName"></v-list-item-title>
                            </v-list-item-content>

                            <v-list-item-content class="ml-12">
                                <v-list-item-title>
                                    <span>цена <strong>{{item.finalPrice}}</strong> ₽</span>
                                </v-list-item-title>
                            </v-list-item-content>

                            <v-list-item-action class="ml-12">
                                <div class="my-2">
                                    <v-btn text small icon>
                                        <v-icon>mdi-minus</v-icon>
                                    </v-btn>
                                    <span>1</span>
                                    <v-btn text small icon>
                                        <v-icon>mdi-plus</v-icon>
                                    </v-btn>
                                </div>
                            </v-list-item-action>


                            <v-list-item-icon>
                                <v-btn icon>
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
                        <p class="display-1">Сумма заказа <span class="text--primary">{{orderTotalPrice}} ₽</span></p>
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
                        <v-btn class="cp" tile outlined color="success">
                            <v-icon left>mdi-login-variant</v-icon>
                            Войдите, что бы получить скидку!
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
                                        <span>3</span>
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
                                        <span>7</span>
                                    </template>
                                </v-badge>
                            </v-col>
                        </v-row>
                    </div>
                    <v-divider></v-divider>
                    <v-card-actions>
                        <v-btn block color="#e52d00" dark>Оформить заказ</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </b-container>


</template>

<script>
    import axios from 'axios'
    export default {
        data: () => ({
            orderedProducts: [],
            orderTotalPrice: 5590,
            totalBonus: 200
        }),
        created() {
            axios.post('/api/order/orderedProducts').then(response => {
                console.log(response.data)
                this.orderedProducts = response.data
            })
        }
    }
</script>

<style scoped>
    .cp {
        padding-top: 0 !important;
        margin-top: -1rem;
    }

</style>