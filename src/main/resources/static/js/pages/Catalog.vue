<template>
    <div>
        <v-progress-linear indeterminate color="green" v-if="loading"></v-progress-linear>
        <v-container fluid>
            <div v-if="!loading">
                <h1>
                    Каталог товаров
                </h1>
                <v-card>
                    <v-tabs center-active dark show-arrows background-color="teal darken-3" :centered="true" :icons-and-text="true">
                        <v-tabs-slider color="teal lighten-3"></v-tabs-slider>

                        <v-tab v-for="(value, key, index) of allCategories" :key="index" :href="'#tab-' + index" @click="loadCatalog()">
                            {{key}}
                            <v-icon>mdi-phone</v-icon>
                        </v-tab>

                        <v-tab-item v-for="(value, key, index) of allCategories" :key="index" :value="'tab-' + index">
                            <v-card flat tile>
                                <v-item-group>
                                    <v-container>
                                        <v-row align="stretch" justify="space-around">
                                            <catalog-groups v-for="(group, i) in value" :key="group.groupName" :group="group" :index="i"></catalog-groups>
                                        </v-row>
                                    </v-container>
                                </v-item-group>
                            </v-card>
                        </v-tab-item>
                    </v-tabs>
                </v-card>
            </div>
        </v-container>
    </div>
</template>

<script>
    import CatalogGroups from "components/CatalogGroups.vue";
    import axios from 'axios'
    export default {
        components: {
            CatalogGroups
        },
        data() {
            return {
                allCategories: new Map(),
                categories: [],
                loading: true
            }
        },
        methods: {
            lol() {
                console.log(this.allCategories)
            },
            /*loadGroups(group) {
                console.log(group);
                axios.get('/api/catalog/'+ group).then(response => {
                    this.productGroups = response.data;
                })
            },
            loadCatalog() {
                axios.post('/api/all/catalog/', this.categories).then(response =>
                {
                    const fullCatalog = response.data
                    Object.entries(fullCatalog).forEach(([category, groupsList]) => {
                        this.allCategories.set(category, groupsList)
                    });
                    console.log(this.allCategories)
                })
            }*/
        },
        beforeCreate() {
            //this.loading = true
            this.categories = [
                'Теле-видео-аудио',
                'Кухонная техника',
                'Техника для дома',
                'Встраиваемая техника',
                'Климатическая техника',
                'Приборы персонального ухода',
                'Цифровые устройства',
                'Компьютеры и оргтехника',
                'Инструменты для дома',
                'Автотовары',
                'Строительные инструменты',
                'Подсобное хозяйство',
                'Товары для дома',
                'Отопительное оборудование',
                'Спорт и отдых',
                'Посуда и кухонные принадлежности',
                'Сопутствующие товары',
            ]
            axios.post('/api/all/catalog/', this.categories).then(response => {
                this.allCategories = response.data
                this.loading = false
            })
            //alert("before")
        },
        created() {

            //alert("after")
        }
    }
</script>
<style scoped></style>
