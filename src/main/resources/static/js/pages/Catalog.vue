<template>
    <v-container fluid>
        <h1>
            Каталог товаров
        </h1>

        <v-card>
            <v-tabs center-active dark show-arrows background-color="teal darken-3" :centered="true" :icons-and-text="true">
                <v-tabs-slider color="teal lighten-3"></v-tabs-slider>

                <v-tab v-for="i in categories.length" :key="i" :href="'#tab-' + i" @click="loadGroups(categories[i])">
                    {{categories[i]}}
                    <v-icon>mdi-phone</v-icon>
                </v-tab>

                <v-tab-item v-for="i in categories.length" :key="i" :value="'tab-' + i">
                    <v-card flat tile>
                        <v-item-group>
                            <v-container>
                                <v-row align="stretch" justify="space-around">
                                    <catalog-groups v-for="(group, i) in productGroups" :key="group.groupName" :group="group" :index="i"></catalog-groups>
                                </v-row>
                            </v-container>
                        </v-item-group>
                    </v-card>
                </v-tab-item>
            </v-tabs>
        </v-card>
    </v-container>
</template>

<script>
    import CatalogGroups from "components/CatalogGroups.vue";
    import axios from 'axios'
    export default {
        components: {
            CatalogGroups
        },
        methods: {
            loadGroups(group) {
                console.log(group);
                axios.get('/api/catalog/'+ group).then(response => {
                    this.productGroups = response.data;
                })
            }
        },
        data() {
            return {
                productGroups: [],
                categories: [
                    '',
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
                    'Гаджеты',
                    'Строительные инструменты',
                    'Подсобное хозяйство',
                    'Товары для дома',
                    'Садово-огородный инвентарь',
                    'Консервирование',
                    'Отопительное оборудование',
                    'Спорт и отдых',
                    'Товары для отдыха на природе',
                    'Посуда и кухонные принадлежности',
                    'Сопутствующие товары',
                ]
            }
        },
        created() {
            axios.get('/api/catalog/теле-видео-аудио').then(response => {
                this.productGroups = response.data;
            })
        }
    }
</script>

<style scoped></style>