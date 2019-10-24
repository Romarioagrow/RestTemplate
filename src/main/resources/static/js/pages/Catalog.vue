<template>
    <div>
        <v-progress-linear indeterminate color="#e52d00" v-if="loading"></v-progress-linear>
        <b-container fluid>
            <div v-if="!loading" class="indent">
                <h1>
                    Каталог товаров
                </h1>
                <v-card>
                    <v-tabs center-active dark show-arrows background-color="#635A4F" :centered="true" :icons-and-text="true">
                        <v-tabs-slider color="#e52d00"></v-tabs-slider>
                        <v-tab v-for="(value, key, index) of allCategories" :key="index" :href="'#tab-' + index" @click="loadCatalog()">
                            {{key}}
                            <v-icon>mdi-phone</v-icon>
                        </v-tab>
                        <v-tab-item v-for="(value, key, index) of allCategories" :key="index" :value="'tab-' + index">
                            <v-card flat tile>
                                <v-item-group>
                                    <b-container fluid>
                                        <v-row align="stretch" justify="space-around">
                                            <catalog-groups v-for="group in value" :key="group[0]" :group="group" ></catalog-groups>
                                        </v-row>
                                    </b-container>
                                </v-item-group>
                            </v-card>
                        </v-tab-item>
                    </v-tabs>
                </v-card>
            </div>
        </b-container>
    </div>
</template>

<script>
    import CatalogGroups from "components/CatalogGroups.vue";
    import catalogJSON from 'assets/json/catalog.json'
    export default {
        components: {
            CatalogGroups
        },
        data() {
            return {
                allCategories: new Map(),
                categories: [],
                loading: true,
            }
        },
        created() {
            this.allCategories = catalogJSON
            this.loading = false
        }
    }
</script>
<style scoped>
    .indent {
        padding-top: 2rem;
        padding-left: 8rem;
        padding-right: 8rem;
    }
</style>
