<template>
    <v-card class="mx-auto mt-3" max-width="350">
        <v-img class="white--text" contain height="200px" :src="product.originalPic"></v-img>
        <v-card-text>
            <v-card-title class="align-end fill-height">{{product.fullName}}</v-card-title>
            <span>{{product.productType}}</span><br>
            <span>{{product.originalAnnotation}}</span><br>
            <span><strong>{{product.finalPrice}} ₽</strong></span>
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
</template>

<script>
    export default {
        props: ['product']
    }
</script>

<style scoped>

</style>