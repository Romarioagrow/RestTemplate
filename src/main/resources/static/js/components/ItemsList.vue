<template>
    <v-container>
        <v-row>
            <item-row v-for="item in items"
                      :key="item.id"
                      :item="item"
                      :items="items"
            />
        </v-row>
    </v-container>
</template>

<script>
    import ItemRow from "components/ItemRow.vue";
    import axios from 'axios'
    export default {
        components: {
            ItemRow
        },
        data() {
            return {
                items: []
            }
        }
        ,
        created() {
            axios.get('/items').then(response => this.items = response.data)
        }
    }
</script>

<style scoped>

</style>