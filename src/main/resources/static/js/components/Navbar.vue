<template>
    <div>
        <v-app-bar color="deep-purple accent-4" dark >
            <v-app-bar-nav-icon></v-app-bar-nav-icon>
            <v-toolbar-title>PleaseDo</v-toolbar-title>
            <div class="flex-grow-1"></div>
            <v-btn icon>
                <v-icon>mdi-heart</v-icon>
            </v-btn>
            <v-btn icon>
                <v-icon>mdi-magnify</v-icon>
            </v-btn>
            <v-menu left bottom>
                <template v-slot:activator="{ on }">
                    <v-btn icon v-on="on">
                        <v-icon>mdi-dots-vertical</v-icon>
                    </v-btn>
                </template>
                <v-list>
                    <v-list-item
                            v-for="n in 5"
                            :key="n"
                            @click="() => {}"
                    >
                        <v-list-item-title>Option {{ n }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-app-bar>
        <!---->
        <v-bottom-navigation
                color="deep-purple accent-4"
        >
            <router-link to="/">
                <v-btn>
                    <span>Каталог</span>
                    <v-icon>mdi-heart</v-icon>
                </v-btn>
            </router-link>
            <router-link to="/test">
                <v-btn>
                    <span>Товары</span>
                    <v-icon>mdi-history</v-icon>
                </v-btn>
            </router-link>
            <router-link to="/admin">
                <v-btn>
                    <span>Админ</span>
                    <v-icon>mdi-account-star-outline</v-icon>
                </v-btn>
            </router-link>
        </v-bottom-navigation>
    </div>
</template>

<script>
    export default {
        name: "Navbar"
    }
</script>

<style scoped>

</style>