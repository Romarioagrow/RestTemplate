<template>
    <!---->
    <v-content>


        <h1>suka</h1>
        <v-img src="D:\Projects\Rest\src\main\resources\static\pics\logo.png"></v-img>
           <!-- <v-row>
                <v-navigation-drawer v-show="true" expand-on-hover="true"
                                     height="300" width="500">
                </v-navigation-drawer>



                <div>
                    kek
                </div>
            </v-row>-->

        <v-sheet
                class="mx-auto mt-3"
                max-width="700"
        >
            <v-slide-group multiple show-arrows>
                <v-slide-item v-for="n in 25" :key="n" v-slot:default="{ active, toggle }">
                    <v-btn
                            class="mx-2"
                            :input-value="active"
                            active-class="purple white--text"
                            depressed
                            rounded
                            @click="toggle"
                    >
                        Options {{ n }}
                    </v-btn>
                </v-slide-item>
            </v-slide-group>
        </v-sheet>
        <!---->
        <v-container>
            <!---->
            <items-list />
            <!---->
            <v-btn class="ma-2" color="red" dark>Удалить
                <v-icon dark right>mdi-cancel</v-icon>
            </v-btn>
            <v-btn class="ma-2" color="primary" dark>Заказать
                <v-icon dark right>mdi-checkbox-marked-circle</v-icon>
            </v-btn>
        </v-container>
        <v-container>
            <v-card-text>
                <v-row>
                    <v-col class="px-4">
                        <v-range-slider
                                v-model="range"
                                :max="max"
                                :min="min"
                                hide-details
                                class="align-center"
                        >
                            <template v-slot:prepend>
                                <v-text-field
                                        v-model="range[0]"
                                        class="mt-0 pt-0"
                                        hide-details
                                        single-line
                                        type="number"
                                        style="width: 60px"
                                ></v-text-field>
                            </template>
                            <template v-slot:append>
                                <v-text-field
                                        v-model="range[1]"
                                        class="mt-0 pt-0"
                                        hide-details
                                        single-line
                                        type="number"
                                        style="width: 60px"
                                ></v-text-field>
                            </template>
                        </v-range-slider>
                    </v-col>
                </v-row>
            </v-card-text>
            <b-alert show>ЧТО ЗА ХУЙНЯ \О.О/</b-alert>
            <!---->
            <v-carousel>
                <v-carousel-item v-for="(color, i) in colors" :key="color">
                    <v-sheet :color="color" height="100%" tile>
                        <v-row class="fill-height" align="center" justify="center">
                            <div class="display-3">Slide {{ i + 1 }}</div>
                        </v-row>
                    </v-sheet>
                </v-carousel-item>
            </v-carousel>
            <!---->
            <v-row justify="center" class="mt-3">
                <v-dialog v-model="dialog" persistent max-width="500">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" dark v-on="on">Ну че там?</v-btn>
                    </template>
                    <v-card>
                        <v-card-title class="headline">Нахуя ты это открыл?</v-card-title>
                        <v-card-text>Блять ну вроде бы нормальный современный фреймворк, но вроде бы чуть-чуть все равно через жопу</v-card-text>
                        <v-card-actions>
                            <div class="flex-grow-1"></div>
                            <v-btn color="green darken-1" text @click="dialog = false">Да норм</v-btn>
                            <v-btn color="green darken-1" text @click="dialog = false">Пиздец</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-row>
            <!---->
            <div class="mt-3">
                <v-alert
                        v-model="alert"
                        border="left"
                        close-text="Close Alert"
                        color="deep-purple accent-4"
                        dark
                        dismissible
                >
                    Малоэтажная застройка благоприятно влияет на безопасность как во дворах, так и на тротуарах. Это происходит из-за появления случайных наблюдателей за этим самым тротуаром или двором в лице жильцов. Тот же принцип не будет работать в многоэтажных муравейниках, поскольку у жителей зданий такого типа не создаётся ощущения принадлежности к чему-либо, кроме их собственной квартиры
                </v-alert>
                <div class="text-center">
                    <v-btn class="mb-3"
                           v-if="!alert"
                           color="deep-purple accent-4"
                           dark
                           @click="alert = true"
                    >
                        Давай по новой
                    </v-btn>
                </div>
            </div>
            <!---->
            <div>
                <div class="text-center mb-4">
                    <v-btn
                            color="primary"
                            @click="alert1 = !alert1"
                    >
                        А это еще что?
                    </v-btn>
                </div>
                <v-alert
                        :value="alert1"
                        color="pink"
                        dark
                        border="top"
                        icon="mdi-home"
                        transition="scale-transition"
                >
                    Мдааа, нахуя нажимал?
                </v-alert>
            </div>
        </v-container>
        <!---->
        <div class="text-center">
            <v-bottom-sheet v-model="sheet" persistent>
                <template v-slot:activator="{ on }">
                    <v-btn
                            color="green"
                            dark
                            v-on="on"
                    >
                        Контактная информация
                    </v-btn>
                </template>
                <v-sheet class="text-center" height="200px">
                    <v-btn
                            class="mt-6"
                            flat
                            color="error"
                            @click="sheet = !sheet"
                    >Закрой это</v-btn>
                    <div class="py-3">Тут нет абсолютно никакой информации ._.</div>
                </v-sheet>
            </v-bottom-sheet>
        </div>
        <!---->
        <v-card
                class="mx-auto mt-3"
                max-width="400"
        >
            <v-toolbar
                    flat
                    color="deep-purple accent-4"
                    dark
            >
                <v-btn icon>
                    <v-icon>mdi-close</v-icon>
                </v-btn>
                <v-toolbar-title>Пофильтруем</v-toolbar-title>
            </v-toolbar>

            <v-card-text>
                <h2 class="title mb-2">Choose amenities</h2>

                <v-chip-group
                        v-model="amenities"
                        column
                        multiple
                >
                    <v-chip filter outlined>Elevator</v-chip>
                    <v-chip filter outlined>Washer / Dryer</v-chip>
                    <v-chip filter outlined>Fireplace</v-chip>
                    <v-chip filter outlined>Wheelchair access</v-chip>
                    <v-chip filter outlined>Dogs ok</v-chip>
                    <v-chip filter outlined>Cats ok</v-chip>
                </v-chip-group>
            </v-card-text>

            <v-card-text>
                <h2 class="title mb-2">Choose neighborhoods</h2>

                <v-chip-group
                        v-model="neighborhoods"
                        column
                        multiple
                >
                    <v-chip filter outlined>Snowy Rock Place</v-chip>
                    <v-chip filter outlined>Honeylane Circle</v-chip>
                    <v-chip filter outlined>Donna Drive</v-chip>
                    <v-chip filter outlined>Elaine Street</v-chip>
                    <v-chip filter outlined>Court Street</v-chip>
                    <v-chip filter outlined>Kennedy Park</v-chip>
                </v-chip-group>
            </v-card-text>
        </v-card>
    </v-content>

</template>

<script>
    import ItemsList from 'components/ItemsList.vue'
    import MenuIcon from 'vue-material-design-icons/Menu.vue'
    export default {
        components: {
            ItemsList, MenuIcon
        },
        data() {
            return {
                colors: [
                    'primary',
                    'secondary',
                    'yellow darken-2',
                    'red',
                    'orange',
                ],
                dialog: false,
                alert: true,
                alert1: false,
                sheet: false,
                links: [
                    'Home',
                    'About Us',
                    'Team',
                    'Services',
                    'Blog',
                    'Contact Us',
                ],
                amenities: [1, 4],
                neighborhoods: [1],
                model: null,
                min: 5500,
                max: 9990,
                slider: 100,
                range: [5500, 9990]
            }
        }
    }
</script>

<style scoped>

</style>