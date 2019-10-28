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
                            {{ product.annotation }}
                        </b-card-body>
                    </b-card>
                </v-card>
            </v-col>
            <v-col class="mt-10">
                <v-card >
                    <v-row>
                        <v-col cols="4">
                            <v-card-title>{{ product.finalPrice }} ₽</v-card-title>
                        </v-col>
                        <v-col class="mr-5">
                            <v-card-text>За покупку будет зачисленно <strong>{{ product.bonus }} </strong> баллов!</v-card-text>
                        </v-col>
                    </v-row>
                    <v-card-actions>
                        <v-btn text color="deep-purple accent-4">В корзину</v-btn>
                        <v-btn text color="deep-purple accent-4">Купить сейчас</v-btn>
                        <div class="flex-grow-1"></div>
                    </v-card-actions>
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
                linkBack: ''
            }
        },
        beforeCreate() {
            const productID = (decodeURI(window.location.href).substr(decodeURI(window.location.href).lastIndexOf('/')+1));
            let url = '/api/products/product/' + productID;
            axios.get(url).then(response =>
            {
                this.product = response.data
                this.linkBack = '/products/'+ (this.product.productGroup).toLowerCase();
            });
        }
    }
</script>

<style scoped>

</style>