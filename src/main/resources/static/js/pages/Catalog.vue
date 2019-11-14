<template>
    <div>
        <v-progress-linear indeterminate color="#e52d00" v-if="loading"></v-progress-linear>
        <b-container fluid class="flu">
            <div v-if="!loading" class="indent">
                <v-tabs center-active show-arrows :centered="true" :icons-and-text="true">
                    <v-tabs-slider color="#e52d00"></v-tabs-slider>
                    <v-tab v-for="(value, key, index) of allCategories" :key="index" :href="'#tab-' + index">
                        {{key}}
                        <v-icon>{{icons[index]}}</v-icon>
                    </v-tab>
                    <v-tab-item class="mt-3 mb-3" v-for="(value, key, index) of allCategories" :key="index" :value="'tab-' + index">
                        <v-card flat tile>
                            <v-item-group>
                                <v-row align="center" justify="space-between" class="bkcl">
                                    <catalog-groups v-for="group in value" :key="group[0]" :group="group" ></catalog-groups>
                                </v-row>
                            </v-item-group>
                        </v-card>
                    </v-tab-item>
                </v-tabs>
            </div>
        </b-container>
    </div>
</template>

<script>
    import CatalogGroups from "components/CatalogGroups.vue"
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
                icons:[
                    'mdi-television',
                    'mdi-fridge',
                    'mdi-washing-machine',
                    'mdi-dice-4',
                    'mdi-air-conditioner',
                    'mdi-content-cut',
                    'mdi-camera',
                    'mdi-desktop-classic',
                    'mdi-radio-handheld',
                    'mdi-circular-saw',
                    'mdi-shovel',
                    'mdi-lightbulb',
                    'mdi-water-boiler',
                    'mdi-bike',
                    'mdi-coffee',
                    'mdi-archive'
                ],
                /*pics: [
                    {navpic: require('@/assets/img/logo.png')},
                ]*/
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
        padding-left: 8rem;
        padding-right: 8rem;
    }
    .bkcl {
        background-color: #f2f2f2;
    }
    .flu {
        width: 95%;
    }
    .navbr {
        width: 70%;
    }
</style>