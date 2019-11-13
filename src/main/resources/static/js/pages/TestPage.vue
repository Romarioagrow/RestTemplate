<template>
    <v-app id="inspire">
        <v-navigation-drawer v-model="drawer" :clipped="$vuetify.breakpoint.lgAndUp" app>
            <v-list dense>
                <template v-for="item in items">
                    <v-row
                            v-if="item.heading"
                            :key="item.heading"
                            align="center"
                    >
                        <v-col cols="6">
                            <v-subheader v-if="item.heading">
                                {{ item.heading }}
                            </v-subheader>
                        </v-col>
                        <v-col
                                cols="6"
                                class="text-center"
                        >
                            <a
                                    href="#!"
                                    class="body-2 black--text"
                            >EDIT</a>
                        </v-col>
                    </v-row>
                    <v-list-group
                            v-else-if="item.children"
                            :key="item.text"
                            v-model="item.model"
                            :prepend-icon="item.model ? item.icon : item['icon-alt']"
                            append-icon=""
                    >
                        <template v-slot:activator>
                            <v-list-item>
                                <v-list-item-content>
                                    <v-list-item-title>
                                        {{ item.text }}
                                    </v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </template>
                        <v-list-item
                                v-for="(child, i) in item.children"
                                :key="i"
                                link
                        >
                            <v-list-item-action v-if="child.icon">
                                <v-icon>{{ child.icon }}</v-icon>
                            </v-list-item-action>
                            <v-list-item-content>
                                <v-list-item-title>
                                    {{ child.text }}
                                </v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list-group>
                    <v-list-item
                            v-else
                            :key="item.text"
                            link
                    >
                        <v-list-item-action>
                            <v-icon>{{ item.icon }}</v-icon>
                        </v-list-item-action>
                        <v-list-item-content>
                            <v-list-item-title>
                                {{ item.text }}
                            </v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </template>
            </v-list>
        </v-navigation-drawer>

        <v-app-bar
                :clipped-left="$vuetify.breakpoint.lgAndUp"
                app
                color="blue darken-3"
                dark
        >
            <v-toolbar-title
                    style="width: 300px"
                    class="ml-0 pl-4"
            >
                <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
                <span class="hidden-sm-and-down">Google Contacts</span>
            </v-toolbar-title>
            <v-text-field
                    flat
                    solo-inverted
                    hide-details
                    prepend-inner-icon="mdi-magnify"
                    label="Search"
                    class="hidden-sm-and-down"
            />
            <v-spacer />
            <v-btn icon>
                <v-icon>mdi-apps</v-icon>
            </v-btn>
            <v-btn icon>
                <v-icon>mdi-bell</v-icon>
            </v-btn>
            <v-btn
                    icon
                    large
            >
                <v-avatar
                        size="32px"
                        item
                >
                    <v-img
                            src="https://cdn.vuetifyjs.com/images/logos/logo.svg"
                            alt="Vuetify"
                    /></v-avatar>
            </v-btn>
        </v-app-bar>
        <v-content>
            <v-container
                    class="fill-height"
                    fluid
            >
                <v-row
                        align="center"
                        justify="center"
                >
                    <v-tooltip right>
                        <template v-slot:activator="{ on }">
                            <v-btn
                                    :href="source"
                                    icon
                                    large
                                    target="_blank"
                                    v-on="on"
                            >
                                <v-icon large>mdi-code-tags</v-icon>
                            </v-btn>
                        </template>
                        <span>Source</span>
                    </v-tooltip>
                    <v-tooltip right>
                        <template v-slot:activator="{ on }">
                            <v-btn
                                    icon
                                    large
                                    href="https://codepen.io/johnjleider/pen/MNYLdL"
                                    target="_blank"
                                    v-on="on"
                            >
                                <v-icon large>mdi-codepen</v-icon>
                            </v-btn>
                        </template>
                        <span>Codepen</span>
                    </v-tooltip>
                </v-row>
            </v-container>
        </v-content>
        <v-btn
                bottom
                color="pink"
                dark
                fab
                fixed
                right
                @click="dialog = !dialog"
        >
            <v-icon>mdi-plus</v-icon>
        </v-btn>
        <v-dialog
                v-model="dialog"
                width="800px"
        >
            <v-card>
                <v-card-title class="grey darken-2">
                    Create contact
                </v-card-title>
                <v-container>
                    <v-row>
                        <v-col
                                class="align-center justify-space-between"
                                cols="12"
                        >
                            <v-row align="center">
                                <v-avatar
                                        size="40px"
                                        class="mr-4"
                                >
                                    <img
                                            src="//ssl.gstatic.com/s2/oz/images/sge/grey_silhouette.png"
                                            alt=""
                                    >
                                </v-avatar>
                                <v-text-field
                                        placeholder="Name"
                                />
                            </v-row>
                        </v-col>
                        <v-col cols="6">
                            <v-text-field
                                    prepend-icon="business"
                                    placeholder="Company"
                            />
                        </v-col>
                        <v-col cols="6">
                            <v-text-field
                                    placeholder="Job title"
                            />
                        </v-col>
                        <v-col cols="12">
                            <v-text-field
                                    prepend-icon="mail"
                                    placeholder="Email"
                            />
                        </v-col>
                        <v-col cols="12">
                            <v-text-field
                                    type="tel"
                                    prepend-icon="phone"
                                    placeholder="(000) 000 - 0000"
                            />
                        </v-col>
                        <v-col cols="12">
                            <v-text-field
                                    prepend-icon="notes"
                                    placeholder="Notes"
                            />
                        </v-col>
                    </v-row>
                </v-container>
                <v-card-actions>
                    <v-btn
                            text
                            color="primary"
                    >More</v-btn>
                    <v-spacer />
                    <v-btn
                            text
                            color="primary"
                            @click="dialog = false"
                    >Cancel</v-btn>
                    <v-btn
                            text
                            @click="dialog = false"
                    >Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-app>
</template>

<script>
    export default {
        props: {
            source: String,
        },
        data: () => ({
            dialog: false,
            drawer: null,
            items: [
                { icon: 'mdi-contacts', text: 'Contacts' },
                { icon: 'mdi-history', text: 'Frequently contacted' },
                { icon: 'mdi-content-copy', text: 'Duplicates' },
                {
                    icon: 'mdi-chevron-up',
                    'icon-alt': 'mdi-chevron-down',
                    text: 'Labels',
                    model: true,
                    children: [
                        { icon: 'mdi-plus', text: 'Create label' },
                    ],
                },
                {
                    icon: 'mdi-chevron-up',
                    'icon-alt': 'mdi-chevron-down',
                    text: 'More',
                    model: false,
                    children: [
                        { text: 'Import' },
                        { text: 'Export' },
                        { text: 'Print' },
                        { text: 'Undo changes' },
                        { text: 'Other contacts' },
                    ],
                },
                { icon: 'mdi-settings', text: 'Settings' },
                { icon: 'mdi-message', text: 'Send feedback' },
                { icon: 'mdi-help-circle', text: 'Help' },
                { icon: 'mdi-cellphone-link', text: 'App downloads' },
                { icon: 'mdi-keyboard', text: 'Go to the old version' },
            ],
        }),
    }
</script>























<!--
<template>
    &lt;!&ndash;&ndash;&gt;
    <v-content>


        <h1>suka</h1>
        <v-img src="D:\Projects\Rest\src\main\resources\static\pics\logo.png"></v-img>
           &lt;!&ndash; <v-row>
                <v-navigation-drawer v-show="true" expand-on-hover="true"
                                     height="300" width="500">
                </v-navigation-drawer>



                <div>
                    kek
                </div>
            </v-row>&ndash;&gt;

        <v-sheet
                class="mx-auto mt-3"
                max-width="700"
        >
            <v-slide-group multiple show-arrows>
                <v-slide-item v-for="n in 25" :key="n" v-slot:default="{ active, toggle }">
                    <v-btn
                            class="mx-2"
                            :input-value="active"
                            active-class="purple white&#45;&#45;text"
                            depressed
                            rounded
                            @click="toggle"
                    >
                        Options {{ n }}
                    </v-btn>
                </v-slide-item>
            </v-slide-group>
        </v-sheet>
        &lt;!&ndash;&ndash;&gt;
        <v-container>
            &lt;!&ndash;&ndash;&gt;
            <items-list />
            &lt;!&ndash;&ndash;&gt;
            <v-btn class="ma-2" color="red" dark>
                Удалить
                <v-icon dark right>mdi-cancel</v-icon>
            </v-btn>
            <v-btn class="ma-2" color="primary" dark>
                Заказать
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
            &lt;!&ndash;&ndash;&gt;
            <v-carousel>
                <v-carousel-item v-for="(color, i) in colors" :key="color">
                    <v-sheet :color="color" height="100%" tile>
                        <v-row class="fill-height" align="center" justify="center">
                            <div class="display-3">Slide {{ i + 1 }}</div>
                        </v-row>
                    </v-sheet>
                </v-carousel-item>
            </v-carousel>
            &lt;!&ndash;&ndash;&gt;
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
            &lt;!&ndash;&ndash;&gt;
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
            &lt;!&ndash;&ndash;&gt;
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
        &lt;!&ndash;&ndash;&gt;
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
        &lt;!&ndash;&ndash;&gt;
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

</style>-->
