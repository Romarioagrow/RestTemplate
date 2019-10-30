<template>


    <v-container fluid>

        <v-row align="center" justify="center">
            <v-col cols="6">
                <v-card>
                    <v-toolbar color="#e52d00" dark flat>
                        <v-toolbar-title>Вход в личный кабинет</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>

                    <v-card-text>
                        <v-form>
                            <v-text-field id="username" label="Номер телефона" name="username" prepend-icon="mdi-phone" type="text"></v-text-field>
                            <v-text-field id="password" label="Пароль" name="password" prepend-icon="mdi-key" type="password"></v-text-field>
                        </v-form>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="success" block >Войти</v-btn>
                    </v-card-actions>

                    <v-card-text>
                        <span>Зашли впервые?</span>
                        <v-row justify="center" class="mt-3">
                            <v-dialog v-model="dialog" persistent max-width="500">
                                <template v-slot:activator="{ on }">
                                    <v-btn color="primary" dark v-on="on">Зарегистрируйтесь</v-btn>
                                </template>
                                <v-card>
                                    <v-card-title class="headline">Регистрация</v-card-title>
                                    <v-card-text>Введите ваши контактные данные</v-card-text>
                                    <v-card-text>
                                        <v-form>
                                            <v-text-field id="username" label="Номер телефона" name="username" prepend-icon="mdi-phone" type="text"></v-text-field>

                                            <v-row>
                                                <v-col>
                                                    <v-text-field id="password" label="Пароль" name="password" prepend-icon="mdi-key" type="password"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field id="password" label="Подтвердите пароль" name="password" type="password"></v-text-field>
                                                </v-col>
                                            </v-row>

                                            <v-row>
                                                <v-col>
                                                    <v-text-field id="password" label="Фамилия" name="password" prepend-icon="mdi-account" type="text"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field id="password" label="Имя" name="password" type="text"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field id="password" label="Отчество" name="password" type="text"></v-text-field>
                                                </v-col>
                                            </v-row>
                                            <v-text-field id="password" label="E-mail" name="password" prepend-icon="mdi-email" type="password"></v-text-field>
                                        </v-form>
                                    </v-card-text>
                                    <v-card-actions>
                                        <div class="flex-grow-1"></div>
                                        <v-btn color="green darken-1" text @click="dialog = false">Регистрация</v-btn>
                                        <v-btn color="red darken-1" text @click="dialog = false">Отмена</v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-row>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>

        <!--<v-row align="center" justify="center">
            <v-col cols="2" sm="8" md="4">
                <v-card width="1000">
                    <v-toolbar color="primary" dark flat>
                        <v-toolbar-title>Вход в личный кабинет</v-toolbar-title>
                        <v-spacer></v-spacer>


                    </v-toolbar>
                    <v-card-text>
                        <v-form>
                            <v-text-field label="Login" name="login" prepend-icon="person" type="text"></v-text-field>

                            <v-text-field
                                    id="password"
                                    label="Password"
                                    name="password"
                                    prepend-icon="lock"
                                    type="password"
                            ></v-text-field>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="primary">Login</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>-->
    </v-container>


</template>

<script>
    export default {
        props: {
            source: String,
        },
        data() {
            return {
                dialog: false,
            }
        }
    }
</script>