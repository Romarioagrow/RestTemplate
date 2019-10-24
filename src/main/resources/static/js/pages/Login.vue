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
                            <v-text-field id="username"  name="username" prepend-icon="mdi-phone" type="text" v-mask="'7-###-###-##-##'" v-model="username" label="Номер телефона +7"></v-text-field>
                            <v-text-field id="password" name="password" prepend-icon="mdi-key" type="password" v-model="password" label="Пароль"></v-text-field>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="success" block @click="loginUser()">Войти</v-btn>
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
                                            <v-text-field id="username" label="Номер телефона" name="username" prepend-icon="mdi-phone" type="text" v-mask="'7-###-###-##-##'" v-model="username"></v-text-field>
                                            <v-row>
                                                <v-col>
                                                    <v-text-field id="password" name="password" prepend-icon="mdi-key" type="password" v-model="password" label="Пароль"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field id="passwordConfirm"  name="passwordConfirm" type="password" v-model="registerPassConfirm" label="Подтвердите пароль"></v-text-field>
                                                </v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col>
                                                    <v-text-field id="lastName" name="lastName" prepend-icon="mdi-account" type="text" v-model="lastName" label="Фамилия"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field id="firstName" name="firstName" type="text" v-model="firstName" label="Имя"></v-text-field>
                                                </v-col>
                                                <v-col>
                                                    <v-text-field id="patronymic" name="patronymic" type="text" v-model="patronymic" label="Отчество"></v-text-field>
                                                </v-col>
                                            </v-row>
                                            <v-text-field id="email" name="email" prepend-icon="mdi-email" type="password" v-model="email" label="E-mail"></v-text-field>
                                        </v-form>
                                    </v-card-text>
                                    <v-card-actions>
                                        <div class="flex-grow-1"></div>
                                        <v-btn color="green darken-1" text @click="submitRegistration()">Регистрация</v-btn>
                                        <v-btn color="red darken-1" text @click="dialog = false">Отмена</v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-row>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import axios from 'axios'
    export default {
        data() {
            return {
                dialog: false,

                username: '',
                password: '',
                registerPassConfirm: '',
                lastName: '',
                firstName:'',
                patronymic: '',
                email: '',


            }
        },
        methods: {
            submitRegistration() {
                this.dialog = false
                //console.log('kok')

                let registrationData = {}
                registrationData['username'] = this.username
                registrationData['password'] = this.password
                registrationData['lastName'] = this.lastName
                registrationData['firstName'] = this.firstName
                registrationData['patronymic'] = this.patronymic
                registrationData['email'] = this.email

                //console.log(registrationData)

                axios.post('/api/user/registration', registrationData).then(response => {
                    console.log(response)
                })
            },
            loginUser() {
                let loginData = {}
                loginData['username'] = this.username
                loginData['password'] = this.password

                axios.post('/api/user/login', loginData).then(response => {
                    console.log(response)
                })
            }
        }
    }
</script>