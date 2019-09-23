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
                                            <div>{{categories[i]}}</div>
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
    export default {
        data() {
            return {
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
        }
    }
</script>

<style scoped>

</style>