<template>
    <v-item v-slot:default="{ active, toggle }">
    <v-card class="mx-auto mt-3" max-width="350">
        <v-img class="white--text" contain height="200px" :src="product.pic" alt="Bad Link"></v-img>
        <v-card-text>
            <router-link :to=productID>
                <v-card-title class="align-end fill-height">{{product.fullName}}</v-card-title>
            </router-link>
            <span>{{product.annotation}}</span><br>
            <h3>{{product.finalPrice}} ₽</h3>
            <span>{{product.supplier}}</span>
        </v-card-text>
        <v-card-actions>
            <v-btn text color="orange">
                В корзину
            </v-btn>
            <v-btn text color="orange">
                Купить в 1 клик
            </v-btn>
        </v-card-actions>
    </v-card>
    </v-item>
</template>

<script>
    export default {
        props: ['product'],
        data() {
            return {
                productID: '/products/product/' + this.product.productID
            }
        }
    }
</script>

<style scoped>

</style>