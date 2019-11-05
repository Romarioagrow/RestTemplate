<template>
    <div v-if="created">
        <v-navigation-drawer absolute permanent>
            <template v-slot:prepend>
                <v-list-item two-line>
                    <v-list-item-content>
                        <v-list-item-title>Admin</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </template>
            <v-list dense>
                <v-list-item v-for="item in menu" :key="item.title" @click="">
                    <v-list-item-icon>
                        <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
            <v-divider></v-divider>
            <v-card-actions>
                <v-btn block tile color="red" @click="logout">
                    <span>Выход</span>
                    <v-icon>mdi-logout</v-icon>
                </v-btn>
            </v-card-actions>
        </v-navigation-drawer>
        <v-content>
            <v-container fluid fill-height>
                <v-container>
                    <v-row>
                        <v-col>
                            <v-card>
                                <v-card-title>Обновить БД</v-card-title>
                                <v-card-actions class="ml-5">
                                    <form enctype="multipart/form-data">
                                        <v-row><input type="file" name="file" v-on:change="fileChange($event.target.files)" /></v-row>
                                        <v-row><v-btn color="success" v-on:click="uploadExcelFile()">Загрузить</v-btn></v-row>
                                    </form>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                        <v-col>
                            <v-card>
                                <v-card-title>Doffler-Leran</v-card-title>
                                <v-card-actions class="ml-5">
                                    <form enctype="multipart/form-data">
                                        <v-row><input type="file" name="fileBrands" v-on:change="brandsFileChange($event.target.files)" /></v-row>
                                        <v-row><v-btn color="success" v-on:click="uploadBrandsPrice()">Загрузить бренд-прайс</v-btn></v-row>
                                    </form>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>Обновить каталог</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="blue" v-on:click="updateCatalog()">Обновить</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>Спарсить ссылки фото RUSBT</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="yellow" v-on:click="parsRUSBTPics()">Запустить парсер</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                    <!--<v-row>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>Test</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="red" v-on:click="test()">BANG</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>logout</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="red" v-on:click="logout()">logout</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col>
                            <v-card class="mt-3">
                                <v-card-title>test auth</v-card-title>
                                <v-card-actions class="ml-5">
                                    <v-btn color="red" v-on:click="testAuth()">test</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>-->
                </v-container>
            </v-container>
        </v-content>
    </div>
</template>

<script>
    import axios from "axios";
    export default {
        data() {
            return {
                menu: [
                    { title: 'База данных',     icon: 'mdi-file-excel' },
                    { title: 'Заказы',  icon: 'mdi-format-list-bulleted-square' },
                    /*{ title: 'Все заказы',          icon: 'mdi-script-text-outline' },*/
                ],
                file: new FormData(),
                fileBrands: new FormData(),
                created: false
            }
        },
        beforeCreate() {
            axios.get('/admin').then(response => {
                this.created = true
            }).catch(error => {
                if (error.response) {
                    console.log(error.response.status);
                    if (error.response.status === 403) {
                        this.$store.dispatch('redirectToRoot')
                        this.$router.push('/')
                    }
                }
            })
        },
        methods: {
            fileChange(fileList) {
                this.file.append("file", fileList[0], fileList[0].name);
            },
            brandsFileChange(fileList) {
                this.fileBrands.append("fileBrands", fileList[0], fileList[0].name);
            },
            uploadExcelFile() {
                axios.post('admin/uploadFileDB', this.file).then(this.file = new FormData());
            },
            uploadBrandsPrice() {
                axios.post('admin/uploadFileBrands', this.fileBrands).then(console.log('brands uploaded'));
            },
            updateCatalog() {
                axios.post('admin/updateCatalog').then(console.log('catalog updated'));
            },
            parsRUSBTPics() {
                axios.post('admin/parsePicsRUSBT').then(console.log('pics parsed'));
            },
            test() {
                axios.post('admin/test').then(console.log('OK'));
            },
            logout() {
                axios.post('http://localhost:9000/user/logout').then((response) => {
                    this.$store.dispatch('logout')
                    this.$router.push('/')
                })
                console.log(this.$store.state.currentUser)
            },
            testAuth() {
                console.log(this.$store.state.currentUser)
            }
        }
    }
</script>

<style scoped></style>