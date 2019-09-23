<template>
        <v-container>
            <h1>
                Каталог товаров
            </h1>
            <v-content>
                <v-card>
                    <v-tabs center-active dark show-arrows background-color="teal darken-3"
                            :centered="true"
                            :icons-and-text="true"
                    >
                        <v-tabs-slider color="teal lighten-3"></v-tabs-slider>

                        <v-tab v-for="i in categories.length"
                                :key="i"
                                :href="'#tab-' + i"
                        >
                            {{categories[i]}}
                            <v-icon>mdi-phone</v-icon>
                        </v-tab>

                        <v-tab-item v-for="i in categories.length"
                                    :key="i"
                                    :value="'tab-' + i"
                        >
                            <v-card flat tile>
                                <v-item-group>
                                    <v-container>
                                        <v-row>
                                            <catalog-groups v-for="(group, i) in productGroups"
                                                            :key="group.groupName"
                                                            :group="group"
                                                            :index="i">

                                            </catalog-groups>
                                        </v-row>
                                    </v-container>
                                </v-item-group>
                            </v-card>
                        </v-tab-item>
                    </v-tabs>
                </v-card>
            </v-content>
        </v-container>
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
                    'Садово-огородный инвентарь',
                    'Консервирование',
                    'Отопительное оборудование',
                    'Спорт и отдых',
                    'Товары для отдыха на природе'
                ]
            }
        },
        created() {
            //var group = '/catalog/' + this.categories[i];
            axios.get('/items/catalog/теле-видео-аудио').then(response => {
                this.productGroups = response.data
                console.log(this.productGroups)
            })
        }
    }
</script>

<style scoped>

</style>