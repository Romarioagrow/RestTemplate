<template>
        <v-card max-width="344" class="mx-auto">
            <v-card-title>
                {{item.fullName}}
            </v-card-title>
            <v-card-text>
                {{item.productType}}
                {{item.finalPrice}}
            </v-card-text>
        </v-card>
</template>

<script>
    export default {
        props: ['item']
    }
</script>

<style scoped></style>