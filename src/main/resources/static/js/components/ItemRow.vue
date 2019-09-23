<template>
        <v-card max-width="344" class="mx-auto">
            <v-card-title>
                {{item.name}}
                <i>({{item.id}})</i>
            </v-card-title>
            <v-card-text>
                {{item.attributes}}
                {{item.type}}
            </v-card-text>
        </v-card>
</template>

<script>
    export default {
        props: ['item']
    }
</script>

<style scoped>
</style>